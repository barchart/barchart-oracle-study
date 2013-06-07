package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract interface WSDLOperation extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract QName getName();

  @NotNull
  public abstract WSDLInput getInput();

  @Nullable
  public abstract WSDLOutput getOutput();

  public abstract boolean isOneWay();

  public abstract Iterable<? extends WSDLFault> getFaults();

  @Nullable
  public abstract WSDLFault getFault(QName paramQName);

  @NotNull
  public abstract QName getPortTypeName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation
 * JD-Core Version:    0.6.2
 */