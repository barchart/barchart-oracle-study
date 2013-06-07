package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface StateEngine
{
  public abstract StateEngine add(State paramState1, Input paramInput, Guard paramGuard, Action paramAction, State paramState2)
    throws IllegalStateException;

  public abstract StateEngine add(State paramState1, Input paramInput, Action paramAction, State paramState2)
    throws IllegalStateException;

  public abstract StateEngine setDefault(State paramState1, Action paramAction, State paramState2)
    throws IllegalStateException;

  public abstract StateEngine setDefault(State paramState1, State paramState2)
    throws IllegalStateException;

  public abstract StateEngine setDefault(State paramState)
    throws IllegalStateException;

  public abstract void setDefaultAction(Action paramAction)
    throws IllegalStateException;

  public abstract void done()
    throws IllegalStateException;

  public abstract FSM makeFSM(State paramState)
    throws IllegalStateException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.orbutil.fsm.StateEngine
 * JD-Core Version:    0.6.2
 */