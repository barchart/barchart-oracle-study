package sun.plugin2.util;

import com.sun.deploy.Environment;
import com.sun.deploy.config.Platform;
import java.io.File;
import java.io.PrintStream;

public class NativeLibLoader
{
  private static final boolean DEBUG = SystemUtil.getenv("JPI_PLUGIN2_DEBUG") != null;

  public static synchronized void load(String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
      throw new InternalError("Wrong library name passed to load method");
    String[] arrayOfString1 = SystemUtil.getSystemProperty("sun.boot.class.path").split(File.pathSeparator);
    String[] arrayOfString2 = getPossibleSubdirs();
    String str = Environment.getForcedDeployRoot();
    if (((str == null) || (!tryLoadingFromJRE(paramArrayOfString, arrayOfString2, str + File.separator + "lib" + File.separator + "deploy.jar"))) && (!tryLoadingFromJRE(paramArrayOfString, arrayOfString2, findSuffix(arrayOfString1, "plugin.jar"))) && (!tryLoadingFromJRE(paramArrayOfString, arrayOfString2, findSuffix(arrayOfString1, "deploy.jar"))) && (!tryLoadingFromJRE(paramArrayOfString, arrayOfString2, findSuffix(arrayOfString1, "javaws.jar"))))
      throw new InternalError("Unable to find plugin native libraries");
  }

  private static boolean tryLoadingFromJRE(String[] paramArrayOfString1, String[] paramArrayOfString2, String paramString)
  {
    if (paramString == null)
      return false;
    File localFile1 = new File(paramString).getParentFile();
    File localFile2 = localFile1.getParentFile();
    File localFile3 = null;
    if (localFile2 != null)
      localFile3 = localFile2.getParentFile();
    boolean bool1 = true;
    for (int i = 0; i < paramArrayOfString1.length; i++)
    {
      boolean bool2 = false;
      if ((paramArrayOfString1[i].equalsIgnoreCase("npjp2")) || (paramArrayOfString1[i].equalsIgnoreCase("jp2iexp")))
      {
        try
        {
          String str = Platform.get().getLoadedNativeLibPath(paramArrayOfString1[i]);
          if (str != null)
          {
            if (DEBUG)
              System.err.println("NativeLibLoader: resolving loaded " + str);
            File localFile4 = new File(str);
            if ((localFile4.exists()) && (localFile4.canRead()))
            {
              System.load(localFile4.getAbsolutePath());
              if (DEBUG)
                System.err.println("   (Succeeded)");
              continue;
            }
          }
        }
        catch (Throwable localThrowable)
        {
          if (DEBUG)
            localThrowable.printStackTrace();
        }
        if (((SystemUtil.getenv("FORCED_DEPLOY_ROOT") != null) || (SystemUtil.getenv("FORCED_JRE_ROOT") != null)) && (SystemUtil.getenv("FORCED_OVERRIDE_BROWSERLIB") == null))
        {
          localFile1 = new File(System.getProperty("java.home"));
          for (j = 0; (j < paramArrayOfString2.length) && (!bool2); j++)
            bool2 = tryLoading(paramArrayOfString1[i], localFile1, paramArrayOfString2[j]);
          if (bool2)
            continue;
          bool1 = false;
          continue;
        }
      }
      for (int j = 0; (j < paramArrayOfString2.length) && (!bool2); j++)
      {
        bool2 = tryLoading(paramArrayOfString1[i], localFile1, paramArrayOfString2[j]);
        if (!bool2)
          bool2 = tryLoading(paramArrayOfString1[i], localFile2, paramArrayOfString2[j]);
        if (!bool2)
          bool2 = tryLoading(paramArrayOfString1[i], localFile3, paramArrayOfString2[j]);
      }
      if (!bool2)
        bool1 = false;
    }
    return bool1;
  }

  private static String[] getPossibleSubdirs()
  {
    String str1 = SystemUtil.getSystemProperty("sun.boot.library.path");
    String str2 = SystemUtil.getJavaHome();
    if (!str1.startsWith(str2))
      throw new InternalError("sun.boot.library.path (\"" + str1 + "\") did not start with java.home (\"" + str2 + "\")");
    String str3 = str1.substring(str2.length());
    if (str3.startsWith(File.separator))
      str3 = str3.substring(1);
    String str4 = "jre" + File.separator + str3;
    String str5 = str3 + File.separator + "plugin2";
    String str6 = str4 + File.separator + "plugin2";
    return new String[] { str3, str4, str5, str6 };
  }

  private static boolean tryLoading(String paramString1, File paramFile, String paramString2)
  {
    if (paramFile == null)
      return false;
    File localFile1 = paramFile;
    if (paramString2 != null)
      localFile1 = new File(paramFile, paramString2);
    File localFile2 = new File(localFile1, System.mapLibraryName(paramString1));
    if (!localFile2.exists())
    {
      if (DEBUG)
        System.err.println("NativeLibLoader: " + localFile2.getAbsolutePath() + " doesn't exist");
      return false;
    }
    if (DEBUG)
      System.err.println("NativeLibLoader: trying to load " + localFile2.getAbsolutePath());
    System.load(localFile2.getAbsolutePath());
    if (DEBUG)
      System.err.println("  (Succeeded)");
    return true;
  }

  private static String findSuffix(String[] paramArrayOfString, String paramString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
      if (paramArrayOfString[i].endsWith(paramString))
        return paramArrayOfString[i];
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.util.NativeLibLoader
 * JD-Core Version:    0.6.2
 */