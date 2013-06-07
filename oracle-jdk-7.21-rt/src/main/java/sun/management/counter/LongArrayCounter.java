package sun.management.counter;

public abstract interface LongArrayCounter extends Counter
{
  public abstract long[] longArrayValue();

  public abstract long longAt(int paramInt);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.counter.LongArrayCounter
 * JD-Core Version:    0.6.2
 */