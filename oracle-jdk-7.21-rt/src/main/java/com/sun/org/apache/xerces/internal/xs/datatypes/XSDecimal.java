package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract interface XSDecimal
{
  public abstract BigDecimal getBigDecimal();

  public abstract BigInteger getBigInteger()
    throws NumberFormatException;

  public abstract long getLong()
    throws NumberFormatException;

  public abstract int getInt()
    throws NumberFormatException;

  public abstract short getShort()
    throws NumberFormatException;

  public abstract byte getByte()
    throws NumberFormatException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal
 * JD-Core Version:    0.6.2
 */