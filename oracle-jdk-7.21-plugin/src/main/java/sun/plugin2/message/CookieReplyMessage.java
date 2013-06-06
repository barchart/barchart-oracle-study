package sun.plugin2.message;

import java.io.IOException;

public class CookieReplyMessage extends PluginMessage
{
  public static final int ID = 46;
  private String cookie;
  private String exceptionMessage;

  public CookieReplyMessage(Conversation paramConversation)
  {
    super(46, paramConversation);
  }

  public CookieReplyMessage(Conversation paramConversation, String paramString1, String paramString2)
  {
    this(paramConversation);
    this.cookie = paramString1;
    this.exceptionMessage = paramString2;
  }

  public String getCookie()
  {
    return this.cookie;
  }

  public String getExceptionMessage()
  {
    return this.exceptionMessage;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeUTF(this.cookie);
    paramSerializer.writeUTF(this.exceptionMessage);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.cookie = paramSerializer.readUTF();
    this.exceptionMessage = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.CookieReplyMessage
 * JD-Core Version:    0.6.2
 */