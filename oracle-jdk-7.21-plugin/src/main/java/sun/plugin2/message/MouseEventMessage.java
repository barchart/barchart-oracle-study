package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class MouseEventMessage extends EventMessage
{
  public static final int ID = 84;
  private int type;
  private int modifierFlags;
  private double pluginX;
  private double pluginY;
  private int buttonNumber;
  private int clickCount;

  public MouseEventMessage(Conversation paramConversation)
  {
    super(84, paramConversation);
  }

  public MouseEventMessage(Conversation paramConversation, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double paramDouble2, int paramInt4, int paramInt5)
  {
    super(84, paramConversation, paramInt1);
    this.type = paramInt2;
    this.modifierFlags = paramInt3;
    this.pluginX = paramDouble1;
    this.pluginY = paramDouble2;
    this.buttonNumber = paramInt4;
    this.clickCount = paramInt5;
  }

  public int getType()
  {
    return this.type;
  }

  public int getModifierFlags()
  {
    return this.modifierFlags;
  }

  public double getPluginX()
  {
    return this.pluginX;
  }

  public double getPluginY()
  {
    return this.pluginY;
  }

  public int getButtonNumber()
  {
    return this.buttonNumber;
  }

  public int getClickCount()
  {
    return this.clickCount;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.type);
    paramSerializer.writeInt(this.modifierFlags);
    paramSerializer.writeDouble(this.pluginX);
    paramSerializer.writeDouble(this.pluginY);
    paramSerializer.writeInt(this.buttonNumber);
    paramSerializer.writeInt(this.clickCount);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.type = paramSerializer.readInt();
    this.modifierFlags = paramSerializer.readInt();
    this.pluginX = paramSerializer.readDouble();
    this.pluginY = paramSerializer.readDouble();
    this.buttonNumber = paramSerializer.readInt();
    this.clickCount = paramSerializer.readInt();
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(this.type));
    paramMap.put("modifierFlags", SystemUtils.integerValueOf(this.modifierFlags));
    paramMap.put("pluginX", Double.valueOf(this.pluginX));
    paramMap.put("pluginY", Double.valueOf(this.pluginY));
    paramMap.put("buttonNumber", SystemUtils.integerValueOf(this.buttonNumber));
    paramMap.put("clickCount", SystemUtils.integerValueOf(this.clickCount));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.MouseEventMessage
 * JD-Core Version:    0.6.2
 */