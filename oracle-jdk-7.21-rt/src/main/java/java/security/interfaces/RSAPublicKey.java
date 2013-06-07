package java.security.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

public abstract interface RSAPublicKey extends PublicKey, RSAKey
{
  public static final long serialVersionUID = -8727434096241101194L;

  public abstract BigInteger getPublicExponent();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.interfaces.RSAPublicKey
 * JD-Core Version:    0.6.2
 */