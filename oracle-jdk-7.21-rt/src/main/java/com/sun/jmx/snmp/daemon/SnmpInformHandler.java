package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpVarBindList;

public abstract interface SnmpInformHandler extends SnmpDefinitions
{
  public abstract void processSnmpPollData(SnmpInformRequest paramSnmpInformRequest, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList);

  public abstract void processSnmpPollTimeout(SnmpInformRequest paramSnmpInformRequest);

  public abstract void processSnmpInternalError(SnmpInformRequest paramSnmpInformRequest, String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.snmp.daemon.SnmpInformHandler
 * JD-Core Version:    0.6.2
 */