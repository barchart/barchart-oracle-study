/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.io.PrintStream;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ import sun.java2d.loops.TransformBlit;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ class XRPMTransformedBlit extends TransformBlit
/*     */ {
/*     */   public XRPMTransformedBlit(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2)
/*     */   {
/* 230 */     super(paramSurfaceType1, CompositeType.AnyAlpha, paramSurfaceType2);
/*     */   }
/*     */ 
/*     */   public Rectangle getCompositeBounds(AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 240 */     double[] arrayOfDouble = new double[8];
/* 241 */     arrayOfDouble[0] = paramInt1;
/* 242 */     arrayOfDouble[1] = paramInt2;
/* 243 */     arrayOfDouble[2] = (paramInt1 + paramInt3);
/* 244 */     arrayOfDouble[3] = paramInt2;
/* 245 */     arrayOfDouble[4] = (paramInt1 + paramInt3);
/* 246 */     arrayOfDouble[5] = (paramInt2 + paramInt4);
/* 247 */     arrayOfDouble[6] = paramInt1;
/* 248 */     arrayOfDouble[7] = (paramInt2 + paramInt4);
/*     */ 
/* 250 */     paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/*     */ 
/* 252 */     double d1 = Math.min(arrayOfDouble[0], Math.min(arrayOfDouble[2], Math.min(arrayOfDouble[4], arrayOfDouble[6])));
/* 253 */     double d2 = Math.min(arrayOfDouble[1], Math.min(arrayOfDouble[3], Math.min(arrayOfDouble[5], arrayOfDouble[7])));
/* 254 */     double d3 = Math.max(arrayOfDouble[0], Math.max(arrayOfDouble[2], Math.max(arrayOfDouble[4], arrayOfDouble[6])));
/* 255 */     double d4 = Math.max(arrayOfDouble[1], Math.max(arrayOfDouble[3], Math.max(arrayOfDouble[5], arrayOfDouble[7])));
/*     */ 
/* 257 */     d1 = Math.floor(d1);
/* 258 */     d2 = Math.floor(d2);
/* 259 */     d3 = Math.ceil(d3);
/* 260 */     d4 = Math.ceil(d4);
/*     */ 
/* 262 */     return new Rectangle((int)d1, (int)d2, (int)(d3 - d1), (int)(d4 - d2));
/*     */   }
/*     */ 
/*     */   public void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/*     */     try {
/* 268 */       SunToolkit.awtLock();
/*     */ 
/* 270 */       int i = XRUtils.ATransOpToXRQuality(paramInt1);
/*     */ 
/* 272 */       XRSurfaceData localXRSurfaceData1 = (XRSurfaceData)paramSurfaceData2;
/* 273 */       localXRSurfaceData1.validateAsDestination(null, paramRegion);
/* 274 */       XRSurfaceData localXRSurfaceData2 = (XRSurfaceData)paramSurfaceData1;
/* 275 */       localXRSurfaceData1.maskBuffer.validateCompositeState(paramComposite, null, null, null);
/*     */ 
/* 277 */       Rectangle localRectangle = getCompositeBounds(paramAffineTransform, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */ 
/* 279 */       AffineTransform localAffineTransform1 = AffineTransform.getTranslateInstance(-localRectangle.x, -localRectangle.y);
/* 280 */       localAffineTransform1.concatenate(paramAffineTransform);
/* 281 */       AffineTransform localAffineTransform2 = (AffineTransform)localAffineTransform1.clone();
/*     */ 
/* 283 */       localAffineTransform1.translate(-paramInt2, -paramInt3);
/*     */       try
/*     */       {
/* 286 */         localAffineTransform1.invert();
/*     */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 288 */         localAffineTransform1.setToIdentity();
/* 289 */         System.err.println("Reseted to identity!");
/*     */       }
/*     */ 
/* 292 */       boolean bool = isMaskOmittable(localAffineTransform1, paramComposite, i);
/*     */ 
/* 294 */       if (!bool) {
/* 295 */         XRMaskImage localXRMaskImage = localXRSurfaceData2.maskBuffer.getMaskImage();
/*     */ 
/* 297 */         localXRSurfaceData2.validateAsSource(localAffineTransform1, 2, i);
/* 298 */         int k = localXRMaskImage.prepareBlitMask(localXRSurfaceData1, localAffineTransform2, paramInt6, paramInt7);
/* 299 */         localXRSurfaceData1.maskBuffer.con.renderComposite(XRCompositeManager.getInstance(localXRSurfaceData2).getCompRule(), localXRSurfaceData2.picture, k, localXRSurfaceData1.picture, 0, 0, 0, 0, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*     */       }
/*     */       else {
/* 302 */         int j = i == 0 ? 0 : 2;
/*     */ 
/* 304 */         localXRSurfaceData2.validateAsSource(localAffineTransform1, j, i);
/* 305 */         localXRSurfaceData1.maskBuffer.compositeBlit(localXRSurfaceData2, localXRSurfaceData1, 0, 0, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
/*     */       }
/*     */     } finally {
/* 308 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static boolean isMaskOmittable(AffineTransform paramAffineTransform, Composite paramComposite, int paramInt)
/*     */   {
/* 314 */     return ((paramInt == 0) || ((paramAffineTransform.getTranslateX() == (int)paramAffineTransform.getTranslateX()) && (paramAffineTransform.getTranslateY() == (int)paramAffineTransform.getTranslateY()) && (((paramAffineTransform.getShearX() == 0.0D) && (paramAffineTransform.getShearY() == 0.0D)) || (paramAffineTransform.getShearX() == -paramAffineTransform.getShearY())))) && (((AlphaComposite)paramComposite).getAlpha() == 1.0F);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRPMTransformedBlit
 * JD-Core Version:    0.6.2
 */