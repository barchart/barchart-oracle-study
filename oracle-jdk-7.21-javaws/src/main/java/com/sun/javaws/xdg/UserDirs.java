package com.sun.javaws.xdg;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.xdg.BaseDir;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

final class UserDirs
{
  private static final String XDG_DESKTOP_DIR = "XDG_DESKTOP_DIR";
  private static UserDirs instance;

  public static synchronized UserDirs getInstance()
  {
    if (instance == null)
      instance = new UserDirs();
    return instance;
  }

  public String getDesktopDir()
  {
    String str = getEnvVarFromDirsConfig("XDG_DESKTOP_DIR");
    if (str == null)
      str = System.getProperty("user.home") + File.separatorChar + "Desktop";
    return str;
  }

  private String getEnvVarFromDirsConfig(String paramString)
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    String str1 = localBaseDir.getUserConfigDir();
    File localFile = new File(str1, "user-dirs.dirs");
    String str2 = null;
    if (localFile.exists())
      try
      {
        Map localMap = parseDirsConfig(localFile);
        return (String)localMap.get(paramString);
      }
      catch (IOException localIOException)
      {
        Trace.ignored(localIOException);
      }
    return str2;
  }

  private Map parseDirsConfig(File paramFile)
    throws IOException
  {
    HashMap localHashMap = new HashMap();
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    InputStreamReader localInputStreamReader = new InputStreamReader(localFileInputStream);
    BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
    for (String str1 = localBufferedReader.readLine(); str1 != null; str1 = localBufferedReader.readLine())
      if (!str1.startsWith("#"))
      {
        int i = str1.indexOf('=');
        if (i != -1)
        {
          String str2 = str1.substring(0, i).trim();
          String str3 = str1.substring(i + 1, str1.length()).trim();
          if (str3.charAt(0) == '"')
            str3 = str3.substring(1);
          int j = str3.length();
          if (str3.charAt(j - 1) == '"')
            str3 = str3.substring(0, j - 1);
          str3 = str3.replace("$HOME", System.getProperty("user.home"));
          Trace.println("Adding env var: " + str2 + "=" + str3, TraceLevel.UI);
          localHashMap.put(str2, str3);
        }
      }
    return localHashMap;
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.xdg.UserDirs
 * JD-Core Version:    0.6.2
 */