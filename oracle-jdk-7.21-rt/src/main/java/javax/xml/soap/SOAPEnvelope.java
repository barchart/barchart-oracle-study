package javax.xml.soap;

public abstract interface SOAPEnvelope extends SOAPElement
{
  public abstract Name createName(String paramString1, String paramString2, String paramString3)
    throws SOAPException;

  public abstract Name createName(String paramString)
    throws SOAPException;

  public abstract SOAPHeader getHeader()
    throws SOAPException;

  public abstract SOAPBody getBody()
    throws SOAPException;

  public abstract SOAPHeader addHeader()
    throws SOAPException;

  public abstract SOAPBody addBody()
    throws SOAPException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.soap.SOAPEnvelope
 * JD-Core Version:    0.6.2
 */