package com.sun.javaws.net.protocol.jar;

import com.sun.jnlp.JNLPCachedJarURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends sun.net.www.protocol.jar.Handler
{
  protected URLConnection openConnection(URL paramURL)
    throws IOException
  {
    return new JNLPCachedJarURLConnection(paramURL, this);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.net.protocol.jar.Handler
 * JD-Core Version:    0.6.2
 */