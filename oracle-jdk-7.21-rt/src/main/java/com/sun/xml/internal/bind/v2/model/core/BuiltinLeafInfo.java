package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface BuiltinLeafInfo<T, C> extends LeafInfo<T, C>
{
  public abstract QName getTypeName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo
 * JD-Core Version:    0.6.2
 */