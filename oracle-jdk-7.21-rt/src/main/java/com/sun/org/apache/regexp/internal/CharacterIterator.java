package com.sun.org.apache.regexp.internal;

public abstract interface CharacterIterator
{
  public abstract String substring(int paramInt1, int paramInt2);

  public abstract String substring(int paramInt);

  public abstract char charAt(int paramInt);

  public abstract boolean isEnd(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.regexp.internal.CharacterIterator
 * JD-Core Version:    0.6.2
 */