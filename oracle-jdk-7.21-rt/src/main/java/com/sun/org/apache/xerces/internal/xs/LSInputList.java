package com.sun.org.apache.xerces.internal.xs;

import java.util.List;
import org.w3c.dom.ls.LSInput;

public abstract interface LSInputList extends List
{
  public abstract int getLength();

  public abstract LSInput item(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.LSInputList
 * JD-Core Version:    0.6.2
 */