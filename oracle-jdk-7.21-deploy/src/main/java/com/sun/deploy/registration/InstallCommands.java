package com.sun.deploy.registration;

public class InstallCommands
{
  public static final int STATUS_OK = 0;
  public static final int STATUS_HINT_OP_FAILED = 1;
  private int installStatus = 0;
  private static final int VERSION = 1;

  void setInstallStatus(int paramInt)
  {
    this.installStatus = paramInt;
  }

  public final int getInstallStatus()
  {
    return this.installStatus;
  }

  int getVesrion()
  {
    return 1;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.registration.InstallCommands
 * JD-Core Version:    0.6.2
 */