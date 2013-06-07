/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.font.GlyphVector;
/*     */ import sun.java2d.loops.FontInfo;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public final class GlyphList
/*     */ {
/*     */   private static final int MINGRAYLENGTH = 1024;
/*     */   private static final int MAXGRAYLENGTH = 8192;
/*     */   private static final int DEFAULT_LENGTH = 32;
/*     */   int glyphindex;
/*     */   int[] metrics;
/*     */   byte[] graybits;
/*     */   Object strikelist;
/* 106 */   int len = 0;
/* 107 */   int maxLen = 0;
/* 108 */   int maxPosLen = 0;
/*     */   int[] glyphData;
/*     */   char[] chData;
/*     */   long[] images;
/*     */   float[] positions;
/*     */   float x;
/*     */   float y;
/*     */   float gposx;
/*     */   float gposy;
/*     */   boolean usePositions;
/*     */   boolean lcdRGBOrder;
/*     */   boolean lcdSubPixPos;
/* 154 */   private static GlyphList reusableGL = new GlyphList();
/*     */   private static boolean inUse;
/*     */ 
/*     */   void ensureCapacity(int paramInt)
/*     */   {
/* 162 */     if (paramInt < 0) {
/* 163 */       paramInt = 0;
/*     */     }
/* 165 */     if ((this.usePositions) && (paramInt > this.maxPosLen)) {
/* 166 */       this.positions = new float[paramInt * 2 + 2];
/* 167 */       this.maxPosLen = paramInt;
/*     */     }
/*     */ 
/* 170 */     if ((this.maxLen == 0) || (paramInt > this.maxLen)) {
/* 171 */       this.glyphData = new int[paramInt];
/* 172 */       this.chData = new char[paramInt];
/* 173 */       this.images = new long[paramInt];
/* 174 */       this.maxLen = paramInt;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static GlyphList getInstance()
/*     */   {
/* 194 */     if (inUse) {
/* 195 */       return new GlyphList();
/*     */     }
/* 197 */     synchronized (GlyphList.class) {
/* 198 */       if (inUse) {
/* 199 */         return new GlyphList();
/*     */       }
/* 201 */       inUse = true;
/* 202 */       return reusableGL;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean setFromString(FontInfo paramFontInfo, String paramString, float paramFloat1, float paramFloat2)
/*     */   {
/* 236 */     this.x = paramFloat1;
/* 237 */     this.y = paramFloat2;
/* 238 */     this.strikelist = paramFontInfo.fontStrike;
/* 239 */     this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
/* 240 */     this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
/* 241 */     this.len = paramString.length();
/* 242 */     ensureCapacity(this.len);
/* 243 */     paramString.getChars(0, this.len, this.chData, 0);
/* 244 */     return mapChars(paramFontInfo, this.len);
/*     */   }
/*     */ 
/*     */   public boolean setFromChars(FontInfo paramFontInfo, char[] paramArrayOfChar, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
/*     */   {
/* 249 */     this.x = paramFloat1;
/* 250 */     this.y = paramFloat2;
/* 251 */     this.strikelist = paramFontInfo.fontStrike;
/* 252 */     this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
/* 253 */     this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
/* 254 */     this.len = paramInt2;
/* 255 */     if (paramInt2 < 0)
/* 256 */       this.len = 0;
/*     */     else {
/* 258 */       this.len = paramInt2;
/*     */     }
/* 260 */     ensureCapacity(this.len);
/* 261 */     System.arraycopy(paramArrayOfChar, paramInt1, this.chData, 0, this.len);
/* 262 */     return mapChars(paramFontInfo, this.len);
/*     */   }
/*     */ 
/*     */   private final boolean mapChars(FontInfo paramFontInfo, int paramInt)
/*     */   {
/* 269 */     if (paramFontInfo.font2D.getMapper().charsToGlyphsNS(paramInt, this.chData, this.glyphData)) {
/* 270 */       return false;
/*     */     }
/* 272 */     paramFontInfo.fontStrike.getGlyphImagePtrs(this.glyphData, this.images, paramInt);
/* 273 */     this.glyphindex = -1;
/* 274 */     return true;
/*     */   }
/*     */ 
/*     */   public void setFromGlyphVector(FontInfo paramFontInfo, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
/*     */   {
/* 280 */     this.x = paramFloat1;
/* 281 */     this.y = paramFloat2;
/* 282 */     this.lcdRGBOrder = paramFontInfo.lcdRGBOrder;
/* 283 */     this.lcdSubPixPos = paramFontInfo.lcdSubPixPos;
/*     */ 
/* 288 */     StandardGlyphVector localStandardGlyphVector = StandardGlyphVector.getStandardGV(paramGlyphVector, paramFontInfo);
/*     */ 
/* 290 */     this.usePositions = localStandardGlyphVector.needsPositions(paramFontInfo.devTx);
/* 291 */     this.len = localStandardGlyphVector.getNumGlyphs();
/* 292 */     ensureCapacity(this.len);
/* 293 */     this.strikelist = localStandardGlyphVector.setupGlyphImages(this.images, this.usePositions ? this.positions : null, paramFontInfo.devTx);
/*     */ 
/* 296 */     this.glyphindex = -1;
/*     */   }
/*     */ 
/*     */   public int[] getBounds()
/*     */   {
/* 304 */     if (this.glyphindex >= 0) {
/* 305 */       throw new InternalError("calling getBounds after setGlyphIndex");
/*     */     }
/* 307 */     if (this.metrics == null) {
/* 308 */       this.metrics = new int[5];
/*     */     }
/*     */ 
/* 312 */     this.gposx = (this.x + 0.5F);
/* 313 */     this.gposy = (this.y + 0.5F);
/* 314 */     fillBounds(this.metrics);
/* 315 */     return this.metrics;
/*     */   }
/*     */ 
/*     */   public void setGlyphIndex(int paramInt)
/*     */   {
/* 325 */     this.glyphindex = paramInt;
/* 326 */     float f1 = StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.topLeftXOffset);
/*     */ 
/* 328 */     float f2 = StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.topLeftYOffset);
/*     */ 
/* 331 */     if (this.usePositions) {
/* 332 */       this.metrics[0] = ((int)Math.floor(this.positions[(paramInt << 1)] + this.gposx + f1));
/* 333 */       this.metrics[1] = ((int)Math.floor(this.positions[((paramInt << 1) + 1)] + this.gposy + f2));
/*     */     } else {
/* 335 */       this.metrics[0] = ((int)Math.floor(this.gposx + f1));
/* 336 */       this.metrics[1] = ((int)Math.floor(this.gposy + f2));
/*     */ 
/* 338 */       this.gposx += StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.xAdvanceOffset);
/*     */ 
/* 340 */       this.gposy += StrikeCache.unsafe.getFloat(this.images[paramInt] + StrikeCache.yAdvanceOffset);
/*     */     }
/*     */ 
/* 343 */     this.metrics[2] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.widthOffset);
/*     */ 
/* 345 */     this.metrics[3] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.heightOffset);
/*     */ 
/* 347 */     this.metrics[4] = StrikeCache.unsafe.getChar(this.images[paramInt] + StrikeCache.rowBytesOffset);
/*     */   }
/*     */ 
/*     */   public int[] getMetrics()
/*     */   {
/* 352 */     return this.metrics;
/*     */   }
/*     */ 
/*     */   public byte[] getGrayBits() {
/* 356 */     int i = this.metrics[4] * this.metrics[3];
/* 357 */     if (this.graybits == null) {
/* 358 */       this.graybits = new byte[Math.max(i, 1024)];
/*     */     }
/* 360 */     else if (i > this.graybits.length)
/* 361 */       this.graybits = new byte[i];
/*     */     long l;
/* 365 */     if (StrikeCache.nativeAddressSize == 4) {
/* 366 */       l = 0xFFFFFFFF & StrikeCache.unsafe.getInt(this.images[this.glyphindex] + StrikeCache.pixelDataOffset);
/*     */     }
/*     */     else
/*     */     {
/* 370 */       l = StrikeCache.unsafe.getLong(this.images[this.glyphindex] + StrikeCache.pixelDataOffset);
/*     */     }
/*     */ 
/* 374 */     if (l == 0L) {
/* 375 */       return this.graybits;
/*     */     }
/*     */ 
/* 383 */     for (int j = 0; j < i; j++) {
/* 384 */       this.graybits[j] = StrikeCache.unsafe.getByte(l + j);
/*     */     }
/* 386 */     return this.graybits;
/*     */   }
/*     */ 
/*     */   public long[] getImages() {
/* 390 */     return this.images;
/*     */   }
/*     */ 
/*     */   public boolean usePositions() {
/* 394 */     return this.usePositions;
/*     */   }
/*     */ 
/*     */   public float[] getPositions() {
/* 398 */     return this.positions;
/*     */   }
/*     */ 
/*     */   public float getX() {
/* 402 */     return this.x;
/*     */   }
/*     */ 
/*     */   public float getY() {
/* 406 */     return this.y;
/*     */   }
/*     */ 
/*     */   public Object getStrike() {
/* 410 */     return this.strikelist;
/*     */   }
/*     */ 
/*     */   public boolean isSubPixPos() {
/* 414 */     return this.lcdSubPixPos;
/*     */   }
/*     */ 
/*     */   public boolean isRGBOrder() {
/* 418 */     return this.lcdRGBOrder;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 426 */     if (this == reusableGL) {
/* 427 */       if ((this.graybits != null) && (this.graybits.length > 8192)) {
/* 428 */         this.graybits = null;
/*     */       }
/* 430 */       this.usePositions = false;
/* 431 */       this.strikelist = null;
/* 432 */       inUse = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getNumGlyphs()
/*     */   {
/* 448 */     return this.len;
/*     */   }
/*     */ 
/*     */   private void fillBounds(int[] paramArrayOfInt)
/*     */   {
/* 456 */     int i = StrikeCache.topLeftXOffset;
/* 457 */     int j = StrikeCache.topLeftYOffset;
/* 458 */     int k = StrikeCache.widthOffset;
/* 459 */     int m = StrikeCache.heightOffset;
/* 460 */     int n = StrikeCache.xAdvanceOffset;
/* 461 */     int i1 = StrikeCache.yAdvanceOffset;
/*     */ 
/* 463 */     if (this.len == 0)
/*     */     {
/*     */       int tmp48_47 = (paramArrayOfInt[2] = paramArrayOfInt[3] = 0); paramArrayOfInt[1] = tmp48_47; paramArrayOfInt[0] = tmp48_47;
/*     */       return;
/*     */     }
/*     */     float f2;
/* 468 */     float f1 = f2 = (1.0F / 1.0F);
/*     */     float f4;
/* 469 */     float f3 = f4 = (1.0F / -1.0F);
/*     */ 
/* 471 */     int i2 = 0;
/* 472 */     float f5 = this.x + 0.5F;
/* 473 */     float f6 = this.y + 0.5F;
/*     */ 
/* 476 */     for (int i5 = 0; i5 < this.len; i5++) {
/* 477 */       float f7 = StrikeCache.unsafe.getFloat(this.images[i5] + i);
/* 478 */       float f8 = StrikeCache.unsafe.getFloat(this.images[i5] + j);
/* 479 */       int i3 = StrikeCache.unsafe.getChar(this.images[i5] + k);
/* 480 */       int i4 = StrikeCache.unsafe.getChar(this.images[i5] + m);
/*     */       float f9;
/*     */       float f10;
/* 482 */       if (this.usePositions) {
/* 483 */         f9 = this.positions[(i2++)] + f7 + f5;
/* 484 */         f10 = this.positions[(i2++)] + f8 + f6;
/*     */       } else {
/* 486 */         f9 = f5 + f7;
/* 487 */         f10 = f6 + f8;
/* 488 */         f5 += StrikeCache.unsafe.getFloat(this.images[i5] + n);
/* 489 */         f6 += StrikeCache.unsafe.getFloat(this.images[i5] + i1);
/*     */       }
/* 491 */       float f11 = f9 + i3;
/* 492 */       float f12 = f10 + i4;
/* 493 */       if (f1 > f9) f1 = f9;
/* 494 */       if (f2 > f10) f2 = f10;
/* 495 */       if (f3 < f11) f3 = f11;
/* 496 */       if (f4 < f12) f4 = f12;
/*     */ 
/*     */     }
/*     */ 
/* 501 */     paramArrayOfInt[0] = ((int)Math.floor(f1));
/* 502 */     paramArrayOfInt[1] = ((int)Math.floor(f2));
/* 503 */     paramArrayOfInt[2] = ((int)Math.floor(f3));
/* 504 */     paramArrayOfInt[3] = ((int)Math.floor(f4));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.GlyphList
 * JD-Core Version:    0.6.2
 */