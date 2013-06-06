package com.sun.deploy.config;

import com.sun.deploy.Environment;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.deploy.util.VersionID;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class JfxRuntime
{
  private final String version;
  private final VersionID versionId;
  private final String path;
  private static final String RT_JAR_DIR = useExtensionJFX() ? "lib" + File.separator + "ext" : "lib";
  public static final String RT_JAR = RT_JAR_DIR + File.separator + "jfxrt.jar";

  public static boolean useExtensionJFX()
  {
    if (!Config.isDeployVersionAtLeast11())
      return false;
    String str = Environment.getDeploymentHome() + File.separator + "lib" + File.separator + "ext" + File.separator + "jfxrt.jar";
    File localFile = new File(str);
    return localFile.canRead();
  }

  public static JfxRuntime runtimeForPath(String paramString)
  {
    File localFile = new File(paramString + File.separator + RT_JAR);
    if (localFile.canRead())
      try
      {
        URLClassLoader localURLClassLoader = new URLClassLoader(new URL[] { localFile.toURL() }, null);
        Class localClass = localURLClassLoader.loadClass("com.sun.javafx.runtime.VersionInfo");
        if (localClass != null)
        {
          Field localField = localClass.getDeclaredField("RAW_VERSION");
          if (localField != null)
          {
            localField.setAccessible(true);
            return new JfxRuntime((String)localField.get(null), paramString);
          }
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
    Trace.println("No valid JFX runtime at [" + paramString + "]", TraceLevel.BASIC);
    return null;
  }

  public JfxRuntime(String paramString1, String paramString2)
  {
    this.version = paramString1;
    this.path = (paramString2 + File.separator);
    this.versionId = new VersionID(paramString1);
  }

  public boolean isValid()
  {
    File localFile = new File(this.path + RT_JAR);
    return localFile.canRead();
  }

  public String toString()
  {
    return "JavaFX " + this.version + " found at " + this.path;
  }

  public URL[] getURLs()
  {
    URL[] arrayOfURL = new URL[1];
    File localFile = getRuntimeJar();
    try
    {
      arrayOfURL[0] = localFile.getCanonicalFile().toURI().toURL();
    }
    catch (IOException localIOException)
    {
      Trace.printException(localIOException);
      return null;
    }
    return arrayOfURL;
  }

  private File getRuntimeJar()
  {
    return new File(this.path + RT_JAR);
  }

  public File getHome()
  {
    return new File(this.path);
  }

  public File getSecurityPolicy()
  {
    File localFile = new File(this.path, "lib/security/javafx.policy");
    if (localFile.canRead())
      return localFile;
    return null;
  }

  public VersionID getProductVersion()
  {
    return this.versionId;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.config.JfxRuntime
 * JD-Core Version:    0.6.2
 */