package javax.xml.transform;

public abstract interface URIResolver
{
  public abstract Source resolve(String paramString1, String paramString2)
    throws TransformerException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.transform.URIResolver
 * JD-Core Version:    0.6.2
 */