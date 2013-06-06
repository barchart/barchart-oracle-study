package com.sun.deploy.cache;

import com.sun.deploy.model.Resource;
import com.sun.deploy.model.ResourceObject;
import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.net.UpdateTracker;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarFile;

public class MemoryCache
{
  private static final Map loadedResource;
  private static ReferenceQueue refQueue = new ReferenceQueue();
  private static Thread cleanupThread;
  static long CLEANUP_DELAY = 60000L;

  static synchronized void reset()
  {
    loadedResource.clear();
  }

  public static synchronized Object addLoadedResource(String paramString, Object paramObject)
  {
    LoadedResourceReference localLoadedResourceReference1 = (LoadedResourceReference)loadedResource.get(paramString);
    Object localObject = localLoadedResourceReference1 != null ? localLoadedResourceReference1.get() : null;
    if (localObject != null)
    {
      if (paramObject == localObject)
        return null;
      if ((localLoadedResourceReference1.getReferenceCount() > 0) && (DownloadEngine.isBackgroundUpdateRequest()))
      {
        Trace.println("MemoryCache: skip loading backgroung update " + paramString + ": refcnt=" + localLoadedResourceReference1.refcnt, TraceLevel.CACHE);
        return null;
      }
      Trace.println("MemoryCache replacing " + paramString + " (refcnt=" + localLoadedResourceReference1.refcnt + "). Was: " + localObject + " Now: " + paramObject, TraceLevel.CACHE);
    }
    else
    {
      Trace.println("Adding MemoryCache entry: " + paramString, TraceLevel.CACHE);
    }
    LoadedResourceReference localLoadedResourceReference2 = new LoadedResourceReference(paramObject);
    if (!(paramObject instanceof CacheEntry))
      localLoadedResourceReference2.registerReference(new CachedResourceReference(paramObject, refQueue, paramString));
    LoadedResourceReference localLoadedResourceReference3 = (LoadedResourceReference)loadedResource.put(paramString, localLoadedResourceReference2);
    if (localLoadedResourceReference3 != null)
      return localLoadedResourceReference3.get();
    return null;
  }

  public static synchronized Object getLoadedResource(String paramString)
  {
    return getLoadedResource(paramString, true);
  }

  static synchronized Object getLoadedResource(String paramString, boolean paramBoolean)
  {
    LoadedResourceReference localLoadedResourceReference = (LoadedResourceReference)loadedResource.get(paramString);
    if (localLoadedResourceReference == null)
      return null;
    Object localObject = localLoadedResourceReference.get();
    if (localObject != null)
    {
      if (validateResource(localObject))
      {
        if (paramBoolean)
        {
          localLoadedResourceReference.cancelDelayRemoval();
          addResourceReference(localObject, paramString);
        }
        return localObject;
      }
      loadedResource.remove(paramString);
    }
    return null;
  }

  static synchronized Object removeLoadedResource(String paramString)
  {
    LoadedResourceReference localLoadedResourceReference = (LoadedResourceReference)loadedResource.remove(paramString);
    if (localLoadedResourceReference != null)
    {
      Object localObject = localLoadedResourceReference.get();
      Trace.println("MemoryCache: removed entry " + paramString, TraceLevel.CACHE);
      return localObject;
    }
    return null;
  }

  public static synchronized void clearLoadedResources()
  {
    loadedResource.clear();
    UpdateTracker.clear();
    DownloadEngine.clearNoCacheJarFileList();
  }

  public static synchronized void shutdown()
  {
    closeJars();
    clearLoadedResources();
  }

  private static void closeJars()
  {
    Iterator localIterator = loadedResource.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      ensureJarsClosed(localEntry.getValue());
    }
    reset();
  }

  private static void ensureJarsClosed(Object paramObject)
  {
    if ((paramObject instanceof LoadedResourceReference))
    {
      LoadedResourceReference localLoadedResourceReference = (LoadedResourceReference)paramObject;
      closeIfJar(localLoadedResourceReference.get());
      Iterator localIterator = localLoadedResourceReference.getReferences();
      while (localIterator.hasNext())
      {
        CachedResourceReference localCachedResourceReference = (CachedResourceReference)localIterator.next();
        closeIfJar(localCachedResourceReference.get());
      }
    }
  }

  private static void closeIfJar(Object paramObject)
  {
    try
    {
      if ((paramObject instanceof CacheEntry))
      {
        CacheEntry localCacheEntry = (CacheEntry)paramObject;
        paramObject = localCacheEntry.getRefJar();
      }
      else if (((paramObject instanceof Resource)) && (((Resource)paramObject).isJarFile()))
      {
        paramObject = ((Resource)paramObject).getJarFile();
      }
      if ((paramObject instanceof ResourceObject))
        ((ResourceObject)paramObject).doClose();
      else if ((paramObject instanceof JarFile))
        ((JarFile)paramObject).close();
    }
    catch (Exception localException)
    {
    }
  }

  static synchronized void addResourceReference(Object paramObject, String paramString)
  {
    LoadedResourceReference localLoadedResourceReference = (LoadedResourceReference)loadedResource.get(paramString);
    if (localLoadedResourceReference != null)
      localLoadedResourceReference.registerReference(new CachedResourceReference(paramObject, refQueue, paramString));
  }

  static synchronized boolean contains(String paramString)
  {
    return loadedResource.containsKey(paramString);
  }

  private static boolean validateResource(Object paramObject)
  {
    if (paramObject == null)
      return false;
    if ((paramObject instanceof CacheEntry))
    {
      CacheEntry localCacheEntry = (CacheEntry)paramObject;
      if (Cache.hasIncompatibleCompressEncoding(localCacheEntry))
        return false;
      if (!localCacheEntry.storageFilesExist())
        return false;
    }
    return true;
  }

  public static synchronized boolean isCacheEntryLoaded(String paramString1, String paramString2)
  {
    Object localObject = getLoadedResource(paramString1, false);
    if ((localObject instanceof CacheEntry))
    {
      CacheEntry localCacheEntry = (CacheEntry)localObject;
      if (((localCacheEntry.getVersion() == null) && (paramString2 == null)) || ((paramString2 != null) && (paramString2.equals(localCacheEntry.getVersion()))))
        return true;
    }
    return false;
  }

  static int getReferenceCount(String paramString)
  {
    LoadedResourceReference localLoadedResourceReference = (LoadedResourceReference)loadedResource.get(paramString);
    return localLoadedResourceReference == null ? 0 : localLoadedResourceReference.getReferenceCount();
  }

  static LoadedResourceReference getReferences(String paramString)
  {
    return (LoadedResourceReference)loadedResource.get(paramString);
  }

  static
  {
    loadedResource = new HashMap();
    cleanupThread = new LoadedResourceCleanupThread("CacheMemoryCleanUpThread");
    cleanupThread.setDaemon(true);
    cleanupThread.start();
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      public void run()
      {
        MemoryCache.shutdown();
      }
    });
  }

  private static class CachedResourceReference extends WeakReference
  {
    String url;

    public CachedResourceReference(Object paramObject, ReferenceQueue paramReferenceQueue, String paramString)
    {
      super(paramReferenceQueue);
      this.url = paramString;
    }

    public String getURL()
    {
      return this.url;
    }

    public int hashCode()
    {
      return this.url.hashCode();
    }

    public boolean equals(Object paramObject)
    {
      CachedResourceReference localCachedResourceReference = (CachedResourceReference)paramObject;
      return (localCachedResourceReference != null) && (get() == localCachedResourceReference.get()) && (this.url.equals(localCachedResourceReference.getURL()));
    }
  }

  static class LoadedResourceCleanupThread extends Thread
  {
    Timer timer = new Timer("MemoryCache-DelayedCleanup", true);

    LoadedResourceCleanupThread(String paramString)
    {
      super();
    }

    public void run()
    {
      while (true)
        try
        {
          MemoryCache.CachedResourceReference localCachedResourceReference = (MemoryCache.CachedResourceReference)MemoryCache.refQueue.remove();
          synchronized (MemoryCache.loadedResource)
          {
            String str = localCachedResourceReference.getURL();
            MemoryCache.LoadedResourceReference localLoadedResourceReference = (MemoryCache.LoadedResourceReference)MemoryCache.loadedResource.get(str);
            if ((localLoadedResourceReference != null) && (localLoadedResourceReference.deregisterReference(localCachedResourceReference)))
              delayedRemoveResource(localLoadedResourceReference, str);
          }
        }
        catch (InterruptedException localInterruptedException)
        {
        }
        catch (Exception localException)
        {
          if (Trace.isEnabled(TraceLevel.CACHE))
            Trace.ignored(localException);
        }
    }

    private void delayedRemoveResource(MemoryCache.LoadedResourceReference paramLoadedResourceReference, String paramString)
    {
      TimerTask local1 = new TimerTask()
      {
        private final String val$url;

        public void run()
        {
          if (Cache.DEBUG)
            Trace.println("Cleanup delay timer expires @" + System.currentTimeMillis() + " for " + this.val$url, TraceLevel.CACHE);
          MemoryCache.removeLoadedResource(this.val$url);
        }
      };
      this.timer.schedule(local1, MemoryCache.CLEANUP_DELAY);
      if (Cache.DEBUG)
        Trace.println("Started cleanup timer (" + MemoryCache.CLEANUP_DELAY + " ms) for: " + paramString, TraceLevel.CACHE);
      paramLoadedResourceReference.setCleanupTask(local1);
    }
  }

  static class LoadedResourceReference
  {
    private Set resourceRefs = new HashSet();
    private int refcnt = 0;
    private Object o;
    private TimerTask delayedRemoveTask;

    LoadedResourceReference(Object paramObject)
    {
      this.o = paramObject;
    }

    Object get()
    {
      return this.o;
    }

    synchronized void registerReference(Reference paramReference)
    {
      if (this.resourceRefs.add(paramReference))
        this.refcnt += 1;
    }

    synchronized boolean deregisterReference(Reference paramReference)
    {
      this.refcnt -= 1;
      this.resourceRefs.remove(paramReference);
      return this.refcnt <= 1;
    }

    synchronized int getReferenceCount()
    {
      return this.refcnt;
    }

    synchronized Iterator getReferences()
    {
      return this.resourceRefs.iterator();
    }

    synchronized void cancelDelayRemoval()
    {
      if (this.delayedRemoveTask != null)
      {
        Trace.println("Cancel delay cleanup: " + this.o, TraceLevel.CACHE);
        this.delayedRemoveTask.cancel();
        this.delayedRemoveTask = null;
      }
    }

    synchronized void setCleanupTask(TimerTask paramTimerTask)
    {
      cancelDelayRemoval();
      this.delayedRemoveTask = paramTimerTask;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.MemoryCache
 * JD-Core Version:    0.6.2
 */