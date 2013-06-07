package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract interface DynamicMBean2 extends DynamicMBean
{
  public abstract Object getResource();

  public abstract String getClassName();

  public abstract void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;

  public abstract void registerFailed();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.mbeanserver.DynamicMBean2
 * JD-Core Version:    0.6.2
 */