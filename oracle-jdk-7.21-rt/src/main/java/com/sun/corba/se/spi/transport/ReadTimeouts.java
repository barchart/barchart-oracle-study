package com.sun.corba.se.spi.transport;

public abstract interface ReadTimeouts
{
  public abstract int get_initial_time_to_wait();

  public abstract int get_max_time_to_wait();

  public abstract double get_backoff_factor();

  public abstract int get_max_giop_header_time_to_wait();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.transport.ReadTimeouts
 * JD-Core Version:    0.6.2
 */