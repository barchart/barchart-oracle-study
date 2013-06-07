package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;

public abstract interface LocateRequestMessage extends Message
{
  public abstract int getRequestId();

  public abstract ObjectKey getObjectKey();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage
 * JD-Core Version:    0.6.2
 */