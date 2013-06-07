package sun.rmi.transport;

import java.rmi.RemoteException;

public abstract interface Endpoint
{
  public abstract Channel getChannel();

  public abstract void exportObject(Target paramTarget)
    throws RemoteException;

  public abstract Transport getInboundTransport();

  public abstract Transport getOutboundTransport();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.rmi.transport.Endpoint
 * JD-Core Version:    0.6.2
 */