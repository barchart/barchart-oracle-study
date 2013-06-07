package sun.awt.datatransfer;

public abstract interface ToolkitThreadBlockedHandler
{
  public abstract void lock();

  public abstract void unlock();

  public abstract void enter();

  public abstract void exit();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.datatransfer.ToolkitThreadBlockedHandler
 * JD-Core Version:    0.6.2
 */