package sun.misc;

import sun.nio.ch.Interruptible;
import sun.reflect.ConstantPool;
import sun.reflect.annotation.AnnotationType;

public abstract interface JavaLangAccess
{
  public abstract ConstantPool getConstantPool(Class paramClass);

  public abstract void setAnnotationType(Class paramClass, AnnotationType paramAnnotationType);

  public abstract AnnotationType getAnnotationType(Class paramClass);

  public abstract <E extends Enum<E>> E[] getEnumConstantsShared(Class<E> paramClass);

  public abstract void blockedOn(Thread paramThread, Interruptible paramInterruptible);

  public abstract void registerShutdownHook(int paramInt, boolean paramBoolean, Runnable paramRunnable);

  public abstract int getStackTraceDepth(Throwable paramThrowable);

  public abstract StackTraceElement getStackTraceElement(Throwable paramThrowable, int paramInt);

  public abstract int getStringHash32(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.JavaLangAccess
 * JD-Core Version:    0.6.2
 */