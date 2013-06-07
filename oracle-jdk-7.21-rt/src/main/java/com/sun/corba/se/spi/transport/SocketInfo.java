package com.sun.corba.se.spi.transport;

public abstract interface SocketInfo
{
  public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";

  public abstract String getType();

  public abstract String getHost();

  public abstract int getPort();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.SocketInfo
 * JD-Core Version:    0.6.2
 */