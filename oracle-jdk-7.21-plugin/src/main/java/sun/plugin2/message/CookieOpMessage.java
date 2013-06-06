package sun.plugin2.message;

import java.io.IOException;
import java.net.URL;
import sun.plugin2.message.helper.URLHelper;

public class CookieOpMessage extends AppletMessage
{
  public static final int ID = 45;
  public static final int GET_COOKIE = 1;
  public static final int SET_COOKIE = 2;
  private int operationKind;
  private URL url;
  private String cookie;

  public CookieOpMessage(Conversation paramConversation)
  {
    super(45, paramConversation);
  }

  public CookieOpMessage(Conversation paramConversation, int paramInt1, int paramInt2, URL paramURL, String paramString)
    throws IllegalArgumentException
  {
    super(45, paramConversation, paramInt1);
    if ((paramInt2 != 1) && (paramInt2 != 2))
      throw new IllegalArgumentException("Illegal operationKind");
    if (paramURL == null)
      throw new IllegalArgumentException("Null URL");
    this.operationKind = paramInt2;
    this.url = paramURL;
    this.cookie = paramString;
  }

  public int getOperationKind()
  {
    return this.operationKind;
  }

  public URL getURL()
  {
    return this.url;
  }

  public String getCookie()
  {
    return this.cookie;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.operationKind);
    URLHelper.write(paramSerializer, this.url);
    paramSerializer.writeUTF(this.cookie);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.operationKind = paramSerializer.readInt();
    this.url = URLHelper.read(paramSerializer);
    this.cookie = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.CookieOpMessage
 * JD-Core Version:    0.6.2
 */