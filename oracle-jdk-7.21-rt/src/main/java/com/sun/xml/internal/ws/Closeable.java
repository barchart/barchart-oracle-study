package com.sun.xml.internal.ws;

import javax.xml.ws.WebServiceException;

public abstract interface Closeable extends java.io.Closeable
{
  public abstract void close()
    throws WebServiceException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.Closeable
 * JD-Core Version:    0.6.2
 */