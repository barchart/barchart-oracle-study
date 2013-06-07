package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;

public abstract interface TaggedComponent extends Identifiable
{
  public abstract org.omg.IOP.TaggedComponent getIOPComponent(ORB paramORB);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.TaggedComponent
 * JD-Core Version:    0.6.2
 */