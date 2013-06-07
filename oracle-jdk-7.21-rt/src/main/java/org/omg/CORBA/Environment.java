package org.omg.CORBA;

public abstract class Environment
{
  public abstract Exception exception();

  public abstract void exception(Exception paramException);

  public abstract void clear();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.Environment
 * JD-Core Version:    0.6.2
 */