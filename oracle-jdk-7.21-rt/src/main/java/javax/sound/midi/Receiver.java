package javax.sound.midi;

public abstract interface Receiver extends AutoCloseable
{
  public abstract void send(MidiMessage paramMidiMessage, long paramLong);

  public abstract void close();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.midi.Receiver
 * JD-Core Version:    0.6.2
 */