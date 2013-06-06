package sun.plugin.util;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.DeployUIManager;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.AccessController;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JDialog;
import sun.plugin2.applet.Plugin2Manager;
import sun.security.action.GetPropertyAction;

public final class PluginSysUtil extends DeploySysRun
{
  private static ThreadGroup pluginThreadGroup = null;
  private static ClassLoader pluginSysClassLoader = null;
  private static final SysExecutionThreadCreator sysExecutionThreadCreator = new SysExecutionThreadCreator(null);

  public static synchronized ThreadGroup getPluginThreadGroup()
  {
    if (pluginThreadGroup == null)
    {
      pluginSysClassLoader = Thread.currentThread().getContextClassLoader();
      pluginThreadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "Plugin Thread Group");
      createAppContext(pluginThreadGroup);
      try
      {
        Thread localThread = new Thread(pluginThreadGroup, new Runnable()
        {
          public void run()
          {
            DeployUIManager.setLookAndFeel();
          }
        });
        localThread.start();
        localThread.join();
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    return pluginThreadGroup;
  }

  public static Thread createPluginSysThread(Runnable paramRunnable)
  {
    Thread localThread = new Thread(pluginThreadGroup, paramRunnable);
    localThread.setContextClassLoader(pluginSysClassLoader);
    return localThread;
  }

  public static Thread createPluginSysThread(Runnable paramRunnable, String paramString)
  {
    Thread localThread = new Thread(pluginThreadGroup, paramRunnable, paramString);
    localThread.setContextClassLoader(pluginSysClassLoader);
    return localThread;
  }

  protected Object delegate(DeploySysAction paramDeploySysAction)
    throws Exception
  {
    return execute(paramDeploySysAction);
  }

  public static Object execute(DeploySysAction paramDeploySysAction)
    throws Exception
  {
    return execute(paramDeploySysAction, getDefaultSetup(), getDefaultCleanup());
  }

  public static Object execute(DeploySysAction paramDeploySysAction, Runnable paramRunnable1, Runnable paramRunnable2)
    throws Exception
  {
    if (pluginThreadGroup == null)
    {
      Trace.println("ERROR:  pluginThreadGroup should not be null!");
      return null;
    }
    if (pluginThreadGroup.equals(Thread.currentThread().getThreadGroup()))
      return paramDeploySysAction.execute();
    SysExecutionThread localSysExecutionThread = sysExecutionThreadCreator.createThread(paramDeploySysAction, paramRunnable1, paramRunnable2);
    localSysExecutionThread.setContextClassLoader(pluginSysClassLoader);
    if (EventQueue.isDispatchThread())
    {
      synchronized (localSysExecutionThread.syncObject)
      {
        final DummyDialog localDummyDialog = new DummyDialog(null, true);
        localSysExecutionThread.theDummy = localDummyDialog;
        localDummyDialog.addWindowListener(new WindowAdapter()
        {
          private final PluginSysUtil.SysExecutionThread val$t;
          private final PluginSysUtil.DummyDialog val$dummy;

          public void windowOpened(WindowEvent paramAnonymousWindowEvent)
          {
            this.val$t.start();
          }

          public void windowClosing(WindowEvent paramAnonymousWindowEvent)
          {
            localDummyDialog.setVisible(false);
          }
        });
        Rectangle localRectangle = new Rectangle(new Point(0, 0), Toolkit.getDefaultToolkit().getScreenSize());
        if (!isOnWindows())
          localDummyDialog.setLocation(localRectangle.x + localRectangle.width / 2 - 50, localRectangle.y + localRectangle.height / 2);
        else
          localDummyDialog.setLocation(-100, -100);
        localDummyDialog.setResizable(false);
        localDummyDialog.toBack();
        localDummyDialog.setVisible(true);
        try
        {
          localSysExecutionThread.syncObject.wait();
        }
        catch (InterruptedException localInterruptedException2)
        {
        }
        finally
        {
          localDummyDialog.setVisible(false);
        }
      }
    }
    else
    {
      localSysExecutionThread.start();
      try
      {
        localSysExecutionThread.join();
      }
      catch (InterruptedException localInterruptedException1)
      {
      }
    }
    if (localSysExecutionThread.exception != null)
      throw localSysExecutionThread.exception;
    return localSysExecutionThread.result;
  }

  private static void createAppContext(ThreadGroup paramThreadGroup)
  {
    AppContextCreatorThread localAppContextCreatorThread = new AppContextCreatorThread(paramThreadGroup);
    synchronized (localAppContextCreatorThread.synObject)
    {
      localAppContextCreatorThread.start();
      try
      {
        localAppContextCreatorThread.synObject.wait();
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
  }

  private static boolean isOnWindows()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
    return str.indexOf("Windows") != -1;
  }

  private static Runnable getDefaultSetup()
  {
    Plugin2Manager localPlugin2Manager = Plugin2Manager.getCurrentManager();
    return new Runnable()
    {
      private final Plugin2Manager val$manager;

      public void run()
      {
        Plugin2Manager.setCurrentManagerThreadLocal(this.val$manager);
      }
    };
  }

  private static Runnable getDefaultCleanup()
  {
    return new Runnable()
    {
      public void run()
      {
        Plugin2Manager.setCurrentManagerThreadLocal(null);
      }
    };
  }

  static
  {
    sysExecutionThreadCreator.start();
  }

  private static class AppContextCreatorThread extends Thread
  {
    final Object synObject = new Object();

    public AppContextCreatorThread(ThreadGroup paramThreadGroup)
    {
      super("AppContext Creator Thread");
    }

    public void run()
    {
      synchronized (this.synObject)
      {
        ToolkitStore.get().createAppContext();
        this.synObject.notifyAll();
      }
    }
  }

  private static class DummyDialog extends JDialog
  {
    private ThreadGroup _unsecureGroup = Thread.currentThread().getThreadGroup();

    DummyDialog(Frame paramFrame, boolean paramBoolean)
    {
      super(paramBoolean);
    }

    public void secureHide()
    {
      new Thread(this._unsecureGroup, new Runnable()
      {
        public void run()
        {
          PluginSysUtil.DummyDialog.this.setVisible(false);
        }
      }).start();
    }
  }

  private static class Request
  {
    private DeploySysAction deploySysAction = null;
    private PluginSysUtil.SysExecutionThread sysExecutionThread = null;
    private Runnable setup;
    private Runnable cleanup;

    Request(DeploySysAction paramDeploySysAction, Runnable paramRunnable1, Runnable paramRunnable2)
    {
      this.deploySysAction = paramDeploySysAction;
      this.setup = paramRunnable1;
      this.cleanup = paramRunnable2;
    }

    void setResult(PluginSysUtil.SysExecutionThread paramSysExecutionThread)
    {
      this.sysExecutionThread = paramSysExecutionThread;
    }

    PluginSysUtil.SysExecutionThread getResult()
    {
      return this.sysExecutionThread;
    }
  }

  private static class SysExecutionThread extends Thread
  {
    Exception exception = null;
    Object result = null;
    final DeploySysAction action;
    final Runnable setup;
    final Runnable cleanup;
    final Object syncObject = new Object();
    PluginSysUtil.DummyDialog theDummy = null;

    public SysExecutionThread(DeploySysAction paramDeploySysAction, Runnable paramRunnable1, Runnable paramRunnable2)
    {
      super("SysExecutionThead");
      setDaemon(true);
      this.action = paramDeploySysAction;
      this.setup = paramRunnable1;
      this.cleanup = paramRunnable2;
    }

    public void run()
    {
      try
      {
        if (this.setup != null)
          this.setup.run();
        this.result = this.action.execute();
      }
      catch (Exception localException)
      {
        this.exception = localException;
      }
      finally
      {
        if (this.theDummy != null)
          this.theDummy.secureHide();
        synchronized (this.syncObject)
        {
          this.syncObject.notifyAll();
        }
        if (this.cleanup != null)
          this.cleanup.run();
      }
    }
  }

  private static class SysExecutionThreadCreator extends Thread
  {
    final List requestQueue = new LinkedList();

    private SysExecutionThreadCreator()
    {
      super();
      setDaemon(true);
    }

    private PluginSysUtil.SysExecutionThread createThread(DeploySysAction paramDeploySysAction, Runnable paramRunnable1, Runnable paramRunnable2)
    {
      PluginSysUtil.Request localRequest = new PluginSysUtil.Request(paramDeploySysAction, paramRunnable1, paramRunnable2);
      synchronized (this)
      {
        this.requestQueue.add(localRequest);
        notifyAll();
        while (localRequest.getResult() == null)
          try
          {
            wait(1000L);
          }
          catch (InterruptedException localInterruptedException)
          {
            Trace.ignoredException(localInterruptedException);
          }
        return localRequest.getResult();
      }
    }

    public void run()
    {
      while (true)
        synchronized (this)
        {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Trace.ignoredException(localInterruptedException);
          }
          if (!this.requestQueue.isEmpty())
          {
            PluginSysUtil.Request localRequest = (PluginSysUtil.Request)this.requestQueue.remove(0);
            localRequest.setResult(new PluginSysUtil.SysExecutionThread(localRequest.deploySysAction, localRequest.setup, localRequest.cleanup));
          }
          else
          {
            notifyAll();
          }
        }
    }

    SysExecutionThreadCreator(PluginSysUtil.1 param1)
    {
      this();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.PluginSysUtil
 * JD-Core Version:    0.6.2
 */