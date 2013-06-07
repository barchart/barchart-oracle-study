package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;

public abstract interface XMLErrorHandler
{
  public abstract void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;

  public abstract void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;

  public abstract void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler
 * JD-Core Version:    0.6.2
 */