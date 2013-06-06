package com.sun.deploy.config;

import com.sun.deploy.Environment;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.PerfLogger;
import com.sun.deploy.util.SecurityBaseline;
import com.sun.deploy.util.SyncFileAccess;
import com.sun.deploy.util.SyncFileAccess.FileInputStreamLock;
import com.sun.deploy.util.SyncFileAccess.FileOutputStreamLock;
import com.sun.deploy.util.VersionID;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.net.ssl.HttpsURLConnection;
import sun.net.www.protocol.https.Handler;

public class ClientConfig extends Config
{
  private boolean _configOK = false;
  private Properties _systemProps;
  private Properties _internalProps;
  private Properties _lockedProps;
  protected Properties _changedProps;
  private boolean _userConfigStore;
  protected boolean _dirty;
  private File _userConfigFile;
  private SyncFileAccess _userConfigFileSyncAccess;
  private String _enterprizeConfig = null;
  private long _lastChanged;
  private long _sysPropsModified = 0L;
  private static final String[] UNCACHABLE_PROPS = { "deployment.javaws.viewer.bounds" };
  private String initVersionOverride = null;

  public ClientConfig()
  {
    PerfLogger.setTime("Start Client Config Constructor");
    init(null, null);
    PerfLogger.setTime("End Client Config Constructor");
  }

  public Object setProperty(String paramString1, String paramString2)
  {
    if (isDiskNewer())
      refreshIfNeeded();
    String str = super.getProperty(paramString1);
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      if ((containsKey(paramString1)) && (!isInternalProp(paramString1)))
        this._dirty = true;
      remove(paramString1);
      this._changedProps.remove(paramString1);
    }
    else if (!paramString2.equals(str))
    {
      if (!isInternalProp(paramString1))
        this._dirty = true;
      super.setProperty(paramString1, paramString2);
      this._changedProps.setProperty(paramString1, paramString2);
    }
    return str;
  }

  public boolean init(String paramString1, String paramString2)
  {
    PerfLogger.setTime("Start ClientConfig.init");
    boolean bool = true;
    String str1 = SecurityBaseline.getDeployNoBuildVersion();
    this._systemProps = new Properties();
    long l1 = 0L;
    this._internalProps = getInternalProps();
    this._changedProps = new Properties();
    this._userConfigStore = false;
    if (paramString1 == null)
    {
      this._userConfigFile = new File(getUserPropertiesFile());
      this._userConfigStore = true;
    }
    else if (!paramString1.equals(""))
    {
      this._userConfigFile = new File(paramString1);
      if (!this._userConfigFile.exists())
      {
        this._userConfigFile = null;
        Trace.println("Test user config file given: " + paramString1 + " is non existant", TraceLevel.BASIC);
      }
    }
    else
    {
      this._userConfigFile = null;
    }
    if (this._userConfigFile != null)
      this._userConfigFileSyncAccess = new SyncFileAccess(this._userConfigFile);
    PerfLogger.setTime("  - Start load system properties");
    String str2;
    Object localObject4;
    if (paramString2 == null)
    {
      int i = 0;
      localObject2 = new File(getSystemHome() + File.separator + "deployment.config");
      if (!((File)localObject2).exists())
        localObject2 = new File(Environment.getDeploymentHomePath() + File.separator + "lib" + File.separator + "deployment.config");
      if (!((File)localObject2).exists())
        localObject2 = new File(Environment.getJavaHome() + File.separator + "lib" + File.separator + "deployment.config");
      if (((File)localObject2).exists())
      {
        localObject3 = loadPropertiesFile(new Properties(), (File)localObject2);
        if (localObject3 != null)
        {
          str2 = ((Properties)localObject3).getProperty("deployment.system.config.mandatory");
          i = (str2 != null) && (!str2.equalsIgnoreCase("false")) ? 1 : 0;
          localObject4 = ((Properties)localObject3).getProperty("deployment.system.config");
          this._enterprizeConfig = ((String)localObject4);
        }
      }
      if (!initializeSysProps(this._enterprizeConfig, this._systemProps))
        bool = i == 0;
    }
    else
    {
      bool = true;
      if (!paramString2.equals(""))
      {
        localObject1 = new File(paramString2);
        if (((File)localObject1).exists())
          loadPropertiesFile(this._systemProps, (File)localObject1);
        else
          Trace.println("Test system config file given: " + paramString2 + " is non existant", TraceLevel.BASIC);
      }
    }
    PerfLogger.setTime("  - End load system properties");
    Object localObject1 = refreshProperties();
    PerfLogger.setTime("  - End refreshProperties");
    versionUpdateCheck();
    PerfLogger.setTime("  - end VersionUpdateCheck");
    Object localObject2 = System.getProperties();
    PerfLogger.setTime("  - end getting all system properties");
    Object localObject3 = propertyNames();
    while (((Enumeration)localObject3).hasMoreElements())
    {
      str2 = (String)((Enumeration)localObject3).nextElement();
      localObject4 = getProperty(str2);
      if (localObject4 != null)
        ((Properties)localObject2).put(str2, replaceVariables((String)localObject4));
    }
    System.setProperties((Properties)localObject2);
    PerfLogger.setTime("  - end setting all system properties");
    setPolicyFiles();
    String str4;
    if (this._userConfigFile != null)
      for (int j = 0; j < PROXY_KEYS.length; j++)
      {
        localObject4 = PROXY_KEYS[j];
        str4 = "active." + (String)localObject4;
        setProperty(str4, getProperty((String)localObject4));
      }
    String str3 = getProperty("deployment.webjava.enabled");
    if ((str3 != null) && (!Boolean.valueOf(str3).booleanValue()) && (Environment.isWebJava()))
      bool = false;
    if (this._systemProps != null)
    {
      localObject4 = this._systemProps.keys();
      while (((Enumeration)localObject4).hasMoreElements())
      {
        str4 = (String)((Enumeration)localObject4).nextElement();
        String str5 = this._systemProps.getProperty(str4);
        if ((str5 != null) && (!str5.equals(((Properties)localObject1).get(str4))))
        {
          this._dirty = true;
          break;
        }
      }
    }
    long l2 = 0L;
    try
    {
      l2 = Long.parseLong(getProperty("deployment.modified.timestamp"));
    }
    catch (Exception localException)
    {
    }
    if ((this._sysPropsModified > l2) && (this._systemProps.containsKey("deployment.webjava.enabled")))
      setProperty("deployment.webjava.enabled", this._systemProps.getProperty("deployment.webjava.enabled"));
    storeIfNeeded();
    PerfLogger.setTime("End Config.init");
    this._configOK = bool;
    return bool;
  }

  private Properties refreshProperties()
  {
    Properties localProperties1 = new Properties();
    Properties localProperties2 = new Properties();
    Properties localProperties3 = new Properties();
    Properties localProperties4 = new Properties();
    Properties localProperties5 = new Properties();
    this._lockedProps = new Properties();
    clear();
    putAll(getDefaults());
    if (this._userConfigFile == null)
    {
      localProperties1.clear();
    }
    else if (this._userConfigFile.exists())
    {
      PerfLogger.setTime("Start loadPropertiesFile");
      loadPropertiesFile(localProperties1, this._userConfigFile);
      PerfLogger.setTime("End loadPropertiesFile");
      this._lastChanged = this._userConfigFile.lastModified();
    }
    else
    {
      this._lastChanged = System.currentTimeMillis();
    }
    Enumeration localEnumeration = this._systemProps.keys();
    String str1;
    String str2;
    while (localEnumeration.hasMoreElements())
    {
      str1 = (String)localEnumeration.nextElement();
      str2 = this._systemProps.getProperty(str1);
      if (str1.startsWith("deployment.javaws.jre."))
      {
        localProperties3.setProperty(str1, str2);
      }
      else if (str1.endsWith(".locked"))
      {
        int i = str1.length() - ".locked".length();
        str1 = str1.substring(0, i);
        String str3 = this._systemProps.getProperty(str1);
        if (str3 != null)
          lockProperty(str1, str3);
      }
      else if (str1.startsWith("deployment."))
      {
        setProperty(str1, str2);
      }
    }
    localEnumeration = localProperties1.keys();
    while (localEnumeration.hasMoreElements())
    {
      str1 = (String)localEnumeration.nextElement();
      str2 = localProperties1.getProperty(str1);
      if (str1.startsWith("deployment.javaws.jre."))
        localProperties2.setProperty(str1, str2);
      else if (str1.startsWith("deployment.javapi.jre."))
        localProperties4.setProperty(str1, str2);
      else if (str1.startsWith("deployment.javapi.jdk."))
        localProperties5.setProperty(str1, str2);
      else if ((str1.startsWith("deployment.")) || (str1.startsWith("javaplugin")))
        setProperty(str1, str2);
    }
    PerfLogger.setTime(" - Start JREInfo.initialize");
    JREInfo.initialize(localProperties3, localProperties2);
    PerfLogger.setTime(" - End JREInfo.initialize");
    JREInfo.importJpiEntries(localProperties4);
    PerfLogger.setTime(" - end importing JPI entries");
    getInternalPropValues();
    localEnumeration = this._lockedProps.keys();
    while (localEnumeration.hasMoreElements())
    {
      str1 = (String)localEnumeration.nextElement();
      setProperty(str1, this._lockedProps.getProperty(str1));
    }
    this._dirty = false;
    this._changedProps.clear();
    return localProperties1;
  }

  public void getInternalPropValues()
  {
    if (getOSName().equals("Windows"))
    {
      if ((Platform.get().isPlatformWindowsVista()) || (!Platform.get().hasAdminPrivileges()))
        lockProperty("deployment.browser.vm.iexplorer", new Boolean(true).toString());
      else
        unlockProperty("deployment.browser.vm.iexplorer");
      String str = getProperty("deployment.browser.vm.mozilla");
      if (!Platform.get().hasAdminPrivileges())
        lockProperty("deployment.browser.vm.mozilla", str);
      else
        unlockProperty("deployment.browser.vm.mozilla");
      Collection localCollection = Platform.get().getInstalledJfxRuntimes(true);
      if (localCollection.isEmpty())
        lockProperty("deployment.javafx.mode.enabled", getProperty("deployment.javafx.mode.enabled"));
      else
        unlockProperty("deployment.javafx.mode.enabled");
    }
    if (this._userConfigFile != null)
    {
      getJavaPlugin();
      getJqs();
    }
  }

  private void setPolicyFiles()
  {
    String str1 = getProperty("deployment.system.security.policy");
    if (str1 != null)
      str1 = replaceVariables(str1).trim();
    String str2 = getProperty("deployment.user.security.policy");
    if (str2 != null)
      str2 = replaceVariables(str2).trim();
    if ((str1 != null) || (str2 != null))
    {
      int i = 1;
      String str3 = null;
      while ((str3 = Security.getProperty("policy.url." + i)) != null)
        i++;
      if (str1 != null)
      {
        Security.setProperty("policy.url." + i, str1);
        i++;
      }
      if (str2 != null)
      {
        Security.setProperty("policy.url." + i, str2);
        i++;
      }
    }
  }

  private boolean tryDownloading(String paramString, Properties paramProperties)
  {
    URLConnection localURLConnection = null;
    try
    {
      URL localURL = null;
      if (paramString.toLowerCase().startsWith("https:"))
        localURL = new URL(null, paramString, new Handler());
      else
        localURL = new URL(paramString);
      localURLConnection = localURL.openConnection();
      if ((localURLConnection instanceof HttpsURLConnection))
        ConfigTrustManager.resetHttpsFactory((HttpsURLConnection)localURLConnection);
      InputStream localInputStream = localURLConnection.getInputStream();
      paramProperties.load(localInputStream);
      boolean bool2 = true;
      return bool2;
    }
    catch (Exception localException)
    {
      Trace.ignoredException(localException);
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      if ((localURLConnection != null) && ((localURLConnection instanceof HttpsURLConnection)))
        ((HttpsURLConnection)localURLConnection).disconnect();
    }
  }

  private boolean initializeSysProps(String paramString, Properties paramProperties)
  {
    boolean bool = true;
    if (paramString != null)
      bool = tryDownloading(paramString, paramProperties);
    File localFile = new File(getSystemHome() + File.separator + getPropertiesFilename());
    if (!localFile.exists())
      localFile = new File(Environment.getJavaHome() + File.separator + "lib" + File.separator + getPropertiesFilename());
    if (localFile.exists())
    {
      this._sysPropsModified = localFile.lastModified();
      loadPropertiesFile(paramProperties, localFile);
    }
    return bool;
  }

  public void refreshUnchangedProps()
  {
    Properties localProperties = this._changedProps;
    refreshProperties();
    Enumeration localEnumeration = localProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      setProperty(str, localProperties.getProperty(str));
    }
  }

  public boolean isDiskNewer()
  {
    return (this._userConfigFile != null) && (this._userConfigFile.exists()) && (this._userConfigFile.lastModified() > this._lastChanged);
  }

  private static boolean isPropertyCachable(String paramString)
  {
    for (int i = 0; i < UNCACHABLE_PROPS.length; i++)
      if (UNCACHABLE_PROPS[i].equals(paramString))
        return false;
    return true;
  }

  private static boolean isValidExpiredDecisionKey(String paramString)
  {
    if (paramString.equals("deployment.expiration.decision." + SecurityBaseline.getDeployNoBuildVersion()))
      return true;
    if (paramString.equals("deployment.expiration.decision.timestamp." + SecurityBaseline.getDeployNoBuildVersion()))
      return true;
    if (paramString.equals("deployment.expiration.decision.suppression." + SecurityBaseline.getDeployNoBuildVersion()))
      return true;
    return paramString.equals("deployment.expiration.decision.ttl." + SecurityBaseline.getDeployNoBuildVersion());
  }

  public void storeConfig()
  {
    if (!this._userConfigStore)
      return;
    if ((this._dirty) && (isDiskNewer()))
    {
      localProperties1 = this._changedProps;
      refreshProperties();
      localObject1 = localProperties1.keys();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (String)((Enumeration)localObject1).nextElement();
        setProperty((String)localObject2, localProperties1.getProperty((String)localObject2));
      }
    }
    Properties localProperties1 = Platform.get().getPendingConfigProperties();
    if (localProperties1 != null)
    {
      localObject1 = "deployment.webjava.enabled";
      localObject2 = localProperties1.getProperty((String)localObject1);
      if (localObject2 != null)
        setProperty((String)localObject1, (String)localObject2);
      localObject1 = "deployment.expiration.decision." + SecurityBaseline.getDeployNoBuildVersion();
      localObject2 = localProperties1.getProperty((String)localObject1);
      if (localObject2 != null)
        setProperty((String)localObject1, (String)localObject2);
      localObject1 = "deployment.expiration.decision.timestamp." + SecurityBaseline.getDeployNoBuildVersion();
      localObject2 = localProperties1.getProperty((String)localObject1);
      if (localObject2 != null)
        setProperty((String)localObject1, (String)localObject2);
      localObject1 = "deployment.expiration.decision.suppression." + SecurityBaseline.getDeployNoBuildVersion();
      localObject2 = localProperties1.getProperty((String)localObject1);
      if (localObject2 != null)
        setProperty((String)localObject1, (String)localObject2);
    }
    Object localObject1 = new Properties();
    Object localObject2 = new Properties();
    Properties localProperties2 = new Properties();
    Enumeration localEnumeration = keys();
    Object localObject4;
    while (localEnumeration.hasMoreElements())
    {
      localObject3 = (String)localEnumeration.nextElement();
      String str1 = getProperty((String)localObject3);
      String str2 = this._systemProps.getProperty((String)localObject3);
      localObject4 = getDefaults().getProperty((String)localObject3);
      if ((!isInternalProp((String)localObject3)) && ((!((String)localObject3).startsWith("deployment.expiration.decision.")) || (isValidExpiredDecisionKey((String)localObject3))))
      {
        if ((!isPropertyLocked((String)localObject3)) && ((str1 == null) || ((str2 != null) && (!str1.equals(str2))) || ((str2 == null) && (!str1.equals(localObject4)))))
          ((Properties)localObject1).setProperty((String)localObject3, str1);
        if (((str1 == null) || (!str1.equals(localObject4))) && (isPropertyCachable((String)localObject3)))
          localProperties2.setProperty((String)localObject3, str1);
      }
    }
    Object localObject3 = JREInfo.getAll();
    int i = 0;
    for (int j = 0; j < localObject3.length; j++)
      if ((!localObject3[j].isSystemJRE()) && (!Environment.isForcedJreRoot(localObject3[j].getPath())))
      {
        if (localObject3[j].getPlatform() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".platform", localObject3[j].getPlatform());
        if (localObject3[j].getProduct() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".product", localObject3[j].getProduct());
        if (localObject3[j].getLocation() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".location", localObject3[j].getLocation());
        if (localObject3[j].getPath() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".path", localObject3[j].getPath());
        if (localObject3[j].getVmArgs() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".args", localObject3[j].getVmArgs());
        if (localObject3[j].getOSArch() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".osarch", localObject3[j].getOSArch());
        if (localObject3[j].getOSName() != null)
          ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".osname", localObject3[j].getOSName());
        ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".enabled", booleanToString(localObject3[j].isEnabled()));
        ((Properties)localObject2).setProperty("deployment.javaws.jre." + i + ".registered", booleanToString(localObject3[j].isRegistered()));
        i++;
      }
    this._userConfigFile.getParentFile().mkdirs();
    try
    {
      ((Properties)localObject1).setProperty("deployment.modified.timestamp", new Long(System.currentTimeMillis()).toString());
      SyncFileAccess.FileOutputStreamLock localFileOutputStreamLock = this._userConfigFileSyncAccess.openLockFileOutputStream(false, 2000, false);
      localObject4 = localFileOutputStreamLock != null ? localFileOutputStreamLock.getFileOutputStream() : new FileOutputStream(this._userConfigFile);
      try
      {
        if (localObject4 != null)
          try
          {
            ((Properties)localObject1).store((OutputStream)localObject4, getPropertiesFilename());
            ((Properties)localObject2).store((OutputStream)localObject4, "Java Deployment jre's");
          }
          catch (IOException localIOException2)
          {
            Trace.println("Exception: " + localIOException2, TraceLevel.BASIC);
          }
      }
      finally
      {
        if (localObject4 != null)
        {
          ((FileOutputStream)localObject4).flush();
          try
          {
            ((FileOutputStream)localObject4).close();
          }
          catch (IOException localIOException4)
          {
          }
        }
        if (localFileOutputStreamLock != null)
          localFileOutputStreamLock.release();
      }
    }
    catch (IOException localIOException1)
    {
    }
    this._dirty = false;
    this._changedProps.clear();
    if (SecurityBaseline.isExpired())
      localProperties2.setProperty("deployment.expired.version", SecurityBaseline.getDeployNoBuildVersion());
    Platform.get().cacheCurrentConfig(localProperties2);
    this._lastChanged = (this._userConfigFile.exists() ? this._userConfigFile.lastModified() : System.currentTimeMillis());
  }

  private Properties loadPropertiesFile(Properties paramProperties, File paramFile)
  {
    Object localObject1 = null;
    FileInputStream localFileInputStream = null;
    try
    {
      localObject1 = paramFile.equals(this._userConfigFile) ? this._userConfigFileSyncAccess.openLockFileInputStream(1000, false) : null;
      localFileInputStream = localObject1 != null ? localObject1.getFileInputStream() : new FileInputStream(paramFile);
      paramProperties.load(localFileInputStream);
      Properties localProperties1 = paramProperties;
      return localProperties1;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Trace.println("Cannot find prop file: " + paramFile.getAbsolutePath(), TraceLevel.BASIC);
      localProperties2 = null;
      return localProperties2;
    }
    catch (IOException localIOException1)
    {
      Trace.println("IO Execption: " + localIOException1, TraceLevel.BASIC);
      Properties localProperties2 = null;
      return localProperties2;
    }
    finally
    {
      if (localFileInputStream != null)
        try
        {
          localFileInputStream.close();
        }
        catch (IOException localIOException5)
        {
        }
      if (localObject1 != null)
        localObject1.release();
    }
  }

  private Properties getInternalProps()
  {
    Properties localProperties = new Properties();
    localProperties.setProperty("java.quick.starter", "false");
    localProperties.setProperty("deployment.browser.vm.iexplorer", "true");
    localProperties.setProperty("deployment.browser.vm.mozilla", "true");
    localProperties.setProperty("deployment.javafx.mode.enabled", "true");
    localProperties.setProperty("deployment.jpi.mode.new", "true");
    return localProperties;
  }

  public boolean isInternalProp(String paramString)
  {
    return (this._internalProps.containsKey(paramString)) || (paramString.startsWith("active."));
  }

  void setInitVersionOverride(String paramString)
  {
    this.initVersionOverride = paramString;
  }

  private void versionUpdateCheck()
  {
    String str1 = this.initVersionOverride == null ? "7.21" : this.initVersionOverride;
    if (this._userConfigStore)
      str1 = getProperty("deployment.version");
    VersionID localVersionID = str1 == null ? null : new VersionID(str1);
    Object localObject1;
    if ((localVersionID == null) || (new VersionID("1.5.0").isGreaterThan(localVersionID)))
    {
      localObject1 = translateMantisProperties(this);
      if (((Properties)localObject1).getProperty("deployment.javaws.cachedir") == null)
      {
        localObject2 = getUserHome() + File.separator + "javaws" + File.separator + "cache";
        if (new File((String)localObject2).exists())
          ((Properties)localObject1).setProperty("deployment.javaws.cachedir", (String)localObject2);
      }
      Object localObject2 = null;
      File localFile1 = new File(getUserHome());
      String str2 = localFile1.getParent() + File.separator + ".deployment";
      String str3 = str2 + File.separator + "deployment.properties";
      File localFile2 = new File(str3);
      if (localFile2.exists())
      {
        localObject2 = translateMantisProperties(loadPropertiesFile(new Properties(), localFile2));
        if (((Properties)localObject2).getProperty("deployment.javaws.cachedir") == null)
        {
          String str4 = str2 + File.separator + "javaws" + File.separator + "cache";
          if (new File(str4).exists())
            ((Properties)localObject2).setProperty("deployment.javaws.cachedir", str4);
        }
      }
      if (localObject2 != null)
        setProperties((Properties)localObject2);
      if (localObject1 != null)
        setProperties((Properties)localObject1);
      setProperty("deployment.browser.path", Platform.get().getBrowserPath());
      localVersionID = new VersionID("1.5.0");
    }
    if (new VersionID("6.0").isGreaterThan(localVersionID))
    {
      setProperties(translateTigerProperties(this));
      setBooleanProperty("deployment.javaws.cache.update", true);
      setBooleanProperty("deployment.javapi.cache.update", true);
      setBooleanProperty("deployment.capture.mime.types", true);
    }
    if ((!new VersionID("7.0").isGreaterThan(localVersionID)) || (new VersionID("7.21").isGreaterThan(localVersionID)))
    {
      localObject1 = getProperty("deployment.security.level");
      if ((!"HIGH".equals(localObject1)) && (!"VERY_HIGH".equals(localObject1)))
        setProperty("deployment.security.level", "HIGH");
    }
    setProperty("deployment.version", "7.21");
  }

  private void setProperties(Properties paramProperties)
  {
    Enumeration localEnumeration = paramProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      setProperty(str, paramProperties.getProperty(str));
    }
  }

  private Properties translateMantisProperties(Properties paramProperties)
  {
    Properties localProperties = new Properties();
    Enumeration localEnumeration = paramProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      str1 = (String)localEnumeration.nextElement();
      String str2 = null;
      String str3 = paramProperties.getProperty(str1);
      if (str1.startsWith("deployment.javaws.proxy"))
      {
        if (str1.equals("deployment.javaws.proxy.httpport"))
          str2 = "deployment.proxy.http.port";
        else if (str1.equals("deployment.javaws.proxy.http"))
          str2 = "deployment.proxy.http.host";
        else if (str1.equals("deployment.javaws.proxy.httpproxyoverride"))
          str2 = "deployment.proxy.override.hosts";
      }
      else if (str1.startsWith("deployment.javaws."))
      {
        if (str1.equals("deployment.javaws.logFileName"))
          str2 = "deployment.javaws.traceFileName";
        else if (str1.equals("deployment.javaws.showConsole"))
        {
          if (str3.equals("true"))
          {
            str2 = "deployment.console.startup.mode";
            str3 = "SHOW";
          }
        }
        else if (str1.equals("deployment.javaws.updateTimeout"))
          str2 = "deployment.javaws.update.timeout";
        else if (str1.equals("deployment.javaws.version"))
          str3 = null;
      }
      else if (str1.startsWith("javaplugin"))
      {
        localProperties.setProperty(str1, str3);
        if (str1.equals("javaplugin.cache.disabled"))
        {
          str2 = "deployment.cache.enabled";
          str3 = str3.equals("true") ? "false" : "true";
        }
        else if (str1.equals("javaplugin.cache.size"))
        {
          str2 = "deployment.cache.max.size";
        }
        else if (str1.equals("javaplugin.cache.compression"))
        {
          str2 = "deployment.cache.jarcompression";
        }
        else if (str1.equals("javaplugin.console"))
        {
          str2 = "deployment.console.startup.mode";
          if (str3.equals("show"))
            str3 = "SHOW";
          else if (str3.equals("hide"))
            str3 = "HIDE";
          else
            str3 = "DISABLE";
        }
        else if (str1.equals("javaplugin.exception"))
        {
          str2 = "deployment.javapi.lifecycle.exception";
        }
      }
      if ((str2 != null) && (str3 != null) && (str3.length() > 0))
        localProperties.setProperty(str2, str3);
    }
    String str1 = paramProperties.getProperty("deployment.javaws.cache.dir");
    if (str1 != null)
      localProperties.setProperty("deployment.javaws.cachedir", str1);
    return localProperties;
  }

  private Properties translateTigerProperties(Properties paramProperties)
  {
    Properties localProperties = new Properties();
    Enumeration localEnumeration = paramProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      String str2 = paramProperties.getProperty(str1);
      if ((str1.equals("deployment.javaws.associations")) && ((str2.equals("NEW_ONLY")) || (str2.equals("REPLACE_ASK"))))
        str2 = "ASK_USER";
      localProperties.setProperty(str1, str2);
    }
    return localProperties;
  }

  public boolean getJavaPlugin()
  {
    boolean bool = true;
    if (getOSArch().equalsIgnoreCase("amd64"))
    {
      lockProperty("deployment.jpi.mode.new", "true");
      bool = true;
    }
    else
    {
      bool = Platform.get().getJavaPluginSettings();
      if (!Platform.get().canBecomeAdmin())
        lockProperty("deployment.jpi.mode.new", new Boolean(bool).toString());
      else
        unlockProperty("deployment.jpi.mode.new");
    }
    setProperty("deployment.jpi.mode.new", new Boolean(bool).toString());
    return bool;
  }

  public boolean getJqs()
  {
    boolean bool = Platform.get().getJqsSettings();
    if (Platform.get().isPlatformWindowsVista())
      lockProperty("java.quick.starter", "false");
    else
      unlockProperty("java.quick.starter");
    setProperty("java.quick.starter", new Boolean(bool).toString());
    return bool;
  }

  void lockProperty(String paramString1, String paramString2)
  {
    if (this._lockedProps == null)
      this._lockedProps = new Properties();
    this._lockedProps.setProperty(paramString1, paramString2);
  }

  void unlockProperty(String paramString)
  {
    if (this._lockedProps != null)
      this._lockedProps.remove(paramString);
  }

  public boolean isPropertyLocked(String paramString)
  {
    if (this._lockedProps == null)
      return false;
    return this._lockedProps.containsKey(paramString);
  }

  public void storeIfNeeded()
  {
    if (this._dirty)
      storeConfig();
  }

  public void refreshIfNeeded()
  {
    if ((!this._dirty) && (isDiskNewer()))
      refreshProperties();
  }

  public void storeInstalledJREs(Vector paramVector)
  {
    this._dirty |= JREInfo.setInstalledJREList(paramVector);
    storeIfNeeded();
  }

  public String getEnterpriseString()
  {
    return this._enterprizeConfig;
  }

  public boolean isValid()
  {
    return this._configOK;
  }

  public boolean isConfigDirty()
  {
    return this._dirty;
  }

  public Properties getSystemProps()
  {
    return this._systemProps;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.ClientConfig
 * JD-Core Version:    0.6.2
 */