package sun.plugin2.applet;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.deploy.Environment;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.DeployCacheHandler;
import com.sun.deploy.config.Config;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.security.DeployAuthenticator;
import com.sun.deploy.security.DeployNTLMAuthCallback;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceListener;
import com.sun.deploy.ui.JavaTrayIcon;
import com.sun.deploy.ui.JavaTrayIconController;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.ui.ConsoleController14;
import com.sun.deploy.uitoolkit.ui.ConsoleWindow;
import com.sun.deploy.uitoolkit.ui.DialogHook;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.SecurityBaseline;
import com.sun.deploy.util.UpdateCheck;
import com.sun.javaws.util.JavawsConsoleController;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.util.Properties;
import sun.awt.DesktopBrowse;
import sun.net.www.protocol.jar.URLJarFile;
import sun.plugin.JavaRunTime;
import sun.plugin.PluginURLJarFileCallBack;
import sun.plugin.cache.CacheUpdateHelper;
import sun.plugin.extension.ExtensionInstallationImpl;
import sun.plugin.security.JDK11ClassFileTransformer;
import sun.plugin.services.BrowserService;
import sun.plugin.util.PluginSysUtil;
import sun.plugin2.util.SystemUtil;

public class Applet2Environment
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  private static boolean initialized = false;
  private static final String theVersion = "1.1";

  public static void initialize(String paramString, boolean paramBoolean1, boolean paramBoolean2, ConsoleController14 paramConsoleController14, Applet2ExecutionContext paramApplet2ExecutionContext, DialogHook paramDialogHook)
  {
    synchronized (Applet2Environment.class)
    {
      if (initialized)
        return;
      long l = DeployPerfUtil.put(0L, "Applet2Environment.initialize() - BEGIN");
      System.setProperty("javaplugin.version", SecurityBaseline.getDeployVersion());
      System.setProperty("javaplugin.nodotversion", SecurityBaseline.getDeployNoDotVersion());
      if (paramString != null)
        System.setProperty("javaplugin.vm.options", paramString);
      DeploySysRun.setOverride(new PluginSysUtil());
      if (paramBoolean1)
        try
        {
          Trace.redirectStdioStderr();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable1.printStackTrace();
        }
      if (paramBoolean2)
        Trace.addTraceListener(new TraceListener()
        {
          public void print(String paramAnonymousString)
          {
            System.out.println(paramAnonymousString);
          }

          public void flush()
          {
          }
        });
      if (!Environment.isJavaPlugin())
        new InternalError("\n****************************************************************\nERROR: the javaplugin.version system property wasn't picked up\nby the com.sun.deploy.Environment class. This probably happened\nbecause of a change to the initialization order in PluginMain\nwhere the deployment classes are being initialized too early.\nThis will break jar cache versioning, and possibly other things.\nPlease undo your recent changes and rethink them.\n****************************************************************").printStackTrace();
      PluginSysUtil.getPluginThreadGroup();
      Applet2BrowserService.install(paramApplet2ExecutionContext);
      if (System.getProperty("java.version").compareTo("1.6.0_10") < 0)
        try
        {
          Class.forName("javax.swing.ImageIcon");
        }
        catch (Throwable localThrowable2)
        {
        }
      try
      {
        JDK11ClassFileTransformer.init();
      }
      catch (Throwable localThrowable3)
      {
        localThrowable3.printStackTrace();
      }
      Properties localProperties = new Properties(System.getProperties());
      localProperties.put("acl.read", "+");
      localProperties.put("acl.read.default", "");
      localProperties.put("acl.write", "+");
      localProperties.put("acl.write.default", "");
      localProperties.put("browser", "sun.plugin");
      localProperties.put("browser.version", "1.1");
      localProperties.put("browser.vendor", "Oracle");
      localProperties.put("http.agent", "Mozilla/4.0 (" + System.getProperty("os.name") + " " + System.getProperty("os.version") + ")");
      localProperties.put("sun.net.http.errorstream.enableBuffering", "true");
      localProperties.put("package.restrict.access.sun", "true");
      localProperties.put("package.restrict.access.com.sun.deploy", "true");
      localProperties.put("package.restrict.access.org.mozilla.jss", "true");
      localProperties.put("package.restrict.access.netscape", "false");
      localProperties.put("package.restrict.definition.java", "true");
      localProperties.put("package.restrict.definition.sun", "true");
      localProperties.put("package.restrict.definition.netscape", "true");
      localProperties.put("package.restrict.definition.com.sun.deploy", "true");
      localProperties.put("package.restrict.definition.org.mozilla.jss", "true");
      localProperties.put("java.version.applet", "true");
      localProperties.put("java.vendor.applet", "true");
      localProperties.put("java.vendor.url.applet", "true");
      localProperties.put("java.class.version.applet", "true");
      localProperties.put("os.name.applet", "true");
      localProperties.put("os.version.applet", "true");
      localProperties.put("os.arch.applet", "true");
      localProperties.put("file.separator.applet", "true");
      localProperties.put("path.separator.applet", "true");
      localProperties.put("line.separator.applet", "true");
      localProperties.put("mrj.version.applet", "true");
      localProperties.put("trustProxy", "true");
      if (Config.installDeployRMIClassLoaderSpi())
        localProperties.put("java.rmi.server.RMIClassLoaderSpi", "sun.plugin2.applet.JNLP2RMIClassLoaderSpi");
      URLConnection.setDefaultAllowUserInteraction(true);
      if (localProperties.get("https.protocols") == null)
      {
        localObject1 = new StringBuffer();
        if (Config.getBooleanProperty("deployment.security.TLSv1.2"))
          ((StringBuffer)localObject1).append("TLSv1.2");
        if (Config.getBooleanProperty("deployment.security.TLSv1.1"))
        {
          if (((StringBuffer)localObject1).length() != 0)
            ((StringBuffer)localObject1).append(",");
          ((StringBuffer)localObject1).append("TLSv1.1");
        }
        if (Config.getBooleanProperty("deployment.security.TLSv1"))
        {
          if (((StringBuffer)localObject1).length() != 0)
            ((StringBuffer)localObject1).append(",");
          ((StringBuffer)localObject1).append("TLSv1");
        }
        if (Config.getBooleanProperty("deployment.security.SSLv3"))
        {
          if (((StringBuffer)localObject1).length() != 0)
            ((StringBuffer)localObject1).append(",");
          ((StringBuffer)localObject1).append("SSLv3");
        }
        if (Config.getBooleanProperty("deployment.security.SSLv2Hello"))
        {
          if (((StringBuffer)localObject1).length() != 0)
            ((StringBuffer)localObject1).append(",");
          ((StringBuffer)localObject1).append("SSLv2Hello");
        }
        localProperties.put("https.protocols", ((StringBuffer)localObject1).toString());
      }
      localProperties.put("http.auth.serializeRequests", "true");
      Object localObject1 = new Thread(new Runnable()
      {
        private final ConsoleController14 val$controller;

        public void run()
        {
          JavaRunTime.initTraceEnvironment(this.val$controller);
          String str = Config.getStringProperty("deployment.console.startup.mode");
          Object localObject;
          if ("SHOW".equalsIgnoreCase(str))
          {
            localObject = JavawsConsoleController.getInstance();
            if ((localObject != null) && (((JavawsConsoleController)localObject).getConsole() != null))
              ((JavawsConsoleController)localObject).getConsole().dispose();
            JavaRunTime.showJavaConsole(true);
          }
          else if (!"DISABLE".equalsIgnoreCase(str))
          {
            localObject = (BrowserService)ServiceManager.getService();
            if (((BrowserService)localObject).isConsoleIconifiedOnClose())
              JavaRunTime.showJavaConsole(false);
          }
          try
          {
            JavaTrayIcon.install(new JavaTrayIconController()
            {
              public boolean isJavaConsoleVisible()
              {
                return JavaRunTime.isJavaConsoleVisible();
              }

              public void showJavaConsole(boolean paramAnonymous2Boolean)
              {
                JavaRunTime.showJavaConsole(paramAnonymous2Boolean);
              }
            });
          }
          catch (Throwable localThrowable)
          {
            localThrowable.printStackTrace();
          }
        }
      });
      ((Thread)localObject1).setDaemon(true);
      ((Thread)localObject1).start();
      String str = localProperties.getProperty("sun.net.client.defaultConnectTimeout", "120000");
      localProperties.put("sun.net.client.defaultConnectTimeout", str);
      try
      {
        Class localClass1 = Class.forName("sun.misc.ExtensionDependency");
        if (localClass1 != null)
        {
          Class[] arrayOfClass = new Class[1];
          arrayOfClass[0] = Class.forName("sun.misc.ExtensionInstallationProvider");
          Method localMethod = localClass1.getMethod("addExtensionInstallationProvider", arrayOfClass);
          if (localMethod != null)
          {
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = new ExtensionInstallationImpl();
            localMethod.invoke(null, arrayOfObject);
          }
          else
          {
            Trace.msgPrintln("optpkg.install.error.nomethod");
          }
        }
        else
        {
          Trace.msgPrintln("optpkg.install.error.noclass");
        }
      }
      catch (Throwable localThrowable4)
      {
        Trace.printException(localThrowable4);
      }
      localProperties.remove("proxyHost");
      localProperties.remove("proxyPort");
      localProperties.remove("http.proxyHost");
      localProperties.remove("http.proxyPort");
      localProperties.remove("https.proxyHost");
      localProperties.remove("https.proxyPort");
      localProperties.remove("ftpProxyHost");
      localProperties.remove("ftpProxyPort");
      localProperties.remove("ftpProxySet");
      localProperties.remove("gopherProxyHost");
      localProperties.remove("gopherProxyPort");
      localProperties.remove("gopherProxySet");
      localProperties.remove("socksProxyHost");
      localProperties.remove("socksProxyPort");
      if ("true".equalsIgnoreCase(localProperties.getProperty("javaplugin.proxy.authentication", "true")))
        Authenticator.setDefault(new DeployAuthenticator());
      System.setProperties(localProperties);
      DeployOfflineManager.reset();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return Policy.getPolicy();
        }
      });
      Class localClass2 = Cache.class;
      try
      {
        DeployCacheHandler.reset();
      }
      catch (Throwable localThrowable5)
      {
      }
      System.setSecurityManager(ToolkitStore.get().getSecurityManager());
      Config.validateSystemCacheDirectory();
      URLJarFile.setCallBack(new PluginURLJarFileCallBack());
      DeployNTLMAuthCallback.install();
      ToolkitStore.getUI().setDialogHook(paramDialogHook);
      if (System.getProperty("os.name").indexOf("Windows") != -1)
        UpdateCheck.showDialog();
      if (Config.getBooleanProperty("deployment.javapi.cache.update"))
        try
        {
          DeploySysRun.execute(new DeploySysAction()
          {
            public Object execute()
              throws Exception
            {
              if (CacheUpdateHelper.updateCache())
              {
                Config.setBooleanProperty("deployment.javapi.cache.update", false);
                Config.get().storeIfNeeded();
              }
              return null;
            }
          });
        }
        catch (Exception localException)
        {
          Trace.printException(localException);
        }
      try
      {
        DesktopBrowse.setInstance(new DesktopBrowse()
        {
          public void browse(URL paramAnonymousURL)
          {
            Applet2Context localApplet2Context = (Applet2Context)ToolkitStore.get().getAppContext().get("Plugin2CtxKey");
            if (localApplet2Context != null)
            {
              Applet2Host localApplet2Host = localApplet2Context.getHost();
              if (localApplet2Host != null)
                localApplet2Host.showDocument(paramAnonymousURL);
            }
          }
        });
      }
      catch (IllegalStateException localIllegalStateException)
      {
        if (DEBUG)
          localIllegalStateException.printStackTrace(System.out);
      }
      catch (NoSuchMethodError localNoSuchMethodError)
      {
      }
      catch (Throwable localThrowable6)
      {
        Trace.ignored(localThrowable6);
      }
      DeployPerfUtil.put(l, "Applet2Environment.initialize() - END");
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2Environment
 * JD-Core Version:    0.6.2
 */