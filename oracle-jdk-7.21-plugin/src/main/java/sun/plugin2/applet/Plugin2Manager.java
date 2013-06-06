package sun.plugin2.applet;

import com.sun.applet2.AppletParameters;
import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.event.AppletInitEvent;
import com.sun.applet2.preloader.event.ApplicationExitEvent;
import com.sun.applet2.preloader.event.ErrorEvent;
import com.sun.applet2.preloader.event.PreloaderEvent;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.net.JARSigningException;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.SandboxSecurity;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.Window.DisposeListener;
import com.sun.deploy.uitoolkit.impl.awt.AWTPluginEmbeddedFrameWindow;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.Property;
import com.sun.deploy.util.ReflectionUtil;
import com.sun.deploy.util.StringUtils;
import com.sun.deploy.util.SystemUtils;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.exceptions.JRESelectException;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.progress.PreloaderDelegate;
import com.sun.javaws.progress.PreloaderPostEventListener;
import com.sun.javaws.progress.Progress;
import java.awt.Container;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import sun.plugin.util.ProgressMonitor;
import sun.plugin.util.ProgressMonitorAdapter;
import sun.plugin2.applet2.Plugin2Context;
import sun.plugin2.applet2.Plugin2Host;
import sun.plugin2.main.client.DisconnectedExecutionContext;
import sun.plugin2.perf.Plugin2Rollup;
import sun.plugin2.util.AppletEnumeration;
import sun.plugin2.util.SystemUtil;

public abstract class Plugin2Manager
{
  protected static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  protected static final boolean VERBOSE = SystemUtil.getenv("JPI_PLUGIN2_VERBOSE") != null;
  private static boolean isEmbedded = true;
  public static final String APPCONTEXT_APPLETCONTEXT_KEY = "AppletContextKey";
  public static final String APPCONTEXT_PLUGIN2CTX_KEY = "Plugin2CtxKey";
  public static final String APPCONTEXT_APPLET2MANAGER_LIST_KEY = "Plugin2ManagerListKey";
  private static final ThreadLocal currentThreadManager = new ThreadLocal();
  private static final InheritableThreadLocal currentManagerThreadLocal = new InheritableThreadLocal();
  private static long appletLaunchTime = -1L;
  private static long appletLaunchCosts = -1L;
  private static long jvmLaunchTime = -1L;
  private static long jvmLaunchCosts = -1L;
  private static RuntimePermission usePolicyPermission;
  private Plugin2Host pluginHost;
  private final Plugin2Context pluginContext;
  private static final AppletEnumeration applets = new AppletEnumeration();
  private static int modalityLevel = 0;
  private ProgressMonitorAdapter progressListener = null;
  private AppletParameters paramsToRelaunch;
  private volatile boolean appletStartResponseSent;
  private volatile boolean errorOccurred;
  private volatile Throwable errorException;
  private volatile String errorMessage;
  private Applet2ExecutionContext appletExecutionContext;
  private static Applet2ExecutionContext defaultAppletExecutionContext;
  private Map appletParameters;
  private URL codebase;
  private boolean gotSize = false;
  private int width = 10;
  private int height = 10;
  private volatile Thread appletExecutionThread;
  protected volatile ThreadGroup appletThreadGroup;
  protected volatile AppContext appletAppContext;
  protected volatile Plugin2ClassLoader loader;
  protected final Object stopLock = new Object();
  protected volatile boolean shouldStop = false;
  protected volatile boolean stopSuccessful = false;
  protected static final int STOP_TIMEOUT_CONFIG = Config.getIntProperty("deployment.javapi.stop.timeout") == -1 ? 200 : Config.getIntProperty("deployment.javapi.stop.timeout");
  protected static final long THREADDIE_TIMEOUT = 3000L;
  private final Applet2Adapter adapter;
  private volatile boolean appletIsActive;
  private volatile Container appletParentContainer;
  private volatile com.sun.deploy.uitoolkit.Window parentWindow;
  protected boolean doInit = true;
  private List listeners = new ArrayList();
  protected static Applet2MessageHandler amh = new Applet2MessageHandler("appletpanel");
  protected Integer appletID;
  private static int sequenceNumber = 0;
  private boolean isForDummyApplet;
  private boolean disconnected;
  protected boolean isSecureVM = true;
  private boolean _appletRelaunched = false;
  private boolean _trustedApplet = false;
  private volatile Object appletStartLock = null;
  private volatile int java2JSCounter = 1;
  private static final boolean _INJECT_EXCEPTION_CREATEAPPLET = bool1;
  private static final boolean _INJECT_CREATEAPPLET_NULL = bool2;
  private static final boolean _INJECT_DELAY_APPLETLOADED = bool3;
  private static final boolean _INJECT_NEVER_APPLETLOADED = bool4;
  private boolean waitedOnce = false;

  public static void setEmbeddedMode(boolean paramBoolean)
  {
    isEmbedded = paramBoolean;
  }

  public static boolean isEmbedded()
  {
    return isEmbedded;
  }

  public void increaseModalityLevel()
  {
    modalityLevel += 1;
  }

  public void decreaseModalityLevel()
  {
    modalityLevel -= 1;
  }

  public int getModalityLevel()
  {
    return modalityLevel;
  }

  public static void setAppletLaunchTime(long paramLong1, long paramLong2)
  {
    if (appletLaunchTime == -1L)
    {
      appletLaunchTime = paramLong1;
      appletLaunchCosts = paramLong2;
    }
  }

  public static long getAppletLaunchCosts()
  {
    return appletLaunchCosts;
  }

  public static long getAppletLaunchTime()
  {
    return appletLaunchTime;
  }

  public static void setJVMLaunchTime(long paramLong1, long paramLong2)
  {
    if (jvmLaunchTime == -1L)
    {
      jvmLaunchTime = paramLong1;
      jvmLaunchCosts = paramLong2;
    }
  }

  public static long getJVMLaunchCosts()
  {
    return jvmLaunchCosts;
  }

  public static long getJVMLaunchTime()
  {
    return jvmLaunchTime;
  }

  public static Plugin2Manager getCurrentManager()
  {
    Plugin2Manager localPlugin2Manager = getFromThreadLocal();
    if ((localPlugin2Manager == null) || (localPlugin2Manager.isStopped()))
      localPlugin2Manager = getFromAppContext();
    if (localPlugin2Manager != null)
      return localPlugin2Manager;
    return (Plugin2Manager)currentThreadManager.get();
  }

  public static void setCurrentManagerThreadLocal(Plugin2Manager paramPlugin2Manager)
  {
    currentManagerThreadLocal.set(paramPlugin2Manager);
  }

  public void forceReloadApplet()
  {
    invalidateClassLoaderCacheEntry();
    this.pluginHost.showDocument(getDocumentBase());
  }

  public Plugin2Manager(boolean paramBoolean)
  {
    this._appletRelaunched = paramBoolean;
    this.pluginContext = new Plugin2Context(this);
    this.adapter = ToolkitStore.get().getApplet2Adapter(this.pluginContext);
  }

  synchronized void uninstallProgressListener()
  {
    try
    {
      if (this.progressListener != null)
      {
        ProgressMonitor localProgressMonitor = ProgressMonitor.get();
        localProgressMonitor.removeProgressListener(this.appletThreadGroup, this.progressListener);
        this.progressListener = null;
      }
    }
    catch (Throwable localThrowable)
    {
    }
  }

  protected synchronized void setupProgress()
  {
    Trace.println(ResourceManager.getMessage("console.trace.plugin.preloader.default"), TraceLevel.PRELOADER);
    final PreloaderDelegate localPreloaderDelegate = getPreloaderDelegate();
    localPreloaderDelegate.markLoadingStarted();
    Thread localThread = new Thread(new Runnable()
    {
      private final PreloaderDelegate val$delegate;

      public void run()
      {
        try
        {
          localPreloaderDelegate.initPreloader(Plugin2Manager.this.getAppletClassLoader(), Plugin2Manager.this.appletThreadGroup);
        }
        catch (Exception localException)
        {
          Trace.println(ResourceManager.getMessage("console.trace.plugin.preloader.error") + localException, TraceLevel.PRELOADER);
          Trace.ignored(localException);
        }
      }
    });
    localThread.start();
    try
    {
      ProgressMonitor localProgressMonitor = ProgressMonitor.get();
      this.progressListener = new ProgressMonitorAdapter(getPreloaderDelegate());
      this.progressListener.setProgressFilter(getCodeBase(), getJarFiles());
      localProgressMonitor.addProgressListener(this.appletThreadGroup, this.progressListener);
    }
    catch (Throwable localThrowable)
    {
      Trace.println(ResourceManager.getMessage("console.trace.plugin.monitor.failed"), TraceLevel.PRELOADER);
      Trace.ignored(localThrowable);
    }
  }

  public void initialize()
    throws Exception
  {
    setParameter("__applet_relaunched", String.valueOf(this._appletRelaunched));
    collectJnlpProperties();
    JVMParameters localJVMParameters = JVMParameters.getRunningJVMParameters();
    setParameter("java_arguments", localJVMParameters != null ? localJVMParameters.getCommandLineArgumentsAsString(false) : "");
  }

  public void setAppletExecutionContext(Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    this.appletExecutionContext = paramApplet2ExecutionContext;
    this.pluginHost = new Plugin2Host(this);
    this.pluginContext.setHost(this.pluginHost);
  }

  public Applet2ExecutionContext getAppletExecutionContext()
  {
    return this.appletExecutionContext;
  }

  public boolean isDisconnectedExecutionContext()
  {
    return this.appletExecutionContext instanceof DisconnectedExecutionContext;
  }

  public static void setDefaultAppletExecutionContext(Applet2ExecutionContext paramApplet2ExecutionContext)
  {
    defaultAppletExecutionContext = paramApplet2ExecutionContext;
  }

  public static Applet2ExecutionContext getDefaultAppletExecutionContext()
  {
    return defaultAppletExecutionContext;
  }

  public static Applet2ExecutionContext getCurrentAppletExecutionContext()
  {
    Plugin2Manager localPlugin2Manager = getCurrentManager();
    if (localPlugin2Manager != null)
      return localPlugin2Manager.getAppletExecutionContext();
    return defaultAppletExecutionContext;
  }

  public String getParameter(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    Map localMap = getAppletParameters();
    synchronized (localMap)
    {
      return StringUtils.trimWhitespace((String)localMap.get(paramString));
    }
  }

  public void setParameter(String paramString, Object paramObject)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    Map localMap = getAppletParameters();
    synchronized (localMap)
    {
      localMap.put(paramString, StringUtils.trimWhitespace(paramObject.toString()));
    }
  }

  public URL getDocumentBase()
  {
    String str = this.appletExecutionContext.getDocumentBase(this);
    if (str == null)
      return null;
    try
    {
      return new URL(str);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new RuntimeException(localMalformedURLException);
    }
  }

  public Plugin2ClassLoader getAppletClassLoader()
  {
    synchronized (this)
    {
      if (this.loader == null)
        this.loader = getOrCreatePlugin2ClassLoader();
      return this.loader;
    }
  }

  protected void setupClassLoaderCodebaseRecursiveRead(Plugin2ClassLoader paramPlugin2ClassLoader)
  {
    URL localURL = getDocumentBase();
    if ((localURL == null) || (!localURL.getProtocol().equalsIgnoreCase("file")) || (URLUtil.isUNCFileURL(localURL)))
      paramPlugin2ClassLoader.disableRecursiveDirectoryRead();
  }

  public ThreadGroup getAppletThreadGroup()
  {
    synchronized (this)
    {
      if (this.appletThreadGroup == null)
        this.appletThreadGroup = getOrCreateAppletThreadGroup();
      return this.appletThreadGroup;
    }
  }

  public AppContext getAppletAppContext()
  {
    synchronized (this)
    {
      if (this.appletAppContext == null)
      {
        this.appletAppContext = getOrCreateAppletAppContext();
        registerInAppContext(this.appletAppContext);
      }
      return this.appletAppContext;
    }
  }

  public synchronized void setAppletParentContainer(Container paramContainer)
  {
    this.appletParentContainer = paramContainer;
  }

  public synchronized void setAppletParent(com.sun.deploy.uitoolkit.Window paramWindow)
  {
    Object localObject = paramWindow.getWindowObject();
    this.parentWindow = paramWindow;
    this.adapter.setParentContainer(paramWindow);
    if ((localObject instanceof Container))
      setAppletParentContainer((Container)localObject);
    if ((this.appletAppContext != null) && (ReflectionUtil.isSubclassOf(this.parentWindow, "com.sun.deploy.uitoolkit.impl.awt.AWTPluginEmbeddedFrameWindow")))
      ((AWTPluginEmbeddedFrameWindow)this.parentWindow).setAppContext(this.appletAppContext);
  }

  public com.sun.deploy.uitoolkit.Window getAppletParent()
  {
    return this.parentWindow;
  }

  public Container getAppletParentContainer()
  {
    return this.appletParentContainer;
  }

  public Applet2Adapter getApplet2Adapter()
  {
    return this.adapter;
  }

  protected PreloaderDelegate getPreloaderDelegate()
  {
    return Progress.get(this.adapter);
  }

  public Applet2Adapter getAppletAdapter(String paramString)
  {
    return applets.getApplet2Adapter(this, paramString);
  }

  public Enumeration getAppletAdapters()
  {
    return applets.getApplet2Adapters(this);
  }

  public boolean isAppletStarted()
  {
    return this.appletIsActive;
  }

  public synchronized Applet2Status getAppletStatus()
  {
    if ((!this.adapter.isInstantiated()) && (!hasErrorOccurred()))
    {
      Exception localException = new Exception("InternalError: LiveConnect GetApplet issued before appletLoaded");
      setErrorOccurred(localException);
      showAppletException(localException);
      fireAppletErrorOccurred();
    }
    return new Applet2Status(getApplet2Adapter(), hasErrorOccurred(), getErrorMessage(), getErrorException());
  }

  public void startWorkerThread(String paramString, Runnable paramRunnable)
  {
    final Thread localThread = new Thread(getAppletThreadGroup(), paramRunnable, paramString);
    final Plugin2ClassLoader localPlugin2ClassLoader = getAppletClassLoader();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Thread val$worker;
      private final ClassLoader val$workerLoader;

      public Object run()
      {
        localThread.setContextClassLoader(localPlugin2ClassLoader);
        return null;
      }
    });
    localThread.start();
  }

  public void setForDummyApplet(boolean paramBoolean)
  {
    this.isForDummyApplet = paramBoolean;
  }

  public boolean isForDummyApplet()
  {
    return this.isForDummyApplet;
  }

  public void setDisconnected()
  {
    this.disconnected = true;
    disposeParentWindow(null, 0L);
  }

  public boolean isDisconnected()
  {
    return this.disconnected;
  }

  public void installShortcuts()
  {
  }

  public boolean isEagerInstall()
  {
    String str = getParameter("eager_install");
    return Boolean.valueOf(str).booleanValue();
  }

  public abstract String getAppletUniqueKey();

  public void checkUntrustedAccess()
    throws SecurityException
  {
    getCurrentManager().getAppletClassLoader().checkUntrustedAccess();
  }

  public void start()
    throws IllegalStateException
  {
    synchronized (this)
    {
      long l = DeployPerfUtil.put(0L, "Plugin2Manager - start() - BEGIN");
      if (this.appletExecutionThread != null)
        throw new IllegalStateException("Plugin2Manager already started");
      if ((isEmbedded()) && (this.parentWindow == null) && (!this.isForDummyApplet))
        throw new IllegalStateException("Applet's parent container not set up");
      final Plugin2ClassLoader localPlugin2ClassLoader = getAppletClassLoader();
      final ThreadGroup localThreadGroup = getAppletThreadGroup();
      final String str = "thread applet-" + getCode() + "-" + nextSequenceNumber();
      final AppletExecutionRunnable localAppletExecutionRunnable = new AppletExecutionRunnable();
      getPreloaderDelegate().setPostEventListener(localAppletExecutionRunnable);
      AccessController.doPrivileged(new PrivilegedAction()
      {
        private final ThreadGroup val$group;
        private final Plugin2Manager.AppletExecutionRunnable val$r;
        private final String val$name;
        private final Plugin2ClassLoader val$acl;

        public Object run()
        {
          Plugin2Manager.this.appletExecutionThread = new Thread(localThreadGroup, localAppletExecutionRunnable, str);
          Plugin2Manager.this.appletExecutionThread.setContextClassLoader(localPlugin2ClassLoader);
          return null;
        }
      });
      this.appletExecutionThread.start();
      DeployPerfUtil.put(l, "Plugin2Manager - start() - END");
    }
  }

  public boolean stop(Runnable paramRunnable)
  {
    return stop(paramRunnable, null);
  }

  public boolean stop(Runnable paramRunnable, Applet2StopListener paramApplet2StopListener)
  {
    long l1 = getAppletStopTimeout();
    long l2 = DeployPerfUtil.put(0L, "Plugin2Manager - stop() - BEGIN");
    this.appletIsActive = false;
    removeAllAppletListeners();
    if (hasErrorOccurred())
    {
      if (paramRunnable != null)
        try
        {
          paramRunnable.run();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable1.printStackTrace();
        }
      unregisterFromAppContext(this.appletAppContext);
      cleanupAppContext(System.currentTimeMillis(), l1, paramApplet2StopListener);
      if (getApplet2Adapter() != null)
        getApplet2Adapter().cleanup();
      DeployPerfUtil.put(l2, "Plugin2Manager - stop() - END(1)");
      return true;
    }
    boolean bool = false;
    DeployPerfUtil.put("Plugin2Manager - stop() - stopLock - pre ");
    long l3 = System.currentTimeMillis();
    synchronized (this.stopLock)
    {
      this.shouldStop = true;
      this.stopLock.notifyAll();
      try
      {
        this.stopLock.wait(l1);
        bool = this.stopSuccessful;
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    long l4 = l1 - (System.currentTimeMillis() - l3);
    DeployPerfUtil.put("Plugin2Manager - stop() - afterStopRunnable.run() - START");
    if (paramRunnable != null)
      try
      {
        paramRunnable.run();
      }
      catch (Throwable localThrowable2)
      {
        localThrowable2.printStackTrace();
      }
    DeployPerfUtil.put("Plugin2Manager - stop() - afterStopRunnable.run() - END");
    disposeParentWindow(paramApplet2StopListener, l4);
    DeployPerfUtil.put("Plugin2Manager - stop() - AWT disposal - post");
    shutdownAppContext(this.appletAppContext, l3, l1, paramApplet2StopListener, bool);
    DeployPerfUtil.put(l2, "Plugin2Manager - stop() - END(2)");
    return bool;
  }

  private boolean isStopped()
  {
    return this.stopSuccessful;
  }

  public boolean hasErrorOccurred()
  {
    return this.errorOccurred;
  }

  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  public Throwable getErrorException()
  {
    return this.errorException;
  }

  private boolean hasAppletStartResponseBeenSent()
  {
    return this.appletStartResponseSent;
  }

  private void setAppletStartResponseSent()
  {
    this.appletStartResponseSent = true;
  }

  protected long getAppletStopTimeout()
  {
    long l;
    try
    {
      l = Integer.parseInt(getParameter("applet_stop_timeout"));
      if (l > 3000L)
        l = 3000L;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      l = STOP_TIMEOUT_CONFIG;
    }
    return l;
  }

  protected void disposeParentWindow(final Applet2StopListener paramApplet2StopListener, long paramLong)
  {
    Container localContainer = null;
    synchronized (this)
    {
      localContainer = getAppletParentContainer();
      setAppletParentContainer(null);
    }
    ??? = new Window.DisposeListener()
    {
      private final Applet2StopListener val$stopListener;

      public void disposeFailed()
      {
        paramApplet2StopListener.stopFailed();
      }
    };
    Trace.println("plugin2manager.parentwindowDispose", TraceLevel.UI);
    if (this.parentWindow != null)
      this.parentWindow.dispose((Window.DisposeListener)???, paramLong);
  }

  protected void shutdownAppContext(AppContext paramAppContext, long paramLong1, long paramLong2, Applet2StopListener paramApplet2StopListener, boolean paramBoolean)
  {
    long l = DeployPerfUtil.put(0L, "Plugin2Manager - shutdownAppContext() - BEGIN");
    unregisterFromAppContext(this.appletAppContext);
    DeployPerfUtil.put("Plugin2Manager - shutdownAppContext() - unregisterFromAppContext() - post");
    cleanupAppContext(paramLong1, paramLong2, paramApplet2StopListener);
    if (getApplet2Adapter() != null)
      getApplet2Adapter().cleanup();
    DeployPerfUtil.put("Plugin2Manager - shutdownAppContext() - cleanupAppContext() - post");
    DeployPerfUtil.put(l, "Plugin2Manager - shutdownAppContext() - END");
  }

  protected void cleanupAppContext(long paramLong1, long paramLong2, Applet2StopListener paramApplet2StopListener)
  {
    AppContext localAppContext = null;
    synchronized (this)
    {
      localAppContext = this.appletAppContext;
      this.appletAppContext = null;
    }
    destroyAppContext(localAppContext, paramApplet2StopListener, paramLong2 - (System.currentTimeMillis() - paramLong1));
  }

  public synchronized void addAppletListener(Applet2Listener paramApplet2Listener)
  {
    this.listeners.add(paramApplet2Listener);
  }

  private synchronized void removeAllAppletListeners()
  {
    while (!this.listeners.isEmpty())
    {
      Applet2Listener localApplet2Listener = (Applet2Listener)this.listeners.get(0);
      this.listeners.remove(localApplet2Listener);
    }
  }

  public synchronized void removeAppletListener(Applet2Listener paramApplet2Listener)
  {
    this.listeners.remove(paramApplet2Listener);
  }

  public void setAppletSize(int paramInt1, int paramInt2)
  {
    if (this.parentWindow != null)
      this.parentWindow.setSize(paramInt1, paramInt2);
    setSize(paramInt1, paramInt2);
    synchronized (this)
    {
      notifyAll();
    }
  }

  public boolean isInSameAppContext(Plugin2Manager paramPlugin2Manager)
  {
    return isInSameAppContextImpl(paramPlugin2Manager);
  }

  public void setAppletID(Integer paramInteger)
  {
    this.appletID = paramInteger;
  }

  public Integer getAppletID()
  {
    return this.appletID;
  }

  public boolean isAppletRelaunched()
  {
    return this._appletRelaunched;
  }

  protected void collectJnlpProperties()
  {
    Property.collectsJnlpProperties(getParameter("java_arguments"), getOrCreateAppletAppContext());
  }

  public void setParametersToRelaunch(AppletParameters paramAppletParameters)
  {
    this.paramsToRelaunch = paramAppletParameters;
  }

  public AppletParameters getParametersToRelaunch()
  {
    return this.paramsToRelaunch;
  }

  private static int nextSequenceNumber()
  {
    synchronized (Plugin2Manager.class)
    {
      return ++sequenceNumber;
    }
  }

  public boolean isTrustedApplet()
  {
    return this._trustedApplet;
  }

  public void setSecureFlag(boolean paramBoolean)
  {
    this.isSecureVM = paramBoolean;
  }

  public boolean isVMSecure()
  {
    return this.isSecureVM;
  }

  public boolean isStopping()
  {
    return this.shouldStop;
  }

  private synchronized List copyListeners()
  {
    return (List)((ArrayList)this.listeners).clone();
  }

  protected boolean fireAppletSSVValidation()
    throws ExitException
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
      if (!((Applet2Listener)localIterator.next()).appletSSVValidation(this))
        return false;
    return true;
  }

  protected String fireGetBestJREVersion(String paramString1, String paramString2)
  {
    String str = null;
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      str = ((Applet2Listener)localIterator.next()).getBestJREVersion(this, paramString1, paramString2);
      if (str != null)
        return str;
    }
    return null;
  }

  protected boolean fireAppletRelaunchSupported()
  {
    boolean bool = true;
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
      if (!((Applet2Listener)localIterator.next()).isAppletRelaunchSupported())
        bool = false;
    return bool;
  }

  protected void fireAppletJRERelaunch(String paramString1, String paramString2)
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      ((Applet2Listener)localIterator.next()).appletJRERelaunch(this, paramString1, paramString2);
      setAppletStartResponseSent();
    }
  }

  private void fireAppletLoaded()
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      ((Applet2Listener)localIterator.next()).appletLoaded(this);
      setAppletStartResponseSent();
    }
  }

  private void fireAppletReady()
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
      ((Applet2Listener)localIterator.next()).appletReady(this);
  }

  private void fireAppletErrorOccurred()
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      ((Applet2Listener)localIterator.next()).appletErrorOccurred(this);
      setAppletStartResponseSent();
    }
  }

  protected Plugin2ClassLoader getOrCreatePlugin2ClassLoader()
  {
    synchronized (this)
    {
      if (this.loader == null)
      {
        currentThreadManager.set(this);
        try
        {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              Plugin2Manager.this.loader = Plugin2Manager.this.newClassLoader();
              return null;
            }
          });
          setupClassLoaderCodebaseRecursiveRead(this.loader);
          this.loader.setSecurityCheck(!this.isSecureVM);
          this.loader.setThreadGroup(getOrCreateAppletThreadGroup());
          this.loader.setAppContext(getOrCreateAppletAppContext());
          this.loader.setPreloader(Progress.get(this.adapter));
          ToolkitStore.get().setContextClassLoader(this.loader);
        }
        finally
        {
          currentThreadManager.set(null);
        }
      }
      String str = getParameter("codebase_lookup");
      if (str != null)
        if (str.equals("false"))
          this.loader.setCodebaseLookup(false);
        else
          this.loader.setCodebaseLookup(true);
      return this.loader;
    }
  }

  protected abstract Plugin2ClassLoader newClassLoader();

  protected ThreadGroup getOrCreateAppletThreadGroup()
  {
    synchronized (this)
    {
      if (this.appletThreadGroup == null)
      {
        final URL localURL = getCodeBase();
        AccessController.doPrivileged(new PrivilegedAction()
        {
          private final URL val$codebase;

          public Object run()
          {
            Plugin2Manager.this.appletThreadGroup = new Applet2ThreadGroup(localURL + "-threadGroup");
            return null;
          }
        });
      }
      return this.appletThreadGroup;
    }
  }

  protected AppContext getOrCreateAppletAppContext()
  {
    synchronized (this)
    {
      if (this.appletAppContext == null)
      {
        final Plugin2ClassLoader localPlugin2ClassLoader = getOrCreatePlugin2ClassLoader();
        if (this.appletAppContext != null)
          return this.appletAppContext;
        final ThreadGroup localThreadGroup = getOrCreateAppletThreadGroup();
        if ((localPlugin2ClassLoader == null) || (localThreadGroup == null))
          throw new InternalError("Error during bootstrapping of AppContext");
        AccessController.doPrivileged(new PrivilegedAction()
        {
          private final ThreadGroup val$tg;
          private final Plugin2ClassLoader val$loader;

          public Object run()
          {
            Plugin2Manager.AppContextCreator localAppContextCreator = new Plugin2Manager.AppContextCreator(localThreadGroup);
            localAppContextCreator.setContextClassLoader(localPlugin2ClassLoader);
            synchronized (localAppContextCreator.syncObject)
            {
              localAppContextCreator.start();
              try
              {
                localAppContextCreator.syncObject.wait();
              }
              catch (InterruptedException localInterruptedException)
              {
              }
              Plugin2Manager.this.appletAppContext = localAppContextCreator.appContext;
              Plugin2Manager.this.adapter.setAppletAppContext(Plugin2Manager.this.appletAppContext);
              Plugin2Manager.this.appletAppContext.put("Plugin2CtxKey", Plugin2Manager.this.pluginContext);
            }
            return null;
          }
        });
      }
      if ((this.parentWindow != null) && (ReflectionUtil.isSubclassOf(this.parentWindow, "com.sun.deploy.uitoolkit.impl.awt.AWTPluginEmbeddedFrameWindow")))
        ((AWTPluginEmbeddedFrameWindow)this.parentWindow).setAppContext(this.appletAppContext);
      return this.appletAppContext;
    }
  }

  protected void destroyAppContext(AppContext paramAppContext, Applet2StopListener paramApplet2StopListener, long paramLong)
  {
    long l = DeployPerfUtil.put(0L, "Plugin2Manager - destroyAppContext() - BEGIN");
    if (paramLong <= 0L)
      paramLong = 10L;
    if (paramAppContext != null)
    {
      ThreadGroup localThreadGroup = paramAppContext.getThreadGroup();
      if (!paramAppContext.destroy(paramLong))
        paramApplet2StopListener.stopFailed();
      else
        new Thread(new ShutdownChecker(localThreadGroup, paramApplet2StopListener)).start();
    }
    DeployPerfUtil.put(l, "Plugin2Manager - destroyAppContext() - END");
  }

  public synchronized void increaseJava2JSCounter()
  {
    if (this.java2JSCounter != 0)
      this.java2JSCounter += 1;
  }

  public synchronized void decreaseJava2JSCounter()
  {
    if (this.java2JSCounter >= 1)
      this.java2JSCounter -= 1;
  }

  public synchronized void unblockJS2Java()
  {
    this.java2JSCounter = 0;
  }

  public synchronized int getJava2JSCounter()
  {
    return this.java2JSCounter;
  }

  public void waitUntilAppletStartDone()
  {
    synchronized (this)
    {
      notifyAll();
    }
    ??? = this.appletStartLock;
    if (??? != null)
      synchronized (???)
      {
        if ((this.appletStartLock != null) && (getJava2JSCounter() != 0))
          try
          {
            ???.wait();
          }
          catch (InterruptedException localInterruptedException2)
          {
          }
      }
    else
      while (getJava2JSCounter() != 0)
        try
        {
          Thread.sleep(10L);
        }
        catch (InterruptedException localInterruptedException1)
        {
        }
  }

  public void stopWaitingForAppletStart()
  {
    unblockJS2Java();
    Object localObject1 = this.appletStartLock;
    if (localObject1 != null)
      synchronized (localObject1)
      {
        this.appletStartLock = null;
        localObject1.notifyAll();
      }
    fireAppletReady();
  }

  protected void initJarVersionMap()
  {
  }

  protected synchronized void invalidateClassLoaderCacheEntry()
  {
  }

  protected boolean usingLegacyLifeCycle()
  {
    return false;
  }

  protected void clearUsingLegacyLifeCycle()
  {
  }

  private Map getAppletParameters()
  {
    if (this.appletParameters == null)
    {
      if (this.appletExecutionContext == null)
        throw new IllegalStateException("Requires AppletExecutionContext to be set by now");
      this.appletParameters = this.appletExecutionContext.getAppletParameters();
      if (this.appletParameters == null)
        throw new IllegalStateException("AppletExecutionContext illegally returned a null parameter map");
    }
    return this.appletParameters;
  }

  public URL getCodeBase()
  {
    if (this.codebase == null)
    {
      String str1 = getParameter("java_codebase");
      if (str1 == null)
        str1 = getParameter("codebase");
      if (str1 != null)
      {
        if ((!str1.equals(".")) && (!str1.endsWith("/")))
          str1 = str1 + "/";
        str1 = URLUtil.canonicalize(str1);
      }
      if (str1 != null)
        try
        {
          URL localURL1 = new URL(str1);
          this.codebase = localURL1;
          return this.codebase;
        }
        catch (MalformedURLException localMalformedURLException1)
        {
        }
      URL localURL2 = getDocumentBase();
      if (localURL2 == null)
        return null;
      URL localURL3 = null;
      if (str1 != null)
        try
        {
          localURL3 = new URL(localURL2, str1);
          if (!URLUtil.checkTargetURL(localURL2, localURL3))
            throw new SecurityException("Permission denied: " + localURL3);
        }
        catch (MalformedURLException localMalformedURLException2)
        {
        }
      if (localURL3 == null)
      {
        String str2 = localURL2.toString();
        int i = str2.indexOf('?');
        if (i > 0)
          str2 = str2.substring(0, i);
        i = str2.lastIndexOf('/');
        if ((i > -1) && (i < str2.length() - 1))
          try
          {
            localURL3 = new URL(URLUtil.canonicalize(str2.substring(0, i + 1)));
          }
          catch (MalformedURLException localMalformedURLException3)
          {
          }
        if (localURL3 == null)
          localURL3 = localURL2;
      }
      this.codebase = localURL3;
    }
    return this.codebase;
  }

  public String getCode()
  {
    String str1 = getParameter("classid");
    String str2 = null;
    if (str1 != null)
    {
      int i = str1.indexOf("java:");
      if (i > -1)
      {
        str2 = str1.substring(5 + i);
        if ((str2 != null) || (!str2.equals("")))
          return str2;
      }
    }
    str2 = getParameter("java_code");
    if (str2 == null)
      str2 = getParameter("code");
    return str2;
  }

  protected String getAppletCode()
  {
    return getCode();
  }

  public String getName()
  {
    String str = getParameter("name");
    if (str != null)
      return str;
    str = getCode();
    int i;
    if (str != null)
    {
      i = str.lastIndexOf(".class");
      if (i != -1)
        str = str.substring(0, i);
    }
    else
    {
      str = getSerializedObject();
      if (str != null)
      {
        i = str.lastIndexOf(".ser");
        if (i != -1)
          str = str.substring(0, i);
      }
    }
    return str;
  }

  protected String getDraggedTitleParam()
  {
    return getParameter("java_applet_title");
  }

  public String getDraggedTitle()
  {
    String str = getDraggedTitleParam();
    if (str != null)
      return str;
    return getName();
  }

  boolean getDecoratedDefault()
  {
    return false;
  }

  String getDecoratedPreference()
  {
    return getParameter("java_decorated_frame");
  }

  public boolean getUndecorated()
  {
    String str = getDecoratedPreference();
    if (str != null)
      return !Boolean.parseBoolean(str);
    return !getDecoratedDefault();
  }

  public void setDraggedApplet()
  {
  }

  protected String getSerializedObject()
  {
    String str = getParameter("java_object");
    if (str == null)
      str = getParameter("object");
    return str;
  }

  private synchronized void setSize(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > 0) && (paramInt2 > 0))
    {
      String str1 = Integer.toString(paramInt1);
      String str2 = Integer.toString(paramInt2);
      setParameter("width", str1);
      setParameter("height", str2);
      if ((this.gotSize) && (this.adapter.isInstantiated()))
        this.adapter.resize(paramInt1, paramInt2);
      this.width = paramInt1;
      this.height = paramInt2;
    }
  }

  private synchronized void getSize()
  {
    if (!this.gotSize)
    {
      String str1 = getParameter("width");
      if (str1 != null)
        this.width = Integer.parseInt(str1);
      String str2 = getParameter("height");
      if (str2 != null)
        this.height = Integer.parseInt(str2);
      this.gotSize = true;
    }
  }

  public int getWidth()
  {
    getSize();
    return this.width;
  }

  public int getHeight()
  {
    getSize();
    return this.height;
  }

  protected void setupAppletAppContext()
  {
    this.appletAppContext.put("deploy.trust.decider.app.name", getName());
  }

  protected abstract String getJarFiles();

  protected abstract String getCodeSourceLocations();

  protected abstract void loadJarFiles()
    throws ExitException;

  protected abstract void performDesktopIntegration();

  protected abstract void appletSSVRelaunch()
    throws JRESelectException;

  protected abstract void checkRunningJVMArgsSatisfying()
    throws JRESelectException;

  protected abstract void checkRunningJVMToolkitSatisfying()
    throws JRESelectException;

  public abstract AppInfo getAppInfo();

  void initAppletAdapter(AppletExecutionRunnable paramAppletExecutionRunnable)
    throws ClassNotFoundException, IllegalAccessException, ExitException, JRESelectException, IOException, InstantiationException
  {
    long l = DeployPerfUtil.put(0L, "Plugin2Manager.createApplet() - BEGIN");
    String str1 = getSerializedObject();
    String str2 = getCode();
    Plugin2ClassLoader localPlugin2ClassLoader = getAppletClassLoader();
    DeployPerfUtil.put("Plugin2Manager.createApplet() - post getAppletClassLoader()");
    Object localObject1;
    if (_INJECT_EXCEPTION_CREATEAPPLET)
    {
      localObject1 = new IOException("INJECT_PLUGIN2MANAGER_EXCEPTION_CREATEAPPLET");
      throw ((Throwable)localObject1);
    }
    if (_INJECT_CREATEAPPLET_NULL)
    {
      System.out.println("INJECT_PLUGIN2MANAGER_CREATEAPPLET_NULL");
      return;
    }
    if ((str2 != null) && (str1 != null))
    {
      System.err.println(amh.getMessage("runloader.err"));
      throw new InstantiationException("Either \"code\" or \"object\" should be specified, but not both.");
    }
    if ((str2 == null) && (str1 == null))
      return;
    if (str2 != null)
    {
      if (!Config.checkPackageAccess(str2, Config.getNoPermissionACC()))
      {
        localObject1 = new SecurityException("Bad package name of main-class");
        throw new ExitException((Throwable)localObject1, 3);
      }
      localObject1 = localPlugin2ClassLoader.loadCode(str2);
      if (!localPlugin2ClassLoader.isClassLoadedByPluginClassLoader((Class)localObject1))
      {
        localObject2 = new SecurityException("Bad applet class name");
        throw new ExitException((Throwable)localObject2, 3);
      }
      DeployPerfUtil.put("Plugin2Manager.createApplet() -  post loader.loadCode()");
      Object localObject2 = getAppletCode();
      Object localObject3 = null;
      if (!((String)localObject2).equals(str2))
        localObject3 = this.loader.loadCode((String)localObject2);
      else
        localObject3 = localObject1;
      this._trustedApplet = isAppletTrusted((Class)localObject3, getPreloaderDelegate());
      if (localObject1 != null)
        if (fireAppletSSVValidation())
        {
          appletSSVRelaunch();
        }
        else
        {
          checkRunningJVMToolkitSatisfying();
          checkRunningJVMArgsSatisfying();
          if (paramAppletExecutionRunnable != null)
            paramAppletExecutionRunnable.sendAndWaitForAppletConstructEvent();
          this.adapter.instantiateApplet((Class)localObject1);
          DeployPerfUtil.put("Plugin2Manager.createApplet() - created applet instance");
        }
    }
    else
    {
      if (!this.isSecureVM)
        return;
      localObject1 = new CodeSource(getCodeBase(), (Certificate[])null);
      SandboxSecurity.isPermissionGranted((CodeSource)localObject1, getAppInfo(), getPreloaderDelegate().get());
      this.adapter.instantiateSerialApplet(localPlugin2ClassLoader, str1);
      this.doInit = false;
      DeployPerfUtil.put("Plugin2Manager.createApplet() - post: secureVM .. serialized .. ");
    }
    if (!this.adapter.isInstantiated())
    {
      System.out.println(ResourceManager.getMessage("console.println.plugin.applet.failed"));
      return;
    }
    if (this.shouldStop)
    {
      setErrorOccurred("death");
      this.adapter.abort();
      if (DEBUG)
        Trace.println("Applet ID " + this.appletID + ResourceManager.getMessage("console.trace.plugin.applet.killed"), TraceLevel.BASIC);
      logAppletStatus("death");
      synchronized (this.stopLock)
      {
        this.stopSuccessful = true;
        this.stopLock.notifyAll();
      }
      return;
    }
    DeployPerfUtil.put(l, "Plugin2Manager.initAppletAdapter() - END");
  }

  private boolean isAppletTrusted(Class paramClass, PreloaderDelegate paramPreloaderDelegate)
    throws ExitException
  {
    int i = 0;
    CodeSource localCodeSource = paramClass.getProtectionDomain().getCodeSource();
    ClassLoader localClassLoader = paramClass.getClassLoader();
    if (localCodeSource == null)
      return false;
    if (usePolicyPermission == null)
      usePolicyPermission = new RuntimePermission("usePolicy");
    Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
    boolean bool = false;
    if ((localClassLoader instanceof Plugin2ClassLoader))
      bool = ((Plugin2ClassLoader)localClassLoader).wantsAllPerms(localCodeSource);
    if (bool)
    {
      try
      {
        i = TrustDecider.isAllPermissionGranted(localCodeSource, paramPreloaderDelegate) != 0L ? 1 : 0;
      }
      catch (Exception localException)
      {
        throw new ExitException(ResourceManager.getMessage("security.badcert.text"), localException);
      }
      if (i == 0)
        throw new ExitException(ResourceManager.getMessage("security.badcert.config.text"), null);
    }
    else
    {
      AppInfo localAppInfo = getAppInfo();
      SandboxSecurity.isPermissionGranted(localCodeSource, localAppInfo, paramPreloaderDelegate.get());
    }
    return (i != 0) || (localPermissionCollection.implies(usePolicyPermission));
  }

  protected void logAppletStatus(String paramString)
  {
    try
    {
      Trace.println(amh.getMessage(paramString), TraceLevel.BASIC);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
      Trace.println(paramString, TraceLevel.BASIC);
    }
  }

  protected void logAppletStatus(String paramString, Object paramObject)
  {
    try
    {
      Trace.println(amh.getMessage(paramString, paramObject), TraceLevel.BASIC);
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
      Trace.println(paramString, TraceLevel.BASIC);
    }
  }

  protected void showAppletException(Throwable paramThrowable)
  {
    showAppletException(paramThrowable, false);
  }

  protected static ErrorEvent getErrorEvent(URL paramURL, Throwable paramThrowable)
  {
    if ((paramThrowable instanceof ExitException))
    {
      ExitException localExitException = (ExitException)paramThrowable;
      if (localExitException.getReason() == 6)
        return new ErrorEvent(paramURL, localExitException.getMessage(), localExitException.getException());
      return new ErrorEvent(paramURL, localExitException);
    }
    if ((paramThrowable instanceof JNLPException))
      return new ErrorEvent(paramURL, ((JNLPException)paramThrowable).getBriefMessage(), paramThrowable);
    return new ErrorEvent(paramURL, paramThrowable);
  }

  protected void showAppletException(Throwable paramThrowable, boolean paramBoolean)
  {
    Trace.ignored(paramThrowable);
    paramThrowable = unwrapIfJARSigningException(paramThrowable);
    String str = (paramThrowable instanceof JARSigningException) ? ResourceManager.getMessage("dialogfactory.security_error") : null;
    if ((!paramBoolean) && (Config.getBooleanProperty("deployment.javapi.lifecycle.exception")))
      try
      {
        Trace.printException(paramThrowable, str, null);
      }
      catch (Exception localException)
      {
      }
    try
    {
      getPreloaderDelegate().handleEvent(getErrorEvent(this.codebase, paramThrowable));
    }
    catch (CancelException localCancelException)
    {
    }
  }

  private static Throwable unwrapIfJARSigningException(Throwable paramThrowable)
  {
    if (((paramThrowable instanceof ClassNotFoundException)) && ((paramThrowable.getCause() instanceof JARSigningException)))
      return paramThrowable.getCause();
    if ((paramThrowable instanceof ExitException))
    {
      ExitException localExitException = (ExitException)paramThrowable;
      if ((localExitException.getException() instanceof JARSigningException))
        return localExitException.getException();
      if ((localExitException.getCause() instanceof JARSigningException))
        return localExitException.getCause();
    }
    return paramThrowable;
  }

  protected void setErrorOccurred(String paramString, Throwable paramThrowable)
  {
    this.errorOccurred = true;
    this.errorMessage = paramString;
    this.errorException = paramThrowable;
  }

  private void setErrorOccurred(String paramString)
  {
    setErrorOccurred(paramString, null);
  }

  private void setErrorOccurred(Throwable paramThrowable)
  {
    setErrorOccurred(paramThrowable.toString(), paramThrowable);
  }

  private static synchronized List getPlugin2ManagerList(AppContext paramAppContext)
  {
    Object localObject = (List)paramAppContext.get("Plugin2ManagerListKey");
    if (localObject == null)
    {
      localObject = new ArrayList();
      paramAppContext.put("Plugin2ManagerListKey", localObject);
    }
    return localObject;
  }

  protected void registerInAppContext(AppContext paramAppContext)
  {
    List localList = getPlugin2ManagerList(paramAppContext);
    synchronized (localList)
    {
      localList.add(new WeakReference(this));
    }
  }

  protected static Plugin2Manager getFromThreadLocal()
  {
    return (Plugin2Manager)currentManagerThreadLocal.get();
  }

  protected static Plugin2Manager getFromAppContext()
  {
    List localList = getPlugin2ManagerList(ToolkitStore.get().getAppContext());
    synchronized (localList)
    {
      if (localList.isEmpty())
        return null;
      Object localObject1 = null;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        Plugin2Manager localPlugin2Manager = (Plugin2Manager)localWeakReference.get();
        if (localPlugin2Manager != null)
          if (localPlugin2Manager.isDisconnected())
            localObject1 = localPlugin2Manager;
          else
            return localPlugin2Manager;
      }
      if (localObject1 != null)
        return localObject1;
    }
    return null;
  }

  protected void unregisterFromAppContext(AppContext paramAppContext)
  {
    if (paramAppContext == null)
      return;
    List localList = getPlugin2ManagerList(paramAppContext);
    synchronized (localList)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        Plugin2Manager localPlugin2Manager = (Plugin2Manager)localWeakReference.get();
        if ((localPlugin2Manager == null) || (localPlugin2Manager == this))
        {
          localIterator.remove();
          break;
        }
      }
    }
  }

  private boolean isInSameAppContextImpl(Plugin2Manager paramPlugin2Manager)
  {
    if (this == paramPlugin2Manager)
      return true;
    AppContext localAppContext = this.appletAppContext;
    if (localAppContext == null)
      return false;
    List localList = getPlugin2ManagerList(localAppContext);
    synchronized (localList)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        WeakReference localWeakReference = (WeakReference)localIterator.next();
        if (localWeakReference.get() == paramPlugin2Manager)
          return true;
      }
    }
    return false;
  }

  protected static String[] splitJarList(String paramString, boolean paramBoolean)
  {
    if (paramString == null)
      return null;
    String[] arrayOfString = paramString.split(",");
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str = arrayOfString[i].trim();
      if (paramBoolean)
      {
        int j = str.indexOf(';');
        if (j >= 0)
          str = str.substring(0, j);
      }
      if ((str != null) && (!str.equals("")))
        localArrayList.add(str);
    }
    return (String[])localArrayList.toArray(new String[0]);
  }

  protected static String[] splitOptionString(String paramString)
  {
    int i = paramString.indexOf(';');
    if (i < 0)
      return new String[] { paramString, null };
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ";");
    if (localStringTokenizer.countTokens() >= 3)
    {
      String str1 = localStringTokenizer.nextToken();
      String str2 = localStringTokenizer.nextToken();
      String str3 = localStringTokenizer.nextToken();
      if (str2.toLowerCase().indexOf("preload") != -1)
        return new String[] { str1, str2, str3 };
      return new String[] { str1, str3, str2 };
    }
    return new String[] { paramString.substring(0, i), paramString.substring(i + 1) };
  }

  protected static String buildJarList(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null)
      return null;
    StringBuffer localStringBuffer = null;
    int i = 0;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      String str = paramArrayOfString[j];
      if (str != null)
      {
        if (localStringBuffer == null)
          localStringBuffer = new StringBuffer();
        if (i != 0)
          localStringBuffer.append(",");
        localStringBuffer.append(str);
        i = 1;
      }
    }
    if (localStringBuffer == null)
      return null;
    return localStringBuffer.toString();
  }

  private boolean haveValidSize()
  {
    return (getWidth() != 0) || (getHeight() != 0);
  }

  public void ensureSizeIsValid()
  {
    if ((!haveValidSize()) && (!this.waitedOnce))
      synchronized (this)
      {
        if ((!haveValidSize()) && (!this.waitedOnce))
          try
          {
            wait(500L);
          }
          catch (InterruptedException localInterruptedException)
          {
          }
          finally
          {
            this.waitedOnce = true;
          }
      }
  }

  static
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    String str1 = SystemUtil.getenv("JPI_PLUGIN2_INJECT_PLUGIN2MANAGER");
    if (null != str1)
    {
      System.out.println("JPI_PLUGIN2_INJECT_PLUGIN2MANAGER: " + str1);
      StringTokenizer localStringTokenizer = new StringTokenizer(str1);
      try
      {
        while (localStringTokenizer.hasMoreTokens())
        {
          String str2 = new String(localStringTokenizer.nextToken());
          if (null != str2)
          {
            if (!bool1)
              bool1 = "EXCEPTION_CREATEAPPLET".equals(str2);
            if (!bool2)
              bool2 = "CREATEAPPLET_NULL".equals(str2);
            if (!bool3)
              bool3 = "DELAY_APPLETLOADED".equals(str2);
            if (!bool4)
              bool4 = "NEVER_APPLETLOADED".equals(str2);
          }
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      System.out.println("\tEXCEPTION_CREATEAPPLET: " + bool1);
      System.out.println("\tCREATEAPPLET_NULL: " + bool2);
      System.out.println("\tDELAY_APPLETLOADED: " + bool3);
      System.out.println("\tNEVER_APPLETLOADED: " + bool4);
    }
  }

  static class AppContextCreator extends Thread
  {
    final Object syncObject = new Object();
    AppContext appContext = null;

    AppContextCreator(ThreadGroup paramThreadGroup)
    {
      super("AppContextCreator");
    }

    public void run()
    {
      synchronized (this.syncObject)
      {
        this.appContext = ToolkitStore.get().createAppContext();
        this.syncObject.notifyAll();
      }
    }
  }

  class AppletExecutionRunnable
    implements Runnable, PreloaderPostEventListener
  {
    int allowedState = 0;
    static final int STATE_CONSTRUCT = 1;
    static final int STATE_INIT = 2;
    static final int STATE_START = 3;
    static final int STATE_ANY = 100;

    AppletExecutionRunnable()
    {
    }

    synchronized void waitForState(int paramInt)
    {
      while (this.allowedState < paramInt)
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException)
        {
        }
    }

    synchronized void moveState(int paramInt)
    {
      if (paramInt > this.allowedState)
      {
        this.allowedState = paramInt;
        notifyAll();
      }
      else if (paramInt != this.allowedState)
      {
        Trace.println(ResourceManager.getMessage("console.trace.plugin.lifecycle.state") + paramInt + ResourceManager.getMessage("console.trace.plugin.lifecycle.in") + this.allowedState, TraceLevel.TEMP);
      }
    }

    private void cleanupOnError(String paramString, Throwable paramThrowable)
    {
      Plugin2Manager.this.invalidateClassLoaderCacheEntry();
      Plugin2Manager.this.clearUsingLegacyLifeCycle();
      Plugin2Manager.this.setErrorOccurred(paramString, paramThrowable);
      Plugin2Manager.this.appletIsActive = false;
    }

    public void run()
    {
      Object localObject1 = null;
      PreloaderDelegate localPreloaderDelegate = Plugin2Manager.this.getPreloaderDelegate();
      long l1 = DeployPerfUtil.put(0L, "AppletExecutionRunnable - BEGIN");
      Plugin2Manager.currentManagerThreadLocal.set(Plugin2Manager.this);
      int i = (!Plugin2Manager.this.usingLegacyLifeCycle()) || (!Plugin2Manager.this.adapter.isInstantiated()) ? 1 : 0;
      int j = 0;
      try
      {
        String str1;
        String str2;
        if (!Config.get().isValid())
        {
          str1 = ResourceManager.getString("common.ok_btn");
          str2 = ResourceManager.getString("common.detail.button");
          ToolkitStore.getUI();
          ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getString("error.default.title.applet"), ResourceManager.getString("launcherrordialog.brief.message.applet"), ResourceManager.getString("enterprize.cfg.mandatory.applet", Config.get().getEnterpriseString()), null, str1, str2, null);
        }
        if (i != 0)
        {
          if (Plugin2Manager.this.shouldStop)
            return;
          Plugin2Manager.this.initJarVersionMap();
          if (Plugin2Manager.this.shouldStop)
            return;
          Plugin2Manager.this.setupAppletAppContext();
        }
        if (!Plugin2Manager.this.isForDummyApplet)
        {
          str1 = Plugin2Manager.this.getCode();
          try
          {
            Plugin2Manager.this.setupProgress();
            if (i != 0)
            {
              if (!URLUtil.checkTargetURL(Plugin2Manager.this.getDocumentBase(), Plugin2Manager.this.getCodeBase()))
                throw new ExitException(new SecurityException("Permission denied: " + Plugin2Manager.this.getCodeBase()), 3);
              Plugin2Manager.this.loadJarFiles();
              DeployPerfUtil.put("AppletExecutionRunnable - post loadJarFiles()");
              Plugin2Manager.this.initAppletAdapter(this);
              DeployPerfUtil.put("AppletExecutionRunnable - post createApplet()");
            }
            else if (Plugin2Manager.this.usingLegacyLifeCycle())
            {
              assert (Plugin2Manager.this.adapter != null);
              localPreloaderDelegate.handleEvent(new AppletInitEvent(6, Plugin2Manager.this.adapter.getApplet2()));
              waitForState(1);
            }
            if (!Plugin2Manager.this.adapter.isInstantiated())
            {
              if (!Plugin2Manager.this.hasErrorOccurred())
              {
                str2 = "nocode";
                Plugin2Manager.this.setErrorOccurred(str2);
                Plugin2Manager.this.logAppletStatus(str2);
              }
              return;
            }
          }
          catch (ExitException localExitException2)
          {
            throw localExitException2;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            Trace.ignoredException(localClassNotFoundException);
            Plugin2Manager.this.logAppletStatus("notfound", str1);
            Plugin2Manager.this.showAppletException(localClassNotFoundException);
            cleanupOnError("notfound " + str1, localClassNotFoundException);
            return;
          }
          catch (InstantiationException localInstantiationException)
          {
            cleanupOnError("nocreate " + str1, localInstantiationException);
            Plugin2Manager.this.logAppletStatus("nocreate", str1);
            Plugin2Manager.this.showAppletException(localInstantiationException);
            return;
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            cleanupOnError("noconstruct " + str1, localIllegalAccessException);
            Plugin2Manager.this.logAppletStatus("noconstruct", str1);
            Plugin2Manager.this.showAppletException(localIllegalAccessException);
            return;
          }
          catch (Exception localException2)
          {
            Throwable localThrowable1 = localException2.getCause();
            if ((localThrowable1 != null) && ((localThrowable1 instanceof ExitException)))
              throw ((ExitException)localThrowable1);
            cleanupOnError(null, localException2);
            Plugin2Manager.this.logAppletStatus("exception", localException2.getMessage());
            Plugin2Manager.this.showAppletException(localException2);
            return;
          }
          catch (ThreadDeath localThreadDeath)
          {
            cleanupOnError("Applet ID " + Plugin2Manager.this.appletID + " killed", null);
            if (Plugin2Manager.DEBUG)
              Trace.println("Applet ID " + Plugin2Manager.this.appletID + " killed", TraceLevel.BASIC);
            Plugin2Manager.this.logAppletStatus("death");
            return;
          }
          catch (Error localError)
          {
            cleanupOnError(null, localError);
            Plugin2Manager.this.logAppletStatus("error", localError.getMessage());
            Plugin2Manager.this.showAppletException(localError);
            return;
          }
          Plugin2Manager.applets.setActiveStatus(Plugin2Manager.this, true);
          Plugin2Manager.this.logAppletStatus("loaded");
          DeployPerfUtil.put("AppletExecutionRunnable - post applet container");
          Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.resized"), TraceLevel.BASIC);
          if (!Plugin2Manager.this.shouldStop)
          {
            if (Plugin2Manager._INJECT_DELAY_APPLETLOADED)
            {
              Trace.msgPrintln("PERF: AppletExecutionRunnable - fireAppletLoaded SLEEP");
              try
              {
                Thread.sleep(5000L);
              }
              catch (InterruptedException localInterruptedException1)
              {
              }
            }
            if (Plugin2Manager._INJECT_NEVER_APPLETLOADED)
            {
              Trace.msgPrintln("PERF: AppletExecutionRunnable - fireAppletLoaded NEVER");
              int k = 999999;
              while (k > 0)
              {
                k--;
                if (k < 999)
                  k = 999999;
                try
                {
                  Thread.sleep(1000L);
                }
                catch (InterruptedException localInterruptedException3)
                {
                }
              }
            }
          }
        }
        Plugin2Manager.this.appletStartLock = new Object();
        if ((Plugin2Manager.this.isForDummyApplet) || (!Plugin2Manager.this.shouldStop))
          Plugin2Manager.this.fireAppletLoaded();
        Plugin2Manager.this.ensureSizeIsValid();
        try
        {
          if ((!Plugin2Manager.this.isForDummyApplet) && (Plugin2Manager.this.doInit))
          {
            try
            {
              localPreloaderDelegate.handleEvent(new AppletInitEvent(3, Plugin2Manager.this.adapter.getApplet2()));
            }
            catch (CancelException localCancelException1)
            {
            }
            long l2 = SystemUtils.microTime() - Plugin2Manager.getJVMLaunchTime();
            Trace.msgPrintln("PERF: AppletExecutionRunnable - applet.init() BEGIN ; jvmLaunch dt " + Plugin2Manager.getJVMLaunchCosts() + " us, pluginInit dt " + (l2 - Plugin2Manager.getJVMLaunchCosts()) + " us, TotalTime: " + l2 + " us");
            DeployPerfUtil.put(l1, "AppletExecutionRunnable - applet.init() BEGIN");
            try
            {
              waitForState(2);
              Plugin2Manager.this.adapter.init();
            }
            catch (Throwable localThrowable2)
            {
              Throwable localThrowable3 = localThrowable2;
              if (((localThrowable2 instanceof RuntimeException)) && (localThrowable2.getCause() != null))
                localThrowable3 = localThrowable2.getCause();
              cleanupOnError(null, localThrowable3);
              Plugin2Manager.this.showAppletException(localThrowable3);
              return;
            }
            finally
            {
              DeployPerfUtil.put(l1, "AppletExecutionRunnable - applet.init() END");
            }
            Plugin2Manager.this.doInit = false;
            Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.initialized"), TraceLevel.BASIC);
          }
          if (!Plugin2Manager.this.shouldStop)
          {
            Plugin2Manager.this.unblockJS2Java();
            Plugin2Manager.this.fireAppletReady();
            Plugin2Manager.this.appletIsActive = true;
            if (!Plugin2Manager.this.isForDummyApplet)
            {
              Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.starting"), TraceLevel.BASIC);
              try
              {
                if (!DeployPerfUtil.isDeployFirstframePerfEnabled())
                {
                  DeployPerfUtil.write(new Plugin2Rollup(Plugin2Manager.appletLaunchCosts, Plugin2Manager.jvmLaunchCosts));
                  Trace.println(ResourceManager.getMessage("console.trace.plugin.rollup.completed"), TraceLevel.BASIC);
                }
              }
              catch (IOException localIOException)
              {
              }
              try
              {
                localPreloaderDelegate.handleEvent(new AppletInitEvent(4, Plugin2Manager.this.adapter.getApplet2()));
                waitForState(3);
                Plugin2Manager.this.adapter.start();
                Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.visible"), TraceLevel.BASIC);
              }
              catch (Exception localException1)
              {
                Object localObject2 = localException1;
                if (((localException1 instanceof RuntimeException)) && (localException1.getCause() != null))
                  localObject2 = localException1.getCause();
                cleanupOnError(null, (Throwable)localObject2);
                Plugin2Manager.this.showAppletException((Throwable)localObject2);
                return;
              }
              Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.started"), TraceLevel.BASIC);
            }
          }
          else
          {
            j = 1;
          }
        }
        finally
        {
          Object localObject7 = Plugin2Manager.this.appletStartLock;
          if (localObject7 != null)
            synchronized (localObject7)
            {
              Plugin2Manager.this.appletStartLock = null;
              localObject7.notifyAll();
            }
          Plugin2Manager.this.unblockJS2Java();
        }
        if (!Plugin2Manager.this.shouldStop)
        {
          Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.told"), TraceLevel.BASIC);
          Plugin2Manager.this.performDesktopIntegration();
        }
        else
        {
          j = 1;
        }
        if (j != 0)
          Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.skipped"), TraceLevel.BASIC);
        if ((Plugin2Manager.this.getAppletExecutionContext() instanceof DisconnectedExecutionContext))
          return;
        synchronized (Plugin2Manager.this.stopLock)
        {
          while (!Plugin2Manager.this.shouldStop)
            try
            {
              Plugin2Manager.this.stopLock.wait();
            }
            catch (InterruptedException localInterruptedException2)
            {
            }
        }
        Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.teardown"), TraceLevel.BASIC);
        Plugin2Manager.this.appletIsActive = false;
        if (!Plugin2Manager.this.isForDummyApplet)
        {
          try
          {
            Plugin2Manager.this.adapter.stop();
          }
          catch (Throwable localException1)
          {
            cleanupOnError(Plugin2Manager.this.errorMessage, ???);
            Trace.ignored(???);
          }
          finally
          {
            if (!Plugin2Manager.this.usingLegacyLifeCycle())
              try
              {
                Plugin2Manager.this.getPreloaderDelegate().handleEvent(new ApplicationExitEvent());
              }
              catch (CancelException localCancelException2)
              {
                Trace.ignored(localCancelException2);
              }
          }
          if (!Plugin2Manager.this.usingLegacyLifeCycle())
            try
            {
              Plugin2Manager.this.adapter.destroy();
            }
            catch (Throwable localException1)
            {
              cleanupOnError(null, ???);
              Trace.ignored(???);
            }
          Plugin2Manager.this.adapter.doClearAppletArea();
        }
        Trace.println(ResourceManager.getMessage("console.trace.plugin.applet.finished"), TraceLevel.BASIC);
        synchronized (Plugin2Manager.this.stopLock)
        {
          Plugin2Manager.this.stopSuccessful = true;
          Plugin2Manager.this.stopLock.notifyAll();
        }
      }
      catch (JRESelectException localJRESelectException)
      {
        localObject1 = localJRESelectException;
      }
      catch (ExitException localExitException1)
      {
        if (localExitException1.isErrorException())
        {
          cleanupOnError(null, localExitException1);
          Plugin2Manager.this.logAppletStatus("exception", localExitException1.getMessage());
          Plugin2Manager.this.showAppletException(localExitException1, localExitException1.isSilentException());
        }
        else if (5 == localExitException1.getReason())
        {
          Plugin2Manager.this.setErrorOccurred(localExitException1);
          Plugin2Manager.this.logAppletStatus(Plugin2Manager.this.getErrorMessage());
          Plugin2Manager.this.showAppletException(localExitException1, localExitException1.isSilentException());
        }
        else if (localExitException1.getReason() == 0)
        {
          Plugin2Manager.this.setErrorOccurred(localExitException1);
          Plugin2Manager.this.logAppletStatus("exception", localExitException1.getMessage());
          Plugin2Manager.this.showAppletException(localExitException1, true);
        }
        else
        {
          Trace.ignoredException(localExitException1);
        }
      }
      finally
      {
        Plugin2Manager.this.uninstallProgressListener();
        Container localContainer = Plugin2Manager.this.getAppletParentContainer();
        if (Plugin2Manager.this.shouldStop)
        {
          Plugin2Manager.this.setAppletStartResponseSent();
          localObject1 = null;
        }
        if (Plugin2Manager.this.hasErrorOccurred())
          Plugin2Manager.this.fireAppletErrorOccurred();
        Plugin2Manager.applets.setActiveStatus(Plugin2Manager.this, false);
        Plugin2Manager.this.appletExecutionThread = null;
        Object localObject15;
        if (null != localObject1)
        {
          Plugin2Manager.this.setAppletParentContainer(null);
          if ((localContainer != null) && ((localContainer instanceof java.awt.Window)))
          {
            localObject15 = (java.awt.Window)localContainer;
            localContainer = null;
            ((java.awt.Window)localObject15).dispose();
          }
          localObject15 = localObject1.getJREDesc();
          Plugin2Manager.this.fireAppletJRERelaunch(null != localObject15 ? ((JREDesc)localObject15).getVersion() : null, localObject1.getJVMArgs());
          localObject1 = null;
        }
        if (!Plugin2Manager.this.hasAppletStartResponseBeenSent())
        {
          localObject15 = new Exception("AppletLifecycle interrupted");
          cleanupOnError(null, (Throwable)localObject15);
          Plugin2Manager.this.showAppletException((Throwable)localObject15);
          Plugin2Manager.this.fireAppletErrorOccurred();
        }
        if (!Plugin2Manager.this.hasAppletStartResponseBeenSent())
        {
          localObject15 = new Exception("Applet2Listener.appletErrorOccurred() has not sent a message");
          cleanupOnError(null, (Throwable)localObject15);
          Plugin2Manager.this.showAppletException((Throwable)localObject15);
          Plugin2Manager.this.fireAppletErrorOccurred();
        }
        Plugin2Manager.this.removeAllAppletListeners();
      }
    }

    public void eventHandled(PreloaderEvent paramPreloaderEvent)
    {
      if ((paramPreloaderEvent instanceof AppletInitEvent))
      {
        AppletInitEvent localAppletInitEvent = (AppletInitEvent)paramPreloaderEvent;
        switch (localAppletInitEvent.getSubtype())
        {
        case 2:
          moveState(1);
          break;
        case 3:
          moveState(2);
          break;
        case 4:
          moveState(3);
          break;
        case 6:
          moveState(1);
        case 5:
        }
      }
      else if (!(paramPreloaderEvent instanceof ErrorEvent));
    }

    private void sendAndWaitForAppletConstructEvent()
      throws CancelException
    {
      Plugin2Manager.this.getPreloaderDelegate().handleEvent(new AppletInitEvent(2, null));
      waitForState(1);
    }
  }

  static class ShutdownChecker
    implements Runnable
  {
    private ThreadGroup group;
    private Applet2StopListener listener;

    ShutdownChecker(ThreadGroup paramThreadGroup, Applet2StopListener paramApplet2StopListener)
    {
      this.group = paramThreadGroup;
      this.listener = paramApplet2StopListener;
    }

    public void run()
    {
      try
      {
        Thread.sleep(3000L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
      if ((this.group != null) && (this.group.activeCount() > 0))
      {
        if (Plugin2Manager.DEBUG)
        {
          System.out.println(ResourceManager.getMessage("console.println.plugin.lingering.threads"));
          this.group.list();
        }
        if (this.listener != null)
          this.listener.stopFailed();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Plugin2Manager
 * JD-Core Version:    0.6.2
 */