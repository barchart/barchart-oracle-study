package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;

public abstract interface CodecOperations
{
  public abstract byte[] encode(Any paramAny)
    throws InvalidTypeForEncoding;

  public abstract Any decode(byte[] paramArrayOfByte)
    throws FormatMismatch;

  public abstract byte[] encode_value(Any paramAny)
    throws InvalidTypeForEncoding;

  public abstract Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode)
    throws FormatMismatch, TypeMismatch;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.IOP.CodecOperations
 * JD-Core Version:    0.6.2
 */