package javax.security.auth;

public abstract interface Refreshable
{
  public abstract boolean isCurrent();

  public abstract void refresh()
    throws RefreshFailedException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.Refreshable
 * JD-Core Version:    0.6.2
 */