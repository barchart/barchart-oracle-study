package sun.plugin2.jvm;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.JVMParameters;
import java.io.InputStream;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.LaunchJVMAppletMessage;
import sun.plugin2.message.Pipe;

public class RemoteJVMLauncher extends JVMLauncher
  implements Runnable
{
  static final boolean DEBUG = false;
  private static final long INTERVAL = 1000L;
  private final Pipe pipe;
  private final Conversation conversation;
  private LaunchJVMAppletMessage message;
  private boolean done;
  private CircularByteBuffer.Streamer inStreamer;
  private CircularByteBuffer.Streamer errStreamer;
  private CallBack callBack;

  public RemoteJVMLauncher(Pipe paramPipe, LaunchJVMAppletMessage paramLaunchJVMAppletMessage, JVMParameters paramJVMParameters, Plugin2Manager paramPlugin2Manager)
  {
    super(paramLaunchJVMAppletMessage.getLaunchTime(), paramLaunchJVMAppletMessage.getJavaHome(), paramJVMParameters);
    this.pipe = paramPipe;
    this.conversation = paramLaunchJVMAppletMessage.getConversation();
    this.message = paramLaunchJVMAppletMessage;
  }

  protected boolean isRemote()
  {
    return true;
  }

  public void start()
  {
    Trace.println("RemoteJVMLauncher.start(), pipe=" + this.pipe.toString() + " message: " + this.message, TraceLevel.BASIC);
    runThread();
    String[] arrayOfString = this.message.getAdditionalArgs();
    for (int i = 0; i < arrayOfString.length; i++)
      addParameter(arrayOfString[i]);
    super.start();
  }

  public void setCallBack(CallBack paramCallBack)
  {
    this.callBack = paramCallBack;
  }

  protected void runThread()
  {
    Thread localThread = new Thread(this, toString());
    localThread.setDaemon(true);
    localThread.start();
  }

  public void afterStart()
  {
    initInErrStreamers();
  }

  private synchronized void initInErrStreamers()
  {
    if (this.inStreamer != null)
      return;
    if (getInputStream() != null)
    {
      Trace.println("RemoteJVMLauncher.afterStart(): initializing streamers", TraceLevel.BASIC);
      this.inStreamer = startStreamToBuffer(getInputStream(), "RemoteJVMLauncher-inputStream");
      this.errStreamer = startStreamToBuffer(getErrorStream(), "RemoteJVMLauncher-errorStream");
    }
  }

  public void run()
  {
    Trace.println(Thread.currentThread() + " running...", TraceLevel.BASIC);
    if (!this.pipe.joinConversation(this.conversation))
      throw new RuntimeException("Failed to join LaunchJVM conversation on " + this.pipe);
    try
    {
      this.message.setProcessStarted(true);
      int i = 0;
      while ((!this.done) && (!exited()))
      {
        initInErrStreamers();
        if ((this.inStreamer != null) && (this.errStreamer != null))
        {
          this.message.setProcessInputBytes(this.inStreamer.readAvailable());
          this.message.setProcessErrorBytes(this.errStreamer.readAvailable());
          if ((this.callBack != null) && (i == 0))
          {
            i = 1;
            this.callBack.jvmStarted();
          }
        }
        else
        {
          localObject1 = "Waiting for process spawning...";
          this.message.setProcessInputBytes(((String)localObject1).getBytes());
          this.message.setProcessErrorBytes(null);
        }
        this.pipe.send(this.message);
        Object localObject1 = (LaunchJVMAppletMessage)this.pipe.receive(1000L, this.conversation);
        if (localObject1 != null)
        {
          this.message = ((LaunchJVMAppletMessage)localObject1);
          if ((this.message != null) && (this.message.isDoKill()))
          {
            destroy();
            this.done = true;
          }
        }
      }
      this.message.setProcessExitCode(0);
    }
    catch (Exception localException1)
    {
      Trace.ignored(localException1);
      this.message.setProcessInputBytes(localException1.getMessage().getBytes());
      this.message.setProcessErrorBytes(localException1.getMessage().getBytes());
      this.message.setProcessExitCode(-1);
    }
    finally
    {
      this.message.setProcessExited(true);
      Trace.println(Thread.currentThread() + " completing: done=" + this.done + " exited=" + exited(), TraceLevel.BASIC);
      Trace.println("Sending final message " + this.message.toClientString(), TraceLevel.BASIC);
      try
      {
        this.pipe.send(this.message);
      }
      catch (Exception localException2)
      {
        Trace.println("Exception sending final message", TraceLevel.BASIC);
        Trace.ignored(localException2);
      }
    }
  }

  static CircularByteBuffer.Streamer startStreamToBuffer(InputStream paramInputStream, String paramString)
  {
    CircularByteBuffer.Streamer localStreamer = new CircularByteBuffer.Streamer(paramInputStream);
    Thread localThread = new Thread(localStreamer, paramString);
    localThread.setDaemon(true);
    localThread.start();
    return localStreamer;
  }

  public String toString()
  {
    String str = "";
    if (this.message != null)
      str = str + this.message.getAppletID();
    return "RemoteJVMLauncher-" + str;
  }

  public static abstract interface CallBack
  {
    public abstract void jvmStarted();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.jvm.RemoteJVMLauncher
 * JD-Core Version:    0.6.2
 */