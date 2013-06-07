package java.security;

public abstract interface Guard
{
  public abstract void checkGuard(Object paramObject)
    throws SecurityException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.Guard
 * JD-Core Version:    0.6.2
 */