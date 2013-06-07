package com.sun.corba.se.spi.orbutil.threadpool;

public abstract interface ThreadPoolChooser
{
  public abstract ThreadPool getThreadPool();

  public abstract ThreadPool getThreadPool(int paramInt);

  public abstract String[] getThreadPoolIds();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser
 * JD-Core Version:    0.6.2
 */