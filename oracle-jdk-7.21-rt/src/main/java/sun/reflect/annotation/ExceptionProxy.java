package sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy
  implements Serializable
{
  protected abstract RuntimeException generateException();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.ExceptionProxy
 * JD-Core Version:    0.6.2
 */