package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract interface ServiceDefinition extends Iterable<SDDocument>
{
  @NotNull
  public abstract SDDocument getPrimary();

  public abstract void addFilter(@NotNull SDDocumentFilter paramSDDocumentFilter);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.ServiceDefinition
 * JD-Core Version:    0.6.2
 */