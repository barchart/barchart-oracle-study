package sun.plugin2.message.helper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import sun.plugin2.message.Serializer;

public final class URLHelper
{
  public static void write(Serializer paramSerializer, URL paramURL)
    throws IOException
  {
    if (paramURL == null)
    {
      paramSerializer.writeBoolean(false);
    }
    else
    {
      paramSerializer.writeBoolean(true);
      paramSerializer.writeUTF(paramURL.getProtocol());
      paramSerializer.writeUTF(paramURL.getHost());
      paramSerializer.writeInt(paramURL.getPort());
      paramSerializer.writeUTF(paramURL.getFile());
    }
  }

  public static URL read(Serializer paramSerializer)
    throws IOException
  {
    if (!paramSerializer.readBoolean())
      return null;
    URL localURL = null;
    try
    {
      localURL = new URL(paramSerializer.readUTF(), paramSerializer.readUTF(), paramSerializer.readInt(), paramSerializer.readUTF());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localMalformedURLException.printStackTrace();
    }
    return localURL;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.helper.URLHelper
 * JD-Core Version:    0.6.2
 */