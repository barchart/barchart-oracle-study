package com.sun.deploy.security;

public class CertType
{
  private String type = null;
  public static final CertType BROWSER = new CertType("browser");
  public static final CertType PLUGIN = new CertType("plugins");

  CertType(String paramString)
  {
    this.type = paramString;
  }

  public String getType()
  {
    return this.type;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.CertType
 * JD-Core Version:    0.6.2
 */