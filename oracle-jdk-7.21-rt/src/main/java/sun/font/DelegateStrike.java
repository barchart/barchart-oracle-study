/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ 
/*     */ class DelegateStrike extends NativeStrike
/*     */ {
/*     */   private FontStrike delegateStrike;
/*     */ 
/*     */   DelegateStrike(NativeFont paramNativeFont, FontStrikeDesc paramFontStrikeDesc, FontStrike paramFontStrike)
/*     */   {
/* 311 */     super(paramNativeFont, paramFontStrikeDesc);
/* 312 */     this.delegateStrike = paramFontStrike;
/*     */   }
/*     */ 
/*     */   StrikeMetrics getFontMetrics()
/*     */   {
/* 324 */     if (this.strikeMetrics == null) {
/* 325 */       if (this.pScalerContext != 0L) {
/* 326 */         this.strikeMetrics = super.getFontMetrics();
/*     */       }
/* 328 */       if (this.strikeMetrics == null) {
/* 329 */         this.strikeMetrics = this.delegateStrike.getFontMetrics();
/*     */       }
/*     */     }
/* 332 */     return this.strikeMetrics;
/*     */   }
/*     */ 
/*     */   void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt) {
/* 336 */     this.delegateStrike.getGlyphImagePtrs(paramArrayOfInt, paramArrayOfLong, paramInt);
/*     */   }
/*     */ 
/*     */   long getGlyphImagePtr(int paramInt) {
/* 340 */     return this.delegateStrike.getGlyphImagePtr(paramInt);
/*     */   }
/*     */ 
/*     */   void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle)
/*     */   {
/* 345 */     this.delegateStrike.getGlyphImageBounds(paramInt, paramFloat, paramRectangle);
/*     */   }
/*     */ 
/*     */   Point2D.Float getGlyphMetrics(int paramInt) {
/* 349 */     return this.delegateStrike.getGlyphMetrics(paramInt);
/*     */   }
/*     */ 
/*     */   float getGlyphAdvance(int paramInt) {
/* 353 */     return this.delegateStrike.getGlyphAdvance(paramInt);
/*     */   }
/*     */ 
/*     */   Point2D.Float getCharMetrics(char paramChar) {
/* 357 */     return this.delegateStrike.getCharMetrics(paramChar);
/*     */   }
/*     */ 
/*     */   float getCodePointAdvance(int paramInt) {
/* 361 */     if ((paramInt < 0) || (paramInt >= 65536)) {
/* 362 */       paramInt = 65535;
/*     */     }
/* 364 */     return this.delegateStrike.getGlyphAdvance(paramInt);
/*     */   }
/*     */ 
/*     */   Rectangle2D.Float getGlyphOutlineBounds(int paramInt) {
/* 368 */     return this.delegateStrike.getGlyphOutlineBounds(paramInt);
/*     */   }
/*     */ 
/*     */   GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
/* 372 */     return this.delegateStrike.getGlyphOutline(paramInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2) {
/* 376 */     return this.delegateStrike.getGlyphVectorOutline(paramArrayOfInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.DelegateStrike
 * JD-Core Version:    0.6.2
 */