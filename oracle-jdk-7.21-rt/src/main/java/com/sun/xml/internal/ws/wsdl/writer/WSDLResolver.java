package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.transform.Result;
import javax.xml.ws.Holder;

public abstract interface WSDLResolver
{
  @NotNull
  public abstract Result getWSDL(@NotNull String paramString);

  @Nullable
  public abstract Result getAbstractWSDL(@NotNull Holder<String> paramHolder);

  @Nullable
  public abstract Result getSchemaOutput(@NotNull String paramString, @NotNull Holder<String> paramHolder);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.wsdl.writer.WSDLResolver
 * JD-Core Version:    0.6.2
 */