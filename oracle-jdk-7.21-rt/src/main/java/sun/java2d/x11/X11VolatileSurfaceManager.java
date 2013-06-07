/*     */ package sun.java2d.x11;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.ImageCapabilities;
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.VolatileSurfaceManager;
/*     */ import sun.java2d.SurfaceData;
/*     */ 
/*     */ public class X11VolatileSurfaceManager extends VolatileSurfaceManager
/*     */ {
/*     */   private boolean accelerationEnabled;
/*     */ 
/*     */   public X11VolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*     */   {
/*  51 */     super(paramSunVolatileImage, paramObject);
/*     */ 
/*  54 */     this.accelerationEnabled = ((X11SurfaceData.isAccelerationEnabled()) && (paramSunVolatileImage.getTransparency() == 1));
/*     */ 
/*  57 */     if ((paramObject != null) && (!this.accelerationEnabled))
/*     */     {
/*  63 */       this.accelerationEnabled = true;
/*  64 */       this.sdAccel = initAcceleratedSurface();
/*  65 */       this.sdCurrent = this.sdAccel;
/*     */ 
/*  67 */       if (this.sdBackup != null)
/*     */       {
/*  70 */         this.sdBackup = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isAccelerationEnabled() {
/*  76 */     return this.accelerationEnabled;
/*     */   }
/*     */ 
/*     */   protected SurfaceData initAcceleratedSurface()
/*     */   {
/*     */     X11SurfaceData.X11PixmapSurfaceData localX11PixmapSurfaceData;
/*     */     try
/*     */     {
/*  86 */       X11GraphicsConfig localX11GraphicsConfig = (X11GraphicsConfig)this.vImg.getGraphicsConfig();
/*  87 */       ColorModel localColorModel = localX11GraphicsConfig.getColorModel();
/*  88 */       long l = 0L;
/*  89 */       if ((this.context instanceof Long)) {
/*  90 */         l = ((Long)this.context).longValue();
/*     */       }
/*  92 */       localX11PixmapSurfaceData = X11SurfaceData.createData(localX11GraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), localColorModel, this.vImg, l, 1);
/*     */     }
/*     */     catch (NullPointerException localNullPointerException)
/*     */     {
/*  98 */       localX11PixmapSurfaceData = null;
/*     */     } catch (OutOfMemoryError localOutOfMemoryError) {
/* 100 */       localX11PixmapSurfaceData = null;
/*     */     }
/*     */ 
/* 103 */     return localX11PixmapSurfaceData;
/*     */   }
/*     */ 
/*     */   protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 112 */     return (paramGraphicsConfiguration == null) || (paramGraphicsConfiguration == this.vImg.getGraphicsConfig());
/*     */   }
/*     */ 
/*     */   public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 121 */     if ((isConfigValid(paramGraphicsConfiguration)) && (isAccelerationEnabled()))
/*     */     {
/* 123 */       return new ImageCapabilities(true);
/*     */     }
/*     */ 
/* 126 */     return new ImageCapabilities(false);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.x11.X11VolatileSurfaceManager
 * JD-Core Version:    0.6.2
 */