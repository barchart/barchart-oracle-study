package com.sun.jmx.snmp;

import java.util.Vector;

public abstract interface SnmpOidTable
{
  public abstract SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException;

  public abstract SnmpOidRecord resolveVarOid(String paramString)
    throws SnmpStatusException;

  public abstract Vector<?> getAllEntries();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpOidTable
 * JD-Core Version:    0.6.2
 */