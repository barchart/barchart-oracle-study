package java.security;

public abstract interface PrivilegedExceptionAction<T>
{
  public abstract T run()
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.PrivilegedExceptionAction
 * JD-Core Version:    0.6.2
 */