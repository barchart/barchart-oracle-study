package com.sun.org.glassfish.external.probe.provider;

public abstract interface StatsProviderManagerDelegate
{
  public abstract void register(StatsProviderInfo paramStatsProviderInfo);

  public abstract void unregister(Object paramObject);

  public abstract boolean hasListeners(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.glassfish.external.probe.provider.StatsProviderManagerDelegate
 * JD-Core Version:    0.6.2
 */