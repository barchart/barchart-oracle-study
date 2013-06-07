package com.sun.org.glassfish.external.statistics;

public abstract interface BoundaryStatistic extends Statistic
{
  public abstract long getUpperBound();

  public abstract long getLowerBound();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.statistics.BoundaryStatistic
 * JD-Core Version:    0.6.2
 */