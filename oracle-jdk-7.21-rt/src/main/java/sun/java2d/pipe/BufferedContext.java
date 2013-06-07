/*     */ package sun.java2d.pipe;
/*     */ 
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Paint;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import sun.java2d.InvalidPipeException;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.loops.XORComposite;
/*     */ import sun.java2d.pipe.hw.AccelSurface;
/*     */ 
/*     */ public abstract class BufferedContext
/*     */ {
/*     */   public static final int NO_CONTEXT_FLAGS = 0;
/*     */   public static final int SRC_IS_OPAQUE = 1;
/*     */   public static final int USE_MASK = 2;
/*     */   protected RenderQueue rq;
/*     */   protected RenderBuffer buf;
/*     */   protected static BufferedContext currentContext;
/*     */   private AccelSurface validatedSrcData;
/*     */   private AccelSurface validatedDstData;
/*     */   private Region validatedClip;
/*     */   private Composite validatedComp;
/*     */   private Paint validatedPaint;
/*     */   private boolean isValidatedPaintJustAColor;
/*     */   private int validatedRGB;
/*     */   private int validatedFlags;
/*     */   private boolean xformInUse;
/*     */   private int transX;
/*     */   private int transY;
/*     */ 
/*     */   protected BufferedContext(RenderQueue paramRenderQueue)
/*     */   {
/* 102 */     this.rq = paramRenderQueue;
/* 103 */     this.buf = paramRenderQueue.getBuffer();
/*     */   }
/*     */ 
/*     */   public static void validateContext(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt)
/*     */   {
/* 129 */     BufferedContext localBufferedContext = paramAccelSurface2.getContext();
/* 130 */     localBufferedContext.validate(paramAccelSurface1, paramAccelSurface2, paramRegion, paramComposite, paramAffineTransform, paramPaint, paramSunGraphics2D, paramInt);
/*     */   }
/*     */ 
/*     */   public static void validateContext(AccelSurface paramAccelSurface)
/*     */   {
/* 149 */     validateContext(paramAccelSurface, paramAccelSurface, null, null, null, null, null, 0);
/*     */   }
/*     */ 
/*     */   public void validate(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2, Region paramRegion, Composite paramComposite, AffineTransform paramAffineTransform, Paint paramPaint, SunGraphics2D paramSunGraphics2D, int paramInt)
/*     */   {
/* 179 */     int i = 0;
/* 180 */     int j = 0;
/*     */ 
/* 182 */     if ((!paramAccelSurface2.isValid()) || (paramAccelSurface2.isSurfaceLost()) || (paramAccelSurface1.isSurfaceLost()))
/*     */     {
/* 185 */       invalidateContext();
/* 186 */       throw new InvalidPipeException("bounds changed or surface lost");
/*     */     }
/*     */ 
/* 189 */     if ((paramPaint instanceof Color))
/*     */     {
/* 191 */       k = ((Color)paramPaint).getRGB();
/* 192 */       if (this.isValidatedPaintJustAColor) {
/* 193 */         if (k != this.validatedRGB) {
/* 194 */           this.validatedRGB = k;
/* 195 */           j = 1;
/*     */         }
/*     */       } else {
/* 198 */         this.validatedRGB = k;
/* 199 */         j = 1;
/* 200 */         this.isValidatedPaintJustAColor = true;
/*     */       }
/* 202 */     } else if (this.validatedPaint != paramPaint) {
/* 203 */       j = 1;
/*     */ 
/* 206 */       this.isValidatedPaintJustAColor = false;
/*     */     }
/*     */ 
/* 209 */     if ((currentContext != this) || (paramAccelSurface1 != this.validatedSrcData) || (paramAccelSurface2 != this.validatedDstData))
/*     */     {
/* 213 */       if (paramAccelSurface2 != this.validatedDstData)
/*     */       {
/* 216 */         i = 1;
/*     */       }
/*     */ 
/* 219 */       if (paramPaint == null)
/*     */       {
/* 223 */         j = 1;
/*     */       }
/*     */ 
/* 227 */       setSurfaces(paramAccelSurface1, paramAccelSurface2);
/*     */ 
/* 229 */       currentContext = this;
/* 230 */       this.validatedSrcData = paramAccelSurface1;
/* 231 */       this.validatedDstData = paramAccelSurface2;
/*     */     }
/*     */ 
/* 235 */     if ((paramRegion != this.validatedClip) || (i != 0)) {
/* 236 */       if (paramRegion != null) {
/* 237 */         if ((i != 0) || (this.validatedClip == null) || (!this.validatedClip.isRectangular()) || (!paramRegion.isRectangular()) || (paramRegion.getLoX() != this.validatedClip.getLoX()) || (paramRegion.getLoY() != this.validatedClip.getLoY()) || (paramRegion.getHiX() != this.validatedClip.getHiX()) || (paramRegion.getHiY() != this.validatedClip.getHiY()))
/*     */         {
/* 245 */           setClip(paramRegion);
/*     */         }
/*     */       }
/* 248 */       else resetClip();
/*     */ 
/* 250 */       this.validatedClip = paramRegion;
/*     */     }
/*     */ 
/* 256 */     if ((paramComposite != this.validatedComp) || (paramInt != this.validatedFlags)) {
/* 257 */       if (paramComposite != null)
/* 258 */         setComposite(paramComposite, paramInt);
/*     */       else {
/* 260 */         resetComposite();
/*     */       }
/*     */ 
/* 264 */       j = 1;
/* 265 */       this.validatedComp = paramComposite;
/* 266 */       this.validatedFlags = paramInt;
/*     */     }
/*     */ 
/* 270 */     int k = 0;
/* 271 */     if (paramAffineTransform == null) {
/* 272 */       if (this.xformInUse) {
/* 273 */         resetTransform();
/* 274 */         this.xformInUse = false;
/* 275 */         k = 1;
/* 276 */       } else if ((paramSunGraphics2D != null) && (
/* 277 */         (this.transX != paramSunGraphics2D.transX) || (this.transY != paramSunGraphics2D.transY))) {
/* 278 */         k = 1;
/*     */       }
/*     */ 
/* 281 */       if (paramSunGraphics2D != null) {
/* 282 */         this.transX = paramSunGraphics2D.transX;
/* 283 */         this.transY = paramSunGraphics2D.transY;
/*     */       }
/*     */     } else {
/* 286 */       setTransform(paramAffineTransform);
/* 287 */       this.xformInUse = true;
/* 288 */       k = 1;
/*     */     }
/*     */ 
/* 291 */     if ((!this.isValidatedPaintJustAColor) && (k != 0)) {
/* 292 */       j = 1;
/*     */     }
/*     */ 
/* 296 */     if (j != 0) {
/* 297 */       if (paramPaint != null)
/* 298 */         BufferedPaints.setPaint(this.rq, paramSunGraphics2D, paramPaint, paramInt);
/*     */       else {
/* 300 */         BufferedPaints.resetPaint(this.rq);
/*     */       }
/* 302 */       this.validatedPaint = paramPaint;
/*     */     }
/*     */ 
/* 307 */     paramAccelSurface2.markDirty();
/*     */   }
/*     */ 
/*     */   public void invalidateSurfaces()
/*     */   {
/* 321 */     this.validatedSrcData = null;
/* 322 */     this.validatedDstData = null;
/*     */   }
/*     */ 
/*     */   private void setSurfaces(AccelSurface paramAccelSurface1, AccelSurface paramAccelSurface2)
/*     */   {
/* 329 */     this.rq.ensureCapacityAndAlignment(20, 4);
/* 330 */     this.buf.putInt(70);
/* 331 */     this.buf.putLong(paramAccelSurface1.getNativeOps());
/* 332 */     this.buf.putLong(paramAccelSurface2.getNativeOps());
/*     */   }
/*     */ 
/*     */   private void resetClip()
/*     */   {
/* 337 */     this.rq.ensureCapacity(4);
/* 338 */     this.buf.putInt(55);
/*     */   }
/*     */ 
/*     */   private void setClip(Region paramRegion)
/*     */   {
/* 343 */     if (paramRegion.isRectangular()) {
/* 344 */       this.rq.ensureCapacity(20);
/* 345 */       this.buf.putInt(51);
/* 346 */       this.buf.putInt(paramRegion.getLoX()).putInt(paramRegion.getLoY());
/* 347 */       this.buf.putInt(paramRegion.getHiX()).putInt(paramRegion.getHiY());
/*     */     } else {
/* 349 */       this.rq.ensureCapacity(28);
/* 350 */       this.buf.putInt(52);
/* 351 */       this.buf.putInt(53);
/*     */ 
/* 353 */       int i = this.buf.position();
/* 354 */       this.buf.putInt(0);
/* 355 */       int j = 0;
/* 356 */       int k = this.buf.remaining() / 16;
/* 357 */       int[] arrayOfInt = new int[4];
/* 358 */       SpanIterator localSpanIterator = paramRegion.getSpanIterator();
/* 359 */       while (localSpanIterator.nextSpan(arrayOfInt)) {
/* 360 */         if (k == 0) {
/* 361 */           this.buf.putInt(i, j);
/* 362 */           this.rq.flushNow();
/* 363 */           this.buf.putInt(53);
/* 364 */           i = this.buf.position();
/* 365 */           this.buf.putInt(0);
/* 366 */           j = 0;
/* 367 */           k = this.buf.remaining() / 16;
/*     */         }
/* 369 */         this.buf.putInt(arrayOfInt[0]);
/* 370 */         this.buf.putInt(arrayOfInt[1]);
/* 371 */         this.buf.putInt(arrayOfInt[2]);
/* 372 */         this.buf.putInt(arrayOfInt[3]);
/* 373 */         j++;
/* 374 */         k--;
/*     */       }
/* 376 */       this.buf.putInt(i, j);
/* 377 */       this.rq.ensureCapacity(4);
/* 378 */       this.buf.putInt(54);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetComposite()
/*     */   {
/* 384 */     this.rq.ensureCapacity(4);
/* 385 */     this.buf.putInt(58);
/*     */   }
/*     */ 
/*     */   private void setComposite(Composite paramComposite, int paramInt)
/*     */   {
/* 390 */     if ((paramComposite instanceof AlphaComposite)) {
/* 391 */       AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
/* 392 */       this.rq.ensureCapacity(16);
/* 393 */       this.buf.putInt(56);
/* 394 */       this.buf.putInt(localAlphaComposite.getRule());
/* 395 */       this.buf.putFloat(localAlphaComposite.getAlpha());
/* 396 */       this.buf.putInt(paramInt);
/* 397 */     } else if ((paramComposite instanceof XORComposite)) {
/* 398 */       int i = ((XORComposite)paramComposite).getXorPixel();
/* 399 */       this.rq.ensureCapacity(8);
/* 400 */       this.buf.putInt(57);
/* 401 */       this.buf.putInt(i);
/*     */     } else {
/* 403 */       throw new InternalError("not yet implemented");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetTransform()
/*     */   {
/* 409 */     this.rq.ensureCapacity(4);
/* 410 */     this.buf.putInt(60);
/*     */   }
/*     */ 
/*     */   private void setTransform(AffineTransform paramAffineTransform)
/*     */   {
/* 415 */     this.rq.ensureCapacityAndAlignment(52, 4);
/* 416 */     this.buf.putInt(59);
/* 417 */     this.buf.putDouble(paramAffineTransform.getScaleX());
/* 418 */     this.buf.putDouble(paramAffineTransform.getShearY());
/* 419 */     this.buf.putDouble(paramAffineTransform.getShearX());
/* 420 */     this.buf.putDouble(paramAffineTransform.getScaleY());
/* 421 */     this.buf.putDouble(paramAffineTransform.getTranslateX());
/* 422 */     this.buf.putDouble(paramAffineTransform.getTranslateY());
/*     */   }
/*     */ 
/*     */   public void invalidateContext()
/*     */   {
/* 434 */     resetTransform();
/* 435 */     resetComposite();
/* 436 */     resetClip();
/* 437 */     BufferedPaints.resetPaint(this.rq);
/* 438 */     invalidateSurfaces();
/* 439 */     this.validatedComp = null;
/* 440 */     this.validatedClip = null;
/* 441 */     this.validatedPaint = null;
/* 442 */     this.isValidatedPaintJustAColor = false;
/* 443 */     this.xformInUse = false;
/*     */   }
/*     */ 
/*     */   public abstract RenderQueue getRenderQueue();
/*     */ 
/*     */   public abstract void saveState();
/*     */ 
/*     */   public abstract void restoreState();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pipe.BufferedContext
 * JD-Core Version:    0.6.2
 */