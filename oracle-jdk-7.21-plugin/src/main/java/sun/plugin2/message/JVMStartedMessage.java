package sun.plugin2.message;

import java.io.IOException;

public class JVMStartedMessage extends PluginMessage
{
  public static final int ID = 2;

  public JVMStartedMessage(Conversation paramConversation)
  {
    super(2, paramConversation);
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
 * Qualified Name:     sun.plugin2.message.JVMStartedMessage
 * JD-Core Version:    0.6.2
 */