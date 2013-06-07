package java.net;

public abstract interface SocketOption<T>
{
  public abstract String name();

  public abstract Class<T> type();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocketOption
 * JD-Core Version:    0.6.2
 */