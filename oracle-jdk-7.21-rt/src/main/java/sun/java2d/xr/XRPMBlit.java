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
/*     */ class XRPMBlit extends Blit
/*     */ {
/*     */   public XRPMBlit(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */   {
/* 155 */     super(paramSurfaceType1, CompositeType.AnyAlpha, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
/*     */     try {
/* 160 */       SunToolkit.awtLock();
/*     */ 
/* 162 */       XRSurfaceData localXRSurfaceData1 = (XRSurfaceData)paramSurfaceData2;
/* 163 */       localXRSurfaceData1.validateAsDestination(null, paramRegion);
/* 164 */       XRSurfaceData localXRSurfaceData2 = (XRSurfaceData)paramSurfaceData1;
/* 165 */       localXRSurfaceData2.validateAsSource(null, 0, 0);
/*     */ 
/* 167 */       localXRSurfaceData1.maskBuffer.validateCompositeState(paramComposite, null, null, null);
/*     */ 
/* 169 */       localXRSurfaceData1.maskBuffer.compositeBlit(localXRSurfaceData2, localXRSurfaceData1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*     */     } finally {
/* 171 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRPMBlit
 * JD-Core Version:    0.6.2
 */