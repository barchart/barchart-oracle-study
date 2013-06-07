package javax.management.remote;

import javax.management.MBeanServer;

public abstract interface MBeanServerForwarder extends MBeanServer
{
  public abstract MBeanServer getMBeanServer();

  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.MBeanServerForwarder
 * JD-Core Version:    0.6.2
 */