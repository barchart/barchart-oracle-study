package sun.plugin.javascript;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;

final class Trampoline
{
  private static void validateDeclaringClass(Class paramClass)
    throws InvocationTargetException, IllegalAccessException
  {
    if ((paramClass.equals(AccessController.class)) || (paramClass.getName().startsWith("java.lang.invoke.")))
      throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported"));
  }

  private static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException
  {
    Method localMethod = paramMethod;
    Object[] arrayOfObject = paramArrayOfObject;
    if ((paramObject instanceof Method))
      validateDeclaringClass(((Method)paramObject).getDeclaringClass());
    while ((localMethod.getDeclaringClass().equals(Method.class)) && (localMethod.getName().equals("invoke")) && (arrayOfObject.length > 1) && ((arrayOfObject[0] instanceof Method)) && ((arrayOfObject[1] instanceof Object[])))
    {
      localMethod = (Method)arrayOfObject[0];
      arrayOfObject = (Object[])arrayOfObject[1];
    }
    validateDeclaringClass(localMethod.getDeclaringClass());
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }

  private static Object newInstance(Constructor paramConstructor, Object[] paramArrayOfObject)
    throws InstantiationException, InvocationTargetException, IllegalAccessException
  {
    validateDeclaringClass(paramConstructor.getDeclaringClass());
    return paramConstructor.newInstance(paramArrayOfObject);
  }

  static
  {
    if (Trampoline.class.getClassLoader() == null)
      throw new Error("Trampoline must not be defined by the bootstrap classloader");
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.javascript.Trampoline
 * JD-Core Version:    0.6.2
 */