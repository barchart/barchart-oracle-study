package sun.plugin2.liveconnect;

import java.io.IOException;
import sun.plugin2.message.Serializer;

public class RemoteJavaObject
{
  private int jvmID;
  private int appletID;
  private int objectID;
  private boolean isApplet;

  public RemoteJavaObject(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    this.jvmID = paramInt1;
    this.appletID = paramInt2;
    this.objectID = paramInt3;
    this.isApplet = paramBoolean;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (getClass() != paramObject.getClass()))
      return false;
    RemoteJavaObject localRemoteJavaObject = (RemoteJavaObject)paramObject;
    return (this.jvmID == localRemoteJavaObject.jvmID) && (this.appletID == localRemoteJavaObject.appletID) && (this.objectID == localRemoteJavaObject.objectID) && (this.isApplet == localRemoteJavaObject.isApplet);
  }

  public int hashCode()
  {
    return this.jvmID ^ this.appletID ^ this.objectID;
  }

  public int getJVMID()
  {
    return this.jvmID;
  }

  public int getAppletID()
  {
    return this.appletID;
  }

  public int getObjectID()
  {
    return this.objectID;
  }

  public boolean isApplet()
  {
    return this.isApplet;
  }

  public static void write(Serializer paramSerializer, RemoteJavaObject paramRemoteJavaObject)
    throws IOException
  {
    if (paramRemoteJavaObject == null)
    {
      paramSerializer.writeBoolean(false);
    }
    else
    {
      paramSerializer.writeBoolean(true);
      paramSerializer.writeInt(paramRemoteJavaObject.getJVMID());
      paramSerializer.writeInt(paramRemoteJavaObject.getAppletID());
      paramSerializer.writeInt(paramRemoteJavaObject.getObjectID());
      paramSerializer.writeBoolean(paramRemoteJavaObject.isApplet());
    }
  }

  public static RemoteJavaObject read(Serializer paramSerializer)
    throws IOException
  {
    if (!paramSerializer.readBoolean())
      return null;
    return new RemoteJavaObject(paramSerializer.readInt(), paramSerializer.readInt(), paramSerializer.readInt(), paramSerializer.readBoolean());
  }

  public String toString()
  {
    return "[RemoteJavaObject jvmID=" + this.jvmID + " appletID=" + this.appletID + " objectID=" + this.objectID + " isApplet=" + this.isApplet + "]";
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.liveconnect.RemoteJavaObject
 * JD-Core Version:    0.6.2
 */