package javax.naming.event;

import java.util.EventListener;

public abstract interface NamingListener extends EventListener
{
  public abstract void namingExceptionThrown(NamingExceptionEvent paramNamingExceptionEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.event.NamingListener
 * JD-Core Version:    0.6.2
 */