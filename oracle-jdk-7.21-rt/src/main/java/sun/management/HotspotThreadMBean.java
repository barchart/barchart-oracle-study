package sun.management;

import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

public abstract interface HotspotThreadMBean
{
  public abstract int getInternalThreadCount();

  public abstract Map<String, Long> getInternalThreadCpuTimes();

  public abstract List<Counter> getInternalThreadingCounters();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.HotspotThreadMBean
 * JD-Core Version:    0.6.2
 */