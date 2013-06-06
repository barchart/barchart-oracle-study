package sun.plugin2.ipc;

import java.util.Map;

public class InProcEvent extends Event
{
  private volatile boolean signalled;

  public synchronized void waitForSignal(long paramLong)
  {
    if (this.signalled)
    {
      this.signalled = false;
      return;
    }
    try
    {
      wait(paramLong);
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
    this.signalled = false;
  }

  public synchronized void signal()
  {
    this.signalled = true;
    notifyAll();
  }

  public Map getChildProcessParameters()
  {
    throw new RuntimeException("Should not call this");
  }

  public void dispose()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.InProcEvent
 * JD-Core Version:    0.6.2
 */