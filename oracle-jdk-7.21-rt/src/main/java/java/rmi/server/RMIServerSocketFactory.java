package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;

public abstract interface RMIServerSocketFactory
{
  public abstract ServerSocket createServerSocket(int paramInt)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.server.RMIServerSocketFactory
 * JD-Core Version:    0.6.2
 */