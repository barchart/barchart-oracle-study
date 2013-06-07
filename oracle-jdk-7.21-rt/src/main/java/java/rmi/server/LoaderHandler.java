package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

@Deprecated
public abstract interface LoaderHandler
{
  public static final String packagePrefix = "sun.rmi.server";

  @Deprecated
  public abstract Class<?> loadClass(String paramString)
    throws MalformedURLException, ClassNotFoundException;

  @Deprecated
  public abstract Class<?> loadClass(URL paramURL, String paramString)
    throws MalformedURLException, ClassNotFoundException;

  @Deprecated
  public abstract Object getSecurityContext(ClassLoader paramClassLoader);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.server.LoaderHandler
 * JD-Core Version:    0.6.2
 */