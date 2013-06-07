package javax.security.auth;

public abstract interface Destroyable
{
  public abstract void destroy()
    throws DestroyFailedException;

  public abstract boolean isDestroyed();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.Destroyable
 * JD-Core Version:    0.6.2
 */