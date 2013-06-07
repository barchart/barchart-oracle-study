package com.sun.org.apache.xerces.internal.xs.datatypes;

import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.List;

public abstract interface ByteList extends List
{
  public abstract int getLength();

  public abstract boolean contains(byte paramByte);

  public abstract byte item(int paramInt)
    throws XSException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.datatypes.ByteList
 * JD-Core Version:    0.6.2
 */