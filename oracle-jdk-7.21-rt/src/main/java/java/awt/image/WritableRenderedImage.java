package java.awt.image;

import java.awt.Point;

public abstract interface WritableRenderedImage extends RenderedImage
{
  public abstract void addTileObserver(TileObserver paramTileObserver);

  public abstract void removeTileObserver(TileObserver paramTileObserver);

  public abstract WritableRaster getWritableTile(int paramInt1, int paramInt2);

  public abstract void releaseWritableTile(int paramInt1, int paramInt2);

  public abstract boolean isTileWritable(int paramInt1, int paramInt2);

  public abstract Point[] getWritableTileIndices();

  public abstract boolean hasTileWriters();

  public abstract void setData(Raster paramRaster);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.image.WritableRenderedImage
 * JD-Core Version:    0.6.2
 */