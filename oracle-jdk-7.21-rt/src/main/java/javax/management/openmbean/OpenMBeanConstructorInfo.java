package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public abstract interface OpenMBeanConstructorInfo
{
  public abstract String getDescription();

  public abstract String getName();

  public abstract MBeanParameterInfo[] getSignature();

  public abstract boolean equals(Object paramObject);

  public abstract int hashCode();

  public abstract String toString();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.openmbean.OpenMBeanConstructorInfo
 * JD-Core Version:    0.6.2
 */