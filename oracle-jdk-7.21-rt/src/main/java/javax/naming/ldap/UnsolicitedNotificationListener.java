package javax.naming.ldap;

import javax.naming.event.NamingListener;

public abstract interface UnsolicitedNotificationListener extends NamingListener
{
  public abstract void notificationReceived(UnsolicitedNotificationEvent paramUnsolicitedNotificationEvent);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.UnsolicitedNotificationListener
 * JD-Core Version:    0.6.2
 */