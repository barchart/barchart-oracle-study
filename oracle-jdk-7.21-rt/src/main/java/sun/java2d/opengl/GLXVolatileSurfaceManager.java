/*     */ package sun.java2d.opengl;
/*     */ 
/*     */ import java.awt.BufferCapabilities.FlipContents;
/*     */ import java.awt.Component;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.image.ColorModel;
/*     */ import sun.awt.X11ComponentPeer;
/*     */ import sun.awt.image.SunVolatileImage;
/*     */ import sun.awt.image.VolatileSurfaceManager;
/*     */ import sun.java2d.BackBufferCapsProvider;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
/*     */ import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;
/*     */ 
/*     */ public class GLXVolatileSurfaceManager extends VolatileSurfaceManager
/*     */ {
/*     */   private boolean accelerationEnabled;
/*     */ 
/*     */   public GLXVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
/*     */   {
/*  49 */     super(paramSunVolatileImage, paramObject);
/*     */ 
/*  59 */     int i = paramSunVolatileImage.getTransparency();
/*  60 */     GLXGraphicsConfig localGLXGraphicsConfig = (GLXGraphicsConfig)paramSunVolatileImage.getGraphicsConfig();
/*  61 */     this.accelerationEnabled = ((i == 1) || ((i == 3) && ((localGLXGraphicsConfig.isCapPresent(12)) || (localGLXGraphicsConfig.isCapPresent(2)))));
/*     */   }
/*     */ 
/*     */   protected boolean isAccelerationEnabled()
/*     */   {
/*  69 */     return this.accelerationEnabled;
/*     */   }
/*     */ 
/*     */   protected SurfaceData initAcceleratedSurface()
/*     */   {
/*  78 */     Component localComponent = this.vImg.getComponent();
/*  79 */     X11ComponentPeer localX11ComponentPeer = localComponent != null ? (X11ComponentPeer)localComponent.getPeer() : null;
/*     */     GLXSurfaceData.GLXOffScreenSurfaceData localGLXOffScreenSurfaceData;
/*     */     try
/*     */     {
/*  83 */       int i = 0;
/*  84 */       boolean bool = false;
/*     */       Object localObject1;
/*     */       Object localObject2;
/*  85 */       if ((this.context instanceof Boolean)) {
/*  86 */         bool = ((Boolean)this.context).booleanValue();
/*  87 */         if ((bool) && ((localX11ComponentPeer instanceof BackBufferCapsProvider))) {
/*  88 */           localObject1 = (BackBufferCapsProvider)localX11ComponentPeer;
/*     */ 
/*  90 */           localObject2 = ((BackBufferCapsProvider)localObject1).getBackBufferCaps();
/*  91 */           if ((localObject2 instanceof ExtendedBufferCapabilities)) {
/*  92 */             ExtendedBufferCapabilities localExtendedBufferCapabilities = (ExtendedBufferCapabilities)localObject2;
/*     */ 
/*  94 */             if ((localExtendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON) && (localExtendedBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED))
/*     */             {
/*  97 */               i = 1;
/*  98 */               bool = false;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 104 */       if (bool)
/*     */       {
/* 106 */         localGLXOffScreenSurfaceData = GLXSurfaceData.createData(localX11ComponentPeer, this.vImg, 4);
/*     */       } else {
/* 108 */         localObject1 = (GLXGraphicsConfig)this.vImg.getGraphicsConfig();
/*     */ 
/* 110 */         localObject2 = ((GLXGraphicsConfig)localObject1).getColorModel(this.vImg.getTransparency());
/* 111 */         int j = this.vImg.getForcedAccelSurfaceType();
/*     */ 
/* 114 */         if (j == 0) {
/* 115 */           j = ((GLXGraphicsConfig)localObject1).isCapPresent(12) ? 5 : 2;
/*     */         }
/*     */ 
/* 118 */         if (i != 0)
/* 119 */           localGLXOffScreenSurfaceData = GLXSurfaceData.createData(localX11ComponentPeer, this.vImg, j);
/*     */         else {
/* 121 */           localGLXOffScreenSurfaceData = GLXSurfaceData.createData((GLXGraphicsConfig)localObject1, this.vImg.getWidth(), this.vImg.getHeight(), (ColorModel)localObject2, this.vImg, j);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (NullPointerException localNullPointerException)
/*     */     {
/* 128 */       localGLXOffScreenSurfaceData = null;
/*     */     } catch (OutOfMemoryError localOutOfMemoryError) {
/* 130 */       localGLXOffScreenSurfaceData = null;
/*     */     }
/*     */ 
/* 133 */     return localGLXOffScreenSurfaceData;
/*     */   }
/*     */ 
/*     */   protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 138 */     return (paramGraphicsConfiguration == null) || (paramGraphicsConfiguration == this.vImg.getGraphicsConfig());
/*     */   }
/*     */ 
/*     */   public void initContents()
/*     */   {
/* 143 */     if (this.vImg.getForcedAccelSurfaceType() != 3)
/* 144 */       super.initContents();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.opengl.GLXVolatileSurfaceManager
 * JD-Core Version:    0.6.2
 */