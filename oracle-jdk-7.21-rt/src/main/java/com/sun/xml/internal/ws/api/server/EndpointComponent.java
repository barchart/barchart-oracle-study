package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface EndpointComponent
{
  @Nullable
  public abstract <T> T getSPI(@NotNull Class<T> paramClass);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.EndpointComponent
 * JD-Core Version:    0.6.2
 */