package sun.plugin2.message;

import java.awt.geom.Point2D.Double;
import java.io.IOException;

public class OverlayWindowMoveMessage extends AppletMessage
{
  public static final int ID = 80;
  private double x;
  private double y;

  public OverlayWindowMoveMessage(Conversation paramConversation)
  {
    super(80, paramConversation);
  }

  public OverlayWindowMoveMessage(Conversation paramConversation, int paramInt, double paramDouble1, double paramDouble2)
  {
    super(80, paramConversation, paramInt);
    this.x = paramDouble1;
    this.y = paramDouble2;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeDouble(this.x);
    paramSerializer.writeDouble(this.y);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.x = paramSerializer.readDouble();
    this.y = paramSerializer.readDouble();
  }

  public Point2D.Double getLocation()
  {
    return new Point2D.Double(this.x, this.y);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.OverlayWindowMoveMessage
 * JD-Core Version:    0.6.2
 */