package com.sun.deploy.security;

import com.sun.deploy.util.SystemUtils;

public class URLClassPathControl
{
  private static final ThreadLocal level = new ThreadLocal();

  public static boolean isDisabledInCurrentThread()
  {
    return currentLevel() > 0;
  }

  public static void disable()
  {
    int i = currentLevel();
    SystemUtils.setThreadLocalInt(level, ++i);
  }

  public static void enable()
  {
    int i = currentLevel();
    if (i > 0)
      SystemUtils.setThreadLocalInt(level, --i);
  }

  private static int currentLevel()
  {
    return SystemUtils.getThreadLocalInt(level);
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.security.URLClassPathControl
 * JD-Core Version:    0.6.2
 */