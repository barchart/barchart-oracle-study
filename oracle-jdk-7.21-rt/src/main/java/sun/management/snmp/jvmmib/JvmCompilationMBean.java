package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmCompilationMBean
{
  public abstract EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring()
    throws SnmpStatusException;

  public abstract Long getJvmJITCompilerTimeMs()
    throws SnmpStatusException;

  public abstract String getJvmJITCompilerName()
    throws SnmpStatusException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.snmp.jvmmib.JvmCompilationMBean
 * JD-Core Version:    0.6.2
 */