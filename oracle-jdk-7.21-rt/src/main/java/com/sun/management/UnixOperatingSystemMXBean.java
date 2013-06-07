package com.sun.management;

public abstract interface UnixOperatingSystemMXBean extends OperatingSystemMXBean
{
  public abstract long getOpenFileDescriptorCount();

  public abstract long getMaxFileDescriptorCount();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.management.UnixOperatingSystemMXBean
 * JD-Core Version:    0.6.2
 */