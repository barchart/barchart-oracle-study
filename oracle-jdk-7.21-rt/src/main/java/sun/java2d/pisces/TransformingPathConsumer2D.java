/*     */ package sun.java2d.pisces;
/*     */ 
/*     */ import java.awt.geom.AffineTransform;
/*     */ import sun.awt.geom.PathConsumer2D;
/*     */ 
/*     */ final class TransformingPathConsumer2D
/*     */ {
/*     */   public static PathConsumer2D transformConsumer(PathConsumer2D paramPathConsumer2D, AffineTransform paramAffineTransform)
/*     */   {
/*  36 */     if (paramAffineTransform == null) {
/*  37 */       return paramPathConsumer2D;
/*     */     }
/*  39 */     float f1 = (float)paramAffineTransform.getScaleX();
/*  40 */     float f2 = (float)paramAffineTransform.getShearX();
/*  41 */     float f3 = (float)paramAffineTransform.getTranslateX();
/*  42 */     float f4 = (float)paramAffineTransform.getShearY();
/*  43 */     float f5 = (float)paramAffineTransform.getScaleY();
/*  44 */     float f6 = (float)paramAffineTransform.getTranslateY();
/*  45 */     if ((f2 == 0.0F) && (f4 == 0.0F)) {
/*  46 */       if ((f1 == 1.0F) && (f5 == 1.0F)) {
/*  47 */         if ((f3 == 0.0F) && (f6 == 0.0F)) {
/*  48 */           return paramPathConsumer2D;
/*     */         }
/*  50 */         return new TranslateFilter(paramPathConsumer2D, f3, f6);
/*     */       }
/*     */ 
/*  53 */       if ((f3 == 0.0F) && (f6 == 0.0F)) {
/*  54 */         return new DeltaScaleFilter(paramPathConsumer2D, f1, f5);
/*     */       }
/*  56 */       return new ScaleFilter(paramPathConsumer2D, f1, f5, f3, f6);
/*     */     }
/*     */ 
/*  59 */     if ((f3 == 0.0F) && (f6 == 0.0F)) {
/*  60 */       return new DeltaTransformFilter(paramPathConsumer2D, f1, f2, f4, f5);
/*     */     }
/*  62 */     return new TransformFilter(paramPathConsumer2D, f1, f2, f3, f4, f5, f6);
/*     */   }
/*     */ 
/*     */   public static PathConsumer2D deltaTransformConsumer(PathConsumer2D paramPathConsumer2D, AffineTransform paramAffineTransform)
/*     */   {
/*  70 */     if (paramAffineTransform == null) {
/*  71 */       return paramPathConsumer2D;
/*     */     }
/*  73 */     float f1 = (float)paramAffineTransform.getScaleX();
/*  74 */     float f2 = (float)paramAffineTransform.getShearX();
/*  75 */     float f3 = (float)paramAffineTransform.getShearY();
/*  76 */     float f4 = (float)paramAffineTransform.getScaleY();
/*  77 */     if ((f2 == 0.0F) && (f3 == 0.0F)) {
/*  78 */       if ((f1 == 1.0F) && (f4 == 1.0F)) {
/*  79 */         return paramPathConsumer2D;
/*     */       }
/*  81 */       return new DeltaScaleFilter(paramPathConsumer2D, f1, f4);
/*     */     }
/*     */ 
/*  84 */     return new DeltaTransformFilter(paramPathConsumer2D, f1, f2, f3, f4);
/*     */   }
/*     */ 
/*     */   public static PathConsumer2D inverseDeltaTransformConsumer(PathConsumer2D paramPathConsumer2D, AffineTransform paramAffineTransform)
/*     */   {
/*  92 */     if (paramAffineTransform == null) {
/*  93 */       return paramPathConsumer2D;
/*     */     }
/*  95 */     float f1 = (float)paramAffineTransform.getScaleX();
/*  96 */     float f2 = (float)paramAffineTransform.getShearX();
/*  97 */     float f3 = (float)paramAffineTransform.getShearY();
/*  98 */     float f4 = (float)paramAffineTransform.getScaleY();
/*  99 */     if ((f2 == 0.0F) && (f3 == 0.0F)) {
/* 100 */       if ((f1 == 1.0F) && (f4 == 1.0F)) {
/* 101 */         return paramPathConsumer2D;
/*     */       }
/* 103 */       return new DeltaScaleFilter(paramPathConsumer2D, 1.0F / f1, 1.0F / f4);
/*     */     }
/*     */ 
/* 106 */     float f5 = f1 * f4 - f2 * f3;
/* 107 */     return new DeltaTransformFilter(paramPathConsumer2D, f4 / f5, -f2 / f5, -f3 / f5, f1 / f5);
/*     */   }
/*     */ 
/*     */   static final class DeltaScaleFilter
/*     */     implements PathConsumer2D
/*     */   {
/*     */     private final float sx;
/*     */     private final float sy;
/*     */     private final PathConsumer2D out;
/*     */ 
/*     */     public DeltaScaleFilter(PathConsumer2D paramPathConsumer2D, float paramFloat1, float paramFloat2)
/*     */     {
/* 290 */       this.sx = paramFloat1;
/* 291 */       this.sy = paramFloat2;
/* 292 */       this.out = paramPathConsumer2D;
/*     */     }
/*     */ 
/*     */     public void moveTo(float paramFloat1, float paramFloat2) {
/* 296 */       this.out.moveTo(paramFloat1 * this.sx, paramFloat2 * this.sy);
/*     */     }
/*     */ 
/*     */     public void lineTo(float paramFloat1, float paramFloat2) {
/* 300 */       this.out.lineTo(paramFloat1 * this.sx, paramFloat2 * this.sy);
/*     */     }
/*     */ 
/*     */     public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 306 */       this.out.quadTo(paramFloat1 * this.sx, paramFloat2 * this.sy, paramFloat3 * this.sx, paramFloat4 * this.sy);
/*     */     }
/*     */ 
/*     */     public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */     {
/* 314 */       this.out.curveTo(paramFloat1 * this.sx, paramFloat2 * this.sy, paramFloat3 * this.sx, paramFloat4 * this.sy, paramFloat5 * this.sx, paramFloat6 * this.sy);
/*     */     }
/*     */ 
/*     */     public void closePath()
/*     */     {
/* 320 */       this.out.closePath();
/*     */     }
/*     */ 
/*     */     public void pathDone() {
/* 324 */       this.out.pathDone();
/*     */     }
/*     */ 
/*     */     public long getNativeConsumer() {
/* 328 */       return 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class DeltaTransformFilter implements PathConsumer2D {
/*     */     private PathConsumer2D out;
/*     */     private final float Mxx;
/*     */     private final float Mxy;
/*     */     private final float Myx;
/*     */     private final float Myy;
/*     */ 
/* 343 */     DeltaTransformFilter(PathConsumer2D paramPathConsumer2D, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) { this.out = paramPathConsumer2D;
/* 344 */       this.Mxx = paramFloat1;
/* 345 */       this.Mxy = paramFloat2;
/* 346 */       this.Myx = paramFloat3;
/* 347 */       this.Myy = paramFloat4; }
/*     */ 
/*     */     public void moveTo(float paramFloat1, float paramFloat2)
/*     */     {
/* 351 */       this.out.moveTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy, paramFloat1 * this.Myx + paramFloat2 * this.Myy);
/*     */     }
/*     */ 
/*     */     public void lineTo(float paramFloat1, float paramFloat2)
/*     */     {
/* 356 */       this.out.lineTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy, paramFloat1 * this.Myx + paramFloat2 * this.Myy);
/*     */     }
/*     */ 
/*     */     public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 363 */       this.out.quadTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy, paramFloat1 * this.Myx + paramFloat2 * this.Myy, paramFloat3 * this.Mxx + paramFloat4 * this.Mxy, paramFloat3 * this.Myx + paramFloat4 * this.Myy);
/*     */     }
/*     */ 
/*     */     public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */     {
/* 373 */       this.out.curveTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy, paramFloat1 * this.Myx + paramFloat2 * this.Myy, paramFloat3 * this.Mxx + paramFloat4 * this.Mxy, paramFloat3 * this.Myx + paramFloat4 * this.Myy, paramFloat5 * this.Mxx + paramFloat6 * this.Mxy, paramFloat5 * this.Myx + paramFloat6 * this.Myy);
/*     */     }
/*     */ 
/*     */     public void closePath()
/*     */     {
/* 382 */       this.out.closePath();
/*     */     }
/*     */ 
/*     */     public void pathDone() {
/* 386 */       this.out.pathDone();
/*     */     }
/*     */ 
/*     */     public long getNativeConsumer() {
/* 390 */       return 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ScaleFilter
/*     */     implements PathConsumer2D
/*     */   {
/*     */     private final PathConsumer2D out;
/*     */     private final float sx;
/*     */     private final float sy;
/*     */     private final float tx;
/*     */     private final float ty;
/*     */ 
/*     */     ScaleFilter(PathConsumer2D paramPathConsumer2D, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 175 */       this.out = paramPathConsumer2D;
/* 176 */       this.sx = paramFloat1;
/* 177 */       this.sy = paramFloat2;
/* 178 */       this.tx = paramFloat3;
/* 179 */       this.ty = paramFloat4;
/*     */     }
/*     */ 
/*     */     public void moveTo(float paramFloat1, float paramFloat2) {
/* 183 */       this.out.moveTo(paramFloat1 * this.sx + this.tx, paramFloat2 * this.sy + this.ty);
/*     */     }
/*     */ 
/*     */     public void lineTo(float paramFloat1, float paramFloat2) {
/* 187 */       this.out.lineTo(paramFloat1 * this.sx + this.tx, paramFloat2 * this.sy + this.ty);
/*     */     }
/*     */ 
/*     */     public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 193 */       this.out.quadTo(paramFloat1 * this.sx + this.tx, paramFloat2 * this.sy + this.ty, paramFloat3 * this.sx + this.tx, paramFloat4 * this.sy + this.ty);
/*     */     }
/*     */ 
/*     */     public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */     {
/* 201 */       this.out.curveTo(paramFloat1 * this.sx + this.tx, paramFloat2 * this.sy + this.ty, paramFloat3 * this.sx + this.tx, paramFloat4 * this.sy + this.ty, paramFloat5 * this.sx + this.tx, paramFloat6 * this.sy + this.ty);
/*     */     }
/*     */ 
/*     */     public void closePath()
/*     */     {
/* 207 */       this.out.closePath();
/*     */     }
/*     */ 
/*     */     public void pathDone() {
/* 211 */       this.out.pathDone();
/*     */     }
/*     */ 
/*     */     public long getNativeConsumer() {
/* 215 */       return 0L;
/*     */     }
/*     */   }
/*     */   static final class TransformFilter implements PathConsumer2D { private final PathConsumer2D out;
/*     */     private final float Mxx;
/*     */     private final float Mxy;
/*     */     private final float Mxt;
/*     */     private final float Myx;
/*     */     private final float Myy;
/*     */     private final float Myt;
/*     */ 
/* 232 */     TransformFilter(PathConsumer2D paramPathConsumer2D, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) { this.out = paramPathConsumer2D;
/* 233 */       this.Mxx = paramFloat1;
/* 234 */       this.Mxy = paramFloat2;
/* 235 */       this.Mxt = paramFloat3;
/* 236 */       this.Myx = paramFloat4;
/* 237 */       this.Myy = paramFloat5;
/* 238 */       this.Myt = paramFloat6; }
/*     */ 
/*     */     public void moveTo(float paramFloat1, float paramFloat2)
/*     */     {
/* 242 */       this.out.moveTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy + this.Mxt, paramFloat1 * this.Myx + paramFloat2 * this.Myy + this.Myt);
/*     */     }
/*     */ 
/*     */     public void lineTo(float paramFloat1, float paramFloat2)
/*     */     {
/* 247 */       this.out.lineTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy + this.Mxt, paramFloat1 * this.Myx + paramFloat2 * this.Myy + this.Myt);
/*     */     }
/*     */ 
/*     */     public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 254 */       this.out.quadTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy + this.Mxt, paramFloat1 * this.Myx + paramFloat2 * this.Myy + this.Myt, paramFloat3 * this.Mxx + paramFloat4 * this.Mxy + this.Mxt, paramFloat3 * this.Myx + paramFloat4 * this.Myy + this.Myt);
/*     */     }
/*     */ 
/*     */     public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */     {
/* 264 */       this.out.curveTo(paramFloat1 * this.Mxx + paramFloat2 * this.Mxy + this.Mxt, paramFloat1 * this.Myx + paramFloat2 * this.Myy + this.Myt, paramFloat3 * this.Mxx + paramFloat4 * this.Mxy + this.Mxt, paramFloat3 * this.Myx + paramFloat4 * this.Myy + this.Myt, paramFloat5 * this.Mxx + paramFloat6 * this.Mxy + this.Mxt, paramFloat5 * this.Myx + paramFloat6 * this.Myy + this.Myt);
/*     */     }
/*     */ 
/*     */     public void closePath()
/*     */     {
/* 273 */       this.out.closePath();
/*     */     }
/*     */ 
/*     */     public void pathDone() {
/* 277 */       this.out.pathDone();
/*     */     }
/*     */ 
/*     */     public long getNativeConsumer() {
/* 281 */       return 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class TranslateFilter
/*     */     implements PathConsumer2D
/*     */   {
/*     */     private final PathConsumer2D out;
/*     */     private final float tx;
/*     */     private final float ty;
/*     */ 
/*     */     TranslateFilter(PathConsumer2D paramPathConsumer2D, float paramFloat1, float paramFloat2)
/*     */     {
/* 123 */       this.out = paramPathConsumer2D;
/* 124 */       this.tx = paramFloat1;
/* 125 */       this.ty = paramFloat2;
/*     */     }
/*     */ 
/*     */     public void moveTo(float paramFloat1, float paramFloat2) {
/* 129 */       this.out.moveTo(paramFloat1 + this.tx, paramFloat2 + this.ty);
/*     */     }
/*     */ 
/*     */     public void lineTo(float paramFloat1, float paramFloat2) {
/* 133 */       this.out.lineTo(paramFloat1 + this.tx, paramFloat2 + this.ty);
/*     */     }
/*     */ 
/*     */     public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */     {
/* 139 */       this.out.quadTo(paramFloat1 + this.tx, paramFloat2 + this.ty, paramFloat3 + this.tx, paramFloat4 + this.ty);
/*     */     }
/*     */ 
/*     */     public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */     {
/* 147 */       this.out.curveTo(paramFloat1 + this.tx, paramFloat2 + this.ty, paramFloat3 + this.tx, paramFloat4 + this.ty, paramFloat5 + this.tx, paramFloat6 + this.ty);
/*     */     }
/*     */ 
/*     */     public void closePath()
/*     */     {
/* 153 */       this.out.closePath();
/*     */     }
/*     */ 
/*     */     public void pathDone() {
/* 157 */       this.out.pathDone();
/*     */     }
/*     */ 
/*     */     public long getNativeConsumer() {
/* 161 */       return 0L;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pisces.TransformingPathConsumer2D
 * JD-Core Version:    0.6.2
 */