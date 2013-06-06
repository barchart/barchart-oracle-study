package com.sun.deploy.registration;

import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.config.Platform.WebJavaSwitch;
import com.sun.deploy.config.SecuritySettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class RegisterDeploy
{
  public static final InstallCommands register(InstallHints paramInstallHints)
  {
    InstallCommands localInstallCommands = new InstallCommands();
    if (paramInstallHints == null)
      return localInstallCommands;
    if (paramInstallHints.getWebJavaEnabled() != -1)
      if (!setWebJavaEnabled(paramInstallHints.getWebJavaEnabled() == 1))
        localInstallCommands.setInstallStatus(1);
    if ((paramInstallHints.getCustomSecurityLevel() != -1) && (!setSystemSecurityLevel(paramInstallHints.getCustomSecurityLevel())))
      localInstallCommands.setInstallStatus(1);
    return localInstallCommands;
  }

  static final boolean hasSystemConfigAccess()
  {
    boolean bool1 = false;
    File localFile1 = null;
    try
    {
      File localFile2 = new File(Config.getSystemHome());
      localFile2.mkdirs();
      localFile1 = new File(Config.getSystemHome() + File.separator + Config.getPropertiesFilename());
      bool1 = localFile1.createNewFile();
      new FileOutputStream(localFile1).close();
    }
    catch (Throwable localThrowable)
    {
      boolean bool2 = false;
      return bool2;
    }
    finally
    {
      if ((localFile1 != null) && (bool1))
        localFile1.delete();
    }
    return true;
  }

  private static final boolean setSystemSecurityLevel(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= SecuritySettings.SECURITY_LEVELS.length))
    {
      System.out.println("unsupported security level: " + paramInt);
      System.out.println("support level is 0 (medium), 1 (high), 2 (very high)");
      return false;
    }
    if (!hasSystemConfigAccess())
    {
      System.out.println("user has no access to system settings");
      return false;
    }
    SecuritySettings.setInstallerRecommendedSecurityLevel(paramInt);
    return true;
  }

  private static final boolean setWebJavaEnabled(boolean paramBoolean)
  {
    if (!hasSystemConfigAccess())
    {
      System.out.println("user has no access to system settings");
      return false;
    }
    Platform.get().getWebJavaSwitch().setWebJavaEnabled(paramBoolean);
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.registration.RegisterDeploy
 * JD-Core Version:    0.6.2
 */