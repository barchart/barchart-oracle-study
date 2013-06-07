package javax.management;

public abstract interface MBeanServerDelegateMBean
{
  public abstract String getMBeanServerId();

  public abstract String getSpecificationName();

  public abstract String getSpecificationVersion();

  public abstract String getSpecificationVendor();

  public abstract String getImplementationName();

  public abstract String getImplementationVersion();

  public abstract String getImplementationVendor();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanServerDelegateMBean
 * JD-Core Version:    0.6.2
 */