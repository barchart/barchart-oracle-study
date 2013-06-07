package javax.naming.ldap;

import java.io.Serializable;
import javax.naming.NamingException;

public abstract interface ExtendedRequest extends Serializable
{
  public abstract String getID();

  public abstract byte[] getEncodedValue();

  public abstract ExtendedResponse createExtendedResponse(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NamingException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.ldap.ExtendedRequest
 * JD-Core Version:    0.6.2
 */