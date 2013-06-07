package sun.misc;

public abstract interface JavaAWTAccess
{
  public abstract Object getContext();

  public abstract Object getExecutionContext();

  public abstract Object get(Object paramObject1, Object paramObject2);

  public abstract void put(Object paramObject1, Object paramObject2, Object paramObject3);

  public abstract void remove(Object paramObject1, Object paramObject2);

  public abstract Object get(Object paramObject);

  public abstract void put(Object paramObject1, Object paramObject2);

  public abstract void remove(Object paramObject);

  public abstract boolean isDisposed();

  public abstract boolean isMainAppContext();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.JavaAWTAccess
 * JD-Core Version:    0.6.2
 */