package javax.management;

import java.io.Serializable;

public abstract interface QueryExp extends Serializable
{
  public abstract boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;

  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.QueryExp
 * JD-Core Version:    0.6.2
 */