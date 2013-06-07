package javax.naming.ldap;

import java.io.Serializable;

public abstract interface ExtendedResponse extends Serializable
{
  public abstract String getID();

  public abstract byte[] getEncodedValue();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.ExtendedResponse
 * JD-Core Version:    0.6.2
 */