package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;

public abstract interface IORFactory extends Writeable, MakeImmutable
{
  public abstract IOR makeIOR(ORB paramORB, String paramString, ObjectId paramObjectId);

  public abstract boolean isEquivalent(IORFactory paramIORFactory);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.IORFactory
 * JD-Core Version:    0.6.2
 */