package sun.plugin2.message;

import java.io.IOException;

public class ShowStatusMessage extends AppletMessage
{
  public static final int ID = 52;
  private String status;

  public ShowStatusMessage(Conversation paramConversation)
  {
    super(52, paramConversation);
  }

  public ShowStatusMessage(Conversation paramConversation, int paramInt, String paramString)
  {
    super(52, paramConversation, paramInt);
    this.status = paramString;
  }

  public String getStatus()
  {
    return this.status;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.status);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.status = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ShowStatusMessage
 * JD-Core Version:    0.6.2
 */