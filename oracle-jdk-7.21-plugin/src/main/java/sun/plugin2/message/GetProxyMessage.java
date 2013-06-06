package sun.plugin2.message;

import java.io.IOException;
import java.net.URL;
import sun.plugin2.message.helper.URLHelper;

public class GetProxyMessage extends AppletMessage
{
  public static final int ID = 41;
  private URL url;
  private boolean isSocketURI;

  public GetProxyMessage(Conversation paramConversation)
  {
    super(41, paramConversation);
  }

  public GetProxyMessage(Conversation paramConversation, int paramInt, URL paramURL, boolean paramBoolean)
    throws IllegalArgumentException
  {
    super(41, paramConversation, paramInt);
    if (paramURL == null)
      throw new IllegalArgumentException();
    this.url = paramURL;
    this.isSocketURI = paramBoolean;
  }

  public URL getURL()
  {
    return this.url;
  }

  public boolean isSocketURI()
  {
    return this.isSocketURI;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    URLHelper.write(paramSerializer, this.url);
    paramSerializer.writeBoolean(this.isSocketURI);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.url = URLHelper.read(paramSerializer);
    this.isSocketURI = paramSerializer.readBoolean();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.GetProxyMessage
 * JD-Core Version:    0.6.2
 */