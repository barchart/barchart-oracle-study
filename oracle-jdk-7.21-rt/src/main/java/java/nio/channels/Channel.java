package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

public abstract interface Channel extends Closeable
{
  public abstract boolean isOpen();

  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.Channel
 * JD-Core Version:    0.6.2
 */