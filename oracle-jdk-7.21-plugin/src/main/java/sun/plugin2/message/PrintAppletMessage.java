package sun.plugin2.message;

import java.io.IOException;

public class PrintAppletMessage extends AppletMessage
{
  public static final int ID = 8;
  private long hdc;
  private boolean isPrinterDC;
  private int x;
  private int y;
  private int width;
  private int height;

  public PrintAppletMessage(Conversation paramConversation)
  {
    super(8, paramConversation);
  }

  public PrintAppletMessage(Conversation paramConversation, int paramInt1, long paramLong, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(8, paramConversation, paramInt1);
    this.hdc = paramLong;
    this.isPrinterDC = paramBoolean;
    this.x = paramInt2;
    this.y = paramInt3;
    this.width = paramInt4;
    this.height = paramInt5;
  }

  public long getHDC()
  {
    return this.hdc;
  }

  public boolean getIsPrinterDC()
  {
    return this.isPrinterDC;
  }

  public int getX()
  {
    return this.x;
  }

  public int getY()
  {
    return this.y;
  }

  public int getWidth()
  {
    return this.width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeLong(this.hdc);
    paramSerializer.writeBoolean(this.isPrinterDC);
    paramSerializer.writeInt(this.x);
    paramSerializer.writeInt(this.y);
    paramSerializer.writeInt(this.width);
    paramSerializer.writeInt(this.height);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.hdc = paramSerializer.readLong();
    this.isPrinterDC = paramSerializer.readBoolean();
    this.x = paramSerializer.readInt();
    this.y = paramSerializer.readInt();
    this.width = paramSerializer.readInt();
    this.height = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.PrintAppletMessage
 * JD-Core Version:    0.6.2
 */