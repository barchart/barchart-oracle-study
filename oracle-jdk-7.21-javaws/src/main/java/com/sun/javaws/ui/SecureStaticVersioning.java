package com.sun.javaws.ui;

import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.SecuritySettings;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.SandboxSecurity;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.SecurityBaseline;
import com.sun.deploy.util.VersionID;
import com.sun.javaws.JnlpxArgs;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.util.JfxHelper;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;

public class SecureStaticVersioning
{
  private static final String SSV2VERSION_KEY = "ssv.version.allowed";
  private static final String SSV2LATEST_VALUE = "ssv.latest.allowed";
  private static final int REASON_CONFIG = 0;
  private static final int REASON_UNSIGNED = 1;
  private static final int REASON_UNAVAIL = 2;
  private static final int REASON_FX = 3;
  private static final int REASON_INVALID = 4;

  public static boolean promptUse(Component paramComponent, AppInfo paramAppInfo, LocalApplicationProperties paramLocalApplicationProperties, String paramString)
    throws ExitException
  {
    if (SecuritySettings.isSSVModeNever())
    {
      useLatest(paramAppInfo, paramString);
      return false;
    }
    if ((paramLocalApplicationProperties != null) && ("ssv.latest.allowed".equals(paramLocalApplicationProperties.get("ssv.version.allowed"))))
      return false;
    if (showSSV2Dialog(paramComponent, paramAppInfo, paramString))
    {
      if (paramLocalApplicationProperties != null)
      {
        paramLocalApplicationProperties.put("ssv.version.allowed", paramString);
        try
        {
          paramLocalApplicationProperties.store();
        }
        catch (IOException localIOException1)
        {
          Trace.ignoredException(localIOException1);
        }
      }
      return true;
    }
    if (paramLocalApplicationProperties != null)
    {
      paramLocalApplicationProperties.put("ssv.version.allowed", "ssv.latest.allowed");
      try
      {
        paramLocalApplicationProperties.store();
      }
      catch (IOException localIOException2)
      {
        Trace.ignoredException(localIOException2);
      }
    }
    return false;
  }

  public static boolean canUse(LaunchDesc paramLaunchDesc, String paramString)
    throws ExitException
  {
    LocalApplicationProperties localLocalApplicationProperties = Cache.getLocalApplicationProperties(paramLaunchDesc.getCanonicalHome());
    if ((paramLaunchDesc.needFX()) && (!JfxHelper.isCompatibleRuntime(paramLaunchDesc.getHomeJRE(), paramLaunchDesc.getSelectedJRE())))
    {
      showCannotUseDialog(null, paramLaunchDesc.getAppInfo(), localLocalApplicationProperties, paramString, 3);
      return false;
    }
    if ((JnlpxArgs.getIsRelaunch()) && (!paramString.equals(paramLaunchDesc.getSelectedJRE().getProduct())))
      return false;
    if ((paramLaunchDesc.getSecurityModel() != 0) || (!isOlderVersion(paramString)) || (SecurityBaseline.satisfiesSecurityBaseline(paramString)))
      return true;
    if ((localLocalApplicationProperties != null) && (paramString.equals(localLocalApplicationProperties.get("ssv.version.allowed"))))
      return true;
    return promptUse(null, paramLaunchDesc.getAppInfo(), localLocalApplicationProperties, paramString);
  }

  public static boolean canUse(AppInfo paramAppInfo, String paramString)
    throws ExitException
  {
    if ((!isOlderVersion(paramString)) || (SecurityBaseline.satisfiesSecurityBaseline(paramString)))
      return true;
    LocalApplicationProperties localLocalApplicationProperties = null;
    try
    {
      URL localURL = new URL(paramAppInfo.getFrom().toString() + "/" + paramAppInfo.getTitle());
      localLocalApplicationProperties = Cache.getLocalApplicationProperties(localURL);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    if ((localLocalApplicationProperties != null) && (paramString.equals(localLocalApplicationProperties.get("ssv.version.allowed"))))
      return true;
    return promptUse(null, paramAppInfo, localLocalApplicationProperties, paramString);
  }

  public static void resetAcceptedVersion(URL paramURL)
  {
    LocalApplicationProperties localLocalApplicationProperties = Cache.getLocalApplicationProperties(paramURL);
    if (localLocalApplicationProperties != null)
    {
      localLocalApplicationProperties.put("ssv.version.allowed", null);
      SandboxSecurity.resetAcceptedVersion(localLocalApplicationProperties);
    }
  }

  private static boolean isOlderVersion(String paramString)
  {
    VersionID localVersionID1 = new VersionID(SecurityBaseline.getCurrentVersion() + "+");
    VersionID localVersionID2 = new VersionID(paramString);
    return !localVersionID1.match(localVersionID2);
  }

  private static LocalApplicationProperties getLap(AppInfo paramAppInfo)
  {
    return Cache.getLocalApplicationProperties(paramAppInfo.getLapURL());
  }

  private static boolean showSSV2Dialog(Component paramComponent, AppInfo paramAppInfo, String paramString)
    throws ExitException
  {
    String str1 = ResourceManager.getString("deployment.ssv2.title");
    String str2 = ResourceManager.getString("deployment.ssv2.masthead");
    String str3 = ResourceManager.getString("deployment.ssv2.risk");
    String str4 = ResourceManager.getString("deployment.ssv2.moreText");
    URL localURL = null;
    try
    {
      localURL = new URL("http://java.com/access_old_java");
    }
    catch (Exception localException)
    {
    }
    String str5 = ResourceManager.getString("deployment.ssv2.choice");
    String str6 = ResourceManager.getString("deployment.ssv2.choice1");
    String str7 = ResourceManager.getString("deployment.ssv2.choice2", paramString);
    String str8 = ResourceManager.getString("deployment.ssv2.run.button");
    String str9 = ResourceManager.getString("common.cancel_btn");
    int i = ToolkitStore.getUI().showSSVDialog(paramComponent, paramAppInfo, str1, str2, str3, str4, localURL, str5, str6, str7, str8, str9);
    ToolkitStore.getUI();
    if (i == 0)
      return true;
    ToolkitStore.getUI();
    if (i == 2)
      return false;
    throw new ExitException(null, 0);
  }

  private static void showCannotUseDialog(Component paramComponent, AppInfo paramAppInfo, LocalApplicationProperties paramLocalApplicationProperties, String paramString, int paramInt)
    throws ExitException
  {
    if ((paramLocalApplicationProperties != null) && ("ssv.latest.allowed".equals(paramLocalApplicationProperties.get("ssv.version.allowed"))))
      return;
    String str1 = ResourceManager.getString("deployment.ssv2.nodl.title");
    String str2 = null;
    switch (paramInt)
    {
    case 0:
      str2 = "deployment.ssv2.nodl.blocked";
      break;
    case 3:
      str2 = "deployment.ssv2.nodl.fx";
      break;
    case 4:
      str2 = "deployment.ssv2.nodl.invalid";
      break;
    case 1:
    case 2:
    default:
      str2 = "deployment.ssv2.nodl.masthead";
    }
    String str3 = ResourceManager.getString(str2, paramString);
    String str4 = ResourceManager.getString("deployment.ssv2.nodl.button");
    String str5 = ResourceManager.getString("common.cancel_btn");
    String str6 = ResourceManager.getString("deployment.ssv2.moreText");
    URL localURL = null;
    try
    {
      localURL = new URL("http://java.com/access_old_java");
    }
    catch (Exception localException)
    {
    }
    ToolkitStore.getUI();
    int i = ToolkitStore.getUI().showMessageDialog(paramComponent, paramAppInfo, 2, str1, str3, null, null, str4, str5, null, localURL, str6, 0);
    ToolkitStore.getUI();
    if (i != 0)
      throw new ExitException(null, 0);
    if (paramLocalApplicationProperties != null)
    {
      paramLocalApplicationProperties.put("ssv.version.allowed", "ssv.latest.allowed");
      try
      {
        paramLocalApplicationProperties.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
    }
  }

  public static boolean useLatest(AppInfo paramAppInfo, String paramString)
    throws ExitException
  {
    int i = SecuritySettings.isSSVModeNever() ? 0 : 2;
    showCannotUseDialog(null, paramAppInfo, getLap(paramAppInfo), paramString, i);
    return true;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.ui.SecureStaticVersioning
 * JD-Core Version:    0.6.2
 */