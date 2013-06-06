package sun.plugin2.message;

import java.io.IOException;

public class ShutdownJVMMessage extends PluginMessage
{
  public static final int ID = 14;

  public ShutdownJVMMessage(Conversation paramConversation)
  {
    super(14, paramConversation);
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
 * Qualified Name:     sun.plugin2.message.ShutdownJVMMessage
 * JD-Core Version:    0.6.2
 */