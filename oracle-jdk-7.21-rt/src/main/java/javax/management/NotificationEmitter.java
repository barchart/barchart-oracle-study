package javax.management;

public abstract interface NotificationEmitter extends NotificationBroadcaster
{
  public abstract void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.NotificationEmitter
 * JD-Core Version:    0.6.2
 */