package sun.plugin2.message;

import java.io.IOException;

public class JavaScriptGetWindowMessage extends AppletMessage
{
  public static final int ID = 21;

  public JavaScriptGetWindowMessage(Conversation paramConversation)
  {
    super(21, paramConversation);
  }

  public JavaScriptGetWindowMessage(Conversation paramConversation, int paramInt)
  {
    super(21, paramConversation, paramInt);
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
 * Qualified Name:     sun.plugin2.message.JavaScriptGetWindowMessage
 * JD-Core Version:    0.6.2
 */