package org.xml.sax;

public abstract interface ErrorHandler
{
  public abstract void warning(SAXParseException paramSAXParseException)
    throws SAXException;

  public abstract void error(SAXParseException paramSAXParseException)
    throws SAXException;

  public abstract void fatalError(SAXParseException paramSAXParseException)
    throws SAXException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.xml.sax.ErrorHandler
 * JD-Core Version:    0.6.2
 */