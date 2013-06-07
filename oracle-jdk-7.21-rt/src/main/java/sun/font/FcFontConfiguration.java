/*     */ package sun.font;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Scanner;
/*     */ import sun.awt.FontConfiguration;
/*     */ import sun.awt.FontDescriptor;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.awt.X11FontManager;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class FcFontConfiguration extends FontConfiguration
/*     */ {
/*     */   private static final String fileVersion = "1";
/*  69 */   private String fcInfoFileName = null;
/*     */ 
/*  71 */   private FontConfigManager.FcCompFont[] fcCompFonts = null;
/*     */ 
/*     */   public FcFontConfiguration(SunFontManager paramSunFontManager) {
/*  74 */     super(paramSunFontManager);
/*  75 */     init();
/*     */   }
/*     */ 
/*     */   public FcFontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/*  82 */     super(paramSunFontManager, paramBoolean1, paramBoolean2);
/*  83 */     init();
/*     */   }
/*     */ 
/*     */   public synchronized boolean init()
/*     */   {
/*  88 */     if (this.fcCompFonts != null) {
/*  89 */       return true;
/*     */     }
/*     */ 
/*  92 */     setFontConfiguration();
/*  93 */     readFcInfo();
/*  94 */     X11FontManager localX11FontManager = (X11FontManager)this.fontManager;
/*  95 */     FontConfigManager localFontConfigManager = localX11FontManager.getFontConfigManager();
/*  96 */     if (this.fcCompFonts == null) {
/*  97 */       this.fcCompFonts = localFontConfigManager.loadFontConfig();
/*  98 */       if (this.fcCompFonts != null) {
/*     */         try {
/* 100 */           writeFcInfo();
/*     */         } catch (Exception localException) {
/* 102 */           if (FontUtilities.debugFonts())
/* 103 */             warning("Exception writing fcInfo " + localException);
/*     */         }
/*     */       }
/* 106 */       else if (FontUtilities.debugFonts())
/* 107 */         warning("Failed to get info from libfontconfig");
/*     */     }
/*     */     else {
/* 110 */       localFontConfigManager.populateFontConfig(this.fcCompFonts);
/*     */     }
/*     */ 
/* 113 */     if (this.fcCompFonts == null) {
/* 114 */       return false;
/*     */     }
/*     */ 
/* 118 */     String str1 = System.getProperty("java.home");
/* 119 */     if (str1 == null) {
/* 120 */       throw new Error("java.home property not set");
/*     */     }
/* 122 */     String str2 = str1 + File.separator + "lib";
/* 123 */     getInstalledFallbackFonts(str2);
/*     */ 
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   public String getFallbackFamilyName(String paramString1, String paramString2)
/*     */   {
/* 133 */     String str = getCompatibilityFamilyName(paramString1);
/* 134 */     if (str != null) {
/* 135 */       return str;
/*     */     }
/* 137 */     return paramString2;
/*     */   }
/*     */ 
/*     */   protected String getFaceNameFromComponentFontName(String paramString)
/*     */   {
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */   protected String getFileNameFromComponentFontName(String paramString)
/*     */   {
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public String getFileNameFromPlatformName(String paramString)
/*     */   {
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   protected Charset getDefaultFontCharset(String paramString)
/*     */   {
/* 161 */     return Charset.forName("ISO8859_1");
/*     */   }
/*     */ 
/*     */   protected String getEncoding(String paramString1, String paramString2)
/*     */   {
/* 167 */     return "default";
/*     */   }
/*     */ 
/*     */   protected void initReorderMap()
/*     */   {
/* 172 */     this.reorderMap = new HashMap();
/*     */   }
/*     */ 
/*     */   public FontDescriptor[] getFontDescriptors(String paramString, int paramInt)
/*     */   {
/* 177 */     return new FontDescriptor[0];
/*     */   }
/*     */ 
/*     */   public int getNumberCoreFonts()
/*     */   {
/* 182 */     return 1;
/*     */   }
/*     */ 
/*     */   public String[] getPlatformFontNames()
/*     */   {
/* 187 */     HashSet localHashSet = new HashSet();
/* 188 */     X11FontManager localX11FontManager = (X11FontManager)this.fontManager;
/* 189 */     FontConfigManager localFontConfigManager = localX11FontManager.getFontConfigManager();
/* 190 */     FontConfigManager.FcCompFont[] arrayOfFcCompFont = localFontConfigManager.loadFontConfig();
/* 191 */     for (int i = 0; i < arrayOfFcCompFont.length; i++) {
/* 192 */       for (int j = 0; j < arrayOfFcCompFont[i].allFonts.length; j++) {
/* 193 */         localHashSet.add(arrayOfFcCompFont[i].allFonts[j].fontFile);
/*     */       }
/*     */     }
/* 196 */     return (String[])localHashSet.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getExtraFontPath()
/*     */   {
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean needToSearchForFile(String paramString)
/*     */   {
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */   private FontConfigManager.FontConfigFont[] getFcFontList(FontConfigManager.FcCompFont[] paramArrayOfFcCompFont, String paramString, int paramInt)
/*     */   {
/* 212 */     if (paramString.equals("dialog"))
/* 213 */       paramString = "sansserif";
/* 214 */     else if (paramString.equals("dialoginput")) {
/* 215 */       paramString = "monospaced";
/*     */     }
/* 217 */     for (int i = 0; i < paramArrayOfFcCompFont.length; i++) {
/* 218 */       if ((paramString.equals(paramArrayOfFcCompFont[i].jdkName)) && (paramInt == paramArrayOfFcCompFont[i].style))
/*     */       {
/* 220 */         return paramArrayOfFcCompFont[i].allFonts;
/*     */       }
/*     */     }
/* 223 */     return paramArrayOfFcCompFont[0].allFonts;
/*     */   }
/*     */ 
/*     */   public CompositeFontDescriptor[] get2DCompositeFontInfo()
/*     */   {
/* 229 */     X11FontManager localX11FontManager = (X11FontManager)this.fontManager;
/* 230 */     FontConfigManager localFontConfigManager = localX11FontManager.getFontConfigManager();
/* 231 */     FontConfigManager.FcCompFont[] arrayOfFcCompFont = localFontConfigManager.loadFontConfig();
/*     */ 
/* 233 */     CompositeFontDescriptor[] arrayOfCompositeFontDescriptor = new CompositeFontDescriptor[20];
/*     */ 
/* 236 */     for (int i = 0; i < 5; i++) {
/* 237 */       String str1 = publicFontNames[i];
/*     */ 
/* 239 */       for (int j = 0; j < 4; j++)
/*     */       {
/* 241 */         String str2 = str1 + "." + styleNames[j];
/* 242 */         FontConfigManager.FontConfigFont[] arrayOfFontConfigFont = getFcFontList(arrayOfFcCompFont, fontNames[i], j);
/*     */ 
/* 246 */         int k = arrayOfFontConfigFont.length;
/*     */ 
/* 248 */         if (installedFallbackFontFiles != null) {
/* 249 */           k += installedFallbackFontFiles.length;
/*     */         }
/*     */ 
/* 252 */         String[] arrayOfString = new String[k];
/*     */ 
/* 255 */         for (int m = 0; m < arrayOfFontConfigFont.length; m++) {
/* 256 */           arrayOfString[m] = arrayOfFontConfigFont[m].fontFile;
/*     */         }
/*     */ 
/* 259 */         if (installedFallbackFontFiles != null) {
/* 260 */           System.arraycopy(installedFallbackFontFiles, 0, arrayOfString, arrayOfFontConfigFont.length, installedFallbackFontFiles.length);
/*     */         }
/*     */ 
/* 265 */         arrayOfCompositeFontDescriptor[(i * 4 + j)] = new CompositeFontDescriptor(str2, 1, null, arrayOfString, null, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 274 */     return arrayOfCompositeFontDescriptor;
/*     */   }
/*     */ 
/*     */   private String getVersionString(File paramFile)
/*     */   {
/*     */     try
/*     */     {
/* 282 */       Scanner localScanner = new Scanner(paramFile);
/* 283 */       return localScanner.findInLine("(\\d)+((\\.)(\\d)+)*");
/*     */     }
/*     */     catch (Exception localException) {
/*     */     }
/* 287 */     return null;
/*     */   }
/*     */ 
/*     */   protected void setOsNameAndVersion()
/*     */   {
/* 296 */     super.setOsNameAndVersion();
/*     */ 
/* 298 */     if (!osName.equals("Linux"))
/* 299 */       return;
/*     */     try
/*     */     {
/*     */       File localFile;
/* 303 */       if ((localFile = new File("/etc/lsb-release")).canRead())
/*     */       {
/* 308 */         Properties localProperties = new Properties();
/* 309 */         localProperties.load(new FileInputStream(localFile));
/* 310 */         osName = localProperties.getProperty("DISTRIB_ID");
/* 311 */         osVersion = localProperties.getProperty("DISTRIB_RELEASE");
/* 312 */       } else if ((localFile = new File("/etc/redhat-release")).canRead()) {
/* 313 */         osName = "RedHat";
/* 314 */         osVersion = getVersionString(localFile);
/* 315 */       } else if ((localFile = new File("/etc/SuSE-release")).canRead()) {
/* 316 */         osName = "SuSE";
/* 317 */         osVersion = getVersionString(localFile);
/* 318 */       } else if ((localFile = new File("/etc/turbolinux-release")).canRead()) {
/* 319 */         osName = "Turbo";
/* 320 */         osVersion = getVersionString(localFile);
/* 321 */       } else if ((localFile = new File("/etc/fedora-release")).canRead()) {
/* 322 */         osName = "Fedora";
/* 323 */         osVersion = getVersionString(localFile);
/*     */       }
/*     */     } catch (Exception localException) {
/* 326 */       if (FontUtilities.debugFonts())
/* 327 */         warning("Exception identifying Linux distro.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private File getFcInfoFile()
/*     */   {
/* 333 */     if (this.fcInfoFileName == null)
/*     */     {
/*     */       String str1;
/*     */       try
/*     */       {
/* 341 */         str1 = InetAddress.getLocalHost().getHostName();
/*     */       } catch (UnknownHostException localUnknownHostException) {
/* 343 */         str1 = "localhost";
/*     */       }
/* 345 */       String str2 = System.getProperty("user.home");
/* 346 */       String str3 = System.getProperty("java.version");
/* 347 */       String str4 = File.separator;
/* 348 */       String str5 = str2 + str4 + ".java" + str4 + "fonts" + str4 + str3;
/* 349 */       String str6 = SunToolkit.getStartupLocale().getLanguage();
/* 350 */       String str7 = "fcinfo-1-" + str1 + "-" + osName + "-" + osVersion + "-" + str6 + ".properties";
/*     */ 
/* 352 */       this.fcInfoFileName = (str5 + str4 + str7);
/*     */     }
/* 354 */     return new File(this.fcInfoFileName);
/*     */   }
/*     */ 
/*     */   private void writeFcInfo() {
/* 358 */     Properties localProperties = new Properties();
/* 359 */     localProperties.setProperty("version", "1");
/* 360 */     X11FontManager localX11FontManager = (X11FontManager)this.fontManager;
/* 361 */     FontConfigManager localFontConfigManager = localX11FontManager.getFontConfigManager();
/* 362 */     FontConfigManager.FontConfigInfo localFontConfigInfo = localFontConfigManager.getFontConfigInfo();
/* 363 */     localProperties.setProperty("fcversion", Integer.toString(localFontConfigInfo.fcVersion));
/* 364 */     if (localFontConfigInfo.cacheDirs != null)
/* 365 */       for (i = 0; i < localFontConfigInfo.cacheDirs.length; i++)
/* 366 */         if (localFontConfigInfo.cacheDirs[i] != null)
/* 367 */           localProperties.setProperty("cachedir." + i, localFontConfigInfo.cacheDirs[i]);
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 371 */     for (int i = 0; i < this.fcCompFonts.length; i++) {
/* 372 */       localObject1 = this.fcCompFonts[i];
/* 373 */       localObject2 = ((FontConfigManager.FcCompFont)localObject1).jdkName + "." + ((FontConfigManager.FcCompFont)localObject1).style;
/* 374 */       localProperties.setProperty((String)localObject2 + ".length", Integer.toString(((FontConfigManager.FcCompFont)localObject1).allFonts.length));
/*     */ 
/* 376 */       for (int j = 0; j < ((FontConfigManager.FcCompFont)localObject1).allFonts.length; j++) {
/* 377 */         localProperties.setProperty((String)localObject2 + "." + j + ".family", localObject1.allFonts[j].familyName);
/*     */ 
/* 379 */         localProperties.setProperty((String)localObject2 + "." + j + ".file", localObject1.allFonts[j].fontFile);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 388 */       File localFile = getFcInfoFile();
/* 389 */       localObject1 = localFile.getParentFile();
/* 390 */       ((File)localObject1).mkdirs();
/* 391 */       localObject2 = Files.createTempFile(((File)localObject1).toPath(), "fcinfo", null, new FileAttribute[0]).toFile();
/* 392 */       FileOutputStream localFileOutputStream = new FileOutputStream((File)localObject2);
/* 393 */       localProperties.store(localFileOutputStream, "JDK Font Configuration Generated File: *Do Not Edit*");
/*     */ 
/* 395 */       localFileOutputStream.close();
/* 396 */       boolean bool = ((File)localObject2).renameTo(localFile);
/* 397 */       if ((!bool) && (FontUtilities.debugFonts())) {
/* 398 */         System.out.println("rename failed");
/* 399 */         warning("Failed renaming file to " + getFcInfoFile());
/*     */       }
/*     */     } catch (Exception localException) {
/* 402 */       if (FontUtilities.debugFonts())
/* 403 */         warning("IOException writing to " + getFcInfoFile());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readFcInfo()
/*     */   {
/* 414 */     File localFile = getFcInfoFile();
/* 415 */     if (!localFile.exists()) {
/* 416 */       return;
/*     */     }
/* 418 */     Properties localProperties = new Properties();
/* 419 */     X11FontManager localX11FontManager = (X11FontManager)this.fontManager;
/* 420 */     FontConfigManager localFontConfigManager = localX11FontManager.getFontConfigManager();
/*     */     try {
/* 422 */       FileInputStream localFileInputStream = new FileInputStream(localFile);
/* 423 */       localProperties.load(localFileInputStream);
/* 424 */       localFileInputStream.close();
/*     */     } catch (IOException localIOException) {
/* 426 */       if (FontUtilities.debugFonts()) {
/* 427 */         warning("IOException reading from " + localFile.toString());
/*     */       }
/* 429 */       return;
/*     */     }
/* 431 */     String str1 = (String)localProperties.get("version");
/* 432 */     if ((str1 == null) || (!str1.equals("1"))) {
/* 433 */       return;
/*     */     }
/*     */ 
/* 438 */     String str2 = (String)localProperties.get("fcversion");
/* 439 */     if (str2 != null) {
/*     */       try
/*     */       {
/* 442 */         int i = Integer.parseInt(str2);
/* 443 */         if ((i != 0) && (i != FontConfigManager.getFontConfigVersion()))
/*     */         {
/* 445 */           return;
/*     */         }
/*     */       } catch (Exception localException) {
/* 448 */         if (FontUtilities.debugFonts()) {
/* 449 */           warning("Exception parsing version " + str2);
/*     */         }
/* 451 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 458 */     long l = localFile.lastModified();
/* 459 */     int j = 0;
/* 460 */     while (j < 4) {
/* 461 */       localObject1 = (String)localProperties.get("cachedir." + j);
/* 462 */       if (localObject1 == null) {
/*     */         break;
/*     */       }
/* 465 */       localObject2 = new File((String)localObject1);
/* 466 */       if ((((File)localObject2).exists()) && (((File)localObject2).lastModified() > l)) {
/* 467 */         return;
/*     */       }
/* 469 */       j++;
/*     */     }
/*     */ 
/* 472 */     Object localObject1 = { "sansserif", "serif", "monospaced" };
/* 473 */     Object localObject2 = { "sans", "serif", "monospace" };
/* 474 */     int k = localObject1.length;
/* 475 */     int m = 4;
/* 476 */     FontConfigManager.FcCompFont[] arrayOfFcCompFont = new FontConfigManager.FcCompFont[k * m];
/*     */     try
/*     */     {
/* 479 */       for (int n = 0; n < k; n++) {
/* 480 */         for (int i1 = 0; i1 < m; i1++) {
/* 481 */           int i2 = n * m + i1;
/* 482 */           arrayOfFcCompFont[i2] = new FontConfigManager.FcCompFont();
/* 483 */           String str3 = localObject1[n] + "." + i1;
/* 484 */           arrayOfFcCompFont[i2].jdkName = localObject1[n];
/* 485 */           arrayOfFcCompFont[i2].fcFamily = localObject2[n];
/* 486 */           arrayOfFcCompFont[i2].style = i1;
/* 487 */           String str4 = (String)localProperties.get(str3 + ".length");
/* 488 */           int i3 = Integer.parseInt(str4);
/* 489 */           if (i3 <= 0) {
/* 490 */             return;
/*     */           }
/* 492 */           arrayOfFcCompFont[i2].allFonts = new FontConfigManager.FontConfigFont[i3];
/* 493 */           for (int i4 = 0; i4 < i3; i4++) {
/* 494 */             arrayOfFcCompFont[i2].allFonts[i4] = new FontConfigManager.FontConfigFont();
/* 495 */             String str5 = str3 + "." + i4 + ".family";
/* 496 */             String str6 = (String)localProperties.get(str5);
/* 497 */             arrayOfFcCompFont[i2].allFonts[i4].familyName = str6;
/* 498 */             str5 = str3 + "." + i4 + ".file";
/* 499 */             String str7 = (String)localProperties.get(str5);
/* 500 */             if (str7 == null) {
/* 501 */               return;
/*     */             }
/* 503 */             arrayOfFcCompFont[i2].allFonts[i4].fontFile = str7;
/*     */           }
/* 505 */           arrayOfFcCompFont[i2].firstFont = arrayOfFcCompFont[i2].allFonts[0];
/*     */         }
/*     */       }
/*     */ 
/* 509 */       this.fcCompFonts = arrayOfFcCompFont;
/*     */     } catch (Throwable localThrowable) {
/* 511 */       if (FontUtilities.debugFonts())
/* 512 */         warning(localThrowable.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void warning(String paramString)
/*     */   {
/* 518 */     PlatformLogger localPlatformLogger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
/* 519 */     localPlatformLogger.warning(paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.FcFontConfiguration
 * JD-Core Version:    0.6.2
 */