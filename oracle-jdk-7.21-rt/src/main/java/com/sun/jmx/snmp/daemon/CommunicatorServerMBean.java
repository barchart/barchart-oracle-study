package com.sun.jmx.snmp.daemon;

public abstract interface CommunicatorServerMBean
{
  public abstract void start();

  public abstract void stop();

  public abstract boolean isActive();

  public abstract boolean waitState(int paramInt, long paramLong);

  public abstract int getState();

  public abstract String getStateString();

  public abstract String getHost();

  public abstract int getPort();

  public abstract void setPort(int paramInt)
    throws IllegalStateException;

  public abstract String getProtocol();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.daemon.CommunicatorServerMBean
 * JD-Core Version:    0.6.2
 */