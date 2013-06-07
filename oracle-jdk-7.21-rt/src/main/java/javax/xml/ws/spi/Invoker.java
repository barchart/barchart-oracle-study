package javax.xml.ws.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker
{
  public abstract void inject(WebServiceContext paramWebServiceContext)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

  public abstract Object invoke(Method paramMethod, Object[] paramArrayOfObject)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.spi.Invoker
 * JD-Core Version:    0.6.2
 */