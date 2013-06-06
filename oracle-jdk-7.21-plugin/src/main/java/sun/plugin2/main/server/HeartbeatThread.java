package sun.plugin2.main.server;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.io.PrintStream;
import sun.plugin2.message.Conversation;
import sun.plugin2.message.HeartbeatMessage;
import sun.plugin2.message.Message;
import sun.plugin2.message.Pipe;
import sun.plugin2.util.SystemUtil;

public class HeartbeatThread extends Thread
{
  private static final boolean DEBUG = SystemUtil.isDebug();
  private static final boolean VERBOSE = SystemUtil.isVerbose();
  private static final long TIMEOUT_AFTER_SLEEP = 30000L;
  private static final int SLEEP_THRESHOLD_MULTIPLIER = 2;
  private final Pipe pipe;
  private final boolean clientSide;
  protected boolean alive = true;
  protected Conversation conversation;
  protected HeartbeatMessage beat;
  private long MICRO_COUNT = 1000000L;

  public HeartbeatThread(String paramString, Pipe paramPipe)
  {
    this(paramString, paramPipe, true);
    setDaemon(true);
  }

  public HeartbeatThread(String paramString, Pipe paramPipe, boolean paramBoolean)
  {
    super(paramString + "-Heartbeat");
    this.pipe = paramPipe;
    this.clientSide = paramBoolean;
  }

  protected boolean keepBeating()
  {
    return this.alive;
  }

  public void stopBeating()
  {
    this.alive = false;
    interrupt();
  }

  protected void handleStart()
    throws InterruptedException, IOException
  {
    if (!this.alive)
      throw new IllegalStateException("Cannot start already stopped heart");
    if (this.clientSide)
    {
      if (this.conversation == null)
      {
        Message localMessage = this.pipe.receive(10000L);
        if ((localMessage instanceof HeartbeatMessage))
        {
          this.beat = ((HeartbeatMessage)localMessage);
          this.conversation = this.beat.getConversation();
        }
      }
      if ((this.conversation == null) || (!this.pipe.joinConversation(this.conversation)))
        throw new InternalError("Client failed to join heartbeat conversation " + this.conversation);
      if (DEBUG)
        System.out.println(getName() + " joined conversation: " + this.conversation);
    }
    else
    {
      this.conversation = this.pipe.beginConversation();
      this.beat = (Pipe.isLoggingEnabled() ? new HeartbeatMessage(this.conversation, 5000L, 30000L) : new HeartbeatMessage(this.conversation));
    }
  }

  public void run()
  {
    long l1 = SystemUtils.microTime();
    try
    {
      handleStart();
      while (keepBeating())
      {
        l1 = SystemUtils.microTime();
        if (this.clientSide)
          this.beat.updateHealthData();
        this.pipe.send(this.beat);
        recordPingDiagnostics(l1);
        Message localMessage = this.pipe.receive(this.beat.getTimeout(), this.conversation);
        if (localMessage == null)
        {
          long l2 = (SystemUtils.microTime() - l1) / 1000L;
          if (l2 > this.beat.getTimeout() * 2L)
          {
            long l3 = 30000L;
            if (DEBUG)
              System.out.println(getName() + ": pipe read returns null elapsed=" + l2 + " millis while timeout=" + this.beat.getTimeout() + " Possible system sleep scenario, retry read.");
            localMessage = this.pipe.receive(l3, this.conversation);
          }
        }
        if (localMessage == null)
        {
          this.alive = false;
          recordNoAckDiagnostics(l1);
          handleNoAck();
          break;
        }
        recordAckDiagnostics(l1);
        handleAck();
        this.beat = ((HeartbeatMessage)localMessage);
        synchronized (this)
        {
          wait(this.beat.getInterval());
        }
      }
    }
    catch (Exception localException)
    {
      handleException(localException, l1);
    }
    finally
    {
      if (this.conversation != null)
        this.pipe.endConversation(this.conversation);
      handleStop();
    }
  }

  private long elapsedSecondsSince(long paramLong)
  {
    return (SystemUtils.microTime() - paramLong) / this.MICRO_COUNT;
  }

  protected void handleException(Exception paramException, long paramLong)
  {
    long l = elapsedSecondsSince(paramLong);
    String str = getName() + " heartbeat dead, exception. dT=" + l + " seconds.";
    if (DEBUG)
    {
      System.out.println(str);
      paramException.printStackTrace();
    }
  }

  protected void handleStop()
  {
  }

  protected void recordPingDiagnostics(long paramLong)
  {
    if (VERBOSE)
      System.out.println(getName() + " sent heartbeat: " + this.beat);
  }

  protected void recordAckDiagnostics(long paramLong)
  {
    if (VERBOSE)
    {
      long l = SystemUtils.microTime() - paramLong;
      System.out.println(getName() + " round-trip heartbeat took " + l + " microsecs.");
    }
  }

  protected void recordNoAckDiagnostics(long paramLong)
  {
    long l = elapsedSecondsSince(paramLong);
    if (DEBUG)
      System.out.println(getName() + " heartbeat dead, waited " + l + " seconds.");
  }

  protected void handleNoAck()
  {
  }

  protected void handleAck()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.HeartbeatThread
 * JD-Core Version:    0.6.2
 */