package netscape.security;

import java.net.URL;

public final class Principal
{
  public static final int CODEBASE_EXACT = 1;
  public static final int CODEBASE_REGEXP = 2;
  public static final int CERT = 3;
  public static final int CERT_FINGERPRINT = 4;
  public static final int CERT_KEY = 5;
  private int type = 3;
  private URL url = null;

  public Principal()
  {
  }

  public Principal(URL paramURL)
  {
    this.url = paramURL;
  }

  public Principal(int paramInt, String paramString)
  {
    this.type = paramInt;
  }

  public Principal(int paramInt, byte[] paramArrayOfByte)
  {
    this.type = paramInt;
  }

  public Principal(int paramInt, byte[] paramArrayOfByte, Class paramClass)
  {
    this.type = paramInt;
  }

  public boolean isCodebase()
  {
    return (isCodebaseExact()) || (isCodebaseRegexp());
  }

  public boolean isCodebaseExact()
  {
    return this.type == 1;
  }

  public boolean isCodebaseRegexp()
  {
    return this.type == 2;
  }

  public boolean isCert()
  {
    return this.type == 3;
  }

  public boolean isCertFingerprint()
  {
    return this.type == 4;
  }

  public String toVerboseString()
  {
    return toString();
  }

  public String getVendor()
  {
    return null;
  }

  public String toVerboseHtml()
  {
    return null;
  }

  public String getNickname()
  {
    return null;
  }

  public boolean isSystemPrincipal()
  {
    return false;
  }

  public static int getZigPtr(Class paramClass)
  {
    return -1;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     netscape.security.Principal
 * JD-Core Version:    0.6.2
 */