package com.sun.org.apache.xml.internal.utils;

import javax.xml.transform.TransformerException;

public abstract interface RawCharacterHandler
{
  public abstract void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws TransformerException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.RawCharacterHandler
 * JD-Core Version:    0.6.2
 */