package sun.plugin2.main.server;

import com.sun.deploy.config.Config;
import com.sun.deploy.util.SystemUtils;
import java.io.PrintStream;
import sun.plugin2.applet.Applet2ThreadGroup;
import sun.plugin2.applet.Plugin2Manager;
import sun.plugin2.util.SystemUtil;

public class JVMHealthData
{
  static final long HEALTHY_AVAILABLE_HEAP_SIZE = 33554432L;
  static final int MAX_APPLET_THREAD_COUNT = Config.getMaxAppletThreadCount();
  private static final boolean DEBUG = SystemUtil.isDebug();
  private static final boolean NO_HEARTBEAT = SystemUtil.getenv("JPI_PLUGIN2_NO_HEARTBEAT") != null;
  private long maxHeapSize = -1L;
  private long heapSize = -1L;
  private long freeHeapSize = -1L;
  private int appletThreadCount = -1;
  private long launchMicroSeconds = -1L;

  public JVMHealthData()
  {
  }

  public JVMHealthData(long paramLong1, long paramLong2, long paramLong3, int paramInt)
  {
    this.maxHeapSize = paramLong1;
    this.heapSize = paramLong2;
    this.freeHeapSize = paramLong3;
    this.appletThreadCount = paramInt;
  }

  public static JVMHealthData getCurrent()
  {
    long l1 = Runtime.getRuntime().maxMemory();
    long l2 = Runtime.getRuntime().totalMemory();
    long l3 = Runtime.getRuntime().freeMemory();
    int i = Applet2ThreadGroup.getAppletThreadCount();
    JVMHealthData localJVMHealthData = new JVMHealthData(l1, l2, l3, i);
    long l4 = Plugin2Manager.getJVMLaunchTime();
    if (l4 <= 0L)
      l4 = SystemUtils.microTime();
    localJVMHealthData.setLaunchMicroSeconds(l4);
    return localJVMHealthData;
  }

  public long getFreeHeapSize()
  {
    return this.freeHeapSize;
  }

  public long getHeapSize()
  {
    return this.heapSize;
  }

  public long getMaxHeapSize()
  {
    return this.maxHeapSize;
  }

  public long getUsedHeapSize()
  {
    if (!validateMemoryData())
      return -1L;
    return this.heapSize - this.freeHeapSize;
  }

  public long getAvailableHeapSize()
  {
    if (!validateMemoryData())
      return -1L;
    return this.maxHeapSize - getUsedHeapSize();
  }

  public int getAppletThreadCount()
  {
    return this.appletThreadCount;
  }

  private boolean validateMemoryData()
  {
    return (this.maxHeapSize >= 0L) && (this.heapSize >= 0L) && (this.freeHeapSize >= 0L);
  }

  public synchronized void updateFrom(JVMHealthData paramJVMHealthData)
  {
    this.maxHeapSize = paramJVMHealthData.getMaxHeapSize();
    this.heapSize = paramJVMHealthData.getHeapSize();
    this.freeHeapSize = paramJVMHealthData.getFreeHeapSize();
    this.appletThreadCount = paramJVMHealthData.getAppletThreadCount();
  }

  public synchronized boolean isHealthy()
  {
    if (NO_HEARTBEAT)
      return true;
    if (!beenLaunched())
      return false;
    if ((getMaxHeapSize() < 0L) && (currentAgeSeconds() < 5L))
      return true;
    return (getMaxHeapSize() > 0L) && (getAvailableHeapSize() >= 33554432L) && (getAppletThreadCount() < MAX_APPLET_THREAD_COUNT);
  }

  void setLaunchMicroSeconds(long paramLong)
  {
    this.launchMicroSeconds = paramLong;
  }

  private boolean beenLaunched()
  {
    return this.launchMicroSeconds > 0L;
  }

  public long currentAgeSeconds()
  {
    if (this.launchMicroSeconds == 0L)
      return 0L;
    return (SystemUtils.microTime() - this.launchMicroSeconds) / 1000000L;
  }

  public String toString()
  {
    return "{ healthy: " + isHealthy() + ", ageSeconds: " + currentAgeSeconds() + ", availableHeapKB: " + getAvailableHeapSize() / 1024L + ", appletThreads: " + getAppletThreadCount() + " }";
  }

  static
  {
    if ((DEBUG) && (NO_HEARTBEAT))
      System.out.println("JPI_PLUGIN2_NO_HEARTBEAT is set, all health data are assumed normal.");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.server.JVMHealthData
 * JD-Core Version:    0.6.2
 */