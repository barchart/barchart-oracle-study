package sun.plugin2.applet;

import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.OSType;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.util.FXLoader;
import com.sun.deploy.util.NativeLibraryBundle;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.LaunchDownload;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.jnl.ResourcesDesc.PackageInformation;
import com.sun.javaws.security.AppPolicy;
import com.sun.javaws.util.JNLPUtils;
import com.sun.jnlp.ClipboardServiceImpl;
import com.sun.jnlp.DownloadService2Impl;
import com.sun.jnlp.DownloadServiceImpl;
import com.sun.jnlp.ExtendedServiceImpl;
import com.sun.jnlp.ExtensionInstallerServiceImpl;
import com.sun.jnlp.FileOpenServiceImpl;
import com.sun.jnlp.FileSaveServiceImpl;
import com.sun.jnlp.IntegrationServiceImpl;
import com.sun.jnlp.JNLPClassLoaderIf;
import com.sun.jnlp.JNLPPreverifyClassLoader;
import com.sun.jnlp.JNLPPreverifyClassLoader.DelegatingThread;
import com.sun.jnlp.JNLPPreverifyClassLoader.UndelegatingThread;
import com.sun.jnlp.PersistenceServiceImpl;
import com.sun.jnlp.PrintServiceImpl;
import com.sun.jnlp.SingleInstanceServiceImpl;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import javax.jnlp.BasicService;
import javax.jnlp.ClipboardService;
import javax.jnlp.DownloadService;
import javax.jnlp.DownloadService2;
import javax.jnlp.ExtendedService;
import javax.jnlp.ExtensionInstallerService;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.IntegrationService;
import javax.jnlp.PersistenceService;
import javax.jnlp.PrintService;
import javax.jnlp.SingleInstanceService;

public final class JNLP2ClassLoader extends Plugin2ClassLoader
  implements JNLPClassLoaderIf
{
  private LaunchDesc _launchDesc = null;
  private AppPolicy _appPolicy;
  private boolean _initialized;
  private Map _jarsInURLClassLoader = new HashMap();
  private ArrayList _jarsNotInURLClassLoader = new ArrayList();
  private NativeLibraryBundle nativeLibraries = null;
  private JNLPPreverifyClassLoader _preverifyCL = null;
  private JNLP2ClassLoader _jclParent;
  private boolean processingException = false;
  BasicService _basicService = null;

  protected JNLP2ClassLoader(URL paramURL, JNLPPreverifyClassLoader paramJNLPPreverifyClassLoader)
  {
    super(new URL[0], paramURL, paramJNLPPreverifyClassLoader);
    this._preverifyCL = paramJNLPPreverifyClassLoader;
    this._initialized = false;
    if (DEBUG)
      Trace.println("JNLP2ClassLoader: cstr ...", TraceLevel.BASIC);
  }

  protected JNLP2ClassLoader(URL paramURL, ClassLoader paramClassLoader)
  {
    super(new URL[0], paramURL, paramClassLoader);
    this._initialized = false;
    if ((paramClassLoader instanceof JNLP2ClassLoader))
      this._jclParent = ((JNLP2ClassLoader)paramClassLoader);
    if (DEBUG)
      Trace.println("JNLP2ClassLoader: cstr ...", TraceLevel.BASIC);
  }

  boolean isClassLoadedByPluginClassLoader(Class paramClass)
  {
    ClassLoader localClassLoader = paramClass.getClassLoader();
    if ((isShadowClassLoader()) && (this._jclParent != null))
    {
      JNLPPreverifyClassLoader localJNLPPreverifyClassLoader = this._jclParent.getJNLPPreverifyClassLoader();
      return (localClassLoader == this) || (localClassLoader == this._jclParent) || ((localJNLPPreverifyClassLoader != null) && (localClassLoader == localJNLPPreverifyClassLoader));
    }
    return (localClassLoader == this) || ((this._preverifyCL != null) && (localClassLoader == this._preverifyCL));
  }

  JNLPPreverifyClassLoader getJNLPPreverifyClassLoader()
  {
    return this._preverifyCL;
  }

  private void setDelegatingClassLoader(JNLP2ClassLoader paramJNLP2ClassLoader)
  {
    if (this._preverifyCL != null)
    {
      this._preverifyCL.setDelegatingClassLoader(paramJNLP2ClassLoader);
      this._delegatingClassLoader = paramJNLP2ClassLoader;
    }
  }

  public boolean wantsAllPerms(CodeSource paramCodeSource)
  {
    if (paramCodeSource != null)
    {
      JARDesc localJARDesc = getJarDescFromURL(paramCodeSource.getLocation());
      if ((localJARDesc != null) && (localJARDesc.getParent() != null))
      {
        LaunchDesc localLaunchDesc = localJARDesc.getParent().getParent();
        if ((localLaunchDesc != null) && (localLaunchDesc.getSecurityModel() != 0))
          return true;
      }
    }
    return false;
  }

  protected void initialize(LaunchDesc paramLaunchDesc, AppPolicy paramAppPolicy)
  {
    if (DEBUG)
      Trace.println("JNLP2ClassLoader: initialize ...", TraceLevel.BASIC);
    this._launchDesc = paramLaunchDesc;
    this._appPolicy = paramAppPolicy;
    this._initialized = true;
    if (this._jclParent != null)
    {
      this._jclParent.initialize(paramLaunchDesc, paramAppPolicy);
      this._jclParent.setDelegatingClassLoader(this);
      drainPendingURLs();
      return;
    }
    if (this._preverifyCL != null)
    {
      this._preverifyCL.initialize(paramLaunchDesc, paramAppPolicy);
      this._preverifyCL.setDelegatingClassLoader(this);
    }
    if (paramLaunchDesc.needFX())
      try
      {
        FXLoader.loadFX(paramLaunchDesc.getHomeJRE().getJfxRuntime());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Trace.ignored(localClassNotFoundException);
      }
      catch (IllegalStateException localIllegalStateException)
      {
        Trace.ignored(localIllegalStateException);
      }
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    ArrayList localArrayList = new ArrayList();
    if (localResourcesDesc != null)
    {
      JNLPUtils.sortResourcesForClasspath(localResourcesDesc, localArrayList, this._jarsNotInURLClassLoader);
      for (int i = 0; i < localArrayList.size(); i++)
      {
        JARDesc localJARDesc = (JARDesc)localArrayList.get(i);
        if (DEBUG)
          Trace.println("\t addURL: " + localJARDesc.getLocationString(), TraceLevel.BASIC);
        this._jarsInURLClassLoader.put(URLUtil.toNormalizedString(localJARDesc.getLocation()), localJARDesc);
        if ((this._preverifyCL == null) || (!this._preverifyCL.contains(localJARDesc.getLocation())))
          addURL2(localJARDesc.getLocation());
      }
    }
    if (DEBUG)
      Trace.println("JNLP2ClassLoader: initialize done", TraceLevel.BASIC);
  }

  public URL getResource(final String paramString)
  {
    URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$name;

      public Object run()
        throws SecurityException
      {
        URL localURL = null;
        for (int i = 0; (localURL == null) && (i < 3); i++)
          localURL = JNLP2ClassLoader.this.getResource(paramString);
        return localURL;
      }
    });
    return localURL;
  }

  private String findLibrary0(String paramString)
  {
    Trace.println("JNLP2ClassLoader.findLibrary: Looking up native library: " + paramString, TraceLevel.BASIC);
    synchronized (this)
    {
      if (this.nativeLibraries != null)
      {
        localObject1 = this.nativeLibraries.get(paramString);
        if (localObject1 != null)
        {
          Trace.println("JNLP2ClassLoader.findLibrary: native library found: " + (String)localObject1, TraceLevel.BASIC);
          DeployPerfUtil.put("JNLP2ClassLoader.findLibrary - reusing library");
          return localObject1;
        }
      }
      else
      {
        this.nativeLibraries = new NativeLibraryBundle();
      }
    }
    ??? = this._launchDesc.getResources();
    Object localObject1 = ((ResourcesDesc)???).getEagerOrAllJarDescs(true);
    for (int i = 0; i < localObject1.length; i++)
      if (localObject1[i].isNativeLib())
        try
        {
          String str1 = ResourceProvider.get().getLibraryDirForJar(paramString, localObject1[i].getLocation(), localObject1[i].getVersion());
          if (str1 != null)
          {
            JarFile localJarFile = ResourceProvider.get().getCachedJarFile(localObject1[i].getLocation(), localObject1[i].getVersion());
            this.nativeLibraries.prepareLibrary(paramString, localJarFile, str1);
            String str2 = this.nativeLibraries.get(paramString);
            Trace.println("JNLP2ClassLoader.findLibrary: native library found: " + str2, TraceLevel.BASIC);
            DeployPerfUtil.put("JNLP2ClassLoader.findLibrary - found library");
            return str2;
          }
        }
        catch (IOException localIOException)
        {
          Trace.ignoredException(localIOException);
        }
    return null;
  }

  protected String findLibrary(String paramString)
  {
    if (this._jclParent != null)
      return this._jclParent.findLibrary(paramString);
    DeployPerfUtil.put("JNLP2ClassLoader.findLibrary - start()");
    if (!this._initialized)
    {
      Trace.println("JNLP2ClassLoader.findLibrary: " + paramString + ": not initialized -> super()", TraceLevel.BASIC);
      return super.findLibrary(paramString);
    }
    String str1 = System.mapLibraryName(paramString);
    String str2 = findLibrary0(str1);
    if (str2 != null)
      return str2;
    if (OSType.isMac())
    {
      str1 = "lib" + paramString + ".jnilib";
      str2 = findLibrary0(str1);
      if (str2 != null)
        return str2;
    }
    Trace.println("JNLP2ClassLoader: Native library " + paramString + " not found", TraceLevel.BASIC);
    DeployPerfUtil.put("JNLP2ClassLoader.findLibrary - return super.findLibrary");
    return super.findLibrary(paramString);
  }

  protected Class findClass(String paramString)
    throws ClassNotFoundException
  {
    throw new ClassNotFoundException("can't happen");
  }

  protected Class findClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    if (!this._initialized)
    {
      Trace.println("JNLP2ClassLoader.findClass: " + paramString + ": not initialized -> super()", TraceLevel.BASIC);
      return super.findClass(paramString);
    }
    try
    {
      return findClassHelper(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Trace.println("JNLP2ClassLoader.findClass: " + paramString + ": try again ..", TraceLevel.BASIC);
      if (checkPackageParts(paramString))
        return findClassHelper(paramString);
      synchronized (this)
      {
        if ((!paramBoolean) && (!this.processingException) && (this._delegatingClassLoader != null) && (needToApplyWorkaround()))
        {
          this.processingException = true;
          try
          {
            JNLPPreverifyClassLoader.DelegatingThread localDelegatingThread = new JNLPPreverifyClassLoader.DelegatingThread(this._delegatingClassLoader, this);
            localDelegatingThread.start();
            while (!localDelegatingThread.done())
              try
              {
                wait();
              }
              catch (InterruptedException localInterruptedException1)
              {
                throw localClassNotFoundException;
              }
            Class localClass = this._delegatingClassLoader.loadClass(paramString);
            jsr 17;
            return localClass;
          }
          finally
          {
            jsr 6;
          }
          localObject2 = returnAddress;
          int i = 0;
          JNLPPreverifyClassLoader.UndelegatingThread localUndelegatingThread = new JNLPPreverifyClassLoader.UndelegatingThread(this._delegatingClassLoader, this);
          localUndelegatingThread.start();
          while (!localUndelegatingThread.done())
            try
            {
              wait();
            }
            catch (InterruptedException localInterruptedException2)
            {
              i = 1;
            }
          if (i == 0)
            this.processingException = false;
          ret;
        }
      }
      throw localClassNotFoundException;
    }
  }

  public URL findResource(String paramString)
  {
    URL localURL = super.findResource(paramString);
    if (!this._initialized)
    {
      Trace.println("JNLP2ClassLoader.findResource: " + paramString + ": not initialized -> super()", TraceLevel.BASIC);
      return localURL;
    }
    if ((localURL == null) && (checkPackageParts(paramString)))
      localURL = super.findResource(paramString);
    return localURL;
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    Trace.println("JNLP2ClassLoader.getPermissions()", TraceLevel.BASIC);
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    try
    {
      this._appPolicy.addPermissions(this, localPermissionCollection, paramCodeSource, false);
    }
    catch (ExitException localExitException)
    {
      Trace.println("_appPolicy.addPermissions: " + localExitException, TraceLevel.BASIC);
      Trace.ignoredException(localExitException);
    }
    URL localURL = paramCodeSource.getLocation();
    JARDesc localJARDesc = getJarDescFromURL(localURL);
    if (localJARDesc != null)
    {
      JarFile localJarFile = ResourceProvider.get().getCachedJarFile(localJARDesc.getLocation(), localJARDesc.getVersion());
      if (localJarFile != null)
      {
        String str = localJarFile.getName();
        localPermissionCollection.add(new FilePermission(str, "read"));
      }
    }
    Trace.println("JNLP2ClassLoader.getPermissions() X", TraceLevel.BASIC);
    return localPermissionCollection;
  }

  public LaunchDesc getLaunchDesc()
  {
    return this._launchDesc;
  }

  public JARDesc getJarDescFromURL(URL paramURL)
  {
    if (this._jclParent != null)
      return this._jclParent.getJarDescFromURL(paramURL);
    String str1 = URLUtil.toNormalizedString(paramURL);
    JARDesc localJARDesc = (JARDesc)this._jarsInURLClassLoader.get(str1);
    if (localJARDesc != null)
      return localJARDesc;
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this._jarsInURLClassLoader.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      localJARDesc = (JARDesc)this._jarsInURLClassLoader.get(str2);
      String str3 = URLUtil.toNormalizedString(DownloadEngine.getKnownRedirectFinalURL(localJARDesc.getLocation()));
      if (!this._jarsInURLClassLoader.containsKey(str3))
      {
        localHashMap.put(str3, localJARDesc);
        if (str1.equals(str3))
        {
          this._jarsInURLClassLoader.putAll(localHashMap);
          return localJARDesc;
        }
      }
    }
    this._jarsInURLClassLoader.putAll(localHashMap);
    return null;
  }

  public JarFile getJarFile(URL paramURL)
    throws IOException
  {
    final JARDesc localJARDesc = getJarDescFromURL(paramURL);
    JarFile localJarFile = null;
    if (localJARDesc != null)
    {
      final int i = LaunchDownload.getDownloadType(localJARDesc);
      localJarFile = (JarFile)AccessController.doPrivileged(new PrivilegedAction()
      {
        private final JARDesc val$jd;
        private final int val$contentType;

        public Object run()
          throws SecurityException
        {
          int i = ResourceProvider.get().incrementInternalUse();
          try
          {
            Resource localResource = ResourceProvider.get().getResource(localJARDesc.getLocation(), localJARDesc.getVersion(), true, i, null);
            Object localObject1 = localResource != null ? localResource.getJarFile() : null;
            if (localObject1 != null)
            {
              Object localObject2 = localObject1;
              return localObject2;
            }
          }
          catch (IOException localIOException)
          {
            Trace.ignoredException(localIOException);
          }
          finally
          {
            ResourceProvider.get().decrementInternalUse(i);
          }
          return null;
        }
      });
      if (localJarFile == null)
        throw new IOException("Resource not found: " + localJARDesc.getLocation() + ":" + localJARDesc.getVersion());
      return localJarFile;
    }
    return null;
  }

  public int getDefaultSecurityModel()
  {
    return this._launchDesc.getSecurityModel();
  }

  private boolean checkPackageParts(String paramString)
  {
    boolean bool = false;
    if (this._jclParent != null)
      return drainPendingURLs();
    try
    {
      JARDesc[] arrayOfJARDesc = null;
      ResourcesDesc localResourcesDesc = this._launchDesc.getResources();
      ResourcesDesc.PackageInformation localPackageInformation = localResourcesDesc.getPackageInformation(paramString);
      if (localPackageInformation != null)
        arrayOfJARDesc = localPackageInformation.getLaunchDesc().getResources().getPart(localPackageInformation.getPart());
      if (arrayOfJARDesc != null)
        for (int i = 0; i < arrayOfJARDesc.length; i++)
        {
          String str = arrayOfJARDesc[i].getLocationString();
          if (this._jarsNotInURLClassLoader.contains(arrayOfJARDesc[i]))
          {
            this._jarsNotInURLClassLoader.remove(arrayOfJARDesc[i]);
            if (!this._jarsInURLClassLoader.containsKey(str))
            {
              this._jarsInURLClassLoader.put(str, arrayOfJARDesc[i]);
              addURL2(arrayOfJARDesc[i].getLocation());
              bool = true;
            }
          }
        }
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
    }
    return bool;
  }

  protected void updateJarDescriptors(ResourcesDesc paramResourcesDesc)
  {
    if (this._jclParent != null)
    {
      this._jclParent.updateJarDescriptors(paramResourcesDesc);
      drainPendingURLs();
      return;
    }
    JARDesc[] arrayOfJARDesc = paramResourcesDesc.getEagerOrAllJarDescs(true);
    if (arrayOfJARDesc != null)
      for (int i = 0; i < arrayOfJARDesc.length; i++)
        updateJarDescriptor(paramResourcesDesc, arrayOfJARDesc[i]);
  }

  protected void updateJarDescriptor(ResourcesDesc paramResourcesDesc, JARDesc paramJARDesc)
  {
    if (paramJARDesc != null)
    {
      String str = paramJARDesc.getLocationString();
      if ((!this._jarsInURLClassLoader.containsKey(str)) && (!this._jarsNotInURLClassLoader.contains(paramJARDesc)))
        if ((!paramJARDesc.isLazyDownload()) || (!paramResourcesDesc.isPackagePart(paramJARDesc.getPartName())))
        {
          this._jarsInURLClassLoader.put(str, paramJARDesc);
          addURL2(paramJARDesc.getLocation());
        }
        else
        {
          this._jarsNotInURLClassLoader.add(paramJARDesc);
        }
    }
  }

  public void addResource(URL paramURL, String paramString1, String paramString2)
  {
    if (this._jclParent != null)
    {
      this._jclParent.addResource(paramURL, paramString1, paramString2);
      drainPendingURLs();
      return;
    }
    JARDesc localJARDesc = new JARDesc(paramURL, paramString1, true, false, false, null, 0, null);
    String str = localJARDesc.getLocationString();
    if (!this._jarsInURLClassLoader.containsKey(str))
    {
      this._launchDesc.getResources().addResource(localJARDesc);
      this._jarsInURLClassLoader.put(str, localJARDesc);
      addURL2(paramURL);
    }
  }

  public BasicService getBasicService()
  {
    if (this._basicService == null)
    {
      URL localURL = this._launchDesc.getCodebase();
      if (localURL == null)
        localURL = this.base;
      this._basicService = new Plugin2BasicService(localURL);
    }
    return this._basicService;
  }

  public FileOpenService getFileOpenService()
  {
    return FileOpenServiceImpl.getInstance();
  }

  public FileSaveService getFileSaveService()
  {
    return FileSaveServiceImpl.getInstance();
  }

  public ExtensionInstallerService getExtensionInstallerService()
  {
    return ExtensionInstallerServiceImpl.getInstance();
  }

  public DownloadService getDownloadService()
  {
    return DownloadServiceImpl.getInstance();
  }

  public ClipboardService getClipboardService()
  {
    return ClipboardServiceImpl.getInstance();
  }

  public PrintService getPrintService()
  {
    return PrintServiceImpl.getInstance();
  }

  public PersistenceService getPersistenceService()
  {
    return PersistenceServiceImpl.getInstance();
  }

  public ExtendedService getExtendedService()
  {
    return ExtendedServiceImpl.getInstance();
  }

  public SingleInstanceService getSingleInstanceService()
  {
    return SingleInstanceServiceImpl.getInstance();
  }

  public IntegrationService getIntegrationService()
  {
    return new IntegrationServiceImpl(this);
  }

  public DownloadService2 getDownloadService2()
  {
    return DownloadService2Impl.getInstance();
  }

  protected AppInfo getAppInfo()
  {
    if (this._launchDesc != null)
      return this._launchDesc.getAppInfo();
    return super.getAppInfo();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.JNLP2ClassLoader
 * JD-Core Version:    0.6.2
 */