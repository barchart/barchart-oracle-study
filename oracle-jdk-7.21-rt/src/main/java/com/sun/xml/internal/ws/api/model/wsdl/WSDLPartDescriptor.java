package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLPartDescriptor extends WSDLObject
{
  public abstract QName name();

  public abstract WSDLDescriptorKind type();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor
 * JD-Core Version:    0.6.2
 */