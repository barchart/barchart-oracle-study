package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;

public abstract interface InvocationHandlerFactory
{
  public abstract InvocationHandler getInvocationHandler();

  public abstract Class[] getProxyInterfaces();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory
 * JD-Core Version:    0.6.2
 */