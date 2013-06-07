package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public abstract interface ObjectKeyFactory
{
  public abstract ObjectKey create(byte[] paramArrayOfByte);

  public abstract ObjectKeyTemplate createTemplate(InputStream paramInputStream);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.ior.ObjectKeyFactory
 * JD-Core Version:    0.6.2
 */