package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;

public abstract interface SAXCatalogParser extends ContentHandler, DocumentHandler
{
  public abstract void setCatalog(Catalog paramCatalog);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogParser
 * JD-Core Version:    0.6.2
 */