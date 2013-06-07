package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTInputArgsEntryMBean
{
  public abstract String getJvmRTInputArgsItem()
    throws SnmpStatusException;

  public abstract Integer getJvmRTInputArgsIndex()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmRTInputArgsEntryMBean
 * JD-Core Version:    0.6.2
 */