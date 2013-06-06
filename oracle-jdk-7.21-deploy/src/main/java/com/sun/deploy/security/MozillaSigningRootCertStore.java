package com.sun.deploy.security;

public final class MozillaSigningRootCertStore extends MozillaCertStore
{
  protected String getName()
  {
    return "ROOT";
  }

  protected boolean isTrustedSigningCACertStore()
  {
    return true;
  }

  protected boolean isTrustedSSLCACertStore()
  {
    return false;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.MozillaSigningRootCertStore
 * JD-Core Version:    0.6.2
 */