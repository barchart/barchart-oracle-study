package sun.plugin2.applet;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JRESelectException;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.util.JfxHelper;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.plugin.cache.JarCacheUtil;
import sun.plugin2.main.server.JVMHealthData;

public class Applet2Manager extends Plugin2Manager
{
  private static final String VERSION_TAG = "version=";
  private static final String PRELOAD = "preload";
  private boolean initializedJarVersionMap;
  private Map jarVersionMap = new HashMap();
  private Map preloadJarMap = new HashMap();
  private List newStyleJarList = new ArrayList();
  private Applet2ClassLoaderCache classLoaderCache;
  private volatile Applet2ClassLoaderCache.Entry classLoaderCacheEntry;
  private volatile String classLoaderCacheKey;
  private Applet2ManagerCache instanceCache;
  private boolean usingLegacyLifeCycle;
  private String legacyCacheKey;
  private Applet2StopListener legacyStopListener;

  public Applet2Manager(Applet2ClassLoaderCache paramApplet2ClassLoaderCache, Applet2ManagerCache paramApplet2ManagerCache, boolean paramBoolean)
  {
    super(paramBoolean);
    this.classLoaderCache = paramApplet2ClassLoaderCache;
    this.instanceCache = paramApplet2ManagerCache;
  }

  public void setAppletExecutionContext(Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    super.setAppletExecutionContext(paramApplet2ExecutionContext);
    String str = getParameter("legacy_lifecycle");
    if ((str != null) && (str.equalsIgnoreCase("true")))
    {
      this.usingLegacyLifeCycle = true;
      this.legacyCacheKey = this.instanceCache.getCacheKey(getDocumentBase().toString(), paramApplet2ExecutionContext.getAppletParameters());
    }
    else
    {
      this.usingLegacyLifeCycle = false;
    }
  }

  public Plugin2ClassLoader getAppletClassLoader()
  {
    synchronized (this)
    {
      if (this.loader == null)
      {
        Applet2ClassLoaderCache.Entry localEntry = getClassLoaderCacheEntry();
        if (localEntry != null)
        {
          this.loader = localEntry.getClassLoader();
          if (this.loader == null)
            throw new InternalError("Error during bootstrapping of ClassLoader");
          setupClassLoaderCodebaseRecursiveRead(this.loader);
        }
        else
        {
          this.loader = getOrCreatePlugin2ClassLoader();
        }
      }
      return this.loader;
    }
  }

  public ThreadGroup getAppletThreadGroup()
  {
    synchronized (this)
    {
      if (this.appletThreadGroup == null)
      {
        Applet2ClassLoaderCache.Entry localEntry = getClassLoaderCacheEntry();
        if (localEntry != null)
        {
          this.appletThreadGroup = localEntry.getThreadGroup();
          if (this.appletThreadGroup == null)
            throw new InternalError("Error during bootstrapping of ThreadGroup");
        }
        else
        {
          this.appletThreadGroup = getOrCreateAppletThreadGroup();
        }
      }
      return this.appletThreadGroup;
    }
  }

  public AppContext getAppletAppContext()
  {
    synchronized (this)
    {
      if (this.appletAppContext == null)
      {
        Applet2ClassLoaderCache.Entry localEntry = getClassLoaderCacheEntry();
        if (localEntry != null)
        {
          this.appletAppContext = localEntry.getAppContext();
          if (this.appletAppContext == null)
            throw new InternalError("Error during bootstrapping of AppContext");
        }
        else
        {
          this.appletAppContext = getOrCreateAppletAppContext();
        }
        registerInAppContext(this.appletAppContext);
      }
      return this.appletAppContext;
    }
  }

  public String getAppletUniqueKey()
  {
    String str1 = "|";
    URL localURL1 = getDocumentBase();
    if (localURL1 != null)
      str1 = str1 + localURL1.toString();
    str1 = str1 + "|";
    URL localURL2 = getCodeBase();
    if (localURL2 != null)
      str1 = str1 + localURL2.toString();
    str1 = str1 + "|";
    String str2 = getJarFiles();
    if (str2 != null)
      str1 = str1 + str2;
    str1 = str1 + "|";
    return str1;
  }

  public String getLegacyLifeCycleCacheKey()
  {
    if (!this.usingLegacyLifeCycle)
      throw new IllegalStateException("Only legal for applets using the legacy lifecycle");
    return this.legacyCacheKey;
  }

  public void destroy()
  {
    if (!this.usingLegacyLifeCycle)
      throw new IllegalStateException("May only call destroy() for applets using the legacy lifecycle");
    long l1 = getAppletStopTimeout();
    final Plugin2ClassLoader localPlugin2ClassLoader = getAppletClassLoader();
    ThreadGroup localThreadGroup = getAppletThreadGroup();
    String str = "thread destroy applet-" + getCode();
    final Thread localThread = new Thread(localThreadGroup, new Runnable()
    {
      public void run()
      {
        try
        {
          Applet2Manager.this.getApplet2Adapter().destroy();
        }
        catch (Throwable localThrowable)
        {
          Applet2Manager.this.invalidateClassLoaderCacheEntry();
          localThrowable.printStackTrace();
        }
        synchronized (Applet2Manager.this.stopLock)
        {
          Applet2Manager.this.stopLock.notifyAll();
        }
      }
    }
    , str);
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Thread val$destroyThread;
      private final Plugin2ClassLoader val$loader;

      public Object run()
      {
        localThread.setContextClassLoader(localPlugin2ClassLoader);
        return null;
      }
    });
    long l2 = System.currentTimeMillis();
    synchronized (this.stopLock)
    {
      localThread.start();
      try
      {
        this.stopLock.wait(l1);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    unregisterFromAppContext(this.appletAppContext);
    cleanupAppContext(l2, l1, this.legacyStopListener);
    if (getApplet2Adapter() != null)
      getApplet2Adapter().cleanup();
  }

  public int getPermissionRequestType()
  {
    String str = getParameter("permissions");
    if ("all-permissions".equals(str))
      return 2;
    if ("sandbox".equals(str))
      return 1;
    return 0;
  }

  public AppInfo getAppInfo()
  {
    AppInfo localAppInfo = new AppInfo();
    String str = getParameter("java_applet_title");
    if ((str == null) || (str.length() == 0))
      str = getName();
    localAppInfo.setTitle(str);
    localAppInfo.setFrom(getCodeBase());
    try
    {
      localAppInfo.setLapURL(new URL(getCodeBase().toString() + "/" + localAppInfo.getTitle()));
    }
    catch (Exception localException)
    {
      localAppInfo.setLapURL(localAppInfo.getFrom());
      Trace.ignored(localException);
    }
    localAppInfo.setSecurity(getPermissionRequestType());
    return localAppInfo;
  }

  protected void shutdownAppContext(AppContext paramAppContext, long paramLong1, long paramLong2, Applet2StopListener paramApplet2StopListener, boolean paramBoolean)
  {
    if (!this.usingLegacyLifeCycle)
    {
      super.shutdownAppContext(paramAppContext, paramLong1, paramLong2, paramApplet2StopListener, paramBoolean);
    }
    else
    {
      this.legacyStopListener = paramApplet2StopListener;
      if (paramBoolean)
      {
        this.instanceCache.put(this);
        synchronized (this.stopLock)
        {
          this.shouldStop = false;
          this.stopSuccessful = false;
        }
      }
      else
      {
        destroy();
      }
    }
  }

  protected void cleanupAppContext(long paramLong1, long paramLong2, Applet2StopListener paramApplet2StopListener)
  {
    Applet2ClassLoaderCache localApplet2ClassLoaderCache = null;
    Applet2ClassLoaderCache.Entry localEntry = null;
    AppContext localAppContext = null;
    synchronized (this)
    {
      localAppContext = this.appletAppContext;
      this.appletAppContext = null;
      localApplet2ClassLoaderCache = this.classLoaderCache;
      this.classLoaderCache = null;
      localEntry = this.classLoaderCacheEntry;
      this.classLoaderCacheEntry = null;
    }
    long l = paramLong2 - (System.currentTimeMillis() - paramLong1);
    if (localEntry != null)
    {
      assert (localApplet2ClassLoaderCache != null);
      synchronized (this)
      {
        localApplet2ClassLoaderCache.release(localEntry, this, paramApplet2StopListener, l);
      }
    }
    else
    {
      destroyAppContext(localAppContext, paramApplet2StopListener, l);
    }
  }

  protected Plugin2ClassLoader newClassLoader()
  {
    URL localURL = getCodeBase();
    Applet2ClassLoader localApplet2ClassLoader = Applet2ClassLoader.newInstance(localURL);
    if (isForDummyApplet())
      localApplet2ClassLoader.setCodebaseLookup(false);
    return localApplet2ClassLoader;
  }

  protected synchronized void initJarVersionMap()
  {
    if (this.initializedJarVersionMap)
      return;
    this.initializedJarVersionMap = true;
    int i = 1;
    String str1 = getParameter("archive_" + i);
    int k;
    Object localObject3;
    if (str1 != null)
      while (str1 != null)
      {
        localObject1 = splitJarList(str1, false);
        localObject2 = null;
        str2 = null;
        int j = 0;
        for (k = 0; k < localObject1.length; k++)
        {
          localObject3 = localObject1[k];
          if (localObject2 == null)
          {
            localObject2 = localObject3;
          }
          else
          {
            localObject3 = ((String)localObject3).toLowerCase();
            if (((String)localObject3).startsWith("version="))
              str2 = ((String)localObject3).substring("version=".length());
            else if (((String)localObject3).equals("preload"))
              j = 1;
          }
        }
        if (localObject2 != null)
        {
          if (j != 0)
            this.preloadJarMap.put(localObject2, str2);
          this.jarVersionMap.put(localObject2, str2);
          this.newStyleJarList.add(localObject2);
        }
        i++;
        str1 = getParameter("archive_" + i);
      }
    Object localObject1 = getParameter("cache_archive");
    Object localObject2 = getParameter("cache_version");
    String str2 = getParameter("cache_archive_ex");
    try
    {
      this.jarVersionMap = JarCacheUtil.getJarsWithVersion((String)localObject1, (String)localObject2, str2);
    }
    catch (Exception localException)
    {
      Trace.printException(localException, ResourceManager.getMessage("cache.error.text"), ResourceManager.getMessage("cache.error.caption"));
    }
    if (str2 != null)
    {
      String[] arrayOfString = splitJarList(str2, false);
      for (k = 0; k < arrayOfString.length; k++)
      {
        localObject3 = splitOptionString(arrayOfString[k]);
        if ((localObject3.length > 1) && (localObject3[1] != null) && (localObject3[1].toLowerCase().indexOf("preload") != -1))
        {
          Object localObject4 = null;
          if (localObject3.length > 2)
            localObject4 = localObject3[2];
          this.preloadJarMap.put(localObject3[0], localObject4);
        }
      }
    }
  }

  private void storeJarVersionMapInAppContext()
  {
    Iterator localIterator = this.jarVersionMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)this.jarVersionMap.get(str1);
      URL localURL = null;
      try
      {
        localURL = new URL(getCodeBase(), str1);
        if (!URLUtil.checkTargetURL(getCodeBase(), localURL))
          throw new SecurityException("Permission denied: " + localURL);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignoredException(localMalformedURLException);
      }
      if (localURL != null)
        this.appletAppContext.put("deploy-" + localURL.toString(), str2);
    }
  }

  protected void setupAppletAppContext()
  {
    storeJarVersionMapInAppContext();
    super.setupAppletAppContext();
  }

  private void installJfxIfNeeded()
    throws ExitException
  {
    String str = getParameter("javafx_version");
    if (str == null)
      return;
    if (ToolkitStore.isUsingPreferredToolkit(11, 1))
      return;
    if (super.isAppletRelaunched())
      throw new ExitException(new Throwable("Cannot initiate FX Toolkit for plugin mode."), 3);
    try
    {
      URL localURL = new URL("http://javaweb.sfbay.sun.com/~hj156752/awtless/fx/installer/fxinstaller.jnlp");
      JfxHelper.installJfxRuntime(localURL, str, getPreloaderDelegate());
    }
    catch (Throwable localThrowable)
    {
      throw new ExitException(localThrowable, 3);
    }
    setParameter("__jfx_installed", String.valueOf(true));
    appletRelaunch(null);
  }

  protected void performDesktopIntegration()
  {
  }

  protected void loadJarFiles()
    throws ExitException
  {
    int i = ResourceProvider.get().incrementInternalUse();
    try
    {
      _loadJarFiles();
    }
    finally
    {
      ResourceProvider.get().decrementInternalUse(i);
    }
  }

  private void _loadJarFiles()
    throws ExitException
  {
    installJfxIfNeeded();
    try
    {
      JarCacheUtil.preload(getCodeBase(), (HashMap)this.preloadJarMap);
    }
    catch (Exception localException)
    {
      Trace.printException(localException, ResourceManager.getMessage("cache.error.text"), ResourceManager.getMessage("cache.error.caption"));
    }
    try
    {
      String str1 = getJarFiles();
      localObject1 = (Applet2ClassLoader)getAppletClassLoader();
      Object localObject2 = File.separator;
      String str2 = System.getProperty("java.home") + (String)localObject2 + "lib" + (String)localObject2 + "applet";
      loadLocalJarFiles((Applet2ClassLoader)localObject1, str2);
      if (Config.getOSName().equalsIgnoreCase("Windows"))
      {
        String str3 = Config.getSystemHome() + (String)localObject2 + "Lib" + (String)localObject2 + "Untrusted";
        loadLocalJarFiles((Applet2ClassLoader)localObject1, str3);
      }
      if (str1 == null)
        return;
      localObject2 = splitJarList(str1, false);
      for (int j = 0; j < localObject2.length; j++)
        ((Applet2ClassLoader)localObject1).addJar(localObject2[j]);
    }
    catch (Throwable localThrowable)
    {
      Object localObject1 = (localThrowable instanceof ExitException) ? (ExitException)localThrowable : new ExitException(localThrowable, 3);
      int i = ((ExitException)localObject1).getReason() == 0 ? 0 : -1;
      if (i != 0)
        throw ((Throwable)localObject1);
    }
  }

  private void appletRelaunch(JREDesc paramJREDesc)
    throws JRESelectException
  {
    String str = System.getProperty("javaplugin.vm.options");
    throw new JRESelectException(paramJREDesc, str);
  }

  protected void appletSSVRelaunch()
    throws JRESelectException
  {
    JREDesc localJREDesc = new JREDesc(getParameter("__applet_ssv_version"), 0L, 0L, null, null, null);
    appletRelaunch(localJREDesc);
  }

  protected void checkRunningJVMToolkitSatisfying()
    throws JRESelectException
  {
  }

  protected void checkRunningJVMArgsSatisfying()
    throws JRESelectException
  {
    if (super.isAppletRelaunched())
      return;
    JVMHealthData localJVMHealthData = JVMHealthData.getCurrent();
    if (!localJVMHealthData.isHealthy())
    {
      if (DEBUG)
        System.out.println("Relaunch due to unhealthy JVM: " + localJVMHealthData);
      appletRelaunch(new JREDesc(null, 0L, 0L, null, null, null));
    }
  }

  private void loadLocalJarFiles(Applet2ClassLoader paramApplet2ClassLoader, String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.exists())
    {
      String[] arrayOfString = localFile.list(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".jar");
        }
      });
      for (int i = 0; i < arrayOfString.length; i++)
        try
        {
          URL localURL = new File(paramString + File.separator + arrayOfString[i]).toURI().toURL();
          paramApplet2ClassLoader.addLocalJar(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
    }
  }

  protected String getJarFiles()
  {
    if (!this.newStyleJarList.isEmpty())
      return buildJarList((String[])this.newStyleJarList.toArray(new String[0]));
    String str1 = getParameter("cache_archive_ex");
    if (str1 != null)
    {
      int i = str1.indexOf(";");
      if (i >= 0)
        str1 = buildJarList(splitJarList(str1, true));
    }
    String str2 = buildJarList(new String[] { str1, getParameter("cache_archive"), getParameter("java_archive"), getParameter("archive") });
    if (DEBUG)
      System.out.println("Applet2Manager.getJarFiles() for applet ID " + this.appletID + " Jar Files:" + str2);
    return str2;
  }

  protected String getCodeSourceLocations()
  {
    return getJarFiles();
  }

  private synchronized Applet2ClassLoaderCache.Entry getClassLoaderCacheEntry()
  {
    if (this.classLoaderCache == null)
      return null;
    if (this.classLoaderCacheEntry == null)
    {
      initJarVersionMap();
      verifyJarVersions();
      this.classLoaderCacheEntry = this.classLoaderCache.get(getClassLoaderCacheKey(), this);
      if (DEBUG)
        System.out.println("Applet2Manager.getClassLoaderCacheEntry() for applet ID " + this.appletID + ": ClassLoader=" + objToString(this.classLoaderCacheEntry.getClassLoader()) + ", ThreadGroup=" + objToString(this.classLoaderCacheEntry.getThreadGroup()) + ", AppContext=" + objToString(this.classLoaderCacheEntry.getAppContext()));
    }
    return this.classLoaderCacheEntry;
  }

  protected synchronized void invalidateClassLoaderCacheEntry()
  {
    if ((this.classLoaderCache != null) && (this.classLoaderCacheEntry != null))
      this.classLoaderCache.markNotCacheable(this.classLoaderCacheEntry);
  }

  protected boolean usingLegacyLifeCycle()
  {
    return this.usingLegacyLifeCycle;
  }

  protected void clearUsingLegacyLifeCycle()
  {
    this.usingLegacyLifeCycle = false;
  }

  private static String objToString(Object paramObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramObject.getClass().getName());
    localStringBuffer.append("@~0x");
    localStringBuffer.append(Integer.toHexString(System.identityHashCode(paramObject)));
    return localStringBuffer.toString();
  }

  public static Applet2ClassLoaderCache.EntryCreator getCacheEntryCreator()
  {
    return new CacheEntryCreator();
  }

  private String getClassLoaderCacheKey()
  {
    if (this.classLoaderCacheKey == null)
    {
      String str1 = getParameter("classloader-policy");
      String str2 = getParameter("permissions");
      if ((str2 == null) && (str1 != null) && (str1.equals("classic")))
      {
        this.classLoaderCacheKey = getCodeBase().toString();
      }
      else
      {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(getCodeBase());
        String str3 = getJarFiles();
        if (str3 != null)
        {
          localStringBuffer.append(",");
          localStringBuffer.append(str3);
        }
        if (str2 != null)
        {
          localStringBuffer.append(",");
          localStringBuffer.append(str2);
        }
        this.classLoaderCacheKey = localStringBuffer.toString();
      }
    }
    return this.classLoaderCacheKey;
  }

  private void verifyJarVersions()
  {
    assert (this.classLoaderCache != null);
    int i = 0;
    URL localURL1 = getCodeBase();
    Iterator localIterator = this.jarVersionMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)this.jarVersionMap.get(str1);
      try
      {
        URL localURL2 = new URL(localURL1, str1);
        if (!URLUtil.checkTargetURL(localURL1, localURL2))
          throw new SecurityException("Permission denied: " + localURL2);
        Trace.msgNetPrintln("cache.version_checking", new Object[] { str1, str2 });
        if (str2 != null)
        {
          String str3 = ResourceProvider.get().getCurrentVersion(localURL2);
          if ((str3 != null) && (str3.compareTo(str2) != 0))
          {
            i = 1;
            break;
          }
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        if (DEBUG)
          localMalformedURLException.printStackTrace();
      }
    }
    if (i != 0)
      this.classLoaderCache.markNotCacheable(getClassLoaderCacheKey());
  }

  static class CacheEntryCreator
    implements Applet2ClassLoaderCache.EntryCreator
  {
    public void createAll(Applet2Manager paramApplet2Manager, Applet2ClassLoaderCache.Entry paramEntry)
    {
      if (Plugin2Manager.DEBUG)
        System.out.println("Applet2Manager executing createAll() for entry " + paramEntry.getClassLoaderCacheKey());
      paramEntry.setClassLoader((Applet2ClassLoader)paramApplet2Manager.getOrCreatePlugin2ClassLoader());
      paramEntry.setThreadGroup(paramApplet2Manager.getOrCreateAppletThreadGroup());
      paramEntry.setAppContext(paramApplet2Manager.getOrCreateAppletAppContext());
    }

    public void createThreadGroupAndAppContext(Applet2Manager paramApplet2Manager, Applet2ClassLoaderCache.Entry paramEntry)
    {
      if (Plugin2Manager.DEBUG)
        System.out.println("Applet2Manager executing createTGAndAC() for entry " + paramEntry.getClassLoaderCacheKey());
      paramApplet2Manager.loader = paramEntry.getClassLoader();
      if (paramApplet2Manager.loader == null)
        throw new InternalError("Error during bootstrapping of new ThreadGroup and AppContext");
      paramEntry.setThreadGroup(paramApplet2Manager.getOrCreateAppletThreadGroup());
      paramEntry.setAppContext(paramApplet2Manager.getOrCreateAppletAppContext());
      Applet2ClassLoader localApplet2ClassLoader = paramEntry.getClassLoader();
      localApplet2ClassLoader.setThreadGroup(paramEntry.getThreadGroup());
      localApplet2ClassLoader.setAppContext(paramEntry.getAppContext());
      paramApplet2Manager.setupClassLoaderCodebaseRecursiveRead(localApplet2ClassLoader);
    }

    public void destroyThreadGroupAndAppContext(Applet2Manager paramApplet2Manager, Applet2StopListener paramApplet2StopListener, long paramLong, Applet2ClassLoaderCache.Entry paramEntry)
    {
      if (Plugin2Manager.DEBUG)
        System.out.println("Applet2Manager executing destroyTGAndAC() for entry " + paramEntry.getClassLoaderCacheKey());
      paramApplet2Manager.destroyAppContext(paramEntry.getAppContext(), paramApplet2StopListener, paramLong);
      paramEntry.setThreadGroup(null);
      paramEntry.setAppContext(null);
      Applet2ClassLoader localApplet2ClassLoader = paramEntry.getClassLoader();
      localApplet2ClassLoader.setThreadGroup(null);
      localApplet2ClassLoader.setAppContext(null);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2Manager
 * JD-Core Version:    0.6.2
 */