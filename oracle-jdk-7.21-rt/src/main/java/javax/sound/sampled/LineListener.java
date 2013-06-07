package javax.sound.sampled;

import java.util.EventListener;

public abstract interface LineListener extends EventListener
{
  public abstract void update(LineEvent paramLineEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.sampled.LineListener
 * JD-Core Version:    0.6.2
 */