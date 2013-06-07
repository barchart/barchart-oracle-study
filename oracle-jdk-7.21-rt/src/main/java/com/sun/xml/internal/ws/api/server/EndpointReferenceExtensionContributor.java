package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import javax.xml.namespace.QName;

public abstract class EndpointReferenceExtensionContributor
{
  public abstract WSEndpointReference.EPRExtension getEPRExtension(WSEndpoint paramWSEndpoint, @Nullable WSEndpointReference.EPRExtension paramEPRExtension);

  public abstract QName getQName();
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.server.EndpointReferenceExtensionContributor
 * JD-Core Version:    0.6.2
 */