package com.sun.xml.internal.bind.v2.model.core;

public abstract interface ValuePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C>
{
  public abstract Adapter<T, C> getAdapter();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo
 * JD-Core Version:    0.6.2
 */