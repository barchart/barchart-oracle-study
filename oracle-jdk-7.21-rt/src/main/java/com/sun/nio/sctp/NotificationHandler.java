package com.sun.nio.sctp;

public abstract interface NotificationHandler<T>
{
  public abstract HandlerResult handleNotification(Notification paramNotification, T paramT);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.nio.sctp.NotificationHandler
 * JD-Core Version:    0.6.2
 */