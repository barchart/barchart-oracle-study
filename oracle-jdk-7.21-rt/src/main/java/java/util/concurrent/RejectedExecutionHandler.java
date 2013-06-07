package java.util.concurrent;

public abstract interface RejectedExecutionHandler
{
  public abstract void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.RejectedExecutionHandler
 * JD-Core Version:    0.6.2
 */