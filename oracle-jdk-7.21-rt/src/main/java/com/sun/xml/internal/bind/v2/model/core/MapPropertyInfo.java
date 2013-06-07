package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface MapPropertyInfo<T, C> extends PropertyInfo<T, C>
{
  public abstract QName getXmlName();

  public abstract boolean isCollectionNillable();

  public abstract NonElement<T, C> getKeyType();

  public abstract NonElement<T, C> getValueType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo
 * JD-Core Version:    0.6.2
 */