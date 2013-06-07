package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpPduPacket extends SnmpPdu
  implements Serializable
{
  public byte[] community;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.SnmpPduPacket
 * JD-Core Version:    0.6.2
 */