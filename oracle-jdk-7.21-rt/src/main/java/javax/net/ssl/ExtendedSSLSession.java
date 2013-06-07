package javax.net.ssl;

public abstract class ExtendedSSLSession
  implements SSLSession
{
  public abstract String[] getLocalSupportedSignatureAlgorithms();

  public abstract String[] getPeerSupportedSignatureAlgorithms();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.net.ssl.ExtendedSSLSession
 * JD-Core Version:    0.6.2
 */