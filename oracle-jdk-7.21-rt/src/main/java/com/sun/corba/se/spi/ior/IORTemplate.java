package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;

public abstract interface IORTemplate extends List, IORFactory, MakeImmutable
{
  public abstract Iterator iteratorById(int paramInt);

  public abstract ObjectKeyTemplate getObjectKeyTemplate();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.IORTemplate
 * JD-Core Version:    0.6.2
 */