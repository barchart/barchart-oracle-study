package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

public abstract interface ManagedEndpointFactory
{
  public abstract <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> paramWSEndpoint, EndpointCreationAttributes paramEndpointCreationAttributes);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.config.management.ManagedEndpointFactory
 * JD-Core Version:    0.6.2
 */