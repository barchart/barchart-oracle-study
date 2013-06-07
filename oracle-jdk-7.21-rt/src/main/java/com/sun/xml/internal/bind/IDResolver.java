package com.sun.xml.internal.bind;

import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

public abstract class IDResolver
{
  public void startDocument(ValidationEventHandler eventHandler)
    throws SAXException
  {
  }

  public void endDocument()
    throws SAXException
  {
  }

  public abstract void bind(String paramString, Object paramObject)
    throws SAXException;

  public abstract Callable<?> resolve(String paramString, Class paramClass)
    throws SAXException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.IDResolver
 * JD-Core Version:    0.6.2
 */