package com.sun.media.sound;

public abstract interface SoftProcess extends SoftControl
{
  public abstract void init(SoftSynthesizer paramSoftSynthesizer);

  public abstract double[] get(int paramInt, String paramString);

  public abstract void processControlLogic();

  public abstract void reset();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SoftProcess
 * JD-Core Version:    0.6.2
 */