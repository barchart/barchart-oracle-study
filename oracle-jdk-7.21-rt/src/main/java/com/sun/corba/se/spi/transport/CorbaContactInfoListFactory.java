package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public abstract interface CorbaContactInfoListFactory
{
  public abstract void setORB(ORB paramORB);

  public abstract CorbaContactInfoList create(IOR paramIOR);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.CorbaContactInfoListFactory
 * JD-Core Version:    0.6.2
 */