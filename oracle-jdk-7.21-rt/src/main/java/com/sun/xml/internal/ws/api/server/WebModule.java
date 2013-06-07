package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class WebModule extends Module
{
  @NotNull
  public abstract String getContextPath();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.WebModule
 * JD-Core Version:    0.6.2
 */