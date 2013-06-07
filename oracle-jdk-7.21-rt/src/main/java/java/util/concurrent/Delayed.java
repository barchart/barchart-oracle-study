package java.util.concurrent;

public abstract interface Delayed extends Comparable<Delayed>
{
  public abstract long getDelay(TimeUnit paramTimeUnit);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.Delayed
 * JD-Core Version:    0.6.2
 */