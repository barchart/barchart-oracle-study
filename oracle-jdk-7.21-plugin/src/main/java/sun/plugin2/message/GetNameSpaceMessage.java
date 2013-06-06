package sun.plugin2.message;

import java.io.IOException;

public class GetNameSpaceMessage extends AppletMessage
{
  public static final int ID = 32;
  private String nameSpace;
  private int resultID;

  public GetNameSpaceMessage(Conversation paramConversation)
  {
    super(32, paramConversation);
  }

  public GetNameSpaceMessage(Conversation paramConversation, int paramInt1, String paramString, int paramInt2)
  {
    super(32, paramConversation, paramInt1);
    this.nameSpace = paramString;
    this.resultID = paramInt2;
  }

  public String getNameSpace()
  {
    return this.nameSpace;
  }

  public int getResultID()
  {
    return this.resultID;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.nameSpace);
    paramSerializer.writeInt(this.resultID);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.nameSpace = paramSerializer.readUTF();
    this.resultID = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.GetNameSpaceMessage
 * JD-Core Version:    0.6.2
 */