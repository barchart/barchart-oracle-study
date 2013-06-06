package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class FocusTransitionEventMessage extends EventMessage
{
  public static final int ID = 82;
  private boolean focused;

  public FocusTransitionEventMessage(Conversation paramConversation)
  {
    super(82, paramConversation);
  }

  public FocusTransitionEventMessage(Conversation paramConversation, int paramInt, boolean paramBoolean)
  {
    super(82, paramConversation, paramInt);
    this.focused = paramBoolean;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeBoolean(this.focused);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.focused = paramSerializer.readBoolean();
  }

  public boolean getFocused()
  {
    return this.focused;
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(11));
    paramMap.put("hasFocus", Boolean.valueOf(this.focused));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.FocusTransitionEventMessage
 * JD-Core Version:    0.6.2
 */