package com.sun.org.apache.xerces.internal.xni;

public abstract interface XMLLocator
{
  public abstract String getPublicId();

  public abstract String getLiteralSystemId();

  public abstract String getBaseSystemId();

  public abstract String getExpandedSystemId();

  public abstract int getLineNumber();

  public abstract int getColumnNumber();

  public abstract int getCharacterOffset();

  public abstract String getEncoding();

  public abstract String getXMLVersion();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.XMLLocator
 * JD-Core Version:    0.6.2
 */