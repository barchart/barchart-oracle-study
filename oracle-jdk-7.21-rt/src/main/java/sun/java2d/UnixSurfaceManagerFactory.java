/*    */ package sun.java2d;
/*    */ 
/*    */ import java.awt.GraphicsConfiguration;
/*    */ import sun.awt.image.SunVolatileImage;
/*    */ import sun.awt.image.VolatileSurfaceManager;
/*    */ import sun.java2d.opengl.GLXGraphicsConfig;
/*    */ import sun.java2d.opengl.GLXVolatileSurfaceManager;
/*    */ import sun.java2d.x11.X11VolatileSurfaceManager;
/*    */ import sun.java2d.xr.XRGraphicsConfig;
/*    */ import sun.java2d.xr.XRVolatileSurfaceManager;
/*    */ 
/*    */ public class UnixSurfaceManagerFactory extends SurfaceManagerFactory
/*    */ {
/*    */   public VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*    */   {
/* 57 */     GraphicsConfiguration localGraphicsConfiguration = paramSunVolatileImage.getGraphicsConfig();
/*    */ 
/* 59 */     if ((localGraphicsConfiguration instanceof GLXGraphicsConfig))
/* 60 */       return new GLXVolatileSurfaceManager(paramSunVolatileImage, paramObject);
/* 61 */     if ((localGraphicsConfiguration instanceof XRGraphicsConfig)) {
/* 62 */       return new XRVolatileSurfaceManager(paramSunVolatileImage, paramObject);
/*    */     }
/* 64 */     return new X11VolatileSurfaceManager(paramSunVolatileImage, paramObject);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.UnixSurfaceManagerFactory
 * JD-Core Version:    0.6.2
 */