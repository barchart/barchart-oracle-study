package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTClassPathEntryMBean
{
  public abstract String getJvmRTClassPathItem()
    throws SnmpStatusException;

  public abstract Integer getJvmRTClassPathIndex()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean
 * JD-Core Version:    0.6.2
 */