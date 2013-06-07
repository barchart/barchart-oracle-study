package com.sun.xml.internal.bind.api;

public abstract class RawAccessor<B, V>
{
  public abstract V get(B paramB)
    throws AccessorException;

  public abstract void set(B paramB, V paramV)
    throws AccessorException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.api.RawAccessor
 * JD-Core Version:    0.6.2
 */