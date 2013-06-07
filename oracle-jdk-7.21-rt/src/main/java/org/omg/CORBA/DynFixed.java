package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidValue;

@Deprecated
public abstract interface DynFixed extends Object, DynAny
{
  public abstract byte[] get_value();

  public abstract void set_value(byte[] paramArrayOfByte)
    throws InvalidValue;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.DynFixed
 * JD-Core Version:    0.6.2
 */