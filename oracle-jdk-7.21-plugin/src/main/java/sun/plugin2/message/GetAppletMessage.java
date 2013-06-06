package sun.plugin2.message;

import java.io.IOException;

public class GetAppletMessage extends AppletMessage
{
  public static final int ID = 31;
  private int resultID;

  public GetAppletMessage(Conversation paramConversation)
  {
    super(31, paramConversation);
  }

  public GetAppletMessage(Conversation paramConversation, int paramInt1, int paramInt2)
  {
    super(31, paramConversation, paramInt1);
    this.resultID = paramInt2;
  }

  public int getResultID()
  {
    return this.resultID;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.resultID);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.resultID = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.GetAppletMessage
 * JD-Core Version:    0.6.2
 */