package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;

public abstract interface WSDLPart extends WSDLObject
{
  public abstract String getName();

  public abstract ParameterBinding getBinding();

  public abstract int getIndex();

  public abstract WSDLPartDescriptor getDescriptor();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLPart
 * JD-Core Version:    0.6.2
 */