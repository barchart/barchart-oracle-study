package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.Writeable;

public abstract interface IIOPAddress extends Writeable
{
  public abstract String getHost();

  public abstract int getPort();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.iiop.IIOPAddress
 * JD-Core Version:    0.6.2
 */