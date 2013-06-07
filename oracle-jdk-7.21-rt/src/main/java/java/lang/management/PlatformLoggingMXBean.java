package java.lang.management;

import java.util.List;

public abstract interface PlatformLoggingMXBean extends PlatformManagedObject
{
  public abstract List<String> getLoggerNames();

  public abstract String getLoggerLevel(String paramString);

  public abstract void setLoggerLevel(String paramString1, String paramString2);

  public abstract String getParentLoggerName(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.management.PlatformLoggingMXBean
 * JD-Core Version:    0.6.2
 */