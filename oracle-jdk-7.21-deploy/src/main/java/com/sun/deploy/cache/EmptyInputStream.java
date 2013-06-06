package com.sun.deploy.cache;

import java.io.IOException;
import java.io.InputStream;

class EmptyInputStream extends InputStream
{
  public int read()
    throws IOException
  {
    return -1;
  }
}

/* Location:           /home/user1/Temp/jvm/deploy.jar
 * Qualified Name:     com.sun.deploy.cache.EmptyInputStream
 * JD-Core Version:    0.6.2
 */