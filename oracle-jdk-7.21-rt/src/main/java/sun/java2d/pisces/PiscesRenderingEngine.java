/*     */ package sun.java2d.pisces;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Path2D;
/*     */ import java.awt.geom.Path2D.Float;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.util.Arrays;
/*     */ import sun.awt.geom.PathConsumer2D;
/*     */ import sun.java2d.pipe.AATileGenerator;
/*     */ import sun.java2d.pipe.Region;
/*     */ import sun.java2d.pipe.RenderingEngine;
/*     */ 
/*     */ public class PiscesRenderingEngine extends RenderingEngine
/*     */ {
/*     */   public Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3)
/*     */   {
/*  67 */     final Path2D.Float localFloat = new Path2D.Float();
/*     */ 
/*  69 */     strokeTo(paramShape, null, paramFloat1, NormMode.OFF, paramInt1, paramInt2, paramFloat2, paramArrayOfFloat, paramFloat3, new PathConsumer2D()
/*     */     {
/*     */       public void moveTo(float paramAnonymousFloat1, float paramAnonymousFloat2)
/*     */       {
/*  80 */         localFloat.moveTo(paramAnonymousFloat1, paramAnonymousFloat2);
/*     */       }
/*     */       public void lineTo(float paramAnonymousFloat1, float paramAnonymousFloat2) {
/*  83 */         localFloat.lineTo(paramAnonymousFloat1, paramAnonymousFloat2);
/*     */       }
/*     */       public void closePath() {
/*  86 */         localFloat.closePath();
/*     */       }
/*     */       public void pathDone() {
/*     */       }
/*     */ 
/*     */       public void curveTo(float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3, float paramAnonymousFloat4, float paramAnonymousFloat5, float paramAnonymousFloat6) {
/*  92 */         localFloat.curveTo(paramAnonymousFloat1, paramAnonymousFloat2, paramAnonymousFloat3, paramAnonymousFloat4, paramAnonymousFloat5, paramAnonymousFloat6);
/*     */       }
/*     */       public void quadTo(float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3, float paramAnonymousFloat4) {
/*  95 */         localFloat.quadTo(paramAnonymousFloat1, paramAnonymousFloat2, paramAnonymousFloat3, paramAnonymousFloat4);
/*     */       }
/*     */       public long getNativeConsumer() {
/*  98 */         throw new InternalError("Not using a native peer");
/*     */       }
/*     */     });
/* 101 */     return localFloat;
/*     */   }
/*     */ 
/*     */   public void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D)
/*     */   {
/* 139 */     NormMode localNormMode = paramBoolean2 ? NormMode.ON_NO_AA : paramBoolean3 ? NormMode.ON_WITH_AA : NormMode.OFF;
/*     */ 
/* 142 */     strokeTo(paramShape, paramAffineTransform, paramBasicStroke, paramBoolean1, localNormMode, paramBoolean3, paramPathConsumer2D);
/*     */   }
/*     */ 
/*     */   void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, NormMode paramNormMode, boolean paramBoolean2, PathConsumer2D paramPathConsumer2D)
/*     */   {
/*     */     float f;
/* 154 */     if (paramBoolean1) {
/* 155 */       if (paramBoolean2)
/* 156 */         f = userSpaceLineWidth(paramAffineTransform, 0.5F);
/*     */       else
/* 158 */         f = userSpaceLineWidth(paramAffineTransform, 1.0F);
/*     */     }
/*     */     else {
/* 161 */       f = paramBasicStroke.getLineWidth();
/*     */     }
/* 163 */     strokeTo(paramShape, paramAffineTransform, f, paramNormMode, paramBasicStroke.getEndCap(), paramBasicStroke.getLineJoin(), paramBasicStroke.getMiterLimit(), paramBasicStroke.getDashArray(), paramBasicStroke.getDashPhase(), paramPathConsumer2D);
/*     */   }
/*     */ 
/*     */   private float userSpaceLineWidth(AffineTransform paramAffineTransform, float paramFloat)
/*     */   {
/*     */     double d1;
/* 179 */     if ((paramAffineTransform.getType() & 0x24) != 0)
/*     */     {
/* 181 */       d1 = Math.sqrt(paramAffineTransform.getDeterminant());
/*     */     }
/*     */     else {
/* 184 */       double d2 = paramAffineTransform.getScaleX();
/* 185 */       double d3 = paramAffineTransform.getShearX();
/* 186 */       double d4 = paramAffineTransform.getShearY();
/* 187 */       double d5 = paramAffineTransform.getScaleY();
/*     */ 
/* 204 */       double d6 = d2 * d2 + d4 * d4;
/* 205 */       double d7 = 2.0D * (d2 * d3 + d4 * d5);
/* 206 */       double d8 = d3 * d3 + d5 * d5;
/*     */ 
/* 231 */       double d9 = Math.sqrt(d7 * d7 + (d6 - d8) * (d6 - d8));
/*     */ 
/* 233 */       double d10 = (d6 + d8 + d9) / 2.0D;
/*     */ 
/* 235 */       d1 = Math.sqrt(d10);
/*     */     }
/*     */ 
/* 238 */     return (float)(paramFloat / d1);
/*     */   }
/*     */ 
/*     */   void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, float paramFloat1, NormMode paramNormMode, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3, PathConsumer2D paramPathConsumer2D)
/*     */   {
/* 267 */     AffineTransform localAffineTransform1 = null;
/* 268 */     AffineTransform localAffineTransform2 = null;
/*     */ 
/* 270 */     Object localObject = null;
/*     */ 
/* 272 */     if ((paramAffineTransform != null) && (!paramAffineTransform.isIdentity())) {
/* 273 */       double d1 = paramAffineTransform.getScaleX();
/* 274 */       double d2 = paramAffineTransform.getShearX();
/* 275 */       double d3 = paramAffineTransform.getShearY();
/* 276 */       double d4 = paramAffineTransform.getScaleY();
/* 277 */       double d5 = d1 * d4 - d3 * d2;
/* 278 */       if (Math.abs(d5) <= 2.802596928649634E-45D)
/*     */       {
/* 290 */         paramPathConsumer2D.moveTo(0.0F, 0.0F);
/* 291 */         paramPathConsumer2D.pathDone();
/* 292 */         return;
/*     */       }
/*     */ 
/* 301 */       if ((nearZero(d1 * d2 + d3 * d4, 2)) && (nearZero(d1 * d1 + d3 * d3 - (d2 * d2 + d4 * d4), 2))) {
/* 302 */         double d6 = Math.sqrt(d1 * d1 + d3 * d3);
/* 303 */         if (paramArrayOfFloat != null) {
/* 304 */           paramArrayOfFloat = Arrays.copyOf(paramArrayOfFloat, paramArrayOfFloat.length);
/* 305 */           for (int i = 0; i < paramArrayOfFloat.length; i++) {
/* 306 */             paramArrayOfFloat[i] = ((float)(d6 * paramArrayOfFloat[i]));
/*     */           }
/* 308 */           paramFloat3 = (float)(d6 * paramFloat3);
/*     */         }
/* 310 */         paramFloat1 = (float)(d6 * paramFloat1);
/* 311 */         localObject = paramShape.getPathIterator(paramAffineTransform);
/* 312 */         if (paramNormMode != NormMode.OFF) {
/* 313 */           localObject = new NormalizingPathIterator((PathIterator)localObject, paramNormMode);
/*     */         }
/*     */ 
/*     */       }
/* 319 */       else if (paramNormMode != NormMode.OFF) {
/* 320 */         localAffineTransform1 = paramAffineTransform;
/* 321 */         localObject = paramShape.getPathIterator(paramAffineTransform);
/* 322 */         localObject = new NormalizingPathIterator((PathIterator)localObject, paramNormMode);
/*     */       }
/*     */       else
/*     */       {
/* 336 */         localAffineTransform2 = paramAffineTransform;
/* 337 */         localObject = paramShape.getPathIterator(null);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 346 */       localObject = paramShape.getPathIterator(null);
/* 347 */       if (paramNormMode != NormMode.OFF) {
/* 348 */         localObject = new NormalizingPathIterator((PathIterator)localObject, paramNormMode);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 356 */     paramPathConsumer2D = TransformingPathConsumer2D.transformConsumer(paramPathConsumer2D, localAffineTransform2);
/* 357 */     paramPathConsumer2D = TransformingPathConsumer2D.deltaTransformConsumer(paramPathConsumer2D, localAffineTransform1);
/* 358 */     paramPathConsumer2D = new Stroker(paramPathConsumer2D, paramFloat1, paramInt1, paramInt2, paramFloat2);
/* 359 */     if (paramArrayOfFloat != null) {
/* 360 */       paramPathConsumer2D = new Dasher(paramPathConsumer2D, paramArrayOfFloat, paramFloat3);
/*     */     }
/* 362 */     paramPathConsumer2D = TransformingPathConsumer2D.inverseDeltaTransformConsumer(paramPathConsumer2D, localAffineTransform1);
/* 363 */     pathTo((PathIterator)localObject, paramPathConsumer2D);
/*     */   }
/*     */ 
/*     */   private static boolean nearZero(double paramDouble, int paramInt) {
/* 367 */     return Math.abs(paramDouble) < paramInt * Math.ulp(paramDouble);
/*     */   }
/*     */ 
/*     */   static void pathTo(PathIterator paramPathIterator, PathConsumer2D paramPathConsumer2D)
/*     */   {
/* 484 */     RenderingEngine.feedConsumer(paramPathIterator, paramPathConsumer2D);
/* 485 */     paramPathConsumer2D.pathDone();
/*     */   }
/*     */ 
/*     */   public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
/*     */   {
/* 544 */     NormMode localNormMode = paramBoolean2 ? NormMode.ON_WITH_AA : NormMode.OFF;
/*     */     Renderer localRenderer;
/* 545 */     if (paramBasicStroke == null)
/*     */     {
/* 547 */       if (paramBoolean2)
/* 548 */         localObject = new NormalizingPathIterator(paramShape.getPathIterator(paramAffineTransform), localNormMode);
/*     */       else {
/* 550 */         localObject = paramShape.getPathIterator(paramAffineTransform);
/*     */       }
/* 552 */       localRenderer = new Renderer(3, 3, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getWidth(), paramRegion.getHeight(), ((PathIterator)localObject).getWindingRule());
/*     */ 
/* 556 */       pathTo((PathIterator)localObject, localRenderer);
/*     */     } else {
/* 558 */       localRenderer = new Renderer(3, 3, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getWidth(), paramRegion.getHeight(), 1);
/*     */ 
/* 562 */       strokeTo(paramShape, paramAffineTransform, paramBasicStroke, paramBoolean1, localNormMode, true, localRenderer);
/*     */     }
/* 564 */     localRenderer.endRendering();
/* 565 */     Object localObject = new PiscesTileGenerator(localRenderer, localRenderer.MAX_AA_ALPHA);
/* 566 */     ((PiscesTileGenerator)localObject).getBbox(paramArrayOfInt);
/* 567 */     return localObject;
/*     */   }
/*     */ 
/*     */   public AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt)
/*     */   {
/* 579 */     int i = (paramDouble7 > 0.0D) && (paramDouble8 > 0.0D) ? 1 : 0;
/*     */     double d1;
/*     */     double d2;
/*     */     double d3;
/*     */     double d4;
/* 581 */     if (i != 0) {
/* 582 */       d1 = paramDouble3 * paramDouble7;
/* 583 */       d2 = paramDouble4 * paramDouble7;
/* 584 */       d3 = paramDouble5 * paramDouble8;
/* 585 */       d4 = paramDouble6 * paramDouble8;
/* 586 */       paramDouble1 -= (d1 + d3) / 2.0D;
/* 587 */       paramDouble2 -= (d2 + d4) / 2.0D;
/* 588 */       paramDouble3 += d1;
/* 589 */       paramDouble4 += d2;
/* 590 */       paramDouble5 += d3;
/* 591 */       paramDouble6 += d4;
/* 592 */       if ((paramDouble7 > 1.0D) && (paramDouble8 > 1.0D))
/*     */       {
/* 594 */         i = 0;
/*     */       }
/*     */     } else {
/* 597 */       d1 = d2 = d3 = d4 = 0.0D;
/*     */     }
/*     */ 
/* 600 */     Renderer localRenderer = new Renderer(3, 3, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getWidth(), paramRegion.getHeight(), 0);
/*     */ 
/* 605 */     localRenderer.moveTo((float)paramDouble1, (float)paramDouble2);
/* 606 */     localRenderer.lineTo((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
/* 607 */     localRenderer.lineTo((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
/* 608 */     localRenderer.lineTo((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
/* 609 */     localRenderer.closePath();
/*     */ 
/* 611 */     if (i != 0) {
/* 612 */       paramDouble1 += d1 + d3;
/* 613 */       paramDouble2 += d2 + d4;
/* 614 */       paramDouble3 -= 2.0D * d1;
/* 615 */       paramDouble4 -= 2.0D * d2;
/* 616 */       paramDouble5 -= 2.0D * d3;
/* 617 */       paramDouble6 -= 2.0D * d4;
/* 618 */       localRenderer.moveTo((float)paramDouble1, (float)paramDouble2);
/* 619 */       localRenderer.lineTo((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
/* 620 */       localRenderer.lineTo((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
/* 621 */       localRenderer.lineTo((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
/* 622 */       localRenderer.closePath();
/*     */     }
/*     */ 
/* 625 */     localRenderer.pathDone();
/*     */ 
/* 627 */     localRenderer.endRendering();
/* 628 */     PiscesTileGenerator localPiscesTileGenerator = new PiscesTileGenerator(localRenderer, localRenderer.MAX_AA_ALPHA);
/* 629 */     localPiscesTileGenerator.getBbox(paramArrayOfInt);
/* 630 */     return localPiscesTileGenerator;
/*     */   }
/*     */ 
/*     */   public float getMinimumAAPenSize()
/*     */   {
/* 639 */     return 0.5F;
/*     */   }
/*     */ 
/*     */   private static enum NormMode
/*     */   {
/*  40 */     OFF, ON_NO_AA, ON_WITH_AA;
/*     */   }
/*     */ 
/*     */   private static class NormalizingPathIterator
/*     */     implements PathIterator
/*     */   {
/*     */     private final PathIterator src;
/*     */     private float curx_adjust;
/*     */     private float cury_adjust;
/*     */     private float movx_adjust;
/*     */     private float movy_adjust;
/*     */     private final float lval;
/*     */     private final float rval;
/*     */ 
/*     */     NormalizingPathIterator(PathIterator paramPathIterator, PiscesRenderingEngine.NormMode paramNormMode)
/*     */     {
/* 383 */       this.src = paramPathIterator;
/* 384 */       switch (PiscesRenderingEngine.2.$SwitchMap$sun$java2d$pisces$PiscesRenderingEngine$NormMode[paramNormMode.ordinal()])
/*     */       {
/*     */       case 1:
/* 387 */         this.lval = (this.rval = 0.25F);
/* 388 */         break;
/*     */       case 2:
/* 391 */         this.lval = 0.0F;
/* 392 */         this.rval = 0.5F;
/* 393 */         break;
/*     */       case 3:
/* 395 */         throw new InternalError("A NormalizingPathIterator should not be created if no normalization is being done");
/*     */       default:
/* 398 */         throw new InternalError("Unrecognized normalization mode");
/*     */       }
/*     */     }
/*     */ 
/*     */     public int currentSegment(float[] paramArrayOfFloat) {
/* 403 */       int i = this.src.currentSegment(paramArrayOfFloat);
/*     */       int j;
/* 406 */       switch (i) {
/*     */       case 3:
/* 408 */         j = 4;
/* 409 */         break;
/*     */       case 2:
/* 411 */         j = 2;
/* 412 */         break;
/*     */       case 0:
/*     */       case 1:
/* 415 */         j = 0;
/* 416 */         break;
/*     */       case 4:
/* 419 */         this.curx_adjust = this.movx_adjust;
/* 420 */         this.cury_adjust = this.movy_adjust;
/* 421 */         return i;
/*     */       default:
/* 423 */         throw new InternalError("Unrecognized curve type");
/*     */       }
/*     */ 
/* 427 */       float f1 = (float)Math.floor(paramArrayOfFloat[j] + this.lval) + this.rval - paramArrayOfFloat[j];
/*     */ 
/* 429 */       float f2 = (float)Math.floor(paramArrayOfFloat[(j + 1)] + this.lval) + this.rval - paramArrayOfFloat[(j + 1)];
/*     */ 
/* 432 */       paramArrayOfFloat[j] += f1;
/* 433 */       paramArrayOfFloat[(j + 1)] += f2;
/*     */ 
/* 436 */       switch (i) {
/*     */       case 3:
/* 438 */         paramArrayOfFloat[0] += this.curx_adjust;
/* 439 */         paramArrayOfFloat[1] += this.cury_adjust;
/* 440 */         paramArrayOfFloat[2] += f1;
/* 441 */         paramArrayOfFloat[3] += f2;
/* 442 */         break;
/*     */       case 2:
/* 444 */         paramArrayOfFloat[0] += (this.curx_adjust + f1) / 2.0F;
/* 445 */         paramArrayOfFloat[1] += (this.cury_adjust + f2) / 2.0F;
/* 446 */         break;
/*     */       case 1:
/* 448 */         break;
/*     */       case 0:
/* 450 */         this.movx_adjust = f1;
/* 451 */         this.movy_adjust = f2;
/* 452 */         break;
/*     */       case 4:
/* 454 */         throw new InternalError("This should be handled earlier.");
/*     */       }
/* 456 */       this.curx_adjust = f1;
/* 457 */       this.cury_adjust = f2;
/* 458 */       return i;
/*     */     }
/*     */ 
/*     */     public int currentSegment(double[] paramArrayOfDouble) {
/* 462 */       float[] arrayOfFloat = new float[6];
/* 463 */       int i = currentSegment(arrayOfFloat);
/* 464 */       for (int j = 0; j < 6; j++) {
/* 465 */         paramArrayOfDouble[j] = arrayOfFloat[j];
/*     */       }
/* 467 */       return i;
/*     */     }
/*     */ 
/*     */     public int getWindingRule() {
/* 471 */       return this.src.getWindingRule();
/*     */     }
/*     */ 
/*     */     public boolean isDone() {
/* 475 */       return this.src.isDone();
/*     */     }
/*     */ 
/*     */     public void next() {
/* 479 */       this.src.next();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pisces.PiscesRenderingEngine
 * JD-Core Version:    0.6.2
 */