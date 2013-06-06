package com.sun.deploy.model;

import java.io.IOException;
import java.net.URL;

public abstract interface ResourceObject
{
  public abstract URL getResourceURL();

  public abstract String getResourceVersion();

  public abstract Object clone()
    throws CloneNotSupportedException;

  public abstract void doClose()
    throws IOException;

  public abstract void close()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.model.ResourceObject
 * JD-Core Version:    0.6.2
 */