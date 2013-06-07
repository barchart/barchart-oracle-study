package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class CacheResponse
{
  public abstract Map<String, List<String>> getHeaders()
    throws IOException;

  public abstract InputStream getBody()
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.CacheResponse
 * JD-Core Version:    0.6.2
 */