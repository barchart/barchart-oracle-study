package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;

public abstract interface Augmentations
{
  public abstract Object putItem(String paramString, Object paramObject);

  public abstract Object getItem(String paramString);

  public abstract Object removeItem(String paramString);

  public abstract Enumeration keys();

  public abstract void removeAllItems();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.Augmentations
 * JD-Core Version:    0.6.2
 */