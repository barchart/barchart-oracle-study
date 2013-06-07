package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTLibraryPathEntryMBean
{
  public abstract String getJvmRTLibraryPathItem()
    throws SnmpStatusException;

  public abstract Integer getJvmRTLibraryPathIndex()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmRTLibraryPathEntryMBean
 * JD-Core Version:    0.6.2
 */