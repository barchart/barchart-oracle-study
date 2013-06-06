package sun.plugin2.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PrintBandMessage extends AppletMessage
{
  public static final int ID = 10;
  private long hdc;
  private byte[] data;
  private ByteBuffer buf;
  private int offset;
  private int sx;
  private int sy;
  private int swidth;
  private int sheight;
  private int dx;
  private int dy;
  private int dwidth;
  private int dheight;

  public PrintBandMessage(Conversation paramConversation)
  {
    super(10, paramConversation);
  }

  public PrintBandMessage(Conversation paramConversation, int paramInt1, long paramLong, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    super(10, paramConversation, paramInt1);
    this.hdc = paramLong;
    this.data = paramArrayOfByte;
    this.offset = paramInt2;
    this.sx = paramInt3;
    this.sy = paramInt4;
    this.swidth = paramInt5;
    this.sheight = paramInt6;
    this.dx = paramInt7;
    this.dy = paramInt8;
    this.dwidth = paramInt9;
    this.dheight = paramInt10;
  }

  public long getHDC()
  {
    return this.hdc;
  }

  public byte[] getData()
  {
    return this.data;
  }

  public ByteBuffer getDataAsByteBuffer()
  {
    return this.buf;
  }

  public int getOffset()
  {
    return this.offset;
  }

  public int getSrcX()
  {
    return this.sx;
  }

  public int getSrcY()
  {
    return this.sy;
  }

  public int getSrcWidth()
  {
    return this.swidth;
  }

  public int getSrcHeight()
  {
    return this.sheight;
  }

  public int getDestX()
  {
    return this.dx;
  }

  public int getDestY()
  {
    return this.dy;
  }

  public int getDestWidth()
  {
    return this.dwidth;
  }

  public int getDestHeight()
  {
    return this.dheight;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeLong(this.hdc);
    paramSerializer.writeByteArray(this.data);
    paramSerializer.writeInt(this.offset);
    paramSerializer.writeInt(this.sx);
    paramSerializer.writeInt(this.sy);
    paramSerializer.writeInt(this.swidth);
    paramSerializer.writeInt(this.sheight);
    paramSerializer.writeInt(this.dx);
    paramSerializer.writeInt(this.dy);
    paramSerializer.writeInt(this.dwidth);
    paramSerializer.writeInt(this.dheight);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.hdc = paramSerializer.readLong();
    this.buf = paramSerializer.readByteBuffer();
    this.offset = paramSerializer.readInt();
    this.sx = paramSerializer.readInt();
    this.sy = paramSerializer.readInt();
    this.swidth = paramSerializer.readInt();
    this.sheight = paramSerializer.readInt();
    this.dx = paramSerializer.readInt();
    this.dy = paramSerializer.readInt();
    this.dwidth = paramSerializer.readInt();
    this.dheight = paramSerializer.readInt();
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.PrintBandMessage
 * JD-Core Version:    0.6.2
 */