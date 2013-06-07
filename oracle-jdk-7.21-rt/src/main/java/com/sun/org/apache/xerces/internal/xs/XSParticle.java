package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSParticle extends XSObject
{
  public abstract int getMinOccurs();

  public abstract int getMaxOccurs();

  public abstract boolean getMaxOccursUnbounded();

  public abstract XSTerm getTerm();

  public abstract XSObjectList getAnnotations();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSParticle
 * JD-Core Version:    0.6.2
 */