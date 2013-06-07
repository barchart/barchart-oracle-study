package javax.management.openmbean;

public abstract interface OpenMBeanAttributeInfo extends OpenMBeanParameterInfo
{
  public abstract boolean isReadable();

  public abstract boolean isWritable();

  public abstract boolean isIs();

  public abstract boolean equals(Object paramObject);

  public abstract int hashCode();

  public abstract String toString();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.openmbean.OpenMBeanAttributeInfo
 * JD-Core Version:    0.6.2
 */