/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Scanner;
/*     */ import sun.awt.FontConfiguration;
/*     */ import sun.awt.X11FontManager;
/*     */ import sun.font.FontUtilities;
/*     */ import sun.font.SunFontManager;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class MFontConfiguration extends FontConfiguration
/*     */ {
/*  51 */   private static FontConfiguration fontConfig = null;
/*     */   private static PlatformLogger logger;
/*     */   private static final String fontsDirPrefix = "$JRE_LIB_FONTS";
/* 263 */   private String[][] motifFontSets = new String[5][4];
/*     */ 
/* 369 */   private static HashMap encodingMap = new HashMap();
/*     */ 
/*     */   public MFontConfiguration(SunFontManager paramSunFontManager)
/*     */   {
/*  55 */     super(paramSunFontManager);
/*  56 */     if (FontUtilities.debugFonts()) {
/*  57 */       logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
/*     */     }
/*  59 */     initTables();
/*     */   }
/*     */ 
/*     */   public MFontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  66 */     super(paramSunFontManager, paramBoolean1, paramBoolean2);
/*  67 */     if (FontUtilities.debugFonts()) {
/*  68 */       logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
/*     */     }
/*  70 */     initTables();
/*     */   }
/*     */ 
/*     */   protected void initReorderMap()
/*     */   {
/*  77 */     this.reorderMap = new HashMap();
/*  78 */     if (osName == null)
/*  79 */       initReorderMapForSolaris();
/*     */     else
/*  81 */       initReorderMapForLinux();
/*     */   }
/*     */ 
/*     */   private void initReorderMapForSolaris()
/*     */   {
/*  91 */     this.reorderMap.put("UTF-8.hi", "devanagari");
/*  92 */     this.reorderMap.put("UTF-8.ja", split("japanese-x0201,japanese-x0208,japanese-x0212"));
/*     */ 
/*  94 */     this.reorderMap.put("UTF-8.ko", "korean-johab");
/*  95 */     this.reorderMap.put("UTF-8.th", "thai");
/*  96 */     this.reorderMap.put("UTF-8.zh.TW", "chinese-big5");
/*  97 */     this.reorderMap.put("UTF-8.zh.HK", split("chinese-big5,chinese-hkscs"));
/*  98 */     if (FontUtilities.isSolaris8)
/*  99 */       this.reorderMap.put("UTF-8.zh.CN", split("chinese-gb2312,chinese-big5"));
/*     */     else {
/* 101 */       this.reorderMap.put("UTF-8.zh.CN", split("chinese-gb18030-0,chinese-gb18030-1"));
/*     */     }
/*     */ 
/* 104 */     this.reorderMap.put("UTF-8.zh", split("chinese-big5,chinese-hkscs,chinese-gb18030-0,chinese-gb18030-1"));
/*     */ 
/* 106 */     this.reorderMap.put("Big5", "chinese-big5");
/* 107 */     this.reorderMap.put("Big5-HKSCS", split("chinese-big5,chinese-hkscs"));
/* 108 */     if ((!FontUtilities.isSolaris8) && (!FontUtilities.isSolaris9))
/* 109 */       this.reorderMap.put("GB2312", split("chinese-gbk,chinese-gb2312"));
/*     */     else {
/* 111 */       this.reorderMap.put("GB2312", "chinese-gb2312");
/*     */     }
/* 113 */     this.reorderMap.put("x-EUC-TW", split("chinese-cns11643-1,chinese-cns11643-2,chinese-cns11643-3"));
/*     */ 
/* 115 */     this.reorderMap.put("GBK", "chinese-gbk");
/* 116 */     this.reorderMap.put("GB18030", split("chinese-gb18030-0,chinese-gb18030-1"));
/*     */ 
/* 118 */     this.reorderMap.put("TIS-620", "thai");
/* 119 */     this.reorderMap.put("x-PCK", split("japanese-x0201,japanese-x0208,japanese-x0212"));
/*     */ 
/* 121 */     this.reorderMap.put("x-eucJP-Open", split("japanese-x0201,japanese-x0208,japanese-x0212"));
/*     */ 
/* 123 */     this.reorderMap.put("EUC-KR", "korean");
/*     */ 
/* 126 */     this.reorderMap.put("ISO-8859-2", "latin-2");
/* 127 */     this.reorderMap.put("ISO-8859-5", "cyrillic-iso8859-5");
/* 128 */     this.reorderMap.put("windows-1251", "cyrillic-cp1251");
/* 129 */     this.reorderMap.put("KOI8-R", "cyrillic-koi8-r");
/* 130 */     this.reorderMap.put("ISO-8859-6", "arabic");
/* 131 */     this.reorderMap.put("ISO-8859-7", "greek");
/* 132 */     this.reorderMap.put("ISO-8859-8", "hebrew");
/* 133 */     this.reorderMap.put("ISO-8859-9", "latin-5");
/* 134 */     this.reorderMap.put("ISO-8859-13", "latin-7");
/* 135 */     this.reorderMap.put("ISO-8859-15", "latin-9");
/*     */   }
/*     */ 
/*     */   private void initReorderMapForLinux() {
/* 139 */     this.reorderMap.put("UTF-8.ja.JP", "japanese-iso10646");
/* 140 */     this.reorderMap.put("UTF-8.ko.KR", "korean-iso10646");
/* 141 */     this.reorderMap.put("UTF-8.zh.TW", "chinese-tw-iso10646");
/* 142 */     this.reorderMap.put("UTF-8.zh.HK", "chinese-tw-iso10646");
/* 143 */     this.reorderMap.put("UTF-8.zh.CN", "chinese-cn-iso10646");
/* 144 */     this.reorderMap.put("x-euc-jp-linux", split("japanese-x0201,japanese-x0208"));
/*     */ 
/* 146 */     this.reorderMap.put("GB2312", "chinese-gb18030");
/* 147 */     this.reorderMap.put("Big5", "chinese-big5");
/* 148 */     this.reorderMap.put("EUC-KR", "korean");
/* 149 */     if (osName.equals("Sun")) {
/* 150 */       this.reorderMap.put("GB18030", "chinese-cn-iso10646");
/*     */     }
/*     */     else
/* 153 */       this.reorderMap.put("GB18030", "chinese-gb18030");
/*     */   }
/*     */ 
/*     */   protected void setOsNameAndVersion()
/*     */   {
/* 161 */     super.setOsNameAndVersion();
/*     */ 
/* 163 */     if (osName.equals("SunOS"))
/*     */     {
/* 165 */       osName = null;
/* 166 */     } else if (osName.equals("Linux"))
/*     */       try
/*     */       {
/*     */         File localFile;
/* 169 */         if ((localFile = new File("/etc/fedora-release")).canRead()) {
/* 170 */           osName = "Fedora";
/* 171 */           osVersion = getVersionString(localFile);
/* 172 */         } else if ((localFile = new File("/etc/redhat-release")).canRead()) {
/* 173 */           osName = "RedHat";
/* 174 */           osVersion = getVersionString(localFile);
/* 175 */         } else if ((localFile = new File("/etc/turbolinux-release")).canRead()) {
/* 176 */           osName = "Turbo";
/* 177 */           osVersion = getVersionString(localFile);
/* 178 */         } else if ((localFile = new File("/etc/SuSE-release")).canRead()) {
/* 179 */           osName = "SuSE";
/* 180 */           osVersion = getVersionString(localFile);
/* 181 */         } else if ((localFile = new File("/etc/lsb-release")).canRead())
/*     */         {
/* 186 */           Properties localProperties = new Properties();
/* 187 */           localProperties.load(new FileInputStream(localFile));
/* 188 */           osName = localProperties.getProperty("DISTRIB_ID");
/* 189 */           osVersion = localProperties.getProperty("DISTRIB_RELEASE");
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private String getVersionString(File paramFile)
/*     */   {
/*     */     try
/*     */     {
/* 202 */       Scanner localScanner = new Scanner(paramFile);
/* 203 */       return localScanner.findInLine("(\\d)+((\\.)(\\d)+)*");
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */   protected String mapFileName(String paramString)
/*     */   {
/* 213 */     if ((paramString != null) && (paramString.startsWith("$JRE_LIB_FONTS"))) {
/* 214 */       return SunFontManager.jreFontDirName + paramString.substring("$JRE_LIB_FONTS".length());
/*     */     }
/*     */ 
/* 217 */     return paramString;
/*     */   }
/*     */ 
/*     */   public String getFallbackFamilyName(String paramString1, String paramString2)
/*     */   {
/* 224 */     String str = getCompatibilityFamilyName(paramString1);
/* 225 */     if (str != null) {
/* 226 */       return str;
/*     */     }
/* 228 */     return paramString2;
/*     */   }
/*     */ 
/*     */   protected String getEncoding(String paramString1, String paramString2)
/*     */   {
/* 234 */     int i = 0;
/* 235 */     int j = 13;
/* 236 */     while ((j-- > 0) && (i >= 0)) {
/* 237 */       i = paramString1.indexOf("-", i) + 1;
/*     */     }
/* 239 */     if (i == -1) {
/* 240 */       return "default";
/*     */     }
/* 242 */     String str1 = paramString1.substring(i);
/* 243 */     if (str1.indexOf("fontspecific") > 0) {
/* 244 */       if (paramString1.indexOf("dingbats") > 0)
/* 245 */         return "sun.awt.motif.X11Dingbats";
/* 246 */       if (paramString1.indexOf("symbol") > 0) {
/* 247 */         return "sun.awt.Symbol";
/*     */       }
/*     */     }
/* 250 */     String str2 = (String)encodingMap.get(str1);
/* 251 */     if (str2 == null) {
/* 252 */       str2 = "default";
/*     */     }
/* 254 */     return str2;
/*     */   }
/*     */ 
/*     */   protected Charset getDefaultFontCharset(String paramString) {
/* 258 */     return Charset.forName("ISO8859_1");
/*     */   }
/*     */ 
/*     */   public String getMotifFontSet(String paramString, int paramInt)
/*     */   {
/* 266 */     assert (isLogicalFontFamilyName(paramString));
/* 267 */     paramString = paramString.toLowerCase(Locale.ENGLISH);
/* 268 */     int i = getFontIndex(paramString);
/* 269 */     int j = getStyleIndex(paramInt);
/* 270 */     return getMotifFontSet(i, j);
/*     */   }
/*     */ 
/*     */   private String getMotifFontSet(int paramInt1, int paramInt2) {
/* 274 */     String str = this.motifFontSets[paramInt1][paramInt2];
/* 275 */     if (str == null) {
/* 276 */       str = buildMotifFontSet(paramInt1, paramInt2);
/* 277 */       this.motifFontSets[paramInt1][paramInt2] = str;
/*     */     }
/* 279 */     return str;
/*     */   }
/*     */ 
/*     */   private String buildMotifFontSet(int paramInt1, int paramInt2) {
/* 283 */     StringBuilder localStringBuilder = new StringBuilder();
/* 284 */     short[] arrayOfShort = getCoreScripts(paramInt1);
/* 285 */     for (int i = 0; i < arrayOfShort.length; i++) {
/* 286 */       short s = getComponentFontIDMotif(arrayOfShort[i], paramInt1, paramInt2);
/* 287 */       if (s == 0) {
/* 288 */         s = getComponentFontID(arrayOfShort[i], paramInt1, paramInt2);
/*     */       }
/* 290 */       String str = getComponentFontName(s);
/* 291 */       if ((str != null) && (!str.endsWith("fontspecific")))
/*     */       {
/* 294 */         if (localStringBuilder.length() > 0) {
/* 295 */           localStringBuilder.append(',');
/*     */         }
/* 297 */         localStringBuilder.append(str);
/*     */       }
/*     */     }
/* 299 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   protected String getFaceNameFromComponentFontName(String paramString) {
/* 303 */     return null;
/*     */   }
/*     */ 
/*     */   protected String getFileNameFromComponentFontName(String paramString)
/*     */   {
/* 310 */     String str = getFileNameFromPlatformName(paramString);
/* 311 */     if ((str != null) && (str.charAt(0) == '/') && (!needToSearchForFile(str)))
/*     */     {
/* 313 */       return str;
/*     */     }
/* 315 */     return ((X11FontManager)this.fontManager).getFileNameFromXLFD(paramString);
/*     */   }
/*     */ 
/*     */   private static String getDefaultMotifFontSet()
/*     */   {
/* 338 */     String str = ((MFontConfiguration)getFontConfiguration()).getMotifFontSet("sansserif", 0);
/* 339 */     if (str != null)
/*     */     {
/*     */       int i;
/* 341 */       while ((i = str.indexOf("%d")) >= 0) {
/* 342 */         str = str.substring(0, i) + "140" + str.substring(i + 2);
/*     */       }
/*     */     }
/* 345 */     return str;
/*     */   }
/*     */ 
/*     */   public HashSet<String> getAWTFontPathSet() {
/* 349 */     HashSet localHashSet = new HashSet();
/* 350 */     short[] arrayOfShort = getCoreScripts(0);
/* 351 */     for (int i = 0; i < arrayOfShort.length; i++) {
/* 352 */       String str = getString(table_awtfontpaths[arrayOfShort[i]]);
/* 353 */       if (str != null) {
/* 354 */         int j = 0;
/* 355 */         int k = str.indexOf(':');
/* 356 */         while (k >= 0) {
/* 357 */           localHashSet.add(str.substring(j, k));
/* 358 */           j = k + 1;
/* 359 */           k = str.indexOf(':', j);
/*     */         }
/* 361 */         localHashSet.add(j == 0 ? str : str.substring(j));
/*     */       }
/*     */     }
/* 364 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   private void initTables()
/*     */   {
/* 374 */     encodingMap.put("iso8859-1", "ISO-8859-1");
/* 375 */     encodingMap.put("iso8859-2", "ISO-8859-2");
/* 376 */     encodingMap.put("iso8859-4", "ISO-8859-4");
/* 377 */     encodingMap.put("iso8859-5", "ISO-8859-5");
/* 378 */     encodingMap.put("iso8859-6", "ISO-8859-6");
/* 379 */     encodingMap.put("iso8859-7", "ISO-8859-7");
/* 380 */     encodingMap.put("iso8859-8", "ISO-8859-8");
/* 381 */     encodingMap.put("iso8859-9", "ISO-8859-9");
/* 382 */     encodingMap.put("iso8859-13", "ISO-8859-13");
/* 383 */     encodingMap.put("iso8859-15", "ISO-8859-15");
/* 384 */     encodingMap.put("gb2312.1980-0", "sun.awt.motif.X11GB2312");
/* 385 */     if (osName == null)
/*     */     {
/* 387 */       encodingMap.put("gbk-0", "GBK");
/*     */     }
/* 389 */     else encodingMap.put("gbk-0", "sun.awt.motif.X11GBK");
/*     */ 
/* 391 */     encodingMap.put("gb18030.2000-0", "sun.awt.motif.X11GB18030_0");
/* 392 */     encodingMap.put("gb18030.2000-1", "sun.awt.motif.X11GB18030_1");
/* 393 */     encodingMap.put("cns11643-1", "sun.awt.motif.X11CNS11643P1");
/* 394 */     encodingMap.put("cns11643-2", "sun.awt.motif.X11CNS11643P2");
/* 395 */     encodingMap.put("cns11643-3", "sun.awt.motif.X11CNS11643P3");
/* 396 */     encodingMap.put("big5-1", "Big5");
/* 397 */     encodingMap.put("big5-0", "Big5");
/* 398 */     encodingMap.put("hkscs-1", "Big5-HKSCS");
/* 399 */     encodingMap.put("ansi-1251", "windows-1251");
/* 400 */     encodingMap.put("koi8-r", "KOI8-R");
/* 401 */     encodingMap.put("jisx0201.1976-0", "sun.awt.motif.X11JIS0201");
/* 402 */     encodingMap.put("jisx0208.1983-0", "sun.awt.motif.X11JIS0208");
/* 403 */     encodingMap.put("jisx0212.1990-0", "sun.awt.motif.X11JIS0212");
/* 404 */     encodingMap.put("ksc5601.1987-0", "sun.awt.motif.X11KSC5601");
/* 405 */     encodingMap.put("ksc5601.1992-3", "sun.awt.motif.X11Johab");
/* 406 */     encodingMap.put("tis620.2533-0", "TIS-620");
/* 407 */     encodingMap.put("iso10646-1", "UTF-16BE");
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.MFontConfiguration
 * JD-Core Version:    0.6.2
 */