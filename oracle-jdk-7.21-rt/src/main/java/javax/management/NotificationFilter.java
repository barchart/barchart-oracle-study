package javax.management;

import java.io.Serializable;

public abstract interface NotificationFilter extends Serializable
{
  public abstract boolean isNotificationEnabled(Notification paramNotification);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.NotificationFilter
 * JD-Core Version:    0.6.2
 */