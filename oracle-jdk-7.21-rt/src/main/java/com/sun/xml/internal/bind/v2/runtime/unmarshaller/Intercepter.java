package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract interface Intercepter
{
  public abstract Object intercept(UnmarshallingContext.State paramState, Object paramObject)
    throws SAXException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter
 * JD-Core Version:    0.6.2
 */