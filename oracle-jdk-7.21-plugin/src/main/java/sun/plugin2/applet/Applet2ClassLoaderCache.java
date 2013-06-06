package sun.plugin2.applet;

import com.sun.deploy.appcontext.AppContext;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.trace.Trace;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.plugin2.util.SystemUtil;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class Applet2ClassLoaderCache
{
  private static final boolean DEBUG;
  private EntryCreator entryCreator;
  private static final boolean IS_IN_USE;
  private Map cache = new HashMap();
  private List zombieList = new ArrayList();
  private static final int MAX_ZOMBIES = i;

  public Applet2ClassLoaderCache(EntryCreator paramEntryCreator)
  {
    this.entryCreator = paramEntryCreator;
  }

  public boolean isInUse()
  {
    return IS_IN_USE;
  }

  public Entry get(String paramString, Applet2Manager paramApplet2Manager)
  {
    assert (Thread.holdsLock(paramApplet2Manager));
    Entry localEntry = null;
    synchronized (this)
    {
      int i = 0;
      localEntry = (Entry)this.cache.get(paramString);
      if (localEntry == null)
      {
        i = 1;
        localEntry = lookupInZombieList(paramString);
        if (localEntry == null)
        {
          localEntry = new Entry(paramString, null);
          if (DEBUG)
            System.out.println("Applet2ClassLoaderCache created new entry for " + paramString);
        }
        else if (DEBUG)
        {
          System.out.println("Applet2ClassLoaderCache using zombie list entry for " + paramString);
        }
      }
      else if (DEBUG)
      {
        System.out.println("Applet2ClassLoaderCache reusing entry for " + paramString);
      }
      if (i != 0)
        this.cache.put(paramString, localEntry);
    }
    localEntry.ref(paramApplet2Manager);
    return localEntry;
  }

  public void release(Entry paramEntry, Applet2Manager paramApplet2Manager, Applet2StopListener paramApplet2StopListener, long paramLong)
  {
    assert (Thread.holdsLock(paramApplet2Manager));
    paramEntry.unref(paramApplet2Manager, paramApplet2StopListener, paramLong);
  }

  public synchronized void markNotCacheable(String paramString)
  {
    this.cache.remove(paramString);
    Iterator localIterator = this.zombieList.iterator();
    while (localIterator.hasNext())
    {
      SoftReference localSoftReference = (SoftReference)localIterator.next();
      Entry localEntry = (Entry)localSoftReference.get();
      if ((localEntry != null) && (localEntry.getClassLoaderCacheKey().equals(paramString)))
        localIterator.remove();
    }
  }

  public synchronized void markNotCacheable(Entry paramEntry)
  {
    conditionallyRemoveFromCache(paramEntry);
  }

  public synchronized void clear()
  {
    this.cache.clear();
    this.zombieList.clear();
  }

  public synchronized void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println(ResourceManager.getMessage("console.dump.classloader.cache"));
    Iterator localIterator = this.cache.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Entry)localIterator.next();
      paramPrintWriter.print(ResourceManager.getMessage("console.dump.classloader.live"));
      ((Entry)localObject).dump(paramPrintWriter);
    }
    localIterator = this.zombieList.iterator();
    while (localIterator.hasNext())
    {
      localObject = (SoftReference)localIterator.next();
      Entry localEntry = (Entry)((SoftReference)localObject).get();
      if (localEntry != null)
      {
        paramPrintWriter.print(ResourceManager.getMessage("console.dump.classloader.zombie"));
        localEntry.dump(paramPrintWriter);
      }
    }
    paramPrintWriter.println(ResourceManager.getMessage("console.dump.classloader.done"));
  }

  private Entry conditionallyRemoveFromCache(Entry paramEntry)
  {
    Entry localEntry = (Entry)this.cache.remove(paramEntry.getClassLoaderCacheKey());
    if (localEntry == paramEntry)
      return localEntry;
    return null;
  }

  private void cleanupZombieList()
  {
    Iterator localIterator = this.zombieList.iterator();
    while (localIterator.hasNext())
    {
      SoftReference localSoftReference = (SoftReference)localIterator.next();
      if (localSoftReference.get() == null)
        localIterator.remove();
    }
  }

  private synchronized void moveToZombieList(Entry paramEntry)
  {
    paramEntry = conditionallyRemoveFromCache(paramEntry);
    if (paramEntry != null)
    {
      cleanupZombieList();
      this.zombieList.add(0, new SoftReference(paramEntry));
      if (this.zombieList.size() > MAX_ZOMBIES)
        this.zombieList.remove(this.zombieList.size() - 1);
    }
  }

  private Entry lookupInZombieList(String paramString)
  {
    Iterator localIterator = this.zombieList.iterator();
    while (localIterator.hasNext())
    {
      SoftReference localSoftReference = (SoftReference)localIterator.next();
      Entry localEntry = (Entry)localSoftReference.get();
      if ((localEntry != null) && (localEntry.getClassLoaderCacheKey().equals(paramString)))
      {
        localIterator.remove();
        return localEntry;
      }
    }
    return null;
  }

  public void shutdown()
  {
    if (DEBUG)
      System.out.println("Classloader Cache shuting down...");
    ArrayList localArrayList = new ArrayList(this.cache.values());
    Object localObject;
    Entry localEntry;
    for (int i = 0; i < localArrayList.size(); i++)
    {
      localObject = localArrayList.get(i);
      if ((localObject instanceof Entry))
      {
        localEntry = (Entry)localObject;
        if ((localEntry != null) && (localEntry.getClassLoader() != null))
          try
          {
            localEntry.getClassLoader().close();
          }
          catch (IOException localIOException1)
          {
            Trace.ignored(localIOException1);
          }
      }
    }
    for (i = 0; i < this.zombieList.size(); i++)
    {
      localObject = (SoftReference)this.zombieList.get(i);
      if (localObject != null)
      {
        localEntry = (Entry)((SoftReference)localObject).get();
        if ((localEntry != null) && (localEntry.getClassLoader() != null))
          try
          {
            localEntry.getClassLoader().close();
          }
          catch (IOException localIOException2)
          {
            Trace.ignored(localIOException2);
          }
      }
    }
    clear();
  }

  static
  {
    DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.classloader.cache.enabled"));
    IS_IN_USE = (str == null) || (str.equals("true"));
    int i = ((Integer)AccessController.doPrivileged(new GetIntegerAction("javaplugin.classloader.cache.sizes", 4))).intValue();
    if (i > 4)
      i = 4;
  }

  public class Entry
  {
    private String classLoaderCacheKey;
    private int refCount;
    private Applet2ClassLoader classLoader;
    private AppContext appContext;
    private ThreadGroup threadGroup;

    private Entry(String arg2)
    {
      Object localObject;
      this.classLoaderCacheKey = localObject;
    }

    public String getClassLoaderCacheKey()
    {
      return this.classLoaderCacheKey;
    }

    public void setClassLoader(Applet2ClassLoader paramApplet2ClassLoader)
    {
      this.classLoader = paramApplet2ClassLoader;
    }

    public Applet2ClassLoader getClassLoader()
    {
      return this.classLoader;
    }

    public void setAppContext(AppContext paramAppContext)
    {
      this.appContext = paramAppContext;
    }

    public AppContext getAppContext()
    {
      return this.appContext;
    }

    public void setThreadGroup(ThreadGroup paramThreadGroup)
    {
      this.threadGroup = paramThreadGroup;
    }

    public ThreadGroup getThreadGroup()
    {
      return this.threadGroup;
    }

    private synchronized void ref(Applet2Manager paramApplet2Manager)
    {
      assert (!Thread.holdsLock(Applet2ClassLoaderCache.this));
      if (++this.refCount == 1)
        if (this.classLoader == null)
          Applet2ClassLoaderCache.this.entryCreator.createAll(paramApplet2Manager, this);
        else
          Applet2ClassLoaderCache.this.entryCreator.createThreadGroupAndAppContext(paramApplet2Manager, this);
    }

    private synchronized void unref(Applet2Manager paramApplet2Manager, Applet2StopListener paramApplet2StopListener, long paramLong)
    {
      assert (!Thread.holdsLock(Applet2ClassLoaderCache.this));
      if (--this.refCount == 0)
      {
        Applet2ClassLoaderCache.this.entryCreator.destroyThreadGroupAndAppContext(paramApplet2Manager, paramApplet2StopListener, paramLong, this);
        Applet2ClassLoaderCache.this.moveToZombieList(this);
      }
    }

    private synchronized void dump(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("key=" + this.classLoaderCacheKey + ", refCount=" + this.refCount + ", threadGroup=" + this.threadGroup);
    }

    Entry(String param1, Applet2ClassLoaderCache.1 arg3)
    {
      this(param1);
    }
  }

  public static abstract interface EntryCreator
  {
    public abstract void createAll(Applet2Manager paramApplet2Manager, Applet2ClassLoaderCache.Entry paramEntry);

    public abstract void createThreadGroupAndAppContext(Applet2Manager paramApplet2Manager, Applet2ClassLoaderCache.Entry paramEntry);

    public abstract void destroyThreadGroupAndAppContext(Applet2Manager paramApplet2Manager, Applet2StopListener paramApplet2StopListener, long paramLong, Applet2ClassLoaderCache.Entry paramEntry);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2ClassLoaderCache
 * JD-Core Version:    0.6.2
 */