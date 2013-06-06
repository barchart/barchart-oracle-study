package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.liveconnect.ArgumentHelper;

public class JavaReplyMessage extends PluginMessage
{
  public static final int ID = 34;
  private int resultID;
  private Object result;
  private boolean resultIsVoid;
  private String exceptionMessage;

  public JavaReplyMessage(Conversation paramConversation)
  {
    super(34, paramConversation);
  }

  public JavaReplyMessage(Conversation paramConversation, int paramInt, Object paramObject, boolean paramBoolean, String paramString)
    throws IllegalArgumentException
  {
    this(paramConversation);
    if ((paramString != null) && (paramObject != null))
      throw new IllegalArgumentException("If the exception message is non-null, the result should be null");
    this.resultID = paramInt;
    this.result = paramObject;
    this.resultIsVoid = paramBoolean;
    this.exceptionMessage = paramString;
  }

  public int getResultID()
  {
    return this.resultID;
  }

  public Object getResult()
  {
    return this.result;
  }

  public boolean isResultVoid()
  {
    return this.resultIsVoid;
  }

  public String getExceptionMessage()
  {
    return this.exceptionMessage;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.resultID);
    ArgumentHelper.writeObject(paramSerializer, this.result);
    paramSerializer.writeBoolean(this.resultIsVoid);
    paramSerializer.writeUTF(this.exceptionMessage);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.resultID = paramSerializer.readInt();
    this.result = ArgumentHelper.readObject(paramSerializer);
    this.resultIsVoid = paramSerializer.readBoolean();
    this.exceptionMessage = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.JavaReplyMessage
 * JD-Core Version:    0.6.2
 */