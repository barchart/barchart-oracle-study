package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import java.io.IOException;

public abstract class HttpMetadataPublisher
{
  public abstract boolean handleMetadataRequest(@NotNull HttpAdapter paramHttpAdapter, @NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.transport.http.HttpMetadataPublisher
 * JD-Core Version:    0.6.2
 */