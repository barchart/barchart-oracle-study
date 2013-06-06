package sun.plugin2.message;

import java.io.IOException;

public class BestJREAvailableMessage extends PluginMessage
{
  public static final int ID = 17;
  public static final int ASK = 1;
  public static final int REPLY = 2;
  private int kind;
  private String javaVersion;
  private String jfxVersion;

  public BestJREAvailableMessage(Conversation paramConversation)
  {
    super(17, paramConversation);
  }

  public BestJREAvailableMessage(Conversation paramConversation, int paramInt, String paramString)
  {
    this(paramConversation, paramInt, paramString, null);
  }

  public BestJREAvailableMessage(Conversation paramConversation, int paramInt, String paramString1, String paramString2)
  {
    this(paramConversation);
    this.kind = paramInt;
    this.javaVersion = paramString1;
    this.jfxVersion = paramString2;
  }

  public boolean isReply()
  {
    return this.kind == 2;
  }

  public String getJavaVersion()
  {
    return this.javaVersion;
  }

  public String getJfxVersion()
  {
    return this.jfxVersion;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.kind);
    paramSerializer.writeUTF(this.javaVersion);
    paramSerializer.writeUTF(this.jfxVersion);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.kind = paramSerializer.readInt();
    this.javaVersion = paramSerializer.readUTF();
    this.jfxVersion = paramSerializer.readUTF();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.BestJREAvailableMessage
 * JD-Core Version:    0.6.2
 */