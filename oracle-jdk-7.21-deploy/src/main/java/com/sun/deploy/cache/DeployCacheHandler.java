package com.sun.deploy.cache;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceProvider;
import com.sun.deploy.net.BasicHttpRequest;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.HttpUtils;
import com.sun.deploy.net.MessageHeader;
import com.sun.deploy.net.UpdateTracker;
import com.sun.deploy.net.offline.DeployOfflineManager;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.uitoolkit.ToolkitStore;
import com.sun.deploy.uitoolkit.UIToolkit;
import com.sun.deploy.util.URLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

public class DeployCacheHandler extends ResponseCache
{
  private final HashMap inProgress = new HashMap();
  private ThreadLocal inCacheHandler = new ThreadLocal();

  public static void reset()
  {
    ResponseCache.setDefault(new DeployCacheHandler());
    URLUtil.clearPack200Original();
  }

  public CacheResponse get(URI paramURI, String paramString, Map paramMap)
    throws IOException
  {
    if (DownloadEngine.haveDownloadInProgress())
      return null;
    Object localObject1 = null;
    if ((!Cache.isCacheEnabled()) || (!paramString.equals("GET")) || (this.inCacheHandler.get() != null))
      return null;
    Object localObject2;
    synchronized (this.inProgress)
    {
      if (!this.inProgress.containsKey(paramURI))
        this.inProgress.put(paramURI, new Object());
      localObject2 = this.inProgress.get(paramURI);
    }
    synchronized (localObject2)
    {
      try
      {
        this.inCacheHandler.set(Boolean.TRUE);
        File localFile = null;
        int i = 0;
        URL localURL1 = URLUtil.getPack200Original();
        if (localURL1 != null)
          i = 1;
        URL localURL2 = i != 0 ? localURL1 : paramURI.toURL();
        URL localURL3 = HttpUtils.removeQueryStringFromURL(localURL2);
        String str = (String)ToolkitStore.get().getAppContext().get("deploy-" + localURL3);
        boolean bool = false;
        int j = 1;
        if (i != 0)
          j = 4352;
        if ((!DeployOfflineManager.isGlobalOffline()) && (UpdateTracker.isUpdateCheckNeeded(localURL2.toString())))
          bool = ResourceProvider.get().isUpdateAvailable(localURL2, str, j, paramMap);
        if (!bool)
          try
          {
            localFile = (File)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              private final String val$jarVersion;
              private final URL val$url;
              private final URL val$urlNoQuery;
              private final Map val$requestHeaders;

              public Object run()
                throws IOException
              {
                URL localURL = this.val$jarVersion == null ? this.val$url : this.val$urlNoQuery;
                return DeployCacheHandler.this.getCacheFile(localURL, this.val$jarVersion, this.val$requestHeaders);
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException1)
          {
            Trace.ignoredException(localPrivilegedActionException1);
          }
        if (localFile == null)
        {
          localObject4 = null;
          this.inCacheHandler.set(null);
          synchronized (this.inProgress)
          {
            this.inProgress.remove(localObject2);
          }
          return localObject4;
        }
        Object localObject4 = null;
        ??? = localFile;
        if (??? != null)
        {
          try
          {
            localObject4 = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              private final File val$cachedFileF;

              public Object run()
                throws IOException
              {
                return new FileInputStream(this.val$cachedFileF);
              }
            });
          }
          catch (PrivilegedActionException localPrivilegedActionException2)
          {
            Trace.ignoredException(localPrivilegedActionException2);
          }
          if (localObject4 != null)
          {
            Resource localResource = ResourceProvider.get().getCachedResource(str == null ? localURL2 : localURL3, str);
            Map localMap = localResource != null ? localResource.getHeaders() : null;
            if (paramURI.getScheme().equals("https"))
              localObject1 = new DeploySecureCacheResponse((InputStream)localObject4, localMap);
            else
              localObject1 = new DeployCacheResponse((InputStream)localObject4, localMap);
          }
        }
      }
      finally
      {
        this.inCacheHandler.set(null);
        synchronized (this.inProgress)
        {
          this.inProgress.remove(localObject2);
        }
      }
    }
    return localObject1;
  }

  private File getCacheFile(URL paramURL, String paramString, Map paramMap)
  {
    CacheEntry localCacheEntry = Cache.getCacheEntry(paramURL, paramString);
    boolean bool = ResourceProvider.get().isInternalUse();
    int i = URLUtil.getPack200Original() != null ? 1 : 0;
    if ((localCacheEntry != null) && (localCacheEntry.isJarFile()))
      if ((bool) || (i != 0))
      {
        if (localCacheEntry.hasCompressEncoding())
          localCacheEntry = null;
      }
      else if (localCacheEntry.hasCompressEncoding())
      {
        if (!HttpUtils.matchEncoding(null, paramMap, localCacheEntry.getHeaders()))
          localCacheEntry = null;
      }
      else if ((!localCacheEntry.hasCompressEncoding()) && (HttpUtils.refusesIdentityEncodings(paramMap)))
        localCacheEntry = null;
    if (localCacheEntry != null)
      return new File(localCacheEntry.getResourceFilename());
    return null;
  }

  static boolean isResourceCacheable(String paramString, URLConnection paramURLConnection)
  {
    if ((!paramURLConnection.getUseCaches()) && (!DownloadEngine.isAlwaysCached(paramString)))
      return false;
    if (((paramURLConnection instanceof HttpURLConnection)) && (!((HttpURLConnection)paramURLConnection).getRequestMethod().equals("GET")))
      return false;
    try
    {
      MessageHeader localMessageHeader = BasicHttpRequest.initializeHeaderFields(paramURLConnection);
      return HttpUtils.isResourceCacheable(paramString, localMessageHeader, true);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    return false;
  }

  public CacheRequest put(URI paramURI, URLConnection paramURLConnection)
    throws IOException
  {
    if (DownloadEngine.haveDownloadInProgress())
      return null;
    if (!isResourceCacheable(paramURI.toString(), paramURLConnection))
      return null;
    URL localURL1 = URLUtil.getPack200Original();
    boolean bool = false;
    if (localURL1 != null)
      bool = true;
    URL localURL2 = bool ? localURL1 : paramURI.toURL();
    return new DeployCacheRequest(localURL2, paramURLConnection, bool);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.DeployCacheHandler
 * JD-Core Version:    0.6.2
 */