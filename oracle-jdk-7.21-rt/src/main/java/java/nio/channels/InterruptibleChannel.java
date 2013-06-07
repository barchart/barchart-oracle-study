package java.nio.channels;

import java.io.IOException;

public abstract interface InterruptibleChannel extends Channel
{
  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.InterruptibleChannel
 * JD-Core Version:    0.6.2
 */