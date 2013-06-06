package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.impl.awt.OldPluginAWTUtil;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import sun.applet.AppletClassLoader;
import sun.awt.EmbeddedFrame;
import sun.plugin.AppletStatusListener;
import sun.plugin.AppletViewer;
import sun.plugin.BeansApplet;
import sun.plugin.BeansViewer;
import sun.plugin.navig.motif.Worker;
import sun.plugin.services.BrowserService;
import sun.plugin.util.NotifierObject;
import sun.plugin.viewer.context.NetscapeAppletContext;
import sun.plugin.viewer.context.PluginAppletContext;
import sun.plugin.viewer.context.PluginBeansContext;
import sun.plugin.viewer.frame.XNetscapeEmbeddedFrame;
import sun.print.PSPrinterJob.PluginPrinter;

public class MNetscapePluginObject
  implements AppletStatusListener
{
  private EmbeddedFrame frame = null;
  protected AppletViewer panel = null;
  protected int id = -1;
  private int winID = 0;
  private int width = 0;
  private int height = 0;
  private String identifier = null;
  private boolean destroyed = false;
  private final Object syncDestroy = new Object();
  private boolean isAppletStoppedOrDestroyed = false;
  private AppletClassLoader tmpLoader;
  private boolean isBeans = false;
  private boolean isLegacy = false;
  private int startCount = -1;
  private boolean initialized = false;
  private boolean setDocBase = false;
  private int nativeJavaObject = 0;
  private boolean evaluatingExp = false;
  private boolean returnedExp = false;
  private String returnJS = null;
  private Object syncObject = new Object();

  MNetscapePluginObject(int paramInt, boolean paramBoolean, String paramString)
  {
    this.id = paramInt;
    this.identifier = paramString;
    this.panel = LifeCycleManager.getAppletPanel(paramString);
    this.isBeans = paramBoolean;
    Object localObject;
    BrowserService localBrowserService;
    if (!paramBoolean)
    {
      if (this.panel == null)
        this.panel = new AppletViewer();
      localObject = (PluginAppletContext)this.panel.getAppletContext();
      if (localObject == null)
      {
        localBrowserService = (BrowserService)ServiceManager.getService();
        localObject = (PluginAppletContext)localBrowserService.getAppletContext();
      }
      ((PluginAppletContext)localObject).setAppletContextHandle(paramInt);
      this.panel.setAppletContext((AppletContext)localObject);
    }
    else
    {
      if (this.panel == null)
        this.panel = new BeansViewer();
      localObject = (PluginBeansContext)this.panel.getAppletContext();
      if (localObject == null)
      {
        localBrowserService = (BrowserService)ServiceManager.getService();
        localObject = (PluginBeansContext)localBrowserService.getBeansContext();
      }
      ((PluginBeansContext)localObject).setAppletContextHandle(paramInt);
      this.panel.setAppletContext((AppletContext)localObject);
    }
    this.isLegacy = this.panel.isLegacyLifeCycle();
  }

  public static void waitForNotification(NotifierObject paramNotifierObject, long paramLong)
  {
    long l = System.currentTimeMillis();
    while ((!paramNotifierObject.getNotified()) && (System.currentTimeMillis() - l < paramLong))
      try
      {
        Thread.sleep(1L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
  }

  private EmbeddedFrame createFrame(final int paramInt1, final int paramInt2)
  {
    DeployPerfUtil.put("START - Java   - ENV - create embedded browser frame (Mozilla:UNIX)");
    this.tmpLoader = this.panel.getAppletClassLoader();
    XNetscapeEmbeddedFrame localXNetscapeEmbeddedFrame;
    if (this.tmpLoader == null)
    {
      localXNetscapeEmbeddedFrame = new XNetscapeEmbeddedFrame(paramInt1, paramInt2 != 0);
    }
    else
    {
      final EmbeddedFrame[] arrayOfEmbeddedFrame = new EmbeddedFrame[1];
      final NotifierObject localNotifierObject = new NotifierObject();
      try
      {
        Runnable local1 = new Runnable()
        {
          private final EmbeddedFrame[] val$box;
          private final int val$handle;
          private final int val$xembed;
          private final NotifierObject val$notifier;

          public void run()
          {
            try
            {
              if (MNetscapePluginObject.this.destroyed)
                return;
              arrayOfEmbeddedFrame[0] = new XNetscapeEmbeddedFrame(paramInt1, paramInt2 != 0);
            }
            catch (Exception localException)
            {
              localException.printStackTrace();
            }
            finally
            {
              localNotifierObject.setNotified();
            }
          }
        };
        OldPluginAWTUtil.invokeLater(this.tmpLoader.getAppContext(), local1);
        waitForNotification(localNotifierObject, 5000L);
        if (!localNotifierObject.getNotified())
          throw new RuntimeException("Error while creating embedded frame");
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      localXNetscapeEmbeddedFrame = arrayOfEmbeddedFrame[0];
    }
    DeployPerfUtil.put("END   - Java   - ENV - create embedded browser frame (Mozilla:UNIX)");
    return localXNetscapeEmbeddedFrame;
  }

  public void setWindow(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (paramInt3 == 0)
      paramInt3 = 1;
    if (paramInt4 == 0)
      paramInt4 = 1;
    if (this.winID == paramInt1)
    {
      if ((paramInt3 != this.width) || (paramInt4 != this.height))
        setFrameSize(paramInt3, paramInt4);
      return;
    }
    if (this.frame != null)
      destroyFrame();
    this.frame = null;
    this.winID = paramInt1;
    Trace.println("New window ID: " + Integer.toHexString(this.winID), TraceLevel.BASIC);
    if (this.winID != 0)
    {
      Trace.println("Value of xembed: " + paramInt2, TraceLevel.BASIC);
      this.frame = createFrame(this.winID, paramInt2);
      if (this.frame == null)
      {
        System.err.println("Creation of frame failed");
        return;
      }
      Object localObject = getJavaObject();
      if (localObject == null)
      {
        Trace.println("setWindow: call before applet exists:" + Integer.toHexString(this.winID), TraceLevel.BASIC);
        try
        {
          int i = Integer.parseInt(getParameter("width"));
          int j = Integer.parseInt(getParameter("height"));
          this.width = i;
          this.height = j;
        }
        catch (NumberFormatException localNumberFormatException)
        {
          setParameter("width", new Integer(paramInt3));
          setParameter("height", new Integer(paramInt4));
          this.width = paramInt3;
          this.height = paramInt4;
        }
      }
      else
      {
        setParameter("width", new Integer(paramInt3));
        setParameter("height", new Integer(paramInt4));
        this.width = paramInt3;
        this.height = paramInt4;
        this.panel.setSize(paramInt3, paramInt4);
        Applet localApplet;
        if ((localObject instanceof Applet))
        {
          localApplet = (Applet)localObject;
        }
        else
        {
          Trace.println("setWindow: Viewing a bean component", TraceLevel.BASIC);
          Component localComponent = (Component)localObject;
          localComponent.setSize(paramInt3, paramInt4);
          localApplet = (Applet)localComponent.getParent();
        }
        localApplet.resize(paramInt3, paramInt4);
        localApplet.setVisible(true);
      }
      this.panel.setBounds(0, 0, paramInt3, paramInt4);
      this.frame.setBounds(0, 0, paramInt3, paramInt4);
      initPlugin();
    }
  }

  private synchronized void initPlugin()
  {
    assert (this.panel != null);
    this.panel.addAppletStatusListener(this);
    if (this.frame != null)
    {
      try
      {
        this.frame.add(this.panel);
        OldPluginAWTUtil.invokeLater(this.frame, new Runnable()
        {
          public void run()
          {
            MNetscapePluginObject.this.frame.setVisible(true);
          }
        });
      }
      catch (Exception localException)
      {
      }
      if ((!this.initialized) && (this.setDocBase == true))
      {
        this.initialized = true;
        LifeCycleManager.checkLifeCycle(this.panel);
        ThreadGroup localThreadGroup = this.tmpLoader == null ? null : this.tmpLoader.getThreadGroup();
        new Initer(localThreadGroup, this.panel, this).start();
      }
    }
  }

  public synchronized void startPlugin()
  {
    assert (this.panel != null);
    if (this.initialized == true)
    {
      LifeCycleManager.startAppletPanel(this.panel);
    }
    else
    {
      if (this.startCount < 0)
        this.startCount = 0;
      this.startCount += 1;
    }
  }

  public synchronized void stopPlugin()
  {
    assert (this.panel != null);
    if (this.initialized == true)
      LifeCycleManager.stopAppletPanel(this.panel);
    else
      this.startCount -= 1;
    if (this.nativeJavaObject != 0)
    {
      releaseGlobalRef(this.nativeJavaObject);
      this.nativeJavaObject = 0;
    }
  }

  public synchronized void destroyPlugin()
  {
    assert (this.panel != null);
    this.panel.waitForLoadingDone(5000L);
    PluginAppletContext localPluginAppletContext = (PluginAppletContext)this.panel.getAppletContext();
    this.isAppletStoppedOrDestroyed = false;
    LifeCycleManager.destroyAppletPanel(this.identifier, this.panel);
    try
    {
      if ((this.panel.getAppletHandlerThread() != null) && (getLoadingStatus() != 7))
        synchronized (this.syncDestroy)
        {
          if (!this.isAppletStoppedOrDestroyed)
            this.syncDestroy.wait(5000L);
        }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    if (localPluginAppletContext != null)
    {
      localPluginAppletContext.setAppletContextHandle(-1);
      ((NetscapeAppletContext)localPluginAppletContext).onClose();
    }
    if (this.frame != null)
      destroyFrame();
    this.panel.removeAppletStatusListener(null);
    LifeCycleManager.cleanupAppletPanel(this.panel);
    LifeCycleManager.releaseAppletPanel(this.panel);
    this.panel = null;
    this.frame = null;
    this.destroyed = true;
  }

  private void signal(int paramInt)
  {
    switch (paramInt)
    {
    case 4:
      if (this.isLegacy)
        synchronized (this.syncDestroy)
        {
          this.isAppletStoppedOrDestroyed = true;
          this.syncDestroy.notify();
        }
      break;
    case 0:
    case 5:
      if (!this.isLegacy)
        synchronized (this.syncDestroy)
        {
          this.isAppletStoppedOrDestroyed = true;
          this.syncDestroy.notify();
        }
      break;
    case 6:
    case 7:
      synchronized (this.syncDestroy)
      {
        this.isAppletStoppedOrDestroyed = true;
        this.syncDestroy.notify();
      }
    case 1:
    case 2:
    case 3:
    }
  }

  public synchronized void setDocumentURL(String paramString)
  {
    assert (this.panel != null);
    try
    {
      notifyAll();
      this.panel.setDocumentBase(paramString);
      this.setDocBase = true;
      initPlugin();
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
  }

  private Frame getFrame()
  {
    return this.frame;
  }

  void setFocus()
  {
    if (this.frame != null)
      try
      {
        this.frame.synthesizeWindowActivation(true);
      }
      catch (NoSuchMethodError localNoSuchMethodError)
      {
      }
  }

  void setFrameSize(int paramInt1, int paramInt2)
  {
    Object localObject1 = getJavaObject();
    if (localObject1 == null)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      if (this.frame != null)
        this.frame.setSize(paramInt1, paramInt2);
      if (this.panel != null)
        this.panel.setBounds(0, 0, paramInt1, paramInt2);
      return;
    }
    if ((paramInt1 > 0) && (paramInt2 > 0))
      try
      {
        synchronized (this)
        {
          setParameter("width", new Integer(paramInt1));
          setParameter("height", new Integer(paramInt2));
          if (this.frame != null)
            this.frame.setSize(paramInt1, paramInt2);
          if (this.panel != null)
          {
            this.panel.setBounds(0, 0, paramInt1, paramInt2);
            Applet localApplet;
            if ((localObject1 instanceof Applet))
            {
              localApplet = (Applet)localObject1;
            }
            else
            {
              Component localComponent = (Component)localObject1;
              localComponent.setSize(paramInt1, paramInt2);
              localApplet = (Applet)localComponent.getParent();
            }
            if (localApplet != null)
            {
              localApplet.resize(paramInt1, paramInt2);
              localApplet.setVisible(true);
            }
          }
        }
      }
      catch (Throwable localThrowable)
      {
        Trace.printException(localThrowable);
      }
  }

  public Object getJavaObject()
  {
    Object localObject = null;
    if (this.panel != null)
      localObject = this.panel.getViewedObject();
    if ((localObject instanceof BeansApplet))
    {
      BeansApplet localBeansApplet = (BeansApplet)localObject;
      localObject = localBeansApplet.getBean();
    }
    return localObject;
  }

  public int getNativeJavaObject()
  {
    if (this.nativeJavaObject == 0)
    {
      Object localObject = getJavaObject();
      this.nativeJavaObject = convertToGlobalRef(localObject);
    }
    return this.nativeJavaObject;
  }

  int getLoadingStatus()
  {
    if (this.panel != null)
      return this.panel.getLoadingStatus();
    return 7;
  }

  public String getParameter(String paramString)
  {
    assert (this.panel != null);
    return this.panel.getParameter(paramString);
  }

  public void setParameter(String paramString, Object paramObject)
  {
    assert (this.panel != null);
    this.panel.setParameter(paramString, paramObject);
  }

  public void setBoxColors()
  {
    this.panel.setColorAndText();
  }

  public void doPrint(int paramInt1, int paramInt2, int paramInt3, int paramInt4, PrintStream paramPrintStream)
  {
    if (this.panel != null)
    {
      PSPrinterJob.PluginPrinter localPluginPrinter = new PSPrinterJob.PluginPrinter(this.panel, paramPrintStream, paramInt1, paramInt2, paramInt3, paramInt4);
      try
      {
        localPluginPrinter.printAll();
      }
      catch (Throwable localThrowable)
      {
        Trace.println("doPrint: printAll failed", TraceLevel.BASIC);
        Trace.printException(localThrowable);
      }
    }
  }

  public String evalString(int paramInt, String paramString)
  {
    synchronized (this.syncObject)
    {
      try
      {
        while (this.evaluatingExp)
          this.syncObject.wait();
        this.evaluatingExp = true;
        this.returnedExp = false;
        Worker.sendJSRequest(paramInt, paramString);
        while (!this.returnedExp)
          this.syncObject.wait();
        this.evaluatingExp = false;
        String str = this.returnJS;
        this.returnJS = null;
        this.syncObject.notifyAll();
        return str;
      }
      catch (Exception localException)
      {
        this.evaluatingExp = false;
        this.returnJS = null;
        this.syncObject.notifyAll();
        return null;
      }
    }
  }

  public void setJSReply(String paramString)
  {
    synchronized (this.syncObject)
    {
      if (this.returnJS == null)
        this.returnJS = paramString;
      else
        this.returnJS += paramString;
    }
  }

  public void finishJSReply()
  {
    synchronized (this.syncObject)
    {
      this.returnedExp = true;
      this.syncObject.notifyAll();
    }
  }

  public void statusChanged(int paramInt)
  {
    if (this.id >= 0)
    {
      Worker.notifyStatusChange(this.id, paramInt);
      signal(paramInt);
    }
  }

  private void destroyFrame()
  {
    final EmbeddedFrame localEmbeddedFrame = this.frame;
    try
    {
      OldPluginAWTUtil.invokeLater(localEmbeddedFrame, new Runnable()
      {
        private final Frame val$embeddedFrame;

        public void run()
        {
          localEmbeddedFrame.setVisible(false);
          localEmbeddedFrame.setEnabled(false);
          WindowEvent localWindowEvent = new WindowEvent(localEmbeddedFrame, 201);
          OldPluginAWTUtil.postEvent(localEmbeddedFrame, localWindowEvent);
        }
      });
    }
    catch (Exception localException)
    {
    }
  }

  private native int convertToGlobalRef(Object paramObject);

  private native void releaseGlobalRef(int paramInt);

  private class Initer extends Thread
  {
    AppletViewer panel;
    MNetscapePluginObject obj;

    Initer(ThreadGroup paramAppletViewer, AppletViewer paramMNetscapePluginObject, MNetscapePluginObject arg4)
    {
      super("Plugin-Initer");
      this.panel = paramMNetscapePluginObject;
      Object localObject;
      this.obj = localObject;
    }

    public void run()
    {
      LifeCycleManager.loadAppletPanel(this.panel);
      LifeCycleManager.initAppletPanel(this.panel);
      synchronized (this.obj)
      {
        this.obj.initialized = true;
        if (this.obj.destroyed)
          return;
        if (this.obj.startCount > 0)
        {
          this.obj.startCount = 0;
          this.obj.startPlugin();
        }
        else if (this.obj.startCount == 0)
        {
          this.obj.startPlugin();
          this.obj.stopPlugin();
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.viewer.MNetscapePluginObject
 * JD-Core Version:    0.6.2
 */