package sun.management.counter;

public abstract interface ByteArrayCounter extends Counter
{
  public abstract byte[] byteArrayValue();

  public abstract byte byteAt(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.counter.ByteArrayCounter
 * JD-Core Version:    0.6.2
 */