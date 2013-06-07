package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceContext;

public abstract interface WSWebServiceContext extends WebServiceContext
{
  @Nullable
  public abstract Packet getRequestPacket();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.WSWebServiceContext
 * JD-Core Version:    0.6.2
 */