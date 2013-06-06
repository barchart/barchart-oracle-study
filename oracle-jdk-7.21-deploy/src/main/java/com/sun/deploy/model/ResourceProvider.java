package com.sun.deploy.model;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.net.UpdateTracker;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public abstract class ResourceProvider
{
  public static final int DOWNLOAD_NORMAL = 1;
  public static final int DOWNLOAD_NATIVE = 16;
  public static final int DOWNLOAD_JAR = 256;
  public static final int DOWNLOAD_PACK200 = 4096;
  public static final int DOWNLOAD_VERSION = 65536;
  private static final String BACKGROUND_STRING = "background";
  private static final String APPCONTEXT_BG_KEY = "deploy-bg-";
  protected static ResourceProvider instance;

  public static ResourceProvider get()
  {
    if (instance == null)
    {
      Trace.println("ResourceProvider not initialized", TraceLevel.CACHE);
      if (Trace.isEnabled(TraceLevel.CACHE))
        Trace.ignored(new Throwable("ResourceProvider not initialized"));
    }
    return instance;
  }

  protected static synchronized void set(ResourceProvider paramResourceProvider)
  {
    if (instance == null)
    {
      Trace.println("Initialize resource manager: " + paramResourceProvider, TraceLevel.CACHE);
      instance = paramResourceProvider;
    }
    else
    {
      Trace.println("Attempt to reset resource manager", TraceLevel.CACHE);
      if (Trace.isEnabled(TraceLevel.CACHE))
        Trace.ignored(new Throwable("" + paramResourceProvider));
    }
  }

  public abstract Resource getResource(URL paramURL, String paramString, boolean paramBoolean, int paramInt, DownloadDelegate paramDownloadDelegate)
    throws IOException;

  public final Resource getResource(URL paramURL, String paramString)
    throws IOException
  {
    return getResource(paramURL, paramString, true, 1, null);
  }

  public final Resource getCachedResource(URL paramURL, String paramString)
  {
    try
    {
      return getResource(paramURL, paramString, false, 0, null);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    return null;
  }

  public abstract String getCachedResourceFilePath(URL paramURL, String paramString)
    throws IOException;

  public final File getCachedJNLPFile(URL paramURL, String paramString)
  {
    try
    {
      Resource localResource = getResource(paramURL, paramString, false, 1, null);
      if (localResource != null)
        return localResource.getDataFile();
    }
    catch (IOException localIOException)
    {
    }
    return null;
  }

  public final boolean isCached(URL paramURL, String paramString)
  {
    return getCachedResource(paramURL, paramString) != null;
  }

  public final JarFile getJarFile(URL paramURL, String paramString, int paramInt)
    throws IOException
  {
    Resource localResource = getResource(paramURL, paramString, true, paramInt, null);
    return localResource != null ? localResource.getJarFile() : null;
  }

  public final JarFile getCachedJarFile(URL paramURL, String paramString)
  {
    Resource localResource = getCachedResource(paramURL, paramString);
    return localResource != null ? localResource.getJarFile() : null;
  }

  public abstract Resource getJreResource(URL paramURL, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2)
    throws IOException;

  public abstract boolean isUpdateAvailable(URL paramURL, String paramString, int paramInt, Map paramMap)
    throws IOException;

  public abstract boolean checkUpdateAvailable(URL paramURL, Resource paramResource, int paramInt, Map paramMap)
    throws IOException;

  public boolean isUpdateAvailable(URL paramURL, String paramString)
    throws IOException
  {
    return isUpdateAvailable(paramURL, paramString, 1, null);
  }

  public abstract Resource downloadUpdate(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException;

  public final Resource downloadUpdate(URL paramURL, String paramString)
    throws IOException
  {
    return downloadUpdate(paramURL, paramString, 1, true);
  }

  public abstract void markReady(Resource[] paramArrayOfResource)
    throws IOException;

  public abstract void markRetired(Resource paramResource, boolean paramBoolean);

  public abstract File getShortcutImage(URL paramURL, String paramString, boolean paramBoolean)
    throws IOException;

  public abstract String getLibraryDirForJar(String paramString1, URL paramURL, String paramString2)
    throws IOException;

  public abstract void preverifyCachedJar(URL paramURL, String paramString, URLClassLoader paramURLClassLoader);

  public abstract Resource getSystemResource(URL paramURL, String paramString);

  public abstract int incrementInternalUse();

  public abstract void decrementInternalUse(int paramInt);

  public abstract void setBackgroundUpdateRequest(boolean paramBoolean);

  public abstract boolean isBackgroundUpdateRequest();

  public abstract void decrementsInternalUse(int paramInt);

  public abstract boolean isInternalUse();

  public abstract LocalApplicationProperties getLocalApplicationProperties(URL paramURL, String paramString, boolean paramBoolean);

  public abstract boolean canCache(URL paramURL);

  public abstract File getCacheDir();

  public abstract String getCurrentVersion(URL paramURL);

  public abstract ResourceObject getResourceObject(String paramString);

  public void storeAppContextBackgroundList(List paramList)
  {
    if (paramList == null)
      return;
    for (int i = 0; i < paramList.size(); i++)
    {
      String str = (String)paramList.get(i);
      if (str != null)
      {
        UpdateTracker.addPending(str);
        ToolkitStore.get().getAppContext().put("deploy-bg-" + str, "background");
      }
    }
  }

  public void clearAppContextBackgroundList(List paramList)
  {
    if (paramList == null)
      return;
    for (int i = 0; i < paramList.size(); i++)
    {
      String str = (String)paramList.get(i);
      if (str != null)
        ToolkitStore.get().getAppContext().remove("deploy-bg-" + str);
    }
  }

  protected static boolean isInBackgroundUpdateCheckList(URL paramURL)
  {
    String str = (String)ToolkitStore.get().getAppContext().get("deploy-bg-" + paramURL);
    return (str != null) && (str.equals("background"));
  }

  public abstract boolean hasEnhancedJarAccess();

  public abstract DeployCacheJarAccess getJarAccess();
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.model.ResourceProvider
 * JD-Core Version:    0.6.2
 */