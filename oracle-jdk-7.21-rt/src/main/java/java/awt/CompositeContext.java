package java.awt;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract interface CompositeContext
{
  public abstract void dispose();

  public abstract void compose(Raster paramRaster1, Raster paramRaster2, WritableRaster paramWritableRaster);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.CompositeContext
 * JD-Core Version:    0.6.2
 */