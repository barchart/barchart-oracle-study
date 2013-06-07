package java.lang.management;

public abstract interface GarbageCollectorMXBean extends MemoryManagerMXBean
{
  public abstract long getCollectionCount();

  public abstract long getCollectionTime();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.GarbageCollectorMXBean
 * JD-Core Version:    0.6.2
 */