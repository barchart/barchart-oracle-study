package javax.sound.midi;

import java.util.EventListener;

public abstract interface ControllerEventListener extends EventListener
{
  public abstract void controlChange(ShortMessage paramShortMessage);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.midi.ControllerEventListener
 * JD-Core Version:    0.6.2
 */