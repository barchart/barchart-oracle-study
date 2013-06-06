package sun.plugin.liveconnect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.PrivilegedExceptionAction;
import sun.plugin.javascript.JSClassLoader;
import sun.plugin.javascript.ReflectUtil;

class PrivilegedCallMethodAction
  implements PrivilegedExceptionAction
{
  Method method;
  Object obj;
  Object[] args;

  PrivilegedCallMethodAction(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
  {
    this.method = paramMethod;
    this.obj = paramObject;
    this.args = paramArrayOfObject;
    if (this.args == null)
      this.args = new Object[0];
  }

  public Object run()
    throws Exception
  {
    if ((this.obj instanceof Class))
    {
      localObject = this.method.getName();
      Class localClass = (Class)this.obj;
      if (((String)localObject).equals("getMethods"))
      {
        Method[] arrayOfMethod = ReflectUtil.getJScriptMethods(localClass);
        for (int i = 0; i < arrayOfMethod.length; i++)
        {
          Method localMethod = arrayOfMethod[i];
          if (Modifier.isAbstract(localMethod.getModifiers()))
          {
            Class[] arrayOfClass = localMethod.getParameterTypes();
            arrayOfMethod[i] = localClass.getMethod(localMethod.getName(), arrayOfClass);
          }
        }
        return arrayOfMethod;
      }
      if (((String)localObject).equals("getFields"))
        return ReflectUtil.getJScriptFields(localClass);
      if ((((String)localObject).equals("getConstructors")) && ((!Modifier.isPublic(localClass.getModifiers())) || (!JSClassLoader.isPackageAccessible(localClass))))
        return new Constructor[0];
    }
    Object localObject = ReplaceMethod.getJScriptMethod(this.method);
    if (localObject != null)
      return JSClassLoader.invoke((Method)localObject, this.obj, this.args);
    throw new NoSuchMethodException(this.method.getName());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.liveconnect.PrivilegedCallMethodAction
 * JD-Core Version:    0.6.2
 */