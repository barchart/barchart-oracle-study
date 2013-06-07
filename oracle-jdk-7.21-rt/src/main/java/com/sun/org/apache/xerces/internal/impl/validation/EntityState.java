package com.sun.org.apache.xerces.internal.impl.validation;

public abstract interface EntityState
{
  public abstract boolean isEntityDeclared(String paramString);

  public abstract boolean isEntityUnparsed(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.validation.EntityState
 * JD-Core Version:    0.6.2
 */