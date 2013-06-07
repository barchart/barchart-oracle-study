package javax.management;

public abstract interface MBeanRegistration
{
  public abstract ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;

  public abstract void postRegister(Boolean paramBoolean);

  public abstract void preDeregister()
    throws Exception;

  public abstract void postDeregister();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanRegistration
 * JD-Core Version:    0.6.2
 */