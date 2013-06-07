package sun.nio.ch;

abstract interface Cancellable
{
  public abstract void onCancel(PendingFuture<?, ?> paramPendingFuture);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.Cancellable
 * JD-Core Version:    0.6.2
 */