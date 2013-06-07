package javax.naming;

import java.util.Enumeration;

public abstract interface NamingEnumeration<T> extends Enumeration<T>
{
  public abstract T next()
    throws NamingException;

  public abstract boolean hasMore()
    throws NamingException;

  public abstract void close()
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.NamingEnumeration
 * JD-Core Version:    0.6.2
 */