package sun.plugin.net.protocol.jar;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends sun.net.www.protocol.jar.Handler
{
  protected URLConnection openConnection(URL paramURL)
    throws IOException
  {
    return new CachedJarURLConnection(paramURL, this);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.net.protocol.jar.Handler
 * JD-Core Version:    0.6.2
 */