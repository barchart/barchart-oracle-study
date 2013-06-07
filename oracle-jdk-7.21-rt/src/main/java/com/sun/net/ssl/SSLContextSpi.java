package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

@Deprecated
public abstract class SSLContextSpi
{
  protected abstract void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom)
    throws KeyManagementException;

  protected abstract SSLSocketFactory engineGetSocketFactory();

  protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.net.ssl.SSLContextSpi
 * JD-Core Version:    0.6.2
 */