package sun.net.www.protocol.http;

import java.net.URL;

@Deprecated
public abstract interface HttpAuthenticator
{
  public abstract boolean schemeSupported(String paramString);

  public abstract String authString(URL paramURL, String paramString1, String paramString2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.www.protocol.http.HttpAuthenticator
 * JD-Core Version:    0.6.2
 */