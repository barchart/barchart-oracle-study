package org.omg.IOP;

import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

public abstract interface CodecFactoryOperations
{
  public abstract Codec create_codec(Encoding paramEncoding)
    throws UnknownEncoding;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.omg.IOP.CodecFactoryOperations
 * JD-Core Version:    0.6.2
 */