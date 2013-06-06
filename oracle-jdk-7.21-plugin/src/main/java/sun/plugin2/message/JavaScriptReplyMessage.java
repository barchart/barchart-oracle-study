package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;

public class JavaScriptReplyMessage extends PluginMessage
{
  public static final int ID = 27;
  private Object result;
  private String exceptionMessage;

  public JavaScriptReplyMessage(Conversation paramConversation)
  {
    super(27, paramConversation);
  }

  public JavaScriptReplyMessage(Conversation paramConversation, Object paramObject, String paramString)
    throws IllegalArgumentException
  {
    this(paramConversation);
    if ((paramString != null) && (paramObject != null))
      throw new IllegalArgumentException("If the exception message is non-null, the result should be null");
    this.result = paramObject;
    this.exceptionMessage = paramString;
  }

  public Object getResult()
  {
    return this.result;
  }

  public String getExceptionMessage()
  {
    return this.exceptionMessage;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    ArgumentHelper.writeObject(paramSerializer, this.result);
    paramSerializer.writeUTF(this.exceptionMessage);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.result = ArgumentHelper.readObject(paramSerializer);
    this.exceptionMessage = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptReplyMessage
 * JD-Core Version:    0.6.2
 */