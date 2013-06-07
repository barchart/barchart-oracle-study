package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public abstract interface ObjectAdapterFactory
{
  public abstract void init(ORB paramORB);

  public abstract void shutdown(boolean paramBoolean);

  public abstract ObjectAdapter find(ObjectAdapterId paramObjectAdapterId);

  public abstract ORB getORB();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.oa.ObjectAdapterFactory
 * JD-Core Version:    0.6.2
 */