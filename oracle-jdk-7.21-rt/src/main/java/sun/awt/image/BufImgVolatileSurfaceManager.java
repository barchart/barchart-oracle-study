/*    */ package sun.awt.image;
/*    */ 
/*    */ import sun.java2d.SurfaceData;
/*    */ 
/*    */ public class BufImgVolatileSurfaceManager extends VolatileSurfaceManager
/*    */ {
/*    */   public BufImgVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*    */   {
/* 47 */     super(paramSunVolatileImage, paramObject);
/*    */   }
/*    */ 
/*    */   protected boolean isAccelerationEnabled()
/*    */   {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   protected SurfaceData initAcceleratedSurface()
/*    */   {
/* 66 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.image.BufImgVolatileSurfaceManager
 * JD-Core Version:    0.6.2
 */