package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public abstract interface MethodAccessor
{
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.MethodAccessor
 * JD-Core Version:    0.6.2
 */