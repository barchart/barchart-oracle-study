package sun.plugin2.message;

import java.io.IOException;

public class ShowDocumentMessage extends AppletMessage
{
  public static final int ID = 51;
  private String url;
  private String target;

  public ShowDocumentMessage(Conversation paramConversation)
  {
    super(51, paramConversation);
  }

  public ShowDocumentMessage(Conversation paramConversation, int paramInt, String paramString1, String paramString2)
  {
    super(51, paramConversation, paramInt);
    this.url = paramString1;
    this.target = paramString2;
  }

  public String getURL()
  {
    return this.url;
  }

  public String getTarget()
  {
    return this.target;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.url);
    paramSerializer.writeUTF(this.target);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.url = paramSerializer.readUTF();
    this.target = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ShowDocumentMessage
 * JD-Core Version:    0.6.2
 */