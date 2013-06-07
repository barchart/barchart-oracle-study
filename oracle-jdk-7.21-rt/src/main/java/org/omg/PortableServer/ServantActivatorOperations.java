package org.omg.PortableServer;

public abstract interface ServantActivatorOperations extends ServantManagerOperations
{
  public abstract Servant incarnate(byte[] paramArrayOfByte, POA paramPOA)
    throws ForwardRequest;

  public abstract void etherealize(byte[] paramArrayOfByte, POA paramPOA, Servant paramServant, boolean paramBoolean1, boolean paramBoolean2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.ServantActivatorOperations
 * JD-Core Version:    0.6.2
 */