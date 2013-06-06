package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptMemberOpMessage extends JavaScriptBaseMessage
{
  public static final int ID = 24;
  public static final int GET = 1;
  public static final int SET = 2;
  public static final int REMOVE = 3;
  private String memberName;
  private int operationKind;
  private Object arg;

  public JavaScriptMemberOpMessage(Conversation paramConversation)
  {
    super(24, paramConversation);
  }

  public JavaScriptMemberOpMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt1, String paramString, int paramInt2, Object paramObject)
  {
    super(24, paramConversation, paramBrowserSideObject, paramInt1);
    this.memberName = paramString;
    this.operationKind = paramInt2;
    this.arg = paramObject;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.memberName);
    paramSerializer.writeByte((byte)this.operationKind);
    ArgumentHelper.writeObject(paramSerializer, this.arg);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.memberName = paramSerializer.readUTF();
    this.operationKind = (paramSerializer.readByte() & 0xFF);
    this.arg = ArgumentHelper.readObject(paramSerializer);
  }

  public String getMemberName()
  {
    return this.memberName;
  }

  public int getOperationKind()
  {
    return this.operationKind;
  }

  public Object getArgument()
  {
    return this.arg;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaScriptMemberOpMessage
 * JD-Core Version:    0.6.2
 */