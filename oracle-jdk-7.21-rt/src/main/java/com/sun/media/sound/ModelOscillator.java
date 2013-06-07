package com.sun.media.sound;

public abstract interface ModelOscillator
{
  public abstract int getChannels();

  public abstract float getAttenuation();

  public abstract ModelOscillatorStream open(float paramFloat);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.ModelOscillator
 * JD-Core Version:    0.6.2
 */