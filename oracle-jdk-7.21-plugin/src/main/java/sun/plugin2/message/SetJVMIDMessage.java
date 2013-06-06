package sun.plugin2.message;

import java.io.IOException;

public class SetJVMIDMessage extends PluginMessage
{
  public static final int ID = 1;
  private int jvmID;
  private int browserType;
  private boolean separateJVM;
  private String[][] properties;
  private String userHome;
  private boolean htmlJavaArgs;

  public SetJVMIDMessage(Conversation paramConversation)
  {
    super(1, paramConversation);
  }

  public SetJVMIDMessage(Conversation paramConversation, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString, String[][] paramArrayOfString)
  {
    super(1, paramConversation);
    this.jvmID = paramInt1;
    this.browserType = paramInt2;
    this.separateJVM = paramBoolean1;
    this.htmlJavaArgs = paramBoolean2;
    this.userHome = paramString;
    this.properties = new String[paramArrayOfString.length][];
    for (int i = 0; i < paramArrayOfString.length; i++)
      this.properties[i] = ((String[])(String[])paramArrayOfString[i].clone());
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeInt(this.jvmID);
    paramSerializer.writeInt(this.browserType);
    paramSerializer.writeBoolean(this.separateJVM);
    paramSerializer.writeBoolean(this.htmlJavaArgs);
    paramSerializer.writeUTF(this.userHome);
    paramSerializer.writeInt(this.properties.length);
    for (int i = 0; i < this.properties.length; i++)
      paramSerializer.writeUTFArray(this.properties[i]);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.jvmID = paramSerializer.readInt();
    this.browserType = paramSerializer.readInt();
    this.separateJVM = paramSerializer.readBoolean();
    this.htmlJavaArgs = paramSerializer.readBoolean();
    this.userHome = paramSerializer.readUTF();
    int i = paramSerializer.readInt();
    this.properties = new String[i][];
    for (int j = 0; j < this.properties.length; j++)
      this.properties[j] = paramSerializer.readUTFArray();
  }

  public int getJVMID()
  {
    return this.jvmID;
  }

  public int getBrowserType()
  {
    return this.browserType;
  }

  public boolean isSeparateJVM()
  {
    return this.separateJVM;
  }

  public boolean isHtmlJavaArgs()
  {
    return this.htmlJavaArgs;
  }

  public String getUserHome()
  {
    return this.userHome;
  }

  public String[][] getParameters()
  {
    return this.properties;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.SetJVMIDMessage
 * JD-Core Version:    0.6.2
 */