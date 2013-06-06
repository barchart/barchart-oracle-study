package com.sun.deploy.security;

import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.Environment;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.SecuritySettings;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.SecurityBaseline;
import com.sun.deploy.util.SessionProperties;
import com.sun.deploy.util.SessionState;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SandboxSecurity
{
  private static final String EXPIRED_VERSION_KEY = "ssv.expired.allowed";
  private static final String UNTRUSTED_KEY = "ssv.untrusted.allowed";
  private static final String RUNLOCAL_KEY = "ssv.run.local.allowed";
  private static SessionProperties sessionProps = new SessionProperties("sandbox.properties");
  private static final String FILENAME = "sandbox.properties";
  private static Object blockLock = new Object();
  private static String blockKey = null;
  private static String title = getMessage("deployment.ssv.title");
  private static String masthead = getMessage("deployment.ssv.masthead");
  private static String run = getMessage("deployment.ssv.run");
  private static String cancel = getMessage("deployment.ssv.cancel");
  private static String securityInfoDescription = getMessage("sandbox.security.info.description");
  private static String securityInfoCanel = getMessage("sandbox.security.info.cancel");
  private static String securityInfoTrusted = getMessage("sandbox.security.info.trusted");

  public static void resetAcceptedVersion(LocalApplicationProperties paramLocalApplicationProperties)
  {
    if (paramLocalApplicationProperties != null)
    {
      paramLocalApplicationProperties.put("ssv.expired.allowed", null);
      paramLocalApplicationProperties.put("ssv.untrusted.allowed", null);
      paramLocalApplicationProperties.put("ssv.run.local.allowed", null);
      try
      {
        paramLocalApplicationProperties.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
      String str = TrustDecider.getLocString(paramLocalApplicationProperties.getLocation());
      if (str != null)
        sessionProps.remove(str);
    }
  }

  public static void isPermissionGranted(CodeSource paramCodeSource, AppInfo paramAppInfo, Preloader paramPreloader)
  {
    if ((paramCodeSource != null) && (paramCodeSource.getCertificates() != null))
    {
      if (Config.isJavaVersionAtLeast16())
      {
        checkSignedSandboxSecurity(paramCodeSource, paramAppInfo, paramPreloader);
      }
      else
      {
        Trace.println("Jar has Certs, treating sandbox app as unsigned due to running old JRE", TraceLevel.SECURITY);
        checkUnsignedSandboxSecurity(paramAppInfo);
      }
    }
    else
      checkUnsignedSandboxSecurity(paramAppInfo);
  }

  private static void checkSignedSandboxSecurity(CodeSource paramCodeSource, AppInfo paramAppInfo, Preloader paramPreloader)
  {
    boolean bool = true;
    int i = 0;
    try
    {
      TrustDecider.grabDeployLock();
      TrustDecider.ValidationState localValidationState = new TrustDecider.ValidationState();
      X509Certificate[] arrayOfX509Certificate = null;
      Certificate[] arrayOfCertificate = paramCodeSource.getCertificates();
      int j = 0;
      List localList1;
      try
      {
        TrustDecider.ensureBasicStoresLoaded();
        localList1 = TrustDecider.breakDownMultiSignerChains(arrayOfCertificate);
      }
      catch (Exception localException1)
      {
        BadCertificateDialog.showDialog(paramCodeSource, paramAppInfo, localException1);
        throw new SecurityException(localException1.getMessage(), localException1);
      }
      Iterator localIterator = localList1.iterator();
      if (localIterator.hasNext())
      {
        List localList2 = (List)localIterator.next();
        arrayOfX509Certificate = (X509Certificate[])localList2.toArray(new X509Certificate[0]);
        try
        {
          localValidationState = TrustDecider.getValidationState(arrayOfX509Certificate, paramCodeSource, 0, paramAppInfo, false, paramPreloader, true);
        }
        catch (Exception localException2)
        {
          BadCertificateDialog.showDialog(paramCodeSource, paramAppInfo, localException2);
          throw new SecurityException(localException2.getMessage(), localException2);
        }
        if (localValidationState.certValidity == 0)
        {
          i = 1;
          j = !localValidationState.rootCANotValid ? 1 : 0;
        }
      }
      if (localValidationState.trustDecision == 0L)
        bool = true;
      else
        bool = j != 0 ? SecuritySettings.isCaSignedNever() : SecuritySettings.isSelfSignedNever();
      if (bool)
        showBlockedDialog(paramAppInfo, "deployment.run.sandbox.signed.never.text");
      if (j == 0)
        if (SecurityBaseline.isExpired())
        {
          if (SecuritySettings.isSSVModeNever())
            showBlockedDialog(paramAppInfo, "deployment.ssv2.mode.never.text");
        }
        else if ((isLocalApp(paramAppInfo)) && (SecuritySettings.isRunLocalAppletsNever()))
          showBlockedDialog(paramAppInfo, "deployment.local.applet.never.text");
      if (localValidationState.trustDecision == 2L)
      {
        int k = showSandboxDialog(paramAppInfo, arrayOfX509Certificate, !localValidationState.timeValid, localValidationState.rootCANotValid);
        TrustDecider.recordSandboxAnswer(arrayOfX509Certificate, paramCodeSource, localValidationState, paramPreloader, k);
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      Trace.ignored(localInterruptedException);
      showBlockedDialog(paramAppInfo, "deployment.run.sandbox.signed.error");
    }
    finally
    {
      TrustDecider.releaseDeployLock();
    }
  }

  private static void checkUnsignedSandboxSecurity(AppInfo paramAppInfo)
  {
    if (sessionProps.getProperty(TrustDecider.getLocString(paramAppInfo.getLapURL())) != null)
      return;
    if (!Environment.isWebJava())
      return;
    synchronized (sessionProps)
    {
      if (sessionProps.getProperty(TrustDecider.getLocString(paramAppInfo.getLapURL())) != null)
        return;
      if (SecurityBaseline.isExpired())
        checkRunExpired(paramAppInfo);
      else if (isLocalApp(paramAppInfo))
        checkRunLocal(paramAppInfo);
      else
        checkRunUntrusted(paramAppInfo);
      sessionProps.setProperty(TrustDecider.getLocString(paramAppInfo.getLapURL()), "true");
    }
  }

  private static void checkRunUntrusted(AppInfo paramAppInfo)
  {
    if (SecuritySettings.isRunUntrustedNever())
      showBlockedDialog(paramAppInfo, "deployment.run.untrusted.never.text");
    String str = JREInfo.getLatest();
    LocalApplicationProperties localLocalApplicationProperties = getLap(paramAppInfo);
    if ((str != null) && (localLocalApplicationProperties != null) && (str.equals(localLocalApplicationProperties.get("ssv.untrusted.allowed"))))
      return;
    if ((showUntrustedDialog(paramAppInfo)) && (localLocalApplicationProperties != null) && (str != null))
    {
      localLocalApplicationProperties.put("ssv.untrusted.allowed", str);
      try
      {
        localLocalApplicationProperties.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
    }
  }

  private static void checkRunExpired(AppInfo paramAppInfo)
  {
    if (SecuritySettings.isSSVModeNever())
      showBlockedDialog(paramAppInfo, "deployment.ssv2.mode.never.text");
    String str = JREInfo.getLatest();
    LocalApplicationProperties localLocalApplicationProperties = getLap(paramAppInfo);
    if ((str != null) && (localLocalApplicationProperties != null) && (str.equals(localLocalApplicationProperties.get("ssv.expired.allowed"))))
      return;
    if ((showExpiredDialog(paramAppInfo, str)) && (localLocalApplicationProperties != null) && (str != null))
    {
      localLocalApplicationProperties.put("ssv.expired.allowed", str);
      try
      {
        localLocalApplicationProperties.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
    }
  }

  private static void checkRunLocal(AppInfo paramAppInfo)
  {
    if (SecuritySettings.isRunLocalAppletsNever())
      showBlockedDialog(paramAppInfo, "deployment.local.applet.never.text");
    if (SecuritySettings.isRunUntrustedNever())
      showBlockedDialog(paramAppInfo, "deployment.run.untrusted.never.text");
    String str = JREInfo.getLatest();
    LocalApplicationProperties localLocalApplicationProperties = getLap(paramAppInfo);
    if ((str != null) && (localLocalApplicationProperties != null) && (str.equals(localLocalApplicationProperties.get("ssv.run.local.allowed"))))
      return;
    if ((showUntrustedDialog(paramAppInfo)) && (localLocalApplicationProperties != null) && (str != null))
    {
      localLocalApplicationProperties.put("ssv.run.local.allowed", str);
      try
      {
        localLocalApplicationProperties.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
    }
  }

  private static boolean isLocalApp(AppInfo paramAppInfo)
  {
    URL localURL = paramAppInfo.getFrom();
    return (localURL != null) && (localURL.getProtocol().equals("file"));
  }

  private static LocalApplicationProperties getLap(AppInfo paramAppInfo)
  {
    return Cache.getLocalApplicationProperties(paramAppInfo.getLapURL());
  }

  private static boolean showUntrustedDialog(AppInfo paramAppInfo)
  {
    String str1 = "deployment.ssv.title";
    String str2 = "deployment.ssv.masthead";
    String str3 = "deployment.ssv.untrusted.main";
    String str4 = "deployment.ssv.localapp.main";
    String str5 = "deployment.ssv.location";
    Object localObject = null;
    String str6 = "deployment.ssv.prompt";
    String str7 = null;
    String str8 = null;
    if (SecuritySettings.isRunUntrustedMultiClick())
    {
      str7 = "deployment.ssv.multi.prompt";
      str8 = "deployment.ssv.multi.text";
    }
    String str9 = "deployment.ssv.always";
    String str10 = "deployment.ssv.run";
    String str11 = null;
    String str12 = "deployment.ssv.cancel";
    URL localURL = null;
    String str13 = isLocalApp(paramAppInfo) ? str4 : str3;
    ToolkitStore.getUI();
    int i = ToolkitStore.getUI().showSSV3Dialog(null, paramAppInfo, 2, str1, str2, str13, str5, str6, str7, str8, str10, str11, str12, str9, localURL);
    ToolkitStore.getUI();
    if (i == 2)
      return true;
    ToolkitStore.getUI();
    if (i == 0)
      return false;
    throw new SecurityException("User declined to run unsigned sandbox app", null);
  }

  private static boolean showExpiredDialog(AppInfo paramAppInfo, String paramString)
  {
    String str1 = "deployment.ssv.title";
    String str2 = "deployment.ssv.masthead";
    String str3 = isLocalApp(paramAppInfo) ? "deployment.ssv.expired.localapp.main" : "deployment.ssv.expired.main";
    String str4 = "deployment.ssv.location";
    String str5 = "deployment.ssv.expired.recommend";
    String str6 = "deployment.ssv.update.prompt";
    String str7 = null;
    String str8 = null;
    if (SecuritySettings.isSSVModeMultiClick())
    {
      str7 = "deployment.ssv.multi.prompt";
      str8 = "deployment.ssv.multi.text";
    }
    String str9 = "deployment.ssv.always";
    String str10 = "deployment.ssv.run";
    String str11 = "deployment.ssv.update";
    String str12 = "deployment.ssv.cancel";
    URL localURL = null;
    try
    {
      localURL = new URL("http://java.com/download");
    }
    catch (Exception localException)
    {
    }
    ToolkitStore.getUI();
    int i = ToolkitStore.getUI().showSSV3Dialog(null, paramAppInfo, 2, str1, str2, str3, str4, str6, str7, str8, str10, str11, str12, str9, localURL);
    ToolkitStore.getUI();
    if (i == 2)
      return true;
    ToolkitStore.getUI();
    if (i == 0)
      return false;
    throw new SecurityException("User declined to run on insecure or expired JRE", null);
  }

  private static void showBlockedDialog(AppInfo paramAppInfo, String paramString)
  {
    String str1 = ResourceManager.getString(paramString);
    Trace.println(str1, TraceLevel.BASIC);
    synchronized (blockLock)
    {
      String str2 = null;
      String str3 = ResourceManager.getString("deployment.blocked.title");
      String str4 = ResourceManager.getString("deployment.blocked.masthead");
      if (str1 == null)
        str1 = ResourceManager.getString("deployment.blocked.text");
      String str5 = ResourceManager.getString("common.ok_btn");
      String str6 = ResourceManager.getString("common.detail.button");
      URL localURL = paramAppInfo.getLapURL();
      String str7 = localURL == null ? null : localURL.toString();
      if ((str7 == null) || (!str7.equals(blockKey)))
        ToolkitStore.getUI().showPublisherInfo(null, paramAppInfo, str3, str4, str1, str5, str6, str2);
      blockKey = str7;
    }
    throw new SecurityException(str1, null);
  }

  private static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  public static int showSandboxDialog(AppInfo paramAppInfo, X509Certificate[] paramArrayOfX509Certificate, boolean paramBoolean1, boolean paramBoolean2)
  {
    String[] arrayOfString = null;
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(securityInfoDescription);
    localArrayList.add(securityInfoCanel);
    String str = CertUtils.extractSubjectAliasName(paramArrayOfX509Certificate[0]);
    if (paramBoolean2)
    {
      arrayOfString = new String[] { getMessage("sandbox.security.info.selfsigned.risk") };
      localArrayList.add(getMessage("sandbox.security.info.publisher.unknown"));
      localArrayList.add(getMessage("sandbox.security.info.selfsigned.state"));
      str = getMessage("security.dialog.notverified.subject").toUpperCase();
    }
    else if (paramBoolean1)
    {
      arrayOfString = new String[] { getMessage("sandbox.security.dialog.expired.signed.label") };
      localArrayList.add(securityInfoTrusted);
      localArrayList.add(getMessage("sandbox.security.info.expired.state"));
    }
    else
    {
      localArrayList.add(securityInfoTrusted);
      localArrayList.add(getMessage("sandbox.security.info.trusted.state"));
    }
    return ToolkitStore.getUI().showSandboxSecurityDialog(paramAppInfo, title, masthead, str, paramAppInfo.getFrom(), true, (!paramBoolean1) && (!paramBoolean2), run, cancel, arrayOfString, (String[])localArrayList.toArray(new String[localArrayList.size()]), true, paramArrayOfX509Certificate, 0, paramArrayOfX509Certificate.length, paramBoolean2);
  }

  static
  {
    SessionState.register(sessionProps);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.SandboxSecurity
 * JD-Core Version:    0.6.2
 */