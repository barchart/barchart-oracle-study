package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;

public abstract interface SnmpTableHandler
{
  public abstract Object getData(SnmpOid paramSnmpOid);

  public abstract SnmpOid getNext(SnmpOid paramSnmpOid);

  public abstract boolean contains(SnmpOid paramSnmpOid);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.util.SnmpTableHandler
 * JD-Core Version:    0.6.2
 */