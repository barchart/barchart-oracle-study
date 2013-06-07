package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public abstract interface XMLDocumentSource
{
  public abstract void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler);

  public abstract XMLDocumentHandler getDocumentHandler();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource
 * JD-Core Version:    0.6.2
 */