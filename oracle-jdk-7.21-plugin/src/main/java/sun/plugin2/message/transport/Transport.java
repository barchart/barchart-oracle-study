package sun.plugin2.message.transport;

import java.io.IOException;
import sun.plugin2.message.Message;

public abstract interface Transport
{
  public abstract void write(Message paramMessage)
    throws IOException;

  public abstract Message read()
    throws IOException;

  public abstract void waitForData(long paramLong)
    throws IOException;

  public abstract String toString();
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.transport.Transport
 * JD-Core Version:    0.6.2
 */