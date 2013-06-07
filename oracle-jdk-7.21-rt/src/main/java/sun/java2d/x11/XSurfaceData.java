/*    */ package sun.java2d.x11;
/*    */ 
/*    */ import java.awt.image.ColorModel;
/*    */ import sun.awt.X11ComponentPeer;
/*    */ import sun.awt.X11GraphicsConfig;
/*    */ import sun.java2d.SurfaceData;
/*    */ import sun.java2d.loops.SurfaceType;
/*    */ import sun.java2d.pipe.Region;
/*    */ 
/*    */ public abstract class XSurfaceData extends SurfaceData
/*    */ {
/* 11 */   static boolean isX11SurfaceDataInitialized = false;
/*    */ 
/*    */   public static boolean isX11SurfaceDataInitialized() {
/* 14 */     return isX11SurfaceDataInitialized;
/*    */   }
/*    */ 
/*    */   public static void setX11SurfaceDataInitialized() {
/* 18 */     isX11SurfaceDataInitialized = true;
/*    */   }
/*    */ 
/*    */   public XSurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel) {
/* 22 */     super(paramSurfaceType, paramColorModel);
/*    */   }
/*    */ 
/*    */   protected native void initOps(X11ComponentPeer paramX11ComponentPeer, X11GraphicsConfig paramX11GraphicsConfig, int paramInt);
/*    */ 
/*    */   protected static native long XCreateGC(long paramLong);
/*    */ 
/*    */   protected static native void XResetClip(long paramLong);
/*    */ 
/*    */   protected static native void XSetClip(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion);
/*    */ 
/*    */   protected native void flushNativeSurface();
/*    */ 
/*    */   protected native boolean isDrawableValid();
/*    */ 
/*    */   protected native void setInvalid();
/*    */ 
/*    */   protected static native void XSetGraphicsExposures(long paramLong, boolean paramBoolean);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.XSurfaceData
 * JD-Core Version:    0.6.2
 */