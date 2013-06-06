package sun.plugin2.main.server;

import com.sun.applet2.AppletParameters;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.JfxRuntime;
import com.sun.deploy.config.Platform;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.VersionID;
import com.sun.deploy.util.VersionString;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import netscape.javascript.JSException;
import sun.plugin2.jvm.JVMEventListener;
import sun.plugin2.jvm.JVMLauncher;
import sun.plugin2.jvm.ProcessLauncher;
import sun.plugin2.jvm.ProxyJVMLauncher;
import sun.plugin2.jvm.RemoteJVMLauncher;
import sun.plugin2.liveconnect.RemoteJavaObject;
import sun.plugin2.message.AppletMessage;
import sun.plugin2.message.BestJREAvailableMessage;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.CookieOpMessage;
import sun.plugin2.message.CookieReplyMessage;
import sun.plugin2.message.CustomSecurityManagerAckMessage;
import sun.plugin2.message.CustomSecurityManagerRequestMessage;
import sun.plugin2.message.FocusTransitionEventMessage;
import sun.plugin2.message.GetAppletMessage;
import sun.plugin2.message.GetAuthenticationMessage;
import sun.plugin2.message.GetAuthenticationReplyMessage;
import sun.plugin2.message.GetNameSpaceMessage;
import sun.plugin2.message.GetProxyMessage;
import sun.plugin2.message.HeartbeatMessage;
import sun.plugin2.message.JavaObjectOpMessage;
import sun.plugin2.message.JavaReplyMessage;
import sun.plugin2.message.JavaScriptCallMessage;
import sun.plugin2.message.JavaScriptEvalMessage;
import sun.plugin2.message.JavaScriptGetWindowMessage;
import sun.plugin2.message.JavaScriptMemberOpMessage;
import sun.plugin2.message.JavaScriptReleaseObjectMessage;
import sun.plugin2.message.JavaScriptReplyMessage;
import sun.plugin2.message.JavaScriptSlotOpMessage;
import sun.plugin2.message.JavaScriptToStringMessage;
import sun.plugin2.message.KeyEventMessage;
import sun.plugin2.message.Message;
import sun.plugin2.message.ModalityChangeMessage;
import sun.plugin2.message.MouseEventMessage;
import sun.plugin2.message.OverlayWindowMoveMessage;
import sun.plugin2.message.Pipe;
import sun.plugin2.message.PluginMessages;
import sun.plugin2.message.PrintAppletMessage;
import sun.plugin2.message.PrintAppletReplyMessage;
import sun.plugin2.message.PrintBandMessage;
import sun.plugin2.message.PrintBandReplyMessage;
import sun.plugin2.message.ProxyReplyMessage;
import sun.plugin2.message.ReleaseRemoteObjectMessage;
import sun.plugin2.message.RemoteCAContextIdMessage;
import sun.plugin2.message.ScrollEventMessage;
import sun.plugin2.message.SetAppletSizeMessage;
import sun.plugin2.message.SetJVMIDMessage;
import sun.plugin2.message.ShowDocumentMessage;
import sun.plugin2.message.ShowStatusMessage;
import sun.plugin2.message.ShutdownJVMMessage;
import sun.plugin2.message.StartAppletAckMessage;
import sun.plugin2.message.StartAppletMessage;
import sun.plugin2.message.StopAppletAckMessage;
import sun.plugin2.message.StopAppletMessage;
import sun.plugin2.message.TextEventMessage;
import sun.plugin2.message.WindowActivationEventMessage;
import sun.plugin2.message.transport.SerializingTransport;
import sun.plugin2.message.transport.TransportFactory;
import sun.plugin2.util.SystemUtil;

public class JVMInstance
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static final boolean VERBOSE = SystemUtil.getenv("JPI_PLUGIN2_VERBOSE") != null;
  private static final boolean NO_HEARTBEAT = SystemUtil.getenv("JPI_PLUGIN2_NO_HEARTBEAT") != null;
  private int jvmID;
  private JREInfo javaInfo;
  private TransportFactory transportFactory;
  private volatile Pipe pipe;
  private boolean started;
  private boolean tainted = false;
  private boolean exclusive = false;
  private JVMParameters originalParams;
  private JVMParameters realParams;
  private ProcessLauncher launcher;
  private volatile boolean shouldStop;
  private Map appletToPluginMap = new HashMap();
  private Set stopAckSet = new HashSet();
  private boolean restartable = true;
  private boolean gotJVMStartedMessage = false;
  private List queuedMessages = new ArrayList();
  private long appletLaunchTime = -1L;
  private final JVMHealthData healthData = new JVMHealthData();
  private VersionID fxVersion = null;
  private boolean useFxToolkit = false;
  int mostRecentAppletID = -1;

  public JVMInstance(long paramLong, int paramInt, JREInfo paramJREInfo, JVMParameters paramJVMParameters, boolean paramBoolean)
  {
    this(paramLong, paramInt, paramJREInfo, paramJVMParameters, paramBoolean, null);
  }

  public JVMInstance(long paramLong, int paramInt, JREInfo paramJREInfo, JVMParameters paramJVMParameters, boolean paramBoolean, AppletID paramAppletID)
  {
    this.jvmID = paramInt;
    this.javaInfo = paramJREInfo;
    this.appletLaunchTime = paramLong;
    this.originalParams = paramJVMParameters.copy();
    this.originalParams.setHtmlJavaArgs(paramJVMParameters.isHtmlJavaArgs());
    this.realParams = paramJVMParameters.copy();
    String str = paramJREInfo.getJREPath();
    JVMInstance localJVMInstance = JVMManager.getManager().getJVMInstance(paramAppletID);
    if ((localJVMInstance != null) && (SystemUtil.isWindowsVista()))
      this.launcher = new ProxyJVMLauncher(paramAppletID, localJVMInstance.pipe, paramLong, str);
    else
      this.launcher = new JVMLauncher(paramLong, str, paramJVMParameters);
    this.launcher.addJVMEventListener(new Listener(null));
    this.exclusive = paramBoolean;
  }

  public int getID()
  {
    return this.jvmID;
  }

  public JREInfo getJavaInfo()
  {
    return this.javaInfo;
  }

  public VersionID getProductVersion()
  {
    return getJavaInfo().getProductVersion();
  }

  public JVMParameters getParameters()
  {
    return this.originalParams;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(100);
    localStringBuffer.append("JVMInstance: id=").append(getID()).append(", ").append(getProductVersion()).append(getParameters());
    return localStringBuffer.toString();
  }

  public boolean isHealthy()
  {
    return (!isTainted()) && (this.healthData.isHealthy());
  }

  public void setAppletLaunchTime(long paramLong)
  {
    this.appletLaunchTime = paramLong;
  }

  public synchronized boolean appletRunning(AppletID paramAppletID)
  {
    return this.appletToPluginMap.get(new Integer(paramAppletID.getID())) != null;
  }

  public synchronized boolean isTainted()
  {
    return this.tainted;
  }

  synchronized void markTainted()
  {
    this.tainted = true;
    if (DEBUG)
      System.out.println("JVMInstance (" + this.javaInfo.getProductVersion() + ") marked tainted");
  }

  public synchronized boolean isExclusive()
  {
    return this.exclusive;
  }

  public void start()
    throws IOException
  {
    startImpl(false);
  }

  private void startImpl(boolean paramBoolean)
    throws IOException
  {
    if (this.started)
      throw new IllegalStateException("Already started");
    this.started = true;
    this.transportFactory = TransportFactory.createForCurrentOS();
    SerializingTransport localSerializingTransport = this.transportFactory.getTransport();
    PluginMessages.register(localSerializingTransport);
    if (!(this.launcher instanceof RemoteJVMLauncher))
      this.pipe = new Pipe(localSerializingTransport, true);
    this.launcher.addParameter("sun.plugin2.main.client.PluginMain");
    String[] arrayOfString = this.transportFactory.getChildProcessParameters();
    for (int i = 0; i < arrayOfString.length; i++)
      this.launcher.addParameter(arrayOfString[i]);
    this.launcher.start();
    this.healthData.setLaunchMicroSeconds(this.launcher.getJVMLaunchTime());
    Exception localException = this.launcher.getErrorDuringStartup();
    if (localException != null)
    {
      localException.printStackTrace();
      throw ((IOException)new IOException().initCause(localException));
    }
    new StreamMonitor(this.launcher.getInputStream());
    new StreamMonitor(this.launcher.getErrorStream());
    new WorkerThread().start();
    String[][] arrayOfString1 = this.realParams.copyToStringArrays();
    String str = Platform.get().getUserHomeOverride();
    SetJVMIDMessage localSetJVMIDMessage = new SetJVMIDMessage(null, this.jvmID, JVMManager.getBrowserType(), this.exclusive, this.originalParams.isHtmlJavaArgs(), str, arrayOfString1);
    if ((DEBUG) && (VERBOSE))
    {
      System.out.println("JVMInstance.start: JVMID original params array:");
      for (int j = 0; j < arrayOfString1.length; j++)
        for (int m = 0; m < arrayOfString1[j].length; m++)
          System.out.println("\t[" + j + "][" + m + "]: <" + arrayOfString1[j][m] + ">");
    }
    if (!paramBoolean)
    {
      try
      {
        sendMessage(localSetJVMIDMessage);
      }
      catch (IOException localIOException1)
      {
        if (DEBUG)
          localIOException1.printStackTrace();
      }
    }
    else
    {
      for (int k = 0; k < this.queuedMessages.size(); k++)
      {
        Message localMessage = (Message)this.queuedMessages.get(k);
        if (localMessage.getID() == 1)
        {
          this.queuedMessages.set(k, localSetJVMIDMessage);
          break;
        }
      }
      try
      {
        Iterator localIterator = this.queuedMessages.iterator();
        while (localIterator.hasNext())
          sendMessage((Message)localIterator.next());
      }
      catch (IOException localIOException2)
      {
        throw new RuntimeException(localIOException2);
      }
    }
  }

  public boolean errorOccurred()
  {
    return this.launcher.getErrorDuringStartup() != null;
  }

  public synchronized boolean exited()
  {
    return (this.launcher.exited()) && ((!this.restartable) || (this.gotJVMStartedMessage));
  }

  public int exitCode()
  {
    return this.launcher.getExitCode();
  }

  public boolean startApplet(AppletParameters paramAppletParameters, Plugin paramPlugin, long paramLong, String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3)
  {
    try
    {
      StartAppletMessage localStartAppletMessage = new StartAppletMessage(null, paramAppletParameters, paramLong, paramString, paramBoolean1, paramInt, paramPlugin.getDocumentBase(), paramBoolean2, this.appletLaunchTime);
      if (DEBUG)
      {
        System.out.println("JVMInstance for " + this.javaInfo.getProductVersion() + " sending start applet message");
        System.out.println("  isRelaunch: " + paramBoolean3);
        System.out.println("  appletLaunchTime: " + this.appletLaunchTime);
        System.out.println("  Parameters:");
        Iterator localIterator = paramAppletParameters.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          System.out.println("    " + str + "=" + (String)paramAppletParameters.get(str));
        }
      }
      synchronized (this)
      {
        if (isTainted())
        {
          if (DEBUG)
            System.out.println("JVMInstance for " + this.javaInfo.getProductVersion() + " is tainted. Don't start applet");
          return false;
        }
        sendMessage(localStartAppletMessage);
        registerApplet(paramInt, paramPlugin);
        return true;
      }
    }
    catch (IOException localIOException)
    {
      if (DEBUG)
        localIOException.printStackTrace();
    }
    return false;
  }

  public void setAppletSize(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      SetAppletSizeMessage localSetAppletSizeMessage = new SetAppletSizeMessage(null, paramInt1, paramInt2, paramInt3);
      sendMessage(localSetAppletSizeMessage);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendStopApplet(int paramInt)
  {
    Pipe localPipe = this.pipe;
    if (localPipe != null)
      try
      {
        StopAppletMessage localStopAppletMessage = new StopAppletMessage(null, paramInt);
        if (DEBUG)
          System.out.println("JVMInstance for " + this.javaInfo.getProductVersion() + " sending stop applet message for applet ID " + paramInt);
        localPipe.send(localStopAppletMessage);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
  }

  public synchronized boolean receivedStopAcknowledgment(int paramInt)
  {
    return this.stopAckSet.contains(new Integer(paramInt));
  }

  public synchronized void recycleAppletID(int paramInt)
  {
    unregisterApplet(paramInt);
  }

  private synchronized void recordStopAck(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    if (this.appletToPluginMap.get(localInteger) != null)
      this.stopAckSet.add(localInteger);
    Plugin localPlugin = getPluginForApplet(paramInt);
    if (localPlugin != null)
      localPlugin.notifyMainThread();
  }

  public void synthesizeWindowActivation(int paramInt, boolean paramBoolean)
  {
    try
    {
      WindowActivationEventMessage localWindowActivationEventMessage = new WindowActivationEventMessage(null, paramInt, paramBoolean);
      sendMessage(localWindowActivationEventMessage);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendGotFocus(int paramInt, boolean paramBoolean)
  {
    try
    {
      sendMessage(new FocusTransitionEventMessage(null, paramInt, paramBoolean));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendOverlayWindowMove(int paramInt, double paramDouble1, double paramDouble2)
  {
    try
    {
      sendMessage(new OverlayWindowMoveMessage(null, paramInt, paramDouble1, paramDouble2));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendMouseEvent(int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, int paramInt4, int paramInt5)
  {
    try
    {
      sendMessage(new MouseEventMessage(null, paramInt1, paramInt2, paramInt3, paramDouble1, paramDouble2, paramInt4, paramInt5));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendKeyEvent(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, boolean paramBoolean1, int paramInt4, boolean paramBoolean2)
  {
    try
    {
      sendMessage(new KeyEventMessage(null, paramInt1, paramInt2, paramInt3, paramString1, paramString2, paramBoolean1, paramInt4, paramBoolean2));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendScrollEvent(int paramInt1, double paramDouble1, double paramDouble2, int paramInt2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    try
    {
      sendMessage(new ScrollEventMessage(null, paramInt1, paramDouble1, paramDouble2, paramInt2, paramDouble3, paramDouble4, paramDouble5));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public void sendTextEvent(int paramInt, String paramString)
  {
    try
    {
      sendMessage(new TextEventMessage(null, paramInt, paramString));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public boolean printApplet(int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool1 = ServerPrintHelper.isPrinterDC(paramLong);
    if (!bool1)
      return true;
    PrintAppletReplyMessage localPrintAppletReplyMessage = null;
    Pipe localPipe = this.pipe;
    if (localPipe != null)
    {
      Conversation localConversation = this.pipe.beginConversation();
      try
      {
        PrintAppletMessage localPrintAppletMessage = new PrintAppletMessage(localConversation, paramInt1, paramLong, bool1, paramInt2, paramInt3, paramInt4, paramInt5);
        if (DEBUG)
          System.out.println("JVMInstance for " + this.javaInfo.getProductVersion() + " sending print applet message for applet ID " + paramInt1 + ", HDC = " + paramLong + ", isPrinterDC = " + bool1);
        localPipe.send(localPrintAppletMessage);
        localPrintAppletReplyMessage = (PrintAppletReplyMessage)localPipe.receive(0L, localConversation);
        if (localPrintAppletReplyMessage != null)
        {
          if (localPrintAppletReplyMessage.getAppletID() != paramInt1)
          {
            bool2 = false;
            return bool2;
          }
          boolean bool2 = localPrintAppletReplyMessage.getRes();
          return bool2;
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        if (DEBUG)
          localInterruptedException.printStackTrace();
      }
      catch (IOException localIOException)
      {
        if (DEBUG)
          localIOException.printStackTrace();
      }
      finally
      {
        localPipe.endConversation(localConversation);
      }
    }
    return false;
  }

  public void sendGetApplet(int paramInt1, int paramInt2)
    throws IOException
  {
    if (DEBUG)
      System.out.println("JVMInstance sending request for applet ID " + paramInt1 + " with result ID " + paramInt2);
    GetAppletMessage localGetAppletMessage = new GetAppletMessage(null, paramInt1, paramInt2);
    sendMessage(localGetAppletMessage);
  }

  public void sendGetNameSpace(int paramInt1, String paramString, int paramInt2)
    throws IOException
  {
    if (DEBUG)
      System.out.println("JVMInstance sending request for namespace \"" + paramString + "\" in applet ID " + paramInt1 + " with result ID " + paramInt2);
    GetNameSpaceMessage localGetNameSpaceMessage = new GetNameSpaceMessage(null, paramInt1, paramString, paramInt2);
    sendMessage(localGetNameSpaceMessage);
  }

  public void releaseRemoteJavaObject(int paramInt)
  {
    try
    {
      ReleaseRemoteObjectMessage localReleaseRemoteObjectMessage = new ReleaseRemoteObjectMessage(null, paramInt);
      if (DEBUG)
        System.out.println("JVMInstance for " + this.javaInfo.getProductVersion() + " sending release remote object message for ID " + paramInt);
      sendMessage(localReleaseRemoteObjectMessage);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void sendRemoteJavaObjectOp(Conversation paramConversation, RemoteJavaObject paramRemoteJavaObject, String paramString, int paramInt1, Object[] paramArrayOfObject, int paramInt2)
    throws IOException
  {
    JavaObjectOpMessage localJavaObjectOpMessage = new JavaObjectOpMessage(paramConversation, paramRemoteJavaObject, paramString, paramInt1, paramArrayOfObject, paramInt2);
    sendMessage(localJavaObjectOpMessage);
  }

  public long getAvailableHeapSize()
  {
    return this.healthData.getAvailableHeapSize();
  }

  boolean requestExclusiveUseOfVM()
  {
    if (this.appletToPluginMap.size() <= 1)
      this.exclusive = true;
    return this.exclusive;
  }

  private void processProxyRequest(final GetProxyMessage paramGetProxyMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramGetProxyMessage.getAppletID());
    if (localPlugin != null)
    {
      if (DEBUG)
        System.out.println("Delegate to plugin instance on browser main thread.");
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final GetProxyMessage val$proxyRequest;

        public void run()
        {
          try
          {
            if (JVMInstance.DEBUG)
              System.out.println("Browser main thread handle GetProxyMessage.");
            JVMInstance.this.sendMessage(ProxySupport.getProxyReply(localPlugin, paramGetProxyMessage));
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
            JVMInstance.this.shutdown();
          }
        }
      });
    }
    else
    {
      if (DEBUG)
        System.out.println("No plugin found for Applet ID " + paramGetProxyMessage.getAppletID());
      try
      {
        sendMessage(new ProxyReplyMessage(paramGetProxyMessage.getConversation(), null));
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        shutdown();
      }
    }
  }

  private void processShowDocRequest(final ShowDocumentMessage paramShowDocumentMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramShowDocumentMessage.getAppletID());
    if (localPlugin != null)
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final ShowDocumentMessage val$showDocRequest;

        public void run()
        {
          localPlugin.showDocument(paramShowDocumentMessage.getURL(), paramShowDocumentMessage.getTarget());
        }
      });
  }

  private void processShowStatusRequest(final ShowStatusMessage paramShowStatusMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramShowStatusMessage.getAppletID());
    if (localPlugin != null)
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final ShowStatusMessage val$showStatusRequest;

        public void run()
        {
          localPlugin.showStatus(paramShowStatusMessage.getStatus());
        }
      });
  }

  private void processStartupStatusRequest(final StartAppletAckMessage paramStartAppletAckMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramStartAppletAckMessage.getAppletID());
    if (localPlugin != null)
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final StartAppletAckMessage val$startupStatusRequest;

        public void run()
        {
          localPlugin.startupStatus(paramStartAppletAckMessage.getStatus());
        }
      });
  }

  private void processGetAuthenticationRequest(final GetAuthenticationMessage paramGetAuthenticationMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramGetAuthenticationMessage.getAppletID());
    if (localPlugin != null)
    {
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final GetAuthenticationMessage val$message;

        public void run()
        {
          PasswordAuthentication localPasswordAuthentication = localPlugin.getAuthentication(paramGetAuthenticationMessage.getProtocol(), paramGetAuthenticationMessage.getHost(), paramGetAuthenticationMessage.getPort(), paramGetAuthenticationMessage.getScheme(), paramGetAuthenticationMessage.getRealm(), paramGetAuthenticationMessage.getRequestURL(), paramGetAuthenticationMessage.getProxyAuthentication());
          GetAuthenticationReplyMessage localGetAuthenticationReplyMessage = new GetAuthenticationReplyMessage(paramGetAuthenticationMessage.getConversation(), localPasswordAuthentication, null);
          try
          {
            JVMInstance.this.sendMessage(localGetAuthenticationReplyMessage);
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
            JVMInstance.this.shutdown();
          }
        }
      });
    }
    else
    {
      GetAuthenticationReplyMessage localGetAuthenticationReplyMessage = new GetAuthenticationReplyMessage(paramGetAuthenticationMessage.getConversation(), null, "No registered plugin for applet ID " + paramGetAuthenticationMessage.getAppletID());
      try
      {
        sendMessage(localGetAuthenticationReplyMessage);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        shutdown();
      }
    }
  }

  private void processCookieRequest(final CookieOpMessage paramCookieOpMessage)
  {
    final Plugin localPlugin = getPluginForApplet(paramCookieOpMessage.getAppletID());
    if (localPlugin != null)
      localPlugin.invokeLater(new Runnable()
      {
        private final Plugin val$plugin;
        private final CookieOpMessage val$request;

        public void run()
        {
          try
          {
            JVMInstance.this.sendMessage(CookieSupport.getCookieReply(localPlugin, paramCookieOpMessage));
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
            JVMInstance.this.shutdown();
          }
        }
      });
    else
      try
      {
        sendMessage(new CookieReplyMessage(paramCookieOpMessage.getConversation(), null, "No registered plugin for applet ID " + paramCookieOpMessage.getAppletID()));
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        shutdown();
      }
  }

  private void doJavaToJavaScript(int paramInt, final Conversation paramConversation, final LiveConnectHelper paramLiveConnectHelper)
  {
    Plugin localPlugin = getPluginForApplet(paramInt);
    if (localPlugin != null)
    {
      localPlugin.invokeLater(new Runnable()
      {
        private final JVMInstance.LiveConnectHelper val$helper;
        private final Conversation val$conversation;

        public void run()
        {
          Object localObject = null;
          String str = null;
          try
          {
            localObject = paramLiveConnectHelper.doWork();
          }
          catch (JSException localJSException)
          {
            str = localJSException.getMessage();
            if (str == null)
              str = localJSException.toString();
          }
          JavaScriptReplyMessage localJavaScriptReplyMessage = new JavaScriptReplyMessage(paramConversation, localObject, str);
          try
          {
            JVMInstance.this.sendMessage(localJavaScriptReplyMessage);
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
            JVMInstance.this.shutdown();
          }
        }
      });
    }
    else
    {
      JavaScriptReplyMessage localJavaScriptReplyMessage = new JavaScriptReplyMessage(paramConversation, null, "No registered plugin for applet ID " + paramInt);
      try
      {
        sendMessage(localJavaScriptReplyMessage);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        shutdown();
      }
    }
  }

  public synchronized void shutdown()
  {
    this.shouldStop = true;
    if (this.pipe != null)
      try
      {
        sendMessage(new ShutdownJVMMessage(null));
      }
      catch (IOException localIOException)
      {
        this.launcher.destroy();
      }
  }

  private void sendMessage(Message paramMessage)
    throws IOException
  {
    if (((paramMessage instanceof AppletMessage)) && (JVMManager.getManager().spoolAppletMessage((AppletMessage)paramMessage)) && (!(paramMessage instanceof SetAppletSizeMessage)))
      return;
    sendMessageDirect(paramMessage);
  }

  protected void sendMessageDirect(Message paramMessage)
    throws IOException
  {
    Pipe localPipe = null;
    synchronized (this)
    {
      localPipe = this.pipe;
      if ((localPipe != null) && (this.restartable) && (!this.gotJVMStartedMessage))
        this.queuedMessages.add(paramMessage);
    }
    if (localPipe != null)
      localPipe.send(paramMessage);
  }

  private synchronized void registerApplet(int paramInt, Plugin paramPlugin)
  {
    if (DEBUG)
      System.out.println("JVMInstance.registerApplet for applet ID " + paramInt + ", plugin " + paramPlugin);
    this.appletToPluginMap.put(new Integer(paramInt), paramPlugin);
    LiveConnectSupport.initialize(paramInt, paramPlugin);
    ModalitySupport.initialize(paramInt, paramPlugin);
  }

  private synchronized void unregisterApplet(int paramInt)
  {
    if (DEBUG)
      System.out.println("JVMInstance.unregisterApplet for applet ID " + paramInt);
    Integer localInteger = new Integer(paramInt);
    this.appletToPluginMap.remove(localInteger);
    this.stopAckSet.remove(localInteger);
    LiveConnectSupport.shutdown(paramInt);
    ModalitySupport.shutdown(paramInt);
    if (isTainted())
      if (this.appletToPluginMap.isEmpty())
      {
        if (DEBUG)
          System.out.println("JVM instance for " + this.javaInfo.getProductVersion() + " shutting down due to tainting");
        shutdown();
      }
      else if (DEBUG)
      {
        System.out.println("JVM instance for " + this.javaInfo.getProductVersion() + " tainted, but still has " + this.appletToPluginMap.size() + " running applets");
      }
  }

  private synchronized Plugin getPluginForApplet(int paramInt)
  {
    if (paramInt < 0)
    {
      Plugin localPlugin = null;
      if (this.mostRecentAppletID >= 0)
      {
        paramInt = this.mostRecentAppletID;
        localPlugin = (Plugin)this.appletToPluginMap.get(new Integer(paramInt));
      }
      if (localPlugin != null)
        return localPlugin;
      Iterator localIterator = this.appletToPluginMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Integer localInteger = (Integer)localIterator.next();
        localPlugin = (Plugin)this.appletToPluginMap.get(localInteger);
        if (localPlugin != null)
        {
          this.mostRecentAppletID = localInteger.intValue();
          return localPlugin;
        }
      }
    }
    else
    {
      this.mostRecentAppletID = paramInt;
    }
    return (Plugin)this.appletToPluginMap.get(new Integer(paramInt));
  }

  private void restart()
  {
    this.started = false;
    this.restartable = false;
    disposePipe();
    this.realParams.clearUserArguments();
    this.launcher.clearUserArguments();
    try
    {
      startImpl(true);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }

  private void dispose()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this)
    {
      localArrayList.addAll(this.appletToPluginMap.keySet());
    }
    ??? = localArrayList.iterator();
    while (((Iterator)???).hasNext())
      unregisterApplet(((Integer)((Iterator)???).next()).intValue());
    disposePipe();
  }

  private void disposePipe()
  {
    if (this.pipe != null)
    {
      this.pipe.shutdown();
      try
      {
        this.transportFactory.dispose();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
      this.pipe = null;
      this.transportFactory = null;
    }
  }

  public JVMHealthData getHealthData()
  {
    return this.healthData;
  }

  public void setJfxSupport(JfxRuntime paramJfxRuntime)
  {
    if (paramJfxRuntime == null)
      this.fxVersion = null;
    else
      this.fxVersion = paramJfxRuntime.getProductVersion();
  }

  public VersionID getJfxAvailability()
  {
    return this.fxVersion;
  }

  public void setUseJfxToolkit(boolean paramBoolean)
  {
    this.useFxToolkit = paramBoolean;
  }

  public boolean isJfxToolkit()
  {
    return this.useFxToolkit;
  }

  private class Heartbeat extends HeartbeatThread
  {
    public Heartbeat()
    {
      super(JVMInstance.this.pipe, false);
    }

    protected boolean keepBeating()
    {
      return !JVMInstance.this.exited();
    }

    protected void handleNoAck()
    {
      if (!JVMInstance.this.launcher.exited())
      {
        if (JVMInstance.DEBUG)
          System.out.println("JVMInstance for " + JVMInstance.this.javaInfo.getProductVersion() + " killing sub-process because of no heartbeat reply");
        JVMInstance.this.launcher.destroy();
      }
    }

    protected void handleAck()
    {
      JVMInstance.this.healthData.updateFrom(this.beat.getHealthData());
    }
  }

  private class Listener
    implements JVMEventListener
  {
    private Listener()
    {
    }

    public void jvmExited(ProcessLauncher paramProcessLauncher)
    {
      synchronized (JVMInstance.this)
      {
        if ((!(paramProcessLauncher instanceof ProxyJVMLauncher)) && (JVMInstance.this.restartable) && (!JVMInstance.this.gotJVMStartedMessage))
        {
          JVMInstance.this.restart();
        }
        else
        {
          if (JVMInstance.DEBUG)
            System.out.println("JVM ID" + JVMInstance.this.jvmID + " for " + JVMInstance.this.javaInfo.getProductVersion() + " exited");
          JVMInstance.this.dispose();
        }
      }
    }

    Listener(JVMInstance.1 arg2)
    {
      this();
    }
  }

  private static abstract class LiveConnectHelper
  {
    private LiveConnectHelper()
    {
    }

    public abstract Object doWork()
      throws JSException;

    LiveConnectHelper(JVMInstance.1 param1)
    {
      this();
    }
  }

  private class StreamMonitor
    implements Runnable
  {
    private String versionString;
    private InputStream istream;

    public StreamMonitor(InputStream arg2)
    {
      Object localObject;
      this.istream = localObject;
      this.versionString = JVMInstance.this.javaInfo.getProductVersion().toString();
      new Thread(this, "JRE " + this.versionString + " Output Reader Thread").start();
    }

    public void run()
    {
      byte[] arrayOfByte = new byte[4096];
      try
      {
        int i = 0;
        do
        {
          i = this.istream.read(arrayOfByte);
          if ((JVMInstance.DEBUG) && (i > 0))
          {
            System.out.print("JRE " + this.versionString + ": ");
            System.out.write(arrayOfByte, 0, i);
            System.out.flush();
          }
        }
        while (i >= 0);
      }
      catch (IOException localIOException1)
      {
        try
        {
          this.istream.close();
        }
        catch (IOException localIOException2)
        {
        }
      }
    }
  }

  private class WorkerThread extends Thread
  {
    public WorkerThread()
    {
      super();
    }

    public void run()
    {
      try
      {
        while (!JVMInstance.this.shouldStop)
        {
          Message localMessage = null;
          Pipe localPipe = JVMInstance.this.pipe;
          if (localPipe == null)
            return;
          if (localPipe != null)
            localMessage = localPipe.receive(2000L);
          if (localMessage != null)
          {
            Object localObject1;
            Object localObject3;
            switch (localMessage.getID())
            {
            case 2:
              synchronized (JVMInstance.this)
              {
                JVMInstance.this.gotJVMStartedMessage = true;
                JVMInstance.this.queuedMessages.clear();
                if ((!JVMInstance.this.realParams.contains("-Xdebug")) && (!JVMInstance.NO_HEARTBEAT))
                {
                  JVMInstance.Heartbeat localHeartbeat = new JVMInstance.Heartbeat(JVMInstance.this);
                  localHeartbeat.start();
                }
              }
              break;
            case 13:
              ??? = (StopAppletAckMessage)localMessage;
              int i = ((StopAppletAckMessage)???).getAppletID();
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing StopAppletAckMessage for applet ID " + i);
              JVMInstance.this.recordStopAck(i);
              break;
            case 41:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing GetProxyMessage");
              ??? = (GetProxyMessage)localMessage;
              JVMInstance.this.processProxyRequest((GetProxyMessage)???);
              break;
            case 21:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptGetWindowMessage");
              ??? = (JavaScriptGetWindowMessage)localMessage;
              JVMManager.getManager().drainAppletMessages(new AppletID(((JavaScriptGetWindowMessage)???).getAppletID()));
              JVMInstance.this.doJavaToJavaScript(((JavaScriptGetWindowMessage)???).getAppletID(), ((JavaScriptGetWindowMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptGetWindowMessage)???)
              {
                private final JavaScriptGetWindowMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  return LiveConnectSupport.javaScriptGetWindow(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID());
                }
              });
              break;
            case 22:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptCallMessage");
              ??? = (JavaScriptCallMessage)localMessage;
              JVMInstance.this.doJavaToJavaScript(((JavaScriptCallMessage)???).getAppletID(), ((JavaScriptCallMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptCallMessage)???)
              {
                private final JavaScriptCallMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  return LiveConnectSupport.javaScriptCall(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getMethodName(), this.val$jsMsg.getArguments());
                }
              });
              break;
            case 23:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptEvalMessage");
              ??? = (JavaScriptEvalMessage)localMessage;
              JVMInstance.this.doJavaToJavaScript(((JavaScriptEvalMessage)???).getAppletID(), ((JavaScriptEvalMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptEvalMessage)???)
              {
                private final JavaScriptEvalMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  return LiveConnectSupport.javaScriptEval(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getCode());
                }
              });
              break;
            case 24:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptMemberOpMessage");
              ??? = (JavaScriptMemberOpMessage)localMessage;
              JVMInstance.this.doJavaToJavaScript(((JavaScriptMemberOpMessage)???).getAppletID(), ((JavaScriptMemberOpMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptMemberOpMessage)???)
              {
                private final JavaScriptMemberOpMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  switch (this.val$jsMsg.getOperationKind())
                  {
                  case 1:
                    return LiveConnectSupport.javaScriptGetMember(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getMemberName());
                  case 2:
                    LiveConnectSupport.javaScriptSetMember(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getMemberName(), this.val$jsMsg.getArgument());
                    return null;
                  case 3:
                    LiveConnectSupport.javaScriptRemoveMember(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getMemberName());
                    return null;
                  }
                  throw new JSException("Unexpected JavaScript member operation " + this.val$jsMsg.getOperationKind());
                }
              });
              break;
            case 25:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptSlotOpMessage");
              ??? = (JavaScriptSlotOpMessage)localMessage;
              JVMInstance.this.doJavaToJavaScript(((JavaScriptSlotOpMessage)???).getAppletID(), ((JavaScriptSlotOpMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptSlotOpMessage)???)
              {
                private final JavaScriptSlotOpMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  switch (this.val$jsMsg.getOperationKind())
                  {
                  case 1:
                    return LiveConnectSupport.javaScriptGetSlot(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getSlot());
                  case 2:
                    LiveConnectSupport.javaScriptSetSlot(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject(), this.val$jsMsg.getSlot(), this.val$jsMsg.getArgument());
                    return null;
                  }
                  throw new JSException("Unexpected JavaScript slot operation " + this.val$jsMsg.getOperationKind());
                }
              });
              break;
            case 26:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptToStringMessage");
              ??? = (JavaScriptToStringMessage)localMessage;
              JVMInstance.this.doJavaToJavaScript(((JavaScriptToStringMessage)???).getAppletID(), ((JavaScriptToStringMessage)???).getConversation(), new JVMInstance.LiveConnectHelper((JavaScriptToStringMessage)???)
              {
                private final JavaScriptToStringMessage val$jsMsg;

                public Object doWork()
                  throws JSException
                {
                  return LiveConnectSupport.javaScriptToString(this.val$jsMsg.getConversation(), this.val$jsMsg.getAppletID(), this.val$jsMsg.getObject());
                }
              });
              break;
            case 28:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaScriptReleaseObjectMessage");
              ??? = (JavaScriptReleaseObjectMessage)localMessage;
              try
              {
                LiveConnectSupport.releaseObject(((JavaScriptReleaseObjectMessage)???).getAppletID(), ((JavaScriptReleaseObjectMessage)???).getObject());
              }
              catch (JSException localJSException)
              {
                if (JVMInstance.DEBUG)
                  localJSException.printStackTrace();
              }
            case 34:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing JavaReplyMessage");
              ??? = (JavaReplyMessage)localMessage;
              if (JVMInstance.DEBUG)
                System.out.println("JVMInstance received JavaReplyMessage with result ID " + ((JavaReplyMessage)???).getResultID() + (((JavaReplyMessage)???).getExceptionMessage() != null ? " (exception)" : ""));
              Object localObject2 = null;
              if (((JavaReplyMessage)???).getExceptionMessage() != null)
                localObject2 = new RuntimeException(((JavaReplyMessage)???).getExceptionMessage());
              else
                localObject2 = ((JavaReplyMessage)???).getResult();
              if (((JavaReplyMessage)???).isResultVoid())
                localObject2 = Void.TYPE;
              LiveConnectSupport.recordResult(new ResultID(((JavaReplyMessage)???).getResultID()), localObject2);
              break;
            case 51:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing ShowDocumentMessage");
              JVMInstance.this.processShowDocRequest((ShowDocumentMessage)localMessage);
              break;
            case 52:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing ShowStatusMessage");
              JVMInstance.this.processShowStatusRequest((ShowStatusMessage)localMessage);
              break;
            case 43:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing GetAuthenticationMessage");
              JVMInstance.this.processGetAuthenticationRequest((GetAuthenticationMessage)localMessage);
              break;
            case 45:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing CookieOpMessage");
              JVMInstance.this.processCookieRequest((CookieOpMessage)localMessage);
              break;
            case 61:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing ModalityChangeMessage");
              ??? = (ModalityChangeMessage)localMessage;
              ModalitySupport.modalityChanged(((ModalityChangeMessage)???).getAppletID(), ((ModalityChangeMessage)???).getModalityPushed());
              break;
            case 16:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing MarkTaintedMessage");
              JVMInstance.this.markTainted();
              boolean bool1;
              synchronized (JVMInstance.this)
              {
                bool1 = JVMInstance.this.appletToPluginMap.isEmpty();
              }
              if (bool1)
                try
                {
                  JVMInstance.this.sendMessage(new ShutdownJVMMessage(null));
                }
                catch (IOException localIOException1)
                {
                  if (JVMInstance.DEBUG)
                    localIOException1.printStackTrace();
                }
              break;
            case 4:
              localObject1 = (StartAppletAckMessage)localMessage;
              int j = ((StartAppletAckMessage)localObject1).getAppletID();
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing StartAppletAckMessage with:" + "\n\tappletID: " + j);
              JVMManager.getManager().drainAppletMessages(new AppletID(j));
              int k = ((StartAppletAckMessage)localObject1).getStatus();
              if (k > 0)
                JVMInstance.this.processStartupStatusRequest((StartAppletAckMessage)localMessage);
              break;
            case 3:
              localObject1 = (StartAppletMessage)localMessage;
              JVMInstance.this.healthData.updateFrom(((StartAppletMessage)localObject1).getHealthData());
              localObject3 = ((StartAppletMessage)localObject1).getParameters();
              String str1 = null;
              boolean bool4 = false;
              if (localObject3 != null)
              {
                String str2 = (String)((AppletParameters)localObject3).remove("__jre_installed");
                if (null != str2)
                  bool4 = Boolean.valueOf(str2).booleanValue();
                str1 = (String)((AppletParameters)localObject3).get("__applet_ssv_version");
                str2 = (String)((AppletParameters)localObject3).remove("__jfx_installed");
                if ((null != str2) && (Boolean.valueOf(str2).booleanValue()))
                  Platform.get().getInstalledJfxRuntimes(true);
              }
              int m = ((StartAppletMessage)localObject1).getAppletID();
              Integer localInteger = new Integer(m);
              Plugin localPlugin = null;
              synchronized (JVMInstance.this)
              {
                localPlugin = (Plugin)JVMInstance.this.appletToPluginMap.remove(localInteger);
              }
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
              {
                ??? = null;
                if (localObject3 != null)
                  ??? = (String)((AppletParameters)localObject3).get("java_arguments");
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing StartAppletMessage with:" + "\n\tappletID: " + m + "\n\targs: " + (String)??? + "\n\t__applet_ssv_version: " + str1 + "\n\tnewJREInstalled: " + bool4 + "\n\tplugin: " + localPlugin);
              }
              if (null == localPlugin)
              {
                System.out.println("ERROR: JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") failed to relaunch applet ID " + m + " because of null plugin instance");
              }
              else
              {
                ??? = JVMManager.getManager().relaunchApplet(JVMInstance.this.appletLaunchTime, (AppletParameters)localObject3, localPlugin, ((StartAppletMessage)localObject1).getParentNativeWindowHandle(), ((StartAppletMessage)localObject1).getCARenderServerName(), ((StartAppletMessage)localObject1).useXEmbed(), str1, m, bool4);
                if (JVMInstance.this.exclusive)
                  JVMInstance.this.sendMessage(new ShutdownJVMMessage(null));
              }
              break;
            case 81:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing RemoteCAContextMessage");
              localObject1 = (RemoteCAContextIdMessage)localMessage;
              localObject3 = JVMInstance.this.getPluginForApplet(((RemoteCAContextIdMessage)localObject1).getAppletID());
              if (localObject3 != null)
                ((Plugin)localObject3).invokeLater(new Runnable()
                {
                  private final Plugin val$plugin;
                  private final RemoteCAContextIdMessage val$contextMsg;

                  public void run()
                  {
                    this.val$plugin.hostRemoteCAContext(this.val$contextMsg.getContextId());
                  }
                });
              break;
            case 10:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing PrintBandMessage");
              localObject1 = (PrintBandMessage)localMessage;
              localObject3 = JVMInstance.this.getPluginForApplet(((PrintBandMessage)localObject1).getAppletID());
              if (localObject3 != null)
                try
                {
                  boolean bool3 = ServerPrintHelper.printBand(((PrintBandMessage)localObject1).getHDC(), ((PrintBandMessage)localObject1).getDataAsByteBuffer(), ((PrintBandMessage)localObject1).getOffset(), ((PrintBandMessage)localObject1).getSrcX(), ((PrintBandMessage)localObject1).getSrcY(), ((PrintBandMessage)localObject1).getSrcWidth(), ((PrintBandMessage)localObject1).getSrcHeight(), ((PrintBandMessage)localObject1).getDestX(), ((PrintBandMessage)localObject1).getDestY(), ((PrintBandMessage)localObject1).getDestWidth(), ((PrintBandMessage)localObject1).getDestHeight());
                  JVMInstance.this.sendMessage(new PrintBandReplyMessage(((PrintBandMessage)localObject1).getConversation(), ((PrintBandMessage)localObject1).getAppletID(), ((PrintBandMessage)localObject1).getDestY(), bool3));
                }
                catch (IOException localIOException2)
                {
                  if (JVMInstance.DEBUG)
                    localIOException2.printStackTrace();
                }
              break;
            case 17:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing BestJREAvailableMessage");
              localObject1 = (BestJREAvailableMessage)localMessage;
              localObject3 = JVMManager.getManager().getBestJREInfo(new VersionString(((BestJREAvailableMessage)localObject1).getJavaVersion()), new VersionString(((BestJREAvailableMessage)localObject1).getJfxVersion())).getProduct();
              JVMInstance.this.sendMessage(new BestJREAvailableMessage(((BestJREAvailableMessage)localObject1).getConversation(), 2, (String)localObject3));
              break;
            case 90:
              if ((JVMInstance.DEBUG) && (JVMInstance.VERBOSE))
                System.out.println("JVMInstance (" + JVMInstance.this.javaInfo.getProductVersion() + ") processing CustomSecurityManagerRequestMessage");
              localObject1 = (CustomSecurityManagerRequestMessage)localMessage;
              boolean bool2 = JVMInstance.this.requestExclusiveUseOfVM();
              CustomSecurityManagerAckMessage localCustomSecurityManagerAckMessage = new CustomSecurityManagerAckMessage(((CustomSecurityManagerRequestMessage)localObject1).getConversation(), bool2);
              JVMInstance.this.sendMessage(localCustomSecurityManagerAckMessage);
              break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 14:
            case 15:
            case 18:
            case 19:
            case 20:
            case 27:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 42:
            case 44:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            default:
              if (JVMInstance.DEBUG)
                System.err.println("JVMInstance.WorkerThread: unexpected message ID " + localMessage.getID() + " from client JVM instance");
              break;
            }
          }
        }
      }
      catch (Exception localException)
      {
        if (JVMInstance.DEBUG)
          localException.printStackTrace();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.JVMInstance
 * JD-Core Version:    0.6.2
 */