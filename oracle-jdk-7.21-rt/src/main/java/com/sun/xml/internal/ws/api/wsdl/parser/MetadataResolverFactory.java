package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.xml.sax.EntityResolver;

public abstract class MetadataResolverFactory
{
  @NotNull
  public abstract MetaDataResolver metadataResolver(@Nullable EntityResolver paramEntityResolver);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory
 * JD-Core Version:    0.6.2
 */