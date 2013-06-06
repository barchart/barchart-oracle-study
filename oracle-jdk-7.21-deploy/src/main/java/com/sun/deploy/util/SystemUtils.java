package com.sun.deploy.util;

import com.sun.deploy.config.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class SystemUtils
{
  public static final long microTime()
  {
    if (Config.isJavaVersionAtLeast15())
      return System.nanoTime() / 1000L;
    return System.currentTimeMillis() * 1000L;
  }

  public static String getJarPath(Class paramClass)
  {
    String str1 = "/" + paramClass.getName().replace('.', '/') + ".class";
    URL localURL1 = Object.class.getResource(str1);
    if (localURL1 == null)
      return null;
    String str2 = null;
    if (localURL1.getProtocol().equals("jar"))
    {
      try
      {
        URL localURL2 = new URL(localURL1.getFile());
        int j = localURL2.getFile().lastIndexOf("!");
        if (j != -1)
          str2 = localURL2.getFile().substring(0, j);
        else
          str2 = localURL2.getFile();
      }
      catch (Exception localException)
      {
        str2 = "SHOULD NEVER HAPPEN";
      }
    }
    else
    {
      int i = localURL1.getPath().length() - str1.length();
      str2 = localURL1.getPath().substring(0, i);
    }
    return new File(URLDecoder.decode(str2)).getPath();
  }

  public static String priviledgedGetSystemProperty(String paramString)
  {
    return (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final String val$key;

      public Object run()
      {
        return System.getProperty(this.val$key);
      }
    });
  }

  public static boolean priviledgedIsDirectory(File paramFile)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      private final File val$f;

      public Object run()
      {
        return Boolean.valueOf(this.val$f.isDirectory());
      }
    });
    return localBoolean.booleanValue();
  }

  public static boolean isPathFromCache(String paramString)
  {
    String str1 = getCanonicalPath(Config.getCacheDirectory());
    String str2 = getCanonicalPath(Config.getSystemCacheDirectory());
    String str3 = getCanonicalPath(paramString);
    if (str3 != null)
      return ((str1 != null) && (str3.startsWith(str1))) || ((str2 != null) && (str3.startsWith(str2)));
    return false;
  }

  private static String getCanonicalPath(String paramString)
  {
    try
    {
      return new File(paramString).getCanonicalPath();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static String getSimpleName(Class paramClass)
  {
    if (paramClass == null)
      return null;
    try
    {
      return paramClass.getSimpleName();
    }
    catch (Throwable localThrowable)
    {
    }
    return paramClass.getName();
  }

  public static int getThreadLocalInt(ThreadLocal paramThreadLocal)
  {
    Integer localInteger = null;
    if ((paramThreadLocal.get() instanceof Integer))
      localInteger = (Integer)paramThreadLocal.get();
    if (localInteger == null)
    {
      localInteger = integerValueOf(0);
      paramThreadLocal.set(localInteger);
    }
    return localInteger.intValue();
  }

  public static void setThreadLocalInt(ThreadLocal paramThreadLocal, int paramInt)
  {
    if (Config.isJavaVersionAtLeast15())
      paramThreadLocal.set(Integer.valueOf(paramInt));
    else
      paramThreadLocal.set(new Integer(paramInt));
  }

  public static Integer integerValueOf(int paramInt)
  {
    if (Config.isJavaVersionAtLeast15())
      return Integer.valueOf(paramInt);
    return new Integer(paramInt);
  }

  public static Long longValueOf(long paramLong)
  {
    if (Config.isJavaVersionAtLeast15())
      return Long.valueOf(paramLong);
    return new Long(paramLong);
  }

  public static boolean deleteRecursive(File paramFile)
    throws FileNotFoundException
  {
    int i = 1;
    if ((paramFile == null) || (!paramFile.exists()))
      return true;
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      for (int j = 0; j < arrayOfFile.length; j++)
        i = (i != 0) && (deleteRecursive(arrayOfFile[j])) ? 1 : 0;
    }
    return (i != 0) && (paramFile.delete());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.SystemUtils
 * JD-Core Version:    0.6.2
 */