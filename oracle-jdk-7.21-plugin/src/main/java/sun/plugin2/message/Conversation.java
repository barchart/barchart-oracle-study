package sun.plugin2.message;

import java.io.IOException;

public class Conversation
{
  private boolean initiatingSide;
  private int id;

  public Conversation()
  {
  }

  public Conversation(boolean paramBoolean, int paramInt)
  {
    this.initiatingSide = paramBoolean;
    this.id = paramInt;
  }

  public int hashCode()
  {
    return this.id;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (getClass() != paramObject.getClass()))
      return false;
    Conversation localConversation = (Conversation)paramObject;
    return (localConversation.initiatingSide == this.initiatingSide) && (localConversation.id == this.id);
  }

  public boolean isInitiatingSide()
  {
    return this.initiatingSide;
  }

  public int getID()
  {
    return this.id;
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.initiatingSide = paramSerializer.readBoolean();
    this.id = paramSerializer.readInt();
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeBoolean(this.initiatingSide);
    paramSerializer.writeInt(this.id);
  }

  public String toString()
  {
    return "[Conversation: id=" + this.id + ", initiatingSide=" + this.initiatingSide + "]";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.Conversation
 * JD-Core Version:    0.6.2
 */