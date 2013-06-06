package com.sun.deploy.perf;

public abstract interface PerfHelper
{
  public abstract void setInitTime(long paramLong);

  public abstract void setInitTime1(long paramLong);

  public abstract long getInitTime0();

  public abstract long getInitTime1();

  public abstract void clear();

  public abstract void put(String paramString);

  public abstract long put(long paramLong, String paramString);

  public abstract PerfLabel[] toArray();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.perf.PerfHelper
 * JD-Core Version:    0.6.2
 */