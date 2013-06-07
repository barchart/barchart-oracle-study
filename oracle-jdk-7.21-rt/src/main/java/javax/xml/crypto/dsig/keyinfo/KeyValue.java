package javax.xml.crypto.dsig.keyinfo;

import java.security.KeyException;
import java.security.PublicKey;
import javax.xml.crypto.XMLStructure;

public abstract interface KeyValue extends XMLStructure
{
  public static final String DSA_TYPE = "http://www.w3.org/2000/09/xmldsig#DSAKeyValue";
  public static final String RSA_TYPE = "http://www.w3.org/2000/09/xmldsig#RSAKeyValue";

  public abstract PublicKey getPublicKey()
    throws KeyException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.keyinfo.KeyValue
 * JD-Core Version:    0.6.2
 */