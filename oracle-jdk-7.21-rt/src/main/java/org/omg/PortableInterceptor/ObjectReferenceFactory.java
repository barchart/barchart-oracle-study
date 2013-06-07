package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.ValueBase;

public abstract interface ObjectReferenceFactory extends ValueBase
{
  public abstract org.omg.CORBA.Object make_object(String paramString, byte[] paramArrayOfByte);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.PortableInterceptor.ObjectReferenceFactory
 * JD-Core Version:    0.6.2
 */