package sun.plugin2.message;

import java.io.IOException;

public class MarkTaintedMessage extends PluginMessage
{
  public static final int ID = 16;

  public MarkTaintedMessage(Conversation paramConversation)
  {
    super(16, paramConversation);
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.MarkTaintedMessage
 * JD-Core Version:    0.6.2
 */