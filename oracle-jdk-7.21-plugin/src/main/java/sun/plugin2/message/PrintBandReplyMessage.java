package sun.plugin2.message;

import java.io.IOException;

public class PrintBandReplyMessage extends AppletMessage
{
  public static final int ID = 11;
  private int destY;
  private boolean res = false;

  public PrintBandReplyMessage(Conversation paramConversation)
  {
    super(11, paramConversation);
  }

  public PrintBandReplyMessage(Conversation paramConversation, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(11, paramConversation, paramInt1);
    this.destY = paramInt2;
    this.res = paramBoolean;
  }

  public int getDestY()
  {
    return this.destY;
  }

  public boolean getRes()
  {
    return this.res;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.destY);
    paramSerializer.writeBoolean(this.res);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.destY = paramSerializer.readInt();
    this.res = paramSerializer.readBoolean();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.PrintBandReplyMessage
 * JD-Core Version:    0.6.2
 */