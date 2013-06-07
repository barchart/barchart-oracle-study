/*    */ package sun.java2d.xr;
/*    */ 
/*    */ import java.awt.GraphicsConfiguration;
/*    */ import java.awt.ImageCapabilities;
/*    */ import java.awt.image.ColorModel;
/*    */ import sun.awt.image.SunVolatileImage;
/*    */ import sun.awt.image.VolatileSurfaceManager;
/*    */ import sun.java2d.SurfaceData;
/*    */ 
/*    */ public class XRVolatileSurfaceManager extends VolatileSurfaceManager
/*    */ {
/*    */   public XRVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*    */   {
/* 41 */     super(paramSunVolatileImage, paramObject);
/*    */   }
/*    */ 
/*    */   protected boolean isAccelerationEnabled() {
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */   protected SurfaceData initAcceleratedSurface()
/*    */   {
/*    */     XRSurfaceData.XRPixmapSurfaceData localXRPixmapSurfaceData;
/*    */     try
/*    */     {
/* 55 */       XRGraphicsConfig localXRGraphicsConfig = (XRGraphicsConfig)this.vImg.getGraphicsConfig();
/* 56 */       ColorModel localColorModel = localXRGraphicsConfig.getColorModel();
/* 57 */       long l = 0L;
/* 58 */       if ((this.context instanceof Long)) {
/* 59 */         l = ((Long)this.context).longValue();
/*    */       }
/* 61 */       localXRPixmapSurfaceData = XRSurfaceData.createData(localXRGraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), localColorModel, this.vImg, l, this.vImg.getTransparency());
/*    */     }
/*    */     catch (NullPointerException localNullPointerException)
/*    */     {
/* 67 */       localXRPixmapSurfaceData = null;
/*    */     } catch (OutOfMemoryError localOutOfMemoryError) {
/* 69 */       localXRPixmapSurfaceData = null;
/*    */     }
/*    */ 
/* 72 */     return localXRPixmapSurfaceData;
/*    */   }
/*    */ 
/*    */   protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
/*    */   {
/* 80 */     return true;
/*    */   }
/*    */ 
/*    */   public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
/*    */   {
/* 89 */     if ((isConfigValid(paramGraphicsConfiguration)) && (isAccelerationEnabled())) {
/* 90 */       return new ImageCapabilities(true);
/*    */     }
/* 92 */     return new ImageCapabilities(false);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRVolatileSurfaceManager
 * JD-Core Version:    0.6.2
 */