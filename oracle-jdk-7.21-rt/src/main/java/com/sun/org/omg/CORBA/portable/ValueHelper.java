package com.sun.org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

@Deprecated
public abstract interface ValueHelper extends BoxedValueHelper
{
  public abstract Class get_class();

  public abstract String[] get_truncatable_base_ids();

  public abstract TypeCode get_type();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.omg.CORBA.portable.ValueHelper
 * JD-Core Version:    0.6.2
 */