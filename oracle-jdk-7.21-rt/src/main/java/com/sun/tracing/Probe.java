package com.sun.tracing;

public abstract interface Probe
{
  public abstract boolean isEnabled();

  public abstract void trigger(Object[] paramArrayOfObject);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.tracing.Probe
 * JD-Core Version:    0.6.2
 */