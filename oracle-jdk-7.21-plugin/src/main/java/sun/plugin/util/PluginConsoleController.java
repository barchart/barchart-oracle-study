package sun.plugin.util;

import com.sun.deploy.cache.MemoryCache;
import com.sun.deploy.net.proxy.DynamicProxyManager;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.CertificateHostnameVerifier;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.security.X509DeployTrustManager;
import com.sun.deploy.services.Service;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.LoggerTraceListener;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ui.ConsoleController14;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.Policy;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.plugin.ClassLoaderInfo;
import sun.plugin.WJcovUtil;
import sun.plugin.services.BrowserService;
import sun.security.action.GetPropertyAction;

public class PluginConsoleController
  implements ConsoleController14
{
  private boolean onWindows = false;
  private boolean isMozilla = false;
  private boolean iconifiedOnClose = false;
  private Logger logger = null;

  public PluginConsoleController()
  {
    try
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if (str1.indexOf("Windows") != -1)
        this.onWindows = true;
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("mozilla.workaround", "false"));
      if ((str2 != null) && (str2.equalsIgnoreCase("true")))
        this.isMozilla = true;
      this.iconifiedOnClose = false;
      Service localService = ServiceManager.getService();
      if ((localService instanceof BrowserService))
        this.iconifiedOnClose = ((BrowserService)localService).isConsoleIconifiedOnClose();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public boolean isIconifiedOnClose()
  {
    return this.iconifiedOnClose;
  }

  public boolean isDumpStackSupported()
  {
    return true;
  }

  public ThreadGroup getMainThreadGroup()
  {
    return PluginSysUtil.getPluginThreadGroup().getParent();
  }

  public boolean isSecurityPolicyReloadSupported()
  {
    return true;
  }

  public void reloadSecurityPolicy()
  {
    Policy localPolicy = Policy.getPolicy();
    localPolicy.refresh();
  }

  public boolean isProxyConfigReloadSupported()
  {
    return true;
  }

  public void reloadProxyConfig()
  {
    DynamicProxyManager.reset();
  }

  public boolean isDumpClassLoaderSupported()
  {
    return true;
  }

  public String dumpClassLoaders()
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    ClassLoaderInfo.dumpClassLoaderCache(localPrintWriter);
    return localStringWriter.toString();
  }

  public boolean isClearClassLoaderSupported()
  {
    return true;
  }

  public void clearClassLoaders()
  {
    MemoryCache.clearLoadedResources();
    ClassLoaderInfo.clearClassLoaderCache();
    TrustDecider.reset();
    X509DeployTrustManager.reset();
    CertificateHostnameVerifier.reset();
  }

  public boolean isLoggingSupported()
  {
    return true;
  }

  public void setLogger(Logger paramLogger)
  {
    this.logger = paramLogger;
  }

  public Logger getLogger()
  {
    return this.logger;
  }

  public boolean toggleLogging()
  {
    if (this.logger == null)
    {
      localObject = new File(UserProfile.getLogDirectory());
      File localFile = Trace.createTempFile("plugin", ".log", (File)localObject);
      LoggerTraceListener localLoggerTraceListener = new LoggerTraceListener("sun.plugin", localFile.getPath());
      this.logger = localLoggerTraceListener.getLogger();
    }
    Object localObject = this.logger.getLevel();
    if (localObject == Level.OFF)
      localObject = Level.ALL;
    else
      localObject = Level.OFF;
    this.logger.setLevel((Level)localObject);
    return localObject == Level.ALL;
  }

  public boolean isJCovSupported()
  {
    boolean bool = false;
    if (this.onWindows)
    {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.vm.options"));
      bool = (str != null) && (str.indexOf("-Xrunjcov") != -1);
    }
    return bool;
  }

  public boolean dumpJCovData()
  {
    return WJcovUtil.dumpJcovData();
  }

  public String getProductName()
  {
    return ResourceManager.getString("product.javapi.name", System.getProperty("java.version"));
  }

  public void notifyConsoleClosed()
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.PluginConsoleController
 * JD-Core Version:    0.6.2
 */