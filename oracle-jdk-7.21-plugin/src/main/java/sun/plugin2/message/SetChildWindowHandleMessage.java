package sun.plugin2.message;

import java.io.IOException;

public class SetChildWindowHandleMessage extends AppletMessage
{
  public static final int ID = 6;
  private long windowHandle;

  public SetChildWindowHandleMessage(Conversation paramConversation)
  {
    super(6, paramConversation);
  }

  public SetChildWindowHandleMessage(Conversation paramConversation, int paramInt, long paramLong)
  {
    super(6, paramConversation, paramInt);
    this.windowHandle = paramLong;
  }

  public long getWindowHandle()
  {
    return this.windowHandle;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeLong(this.windowHandle);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.windowHandle = paramSerializer.readLong();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.SetChildWindowHandleMessage
 * JD-Core Version:    0.6.2
 */