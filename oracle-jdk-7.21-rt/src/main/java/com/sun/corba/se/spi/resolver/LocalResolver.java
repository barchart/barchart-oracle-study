package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public abstract interface LocalResolver extends Resolver
{
  public abstract void register(String paramString, Closure paramClosure);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.resolver.LocalResolver
 * JD-Core Version:    0.6.2
 */