package com.sun.deploy.util;

import com.sun.deploy.config.JfxRuntime;
import com.sun.deploy.config.Platform;
import com.sun.deploy.trace.Trace;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.security.Security;
import java.util.List;

public class FXLoader
{
  private static final String POLICY_URL = "policy.url.";
  private static final String JAVAFX_RUNTIME_PROPERTY = "javafx.runtime.home";

  private static void registerJavaFXPolicy(JfxRuntime paramJfxRuntime)
    throws MalformedURLException
  {
    for (int i = 1; Security.getProperty("policy.url." + i) != null; i++);
    Security.setProperty("policy.url." + i, paramJfxRuntime.getSecurityPolicy().toURI().toURL().toString());
    System.setProperty("javafx.runtime.home", paramJfxRuntime.getHome().toString());
    Policy.getPolicy().refresh();
  }

  private static void addURL(URLClassLoader paramURLClassLoader, URL paramURL)
    throws Exception
  {
    Class localClass = URLClassLoader.class;
    Method localMethod = localClass.getDeclaredMethod("addURL", new Class[] { URL.class });
    localMethod.setAccessible(true);
    localMethod.invoke(paramURLClassLoader, new Object[] { paramURL });
  }

  public static void loadFX(JfxRuntime paramJfxRuntime)
    throws ClassNotFoundException
  {
    if (JfxRuntime.useExtensionJFX())
      return;
    if (null == paramJfxRuntime)
      throw new ClassNotFoundException();
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    URLClassLoader localURLClassLoader = null;
    while (localClassLoader != null)
    {
      if ((localClassLoader instanceof URLClassLoader))
        localURLClassLoader = (URLClassLoader)localClassLoader;
      localClassLoader = localClassLoader.getParent();
    }
    URL[] arrayOfURL = paramJfxRuntime.getURLs();
    if (null == arrayOfURL)
      throw new ClassNotFoundException();
    for (int i = 0; i < arrayOfURL.length; i++)
      try
      {
        addURL(localURLClassLoader, arrayOfURL[i]);
      }
      catch (Exception localException2)
      {
        throw new ClassNotFoundException("Failed to add JavaFX Runtime", localException2);
      }
    try
    {
      registerJavaFXPolicy(paramJfxRuntime);
    }
    catch (Exception localException1)
    {
      Trace.ignored(localException1);
      throw new ClassNotFoundException();
    }
  }

  public static void loadFX(String paramString)
    throws ClassNotFoundException
  {
    if (JfxRuntime.useExtensionJFX())
      return;
    if ((null == paramString) || (paramString.length() == 0))
      paramString = "2.0+";
    VersionString localVersionString = new VersionString(paramString);
    List localList = localVersionString.getAllVersionIDs();
    for (int i = 0; i < localList.size(); i++)
    {
      VersionID localVersionID = (VersionID)localList.get(i);
      JfxRuntime localJfxRuntime = Platform.get().getBestJfxRuntime(localVersionID);
      if (localJfxRuntime != null)
      {
        loadFX(localJfxRuntime);
        return;
      }
    }
    throw new ClassNotFoundException();
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.util.FXLoader
 * JD-Core Version:    0.6.2
 */