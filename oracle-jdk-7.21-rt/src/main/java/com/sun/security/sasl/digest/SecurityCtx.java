package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

abstract interface SecurityCtx
{
  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;

  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.sasl.digest.SecurityCtx
 * JD-Core Version:    0.6.2
 */