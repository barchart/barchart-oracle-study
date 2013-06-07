package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract interface WSDLBoundFault extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract String getName();

  @Nullable
  public abstract QName getQName();

  @Nullable
  public abstract WSDLFault getFault();

  @NotNull
  public abstract WSDLBoundOperation getBoundOperation();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault
 * JD-Core Version:    0.6.2
 */