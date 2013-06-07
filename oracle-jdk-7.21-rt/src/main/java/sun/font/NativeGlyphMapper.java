/*    */ package sun.font;
/*    */ 
/*    */ public class NativeGlyphMapper extends CharToGlyphMapper
/*    */ {
/*    */   NativeFont font;
/*    */   XMap xmapper;
/*    */   int numGlyphs;
/*    */ 
/*    */   NativeGlyphMapper(NativeFont paramNativeFont)
/*    */   {
/* 52 */     this.font = paramNativeFont;
/* 53 */     this.xmapper = XMap.getXMapper(this.font.encoding);
/* 54 */     this.numGlyphs = paramNativeFont.getNumGlyphs();
/* 55 */     this.missingGlyph = 0;
/*    */   }
/*    */ 
/*    */   public int getNumGlyphs() {
/* 59 */     return this.numGlyphs;
/*    */   }
/*    */ 
/*    */   public int charToGlyph(char paramChar) {
/* 63 */     if (paramChar >= this.xmapper.convertedGlyphs.length) {
/* 64 */       return 0;
/*    */     }
/* 66 */     return this.xmapper.convertedGlyphs[paramChar];
/*    */   }
/*    */ 
/*    */   public int charToGlyph(int paramInt)
/*    */   {
/* 71 */     if (paramInt >= this.xmapper.convertedGlyphs.length) {
/* 72 */       return 0;
/*    */     }
/* 74 */     return this.xmapper.convertedGlyphs[paramInt];
/*    */   }
/*    */ 
/*    */   public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
/*    */   {
/* 79 */     for (int i = 0; i < paramInt; i++) {
/* 80 */       int j = paramArrayOfChar[i];
/* 81 */       if (j >= this.xmapper.convertedGlyphs.length)
/* 82 */         paramArrayOfInt[i] = 0;
/*    */       else
/* 84 */         paramArrayOfInt[i] = this.xmapper.convertedGlyphs[j];
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
/*    */   {
/* 90 */     charsToGlyphs(paramInt, paramArrayOfChar, paramArrayOfInt);
/* 91 */     return false;
/*    */   }
/*    */ 
/*    */   public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
/* 95 */     for (int i = 0; i < paramInt; i++) {
/* 96 */       int j = (char)paramArrayOfInt1[i];
/* 97 */       if (j >= this.xmapper.convertedGlyphs.length)
/* 98 */         paramArrayOfInt2[i] = 0;
/*    */       else
/* 100 */         paramArrayOfInt2[i] = this.xmapper.convertedGlyphs[j];
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.NativeGlyphMapper
 * JD-Core Version:    0.6.2
 */