package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public abstract interface WSDLOutput extends WSDLObject, WSDLExtensible
{
  public abstract String getName();

  public abstract WSDLMessage getMessage();

  public abstract String getAction();

  @NotNull
  public abstract WSDLOperation getOperation();

  @NotNull
  public abstract QName getQName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput
 * JD-Core Version:    0.6.2
 */