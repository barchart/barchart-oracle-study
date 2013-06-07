package java.util.concurrent;

public abstract interface CompletionService<V>
{
  public abstract Future<V> submit(Callable<V> paramCallable);

  public abstract Future<V> submit(Runnable paramRunnable, V paramV);

  public abstract Future<V> take()
    throws InterruptedException;

  public abstract Future<V> poll();

  public abstract Future<V> poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.CompletionService
 * JD-Core Version:    0.6.2
 */