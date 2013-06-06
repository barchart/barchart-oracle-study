package sun.plugin2.message;

import java.io.IOException;

public class SetAppletSizeMessage extends AppletMessage
{
  public static final int ID = 5;
  private int width;
  private int height;

  public SetAppletSizeMessage(Conversation paramConversation)
  {
    super(5, paramConversation);
  }

  public SetAppletSizeMessage(Conversation paramConversation, int paramInt1, int paramInt2, int paramInt3)
  {
    super(5, paramConversation, paramInt1);
    this.width = paramInt2;
    this.height = paramInt3;
  }

  public int getWidth()
  {
    return this.width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.width);
    paramSerializer.writeInt(this.height);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.width = paramSerializer.readInt();
    this.height = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.SetAppletSizeMessage
 * JD-Core Version:    0.6.2
 */