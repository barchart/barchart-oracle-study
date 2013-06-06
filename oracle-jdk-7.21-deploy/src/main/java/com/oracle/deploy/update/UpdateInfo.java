package com.oracle.deploy.update;

public class UpdateInfo
{
  public String mVersion;
  public String mSize;
  public String mType;

  public UpdateInfo(String paramString1, String paramString2, String paramString3)
  {
    this.mVersion = paramString1;
    this.mSize = paramString2;
    this.mType = paramString3;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.oracle.deploy.update.UpdateInfo
 * JD-Core Version:    0.6.2
 */