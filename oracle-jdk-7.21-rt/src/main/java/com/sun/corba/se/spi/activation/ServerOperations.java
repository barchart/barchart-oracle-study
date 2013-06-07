package com.sun.corba.se.spi.activation;

public abstract interface ServerOperations
{
  public abstract void shutdown();

  public abstract void install();

  public abstract void uninstall();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.activation.ServerOperations
 * JD-Core Version:    0.6.2
 */