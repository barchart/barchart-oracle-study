/*      */ package sun.java2d.loops;
/*      */ 
/*      */ import java.awt.image.WritableRaster;
/*      */ 
/*      */ abstract class PixelWriter
/*      */ {
/*      */   protected WritableRaster dstRast;
/*      */ 
/*      */   public void setRaster(WritableRaster paramWritableRaster)
/*      */   {
/* 1002 */     this.dstRast = paramWritableRaster;
/*      */   }
/*      */ 
/*      */   public abstract void writePixel(int paramInt1, int paramInt2);
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.loops.PixelWriter
 * JD-Core Version:    0.6.2
 */