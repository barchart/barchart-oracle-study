package sun.plugin2.message;

import java.io.IOException;

public class RemoteCAContextIdMessage extends AppletMessage
{
  public static final int ID = 81;
  private int contextId;

  public RemoteCAContextIdMessage(Conversation paramConversation)
  {
    super(81, paramConversation);
  }

  public RemoteCAContextIdMessage(Conversation paramConversation, int paramInt1, int paramInt2)
  {
    super(81, paramConversation, paramInt1);
    this.contextId = paramInt2;
  }

  public int getContextId()
  {
    return this.contextId;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.contextId);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.contextId = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.RemoteCAContextIdMessage
 * JD-Core Version:    0.6.2
 */