package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public abstract interface ListIterator<E>
{
  public abstract boolean hasNext();

  public abstract E next()
    throws SAXException, JAXBException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator
 * JD-Core Version:    0.6.2
 */