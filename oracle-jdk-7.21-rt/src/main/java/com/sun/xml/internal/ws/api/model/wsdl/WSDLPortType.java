package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLPortType extends WSDLObject, WSDLExtensible
{
  public abstract QName getName();

  public abstract WSDLOperation get(String paramString);

  public abstract Iterable<? extends WSDLOperation> getOperations();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType
 * JD-Core Version:    0.6.2
 */