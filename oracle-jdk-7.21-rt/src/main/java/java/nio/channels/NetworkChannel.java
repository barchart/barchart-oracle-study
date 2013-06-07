package java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.util.Set;

public abstract interface NetworkChannel extends Channel
{
  public abstract NetworkChannel bind(SocketAddress paramSocketAddress)
    throws IOException;

  public abstract SocketAddress getLocalAddress()
    throws IOException;

  public abstract <T> NetworkChannel setOption(SocketOption<T> paramSocketOption, T paramT)
    throws IOException;

  public abstract <T> T getOption(SocketOption<T> paramSocketOption)
    throws IOException;

  public abstract Set<SocketOption<?>> supportedOptions();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.channels.NetworkChannel
 * JD-Core Version:    0.6.2
 */