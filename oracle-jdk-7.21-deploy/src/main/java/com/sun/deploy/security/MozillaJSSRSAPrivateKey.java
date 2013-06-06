package com.sun.deploy.security;

class MozillaJSSRSAPrivateKey extends MozillaJSSPrivateKey
{
  MozillaJSSRSAPrivateKey(Object paramObject, int paramInt)
  {
    super(paramObject, paramInt);
  }

  public String getAlgorithm()
  {
    return "RSA";
  }

  public String toString()
  {
    return "MozillaJSSRSAPrivateKey [JSSKey=" + this.key + ", key length=" + this.keyLength + "bits]";
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.MozillaJSSRSAPrivateKey
 * JD-Core Version:    0.6.2
 */