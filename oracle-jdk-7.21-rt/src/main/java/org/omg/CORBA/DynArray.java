package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public abstract interface DynArray extends Object, DynAny
{
  public abstract Any[] get_elements();

  public abstract void set_elements(Any[] paramArrayOfAny)
    throws InvalidSeq;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.DynArray
 * JD-Core Version:    0.6.2
 */