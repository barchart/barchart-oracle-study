package java.lang.management;

public abstract interface ClassLoadingMXBean extends PlatformManagedObject
{
  public abstract long getTotalLoadedClassCount();

  public abstract int getLoadedClassCount();

  public abstract long getUnloadedClassCount();

  public abstract boolean isVerbose();

  public abstract void setVerbose(boolean paramBoolean);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.ClassLoadingMXBean
 * JD-Core Version:    0.6.2
 */