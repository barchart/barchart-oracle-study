package org.omg.PortableServer;

import org.omg.CORBA.PolicyOperations;

public abstract interface LifespanPolicyOperations extends PolicyOperations
{
  public abstract LifespanPolicyValue value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableServer.LifespanPolicyOperations
 * JD-Core Version:    0.6.2
 */