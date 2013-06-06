package com.sun.javaws;

import com.sun.applet2.preloader.CancelException;
import com.sun.applet2.preloader.Preloader;
import com.sun.applet2.preloader.event.DownloadErrorEvent;
import com.sun.applet2.preloader.event.DownloadEvent;
import com.sun.deploy.Environment;
import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.cache.Cache;
import com.sun.deploy.config.Config;
import com.sun.deploy.config.JREInfo;
import com.sun.deploy.model.DownloadDelegate;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.perf.DeployPerfUtil;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.exceptions.BadFieldException;
import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.exceptions.JNLParseException;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.javaws.exceptions.MultipleHostsException;
import com.sun.javaws.exceptions.NativeLibViolationException;
import com.sun.javaws.jnl.AppletDesc;
import com.sun.javaws.jnl.ApplicationDesc;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.InstallerDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.jnl.JavaFXAppDesc;
import com.sun.javaws.jnl.LDUpdater;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.jnl.ResourceVisitor;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.progress.PreloaderDelegate;
import com.sun.javaws.security.JNLPSignedResourcesHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LaunchDownload
{
  private final LaunchDesc ld;
  public static final String APPCONTEXT_THREADPOOL_KEY = "deploy-launchdownloadthreadpoolinappcontext";

  public LaunchDownload(LaunchDesc paramLaunchDesc)
  {
    this.ld = paramLaunchDesc;
  }

  public static LaunchDesc updateLaunchDescInCache(LaunchDesc paramLaunchDesc)
  {
    return updateLaunchDescInCache(paramLaunchDesc, null, null);
  }

  public static LaunchDesc updateLaunchDescInCache(LaunchDesc paramLaunchDesc, URL paramURL1, URL paramURL2)
  {
    if (!Cache.isCacheEnabled())
      return paramLaunchDesc;
    int i = paramLaunchDesc.getLocation() == null ? 1 : 0;
    URL localURL = i != 0 ? paramLaunchDesc.getCanonicalHome() : paramLaunchDesc.getLocation();
    try
    {
      File localFile = ResourceProvider.get().getCachedJNLPFile(localURL, null);
      if (localFile == null)
      {
        Cache.createOrUpdateCacheEntry(localURL, paramLaunchDesc.getBytes());
        return paramLaunchDesc;
      }
      Trace.println("Loaded descriptor from cache at: " + localURL, TraceLevel.BASIC);
      LaunchDesc localLaunchDesc = LaunchDescFactory.buildDescriptor(localFile, paramURL1, paramURL2, null);
      if (paramLaunchDesc.hasIdenticalContent(localLaunchDesc))
        return localLaunchDesc;
      Cache.createOrUpdateCacheEntry(localURL, paramLaunchDesc.getBytes());
      return paramLaunchDesc;
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
    }
    catch (BadFieldException localBadFieldException)
    {
      Trace.ignoredException(localBadFieldException);
    }
    catch (MissingFieldException localMissingFieldException)
    {
      Trace.ignoredException(localMissingFieldException);
    }
    catch (JNLParseException localJNLParseException)
    {
      Trace.ignoredException(localJNLParseException);
    }
    return paramLaunchDesc;
  }

  static LaunchDesc getUpdatedLaunchDesc(URL paramURL1, URL paramURL2, boolean paramBoolean)
    throws JNLPException, IOException
  {
    ResourceProvider localResourceProvider = ResourceProvider.get();
    if (paramURL1 == null)
      return null;
    Resource localResource1 = localResourceProvider.getCachedResource(paramURL1, null);
    boolean bool;
    if (localResource1 != null)
      try
      {
        if (paramBoolean)
          bool = localResourceProvider.checkUpdateAvailable(paramURL1, localResource1, 1, null);
        else
          bool = localResourceProvider.isUpdateAvailable(paramURL1, null);
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
        bool = false;
      }
    else
      bool = true;
    if (!bool)
    {
      Trace.println("Update JNLP: no update for: " + paramURL1, TraceLevel.BASIC);
      return null;
    }
    Trace.println("Update JNLP: " + paramURL1 + ", thisCodebase: " + paramURL2, TraceLevel.BASIC);
    File localFile = null;
    int i = ResourceProvider.get().incrementInternalUse();
    try
    {
      Resource localResource2 = localResourceProvider.downloadUpdate(paramURL1, null);
      localFile = localResource2.getDataFile();
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Trace.ignoredException(localFileNotFoundException);
    }
    finally
    {
      ResourceProvider.get().decrementInternalUse(i);
    }
    if (localFile != null)
    {
      LaunchDesc localLaunchDesc = null;
      try
      {
        localLaunchDesc = LaunchDescFactory.buildDescriptor(localFile, paramURL2, paramURL1, paramURL1);
        return localLaunchDesc;
      }
      catch (LaunchDescException localLaunchDescException)
      {
        localLaunchDesc = LaunchDescFactory.buildDescriptor(localFile);
        if (localLaunchDesc == null)
          throw localLaunchDescException;
        return localLaunchDesc;
      }
    }
    return LaunchDescFactory.buildDescriptor(paramURL1, paramURL1);
  }

  public static boolean isJnlpCached(LaunchDesc paramLaunchDesc)
  {
    try
    {
      return ResourceProvider.get().isCached(paramLaunchDesc.getCanonicalHome(), null);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    return false;
  }

  public boolean isInCache()
  {
    return isInCache(false);
  }

  public boolean isInCache(boolean paramBoolean)
  {
    ResourcesDesc localResourcesDesc = this.ld.getResources();
    if (localResourcesDesc == null)
      return true;
    try
    {
      if ((this.ld.getLocation() != null) && (!ResourceProvider.get().isCached(this.ld.getLocation(), null)))
        return false;
      if (!paramBoolean)
      {
        bool = getCachedExtensions();
        if (!bool)
          return false;
      }
      boolean bool = Environment.isImportMode();
      JARDesc[] arrayOfJARDesc = localResourcesDesc.getEagerOrAllJarDescs(bool);
      for (int i = 0; i < arrayOfJARDesc.length; i++)
      {
        JARDesc localJARDesc = arrayOfJARDesc[i];
        Resource localResource = ResourceProvider.get().getCachedResource(localJARDesc.getLocation(), localJARDesc.getVersion());
        if (localResource == null)
          return false;
        if (checkJarFileCorrupted(localResource))
          return false;
      }
    }
    catch (JNLPException localJNLPException)
    {
      Trace.ignoredException(localJNLPException);
      return false;
    }
    catch (IOException localIOException)
    {
      Trace.ignoredException(localIOException);
      return false;
    }
    return true;
  }

  private static boolean isUpdateAvailable(LaunchDesc paramLaunchDesc)
    throws JNLPException
  {
    try
    {
      return new LDUpdater(paramLaunchDesc).isUpdateAvailable();
    }
    catch (Exception localException)
    {
      if ((localException instanceof JNLPException))
        throw ((JNLPException)localException);
      if ((localException.getCause() instanceof JNLPException))
        throw ((JNLPException)localException.getCause());
      throw new FailedDownloadingResourceException(paramLaunchDesc.getLocation(), null, localException);
    }
  }

  private boolean checkJarFileCorrupted(Resource paramResource)
  {
    if ((paramResource == null) || (paramResource.getDataFile() == null))
      return true;
    JarFile localJarFile = null;
    try
    {
      localJarFile = new JarFile(paramResource.getDataFile());
      boolean bool1 = false;
      return bool1;
    }
    catch (Exception localException1)
    {
      Trace.ignored(localException1);
      ResourceProvider.get().markRetired(paramResource, false);
      boolean bool2 = true;
      return bool2;
    }
    finally
    {
      if (localJarFile != null)
        try
        {
          localJarFile.close();
        }
        catch (Exception localException4)
        {
        }
    }
  }

  public void downloadExtensions(Preloader paramPreloader, int paramInt, ArrayList paramArrayList)
    throws IOException, JNLPException
  {
    downloadExtensionsHelper(paramPreloader, paramInt, false, paramArrayList);
  }

  public boolean getCachedExtensions()
    throws IOException, JNLPException
  {
    return downloadExtensionsHelper(null, 0, true, null);
  }

  private boolean downloadExtensionsHelper(Preloader paramPreloader, int paramInt, boolean paramBoolean, ArrayList paramArrayList)
    throws IOException, JNLPException
  {
    int i = ResourceProvider.get().incrementInternalUse();
    try
    {
      boolean bool = _downloadExtensionsHelper(paramPreloader, paramInt, paramBoolean, paramArrayList);
      return bool;
    }
    finally
    {
      ResourceProvider.get().decrementInternalUse(i);
    }
  }

  private boolean _downloadExtensionsHelper(Preloader paramPreloader, int paramInt, boolean paramBoolean, ArrayList paramArrayList)
    throws IOException, JNLPException
  {
    ResourcesDesc localResourcesDesc = this.ld.getResources();
    if (localResourcesDesc == null)
      return true;
    String str1 = JREInfo.getKnownPlatforms();
    ArrayList localArrayList = new ArrayList();
    localResourcesDesc.visit(new ResourceVisitor()
    {
      private final ArrayList val$list;

      public void visitExtensionDesc(ExtensionDesc paramAnonymousExtensionDesc)
      {
        this.val$list.add(paramAnonymousExtensionDesc);
      }
    });
    paramInt += localArrayList.size();
    for (int i = 0; i < localArrayList.size(); i++)
    {
      ExtensionDesc localExtensionDesc = (ExtensionDesc)localArrayList.get(i);
      String str2 = localExtensionDesc.getName();
      if (str2 == null)
      {
        str2 = localExtensionDesc.getLocation().toString();
        int j = str2.lastIndexOf('/');
        if (j > 0)
          str2 = str2.substring(j + 1, str2.length());
      }
      paramInt--;
      if (paramPreloader != null)
        paramPreloader.handleEvent(new DownloadEvent(0, localExtensionDesc.getLocation(), localExtensionDesc.getVersion(), str2, paramInt, localArrayList.size(), localArrayList.size()));
      Resource localResource = ResourceProvider.get().getJreResource(localExtensionDesc.getLocation(), localExtensionDesc.getVersion(), !paramBoolean, false, JREInfo.getKnownPlatforms());
      Object localObject1 = localResource != null ? localResource.getDataFile() : null;
      Trace.println("Downloaded extension: " + localExtensionDesc.getLocation() + "\n\tcodebase: " + localExtensionDesc.getCodebase() + "\n\tld parentCodebase: " + this.ld.getCodebase() + "\n\tfile: " + localObject1, TraceLevel.NETWORK);
      if (localObject1 == null)
        return false;
      LaunchDesc localLaunchDesc = LaunchDescFactory.buildDescriptor(localObject1, localExtensionDesc.getCodebase(), localExtensionDesc.getLocation(), localExtensionDesc.getLocation());
      int k = 0;
      Object localObject2;
      if (localLaunchDesc.getLaunchType() == 3)
      {
        k = 1;
      }
      else if (localLaunchDesc.getLaunchType() == 4)
      {
        localObject2 = Cache.getLocalApplicationProperties(localExtensionDesc.getLocation(), localExtensionDesc.getVersion(), false);
        k = !((LocalApplicationProperties)localObject2).isExtensionInstalled() ? 1 : 0;
        if ((paramArrayList != null) && ((isUpdateAvailable(localLaunchDesc)) || (k != 0)))
          paramArrayList.add(localObject1);
        if ((paramBoolean) && (k != 0))
          return false;
      }
      else
      {
        throw new MissingFieldException(localLaunchDesc.getSource(), "<component-desc>|<installer-desc>");
      }
      if (k != 0)
      {
        localExtensionDesc.setExtensionDesc(localLaunchDesc);
        localObject2 = new LaunchDownload(localLaunchDesc);
        boolean bool = ((LaunchDownload)localObject2).downloadExtensionsHelper(paramPreloader, paramInt, paramBoolean, paramArrayList);
        if (!bool)
          return false;
      }
    }
    return true;
  }

  public void downloadJRE(Preloader paramPreloader, ArrayList paramArrayList)
    throws JNLPException, IOException
  {
    JREDesc localJREDesc = this.ld.getResources().getSelectedJRE();
    String str1 = localJREDesc.getVersion();
    URL localURL = localJREDesc.getHref();
    boolean bool = localURL == null;
    if (localURL == null)
    {
      str2 = Config.getStringProperty("deployment.javaws.installURL");
      if (str2 != null)
        try
        {
          localURL = new URL(str2);
        }
        catch (MalformedURLException localMalformedURLException)
        {
        }
    }
    paramPreloader.handleEvent(new DownloadEvent(0, localURL, str1, str1, 0L, 1000L, 0));
    String str2 = JREInfo.getKnownPlatforms();
    Resource localResource = ResourceProvider.get().getJreResource(localURL, str1, true, bool, str2);
    File localFile = localResource != null ? localResource.getDataFile() : null;
    if ((localFile == null) || (!localFile.isFile()))
      return;
    LaunchDesc localLaunchDesc = LaunchDescFactory.buildDescriptor(localFile, null, null, null);
    if (localLaunchDesc.getLaunchType() != 4)
      throw new MissingFieldException(localLaunchDesc.getSource(), "<installer-desc>");
    if (paramArrayList != null)
      paramArrayList.add(localFile);
    localJREDesc.setExtensionDesc(localLaunchDesc);
    LaunchDownload localLaunchDownload = new LaunchDownload(localLaunchDesc);
    localLaunchDownload.downloadExtensionsHelper(paramPreloader, 0, false, paramArrayList);
  }

  public static void downloadResource(LaunchDesc paramLaunchDesc, URL paramURL, String paramString, Preloader paramPreloader, boolean paramBoolean)
    throws IOException, JNLPException
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return;
    int i = localResourcesDesc.getConcurrentDownloads();
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getResource(paramURL, paramString);
    downloadJarFiles(arrayOfJARDesc, paramPreloader, paramBoolean, i);
  }

  public static void downloadParts(LaunchDesc paramLaunchDesc, String[] paramArrayOfString, Preloader paramPreloader, boolean paramBoolean)
    throws IOException, JNLPException
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return;
    int i = localResourcesDesc.getConcurrentDownloads();
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getPartJars(paramArrayOfString);
    downloadJarFiles(arrayOfJARDesc, paramPreloader, paramBoolean, i);
  }

  public static void downloadExtensionPart(LaunchDesc paramLaunchDesc, URL paramURL, String paramString, String[] paramArrayOfString, Preloader paramPreloader, boolean paramBoolean)
    throws IOException, JNLPException
  {
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return;
    int i = localResourcesDesc.getConcurrentDownloads();
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getExtensionPart(paramURL, paramString, paramArrayOfString);
    downloadJarFiles(arrayOfJARDesc, paramPreloader, paramBoolean, i);
  }

  public void downloadEagerorAll(boolean paramBoolean1, Preloader paramPreloader, boolean paramBoolean2)
    throws IOException, JNLPException
  {
    ResourcesDesc localResourcesDesc = this.ld.getResources();
    if (localResourcesDesc == null)
      return;
    Object localObject1 = localResourcesDesc.getEagerOrAllJarDescs(paramBoolean1);
    int j;
    if (!paramBoolean1)
    {
      JARDesc[] arrayOfJARDesc1 = localResourcesDesc.getEagerOrAllJarDescs(true);
      if (arrayOfJARDesc1.length != localObject1.length)
      {
        localObject2 = new HashSet(Arrays.asList((Object[])localObject1));
        j = 0;
        for (int k = 0; k < arrayOfJARDesc1.length; k++)
        {
          URL localURL = arrayOfJARDesc1[k].getLocation();
          String str = arrayOfJARDesc1[k].getVersion();
          if ((!((HashSet)localObject2).contains(arrayOfJARDesc1[k])) && (ResourceProvider.get().isCached(localURL, str)))
            j++;
          else
            arrayOfJARDesc1[k] = null;
        }
        if (j > 0)
        {
          JARDesc[] arrayOfJARDesc2 = new JARDesc[localObject1.length + j];
          System.arraycopy(localObject1, 0, arrayOfJARDesc2, 0, localObject1.length);
          int m = localObject1.length;
          for (int n = 0; n < arrayOfJARDesc1.length; n++)
            if (arrayOfJARDesc1[n] != null)
              arrayOfJARDesc2[(m++)] = arrayOfJARDesc1[n];
          localObject1 = arrayOfJARDesc2;
        }
      }
    }
    int i = this.ld.getResources().getConcurrentDownloads();
    Trace.println("LaunchDownload: concurrent downloads from LD: " + i, TraceLevel.NETWORK);
    downloadJarFiles((JARDesc[])localObject1, paramPreloader, paramBoolean2, i);
    Object localObject2 = this.ld.getInformation().getIconLocation(48, 0);
    if (localObject2 != null)
    {
      j = ResourceProvider.get().incrementInternalUse();
      try
      {
        ResourceProvider.get().getResource(((IconDesc)localObject2).getLocation(), ((IconDesc)localObject2).getVersion(), true, 1, null);
        Trace.println("Downloaded " + ((IconDesc)localObject2).getLocation(), TraceLevel.NETWORK);
      }
      catch (Exception localException)
      {
        Trace.ignored(localException);
      }
      finally
      {
        ResourceProvider.get().decrementInternalUse(j);
      }
    }
  }

  public static void reverse(JARDesc[] paramArrayOfJARDesc)
  {
    int i = 0;
    for (int j = paramArrayOfJARDesc.length - 1; i < j; j--)
    {
      JARDesc localJARDesc = paramArrayOfJARDesc[i];
      paramArrayOfJARDesc[i] = paramArrayOfJARDesc[j];
      paramArrayOfJARDesc[j] = localJARDesc;
      i++;
    }
  }

  public static int getDownloadType(JARDesc paramJARDesc)
  {
    int i = 256;
    if (paramJARDesc.isNativeLib())
      i |= 16;
    if (paramJARDesc.isPack200Enabled())
      i |= 4096;
    if (paramJARDesc.isVersionEnabled())
      i |= 65536;
    return i;
  }

  public void prepareCustomProgress(PreloaderDelegate paramPreloaderDelegate, JNLPSignedResourcesHelper paramJNLPSignedResourcesHelper, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean)
  {
    prepareCustomProgress(paramPreloaderDelegate, paramJNLPSignedResourcesHelper, paramRunnable1, paramRunnable2, paramBoolean, true);
  }

  void prepareCustomProgress(PreloaderDelegate paramPreloaderDelegate, JNLPSignedResourcesHelper paramJNLPSignedResourcesHelper, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean1, boolean paramBoolean2)
  {
    DeployPerfUtil.put("begining of prepareCustomProgress()");
    paramPreloaderDelegate.setPreloaderClass(this.ld.getProgressClassName());
    paramPreloaderDelegate.markLoadingStarted();
    Runnable local2 = new Runnable()
    {
      private final boolean val$doUpdate;
      private final PreloaderDelegate val$delegate;
      private final Runnable val$okAction;
      private final JNLPSignedResourcesHelper val$signingHelper;
      private final Runnable val$failAction;

      public void run()
      {
        try
        {
          if (this.val$doUpdate)
            LaunchDownload.this.downloadProgressJars(this.val$delegate);
          if (this.val$okAction != null)
            this.val$okAction.run();
          this.val$delegate.markLoaded(null);
          this.val$signingHelper.warmup();
        }
        catch (Exception localException1)
        {
          Exception localException2;
          if ((localException1 instanceof RuntimeException))
            localException2 = (localException1.getCause() instanceof Exception) ? (Exception)localException1.getCause() : localException1;
          Trace.println("Error preparing preloader : " + localException2, TraceLevel.PRELOADER);
          Trace.ignored(localException2);
          this.val$delegate.markLoaded(localException2);
          if (this.val$failAction != null)
            this.val$failAction.run();
        }
      }
    };
    if (paramBoolean2)
    {
      Thread localThread = new Thread(local2, "Loading Custom Progress");
      localThread.setDaemon(true);
      localThread.start();
    }
    else
    {
      local2.run();
    }
  }

  void downloadProgressJars(PreloaderDelegate paramPreloaderDelegate)
    throws IOException, JNLPException
  {
    ExecutorService localExecutorService = null;
    List localList = null;
    ResourcesDesc localResourcesDesc = this.ld.getResources();
    if (localResourcesDesc == null)
      return;
    localExecutorService = getThreadPool(2);
    if (localExecutorService == null)
      return;
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getEagerOrAllJarDescs(false);
    ArrayList localArrayList = new ArrayList(2);
    for (int i = 0; i < arrayOfJARDesc.length; i++)
    {
      JARDesc localJARDesc = arrayOfJARDesc[i];
      if (localJARDesc.isProgressJar())
      {
        DownloadTask localDownloadTask = new DownloadTask(localJARDesc.getLocation(), null, localJARDesc.getVersion(), null, true, getDownloadType(localJARDesc), null, null, null);
        if (!localArrayList.contains(localDownloadTask))
          localArrayList.add(localDownloadTask);
      }
    }
    if (localArrayList.size() > 0)
    {
      try
      {
        localList = localExecutorService.invokeAll(localArrayList);
      }
      catch (InterruptedException localInterruptedException)
      {
        Trace.ignored(localInterruptedException);
        localExecutorService.shutdownNow();
      }
      localExecutorService.shutdown();
      validateResults(localList, localArrayList, null);
    }
  }

  private static void downloadJarFiles(JARDesc[] paramArrayOfJARDesc, Preloader paramPreloader, boolean paramBoolean, int paramInt)
    throws JNLPException, IOException
  {
    if (paramArrayOfJARDesc == null)
      return;
    DeployPerfUtil.put("LaunchDownload.downloadJarFiles - begin");
    if (Globals.isReverseMode())
      reverse(paramArrayOfJARDesc);
    long l = 0L;
    DownloadCallbackHelper localDownloadCallbackHelper = DownloadCallbackHelper.get(paramPreloader);
    int i = 0;
    int j = 1;
    for (int k = 0; k < paramArrayOfJARDesc.length; k++)
    {
      m = paramArrayOfJARDesc[k].getSize();
      if (!paramArrayOfJARDesc[k].isProgressJar())
        if (m > 0)
        {
          i++;
          l += m;
        }
        else
        {
          j = 0;
        }
    }
    k = 0;
    for (int m = 0; m < paramArrayOfJARDesc.length; m++)
    {
      int n = paramArrayOfJARDesc[m].getSize();
      if (!paramArrayOfJARDesc[m].isProgressJar())
      {
        if (n <= 0)
          localDownloadCallbackHelper.register(paramArrayOfJARDesc[m].getLocation().toString(), paramArrayOfJARDesc[m].getVersion(), 0, 1.0D);
        else if (n > 0)
          localDownloadCallbackHelper.register(paramArrayOfJARDesc[m].getLocation().toString(), paramArrayOfJARDesc[m].getVersion(), n, 0.5D + n * i / l);
        k++;
      }
    }
    if (j == 0)
      l = -1L;
    Trace.println("Total size to download: " + l, TraceLevel.NETWORK);
    if (l == 0L)
      return;
    localDownloadCallbackHelper.setTotalSize(l);
    localDownloadCallbackHelper.setNumOfJars(paramArrayOfJARDesc.length);
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    ExecutorService localExecutorService = getThreadPool(paramInt);
    if (localExecutorService != null)
    {
      ToolkitStore.get().getAppContext().put("deploy-launchdownloadthreadpoolinappcontext", localExecutorService);
      localDownloadCallbackHelper.setNumOfJars(k);
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfJARDesc.length);
    for (int i1 = 0; i1 < paramArrayOfJARDesc.length; i1++)
    {
      JARDesc localJARDesc = paramArrayOfJARDesc[i1];
      int i2 = ResourceProvider.get().incrementInternalUse();
      try
      {
        int i3 = getDownloadType(localJARDesc);
        Object localObject1;
        if (localExecutorService == null)
        {
          localObject1 = ResourceProvider.get().getResource(localJARDesc.getLocation(), localJARDesc.getVersion(), true, i3, localDownloadCallbackHelper);
          arrayOfInt[0] += 1;
          localDownloadCallbackHelper.setJarsDone(arrayOfInt[0]);
          if ((Cache.isCacheEnabled()) && (localObject1 == null) && (!Environment.isImportMode()))
            throw new FailedDownloadingResourceException(null, localJARDesc.getLocation(), localJARDesc.getVersion(), null);
        }
        else if (!localJARDesc.isProgressJar())
        {
          localObject1 = new DownloadTask(localJARDesc.getLocation(), null, localJARDesc.getVersion(), localDownloadCallbackHelper, true, i3, paramPreloader, arrayOfInt, localDownloadCallbackHelper);
          if (!localArrayList.contains(localObject1))
            localArrayList.add(localObject1);
        }
      }
      catch (JNLPException localJNLPException)
      {
        if (paramPreloader != null)
          paramPreloader.handleEvent(new DownloadErrorEvent(localJARDesc.getLocation(), localJARDesc.getVersion()));
        throw localJNLPException;
      }
      finally
      {
        ResourceProvider.get().decrementInternalUse(i2);
      }
    }
    List localList = null;
    try
    {
      if (localExecutorService != null)
        localList = localExecutorService.invokeAll(localArrayList);
    }
    catch (Exception localException)
    {
      Trace.ignored(localException);
    }
    if (localExecutorService != null)
    {
      ToolkitStore.get().getAppContext().remove("deploy-launchdownloadthreadpoolinappcontext");
      localExecutorService.shutdown();
      validateResults(localList, localArrayList, paramPreloader);
    }
    DeployPerfUtil.put("LaunchDownload.downloadJarFiles - end");
  }

  private static void validateResults(List paramList, ArrayList paramArrayList, Preloader paramPreloader)
    throws IOException, JNLPException
  {
    if (paramList != null)
    {
      int i = 0;
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Future localFuture = (Future)localIterator.next();
        URL localURL = ((DownloadTask)paramArrayList.get(i)).getURL();
        String str = ((DownloadTask)paramArrayList.get(i)).getVersion();
        i++;
        try
        {
          localFuture.get();
        }
        catch (ExecutionException localExecutionException)
        {
          Throwable localThrowable = localExecutionException.getCause();
          if (null != localThrowable)
          {
            if ((localThrowable instanceof IOException))
            {
              if (paramPreloader != null)
                paramPreloader.handleEvent(new DownloadErrorEvent(localURL, str, localThrowable));
              throw ((IOException)localThrowable);
            }
            if ((localThrowable instanceof JNLPException))
            {
              if (paramPreloader != null)
                paramPreloader.handleEvent(new DownloadErrorEvent(localURL, str));
              throw ((JNLPException)localThrowable);
            }
            throw new IOException("JNLP Jar download failure.");
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          Trace.ignored(localInterruptedException);
        }
      }
    }
  }

  private static synchronized void notifyProgress(DownloadCallbackHelper paramDownloadCallbackHelper, int[] paramArrayOfInt, URL paramURL)
  {
    if ((paramArrayOfInt != null) && (paramDownloadCallbackHelper != null))
    {
      paramArrayOfInt[0] += 1;
      Trace.println("Download Progress: jarsDone: " + paramArrayOfInt[0], TraceLevel.NETWORK);
      paramDownloadCallbackHelper.jarDone(paramURL);
      paramDownloadCallbackHelper.setJarsDone(paramArrayOfInt[0]);
    }
  }

  private static ExecutorService getThreadPool(int paramInt)
  {
    if (Config.isJavaVersionAtLeast15())
    {
      ExecutorService localExecutorService = Executors.newFixedThreadPool(paramInt, new ThreadFactory()
      {
        public Thread newThread(Runnable paramAnonymousRunnable)
        {
          Thread localThread = new Thread(paramAnonymousRunnable);
          localThread.setDaemon(true);
          return localThread;
        }
      });
      return localExecutorService;
    }
    return null;
  }

  public static void checkJNLPSecurity(LaunchDesc paramLaunchDesc)
    throws MultipleHostsException, NativeLibViolationException
  {
    boolean[] arrayOfBoolean1 = new boolean[1];
    boolean[] arrayOfBoolean2 = new boolean[1];
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return;
    JARDesc localJARDesc = paramLaunchDesc.getResources().getMainJar(true);
    if (localJARDesc == null)
      return;
    checkJNLPSecurityHelper(paramLaunchDesc, localJARDesc.getLocation().getHost(), arrayOfBoolean2, arrayOfBoolean1);
    if (arrayOfBoolean2[0] != 0)
      throw new MultipleHostsException();
    if (arrayOfBoolean1[0] != 0)
      throw new NativeLibViolationException();
  }

  private static void checkJNLPSecurityHelper(LaunchDesc paramLaunchDesc, String paramString, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    if (paramLaunchDesc.getSecurityModel() != 0)
      return;
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return;
    localResourcesDesc.visit(new ResourceVisitor()
    {
      private final boolean[] val$hostViolation;
      private final String val$host;
      private final boolean[] val$nativeLibViolation;

      public void visitJARDesc(JARDesc paramAnonymousJARDesc)
      {
        String str = paramAnonymousJARDesc.getLocation().getHost();
        this.val$hostViolation[0] = ((this.val$hostViolation[0] != 0) || (!this.val$host.equals(str)) ? 1 : false);
        this.val$nativeLibViolation[0] = ((this.val$nativeLibViolation[0] != 0) || (paramAnonymousJARDesc.isNativeLib()) ? 1 : false);
      }

      public void visitExtensionDesc(ExtensionDesc paramAnonymousExtensionDesc)
      {
        if ((this.val$hostViolation[0] == 0) && (this.val$nativeLibViolation[0] == 0))
        {
          LaunchDesc localLaunchDesc = paramAnonymousExtensionDesc.getExtensionDesc();
          String str = paramAnonymousExtensionDesc.getLocation().getHost();
          if ((localLaunchDesc != null) && (localLaunchDesc.getSecurityModel() == 0) && (this.val$hostViolation[0] == 0))
            LaunchDownload.checkJNLPSecurityHelper(localLaunchDesc, str, this.val$hostViolation, this.val$nativeLibViolation);
        }
      }
    });
  }

  public static long getCachedSize(LaunchDesc paramLaunchDesc)
  {
    long l = 0L;
    ResourcesDesc localResourcesDesc = paramLaunchDesc.getResources();
    if (localResourcesDesc == null)
      return l;
    JARDesc[] arrayOfJARDesc = localResourcesDesc.getEagerOrAllJarDescs(true);
    for (int i = 0; i < arrayOfJARDesc.length; i++)
    {
      Resource localResource1 = ResourceProvider.get().getCachedResource(arrayOfJARDesc[i].getLocation(), arrayOfJARDesc[i].getVersion());
      if (localResource1 != null)
        l += localResource1.getSize();
    }
    IconDesc[] arrayOfIconDesc = paramLaunchDesc.getInformation().getIcons();
    if (arrayOfIconDesc != null)
      for (int j = 0; j < arrayOfIconDesc.length; j++)
      {
        Resource localResource2 = ResourceProvider.get().getCachedResource(arrayOfIconDesc[j].getLocation(), arrayOfIconDesc[j].getVersion());
        if (localResource2 != null)
          l += localResource2.getSize();
      }
    return l;
  }

  static String getMainClassName(LaunchDesc paramLaunchDesc, boolean paramBoolean)
    throws IOException, JNLPException, LaunchDescException
  {
    String str1 = null;
    ApplicationDesc localApplicationDesc = paramLaunchDesc.getApplicationDescriptor();
    if (localApplicationDesc != null)
      str1 = localApplicationDesc.getMainClass();
    InstallerDesc localInstallerDesc = paramLaunchDesc.getInstallerDescriptor();
    if (localInstallerDesc != null)
      str1 = localInstallerDesc.getMainClass();
    AppletDesc localAppletDesc = paramLaunchDesc.getAppletDescriptor();
    if (localAppletDesc != null)
      str1 = localAppletDesc.getAppletClass();
    JavaFXAppDesc localJavaFXAppDesc = paramLaunchDesc.getJavaFXAppDescriptor();
    if (localJavaFXAppDesc != null)
      str1 = localJavaFXAppDesc.getMainClass();
    if ((str1 != null) && (str1.length() == 0))
      str1 = null;
    if (str1 != null)
      return str1;
    if (paramLaunchDesc.getResources() == null)
      return null;
    JARDesc localJARDesc = paramLaunchDesc.getResources().getMainJar(paramBoolean);
    if (localJARDesc == null)
      return null;
    JarFile localJarFile = null;
    try
    {
      localJarFile = new JarFile(ResourceProvider.get().getCachedJNLPFile(localJARDesc.getLocation(), localJARDesc.getVersion()), false);
      if ((localJarFile != null) && (str1 == null) && (paramLaunchDesc.getLaunchType() != 2))
      {
        localObject1 = localJarFile.getManifest();
        str1 = localObject1 != null ? ((Manifest)localObject1).getMainAttributes().getValue("Main-Class") : null;
      }
      if (str1 == null)
        throw new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.nomainclassspec"), null);
      Object localObject1 = str1.replace('.', '/') + ".class";
      if (localJarFile.getEntry((String)localObject1) == null)
        throw new LaunchDescException(paramLaunchDesc, ResourceManager.getString("launch.error.nomainclass", str1, localJARDesc.getLocation().toString()), null);
      String str2 = str1;
      return str2;
    }
    finally
    {
      if (localJarFile != null)
        localJarFile.close();
    }
  }

  public static boolean inCache(JARDesc paramJARDesc)
  {
    return ResourceProvider.get().isCached(paramJARDesc.getLocation(), paramJARDesc.getVersion());
  }

  private static class DownloadCallbackHelper
    implements DownloadDelegate
  {
    Preloader _preloader;
    long _totalSize = -1L;
    final ArrayList _records;
    int _numOfJars = 1;
    int _jarsDone = 0;
    private static WeakHashMap helpers = new WeakHashMap();

    private DownloadCallbackHelper(Preloader paramPreloader)
    {
      this._preloader = paramPreloader;
      this._records = new ArrayList();
    }

    static DownloadCallbackHelper get(Preloader paramPreloader)
    {
      DownloadCallbackHelper localDownloadCallbackHelper = (DownloadCallbackHelper)helpers.get(paramPreloader);
      if (localDownloadCallbackHelper == null)
      {
        localDownloadCallbackHelper = new DownloadCallbackHelper(paramPreloader);
        helpers.put(paramPreloader, localDownloadCallbackHelper);
      }
      return localDownloadCallbackHelper;
    }

    public void register(String paramString1, String paramString2, int paramInt, double paramDouble)
    {
      LaunchDownload.ProgressRecord localProgressRecord = getProgressRecord(paramString1);
      if (localProgressRecord == null)
      {
        localProgressRecord = new LaunchDownload.ProgressRecord(paramString1, paramString2, paramInt);
        localProgressRecord.setWeight(paramDouble);
        synchronized (this._records)
        {
          this._records.add(localProgressRecord);
        }
      }
      else
      {
        localProgressRecord.setWeight(paramDouble);
        localProgressRecord.setSize(paramInt);
      }
    }

    public void setTotalSize(long paramLong)
    {
      this._totalSize = paramLong;
    }

    public void setNumOfJars(int paramInt)
    {
      this._numOfJars = paramInt;
    }

    public void setJarsDone(int paramInt)
    {
      this._jarsDone = paramInt;
    }

    public void downloading(URL paramURL, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
      throws CancelException
    {
      if (this._preloader != null)
      {
        String str = paramURL.toString();
        LaunchDownload.ProgressRecord localProgressRecord = getProgressRecord(str);
        if (localProgressRecord == null)
        {
          localProgressRecord = new LaunchDownload.ProgressRecord(str, paramString, paramInt2);
          synchronized (this._records)
          {
            this._records.add(localProgressRecord);
          }
        }
        else
        {
          localProgressRecord.setSize(paramInt2);
        }
        localProgressRecord.downloadProgress(paramInt1);
        int i = getOverallPercent();
        this._preloader.handleEvent(new DownloadEvent(0, paramURL, paramString, null, paramInt1, paramInt2, i));
      }
    }

    public void patching(URL paramURL, String paramString, int paramInt)
      throws CancelException
    {
      if (this._preloader != null)
      {
        String str = paramURL.toString();
        LaunchDownload.ProgressRecord localProgressRecord = getProgressRecord(str);
        if (localProgressRecord != null)
        {
          localProgressRecord.patchProgress(paramInt);
          int i = getOverallPercent();
          this._preloader.handleEvent(new DownloadEvent(2, paramURL, paramString, null, paramInt, 100L, i));
        }
      }
    }

    public void validating(URL paramURL, int paramInt1, int paramInt2)
      throws CancelException
    {
      if (this._preloader != null)
      {
        String str = paramURL.toString();
        LaunchDownload.ProgressRecord localProgressRecord = getProgressRecord(str);
        if (localProgressRecord != null)
        {
          localProgressRecord.cacheTotalElements(paramInt2);
          localProgressRecord.validateProgress(paramInt1, paramInt2);
          int i = getOverallPercent();
          this._preloader.handleEvent(new DownloadEvent(1, paramURL, null, null, paramInt1, paramInt2, i));
        }
      }
    }

    public LaunchDownload.ProgressRecord getProgressRecord(String paramString)
    {
      synchronized (this._records)
      {
        Iterator localIterator = this._records.iterator();
        while (localIterator.hasNext())
        {
          LaunchDownload.ProgressRecord localProgressRecord = (LaunchDownload.ProgressRecord)localIterator.next();
          if ((paramString != null) && (paramString.equals(localProgressRecord.getUrl())))
            return localProgressRecord;
        }
      }
      return null;
    }

    public int getOverallPercent()
    {
      double d1 = 0.0D;
      double d2 = 0.0D;
      synchronized (this._records)
      {
        Iterator localIterator = this._records.iterator();
        while (localIterator.hasNext())
        {
          LaunchDownload.ProgressRecord localProgressRecord = (LaunchDownload.ProgressRecord)localIterator.next();
          d1 += localProgressRecord.getPercent() * localProgressRecord.getWeight();
          d2 += localProgressRecord.getWeight();
        }
      }
      int i = (int)(d1 * 100.0D / d2);
      if (i > 100)
        i = 100;
      return i;
    }

    public void downloadFailed(URL paramURL, String paramString)
    {
      if (this._preloader != null)
        try
        {
          this._preloader.handleEvent(new DownloadErrorEvent(paramURL, paramString));
        }
        catch (CancelException localCancelException)
        {
        }
    }

    void jarDone(URL paramURL)
    {
      if (this._preloader != null)
      {
        String str = paramURL.toString();
        LaunchDownload.ProgressRecord localProgressRecord = getProgressRecord(str);
        if ((localProgressRecord != null) && (localProgressRecord.getPercent() < 1.0D))
        {
          LaunchDownload.ProgressRecord.access$000(localProgressRecord);
          int i = getOverallPercent();
          try
          {
            this._preloader.handleEvent(new DownloadEvent(1, paramURL, null, null, localProgressRecord.getCachedTotalElements(), localProgressRecord.getCachedTotalElements(), i));
          }
          catch (CancelException localCancelException)
          {
          }
        }
      }
    }
  }

  private static class DownloadTask
    implements Callable
  {
    private URL url;
    private int downloadType;
    private String resourceID;
    private String versionString;
    private DownloadDelegate dd;
    private final boolean doDownload;
    private Preloader dp;
    private int[] counterBox;
    private LaunchDownload.DownloadCallbackHelper dch;

    public DownloadTask(URL paramURL, String paramString1, String paramString2, DownloadDelegate paramDownloadDelegate, boolean paramBoolean, int paramInt, Preloader paramPreloader, int[] paramArrayOfInt, LaunchDownload.DownloadCallbackHelper paramDownloadCallbackHelper)
    {
      this.url = paramURL;
      this.downloadType = paramInt;
      this.resourceID = paramString1;
      this.versionString = paramString2;
      this.dd = paramDownloadDelegate;
      this.doDownload = paramBoolean;
      this.dp = paramPreloader;
      this.counterBox = paramArrayOfInt;
      this.dch = paramDownloadCallbackHelper;
    }

    public URL getURL()
    {
      return this.url;
    }

    public String getVersion()
    {
      return this.versionString;
    }

    public int hashCode()
    {
      if (this.url == null)
        return 0;
      return this.url.hashCode();
    }

    public String toString()
    {
      return this.url.toString() + (this.versionString != null ? ":" + this.versionString : "");
    }

    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof DownloadTask))
      {
        DownloadTask localDownloadTask = (DownloadTask)paramObject;
        URL localURL = localDownloadTask.getURL();
        String str = localDownloadTask.getVersion();
        if (this.url.toString().equals(localURL.toString()))
        {
          if ((this.versionString == null) && (str == null))
            return true;
          if ((this.versionString != null) && (str != null) && (this.versionString.equals(str)))
            return true;
        }
      }
      return false;
    }

    public Object call()
      throws IOException, JNLPException
    {
      int i = ResourceProvider.get().incrementInternalUse();
      try
      {
        Resource localResource = ResourceProvider.get().getResource(this.url, this.versionString, this.doDownload, this.downloadType, this.dch);
        Object localObject1 = localResource != null ? URLUtil.fileToURL(localResource.getDataFile()) : null;
        if ((Cache.isCacheEnabled()) && (localObject1 == null) && (!Environment.isImportMode()))
          throw new FailedDownloadingResourceException(null, this.url, this.versionString, null);
        LaunchDownload.notifyProgress(this.dch, this.counterBox, this.url);
      }
      finally
      {
        ResourceProvider.get().decrementInternalUse(i);
      }
      return null;
    }
  }

  private static class ProgressRecord
  {
    private String _url;
    private String _ver;
    private int _size;
    private double _percent;
    private int _totalElements = 1;
    private double _weight;

    public ProgressRecord(String paramString1, String paramString2, int paramInt)
    {
      this._url = paramString1;
      this._ver = paramString2;
      this._size = paramInt;
      this._weight = 1.0D;
      this._percent = 0.0D;
    }

    void cacheTotalElements(int paramInt)
    {
      this._totalElements = paramInt;
    }

    int getCachedTotalElements()
    {
      return this._totalElements;
    }

    void setWeight(double paramDouble)
    {
      this._weight = paramDouble;
    }

    void setSize(int paramInt)
    {
      this._size = paramInt;
    }

    double getPercent()
    {
      return this._percent;
    }

    String getUrl()
    {
      return this._url;
    }

    public int hashCode()
    {
      int i = 7;
      i = 79 * i + (this._url != null ? this._url.hashCode() : 0);
      return i;
    }

    public boolean equals(Object paramObject)
    {
      return this._url.equals(((ProgressRecord)paramObject)._url);
    }

    double getWeight()
    {
      return this._weight;
    }

    void downloadProgress(int paramInt)
    {
      if (this._size != 0)
        this._percent = (paramInt / this._size * 0.8D);
      else
        this._percent = 0.8D;
    }

    void patchProgress(int paramInt)
    {
      this._percent = (paramInt / 100.0D * 0.1D + 0.8D);
    }

    void validateProgress(int paramInt1, int paramInt2)
    {
      if (paramInt2 != 0)
        this._percent = (paramInt1 / paramInt2 * 0.05D + 0.9D);
      else
        this._percent = 0.95D;
    }

    private void markComplete()
    {
      this._percent = 1.0D;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.LaunchDownload
 * JD-Core Version:    0.6.2
 */