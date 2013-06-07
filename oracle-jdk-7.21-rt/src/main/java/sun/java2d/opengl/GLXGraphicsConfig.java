/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.BufferCapabilities;
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.ImageCapabilities;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.DirectColorModel;
/*     */ import java.awt.image.VolatileImage;
/*     */ import java.awt.image.WritableRaster;
/*     */ import sun.awt.X11ComponentPeer;
/*     */ import sun.awt.X11GraphicsConfig;
/*     */ import sun.awt.X11GraphicsDevice;
/*     */ import sun.awt.X11GraphicsEnvironment;
/*     */ import sun.awt.image.OffScreenImage;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.SurfaceManager;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.Surface;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.hw.AccelDeviceEventListener;
/*     */ import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
/*     */ import sun.java2d.pipe.hw.AccelSurface;
/*     */ import sun.java2d.pipe.hw.AccelTypedVolatileImage;
/*     */ import sun.java2d.pipe.hw.ContextCapabilities;
/*     */ 
/*     */ public class GLXGraphicsConfig extends X11GraphicsConfig
/*     */   implements OGLGraphicsConfig
/*     */ {
/*  69 */   private static ImageCapabilities imageCaps = new GLXImageCaps(null);
/*     */   private BufferCapabilities bufferCaps;
/*     */   private long pConfigInfo;
/*     */   private ContextCapabilities oglCaps;
/*     */   private OGLContext context;
/*     */ 
/*     */   private static native long getGLXConfigInfo(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native int getOGLCapabilities(long paramLong);
/*     */ 
/*     */   private native void initConfig(long paramLong1, long paramLong2);
/*     */ 
/*     */   private GLXGraphicsConfig(X11GraphicsDevice paramX11GraphicsDevice, int paramInt, long paramLong, ContextCapabilities paramContextCapabilities)
/*     */   {
/*  82 */     super(paramX11GraphicsDevice, paramInt, 0, 0, (paramContextCapabilities.getCaps() & 0x10000) != 0);
/*     */ 
/*  84 */     this.pConfigInfo = paramLong;
/*  85 */     initConfig(getAData(), paramLong);
/*  86 */     this.oglCaps = paramContextCapabilities;
/*  87 */     this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
/*     */   }
/*     */ 
/*     */   public Object getProxyKey()
/*     */   {
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   public SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  97 */     return GLXSurfaceData.createData(this, paramInt1, paramInt2, getColorModel(paramInt3), null, 3);
/*     */   }
/*     */ 
/*     */   public static GLXGraphicsConfig getConfig(X11GraphicsDevice paramX11GraphicsDevice, int paramInt)
/*     */   {
/* 106 */     if (!X11GraphicsEnvironment.isGLXAvailable()) {
/* 107 */       return null;
/*     */     }
/*     */ 
/* 110 */     long l = 0L;
/* 111 */     String[] arrayOfString = new String[1];
/* 112 */     OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
/* 113 */     localOGLRenderQueue.lock();
/*     */     try
/*     */     {
/* 118 */       OGLContext.invalidateCurrentContext();
/* 119 */       GLXGetConfigInfo localGLXGetConfigInfo = new GLXGetConfigInfo(paramX11GraphicsDevice.getScreen(), paramInt, null);
/*     */ 
/* 121 */       localOGLRenderQueue.flushAndInvokeNow(localGLXGetConfigInfo);
/* 122 */       l = localGLXGetConfigInfo.getConfigInfo();
/* 123 */       if (l != 0L) {
/* 124 */         OGLContext.setScratchSurface(l);
/* 125 */         localOGLRenderQueue.flushAndInvokeNow(new Runnable() {
/*     */           public void run() {
/* 127 */             this.val$ids[0] = OGLContext.getOGLIdString();
/*     */           } } );
/*     */       }
/*     */     }
/*     */     finally {
/* 132 */       localOGLRenderQueue.unlock();
/*     */     }
/* 134 */     if (l == 0L) {
/* 135 */       return null;
/*     */     }
/*     */ 
/* 138 */     int i = getOGLCapabilities(l);
/* 139 */     OGLContext.OGLContextCaps localOGLContextCaps = new OGLContext.OGLContextCaps(i, arrayOfString[0]);
/*     */ 
/* 141 */     return new GLXGraphicsConfig(paramX11GraphicsDevice, paramInt, l, localOGLContextCaps);
/*     */   }
/*     */ 
/*     */   public final boolean isCapPresent(int paramInt)
/*     */   {
/* 170 */     return (this.oglCaps.getCaps() & paramInt) != 0;
/*     */   }
/*     */ 
/*     */   public final long getNativeConfigInfo()
/*     */   {
/* 175 */     return this.pConfigInfo;
/*     */   }
/*     */ 
/*     */   public final OGLContext getContext()
/*     */   {
/* 185 */     return this.context;
/*     */   }
/*     */ 
/*     */   public BufferedImage createCompatibleImage(int paramInt1, int paramInt2)
/*     */   {
/* 190 */     DirectColorModel localDirectColorModel = new DirectColorModel(24, 16711680, 65280, 255);
/*     */ 
/* 192 */     WritableRaster localWritableRaster = localDirectColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/* 193 */     return new BufferedImage(localDirectColorModel, localWritableRaster, localDirectColorModel.isAlphaPremultiplied(), null);
/*     */   }
/*     */ 
/*     */   public ColorModel getColorModel(int paramInt)
/*     */   {
/* 199 */     switch (paramInt)
/*     */     {
/*     */     case 1:
/* 203 */       return new DirectColorModel(24, 16711680, 65280, 255);
/*     */     case 2:
/* 205 */       return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
/*     */     case 3:
/* 207 */       ColorSpace localColorSpace = ColorSpace.getInstance(1000);
/* 208 */       return new DirectColorModel(localColorSpace, 32, 16711680, 65280, 255, -16777216, true, 3);
/*     */     }
/*     */ 
/* 212 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 217 */     return "GLXGraphicsConfig[dev=" + this.screen + ",vis=0x" + Integer.toHexString(this.visual) + "]";
/*     */   }
/*     */ 
/*     */   public SurfaceData createSurfaceData(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/* 236 */     return GLXSurfaceData.createData(paramX11ComponentPeer);
/*     */   }
/*     */ 
/*     */   public Image createAcceleratedImage(Component paramComponent, int paramInt1, int paramInt2)
/*     */   {
/* 247 */     ColorModel localColorModel = getColorModel(1);
/* 248 */     WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
/*     */ 
/* 250 */     return new OffScreenImage(paramComponent, localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied());
/*     */   }
/*     */ 
/*     */   public long createBackBuffer(X11ComponentPeer paramX11ComponentPeer, int paramInt, BufferCapabilities paramBufferCapabilities)
/*     */     throws AWTException
/*     */   {
/* 270 */     if (paramInt > 2) {
/* 271 */       throw new AWTException("Only double or single buffering is supported");
/*     */     }
/*     */ 
/* 274 */     BufferCapabilities localBufferCapabilities = getBufferCapabilities();
/* 275 */     if (!localBufferCapabilities.isPageFlipping()) {
/* 276 */       throw new AWTException("Page flipping is not supported");
/*     */     }
/* 278 */     if (paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.PRIOR) {
/* 279 */       throw new AWTException("FlipContents.PRIOR is not supported");
/*     */     }
/*     */ 
/* 284 */     return 1L;
/*     */   }
/*     */ 
/*     */   public void destroyBackBuffer(long paramLong)
/*     */   {
/*     */   }
/*     */ 
/*     */   public VolatileImage createBackBufferImage(Component paramComponent, long paramLong)
/*     */   {
/* 302 */     return new SunVolatileImage(paramComponent, paramComponent.getWidth(), paramComponent.getHeight(), Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public void flip(X11ComponentPeer paramX11ComponentPeer, Component paramComponent, VolatileImage paramVolatileImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
/*     */   {
/*     */     Object localObject1;
/* 316 */     if (paramFlipContents == BufferCapabilities.FlipContents.COPIED) {
/* 317 */       localObject1 = SurfaceManager.getManager(paramVolatileImage);
/* 318 */       SurfaceData localSurfaceData1 = ((SurfaceManager)localObject1).getPrimarySurfaceData();
/*     */       Object localObject2;
/* 320 */       if ((localSurfaceData1 instanceof GLXSurfaceData.GLXVSyncOffScreenSurfaceData)) {
/* 321 */         localObject2 = (GLXSurfaceData.GLXVSyncOffScreenSurfaceData)localSurfaceData1;
/*     */ 
/* 323 */         SurfaceData localSurfaceData2 = ((GLXSurfaceData.GLXVSyncOffScreenSurfaceData)localObject2).getFlipSurface();
/* 324 */         SunGraphics2D localSunGraphics2D = new SunGraphics2D(localSurfaceData2, Color.black, Color.white, null);
/*     */         try
/*     */         {
/* 327 */           localSunGraphics2D.drawImage(paramVolatileImage, 0, 0, null);
/*     */         } finally {
/* 329 */           localSunGraphics2D.dispose();
/*     */         }
/*     */       } else {
/* 332 */         localObject2 = paramX11ComponentPeer.getGraphics();
/*     */         try {
/* 334 */           ((Graphics)localObject2).drawImage(paramVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
/*     */         }
/*     */         finally
/*     */         {
/* 339 */           ((Graphics)localObject2).dispose();
/*     */         }
/* 341 */         return;
/*     */       }
/* 343 */     } else if (paramFlipContents == BufferCapabilities.FlipContents.PRIOR)
/*     */     {
/* 345 */       return;
/*     */     }
/*     */ 
/* 348 */     OGLSurfaceData.swapBuffers(paramX11ComponentPeer.getContentWindow());
/*     */ 
/* 350 */     if (paramFlipContents == BufferCapabilities.FlipContents.BACKGROUND) {
/* 351 */       localObject1 = paramVolatileImage.getGraphics();
/*     */       try {
/* 353 */         ((Graphics)localObject1).setColor(paramComponent.getBackground());
/* 354 */         ((Graphics)localObject1).fillRect(0, 0, paramVolatileImage.getWidth(), paramVolatileImage.getHeight());
/*     */       }
/*     */       finally
/*     */       {
/* 358 */         ((Graphics)localObject1).dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public BufferCapabilities getBufferCapabilities()
/*     */   {
/* 372 */     if (this.bufferCaps == null) {
/* 373 */       this.bufferCaps = new GLXBufferCaps(isDoubleBuffered());
/*     */     }
/* 375 */     return this.bufferCaps;
/*     */   }
/*     */ 
/*     */   public ImageCapabilities getImageCapabilities()
/*     */   {
/* 389 */     return imageCaps;
/*     */   }
/*     */ 
/*     */   public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 402 */     if ((paramInt4 == 4) || (paramInt4 == 1) || (paramInt4 == 0) || (paramInt3 == 2))
/*     */     {
/* 405 */       return null;
/*     */     }
/*     */ 
/* 408 */     if (paramInt4 == 5) {
/* 409 */       if (!isCapPresent(12))
/* 410 */         return null;
/*     */     }
/* 412 */     else if (paramInt4 == 2) {
/* 413 */       int i = paramInt3 == 1 ? 1 : 0;
/* 414 */       if ((i == 0) && (!isCapPresent(2))) {
/* 415 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 419 */     AccelTypedVolatileImage localAccelTypedVolatileImage = new AccelTypedVolatileImage(this, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */ 
/* 421 */     Surface localSurface = localAccelTypedVolatileImage.getDestSurface();
/* 422 */     if ((!(localSurface instanceof AccelSurface)) || (((AccelSurface)localSurface).getType() != paramInt4))
/*     */     {
/* 425 */       localAccelTypedVolatileImage.flush();
/* 426 */       localAccelTypedVolatileImage = null;
/*     */     }
/*     */ 
/* 429 */     return localAccelTypedVolatileImage;
/*     */   }
/*     */ 
/*     */   public ContextCapabilities getContextCapabilities()
/*     */   {
/* 439 */     return this.oglCaps;
/*     */   }
/*     */ 
/*     */   public void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
/*     */   {
/* 444 */     AccelDeviceEventNotifier.addListener(paramAccelDeviceEventListener, this.screen.getScreen());
/*     */   }
/*     */ 
/*     */   public void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener)
/*     */   {
/* 449 */     AccelDeviceEventNotifier.removeListener(paramAccelDeviceEventListener);
/*     */   }
/*     */ 
/*     */   private static class GLXBufferCaps extends BufferCapabilities
/*     */   {
/*     */     public GLXBufferCaps(boolean paramBoolean)
/*     */     {
/* 365 */       super(GLXGraphicsConfig.imageCaps, paramBoolean ? BufferCapabilities.FlipContents.UNDEFINED : null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class GLXGetConfigInfo
/*     */     implements Runnable
/*     */   {
/*     */     private int screen;
/*     */     private int visual;
/*     */     private long cfginfo;
/*     */ 
/*     */     private GLXGetConfigInfo(int paramInt1, int paramInt2)
/*     */     {
/* 153 */       this.screen = paramInt1;
/* 154 */       this.visual = paramInt2;
/*     */     }
/*     */     public void run() {
/* 157 */       this.cfginfo = GLXGraphicsConfig.getGLXConfigInfo(this.screen, this.visual);
/*     */     }
/*     */     public long getConfigInfo() {
/* 160 */       return this.cfginfo;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class GLXImageCaps extends ImageCapabilities
/*     */   {
/*     */     private GLXImageCaps()
/*     */     {
/* 380 */       super();
/*     */     }
/*     */     public boolean isTrueVolatile() {
/* 383 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.opengl.GLXGraphicsConfig
 * JD-Core Version:    0.6.2
 */