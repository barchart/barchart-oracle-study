package com.sun.deploy.registration;

public class InstallHints
{
  private int webJavaSwitchEnabled = -1;
  private int customSecurityLevel = -1;
  static final int NOT_SPECIFIED = -1;
  public static final int WEB_JAVA_ENABLED = 1;
  public static final int WEB_JAVA_DISABLED = 0;
  public static final int SECURITY_LEVEL_LOW = 1;
  public static final int SECURITY_LEVEL_MED = 0;
  public static final int SECURITY_LEVEL_HIGH = 1;
  public static final int SECURITY_LEVEL_VERY_HIGH = 2;

  public void setWebJavaEnabled(boolean paramBoolean)
  {
    this.webJavaSwitchEnabled = (paramBoolean ? 1 : 0);
  }

  public void setCustomSecurityLevel(int paramInt)
  {
    this.customSecurityLevel = paramInt;
  }

  int getWebJavaEnabled()
  {
    return this.webJavaSwitchEnabled;
  }

  int getCustomSecurityLevel()
  {
    return this.customSecurityLevel;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.registration.InstallHints
 * JD-Core Version:    0.6.2
 */