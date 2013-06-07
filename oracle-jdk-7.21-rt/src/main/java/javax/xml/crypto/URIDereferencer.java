package javax.xml.crypto;

public abstract interface URIDereferencer
{
  public abstract Data dereference(URIReference paramURIReference, XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.URIDereferencer
 * JD-Core Version:    0.6.2
 */