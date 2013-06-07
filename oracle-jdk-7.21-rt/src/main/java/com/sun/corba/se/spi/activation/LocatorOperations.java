package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;

public abstract interface LocatorOperations
{
  public abstract ServerLocation locateServer(int paramInt, String paramString)
    throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown;

  public abstract ServerLocationPerORB locateServerForORB(int paramInt, String paramString)
    throws InvalidORBid, ServerNotRegistered, ServerHeldDown;

  public abstract int getEndpoint(String paramString)
    throws NoSuchEndPoint;

  public abstract int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString)
    throws NoSuchEndPoint;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.LocatorOperations
 * JD-Core Version:    0.6.2
 */