package java.util.concurrent;

public abstract interface Callable<V>
{
  public abstract V call()
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.Callable
 * JD-Core Version:    0.6.2
 */