package sun.plugin.javascript;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;

public final class JSClassLoader extends SecureClassLoader
{
  private static final String JS_PROXY_PKG = "sun.plugin.javascript.";
  private static final String TRAMPOLINE = "sun.plugin.javascript.Trampoline";
  private static Method bounce = bounce();
  private static Method bounceForNewInstance = bounceForNewInstance();

  public static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException
  {
    try
    {
      return bounce.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getCause();
      if ((localThrowable instanceof InvocationTargetException))
        throw ((InvocationTargetException)localThrowable);
      if ((localThrowable instanceof IllegalAccessException))
        throw ((IllegalAccessException)localThrowable);
      throw new Error("Unexpected invocation error", localThrowable);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Unexpected invocation error", localIllegalAccessException);
    }
  }

  public static Object newInstance(Constructor paramConstructor, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException, InstantiationException
  {
    try
    {
      return bounceForNewInstance.invoke(null, new Object[] { paramConstructor, paramArrayOfObject });
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Throwable localThrowable = localInvocationTargetException.getCause();
      if ((localThrowable instanceof InvocationTargetException))
        throw ((InvocationTargetException)localThrowable);
      if ((localThrowable instanceof IllegalAccessException))
        throw ((IllegalAccessException)localThrowable);
      if ((localThrowable instanceof InstantiationException))
        throw ((InstantiationException)localThrowable);
      throw new Error("Unexpected invocation error", localThrowable);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Unexpected invocation error", localIllegalAccessException);
    }
  }

  public static void checkPackageAccess(Class paramClass)
  {
    String str1 = paramClass.getName();
    int i = str1.lastIndexOf(".");
    if (i != -1)
    {
      String str2 = str1.substring(0, i);
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
        localSecurityManager.checkPackageAccess(str2);
    }
  }

  public static boolean isPackageAccessible(Class paramClass)
  {
    try
    {
      checkPackageAccess(paramClass);
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    return true;
  }

  private static Method bounce()
  {
    if (bounce == null)
      try
      {
        bounce = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws Exception
          {
            Class localClass = JSClassLoader.access$000();
            Class[] arrayOfClass = { Method.class, Object.class, new Object[0].getClass() };
            Method localMethod = localClass.getDeclaredMethod("invoke", arrayOfClass);
            localMethod.setAccessible(true);
            return localMethod;
          }
        });
      }
      catch (Exception localException)
      {
        throw new InternalError("No trampoline to bounce on");
      }
    return bounce;
  }

  private static Method bounceForNewInstance()
  {
    if (bounceForNewInstance == null)
      try
      {
        bounceForNewInstance = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws Exception
          {
            Class localClass = JSClassLoader.access$000();
            Class[] arrayOfClass = { Constructor.class, new Object[0].getClass() };
            Method localMethod = localClass.getDeclaredMethod("newInstance", arrayOfClass);
            localMethod.setAccessible(true);
            return localMethod;
          }
        });
      }
      catch (Exception localException)
      {
        throw new InternalError("No trampoline to bounce on");
      }
    return bounceForNewInstance;
  }

  private static Class getTrampolineClass()
  {
    try
    {
      return Class.forName("sun.plugin.javascript.Trampoline", true, new JSClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
    }
    return null;
  }

  protected synchronized Class loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject = paramString.replace('/', '.');
      if (((String)localObject).startsWith("["))
      {
        i = ((String)localObject).lastIndexOf('[') + 2;
        if ((i > 1) && (i < ((String)localObject).length()))
          localObject = ((String)localObject).substring(i);
      }
      int i = ((String)localObject).lastIndexOf('.');
      if (i != -1)
        localSecurityManager.checkPackageAccess(((String)localObject).substring(0, i));
    }
    Object localObject = findLoadedClass(paramString);
    if (localObject == null)
    {
      try
      {
        localObject = findClass(paramString);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
      }
      if (localObject == null)
        localObject = getParent().loadClass(paramString);
    }
    if (paramBoolean)
      resolveClass((Class)localObject);
    return localObject;
  }

  protected Class findClass(String paramString)
    throws ClassNotFoundException
  {
    if (!paramString.startsWith("sun.plugin.javascript."))
      throw new ClassNotFoundException(paramString);
    String str = paramString.replace('.', '/').concat(".class");
    URL localURL = getResource(str);
    if (localURL != null)
      try
      {
        return defineClass(paramString, localURL);
      }
      catch (IOException localIOException)
      {
        throw new ClassNotFoundException(paramString, localIOException);
      }
    throw new ClassNotFoundException(paramString);
  }

  private Class defineClass(String paramString, URL paramURL)
    throws IOException
  {
    byte[] arrayOfByte = getBytes(paramURL);
    CodeSource localCodeSource = new CodeSource(null, (Certificate[])null);
    if (!paramString.equals("sun.plugin.javascript.Trampoline"))
      throw new IOException("JSClassLoader: bad name " + paramString);
    return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, localCodeSource);
  }

  private static byte[] getBytes(URL paramURL)
    throws IOException
  {
    URLConnection localURLConnection = paramURL.openConnection();
    if ((localURLConnection instanceof HttpURLConnection))
    {
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURLConnection;
      int j = localHttpURLConnection.getResponseCode();
      if (j >= 400)
        throw new IOException("open HTTP connection failed.");
    }
    int i = localURLConnection.getContentLength();
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localURLConnection.getInputStream());
    Object localObject1;
    try
    {
      if (i != -1)
      {
        localObject1 = new byte[i];
        while (i > 0)
        {
          k = localBufferedInputStream.read((byte[])localObject1, localObject1.length - i, i);
          if (k == -1)
            throw new IOException("unexpected EOF");
          i -= k;
        }
      }
      localObject1 = new byte[8192];
      int k = 0;
      byte[] arrayOfByte;
      while ((i = localBufferedInputStream.read((byte[])localObject1, k, localObject1.length - k)) != -1)
      {
        k += i;
        if (k >= localObject1.length)
        {
          arrayOfByte = new byte[k * 2];
          System.arraycopy(localObject1, 0, arrayOfByte, 0, k);
          localObject1 = arrayOfByte;
        }
      }
      if (k != localObject1.length)
      {
        arrayOfByte = new byte[k];
        System.arraycopy(localObject1, 0, arrayOfByte, 0, k);
        localObject1 = arrayOfByte;
      }
    }
    finally
    {
      localBufferedInputStream.close();
    }
    return localObject1;
  }

  protected PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    PermissionCollection localPermissionCollection = super.getPermissions(paramCodeSource);
    localPermissionCollection.add(new AllPermission());
    return localPermissionCollection;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.JSClassLoader
 * JD-Core Version:    0.6.2
 */