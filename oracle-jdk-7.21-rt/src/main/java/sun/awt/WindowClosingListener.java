package sun.awt;

import java.awt.event.WindowEvent;

public abstract interface WindowClosingListener
{
  public abstract RuntimeException windowClosingNotify(WindowEvent paramWindowEvent);

  public abstract RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.WindowClosingListener
 * JD-Core Version:    0.6.2
 */