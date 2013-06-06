package sun.plugin2.message;

import java.io.IOException;

public abstract class AppletMessage extends PluginMessage
{
  private int appletID;

  public AppletMessage(int paramInt, Conversation paramConversation)
  {
    super(paramInt, paramConversation);
  }

  public AppletMessage(int paramInt1, Conversation paramConversation, int paramInt2)
  {
    super(paramInt1, paramConversation);
    this.appletID = paramInt2;
  }

  public int getAppletID()
  {
    return this.appletID;
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
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.AppletMessage
 * JD-Core Version:    0.6.2
 */