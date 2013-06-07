package com.sun.corba.se.spi.transport;

import java.net.ServerSocket;

public abstract interface SocketOrChannelAcceptor
{
  public abstract ServerSocket getServerSocket();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.SocketOrChannelAcceptor
 * JD-Core Version:    0.6.2
 */