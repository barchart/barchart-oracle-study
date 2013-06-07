package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface DocumentAddressResolver
{
  @Nullable
  public abstract String getRelativeAddressFor(@NotNull SDDocument paramSDDocument1, @NotNull SDDocument paramSDDocument2);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.DocumentAddressResolver
 * JD-Core Version:    0.6.2
 */