package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface State
{
  public abstract void preAction(FSM paramFSM);

  public abstract void postAction(FSM paramFSM);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.fsm.State
 * JD-Core Version:    0.6.2
 */