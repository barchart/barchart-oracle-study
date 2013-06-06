package com.sun.deploy.config;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SecuritySettings
{
  public static final String[] SEC_LEVEL_MANAGED_PROPERTIES = { "deployment.insecure.jres", "deployment.security.run.untrusted", "deployment.security.local.applets", "deployment.security.sandbox.selfsigned", "deployment.security.sandbox.casigned", "deployment.security.askgrantdialog.notinca" };
  public static final String[][] SEC_LEVEL_MAP = { { "PROMPT_MULTI", "NEVER", "NEVER" }, { "PROMPT", "PROMPT_MULTI", "NEVER" }, { "PROMPT", "NEVER", "NEVER" }, { "PROMPT_MULTI", "PROMPT_MULTI", "NEVER" }, { "PROMPT", "PROMPT", "PROMPT" }, { "true", "true", "false" } };
  public static final String[] SECURITY_LEVELS = { "MEDIUM", "HIGH", "VERY_HIGH" };
  public static final int LEVEL_MEDIUM_INDEX = 0;
  public static final int LEVEL_HIGH_INDEX = 1;
  public static final int LEVEL_VERY_HIGH_INDEX = 2;
  public static final int LEVEL_DEFAULT_INDEX = 1;

  public static boolean isSelfSignedNever()
  {
    return "NEVER".equals(getManagedPropertyValue("deployment.security.sandbox.selfsigned"));
  }

  public static boolean isCaSignedNever()
  {
    return "NEVER".equals(getManagedPropertyValue("deployment.security.sandbox.casigned"));
  }

  public static boolean isSSVModeNever()
  {
    return "NEVER".equals(getManagedPropertyValue("deployment.insecure.jres"));
  }

  public static boolean isSSVModePrompt()
  {
    return "PROMPT".equals(getManagedPropertyValue("deployment.insecure.jres"));
  }

  public static boolean isSSVModeMultiClick()
  {
    return "PROMPT_MULTI".equals(getManagedPropertyValue("deployment.insecure.jres"));
  }

  public static boolean isAskGrantShowSet()
  {
    return Config.getBooleanProperty("deployment.security.askgrantdialog.show");
  }

  public static boolean isAskGrantSelfSignedSet()
  {
    return getManagedBooleanValue("deployment.security.askgrantdialog.notinca");
  }

  public static boolean isRunUntrustedNever()
  {
    return "NEVER".equals(getManagedPropertyValue("deployment.security.run.untrusted"));
  }

  public static boolean isRunUntrustedMultiClick()
  {
    return "PROMPT_MULTI".equals(getManagedPropertyValue("deployment.security.run.untrusted"));
  }

  public static boolean isRunLocalAppletsNever()
  {
    return "NEVER".equals(getManagedPropertyValue("deployment.security.local.applets"));
  }

  public static String getManagedPropertyValue(String paramString)
  {
    int i = getSecurityLevelInt();
    int j = getPropertyIndex(paramString);
    if (j < 0)
    {
      Trace.println("Trying to call getManagedProperty(" + paramString + ");", TraceLevel.BASIC);
      return Config.getStringProperty(paramString);
    }
    return SEC_LEVEL_MAP[j][i];
  }

  public static boolean getManagedBooleanValue(String paramString)
  {
    return "true".equalsIgnoreCase(getManagedPropertyValue(paramString));
  }

  private static int getPropertyIndex(String paramString)
  {
    for (int i = 0; i < SEC_LEVEL_MANAGED_PROPERTIES.length; i++)
      if (SEC_LEVEL_MANAGED_PROPERTIES[i].equals(paramString))
        return i;
    return -1;
  }

  public static void setSecurityLevel(String paramString)
  {
    Config.setStringProperty("deployment.security.level", paramString);
    for (int i = 0; i < SEC_LEVEL_MANAGED_PROPERTIES.length; i++)
    {
      Config.get();
      Config.setStringProperty(SEC_LEVEL_MANAGED_PROPERTIES[i], null);
    }
  }

  public static String getSecurityLevel()
  {
    Config.get();
    return Config.getStringProperty("deployment.security.level");
  }

  private static int getSecurityLevelInt()
  {
    return getLevelInt(getSecurityLevel());
  }

  private static int getLevelInt(String paramString)
  {
    for (int i = 0; i < SECURITY_LEVELS.length; i++)
      if (SECURITY_LEVELS[i].equals(paramString))
        return i;
    return 1;
  }

  public static void setInstallerRecommendedSecurityLevel(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < SECURITY_LEVELS.length))
      setSystemDeploymentProperty("deployment.security.level", SECURITY_LEVELS[paramInt]);
  }

  public static void setSystemDeploymentProperty(String paramString1, String paramString2)
  {
    try
    {
      File localFile = new File(Config.getSystemHomePropertiesFile());
      Properties localProperties = new Properties();
      if (localFile.exists())
        localProperties.load(new FileInputStream(localFile));
      localProperties.setProperty(paramString1, paramString2);
      new FileOutputStream(localFile);
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      if (localFileOutputStream != null)
        try
        {
          localProperties.store(localFileOutputStream, "System Deployment Properties");
        }
        finally
        {
          localFileOutputStream.flush();
          try
          {
            localFileOutputStream.close();
          }
          catch (IOException localIOException)
          {
          }
        }
    }
    catch (Throwable localThrowable)
    {
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.SecuritySettings
 * JD-Core Version:    0.6.2
 */