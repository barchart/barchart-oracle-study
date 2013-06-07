package java.util.concurrent;

import java.util.Collection;
import java.util.List;

public abstract interface ExecutorService extends Executor
{
  public abstract void shutdown();

  public abstract List<Runnable> shutdownNow();

  public abstract boolean isShutdown();

  public abstract boolean isTerminated();

  public abstract boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;

  public abstract <T> Future<T> submit(Callable<T> paramCallable);

  public abstract <T> Future<T> submit(Runnable paramRunnable, T paramT);

  public abstract Future<?> submit(Runnable paramRunnable);

  public abstract <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection)
    throws InterruptedException;

  public abstract <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;

  public abstract <T> T invokeAny(Collection<? extends Callable<T>> paramCollection)
    throws InterruptedException, ExecutionException;

  public abstract <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.ExecutorService
 * JD-Core Version:    0.6.2
 */