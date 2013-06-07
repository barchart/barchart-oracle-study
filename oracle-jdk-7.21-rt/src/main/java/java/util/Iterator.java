package java.util;

public abstract interface Iterator<E>
{
  public abstract boolean hasNext();

  public abstract E next();

  public abstract void remove();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.Iterator
 * JD-Core Version:    0.6.2
 */