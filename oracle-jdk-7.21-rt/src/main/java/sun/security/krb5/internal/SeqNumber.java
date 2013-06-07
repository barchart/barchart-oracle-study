package sun.security.krb5.internal;

public abstract interface SeqNumber
{
  public abstract void randInit();

  public abstract void init(int paramInt);

  public abstract int current();

  public abstract int next();

  public abstract int step();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.krb5.internal.SeqNumber
 * JD-Core Version:    0.6.2
 */