package java.security;

import java.util.Set;

public abstract interface AlgorithmConstraints
{
  public abstract boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters);

  public abstract boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey);

  public abstract boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.AlgorithmConstraints
 * JD-Core Version:    0.6.2
 */