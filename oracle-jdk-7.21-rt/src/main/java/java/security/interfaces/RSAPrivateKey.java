package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public abstract interface RSAPrivateKey extends PrivateKey, RSAKey
{
  public static final long serialVersionUID = 5187144804936595022L;

  public abstract BigInteger getPrivateExponent();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.interfaces.RSAPrivateKey
 * JD-Core Version:    0.6.2
 */