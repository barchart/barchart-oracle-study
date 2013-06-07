package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public abstract interface HotspotMemoryMBean
{
  public abstract List<Counter> getInternalMemoryCounters();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.management.HotspotMemoryMBean
 * JD-Core Version:    0.6.2
 */