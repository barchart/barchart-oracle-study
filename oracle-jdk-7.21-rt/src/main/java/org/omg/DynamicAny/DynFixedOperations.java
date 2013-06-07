package org.omg.DynamicAny;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public abstract interface DynFixedOperations extends DynAnyOperations
{
  public abstract String get_value();

  public abstract boolean set_value(String paramString)
    throws TypeMismatch, InvalidValue;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.DynamicAny.DynFixedOperations
 * JD-Core Version:    0.6.2
 */