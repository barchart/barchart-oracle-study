package java.rmi.server;

import java.io.IOException;
import java.net.Socket;

public abstract interface RMIClientSocketFactory
{
  public abstract Socket createSocket(String paramString, int paramInt)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.server.RMIClientSocketFactory
 * JD-Core Version:    0.6.2
 */