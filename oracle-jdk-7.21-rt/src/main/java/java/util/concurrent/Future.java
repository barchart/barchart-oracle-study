package java.util.concurrent;

public abstract interface Future<V>
{
  public abstract boolean cancel(boolean paramBoolean);

  public abstract boolean isCancelled();

  public abstract boolean isDone();

  public abstract V get()
    throws InterruptedException, ExecutionException;

  public abstract V get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.Future
 * JD-Core Version:    0.6.2
 */