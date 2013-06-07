package java.util.concurrent.locks;

public abstract interface ReadWriteLock
{
  public abstract Lock readLock();

  public abstract Lock writeLock();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.locks.ReadWriteLock
 * JD-Core Version:    0.6.2
 */