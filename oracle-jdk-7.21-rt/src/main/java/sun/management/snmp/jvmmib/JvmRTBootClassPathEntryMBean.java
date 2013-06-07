package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTBootClassPathEntryMBean
{
  public abstract String getJvmRTBootClassPathItem()
    throws SnmpStatusException;

  public abstract Integer getJvmRTBootClassPathIndex()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmRTBootClassPathEntryMBean
 * JD-Core Version:    0.6.2
 */