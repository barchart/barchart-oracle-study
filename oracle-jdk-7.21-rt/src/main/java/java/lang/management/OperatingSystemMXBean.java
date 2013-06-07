package java.lang.management;

public abstract interface OperatingSystemMXBean extends PlatformManagedObject
{
  public abstract String getName();

  public abstract String getArch();

  public abstract String getVersion();

  public abstract int getAvailableProcessors();

  public abstract double getSystemLoadAverage();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.OperatingSystemMXBean
 * JD-Core Version:    0.6.2
 */