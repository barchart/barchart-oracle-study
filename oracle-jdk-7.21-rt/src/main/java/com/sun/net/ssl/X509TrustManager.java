package com.sun.net.ssl;

import java.security.cert.X509Certificate;

@Deprecated
public abstract interface X509TrustManager extends TrustManager
{
  public abstract boolean isClientTrusted(X509Certificate[] paramArrayOfX509Certificate);

  public abstract boolean isServerTrusted(X509Certificate[] paramArrayOfX509Certificate);

  public abstract X509Certificate[] getAcceptedIssuers();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.net.ssl.X509TrustManager
 * JD-Core Version:    0.6.2
 */