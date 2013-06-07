package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.NamingException;

public abstract interface InitialContextFactoryBuilder
{
  public abstract InitialContextFactory createInitialContextFactory(Hashtable<?, ?> paramHashtable)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.spi.InitialContextFactoryBuilder
 * JD-Core Version:    0.6.2
 */