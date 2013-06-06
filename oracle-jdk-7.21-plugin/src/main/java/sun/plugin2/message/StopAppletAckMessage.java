package sun.plugin2.message;

public class StopAppletAckMessage extends StopAppletMessage
{
  public static final int ID = 13;

  public StopAppletAckMessage(Conversation paramConversation)
  {
    super(13, paramConversation);
  }

  public StopAppletAckMessage(Conversation paramConversation, int paramInt)
  {
    super(13, paramConversation, paramInt);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.StopAppletAckMessage
 * JD-Core Version:    0.6.2
 */