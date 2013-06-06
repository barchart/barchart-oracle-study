package sun.plugin2.message;

import com.sun.applet2.AppletParameters;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.plugin2.main.server.JVMHealthData;

public class StartAppletMessage extends PluginMessage
{
  public static final int ID = 3;
  private String[] keys;
  private String[] values;
  private int appletID;
  private long parentNativeWindowHandle;
  private String caRenderServerName;
  private boolean useXEmbed;
  private String docbase;
  private boolean isForDummyApplet;
  private long appletLaunchTime;
  private JVMHealthData healthData;

  public StartAppletMessage(Conversation paramConversation)
  {
    super(3, paramConversation);
  }

  public StartAppletMessage(Conversation paramConversation, AppletParameters paramAppletParameters, long paramLong1, String paramString1, boolean paramBoolean1, int paramInt, String paramString2, boolean paramBoolean2, long paramLong2)
  {
    this(paramConversation);
    setParameters(paramAppletParameters);
    this.parentNativeWindowHandle = paramLong1;
    this.caRenderServerName = paramString1;
    this.useXEmbed = paramBoolean1;
    this.appletID = paramInt;
    this.docbase = paramString2;
    this.isForDummyApplet = paramBoolean2;
    this.appletLaunchTime = paramLong2;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    paramSerializer.writeUTFArray(this.keys);
    paramSerializer.writeUTFArray(this.values);
    paramSerializer.writeLong(this.parentNativeWindowHandle);
    paramSerializer.writeUTF(this.caRenderServerName);
    paramSerializer.writeBoolean(this.useXEmbed);
    paramSerializer.writeInt(this.appletID);
    paramSerializer.writeUTF(this.docbase);
    paramSerializer.writeBoolean(this.isForDummyApplet);
    paramSerializer.writeLong(this.appletLaunchTime);
    HeartbeatMessage.writeJVMHealthData(paramSerializer, this.healthData);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.keys = paramSerializer.readUTFArray();
    this.values = paramSerializer.readUTFArray();
    this.parentNativeWindowHandle = paramSerializer.readLong();
    this.caRenderServerName = paramSerializer.readUTF();
    this.useXEmbed = paramSerializer.readBoolean();
    this.appletID = paramSerializer.readInt();
    this.docbase = paramSerializer.readUTF();
    this.isForDummyApplet = paramSerializer.readBoolean();
    this.appletLaunchTime = paramSerializer.readLong();
    this.healthData = HeartbeatMessage.readJVMHealthData(paramSerializer);
  }

  public void setParameters(AppletParameters paramAppletParameters)
  {
    Map localMap = paramAppletParameters.rawMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList1.addAll(localMap.keySet());
    Iterator localIterator = localArrayList1.iterator();
    while (localIterator.hasNext())
      localArrayList2.add(localMap.get(localIterator.next()));
    this.keys = ((String[])localArrayList1.toArray(new String[0]));
    this.values = ((String[])localArrayList2.toArray(new String[0]));
  }

  public AppletParameters getParameters()
  {
    AppletParameters localAppletParameters = new AppletParameters();
    for (int i = 0; i < this.keys.length; i++)
      localAppletParameters.put(this.keys[i], this.values[i]);
    return localAppletParameters;
  }

  public long getParentNativeWindowHandle()
  {
    return this.parentNativeWindowHandle;
  }

  public String getCARenderServerName()
  {
    return this.caRenderServerName;
  }

  public boolean useXEmbed()
  {
    return this.useXEmbed;
  }

  public int getAppletID()
  {
    return this.appletID;
  }

  public String getDocumentBase()
  {
    return this.docbase;
  }

  public boolean isForDummyApplet()
  {
    return this.isForDummyApplet;
  }

  public long getAppletLaunchTime()
  {
    return this.appletLaunchTime;
  }

  public void collectJVMHealthData()
  {
    this.healthData = JVMHealthData.getCurrent();
  }

  public JVMHealthData getHealthData()
  {
    return this.healthData;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.StartAppletMessage
 * JD-Core Version:    0.6.2
 */