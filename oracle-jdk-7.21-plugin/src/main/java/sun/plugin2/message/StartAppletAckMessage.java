package sun.plugin2.message;

import java.io.IOException;

public class StartAppletAckMessage extends AppletMessage
{
  public static final int ID = 4;
  public static final int STATUS_IGNORE = 0;
  public static final int STATUS_LOADED = 1;
  public static final int STATUS_READY = 2;
  public static final int STATUS_ERROR = 3;
  public static final int STATUS_DONE = 4;
  private int status;

  public StartAppletAckMessage(Conversation paramConversation)
  {
    super(4, paramConversation);
  }

  public StartAppletAckMessage(Conversation paramConversation, int paramInt1, int paramInt2)
  {
    super(4, paramConversation, paramInt1);
    this.status = paramInt2;
  }

  public int getStatus()
  {
    return this.status;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.status);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.status = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.StartAppletAckMessage
 * JD-Core Version:    0.6.2
 */