package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface FSM
{
  public abstract State getState();

  public abstract void doIt(Input paramInput);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.fsm.FSM
 * JD-Core Version:    0.6.2
 */