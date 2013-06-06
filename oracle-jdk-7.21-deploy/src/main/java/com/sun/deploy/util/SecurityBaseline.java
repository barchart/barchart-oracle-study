package com.sun.deploy.util;

import com.sun.deploy.config.BuiltInProperties;
import com.sun.deploy.config.ClientConfig;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.Platform;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class SecurityBaseline
{
  private static String baseline_131 = "1.3.1_21";
  private static String baseline_142 = "1.4.2_43";
  private static String baseline_150 = "1.5.0_45";
  private static String baseline_160 = "1.6.0_45";
  private static String baseline_170 = "1.7.0_21";
  private static String baseline_180 = "1.8.0";
  private static boolean baselines_initialized = false;
  private static final boolean DEBUG = Config.getBooleanProperty("deployment.baseline.debug");
  private static final long UPDATE_INTERVAL = DEBUG ? 10000 : 86400000;
  private static final long THREAD_SLEEP_INTERVAL = DEBUG ? 1000 : 30000;
  private static final String BASELINE_FILENAME = "baseline.versions";
  private static final String UPDATE_TIMESTAMP = "update.timestamp";
  private static final File securityDir = new File(Config.getUserHome(), "security");
  private static final File baselineFile = new File(securityDir, "baseline.versions");
  private static final File blacklistFile = new File(Config.getDynamicBlacklistFile());
  private static final File blacklistCertsFile = new File(Config.getDynamicBlacklistCertsFile());
  private static final File updateTimestampFile = new File(securityDir, "update.timestamp");

  private static void initialize_baselines()
  {
    if ((!baselines_initialized) && (baselineFile.exists()))
    {
      BufferedReader localBufferedReader = null;
      try
      {
        long l = System.currentTimeMillis();
        localBufferedReader = new BufferedReader(new FileReader(baselineFile));
        int i = 0;
        while (i == 0)
        {
          String str = localBufferedReader.readLine();
          if (str == null)
          {
            i = 1;
          }
          else if (str.startsWith("1.8"))
          {
            baseline_180 = str;
            Platform.get().cacheSecurityBaseline("1.8.0", baseline_180);
          }
          else if (str.startsWith("1.7"))
          {
            baseline_170 = str;
            Platform.get().cacheSecurityBaseline("1.7.0", baseline_170);
          }
          else if (str.startsWith("1.6"))
          {
            baseline_160 = str;
            Platform.get().cacheSecurityBaseline("1.6.0", baseline_160);
          }
          else if (str.startsWith("1.5"))
          {
            baseline_150 = str;
            Platform.get().cacheSecurityBaseline("1.5.0", baseline_150);
          }
          else if (str.startsWith("1.4.2"))
          {
            baseline_142 = str;
            Platform.get().cacheSecurityBaseline("1.4.2", baseline_142);
          }
          else if (str.startsWith("1.3.1"))
          {
            baseline_131 = str;
            Platform.get().cacheSecurityBaseline("1.3.1", baseline_131);
          }
        }
        if (DEBUG)
          Trace.println("It took " + (System.currentTimeMillis() - l) + " Ms. to read baseline file", TraceLevel.BASIC);
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
      finally
      {
        try
        {
          if (localBufferedReader != null)
            localBufferedReader.close();
        }
        catch (IOException localIOException)
        {
          Trace.ignored(localIOException);
        }
      }
    }
    baselines_initialized = true;
  }

  public static String getBaselineVersion(String paramString)
  {
    if (!baselines_initialized)
      initialize_baselines();
    String str;
    if (paramString.startsWith("1.8"))
      str = baseline_180;
    else if (paramString.startsWith("1.7"))
      str = baseline_170;
    else if (paramString.startsWith("1.6"))
      str = baseline_160;
    else if (paramString.startsWith("1.5"))
      str = baseline_150;
    else if (paramString.startsWith("1.4.2"))
      str = baseline_142;
    else if (paramString.startsWith("1.3.1"))
      str = baseline_131;
    else
      str = "1.7.0_21";
    if (DEBUG)
      Trace.println("for requested version: " + paramString + "baseline version is: " + str, TraceLevel.SECURITY);
    return str;
  }

  public static boolean satisfiesSecurityBaseline(String paramString)
  {
    VersionID localVersionID1 = new VersionID(paramString);
    VersionID localVersionID2 = new VersionID(getBaselineVersion(paramString));
    if (localVersionID1.isGreaterThanOrEqual(localVersionID2))
      return true;
    return localVersionID1.equals(JREInfo.getLatestVersion(true));
  }

  public static boolean satisfiesBaselineStrictly(String paramString)
  {
    int i = paramString.indexOf("-");
    String str = i > 0 ? paramString.substring(0, i) : paramString;
    VersionID localVersionID1 = new VersionID(str);
    VersionID localVersionID2 = new VersionID(getBaselineVersion(paramString));
    boolean bool = localVersionID1.isGreaterThanOrEqual(localVersionID2);
    if (DEBUG)
      Trace.println("strictly satisfied=" + bool + "  for version: " + paramString + "  baseline is: " + getBaselineVersion(paramString));
    return bool;
  }

  public static String getDeployVersion()
  {
    return "10.21.2.11";
  }

  public static String getDeployNoBuildVersion()
  {
    return "10.21.2";
  }

  public static String getDeployNoDotVersion()
  {
    return "10212";
  }

  public static String getCurrentVersion()
  {
    return "1.7.0_21";
  }

  public static String getCurrentNoDotVersion()
  {
    return "170";
  }

  private static synchronized void checkForUpdates(boolean paramBoolean)
  {
    Thread localThread = new Thread(new Runnable()
    {
      public void run()
      {
        if (SecurityBaseline.checkForUpdate(Config.getStringProperty("deployment.baseline.url"), SecurityBaseline.baselineFile))
        {
          SecurityBaseline.access$200();
          if (SecurityBaseline.isExpired())
          {
            Config localConfig = Config.get();
            if ((localConfig instanceof ClientConfig))
              ((ClientConfig)localConfig).storeConfig();
          }
        }
      }
    });
    localThread.setDaemon(!paramBoolean);
    localThread.start();
    localThread = new Thread(new Runnable()
    {
      public void run()
      {
        SecurityBaseline.checkForUpdate(Config.getStringProperty("deployment.blacklist.url"), SecurityBaseline.blacklistFile);
      }
    });
    localThread.setDaemon(!paramBoolean);
    localThread.start();
    localThread = new Thread(new Runnable()
    {
      public void run()
      {
        SecurityBaseline.checkForUpdate(Config.getStringProperty("deployment.blacklisted.certs.url"), SecurityBaseline.blacklistCertsFile);
      }
    });
    localThread.setDaemon(!paramBoolean);
    localThread.start();
  }

  private static boolean checkForUpdate(String paramString, File paramFile)
  {
    if ((paramString != null) && (paramString.length() > 0))
    {
      long l = 0L;
      if (paramFile.exists())
        l = paramFile.lastModified();
      InputStream localInputStream = null;
      FileOutputStream localFileOutputStream = null;
      try
      {
        Trace.println("Checking for update at: " + paramString, TraceLevel.NETWORK);
        URL localURL = new URL(paramString);
        URLConnection localURLConnection = localURL.openConnection();
        localURLConnection.setUseCaches(false);
        if (localURLConnection.getLastModified() >= l)
        {
          Trace.println("Updating file at: " + paramFile + " from url: " + paramString, TraceLevel.NETWORK);
          localInputStream = localURLConnection.getInputStream();
          localFileOutputStream = new FileOutputStream(paramFile);
          byte[] arrayOfByte = new byte[8192];
          int i;
          while ((i = localInputStream.read(arrayOfByte)) != -1)
            localFileOutputStream.write(arrayOfByte, 0, i);
          boolean bool = true;
          return bool;
        }
      }
      catch (Exception localException1)
      {
        Trace.ignored(localException1);
      }
      finally
      {
        if (localFileOutputStream != null)
          try
          {
            localFileOutputStream.close();
          }
          catch (Exception localException2)
          {
            Trace.ignored(localException2);
          }
        if (localInputStream != null)
          try
          {
            localInputStream.close();
          }
          catch (Exception localException3)
          {
            Trace.ignored(localException3);
          }
      }
    }
    return false;
  }

  private static long getLastChecked()
  {
    if (updateTimestampFile.exists())
      return updateTimestampFile.lastModified();
    return 0L;
  }

  private static void setLastChecked(long paramLong)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(updateTimestampFile);
      localFileOutputStream.write(46);
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
    }
  }

  public static void backgroundUpdate()
  {
    Thread localThread = new Thread(new Runnable()
    {
      public void run()
      {
        long l = new Date().getTime();
        if (l > SecurityBaseline.access$500() + SecurityBaseline.UPDATE_INTERVAL)
          try
          {
            Thread.sleep(SecurityBaseline.THREAD_SLEEP_INTERVAL);
            SecurityBaseline.checkForUpdates(false);
            SecurityBaseline.setLastChecked(l);
          }
          catch (Exception localException)
          {
            Trace.ignored(localException);
          }
        if (SecurityBaseline.DEBUG)
          Trace.println("Baseline/Blacklist thread exiting time: " + (new Date().getTime() - l), TraceLevel.BASIC);
      }
    });
    localThread.setDaemon(true);
    localThread.start();
  }

  public static void forceBaselineUpdate()
  {
    checkForUpdates(true);
    initialize_baselines();
    setLastChecked(new Date().getTime());
  }

  public static boolean isExpired()
  {
    if (Config.isJavaVersionAtLeast15())
    {
      if (System.getenv("JRE_NOTEXPIRED") != null)
        return false;
      if (System.getenv("JRE_EXPIRED") != null)
        return true;
    }
    Date localDate = new Date(BuiltInProperties.expirationTime);
    if (localDate.before(new Date()))
      return true;
    return !satisfiesBaselineStrictly(JREInfo.getLatest());
  }

  static
  {
    securityDir.mkdirs();
    backgroundUpdate();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.SecurityBaseline
 * JD-Core Version:    0.6.2
 */