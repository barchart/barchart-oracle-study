package sun.rmi.transport;

import java.rmi.RemoteException;

public abstract interface Channel
{
  public abstract Connection newConnection()
    throws RemoteException;

  public abstract Endpoint getEndpoint();

  public abstract void free(Connection paramConnection, boolean paramBoolean)
    throws RemoteException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.Channel
 * JD-Core Version:    0.6.2
 */