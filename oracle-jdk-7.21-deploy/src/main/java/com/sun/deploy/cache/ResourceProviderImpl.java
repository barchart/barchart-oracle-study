package com.sun.deploy.cache;

import com.sun.deploy.Environment;
import com.sun.deploy.model.DeployCacheJarAccess;
import com.sun.deploy.model.DownloadDelegate;
import com.sun.deploy.model.LocalApplicationProperties;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.FailedDownloadException;
import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;
import com.sun.deploy.net.HttpUtils;
import com.sun.deploy.net.UpdateTracker;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.URLUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceProviderImpl extends ResourceProvider
{
  static void init()
  {
    if (instance == null)
      set(new ResourceProviderImpl());
  }

  public void setBackgroundUpdateRequest(boolean paramBoolean)
  {
    DownloadEngine._setBackgroundUpdateRequest(paramBoolean);
  }

  public boolean isBackgroundUpdateRequest()
  {
    return DownloadEngine.isBackgroundUpdateRequest();
  }

  public int incrementInternalUse()
  {
    return DownloadEngine._incrementInternalUse();
  }

  public void decrementInternalUse(int paramInt)
  {
    DownloadEngine._decrementInternalUse(paramInt);
  }

  public boolean isUpdateAvailable(URL paramURL, String paramString, int paramInt, Map paramMap)
    throws IOException
  {
    if (UpdateTracker.isUpdated(paramURL.toString()))
      return false;
    URL localURL = HttpUtils.removeQueryStringFromURL(paramURL);
    if ((!DownloadEngine.isBackgroundUpdateRequest()) && (isInBackgroundUpdateCheckList(paramURL)))
      return false;
    CacheEntry localCacheEntry = null;
    if (Cache.isCacheEnabled())
      localCacheEntry = Cache.getCacheEntry(paramString == null ? paramURL : localURL, paramString);
    if (localCacheEntry == null)
      return true;
    if (paramString != null)
      return false;
    if (!isValidationRequired(localCacheEntry))
      return false;
    return checkUpdateAvailable(paramURL, localCacheEntry, paramInt, paramMap);
  }

  public boolean checkUpdateAvailable(URL paramURL, Resource paramResource, int paramInt, Map paramMap)
    throws IOException
  {
    String str = paramResource.getVersion();
    if (!DeployOfflineManager.promptUserGoOnline(paramURL))
      throw new FailedDownloadException(paramURL, null, null, true);
    if (DeployOfflineManager.isGlobalOffline())
      throw new FailedDownloadException(paramURL, null, null, true);
    URL localURL1 = DownloadEngine.getRequestURL(paramURL, str, null, false, null);
    HttpRequest localHttpRequest = DownloadEngine.getHttpRequestImpl();
    HttpResponse localHttpResponse = null;
    long l1 = -1L;
    l1 = paramResource.getLastModified();
    URL localURL2 = null;
    if (DownloadEngine.isPackContentType(paramInt))
      localURL2 = URLUtil.getPack200URL(localURL1, false);
    String[] arrayOfString1 = null;
    String[] arrayOfString2 = null;
    if (paramMap != null)
    {
      arrayOfString1 = (String[])paramMap.keySet().toArray(new String[0]);
      arrayOfString2 = new String[arrayOfString1.length];
      for (int i = 0; i < arrayOfString1.length; i++)
      {
        Object localObject = paramMap.get(arrayOfString1[i]);
        if ((localObject != null) && ((localObject instanceof List)))
          arrayOfString2[i] = ((String)(String)((List)localObject).get(0));
      }
    }
    try
    {
      localHttpResponse = localHttpRequest.doGetRequestEX(localURL2 != null ? localURL2 : localURL1, arrayOfString1, arrayOfString2, l1);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      if (localURL2 == null)
        throw localFileNotFoundException;
      localHttpResponse = localHttpRequest.doGetRequestEX(localURL1, l1);
    }
    if (localHttpResponse == null)
      return true;
    int j = localHttpResponse.getStatusCode();
    localHttpResponse.disconnect();
    boolean bool = true;
    if (j == 304)
    {
      bool = false;
    }
    else if (j == 200)
    {
      int k = localHttpResponse.getContentLength();
      long l3 = localHttpResponse.getLastModified();
      if ((l3 == l1) && (k == paramResource.getContentLength()))
        bool = false;
      Trace.println("CacheEntry[" + paramURL + "]: updateAvailable=" + bool + ",lastModified=" + new Date(l1) + ",length=" + paramResource.getContentLength(), TraceLevel.DEFAULT);
    }
    if (!bool)
    {
      UpdateTracker.checkDone(paramURL.toString());
      long l2 = HttpUtils.getEffectiveExpiration(localHttpResponse);
      if ((l2 != 0L) && ((paramResource instanceof CacheEntry)))
        ((CacheEntry)paramResource).updateExpirationInIndexFile(l2);
    }
    return bool;
  }

  public Resource getResource(URL paramURL, String paramString, boolean paramBoolean, int paramInt, DownloadDelegate paramDownloadDelegate)
    throws IOException
  {
    return getResource(paramURL, paramString, paramBoolean, paramInt, paramDownloadDelegate, false, null);
  }

  private Resource getResource(URL paramURL, String paramString1, boolean paramBoolean1, int paramInt, DownloadDelegate paramDownloadDelegate, boolean paramBoolean2, String paramString2)
    throws IOException
  {
    if (paramURL == null)
      return null;
    Object localObject1 = null;
    int i = (paramBoolean1) && (isUpdateAvailable(paramURL, paramString1, paramInt, null)) ? 1 : 0;
    Object localObject2;
    if (i == 0)
    {
      localObject2 = MemoryCache.getLoadedResource(paramURL.toString());
      if ((localObject2 instanceof Resource))
        localObject1 = (Resource)localObject2;
      if ((localObject1 != null) && (CacheEntry.matchesVersionString((Resource)localObject1, paramString1, true)))
        return localObject1;
    }
    if (Cache.isCacheEnabled())
    {
      localObject1 = Cache.getCacheEntry(paramURL, paramString1, paramInt);
    }
    else
    {
      localObject2 = MemoryCache.getLoadedResource(paramURL.toString());
      if ((localObject2 instanceof Resource))
        localObject1 = (Resource)localObject2;
    }
    if ((paramBoolean1) && ((localObject1 == null) || (isValidationRequired((Resource)localObject1))))
    {
      Environment.setDownloadInitiated(true);
      localObject1 = DownloadEngine.downloadResource((Resource)localObject1, paramURL, paramString1, paramDownloadDelegate, paramBoolean2, paramString2, paramInt, true);
      if (localObject1 != null)
      {
        Trace.println("Downloaded " + paramURL + ": " + ((Resource)localObject1).getResourceFilename(), TraceLevel.NETWORK);
        MemoryCache.addLoadedResource(paramURL.toString(), localObject1);
      }
      if ((localObject1 != null) && (Cache.isCacheEnabled()))
        Cache.touch(new File(((Resource)localObject1).getResourceFilename() + Cache.getIndexFileExtension()));
    }
    return localObject1;
  }

  public Resource downloadUpdate(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return DownloadEngine.downloadResource(null, paramURL, paramString, null, false, null, paramInt, paramBoolean);
  }

  public void markReady(Resource[] paramArrayOfResource)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfResource.length; i++)
      if ((paramArrayOfResource[i] instanceof CacheEntry))
      {
        CacheEntry localCacheEntry1 = (CacheEntry)paramArrayOfResource[i];
        URL localURL = new URL(localCacheEntry1.getURL());
        CacheEntry localCacheEntry2 = Cache.getCacheEntry(localURL, localCacheEntry1.getVersion());
        Cache.processNewCacheEntry(localURL, true, localCacheEntry1, localCacheEntry2);
      }
  }

  private boolean isValidationRequired(Resource paramResource)
  {
    if (!(paramResource instanceof CacheEntry))
      return true;
    CacheEntry localCacheEntry = (CacheEntry)paramResource;
    boolean bool = true;
    if (!localCacheEntry.isExpired())
    {
      Trace.println("Resource " + localCacheEntry.getURL() + " has future expires: " + new Date(localCacheEntry.getExpirationDate()) + " update check skipped.");
      bool = false;
    }
    else if (Trace.isEnabled(TraceLevel.CACHE))
    {
      Trace.println("Resource " + localCacheEntry.getURL() + " has expired.", TraceLevel.CACHE);
    }
    if (localCacheEntry.isHttpNoCacheEnabled())
    {
      bool = true;
      if (Trace.isEnabled(TraceLevel.CACHE))
        Trace.println("Resource " + localCacheEntry.getURL() + " has cache control: no-cache.", TraceLevel.CACHE);
    }
    return bool;
  }

  public Resource getJreResource(URL paramURL, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2)
    throws IOException
  {
    return getResource(paramURL, paramString1, paramBoolean1, 1, null, paramBoolean2, paramString2);
  }

  public void markRetired(Resource paramResource, boolean paramBoolean)
  {
    if (!(paramResource instanceof CacheEntry))
      return;
    CacheEntry localCacheEntry = (CacheEntry)paramResource;
    if (paramBoolean)
      Cache.removeCacheEntry(localCacheEntry);
    else
      Cache.markResourceIncomplete(localCacheEntry);
  }

  public File getShortcutImage(URL paramURL, String paramString, boolean paramBoolean)
    throws IOException
  {
    Resource localResource = getResource(paramURL, paramString, paramBoolean, 1, null, false, null);
    if ((localResource instanceof CacheEntry))
    {
      ((CacheEntry)localResource).generateShortcutImage();
      return localResource.getDataFile();
    }
    return null;
  }

  public String getLibraryDirForJar(String paramString1, URL paramURL, String paramString2)
    throws IOException
  {
    int i = ResourceProvider.get().incrementInternalUse();
    try
    {
      Resource localResource = getCachedResource(paramURL, paramString2);
      if (localResource == null)
        localResource = getResource(paramURL, paramString2, true, 272, null);
      Object localObject1 = localResource != null ? localResource.getDataFile() : null;
      if (localObject1 != null)
      {
        String str1 = localObject1.getPath() + "-n";
        File localFile = new File(str1, paramString1);
        Trace.println("Looking up native library in: " + localFile, TraceLevel.CACHE);
        if (localFile.exists())
        {
          String str2 = str1;
          return str2;
        }
      }
    }
    finally
    {
      ResourceProvider.get().decrementInternalUse(i);
    }
    return null;
  }

  public Resource getSystemResource(URL paramURL, String paramString)
  {
    return Cache.getSystemCacheEntry(paramURL, paramString);
  }

  public void preverifyCachedJar(URL paramURL, String paramString, URLClassLoader paramURLClassLoader)
  {
    Resource localResource = getSystemResource(paramURL, paramString);
    if (!(localResource instanceof CacheEntry))
      return;
    CacheEntry localCacheEntry = (CacheEntry)localResource;
    if (localCacheEntry.getClassesVerificationStatus() == 0)
      localCacheEntry.verifyJAR(paramURLClassLoader);
  }

  public boolean isInternalUse()
  {
    return DownloadEngine.isInternalUse();
  }

  public void decrementsInternalUse(int paramInt)
  {
    DownloadEngine._decrementInternalUse(paramInt);
  }

  public LocalApplicationProperties getLocalApplicationProperties(URL paramURL, String paramString, boolean paramBoolean)
  {
    return Cache.getLocalApplicationProperties(paramURL, paramString, paramBoolean);
  }

  public boolean canCache(URL paramURL)
  {
    if (paramURL == null)
      return Cache.isCacheEnabled();
    if ((Cache.isCacheEnabled()) && (Cache.isSupportedProtocol(paramURL)))
    {
      boolean bool = HttpUtils.resourceNotCached(paramURL.toString());
      return !bool;
    }
    return false;
  }

  public File getCacheDir()
  {
    return Cache.getCacheDir();
  }

  public String getCurrentVersion(URL paramURL)
  {
    return Cache.getCacheEntryVersion(paramURL);
  }

  public ResourceObject getResourceObject(String paramString)
  {
    String str = paramString + ".idx";
    File localFile = new File(str);
    CacheEntry localCacheEntry = Cache.getCacheEntryFromFileIncludeTempJNLP(localFile);
    if (localCacheEntry == null)
      return null;
    if ((!localCacheEntry.isJNLPFile()) && ((localCacheEntry.getJarFile() instanceof ResourceObject)))
      return (ResourceObject)localCacheEntry.getJarFile();
    return new ResourceObject()
    {
      private final CacheEntry val$ce;

      public Object clone()
        throws CloneNotSupportedException
      {
        return super.clone();
      }

      public URL getResourceURL()
      {
        try
        {
          return new URL(this.val$ce.getURL());
        }
        catch (MalformedURLException localMalformedURLException)
        {
          Trace.ignored(localMalformedURLException);
        }
        return null;
      }

      public String getResourceVersion()
      {
        return this.val$ce.getVersion();
      }

      public void doClose()
        throws IOException
      {
        close();
      }

      public void close()
        throws IOException
      {
      }
    };
  }

  public DeployCacheJarAccess getJarAccess()
  {
    return DeployCacheJarAccessImpl.getJarAccess();
  }

  public boolean hasEnhancedJarAccess()
  {
    return CacheEntry.hasEnhancedJarAccess();
  }

  public String getCachedResourceFilePath(URL paramURL, String paramString)
    throws IOException
  {
    return Cache.getCachedResourceFilePath(paramURL, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.ResourceProviderImpl
 * JD-Core Version:    0.6.2
 */