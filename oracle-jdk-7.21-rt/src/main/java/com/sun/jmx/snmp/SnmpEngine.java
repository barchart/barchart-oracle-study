package com.sun.jmx.snmp;

public abstract interface SnmpEngine
{
  public abstract int getEngineTime();

  public abstract SnmpEngineId getEngineId();

  public abstract int getEngineBoots();

  public abstract SnmpUsmKeyHandler getUsmKeyHandler();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpEngine
 * JD-Core Version:    0.6.2
 */