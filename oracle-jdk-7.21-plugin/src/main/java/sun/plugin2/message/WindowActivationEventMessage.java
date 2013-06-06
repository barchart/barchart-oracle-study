package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class WindowActivationEventMessage extends EventMessage
{
  public static final int ID = 7;
  private boolean active;

  public WindowActivationEventMessage(Conversation paramConversation)
  {
    super(7, paramConversation);
  }

  public WindowActivationEventMessage(Conversation paramConversation, int paramInt, boolean paramBoolean)
  {
    super(7, paramConversation, paramInt);
    this.active = paramBoolean;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeBoolean(this.active);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.active = paramSerializer.readBoolean();
  }

  public boolean getActive()
  {
    return this.active;
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(12));
    paramMap.put("hasFocus", Boolean.valueOf(this.active));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.WindowActivationEventMessage
 * JD-Core Version:    0.6.2
 */