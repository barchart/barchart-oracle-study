package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSModelGroupDefinition extends XSObject
{
  public abstract XSModelGroup getModelGroup();

  public abstract XSAnnotation getAnnotation();

  public abstract XSObjectList getAnnotations();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition
 * JD-Core Version:    0.6.2
 */