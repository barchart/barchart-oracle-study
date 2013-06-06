package sun.plugin.liveconnect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import sun.plugin.javascript.JSClassLoader;

public class ReplaceMethod
{
  static Method getJScriptMethod(Method paramMethod)
  {
    Class localClass = paramMethod.getDeclaringClass();
    if (Modifier.isPublic(localClass.getModifiers()))
      return paramMethod;
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Method localMethod = null;
    while ((localClass != null) && (localMethod == null))
    {
      localMethod = getPublicMethod(localClass, str, arrayOfClass);
      if (localMethod == null)
        localMethod = getJScriptInterfaceMethod(localClass, str, arrayOfClass);
      localClass = localClass.getSuperclass();
    }
    return localMethod;
  }

  static Method getJScriptInterfaceMethod(Class paramClass, String paramString, Class[] paramArrayOfClass)
  {
    Method localMethod = null;
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; (i < arrayOfClass.length) && (localMethod == null); i++)
    {
      Class localClass = arrayOfClass[i];
      localMethod = getPublicMethod(localClass, paramString, paramArrayOfClass);
      if (localMethod == null)
        localMethod = getJScriptInterfaceMethod(localClass, paramString, paramArrayOfClass);
    }
    return localMethod;
  }

  private static Method getPublicMethod(Class paramClass, String paramString, Class[] paramArrayOfClass)
  {
    try
    {
      if (!Modifier.isPublic(paramClass.getModifiers()))
        return null;
      if (!JSClassLoader.isPackageAccessible(paramClass))
        return null;
      return paramClass.getMethod(paramString, paramArrayOfClass);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    catch (SecurityException localSecurityException)
    {
    }
    return null;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.ReplaceMethod
 * JD-Core Version:    0.6.2
 */