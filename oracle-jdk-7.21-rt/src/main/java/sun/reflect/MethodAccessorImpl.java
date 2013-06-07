package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl extends MagicAccessorImpl
  implements MethodAccessor
{
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.MethodAccessorImpl
 * JD-Core Version:    0.6.2
 */