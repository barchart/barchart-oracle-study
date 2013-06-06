package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptEvalMessage extends JavaScriptBaseMessage
{
  public static final int ID = 23;
  private String code;

  public JavaScriptEvalMessage(Conversation paramConversation)
  {
    super(23, paramConversation);
  }

  public JavaScriptEvalMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt, String paramString)
  {
    super(23, paramConversation, paramBrowserSideObject, paramInt);
    this.code = paramString;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.code);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.code = paramSerializer.readUTF();
  }

  public String getCode()
  {
    return this.code;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptEvalMessage
 * JD-Core Version:    0.6.2
 */