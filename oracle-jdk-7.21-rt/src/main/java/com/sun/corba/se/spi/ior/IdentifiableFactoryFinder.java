package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface IdentifiableFactoryFinder
{
  public abstract Identifiable create(int paramInt, InputStream paramInputStream);

  public abstract void registerFactory(IdentifiableFactory paramIdentifiableFactory);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.IdentifiableFactoryFinder
 * JD-Core Version:    0.6.2
 */