package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public abstract interface ModelChannelMixer extends MidiChannel
{
  public abstract boolean process(float[][] paramArrayOfFloat, int paramInt1, int paramInt2);

  public abstract void stop();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.ModelChannelMixer
 * JD-Core Version:    0.6.2
 */