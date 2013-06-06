package sun.plugin2.applet;

import com.sun.applet2.preloader.Preloader;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.config.Config;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.security.BadCertificateDialog;
import com.sun.deploy.security.CPCallbackClassLoaderIf;
import com.sun.deploy.security.CPCallbackHandler;
import com.sun.deploy.security.CeilingPolicy;
import com.sun.deploy.security.DeployURLClassPath;
import com.sun.deploy.security.DeployURLClassPathCallback;
import com.sun.deploy.security.DeployURLClassPathCallback.Element;
import com.sun.deploy.security.SecureCookiePermission;
import com.sun.deploy.security.TrustDecider;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.PerfLogger;
import com.sun.deploy.util.SystemUtils;
import com.sun.deploy.util.URLUtil;
import com.sun.jnlp.JNLPPreverifyClassLoader;
import java.awt.AWTPermission;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyPermission;
import java.util.jar.Manifest;
import sun.misc.JavaNetAccess;
import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import sun.plugin2.util.SystemUtil;

public abstract class Plugin2ClassLoader extends URLClassLoader
  implements CPCallbackClassLoaderIf
{
  protected static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
  protected static final String APPLET_CODE_REWRITE_KEY = "applet-code-rewrite-";
  protected final URL base;
  protected CodeSource codesource;
  private static RuntimePermission usePolicyPermission;
  protected AccessControlContext _acc;
  private AppContext appContext;
  private ThreadGroup threadGroup;
  private boolean codebaseLookup = true;
  private boolean codebaseLookupInitialized = false;
  private boolean securityCheck = false;
  private volatile boolean allowRecursiveDirectoryRead = true;
  private static Field ucpField = getUCPField("ucp");
  private static Method defineClassMethod = getMethod(class$java$net$URLClassLoader, "defineClass", new Class[] { String.class, sun.misc.Resource.class });
  private Plugin2ClassLoader pclParent;
  private List addedURLs = new ArrayList();
  private CPCallbackHandler cpHandler;
  private DeployURLClassPathCallback.Element codebaseElement;
  protected ClassLoader _delegatingClassLoader = null;
  protected boolean quiescenceRequested;
  protected Thread delegatingThread;
  protected int pendingCalls;
  protected ClassLoader parent;
  private static final String UNSIGNED_MESSAGE = " because the class is not signed.";
  private boolean shadowClassLoader = false;
  private Preloader preloader = null;
  private static CodeSource lastBadCertCodeSource = null;
  private HashMap knownSources = new HashMap();
  protected static final ThreadLocal cnfeThreadLocal = new ThreadLocal();
  private boolean ssvDialogShown;

  protected Plugin2ClassLoader(URL[] paramArrayOfURL, URL paramURL)
  {
    super(paramArrayOfURL);
    setUCP(this, new DeployURLClassPath(paramArrayOfURL));
    this.base = paramURL;
    this.codesource = new CodeSource(paramURL, (Certificate[])null);
    this._acc = AccessController.getContext();
    this.parent = getParent();
  }

  protected Plugin2ClassLoader(URL[] paramArrayOfURL, URL paramURL, ClassLoader paramClassLoader)
  {
    super(paramArrayOfURL, paramClassLoader);
    if ((paramClassLoader instanceof Plugin2ClassLoader))
    {
      this.pclParent = ((Plugin2ClassLoader)paramClassLoader);
      this.shadowClassLoader = true;
    }
    setUCP(this, new DeployURLClassPath(paramArrayOfURL, this.shadowClassLoader));
    this.parent = paramClassLoader;
    this.base = paramURL;
    this.codesource = new CodeSource(paramURL, (Certificate[])null);
    this._acc = AccessController.getContext();
  }

  boolean isShadowClassLoader()
  {
    return this.shadowClassLoader;
  }

  public void close()
    throws IOException
  {
    if (Config.isJavaVersionAtLeast17())
      super.close();
    setUCP(this, null);
    if (this.pclParent != null)
      this.pclParent.close();
  }

  public void setPreloader(Preloader paramPreloader)
  {
    this.preloader = paramPreloader;
  }

  public Preloader getPreloader()
  {
    return this.preloader;
  }

  public abstract boolean wantsAllPerms(CodeSource paramCodeSource);

  protected abstract Class findClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException;

  protected synchronized Class loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    return loadClass(paramString, paramBoolean, false);
  }

  protected synchronized Class loadClass(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws ClassNotFoundException
  {
    while ((this.quiescenceRequested) && (this.pendingCalls == 0) && (!Thread.currentThread().equals(this.delegatingThread)))
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new ClassNotFoundException("Quiescence interrupted");
      }
    try
    {
      this.pendingCalls += 1;
      Class localClass = loadClass0(paramString, paramBoolean1, paramBoolean2);
      return localClass;
    }
    finally
    {
      this.pendingCalls -= 1;
    }
  }

  public synchronized void quiescenceRequested(Thread paramThread, boolean paramBoolean)
  {
    if (paramBoolean)
      this.pendingCalls -= 1;
    this.delegatingThread = paramThread;
    this.quiescenceRequested = true;
  }

  public synchronized void quiescenceCancelled(boolean paramBoolean)
  {
    if (this.quiescenceRequested)
    {
      if (paramBoolean)
        this.pendingCalls += 1;
      this.delegatingThread = null;
      this.quiescenceRequested = false;
      notifyAll();
    }
  }

  private Class loadClass0(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws ClassNotFoundException
  {
    Class localClass = findLoadedClass(paramString);
    if (localClass == null)
      try
      {
        if (this.pclParent != null)
        {
          localClass = this.pclParent.loadClass(paramString, false, true);
        }
        else if ((this.parent instanceof JNLPPreverifyClassLoader))
        {
          JNLPPreverifyClassLoader localJNLPPreverifyClassLoader = (JNLPPreverifyClassLoader)this.parent;
          localClass = localJNLPPreverifyClassLoader.loadClass(paramString, false, true);
        }
        else
        {
          localClass = this.parent.loadClass(paramString);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localClass = findClass(paramString, paramBoolean2);
      }
    if (paramBoolean1)
      resolveClass(localClass);
    return localClass;
  }

  protected boolean needToApplyWorkaround()
  {
    StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
    String str;
    for (int i = 0; i < arrayOfStackTraceElement.length; i++)
    {
      str = arrayOfStackTraceElement[i].getClassName();
      if ((!str.equals(JNLP2ClassLoader.class.getName())) && (!str.equals(Plugin2ClassLoader.class.getName())) && (!str.equals(Class.class.getName())))
        if (!str.equals(ClassLoader.class.getName()))
          break;
    }
    if ((i > 0) && (i < arrayOfStackTraceElement.length) && (arrayOfStackTraceElement[(i - 1)].getClassName().equals(Class.class.getName())) && (arrayOfStackTraceElement[(i - 1)].getMethodName().equals("forName")))
    {
      str = arrayOfStackTraceElement[i].getClassName();
      if (str.equals("com.sun.javafx.runtime.adapter.AppletStartupRoutine"))
        return true;
      if (str.equals("org.jdesktop.applet.util.JNLPAppletLauncher"))
        return true;
    }
    return false;
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    long l1 = DeployPerfUtil.put(0L, "Plugin2ClassLoader - getPermissions() - BEGIN");
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    URL localURL = paramCodeSource.getLocation();
    String str1 = null;
    Permission localPermission;
    try
    {
      localPermission = localURL.openConnection().getPermission();
    }
    catch (IOException localIOException1)
    {
      localPermission = null;
    }
    if ((localPermission instanceof FilePermission))
    {
      str1 = localPermission.getName();
    }
    else if ((localPermission == null) && (localURL.getProtocol().equals("file")))
    {
      str1 = localURL.getFile().replace('/', File.separatorChar);
      str1 = ParseUtil.decode(str1);
    }
    if (str1 != null)
    {
      localObject1 = str1;
      if (!str1.endsWith(File.separator))
      {
        int i = str1.lastIndexOf(File.separatorChar);
        if (i != -1)
        {
          str1 = str1.substring(0, i + 1) + "-";
          localPermissionCollection.add(new FilePermission(str1, "read"));
        }
      }
      File localFile = new File((String)localObject1);
      boolean bool1 = SystemUtils.priviledgedIsDirectory(localFile);
      if ((this.allowRecursiveDirectoryRead) && ((bool1) || (((String)localObject1).toLowerCase().endsWith(".jar")) || (((String)localObject1).toLowerCase().endsWith(".zip"))))
      {
        try
        {
          localObject2 = this.base.openConnection().getPermission();
        }
        catch (IOException localIOException3)
        {
          localObject2 = null;
        }
        String str2;
        if ((localObject2 instanceof FilePermission))
        {
          str2 = ((Permission)localObject2).getName();
          if (str2.endsWith(File.separator))
            str2 = str2 + "-";
          localPermissionCollection.add(new FilePermission(str2, "read"));
        }
        else if ((localObject2 == null) && (this.base.getProtocol().equals("file")))
        {
          str2 = this.base.getFile().replace('/', File.separatorChar);
          str2 = ParseUtil.decode(str2);
          if (str2.endsWith(File.separator))
            str2 = str2 + "-";
          localPermissionCollection.add(new FilePermission(str2, "read"));
        }
      }
    }
    if ((localURL != null) && (localURL.getProtocol().equals("file")))
    {
      str1 = ParseUtil.decode(localURL.getFile());
      if (str1 != null)
      {
        str1 = str1.replace('/', File.separatorChar);
        localObject1 = File.separator + Config.getJREHome() + File.separator + "axbridge" + File.separator + "lib";
        try
        {
          str1 = new File(str1).getCanonicalPath();
          localObject1 = new File((String)localObject1).getCanonicalPath();
          if ((str1 != null) && (localObject1 != null) && (str1.startsWith((String)localObject1)))
          {
            localPermissionCollection.add(new AllPermission());
            Trace.println("Plugin2ClassLoader.getPermissions() X0", TraceLevel.BASIC);
            return localPermissionCollection;
          }
        }
        catch (IOException localIOException2)
        {
        }
      }
    }
    Object localObject1 = null;
    long l2 = 0L;
    if (DEBUG)
      l2 = System.currentTimeMillis();
    Object localObject2 = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    if (DEBUG)
    {
      long l3 = System.currentTimeMillis();
      System.out.println("Applet2ClassLoader: Policy.getPolicy() took " + (l3 - l2) + " ms");
    }
    localObject1 = ((Policy)localObject2).getPermissions(paramCodeSource);
    DeployPerfUtil.put(l1, "Plugin2ClassLoader - after newPolicy.getPermissions");
    addDefaultPermissions(localPermissionCollection);
    if (usePolicyPermission == null)
      usePolicyPermission = new RuntimePermission("usePolicy");
    if (!((PermissionCollection)localObject1).implies(usePolicyPermission))
    {
      boolean bool2 = isTrustedByTrustDecider(paramCodeSource);
      DeployPerfUtil.put(l1, "Plugin2ClassLoader - after isAllPermissionGranted()");
      if (bool2)
      {
        CeilingPolicy.addTrustedPermissions(localPermissionCollection);
        Trace.println("Plugin2ClassLoader.getPermissions CeilingPolicy allPerms", TraceLevel.BASIC);
      }
    }
    if ((!localPermissionCollection.implies(new AWTPermission("accessClipboard"))) && (!((PermissionCollection)localObject1).implies(new AWTPermission("accessClipboard"))))
      ToolkitStore.get().getAppContext().put("UNTRUSTED_CLIPBOARD_ACCESS_KEY", Boolean.TRUE);
    localPermissionCollection.add(new SecureCookiePermission(SecureCookiePermission.getURLOriginString(paramCodeSource.getLocation())));
    DeployPerfUtil.put(l1, "Plugin2ClassLoader - getPermissions() - END");
    return localPermissionCollection;
  }

  private boolean usePolicyPermissions(PermissionCollection paramPermissionCollection)
  {
    if (usePolicyPermission == null)
      usePolicyPermission = new RuntimePermission("usePolicy");
    return paramPermissionCollection.implies(usePolicyPermission);
  }

  private boolean isTrustedByTrustDecider(CodeSource paramCodeSource)
  {
    if (!wantsAllPerms(paramCodeSource))
      return false;
    PerfLogger.setTime("Security: Start calling TrustDecider for AllPermission in Plugin2ClassLoader");
    boolean bool = false;
    if (isTrustedCodesource(paramCodeSource.getLocation()))
      bool = true;
    else
      try
      {
        long l = TrustDecider.isAllPermissionGranted(paramCodeSource, getAppInfo(), false, null);
        bool = l != 0L;
        if ((bool) && (l != 1L))
        {
          com.sun.deploy.model.Resource localResource = ResourceProvider.get().getCachedResource(paramCodeSource.getLocation(), null);
          if (localResource != null)
            localResource.updateValidationResults(true, null, System.currentTimeMillis(), l, true);
        }
      }
      catch (CertificateExpiredException localCertificateExpiredException)
      {
        securityPrintException(localCertificateExpiredException, ResourceManager.getMessage("rsa.cert_expired"), ResourceManager.getMessage("security.dialog.caption"));
      }
      catch (CertificateNotYetValidException localCertificateNotYetValidException)
      {
        securityPrintException(localCertificateNotYetValidException, ResourceManager.getMessage("rsa.cert_notyieldvalid"), ResourceManager.getMessage("security.dialog.caption"));
      }
      catch (Exception localException)
      {
        if (!paramCodeSource.equals(lastBadCertCodeSource))
        {
          lastBadCertCodeSource = paramCodeSource;
          BadCertificateDialog.showDialog(paramCodeSource, getAppInfo(), localException);
        }
      }
    PerfLogger.setTime("Security: End calling TrustDecider for AllPermission in Plugin2ClassLoader");
    return bool;
  }

  private void securityPrintException(Exception paramException, String paramString1, String paramString2)
  {
    if (this.shadowClassLoader)
      Trace.ignored(paramException);
    else
      Trace.securityPrintException(paramException, paramString1, paramString2);
  }

  public static void addDefaultPermissions(PermissionCollection paramPermissionCollection)
  {
    paramPermissionCollection.add(new RuntimePermission("accessClassInPackage.sun.audio"));
    paramPermissionCollection.add(new PropertyPermission("browser", "read"));
    paramPermissionCollection.add(new PropertyPermission("browser.version", "read"));
    paramPermissionCollection.add(new PropertyPermission("browser.vendor", "read"));
    paramPermissionCollection.add(new PropertyPermission("http.agent", "read"));
    paramPermissionCollection.add(new PropertyPermission("javapi.*", "read,write"));
    paramPermissionCollection.add(new PropertyPermission("javaws.*", "read,write"));
    paramPermissionCollection.add(new PropertyPermission("jnlp.*", "read,write"));
    paramPermissionCollection.add(new PropertyPermission("javaplugin.version", "read"));
    paramPermissionCollection.add(new PropertyPermission("javaplugin.vm.options", "read"));
    paramPermissionCollection.add(new PropertyPermission("mrj.version", "read"));
  }

  public AppContext getAppContext()
  {
    return this.appContext;
  }

  public void setAppContext(AppContext paramAppContext)
  {
    if ((paramAppContext != null) && (this.appContext != null))
      throw new IllegalStateException("May not set the AppContext twice");
    if (this.pclParent != null)
      this.pclParent.setAppContext(paramAppContext);
    this.appContext = paramAppContext;
  }

  public ThreadGroup getThreadGroup()
  {
    return this.threadGroup;
  }

  public void setThreadGroup(ThreadGroup paramThreadGroup)
  {
    if ((paramThreadGroup != null) && (this.threadGroup != null))
      throw new IllegalStateException("May not set the ThreadGroup twice");
    if (this.pclParent != null)
      this.pclParent.setThreadGroup(paramThreadGroup);
    this.threadGroup = paramThreadGroup;
  }

  public synchronized boolean getCodebaseLookup()
  {
    if ((this.codebaseLookup) && (!this.codebaseLookupInitialized))
    {
      Object localObject = getCallbackHandler();
      if (localObject != null)
      {
        CPCallbackHandler localCPCallbackHandler = (CPCallbackHandler)localObject;
        DeployURLClassPathCallback localDeployURLClassPathCallback1 = localCPCallbackHandler.getChildCallback();
        DeployURLClassPathCallback localDeployURLClassPathCallback2 = localCPCallbackHandler.getParentCallback();
        try
        {
          this.codebaseLookup = false;
          localDeployURLClassPathCallback2.openClassPathElement(getBaseURL());
          DeployURLClassPathCallback.Element localElement = localDeployURLClassPathCallback1.openClassPathElement(getBaseURL());
          setCodebaseElement(localElement);
          this.codebaseLookup = (!localElement.skip());
        }
        catch (SecurityException localSecurityException)
        {
        }
        catch (IOException localIOException)
        {
        }
      }
      this.codebaseLookupInitialized = true;
    }
    return this.codebaseLookup;
  }

  public void setCodebaseLookup(boolean paramBoolean)
  {
    if (this.pclParent != null)
      this.pclParent.setCodebaseLookup(false);
    this.codebaseLookup = paramBoolean;
  }

  void setSecurityCheck(boolean paramBoolean)
  {
    if (this.pclParent != null)
      this.pclParent.setSecurityCheck(paramBoolean);
    this.securityCheck = paramBoolean;
  }

  boolean getSecurityCheck()
  {
    return this.securityCheck;
  }

  void disableRecursiveDirectoryRead()
  {
    if (this.pclParent != null)
      this.pclParent.disableRecursiveDirectoryRead();
    this.allowRecursiveDirectoryRead = false;
  }

  public Class loadCode(String paramString)
    throws ClassNotFoundException
  {
    long l = DeployPerfUtil.put(0L, "Plugin2ClassLoader - loadCode() - BEGIN");
    paramString = paramString.replace('/', '.');
    paramString = paramString.replace(File.separatorChar, '.');
    String str1 = null;
    int i = paramString.indexOf(";");
    if (i != -1)
    {
      str1 = paramString.substring(i, paramString.length());
      paramString = paramString.substring(0, i);
    }
    String str2 = paramString;
    if ((paramString.endsWith(".class")) || (paramString.endsWith(".java")))
      paramString = paramString.substring(0, paramString.lastIndexOf('.'));
    if (str1 != null)
    {
      localObject = getAppContext();
      if (localObject != null)
        ((AppContext)localObject).put("applet-code-rewrite-" + paramString, str1);
    }
    Object localObject = null;
    try
    {
      return loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      if (localClassNotFoundException1.getMessage().indexOf(" because the class is not signed.") >= 0)
        localObject = localClassNotFoundException1;
      try
      {
        return loadClass(str2);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        if (localObject != null)
          throw ((Throwable)localObject);
        throw localClassNotFoundException2;
      }
    }
  }

  boolean isClassLoadedByPluginClassLoader(Class paramClass)
  {
    ClassLoader localClassLoader = paramClass.getClassLoader();
    if (isShadowClassLoader())
      return (localClassLoader == this) || (localClassLoader == this.pclParent);
    return localClassLoader == this;
  }

  public void checkUntrustedAccess()
    throws SecurityException
  {
    if (this.cpHandler != null)
      this.cpHandler.checkUntrustedAccess();
  }

  private boolean isTrustedCodesource(URL paramURL)
  {
    Object localObject = this.knownSources.get(paramURL);
    if (localObject != null)
      return Boolean.TRUE.equals(localObject);
    com.sun.deploy.model.Resource localResource = ResourceProvider.get().getCachedResource(paramURL, null);
    if ((localResource != null) && (localResource.getValidationTimestamp() != 0L) && (localResource.isKnownToBeSigned()))
    {
      this.knownSources.put(paramURL, Boolean.TRUE);
      return true;
    }
    this.knownSources.put(paramURL, Boolean.FALSE);
    return false;
  }

  private void setCallbackHandler(CPCallbackHandler paramCPCallbackHandler)
  {
    this.cpHandler = paramCPCallbackHandler;
  }

  private Object getCallbackHandler()
  {
    return this.cpHandler;
  }

  private void setCodebaseElement(DeployURLClassPathCallback.Element paramElement)
  {
    this.codebaseElement = paramElement;
  }

  private Object getCodebaseElement()
  {
    return this.codebaseElement;
  }

  static boolean setDeployURLClassPathCallbacks(Plugin2ClassLoader paramPlugin2ClassLoader1, Plugin2ClassLoader paramPlugin2ClassLoader2)
  {
    try
    {
      if (!ResourceProvider.get().hasEnhancedJarAccess())
      {
        Trace.println("setDeployURLClassPathCallbacks: no enhanced access", TraceLevel.BASIC);
        return false;
      }
      CPCallbackHandler localCPCallbackHandler = new CPCallbackHandler(paramPlugin2ClassLoader1, paramPlugin2ClassLoader2);
      getDUCP(paramPlugin2ClassLoader1).setDeployURLClassPathCallback(localCPCallbackHandler.getParentCallback());
      getDUCP(paramPlugin2ClassLoader2).setDeployURLClassPathCallback(localCPCallbackHandler.getChildCallback());
      paramPlugin2ClassLoader2.setCallbackHandler(localCPCallbackHandler);
      paramPlugin2ClassLoader1.setCodebaseLookup(false);
    }
    catch (ThreadDeath localThreadDeath)
    {
      throw localThreadDeath;
    }
    catch (Exception localException)
    {
      return false;
    }
    catch (Error localError)
    {
      return false;
    }
    return true;
  }

  void setDeployURLClassPathCallback(DeployURLClassPath paramDeployURLClassPath)
  {
    Object localObject = getCallbackHandler();
    if (localObject != null)
    {
      CPCallbackHandler localCPCallbackHandler = (CPCallbackHandler)localObject;
      paramDeployURLClassPath.setDeployURLClassPathCallback(localCPCallbackHandler.getChildCallback());
    }
  }

  void checkResource(String paramString)
    throws SecurityException
  {
    Object localObject = getCodebaseElement();
    if (localObject != null)
    {
      DeployURLClassPathCallback.Element localElement = (DeployURLClassPathCallback.Element)localObject;
      try
      {
        localElement.checkResource(paramString);
      }
      catch (SecurityException localSecurityException)
      {
        Trace.println("resource name \"" + paramString + "\" in " + this.base + " : " + localSecurityException, TraceLevel.SECURITY);
        throw localSecurityException;
      }
    }
  }

  static DeployURLClassPath getDUCP(Plugin2ClassLoader paramPlugin2ClassLoader)
  {
    return (DeployURLClassPath)getUCP(paramPlugin2ClassLoader);
  }

  private static URLClassPath getUCP(Plugin2ClassLoader paramPlugin2ClassLoader)
  {
    URLClassPath localURLClassPath = null;
    try
    {
      localURLClassPath = (URLClassPath)ucpField.get(paramPlugin2ClassLoader);
    }
    catch (Exception localException)
    {
    }
    return localURLClassPath;
  }

  private static void setUCP(Plugin2ClassLoader paramPlugin2ClassLoader, URLClassPath paramURLClassPath)
  {
    try
    {
      ucpField.set(paramPlugin2ClassLoader, paramURLClassPath);
    }
    catch (Exception localException)
    {
    }
  }

  protected void addURL(URL paramURL)
  {
    addURL(paramURL, false);
  }

  protected void addURL(URL paramURL, boolean paramBoolean)
  {
    if ((!paramBoolean) && (!URLUtil.checkTargetURL(URLUtil.getBase(this.base), paramURL)))
      throw new SecurityException("Permission denied: " + paramURL);
    if (this.pclParent != null)
    {
      Trace.println("Plugin2ClassLoader.addURL parent called for " + paramURL, TraceLevel.BASIC);
      this.pclParent.addURL(paramURL, paramBoolean);
    }
    super.addURL(paramURL);
  }

  void addURL2(URL paramURL)
  {
    if (!URLUtil.checkTargetURL(URLUtil.getBase(this.base), paramURL))
      throw new SecurityException("Permission denied: " + paramURL);
    if (this.pclParent != null)
      drainPendingURLs();
    else
      putAddedURL(paramURL);
    Trace.println("Plugin2ClassLoader.addURL2 called for " + paramURL, TraceLevel.BASIC);
    super.addURL(paramURL);
  }

  boolean drainPendingURLs()
  {
    List localList = this.pclParent.grabAddedURLs();
    for (int i = 0; i < localList.size(); i++)
    {
      Trace.println("Plugin2ClassLoader.drainPendingURLs addURL called for " + localList.get(i), TraceLevel.BASIC);
      super.addURL((URL)localList.get(i));
    }
    return i != 0;
  }

  synchronized List grabAddedURLs()
  {
    List localList = this.addedURLs;
    this.addedURLs = new ArrayList();
    return localList;
  }

  synchronized void putAddedURL(URL paramURL)
  {
    this.addedURLs.add(paramURL);
  }

  protected Class findClassHelper(String paramString)
    throws ClassNotFoundException
  {
    if ((ucpField == null) || (defineClassMethod == null))
      return super.findClass(paramString);
    final String str = paramString;
    try
    {
      return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        private final String val$className;

        public Object run()
          throws ClassNotFoundException
        {
          String str = str.replace('.', '/').concat(".class");
          URLClassPath localURLClassPath;
          try
          {
            localURLClassPath = (URLClassPath)Plugin2ClassLoader.ucpField.get(Plugin2ClassLoader.this);
          }
          catch (Exception localException)
          {
            throw new ClassNotFoundException(str, localException);
          }
          sun.misc.Resource localResource = localURLClassPath.getResource(str, false);
          if (localResource == null)
            throw new ClassNotFoundException(str);
          Plugin2ClassLoader.WrapResource localWrapResource = new Plugin2ClassLoader.WrapResource(Plugin2ClassLoader.this, localResource);
          if (Plugin2ClassLoader.this.getSecurityCheck())
            try
            {
              localWrapResource.getBytes();
            }
            catch (IOException localIOException1)
            {
              throw new ClassNotFoundException(str, localIOException1);
            }
          if ((Plugin2ClassLoader.this.getSecurityCheck()) && (!Plugin2ClassLoader.isAllPermissionGranted(localWrapResource, Plugin2ClassLoader.this.getPreloader())))
          {
            Plugin2ClassLoader.cnfeThreadLocal.set(Plugin2ClassLoader.newClassNotFoundException(str));
            throw ((ClassNotFoundException)Plugin2ClassLoader.cnfeThreadLocal.get());
          }
          try
          {
            return Plugin2ClassLoader.this.defineClassHelper(str, localWrapResource);
          }
          catch (IOException localIOException2)
          {
            throw new ClassNotFoundException(str, localIOException2);
          }
        }
      }
      , this._acc);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((ClassNotFoundException)localPrivilegedActionException.getException());
    }
  }

  private Class defineClassHelper(String paramString, sun.misc.Resource paramResource)
    throws IOException
  {
    try
    {
      return (Class)defineClassMethod.invoke(this, new Object[] { paramString, paramResource });
    }
    catch (Exception localException)
    {
      for (Throwable localThrowable = localException.getCause(); localThrowable != null; localThrowable = localThrowable.getCause())
      {
        if ((localThrowable instanceof LinkageError))
          throw ((LinkageError)localThrowable);
        if ((localThrowable instanceof IOException))
          throw ((IOException)localThrowable);
        if ((localThrowable instanceof SecurityException))
          throw ((SecurityException)localThrowable);
      }
      throw new RuntimeException(localException);
    }
  }

  private static Field getUCPField(String paramString)
  {
    return (Field)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$name;

      public Object run()
      {
        try
        {
          Field localField = URLClassLoader.class.getDeclaredField(this.val$name);
          localField.setAccessible(true);
          return localField;
        }
        catch (Exception localException)
        {
        }
        return null;
      }
    });
  }

  protected static ClassNotFoundException newClassNotFoundException(String paramString)
  {
    return new ClassNotFoundException(paramString + " because the class is not signed.");
  }

  protected static boolean isAllPermissionGranted(sun.misc.Resource paramResource, Preloader paramPreloader)
  {
    URL localURL = paramResource.getCodeSourceURL();
    Object localObject;
    CodeSource localCodeSource;
    if (Config.isJavaVersionAtLeast15())
    {
      localObject = paramResource.getCodeSigners();
      if (localObject != null)
        localCodeSource = new CodeSource(localURL, (CodeSigner[])localObject);
      else
        localCodeSource = new CodeSource(localURL, paramResource.getCertificates());
    }
    else
    {
      localObject = paramResource.getCertificates();
      localCodeSource = new CodeSource(localURL, (Certificate[])localObject);
    }
    try
    {
      return TrustDecider.isAllPermissionGranted(localCodeSource, paramPreloader) != 0L;
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  public CodeSource[] getTrustedCodeSources(CodeSource[] paramArrayOfCodeSource)
  {
    ArrayList localArrayList = new ArrayList();
    Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Policy.getPolicy();
      }
    });
    for (int i = 0; i < paramArrayOfCodeSource.length; i++)
    {
      CodeSource localCodeSource = paramArrayOfCodeSource[i];
      boolean bool = false;
      PermissionCollection localPermissionCollection = localPolicy.getPermissions(localCodeSource);
      if (usePolicyPermissions(localPermissionCollection))
        bool = isTrustedByPolicy(localPolicy, localPermissionCollection);
      else
        bool = isTrustedByTrustDecider(localCodeSource);
      if (bool)
        localArrayList.add(localCodeSource);
    }
    return (CodeSource[])localArrayList.toArray(new CodeSource[localArrayList.size()]);
  }

  private boolean isTrustedByPolicy(Policy paramPolicy, PermissionCollection paramPermissionCollection)
  {
    Trace.println("Plugin2ClassLoader.isTrustedByPolicy called ", TraceLevel.BASIC);
    PermissionCollection localPermissionCollection = paramPolicy.getPermissions(new CodeSource(null, (Certificate[])null));
    Enumeration localEnumeration = paramPermissionCollection.elements();
    while (localEnumeration.hasMoreElements())
    {
      Permission localPermission = (Permission)localEnumeration.nextElement();
      if (!localPermissionCollection.implies(localPermission))
      {
        Trace.println("Plugin2ClassLoader.isTrustedByPolicy extended policy perm " + localPermission, TraceLevel.BASIC);
        return true;
      }
    }
    Trace.println("Plugin2ClassLoader.isTrustedByPolicy returns false ", TraceLevel.BASIC);
    return false;
  }

  public boolean getSSVDialogShown()
  {
    return this.ssvDialogShown;
  }

  public void setSSVDialogShown(boolean paramBoolean)
  {
    this.ssvDialogShown = paramBoolean;
  }

  protected AppInfo getAppInfo()
  {
    return new AppInfo();
  }

  public AccessControlContext getACC()
  {
    return this._acc;
  }

  URL getBaseURL()
  {
    return this.base;
  }

  public sun.misc.Resource getResourceAsResource(String paramString)
    throws MalformedURLException, FileNotFoundException
  {
    if (this.pclParent != null)
      try
      {
        return this.pclParent.getResourceAsResource(paramString);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
      }
    URLClassPath localURLClassPath = SharedSecrets.getJavaNetAccess().getURLClassPath(this);
    sun.misc.Resource localResource1 = localURLClassPath.getResource(paramString, false);
    if (localResource1 != null)
      return localResource1;
    if (getCodebaseLookup())
    {
      DeployURLClassPath localDeployURLClassPath = new DeployURLClassPath(new URL[] { getBaseURL() });
      setDeployURLClassPathCallback(localDeployURLClassPath);
      sun.misc.Resource localResource2 = localDeployURLClassPath.getResource(paramString, false);
      if (localResource2 != null)
        return localResource2;
    }
    throw new FileNotFoundException("Resource " + paramString + " not found");
  }

  public InputStream getResourceAsStream(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      sun.misc.Resource localResource = getResourceAsResource(paramString);
      if (localResource != null)
        localInputStream = localResource.getInputStream();
    }
    catch (Throwable localThrowable)
    {
    }
    if (localInputStream == null)
      localInputStream = super.getResourceAsStream(paramString);
    return localInputStream;
  }

  public AccessControlContext getACC(sun.misc.Resource paramResource)
  {
    CodeSource localCodeSource = new CodeSource(paramResource.getCodeSourceURL(), paramResource.getCodeSigners());
    ProtectionDomain localProtectionDomain = new ProtectionDomain(localCodeSource, getPermissions(localCodeSource), this, null);
    return new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
  }

  static Method getMethod(Class paramClass, final String paramString, final Class[] paramArrayOfClass)
  {
    return (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final Class val$c;
      private final String val$name;
      private final Class[] val$params;

      public Object run()
      {
        try
        {
          Method localMethod = this.val$c.getDeclaredMethod(paramString, paramArrayOfClass);
          localMethod.setAccessible(true);
          return localMethod;
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
        }
        return null;
      }
    });
  }

  private class WrapResource extends sun.misc.Resource
  {
    private sun.misc.Resource res = null;
    private byte[] cbytes;

    public WrapResource(sun.misc.Resource arg2)
    {
      Object localObject;
      this.res = localObject;
    }

    public String getName()
    {
      return this.res.getName();
    }

    public URL getURL()
    {
      return this.res.getURL();
    }

    public URL getCodeSourceURL()
    {
      return this.res.getCodeSourceURL();
    }

    public InputStream getInputStream()
      throws IOException
    {
      return this.res.getInputStream();
    }

    public int getContentLength()
      throws IOException
    {
      return this.res.getContentLength();
    }

    public byte[] getBytes()
      throws IOException
    {
      if (this.cbytes != null)
        return this.cbytes;
      return this.cbytes = super.getBytes();
    }

    public ByteBuffer getByteBuffer()
      throws IOException
    {
      return this.res.getByteBuffer();
    }

    public Manifest getManifest()
      throws IOException
    {
      return this.res.getManifest();
    }

    public Certificate[] getCertificates()
    {
      try
      {
        getBytes();
      }
      catch (IOException localIOException)
      {
      }
      return this.res.getCertificates();
    }

    public CodeSigner[] getCodeSigners()
    {
      try
      {
        getBytes();
      }
      catch (IOException localIOException)
      {
      }
      return this.res.getCodeSigners();
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Plugin2ClassLoader
 * JD-Core Version:    0.6.2
 */