package com.sun.org.apache.xerces.internal.xs;

public abstract interface PSVIProvider
{
  public abstract ElementPSVI getElementPSVI();

  public abstract AttributePSVI getAttributePSVI(int paramInt);

  public abstract AttributePSVI getAttributePSVIByName(String paramString1, String paramString2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.PSVIProvider
 * JD-Core Version:    0.6.2
 */