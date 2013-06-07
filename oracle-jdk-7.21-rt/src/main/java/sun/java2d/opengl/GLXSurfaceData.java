/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.X11ComponentPeer;
/*     */ import sun.java2d.SurfaceData;
/*     */ 
/*     */ public abstract class GLXSurfaceData extends OGLSurfaceData
/*     */ {
/*     */   protected X11ComponentPeer peer;
/*     */   private GLXGraphicsConfig graphicsConfig;
/*     */ 
/*     */   private native void initOps(X11ComponentPeer paramX11ComponentPeer, long paramLong);
/*     */ 
/*     */   protected native boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
/*     */ 
/*     */   protected GLXSurfaceData(X11ComponentPeer paramX11ComponentPeer, GLXGraphicsConfig paramGLXGraphicsConfig, ColorModel paramColorModel, int paramInt)
/*     */   {
/*  52 */     super(paramGLXGraphicsConfig, paramColorModel, paramInt);
/*  53 */     this.peer = paramX11ComponentPeer;
/*  54 */     this.graphicsConfig = paramGLXGraphicsConfig;
/*  55 */     initOps(paramX11ComponentPeer, this.graphicsConfig.getAData());
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration getDeviceConfiguration() {
/*  59 */     return this.graphicsConfig;
/*     */   }
/*     */ 
/*     */   public static GLXWindowSurfaceData createData(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/*  67 */     GLXGraphicsConfig localGLXGraphicsConfig = getGC(paramX11ComponentPeer);
/*  68 */     return new GLXWindowSurfaceData(paramX11ComponentPeer, localGLXGraphicsConfig);
/*     */   }
/*     */ 
/*     */   public static GLXOffScreenSurfaceData createData(X11ComponentPeer paramX11ComponentPeer, Image paramImage, int paramInt)
/*     */   {
/*  79 */     GLXGraphicsConfig localGLXGraphicsConfig = getGC(paramX11ComponentPeer);
/*  80 */     Rectangle localRectangle = paramX11ComponentPeer.getBounds();
/*  81 */     if (paramInt == 4) {
/*  82 */       return new GLXOffScreenSurfaceData(paramX11ComponentPeer, localGLXGraphicsConfig, localRectangle.width, localRectangle.height, paramImage, paramX11ComponentPeer.getColorModel(), 4);
/*     */     }
/*     */ 
/*  86 */     return new GLXVSyncOffScreenSurfaceData(paramX11ComponentPeer, localGLXGraphicsConfig, localRectangle.width, localRectangle.height, paramImage, paramX11ComponentPeer.getColorModel(), paramInt);
/*     */   }
/*     */ 
/*     */   public static GLXOffScreenSurfaceData createData(GLXGraphicsConfig paramGLXGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, int paramInt3)
/*     */   {
/* 101 */     return new GLXOffScreenSurfaceData(null, paramGLXGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, paramInt3);
/*     */   }
/*     */ 
/*     */   public static GLXGraphicsConfig getGC(X11ComponentPeer paramX11ComponentPeer)
/*     */   {
/* 106 */     if (paramX11ComponentPeer != null) {
/* 107 */       return (GLXGraphicsConfig)paramX11ComponentPeer.getGraphicsConfiguration();
/*     */     }
/*     */ 
/* 111 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */ 
/* 113 */     GraphicsDevice localGraphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
/* 114 */     return (GLXGraphicsConfig)localGraphicsDevice.getDefaultConfiguration();
/*     */   }
/*     */ 
/*     */   public static class GLXOffScreenSurfaceData extends GLXSurfaceData
/*     */   {
/*     */     private Image offscreenImage;
/*     */     private int width;
/*     */     private int height;
/*     */ 
/*     */     public GLXOffScreenSurfaceData(X11ComponentPeer paramX11ComponentPeer, GLXGraphicsConfig paramGLXGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3)
/*     */     {
/* 191 */       super(paramGLXGraphicsConfig, paramColorModel, paramInt3);
/*     */ 
/* 193 */       this.width = paramInt1;
/* 194 */       this.height = paramInt2;
/* 195 */       this.offscreenImage = paramImage;
/*     */ 
/* 197 */       initSurface(paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 201 */       return restoreContents(this.offscreenImage);
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 205 */       if (this.type == 4) {
/* 206 */         Rectangle localRectangle = this.peer.getBounds();
/* 207 */         localRectangle.x = (localRectangle.y = 0);
/* 208 */         return localRectangle;
/*     */       }
/* 210 */       return new Rectangle(this.width, this.height);
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 218 */       return this.offscreenImage;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class GLXVSyncOffScreenSurfaceData extends GLXSurfaceData.GLXOffScreenSurfaceData
/*     */   {
/*     */     private GLXSurfaceData.GLXOffScreenSurfaceData flipSurface;
/*     */ 
/*     */     public GLXVSyncOffScreenSurfaceData(X11ComponentPeer paramX11ComponentPeer, GLXGraphicsConfig paramGLXGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3)
/*     */     {
/* 164 */       super(paramGLXGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, paramInt3);
/* 165 */       this.flipSurface = GLXSurfaceData.createData(paramX11ComponentPeer, paramImage, 4);
/*     */     }
/*     */ 
/*     */     public SurfaceData getFlipSurface() {
/* 169 */       return this.flipSurface;
/*     */     }
/*     */ 
/*     */     public void flush()
/*     */     {
/* 174 */       this.flipSurface.flush();
/* 175 */       super.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class GLXWindowSurfaceData extends GLXSurfaceData
/*     */   {
/*     */     public GLXWindowSurfaceData(X11ComponentPeer paramX11ComponentPeer, GLXGraphicsConfig paramGLXGraphicsConfig)
/*     */     {
/* 123 */       super(paramGLXGraphicsConfig, paramX11ComponentPeer.getColorModel(), 1);
/*     */     }
/*     */ 
/*     */     public SurfaceData getReplacement() {
/* 127 */       return this.peer.getSurfaceData();
/*     */     }
/*     */ 
/*     */     public Rectangle getBounds() {
/* 131 */       Rectangle localRectangle = this.peer.getBounds();
/* 132 */       localRectangle.x = (localRectangle.y = 0);
/* 133 */       return localRectangle;
/*     */     }
/*     */ 
/*     */     public Object getDestination()
/*     */     {
/* 140 */       return this.peer.getTarget();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.opengl.GLXSurfaceData
 * JD-Core Version:    0.6.2
 */