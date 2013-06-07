package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;

public abstract interface CurrentOperations extends org.omg.CORBA.CurrentOperations
{
  public abstract Any get_slot(int paramInt)
    throws InvalidSlot;

  public abstract void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableInterceptor.CurrentOperations
 * JD-Core Version:    0.6.2
 */