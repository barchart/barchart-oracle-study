package javax.xml.bind;

import org.xml.sax.ContentHandler;

public abstract interface UnmarshallerHandler extends ContentHandler
{
  public abstract Object getResult()
    throws JAXBException, IllegalStateException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.bind.UnmarshallerHandler
 * JD-Core Version:    0.6.2
 */