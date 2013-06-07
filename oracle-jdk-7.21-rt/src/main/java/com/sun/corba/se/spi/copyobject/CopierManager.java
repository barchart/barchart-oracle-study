package com.sun.corba.se.spi.copyobject;

public abstract interface CopierManager
{
  public abstract void setDefaultId(int paramInt);

  public abstract int getDefaultId();

  public abstract ObjectCopierFactory getObjectCopierFactory(int paramInt);

  public abstract ObjectCopierFactory getDefaultObjectCopierFactory();

  public abstract void registerObjectCopierFactory(ObjectCopierFactory paramObjectCopierFactory, int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.copyobject.CopierManager
 * JD-Core Version:    0.6.2
 */