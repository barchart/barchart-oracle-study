package java.security;

public abstract interface Principal
{
  public abstract boolean equals(Object paramObject);

  public abstract String toString();

  public abstract int hashCode();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.Principal
 * JD-Core Version:    0.6.2
 */