package sun.plugin2.main.client;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.cache.MemoryCache;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.OSType;
import com.sun.deploy.config.Platform;
import com.sun.deploy.config.PluginClientConfig;
import com.sun.deploy.perf.DefaultPerfHelper;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.Applet2Adapter;
import com.sun.deploy.uitoolkit.DragContext;
import com.sun.deploy.uitoolkit.DragHelper;
import com.sun.deploy.uitoolkit.DragListener;
import com.sun.deploy.uitoolkit.PluginUIToolkit;
import com.sun.deploy.uitoolkit.PluginWindowFactory;
import com.sun.deploy.uitoolkit.SynthesizedEventListener;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.Window;
import com.sun.deploy.uitoolkit.WindowFactory;
import com.sun.deploy.uitoolkit.ui.AbstractDialog;
import com.sun.deploy.uitoolkit.ui.DialogHook;
import com.sun.deploy.uitoolkit.ui.ModalityHelper;
import com.sun.deploy.uitoolkit.ui.PluginUIFactory;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.ArrayUtil;
import com.sun.deploy.util.DeploymentHooks;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.SessionState;
import com.sun.deploy.util.SystemUtils;
import com.sun.deploy.util.URLUtil;
import com.sun.deploy.util.VersionID;
import com.sun.javaws.Cache6UpgradeHelper;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.ui.SecureStaticVersioning;
import java.awt.Container;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import sun.plugin.util.ProgressMonitor;
import sun.plugin2.applet.Applet2ClassLoaderCache;
import sun.plugin2.applet.Applet2Environment;
import sun.plugin2.applet.Applet2ExecutionContext;
import sun.plugin2.applet.Applet2Listener;
import sun.plugin2.applet.Applet2Manager;
import sun.plugin2.applet.Applet2ManagerCache;
import sun.plugin2.applet.Applet2Status;
import sun.plugin2.applet.Applet2StopListener;
import sun.plugin2.applet.JNLP2Manager;
import sun.plugin2.applet.JNLP2Tag;
import sun.plugin2.applet.Plugin2ClassLoader;
import sun.plugin2.applet.Plugin2ConsoleController;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.jvm.RemoteJVMLauncher;
import sun.plugin2.jvm.RemoteJVMLauncher.CallBack;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.main.server.ClientJVMSelectionParameters;
import sun.plugin2.main.server.HeartbeatThread;
import sun.plugin2.message.BestJREAvailableMessage;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.EventMessage;
import sun.plugin2.message.GetAppletMessage;
import sun.plugin2.message.GetNameSpaceMessage;
import sun.plugin2.message.HeartbeatMessage;
import sun.plugin2.message.JVMStartedMessage;
import sun.plugin2.message.JavaObjectOpMessage;
import sun.plugin2.message.JavaReplyMessage;
import sun.plugin2.message.LaunchJVMAppletMessage;
import sun.plugin2.message.MarkTaintedMessage;
import sun.plugin2.message.Message;
import sun.plugin2.message.ModalityChangeMessage;
import sun.plugin2.message.OverlayWindowMoveMessage;
import sun.plugin2.message.Pipe;
import sun.plugin2.message.PluginMessages;
import sun.plugin2.message.PrintAppletMessage;
import sun.plugin2.message.PrintAppletReplyMessage;
import sun.plugin2.message.ReleaseRemoteObjectMessage;
import sun.plugin2.message.RemoteCAContextIdMessage;
import sun.plugin2.message.SetAppletSizeMessage;
import sun.plugin2.message.SetJVMIDMessage;
import sun.plugin2.message.StartAppletAckMessage;
import sun.plugin2.message.StartAppletMessage;
import sun.plugin2.message.StopAppletAckMessage;
import sun.plugin2.message.StopAppletMessage;
import sun.plugin2.message.WindowActivationEventMessage;
import sun.plugin2.message.transport.SerializingTransport;
import sun.plugin2.message.transport.TransportFactory;
import sun.plugin2.util.ColorUtil;
import sun.plugin2.util.ColorUtil.ColorRGB;
import sun.plugin2.util.SystemUtil;

public class PluginMain
  implements ModalityInterface
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static final boolean VERBOSE = SystemUtil.getenv("JPI_PLUGIN2_VERBOSE") != null;
  private static final boolean NO_HEARTBEAT = SystemUtil.getenv("JPI_PLUGIN2_NO_HEARTBEAT") != null;
  private TransportFactory transportFactory;
  private Pipe pipe;
  private int jvmID;
  private boolean jvmTainted = false;
  private boolean separateJVM;
  private boolean anyAppletRelaunchOccurred;
  private boolean isSecureJVM = true;
  private Applet2ClassLoaderCache classLoaderCache = new Applet2ClassLoaderCache(Applet2Manager.getCacheEntryCreator());
  private Applet2ManagerCache instanceCache = new Applet2ManagerCache();
  private final Map applets = Collections.synchronizedMap(new HashMap());
  private final Set disconnectedManagers = Collections.synchronizedSet(new HashSet());
  private boolean usingModalityListener;
  private boolean sendConservativeModalNotifications;
  private long lastReactivationTime;
  private static final long MIN_REACTIVATION_DELAY = 500L;
  protected volatile boolean shouldShutdown;
  private Timer idleTimer = null;
  private HeartbeatThread hbt = null;
  private ThreadGroup mainThreadGroup;
  private static final int AWT_LIGHTGRAY = 12632256;
  static final Object lock = new Object();
  static ModalityHelper theModalityHelper = null;
  private DragListener pluginMainDragListener;
  private static final String PROTOCOL_HANDLERS = "java.protocol.handler.pkgs";
  private static final long IDLE_TIMEOUT = 60000L;
  private Map modalityMap = new HashMap();
  private Map appletToDialogMap = new HashMap();
  private boolean modalDialogHasPopped = false;

  private synchronized boolean isJVMTainted()
  {
    return this.jvmTainted;
  }

  private synchronized void startIdleTimer()
  {
    if (this.idleTimer == null)
    {
      this.idleTimer = new Timer();
      this.idleTimer.schedule(new AutoShutdownTask(null), 60000L, 60000L);
    }
  }

  private synchronized void stopIdleTimer()
  {
    if (this.idleTimer != null)
    {
      this.idleTimer.cancel();
      this.idleTimer = null;
    }
  }

  private ModalityHelper getModalityHelper()
  {
    synchronized (lock)
    {
      if (theModalityHelper == null)
      {
        UIFactory localUIFactory = ToolkitStore.getUI();
        if ((localUIFactory instanceof PluginUIFactory))
          theModalityHelper = ((PluginUIFactory)localUIFactory).getModalityHelper();
      }
    }
    return theModalityHelper;
  }

  protected void run(String[] paramArrayOfString)
    throws IOException
  {
    long l1 = 0L;
    long l2 = SystemUtils.microTime();
    long l7 = 0L;
    long l8 = 0L;
    try
    {
      l7 = Long.parseLong(System.getProperty("__applet_launched"));
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      localNumberFormatException1.printStackTrace();
    }
    try
    {
      l8 = Long.parseLong(System.getProperty("__jvm_launched"));
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      localNumberFormatException2.printStackTrace();
    }
    DefaultPerfHelper localDefaultPerfHelper = null;
    if (DeployPerfUtil.isEnabled())
    {
      localDefaultPerfHelper = new DefaultPerfHelper();
      DeployPerfUtil.initialize(localDefaultPerfHelper);
    }
    long l9 = SystemUtils.microTime();
    long l3 = l7 > 0L ? l7 : l9;
    long l5 = l8 > 0L ? l8 : l9;
    long l4 = l9 - l3;
    Plugin2Manager.setAppletLaunchTime(l3, l4);
    long l6 = l9 - l5;
    Plugin2Manager.setJVMLaunchTime(l5, l6);
    if (null != localDefaultPerfHelper)
    {
      localDefaultPerfHelper.setInitTime(l3);
      localDefaultPerfHelper.setInitTime1(l5);
      l1 = DeployPerfUtil.put(0L, "PluginMain - run() - BEGIN (numbers are in unit of us)");
      System.out.println("PluginMain:");
      System.out.println("            Applet launch cost : " + l4 + " us");
      System.out.println("            Applet launch time : " + l3 + " us");
      System.out.println("            JVM launch cost    : " + l6 + " us");
      System.out.println("            JVM launch time    : " + l5 + " us");
      System.out.println("        pluginMainStartTimeJVM : " + l9 + " us");
      System.out.println("        pluginMainPerf Costs   : " + (SystemUtils.microTime() - l2) + " us");
    }
    ToolkitStore.setMode(1);
    new Thread(new Runnable()
    {
      public void run()
      {
        ToolkitStore.get().warmup();
        ProgressMonitor.warmup();
      }
    }).start();
    if ((DEBUG) && (VERBOSE))
    {
      System.out.print("PluginMain.run({");
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        if (i > 0)
          System.out.print(" ");
        System.out.print(paramArrayOfString[i]);
      }
      System.out.println("})");
    }
    this.mainThreadGroup = Thread.currentThread().getThreadGroup();
    this.transportFactory = TransportFactory.createForCurrentOS(paramArrayOfString);
    SerializingTransport localSerializingTransport = this.transportFactory.getTransport();
    PluginMessages.register(localSerializingTransport);
    this.pipe = new Pipe(localSerializingTransport, false);
    MessagePassingExecutionContext.setBrowserPID(-1L);
    Plugin2Manager.setDefaultAppletExecutionContext(new MessagePassingExecutionContext(null, this.pipe, -1, null));
    try
    {
      PluginProxySelector.initialize();
    }
    catch (Throwable localThrowable1)
    {
      if (DEBUG)
      {
        System.err.println("Error initializing PluginProxySelector (this error is expected on 1.4.2):");
        localThrowable1.printStackTrace();
      }
    }
    try
    {
      PluginCookieSelector.initialize();
    }
    catch (Throwable localThrowable2)
    {
      if (DEBUG)
      {
        System.err.println("Error initializing PluginCookieSelector (this error is expected on 1.4.2):");
        localThrowable2.printStackTrace();
      }
    }
    DeployPerfUtil.put("PluginMain - run() - post PluginCookieSelector.initialize()");
    setupModality();
    DeployPerfUtil.put("PluginMain - run() - post installModalityListener()");
    Cache6UpgradeHelper.getInstance();
    startIdleTimer();
    DeployPerfUtil.put(l1, "PluginMain - run() - END init - pre mainLoop()");
    try
    {
      mainLoop();
    }
    catch (RuntimeException localRuntimeException)
    {
      if (DEBUG)
        localRuntimeException.printStackTrace();
    }
    catch (Error localError)
    {
      if (DEBUG)
        localError.printStackTrace();
    }
    finally
    {
      this.instanceCache.clear();
      this.pipe.shutdown();
      this.transportFactory.dispose();
      if (this.disconnectedManagers.isEmpty())
      {
        if (DEBUG)
        {
          System.out.println("Exiting cleanly");
          if (VERBOSE)
            try
            {
              Thread.sleep(10000L);
            }
            catch (InterruptedException localInterruptedException)
            {
            }
        }
        exit(0);
      }
    }
  }

  private void unregisterApplet(Integer paramInteger, Plugin2Manager paramPlugin2Manager)
  {
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(paramInteger);
    Trace.println("PluginMain.unregisterApplet: " + paramInteger + " from mananger " + localPlugin2Manager, TraceLevel.BASIC);
    long l = DeployPerfUtil.put(0L, "PluginMain - unregisterApplet() - BEGIN");
    if ((localPlugin2Manager != null) && (!paramPlugin2Manager.equals(localPlugin2Manager)))
    {
      Exception localException = new Exception("PluginMain.unregisterApplet: " + paramInteger + ". Manager confusion: msg: " + paramPlugin2Manager + ", map: " + localPlugin2Manager);
      localException.printStackTrace();
    }
    if ((this.separateJVM) && (!this.anyAppletRelaunchOccurred))
    {
      if ((DEBUG) && (VERBOSE))
      {
        System.out.println("Exiting JVM because only applet in separate JVM just exited");
        try
        {
          Thread.sleep(10000L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
      }
      exitJVM(false);
    }
    LiveConnectSupport.appletStopped(paramInteger.intValue());
    PluginUIToolkit localPluginUIToolkit = (PluginUIToolkit)ToolkitStore.get();
    localPluginUIToolkit.getDragHelper().unregister(Applet2DragContext.getDragContext(paramPlugin2Manager));
    synchronized (this.applets)
    {
      this.applets.remove(paramInteger);
      if (this.applets.isEmpty())
        startIdleTimer();
    }
    DeployPerfUtil.put(l, "PluginMain - unregisterApplet() - END");
  }

  private void registerApplet(Integer paramInteger, Plugin2Manager paramPlugin2Manager)
  {
    if ((DEBUG) && (VERBOSE))
    {
      Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(paramInteger);
      System.out.println("PluginMain.registerApplet: " + paramInteger + " -> " + paramPlugin2Manager + ", previous manager: " + localPlugin2Manager);
    }
    this.applets.put(paramInteger, paramPlugin2Manager);
    LiveConnectSupport.appletStarted(paramInteger.intValue(), paramPlugin2Manager);
  }

  private void abortStartApplet(Plugin2Manager paramPlugin2Manager)
  {
    Integer localInteger = paramPlugin2Manager.getAppletID();
    if (DEBUG)
      System.out.println("PluginMain.abortStartApplet for applet ID " + localInteger);
    unregisterApplet(localInteger, paramPlugin2Manager);
    sendAppletAck(localInteger, 3);
  }

  private void sendAppletAck(Integer paramInteger, int paramInt)
  {
    try
    {
      this.pipe.send(new StartAppletAckMessage(null, paramInteger.intValue(), paramInt));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  private void mainLoop()
  {
    while (!this.shouldShutdown)
    {
      Message localMessage;
      try
      {
        localMessage = this.pipe.receive(0L);
      }
      catch (Exception localException)
      {
        if (DEBUG)
          localException.printStackTrace();
        this.shouldShutdown = true;
        break;
      }
      try
      {
        handleMessage(localMessage);
      }
      catch (Throwable localThrowable)
      {
        if (DEBUG)
          localThrowable.printStackTrace();
        if ((localThrowable instanceof IOException))
        {
          this.shouldShutdown = true;
        }
        else if ((localThrowable instanceof Error))
        {
          Trace.flush();
          try
          {
            Thread.sleep(100L);
          }
          catch (InterruptedException localInterruptedException)
          {
          }
          exit(-1);
        }
      }
    }
  }

  protected void handleMessage(Message paramMessage)
    throws Exception
  {
    if (paramMessage != null)
      switch (paramMessage.getID())
      {
      case 1:
        handleMessageSetJVMID((SetJVMIDMessage)paramMessage);
        break;
      case 3:
        handleMessageStartApplet((StartAppletMessage)paramMessage);
        break;
      case 18:
        handleLaunchJVM((LaunchJVMAppletMessage)paramMessage);
        break;
      case 5:
        handleMessageSetAppletSize((SetAppletSizeMessage)paramMessage);
        break;
      case 82:
      case 83:
      case 84:
      case 85:
      case 86:
        handleSyntheticEvent((EventMessage)paramMessage);
        break;
      case 80:
        handleMessageOverlayWindowMove((OverlayWindowMoveMessage)paramMessage);
        break;
      case 7:
        handleMessageSynthesizeWindowActivation((WindowActivationEventMessage)paramMessage);
        break;
      case 8:
        handleMessagePrintApplet((PrintAppletMessage)paramMessage);
        break;
      case 12:
        handleMessageStopApplet((StopAppletMessage)paramMessage);
        break;
      case 31:
        handleMessageGetApplet((GetAppletMessage)paramMessage);
        break;
      case 32:
        handleMessageGetNameSpace((GetNameSpaceMessage)paramMessage);
        break;
      case 33:
        if ((DEBUG) && (VERBOSE))
          System.out.println("PluginMain: processing JavaObjectOpMessage");
        LiveConnectSupport.doObjectOp((JavaObjectOpMessage)paramMessage);
        break;
      case 35:
        if ((DEBUG) && (VERBOSE))
          System.out.println("PluginMain: processing ReleaseRemoteObjectMessage");
        RemoteJavaObject localRemoteJavaObject = new RemoteJavaObject(-1, -1, ((ReleaseRemoteObjectMessage)paramMessage).getObjectID(), false);
        LiveConnectSupport.releaseRemoteObject(localRemoteJavaObject);
        break;
      case 15:
        if (this.hbt == null)
        {
          if (DEBUG)
            System.out.println("PluginMain: starting heartbeat");
          if (!NO_HEARTBEAT)
          {
            this.hbt = new Heartbeat((HeartbeatMessage)paramMessage);
            this.hbt.start();
          }
        }
        else if (DEBUG)
        {
          System.out.println("Main loop got more heartbeat after the initial one");
        }
        break;
      case 14:
        if ((DEBUG) && (VERBOSE))
          System.out.println("PluginMain: processing ShutdownJVMMessage");
        if ((!NO_HEARTBEAT) && (this.disconnectedManagers.isEmpty()))
        {
          this.instanceCache.clear();
          exitJVM(true);
        }
        break;
      default:
        System.err.println("sun.plugin2.main.client.PluginMain: unrecognized message ID " + paramMessage.getID());
      }
  }

  protected void cleanup()
    throws IOException
  {
    if (this.pipe != null)
      this.pipe.shutdown();
    if (this.transportFactory != null)
      this.transportFactory.dispose();
  }

  private void exitJVM(boolean paramBoolean)
  {
    try
    {
      if (paramBoolean)
        cleanup();
    }
    catch (Exception localException)
    {
    }
    finally
    {
      exit(0);
    }
  }

  protected void exit(int paramInt)
  {
    MemoryCache.shutdown();
    this.classLoaderCache.shutdown();
    sureExit(paramInt);
  }

  private static void sureExit(int paramInt)
  {
    try
    {
      if (DEBUG)
        new Throwable("DEBUG: System.exit(." + paramInt + ");").printStackTrace();
      System.exit(paramInt);
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
      Runtime.getRuntime().halt(paramInt);
    }
  }

  private void handleMessageSetJVMID(SetJVMIDMessage paramSetJVMIDMessage)
    throws IOException, JNLPException
  {
    this.jvmID = paramSetJVMIDMessage.getJVMID();
    long l = DeployPerfUtil.put(0L, "PluginMain - handleMessageSetJVMID() - BEGIN");
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing SetJVMIDMessage, params:");
    if (System.getProperty("jnlpx.session.data") != null)
    {
      SessionState.init(System.getProperty("jnlpx.session.data"));
      System.setProperty("jnlpx.session.data", "");
    }
    this.separateJVM = paramSetJVMIDMessage.isSeparateJVM();
    JVMParameters localJVMParameters = null;
    String[][] arrayOfString = paramSetJVMIDMessage.getParameters();
    localJVMParameters = new JVMParameters();
    localJVMParameters.getFromStringArrays(arrayOfString);
    if ((DEBUG) && (VERBOSE))
    {
      localObject = localJVMParameters.getCommandLineArguments(false, false);
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
        System.out.println("\t<" + (String)localIterator.next() + ">");
    }
    this.isSecureJVM = localJVMParameters.isSecure();
    localJVMParameters.setHtmlJavaArgs(paramSetJVMIDMessage.isHtmlJavaArgs());
    if (DEBUG)
      System.out.println("PluginMain: The running JVM is " + (this.isSecureJVM ? "" : "NOT ") + "secure+\n\tJVMParameters: " + localJVMParameters);
    JVMParameters.setRunningJVMParameters(localJVMParameters);
    if ((DEBUG) && (VERBOSE))
      System.out.println("Running JVMParams: " + localJVMParameters + "\n\t-> " + JVMParameters.getRunningJVMParameters());
    ServiceDelegate.initialize(paramSetJVMIDMessage.getBrowserType());
    Object localObject = paramSetJVMIDMessage.getUserHome();
    if (localObject != null)
      Platform.get().setUserHomeOverride((String)localObject);
    initializeApplet2Environment(localJVMParameters);
    JNLP2Manager.initializeExecutionEnvironment();
    LiveConnectSupport.initialize(this.pipe, paramSetJVMIDMessage.getJVMID());
    this.pipe.send(new JVMStartedMessage(null));
    l = DeployPerfUtil.put(l, "PluginMain - handleMessageSetJVMID() - END");
    DeployPerfUtil.put("PluginMain - Init - END ;  Total Time: " + (Plugin2Manager.getJVMLaunchCosts() + l) + " us");
  }

  protected void initializeApplet2Environment(JVMParameters paramJVMParameters)
  {
    Applet2Environment.initialize(paramJVMParameters.getCommandLineArgumentsAsString(false), true, false, new Plugin2ConsoleController(this.classLoaderCache, this.instanceCache), new MessagePassingExecutionContext(null, this.pipe, -1, null), getDialogHook());
  }

  private void handleMessageSetAppletSize(SetAppletSizeMessage paramSetAppletSizeMessage)
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing SetAppletSizeMessage");
    setAppletSize(new Integer(paramSetAppletSizeMessage.getAppletID()), paramSetAppletSizeMessage.getWidth(), paramSetAppletSizeMessage.getHeight());
  }

  private void setAppletSize(Integer paramInteger, int paramInt1, int paramInt2)
  {
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(paramInteger);
    if (localPlugin2Manager != null)
    {
      if (DEBUG)
        System.out.println("PluginMain: setting size of applet " + paramInteger + " to (" + paramInt1 + ", " + paramInt2 + ")");
      localPlugin2Manager.setAppletSize(paramInt1, paramInt2);
    }
  }

  private void handleMessageOverlayWindowMove(OverlayWindowMoveMessage paramOverlayWindowMoveMessage)
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing OverlayWindowMoveMessage");
    moveAppletWindow(new Integer(paramOverlayWindowMoveMessage.getAppletID()), (int)paramOverlayWindowMoveMessage.getLocation().x, (int)paramOverlayWindowMoveMessage.getLocation().y);
  }

  private void moveAppletWindow(Integer paramInteger, int paramInt1, int paramInt2)
  {
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(paramInteger);
    if (localPlugin2Manager != null)
    {
      if (DEBUG)
        System.out.println("PluginMain: setting location of applet window" + paramInteger + " to (" + paramInt1 + ", " + paramInt2 + ")");
      localPlugin2Manager.getAppletParent().setPosition(paramInt1, paramInt2);
    }
  }

  private void handleMessageStartApplet(final StartAppletMessage paramStartAppletMessage)
  {
    long l1 = SystemUtils.microTime();
    long l2 = paramStartAppletMessage.getAppletLaunchTime();
    boolean bool1;
    if (l2 > l1)
    {
      l2 = l1;
      bool1 = true;
    }
    else
    {
      bool1 = false;
    }
    long l3 = l1 - l2;
    if (DeployPerfUtil.isEnabled())
    {
      DeployPerfUtil.setInitTime(l2);
      boolean bool2;
      if (Plugin2Manager.getAppletLaunchCosts() < l3)
      {
        bool2 = false;
      }
      else
      {
        bool2 = true;
        Plugin2Manager.setAppletLaunchTime(l2, l3);
        DeployPerfUtil.clear();
      }
      l1 = DeployPerfUtil.put(0L, "PluginMain - handleMessageStartApplet() - BEGIN (t0 set, newStart " + bool2 + ", tweaked " + bool1 + ")");
    }
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing StartAppletMessage");
    stopIdleTimer();
    AppletParameters localAppletParameters = paramStartAppletMessage.getParameters();
    final boolean bool3 = paramStartAppletMessage.isForDummyApplet();
    Object localObject1 = null;
    String str1 = paramStartAppletMessage.getDocumentBase();
    try
    {
      str1 = URLUtil.canonicalize(str1);
      str1 = URLUtil.canonicalizeDocumentBaseURL(str1);
      str1 = new URL(str1).toString();
    }
    catch (MalformedURLException localMalformedURLException)
    {
    }
    catch (NullPointerException localNullPointerException)
    {
    }
    String str2 = str1;
    String str3 = null;
    try
    {
      str3 = (String)localAppletParameters.get(JNLP2Tag.JNLP_HREF);
    }
    catch (Exception localException1)
    {
    }
    String str4 = (String)localAppletParameters.get("jnlp_embedded");
    byte[] arrayOfByte = SystemUtil.decodeBase64(str4);
    boolean bool4 = false;
    Object localObject2 = (String)localAppletParameters.get("__applet_relaunched");
    try
    {
      bool4 = Boolean.valueOf((String)localObject2).booleanValue();
    }
    catch (Exception localException2)
    {
    }
    if ((str3 == null) && (str4 == null))
    {
      localObject1 = this.instanceCache.get(str2, localAppletParameters);
      if (localObject1 == null)
      {
        localObject2 = bool3 ? null : getClassLoaderCacheForManager(localAppletParameters);
        localObject1 = new Applet2Manager((Applet2ClassLoaderCache)localObject2, this.instanceCache, bool4);
      }
    }
    else if ((str3 != null) || (arrayOfByte != null))
    {
      if (str3 != null)
        str3 = URLUtil.canonicalize(str3);
      localObject2 = null;
      try
      {
        localObject2 = (String)localAppletParameters.get("java_codebase");
        if (localObject2 == null)
          localObject2 = (String)localAppletParameters.get("codebase");
      }
      catch (Exception localException3)
      {
      }
      try
      {
        URL localURL = new URL(str2);
        localObject1 = new JNLP2Manager((String)localObject2, localURL, str3, arrayOfByte, bool4);
      }
      catch (Exception localException4)
      {
        System.out.println("PluginMain: JNLP2Manager creation: " + localException4);
        localException4.printStackTrace();
      }
    }
    localObject2 = new Integer(paramStartAppletMessage.getAppletID());
    if (null == localObject1)
    {
      if ((DEBUG) && (VERBOSE))
        System.out.println("PluginMain: Couldn't deduce a Plugin2Manager - bail out");
      sendAppletAck((Integer)localObject2, 3);
      return;
    }
    final Object localObject3 = localObject1;
    localObject3.setAppletID((Integer)localObject2);
    if (bool3)
      localObject3.setForDummyApplet(true);
    else
      Config.getHooks().preLaunch("plugin2", paramStartAppletMessage.getDocumentBase() + ": " + ArrayUtil.mapToString(localAppletParameters));
    localObject3.setSecureFlag(this.isSecureJVM);
    if (DEBUG)
    {
      System.out.println("PluginMain: starting applet ID " + localObject2 + " in parent window 0x" + Long.toHexString(paramStartAppletMessage.getParentNativeWindowHandle()) + " with parameters:");
      System.out.println("    Document base = " + str2);
      localObject4 = localAppletParameters.keySet().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        String str5 = (String)((Iterator)localObject4).next();
        System.out.println("    " + str5 + "=" + (String)localAppletParameters.get(str5));
      }
    }
    localObject3.setAppletExecutionContext(new MessagePassingExecutionContext(localAppletParameters, this.pipe, ((Integer)localObject2).intValue(), str2));
    DeployPerfUtil.put("PluginMain - setAppletExecutionContext()");
    Object localObject4 = localObject3.getAppletAppContext();
    DeployPerfUtil.put("PluginMain - getAppletAppContext()");
    registerApplet((Integer)localObject2, localObject3);
    DeployPerfUtil.put(l1, "PluginMain - handleMessageStartApplet() - END");
    new Thread(((AppContext)localObject4).getThreadGroup(), new Runnable()
    {
      private final Plugin2Manager val$manager;
      private final StartAppletMessage val$startMessage;
      private final boolean val$isForDummyApplet;
      private final AppContext val$appContext;
      private final Integer val$appletID;

      public void run()
      {
        PluginMain.AppletFrameCreator localAppletFrameCreator = new PluginMain.AppletFrameCreator(PluginMain.this, localObject3, paramStartAppletMessage, PluginMain.this.usingModalityListener ? null : PluginMain.this);
        PluginMain.StartAppletRunner localStartAppletRunner = new PluginMain.StartAppletRunner(PluginMain.this, localObject3, paramStartAppletMessage);
        if (!bool3)
        {
          this.val$appContext.invokeLater(localAppletFrameCreator);
          if (!PluginMain.this.initManager(localObject3))
            return;
          this.val$appContext.invokeLater(localStartAppletRunner);
        }
        else
        {
          localObject3.startWorkerThread("Applet " + this.val$appletID + " start thread", localStartAppletRunner);
        }
      }
    }).start();
  }

  protected void setupModality()
  {
    if (SystemUtil.getOSType() == 1)
    {
      this.usingModalityListener = getModalityHelper().installModalityListener(this);
      this.sendConservativeModalNotifications = (!this.usingModalityListener);
    }
    else
    {
      this.usingModalityListener = true;
    }
  }

  private synchronized DragListener getDragListener()
  {
    if (this.pluginMainDragListener == null)
      this.pluginMainDragListener = new PluginMainDragListener();
    return this.pluginMainDragListener;
  }

  private boolean initManager(final Plugin2Manager paramPlugin2Manager)
  {
    try
    {
      paramPlugin2Manager.initialize();
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
      Trace.println("Error while initializing manager: " + localException1 + ", bail out");
      AppContext localAppContext = paramPlugin2Manager.getAppletAppContext();
      final Exception localException2 = localException1;
      localAppContext.invokeLater(new Runnable()
      {
        private final Plugin2Manager val$manager;
        private final Exception val$exception;

        public void run()
        {
          paramPlugin2Manager.getApplet2Adapter().doShowError("Error while initializing managers", localException2, false);
        }
      });
      return false;
    }
    DeployPerfUtil.put("PluginMain.StartAppletRunner - post manager.initialize()");
    return true;
  }

  private static void installProtocolHandlers()
  {
    String str1 = System.getProperty("java.protocol.handler.pkgs");
    String str2 = "sun.plugin.net.protocol|com.sun.deploy.net.protocol";
    if ((str1 != null) && (str1.trim().length() > 0))
      str2 = str2 + "|" + str1;
    System.setProperty("java.protocol.handler.pkgs", str2);
  }

  private Plugin2Manager getActiveOrDisconnectedApplet(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(localInteger);
    if (localPlugin2Manager != null)
      return localPlugin2Manager;
    synchronized (this.disconnectedManagers)
    {
      Iterator localIterator = this.disconnectedManagers.iterator();
      while (localIterator.hasNext())
      {
        localPlugin2Manager = (Plugin2Manager)localIterator.next();
        if (localInteger.equals(localPlugin2Manager.getAppletID()))
          return localPlugin2Manager;
      }
    }
    return null;
  }

  private void handleLaunchJVM(LaunchJVMAppletMessage paramLaunchJVMAppletMessage)
  {
    Trace.println("Remote relaunch: " + paramLaunchJVMAppletMessage, TraceLevel.BASIC);
    final Integer localInteger = new Integer(paramLaunchJVMAppletMessage.getAppletID());
    final Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(localInteger);
    if (localPlugin2Manager == null)
    {
      Trace.println("Hosting manager is null, can't continue remote launch JVM", TraceLevel.BASIC);
      return;
    }
    JREInfo localJREInfo = JREInfo.findByJREPath(paramLaunchJVMAppletMessage.getJavaHome());
    JVMParameters localJVMParameters1 = SystemUtil.getDefaultVmArgs(localJREInfo);
    AppletParameters localAppletParameters = localPlugin2Manager.getParametersToRelaunch();
    String str1 = (String)localAppletParameters.get("__applet_relaunched");
    if ((str1 == null) || (!Boolean.valueOf(str1).booleanValue()))
    {
      Trace.println("Expect parameter __applet_relaunched", TraceLevel.BASIC);
      return;
    }
    String str2 = localPlugin2Manager.getAppletExecutionContext().getDocumentBase(localPlugin2Manager);
    JVMParameters localJVMParameters2 = SystemUtil.extractAppletParamsToJVMParameters(localAppletParameters, str2, true);
    ClientJVMSelectionParameters localClientJVMSelectionParameters = ClientJVMSelectionParameters.extract(localAppletParameters);
    localJVMParameters2 = SystemUtil.prepareJVMParameter(localJVMParameters2, localJREInfo, localJVMParameters1, localClientJVMSelectionParameters);
    RemoteJVMLauncher localRemoteJVMLauncher = new RemoteJVMLauncher(this.pipe, paramLaunchJVMAppletMessage, localJVMParameters2, localPlugin2Manager);
    localRemoteJVMLauncher.start();
    Trace.println("handleLaunchJVM(): RemoteJVMLauncer.setCallBack for " + localInteger);
    localRemoteJVMLauncher.setCallBack(new RemoteJVMLauncher.CallBack()
    {
      private final Integer val$appletID;
      private final Plugin2Manager val$manager;

      public void jvmStarted()
      {
        Trace.println("handleLaunchJVM(): now unregisterApplet " + localInteger);
        PluginMain.this.unregisterApplet(localInteger, localPlugin2Manager);
      }
    });
  }

  private void handleMessageSynthesizeWindowActivation(final WindowActivationEventMessage paramWindowActivationEventMessage)
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing SynthesizeWindowActivationMessage");
    final Integer localInteger = new Integer(paramWindowActivationEventMessage.getAppletID());
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(localInteger);
    if (localPlugin2Manager != null)
    {
      final Container localContainer = localPlugin2Manager.getAppletParentContainer();
      Object localObject1 = null;
      if (localContainer == null)
        localObject1 = OSType.isMac() ? localPlugin2Manager.getAppletParent() : null;
      final Object localObject2 = localObject1;
      localPlugin2Manager.getAppletAppContext().invokeLater(new Runnable()
      {
        private final WindowActivationEventMessage val$message;
        private final Container val$container;
        private final Window val$fxWindow;
        private final Integer val$appletID;

        public void run()
        {
          if ((PluginMain.DEBUG) && (PluginMain.VERBOSE))
            System.out.println("Calling synthesizeWindowActivation(" + paramWindowActivationEventMessage.getActive() + ") for applet " + paramWindowActivationEventMessage.getAppletID());
          Object localObject;
          if ((localContainer != null) && (!PluginMain.this.modalDialogHasPopped))
          {
            try
            {
              ((PluginEmbeddedFrame)localContainer).synthesizeWindowActivation(paramWindowActivationEventMessage.getActive());
            }
            catch (NoSuchMethodError localNoSuchMethodError)
            {
            }
          }
          else if ((localObject2 instanceof SynthesizedEventListener))
          {
            localObject = new HashMap();
            paramWindowActivationEventMessage.flattenInto((Map)localObject);
            ((SynthesizedEventListener)localObject2).synthesizeEvent((Map)localObject);
          }
          if (PluginMain.this.modalDialogHasPopped)
            PluginMain.this.modalDialogHasPopped = false;
          if (paramWindowActivationEventMessage.getActive())
          {
            localObject = PluginMain.this.getModalDialogForApplet(localInteger);
            if (localObject != null)
            {
              ((AbstractDialog)localObject).toFront();
              ((AbstractDialog)localObject).requestFocus();
              if (PluginMain.DEBUG)
                System.out.println("  Called Dialog.toFront() / requestFocus() for blocker of applet ID " + localInteger);
              if ((PluginMain.this.lastReactivationTime == 0L) || (System.currentTimeMillis() > PluginMain.this.lastReactivationTime + 500L))
              {
                PluginMain.this.getModalityHelper().reactivateDialog((AbstractDialog)localObject);
                PluginMain.this.lastReactivationTime = System.currentTimeMillis();
              }
            }
            else if (localContainer != null)
            {
              localContainer.requestFocus();
              if (PluginMain.DEBUG)
                System.out.println("  Called SyntheticEventHandler.requestFocus()");
            }
          }
        }
      });
    }
  }

  private void handleSyntheticEvent(final EventMessage paramEventMessage)
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing EventMessage");
    Integer localInteger = new Integer(paramEventMessage.getAppletID());
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: for applet ID = " + localInteger);
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(localInteger);
    if (localPlugin2Manager != null)
    {
      SynthesizedEventListener localSynthesizedEventListener1 = (SynthesizedEventListener)localPlugin2Manager.getAppletParentContainer();
      if (localSynthesizedEventListener1 == null)
        localSynthesizedEventListener1 = (SynthesizedEventListener)localPlugin2Manager.getAppletParent();
      final SynthesizedEventListener localSynthesizedEventListener2 = localSynthesizedEventListener1;
      localPlugin2Manager.getAppletAppContext().invokeLater(new Runnable()
      {
        private final EventMessage val$msg;
        private final SynthesizedEventListener val$container;

        public void run()
        {
          if ((PluginMain.DEBUG) && (PluginMain.VERBOSE))
            System.out.println("Calling synthesizeEvent() for applet " + paramEventMessage.getAppletID());
          if (localSynthesizedEventListener2 != null)
          {
            HashMap localHashMap = new HashMap();
            paramEventMessage.flattenInto(localHashMap);
            localSynthesizedEventListener2.synthesizeEvent(localHashMap);
          }
        }
      });
    }
  }

  private void handleMessagePrintApplet(final PrintAppletMessage paramPrintAppletMessage)
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing PrintAppletMessage");
    final Plugin2Manager localPlugin2Manager = getActiveOrDisconnectedApplet(paramPrintAppletMessage.getAppletID());
    if (localPlugin2Manager != null)
    {
      if (DEBUG)
        System.out.println("PluginMain: printing applet " + paramPrintAppletMessage.getAppletID() + " isPrinterDC = " + paramPrintAppletMessage.getIsPrinterDC());
      localPlugin2Manager.getAppletAppContext().invokeLater(new Runnable()
      {
        private final Plugin2Manager val$manager;
        private final PrintAppletMessage val$printAppletMessage;

        public void run()
        {
          try
          {
            PluginUIToolkit localPluginUIToolkit = (PluginUIToolkit)ToolkitStore.get();
            boolean bool = localPluginUIToolkit.printApplet(localPlugin2Manager, paramPrintAppletMessage.getAppletID(), PluginMain.this.pipe, paramPrintAppletMessage.getHDC(), paramPrintAppletMessage.getIsPrinterDC(), paramPrintAppletMessage.getX(), paramPrintAppletMessage.getY(), paramPrintAppletMessage.getWidth(), paramPrintAppletMessage.getHeight());
            PrintAppletReplyMessage localPrintAppletReplyMessage = new PrintAppletReplyMessage(paramPrintAppletMessage.getConversation(), paramPrintAppletMessage.getAppletID(), bool);
            PluginMain.this.pipe.send(localPrintAppletReplyMessage);
          }
          catch (IOException localIOException)
          {
            if (PluginMain.DEBUG)
              localIOException.printStackTrace();
          }
        }
      });
    }
  }

  private void handleMessageStopApplet(StopAppletMessage paramStopAppletMessage)
    throws IOException
  {
    DeployPerfUtil.setInitTime(SystemUtils.microTime());
    long l = DeployPerfUtil.put(0L, "PluginMain - handleMessageStopApplet() - BEGIN (time reset)");
    if (DEBUG)
      System.out.println("PluginMain: processing StopAppletMessage, applet ID " + paramStopAppletMessage.getAppletID());
    final Integer localInteger = new Integer(paramStopAppletMessage.getAppletID());
    Plugin2Manager localPlugin2Manager1 = (Plugin2Manager)this.applets.get(localInteger);
    Object localObject1;
    Object localObject2;
    if (localPlugin2Manager1 != null)
    {
      final Plugin2Manager localPlugin2Manager2 = localPlugin2Manager1;
      localObject1 = new Runnable()
      {
        private final Integer val$key;
        private final Plugin2Manager val$f_manager;

        public void run()
        {
          PluginMain.this.unregisterApplet(localInteger, localPlugin2Manager2);
        }
      };
      if (!isJVMTainted())
      {
        localObject2 = new Applet2StopListener()
        {
          public void stopFailed()
          {
            synchronized (PluginMain.this)
            {
              if (PluginMain.this.jvmTainted)
                return;
              PluginMain.this.jvmTainted = true;
            }
            ??? = new MarkTaintedMessage(null);
            try
            {
              PluginMain.this.pipe.send((Message)???);
            }
            catch (IOException localIOException)
            {
              if (PluginMain.DEBUG)
                localIOException.printStackTrace();
            }
          }
        };
        DeployPerfUtil.put("PluginMain - handleMessageStopApplet() - 1 - manager.stop() - START");
        localPlugin2Manager1.stop((Runnable)localObject1, (Applet2StopListener)localObject2);
        DeployPerfUtil.put("PluginMain - handleMessageStopApplet() - 1 - manager.stop() - END");
      }
      else
      {
        DeployPerfUtil.put("PluginMain - handleMessageStopApplet() - 2 - manager.stop() - START");
        localPlugin2Manager1.stop((Runnable)localObject1);
        DeployPerfUtil.put("PluginMain - handleMessageStopApplet() - 2 - manager.stop() - END");
      }
    }
    else
    {
      synchronized (this.disconnectedManagers)
      {
        localObject1 = this.disconnectedManagers.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localPlugin2Manager1 = (Plugin2Manager)((Iterator)localObject1).next();
          if (localInteger.equals(localPlugin2Manager1.getAppletID()))
          {
            localObject2 = localPlugin2Manager1.getAppletExecutionContext().getDocumentBase(localPlugin2Manager1);
            AppletParameters localAppletParameters = localPlugin2Manager1.getAppletExecutionContext().getAppletParameters();
            localPlugin2Manager1.setDisconnected();
            localPlugin2Manager1.setAppletExecutionContext(new DisconnectedExecutionContext(localAppletParameters, (String)localObject2));
            if (!localPlugin2Manager1.isEagerInstall())
              localPlugin2Manager1.installShortcuts();
            LiveConnectSupport.appletStopped(localInteger.intValue());
          }
        }
      }
      DeployPerfUtil.put("PluginMain - handleMessageStopApplet() - 3 - disconnectedApplets - POST");
    }
    ??? = new StopAppletAckMessage(paramStopAppletMessage.getConversation(), paramStopAppletMessage.getAppletID());
    this.pipe.send((Message)???);
    DeployPerfUtil.put(l, "PluginMain - handleMessageStopApplet() - END");
  }

  private void handleMessageGetApplet(GetAppletMessage paramGetAppletMessage)
    throws IOException
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing GetAppletMessage");
    Plugin2Manager localPlugin2Manager = getActiveOrDisconnectedApplet(paramGetAppletMessage.getAppletID());
    if (localPlugin2Manager != null)
    {
      if (localPlugin2Manager.hasErrorOccurred())
      {
        this.pipe.send(new JavaReplyMessage(paramGetAppletMessage.getConversation(), paramGetAppletMessage.getResultID(), null, false, "Applet ID " + paramGetAppletMessage.getAppletID() + localPlugin2Manager.getErrorMessage()));
        return;
      }
      localPlugin2Manager.waitUntilAppletStartDone();
    }
    if ((localPlugin2Manager == null) || (!localPlugin2Manager.getApplet2Adapter().isInstantiated()))
    {
      this.pipe.send(new JavaReplyMessage(paramGetAppletMessage.getConversation(), paramGetAppletMessage.getResultID(), null, false, "Applet ID " + paramGetAppletMessage.getAppletID() + " is not currently running"));
    }
    else
    {
      Applet2Status localApplet2Status = localPlugin2Manager.getAppletStatus();
      if (localApplet2Status != null)
      {
        Object localObject;
        if (localApplet2Status.getAdapter().isInstantiated())
        {
          localObject = LiveConnectSupport.exportObject(localApplet2Status.getAdapter().getLiveConnectObject(), paramGetAppletMessage.getAppletID(), false, true);
          this.pipe.send(new JavaReplyMessage(paramGetAppletMessage.getConversation(), paramGetAppletMessage.getResultID(), localObject, false, null));
        }
        else
        {
          localObject = localApplet2Status.getErrorMessage();
          if (localObject == null)
            localObject = "Unspecified error while fetching applet";
          this.pipe.send(new JavaReplyMessage(paramGetAppletMessage.getConversation(), paramGetAppletMessage.getResultID(), null, false, (String)localObject));
        }
      }
    }
  }

  private void handleMessageGetNameSpace(GetNameSpaceMessage paramGetNameSpaceMessage)
    throws IOException
  {
    if ((DEBUG) && (VERBOSE))
      System.out.println("PluginMain: processing GetNameSpaceMessage");
    this.pipe.send(new JavaReplyMessage(paramGetNameSpaceMessage.getConversation(), paramGetNameSpaceMessage.getResultID(), null, false, "Java namespace is no longer supported"));
  }

  private synchronized void pushDialogForApplet(Integer paramInteger, Plugin2Manager paramPlugin2Manager, AbstractDialog paramAbstractDialog)
  {
    ModalityLevel localModalityLevel = (ModalityLevel)this.modalityMap.get(paramAbstractDialog);
    if (localModalityLevel == null)
    {
      localModalityLevel = new ModalityLevel(paramInteger.intValue(), paramPlugin2Manager);
      this.modalityMap.put(paramAbstractDialog, localModalityLevel);
      paramPlugin2Manager.increaseModalityLevel();
    }
    localModalityLevel.push();
  }

  private synchronized AbstractDialog getModalDialogForApplet(Integer paramInteger)
  {
    Plugin2Manager localPlugin2Manager = (Plugin2Manager)this.applets.get(paramInteger);
    if (localPlugin2Manager == null)
      return null;
    Iterator localIterator = this.modalityMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      AbstractDialog localAbstractDialog = (AbstractDialog)localIterator.next();
      ModalityLevel localModalityLevel = (ModalityLevel)this.modalityMap.get(localAbstractDialog);
      if (localPlugin2Manager.isInSameAppContext(localModalityLevel.getManager()))
        return localAbstractDialog;
    }
    return null;
  }

  private synchronized ModalityLevel popDialog(AbstractDialog paramAbstractDialog)
  {
    ModalityLevel localModalityLevel = (ModalityLevel)this.modalityMap.get(paramAbstractDialog);
    if (localModalityLevel == null)
      return null;
    if (localModalityLevel.pop() == 0)
    {
      this.modalityMap.remove(paramAbstractDialog);
      this.modalDialogHasPopped = true;
      localModalityLevel.getManager().decreaseModalityLevel();
    }
    return localModalityLevel;
  }

  private boolean skipManagerForModalOperation(Plugin2Manager paramPlugin2Manager)
  {
    return (paramPlugin2Manager.isDisconnected()) || (this.disconnectedManagers.contains(paramPlugin2Manager));
  }

  public void modalityPushed(AbstractDialog paramAbstractDialog)
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    if (localPlugin2Manager == null)
    {
      localPlugin2Manager = getModalityHelper().getManagerShowingSystemDialog();
      if (localPlugin2Manager == null)
      {
        Trace.println("Skip modalityPush: manager null.", TraceLevel.UI);
        return;
      }
    }
    if (skipManagerForModalOperation(localPlugin2Manager))
    {
      Trace.println("Skip modalityPush: manager disconnected.", TraceLevel.UI);
      return;
    }
    Integer localInteger = localPlugin2Manager.getAppletID();
    if (localInteger == null)
    {
      Trace.println("Skip modalityPush: manager has no appletID.", TraceLevel.UI);
      return;
    }
    pushDialogForApplet(localInteger, localPlugin2Manager, paramAbstractDialog);
    if (Trace.isEnabled(TraceLevel.UI))
      Trace.println("Pushing modality for applet ID " + localInteger + " with dialog " + paramAbstractDialog, TraceLevel.UI);
    try
    {
      this.pipe.send(new ModalityChangeMessage(null, localInteger.intValue(), true));
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
  }

  public void modalityPopped(AbstractDialog paramAbstractDialog)
  {
    ModalityLevel localModalityLevel = popDialog(paramAbstractDialog);
    if (localModalityLevel == null)
      return;
    int i = localModalityLevel.getAppletID();
    if (DEBUG)
      System.out.println("modalityPopped for applet ID " + i);
    try
    {
      this.pipe.send(new ModalityChangeMessage(null, i, false));
    }
    catch (IOException localIOException)
    {
    }
  }

  private void sendConservativeModalPush()
  {
    if (this.sendConservativeModalNotifications)
    {
      Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
      if (localPlugin2Manager != null)
      {
        Integer localInteger = localPlugin2Manager.getAppletID();
        try
        {
          this.pipe.send(new ModalityChangeMessage(null, localInteger.intValue(), true));
        }
        catch (IOException localIOException)
        {
        }
      }
    }
  }

  private void sendConservativeModalPop()
  {
    if (this.sendConservativeModalNotifications)
    {
      Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
      if (localPlugin2Manager != null)
      {
        Integer localInteger = localPlugin2Manager.getAppletID();
        try
        {
          this.pipe.send(new ModalityChangeMessage(null, localInteger.intValue(), false));
        }
        catch (IOException localIOException)
        {
        }
      }
    }
  }

  private DialogHook getDialogHook()
  {
    return new DialogHook()
    {
      public Object beforeDialog(Object paramAnonymousObject)
      {
        Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
        if ((localPlugin2Manager != null) && (!PluginMain.this.skipManagerForModalOperation(localPlugin2Manager)))
        {
          PluginMain.this.sendConservativeModalPush();
          PluginMain.this.getModalityHelper().pushManagerShowingSystemDialog();
          return localPlugin2Manager.getAppletParentContainer();
        }
        return null;
      }

      public void afterDialog()
      {
        PluginMain.this.getModalityHelper().popManagerShowingSystemDialog();
        PluginMain.this.sendConservativeModalPop();
      }

      public boolean ignoreOwnerVisibility()
      {
        return true;
      }
    };
  }

  public static boolean performSSVValidation(Plugin2Manager paramPlugin2Manager)
    throws ExitException
  {
    boolean bool = Boolean.valueOf(paramPlugin2Manager.getParameter("__applet_ssv_validated")).booleanValue();
    if (bool)
      return false;
    String str1 = paramPlugin2Manager.getParameter("__applet_ssv_version");
    String str2 = paramPlugin2Manager.getParameter("__applet_request_version");
    String str3 = System.getProperty("java.version");
    int i = str3.indexOf("-");
    if (i != -1)
      str3 = str3.substring(0, i);
    if ((new VersionID(str3).equals(new VersionID(str1))) || ((str1 == null) && (new VersionID(str3).equals(new VersionID(str2)))))
      return false;
    String str4 = null;
    Object localObject;
    if ((paramPlugin2Manager instanceof JNLP2Manager))
    {
      localObject = ((JNLP2Manager)paramPlugin2Manager).getLaunchDesc();
      if (str1 == null)
      {
        if (str2 != null)
          SecureStaticVersioning.useLatest(((LaunchDesc)localObject).getAppInfo(), str2);
        return false;
      }
      ((LaunchDesc)localObject).selectJRE(true);
      if (SecureStaticVersioning.canUse((LaunchDesc)localObject, str1))
        str4 = str1;
    }
    else
    {
      localObject = paramPlugin2Manager.getAppInfo();
      if (str1 != null)
      {
        if (SecureStaticVersioning.canUse((AppInfo)localObject, str1))
          str4 = str1;
      }
      else
      {
        if (str2 != null)
          SecureStaticVersioning.useLatest((AppInfo)localObject, str2);
        return false;
      }
    }
    if (str4 != null)
    {
      paramPlugin2Manager.getAppletClassLoader().setSSVDialogShown(true);
      int j = str4.indexOf("-");
      if (j != -1)
        str4 = str4.substring(0, i);
      return !new VersionID(str4).equals(new VersionID(str3));
    }
    return false;
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      Config.setInstance(new PluginClientConfig());
      new PluginMain().run(paramArrayOfString);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      Trace.flush();
      try
      {
        Thread.sleep(100L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
      sureExit(1);
    }
  }

  private Applet2ClassLoaderCache getClassLoaderCacheForManager(Map paramMap)
  {
    String str = (String)paramMap.get("classloader_cache");
    if ((str != null) && (str.equalsIgnoreCase("false")))
      return null;
    if (!this.classLoaderCache.isInUse())
      return null;
    return this.classLoaderCache;
  }

  static
  {
    Platform.get().loadDeployNativeLib();
    installProtocolHandlers();
  }

  class AppletFrameCreator
    implements Runnable
  {
    final ModalityInterface modalityInterface;
    final Plugin2Manager manager;
    StartAppletMessage startMessage;

    AppletFrameCreator(Plugin2Manager paramStartAppletMessage, StartAppletMessage paramModalityInterface, ModalityInterface arg4)
    {
      this.manager = paramStartAppletMessage;
      this.startMessage = paramModalityInterface;
      Object localObject;
      this.modalityInterface = localObject;
    }

    private boolean shouldStop()
    {
      return (this.manager != null) && (this.manager.isStopping());
    }

    public void run()
    {
      if (shouldStop())
        return;
      long l = DeployPerfUtil.put(0L, "PluginMain.AppletFrameCreator - BEGIN - " + Thread.currentThread());
      Integer localInteger = new Integer(this.startMessage.getAppletID());
      AppletParameters localAppletParameters = this.startMessage.getParameters();
      boolean bool = this.startMessage.isForDummyApplet();
      DeployPerfUtil.put("PluginMain.AppletFrameCreator - post startMessage.get*()");
      if (shouldStop())
        return;
      Window localWindow = null;
      if (!bool)
      {
        try
        {
          WindowFactory localWindowFactory = ToolkitStore.getWindowFactory();
          if ((localWindowFactory instanceof PluginWindowFactory))
          {
            try
            {
              localWindow = ((PluginWindowFactory)localWindowFactory).createWindow(this.startMessage.getParentNativeWindowHandle(), this.startMessage.getCARenderServerName(), this.startMessage.useXEmbed(), this.modalityInterface, PluginMain.this.pipe, localInteger.intValue());
            }
            catch (AbstractMethodError localAbstractMethodError)
            {
              if (PluginMain.DEBUG)
                System.out.println("re-try with old createWindow");
              localWindow = ((PluginWindowFactory)localWindowFactory).createWindow(this.startMessage.getParentNativeWindowHandle(), 0L, this.startMessage.useXEmbed(), this.modalityInterface, PluginMain.this.pipe, localInteger.intValue());
            }
            if (PluginMain.DEBUG)
              System.out.println("Created Embedded Window " + localWindow);
          }
          else
          {
            System.err.println("WindowFactory (" + localWindowFactory + ") is unsuitable for plugin");
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
        if (localWindow == null)
        {
          System.err.println("PluginMain: could not create embedded frame");
          PluginMain.this.abortStartApplet(this.manager);
          return;
        }
      }
      DeployPerfUtil.put("PluginMain.AppletFrameCreator - post createEmbeddedFrame()");
      if (shouldStop())
        return;
      if (!bool)
      {
        int i = localWindow.getWindowLayerID();
        if (i != -1)
          try
          {
            if (PluginMain.DEBUG)
              System.out.println("Sending back " + i + " as remote layer");
            RemoteCAContextIdMessage localRemoteCAContextIdMessage = new RemoteCAContextIdMessage(null, localInteger.intValue(), i);
            PluginMain.this.pipe.send(localRemoteCAContextIdMessage);
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
            throw new RuntimeException(localIOException);
          }
        else if (PluginMain.DEBUG)
          System.out.println("Remote layer was -1 !!!");
        this.manager.setAppletParent(localWindow);
        int j = 256;
        int k = 256;
        int m = j;
        int n = k;
        String str1 = (String)localAppletParameters.get("width");
        String str2 = (String)localAppletParameters.get("height");
        try
        {
          m = Integer.parseInt(str1);
          n = Integer.parseInt(str2);
        }
        catch (Exception localException)
        {
          m = j;
          n = k;
          System.err.println("Error parsing width (\"" + str1 + "\") or height (\"" + str2 + "\")");
          System.err.println("Defaulting to (" + m + ", " + n + ")");
        }
        if ((m == 0) || (n == 0))
        {
          m = this.manager.getWidth();
          n = this.manager.getHeight();
        }
        String str3 = (String)localAppletParameters.get("boxbgcolor");
        ColorUtil.ColorRGB localColorRGB = null;
        if (str3 != null)
          localColorRGB = ColorUtil.createColorRGB("boxbgcolor", str3);
        int i1;
        if (localColorRGB != null)
          i1 = localColorRGB.rgb;
        else
          i1 = 12632256;
        localWindow.setBackground(i1);
        localWindow.setVisible(true);
        PluginMain.this.setAppletSize(localInteger, m, n);
        if (PluginMain.DEBUG)
          System.out.println("Made EmbeddedFrame for applet " + this.startMessage.getAppletID() + " visible");
        if ((Boolean.valueOf((String)localAppletParameters.get("draggable")).booleanValue()) || (SystemUtil.getenv("JPI_PLUGIN2_FORCE_DRAGGABLE") != null))
        {
          PluginUIToolkit localPluginUIToolkit = (PluginUIToolkit)ToolkitStore.get();
          localPluginUIToolkit.getDragHelper().register(Applet2DragContext.getDragContext(this.manager), PluginMain.this.getDragListener());
        }
        DeployPerfUtil.put(l, "PluginMain.AppletFrameCreator - post embeddedFrame setup");
      }
    }
  }

  private class AutoShutdownTask extends TimerTask
  {
    private AutoShutdownTask()
    {
    }

    public void run()
    {
      if ((PluginMain.this.disconnectedManagers.isEmpty()) && (PluginMain.this.applets.isEmpty()) && (PluginMain.this.instanceCache.isEmpty()))
      {
        if (PluginMain.DEBUG)
          System.out.println("JVM instance exiting due to no applets running");
        PluginMain.this.exit(0);
      }
    }

    AutoShutdownTask(PluginMain.1 arg2)
    {
      this();
    }
  }

  private class Heartbeat extends HeartbeatThread
  {
    public Heartbeat(HeartbeatMessage arg2)
    {
      super(PluginMain.this.pipe);
      Object localObject;
      this.conversation = localObject.getConversation();
      this.beat = localObject;
    }

    protected void handleStop()
    {
      PluginMain.this.shouldShutdown = true;
      if (PluginMain.this.disconnectedManagers.isEmpty())
      {
        PluginMain.this.instanceCache.clear();
        if (PluginMain.DEBUG)
        {
          System.out.println("JVM exiting due to no heartbeat reply");
          if (PluginMain.VERBOSE)
            try
            {
              Thread.sleep(10000L);
            }
            catch (InterruptedException localInterruptedException)
            {
            }
        }
        PluginMain.this.exit(0);
      }
    }
  }

  private static class ModalityLevel
  {
    private int level;
    private int appletID;
    private Plugin2Manager manager;

    public ModalityLevel(int paramInt, Plugin2Manager paramPlugin2Manager)
    {
      this.appletID = paramInt;
      this.manager = paramPlugin2Manager;
    }

    public int getAppletID()
    {
      return this.appletID;
    }

    public Plugin2Manager getManager()
    {
      return this.manager;
    }

    public synchronized void push()
    {
      this.level += 1;
    }

    public synchronized int pop()
    {
      return --this.level;
    }
  }

  class PluginMainDragListener
    implements DragListener
  {
    PluginMainDragListener()
    {
    }

    public void appletDraggingToDesktop(DragContext paramDragContext)
    {
      Plugin2Manager localPlugin2Manager = ((Applet2DragContext)paramDragContext).getManager();
      PluginMain.this.applets.remove(localPlugin2Manager.getAppletID());
      PluginMain.this.disconnectedManagers.add(localPlugin2Manager);
    }

    public void appletDroppedOntoDesktop(DragContext paramDragContext)
    {
      Plugin2Manager localPlugin2Manager = ((Applet2DragContext)paramDragContext).getManager();
      if (localPlugin2Manager.isEagerInstall())
        localPlugin2Manager.installShortcuts();
    }

    public void appletExternalWindowClosed(DragContext paramDragContext)
    {
      final Plugin2Manager localPlugin2Manager = ((Applet2DragContext)paramDragContext).getManager();
      if (!paramDragContext.isDisconnected())
      {
        PluginUIToolkit localPluginUIToolkit = (PluginUIToolkit)ToolkitStore.get();
        localPluginUIToolkit.getDragHelper().restore(paramDragContext);
        PluginMain.this.applets.put(localPlugin2Manager.getAppletID(), localPlugin2Manager);
        PluginMain.this.disconnectedManagers.remove(localPlugin2Manager);
      }
      else
      {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          private final Plugin2Manager val$manager;

          public Object run()
          {
            new Thread(PluginMain.this.mainThreadGroup, new Runnable()
            {
              public void run()
              {
                LiveConnectSupport.appletStopped(PluginMain.PluginMainDragListener.1.this.val$manager.getAppletID().intValue());
                PluginMain.PluginMainDragListener.1.this.val$manager.stop(null, null);
                PluginMain.this.disconnectedManagers.remove(PluginMain.PluginMainDragListener.1.this.val$manager);
                if ((PluginMain.this.shouldShutdown) && (PluginMain.this.disconnectedManagers.isEmpty()))
                {
                  PluginMain.this.instanceCache.clear();
                  PluginMain.this.exit(0);
                }
              }
            }).start();
            return null;
          }
        });
      }
    }
  }

  class StartAppletListener
    implements Applet2Listener
  {
    final Plugin2Manager manager;
    StartAppletMessage startMessage;
    boolean _ssvValidated = false;

    StartAppletListener(Plugin2Manager paramStartAppletMessage, StartAppletMessage arg3)
    {
      this.manager = paramStartAppletMessage;
      Object localObject;
      this.startMessage = localObject;
    }

    public String getBestJREVersion(Plugin2Manager paramPlugin2Manager, String paramString1, String paramString2)
    {
      String str = null;
      if (paramString1 != null)
      {
        Conversation localConversation = PluginMain.this.pipe.beginConversation();
        BestJREAvailableMessage localBestJREAvailableMessage1 = new BestJREAvailableMessage(localConversation, 1, paramString1, paramString2);
        try
        {
          PluginMain.this.pipe.send(localBestJREAvailableMessage1);
          BestJREAvailableMessage localBestJREAvailableMessage2 = (BestJREAvailableMessage)PluginMain.this.pipe.receive(0L, localConversation);
          if (localBestJREAvailableMessage2.isReply())
            str = localBestJREAvailableMessage2.getJavaVersion();
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
      return str;
    }

    public boolean appletSSVValidation(Plugin2Manager paramPlugin2Manager)
      throws ExitException
    {
      boolean bool = PluginMain.performSSVValidation(paramPlugin2Manager);
      this._ssvValidated = true;
      return bool;
    }

    public boolean isAppletRelaunchSupported()
    {
      return true;
    }

    public void appletJRERelaunch(Plugin2Manager paramPlugin2Manager, String paramString1, String paramString2)
    {
      PluginMain.this.anyAppletRelaunchOccurred = true;
      this.startMessage.collectJVMHealthData();
      Conversation localConversation = this.startMessage.getConversation();
      assert (localConversation == null);
      if ((null != paramString1) && (paramPlugin2Manager.isAppletRelaunched()))
        throw new InternalError("appletJRERelaunch: incorrectly looped in relaunch code");
      AppletParameters localAppletParameters = this.startMessage.getParameters();
      if (null != paramString1)
        localAppletParameters.put("__applet_ssv_version", paramString1);
      else
        localAppletParameters.remove("__applet_ssv_version");
      localAppletParameters.put("java_arguments", paramString2);
      localAppletParameters.put("__applet_relaunched", String.valueOf(true));
      String str = paramPlugin2Manager.getParameter("__jre_installed");
      if (null != str)
        localAppletParameters.put("__jre_installed", str);
      if (this._ssvValidated)
        localAppletParameters.put("__applet_ssv_validated", "true");
      str = paramPlugin2Manager.getParameter("javafx_version");
      if (null != str)
        localAppletParameters.put("javafx_version", str);
      str = paramPlugin2Manager.getParameter("__ui_tk");
      if (null != str)
        localAppletParameters.put("__ui_tk", str);
      str = paramPlugin2Manager.getParameter("__jfx_installed");
      if (null != str)
        localAppletParameters.put("__jfx_installed", str);
      File localFile = SessionState.save();
      if (localFile != null)
        localAppletParameters.put("__applet_session_data", localFile.getAbsolutePath());
      localAppletParameters.put("height", Integer.toString(paramPlugin2Manager.getHeight()));
      localAppletParameters.put("width", Integer.toString(paramPlugin2Manager.getWidth()));
      this.startMessage.setParameters(localAppletParameters);
      if (PluginMain.DEBUG)
      {
        System.out.println("PluginMain.StartAppletListener: appletJRERelaunch for applet ID " + paramPlugin2Manager.getAppletID());
        System.out.println("\t javaVersion: " + paramString1);
        System.out.println("\t jvmArgs: " + paramString2);
      }
      try
      {
        paramPlugin2Manager.setParametersToRelaunch(localAppletParameters);
        PluginMain.this.pipe.send(this.startMessage);
      }
      catch (IOException localIOException)
      {
        if (PluginMain.DEBUG)
          localIOException.printStackTrace();
      }
    }

    public void appletLoaded(Plugin2Manager paramPlugin2Manager)
    {
      if (PluginMain.DEBUG)
        System.out.println("PluginMain.StartAppletListener: appletLoaded for applet ID " + paramPlugin2Manager.getAppletID());
      Integer localInteger = new Integer(this.startMessage.getAppletID());
      PluginMain.this.sendAppletAck(localInteger, 0);
    }

    public void appletReady(Plugin2Manager paramPlugin2Manager)
    {
      PluginMain.this.sendAppletAck(paramPlugin2Manager.getAppletID(), 2);
    }

    public void appletErrorOccurred(Plugin2Manager paramPlugin2Manager)
    {
      if (PluginMain.DEBUG)
        System.out.println("PluginMain.StartAppletListener: appletErrorOccurred for applet ID " + paramPlugin2Manager.getAppletID());
      PluginMain.this.sendAppletAck(paramPlugin2Manager.getAppletID(), 3);
    }
  }

  class StartAppletRunner
    implements Runnable
  {
    final Plugin2Manager manager;
    StartAppletMessage startMessage;

    StartAppletRunner(Plugin2Manager paramStartAppletMessage, StartAppletMessage arg3)
    {
      this.manager = paramStartAppletMessage;
      Object localObject;
      this.startMessage = localObject;
    }

    public void run()
    {
      this.manager.addAppletListener(new PluginMain.StartAppletListener(PluginMain.this, this.manager, this.startMessage));
      DeployPerfUtil.put("PluginMain.StartAppletRunner - END");
      this.manager.start();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.PluginMain
 * JD-Core Version:    0.6.2
 */