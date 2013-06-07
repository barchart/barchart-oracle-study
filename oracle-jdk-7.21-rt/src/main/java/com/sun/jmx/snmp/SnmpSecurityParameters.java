package com.sun.jmx.snmp;

public abstract interface SnmpSecurityParameters
{
  public abstract int encode(byte[] paramArrayOfByte)
    throws SnmpTooBigException;

  public abstract void decode(byte[] paramArrayOfByte)
    throws SnmpStatusException;

  public abstract String getPrincipal();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpSecurityParameters
 * JD-Core Version:    0.6.2
 */