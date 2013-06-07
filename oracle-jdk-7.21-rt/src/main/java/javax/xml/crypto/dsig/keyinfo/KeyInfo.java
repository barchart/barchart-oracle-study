package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public abstract interface KeyInfo extends XMLStructure
{
  public abstract List getContent();

  public abstract String getId();

  public abstract void marshal(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.keyinfo.KeyInfo
 * JD-Core Version:    0.6.2
 */