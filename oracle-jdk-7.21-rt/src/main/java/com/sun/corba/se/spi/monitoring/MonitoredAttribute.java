package com.sun.corba.se.spi.monitoring;

public abstract interface MonitoredAttribute
{
  public abstract MonitoredAttributeInfo getAttributeInfo();

  public abstract void setValue(Object paramObject);

  public abstract Object getValue();

  public abstract String getName();

  public abstract void clearState();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.monitoring.MonitoredAttribute
 * JD-Core Version:    0.6.2
 */