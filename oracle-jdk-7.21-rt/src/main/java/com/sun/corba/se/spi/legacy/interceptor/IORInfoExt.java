package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public abstract interface IORInfoExt
{
  public abstract int getServerPort(String paramString)
    throws UnknownType;

  public abstract ObjectAdapter getObjectAdapter();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.legacy.interceptor.IORInfoExt
 * JD-Core Version:    0.6.2
 */