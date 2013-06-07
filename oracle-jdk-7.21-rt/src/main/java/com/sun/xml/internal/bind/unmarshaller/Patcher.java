package com.sun.xml.internal.bind.unmarshaller;

import org.xml.sax.SAXException;

public abstract interface Patcher
{
  public abstract void run()
    throws SAXException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.unmarshaller.Patcher
 * JD-Core Version:    0.6.2
 */