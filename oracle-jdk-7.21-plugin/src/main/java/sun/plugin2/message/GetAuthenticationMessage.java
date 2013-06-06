package sun.plugin2.message;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GetAuthenticationMessage extends AppletMessage
{
  public static final int ID = 43;
  private String protocol;
  private String host;
  private int port;
  private String scheme;
  private String realm;
  private String requestURL;
  private boolean proxyAuthentication;

  public GetAuthenticationMessage(Conversation paramConversation)
  {
    super(43, paramConversation);
  }

  public GetAuthenticationMessage(Conversation paramConversation, int paramInt1, String paramString1, String paramString2, int paramInt2, String paramString3, String paramString4, URL paramURL, boolean paramBoolean)
  {
    super(43, paramConversation, paramInt1);
    this.protocol = paramString1;
    this.host = paramString2;
    this.port = paramInt2;
    this.scheme = paramString3;
    this.realm = paramString4;
    this.requestURL = paramURL.toExternalForm();
    this.proxyAuthentication = paramBoolean;
  }

  public String getProtocol()
  {
    return this.protocol;
  }

  public String getHost()
  {
    return this.host;
  }

  public int getPort()
  {
    return this.port;
  }

  public String getScheme()
  {
    return this.scheme;
  }

  public String getRealm()
  {
    return this.realm;
  }

  public URL getRequestURL()
  {
    try
    {
      return new URL(this.requestURL);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localMalformedURLException.printStackTrace();
    }
    return null;
  }

  public boolean getProxyAuthentication()
  {
    return this.proxyAuthentication;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.protocol);
    paramSerializer.writeUTF(this.host);
    paramSerializer.writeInt(this.port);
    paramSerializer.writeUTF(this.scheme);
    paramSerializer.writeUTF(this.realm);
    paramSerializer.writeUTF(this.requestURL);
    paramSerializer.writeBoolean(this.proxyAuthentication);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.protocol = paramSerializer.readUTF();
    this.host = paramSerializer.readUTF();
    this.port = paramSerializer.readInt();
    this.scheme = paramSerializer.readUTF();
    this.realm = paramSerializer.readUTF();
    this.requestURL = paramSerializer.readUTF();
    this.proxyAuthentication = paramSerializer.readBoolean();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.GetAuthenticationMessage
 * JD-Core Version:    0.6.2
 */