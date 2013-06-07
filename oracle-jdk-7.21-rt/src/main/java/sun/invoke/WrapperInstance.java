package sun.invoke;

import java.lang.invoke.MethodHandle;

public abstract interface WrapperInstance
{
  public abstract MethodHandle getWrapperInstanceTarget();

  public abstract Class<?> getWrapperInstanceType();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.WrapperInstance
 * JD-Core Version:    0.6.2
 */