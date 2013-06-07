package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;

public abstract class BindingIDFactory
{
  @Nullable
  public abstract BindingID parse(@NotNull String paramString)
    throws WebServiceException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.BindingIDFactory
 * JD-Core Version:    0.6.2
 */