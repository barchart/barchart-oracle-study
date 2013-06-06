package sun.plugin2.message;

import java.io.IOException;

public class CustomSecurityManagerRequestMessage extends PluginMessage
{
  public static final int ID = 90;
  private int appletID;

  public CustomSecurityManagerRequestMessage(Conversation paramConversation)
  {
    super(90, paramConversation);
  }

  public CustomSecurityManagerRequestMessage(Conversation paramConversation, int paramInt)
  {
    super(90, paramConversation);
    this.appletID = paramInt;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.appletID);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.appletID = paramSerializer.readInt();
  }

  public int getAppletID()
  {
    return this.appletID;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.CustomSecurityManagerRequestMessage
 * JD-Core Version:    0.6.2
 */