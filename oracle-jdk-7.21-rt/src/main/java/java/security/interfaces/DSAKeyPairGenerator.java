package java.security.interfaces;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

public abstract interface DSAKeyPairGenerator
{
  public abstract void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom)
    throws InvalidParameterException;

  public abstract void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom)
    throws InvalidParameterException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.interfaces.DSAKeyPairGenerator
 * JD-Core Version:    0.6.2
 */