package com.sun.corba.se.impl.protocol.giopmsgheaders;

public abstract interface FragmentMessage extends Message
{
  public abstract int getRequestId();

  public abstract int getHeaderLength();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage
 * JD-Core Version:    0.6.2
 */