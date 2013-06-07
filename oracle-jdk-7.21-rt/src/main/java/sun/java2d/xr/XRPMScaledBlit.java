/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Composite;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.ScaledBlit;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ class XRPMScaledBlit extends ScaledBlit
/*     */ {
/*     */   public XRPMScaledBlit(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */   {
/* 178 */     super(paramSurfaceType1, CompositeType.AnyAlpha, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       SunToolkit.awtLock();
/*     */ 
/* 189 */       XRSurfaceData localXRSurfaceData1 = (XRSurfaceData)paramSurfaceData2;
/* 190 */       localXRSurfaceData1.validateAsDestination(null, paramRegion);
/* 191 */       XRSurfaceData localXRSurfaceData2 = (XRSurfaceData)paramSurfaceData1;
/* 192 */       localXRSurfaceData1.maskBuffer.validateCompositeState(paramComposite, null, null, null);
/*     */ 
/* 194 */       double d1 = (paramDouble3 - paramDouble1) / (paramInt3 - paramInt1);
/* 195 */       double d2 = (paramDouble4 - paramDouble2) / (paramInt4 - paramInt2);
/*     */ 
/* 197 */       paramInt1 = (int)(paramInt1 * d1);
/* 198 */       paramInt3 = (int)(paramInt3 * d1);
/* 199 */       paramInt2 = (int)(paramInt2 * d2);
/* 200 */       paramInt4 = (int)(paramInt4 * d2);
/*     */ 
/* 202 */       AffineTransform localAffineTransform = AffineTransform.getScaleInstance(1.0D / d1, 1.0D / d2);
/*     */ 
/* 204 */       localXRSurfaceData2.validateAsSource(localAffineTransform, 0, 0);
/*     */ 
/* 215 */       localXRSurfaceData1.maskBuffer.compositeBlit(localXRSurfaceData2, localXRSurfaceData1, paramInt1, paramInt2, (int)paramDouble1, (int)paramDouble2, (int)(paramDouble3 - paramDouble1), (int)(paramDouble4 - paramDouble2));
/*     */     } finally {
/* 217 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRPMScaledBlit
 * JD-Core Version:    0.6.2
 */