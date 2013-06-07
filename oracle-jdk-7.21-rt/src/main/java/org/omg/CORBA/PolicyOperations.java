package org.omg.CORBA;

public abstract interface PolicyOperations
{
  public abstract int policy_type();

  public abstract Policy copy();

  public abstract void destroy();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.PolicyOperations
 * JD-Core Version:    0.6.2
 */