package java.util;

public abstract interface Queue<E> extends Collection<E>
{
  public abstract boolean add(E paramE);

  public abstract boolean offer(E paramE);

  public abstract E remove();

  public abstract E poll();

  public abstract E element();

  public abstract E peek();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.Queue
 * JD-Core Version:    0.6.2
 */