package sun.java2d.xr;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import sun.font.GlyphList;
import sun.font.XRGlyphCacheEntry;
import sun.java2d.jules.TrapezoidList;
import sun.java2d.pipe.Region;

public abstract interface XRBackend
{
  public abstract void freePicture(int paramInt);

  public abstract void freePixmap(int paramInt);

  public abstract int createPixmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract int createPicture(int paramInt1, int paramInt2);

  public abstract long createGC(int paramInt);

  public abstract void freeGC(long paramLong);

  public abstract void copyArea(int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);

  public abstract void putMaskImage(int paramInt1, long paramLong, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, float paramFloat);

  public abstract void setGCClipRectangles(long paramLong, Region paramRegion);

  public abstract void GCRectangles(int paramInt, long paramLong, GrowableRectArray paramGrowableRectArray);

  public abstract void setClipRectangles(int paramInt, Region paramRegion);

  public abstract void setGCExposures(long paramLong, boolean paramBoolean);

  public abstract void setGCForeground(long paramLong, int paramInt);

  public abstract void setPictureTransform(int paramInt, AffineTransform paramAffineTransform);

  public abstract void setPictureRepeat(int paramInt1, int paramInt2);

  public abstract void setFilter(int paramInt1, int paramInt2);

  public abstract void renderRectangle(int paramInt1, byte paramByte, XRColor paramXRColor, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

  public abstract void renderRectangles(int paramInt, byte paramByte, XRColor paramXRColor, GrowableRectArray paramGrowableRectArray);

  public abstract void renderComposite(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11);

  public abstract int XRenderCreateGlyphSet(int paramInt);

  public abstract void XRenderAddGlyphs(int paramInt, GlyphList paramGlyphList, List<XRGlyphCacheEntry> paramList, byte[] paramArrayOfByte);

  public abstract void XRenderFreeGlyphs(int paramInt, int[] paramArrayOfInt);

  public abstract void XRenderCompositeText(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, GrowableEltArray paramGrowableEltArray);

  public abstract int createRadialGradient(Point2D paramPoint2D1, Point2D paramPoint2D2, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int[] paramArrayOfInt, int paramInt, AffineTransform paramAffineTransform);

  public abstract int createLinearGradient(Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, int[] paramArrayOfInt, int paramInt, AffineTransform paramAffineTransform);

  public abstract void setGCMode(long paramLong, boolean paramBoolean);

  public abstract void renderCompositeTrapezoids(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, TrapezoidList paramTrapezoidList);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRBackend
 * JD-Core Version:    0.6.2
 */