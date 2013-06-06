package com.sun.jnlp;

import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.OSType;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.security.CPCallbackClassLoaderIf;
import com.sun.deploy.security.CPCallbackHandler;
import com.sun.deploy.security.DeployURLClassPath;
import com.sun.deploy.security.SecureCookiePermission;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.FXLoader;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.LaunchDownload;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.jnl.ResourcesDesc.PackageInformation;
import com.sun.javaws.progress.Progress;
import com.sun.javaws.security.AppPolicy;
import com.sun.javaws.util.JNLPUtils;
import com.sun.javaws.util.JfxHelper;
import java.awt.AWTPermission;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import sun.misc.JavaNetAccess;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;

public final class JNLPClassLoader extends URLClassLoader
  implements JNLPClassLoaderIf, CPCallbackClassLoaderIf
{
  private static JNLPClassLoader _instance = null;
  private static JNLPPreverifyClassLoader _preverifyCL = null;
  private LaunchDesc _launchDesc = null;
  private AppPolicy _appPolicy;
  private AccessControlContext _acc = null;
  private boolean _initialized = false;
  private Map _jarsInURLClassLoader = new HashMap();
  private ArrayList _jarsNotInURLClassLoader = new ArrayList();
  private static Field ucpField = getUCPField("ucp");
  private List addedURLs = new ArrayList();
  private JNLPClassLoader _jclParent;

  private JNLPClassLoader(ClassLoader paramClassLoader)
  {
    super(new URL[0], paramClassLoader);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
      localSecurityManager.checkCreateClassLoader();
    if ((paramClassLoader instanceof JNLPClassLoader))
      this._jclParent = ((JNLPClassLoader)paramClassLoader);
    setUCP(this, new DeployURLClassPath(new URL[0]));
  }

  private void initialize(LaunchDesc paramLaunchDesc, AppPolicy paramAppPolicy)
  {
    this._launchDesc = paramLaunchDesc;
    this._acc = AccessController.getContext();
    this._appPolicy = paramAppPolicy;
    this._initialized = true;
    if (this._jclParent != null)
    {
      this._jclParent.initialize(paramLaunchDesc, paramAppPolicy);
      drainPendingURLs();
      return;
    }
    if (_preverifyCL != null)
      _preverifyCL.initialize(paramLaunchDesc, paramAppPolicy);
    if (paramLaunchDesc.needFX())
      try
      {
        FXLoader.loadFX(JfxHelper.getBestJfxInstalled(paramLaunchDesc));
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
        this._jarsInURLClassLoader.put(URLUtil.toNormalizedString(localJARDesc.getLocation()), localJARDesc);
        if ((_preverifyCL == null) || (!_preverifyCL.contains(localJARDesc.getLocation())))
          addURL2(localJARDesc.getLocation());
      }
    }
  }

  public JNLPPreverifyClassLoader getJNLPPreverifyClassLoader()
  {
    return _preverifyCL;
  }

  public Preloader getPreloader()
  {
    return Progress.get(null);
  }

  public static JNLPClassLoader createClassLoader()
  {
    if (_instance == null)
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      _preverifyCL = new JNLPPreverifyClassLoader(localClassLoader);
      JNLPClassLoader localJNLPClassLoader1 = new JNLPClassLoader(_preverifyCL);
      if (Config.getMixcodeValue() != 3)
      {
        JNLPClassLoader localJNLPClassLoader2 = new JNLPClassLoader(localJNLPClassLoader1);
        if (!setDeployURLClassPathCallbacks(localJNLPClassLoader1, localJNLPClassLoader2))
          _instance = localJNLPClassLoader1;
        else
          _instance = localJNLPClassLoader2;
      }
      else
      {
        _instance = localJNLPClassLoader1;
      }
    }
    return _instance;
  }

  public static JNLPClassLoader createClassLoader(LaunchDesc paramLaunchDesc, AppPolicy paramAppPolicy)
  {
    JNLPClassLoader localJNLPClassLoader = createClassLoader();
    if (!localJNLPClassLoader._initialized)
      localJNLPClassLoader.initialize(paramLaunchDesc, paramAppPolicy);
    return localJNLPClassLoader;
  }

  public static JNLPClassLoaderIf getInstance()
  {
    return _instance;
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

  public int getDefaultSecurityModel()
  {
    return this._launchDesc.getSecurityModel();
  }

  public URL getResource(String paramString)
  {
    URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$name;

      public Object run()
        throws SecurityException
      {
        URL localURL = null;
        for (int i = 0; (localURL == null) && (i < 3); i++)
          localURL = JNLPClassLoader.this.getResource(this.val$name);
        return localURL;
      }
    });
    return localURL;
  }

  private String findLibrary0(String paramString)
  {
    ResourcesDesc localResourcesDesc = this._launchDesc.getResources();
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getEagerOrAllJarDescs(true);
    for (int i = 0; i < arrayOfJARDesc.length; i++)
      if (arrayOfJARDesc[i].isNativeLib())
        try
        {
          String str = ResourceProvider.get().getLibraryDirForJar(paramString, arrayOfJARDesc[i].getLocation(), arrayOfJARDesc[i].getVersion());
          if (str != null)
            return new File(str, paramString).getAbsolutePath();
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
    if (!this._initialized)
      return super.findLibrary(paramString);
    String str1 = System.mapLibraryName(paramString);
    Trace.println("JNLPClassLoader: Finding library " + str1);
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
    Trace.println("JNLPClassLoader: Native library " + paramString + " not found", TraceLevel.NETWORK);
    return super.findLibrary(paramString);
  }

  protected Class findClass(String paramString)
    throws ClassNotFoundException
  {
    if (!this._initialized)
      return super.findClass(paramString);
    try
    {
      return super.findClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if ((!(localClassNotFoundException.getCause() instanceof JARSigningException)) && (checkPackageParts(paramString)))
        return super.findClass(paramString);
      throw localClassNotFoundException;
    }
  }

  public InputStream getResourceAsStream(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      sun.misc.Resource localResource = getResourceAsResource(paramString);
      if (localResource != null)
        localInputStream = localResource.getInputStream();
    }
    catch (Throwable localThrowable)
    {
    }
    if (localInputStream == null)
      localInputStream = super.getResourceAsStream(paramString);
    return localInputStream;
  }

  protected sun.misc.Resource getResourceAsResource(String paramString)
    throws MalformedURLException, FileNotFoundException
  {
    if (this._jclParent != null)
      try
      {
        return this._jclParent.getResourceAsResource(paramString);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
      }
    URLClassPath localURLClassPath = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
    sun.misc.Resource localResource = localURLClassPath.getResource(paramString, false);
    if (localResource != null)
      return localResource;
    throw new FileNotFoundException("Resource " + paramString + " not found");
  }

  public void quiescenceRequested(Thread paramThread, boolean paramBoolean)
  {
  }

  public void quiescenceCancelled(boolean paramBoolean)
  {
  }

  public URL findResource(String paramString)
  {
    URL localURL = super.findResource(paramString);
    if (!this._initialized)
      return localURL;
    if ((localURL == null) && (checkPackageParts(paramString)))
      localURL = super.findResource(paramString);
    return localURL;
  }

  private boolean checkPackageParts(String paramString)
  {
    if (this._jclParent != null)
      return drainPendingURLs();
    boolean bool = false;
    ResourcesDesc.PackageInformation localPackageInformation = this._launchDesc.getResources().getPackageInformation(paramString);
    if (localPackageInformation != null)
    {
      JARDesc[] arrayOfJARDesc = localPackageInformation.getLaunchDesc().getResources().getPart(localPackageInformation.getPart());
      for (int i = 0; i < arrayOfJARDesc.length; i++)
        if (this._jarsNotInURLClassLoader.contains(arrayOfJARDesc[i]))
        {
          this._jarsNotInURLClassLoader.remove(arrayOfJARDesc[i]);
          addLoadedJarsEntry(arrayOfJARDesc[i]);
          addURL2(arrayOfJARDesc[i].getLocation());
          bool = true;
        }
    }
    return bool;
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    try
    {
      this._appPolicy.addPermissions(getInstance(), localPermissionCollection, paramCodeSource, true);
    }
    catch (ExitException localExitException)
    {
      throw new RuntimeException(localExitException);
    }
    URL localURL = paramCodeSource.getLocation();
    JARDesc localJARDesc = getJarDescFromURL(localURL);
    if (localJARDesc != null)
    {
      com.sun.deploy.model.Resource localResource = ResourceProvider.get().getCachedResource(localJARDesc.getLocation(), localJARDesc.getVersion());
      if ((localResource != null) && (localResource.getDataFile() != null))
      {
        String str = localResource.getDataFile().getPath();
        localPermissionCollection.add(new FilePermission(str, "read"));
      }
    }
    if (!localPermissionCollection.implies(new AWTPermission("accessClipboard")))
      ToolkitStore.get().getAppContext().put("UNTRUSTED_URLClassLoader", Boolean.TRUE);
    localPermissionCollection.add(new SecureCookiePermission(SecureCookiePermission.getURLOriginString(paramCodeSource.getLocation())));
    return localPermissionCollection;
  }

  public JarFile getJarFile(URL paramURL)
    throws IOException
  {
    JARDesc localJARDesc = getJarDescFromURL(paramURL);
    if (localJARDesc == null)
      return null;
    int i = LaunchDownload.getDownloadType(localJARDesc);
    try
    {
      return (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final JARDesc val$jd;
        private final int val$contentType;

        public Object run()
          throws IOException
        {
          int i = ResourceProvider.get().incrementInternalUse();
          try
          {
            JarFile localJarFile1 = ResourceProvider.get().getCachedJarFile(this.val$jd.getLocation(), this.val$jd.getVersion());
            if (localJarFile1 != null)
            {
              localJarFile2 = localJarFile1;
              return localJarFile2;
            }
            JarFile localJarFile2 = ResourceProvider.get().getJarFile(this.val$jd.getLocation(), this.val$jd.getVersion(), this.val$contentType);
            return localJarFile2;
          }
          finally
          {
            ResourceProvider.get().decrementInternalUse(i);
          }
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }

  private void addLoadedJarsEntry(JARDesc paramJARDesc)
  {
    String str = paramJARDesc.getLocationString();
    if (!this._jarsInURLClassLoader.containsKey(str))
      this._jarsInURLClassLoader.put(str, paramJARDesc);
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
      addLoadedJarsEntry(localJARDesc);
      addURL2(paramURL);
    }
  }

  static boolean setDeployURLClassPathCallbacks(JNLPClassLoader paramJNLPClassLoader1, JNLPClassLoader paramJNLPClassLoader2)
  {
    try
    {
      if (!ResourceProvider.get().hasEnhancedJarAccess())
      {
        Trace.println("setDeployURLClassPathCallbacks: no enhanced access", TraceLevel.BASIC);
        return false;
      }
      CPCallbackHandler localCPCallbackHandler = new CPCallbackHandler(paramJNLPClassLoader1, paramJNLPClassLoader2);
      getDUCP(paramJNLPClassLoader1).setDeployURLClassPathCallback(localCPCallbackHandler.getParentCallback());
      getDUCP(paramJNLPClassLoader2).setDeployURLClassPathCallback(localCPCallbackHandler.getChildCallback());
    }
    catch (ThreadDeath localThreadDeath)
    {
      throw localThreadDeath;
    }
    catch (Exception localException)
    {
      Trace.ignored(localException, true);
      return false;
    }
    catch (Error localError)
    {
      Trace.ignored(localError, true);
      return false;
    }
    return true;
  }

  private static DeployURLClassPath getDUCP(JNLPClassLoader paramJNLPClassLoader)
  {
    return (DeployURLClassPath)getUCP(paramJNLPClassLoader);
  }

  private static URLClassPath getUCP(JNLPClassLoader paramJNLPClassLoader)
  {
    URLClassPath localURLClassPath = null;
    try
    {
      localURLClassPath = (URLClassPath)ucpField.get(paramJNLPClassLoader);
    }
    catch (Exception localException)
    {
    }
    return localURLClassPath;
  }

  private static void setUCP(JNLPClassLoader paramJNLPClassLoader, URLClassPath paramURLClassPath)
  {
    try
    {
      ucpField.set(paramJNLPClassLoader, paramURLClassPath);
    }
    catch (Exception localException)
    {
    }
  }

  private static Field getUCPField(String paramString)
  {
    return (Field)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$name;

      public Object run()
      {
        try
        {
          Field localField = URLClassLoader.class.getDeclaredField(this.val$name);
          localField.setAccessible(true);
          return localField;
        }
        catch (Exception localException)
        {
        }
        return null;
      }
    });
  }

  protected void addURL(URL paramURL)
  {
    if (this._jclParent != null)
      this._jclParent.addURL(paramURL);
    super.addURL(paramURL);
  }

  void addURL2(URL paramURL)
  {
    if (this._jclParent != null)
      drainPendingURLs();
    else
      putAddedURL(paramURL);
    super.addURL(paramURL);
  }

  boolean drainPendingURLs()
  {
    List localList = this._jclParent.grabAddedURLs();
    for (int i = 0; i < localList.size(); i++)
      super.addURL((URL)localList.get(i));
    return i != 0;
  }

  synchronized List grabAddedURLs()
  {
    List localList = this.addedURLs;
    this.addedURLs = new ArrayList();
    return localList;
  }

  synchronized void putAddedURL(URL paramURL)
  {
    this.addedURLs.add(paramURL);
  }

  public CodeSource[] getTrustedCodeSources(CodeSource[] paramArrayOfCodeSource)
  {
    ArrayList localArrayList = new ArrayList();
    Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
    {
      CodeSource localCodeSource = paramArrayOfCodeSource[i];
      boolean bool = false;
      PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
      try
      {
        bool = this._appPolicy.addPermissions(getInstance(), localPermissionCollection, localCodeSource, true);
      }
      catch (ExitException localExitException)
      {
        throw new RuntimeException(localExitException);
      }
      if (!bool)
        bool = localPermissionCollection.implies(new AllPermission());
      if (!bool)
        bool = isTrustedByPolicy(localPolicy, localCodeSource);
      if (bool)
        localArrayList.add(localCodeSource);
    }
    return (CodeSource[])localArrayList.toArray(new CodeSource[localArrayList.size()]);
  }

  private boolean isTrustedByPolicy(Policy paramPolicy, CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection1 = paramPolicy.getPermissions(paramCodeSource);
    PermissionCollection localPermissionCollection2 = paramPolicy.getPermissions(new CodeSource(null, (Certificate[])null));
    Enumeration localEnumeration = localPermissionCollection1.elements();
    while (localEnumeration.hasMoreElements())
    {
      Permission localPermission = (Permission)localEnumeration.nextElement();
      if (!localPermissionCollection2.implies(localPermission))
        return true;
    }
    return false;
  }

  public BasicService getBasicService()
  {
    return BasicServiceImpl.getInstance();
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
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.jnlp.JNLPClassLoader
 * JD-Core Version:    0.6.2
 */