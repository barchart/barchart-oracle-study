package sun.plugin2.message;

import java.io.IOException;

public abstract class Message
{
  private int id;
  private Conversation conversation;

  public Message(int paramInt, Conversation paramConversation)
  {
    this.id = paramInt;
    this.conversation = paramConversation;
  }

  public int hashCode()
  {
    int i = 0;
    if (this.conversation != null)
      i = 17 * this.conversation.hashCode();
    i += this.id;
    return i;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (getClass() != paramObject.getClass()))
      return false;
    Message localMessage = (Message)paramObject;
    if ((this.conversation == null) && (localMessage.conversation != null))
      return false;
    return (this.id == localMessage.id) && ((this.conversation == null) || (this.conversation.equals(localMessage.conversation)));
  }

  public int getID()
  {
    return this.id;
  }

  public Conversation getConversation()
  {
    return this.conversation;
  }

  public abstract void writeFields(Serializer paramSerializer)
    throws IOException;

  public abstract void readFields(Serializer paramSerializer)
    throws IOException;
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.Message
 * JD-Core Version:    0.6.2
 */