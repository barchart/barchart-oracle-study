package sun.nio.ch;

import sun.misc.Cleaner;

public abstract interface DirectBuffer
{
  public abstract long address();

  public abstract Object attachment();

  public abstract Cleaner cleaner();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.DirectBuffer
 * JD-Core Version:    0.6.2
 */