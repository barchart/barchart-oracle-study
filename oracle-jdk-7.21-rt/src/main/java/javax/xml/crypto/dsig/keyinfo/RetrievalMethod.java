package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public abstract interface RetrievalMethod extends URIReference, XMLStructure
{
  public abstract List getTransforms();

  public abstract String getURI();

  public abstract Data dereference(XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.keyinfo.RetrievalMethod
 * JD-Core Version:    0.6.2
 */