package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public abstract interface TOA extends ObjectAdapter
{
  public abstract void connect(org.omg.CORBA.Object paramObject);

  public abstract void disconnect(org.omg.CORBA.Object paramObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.oa.toa.TOA
 * JD-Core Version:    0.6.2
 */