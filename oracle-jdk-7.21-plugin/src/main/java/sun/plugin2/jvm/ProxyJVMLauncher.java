package sun.plugin2.jvm;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.plugin2.main.server.AppletID;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.LaunchJVMAppletMessage;
import sun.plugin2.message.Pipe;

public class ProxyJVMLauncher
  implements ProcessLauncher, Runnable
{
  private static final boolean DEBUG = false;
  private static final long TIMEOUT = 60000L;
  private static final long INTERVAL = 200L;
  private final int appletID;
  private final Pipe pipe;
  private Conversation conversation;
  private LaunchJVMAppletMessage message;
  private long launchTime;
  private String javaHome;
  private List additionalArguments = new ArrayList();
  private boolean done = false;
  private int exitCode = 0;
  private PipedInputStream in;
  private PipedInputStream err;
  private Exception launchException;
  private boolean sendKill;
  private final ArrayList listeners = new ArrayList();

  public ProxyJVMLauncher(AppletID paramAppletID, Pipe paramPipe, long paramLong, String paramString)
  {
    this.appletID = paramAppletID.getID();
    this.pipe = paramPipe;
    this.launchTime = paramLong;
    this.javaHome = paramString;
    this.in = new PipedInputStream();
    this.err = new PipedInputStream();
  }

  public void start()
  {
    runThread();
    waitForRemoteProcessStarted(60000L);
  }

  protected Thread runThread()
  {
    Thread localThread = new Thread(this, toString());
    localThread.setDaemon(true);
    localThread.start();
    return localThread;
  }

  protected void waitForRemoteProcessStarted(long paramLong)
  {
    try
    {
      long l = 0L;
      while ((this.message == null) || (!this.message.isProcessStarted()))
        synchronized (this)
        {
          wait(200L);
          l += 200L;
          if (l > paramLong)
            throw new RuntimeException(this + ": exceeds wait time for remote process");
        }
    }
    catch (Exception localException)
    {
      this.launchException = localException;
    }
    finally
    {
    }
  }

  private String[] getAdditionalArgs()
  {
    String[] arrayOfString = new String[this.additionalArguments.size()];
    return (String[])this.additionalArguments.toArray(arrayOfString);
  }

  public void run()
  {
    try
    {
      this.conversation = this.pipe.beginConversation();
      this.message = new LaunchJVMAppletMessage(this.conversation, this.appletID, this.javaHome, this.launchTime, getAdditionalArgs());
      this.pipe.send(this.message);
      PipedOutputStream localPipedOutputStream1 = new PipedOutputStream(this.in);
      PipedOutputStream localPipedOutputStream2 = new PipedOutputStream(this.err);
      while (!this.done)
      {
        LaunchJVMAppletMessage localLaunchJVMAppletMessage = (LaunchJVMAppletMessage)this.pipe.receive(60000L, this.conversation);
        if (localLaunchJVMAppletMessage == null)
        {
          synchronized (this)
          {
            wait(200L);
          }
        }
        else
        {
          this.message = localLaunchJVMAppletMessage;
          if (this.message.getProcessInputBytes() != null)
          {
            localPipedOutputStream1.write(this.message.getProcessInputBytes());
            localPipedOutputStream1.flush();
          }
          if (this.message.getProcessErrorBytes() != null)
          {
            localPipedOutputStream2.write(this.message.getProcessErrorBytes());
            localPipedOutputStream2.flush();
          }
          this.done = this.message.isProcessExited();
          if (this.done)
          {
            System.out.println("ProxyJVMLauncher: remote process exited.");
            this.exitCode = this.message.getProcessExitCode();
            break;
          }
          if (this.sendKill)
          {
            System.out.println("ProxyJVMLauncher: sending kill " + this.message);
            this.message.setDoKill(true);
            this.sendKill = false;
            this.pipe.send(this.message);
          }
        }
      }
    }
    catch (Exception localException)
    {
      this.launchException = localException;
      System.out.println("ProxyJVMLauncher.run() got error ");
      localException.printStackTrace();
    }
    finally
    {
    }
  }

  public void launchCompleted()
  {
    this.done = true;
  }

  public void addParameter(String paramString)
  {
    this.additionalArguments.add(paramString);
  }

  public InputStream getInputStream()
  {
    return this.in;
  }

  public InputStream getErrorStream()
  {
    return this.err;
  }

  public boolean processStarted()
  {
    return this.message.isProcessStarted();
  }

  public boolean exited()
  {
    return this.done;
  }

  public int getExitCode()
  {
    return this.exitCode;
  }

  public long getJVMLaunchTime()
  {
    return this.launchTime;
  }

  public Exception getErrorDuringStartup()
  {
    return this.launchException;
  }

  public void addJVMEventListener(JVMEventListener paramJVMEventListener)
  {
    synchronized (this.listeners)
    {
      this.listeners.add(paramJVMEventListener);
    }
  }

  public void clearUserArguments()
  {
  }

  public void destroy()
  {
    this.sendKill = true;
  }

  private synchronized List copyListeners()
  {
    return (List)this.listeners.clone();
  }

  private void fireJVMExited()
  {
    Iterator localIterator = copyListeners().iterator();
    while (localIterator.hasNext())
    {
      JVMEventListener localJVMEventListener = (JVMEventListener)localIterator.next();
      localJVMEventListener.jvmExited(this);
    }
  }

  public String toString()
  {
    return "ProxyJVMLauncher(appletID=" + this.appletID + ")";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.ProxyJVMLauncher
 * JD-Core Version:    0.6.2
 */