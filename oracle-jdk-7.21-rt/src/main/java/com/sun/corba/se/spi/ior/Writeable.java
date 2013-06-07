package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream;

public abstract interface Writeable
{
  public abstract void write(OutputStream paramOutputStream);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.Writeable
 * JD-Core Version:    0.6.2
 */