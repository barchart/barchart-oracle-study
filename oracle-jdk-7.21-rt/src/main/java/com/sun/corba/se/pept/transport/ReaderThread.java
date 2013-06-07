package com.sun.corba.se.pept.transport;

public abstract interface ReaderThread
{
  public abstract Connection getConnection();

  public abstract void close();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.pept.transport.ReaderThread
 * JD-Core Version:    0.6.2
 */