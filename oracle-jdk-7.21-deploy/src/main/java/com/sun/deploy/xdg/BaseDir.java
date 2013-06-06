package com.sun.deploy.xdg;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;

public final class BaseDir
{
  private static BaseDir instance;

  public static synchronized BaseDir getInstance()
  {
    if (instance == null)
      instance = new BaseDir();
    return instance;
  }

  public String getUserDataDir()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getenv("XDG_DATA_HOME");
      }
    });
    if (str == null)
      str = (String)AccessController.doPrivileged(new GetPropertyAction("user.home")) + File.separatorChar + ".local" + File.separatorChar + "share";
    if (!checkDir(str))
      str = null;
    return str;
  }

  public String getUserConfigDir()
  {
    return System.getProperty("user.home") + File.separatorChar + ".config";
  }

  public String getSystemDataDir()
  {
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getenv("XDG_DATA_HOME");
      }
    });
    if (str1 == null)
      str1 = File.separatorChar + "usr" + File.separatorChar + "local" + File.separatorChar + "share" + File.pathSeparatorChar + File.separatorChar + "usr" + File.separatorChar + "share";
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, File.pathSeparator);
    ArrayList localArrayList = new ArrayList(localStringTokenizer.countTokens());
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken());
    ListIterator localListIterator = localArrayList.listIterator(localArrayList.size());
    Object localObject = null;
    while ((localObject == null) && (localListIterator.hasPrevious()))
    {
      String str2 = (String)localListIterator.previous();
      if (checkDir(str2))
        localObject = str2;
    }
    return localObject;
  }

  public String[] getSystemDataDirs()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getenv("XDG_DATA_HOME");
      }
    });
    if (str == null)
      str = File.separatorChar + "usr" + File.separatorChar + "local" + File.separatorChar + "share" + File.pathSeparatorChar + File.separatorChar + "usr" + File.separatorChar + "share";
    StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
    ArrayList localArrayList = new ArrayList(localStringTokenizer.countTokens());
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken());
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }

  private boolean checkDir(String paramString)
  {
    File localFile = new File(paramString);
    return (localFile.isDirectory()) && (localFile.canRead()) && (localFile.canWrite());
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.xdg.BaseDir
 * JD-Core Version:    0.6.2
 */