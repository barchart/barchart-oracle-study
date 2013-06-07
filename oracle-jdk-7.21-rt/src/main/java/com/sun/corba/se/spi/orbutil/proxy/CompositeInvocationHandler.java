package com.sun.corba.se.spi.orbutil.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

public abstract interface CompositeInvocationHandler extends InvocationHandler, Serializable
{
  public abstract void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler);

  public abstract void setDefaultHandler(InvocationHandler paramInvocationHandler);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler
 * JD-Core Version:    0.6.2
 */