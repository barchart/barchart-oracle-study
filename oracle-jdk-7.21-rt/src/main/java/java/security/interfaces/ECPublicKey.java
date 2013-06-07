package java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

public abstract interface ECPublicKey extends PublicKey, ECKey
{
  public static final long serialVersionUID = -3314988629879632826L;

  public abstract ECPoint getW();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.interfaces.ECPublicKey
 * JD-Core Version:    0.6.2
 */