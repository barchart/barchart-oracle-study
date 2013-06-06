package sun.plugin2.message;

import java.io.IOException;

public class StopAppletMessage extends AppletMessage
{
  public static final int ID = 12;

  public StopAppletMessage(Conversation paramConversation)
  {
    super(12, paramConversation);
  }

  public StopAppletMessage(int paramInt, Conversation paramConversation)
  {
    super(paramInt, paramConversation);
  }

  public StopAppletMessage(Conversation paramConversation, int paramInt)
  {
    super(12, paramConversation, paramInt);
  }

  public StopAppletMessage(int paramInt1, Conversation paramConversation, int paramInt2)
  {
    super(paramInt1, paramConversation, paramInt2);
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.StopAppletMessage
 * JD-Core Version:    0.6.2
 */