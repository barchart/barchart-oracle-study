package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;

public abstract interface PolicyFactoryOperations
{
  public abstract Policy create_policy(int paramInt, Any paramAny)
    throws PolicyError;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableInterceptor.PolicyFactoryOperations
 * JD-Core Version:    0.6.2
 */