package javax.sound.midi;

public abstract interface Transmitter extends AutoCloseable
{
  public abstract void setReceiver(Receiver paramReceiver);

  public abstract Receiver getReceiver();

  public abstract void close();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sound.midi.Transmitter
 * JD-Core Version:    0.6.2
 */