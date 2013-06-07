package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ResourceLoader
{
  public abstract URL getResource(String paramString)
    throws MalformedURLException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.ResourceLoader
 * JD-Core Version:    0.6.2
 */