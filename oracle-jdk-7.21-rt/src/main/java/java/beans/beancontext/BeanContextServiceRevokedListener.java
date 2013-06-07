package java.beans.beancontext;

import java.util.EventListener;

public abstract interface BeanContextServiceRevokedListener extends EventListener
{
  public abstract void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.beancontext.BeanContextServiceRevokedListener
 * JD-Core Version:    0.6.2
 */