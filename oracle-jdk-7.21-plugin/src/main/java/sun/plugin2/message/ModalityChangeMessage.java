package sun.plugin2.message;

import java.io.IOException;

public class ModalityChangeMessage extends AppletMessage
{
  public static final int ID = 61;
  private boolean modalityPushed;

  public ModalityChangeMessage(Conversation paramConversation)
  {
    super(61, paramConversation);
  }

  public ModalityChangeMessage(Conversation paramConversation, int paramInt, boolean paramBoolean)
  {
    super(61, paramConversation, paramInt);
    this.modalityPushed = paramBoolean;
  }

  public boolean getModalityPushed()
  {
    return this.modalityPushed;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeBoolean(this.modalityPushed);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.modalityPushed = paramSerializer.readBoolean();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ModalityChangeMessage
 * JD-Core Version:    0.6.2
 */