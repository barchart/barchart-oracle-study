package java.lang.management;

public abstract interface BufferPoolMXBean extends PlatformManagedObject
{
  public abstract String getName();

  public abstract long getCount();

  public abstract long getTotalCapacity();

  public abstract long getMemoryUsed();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.BufferPoolMXBean
 * JD-Core Version:    0.6.2
 */