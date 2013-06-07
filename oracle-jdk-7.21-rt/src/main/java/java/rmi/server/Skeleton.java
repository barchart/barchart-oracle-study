package java.rmi.server;

import java.rmi.Remote;

@Deprecated
public abstract interface Skeleton
{
  @Deprecated
  public abstract void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong)
    throws Exception;

  @Deprecated
  public abstract Operation[] getOperations();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.server.Skeleton
 * JD-Core Version:    0.6.2
 */