package sun.plugin2.message;

import java.io.IOException;
import java.net.PasswordAuthentication;

public class GetAuthenticationReplyMessage extends PluginMessage
{
  public static final int ID = 44;
  private String username;
  private char[] password;
  private String errorMessage;

  public GetAuthenticationReplyMessage(Conversation paramConversation)
  {
    super(44, paramConversation);
  }

  public GetAuthenticationReplyMessage(Conversation paramConversation, PasswordAuthentication paramPasswordAuthentication, String paramString)
  {
    this(paramConversation);
    if (paramPasswordAuthentication != null)
    {
      this.username = paramPasswordAuthentication.getUserName();
      this.password = paramPasswordAuthentication.getPassword();
    }
    this.errorMessage = paramString;
  }

  public PasswordAuthentication getAuthentication()
  {
    if (this.username == null)
      return null;
    return new PasswordAuthentication(this.username, this.password);
  }

  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeUTF(this.username);
    paramSerializer.writeCharArray(this.password);
    paramSerializer.writeUTF(this.errorMessage);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.username = paramSerializer.readUTF();
    this.password = paramSerializer.readCharArray();
    this.errorMessage = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.GetAuthenticationReplyMessage
 * JD-Core Version:    0.6.2
 */