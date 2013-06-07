package com.sun.corba.se.impl.encoding;

abstract interface RestorableInputStream
{
  public abstract Object createStreamMemento();

  public abstract void restoreInternalState(Object paramObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.encoding.RestorableInputStream
 * JD-Core Version:    0.6.2
 */