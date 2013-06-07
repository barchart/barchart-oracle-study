package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;

public abstract interface AccessorFactory
{
  public abstract Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean)
    throws JAXBException;

  public abstract Accessor createPropertyAccessor(Class paramClass, Method paramMethod1, Method paramMethod2)
    throws JAXBException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.AccessorFactory
 * JD-Core Version:    0.6.2
 */