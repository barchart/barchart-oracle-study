/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.util.Hashtable;
/*     */ import sun.awt.PlatformFont;
/*     */ 
/*     */ public class X11FontMetrics extends FontMetrics
/*     */ {
/*     */   int[] widths;
/*     */   int ascent;
/*     */   int descent;
/*     */   int leading;
/*     */   int height;
/*     */   int maxAscent;
/*     */   int maxDescent;
/*     */   int maxHeight;
/*     */   int maxAdvance;
/* 221 */   static Hashtable table = new Hashtable();
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   public X11FontMetrics(Font paramFont)
/*     */   {
/* 119 */     super(paramFont);
/* 120 */     init();
/*     */   }
/*     */ 
/*     */   public int getLeading()
/*     */   {
/* 127 */     return this.leading;
/*     */   }
/*     */ 
/*     */   public int getAscent()
/*     */   {
/* 134 */     return this.ascent;
/*     */   }
/*     */ 
/*     */   public int getDescent()
/*     */   {
/* 141 */     return this.descent;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 148 */     return this.height;
/*     */   }
/*     */ 
/*     */   public int getMaxAscent()
/*     */   {
/* 155 */     return this.maxAscent;
/*     */   }
/*     */ 
/*     */   public int getMaxDescent()
/*     */   {
/* 162 */     return this.maxDescent;
/*     */   }
/*     */ 
/*     */   public int getMaxAdvance()
/*     */   {
/* 169 */     return this.maxAdvance;
/*     */   }
/*     */ 
/*     */   public int stringWidth(String paramString)
/*     */   {
/* 176 */     return charsWidth(paramString.toCharArray(), 0, paramString.length());
/*     */   }
/*     */ 
/*     */   public int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*     */   {
/* 183 */     Font localFont = getFont();
/* 184 */     PlatformFont localPlatformFont = (PlatformFont)localFont.getPeer();
/* 185 */     if (localPlatformFont.mightHaveMultiFontMetrics()) {
/* 186 */       return getMFCharsWidth(paramArrayOfChar, paramInt1, paramInt2, localFont);
/*     */     }
/* 188 */     if (this.widths != null) {
/* 189 */       int i = 0;
/* 190 */       for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
/* 191 */         int k = paramArrayOfChar[j];
/* 192 */         if ((k < 0) || (k >= this.widths.length))
/* 193 */           i += this.maxAdvance;
/*     */         else {
/* 195 */           i += this.widths[k];
/*     */         }
/*     */       }
/* 198 */       return i;
/*     */     }
/* 200 */     return this.maxAdvance * paramInt2;
/*     */   }
/*     */ 
/*     */   private native int getMFCharsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2, Font paramFont);
/*     */ 
/*     */   public native int bytesWidth(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   public int[] getWidths()
/*     */   {
/* 216 */     return this.widths;
/*     */   }
/*     */ 
/*     */   native void init();
/*     */ 
/*     */   static synchronized FontMetrics getFontMetrics(Font paramFont)
/*     */   {
/* 224 */     Object localObject = (FontMetrics)table.get(paramFont);
/* 225 */     if (localObject == null) {
/* 226 */       table.put(paramFont, localObject = new X11FontMetrics(paramFont));
/*     */     }
/* 228 */     return localObject;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 106 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11FontMetrics
 * JD-Core Version:    0.6.2
 */