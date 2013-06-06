package sun.plugin2.ipc;

import java.util.Map;

public abstract class Event
{
  public abstract void waitForSignal(long paramLong);

  public abstract void signal();

  public abstract Map getChildProcessParameters();

  public abstract void dispose();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.ipc.Event
 * JD-Core Version:    0.6.2
 */