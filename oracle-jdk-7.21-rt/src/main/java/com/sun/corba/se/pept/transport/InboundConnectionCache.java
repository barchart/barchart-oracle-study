package com.sun.corba.se.pept.transport;

public abstract interface InboundConnectionCache extends ConnectionCache
{
  public abstract Connection get(Acceptor paramAcceptor);

  public abstract void put(Acceptor paramAcceptor, Connection paramConnection);

  public abstract void remove(Connection paramConnection);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.transport.InboundConnectionCache
 * JD-Core Version:    0.6.2
 */