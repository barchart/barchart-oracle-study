package com.sun.corba.se.pept.transport;

public abstract interface ListenerThread
{
  public abstract Acceptor getAcceptor();

  public abstract void close();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.transport.ListenerThread
 * JD-Core Version:    0.6.2
 */