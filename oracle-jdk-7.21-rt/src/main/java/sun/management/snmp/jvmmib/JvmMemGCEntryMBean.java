package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemGCEntryMBean
{
  public abstract Long getJvmMemGCTimeMs()
    throws SnmpStatusException;

  public abstract Long getJvmMemGCCount()
    throws SnmpStatusException;

  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmMemGCEntryMBean
 * JD-Core Version:    0.6.2
 */