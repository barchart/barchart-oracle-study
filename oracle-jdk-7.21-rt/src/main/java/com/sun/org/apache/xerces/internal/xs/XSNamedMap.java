package com.sun.org.apache.xerces.internal.xs;

import java.util.Map;

public abstract interface XSNamedMap extends Map
{
  public abstract int getLength();

  public abstract XSObject item(int paramInt);

  public abstract XSObject itemByName(String paramString1, String paramString2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSNamedMap
 * JD-Core Version:    0.6.2
 */