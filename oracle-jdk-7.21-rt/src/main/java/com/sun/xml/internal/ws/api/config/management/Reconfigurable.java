package com.sun.xml.internal.ws.api.config.management;

import javax.xml.ws.WebServiceException;

public abstract interface Reconfigurable
{
  public abstract void reconfigure()
    throws WebServiceException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.config.management.Reconfigurable
 * JD-Core Version:    0.6.2
 */