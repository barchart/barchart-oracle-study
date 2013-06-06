package sun.plugin;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.io.PrintWriter;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import sun.applet.AppletClassLoader;
import sun.applet.AppletPanel;
import sun.plugin.security.PluginClassLoader;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class ClassLoaderInfo
{
  private URL codebase;
  private String key;
  private int references = 0;
  private HashMap jars;
  private boolean locked;
  private boolean isCachable = true;
  private static boolean initialized;
  private static HashMap infos = new HashMap();
  private static int zombieLimit = 0;
  private static ArrayList zombies = new ArrayList();
  private LoaderReference loaderRef = null;
  private static ReferenceQueue refQueue = new ReferenceQueue();
  private boolean localJarsLoaded = false;

  private static synchronized void initialize()
  {
    if (initialized)
      return;
    initialized = true;
    reset();
  }

  public static synchronized void reset()
  {
    initialized = true;
    zombieLimit = 0;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("javaplugin.classloader.cache.enabled"));
    if ((str == null) || (str.equals("true")))
      zombieLimit = ((Integer)AccessController.doPrivileged(new GetIntegerAction("javaplugin.classloader.cache.sizes", 4))).intValue();
    if (zombieLimit > 4)
      zombieLimit = 4;
  }

  public static synchronized void clearClassLoaderCache()
  {
    Iterator localIterator1 = zombies.iterator();
    while (localIterator1.hasNext())
    {
      localObject = (ClassLoaderInfo)localIterator1.next();
      if (localObject != null)
      {
        infos.remove(((ClassLoaderInfo)localObject).key);
        ((ClassLoaderInfo)localObject).clearLoaderRef();
      }
    }
    zombies.clear();
    Object localObject = infos.values();
    if (localObject != null)
    {
      localIterator1 = ((Collection)localObject).iterator();
      while (localIterator1.hasNext())
      {
        ClassLoaderInfo localClassLoaderInfo = null;
        ArrayList localArrayList = (ArrayList)localIterator1.next();
        if (localArrayList != null)
        {
          Iterator localIterator2 = localArrayList.iterator();
          while (localIterator2.hasNext())
          {
            localClassLoaderInfo = (ClassLoaderInfo)localIterator2.next();
            if (localClassLoaderInfo != null)
              localClassLoaderInfo.isCachable = false;
          }
        }
      }
    }
    AppletPanel.flushClassLoaders();
  }

  public static synchronized void dumpClassLoaderCache(PrintWriter paramPrintWriter)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Dump classloader list ...\n");
    Collection localCollection = infos.values();
    if (localCollection != null)
    {
      Iterator localIterator1 = localCollection.iterator();
      while (localIterator1.hasNext())
      {
        ArrayList localArrayList = (ArrayList)localIterator1.next();
        if (localArrayList != null)
        {
          Iterator localIterator2 = localArrayList.iterator();
          while (localIterator2.hasNext())
          {
            ClassLoaderInfo localClassLoaderInfo = (ClassLoaderInfo)localIterator2.next();
            if ((localClassLoaderInfo != null) && (localClassLoaderInfo.loaderRef != null) && (localClassLoaderInfo.loaderRef.get() != null))
            {
              boolean bool = zombies.contains(localClassLoaderInfo);
              localStringBuffer.append("    codebase=" + localClassLoaderInfo.codebase);
              localStringBuffer.append(", key=" + localClassLoaderInfo.key);
              localStringBuffer.append(", zombie=" + bool);
              localStringBuffer.append(", cache=" + localClassLoaderInfo.isCachable);
              localStringBuffer.append(", refcount=" + localClassLoaderInfo.references);
              localStringBuffer.append(", info=" + localClassLoaderInfo);
              localStringBuffer.append("\n");
            }
          }
        }
      }
    }
    localStringBuffer.append("Done.");
    paramPrintWriter.println(localStringBuffer.toString());
  }

  public static synchronized void markNotCachable(URL paramURL, String paramString)
  {
    assert (checkListsValidity() == true);
    ClassLoaderInfo localClassLoaderInfo = getUsableClassLoaderInfo(paramString);
    if (localClassLoaderInfo != null)
    {
      localClassLoaderInfo.isCachable = false;
      if (zombies.remove(localClassLoaderInfo) == true)
        removeClassLoaderInfo(localClassLoaderInfo);
      AppletPanel.flushClassLoader(paramString);
    }
    assert (checkListsValidity() == true);
  }

  private static synchronized void removeClassLoaderInfo(ClassLoaderInfo paramClassLoaderInfo)
  {
    ArrayList localArrayList = (ArrayList)infos.get(paramClassLoaderInfo.key);
    if (localArrayList != null)
    {
      localArrayList.remove(paramClassLoaderInfo);
      if (localArrayList.size() == 0)
        infos.remove(paramClassLoaderInfo.key);
    }
    paramClassLoaderInfo.clearLoaderRef();
  }

  private static synchronized void addClassLoaderInfo(ClassLoaderInfo paramClassLoaderInfo)
  {
    ArrayList localArrayList = (ArrayList)infos.get(paramClassLoaderInfo.key);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      localArrayList.add(paramClassLoaderInfo);
      infos.put(paramClassLoaderInfo.key, localArrayList);
    }
    else
    {
      localArrayList.add(paramClassLoaderInfo);
    }
  }

  private static synchronized ClassLoaderInfo getUsableClassLoaderInfo(String paramString)
  {
    ArrayList localArrayList = (ArrayList)infos.get(paramString);
    if (localArrayList != null)
    {
      Iterator localIterator = localArrayList.iterator();
      ClassLoaderInfo localClassLoaderInfo = null;
      while (localIterator.hasNext())
      {
        localClassLoaderInfo = (ClassLoaderInfo)localIterator.next();
        if (localClassLoaderInfo.isCachable == true)
          return localClassLoaderInfo;
      }
    }
    return null;
  }

  public static synchronized ClassLoaderInfo find(URL paramURL, String paramString)
  {
    assert (checkListsValidity() == true);
    initialize();
    if (paramURL == null)
      return null;
    ClassLoaderInfo localClassLoaderInfo = getUsableClassLoaderInfo(paramString);
    if (localClassLoaderInfo != null)
    {
      zombies.remove(localClassLoaderInfo);
    }
    else
    {
      localClassLoaderInfo = new ClassLoaderInfo(paramURL, paramString);
      addClassLoaderInfo(localClassLoaderInfo);
    }
    assert (checkListsValidity() == true);
    return localClassLoaderInfo;
  }

  public synchronized void addReference()
  {
    this.references += 1;
    Trace.msgPrintln("classloaderinfo.referencing", new Object[] { this, String.valueOf(this.references) }, TraceLevel.BASIC);
  }

  synchronized int removeReference()
  {
    this.references -= 1;
    Trace.msgPrintln("classloaderinfo.releasing", new Object[] { this, String.valueOf(this.references) }, TraceLevel.BASIC);
    if (this.references < 0)
      throw new Error("negative ref count???");
    if (this.references == 0)
      addZombie(this);
    return this.references;
  }

  private static synchronized void addZombie(ClassLoaderInfo paramClassLoaderInfo)
  {
    assert (checkListsValidity() == true);
    AppletPanel.flushClassLoader(paramClassLoaderInfo.key);
    if ((zombieLimit == 0) || (!paramClassLoaderInfo.isCachable))
    {
      removeClassLoaderInfo(paramClassLoaderInfo);
    }
    else
    {
      Trace.msgPrintln("classloaderinfo.caching", new Object[] { paramClassLoaderInfo }, TraceLevel.BASIC);
      AppletClassLoader localAppletClassLoader = (AppletClassLoader)paramClassLoaderInfo.loaderRef.get();
      if ((localAppletClassLoader != null) && (localAppletClassLoader.getExceptionStatus()))
        paramClassLoaderInfo.clearLoaderRef();
      zombies.add(paramClassLoaderInfo);
      cleanupZombies();
      Trace.msgPrintln("classloaderinfo.cachesize", new Object[] { new Integer(zombies.size()) }, TraceLevel.BASIC);
      if (zombies.size() > zombieLimit)
      {
        ClassLoaderInfo localClassLoaderInfo = (ClassLoaderInfo)zombies.get(0);
        Trace.msgPrintln("classloaderinfo.num", new Object[] { String.valueOf(zombieLimit), localClassLoaderInfo }, TraceLevel.BASIC);
        zombies.remove(0);
        removeClassLoaderInfo(localClassLoaderInfo);
        localClassLoaderInfo.clearLoaderRef();
      }
    }
    assert (checkListsValidity() == true);
  }

  public synchronized AppletClassLoader getLoader()
  {
    Object localObject = null;
    if (this.loaderRef != null)
      localObject = (AppletClassLoader)this.loaderRef.get();
    if (localObject == null)
    {
      localObject = (PluginClassLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return new PluginClassLoader(ClassLoaderInfo.this.codebase);
        }
      });
      this.loaderRef = new LoaderReference((ClassLoader)localObject);
      this.jars.clear();
      this.localJarsLoaded = false;
    }
    return localObject;
  }

  AppletClassLoader grabClassLoader()
  {
    AppletClassLoader localAppletClassLoader = null;
    synchronized (this)
    {
      localAppletClassLoader = getLoader();
      addReference();
    }
    localAppletClassLoader.getThreadGroup();
    return localAppletClassLoader;
  }

  private synchronized void clearLoaderRef()
  {
    if (this.loaderRef != null)
    {
      this.loaderRef.clear();
      this.loaderRef = null;
    }
  }

  private static synchronized void cleanupZombies()
  {
    for (LoaderReference localLoaderReference = (LoaderReference)refQueue.poll(); localLoaderReference != null; localLoaderReference = (LoaderReference)refQueue.poll())
    {
      String str = localLoaderReference.getKey();
      ArrayList localArrayList1 = (ArrayList)infos.get(str);
      if (localArrayList1 != null)
      {
        ArrayList localArrayList2 = (ArrayList)localArrayList1.clone();
        Iterator localIterator = localArrayList2.iterator();
        while (localIterator.hasNext())
        {
          ClassLoaderInfo localClassLoaderInfo = (ClassLoaderInfo)localIterator.next();
          if ((localClassLoaderInfo.loaderRef == localLoaderReference) && (zombies.contains(localClassLoaderInfo)))
          {
            localArrayList1.remove(localClassLoaderInfo);
            zombies.remove(localClassLoaderInfo);
          }
        }
      }
    }
  }

  private ClassLoaderInfo(URL paramURL, String paramString)
  {
    this.codebase = paramURL;
    this.key = paramString;
    this.jars = new HashMap();
  }

  synchronized void addJar(String paramString)
  {
    this.jars.put(paramString, paramString);
  }

  synchronized boolean hasJar(String paramString)
  {
    return this.jars.get(paramString) != null;
  }

  public boolean getLocalJarsLoaded()
  {
    return this.localJarsLoaded;
  }

  public void setLocalJarsLoaded(boolean paramBoolean)
  {
    this.localJarsLoaded = paramBoolean;
  }

  public final synchronized void lock()
    throws InterruptedException
  {
    while (this.locked)
      wait();
    this.locked = true;
  }

  public final synchronized void unlock()
  {
    this.locked = false;
    notifyAll();
  }

  public static synchronized boolean checkListsValidity()
  {
    ClassLoaderInfo localClassLoaderInfo = null;
    Collection localCollection = infos.values();
    Iterator localIterator1 = localCollection.iterator();
    while (localIterator1.hasNext())
    {
      ArrayList localArrayList = (ArrayList)localIterator1.next();
      if (localArrayList != null)
      {
        int i = 0;
        Iterator localIterator2 = localArrayList.iterator();
        while (localIterator2.hasNext())
        {
          localClassLoaderInfo = (ClassLoaderInfo)localIterator2.next();
          if ((localClassLoaderInfo != null) && (localClassLoaderInfo.isCachable == true))
            i++;
        }
        if (i > 1)
          return false;
      }
    }
    localIterator1 = zombies.iterator();
    while (localIterator1.hasNext())
    {
      localClassLoaderInfo = (ClassLoaderInfo)localIterator1.next();
      if ((localClassLoaderInfo != null) && (!localClassLoaderInfo.isCachable))
        return false;
    }
    return true;
  }

  private class LoaderReference extends SoftReference
  {
    public LoaderReference(ClassLoader arg2)
    {
      super(ClassLoaderInfo.refQueue);
    }

    public String getCodebase()
    {
      return ClassLoaderInfo.this.codebase.toString();
    }

    public String getKey()
    {
      return ClassLoaderInfo.this.key;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.ClassLoaderInfo
 * JD-Core Version:    0.6.2
 */