package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public abstract interface XSObjectList extends List
{
  public abstract int getLength();

  public abstract XSObject item(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.XSObjectList
 * JD-Core Version:    0.6.2
 */