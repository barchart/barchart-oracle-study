package com.sun.org.apache.xalan.internal.xsltc.dom;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract interface ExtendedSAX extends ContentHandler, LexicalHandler, DTDHandler, DeclHandler
{
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.dom.ExtendedSAX
 * JD-Core Version:    0.6.2
 */