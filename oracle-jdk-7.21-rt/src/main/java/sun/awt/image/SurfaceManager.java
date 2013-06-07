/*     */ package sun.awt.image;
/*     */ 
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.ImageCapabilities;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.SurfaceDataProxy;
/*     */ 
/*     */ public abstract class SurfaceManager
/*     */ {
/*     */   private static ImageAccessor imgaccessor;
/*     */   private ConcurrentHashMap cacheMap;
/*     */ 
/*     */   public static void setImageAccessor(ImageAccessor paramImageAccessor)
/*     */   {
/*  61 */     if (imgaccessor != null) {
/*  62 */       throw new InternalError("Attempt to set ImageAccessor twice");
/*     */     }
/*  64 */     imgaccessor = paramImageAccessor;
/*     */   }
/*     */ 
/*     */   public static SurfaceManager getManager(Image paramImage)
/*     */   {
/*  71 */     Object localObject = imgaccessor.getSurfaceManager(paramImage);
/*  72 */     if (localObject == null)
/*     */     {
/*     */       try
/*     */       {
/*  77 */         BufferedImage localBufferedImage = (BufferedImage)paramImage;
/*  78 */         localObject = new BufImgSurfaceManager(localBufferedImage);
/*  79 */         setManager(localBufferedImage, (SurfaceManager)localObject);
/*     */       } catch (ClassCastException localClassCastException) {
/*  81 */         throw new IllegalArgumentException("Invalid Image variant");
/*     */       }
/*     */     }
/*  84 */     return localObject;
/*     */   }
/*     */ 
/*     */   public static void setManager(Image paramImage, SurfaceManager paramSurfaceManager) {
/*  88 */     imgaccessor.setSurfaceManager(paramImage, paramSurfaceManager);
/*     */   }
/*     */ 
/*     */   public Object getCacheData(Object paramObject)
/*     */   {
/* 114 */     return this.cacheMap == null ? null : this.cacheMap.get(paramObject);
/*     */   }
/*     */ 
/*     */   public void setCacheData(Object paramObject1, Object paramObject2)
/*     */   {
/* 123 */     if (this.cacheMap == null) {
/* 124 */       synchronized (this) {
/* 125 */         if (this.cacheMap == null) {
/* 126 */           this.cacheMap = new ConcurrentHashMap(2);
/*     */         }
/*     */       }
/*     */     }
/* 130 */     this.cacheMap.put(paramObject1, paramObject2);
/*     */   }
/*     */ 
/*     */   public abstract SurfaceData getPrimarySurfaceData();
/*     */ 
/*     */   public abstract SurfaceData restoreContents();
/*     */ 
/*     */   public void acceleratedSurfaceLost()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
/*     */   {
/* 184 */     return new ImageCapabilitiesGc(paramGraphicsConfiguration);
/*     */   }
/*     */ 
/*     */   public synchronized void flush()
/*     */   {
/* 243 */     flush(false);
/*     */   }
/*     */ 
/*     */   synchronized void flush(boolean paramBoolean) {
/* 247 */     if (this.cacheMap != null) {
/* 248 */       Iterator localIterator = this.cacheMap.values().iterator();
/* 249 */       while (localIterator.hasNext()) {
/* 250 */         Object localObject = localIterator.next();
/* 251 */         if (((localObject instanceof FlushableCacheData)) && 
/* 252 */           (((FlushableCacheData)localObject).flush(paramBoolean)))
/* 253 */           localIterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAccelerationPriority(float paramFloat)
/*     */   {
/* 286 */     if (paramFloat == 0.0F)
/* 287 */       flush(true);
/*     */   }
/*     */ 
/*     */   public static abstract interface FlushableCacheData
/*     */   {
/*     */     public abstract boolean flush(boolean paramBoolean);
/*     */   }
/*     */ 
/*     */   public static abstract class ImageAccessor
/*     */   {
/*     */     public abstract SurfaceManager getSurfaceManager(Image paramImage);
/*     */ 
/*     */     public abstract void setSurfaceManager(Image paramImage, SurfaceManager paramSurfaceManager);
/*     */   }
/*     */ 
/*     */   class ImageCapabilitiesGc extends ImageCapabilities
/*     */   {
/*     */     GraphicsConfiguration gc;
/*     */ 
/*     */     public ImageCapabilitiesGc(GraphicsConfiguration arg2)
/*     */     {
/* 191 */       super();
/*     */       Object localObject;
/* 192 */       this.gc = localObject;
/*     */     }
/*     */ 
/*     */     public boolean isAccelerated()
/*     */     {
/* 199 */       GraphicsConfiguration localGraphicsConfiguration = this.gc;
/* 200 */       if (localGraphicsConfiguration == null) {
/* 201 */         localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
/*     */       }
/*     */ 
/* 204 */       if ((localGraphicsConfiguration instanceof SurfaceManager.ProxiedGraphicsConfig)) {
/* 205 */         Object localObject = ((SurfaceManager.ProxiedGraphicsConfig)localGraphicsConfiguration).getProxyKey();
/*     */ 
/* 207 */         if (localObject != null) {
/* 208 */           SurfaceDataProxy localSurfaceDataProxy = (SurfaceDataProxy)SurfaceManager.this.getCacheData(localObject);
/*     */ 
/* 210 */           return (localSurfaceDataProxy != null) && (localSurfaceDataProxy.isAccelerated());
/*     */         }
/*     */       }
/* 213 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface ProxiedGraphicsConfig
/*     */   {
/*     */     public abstract Object getProxyKey();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.image.SurfaceManager
 * JD-Core Version:    0.6.2
 */