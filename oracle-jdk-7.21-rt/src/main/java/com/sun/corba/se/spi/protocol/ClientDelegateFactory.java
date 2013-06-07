package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.transport.CorbaContactInfoList;

public abstract interface ClientDelegateFactory
{
  public abstract CorbaClientDelegate create(CorbaContactInfoList paramCorbaContactInfoList);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.protocol.ClientDelegateFactory
 * JD-Core Version:    0.6.2
 */