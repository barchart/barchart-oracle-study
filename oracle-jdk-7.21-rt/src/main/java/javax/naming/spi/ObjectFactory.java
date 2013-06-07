package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;

public abstract interface ObjectFactory
{
  public abstract Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.spi.ObjectFactory
 * JD-Core Version:    0.6.2
 */