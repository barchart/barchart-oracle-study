/*      */ package sun.java2d.loops;
/*      */ 
/*      */ import java.awt.image.WritableRaster;
/*      */ 
/*      */ class SolidPixelWriter extends PixelWriter
/*      */ {
/*      */   protected Object srcData;
/*      */ 
/*      */   SolidPixelWriter(Object paramObject)
/*      */   {
/* 1012 */     this.srcData = paramObject;
/*      */   }
/*      */ 
/*      */   public void writePixel(int paramInt1, int paramInt2) {
/* 1016 */     this.dstRast.setDataElements(paramInt1, paramInt2, this.srcData);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.loops.SolidPixelWriter
 * JD-Core Version:    0.6.2
 */