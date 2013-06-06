package sun.plugin2.liveconnect;

import com.sun.deploy.config.Config;
import com.sun.deploy.trace.Trace;
import com.sun.deploy.trace.TraceLevel;
import com.sun.java.browser.plugin2.liveconnect.v1.Bridge;
import com.sun.java.browser.plugin2.liveconnect.v1.InvocationDelegate;
import com.sun.java.browser.plugin2.liveconnect.v1.Result;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.plugin.javascript.JSClassLoader;
import sun.plugin.javascript.ReflectUtil;

public class JavaClass
  implements InvocationDelegate
{
  private Class clazz;
  private Bridge bridge;
  private Map methodMap;
  private Map fieldMap;
  private MemberBundle constructors;
  private Map lowerCaseMethodMap;
  private Map lowerCaseFieldMap;
  private boolean isArray;
  private Class componentType;

  public JavaClass(Class paramClass, Bridge paramBridge)
  {
    this.clazz = paramClass;
    this.bridge = paramBridge;
    this.isArray = paramClass.isArray();
    if (this.isArray)
      this.componentType = paramClass.getComponentType();
  }

  private static String argsToString(Object[] paramArrayOfObject)
  {
    StringBuffer localStringBuffer = new StringBuffer("[");
    if (paramArrayOfObject != null)
      for (int i = 0; i < paramArrayOfObject.length; i++)
      {
        if (i > 0)
          localStringBuffer.append(", ");
        Object localObject = paramArrayOfObject[i];
        String str = null;
        if (localObject != null)
          str = localObject.getClass().getName();
        localStringBuffer.append(str);
      }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }

  public boolean hasField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean[0] = hasField0(paramString, paramObject, paramBoolean2);
    return true;
  }

  private boolean hasField0(String paramString, Object paramObject, boolean paramBoolean)
  {
    if (this.isArray)
    {
      if ("length".equals(paramString))
        return true;
      try
      {
        int i = Integer.parseInt(paramString);
        return (i >= 0) && (i < Array.getLength(paramObject));
      }
      catch (Exception localException)
      {
        return false;
      }
    }
    if (this.fieldMap == null)
      collectFields();
    Field localField = (Field)this.fieldMap.get(paramString);
    if (localField != null)
      return true;
    localField = (Field)this.lowerCaseFieldMap.get(paramString.toLowerCase());
    return localField != null;
  }

  public boolean hasMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean[0] = hasMethod0(paramString, paramObject, paramBoolean2);
    return true;
  }

  private boolean hasMethod0(String paramString, Object paramObject, boolean paramBoolean)
  {
    if (this.methodMap == null)
      collectMethods();
    MemberBundle localMemberBundle = (MemberBundle)this.methodMap.get(paramString);
    if (localMemberBundle != null)
      return true;
    localMemberBundle = (MemberBundle)this.lowerCaseMethodMap.get(paramString.toLowerCase());
    return localMemberBundle != null;
  }

  public boolean hasFieldOrMethod(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean[] paramArrayOfBoolean)
  {
    int i = (hasField0(paramString, paramObject, paramBoolean2)) || (hasMethod0(paramString, paramObject, paramBoolean2)) ? 1 : 0;
    paramArrayOfBoolean[0] = i;
    return true;
  }

  public boolean invoke(String paramString, Object paramObject, Object[] paramArrayOfObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
    throws Exception
  {
    paramArrayOfResult[0] = invoke0(paramString, paramObject, paramArrayOfObject, paramBoolean2);
    return true;
  }

  private Result invoke0(String paramString, Object paramObject, Object[] paramArrayOfObject, boolean paramBoolean)
    throws Exception
  {
    if (this.methodMap == null)
      collectMethods();
    MemberBundle localMemberBundle = (MemberBundle)this.methodMap.get(paramString);
    if (localMemberBundle == null)
      localMemberBundle = (MemberBundle)this.lowerCaseMethodMap.get(paramString.toLowerCase());
    if (localMemberBundle == null)
      throw new NoSuchMethodException(paramString + " in class: " + this.clazz.getName());
    return localMemberBundle.invoke(paramObject, paramArrayOfObject);
  }

  public Object findClass(String paramString)
  {
    throw new UnsupportedOperationException("Should not call this");
  }

  public Object newInstance(Object paramObject, Object[] paramArrayOfObject)
    throws Exception
  {
    if (this.constructors == null)
      collectConstructors();
    Result localResult = null;
    try
    {
      localResult = this.constructors.invoke(null, paramArrayOfObject);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      localResult = this.constructors.invoke(null, null);
    }
    return localResult.value();
  }

  public boolean getField(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, Result[] paramArrayOfResult)
    throws Exception
  {
    paramArrayOfResult[0] = getField0(paramString, paramObject, paramBoolean2);
    return true;
  }

  private Result getField0(String paramString, Object paramObject, boolean paramBoolean)
    throws Exception
  {
    if (this.isArray)
    {
      if ("length".equals(paramString))
        return new Result(new Integer(Array.getLength(paramObject)), false);
      int i = Integer.parseInt(paramString);
      return new Result(Array.get(paramObject, i), isBoxingClass(paramObject.getClass().getComponentType()));
    }
    if (this.fieldMap == null)
      collectFields();
    Field localField = (Field)this.fieldMap.get(paramString);
    if (localField == null)
      localField = (Field)this.lowerCaseFieldMap.get(paramString.toLowerCase());
    if (localField == null)
      throw new NoSuchFieldException(paramString + " in class: " + this.clazz.getName());
    return new Result(localField.get(paramObject), isBoxingClass(localField.getType()));
  }

  public boolean setField(String paramString, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2)
    throws Exception
  {
    if (this.isArray)
    {
      int i = Integer.parseInt(paramString);
      Array.set(paramObject1, i, this.bridge.convert(paramObject2, this.componentType));
    }
    else
    {
      if (this.fieldMap == null)
        collectFields();
      Field localField = (Field)this.fieldMap.get(paramString);
      if (localField == null)
        localField = (Field)this.lowerCaseFieldMap.get(paramString.toLowerCase());
      if (localField == null)
        throw new NoSuchFieldException(paramString);
      Class localClass = localField.getType();
      localField.set(paramObject1, this.bridge.convert(paramObject2, localClass));
    }
    return true;
  }

  private static boolean isBoxingClass(Class paramClass)
  {
    return (paramClass == Boolean.class) || (paramClass == Byte.class) || (paramClass == Short.class) || (paramClass == Character.class) || (paramClass == Integer.class) || (paramClass == Long.class) || (paramClass == Float.class) || (paramClass == Double.class);
  }

  private void collectMethods()
  {
    Method[] arrayOfMethod = ReflectUtil.getJScriptMethods(this.clazz);
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    for (int i = 0; i < arrayOfMethod.length; i++)
    {
      Method localMethod = arrayOfMethod[i];
      MemberBundle localMemberBundle = (MemberBundle)localHashMap1.get(localMethod.getName());
      if (localMemberBundle == null)
      {
        localMemberBundle = new MemberBundle(null);
        localHashMap1.put(localMethod.getName(), localMemberBundle);
      }
      localMemberBundle.add(localMethod);
      String str = localMethod.getName().toLowerCase();
      localMemberBundle = (MemberBundle)localHashMap2.get(str);
      if (localMemberBundle == null)
      {
        localMemberBundle = new MemberBundle(null);
        localHashMap2.put(str, localMemberBundle);
      }
      localMemberBundle.add(localMethod);
    }
    this.methodMap = localHashMap1;
    this.lowerCaseMethodMap = localHashMap2;
  }

  private void collectConstructors()
  {
    Constructor[] arrayOfConstructor = this.clazz.getConstructors();
    MemberBundle localMemberBundle = new MemberBundle(null);
    for (int i = 0; i < arrayOfConstructor.length; i++)
      localMemberBundle.add(arrayOfConstructor[i]);
    this.constructors = localMemberBundle;
  }

  private void collectFields()
  {
    Field[] arrayOfField = ReflectUtil.getJScriptFields(this.clazz);
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    for (int i = 0; i < arrayOfField.length; i++)
    {
      localHashMap1.put(arrayOfField[i].getName(), arrayOfField[i]);
      localHashMap2.put(arrayOfField[i].getName().toLowerCase(), arrayOfField[i]);
    }
    this.fieldMap = localHashMap1;
    this.lowerCaseFieldMap = localHashMap2;
  }

  private static class ConstructorInfo extends JavaClass.MemberInfo
  {
    protected ConstructorInfo(Constructor paramConstructor)
    {
      super();
      this.parameterTypes = paramConstructor.getParameterTypes();
    }

    public Constructor getConstructor()
    {
      return (Constructor)getMember();
    }

    public Object invoke(Object paramObject, Object[] paramArrayOfObject)
      throws Exception
    {
      return JSClassLoader.newInstance(getConstructor(), paramArrayOfObject);
    }

    public Class getReturnType()
    {
      return getConstructor().getDeclaringClass();
    }

    public boolean isBridge()
    {
      return false;
    }
  }

  private class MemberBundle
  {
    protected List members = new ArrayList();

    private MemberBundle()
    {
    }

    public void add(Method paramMethod)
    {
      JavaClass.MethodInfo localMethodInfo = new JavaClass.MethodInfo(paramMethod);
      if ((!localMethodInfo.isBridge()) && (!this.members.contains(localMethodInfo)))
        this.members.add(localMethodInfo);
    }

    public void add(Constructor paramConstructor)
    {
      this.members.add(new JavaClass.ConstructorInfo(paramConstructor));
    }

    public Result invoke(Object paramObject, Object[] paramArrayOfObject)
      throws Exception
    {
      Object localObject1 = null;
      Object localObject2 = null;
      Class[] arrayOfClass1 = null;
      int i = 0;
      int j = 0;
      Object localObject3 = this.members.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        JavaClass.MemberInfo localMemberInfo = (JavaClass.MemberInfo)((Iterator)localObject3).next();
        if (Config.getPluginDebug())
          Trace.println("Try MemberInfo: " + localMemberInfo, TraceLevel.LIVECONNECT);
        Class[] arrayOfClass2 = localMemberInfo.getParameterTypes();
        if (paramArrayOfObject == null ? arrayOfClass2.length == 0 : arrayOfClass2.length == paramArrayOfObject.length)
        {
          int m = 0;
          for (int n = 0; n < arrayOfClass2.length; n++)
          {
            Object localObject5 = paramArrayOfObject[n];
            Class localClass = arrayOfClass2[n];
            int i1 = JavaClass.this.bridge.conversionCost(localObject5, localClass);
            if (i1 < 0)
            {
              m = -1;
              break;
            }
            m += i1;
          }
          if (m >= 0)
          {
            if ((localObject1 == null) || (m < i))
            {
              localObject1 = localMemberInfo;
              arrayOfClass1 = localMemberInfo.getParameterTypes();
              i = m;
              j = 0;
            }
            else if (m == i)
            {
              j = 1;
              localObject2 = localMemberInfo;
            }
          }
          else
            Trace.println("Failed: " + localMemberInfo + ", convert cost " + m, TraceLevel.LIVECONNECT);
        }
      }
      if (localObject1 == null)
        throw new IllegalArgumentException("No method found matching name " + ((JavaClass.MemberInfo)this.members.get(0)).getName() + " and arguments " + JavaClass.argsToString(paramArrayOfObject));
      if (j != 0)
        throw new IllegalArgumentException("More than one method matching name " + ((JavaClass.MemberInfo)this.members.get(0)).getName() + " and arguments " + JavaClass.argsToString(paramArrayOfObject) + "\n  Method 1: " + localObject1.getMember().toString() + "\n  Method 2: " + localObject2.getMember().toString());
      localObject3 = null;
      if (paramArrayOfObject != null)
      {
        localObject3 = new Object[paramArrayOfObject.length];
        for (int k = 0; k < paramArrayOfObject.length; k++)
          localObject3[k] = JavaClass.this.bridge.convert(paramArrayOfObject[k], arrayOfClass1[k]);
      }
      Object localObject4 = localObject1.invoke(paramObject, (Object[])localObject3);
      return new Result(localObject4, JavaClass.isBoxingClass(localObject1.getReturnType()));
    }

    MemberBundle(JavaClass.1 arg2)
    {
      this();
    }
  }

  private static abstract class MemberInfo
  {
    private Member member;
    protected Class[] parameterTypes;

    protected MemberInfo(Member paramMember)
    {
      this.member = paramMember;
    }

    protected Member getMember()
    {
      return this.member;
    }

    public String getName()
    {
      return getMember().getName();
    }

    public Class[] getParameterTypes()
    {
      return this.parameterTypes;
    }

    public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
      throws Exception;

    public abstract Class getReturnType();

    public abstract boolean isBridge();

    public String toString()
    {
      return this.member.toString();
    }
  }

  private static class MethodInfo extends JavaClass.MemberInfo
  {
    protected MethodInfo(Method paramMethod)
    {
      super();
      this.parameterTypes = paramMethod.getParameterTypes();
    }

    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != getClass()))
        return false;
      Method localMethod1 = getMethod();
      Method localMethod2 = ((MethodInfo)paramObject).getMethod();
      return (localMethod1.getName().equals(localMethod2.getName())) && (localMethod1.getReturnType() == localMethod2.getReturnType()) && (arraysEq(localMethod1.getParameterTypes(), localMethod2.getParameterTypes()));
    }

    public Method getMethod()
    {
      return (Method)getMember();
    }

    public Object invoke(Object paramObject, Object[] paramArrayOfObject)
      throws Exception
    {
      Object localObject = JSClassLoader.invoke(getMethod(), paramObject, paramArrayOfObject);
      if ((localObject == null) && (getMethod().getReturnType() == Void.TYPE))
        return Void.TYPE;
      return localObject;
    }

    public Class getReturnType()
    {
      return getMethod().getReturnType();
    }

    public boolean isBridge()
    {
      try
      {
        return getMethod().isBridge();
      }
      catch (Error localError)
      {
      }
      return false;
    }

    private boolean arraysEq(Class[] paramArrayOfClass1, Class[] paramArrayOfClass2)
    {
      if ((paramArrayOfClass1 == null ? 1 : 0) != (paramArrayOfClass2 == null ? 1 : 0))
        return false;
      if (paramArrayOfClass1 == null)
        return true;
      if (paramArrayOfClass1.length != paramArrayOfClass2.length)
        return false;
      for (int i = 0; i < paramArrayOfClass1.length; i++)
        if (paramArrayOfClass1[i] != paramArrayOfClass2[i])
          return false;
      return true;
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.liveconnect.JavaClass
 * JD-Core Version:    0.6.2
 */