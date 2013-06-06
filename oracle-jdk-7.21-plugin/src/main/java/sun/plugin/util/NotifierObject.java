package sun.plugin.util;

public class NotifierObject
{
  private volatile boolean notified = false;

  public void setNotified()
  {
    this.notified = true;
  }

  public boolean getNotified()
  {
    return this.notified;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.NotifierObject
 * JD-Core Version:    0.6.2
 */