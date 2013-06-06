package sun.plugin.javascript;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReflectUtil
{
  public static Method[] getJScriptMethods(Class paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    HashMap localHashMap = new HashMap();
    while (paramClass != null)
    {
      boolean bool = getPublicMethods(paramClass, localArrayList, localHashMap);
      if (bool)
        break;
      getJScriptInterfaceMethods(paramClass, localArrayList, localHashMap);
      paramClass = paramClass.getSuperclass();
    }
    return (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
  }

  private static void getJScriptInterfaceMethods(Class paramClass, List paramList, Map paramMap)
  {
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      boolean bool = getPublicMethods(localClass, paramList, paramMap);
      if (!bool)
        getJScriptInterfaceMethods(localClass, paramList, paramMap);
    }
  }

  private static boolean getPublicMethods(Class paramClass, List paramList, Map paramMap)
  {
    Method[] arrayOfMethod = null;
    try
    {
      if (!Modifier.isPublic(paramClass.getModifiers()))
        return false;
      if (!JSClassLoader.isPackageAccessible(paramClass))
        return false;
      arrayOfMethod = paramClass.getMethods();
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    boolean bool = true;
    Class localClass;
    for (int i = 0; i < arrayOfMethod.length; i++)
    {
      localClass = arrayOfMethod[i].getDeclaringClass();
      if (!Modifier.isPublic(localClass.getModifiers()))
      {
        bool = false;
        break;
      }
    }
    if ((paramMap.isEmpty()) && (!paramList.isEmpty()))
      initSignatureMap(paramList, paramMap);
    if (bool)
      for (i = 0; i < arrayOfMethod.length; i++)
        addMethod(paramList, paramMap, arrayOfMethod[i]);
    else
      for (i = 0; i < arrayOfMethod.length; i++)
      {
        localClass = arrayOfMethod[i].getDeclaringClass();
        if (paramClass.equals(localClass))
          addMethod(paramList, paramMap, arrayOfMethod[i]);
      }
    return bool;
  }

  private static void initSignatureMap(List paramList, Map paramMap)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      paramMap.put(getSignature(localMethod), localMethod);
    }
  }

  private static void addMethod(List paramList, Map paramMap, Method paramMethod)
  {
    if (paramMap.isEmpty())
    {
      paramList.add(paramMethod);
      return;
    }
    String str = getSignature(paramMethod);
    if (!paramMap.containsKey(str))
    {
      paramList.add(paramMethod);
      paramMap.put(str, paramMethod);
    }
  }

  private static String getSignature(Method paramMethod)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramMethod.getName());
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    localStringBuffer.append('(');
    if (arrayOfClass.length > 0)
      localStringBuffer.append(arrayOfClass[0].getName());
    for (int i = 1; i < arrayOfClass.length; i++)
    {
      localStringBuffer.append(',');
      localStringBuffer.append(arrayOfClass[i].getName());
    }
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }

  public static Field[] getJScriptFields(Class paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    HashMap localHashMap = new HashMap();
    while (paramClass != null)
    {
      boolean bool = getPublicFields(paramClass, localArrayList, localHashMap);
      if (bool)
        break;
      getJScriptInterfaceFields(paramClass, localArrayList, localHashMap);
      paramClass = paramClass.getSuperclass();
    }
    return (Field[])localArrayList.toArray(new Field[localArrayList.size()]);
  }

  private static void getJScriptInterfaceFields(Class paramClass, List paramList, Map paramMap)
  {
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      boolean bool = getPublicFields(localClass, paramList, paramMap);
      if (!bool)
        getJScriptInterfaceFields(localClass, paramList, paramMap);
    }
  }

  private static boolean getPublicFields(Class paramClass, List paramList, Map paramMap)
  {
    Field[] arrayOfField = null;
    try
    {
      if (!Modifier.isPublic(paramClass.getModifiers()))
        return false;
      if (!JSClassLoader.isPackageAccessible(paramClass))
        return false;
      arrayOfField = paramClass.getFields();
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    boolean bool = true;
    Class localClass;
    for (int i = 0; i < arrayOfField.length; i++)
    {
      localClass = arrayOfField[i].getDeclaringClass();
      if (!Modifier.isPublic(localClass.getModifiers()))
      {
        bool = false;
        break;
      }
    }
    if (bool)
      for (i = 0; i < arrayOfField.length; i++)
        addField(paramList, paramMap, arrayOfField[i]);
    else
      for (i = 0; i < arrayOfField.length; i++)
      {
        localClass = arrayOfField[i].getDeclaringClass();
        if (paramClass.equals(localClass))
          addField(paramList, paramMap, arrayOfField[i]);
      }
    return bool;
  }

  private static void addField(List paramList, Map paramMap, Field paramField)
  {
    String str = paramField.getName();
    if (!paramMap.containsKey(str))
    {
      paramList.add(paramField);
      paramMap.put(str, paramField);
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.ReflectUtil
 * JD-Core Version:    0.6.2
 */