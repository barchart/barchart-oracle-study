package java.lang.management;

public abstract interface CompilationMXBean extends PlatformManagedObject
{
  public abstract String getName();

  public abstract boolean isCompilationTimeMonitoringSupported();

  public abstract long getTotalCompilationTime();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.CompilationMXBean
 * JD-Core Version:    0.6.2
 */