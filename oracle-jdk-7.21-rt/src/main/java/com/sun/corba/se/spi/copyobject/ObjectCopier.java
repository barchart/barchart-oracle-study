package com.sun.corba.se.spi.copyobject;

public abstract interface ObjectCopier
{
  public abstract Object copy(Object paramObject)
    throws ReflectiveCopyException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.copyobject.ObjectCopier
 * JD-Core Version:    0.6.2
 */