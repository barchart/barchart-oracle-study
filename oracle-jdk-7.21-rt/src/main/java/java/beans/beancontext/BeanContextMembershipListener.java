package java.beans.beancontext;

import java.util.EventListener;

public abstract interface BeanContextMembershipListener extends EventListener
{
  public abstract void childrenAdded(BeanContextMembershipEvent paramBeanContextMembershipEvent);

  public abstract void childrenRemoved(BeanContextMembershipEvent paramBeanContextMembershipEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.beancontext.BeanContextMembershipListener
 * JD-Core Version:    0.6.2
 */