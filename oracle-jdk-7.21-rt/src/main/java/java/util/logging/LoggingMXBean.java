package java.util.logging;

import java.util.List;

public abstract interface LoggingMXBean
{
  public abstract List<String> getLoggerNames();

  public abstract String getLoggerLevel(String paramString);

  public abstract void setLoggerLevel(String paramString1, String paramString2);

  public abstract String getParentLoggerName(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.logging.LoggingMXBean
 * JD-Core Version:    0.6.2
 */