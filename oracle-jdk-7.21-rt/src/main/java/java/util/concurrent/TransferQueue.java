package java.util.concurrent;

public abstract interface TransferQueue<E> extends BlockingQueue<E>
{
  public abstract boolean tryTransfer(E paramE);

  public abstract void transfer(E paramE)
    throws InterruptedException;

  public abstract boolean tryTransfer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;

  public abstract boolean hasWaitingConsumer();

  public abstract int getWaitingConsumerCount();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.TransferQueue
 * JD-Core Version:    0.6.2
 */