package com.sun.jmx.snmp;

public abstract interface SnmpPduFactory
{
  public abstract SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg)
    throws SnmpStatusException;

  public abstract SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpPduFactory
 * JD-Core Version:    0.6.2
 */