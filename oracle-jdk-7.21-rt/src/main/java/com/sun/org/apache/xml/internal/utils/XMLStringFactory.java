package com.sun.org.apache.xml.internal.utils;

public abstract class XMLStringFactory
{
  public abstract XMLString newstr(String paramString);

  public abstract XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2);

  public abstract XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2);

  public abstract XMLString emptystr();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.XMLStringFactory
 * JD-Core Version:    0.6.2
 */