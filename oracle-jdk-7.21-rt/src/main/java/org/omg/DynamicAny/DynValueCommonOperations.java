package org.omg.DynamicAny;

public abstract interface DynValueCommonOperations extends DynAnyOperations
{
  public abstract boolean is_null();

  public abstract void set_to_null();

  public abstract void set_to_value();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.DynamicAny.DynValueCommonOperations
 * JD-Core Version:    0.6.2
 */