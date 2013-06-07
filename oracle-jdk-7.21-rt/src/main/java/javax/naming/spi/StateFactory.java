package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public abstract interface StateFactory
{
  public abstract Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.spi.StateFactory
 * JD-Core Version:    0.6.2
 */