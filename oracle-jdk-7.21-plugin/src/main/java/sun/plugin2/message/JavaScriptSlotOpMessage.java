package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.BrowserSideObject;

public class JavaScriptSlotOpMessage extends JavaScriptBaseMessage
{
  public static final int ID = 25;
  public static final int GET = 1;
  public static final int SET = 2;
  private int slot;
  private int operationKind;
  private Object arg;

  public JavaScriptSlotOpMessage(Conversation paramConversation)
  {
    super(25, paramConversation);
  }

  public JavaScriptSlotOpMessage(Conversation paramConversation, BrowserSideObject paramBrowserSideObject, int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    super(25, paramConversation, paramBrowserSideObject, paramInt1);
    this.slot = paramInt2;
    this.operationKind = paramInt3;
    this.arg = paramObject;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.slot);
    paramSerializer.writeByte((byte)this.operationKind);
    ArgumentHelper.writeObject(paramSerializer, this.arg);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.slot = paramSerializer.readInt();
    this.operationKind = (paramSerializer.readByte() & 0xFF);
    this.arg = ArgumentHelper.readObject(paramSerializer);
  }

  public int getSlot()
  {
    return this.slot;
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
 * Qualified Name:     sun.plugin2.message.JavaScriptSlotOpMessage
 * JD-Core Version:    0.6.2
 */