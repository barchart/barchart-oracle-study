package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSNotationDeclaration extends XSObject
{
  public abstract String getSystemId();

  public abstract String getPublicId();

  public abstract XSAnnotation getAnnotation();

  public abstract XSObjectList getAnnotations();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration
 * JD-Core Version:    0.6.2
 */