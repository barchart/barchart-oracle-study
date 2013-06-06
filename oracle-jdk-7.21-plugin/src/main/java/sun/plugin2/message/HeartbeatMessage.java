package sun.plugin2.message;

import java.io.IOException;
import sun.plugin2.main.server.JVMHealthData;

public class HeartbeatMessage extends PluginMessage
{
  public static final int ID = 15;
  public static final long DEFAULT_INTERVAL = 1000L;
  public static final long DEFAULT_TIMEOUT = 10000L;
  private static long INTERVAL_TIMEOUT_SAFETY = 1000L;
  private static long MINIMAL_TIMEOUT = 2000L;
  private JVMHealthData healthData;
  private long timeout = 10000L;
  private long interval = 1000L;

  public HeartbeatMessage(Conversation paramConversation)
  {
    this(paramConversation, new JVMHealthData());
  }

  public HeartbeatMessage(Conversation paramConversation, JVMHealthData paramJVMHealthData)
  {
    super(15, paramConversation);
    this.healthData = paramJVMHealthData;
  }

  public HeartbeatMessage(Conversation paramConversation, long paramLong1, long paramLong2)
  {
    this(paramConversation);
    this.interval = paramLong1;
    this.timeout = paramLong2;
  }

  public void updateHealthData()
  {
    this.healthData = JVMHealthData.getCurrent();
  }

  public JVMHealthData getHealthData()
  {
    return this.healthData;
  }

  public void adjustTiming(long paramLong1, long paramLong2)
  {
    if (paramLong2 < MINIMAL_TIMEOUT)
      paramLong2 = MINIMAL_TIMEOUT;
    if (paramLong1 > this.timeout - INTERVAL_TIMEOUT_SAFETY)
      this.interval = (this.timeout - INTERVAL_TIMEOUT_SAFETY);
    else
      this.interval = paramLong1;
    if (paramLong2 < this.interval + INTERVAL_TIMEOUT_SAFETY)
      this.timeout = (this.interval + INTERVAL_TIMEOUT_SAFETY);
    else
      this.timeout = paramLong2;
  }

  public long getInterval()
  {
    return this.interval;
  }

  public long getTimeout()
  {
    return this.timeout;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    writeJVMHealthData(paramSerializer, this.healthData);
    paramSerializer.writeLong(this.timeout);
    paramSerializer.writeLong(this.interval);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    this.healthData = readJVMHealthData(paramSerializer);
    this.timeout = paramSerializer.readLong();
    this.interval = paramSerializer.readLong();
  }

  static void writeJVMHealthData(Serializer paramSerializer, JVMHealthData paramJVMHealthData)
    throws IOException
  {
    long l1 = paramJVMHealthData == null ? -1L : paramJVMHealthData.getMaxHeapSize();
    long l2 = paramJVMHealthData == null ? -1L : paramJVMHealthData.getHeapSize();
    long l3 = paramJVMHealthData == null ? -1L : paramJVMHealthData.getFreeHeapSize();
    int i = paramJVMHealthData == null ? -1 : paramJVMHealthData.getAppletThreadCount();
    paramSerializer.writeLong(l1);
    paramSerializer.writeLong(l2);
    paramSerializer.writeLong(l3);
    paramSerializer.writeInt(i);
  }

  static JVMHealthData readJVMHealthData(Serializer paramSerializer)
    throws IOException
  {
    long l1 = paramSerializer.readLong();
    long l2 = paramSerializer.readLong();
    long l3 = paramSerializer.readLong();
    int i = paramSerializer.readInt();
    return new JVMHealthData(l1, l2, l3, i);
  }

  public String toString()
  {
    String str = getConversation() + " ";
    return str + (this.healthData == null ? "{healthData: null }" : this.healthData.toString());
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.HeartbeatMessage
 * JD-Core Version:    0.6.2
 */