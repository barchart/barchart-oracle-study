package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import java.net.URI;

public abstract interface BoundEndpoint
{
  @NotNull
  public abstract WSEndpoint getEndpoint();

  @NotNull
  public abstract URI getAddress();

  @NotNull
  public abstract URI getAddress(String paramString);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.BoundEndpoint
 * JD-Core Version:    0.6.2
 */