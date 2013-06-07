/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.loops.TransformBlit;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ class XrSwToPMTransformedBlit extends TransformBlit
/*     */ {
/*     */   TransformBlit pmToSurfaceBlit;
/*     */ 
/*     */   XrSwToPMTransformedBlit(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */   {
/* 386 */     super(paramSurfaceType1, CompositeType.AnyAlpha, paramSurfaceType2);
/* 387 */     this.pmToSurfaceBlit = new XRPMTransformedBlit(paramSurfaceType2, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/*     */     try {
/* 393 */       SunToolkit.awtLock();
/*     */ 
/* 395 */       XRSurfaceData localXRSurfaceData = XRPMBlitLoops.cacheToTmpSurface(paramSurfaceData1, (XRSurfaceData)paramSurfaceData2, paramInt6, paramInt7, paramInt2, paramInt3);
/* 396 */       this.pmToSurfaceBlit.Transform(localXRSurfaceData, paramSurfaceData2, paramComposite, paramRegion, paramAffineTransform, paramInt1, 0, 0, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */     } finally {
/* 398 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XrSwToPMTransformedBlit
 * JD-Core Version:    0.6.2
 */