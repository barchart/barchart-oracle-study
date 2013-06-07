package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

@Deprecated
public abstract class TrustManagerFactorySpi
{
  protected abstract void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException;

  protected abstract TrustManager[] engineGetTrustManagers();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.net.ssl.TrustManagerFactorySpi
 * JD-Core Version:    0.6.2
 */