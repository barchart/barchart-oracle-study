package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface AttributePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C>
{
  public abstract NonElement<T, C> getTarget();

  public abstract boolean isRequired();

  public abstract QName getXmlName();

  public abstract Adapter<T, C> getAdapter();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo
 * JD-Core Version:    0.6.2
 */