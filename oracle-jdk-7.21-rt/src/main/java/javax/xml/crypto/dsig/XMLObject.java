package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface XMLObject extends XMLStructure
{
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#Object";

  public abstract List getContent();

  public abstract String getId();

  public abstract String getMimeType();

  public abstract String getEncoding();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.XMLObject
 * JD-Core Version:    0.6.2
 */