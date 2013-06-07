package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent;

public abstract interface AlternateIIOPAddressComponent extends TaggedComponent
{
  public abstract IIOPAddress getAddress();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent
 * JD-Core Version:    0.6.2
 */