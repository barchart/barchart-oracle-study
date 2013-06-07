package com.sun.jmx.snmp;

public abstract interface SnmpEngineFactory
{
  public abstract SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters);

  public abstract SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters, InetAddressAcl paramInetAddressAcl);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpEngineFactory
 * JD-Core Version:    0.6.2
 */