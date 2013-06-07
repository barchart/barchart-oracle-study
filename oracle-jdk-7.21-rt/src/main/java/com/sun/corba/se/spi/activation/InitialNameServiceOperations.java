package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;

public abstract interface InitialNameServiceOperations
{
  public abstract void bind(String paramString, org.omg.CORBA.Object paramObject, boolean paramBoolean)
    throws NameAlreadyBound;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.InitialNameServiceOperations
 * JD-Core Version:    0.6.2
 */