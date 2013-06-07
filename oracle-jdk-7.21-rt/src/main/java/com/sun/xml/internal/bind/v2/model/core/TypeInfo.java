package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

public abstract interface TypeInfo<T, C> extends Locatable
{
  public abstract T getType();

  public abstract boolean canBeReferencedByIDREF();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.TypeInfo
 * JD-Core Version:    0.6.2
 */