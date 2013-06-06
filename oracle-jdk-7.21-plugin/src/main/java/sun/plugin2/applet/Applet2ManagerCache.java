package sun.plugin2.applet;

import com.sun.deploy.trace.Trace;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Applet2ManagerCache
{
  private int cacheSize = Integer.getInteger("javaplugin.lifecycle.cachesize", 4).intValue();
  private List entries = new LinkedList();
  private Timer memoryPressureTimer = new Timer();
  private TimerTask currentTask = null;

  public Applet2Manager get(String paramString, Map paramMap)
  {
    if (paramMap.get("legacy_lifecycle") == null)
      return null;
    String str = getCacheKey(paramString, paramMap);
    synchronized (this)
    {
      Iterator localIterator = this.entries.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry.getKey().equals(str))
        {
          localIterator.remove();
          Trace.msgPrintln("lifecycle.applet.found");
          return localEntry.getManager();
        }
      }
    }
    return null;
  }

  public void put(Applet2Manager paramApplet2Manager)
  {
    String str = paramApplet2Manager.getLegacyLifeCycleCacheKey();
    ArrayList localArrayList = new ArrayList();
    Entry localEntry1 = new Entry(str, paramApplet2Manager);
    synchronized (this)
    {
      Trace.msgPrintln("lifecycle.applet.support");
      this.entries.add(0, localEntry1);
      while (this.entries.size() > this.cacheSize)
      {
        Entry localEntry2 = (Entry)this.entries.remove(this.entries.size() - 1);
        localArrayList.add(localEntry2);
      }
    }
    if (localArrayList.size() > 0)
    {
      Trace.msgPrintln("lifecycle.applet.cachefull");
      destroy(localArrayList);
    }
    startWatchdog();
  }

  public boolean isEmpty()
  {
    synchronized (this)
    {
      return this.entries.isEmpty();
    }
  }

  public void clear()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this)
    {
      localArrayList.addAll(this.entries);
      this.entries.clear();
    }
    destroy(localArrayList);
    stopWatchdog();
  }

  public String getCacheKey(String paramString, Map paramMap)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramString != null)
    {
      localStringBuffer.append("<NAME=_documentBase VALUE=");
      localStringBuffer.append(paramString);
      localStringBuffer.append(">");
    }
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)paramMap.get(str1);
      if ((str1 != null) && (str2 != null))
      {
        localStringBuffer.append("<NAME=");
        localStringBuffer.append(str1);
        localStringBuffer.append(" VALUE=");
        localStringBuffer.append(str2);
        localStringBuffer.append(">");
      }
    }
    return localStringBuffer.toString();
  }

  private static void destroy(List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
      ((Entry)localIterator.next()).getManager().destroy();
  }

  private synchronized void startWatchdog()
  {
    if (this.currentTask == null)
    {
      this.currentTask = new WatchdogTask();
      long l = getTimerPeriod(getMemoryPressure());
      this.memoryPressureTimer.schedule(this.currentTask, l, l);
    }
  }

  private synchronized void stopWatchdog()
  {
    if (this.currentTask != null)
    {
      this.currentTask.cancel();
      this.currentTask = null;
    }
  }

  private static float getMemoryPressure()
  {
    Runtime localRuntime = Runtime.getRuntime();
    long l1 = localRuntime.maxMemory();
    if (l1 == 9223372036854775807L)
      return 0.0F;
    long l2 = localRuntime.totalMemory();
    long l3 = l2 - localRuntime.freeMemory();
    float f = (float)(l3 / l1);
    if (f < 0.0F)
      f = 0.0F;
    if (f > 1.0F)
      f = 1.0F;
    return f;
  }

  private static long getTimerPeriod(float paramFloat)
  {
    if (paramFloat < 0.5F)
      return 30000L;
    if (paramFloat < 0.75F)
      return 15000L;
    return 5000L;
  }

  class Entry
  {
    private String key;
    private Applet2Manager manager;

    Entry(String paramApplet2Manager, Applet2Manager arg3)
    {
      this.key = paramApplet2Manager;
      Object localObject;
      this.manager = localObject;
    }

    public String getKey()
    {
      return this.key;
    }

    public Applet2Manager getManager()
    {
      return this.manager;
    }
  }

  class WatchdogTask extends TimerTask
  {
    private long period = Applet2ManagerCache.getTimerPeriod(Applet2ManagerCache.access$000());

    WatchdogTask()
    {
    }

    public void run()
    {
      float f = Applet2ManagerCache.access$000();
      if (f > 0.9F)
        Applet2ManagerCache.this.clear();
      if (Applet2ManagerCache.getTimerPeriod(Applet2ManagerCache.access$000()) != this.period)
      {
        Applet2ManagerCache.this.stopWatchdog();
        Applet2ManagerCache.this.startWatchdog();
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.applet.Applet2ManagerCache
 * JD-Core Version:    0.6.2
 */