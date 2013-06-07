package com.sun.xml.internal.bind.v2.model.core;

public abstract interface EnumConstant<T, C>
{
  public abstract EnumLeafInfo<T, C> getEnclosingClass();

  public abstract String getLexicalValue();

  public abstract String getName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.core.EnumConstant
 * JD-Core Version:    0.6.2
 */