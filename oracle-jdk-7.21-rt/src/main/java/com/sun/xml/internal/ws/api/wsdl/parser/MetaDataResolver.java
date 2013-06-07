package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.net.URI;

public abstract class MetaDataResolver
{
  @Nullable
  public abstract ServiceDescriptor resolve(@NotNull URI paramURI);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver
 * JD-Core Version:    0.6.2
 */