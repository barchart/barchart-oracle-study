package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import org.xml.sax.Locator;

public abstract interface WSDLObject
{
  @NotNull
  public abstract Locator getLocation();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLObject
 * JD-Core Version:    0.6.2
 */