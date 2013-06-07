package com.sun.xml.internal.bind.v2.model.core;

public abstract interface EnumLeafInfo<T, C> extends LeafInfo<T, C>
{
  public abstract C getClazz();

  public abstract NonElement<T, C> getBaseType();

  public abstract Iterable<? extends EnumConstant> getConstants();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo
 * JD-Core Version:    0.6.2
 */