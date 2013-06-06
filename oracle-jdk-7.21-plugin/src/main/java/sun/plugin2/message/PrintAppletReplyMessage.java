package sun.plugin2.message;

import java.io.IOException;

public class PrintAppletReplyMessage extends AppletMessage
{
  public static final int ID = 9;
  private boolean res = false;

  public PrintAppletReplyMessage(Conversation paramConversation)
  {
    super(9, paramConversation);
  }

  public PrintAppletReplyMessage(Conversation paramConversation, int paramInt, boolean paramBoolean)
  {
    super(9, paramConversation, paramInt);
    this.res = paramBoolean;
  }

  public boolean getRes()
  {
    return this.res;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeBoolean(this.res);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.res = paramSerializer.readBoolean();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.PrintAppletReplyMessage
 * JD-Core Version:    0.6.2
 */