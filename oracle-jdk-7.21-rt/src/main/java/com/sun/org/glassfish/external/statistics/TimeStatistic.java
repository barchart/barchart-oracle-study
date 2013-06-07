package com.sun.org.glassfish.external.statistics;

public abstract interface TimeStatistic extends Statistic
{
  public abstract long getCount();

  public abstract long getMaxTime();

  public abstract long getMinTime();

  public abstract long getTotalTime();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.TimeStatistic
 * JD-Core Version:    0.6.2
 */