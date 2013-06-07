package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface IdentifiableFactory
{
  public abstract int getId();

  public abstract Identifiable create(InputStream paramInputStream);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.IdentifiableFactory
 * JD-Core Version:    0.6.2
 */