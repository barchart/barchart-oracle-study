package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public abstract interface ReferenceCountingDevice
{
  public abstract Receiver getReceiverReferenceCounting()
    throws MidiUnavailableException;

  public abstract Transmitter getTransmitterReferenceCounting()
    throws MidiUnavailableException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.ReferenceCountingDevice
 * JD-Core Version:    0.6.2
 */