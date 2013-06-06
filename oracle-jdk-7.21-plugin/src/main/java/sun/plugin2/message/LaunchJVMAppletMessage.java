package sun.plugin2.message;

import java.io.IOException;
import java.util.Arrays;

public class LaunchJVMAppletMessage extends AppletMessage
{
  public static final int ID = 18;
  private String javaHome;
  private long launchTime;
  private String[] additionalArgs;
  private byte[] processInputBytes;
  private byte[] processErrorBytes;
  private boolean processStarted;
  private boolean processExited;
  private int processExitCode;
  private boolean killProcess;

  public LaunchJVMAppletMessage(Conversation paramConversation)
  {
    super(18, paramConversation);
  }

  public LaunchJVMAppletMessage(Conversation paramConversation, int paramInt, String paramString, long paramLong, String[] paramArrayOfString)
  {
    super(18, paramConversation, paramInt);
    this.javaHome = paramString;
    this.launchTime = paramLong;
    this.additionalArgs = paramArrayOfString;
  }

  public String getJavaHome()
  {
    return this.javaHome;
  }

  public void setJavaHome(String paramString)
  {
    this.javaHome = paramString;
  }

  public byte[] getProcessInputBytes()
  {
    return this.processInputBytes;
  }

  public long getLaunchTime()
  {
    return this.launchTime;
  }

  public void setLaunchTime(long paramLong)
  {
    this.launchTime = paramLong;
  }

  public String[] getAdditionalArgs()
  {
    return this.additionalArgs;
  }

  public void setTransportArgs(String[] paramArrayOfString)
  {
    this.additionalArgs = paramArrayOfString;
  }

  public boolean isKillProcess()
  {
    return this.killProcess;
  }

  public void setKillProcess(boolean paramBoolean)
  {
    this.killProcess = paramBoolean;
  }

  public void setProcessInputBytes(byte[] paramArrayOfByte)
  {
    this.processInputBytes = paramArrayOfByte;
  }

  public byte[] getProcessErrorBytes()
  {
    return this.processErrorBytes;
  }

  public void setProcessErrorBytes(byte[] paramArrayOfByte)
  {
    this.processErrorBytes = paramArrayOfByte;
  }

  public boolean isProcessStarted()
  {
    return this.processStarted;
  }

  public void setProcessStarted(boolean paramBoolean)
  {
    this.processStarted = paramBoolean;
  }

  public boolean isProcessExited()
  {
    return this.processExited;
  }

  public void setProcessExited(boolean paramBoolean)
  {
    this.processExited = paramBoolean;
  }

  public int getProcessExitCode()
  {
    return this.processExitCode;
  }

  public void setProcessExitCode(int paramInt)
  {
    this.processExitCode = paramInt;
  }

  public void setDoKill(boolean paramBoolean)
  {
    this.killProcess = paramBoolean;
  }

  public boolean isDoKill()
  {
    return this.killProcess;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeUTF(this.javaHome);
    paramSerializer.writeLong(this.launchTime);
    paramSerializer.writeUTFArray(this.additionalArgs);
    paramSerializer.writeByteArray(this.processInputBytes);
    paramSerializer.writeByteArray(this.processErrorBytes);
    paramSerializer.writeBoolean(this.processStarted);
    paramSerializer.writeBoolean(this.processExited);
    paramSerializer.writeInt(this.processExitCode);
    paramSerializer.writeBoolean(this.killProcess);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.javaHome = paramSerializer.readUTF();
    this.launchTime = paramSerializer.readLong();
    this.additionalArgs = paramSerializer.readUTFArray();
    this.processInputBytes = paramSerializer.readByteArray();
    this.processErrorBytes = paramSerializer.readByteArray();
    this.processStarted = paramSerializer.readBoolean();
    this.processExited = paramSerializer.readBoolean();
    this.processExitCode = paramSerializer.readInt();
    this.killProcess = paramSerializer.readBoolean();
  }

  public String toString()
  {
    return "LaunchJVMAppletMessage{appletID=" + getAppletID() + ", conversationID=" + getConversation().getID() + ", javaHome=" + this.javaHome + ", launchTime=" + this.launchTime + ", sendKill=" + this.killProcess + ", " + Arrays.asList(this.additionalArgs) + '}';
  }

  public String toClientString()
  {
    return "LaunchJVMAppletMessage{appletID=" + getAppletID() + ", conversationID=" + getConversation().getID() + ", process started=" + this.processStarted + ", process exited=" + this.processExited + ", process exitedCode=" + this.processExitCode + '}';
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.LaunchJVMAppletMessage
 * JD-Core Version:    0.6.2
 */