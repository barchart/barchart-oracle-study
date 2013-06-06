package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class TextEventMessage extends EventMessage
{
  public static final int ID = 86;
  private String text;

  public TextEventMessage(Conversation paramConversation)
  {
    super(86, paramConversation);
  }

  public TextEventMessage(Conversation paramConversation, int paramInt, String paramString)
  {
    super(86, paramConversation, paramInt);
    this.text = paramString;
  }

  public String getText()
  {
    return this.text;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.text);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.text = paramSerializer.readUTF();
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(14));
    paramMap.put("text", this.text);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.TextEventMessage
 * JD-Core Version:    0.6.2
 */