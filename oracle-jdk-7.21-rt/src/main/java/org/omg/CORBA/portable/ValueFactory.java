package org.omg.CORBA.portable;

import java.io.Serializable;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface ValueFactory
{
  public abstract Serializable read_value(InputStream paramInputStream);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.CORBA.portable.ValueFactory
 * JD-Core Version:    0.6.2
 */