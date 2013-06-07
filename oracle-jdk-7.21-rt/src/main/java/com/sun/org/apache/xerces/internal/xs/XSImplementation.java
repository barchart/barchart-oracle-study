package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSImplementation
{
  public abstract StringList getRecognizedVersions();

  public abstract XSLoader createXSLoader(StringList paramStringList)
    throws XSException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSImplementation
 * JD-Core Version:    0.6.2
 */