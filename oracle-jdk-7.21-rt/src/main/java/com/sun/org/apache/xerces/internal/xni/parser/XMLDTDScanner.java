package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLDTDScanner extends XMLDTDSource, XMLDTDContentModelSource
{
  public abstract void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException;

  public abstract boolean scanDTDInternalSubset(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException, XNIException;

  public abstract boolean scanDTDExternalSubset(boolean paramBoolean)
    throws IOException, XNIException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner
 * JD-Core Version:    0.6.2
 */