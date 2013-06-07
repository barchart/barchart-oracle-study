package com.sun.corba.se.spi.presentation.rmi;

import java.lang.reflect.Method;

public abstract interface IDLNameTranslator
{
  public abstract Class[] getInterfaces();

  public abstract Method[] getMethods();

  public abstract Method getMethod(String paramString);

  public abstract String getIDLName(Method paramMethod);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator
 * JD-Core Version:    0.6.2
 */