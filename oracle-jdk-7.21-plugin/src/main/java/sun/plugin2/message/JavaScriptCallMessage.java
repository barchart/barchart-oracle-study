package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptCallMessage extends JavaScriptBaseMessage
{
  public static final int ID = 22;
  private String methodName;
  private Object[] args;

  public JavaScriptCallMessage(Conversation paramConversation)
  {
    super(22, paramConversation);
  }

  public JavaScriptCallMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt, String paramString, Object[] paramArrayOfObject)
  {
    super(22, paramConversation, paramBrowserSideObject, paramInt);
    this.methodName = paramString;
    this.args = paramArrayOfObject;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.methodName);
    ArgumentHelper.writeObjectArray(paramSerializer, this.args);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.methodName = paramSerializer.readUTF();
    this.args = ArgumentHelper.readObjectArray(paramSerializer);
  }

  public String getMethodName()
  {
    return this.methodName;
  }

  public Object[] getArguments()
  {
    return this.args;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptCallMessage
 * JD-Core Version:    0.6.2
 */