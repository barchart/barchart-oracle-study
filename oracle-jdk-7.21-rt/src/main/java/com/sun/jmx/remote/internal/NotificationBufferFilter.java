package com.sun.jmx.remote.internal;

import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;

public abstract interface NotificationBufferFilter
{
  public abstract void apply(List<TargetedNotification> paramList, ObjectName paramObjectName, Notification paramNotification);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.internal.NotificationBufferFilter
 * JD-Core Version:    0.6.2
 */