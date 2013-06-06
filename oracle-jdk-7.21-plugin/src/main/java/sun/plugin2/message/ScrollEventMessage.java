package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class ScrollEventMessage extends EventMessage
{
  public static final int ID = 85;
  private double pluginX;
  private double pluginY;
  private int modifierFlags;
  private double deltaX;
  private double deltaY;
  private double deltaZ;

  public ScrollEventMessage(Conversation paramConversation)
  {
    super(85, paramConversation);
  }

  public ScrollEventMessage(Conversation paramConversation, int paramInt1, double paramDouble1, double paramDouble2, int paramInt2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    super(85, paramConversation, paramInt1);
    this.pluginX = paramDouble1;
    this.pluginY = paramDouble2;
    this.modifierFlags = paramInt2;
    this.deltaX = paramDouble3;
    this.deltaY = paramDouble4;
    this.deltaZ = paramDouble5;
  }

  public double getPluginX()
  {
    return this.pluginX;
  }

  public double getPluginY()
  {
    return this.pluginY;
  }

  public int getModifierFlags()
  {
    return this.modifierFlags;
  }

  public double getDeltaX()
  {
    return this.deltaX;
  }

  public double getDeltaY()
  {
    return this.deltaY;
  }

  public double getDeltaZ()
  {
    return this.deltaZ;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeDouble(this.pluginX);
    paramSerializer.writeDouble(this.pluginY);
    paramSerializer.writeInt(this.modifierFlags);
    paramSerializer.writeDouble(this.deltaX);
    paramSerializer.writeDouble(this.deltaY);
    paramSerializer.writeDouble(this.deltaZ);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.pluginX = paramSerializer.readDouble();
    this.pluginY = paramSerializer.readDouble();
    this.modifierFlags = paramSerializer.readInt();
    this.deltaX = paramSerializer.readDouble();
    this.deltaY = paramSerializer.readDouble();
    this.deltaZ = paramSerializer.readDouble();
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(13));
    paramMap.put("pluginX", new Double(this.pluginX));
    paramMap.put("pluginY", new Double(this.pluginY));
    paramMap.put("modifierFlags", SystemUtils.integerValueOf(this.modifierFlags));
    paramMap.put("deltaX", new Double(this.deltaX));
    paramMap.put("deltaY", new Double(this.deltaY));
    paramMap.put("deltaZ", new Double(this.deltaZ));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.ScrollEventMessage
 * JD-Core Version:    0.6.2
 */