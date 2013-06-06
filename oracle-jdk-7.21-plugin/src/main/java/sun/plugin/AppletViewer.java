package sun.plugin;

import com.sun.applet2.Applet2Context;
import com.sun.applet2.Applet2Host;
import com.sun.applet2.AppletParameters;
import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.ErrorEvent;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.cache.DeployCacheHandler;
import com.sun.deploy.cache.MemoryCache;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.Platform;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.cookie.DeployCookieSelector;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.net.proxy.DeployProxySelector;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.DeployAuthenticator;
import com.sun.deploy.security.DeployNTLMAuthCallback;
import com.sun.deploy.services.Service;
import com.sun.deploy.services.ServiceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.JavaTrayIcon;
import com.sun.deploy.ui.JavaTrayIconController;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.uitoolkit.impl.awt.AWTDefaultPreloader;
import com.sun.deploy.uitoolkit.impl.awt.AWTErrorPanel;
import com.sun.deploy.uitoolkit.ui.UIFactory;
import com.sun.deploy.util.ArrayUtil;
import com.sun.deploy.util.DeploySysAction;
import com.sun.deploy.util.DeploySysRun;
import com.sun.deploy.util.DeploymentHooks;
import com.sun.deploy.util.StringUtils;
import com.sun.deploy.util.URLUtil;
import com.sun.deploy.util.UpdateCheck;
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.applet.AppletClassLoader;
import sun.applet.AppletEvent;
import sun.applet.AppletListener;
import sun.applet.AppletPanel;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ContainerAccessor;
import sun.awt.DesktopBrowse;
import sun.net.www.protocol.jar.URLJarFile;
import sun.plugin.cache.CacheUpdateHelper;
import sun.plugin.cache.JarCacheUtil;
import sun.plugin.cache.JarCacheVersionException;
import sun.plugin.extension.ExtensionInstallationImpl;
import sun.plugin.javascript.JSContext;
import sun.plugin.perf.PluginRollup;
import sun.plugin.security.ActivatorSecurityManager;
import sun.plugin.security.JDK11ClassFileTransformer;
import sun.plugin.security.PluginClassLoader;
import sun.plugin.services.BrowserService;
import sun.plugin.util.PluginSysUtil;
import sun.plugin.util.ProgressMonitor;
import sun.plugin.util.ProgressMonitorAdapter;
import sun.plugin.util.UserProfile;
import sun.plugin.viewer.context.PluginAppletContext;

public class AppletViewer extends AppletPanel
  implements WindowListener
{
  private AWTDefaultPreloader preloader = null;
  private boolean loading_first_time = true;
  private volatile boolean stopped = false;
  private volatile boolean stopLoadJar = false;
  private static String APPCONTEXT_APPLETCONTEXT_KEY = "AppletContextKey";
  private static boolean initialized = false;
  public static final String theVersion = "1.1";
  private URL documentURL = null;
  private String documentURLString = null;
  protected URL baseURL = null;
  protected final HashMap atts = new HashMap();
  private ClassLoaderInfo cli = null;
  private static boolean fShowException = false;
  ProgressMonitorAdapter progressListener = null;
  private AppletEventListener appletEventListener = new AppletEventListener(null);
  private final Object syncInit = new Object();
  private boolean bInit = false;
  private static final String VERSION_TAG = "version=";
  private HashMap jarVersionMap = new HashMap();
  private HashMap preloadJarMap = new HashMap();
  private ArrayList newStyleJarList = new ArrayList();
  private static final String PRELOAD = "preload";
  private boolean loadingDone = false;
  private final Object syncLoading = new Object();
  private boolean docbaseInit = false;
  private final Object docBaseSyncObj = new Object();
  protected boolean codeBaseInit = false;
  private String classLoaderCacheKey = null;
  private AppletStatusListener statusListener = null;
  private int lastStatus = -1;
  private volatile PluginAppletContext appletContext;
  private InputStream is;

  public static void loadPropertiesFiles()
  {
    DeployPerfUtil.put("START - Java   - JVM - AppletViewer.loadPropertiesFiles");
    try
    {
      File localFile = new File(UserProfile.getPropertyFile());
      localFile.getParentFile().mkdirs();
    }
    catch (Throwable localThrowable)
    {
      Trace.printException(localThrowable);
    }
    DeployPerfUtil.put("END   - Java   - JVM - AppletViewer.loadPropertiesFiles");
  }

  public static void setStartTime(long paramLong)
  {
    String str = System.getProperty("sun.perflog");
    if (str != null)
      try
      {
        Class localClass = Class.forName("sun.misc.PerformanceLogger");
        if (localClass != null)
        {
          Class[] arrayOfClass = new Class[2];
          arrayOfClass[0] = String.class;
          arrayOfClass[1] = Long.TYPE;
          Method localMethod = localClass.getMethod("setStartTime", arrayOfClass);
          if (localMethod != null)
          {
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = "Java Plug-in load time";
            arrayOfObject[1] = new Long(paramLong);
            localMethod.invoke(null, arrayOfObject);
          }
        }
      }
      catch (Exception localException)
      {
      }
  }

  public static void initEnvironment(int paramInt, long paramLong)
  {
    if (initialized)
      return;
    setStartTime(paramLong);
    initEnvironment(paramInt);
  }

  public static void initEnvironment(int paramInt)
  {
    if (initialized)
      return;
    initialized = true;
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup");
    PluginSysUtil.getPluginThreadGroup();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - PluginSysUtil.getPluginThreadGroup");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - ServiceManager.setService");
    ServiceManager.setService(paramInt);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - ServiceManager.setService");
    try
    {
      Class localClass1 = ImageIcon.class;
    }
    catch (Throwable localThrowable1)
    {
    }
    try
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - JDK11ClassFileTransformer.init");
      JDK11ClassFileTransformer.init();
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - JDK11ClassFileTransformer.init");
    }
    catch (Throwable localThrowable2)
    {
      localThrowable2.printStackTrace();
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - load deploy.properties");
    Properties localProperties = new Properties(System.getProperties());
    localProperties.put("acl.read", "+");
    localProperties.put("acl.read.default", "");
    localProperties.put("acl.write", "+");
    localProperties.put("acl.write.default", "");
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
    String str1 = localProperties.getProperty("java.protocol.handler.pkgs");
    if (str1 != null)
      localProperties.put("java.protocol.handler.pkgs", str1 + "|sun.plugin.net.protocol|com.sun.deploy.net.protocol");
    else
      localProperties.put("java.protocol.handler.pkgs", "sun.plugin.net.protocol|com.sun.deploy.net.protocol");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - URLConnection.setDefaultAllowUserInteraction");
    URLConnection.setDefaultAllowUserInteraction(true);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - URLConnection.setDefaultAllowUserInteraction");
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
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment");
    JavaRunTime.initTraceEnvironment();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - JavaRunTime.initTraceEnvironment");
    Object localObject1 = Config.getStringProperty("deployment.console.startup.mode");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - display JavaConsole");
    if ("SHOW".equalsIgnoreCase((String)localObject1))
    {
      JavaRunTime.showJavaConsole(true);
      JavaRunTime.showOldPluginWarning();
    }
    else if (!"DISABLE".equalsIgnoreCase((String)localObject1))
    {
      BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
      if (localBrowserService.isConsoleIconifiedOnClose())
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

        public void showJavaConsole(boolean paramAnonymousBoolean)
        {
          JavaRunTime.showJavaConsole(paramAnonymousBoolean);
        }
      });
    }
    catch (Throwable localThrowable3)
    {
      localThrowable3.printStackTrace();
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - display JavaConsole");
    if ("true".equalsIgnoreCase(localProperties.getProperty("deployment.javapi.lifecycle.exception", "false")))
      fShowException = true;
    String str2 = localProperties.getProperty("sun.net.client.defaultConnectTimeout", "120000");
    localProperties.put("sun.net.client.defaultConnectTimeout", str2);
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - install extension package");
    try
    {
      Class localClass2 = Class.forName("sun.misc.ExtensionDependency");
      if (localClass2 != null)
      {
        localObject2 = new Class[1];
        localObject2[0] = Class.forName("sun.misc.ExtensionInstallationProvider");
        Method localMethod = localClass2.getMethod("addExtensionInstallationProvider", (Class[])localObject2);
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
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - install extension package");
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
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - enable proxy/web server authentication");
      Authenticator.setDefault(new DeployAuthenticator());
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - enable proxy/web server authentication");
    }
    System.setProperties(localProperties);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - load deploy.properties");
    System.out.println("");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployProxySelector.reset");
    DeployProxySelector.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployProxySelector.reset");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployCookieSelector.reset");
    DeployCookieSelector.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployCookieSelector.reset");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployOfflineManager.reset");
    DeployOfflineManager.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployOfflineManager.reset");
    System.out.println("");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - DeployCacheHandler.reset");
    Class localClass3 = Cache.class;
    DeployCacheHandler.reset();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - DeployCacheHandler.reset");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - install security manager");
    Object localObject2 = new ActivatorSecurityManager();
    System.setSecurityManager((SecurityManager)localObject2);
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - install security manager");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - Config.validateSystemCacheDirectory");
    Config.validateSystemCacheDirectory();
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - Config.validateSystemCacheDirectory");
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - URLJarFile.setCallBack");
    URLJarFile.setCallBack(new PluginURLJarFileCallBack());
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - URLJarFile.setCallBack");
    DeployNTLMAuthCallback.install();
    if (System.getProperty("os.name").indexOf("Windows") != -1)
    {
      DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - show update message");
      UpdateCheck.showDialog();
      DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - show update message");
    }
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initEnvironment - upgrade cache");
    try
    {
      DeploySysRun.executePrivileged(new DeploySysAction()
      {
        public Object execute()
        {
          if ((Config.getBooleanProperty("deployment.javapi.cache.update")) && (CacheUpdateHelper.updateCache()))
          {
            Config.setBooleanProperty("deployment.javapi.cache.update", false);
            Config.get().storeIfNeeded();
          }
          return null;
        }
      }
      , null);
    }
    catch (Exception localException)
    {
      Trace.printException(localException);
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment - upgrade cache");
    try
    {
      DesktopBrowse.setInstance(new DesktopBrowse()
      {
        public void browse(URL paramAnonymousURL)
        {
          AppletContext localAppletContext = (AppletContext)ToolkitStore.get().getAppContext().get(AppletViewer.APPCONTEXT_APPLETCONTEXT_KEY);
          if (localAppletContext != null)
            localAppletContext.showDocument(paramAnonymousURL);
        }
      });
    }
    catch (IllegalStateException localIllegalStateException)
    {
      if (Config.getDeployDebug())
        localIllegalStateException.printStackTrace(System.out);
    }
    catch (Throwable localThrowable5)
    {
      Trace.ignored(localThrowable5);
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initEnvironment");
  }

  private void doValidate()
  {
    try
    {
      AWTAccessor.getContainerAccessor().validateUnconditionally(getParent());
    }
    catch (Throwable localThrowable)
    {
      getParent().validate();
    }
  }

  private Applet2Context createApplet2Context()
  {
    final Applet2Host local4 = new Applet2Host()
    {
      public URL getDocumentBase()
      {
        return AppletViewer.this.getDocumentBase();
      }

      public void showDocument(URL paramAnonymousURL)
      {
        AppletContext localAppletContext = AppletViewer.this.getAppletContext();
        if (localAppletContext != null)
          localAppletContext.showDocument(paramAnonymousURL);
      }

      public void showDocument(URL paramAnonymousURL, String paramAnonymousString)
      {
        AppletContext localAppletContext = AppletViewer.this.getAppletContext();
        if (localAppletContext != null)
          localAppletContext.showDocument(paramAnonymousURL, paramAnonymousString);
      }

      public void showApplet()
      {
        Trace.ignored(new Exception("Request to show applet. Do not expect this to happen in the AppletViewer!"));
      }

      public void showError(String paramAnonymousString, Throwable paramAnonymousThrowable, boolean paramAnonymousBoolean)
      {
        AppletViewer.this.removeAll();
        AppletViewer.this.add(new AWTErrorPanel(AppletViewer.this.preloader.getBGColor(), AppletViewer.this.preloader.getFGColor(), this, true));
        AppletViewer.this.doValidate();
      }

      public void reloadAppletPage()
      {
        ClassLoaderInfo.clearClassLoaderCache();
        AppletContext localAppletContext = AppletViewer.this.getAppletContext();
        if (localAppletContext != null)
          localAppletContext.showDocument(getDocumentBase());
      }

      public Object getWindow()
      {
        return null;
      }
    };
    Applet2Context local5 = new Applet2Context()
    {
      private final Applet2Host val$ah;

      public String getName()
      {
        return AppletViewer.this.getName();
      }

      public int getHeight()
      {
        return AppletViewer.this.getHeight();
      }

      public int getWidth()
      {
        return AppletViewer.this.getWidth();
      }

      public AppletParameters getParameters()
      {
        return (AppletParameters)AppletViewer.this.atts.clone();
      }

      public String getParameter(String paramAnonymousString)
      {
        return AppletViewer.this.getParameter(paramAnonymousString);
      }

      public boolean isActive()
      {
        return AppletViewer.this.isActive();
      }

      public URL getCodeBase()
      {
        return AppletViewer.this.getCodeBase();
      }

      public Applet2Host getHost()
      {
        return local4;
      }

      public Preloader getPreloader()
      {
        return AppletViewer.this.preloader;
      }
    };
    return local5;
  }

  private void installProgressListener()
  {
    try
    {
      ProgressMonitor localProgressMonitor = ProgressMonitor.get();
      this.progressListener = new ProgressMonitorAdapter(this.preloader);
      this.progressListener.setProgressFilter(getCodeBase(), getJarFiles());
      localProgressMonitor.addProgressListener(this.loader.getThreadGroup(), this.progressListener);
    }
    catch (Throwable localThrowable)
    {
      Trace.println("Failed to install old-style progress monitor", TraceLevel.PRELOADER);
      Trace.ignored(localThrowable);
    }
  }

  public void appletInit()
  {
    if (!Config.get().isValid())
    {
      String str1 = ResourceManager.getString("common.ok_btn");
      String str2 = ResourceManager.getString("common.detail.button");
      ToolkitStore.getUI();
      ToolkitStore.getUI().showMessageDialog(null, null, 0, ResourceManager.getString("error.default.title.applet"), ResourceManager.getString("launcherrordialog.brief.message.applet"), ResourceManager.getString("enterprize.cfg.mandatory.applet", Config.get().getEnterpriseString()), null, str1, str2, null);
      notifyLoadingDone();
      return;
    }
    try
    {
      if (createClassLoader())
      {
        installProgressListener();
        initApplet();
      }
      else
      {
        assert ((this.status == 0) || (this.status == 7) || (this.status == 6));
        notifyLoadingDone();
      }
    }
    catch (SecurityException localSecurityException)
    {
      localSecurityException.printStackTrace();
      this.status = 7;
      notifyLoadingDone();
    }
  }

  private void initJarVersionMap()
  {
    int i = 1;
    String str1 = getParameter("archive_" + i);
    if (str1 != null)
      while (str1 != null)
      {
        localObject1 = new StringTokenizer(str1, ",", false);
        localObject2 = null;
        str3 = null;
        int j = 0;
        while (((StringTokenizer)localObject1).hasMoreTokens())
        {
          String str2 = ((StringTokenizer)localObject1).nextToken().trim();
          if (localObject2 == null)
            localObject2 = str2;
          else if (str2.toLowerCase().startsWith("version="))
            str3 = str2.substring("version=".length());
          else if (str2.toLowerCase().equals("preload"))
            j = 1;
        }
        if (localObject2 != null)
        {
          if (j != 0)
            this.preloadJarMap.put(localObject2, str3);
          this.jarVersionMap.put(localObject2, str3);
          this.newStyleJarList.add(localObject2);
        }
        i++;
        str1 = getParameter("archive_" + i);
      }
    Object localObject1 = getParameter("cache_archive");
    Object localObject2 = getParameter("cache_version");
    String str3 = getParameter("cache_archive_ex");
    try
    {
      this.jarVersionMap = JarCacheUtil.getJarsWithVersion((String)localObject1, (String)localObject2, str3);
    }
    catch (Exception localException)
    {
      Trace.printException(localException, ResourceManager.getMessage("cache.error.text"), ResourceManager.getMessage("cache.error.caption"));
    }
    if (str3 != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str3, ",", false);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str4 = localStringTokenizer.nextToken().trim();
        int k = str4.indexOf(';');
        if (k != -1)
        {
          String str5 = str4.substring(k);
          if (str5.toLowerCase().indexOf("preload") != -1)
          {
            String str6 = str4.substring(0, k);
            this.preloadJarMap.put(str6, null);
          }
        }
      }
    }
  }

  private void storeJarVersionMapInAppContext()
  {
    Iterator localIterator = this.jarVersionMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)this.jarVersionMap.get(str1);
      URL localURL = null;
      try
      {
        localURL = new URL(getCodeBase(), str1);
        if (!URLUtil.checkTargetURL(getCodeBase(), localURL))
          throw new SecurityException("Permission denied: " + localURL);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Trace.ignoredException(localMalformedURLException);
      }
      if (localURL != null)
        ToolkitStore.get().getAppContext().put("deploy-" + localURL.toString(), str2);
    }
  }

  public AppletClassLoader getAppletClassLoader()
  {
    AppletClassLoader localAppletClassLoader = null;
    URL localURL1 = null;
    try
    {
      localURL1 = getCodeBase();
    }
    catch (SecurityException localSecurityException)
    {
      localSecurityException.printStackTrace();
      return null;
    }
    if (localURL1 == null)
    {
      System.err.println("ERROR: unexpectedly couldn't get the codebase");
      return null;
    }
    ClassLoaderInfo localClassLoaderInfo = ClassLoaderInfo.find(localURL1, getClassLoaderCacheKey());
    if (localClassLoaderInfo == null)
    {
      System.err.println("ERROR: unexpectedly couldn't get the ClassLoaderInfo for the codebase/cache key");
    }
    else
    {
      this.cli = localClassLoaderInfo;
      localAppletClassLoader = localClassLoaderInfo.grabClassLoader();
      URL localURL2 = getDocumentBase();
      if ((localURL2 == null) || (!localURL2.getProtocol().equalsIgnoreCase("file")) || (URLUtil.isUNCFileURL(localURL2)))
        localAppletClassLoader.disableRecursiveDirectoryRead();
    }
    return localAppletClassLoader;
  }

  public boolean createClassLoader()
  {
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.createClassLoader");
    addAppletListener(this.appletEventListener);
    initJarVersionMap();
    URL localURL = getCodeBase();
    if (localURL == null)
      return false;
    try
    {
      if (!this.jarVersionMap.isEmpty())
        verifyJarVersions(localURL, getClassLoaderCacheKey(), this.jarVersionMap);
    }
    catch (Exception localException)
    {
      Trace.printException(localException, ResourceManager.getMessage("cache.error.text"), ResourceManager.getMessage("cache.error.caption"));
    }
    this.appletContext.addAppletPanelInContext(this);
    synchronized (AppletViewer.class)
    {
      super.init();
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.createClassLoader");
    return true;
  }

  private void verifyJarVersions(URL paramURL, String paramString, HashMap paramHashMap)
    throws IOException, JarCacheVersionException
  {
    int i = 0;
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramHashMap.get(str1);
      URL localURL = new URL(paramURL, str1);
      if (!URLUtil.checkTargetURL(paramURL, localURL))
        throw new SecurityException("Permission denied: " + localURL);
      Trace.msgNetPrintln("cache.version_checking", new Object[] { str1, str2 });
      if (str2 != null)
      {
        String str3 = ResourceProvider.get().getCurrentVersion(localURL);
        if ((str3 != null) && (str3.compareTo(str2) < 0))
          i = 1;
      }
    }
    if (i == 1)
      ClassLoaderInfo.markNotCachable(paramURL, paramString);
  }

  protected synchronized void createAppletThread()
  {
    String str = "applet-" + getCode();
    ThreadGroup localThreadGroup = this.loader.getThreadGroup();
    this.handler = new Thread(localThreadGroup, this, "thread " + str);
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        AppletViewer.this.handler.setContextClassLoader(AppletViewer.this.loader);
        return null;
      }
    });
    this.handler.start();
  }

  public void setLoader(AppletClassLoader paramAppletClassLoader)
  {
    this.loader = paramAppletClassLoader;
  }

  public void initApplet()
  {
    DeployPerfUtil.put("START - Java   - ENV - AppletViewer.initApplet");
    Thread localThread = getAppletHandlerThread();
    if (!this.bInit);
    Trace.msgPrintln("applet.progress.load");
    sendEvent(1);
    synchronized (this.atts)
    {
      Config.getHooks().preLaunch("plugin", this.documentURLString + ": " + ArrayUtil.mapToString(this.atts));
    }
  }

  public void sendAppletInit()
  {
    Trace.msgPrintln("applet.progress.init");
    sendEvent(2);
    synchronized (this.syncInit)
    {
      this.bInit = true;
    }
    DeployPerfUtil.put("END   - Java   - ENV - AppletViewer.initApplet");
  }

  public void stopLoading()
  {
    this.stopLoadJar = true;
    if (this.status == 1)
    {
      Trace.msgPrintln("applet.progress.stoploading");
      super.stopLoading();
    }
  }

  public void appletStart()
  {
    synchronized (this.syncInit)
    {
      if (!this.bInit)
        return;
    }
    if (this.stopped)
    {
      this.preloader.shutdownGrayBoxPainter();
      this.stopped = false;
    }
    Trace.msgPrintln("applet.progress.start");
    try
    {
      DeployPerfUtil.write(new PluginRollup());
      Trace.println("completed perf rollup", TraceLevel.BASIC);
    }
    catch (IOException localIOException)
    {
    }
    sendEvent(3);
  }

  public void appletStop()
  {
    this.stopped = true;
    synchronized (this.syncInit)
    {
      if (!this.bInit)
        return;
    }
    this.preloader.shutdownGrayBoxPainter();
    if (this.status == 1)
      stopLoading();
    Trace.msgPrintln("applet.progress.stop");
    sendEvent(4);
  }

  public void notifyLoadingDone()
  {
    synchronized (this.syncLoading)
    {
      this.loadingDone = true;
      this.syncLoading.notify();
    }
  }

  public void waitForLoadingDone(long paramLong)
  {
    try
    {
      synchronized (this.syncLoading)
      {
        while (!this.loadingDone)
          this.syncLoading.wait(paramLong);
      }
    }
    catch (InterruptedException localInterruptedException)
    {
    }
    catch (Exception localException)
    {
      Trace.printException(localException);
    }
  }

  public void appletDestroy()
  {
    this.stopped = true;
    this.preloader.shutdownGrayBoxPainter();
    Trace.msgPrintln("applet.progress.destroy");
    sendEvent(5);
    Trace.msgPrintln("applet.progress.dispose");
    sendEvent(0);
  }

  public void miniCleanup()
  {
    removeAppletListener(this.appletEventListener);
    this.appletEventListener = null;
    if (this.appletContext != null)
    {
      this.appletContext.removeAppletPanelFromContext(this);
      this.appletContext = null;
    }
  }

  public void cleanup()
  {
    miniCleanup();
    Trace.msgPrintln("applet.progress.joining");
    joinAppletThread();
    Trace.msgPrintln("applet.progress.joined");
  }

  public void joinAppletThread()
  {
    Thread localThread = getAppletHandlerThread();
    if (localThread != null)
    {
      try
      {
        localThread.join(1000L);
      }
      catch (Exception localException)
      {
        Trace.printException(localException);
      }
      if (localThread.isAlive())
        localThread.interrupt();
    }
  }

  public void release()
  {
    if (this.cli == null)
      this.cli = ClassLoaderInfo.find(getCodeBase(), getClassLoaderCacheKey());
    AppletClassLoader localAppletClassLoader = this.cli.getLoader();
    ThreadGroup localThreadGroup = localAppletClassLoader == null ? null : localAppletClassLoader.getThreadGroup();
    PluginClassLoader localPluginClassLoader = null;
    sun.awt.AppContext localAppContext = null;
    synchronized (this.cli)
    {
      Trace.msgPrintln("applet.progress.findinfo.0");
      int i = this.cli.removeReference();
      Trace.msgPrintln("applet.progress.findinfo.1");
      if (i > 0)
        return;
      if ((localAppletClassLoader != null) && ((localAppletClassLoader instanceof PluginClassLoader)))
      {
        localPluginClassLoader = (PluginClassLoader)localAppletClassLoader;
        Trace.msgPrintln("applet.progress.quit");
        localAppContext = localPluginClassLoader.resetAppContext();
      }
    }
    localPluginClassLoader.release(localAppContext);
    long l = System.currentTimeMillis();
    while ((localThreadGroup != null) && (localThreadGroup.activeCount() > 0) && (System.currentTimeMillis() - l < 5000L))
    {
      localThreadGroup.stop();
      try
      {
        Thread.sleep(10L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
    this.cli = null;
  }

  public void preRefresh()
  {
    MemoryCache.clearLoadedResources();
    if (this.cli != null)
      ClassLoaderInfo.markNotCachable(getCodeBase(), getClassLoaderCacheKey());
  }

  public String getParameter(String paramString)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    synchronized (this.atts)
    {
      String str = (String)this.atts.get(paramString);
      if (str != null)
        str = StringUtils.trimWhitespace(str);
      return str;
    }
  }

  public void setParameter(String paramString, Object paramObject)
  {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    synchronized (this.atts)
    {
      this.atts.put(paramString, StringUtils.trimWhitespace(paramObject.toString()));
    }
  }

  public void setDocumentBase(String paramString)
  {
    if (!this.docbaseInit)
    {
      String str = URLUtil.canonicalize(paramString);
      this.documentURLString = canonicalizeDocumentURL(str);
      this.docbaseInit = true;
      synchronized (this.docBaseSyncObj)
      {
        this.docBaseSyncObj.notifyAll();
      }
    }
  }

  public String canonicalizeDocumentURL(String paramString)
  {
    int i = -1;
    int j = paramString.indexOf('#');
    int k = paramString.indexOf('?');
    if ((k != -1) && (j != -1))
      i = Math.min(j, k);
    else if (j != -1)
      i = j;
    else if (k != -1)
      i = k;
    String str;
    if (i == -1)
      str = paramString;
    else
      str = paramString.substring(0, i);
    StringBuffer localStringBuffer = new StringBuffer(str);
    int m = localStringBuffer.toString().indexOf("|");
    if (m >= 0)
      localStringBuffer.setCharAt(m, ':');
    if (i != -1)
      localStringBuffer.append(paramString.substring(i));
    return localStringBuffer.toString();
  }

  public URL getDocumentBase()
  {
    Object localObject1 = new Object();
    synchronized (localObject1)
    {
      if (!this.docbaseInit)
      {
        Service localService = ServiceManager.getService();
        if ((localService != null) && (localService.isNetscape()) && ((localService instanceof BrowserService)) && (((BrowserService)localService).getBrowserVersion() >= 5.0F))
        {
          try
          {
            synchronized (this.docBaseSyncObj)
            {
              while (!this.docbaseInit)
                this.docBaseSyncObj.wait(0L);
            }
          }
          catch (InterruptedException localInterruptedException)
          {
            localInterruptedException.printStackTrace();
          }
        }
        else
        {
          JSContext localJSContext = (JSContext)getAppletContext();
          try
          {
            JSObject localJSObject1 = localJSContext.getJSObject();
            if (localJSObject1 == null)
              throw new JSException("Unable to obtain Window object");
            JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
            if (localJSObject2 == null)
              throw new JSException("Unable to obtain Document object");
            String str1 = (String)localJSObject2.getMember("URL");
            String str2 = URLUtil.canonicalize(str1);
            AccessController.doPrivileged(new PrivilegedAction()
            {
              public Object run()
              {
                return Policy.getPolicy();
              }
            });
            this.documentURL = new URL(canonicalizeDocumentURL(str2));
          }
          catch (Throwable localThrowable2)
          {
            Trace.println(localThrowable2.getMessage(), TraceLevel.BASIC);
            return null;
          }
          this.docbaseInit = true;
        }
      }
    }
    if (this.documentURL == null)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return Policy.getPolicy();
        }
      });
      try
      {
        if (this.documentURLString == null)
          throw new IllegalStateException("documentURLString null");
        this.documentURL = new URL(this.documentURLString);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable1.printStackTrace();
      }
    }
    return this.documentURL;
  }

  public URL getCodeBase()
  {
    Object localObject1 = new Object();
    synchronized (localObject1)
    {
      if (!this.codeBaseInit)
      {
        String str1 = getParameter("java_codebase");
        if (str1 == null)
          str1 = getParameter("codebase");
        URL localURL = getDocumentBase();
        if (localURL == null)
          return null;
        if (str1 != null)
        {
          if ((!str1.equals(".")) && (!str1.endsWith("/")))
            str1 = str1 + "/";
          str1 = URLUtil.canonicalize(str1);
          try
          {
            this.baseURL = new URL(localURL, str1);
            if (!URLUtil.checkTargetURL(localURL, this.baseURL))
              throw new SecurityException("Permission denied: " + this.baseURL);
          }
          catch (MalformedURLException localMalformedURLException1)
          {
          }
        }
        if (this.baseURL == null)
        {
          String str2 = localURL.toString();
          int i = str2.indexOf('?');
          if (i > 0)
            str2 = str2.substring(0, i);
          i = str2.lastIndexOf('/');
          if ((i > -1) && (i < str2.length() - 1))
            try
            {
              this.baseURL = new URL(URLUtil.canonicalize(str2.substring(0, i + 1)));
            }
            catch (MalformedURLException localMalformedURLException2)
            {
            }
          if (this.baseURL == null)
            this.baseURL = localURL;
        }
        this.codeBaseInit = true;
      }
    }
    return this.baseURL;
  }

  public int getWidth()
  {
    String str = getParameter("width");
    if (str != null)
      return Integer.valueOf(str).intValue();
    return 0;
  }

  public int getHeight()
  {
    String str = getParameter("height");
    if (str != null)
      return Integer.valueOf(str).intValue();
    return 0;
  }

  public boolean hasInitialFocus()
  {
    doValidate();
    if ((isJDK11Applet()) || (isJDK12Applet()))
      return false;
    String str = getParameter("initial_focus");
    if ((str != null) && (str.toLowerCase().equals("false")))
      return false;
    return !Platform.get().isNativeModalDialogUp();
  }

  public void updateHostIPFile(String paramString)
  {
    Cache.updateHostIPFile(paramString);
  }

  public String getCode()
  {
    String str1 = getParameter("classid");
    String str2 = null;
    if (str1 != null)
    {
      int i = str1.indexOf("java:");
      if (i > -1)
      {
        str2 = str1.substring(5 + i);
        if ((str2 != null) || (!str2.equals("")))
          return str2;
      }
    }
    str2 = getParameter("java_code");
    if (str2 == null)
      str2 = getParameter("code");
    return str2;
  }

  public boolean isLegacyLifeCycle()
  {
    String str = getParameter("legacy_lifecycle");
    return (str != null) && (str.equalsIgnoreCase("true"));
  }

  public String getClassLoaderCacheKey()
  {
    String str1 = getParameter("classloader-policy");
    if ((str1 != null) && (str1.equals("classic")))
      return super.getClassLoaderCacheKey();
    if (this.classLoaderCacheKey == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(getCodeBase());
      String str2 = getJarFiles();
      if (str2 != null)
      {
        localStringBuffer.append(",");
        localStringBuffer.append(str2);
      }
      this.classLoaderCacheKey = localStringBuffer.toString();
    }
    return this.classLoaderCacheKey;
  }

  private static synchronized String getJarsInCacheArchiveEx(String paramString)
  {
    if (paramString == null)
      return null;
    String str1 = "";
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",", false);
    int i = localStringTokenizer.countTokens();
    for (int j = 0; j < i; j++)
    {
      String str2 = localStringTokenizer.nextToken().trim();
      int k = str2.indexOf(";");
      if (k != -1)
      {
        String str3 = str2.substring(0, k);
        str1 = str1 + str3;
        str1 = str1 + (j != i - 1 ? "," : "");
      }
    }
    return str1;
  }

  public String getJarFiles()
  {
    StringBuffer localStringBuffer = null;
    if (!this.newStyleJarList.isEmpty())
    {
      localObject = this.newStyleJarList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        if (localStringBuffer == null)
          localStringBuffer = new StringBuffer();
        str1 = (String)((Iterator)localObject).next();
        localStringBuffer.append(str1);
        if (((Iterator)localObject).hasNext())
          localStringBuffer.append(",");
      }
      return addJarFileToPath(localStringBuffer == null ? null : localStringBuffer.toString(), null);
    }
    Object localObject = getParameter("archive");
    String str1 = getParameter("java_archive");
    String str2 = getParameter("cache_archive");
    String str3 = getParameter("cache_archive_ex");
    String str4 = null;
    if (str3 != null)
    {
      int i = str3.indexOf(";");
      if (i != -1)
        str4 = getJarsInCacheArchiveEx(str3);
      else
        str4 = str3;
    }
    return addJarFileToPath(str4, addJarFileToPath(str2, addJarFileToPath(str1, (String)localObject)));
  }

  private String addJarFileToPath(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null))
      return null;
    if ((paramString1 == null) && (paramString2 != null))
      return paramString2;
    if ((paramString1 != null) && (paramString2 == null))
      return paramString1;
    return paramString1 + "," + paramString2;
  }

  private void loadLocalJarFiles(PluginClassLoader paramPluginClassLoader, String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.exists())
    {
      String[] arrayOfString = localFile.list(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.endsWith(".jar");
        }
      });
      for (int i = 0; i < arrayOfString.length; i++)
        try
        {
          URL localURL = new File(paramString + File.separator + arrayOfString[i]).toURI().toURL();
          paramPluginClassLoader.addLocalJar(localURL);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localMalformedURLException.printStackTrace();
        }
    }
  }

  protected void setupAppletAppContext()
  {
    storeJarVersionMapInAppContext();
    ToolkitStore.get().getAppContext().put("deploy.trust.decider.app.name", getName());
    ToolkitStore.get().getAppContext().put(APPCONTEXT_APPLETCONTEXT_KEY, getAppletContext());
  }

  protected void loadJarFiles(AppletClassLoader paramAppletClassLoader)
    throws IOException, InterruptedException
  {
    if (this.loading_first_time)
    {
      this.loading_first_time = false;
      try
      {
        JarCacheUtil.preload(getCodeBase(), this.preloadJarMap);
      }
      catch (Exception localException)
      {
        Trace.printException(localException, ResourceManager.getMessage("cache.error.text"), ResourceManager.getMessage("cache.error.caption"));
      }
    }
    String str1 = getJarFiles();
    ClassLoaderInfo localClassLoaderInfo = this.cli;
    if (localClassLoaderInfo == null)
      return;
    try
    {
      localClassLoaderInfo.lock();
      String str3;
      if ((!localClassLoaderInfo.getLocalJarsLoaded()) && ((paramAppletClassLoader instanceof PluginClassLoader)))
      {
        localObject1 = File.separator;
        str2 = System.getProperty("java.home") + (String)localObject1 + "lib" + (String)localObject1 + "applet";
        loadLocalJarFiles((PluginClassLoader)paramAppletClassLoader, str2);
        if (Config.getOSName().equalsIgnoreCase("Windows"))
        {
          str3 = Config.getSystemHome() + (String)localObject1 + "Lib" + (String)localObject1 + "Untrusted";
          loadLocalJarFiles((PluginClassLoader)paramAppletClassLoader, str3);
        }
        localClassLoaderInfo.setLocalJarsLoaded(true);
      }
      if (str1 == null)
        return;
      Object localObject1 = new StringTokenizer(str1, ",", false);
      while (((StringTokenizer)localObject1).hasMoreTokens())
      {
        str2 = ((StringTokenizer)localObject1).nextToken().trim();
        if (!localClassLoaderInfo.hasJar(str2))
          localClassLoaderInfo.addJar(str2);
      }
      String str2 = getJarFiles();
      localObject1 = new StringTokenizer(str2, ",", false);
      while ((!this.stopLoadJar) && (((StringTokenizer)localObject1).hasMoreTokens()))
      {
        str3 = ((StringTokenizer)localObject1).nextToken().trim();
        try
        {
          ((PluginClassLoader)paramAppletClassLoader).addJar(str3);
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
        }
      }
    }
    finally
    {
      localClassLoaderInfo.unlock();
      this.stopLoadJar = false;
    }
  }

  public String getSerializedObject()
  {
    String str = getParameter("java_object");
    if (str == null)
      str = getParameter("object");
    return str;
  }

  public Applet getApplet()
  {
    Applet localApplet = super.getApplet();
    if (localApplet != null)
    {
      if ((localApplet instanceof BeansApplet))
        return null;
      return localApplet;
    }
    return null;
  }

  public Object getViewedObject()
  {
    Applet localApplet = super.getApplet();
    if ((localApplet instanceof BeansApplet))
      return ((BeansApplet)localApplet).bean;
    return localApplet;
  }

  public void setAppletContext(AppletContext paramAppletContext)
  {
    if (paramAppletContext == null)
      throw new IllegalArgumentException("AppletContext");
    if (this.appletContext != null)
      this.appletContext.removeAppletPanelFromContext(this);
    this.appletContext = ((PluginAppletContext)paramAppletContext);
  }

  public AppletContext getAppletContext()
  {
    return this.appletContext;
  }

  public void setColorAndText()
  {
  }

  public void paint(Graphics paramGraphics)
  {
    Dimension localDimension = getSize();
    if ((localDimension.width > 0) && (localDimension.height > 0) && ((this.status == 1) || (this.status == 2)))
      paintForegrnd(paramGraphics);
    else
      super.paint(paramGraphics);
  }

  public Color getForeground()
  {
    Color localColor = super.getForeground();
    if (null == localColor)
      localColor = Color.BLACK;
    return localColor;
  }

  public void paintForegrnd(Graphics paramGraphics)
  {
    if ((this.preloader != null) && (this.status != 7))
      this.preloader.doPaint(paramGraphics);
  }

  public String getWaitingMessage()
  {
    if (this.status == 7)
      return getMessage("failed");
    MessageFormat localMessageFormat = new MessageFormat(getMessage("loading"));
    return localMessageFormat.format(new Object[] { getHandledType() });
  }

  protected void load(InputStream paramInputStream)
  {
    this.is = paramInputStream;
  }

  protected Applet createApplet(AppletClassLoader paramAppletClassLoader)
    throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
  {
    if (this.is == null)
      return super.createApplet(paramAppletClassLoader);
    AppletObjectInputStream localAppletObjectInputStream = new AppletObjectInputStream(this.is, paramAppletClassLoader);
    Object localObject1 = localAppletObjectInputStream.readObject();
    Applet localApplet = (Applet)localObject1;
    this.doInit = false;
    if (Thread.interrupted())
    {
      try
      {
        this.status = 0;
        localApplet = null;
        showAppletStatus("death");
      }
      finally
      {
        Thread.currentThread().interrupt();
      }
      return null;
    }
    this.is = null;
    return localApplet;
  }

  public String getName()
  {
    String str = getParameter("name");
    if (str != null)
      return str;
    str = getCode();
    int i;
    if (str != null)
    {
      i = str.lastIndexOf(".class");
      if (i != -1)
        str = str.substring(0, i);
    }
    else
    {
      str = getSerializedObject();
      if (str != null)
      {
        i = str.lastIndexOf(".ser");
        if (i != -1)
          str = str.substring(0, i);
      }
    }
    return str;
  }

  public static int getAcceleratorKey(String paramString)
  {
    return ResourceManager.getAcceleratorKey(paramString);
  }

  protected String getHandledType()
  {
    return getMessage("java_applet");
  }

  public void addAppletStatusListener(AppletStatusListener paramAppletStatusListener)
  {
    this.statusListener = paramAppletStatusListener;
  }

  public void removeAppletStatusListener(AppletStatusListener paramAppletStatusListener)
  {
    this.statusListener = null;
  }

  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }

  public void showAppletLog(String paramString)
  {
    super.showAppletLog(paramString);
  }

  public boolean isStopped()
  {
    return this.stopped;
  }

  public void showAppletStatus(String paramString)
  {
    if (this.lastStatus != this.status)
    {
      this.lastStatus = this.status;
      if (this.status == 3)
        this.preloader.shutdownGrayBoxPainter();
      else if (this.status == 7)
        try
        {
          this.preloader.handleEvent(new ErrorEvent(getCodeBase(), paramString));
        }
        catch (CancelException localCancelException)
        {
        }
    }
    if ((paramString != null) && (!paramString.equals("")) && (!paramString.equals("\n")))
    {
      String str = getName();
      MessageFormat localMessageFormat = new MessageFormat(getMessage("status_applet"));
      if (this.appletContext != null)
        if ((str != null) && (!paramString.equals("")))
          this.appletContext.showStatus(localMessageFormat.format(new Object[] { str, paramString }));
        else
          this.appletContext.showStatus(localMessageFormat.format(new Object[] { paramString, "" }));
      if (this.statusListener != null)
        this.statusListener.statusChanged(this.status);
    }
  }

  protected void showAppletStatus(String paramString, Object paramObject)
  {
    if (this.appletContext != null)
      super.showAppletStatus(paramString, paramObject);
    if ((this.status == 7) && (this.lastStatus != this.status))
    {
      this.lastStatus = this.status;
      try
      {
        this.preloader.handleEvent(new ErrorEvent(getCodeBase(), paramString));
      }
      catch (CancelException localCancelException)
      {
      }
    }
  }

  protected void showAppletStatus(String paramString, Object paramObject1, Object paramObject2)
  {
    if (this.appletContext != null)
      super.showAppletStatus(paramString, paramObject1, paramObject2);
    if ((this.status == 7) && (this.lastStatus != this.status))
    {
      this.lastStatus = this.status;
      try
      {
        this.preloader.handleEvent(new ErrorEvent(getCodeBase(), paramString));
      }
      catch (CancelException localCancelException)
      {
      }
    }
  }

  public void setDoInit(boolean paramBoolean)
  {
    this.doInit = paramBoolean;
  }

  public static String getMessage(String paramString)
  {
    return ResourceManager.getMessage(paramString);
  }

  protected AppletClassLoader createClassLoader(URL paramURL)
  {
    return ClassLoaderInfo.find(paramURL, getClassLoaderCacheKey()).getLoader();
  }

  protected void showAppletException(Throwable paramThrowable)
  {
    super.showAppletException(paramThrowable);
    Trace.msgPrintln("exception", new Object[] { paramThrowable.toString() }, TraceLevel.BASIC);
    if (fShowException)
      Trace.printException(paramThrowable);
    if (this.lastStatus != 7)
      try
      {
        this.preloader.handleEvent(new ErrorEvent(getCodeBase(), paramThrowable));
      }
      catch (CancelException localCancelException)
      {
      }
  }

  public void showStatusText(String paramString)
  {
    if (this.appletContext != null)
      this.appletContext.showStatus(paramString);
  }

  public void update(Graphics paramGraphics)
  {
    Dimension localDimension = getSize();
    if ((localDimension.width > 0) && (localDimension.height > 0) && ((this.status == 1) || (this.status == 2)))
      paintForegrnd(paramGraphics);
    else
      super.update(paramGraphics);
  }

  public int getLoadingStatus()
  {
    return this.status;
  }

  public void windowActivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosed(WindowEvent paramWindowEvent)
  {
  }

  public void windowClosing(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeactivated(WindowEvent paramWindowEvent)
  {
  }

  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowIconified(WindowEvent paramWindowEvent)
  {
  }

  public void windowOpened(WindowEvent paramWindowEvent)
  {
  }

  private static class AppletEventListener
    implements AppletListener
  {
    private AppletEventListener()
    {
    }

    public void appletStateChanged(AppletEvent paramAppletEvent)
    {
      AppletViewer localAppletViewer = (AppletViewer)paramAppletEvent.getSource();
      switch (paramAppletEvent.getID())
      {
      case 51234:
        if (localAppletViewer != null)
        {
          Object localObject = localAppletViewer.getViewedObject();
          if ((localObject instanceof Component))
          {
            localAppletViewer.setSize(localAppletViewer.getSize());
            ((Component)localObject).setSize(localAppletViewer.getSize());
            localAppletViewer.validate();
          }
        }
        break;
      case 51236:
        localAppletViewer.notifyLoadingDone();
      }
    }

    AppletEventListener(AppletViewer.1 param1)
    {
      this();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.AppletViewer
 * JD-Core Version:    0.6.2
 */