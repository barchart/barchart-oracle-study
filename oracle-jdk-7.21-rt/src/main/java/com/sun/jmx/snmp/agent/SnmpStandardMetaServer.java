package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public abstract interface SnmpStandardMetaServer
{
  public abstract SnmpValue get(long paramLong, Object paramObject)
    throws SnmpStatusException;

  public abstract SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;

  public abstract void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.agent.SnmpStandardMetaServer
 * JD-Core Version:    0.6.2
 */