package com.sun.deploy.security;

class MozillaJSSDSAPrivateKey extends MozillaJSSPrivateKey
{
  MozillaJSSDSAPrivateKey(Object paramObject, int paramInt)
  {
    super(paramObject, paramInt);
  }

  public String getAlgorithm()
  {
    return "DSA";
  }

  public String toString()
  {
    return "MozillaJSSDSAPrivateKey [JSSKey=" + this.key + ", key length=" + this.keyLength + "bits]";
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.MozillaJSSDSAPrivateKey
 * JD-Core Version:    0.6.2
 */