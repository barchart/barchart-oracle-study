package com.sun.corba.se.spi.monitoring;

public abstract interface MonitoredAttributeInfo
{
  public abstract boolean isWritable();

  public abstract boolean isStatistic();

  public abstract Class type();

  public abstract String getDescription();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo
 * JD-Core Version:    0.6.2
 */