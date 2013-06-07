package com.sun.org.glassfish.external.statistics;

public abstract interface RangeStatistic extends Statistic
{
  public abstract long getHighWaterMark();

  public abstract long getLowWaterMark();

  public abstract long getCurrent();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.RangeStatistic
 * JD-Core Version:    0.6.2
 */