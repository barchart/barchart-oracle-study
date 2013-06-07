/*    */ package sun.java2d.xr;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Image;
/*    */ import java.awt.geom.AffineTransform;
/*    */ import sun.java2d.SunGraphics2D;
/*    */ import sun.java2d.SurfaceData;
/*    */ import sun.java2d.loops.SurfaceType;
/*    */ import sun.java2d.loops.TransformBlit;
/*    */ import sun.java2d.pipe.DrawImage;
/*    */ 
/*    */ public class XRDrawImage extends DrawImage
/*    */ {
/*    */   protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
/*    */   {
/* 44 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/* 45 */     SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*    */ 
/* 48 */     if ((localSurfaceData2 != null) && (!isBgOperation(localSurfaceData2, paramColor)))
/*    */     {
/* 50 */       SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/* 51 */       SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*    */ 
/* 53 */       TransformBlit localTransformBlit = TransformBlit.getFromCache(localSurfaceType1, paramSunGraphics2D.imageComp, localSurfaceType2);
/*    */ 
/* 56 */       if (localTransformBlit != null) {
/* 57 */         localTransformBlit.Transform(localSurfaceData2, localSurfaceData1, paramSunGraphics2D.composite, paramSunGraphics2D.getCompClip(), paramAffineTransform, paramInt1, paramInt2, paramInt3, 0, 0, paramInt4 - paramInt2, paramInt5 - paramInt3);
/*    */ 
/* 60 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 64 */     super.renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRDrawImage
 * JD-Core Version:    0.6.2
 */