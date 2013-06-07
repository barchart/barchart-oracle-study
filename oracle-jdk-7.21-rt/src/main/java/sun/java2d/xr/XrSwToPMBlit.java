/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.Blit;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ class XrSwToPMBlit extends Blit
/*     */ {
/*     */   Blit pmToSurfaceBlit;
/*     */ 
/*     */   XrSwToPMBlit(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */   {
/* 333 */     super(paramSurfaceType1, CompositeType.AnyAlpha, paramSurfaceType2);
/* 334 */     this.pmToSurfaceBlit = new XRPMBlit(paramSurfaceType2, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*     */   {
/*     */     Object localObject1;
/* 341 */     if ((CompositeType.SrcOverNoEa.equals(paramComposite)) && (paramSurfaceData1.getTransparency() == 1)) {
/* 342 */       localObject1 = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, paramSurfaceData2.getSurfaceType());
/* 343 */       ((Blit)localObject1).Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     } else {
/*     */       try {
/* 346 */         SunToolkit.awtLock();
/*     */ 
/* 348 */         localObject1 = XRPMBlitLoops.cacheToTmpSurface(paramSurfaceData1, (XRSurfaceData)paramSurfaceData2, paramInt5, paramInt6, paramInt1, paramInt2);
/* 349 */         this.pmToSurfaceBlit.Blit((SurfaceData)localObject1, paramSurfaceData2, paramComposite, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */       } finally {
/* 351 */         SunToolkit.awtUnlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XrSwToPMBlit
 * JD-Core Version:    0.6.2
 */