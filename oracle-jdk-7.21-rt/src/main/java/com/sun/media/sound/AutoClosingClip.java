package com.sun.media.sound;

import javax.sound.sampled.Clip;

abstract interface AutoClosingClip extends Clip
{
  public abstract boolean isAutoClosing();

  public abstract void setAutoClosing(boolean paramBoolean);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AutoClosingClip
 * JD-Core Version:    0.6.2
 */