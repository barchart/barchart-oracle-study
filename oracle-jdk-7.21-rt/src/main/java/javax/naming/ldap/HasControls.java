package javax.naming.ldap;

import javax.naming.NamingException;

public abstract interface HasControls
{
  public abstract Control[] getControls()
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.HasControls
 * JD-Core Version:    0.6.2
 */