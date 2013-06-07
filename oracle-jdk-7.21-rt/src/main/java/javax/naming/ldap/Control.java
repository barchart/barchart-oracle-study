package javax.naming.ldap;

import java.io.Serializable;

public abstract interface Control extends Serializable
{
  public static final boolean CRITICAL = true;
  public static final boolean NONCRITICAL = false;

  public abstract String getID();

  public abstract boolean isCritical();

  public abstract byte[] getEncodedValue();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.Control
 * JD-Core Version:    0.6.2
 */