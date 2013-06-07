package javax.xml.transform;

public abstract interface ErrorListener
{
  public abstract void warning(TransformerException paramTransformerException)
    throws TransformerException;

  public abstract void error(TransformerException paramTransformerException)
    throws TransformerException;

  public abstract void fatalError(TransformerException paramTransformerException)
    throws TransformerException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.transform.ErrorListener
 * JD-Core Version:    0.6.2
 */