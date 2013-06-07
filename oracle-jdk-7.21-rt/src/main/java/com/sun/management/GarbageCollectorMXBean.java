package com.sun.management;

public abstract interface GarbageCollectorMXBean extends java.lang.management.GarbageCollectorMXBean
{
  public abstract GcInfo getLastGcInfo();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.management.GarbageCollectorMXBean
 * JD-Core Version:    0.6.2
 */