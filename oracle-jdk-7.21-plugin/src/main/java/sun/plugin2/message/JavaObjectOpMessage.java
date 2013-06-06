package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;
import sun.plugin2.liveconnect.RemoteJavaObject;

public class JavaObjectOpMessage extends AppletMessage
{
  public static final int ID = 33;
  private RemoteJavaObject object;
  private String memberName;
  private int operationKind;
  private Object[] args;
  private int resultID;
  public static final int CALL_METHOD = 1;
  public static final int GET_FIELD = 2;
  public static final int SET_FIELD = 3;
  public static final int HAS_FIELD = 4;
  public static final int HAS_METHOD = 5;
  public static final int HAS_FIELD_OR_METHOD = 6;

  public JavaObjectOpMessage(Conversation paramConversation)
  {
    super(33, paramConversation);
  }

  public JavaObjectOpMessage(Conversation paramConversation, RemoteJavaObject paramRemoteJavaObject, String paramString, int paramInt1, Object[] paramArrayOfObject, int paramInt2)
  {
    super(33, paramConversation, paramRemoteJavaObject.getAppletID());
    this.object = paramRemoteJavaObject;
    this.memberName = paramString;
    this.operationKind = paramInt1;
    this.args = paramArrayOfObject;
    this.resultID = paramInt2;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    RemoteJavaObject.write(paramSerializer, this.object);
    paramSerializer.writeUTF(this.memberName);
    paramSerializer.writeInt(this.operationKind);
    ArgumentHelper.writeObjectArray(paramSerializer, this.args);
    paramSerializer.writeInt(this.resultID);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.object = RemoteJavaObject.read(paramSerializer);
    this.memberName = paramSerializer.readUTF();
    this.operationKind = paramSerializer.readInt();
    this.args = ArgumentHelper.readObjectArray(paramSerializer);
    this.resultID = paramSerializer.readInt();
  }

  public RemoteJavaObject getObject()
  {
    return this.object;
  }

  public String getMemberName()
  {
    return this.memberName;
  }

  public int getOperationKind()
  {
    return this.operationKind;
  }

  public Object[] getArguments()
  {
    return this.args;
  }

  public int getResultID()
  {
    return this.resultID;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaObjectOpMessage
 * JD-Core Version:    0.6.2
 */