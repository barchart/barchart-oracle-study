package javax.net.ssl;

public abstract interface HostnameVerifier
{
  public abstract boolean verify(String paramString, SSLSession paramSSLSession);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.net.ssl.HostnameVerifier
 * JD-Core Version:    0.6.2
 */