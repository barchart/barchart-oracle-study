package sun.rmi.server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;

public abstract interface Dispatcher
{
  public abstract void dispatch(Remote paramRemote, RemoteCall paramRemoteCall)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.server.Dispatcher
 * JD-Core Version:    0.6.2
 */