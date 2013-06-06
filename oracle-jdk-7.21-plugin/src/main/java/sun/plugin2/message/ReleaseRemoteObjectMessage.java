package sun.plugin2.message;

import java.io.IOException;

public class ReleaseRemoteObjectMessage extends PluginMessage
{
  public static final int ID = 35;
  private int objectID;

  public ReleaseRemoteObjectMessage(Conversation paramConversation)
  {
    super(35, paramConversation);
  }

  public ReleaseRemoteObjectMessage(Conversation paramConversation, int paramInt)
  {
    this(paramConversation);
    this.objectID = paramInt;
  }

  public int getObjectID()
  {
    return this.objectID;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.objectID);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.objectID = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ReleaseRemoteObjectMessage
 * JD-Core Version:    0.6.2
 */