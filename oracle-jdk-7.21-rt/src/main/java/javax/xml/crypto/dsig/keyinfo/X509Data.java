package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface X509Data extends XMLStructure
{
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#X509Data";
  public static final String RAW_X509_CERTIFICATE_TYPE = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";

  public abstract List getContent();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.keyinfo.X509Data
 * JD-Core Version:    0.6.2
 */