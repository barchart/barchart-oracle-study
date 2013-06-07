/*     */ package sun.java2d.pisces;
/*     */ 
/*     */ import sun.awt.geom.PathConsumer2D;
/*     */ 
/*     */ final class Dasher
/*     */   implements PathConsumer2D
/*     */ {
/*     */   private final PathConsumer2D out;
/*     */   private final float[] dash;
/*     */   private final float startPhase;
/*     */   private final boolean startDashOn;
/*     */   private final int startIdx;
/*     */   private boolean starting;
/*     */   private boolean needsMoveTo;
/*     */   private int idx;
/*     */   private boolean dashOn;
/*     */   private float phase;
/*     */   private float sx;
/*     */   private float sy;
/*     */   private float x0;
/*     */   private float y0;
/*     */   private float[] curCurvepts;
/* 139 */   private float[] firstSegmentsBuffer = new float[7];
/* 140 */   private int firstSegidx = 0;
/*     */ 
/* 220 */   private LengthIterator li = null;
/*     */ 
/*     */   public Dasher(PathConsumer2D paramPathConsumer2D, float[] paramArrayOfFloat, float paramFloat)
/*     */   {
/*  70 */     if (paramFloat < 0.0F) {
/*  71 */       throw new IllegalArgumentException("phase < 0 !");
/*     */     }
/*     */ 
/*  74 */     this.out = paramPathConsumer2D;
/*     */ 
/*  77 */     int i = 0;
/*  78 */     this.dashOn = true;
/*     */     float f;
/*  80 */     while (paramFloat >= (f = paramArrayOfFloat[i])) {
/*  81 */       paramFloat -= f;
/*  82 */       i = (i + 1) % paramArrayOfFloat.length;
/*  83 */       this.dashOn = (!this.dashOn);
/*     */     }
/*     */ 
/*  86 */     this.dash = paramArrayOfFloat;
/*  87 */     this.startPhase = (this.phase = paramFloat);
/*  88 */     this.startDashOn = this.dashOn;
/*  89 */     this.startIdx = i;
/*  90 */     this.starting = true;
/*     */ 
/*  94 */     this.curCurvepts = new float[16];
/*     */   }
/*     */ 
/*     */   public void moveTo(float paramFloat1, float paramFloat2) {
/*  98 */     if (this.firstSegidx > 0) {
/*  99 */       this.out.moveTo(this.sx, this.sy);
/* 100 */       emitFirstSegments();
/*     */     }
/* 102 */     this.needsMoveTo = true;
/* 103 */     this.idx = this.startIdx;
/* 104 */     this.dashOn = this.startDashOn;
/* 105 */     this.phase = this.startPhase;
/* 106 */     this.sx = (this.x0 = paramFloat1);
/* 107 */     this.sy = (this.y0 = paramFloat2);
/* 108 */     this.starting = true;
/*     */   }
/*     */ 
/*     */   private void emitSeg(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
/* 112 */     switch (paramInt2) {
/*     */     case 8:
/* 114 */       this.out.curveTo(paramArrayOfFloat[(paramInt1 + 0)], paramArrayOfFloat[(paramInt1 + 1)], paramArrayOfFloat[(paramInt1 + 2)], paramArrayOfFloat[(paramInt1 + 3)], paramArrayOfFloat[(paramInt1 + 4)], paramArrayOfFloat[(paramInt1 + 5)]);
/*     */ 
/* 117 */       break;
/*     */     case 6:
/* 119 */       this.out.quadTo(paramArrayOfFloat[(paramInt1 + 0)], paramArrayOfFloat[(paramInt1 + 1)], paramArrayOfFloat[(paramInt1 + 2)], paramArrayOfFloat[(paramInt1 + 3)]);
/*     */ 
/* 121 */       break;
/*     */     case 4:
/* 123 */       this.out.lineTo(paramArrayOfFloat[paramInt1], paramArrayOfFloat[(paramInt1 + 1)]);
/*     */     case 5:
/*     */     case 7:
/*     */     }
/*     */   }
/* 128 */   private void emitFirstSegments() { for (int i = 0; i < this.firstSegidx; ) {
/* 129 */       emitSeg(this.firstSegmentsBuffer, i + 1, (int)this.firstSegmentsBuffer[i]);
/* 130 */       i += (int)this.firstSegmentsBuffer[i] - 1;
/*     */     }
/* 132 */     this.firstSegidx = 0;
/*     */   }
/*     */ 
/*     */   private void goTo(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
/*     */   {
/* 144 */     float f1 = paramArrayOfFloat[(paramInt1 + paramInt2 - 4)];
/* 145 */     float f2 = paramArrayOfFloat[(paramInt1 + paramInt2 - 3)];
/* 146 */     if (this.dashOn) {
/* 147 */       if (this.starting) {
/* 148 */         this.firstSegmentsBuffer = Helpers.widenArray(this.firstSegmentsBuffer, this.firstSegidx, paramInt2 - 2);
/*     */ 
/* 150 */         this.firstSegmentsBuffer[(this.firstSegidx++)] = paramInt2;
/* 151 */         System.arraycopy(paramArrayOfFloat, paramInt1, this.firstSegmentsBuffer, this.firstSegidx, paramInt2 - 2);
/* 152 */         this.firstSegidx += paramInt2 - 2;
/*     */       } else {
/* 154 */         if (this.needsMoveTo) {
/* 155 */           this.out.moveTo(this.x0, this.y0);
/* 156 */           this.needsMoveTo = false;
/*     */         }
/* 158 */         emitSeg(paramArrayOfFloat, paramInt1, paramInt2);
/*     */       }
/*     */     } else {
/* 161 */       this.starting = false;
/* 162 */       this.needsMoveTo = true;
/*     */     }
/* 164 */     this.x0 = f1;
/* 165 */     this.y0 = f2;
/*     */   }
/*     */ 
/*     */   public void lineTo(float paramFloat1, float paramFloat2) {
/* 169 */     float f1 = paramFloat1 - this.x0;
/* 170 */     float f2 = paramFloat2 - this.y0;
/*     */ 
/* 172 */     float f3 = (float)Math.sqrt(f1 * f1 + f2 * f2);
/*     */ 
/* 174 */     if (f3 == 0.0F) {
/* 175 */       return;
/*     */     }
/*     */ 
/* 180 */     float f4 = f1 / f3;
/* 181 */     float f5 = f2 / f3;
/*     */     while (true)
/*     */     {
/* 184 */       float f6 = this.dash[this.idx] - this.phase;
/* 185 */       if (f3 <= f6) {
/* 186 */         this.curCurvepts[0] = paramFloat1;
/* 187 */         this.curCurvepts[1] = paramFloat2;
/* 188 */         goTo(this.curCurvepts, 0, 4);
/*     */ 
/* 190 */         this.phase += f3;
/* 191 */         if (f3 == f6) {
/* 192 */           this.phase = 0.0F;
/* 193 */           this.idx = ((this.idx + 1) % this.dash.length);
/* 194 */           this.dashOn = (!this.dashOn);
/*     */         }
/* 196 */         return;
/*     */       }
/*     */ 
/* 199 */       float f7 = this.dash[this.idx] * f4;
/* 200 */       float f8 = this.dash[this.idx] * f5;
/* 201 */       if (this.phase == 0.0F) {
/* 202 */         this.curCurvepts[0] = (this.x0 + f7);
/* 203 */         this.curCurvepts[1] = (this.y0 + f8);
/*     */       } else {
/* 205 */         float f9 = f6 / this.dash[this.idx];
/* 206 */         this.curCurvepts[0] = (this.x0 + f9 * f7);
/* 207 */         this.curCurvepts[1] = (this.y0 + f9 * f8);
/*     */       }
/*     */ 
/* 210 */       goTo(this.curCurvepts, 0, 4);
/*     */ 
/* 212 */       f3 -= f6;
/*     */ 
/* 214 */       this.idx = ((this.idx + 1) % this.dash.length);
/* 215 */       this.dashOn = (!this.dashOn);
/* 216 */       this.phase = 0.0F;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void somethingTo(int paramInt)
/*     */   {
/* 225 */     if (pointCurve(this.curCurvepts, paramInt)) {
/* 226 */       return;
/*     */     }
/* 228 */     if (this.li == null) {
/* 229 */       this.li = new LengthIterator(4, 0.01F);
/*     */     }
/* 231 */     this.li.initializeIterationOnCurve(this.curCurvepts, paramInt);
/*     */ 
/* 233 */     int i = 0;
/* 234 */     float f1 = 0.0F;
/* 235 */     float f2 = 0.0F;
/* 236 */     float f3 = this.dash[this.idx] - this.phase;
/* 237 */     while ((f2 = this.li.next(f3)) < 1.0F) {
/* 238 */       if (f2 != 0.0F) {
/* 239 */         Helpers.subdivideAt((f2 - f1) / (1.0F - f1), this.curCurvepts, i, this.curCurvepts, 0, this.curCurvepts, paramInt, paramInt);
/*     */ 
/* 243 */         f1 = f2;
/* 244 */         goTo(this.curCurvepts, 2, paramInt);
/* 245 */         i = paramInt;
/*     */       }
/*     */ 
/* 248 */       this.idx = ((this.idx + 1) % this.dash.length);
/* 249 */       this.dashOn = (!this.dashOn);
/* 250 */       this.phase = 0.0F;
/* 251 */       f3 = this.dash[this.idx];
/*     */     }
/* 253 */     goTo(this.curCurvepts, i + 2, paramInt);
/* 254 */     this.phase += this.li.lastSegLen();
/* 255 */     if (this.phase >= this.dash[this.idx]) {
/* 256 */       this.phase = 0.0F;
/* 257 */       this.idx = ((this.idx + 1) % this.dash.length);
/* 258 */       this.dashOn = (!this.dashOn);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean pointCurve(float[] paramArrayOfFloat, int paramInt) {
/* 263 */     for (int i = 2; i < paramInt; i++) {
/* 264 */       if (paramArrayOfFloat[i] != paramArrayOfFloat[(i - 2)]) {
/* 265 */         return false;
/*     */       }
/*     */     }
/* 268 */     return true;
/*     */   }
/*     */ 
/*     */   public void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
/*     */   {
/* 536 */     this.curCurvepts[0] = this.x0; this.curCurvepts[1] = this.y0;
/* 537 */     this.curCurvepts[2] = paramFloat1; this.curCurvepts[3] = paramFloat2;
/* 538 */     this.curCurvepts[4] = paramFloat3; this.curCurvepts[5] = paramFloat4;
/* 539 */     this.curCurvepts[6] = paramFloat5; this.curCurvepts[7] = paramFloat6;
/* 540 */     somethingTo(8);
/*     */   }
/*     */ 
/*     */   public void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
/*     */   {
/* 545 */     this.curCurvepts[0] = this.x0; this.curCurvepts[1] = this.y0;
/* 546 */     this.curCurvepts[2] = paramFloat1; this.curCurvepts[3] = paramFloat2;
/* 547 */     this.curCurvepts[4] = paramFloat3; this.curCurvepts[5] = paramFloat4;
/* 548 */     somethingTo(6);
/*     */   }
/*     */ 
/*     */   public void closePath() {
/* 552 */     lineTo(this.sx, this.sy);
/* 553 */     if (this.firstSegidx > 0) {
/* 554 */       if ((!this.dashOn) || (this.needsMoveTo)) {
/* 555 */         this.out.moveTo(this.sx, this.sy);
/*     */       }
/* 557 */       emitFirstSegments();
/*     */     }
/* 559 */     moveTo(this.sx, this.sy);
/*     */   }
/*     */ 
/*     */   public void pathDone() {
/* 563 */     if (this.firstSegidx > 0) {
/* 564 */       this.out.moveTo(this.sx, this.sy);
/* 565 */       emitFirstSegments();
/*     */     }
/* 567 */     this.out.pathDone();
/*     */   }
/*     */ 
/*     */   public long getNativeConsumer()
/*     */   {
/* 572 */     throw new InternalError("Dasher does not use a native consumer");
/*     */   }
/*     */ 
/*     */   private static class LengthIterator
/*     */   {
/*     */     private float[][] recCurveStack;
/*     */     private Side[] sides;
/*     */     private int curveType;
/*     */     private final int limit;
/*     */     private final float ERR;
/*     */     private final float minTincrement;
/*     */     private float nextT;
/*     */     private float lenAtNextT;
/*     */     private float lastT;
/*     */     private float lenAtLastT;
/*     */     private float lenAtLastSplit;
/*     */     private float lastSegLen;
/*     */     private int recLevel;
/*     */     private boolean done;
/* 313 */     private float[] curLeafCtrlPolyLengths = new float[3];
/*     */ 
/* 353 */     private int cachedHaveLowAcceleration = -1;
/*     */ 
/* 386 */     private float[] nextRoots = new float[4];
/*     */ 
/* 392 */     private float[] flatLeafCoefCache = { 0.0F, 0.0F, -1.0F, 0.0F };
/*     */ 
/*     */     public LengthIterator(int paramInt, float paramFloat)
/*     */     {
/* 316 */       this.limit = paramInt;
/* 317 */       this.minTincrement = (1.0F / (1 << this.limit));
/* 318 */       this.ERR = paramFloat;
/* 319 */       this.recCurveStack = new float[paramInt + 1][8];
/* 320 */       this.sides = new Side[paramInt];
/*     */ 
/* 323 */       this.nextT = 3.4028235E+38F;
/* 324 */       this.lenAtNextT = 3.4028235E+38F;
/* 325 */       this.lenAtLastSplit = 1.4E-45F;
/* 326 */       this.recLevel = -2147483648;
/* 327 */       this.lastSegLen = 3.4028235E+38F;
/* 328 */       this.done = true;
/*     */     }
/*     */ 
/*     */     public void initializeIterationOnCurve(float[] paramArrayOfFloat, int paramInt) {
/* 332 */       System.arraycopy(paramArrayOfFloat, 0, this.recCurveStack[0], 0, paramInt);
/* 333 */       this.curveType = paramInt;
/* 334 */       this.recLevel = 0;
/* 335 */       this.lastT = 0.0F;
/* 336 */       this.lenAtLastT = 0.0F;
/* 337 */       this.nextT = 0.0F;
/* 338 */       this.lenAtNextT = 0.0F;
/* 339 */       goLeft();
/* 340 */       this.lenAtLastSplit = 0.0F;
/* 341 */       if (this.recLevel > 0) {
/* 342 */         this.sides[0] = Side.LEFT;
/* 343 */         this.done = false;
/*     */       }
/*     */       else {
/* 346 */         this.sides[0] = Side.RIGHT;
/* 347 */         this.done = true;
/*     */       }
/* 349 */       this.lastSegLen = 0.0F;
/*     */     }
/*     */ 
/*     */     private boolean haveLowAcceleration(float paramFloat)
/*     */     {
/* 356 */       if (this.cachedHaveLowAcceleration == -1) {
/* 357 */         float f1 = this.curLeafCtrlPolyLengths[0];
/* 358 */         float f2 = this.curLeafCtrlPolyLengths[1];
/*     */ 
/* 362 */         if (!Helpers.within(f1, f2, paramFloat * f2)) {
/* 363 */           this.cachedHaveLowAcceleration = 0;
/* 364 */           return false;
/*     */         }
/* 366 */         if (this.curveType == 8) {
/* 367 */           float f3 = this.curLeafCtrlPolyLengths[2];
/*     */ 
/* 371 */           if ((!Helpers.within(f2, f3, paramFloat * f3)) || (!Helpers.within(f1, f3, paramFloat * f3)))
/*     */           {
/* 373 */             this.cachedHaveLowAcceleration = 0;
/* 374 */             return false;
/*     */           }
/*     */         }
/* 377 */         this.cachedHaveLowAcceleration = 1;
/* 378 */         return true;
/*     */       }
/*     */ 
/* 381 */       return this.cachedHaveLowAcceleration == 1;
/*     */     }
/*     */ 
/*     */     public float next(float paramFloat)
/*     */     {
/* 397 */       float f1 = this.lenAtLastSplit + paramFloat;
/* 398 */       while (this.lenAtNextT < f1) {
/* 399 */         if (this.done) {
/* 400 */           this.lastSegLen = (this.lenAtNextT - this.lenAtLastSplit);
/* 401 */           return 1.0F;
/*     */         }
/* 403 */         goToNextLeaf();
/*     */       }
/* 405 */       this.lenAtLastSplit = f1;
/* 406 */       float f2 = this.lenAtNextT - this.lenAtLastT;
/* 407 */       float f3 = (f1 - this.lenAtLastT) / f2;
/*     */ 
/* 411 */       if (!haveLowAcceleration(0.05F))
/*     */       {
/* 417 */         if (this.flatLeafCoefCache[2] < 0.0F) {
/* 418 */           f4 = 0.0F + this.curLeafCtrlPolyLengths[0];
/* 419 */           f5 = f4 + this.curLeafCtrlPolyLengths[1];
/* 420 */           if (this.curveType == 8) {
/* 421 */             f6 = f5 + this.curLeafCtrlPolyLengths[2];
/* 422 */             this.flatLeafCoefCache[0] = (3.0F * (f4 - f5) + f6);
/* 423 */             this.flatLeafCoefCache[1] = (3.0F * (f5 - 2.0F * f4));
/* 424 */             this.flatLeafCoefCache[2] = (3.0F * f4);
/* 425 */             this.flatLeafCoefCache[3] = (-f6);
/* 426 */           } else if (this.curveType == 6) {
/* 427 */             this.flatLeafCoefCache[0] = 0.0F;
/* 428 */             this.flatLeafCoefCache[1] = (f5 - 2.0F * f4);
/* 429 */             this.flatLeafCoefCache[2] = (2.0F * f4);
/* 430 */             this.flatLeafCoefCache[3] = (-f5);
/*     */           }
/*     */         }
/* 433 */         float f4 = this.flatLeafCoefCache[0];
/* 434 */         float f5 = this.flatLeafCoefCache[1];
/* 435 */         float f6 = this.flatLeafCoefCache[2];
/* 436 */         float f7 = f3 * this.flatLeafCoefCache[3];
/*     */ 
/* 441 */         int i = Helpers.cubicRootsInAB(f4, f5, f6, f7, this.nextRoots, 0, 0.0F, 1.0F);
/* 442 */         if ((i == 1) && (!Float.isNaN(this.nextRoots[0]))) {
/* 443 */           f3 = this.nextRoots[0];
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 448 */       f3 = f3 * (this.nextT - this.lastT) + this.lastT;
/* 449 */       if (f3 >= 1.0F) {
/* 450 */         f3 = 1.0F;
/* 451 */         this.done = true;
/*     */       }
/*     */ 
/* 458 */       this.lastSegLen = paramFloat;
/* 459 */       return f3;
/*     */     }
/*     */ 
/*     */     public float lastSegLen() {
/* 463 */       return this.lastSegLen;
/*     */     }
/*     */ 
/*     */     private void goToNextLeaf()
/*     */     {
/* 471 */       this.recLevel -= 1;
/* 472 */       while (this.sides[this.recLevel] == Side.RIGHT) {
/* 473 */         if (this.recLevel == 0) {
/* 474 */           this.done = true;
/* 475 */           return;
/*     */         }
/* 477 */         this.recLevel -= 1;
/*     */       }
/*     */ 
/* 480 */       this.sides[this.recLevel] = Side.RIGHT;
/* 481 */       System.arraycopy(this.recCurveStack[this.recLevel], 0, this.recCurveStack[(this.recLevel + 1)], 0, this.curveType);
/* 482 */       this.recLevel += 1;
/* 483 */       goLeft();
/*     */     }
/*     */ 
/*     */     private void goLeft()
/*     */     {
/* 488 */       float f = onLeaf();
/* 489 */       if (f >= 0.0F) {
/* 490 */         this.lastT = this.nextT;
/* 491 */         this.lenAtLastT = this.lenAtNextT;
/* 492 */         this.nextT += (1 << this.limit - this.recLevel) * this.minTincrement;
/* 493 */         this.lenAtNextT += f;
/*     */ 
/* 495 */         this.flatLeafCoefCache[2] = -1.0F;
/* 496 */         this.cachedHaveLowAcceleration = -1;
/*     */       } else {
/* 498 */         Helpers.subdivide(this.recCurveStack[this.recLevel], 0, this.recCurveStack[(this.recLevel + 1)], 0, this.recCurveStack[this.recLevel], 0, this.curveType);
/*     */ 
/* 501 */         this.sides[this.recLevel] = Side.LEFT;
/* 502 */         this.recLevel += 1;
/* 503 */         goLeft();
/*     */       }
/*     */     }
/*     */ 
/*     */     private float onLeaf()
/*     */     {
/* 510 */       float[] arrayOfFloat = this.recCurveStack[this.recLevel];
/* 511 */       float f1 = 0.0F;
/*     */ 
/* 513 */       float f2 = arrayOfFloat[0]; float f3 = arrayOfFloat[1];
/* 514 */       for (int i = 2; i < this.curveType; i += 2) {
/* 515 */         float f5 = arrayOfFloat[i]; float f6 = arrayOfFloat[(i + 1)];
/* 516 */         float f7 = Helpers.linelen(f2, f3, f5, f6);
/* 517 */         f1 += f7;
/* 518 */         this.curLeafCtrlPolyLengths[(i / 2 - 1)] = f7;
/* 519 */         f2 = f5;
/* 520 */         f3 = f6;
/*     */       }
/*     */ 
/* 523 */       float f4 = Helpers.linelen(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[(this.curveType - 2)], arrayOfFloat[(this.curveType - 1)]);
/* 524 */       if ((f1 - f4 < this.ERR) || (this.recLevel == this.limit)) {
/* 525 */         return (f1 + f4) / 2.0F;
/*     */       }
/* 527 */       return -1.0F;
/*     */     }
/*     */ 
/*     */     private static enum Side
/*     */     {
/* 285 */       LEFT, RIGHT;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pisces.Dasher
 * JD-Core Version:    0.6.2
 */