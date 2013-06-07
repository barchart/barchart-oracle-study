/*     */ package sun.font;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import sun.awt.SunHints.Value;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class FontConfigManager
/*     */ {
/*  39 */   static boolean fontConfigFailed = false;
/*     */ 
/*  42 */   private static final FontConfigInfo fcInfo = new FontConfigInfo();
/*     */ 
/*  82 */   private static String[] fontConfigNames = { "sans:regular:roman", "sans:bold:roman", "sans:regular:italic", "sans:bold:italic", "serif:regular:roman", "serif:bold:roman", "serif:regular:italic", "serif:bold:italic", "monospace:regular:roman", "monospace:bold:roman", "monospace:regular:italic", "monospace:bold:italic" };
/*     */   private FcCompFont[] fontConfigFonts;
/*     */ 
/*     */   public static String[] getFontConfigNames()
/*     */   {
/* 112 */     return fontConfigNames;
/*     */   }
/*     */ 
/*     */   public static Object getFontConfigAAHint()
/*     */   {
/* 123 */     return getFontConfigAAHint("sans");
/*     */   }
/*     */ 
/*     */   public static Object getFontConfigAAHint(String paramString)
/*     */   {
/* 132 */     if (FontUtilities.isWindows) {
/* 133 */       return null;
/*     */     }
/* 135 */     int i = getFontConfigAASettings(getFCLocaleStr(), paramString);
/* 136 */     if (i < 0) {
/* 137 */       return null;
/*     */     }
/* 139 */     return SunHints.Value.get(2, i);
/*     */   }
/*     */ 
/*     */   private static String getFCLocaleStr()
/*     */   {
/* 147 */     Locale localLocale = SunToolkit.getStartupLocale();
/* 148 */     String str1 = localLocale.getLanguage();
/* 149 */     String str2 = localLocale.getCountry();
/* 150 */     if (!str2.equals("")) {
/* 151 */       str1 = str1 + "-" + str2;
/*     */     }
/* 153 */     return str1;
/*     */   }
/*     */ 
/*     */   public static native int getFontConfigVersion();
/*     */ 
/*     */   public synchronized void initFontConfigFonts(boolean paramBoolean)
/*     */   {
/* 170 */     if ((this.fontConfigFonts != null) && (
/* 171 */       (!paramBoolean) || (this.fontConfigFonts[0].allFonts != null))) {
/* 172 */       return;
/*     */     }
/*     */ 
/* 176 */     if ((FontUtilities.isWindows) || (fontConfigFailed)) {
/* 177 */       return;
/*     */     }
/*     */ 
/* 180 */     long l1 = 0L;
/* 181 */     if (FontUtilities.isLogging()) {
/* 182 */       l1 = System.nanoTime();
/*     */     }
/*     */ 
/* 185 */     String[] arrayOfString = getFontConfigNames();
/* 186 */     FcCompFont[] arrayOfFcCompFont = new FcCompFont[arrayOfString.length];
/*     */ 
/* 188 */     for (int i = 0; i < arrayOfFcCompFont.length; i++) {
/* 189 */       arrayOfFcCompFont[i] = new FcCompFont();
/* 190 */       arrayOfFcCompFont[i].fcName = arrayOfString[i];
/* 191 */       j = arrayOfFcCompFont[i].fcName.indexOf(':');
/* 192 */       arrayOfFcCompFont[i].fcFamily = arrayOfFcCompFont[i].fcName.substring(0, j);
/* 193 */       arrayOfFcCompFont[i].jdkName = FontUtilities.mapFcName(arrayOfFcCompFont[i].fcFamily);
/* 194 */       arrayOfFcCompFont[i].style = (i % 4);
/*     */     }
/* 196 */     getFontConfig(getFCLocaleStr(), fcInfo, arrayOfFcCompFont, paramBoolean);
/* 197 */     FontConfigFont localFontConfigFont1 = null;
/*     */ 
/* 199 */     for (int j = 0; j < arrayOfFcCompFont.length; j++) {
/* 200 */       FcCompFont localFcCompFont1 = arrayOfFcCompFont[j];
/* 201 */       if (localFcCompFont1.firstFont == null) {
/* 202 */         if (FontUtilities.isLogging()) {
/* 203 */           PlatformLogger localPlatformLogger3 = FontUtilities.getLogger();
/* 204 */           localPlatformLogger3.info("Fontconfig returned no font for " + arrayOfFcCompFont[j].fcName);
/*     */         }
/*     */ 
/* 207 */         fontConfigFailed = true;
/* 208 */       } else if (localFontConfigFont1 == null) {
/* 209 */         localFontConfigFont1 = localFcCompFont1.firstFont;
/*     */       }
/*     */     }
/*     */ 
/* 213 */     if (localFontConfigFont1 == null) {
/* 214 */       if (FontUtilities.isLogging()) {
/* 215 */         PlatformLogger localPlatformLogger1 = FontUtilities.getLogger();
/* 216 */         localPlatformLogger1.info("Fontconfig returned no fonts at all.");
/*     */       }
/* 218 */       fontConfigFailed = true;
/* 219 */       return;
/* 220 */     }if (fontConfigFailed) {
/* 221 */       for (int k = 0; k < arrayOfFcCompFont.length; k++) {
/* 222 */         if (arrayOfFcCompFont[k].firstFont == null) {
/* 223 */           arrayOfFcCompFont[k].firstFont = localFontConfigFont1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 228 */     this.fontConfigFonts = arrayOfFcCompFont;
/*     */ 
/* 230 */     if (FontUtilities.isLogging())
/*     */     {
/* 232 */       PlatformLogger localPlatformLogger2 = FontUtilities.getLogger();
/*     */ 
/* 234 */       long l2 = System.nanoTime();
/* 235 */       localPlatformLogger2.info("Time spent accessing fontconfig=" + (l2 - l1) / 1000000L + "ms.");
/*     */ 
/* 238 */       for (int m = 0; m < this.fontConfigFonts.length; m++) {
/* 239 */         FcCompFont localFcCompFont2 = this.fontConfigFonts[m];
/* 240 */         localPlatformLogger2.info("FC font " + localFcCompFont2.fcName + " maps to family " + localFcCompFont2.firstFont.familyName + " in file " + localFcCompFont2.firstFont.fontFile);
/*     */ 
/* 243 */         if (localFcCompFont2.allFonts != null)
/* 244 */           for (int n = 0; n < localFcCompFont2.allFonts.length; n++) {
/* 245 */             FontConfigFont localFontConfigFont2 = localFcCompFont2.allFonts[n];
/* 246 */             localPlatformLogger2.info("Family=" + localFontConfigFont2.familyName + " Style=" + localFontConfigFont2.styleStr + " Fullname=" + localFontConfigFont2.fullName + " File=" + localFontConfigFont2.fontFile);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public PhysicalFont registerFromFcInfo(FcCompFont paramFcCompFont)
/*     */   {
/* 258 */     SunFontManager localSunFontManager = SunFontManager.getInstance();
/*     */ 
/* 262 */     String str1 = paramFcCompFont.firstFont.fontFile;
/* 263 */     int i = str1.length() - 4;
/* 264 */     if (i <= 0) {
/* 265 */       return null;
/*     */     }
/* 267 */     String str2 = str1.substring(i).toLowerCase();
/* 268 */     boolean bool = str2.equals(".ttc");
/*     */ 
/* 275 */     PhysicalFont localPhysicalFont = localSunFontManager.getRegisteredFontFile(str1);
/*     */     Font2D localFont2D;
/* 276 */     if (localPhysicalFont != null) {
/* 277 */       if (bool) {
/* 278 */         localFont2D = localSunFontManager.findFont2D(paramFcCompFont.firstFont.familyName, paramFcCompFont.style, 0);
/*     */ 
/* 281 */         if ((localFont2D instanceof PhysicalFont)) {
/* 282 */           return (PhysicalFont)localFont2D;
/*     */         }
/* 284 */         return null;
/*     */       }
/*     */ 
/* 287 */       return localPhysicalFont;
/*     */     }
/*     */ 
/* 295 */     localPhysicalFont = localSunFontManager.findJREDeferredFont(paramFcCompFont.firstFont.familyName, paramFcCompFont.style);
/*     */ 
/* 301 */     if ((localPhysicalFont == null) && (localSunFontManager.isDeferredFont(str1) == true))
/*     */     {
/* 303 */       localPhysicalFont = localSunFontManager.initialiseDeferredFont(paramFcCompFont.firstFont.fontFile);
/*     */ 
/* 305 */       if (localPhysicalFont != null) {
/* 306 */         if (bool) {
/* 307 */           localFont2D = localSunFontManager.findFont2D(paramFcCompFont.firstFont.familyName, paramFcCompFont.style, 0);
/*     */ 
/* 310 */           if ((localFont2D instanceof PhysicalFont)) {
/* 311 */             return (PhysicalFont)localFont2D;
/*     */           }
/* 313 */           return null;
/*     */         }
/*     */ 
/* 316 */         return localPhysicalFont;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 324 */     if (localPhysicalFont == null) {
/* 325 */       int j = -1;
/* 326 */       int k = 6;
/*     */ 
/* 328 */       if ((str2.equals(".ttf")) || (bool)) {
/* 329 */         j = 0;
/* 330 */         k = 3;
/* 331 */       } else if ((str2.equals(".pfa")) || (str2.equals(".pfb"))) {
/* 332 */         j = 1;
/* 333 */         k = 4;
/*     */       }
/* 335 */       localPhysicalFont = localSunFontManager.registerFontFile(paramFcCompFont.firstFont.fontFile, null, j, true, k);
/*     */     }
/*     */ 
/* 338 */     return localPhysicalFont;
/*     */   }
/*     */ 
/*     */   public CompositeFont getFontConfigFont(String paramString, int paramInt)
/*     */   {
/* 347 */     paramString = paramString.toLowerCase();
/*     */ 
/* 349 */     initFontConfigFonts(false);
/*     */ 
/* 351 */     FcCompFont localFcCompFont1 = null;
/* 352 */     for (int i = 0; i < this.fontConfigFonts.length; i++) {
/* 353 */       if ((paramString.equals(this.fontConfigFonts[i].fcFamily)) && (paramInt == this.fontConfigFonts[i].style))
/*     */       {
/* 355 */         localFcCompFont1 = this.fontConfigFonts[i];
/* 356 */         break;
/*     */       }
/*     */     }
/* 359 */     if (localFcCompFont1 == null) {
/* 360 */       localFcCompFont1 = this.fontConfigFonts[0];
/*     */     }
/*     */ 
/* 363 */     if (FontUtilities.isLogging()) {
/* 364 */       FontUtilities.getLogger().info("FC name=" + paramString + " style=" + paramInt + " uses " + localFcCompFont1.firstFont.familyName + " in file: " + localFcCompFont1.firstFont.fontFile);
/*     */     }
/*     */ 
/* 370 */     if (localFcCompFont1.compFont != null) {
/* 371 */       return localFcCompFont1.compFont;
/*     */     }
/*     */ 
/* 377 */     FontManager localFontManager = FontManagerFactory.getInstance();
/* 378 */     CompositeFont localCompositeFont = (CompositeFont)localFontManager.findFont2D(localFcCompFont1.jdkName, paramInt, 2);
/*     */ 
/* 381 */     if ((localFcCompFont1.firstFont.familyName == null) || (localFcCompFont1.firstFont.fontFile == null))
/*     */     {
/* 383 */       return localFcCompFont1.compFont = localCompositeFont;
/*     */     }
/*     */ 
/* 394 */     FontFamily localFontFamily = FontFamily.getFamily(localFcCompFont1.firstFont.familyName);
/* 395 */     PhysicalFont localPhysicalFont = null;
/* 396 */     if (localFontFamily != null) {
/* 397 */       Font2D localFont2D = localFontFamily.getFontWithExactStyleMatch(localFcCompFont1.style);
/* 398 */       if ((localFont2D instanceof PhysicalFont)) {
/* 399 */         localPhysicalFont = (PhysicalFont)localFont2D;
/*     */       }
/*     */     }
/*     */ 
/* 403 */     if ((localPhysicalFont == null) || (!localFcCompFont1.firstFont.fontFile.equals(localPhysicalFont.platName)))
/*     */     {
/* 405 */       localPhysicalFont = registerFromFcInfo(localFcCompFont1);
/* 406 */       if (localPhysicalFont == null) {
/* 407 */         return localFcCompFont1.compFont = localCompositeFont;
/*     */       }
/* 409 */       localFontFamily = FontFamily.getFamily(localPhysicalFont.getFamilyName(null));
/*     */     }
/*     */ 
/* 421 */     for (int j = 0; j < this.fontConfigFonts.length; j++) {
/* 422 */       FcCompFont localFcCompFont2 = this.fontConfigFonts[j];
/* 423 */       if ((localFcCompFont2 != localFcCompFont1) && (localPhysicalFont.getFamilyName(null).equals(localFcCompFont2.firstFont.familyName)) && (!localFcCompFont2.firstFont.fontFile.equals(localPhysicalFont.platName)) && (localFontFamily.getFontWithExactStyleMatch(localFcCompFont2.style) == null))
/*     */       {
/* 428 */         registerFromFcInfo(this.fontConfigFonts[j]);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 436 */     return localFcCompFont1.compFont = new CompositeFont(localPhysicalFont, localCompositeFont);
/*     */   }
/*     */ 
/*     */   public FcCompFont[] getFontConfigFonts()
/*     */   {
/* 446 */     return this.fontConfigFonts;
/*     */   }
/*     */ 
/*     */   private static native void getFontConfig(String paramString, FontConfigInfo paramFontConfigInfo, FcCompFont[] paramArrayOfFcCompFont, boolean paramBoolean);
/*     */ 
/*     */   void populateFontConfig(FcCompFont[] paramArrayOfFcCompFont)
/*     */   {
/* 458 */     this.fontConfigFonts = paramArrayOfFcCompFont;
/*     */   }
/*     */ 
/*     */   FcCompFont[] loadFontConfig() {
/* 462 */     initFontConfigFonts(true);
/* 463 */     return this.fontConfigFonts;
/*     */   }
/*     */ 
/*     */   FontConfigInfo getFontConfigInfo() {
/* 467 */     initFontConfigFonts(true);
/* 468 */     return fcInfo;
/*     */   }
/*     */ 
/*     */   private static native int getFontConfigAASettings(String paramString1, String paramString2);
/*     */ 
/*     */   public static class FcCompFont
/*     */   {
/*     */     public String fcName;
/*     */     public String fcFamily;
/*     */     public String jdkName;
/*     */     public int style;
/*     */     public FontConfigManager.FontConfigFont firstFont;
/*     */     public FontConfigManager.FontConfigFont[] allFonts;
/*     */     public CompositeFont compFont;
/*     */   }
/*     */ 
/*     */   public static class FontConfigFont
/*     */   {
/*     */     public String familyName;
/*     */     public String styleStr;
/*     */     public String fullName;
/*     */     public String fontFile;
/*     */   }
/*     */ 
/*     */   public static class FontConfigInfo
/*     */   {
/*     */     public int fcVersion;
/*  70 */     public String[] cacheDirs = new String[4];
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.FontConfigManager
 * JD-Core Version:    0.6.2
 */