package com.sun.jmx.remote.internal;

import javax.management.remote.NotificationResult;

public abstract interface NotificationBuffer
{
  public abstract NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt)
    throws InterruptedException;

  public abstract void dispose();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.internal.NotificationBuffer
 * JD-Core Version:    0.6.2
 */