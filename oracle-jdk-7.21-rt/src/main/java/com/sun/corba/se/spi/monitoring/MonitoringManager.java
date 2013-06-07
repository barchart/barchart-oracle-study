package com.sun.corba.se.spi.monitoring;

import java.io.Closeable;

public abstract interface MonitoringManager extends Closeable
{
  public abstract MonitoredObject getRootMonitoredObject();

  public abstract void clearState();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.monitoring.MonitoringManager
 * JD-Core Version:    0.6.2
 */