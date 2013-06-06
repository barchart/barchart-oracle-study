package sun.plugin.util;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import sun.net.ProgressEvent;
import sun.net.ProgressListener;
import sun.net.ProgressSource;
import sun.net.ProgressSource.State;

public class ProgressMonitor extends sun.net.ProgressMonitor
{
  private final ArrayList progressSourceList = new ArrayList();
  private final HashMap threadGroupListenerMap = new HashMap();
  private static volatile Method progressSourceGetProgressMethod;
  private static volatile Method progressSourceGetExpectedMethod;
  private static volatile Method progressEventGetProgressMethod;
  private static volatile Method progressEventGetExpectedMethod;
  private static volatile Constructor progressEventCtor;
  private static volatile boolean usingLongs;
  private static ProgressMonitor instance = new ProgressMonitor();
  private static boolean isInstalled = false;

  public ArrayList getProgressSources()
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      synchronized (this.progressSourceList)
      {
        Iterator localIterator = this.progressSourceList.iterator();
        while (localIterator.hasNext())
        {
          ProgressSource localProgressSource = (ProgressSource)localIterator.next();
          localArrayList.add((ProgressSource)localProgressSource.clone());
        }
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return localArrayList;
  }

  public int getProgressUpdateThreshold()
  {
    return 65536;
  }

  public boolean shouldMeterInput(URL paramURL, String paramString)
  {
    Thread localThread = Thread.currentThread();
    ThreadGroup localThreadGroup = localThread.getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener == null)
      return false;
    ??? = paramURL.getProtocol();
    return ((((String)???).equalsIgnoreCase("http")) || (((String)???).equalsIgnoreCase("https")) || (((String)???).equalsIgnoreCase("file"))) && (paramString.equalsIgnoreCase("GET"));
  }

  public void registerSource(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (this.progressSourceList.contains(paramProgressSource))
        return;
      this.progressSourceList.add(paramProgressSource);
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = newProgressEvent(paramProgressSource);
      localProgressListener.progressStart((ProgressEvent)???);
    }
  }

  public void unregisterSource(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (!this.progressSourceList.contains(paramProgressSource))
        return;
      paramProgressSource.close();
      this.progressSourceList.remove(paramProgressSource);
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = newProgressEvent(paramProgressSource);
      localProgressListener.progressFinish((ProgressEvent)???);
    }
  }

  public void updateProgress(ProgressSource paramProgressSource)
  {
    synchronized (this.progressSourceList)
    {
      if (!this.progressSourceList.contains(paramProgressSource))
        return;
    }
    ??? = Thread.currentThread();
    ThreadGroup localThreadGroup = ((Thread)???).getThreadGroup();
    ProgressListener localProgressListener = null;
    synchronized (this.threadGroupListenerMap)
    {
      localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(localThreadGroup.hashCode()));
    }
    if (localProgressListener != null)
    {
      ??? = newProgressEvent(paramProgressSource);
      localProgressListener.progressUpdate((ProgressEvent)???);
    }
  }

  public void addProgressListener(ThreadGroup paramThreadGroup, ProgressListener paramProgressListener)
  {
    Trace.msgPrintln("progress.listener.added", new Object[] { paramProgressListener }, TraceLevel.BASIC);
    install();
    Integer localInteger = paramThreadGroup != null ? new Integer(paramThreadGroup.hashCode()) : new Integer(0);
    synchronized (this.threadGroupListenerMap)
    {
      ProgressListener localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(localInteger);
      localProgressListener = EventMulticaster.add(localProgressListener, paramProgressListener);
      this.threadGroupListenerMap.put(localInteger, localProgressListener);
    }
  }

  public void removeProgressListener(ThreadGroup paramThreadGroup, ProgressListener paramProgressListener)
  {
    Trace.msgPrintln("progress.listener.removed", new Object[] { paramProgressListener }, TraceLevel.BASIC);
    synchronized (this.threadGroupListenerMap)
    {
      ProgressListener localProgressListener = (ProgressListener)this.threadGroupListenerMap.get(new Integer(paramThreadGroup.hashCode()));
      localProgressListener = EventMulticaster.remove(localProgressListener, paramProgressListener);
      if (localProgressListener != null)
        this.threadGroupListenerMap.put(new Integer(paramThreadGroup.hashCode()), localProgressListener);
      else
        this.threadGroupListenerMap.remove(new Integer(paramThreadGroup.hashCode()));
    }
  }

  public static long getProgress(ProgressEvent paramProgressEvent)
  {
    if (progressEventGetProgressMethod == null)
      progressEventGetProgressMethod = getProgressEventMethod("getProgress");
    try
    {
      return ((Number)progressEventGetProgressMethod.invoke(paramProgressEvent, null)).longValue();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  public static long getExpected(ProgressEvent paramProgressEvent)
  {
    if (progressEventGetExpectedMethod == null)
      progressEventGetExpectedMethod = getProgressEventMethod("getExpected");
    try
    {
      return ((Number)progressEventGetExpectedMethod.invoke(paramProgressEvent, null)).longValue();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private static Method getProgressSourceMethod(String paramString)
  {
    return (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$method;

      public Object run()
      {
        try
        {
          Method localMethod = ProgressSource.class.getDeclaredMethod(this.val$method, null);
          localMethod.setAccessible(true);
          return localMethod;
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
      }
    });
  }

  private static long getProgress(ProgressSource paramProgressSource)
  {
    if (progressSourceGetProgressMethod == null)
      progressSourceGetProgressMethod = getProgressSourceMethod("getProgress");
    try
    {
      return ((Number)progressSourceGetProgressMethod.invoke(paramProgressSource, null)).longValue();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private static long getExpected(ProgressSource paramProgressSource)
  {
    if (progressSourceGetExpectedMethod == null)
      progressSourceGetExpectedMethod = getProgressSourceMethod("getExpected");
    try
    {
      return ((Number)progressSourceGetExpectedMethod.invoke(paramProgressSource, null)).longValue();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private static Method getProgressEventMethod(String paramString)
  {
    return (Method)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$method;

      public Object run()
      {
        try
        {
          Method localMethod = ProgressEvent.class.getDeclaredMethod(this.val$method, null);
          localMethod.setAccessible(true);
          return localMethod;
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
      }
    });
  }

  private static Constructor getProgressEventConstructor(boolean paramBoolean)
  {
    try
    {
      return class$sun$net$ProgressEvent.getDeclaredConstructor(new Class[] { ProgressSource.class, URL.class, String.class, String.class, ProgressSource.State.class, paramBoolean ? Long.TYPE : Integer.TYPE, paramBoolean ? Long.TYPE : Integer.TYPE });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
    }
    return null;
  }

  private static ProgressEvent newProgressEvent(ProgressSource paramProgressSource)
  {
    if (progressEventCtor == null)
      progressEventCtor = (Constructor)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            Constructor localConstructor = ProgressMonitor.getProgressEventConstructor(true);
            if (localConstructor != null)
              ProgressMonitor.access$102(true);
            else
              localConstructor = ProgressMonitor.getProgressEventConstructor(false);
            localConstructor.setAccessible(true);
            return localConstructor;
          }
          catch (Exception localException)
          {
            throw new RuntimeException(localException);
          }
        }
      });
    long l1 = getProgress(paramProgressSource);
    long l2 = getExpected(paramProgressSource);
    Object localObject1 = null;
    Object localObject2 = null;
    if (usingLongs)
    {
      localObject1 = new Long(l1);
      localObject2 = new Long(l2);
    }
    else
    {
      localObject1 = new Integer((int)l1);
      localObject2 = new Integer((int)l2);
    }
    try
    {
      return (ProgressEvent)progressEventCtor.newInstance(new Object[] { paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), localObject1, localObject2 });
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  public static void warmup()
  {
    install();
  }

  private static synchronized void install()
  {
    Trace.println("Installing progress monitor " + isInstalled, TraceLevel.PRELOADER);
    try
    {
      if (!isInstalled)
        sun.net.ProgressMonitor.setDefault(instance);
    }
    catch (Throwable localThrowable)
    {
      Trace.ignored(localThrowable);
    }
    isInstalled = true;
  }

  public static ProgressMonitor get()
  {
    return instance;
  }

  static ProgressEvent unusedCreateProgressEvent(ProgressSource paramProgressSource)
  {
    return new ProgressEvent(paramProgressSource, paramProgressSource.getURL(), paramProgressSource.getMethod(), paramProgressSource.getContentType(), paramProgressSource.getState(), paramProgressSource.getProgress(), paramProgressSource.getExpected());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.util.ProgressMonitor
 * JD-Core Version:    0.6.2
 */