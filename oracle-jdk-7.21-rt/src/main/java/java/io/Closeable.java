package java.io;

public abstract interface Closeable extends AutoCloseable
{
  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.io.Closeable
 * JD-Core Version:    0.6.2
 */