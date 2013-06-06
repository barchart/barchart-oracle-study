package sun.plugin2.message;

import java.io.IOException;

public class CustomSecurityManagerAckMessage extends PluginMessage
{
  public static final int ID = 91;
  boolean allowed = false;

  public CustomSecurityManagerAckMessage(Conversation paramConversation)
  {
    super(91, paramConversation);
  }

  public CustomSecurityManagerAckMessage(Conversation paramConversation, boolean paramBoolean)
  {
    super(91, paramConversation);
    this.allowed = paramBoolean;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeBoolean(this.allowed);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.allowed = paramSerializer.readBoolean();
  }

  public boolean isAllowed()
  {
    return this.allowed;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.CustomSecurityManagerAckMessage
 * JD-Core Version:    0.6.2
 */