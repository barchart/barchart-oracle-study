package sun.plugin2.applet;

import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.ConfigEvent;
import com.sun.applet2.preloader.event.DownloadEvent;
import com.sun.applet2.preloader.event.InitEvent;
import com.sun.applet2.preloader.event.PreloaderEvent;
import com.sun.deploy.Environment;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.config.Platform;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.DownloadException;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.si.SingleInstanceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.ui.AppInfo;
import com.sun.deploy.ui.UIFactory;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.util.JVMParameters;
import com.sun.deploy.util.StringQuoteUtil;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.CacheUpdateHelper;
import com.sun.javaws.Globals;
import com.sun.javaws.JnlpxArgs;
import com.sun.javaws.LaunchDownload;
import com.sun.javaws.LocalInstallHandler;
import com.sun.javaws.exceptions.CacheAccessException;
import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.exceptions.JRESelectException;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.javaws.exceptions.NoLocalJREException;
import com.sun.javaws.exceptions.OfflineLaunchException;
import com.sun.javaws.jnl.AppletDesc;
import com.sun.javaws.jnl.ApplicationDesc;
import com.sun.javaws.jnl.EmbeddedJNLPValidation;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.jnl.JavaFXAppDesc;
import com.sun.javaws.jnl.JavaFXRuntimeDesc;
import com.sun.javaws.jnl.LDUpdater;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.jnl.MatchJREIf;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.jnl.SecureMatchJRE;
import com.sun.javaws.jnl.UpdateDesc;
import com.sun.javaws.progress.PreloaderDelegate;
import com.sun.javaws.security.AppPolicy;
import com.sun.javaws.security.JNLPSignedResourcesHelper;
import com.sun.javaws.util.JavawsDialogListener;
import com.sun.javaws.util.JfxHelper;
import com.sun.jnlp.JNLPPreverifyClassLoader;
import com.sun.jnlp.JnlpLookupStub;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import javax.jnlp.ServiceManager;
import sun.plugin2.main.server.JVMHealthData;

public class JNLP2Manager extends Plugin2Manager
{
  private URL _initDocumentBaseURL = null;
  private String _initJnlpFile = null;
  private byte[] _initJnlpBytes;
  private boolean _initialized;
  private JREInfo homeJREInfo = null;
  private URL _codebase = null;
  private LaunchDesc _launchDesc = null;
  private AppPolicy _appPolicy = null;
  private LocalApplicationProperties _lap = null;
  private static boolean _environmentInitialized = false;
  private String _cachedJNLPFilePath = null;
  private JRESelectException _relaunchException = null;
  private boolean _allSigned = false;

  public void setCachedJNLPFilePath(String paramString)
  {
    this._cachedJNLPFilePath = paramString;
  }

  public JNLP2Manager(String paramString1, URL paramURL, String paramString2, boolean paramBoolean)
    throws Exception
  {
    this(paramString1, paramURL, paramString2, null, paramBoolean);
  }

  public JNLP2Manager(String paramString1, URL paramURL, String paramString2, byte[] paramArrayOfByte, boolean paramBoolean)
    throws Exception
  {
    super(paramBoolean);
    this._initDocumentBaseURL = paramURL;
    this._initJnlpFile = paramString2;
    this._initJnlpBytes = paramArrayOfByte;
    this._initialized = false;
    if (paramString1 == null)
    {
      this._codebase = URLUtil.getBase(this._initDocumentBaseURL);
      if (DEBUG)
        System.out.println("   JNLP Codebase (is documentbase): " + this._codebase);
    }
    else
    {
      try
      {
        this._codebase = URLUtil.asPathURL(new URL(paramString1));
        if (DEBUG)
          System.out.println("   JNLP Codebase (absolute): " + this._codebase);
      }
      catch (Exception localException1)
      {
        this._codebase = null;
      }
      if (this._codebase == null)
        try
        {
          URL localURL = new URL(URLUtil.getBase(this._initDocumentBaseURL), paramString1);
          if (!URLUtil.checkTargetURL(URLUtil.getBase(this._initDocumentBaseURL), localURL))
            throw new SecurityException("Permission denied: " + localURL);
          this._codebase = URLUtil.asPathURL(localURL);
          if (DEBUG)
            System.out.println("   JNLP Codebase (documentbase+codebase): " + this._codebase);
        }
        catch (Exception localException2)
        {
          this._codebase = null;
        }
      if ((DEBUG) && (this._codebase == null))
        System.out.println("   JNLP Codebase (null)");
    }
    if (DEBUG)
    {
      Trace.println("new JNLP2Manager: " + this._initJnlpFile + ", codebase: " + this._codebase + ", documentBase: " + this._initDocumentBaseURL, TraceLevel.BASIC);
      if (this._initJnlpBytes != null)
        Trace.println("Embedded JNLP: " + new String(this._initJnlpBytes), TraceLevel.BASIC);
    }
  }

  public AppInfo getAppInfo()
  {
    return getLaunchDesc().getAppInfo();
  }

  protected Plugin2ClassLoader newClassLoader()
  {
    if (null == this._codebase)
    {
      localObject = new Exception("newClassLoader - init failed: _codebase is null");
      Trace.ignoredException((Exception)localObject);
      return null;
    }
    Object localObject = Thread.currentThread().getContextClassLoader();
    JNLPPreverifyClassLoader localJNLPPreverifyClassLoader = new JNLPPreverifyClassLoader((ClassLoader)localObject);
    JNLP2ClassLoader localJNLP2ClassLoader1 = new JNLP2ClassLoader(this._codebase, localJNLPPreverifyClassLoader);
    if (Config.getMixcodeValue() != 3)
    {
      JNLP2ClassLoader localJNLP2ClassLoader2 = new JNLP2ClassLoader(this._codebase, localJNLP2ClassLoader1);
      if (!Plugin2ClassLoader.setDeployURLClassPathCallbacks(localJNLP2ClassLoader1, localJNLP2ClassLoader2))
        return localJNLP2ClassLoader1;
      return localJNLP2ClassLoader2;
    }
    return localJNLP2ClassLoader1;
  }

  public String getAppletUniqueKey()
  {
    String str = "|";
    if (this._initDocumentBaseURL != null)
      str = str + this._initDocumentBaseURL.toString();
    str = str + "|";
    if (this._initJnlpFile != null)
      str = str + this._initJnlpFile;
    str = str + "|";
    return str;
  }

  protected void collectJnlpProperties()
  {
  }

  public void initialize()
    throws Exception
  {
    super.initialize();
    Plugin2ClassLoader localPlugin2ClassLoader = getAppletClassLoader();
    if (!(localPlugin2ClassLoader instanceof JNLP2ClassLoader))
    {
      localObject1 = new Exception("ClassLoader not JNLP2ClassLoader (" + localPlugin2ClassLoader + ")");
      throw ((Throwable)localObject1);
    }
    if (!isAppletRelaunched())
      Config.get().refreshIfNeeded();
    this.homeJREInfo = JREInfo.getHomeJRE();
    if ((DEBUG) && (VERBOSE))
    {
      Trace.println("JNLP2Manager.initialize(): java.home:" + Environment.getJavaHome() + ", RUnning JRE: " + this.homeJREInfo, TraceLevel.BASIC);
      Trace.println("JREInfos");
      JREInfo.traceJREs();
    }
    if (this.homeJREInfo == null)
      throw new ExitException(new Exception("Internal Error: no running JRE"), 3);
    Object localObject1 = checkForEmbeddedJNLP();
    if (localObject1 == null)
    {
      DeployPerfUtil.put("JNLP2Manager.initialize - before buildDescriptorFromCache()");
      localObject1 = LaunchDescFactory.buildDescriptorFromCache(this._initJnlpFile, this._codebase, this._initDocumentBaseURL);
      if (this._initJnlpFile.endsWith(".jarjnlp"))
        this._launchDesc = ((LaunchDesc)localObject1);
      DeployPerfUtil.put("JNLP2Manager.initialize - after buildDescriptorFromCache()");
    }
    if ((this._launchDesc == null) && (localObject1 != null) && (((LaunchDesc)localObject1).getUpdate().isBackgroundCheck()))
    {
      localObject2 = ResourceProvider.get().getLocalApplicationProperties(((LaunchDesc)localObject1).getCanonicalHome(), null, true);
      if ((localObject2 == null) || (!((LocalApplicationProperties)localObject2).forceUpdateCheck()))
        if (redirectLaunchDesc((LaunchDesc)localObject1, this._initJnlpFile, this._codebase, this._initDocumentBaseURL))
        {
          this._lap = ResourceProvider.get().getLocalApplicationProperties(this._launchDesc.getCanonicalHome(), null, true);
        }
        else
        {
          this._launchDesc = ((LaunchDesc)localObject1);
          this._lap = ((LocalApplicationProperties)localObject2);
        }
    }
    if (this._launchDesc == null)
    {
      this._launchDesc = LaunchDescFactory.buildDescriptor(this._initJnlpFile, this._codebase, this._initDocumentBaseURL, DEBUG);
      if (null != this._launchDesc)
      {
        if ((redirectLaunchDesc(this._launchDesc, this._initJnlpFile, this._codebase, this._initDocumentBaseURL)) && (DEBUG))
          Trace.println("JNLP2Manager.initialize(): JNLP redirect Ref: " + this._launchDesc.getLocation(), TraceLevel.BASIC);
      }
      else if (this._cachedJNLPFilePath != null)
      {
        localObject1 = LaunchDescFactory.buildDescriptor(new File(this._cachedJNLPFilePath), this._codebase, this._initDocumentBaseURL, null);
        if ((localObject1 != null) && ((((LaunchDesc)localObject1).getLocation() == null) || (((LaunchDesc)localObject1).getInformation().supportsOfflineOperation())))
          this._launchDesc = ((LaunchDesc)localObject1);
      }
    }
    if (null == this._launchDesc)
    {
      Trace.println("JNLP2Manager.initialize(): JNLP not available: " + this._initJnlpFile, TraceLevel.BASIC);
      return;
    }
    if ((!this._launchDesc.isApplet()) && (!this._launchDesc.isFXApp()))
    {
      if (Trace.isEnabled(TraceLevel.BASIC))
        Trace.println("JNLP2Manager.initialize(): JNLP not an applet nor JavaFX application: " + this._launchDesc, TraceLevel.BASIC);
      return;
    }
    if (Environment.isImportMode())
    {
      if (Trace.isEnabled(TraceLevel.BASIC))
        Trace.println("JNLP2Manager.initialize(): JNLP import mode not supported: " + this._launchDesc, TraceLevel.BASIC);
      return;
    }
    Object localObject2 = this._launchDesc.getJavaFXAppDescriptor();
    AppletDesc localAppletDesc = this._launchDesc.getAppletDescriptor();
    if ((null == localAppletDesc) && (null == localObject2))
      throw new Exception("initialize - init failed: Both javafx-desc and AppletDesc are null.");
    this._codebase = this._launchDesc.getCodebase();
    this._initialized = true;
    Applet2ExecutionContext localApplet2ExecutionContext = getAppletExecutionContext();
    if (null != localObject2)
      localApplet2ExecutionContext.setAppletParameters(JNLP2Tag.addJnlpJfxParams(localApplet2ExecutionContext.getAppletParameters(), (JavaFXAppDesc)localObject2));
    else
      localApplet2ExecutionContext.setAppletParameters(JNLP2Tag.addJNLParams2Map(localApplet2ExecutionContext.getAppletParameters(), localAppletDesc));
    prepareToLaunch();
    if (null == this._appPolicy)
    {
      Exception localException = new Exception("initialize - init failed: _appPolicy is null");
      throw localException;
    }
  }

  protected void performDesktopIntegration()
  {
    if ((this._launchDesc != null) && ((this._launchDesc.isFXApp()) || (isDisconnectedExecutionContext())))
      installShortcuts();
  }

  protected void loadJarFiles()
    throws ExitException
  {
    try
    {
      getPreloaderDelegate().handleEvent(new ConfigEvent(4, true));
    }
    catch (CancelException localCancelException1)
    {
    }
    if (!this._initialized)
    {
      if (null == this._launchDesc)
      {
        localObject1 = new ExitException(new FileNotFoundException("JNLP file error: " + this._initJnlpFile + ". Please make sure the file exists and check if " + "\"codebase\" and \"href\" in the JNLP file are correct."), 3);
        throw ((Throwable)localObject1);
      }
      if ((!this._launchDesc.isApplet()) && (!this._launchDesc.isFXApp()))
      {
        localObject1 = new ExitException(new LaunchDescException(this._launchDesc, "JNLP not an applet, nor a JavaFX application", new Exception("JNLP not an applet, nor a JavaFX application")), 3);
        throw ((Throwable)localObject1);
      }
      if (Environment.isImportMode())
      {
        localObject1 = new ExitException(new LaunchDescException(this._launchDesc, "JNLP import mode not supported", new Exception("JNLP import mode not supported")), 3);
        throw ((Throwable)localObject1);
      }
      localObject1 = new ExitException(new LaunchDescException(this._launchDesc, "JNLP2Manager not initialized", new Exception("JNLP2Manager not initialized")), 3);
      throw ((Throwable)localObject1);
    }
    Object localObject1 = new DownloadEvent(1, this._launchDesc.getLocation(), null, null, 1L, 1L, 100);
    try
    {
      prepareLaunchFile(this._launchDesc);
      this._launchDesc.getUpdater().startBackgroundUpdateOpt();
    }
    catch (Throwable localThrowable)
    {
      ExitException localExitException = (localThrowable instanceof ExitException) ? (ExitException)localThrowable : new ExitException(localThrowable, 3);
      if (localExitException.getReason() != 0)
        if ((localExitException.getReason() == 2) || (localExitException.isSilentException()))
          localObject1 = null;
        else
          localObject1 = getErrorEvent(this._launchDesc.getLocation(), localExitException);
      throw localExitException;
    }
    finally
    {
      try
      {
        if (localObject1 != null)
          getPreloaderDelegate().handleEvent((PreloaderEvent)localObject1);
      }
      catch (CancelException localCancelException2)
      {
        localCancelException2.printStackTrace();
      }
    }
  }

  public URL getCodeBase()
  {
    return this._codebase;
  }

  public String getCode()
  {
    if (null == this._launchDesc)
      return "<applet error>";
    if (this._launchDesc.isFXApp())
      return this._launchDesc.getJavaFXAppDescriptor().getMainClass();
    if (this._launchDesc.isApplet())
      return this._launchDesc.getAppletDescriptor().getAppletClass();
    return "<applet error>";
  }

  protected String getAppletCode()
  {
    String str = getParameter("mainjavafxscript");
    if (str != null)
      return str;
    return getCode();
  }

  protected void destroyAppContext(AppContext paramAppContext, Applet2StopListener paramApplet2StopListener, long paramLong)
  {
    Object localObject = paramAppContext.get("deploy-launchdownloadthreadpoolinappcontext");
    if (localObject != null)
    {
      ExecutorService localExecutorService = (ExecutorService)localObject;
      localExecutorService.shutdown();
    }
    super.destroyAppContext(paramAppContext, paramApplet2StopListener, paramLong);
  }

  protected void appletSSVRelaunch()
    throws JRESelectException
  {
    if (this._relaunchException != null)
      throw this._relaunchException;
  }

  protected void checkRunningJVMToolkitSatisfying()
    throws JRESelectException
  {
    int i = (!this._launchDesc.isFXApp()) && (ToolkitStore.isUsingPreferredToolkitType(10)) ? 1 : 0;
    int j = (this._launchDesc.isFXApp()) && (ToolkitStore.isUsingPreferredToolkitType(11)) ? 1 : 0;
    if ((i != 0) || (j != 0))
      return;
    JREInfo localJREInfo = this._launchDesc.selectJRE(new SecureMatchJRE());
    if ((localJREInfo != null) && (localJREInfo.getJfxRuntime() != null))
      throw new JRESelectException(null, null);
  }

  protected void checkRunningJVMArgsSatisfying()
    throws JRESelectException
  {
    MatchJREIf localMatchJREIf = this._launchDesc.getJREMatcher();
    if (((!localMatchJREIf.isRunningJVMArgsSatisfying(this._allSigned)) || (!JVMHealthData.getCurrent().isHealthy())) && (this._relaunchException != null))
      throw new JRESelectException(null, this._relaunchException.getJVMArgs());
  }

  public void clearRelaunchException()
  {
    this._relaunchException = null;
  }

  public LaunchDesc getLaunchDesc()
  {
    return this._launchDesc;
  }

  public String getDraggedTitle()
  {
    String str = super.getDraggedTitleParam();
    if ((str == null) && (this._launchDesc != null))
    {
      InformationDesc localInformationDesc = this._launchDesc.getInformation();
      if (localInformationDesc != null)
        str = localInformationDesc.getTitle();
    }
    return str;
  }

  public void setDraggedApplet()
  {
    if (this._launchDesc == null)
      return;
    LocalApplicationProperties localLocalApplicationProperties = ResourceProvider.get().getLocalApplicationProperties(this._launchDesc.getCanonicalHome(), null, true);
    if (localLocalApplicationProperties != null)
      localLocalApplicationProperties.setDraggedApplet();
  }

  boolean getDecoratedDefault()
  {
    if (this._launchDesc == null)
      return true;
    LocalApplicationProperties localLocalApplicationProperties = ResourceProvider.get().getLocalApplicationProperties(this._launchDesc.getCanonicalHome(), null, true);
    if (localLocalApplicationProperties == null)
      return true;
    return !localLocalApplicationProperties.isDraggedApplet();
  }

  String getDecoratedPreference()
  {
    if (this._launchDesc != null)
    {
      AppletDesc localAppletDesc = this._launchDesc.getAppletDescriptor();
      if (localAppletDesc != null)
      {
        String str = localAppletDesc.getParameters().getProperty("java_decorated_frame");
        if (str != null)
          return str;
      }
    }
    return getParameter("java_decorated_frame");
  }

  protected void setupProgress()
  {
  }

  private boolean redirectLaunchDesc(LaunchDesc paramLaunchDesc, String paramString, URL paramURL1, URL paramURL2)
    throws ExitException
  {
    try
    {
      URL localURL1 = paramLaunchDesc.getLocation();
      if (localURL1 != null)
      {
        URL localURL2 = null;
        try
        {
          localURL2 = new URL(paramString);
        }
        catch (Exception localException2)
        {
        }
        if ((localURL2 != null) && (localURL2.toString().equals(localURL1.toString())))
          return false;
        if (paramURL1 != null)
        {
          try
          {
            localURL2 = new URL(paramURL1, paramString);
            if (!URLUtil.checkTargetURL(paramURL1, localURL2))
              throw new SecurityException("Permission denied: " + localURL2);
          }
          catch (Exception localException3)
          {
            if (DEBUG)
            {
              System.out.println(localException3);
              localException3.printStackTrace();
            }
            localURL2 = null;
          }
          if ((localURL2 != null) && (localURL2.toString().equals(localURL1.toString())))
            return false;
        }
        if ((paramURL1 == null) && (paramURL2 != null))
        {
          try
          {
            localURL2 = new URL(URLUtil.getBase(paramURL2), paramString);
            if (!URLUtil.checkTargetURL(URLUtil.getBase(paramURL2), localURL2))
              throw new SecurityException("Permission denied: " + localURL2);
          }
          catch (Exception localException4)
          {
            if (DEBUG)
            {
              System.out.println(localException4);
              localException4.printStackTrace();
            }
            localURL2 = null;
          }
          if ((localURL2 != null) && (localURL2.toString().equals(localURL1.toString())))
            return false;
        }
        this._launchDesc = LaunchDescFactory.buildDescriptor(localURL1.toString(), paramURL1, paramURL2, DEBUG);
        return true;
      }
      return false;
    }
    catch (Exception localException1)
    {
      if (DEBUG)
      {
        System.out.println(localException1);
        localException1.printStackTrace();
      }
      throw new ExitException(localException1, 3);
    }
  }

  private void prepareToLaunch()
    throws ExitException
  {
    try
    {
      if (this._launchDesc == null)
        throw new ExitException("No launch descriptor to use", null);
      this._lap = ResourceProvider.get().getLocalApplicationProperties(this._launchDesc.getCanonicalHome(), null, true);
      URL localURL = this._launchDesc.getLocation();
      if (localURL != null)
        Cache.removeRemovedApp(localURL.toString(), this._launchDesc.getInformation().getTitle());
      if (this._launchDesc.getResources() != null)
        Globals.getDebugOptionsFromProperties(this._launchDesc.getResources().getResourceProperties());
      this._appPolicy = AppPolicy.createInstance(this._launchDesc.getCanonicalHome().getHost());
    }
    catch (Throwable localThrowable)
    {
      ExitException localExitException = (localThrowable instanceof ExitException) ? (ExitException)localThrowable : new ExitException(localThrowable, 3);
      int i = localExitException.getReason();
      if (i == 3)
      {
        setErrorOccurred(localExitException.getException().getMessage(), localExitException.getException());
        showAppletException(localExitException);
      }
      if (i != 0)
        throw localExitException;
    }
  }

  private URL validateLaunchDesc(LaunchDesc paramLaunchDesc)
    throws ExitException
  {
    if (paramLaunchDesc.getResources() == null)
      handleJnlpFileException(paramLaunchDesc, new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.noappresources", paramLaunchDesc.getSpecVersion()), null));
    if (!paramLaunchDesc.isJRESpecified())
    {
      localObject = new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.missingjreversion"), null);
      handleJnlpFileException(paramLaunchDesc, (Exception)localObject);
    }
    JNLPException.setDefaultLaunchDesc(paramLaunchDesc);
    if ((!paramLaunchDesc.getInformation().supportsOfflineOperation()) && (DeployOfflineManager.isGlobalOffline() == true))
      throw new ExitException(new OfflineLaunchException(1), 3);
    Object localObject = paramLaunchDesc.getCanonicalHome();
    if (localObject == null)
    {
      LaunchDescException localLaunchDescException = new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.nomainjar"), null);
      throw new ExitException(localLaunchDescException, 3);
    }
    return localObject;
  }

  private boolean ensureAllJnlpFilesAreAvailable(LaunchDesc paramLaunchDesc, ArrayList paramArrayList)
    throws ExitException
  {
    boolean bool = false;
    LaunchDownload localLaunchDownload = new LaunchDownload(paramLaunchDesc);
    if ((paramLaunchDesc.getUpdate().isBackgroundCheck()) && ((this._lap == null) || (!this._lap.forceUpdateCheck())))
      bool = localLaunchDownload.isInCache();
    if (!bool)
      try
      {
        localLaunchDownload.downloadExtensions(null, 0, paramArrayList);
      }
      catch (Exception localException)
      {
        if ((paramLaunchDesc.getInformation().supportsOfflineOperation()) && (localLaunchDownload.isInCache()))
          bool = true;
        else
          throw new ExitException(localException, 3);
      }
    return bool;
  }

  boolean isJfxSupportSatisfiedImpl(ClassLoader paramClassLoader, LaunchDesc paramLaunchDesc)
  {
    if (!paramLaunchDesc.needFX())
      return true;
    if ((paramLaunchDesc.isFXApp()) && (!ToolkitStore.isUsingPreferredToolkit(11, 1)))
      return false;
    return JfxHelper.isJfxSupportSatisfied(paramClassLoader, paramLaunchDesc);
  }

  private boolean isJfxSupportSatisfied(LaunchDesc paramLaunchDesc)
  {
    return isJfxSupportSatisfiedImpl(null, paramLaunchDesc);
  }

  private void prepareLaunchFile(LaunchDesc paramLaunchDesc)
    throws ExitException
  {
    JnlpLaunchState localJnlpLaunchState = new JnlpLaunchState(paramLaunchDesc);
    URL localURL = validateLaunchDesc(paramLaunchDesc);
    paramLaunchDesc.setPropsSet(true);
    JNLP2ClassLoader localJNLP2ClassLoader = (JNLP2ClassLoader)getOrCreatePlugin2ClassLoader();
    ArrayList localArrayList = new ArrayList();
    boolean bool1 = ensureAllJnlpFilesAreAvailable(paramLaunchDesc, localArrayList);
    boolean bool2 = (!paramLaunchDesc.getUpdate().isBackgroundCheck()) && (!paramLaunchDesc.getUpdate().isPromptPolicy()) && (!isAppletRelaunched());
    localJNLP2ClassLoader.initialize(paramLaunchDesc, this._appPolicy);
    final JNLPSignedResourcesHelper localJNLPSignedResourcesHelper = new JNLPSignedResourcesHelper(paramLaunchDesc);
    final PreloaderDelegate localPreloaderDelegate1 = getPreloaderDelegate();
    Runnable local1 = new Runnable()
    {
      private final JNLPSignedResourcesHelper val$signingHelper;
      private final PreloaderDelegate val$preloader;

      public void run()
      {
        try
        {
          boolean bool = localJNLPSignedResourcesHelper.checkSignedResources(JNLP2Manager.this.getPreloaderDelegate(), true);
          Trace.println("Security check for progress jars: allSigned=" + bool, TraceLevel.SECURITY);
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
        localPreloaderDelegate1.initPreloader(JNLP2Manager.this.loader, JNLP2Manager.this.appletThreadGroup);
      }
    };
    Runnable local2 = new Runnable()
    {
      private final PreloaderDelegate val$preloader;

      public void run()
      {
        localPreloaderDelegate1.setPreloaderClass(null);
        localPreloaderDelegate1.initPreloader(JNLP2Manager.this.loader, JNLP2Manager.this.appletThreadGroup);
      }
    };
    new LaunchDownload(paramLaunchDesc).prepareCustomProgress(localPreloaderDelegate1, localJNLPSignedResourcesHelper, local1, local2, (!bool1) || (bool2));
    boolean bool3 = false;
    bool3 = downloadResources(localJNLP2ClassLoader, paramLaunchDesc, localArrayList, bool1, bool2, true);
    paramLaunchDesc.selectJRE();
    if (bool3)
      resetForceUpdateCheck();
    Trace.println("LaunchDesc location: " + localURL, TraceLevel.BASIC);
    boolean bool4 = localJnlpLaunchState.isHomeJvmMatch();
    try
    {
      localJnlpLaunchState.doJvmSelection();
    }
    catch (LaunchDescException localLaunchDescException1)
    {
      throw new ExitException(localLaunchDescException1, 2);
    }
    boolean bool5 = localJnlpLaunchState.jreInfo == null;
    boolean bool6 = DeployOfflineManager.isGlobalOffline();
    if ((bool5) && (bool6))
      throw new ExitException(new OfflineLaunchException(0), 3);
    boolean bool7 = bool5;
    if ((!bool5) && (!bool3) && (!paramLaunchDesc.getUpdate().isBackgroundCheck()) && (!isAppletRelaunched()) && (!bool6))
      if ((this._lap != null) && (this._lap.forceUpdateCheck()))
      {
        if (DEBUG)
          Trace.println("Forced update check in LAP, do full update", TraceLevel.BASIC);
        bool7 = true;
      }
      else
      {
        try
        {
          bool7 = paramLaunchDesc.getUpdater().isUpdateAvailable();
        }
        catch (Exception localException1)
        {
          throw new ExitException(localException1, 3);
        }
        if (paramLaunchDesc.getUpdater().isCheckAborted())
          throw new ExitException(new LaunchDescException(paramLaunchDesc, "User rejected cert - aborted", null), 4);
      }
    if (DEBUG)
      Trace.println("\n\tisRelaunch: " + isAppletRelaunched() + "\n\tOffline mode: " + bool6 + "\n\tforceUpdate: " + bool5 + "\n\tneedUpdate: " + bool7 + "\n\tbgrUpdCheck: " + paramLaunchDesc.getUpdate().isBackgroundCheck() + "\n\tbgrUpdThread: " + paramLaunchDesc.getUpdater().isBackgroundUpdateRunning() + "\n\tRunning  JREInfo: " + this.homeJREInfo + "\n\t" + localJnlpLaunchState.jreMatcher, TraceLevel.BASIC);
    if ((bool7) && (!bool5))
      bool5 = paramLaunchDesc.getUpdater().needUpdatePerPolicy();
    if (bool5)
      localJnlpLaunchState.updateResources(localJNLP2ClassLoader, localArrayList, bool1, true);
    PreloaderDelegate localPreloaderDelegate2 = getPreloaderDelegate();
    try
    {
      localPreloaderDelegate2.waitTillLoaded();
    }
    catch (JNLPException localJNLPException1)
    {
      throw new ExitException(localJNLPException1, 3);
    }
    catch (IOException localIOException1)
    {
      if ((paramLaunchDesc.getInformation().supportsOfflineOperation()) && (new LaunchDownload(paramLaunchDesc).isInCache(true)))
        Trace.ignoredException(localIOException1);
      else
        throw new ExitException(localIOException1, 3);
    }
    if ((localJnlpLaunchState.jreInstalled) && (bool4))
      throw new ExitException(new Exception("Internal Error: jreInstalled, but homeJVM matches"), 3);
    if (bool4)
      localJnlpLaunchState.doInstallers(localArrayList);
    if (!paramLaunchDesc.isValidSpecificationVersion())
    {
      JNLPException.setDefaultLaunchDesc(paramLaunchDesc);
      handleJnlpFileException(paramLaunchDesc, new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.badjnlversion", paramLaunchDesc.getSpecVersion()), null));
    }
    if (localJnlpLaunchState.jreInstalled)
    {
      try
      {
        localJnlpLaunchState.doJvmSelection();
      }
      catch (LaunchDescException localLaunchDescException2)
      {
        throw new ExitException(localLaunchDescException2, 2);
      }
      if (localJnlpLaunchState.jreInfo != null)
      {
        localJnlpLaunchState.relaunchApplet(false);
        return;
      }
      localObject = null;
      if (bool6)
        throw new ExitException(new OfflineLaunchException(0), 3);
      try
      {
        localObject = JfxHelper.installJfxRuntime(paramLaunchDesc, getPreloaderDelegate());
      }
      catch (DownloadException localDownloadException)
      {
        throw new ExitException(ResourceManager.getMessage("launch.error.jfx.download"), localDownloadException);
      }
      catch (Throwable localThrowable)
      {
        throw new ExitException(localThrowable, 3);
      }
      setParameter("__jfx_installed", String.valueOf(true));
      try
      {
        localJnlpLaunchState.doJvmSelection();
      }
      catch (LaunchDescException localLaunchDescException3)
      {
        throw new ExitException(localLaunchDescException3, 2);
      }
      if (localJnlpLaunchState.jreInfo != null)
      {
        localJnlpLaunchState.relaunchApplet(false);
        return;
      }
    }
    Object localObject = JVMHealthData.getCurrent();
    if (DEBUG)
      Trace.println("Checking current JVM health: " + localObject, TraceLevel.BASIC);
    int i = !ToolkitStore.isUsingPreferredToolkitType(paramLaunchDesc.isFXApp() ? 11 : 10) ? 1 : 0;
    if ((paramLaunchDesc.isSecureJVMArgs()) && ((localJnlpLaunchState.installerRelaunch) || (!bool4) || (i != 0) || (!((JVMHealthData)localObject).isHealthy())))
    {
      Trace.println("Relaunch because: " + (localJnlpLaunchState.installerRelaunch ? "[installer demands a relaunch] " : "") + (bool4 ? "" : "[currently running JRE doesn't satisfy (version/args)] ") + (i != 0 ? "[current UI toolkit does not match] " : "") + (((JVMHealthData)localObject).isHealthy() ? "" : "[unhealthy JVM state] "), TraceLevel.BASIC);
      localJnlpLaunchState.relaunchApplet(false);
    }
    boolean bool8 = false;
    try
    {
      bool8 = localJNLPSignedResourcesHelper.checkSignedResources(getPreloaderDelegate(), false);
      localJNLPSignedResourcesHelper.checkSignedLaunchDesc(this._codebase, this._initDocumentBaseURL);
      bool8 = (bool8) && (paramLaunchDesc.isSigned());
      this._allSigned = bool8;
    }
    catch (JNLPException localJNLPException2)
    {
      if (DEBUG)
        Trace.ignored(localJNLPException2);
      throw new ExitException(localJNLPException2, 3);
    }
    catch (IOException localIOException2)
    {
      throw new ExitException(localIOException2, 3);
    }
    catch (ExitException localExitException)
    {
      throw localExitException;
    }
    catch (Exception localException2)
    {
      if (DEBUG)
        Trace.ignored(localException2);
      throw new ExitException(localException2, 3);
    }
    Trace.println("passing security checks; secureArgs:" + paramLaunchDesc.isSecureJVMArgs() + ", allSigned:" + bool8, TraceLevel.BASIC);
    bool4 = localJnlpLaunchState.jreMatcher.isRunningJVMSatisfying(bool8);
    if ((localJnlpLaunchState.installerRelaunch) || (!bool4) || (!((JVMHealthData)localObject).isHealthy()))
      localJnlpLaunchState.relaunchApplet(bool8);
    Trace.println("continuing launch in this VM", TraceLevel.BASIC);
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc != null)
    {
      JARDesc[] arrayOfJARDesc = localResourcesDesc.getEagerOrAllJarDescs(true);
      for (int j = 0; j < arrayOfJARDesc.length; j++)
        storeJarVersionMapInAppContext(arrayOfJARDesc[j]);
    }
  }

  private boolean downloadResources(JNLP2ClassLoader paramJNLP2ClassLoader, LaunchDesc paramLaunchDesc, ArrayList paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws ExitException
  {
    boolean bool1 = false;
    boolean bool2 = (paramBoolean2) || (!paramBoolean1);
    if (DEBUG)
      Trace.println("JNLP2Manager.downloadResources(): updateCache " + paramBoolean2 + ", allInCache " + paramBoolean1 + ", doDownload " + bool2, TraceLevel.BASIC);
    if (!bool2)
      return bool1;
    try
    {
      getPreloaderDelegate().handleEvent(new ConfigEvent(3, getLaunchDesc().getAppInfo()));
      getPreloaderDelegate().handleEvent(new InitEvent(4));
    }
    catch (CancelException localCancelException)
    {
    }
    LaunchDownload localLaunchDownload = new LaunchDownload(paramLaunchDesc);
    try
    {
      if (!paramBoolean3)
        localLaunchDownload.downloadExtensions(getPreloaderDelegate(), 0, paramArrayList);
      paramJNLP2ClassLoader.updateJarDescriptors(paramLaunchDesc.getResources());
      localLaunchDownload.downloadEagerorAll(false, getPreloaderDelegate(), false);
      bool1 = true;
    }
    catch (SecurityException localSecurityException)
    {
      throw new ExitException(localSecurityException, 3);
    }
    catch (JNLPException localJNLPException)
    {
      throw new ExitException(localJNLPException, 3);
    }
    catch (IOException localIOException)
    {
      if ((paramLaunchDesc.getInformation().supportsOfflineOperation()) && (paramBoolean1))
      {
        Trace.ignoredException(localIOException);
        return bool1;
      }
      throw new ExitException(localIOException, 3);
    }
    return bool1;
  }

  private void resetForceUpdateCheck()
  {
    if ((this._lap != null) && (this._lap.forceUpdateCheck()))
    {
      this._lap.setForceUpdateCheck(false);
      try
      {
        this._lap.store();
      }
      catch (IOException localIOException)
      {
        Trace.ignoredException(localIOException);
      }
    }
  }

  private void handleJnlpFileException(LaunchDesc paramLaunchDesc, Exception paramException)
    throws ExitException
  {
    if (paramLaunchDesc == null)
      return;
    Resource localResource = ResourceProvider.get().getCachedResource(paramLaunchDesc.getCanonicalHome(), null);
    if (localResource != null)
      ResourceProvider.get().markRetired(localResource, true);
    throw new ExitException(paramException, 3);
  }

  private void downloadJREResource(LaunchDesc paramLaunchDesc, JREDesc paramJREDesc, ArrayList paramArrayList, Preloader paramPreloader)
    throws ExitException
  {
    Trace.println("downloadJREResource ...", TraceLevel.BASIC);
    try
    {
      paramPreloader.handleEvent(new ConfigEvent(3, getLaunchDesc().getAppInfo()));
      paramPreloader.handleEvent(new InitEvent(0));
      paramPreloader.handleEvent(new ConfigEvent(4, true));
    }
    catch (CancelException localCancelException)
    {
      throw new ExitException(localCancelException, 3);
    }
    try
    {
      if (Cache.isCacheEnabled())
      {
        LaunchDownload localLaunchDownload = new LaunchDownload(paramLaunchDesc);
        localLaunchDownload.downloadJRE(paramPreloader, paramArrayList);
      }
      else
      {
        throw new IOException("Cache disabled, cannot download JRE");
      }
    }
    catch (SecurityException localSecurityException)
    {
      throw new ExitException(localSecurityException, 3);
    }
    catch (JNLPException localJNLPException)
    {
      throw new ExitException(localJNLPException, 3);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
      throw new ExitException(new NoLocalJREException(paramLaunchDesc, paramLaunchDesc.getResources().getSelectedJRE().getVersion(), false), 3);
    }
    Trace.println("downloadJREResource fin", TraceLevel.BASIC);
  }

  private void storeJarVersionMapInAppContext(JARDesc paramJARDesc)
  {
    if ((null == paramJARDesc) || (null == this.appletAppContext))
      return;
    URL localURL = paramJARDesc.getLocation();
    if (localURL != null)
      this.appletAppContext.put("deploy-" + localURL.toString(), paramJARDesc.getVersion());
  }

  protected String getCodeSourceLocations()
  {
    if (!this._initialized)
      return null;
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(this._launchDesc);
    ResourcesDesc localResourcesDesc = this._launchDesc.getResources();
    if (localResourcesDesc != null)
    {
      localArrayList.addAll(Arrays.asList(localResourcesDesc.getEagerOrAllJarDescs(true)));
      localArrayList.addAll(Arrays.asList(localResourcesDesc.getExtensionDescs()));
    }
    return buildJarList(localArrayList.toArray(new Object[localArrayList.size()]));
  }

  protected String getJarFiles()
  {
    if (!this._initialized)
      return null;
    ResourcesDesc localResourcesDesc = this._launchDesc.getResources();
    if (localResourcesDesc != null)
      return buildJarList(localResourcesDesc.getEagerOrAllJarDescs(true));
    return null;
  }

  protected static String buildJarList(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
      return null;
    StringBuffer localStringBuffer = null;
    int i = 0;
    for (int j = 0; j < paramArrayOfObject.length; j++)
    {
      String str = null;
      if ((paramArrayOfObject[j] instanceof LaunchDesc))
      {
        if (((LaunchDesc)paramArrayOfObject[j]).getLocation() != null)
          str = ((LaunchDesc)paramArrayOfObject[j]).getLocation().toString();
      }
      else if ((paramArrayOfObject[j] instanceof JARDesc))
      {
        if (((JARDesc)paramArrayOfObject[j]).getLocation() != null)
          str = ((JARDesc)paramArrayOfObject[j]).getLocation().toString();
      }
      else
      {
        if (!(paramArrayOfObject[j] instanceof ExtensionDesc))
          continue;
        if (((ExtensionDesc)paramArrayOfObject[j]).getLocation() != null)
          str = ((ExtensionDesc)paramArrayOfObject[j]).getLocation().toString();
      }
      if (str != null)
      {
        if (localStringBuffer == null)
          localStringBuffer = new StringBuffer();
        if (i != 0)
          localStringBuffer.append(",");
        localStringBuffer.append(str);
        i = 1;
      }
    }
    if (localStringBuffer == null)
      return null;
    return localStringBuffer.toString();
  }

  public static void initializeExecutionEnvironment()
    throws JNLPException
  {
    if (_environmentInitialized)
      return;
    long l = DeployPerfUtil.put(0L, "JNLP2Manager - initializeExecutionEnvironment() - BEGIN");
    if (!Cache.canWrite())
    {
      localObject = new CacheAccessException(Environment.isSystemCacheMode());
      throw ((Throwable)localObject);
    }
    Object localObject = System.getProperties();
    ((Properties)localObject).setProperty("javawebstart.version", Globals.getComponentName());
    ServiceManager.setServiceManagerStub(new JnlpLookupStub());
    Config.setupPackageAccessRestriction();
    UIFactory.setDialogListener(new JavawsDialogListener());
    if (DeployOfflineManager.isGlobalOffline())
      DeployOfflineManager.setForcedOffline(true);
    if (Environment.isSystemCacheMode())
    {
      CacheUpdateHelper.systemUpdateCheck();
    }
    else if ((Config.getBooleanProperty("deployment.javaws.cache.update")) && (CacheUpdateHelper.updateCache()))
    {
      Config.setBooleanProperty("deployment.javaws.cache.update", false);
      Config.get().storeIfNeeded();
    }
    _environmentInitialized = true;
    DeployPerfUtil.put(l, "JNLP2Manager - initializeExecutionEnvironment() - END");
  }

  protected boolean useGrayBoxProgressListener()
  {
    return false;
  }

  public void installShortcuts()
  {
    startWorkerThread("Shortcut Installer Thread", new Runnable()
    {
      public void run()
      {
        URL localURL = JNLP2Manager.this._launchDesc.getLocation();
        LocalApplicationProperties localLocalApplicationProperties = ResourceProvider.get().getLocalApplicationProperties(localURL, null, true);
        if (localLocalApplicationProperties != null)
        {
          localLocalApplicationProperties.setLastAccessed(new Date());
          localLocalApplicationProperties.incrementLaunchCount();
          if (JNLP2Manager.this._initDocumentBaseURL != null)
            localLocalApplicationProperties.setDocumentBase(JNLP2Manager.this._initDocumentBaseURL.toString());
          if (JNLP2Manager.this._codebase != null)
            localLocalApplicationProperties.setCodebase(JNLP2Manager.this._codebase.toString());
          LocalInstallHandler localLocalInstallHandler = LocalInstallHandler.getInstance();
          if ((localLocalInstallHandler != null) && (!localLocalInstallHandler.isShortcutExists(localLocalApplicationProperties)))
          {
            if ((!JNLP2Manager.this._launchDesc.isFXApp()) && (!JNLP2Manager.this.isDisconnectedExecutionContext()))
              localLocalApplicationProperties.setAskedForInstall(false);
            localLocalInstallHandler.install(JNLP2Manager.this._launchDesc, localLocalApplicationProperties, true, false, null);
          }
          try
          {
            localLocalApplicationProperties.store();
          }
          catch (IOException localIOException)
          {
            Trace.ignored(localIOException);
          }
        }
      }
    });
  }

  private LaunchDesc checkForEmbeddedJNLP()
  {
    if (this._initJnlpBytes == null)
      return null;
    LaunchDesc localLaunchDesc = null;
    try
    {
      URL localURL = this._codebase;
      if (this._initJnlpFile != null)
        try
        {
          localURL = new URL(this._initDocumentBaseURL, this._initJnlpFile);
          localURL = URLUtil.asPathURL(URLUtil.getBase(localURL));
        }
        catch (MalformedURLException localMalformedURLException)
        {
          Trace.ignored(localMalformedURLException);
        }
      if (localURL == null)
        localURL = this._codebase;
      localLaunchDesc = LaunchDescFactory.buildDescriptor(this._initJnlpBytes, localURL, this._initDocumentBaseURL);
      if (DEBUG)
        System.out.println("buildDescriptor(embeddedJnlp) returns " + localLaunchDesc);
      if (localLaunchDesc != null)
      {
        new EmbeddedJNLPValidation(localLaunchDesc, localURL).validate();
        localLaunchDesc = LaunchDownload.updateLaunchDescInCache(localLaunchDesc, localURL, this._initDocumentBaseURL);
        this._launchDesc = localLaunchDesc;
      }
    }
    catch (Exception localException)
    {
      localLaunchDesc = null;
      Trace.println("Ignore exception processing jnlp_embedded:", TraceLevel.BASIC);
      Trace.println(new String(this._initJnlpBytes), TraceLevel.BASIC);
      Trace.ignoredException(localException);
    }
    this._initJnlpBytes = null;
    return localLaunchDesc;
  }

  private class JnlpLaunchState
  {
    LaunchDesc ld;
    boolean jreInstalled;
    boolean installerRelaunch;
    private MatchJREIf jreMatcher;
    private JREDesc jreDesc;
    private JREInfo jreInfo;
    private boolean homeJVMMatch;
    private boolean homeJVMVersionMatch;
    private String bestJREVersion = null;

    JnlpLaunchState(LaunchDesc arg2)
    {
      Object localObject;
      this.ld = localObject;
      this.jreInstalled = false;
      this.installerRelaunch = false;
    }

    boolean isHomeJvmMatch()
    {
      this.jreMatcher = this.ld.getJREMatcher();
      if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
      {
        Trace.println("JnlpLaunchState.isHomeJvmMatch: JRE matcher:");
        Trace.println(this.jreMatcher.toString());
      }
      this.homeJVMMatch = this.jreMatcher.isRunningJVMSatisfying(true);
      return this.homeJVMMatch;
    }

    String findBestJreVersion()
    {
      if (null == this.bestJREVersion)
      {
        this.homeJVMVersionMatch = this.jreMatcher.isRunningJVMVersionSatisfying();
        if (!this.homeJVMVersionMatch)
        {
          JavaFXRuntimeDesc localJavaFXRuntimeDesc = this.ld.getJavaFXRuntimeDescriptor();
          this.bestJREVersion = JNLP2Manager.this.fireGetBestJREVersion(this.jreDesc.getVersion(), localJavaFXRuntimeDesc == null ? null : localJavaFXRuntimeDesc.getVersion());
        }
        if (this.bestJREVersion == null)
          this.bestJREVersion = this.jreDesc.getVersion();
      }
      return this.bestJREVersion;
    }

    void doJvmSelection()
      throws ExitException, LaunchDescException
    {
      this.jreInfo = this.ld.getSelectedJRE();
      if (this.jreInfo == null)
      {
        Vector localVector = Platform.get().getInstalledJREList();
        if (localVector != null)
          Config.get().storeInstalledJREs(localVector);
        if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
        {
          Trace.println("JnlpLaunchState.doJvmSelection: Refreshed JREInfo list:");
          JREInfo.traceJREs();
        }
        this.jreInfo = this.ld.selectJRE(true);
        if (this.jreInfo != null)
        {
          if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
          {
            Trace.println("JnlpLaunchState.doJvmSelection: Found a new match JRE:");
            Trace.println(this.jreInfo.toString());
          }
          this.jreInstalled = true;
        }
      }
      this.jreDesc = this.ld.getResources().getSelectedJRE();
      if ((this.jreInfo == null) && (this.jreDesc == null))
      {
        Trace.println(this.jreMatcher.toString());
        throw new ExitException(new Exception("Internal Error: Internal error, no JREDesc and no JREInfo"), 3);
      }
      if (this.jreInfo == null)
      {
        JNLP2Manager.this.setParameter("__applet_request_version", this.jreDesc.getVersion());
        this.jreInfo = this.ld.selectJRE(new SecureMatchJRE());
      }
      if (this.ld.needFX())
        if (this.jreInfo != null)
          JfxHelper.validateJfxRequest(this.ld, this.jreInfo);
        else
          JfxHelper.validateJfxRequest(this.ld, this.jreDesc);
    }

    void updateResources(JNLP2ClassLoader paramJNLP2ClassLoader, ArrayList paramArrayList, boolean paramBoolean1, boolean paramBoolean2)
      throws ExitException
    {
      if ((!JNLP2Manager.this.isAppletRelaunched()) && (this.jreInfo == null))
      {
        JNLP2Manager.this.downloadJREResource(this.ld, this.jreDesc, paramArrayList, JNLP2Manager.this.getPreloaderDelegate());
        if (!paramArrayList.isEmpty())
        {
          try
          {
            JnlpxArgs.executeInstallers(paramArrayList, JNLP2Manager.this.getPreloaderDelegate());
          }
          catch (ExitException localExitException)
          {
            this.installerRelaunch = (localExitException.getReason() == 1);
            if ((!this.installerRelaunch) && (localExitException.isErrorException()))
              throw new ExitException(null, 0);
            Trace.ignoredException(localExitException);
          }
          if (!this.ld.isValidSpecificationVersion())
            Platform.get().resetJavaHome();
          String str = Environment.getJavaHome() + File.separator + "bin" + File.separator;
          Platform.get().notifyJREInstalled(str);
          this.jreInstalled = true;
        }
      }
      else
      {
        JNLP2Manager.this.downloadResources(paramJNLP2ClassLoader, this.ld, paramArrayList, paramBoolean1, true, paramBoolean2);
      }
      JNLP2Manager.this.resetForceUpdateCheck();
    }

    void doInstallers(ArrayList paramArrayList)
      throws ExitException
    {
      if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
        Trace.println("JnlpLaunchState.doInstallers(): SingleInstanceManager ?: " + this.ld.getCanonicalHome().toString());
      if (SingleInstanceManager.isServerRunning(this.ld.getCanonicalHome().toString()))
      {
        String[] arrayOfString = Globals.getApplicationArgs();
        if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
          Trace.println("JnlpLaunchState.doInstallers(): SingleInstanceManager: Running with appArgs: " + arrayOfString + ", thread: " + Thread.currentThread());
        Object localObject;
        if (arrayOfString != null)
        {
          localObject = this.ld.getApplicationDescriptor();
          if (localObject != null)
            ((ApplicationDesc)localObject).setArguments(arrayOfString);
        }
        if (SingleInstanceManager.connectToServer(this.ld.toString()))
        {
          if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
            Trace.println("JnlpLaunchState.doInstallers(): SingleInstanceManager: OK from server");
          localObject = "Single Instance already exist: " + this.ld.getCanonicalHome().toString();
          Exception localException = new Exception((String)localObject);
          JNLP2Manager.this.setErrorOccurred((String)localObject, localException);
          throw new ExitException(localException, 5);
        }
        if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
          Trace.println("JnlpLaunchState.doInstallers(): SingleInstanceManager: NOK from server");
      }
      else if ((Plugin2Manager.DEBUG) && (Plugin2Manager.VERBOSE))
      {
        Trace.println("JnlpLaunchState.doInstallers(): No SingleInstanceManager, thread: " + Thread.currentThread());
      }
      if (!paramArrayList.isEmpty())
        try
        {
          JnlpxArgs.executeInstallers(paramArrayList, JNLP2Manager.this.getPreloaderDelegate());
        }
        catch (ExitException localExitException)
        {
          if (localExitException.getReason() != 1)
            throw localExitException;
          this.installerRelaunch = true;
        }
    }

    private void relaunchApplet(boolean paramBoolean)
      throws JRESelectException, ExitException
    {
      JREDesc localJREDesc = null;
      findBestJreVersion();
      if (this.jreInstalled)
        JNLP2Manager.this.setParameter("__jre_installed", String.valueOf(true));
      if (!this.homeJVMVersionMatch)
      {
        if (JNLP2Manager.this.getParameter("__applet_request_version") == null)
          JNLP2Manager.this.setParameter("__applet_ssv_version", this.bestJREVersion);
        localJREDesc = new JREDesc(this.bestJREVersion, 0L, 0L, this.jreDesc.getVmArgs(), null, null);
      }
      JavaFXRuntimeDesc localJavaFXRuntimeDesc = this.ld.getJavaFXRuntimeDescriptor();
      if (localJavaFXRuntimeDesc != null)
      {
        JNLP2Manager.this.setParameter("javafx_version", localJavaFXRuntimeDesc.getVersion());
        if (!this.ld.isFXApp())
        {
          JNLP2Manager.this.setParameter("__ui_tk", "awt");
          if (Plugin2Manager.DEBUG)
            Trace.println("Use AWT TK along with FX", TraceLevel.BASIC);
        }
      }
      if (Plugin2Manager.DEBUG)
      {
        Trace.println("JRESelectException: installerRelaunch: " + this.installerRelaunch, TraceLevel.BASIC);
        Trace.println("JRESelectException: jreInstalled: " + this.jreInstalled, TraceLevel.BASIC);
        Trace.println("JRESelectException: running JREInfo: " + JNLP2Manager.this.homeJREInfo, TraceLevel.BASIC);
        Trace.println("JRESelectException: " + this.jreMatcher, TraceLevel.BASIC);
      }
      if (!JNLP2Manager.this.isAppletRelaunched())
      {
        List localList = this.jreMatcher.getSelectedJVMParameters().getCommandLineArguments(false, false, false, paramBoolean, false, Config.getMaxCommandLineLength());
        String str = StringQuoteUtil.getStringByCommandList(localList);
        if (JNLP2Manager.this.fireAppletRelaunchSupported())
        {
          JRESelectException localJRESelectException = new JRESelectException(localJREDesc, str);
          if (this.homeJVMVersionMatch)
            throw localJRESelectException;
          Trace.println("jre version mismatch, delay relaunch to SSVValidation", TraceLevel.BASIC);
          JNLP2Manager.this._relaunchException = localJRESelectException;
        }
        else
        {
          Trace.println("JRESelectException: ignored - relaunch not supported", TraceLevel.BASIC);
        }
      }
      else
      {
        Trace.println("JRESelectException: ignored - relaunched already", TraceLevel.BASIC);
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.JNLP2Manager
 * JD-Core Version:    0.6.2
 */