package sun.plugin2.main.client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PrintBandDescriptor
{
  private BufferedImage bandImage = null;
  private Graphics2D g2d = null;
  private int nextBandTop = 0;
  private boolean isLastBand = false;
  private byte[] data = null;
  private int offset = 0;
  private int sx = 0;
  private int sy = 0;
  private int swidth = 0;
  private int sheight = 0;
  private int dx = 0;
  private int dy = 0;
  private int dwidth = 0;
  private int dheight = 0;

  public PrintBandDescriptor(BufferedImage paramBufferedImage, Graphics2D paramGraphics2D, int paramInt1, boolean paramBoolean, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    this.bandImage = paramBufferedImage;
    this.g2d = paramGraphics2D;
    updateBandInfo(paramInt1, paramBoolean, paramArrayOfByte, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9, paramInt10);
  }

  public void updateBandInfo(int paramInt1, boolean paramBoolean, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
  {
    this.nextBandTop = paramInt1;
    this.isLastBand = paramBoolean;
    this.data = paramArrayOfByte;
    this.offset = paramInt2;
    this.sx = paramInt3;
    this.sy = this.sy;
    this.swidth = paramInt5;
    this.sheight = paramInt6;
    this.dx = paramInt7;
    this.dy = paramInt8;
    this.dwidth = paramInt9;
    this.dheight = paramInt10;
  }

  public BufferedImage getBandImage()
  {
    return this.bandImage;
  }

  public Graphics2D getG2D()
  {
    return this.g2d;
  }

  public int getNextBandTop()
  {
    return this.nextBandTop;
  }

  public boolean isLastBand()
  {
    return this.isLastBand;
  }

  public byte[] getData()
  {
    return this.data;
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
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.main.client.PrintBandDescriptor
 * JD-Core Version:    0.6.2
 */