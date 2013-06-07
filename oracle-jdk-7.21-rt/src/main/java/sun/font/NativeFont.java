/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.FontFormatException;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class NativeFont extends PhysicalFont
/*     */ {
/*     */   String encoding;
/*  61 */   private int numGlyphs = -1;
/*     */   boolean isBitmapDelegate;
/*     */   PhysicalFont delegateFont;
/*     */ 
/*     */   public NativeFont(String paramString, boolean paramBoolean)
/*     */     throws FontFormatException
/*     */   {
/*  71 */     super(paramString, null);
/*     */ 
/*  83 */     this.isBitmapDelegate = paramBoolean;
/*     */ 
/*  85 */     if (GraphicsEnvironment.isHeadless()) {
/*  86 */       throw new FontFormatException("Native font in headless toolkit");
/*     */     }
/*  88 */     this.fontRank = 5;
/*  89 */     initNames();
/*  90 */     if (getNumGlyphs() == 0)
/*  91 */       throw new FontFormatException("Couldn't locate font" + paramString);
/*     */   }
/*     */ 
/*     */   private void initNames()
/*     */     throws FontFormatException
/*     */   {
/* 101 */     int[] arrayOfInt = new int[14];
/* 102 */     int i = 1;
/* 103 */     int j = 1;
/*     */ 
/* 105 */     String str1 = this.platName.toLowerCase(Locale.ENGLISH);
/* 106 */     if (str1.startsWith("-")) {
/* 107 */       while ((j != -1) && (i < 14)) {
/* 108 */         j = str1.indexOf('-', j);
/* 109 */         if (j != -1) {
/* 110 */           arrayOfInt[(i++)] = j;
/* 111 */           j++;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 116 */     if ((i == 14) && (j != -1))
/*     */     {
/* 119 */       String str2 = str1.substring(arrayOfInt[1] + 1, arrayOfInt[2]);
/* 120 */       StringBuilder localStringBuilder = new StringBuilder(str2);
/* 121 */       char c = Character.toUpperCase(localStringBuilder.charAt(0));
/* 122 */       localStringBuilder.replace(0, 1, String.valueOf(c));
/* 123 */       for (int k = 1; k < localStringBuilder.length() - 1; k++) {
/* 124 */         if (localStringBuilder.charAt(k) == ' ') {
/* 125 */           c = Character.toUpperCase(localStringBuilder.charAt(k + 1));
/* 126 */           localStringBuilder.replace(k + 1, k + 2, String.valueOf(c));
/*     */         }
/*     */       }
/* 129 */       this.familyName = localStringBuilder.toString();
/*     */ 
/* 131 */       String str3 = str1.substring(arrayOfInt[2] + 1, arrayOfInt[3]);
/* 132 */       String str4 = str1.substring(arrayOfInt[3] + 1, arrayOfInt[4]);
/*     */ 
/* 134 */       String str5 = null;
/*     */ 
/* 136 */       if ((str3.indexOf("bold") >= 0) || (str3.indexOf("demi") >= 0))
/*     */       {
/* 138 */         this.style |= 1;
/* 139 */         str5 = "Bold";
/*     */       }
/*     */ 
/* 142 */       if ((str4.equals("i")) || (str4.indexOf("italic") >= 0))
/*     */       {
/* 144 */         this.style |= 2;
/*     */ 
/* 146 */         if (str5 == null)
/* 147 */           str5 = "Italic";
/*     */         else {
/* 149 */           str5 = str5 + " Italic";
/*     */         }
/*     */       }
/* 152 */       else if ((str4.equals("o")) || (str4.indexOf("oblique") >= 0))
/*     */       {
/* 154 */         this.style |= 2;
/* 155 */         if (str5 == null)
/* 156 */           str5 = "Oblique";
/*     */         else {
/* 158 */           str5 = str5 + " Oblique";
/*     */         }
/*     */       }
/*     */ 
/* 162 */       if (str5 == null)
/* 163 */         this.fullName = this.familyName;
/*     */       else {
/* 165 */         this.fullName = (this.familyName + " " + str5);
/*     */       }
/*     */ 
/* 168 */       this.encoding = str1.substring(arrayOfInt[12] + 1);
/* 169 */       if (this.encoding.startsWith("-")) {
/* 170 */         this.encoding = str1.substring(arrayOfInt[13] + 1);
/*     */       }
/* 172 */       if (this.encoding.indexOf("fontspecific") >= 0)
/* 173 */         if (str2.indexOf("dingbats") >= 0)
/* 174 */           this.encoding = "dingbats";
/* 175 */         else if (str2.indexOf("symbol") >= 0)
/* 176 */           this.encoding = "symbol";
/*     */         else
/* 178 */           this.encoding = "iso8859-1";
/*     */     }
/*     */     else
/*     */     {
/* 182 */       throw new FontFormatException("Bad native name " + this.platName);
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean hasExternalBitmaps(String paramString)
/*     */   {
/* 209 */     StringBuilder localStringBuilder = new StringBuilder(paramString);
/* 210 */     int i = localStringBuilder.indexOf("-0-");
/* 211 */     while (i >= 0) {
/* 212 */       localStringBuilder.replace(i + 1, i + 2, "*");
/* 213 */       i = localStringBuilder.indexOf("-0-", i);
/*     */     }
/* 215 */     String str = localStringBuilder.toString();
/* 216 */     byte[] arrayOfByte = null;
/*     */     try {
/* 218 */       arrayOfByte = str.getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 220 */       arrayOfByte = str.getBytes();
/*     */     }
/* 222 */     return haveBitmapFonts(arrayOfByte);
/*     */   }
/*     */ 
/*     */   public static boolean fontExists(String paramString) {
/* 226 */     byte[] arrayOfByte = null;
/*     */     try {
/* 228 */       arrayOfByte = paramString.getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 230 */       arrayOfByte = paramString.getBytes();
/*     */     }
/* 232 */     return fontExists(arrayOfByte);
/*     */   }
/*     */   private static native boolean haveBitmapFonts(byte[] paramArrayOfByte);
/*     */ 
/*     */   private static native boolean fontExists(byte[] paramArrayOfByte);
/*     */ 
/*     */   public CharToGlyphMapper getMapper() {
/* 239 */     if (this.mapper == null) {
/* 240 */       if (this.isBitmapDelegate)
/*     */       {
/* 242 */         this.mapper = new NativeGlyphMapper(this);
/*     */       }
/*     */       else {
/* 245 */         SunFontManager localSunFontManager = SunFontManager.getInstance();
/* 246 */         this.delegateFont = localSunFontManager.getDefaultPhysicalFont();
/* 247 */         this.mapper = this.delegateFont.getMapper();
/*     */       }
/*     */     }
/* 250 */     return this.mapper;
/*     */   }
/*     */ 
/*     */   FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc) {
/* 254 */     if (this.isBitmapDelegate) {
/* 255 */       return new NativeStrike(this, paramFontStrikeDesc);
/*     */     }
/* 257 */     if (this.delegateFont == null) {
/* 258 */       localObject = SunFontManager.getInstance();
/* 259 */       this.delegateFont = ((SunFontManager)localObject).getDefaultPhysicalFont();
/*     */     }
/*     */ 
/* 264 */     if ((this.delegateFont instanceof NativeFont)) {
/* 265 */       return new NativeStrike((NativeFont)this.delegateFont, paramFontStrikeDesc);
/*     */     }
/* 267 */     Object localObject = this.delegateFont.createStrike(paramFontStrikeDesc);
/* 268 */     return new DelegateStrike(this, paramFontStrikeDesc, (FontStrike)localObject);
/*     */   }
/*     */ 
/*     */   public Rectangle2D getMaxCharBounds(FontRenderContext paramFontRenderContext)
/*     */   {
/* 273 */     return null;
/*     */   }
/*     */ 
/*     */   native StrikeMetrics getFontMetrics(long paramLong);
/*     */ 
/*     */   native float getGlyphAdvance(long paramLong, int paramInt);
/*     */ 
/*     */   Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt)
/*     */   {
/* 282 */     return new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
/*     */   }
/*     */ 
/*     */   public GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/* 289 */     return null;
/*     */   }
/*     */ 
/*     */   native long getGlyphImage(long paramLong, int paramInt);
/*     */ 
/*     */   native long getGlyphImageNoDefault(long paramLong, int paramInt);
/*     */ 
/*     */   void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat)
/*     */   {
/* 298 */     throw new RuntimeException("this should be called on the strike");
/*     */   }
/*     */ 
/*     */   public GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   private native int countGlyphs(byte[] paramArrayOfByte, int paramInt);
/*     */ 
/*     */   public int getNumGlyphs() {
/* 310 */     if (this.numGlyphs == -1) {
/* 311 */       byte[] arrayOfByte = getPlatformNameBytes(8);
/* 312 */       this.numGlyphs = countGlyphs(arrayOfByte, 8);
/*     */     }
/* 314 */     return this.numGlyphs;
/*     */   }
/*     */ 
/*     */   PhysicalFont getDelegateFont() {
/* 318 */     if (this.delegateFont == null) {
/* 319 */       SunFontManager localSunFontManager = SunFontManager.getInstance();
/* 320 */       this.delegateFont = localSunFontManager.getDefaultPhysicalFont();
/*     */     }
/* 322 */     return this.delegateFont;
/*     */   }
/*     */ 
/*     */   byte[] getPlatformNameBytes(int paramInt)
/*     */   {
/* 335 */     int[] arrayOfInt = new int[14];
/* 336 */     int i = 1;
/* 337 */     int j = 1;
/*     */ 
/* 339 */     while ((j != -1) && (i < 14)) {
/* 340 */       j = this.platName.indexOf('-', j);
/* 341 */       if (j != -1) {
/* 342 */         arrayOfInt[(i++)] = j;
/* 343 */         j++;
/*     */       }
/*     */     }
/* 346 */     String str1 = Integer.toString(Math.abs(paramInt) * 10);
/* 347 */     StringBuilder localStringBuilder = new StringBuilder(this.platName);
/*     */ 
/* 349 */     localStringBuilder.replace(arrayOfInt[11] + 1, arrayOfInt[12], "*");
/*     */ 
/* 351 */     localStringBuilder.replace(arrayOfInt[9] + 1, arrayOfInt[10], "72");
/*     */ 
/* 353 */     localStringBuilder.replace(arrayOfInt[8] + 1, arrayOfInt[9], "72");
/*     */ 
/* 362 */     localStringBuilder.replace(arrayOfInt[7] + 1, arrayOfInt[8], str1);
/*     */ 
/* 364 */     localStringBuilder.replace(arrayOfInt[6] + 1, arrayOfInt[7], "*");
/*     */ 
/* 372 */     if ((arrayOfInt[0] == 0) && (arrayOfInt[1] == 1))
/*     */     {
/* 378 */       localStringBuilder.replace(arrayOfInt[0] + 1, arrayOfInt[1], "*");
/*     */     }
/*     */ 
/* 381 */     String str2 = localStringBuilder.toString();
/* 382 */     byte[] arrayOfByte = null;
/*     */     try {
/* 384 */       arrayOfByte = str2.getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 386 */       arrayOfByte = str2.getBytes();
/*     */     }
/* 388 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 392 */     return " ** Native Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " nativeName=" + this.platName;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.NativeFont
 * JD-Core Version:    0.6.2
 */