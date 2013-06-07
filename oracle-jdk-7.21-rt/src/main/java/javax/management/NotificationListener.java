package javax.management;

import java.util.EventListener;

public abstract interface NotificationListener extends EventListener
{
  public abstract void handleNotification(Notification paramNotification, Object paramObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.NotificationListener
 * JD-Core Version:    0.6.2
 */