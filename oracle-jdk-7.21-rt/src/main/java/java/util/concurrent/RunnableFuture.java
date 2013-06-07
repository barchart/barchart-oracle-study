package java.util.concurrent;

public abstract interface RunnableFuture<V> extends Runnable, Future<V>
{
  public abstract void run();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.RunnableFuture
 * JD-Core Version:    0.6.2
 */