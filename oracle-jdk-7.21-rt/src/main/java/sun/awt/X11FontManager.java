/*     */ package sun.awt;
/*     */ 
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.StreamTokenizer;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.swing.plaf.FontUIResource;
/*     */ import sun.awt.motif.MFontConfiguration;
/*     */ import sun.font.CompositeFont;
/*     */ import sun.font.FcFontConfiguration;
/*     */ import sun.font.FontAccess;
/*     */ import sun.font.FontConfigManager;
/*     */ import sun.font.FontConfigManager.FcCompFont;
/*     */ import sun.font.FontConfigManager.FontConfigFont;
/*     */ import sun.font.FontUtilities;
/*     */ import sun.font.NativeFont;
/*     */ import sun.font.SunFontManager;
/*     */ import sun.util.logging.PlatformLogger;
/*     */ 
/*     */ public class X11FontManager extends SunFontManager
/*     */ {
/*     */   private static final int FOUNDRY_FIELD = 1;
/*     */   private static final int FAMILY_NAME_FIELD = 2;
/*     */   private static final int WEIGHT_NAME_FIELD = 3;
/*     */   private static final int SLANT_FIELD = 4;
/*     */   private static final int SETWIDTH_NAME_FIELD = 5;
/*     */   private static final int ADD_STYLE_NAME_FIELD = 6;
/*     */   private static final int PIXEL_SIZE_FIELD = 7;
/*     */   private static final int POINT_SIZE_FIELD = 8;
/*     */   private static final int RESOLUTION_X_FIELD = 9;
/*     */   private static final int RESOLUTION_Y_FIELD = 10;
/*     */   private static final int SPACING_FIELD = 11;
/*     */   private static final int AVERAGE_WIDTH_FIELD = 12;
/*     */   private static final int CHARSET_REGISTRY_FIELD = 13;
/*     */   private static final int CHARSET_ENCODING_FIELD = 14;
/*  62 */   private static Map fontNameMap = new HashMap();
/*     */ 
/*  75 */   private static Map xlfdMap = new HashMap();
/*     */   private static Map xFontDirsMap;
/* 103 */   private static HashSet<String> fontConfigDirs = null;
/*     */ 
/* 113 */   HashMap<String, String> oblmap = null;
/*     */ 
/* 124 */   private static HashMap registeredDirs = new HashMap();
/*     */ 
/* 130 */   private static String[] fontdirs = null;
/*     */ 
/* 132 */   private static String[] defaultPlatformFont = null;
/*     */ 
/* 134 */   private FontConfigManager fcManager = null;
/*     */ 
/*     */   public static X11FontManager getInstance() {
/* 137 */     return (X11FontManager)SunFontManager.getInstance();
/*     */   }
/*     */ 
/*     */   public String getFileNameFromPlatformName(String paramString)
/*     */   {
/* 153 */     if (paramString.startsWith("/")) {
/* 154 */       return paramString;
/*     */     }
/*     */ 
/* 157 */     String str1 = null;
/* 158 */     String str2 = specificFontIDForName(paramString);
/*     */ 
/* 166 */     str1 = super.getFileNameFromPlatformName(paramString);
/*     */     Object localObject;
/* 167 */     if (str1 != null) {
/* 168 */       if ((isHeadless()) && (str1.startsWith("-")))
/*     */       {
/* 170 */         return null;
/*     */       }
/* 172 */       if (str1.startsWith("/"))
/*     */       {
/* 186 */         localObject = (Vector)xlfdMap.get(str1);
/* 187 */         if (localObject == null)
/*     */         {
/* 193 */           if (getFontConfiguration().needToSearchForFile(str1)) {
/* 194 */             str1 = null;
/*     */           }
/* 196 */           if (str1 != null) {
/* 197 */             localObject = new Vector();
/* 198 */             ((Vector)localObject).add(paramString);
/* 199 */             xlfdMap.put(str1, localObject);
/*     */           }
/*     */         }
/* 202 */         else if (!((Vector)localObject).contains(paramString)) {
/* 203 */           ((Vector)localObject).add(paramString);
/*     */         }
/*     */       }
/*     */ 
/* 207 */       if (str1 != null) {
/* 208 */         fontNameMap.put(str2, str1);
/* 209 */         return str1;
/*     */       }
/*     */     }
/*     */ 
/* 213 */     if (str2 != null) {
/* 214 */       str1 = (String)fontNameMap.get(str2);
/*     */ 
/* 216 */       if ((str1 == null) && (FontUtilities.isLinux) && (!isOpenJDK())) {
/* 217 */         if (this.oblmap == null) {
/* 218 */           initObliqueLucidaFontMap();
/*     */         }
/* 220 */         localObject = getObliqueLucidaFontID(str2);
/* 221 */         if (localObject != null) {
/* 222 */           str1 = (String)this.oblmap.get(localObject);
/*     */         }
/*     */       }
/* 225 */       if ((this.fontPath == null) && ((str1 == null) || (!str1.startsWith("/"))))
/*     */       {
/* 227 */         if (FontUtilities.debugFonts()) {
/* 228 */           FontUtilities.getLogger().warning("** Registering all font paths because can't find file for " + paramString);
/*     */         }
/*     */ 
/* 232 */         this.fontPath = getPlatformFontPath(noType1Font);
/* 233 */         registerFontDirs(this.fontPath);
/* 234 */         if (FontUtilities.debugFonts()) {
/* 235 */           FontUtilities.getLogger().warning("** Finished registering all font paths");
/*     */         }
/*     */ 
/* 238 */         str1 = (String)fontNameMap.get(str2);
/*     */       }
/* 240 */       if ((str1 == null) && (!isHeadless()))
/*     */       {
/* 244 */         str1 = getX11FontName(paramString);
/*     */       }
/* 246 */       if (str1 == null) {
/* 247 */         str2 = switchFontIDForName(paramString);
/* 248 */         str1 = (String)fontNameMap.get(str2);
/*     */       }
/* 250 */       if (str1 != null) {
/* 251 */         fontNameMap.put(str2, str1);
/*     */       }
/*     */     }
/* 254 */     return str1;
/*     */   }
/*     */ 
/*     */   protected String[] getNativeNames(String paramString1, String paramString2)
/*     */   {
/*     */     Vector localVector;
/* 261 */     if ((localVector = (Vector)xlfdMap.get(paramString1)) == null) {
/* 262 */       if (paramString2 == null) {
/* 263 */         return null;
/*     */       }
/*     */ 
/* 268 */       String[] arrayOfString = new String[1];
/* 269 */       arrayOfString[0] = paramString2;
/* 270 */       return arrayOfString;
/*     */     }
/*     */ 
/* 273 */     int i = localVector.size();
/* 274 */     return (String[])localVector.toArray(new String[i]);
/*     */   }
/*     */ 
/*     */   protected void registerFontDir(String paramString)
/*     */   {
/* 292 */     if (FontUtilities.debugFonts()) {
/* 293 */       FontUtilities.getLogger().info("ParseFontDir " + paramString);
/*     */     }
/* 295 */     File localFile1 = new File(paramString + File.separator + "fonts.dir");
/* 296 */     FileReader localFileReader = null;
/*     */     try {
/* 298 */       if (localFile1.canRead()) {
/* 299 */         localFileReader = new FileReader(localFile1);
/* 300 */         BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
/* 301 */         StreamTokenizer localStreamTokenizer = new StreamTokenizer(localBufferedReader);
/* 302 */         localStreamTokenizer.eolIsSignificant(true);
/* 303 */         int i = localStreamTokenizer.nextToken();
/* 304 */         if (i == -2) {
/* 305 */           int j = (int)localStreamTokenizer.nval;
/* 306 */           i = localStreamTokenizer.nextToken();
/* 307 */           if (i == 10) {
/* 308 */             localStreamTokenizer.resetSyntax();
/* 309 */             localStreamTokenizer.wordChars(32, 127);
/* 310 */             localStreamTokenizer.wordChars(160, 255);
/* 311 */             localStreamTokenizer.whitespaceChars(0, 31);
/*     */ 
/* 313 */             for (int k = 0; k < j; k++) {
/* 314 */               i = localStreamTokenizer.nextToken();
/* 315 */               if (i == -1) {
/*     */                 break;
/*     */               }
/* 318 */               if (i != -3) {
/*     */                 break;
/*     */               }
/* 321 */               int m = localStreamTokenizer.sval.indexOf(' ');
/* 322 */               if (m <= 0)
/*     */               {
/* 333 */                 j++;
/* 334 */                 i = localStreamTokenizer.nextToken();
/* 335 */                 if (i != 10) {
/* 336 */                   break;
/*     */                 }
/*     */ 
/*     */               }
/* 341 */               else if (localStreamTokenizer.sval.charAt(0) == '!')
/*     */               {
/* 347 */                 j++;
/* 348 */                 i = localStreamTokenizer.nextToken();
/* 349 */                 if (i != 10)
/* 350 */                   break;
/*     */               }
/*     */               else
/*     */               {
/* 354 */                 String str1 = localStreamTokenizer.sval.substring(0, m);
/*     */ 
/* 360 */                 int n = str1.lastIndexOf(':');
/* 361 */                 if (n > 0) {
/* 362 */                   if (n + 1 < str1.length())
/*     */                   {
/* 365 */                     str1 = str1.substring(n + 1);
/*     */                   }
/*     */                 } else { String str2 = localStreamTokenizer.sval.substring(m + 1);
/* 368 */                   String str3 = specificFontIDForName(str2);
/* 369 */                   String str4 = (String)fontNameMap.get(str3);
/*     */ 
/* 371 */                   if (FontUtilities.debugFonts()) {
/* 372 */                     localObject1 = FontUtilities.getLogger();
/* 373 */                     ((PlatformLogger)localObject1).info("file=" + str1 + " xlfd=" + str2);
/*     */ 
/* 375 */                     ((PlatformLogger)localObject1).info("fontID=" + str3 + " sVal=" + str4);
/*     */                   }
/*     */ 
/* 378 */                   Object localObject1 = null;
/*     */                   try {
/* 380 */                     File localFile2 = new File(paramString, str1);
/*     */ 
/* 388 */                     if (xFontDirsMap == null) {
/* 389 */                       xFontDirsMap = new HashMap();
/*     */                     }
/* 391 */                     xFontDirsMap.put(str3, paramString);
/* 392 */                     localObject1 = localFile2.getCanonicalPath();
/*     */                   } catch (IOException localIOException4) {
/* 394 */                     localObject1 = paramString + File.separator + str1;
/*     */                   }
/* 396 */                   Vector localVector = (Vector)xlfdMap.get(localObject1);
/* 397 */                   if (FontUtilities.debugFonts()) {
/* 398 */                     FontUtilities.getLogger().info("fullPath=" + (String)localObject1 + " xVal=" + localVector);
/*     */                   }
/*     */ 
/* 402 */                   if (((localVector != null) && (localVector.contains(str2))) || ((str4 == null) || (!str4.startsWith("/"))))
/*     */                   {
/* 404 */                     if (FontUtilities.debugFonts()) {
/* 405 */                       FontUtilities.getLogger().info("Map fontID:" + str3 + "to file:" + (String)localObject1);
/*     */                     }
/*     */ 
/* 409 */                     fontNameMap.put(str3, localObject1);
/* 410 */                     if (localVector == null) {
/* 411 */                       localVector = new Vector();
/* 412 */                       xlfdMap.put(localObject1, localVector);
/*     */                     }
/* 414 */                     localVector.add(str2);
/*     */                   }
/*     */ 
/* 417 */                   i = localStreamTokenizer.nextToken();
/* 418 */                   if (i != 10)
/*     */                     break; }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 424 */         localFileReader.close();
/*     */       }
/*     */     } catch (IOException localIOException2) {
/*     */     } finally {
/* 428 */       if (localFileReader != null)
/*     */         try {
/* 430 */           localFileReader.close();
/*     */         }
/*     */         catch (IOException localIOException5)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void loadFonts() {
/* 439 */     super.loadFonts();
/*     */ 
/* 449 */     xFontDirsMap = null;
/* 450 */     xlfdMap = new HashMap(1);
/* 451 */     fontNameMap = new HashMap(1);
/*     */   }
/*     */ 
/*     */   private String getObliqueLucidaFontID(String paramString) {
/* 455 */     if ((paramString.startsWith("-lucidasans-medium-i-normal")) || (paramString.startsWith("-lucidasans-bold-i-normal")) || (paramString.startsWith("-lucidatypewriter-medium-i-normal")) || (paramString.startsWith("-lucidatypewriter-bold-i-normal")))
/*     */     {
/* 459 */       return paramString.substring(0, paramString.indexOf("-i-"));
/*     */     }
/* 461 */     return null;
/*     */   }
/*     */ 
/*     */   private static String getX11FontName(String paramString)
/*     */   {
/* 466 */     String str = paramString.replaceAll("%d", "*");
/* 467 */     if (NativeFont.fontExists(str)) {
/* 468 */       return str;
/*     */     }
/* 470 */     return null;
/*     */   }
/*     */ 
/*     */   private void initObliqueLucidaFontMap()
/*     */   {
/* 475 */     this.oblmap = new HashMap();
/* 476 */     this.oblmap.put("-lucidasans-medium", jreLibDirName + "/fonts/LucidaSansRegular.ttf");
/*     */ 
/* 478 */     this.oblmap.put("-lucidasans-bold", jreLibDirName + "/fonts/LucidaSansDemiBold.ttf");
/*     */ 
/* 480 */     this.oblmap.put("-lucidatypewriter-medium", jreLibDirName + "/fonts/LucidaTypewriterRegular.ttf");
/*     */ 
/* 482 */     this.oblmap.put("-lucidatypewriter-bold", jreLibDirName + "/fonts/LucidaTypewriterBold.ttf");
/*     */   }
/*     */ 
/*     */   private boolean isHeadless()
/*     */   {
/* 487 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */ 
/* 489 */     return GraphicsEnvironment.isHeadless();
/*     */   }
/*     */ 
/*     */   private String specificFontIDForName(String paramString)
/*     */   {
/* 494 */     int[] arrayOfInt = new int[14];
/* 495 */     int i = 1;
/* 496 */     int j = 1;
/*     */ 
/* 498 */     while ((j != -1) && (i < 14)) {
/* 499 */       j = paramString.indexOf('-', j);
/* 500 */       if (j != -1) {
/* 501 */         arrayOfInt[(i++)] = j;
/* 502 */         j++;
/*     */       }
/*     */     }
/*     */ 
/* 506 */     if (i != 14) {
/* 507 */       if (FontUtilities.debugFonts()) {
/* 508 */         FontUtilities.getLogger().severe("Font Configuration Font ID is malformed:" + paramString);
/*     */       }
/*     */ 
/* 511 */       return paramString;
/*     */     }
/*     */ 
/* 514 */     StringBuffer localStringBuffer = new StringBuffer(paramString.substring(arrayOfInt[1], arrayOfInt[5]));
/*     */ 
/* 517 */     localStringBuffer.append(paramString.substring(arrayOfInt[12]));
/* 518 */     String str = localStringBuffer.toString().toLowerCase(Locale.ENGLISH);
/* 519 */     return str;
/*     */   }
/*     */ 
/*     */   private String switchFontIDForName(String paramString)
/*     */   {
/* 524 */     int[] arrayOfInt = new int[14];
/* 525 */     int i = 1;
/* 526 */     int j = 1;
/*     */ 
/* 528 */     while ((j != -1) && (i < 14)) {
/* 529 */       j = paramString.indexOf('-', j);
/* 530 */       if (j != -1) {
/* 531 */         arrayOfInt[(i++)] = j;
/* 532 */         j++;
/*     */       }
/*     */     }
/*     */ 
/* 536 */     if (i != 14) {
/* 537 */       if (FontUtilities.debugFonts()) {
/* 538 */         FontUtilities.getLogger().severe("Font Configuration Font ID is malformed:" + paramString);
/*     */       }
/*     */ 
/* 541 */       return paramString;
/*     */     }
/*     */ 
/* 544 */     String str1 = paramString.substring(arrayOfInt[3] + 1, arrayOfInt[4]);
/*     */ 
/* 546 */     String str2 = paramString.substring(arrayOfInt[1] + 1, arrayOfInt[2]);
/*     */ 
/* 548 */     String str3 = paramString.substring(arrayOfInt[12] + 1, arrayOfInt[13]);
/*     */ 
/* 550 */     String str4 = paramString.substring(arrayOfInt[13] + 1);
/*     */ 
/* 552 */     if (str1.equals("i"))
/* 553 */       str1 = "o";
/* 554 */     else if (str1.equals("o")) {
/* 555 */       str1 = "i";
/*     */     }
/*     */ 
/* 558 */     if ((str2.equals("itc zapfdingbats")) && (str3.equals("sun")) && (str4.equals("fontspecific")))
/*     */     {
/* 561 */       str3 = "adobe";
/*     */     }
/* 563 */     StringBuffer localStringBuffer = new StringBuffer(paramString.substring(arrayOfInt[1], arrayOfInt[3] + 1));
/*     */ 
/* 566 */     localStringBuffer.append(str1);
/* 567 */     localStringBuffer.append(paramString.substring(arrayOfInt[4], arrayOfInt[5] + 1));
/*     */ 
/* 569 */     localStringBuffer.append(str3);
/* 570 */     localStringBuffer.append(paramString.substring(arrayOfInt[13]));
/* 571 */     String str5 = localStringBuffer.toString().toLowerCase(Locale.ENGLISH);
/* 572 */     return str5;
/*     */   }
/*     */ 
/*     */   public String getFileNameFromXLFD(String paramString)
/*     */   {
/* 579 */     String str1 = null;
/* 580 */     String str2 = specificFontIDForName(paramString);
/* 581 */     if (str2 != null) {
/* 582 */       str1 = (String)fontNameMap.get(str2);
/* 583 */       if (str1 == null) {
/* 584 */         str2 = switchFontIDForName(paramString);
/* 585 */         str1 = (String)fontNameMap.get(str2);
/*     */       }
/* 587 */       if (str1 == null) {
/* 588 */         str1 = getDefaultFontFile();
/*     */       }
/*     */     }
/* 591 */     return str1;
/*     */   }
/*     */ 
/*     */   protected void registerFontDirs(String paramString)
/*     */   {
/* 645 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
/*     */     try
/*     */     {
/* 648 */       while (localStringTokenizer.hasMoreTokens()) {
/* 649 */         String str = localStringTokenizer.nextToken();
/* 650 */         if ((str != null) && (!registeredDirs.containsKey(str))) {
/* 651 */           registeredDirs.put(str, null);
/* 652 */           registerFontDir(str);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoSuchElementException localNoSuchElementException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addFontToPlatformFontPath(String paramString)
/*     */   {
/* 685 */     getPlatformFontPathFromFontConfig();
/* 686 */     if (xFontDirsMap != null) {
/* 687 */       String str1 = specificFontIDForName(paramString);
/* 688 */       String str2 = (String)xFontDirsMap.get(str1);
/* 689 */       if (str2 != null)
/* 690 */         fontConfigDirs.add(str2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getPlatformFontPathFromFontConfig()
/*     */   {
/* 697 */     if (fontConfigDirs == null) {
/* 698 */       fontConfigDirs = getFontConfiguration().getAWTFontPathSet();
/* 699 */       if ((FontUtilities.debugFonts()) && (fontConfigDirs != null)) {
/* 700 */         String[] arrayOfString = (String[])fontConfigDirs.toArray(new String[0]);
/* 701 */         for (int i = 0; i < arrayOfString.length; i++)
/* 702 */           FontUtilities.getLogger().info("awtfontpath : " + arrayOfString[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void registerPlatformFontsUsedByFontConfiguration()
/*     */   {
/* 711 */     getPlatformFontPathFromFontConfig();
/* 712 */     if (fontConfigDirs == null) {
/* 713 */       return;
/*     */     }
/* 715 */     if (FontUtilities.isLinux) {
/* 716 */       fontConfigDirs.add(jreLibDirName + File.separator + "oblique-fonts");
/*     */     }
/* 718 */     fontdirs = (String[])fontConfigDirs.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   protected FontConfiguration createFontConfiguration()
/*     */   {
/* 741 */     MFontConfiguration localMFontConfiguration = new MFontConfiguration(this);
/* 742 */     if ((FontUtilities.isOpenSolaris) || ((FontUtilities.isLinux) && ((!localMFontConfiguration.foundOsSpecificFile()) || (!localMFontConfiguration.fontFilesArePresent()))) || ((FontUtilities.isSolaris) && (!localMFontConfiguration.fontFilesArePresent())))
/*     */     {
/* 747 */       FcFontConfiguration localFcFontConfiguration = new FcFontConfiguration(this);
/*     */ 
/* 749 */       if (localFcFontConfiguration.init()) {
/* 750 */         return localFcFontConfiguration;
/*     */       }
/*     */     }
/* 753 */     localMFontConfiguration.init();
/* 754 */     return localMFontConfiguration;
/*     */   }
/*     */ 
/*     */   public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2)
/*     */   {
/* 760 */     return new MFontConfiguration(this, paramBoolean1, paramBoolean2);
/*     */   }
/*     */ 
/*     */   public synchronized native String getFontPathNative(boolean paramBoolean);
/*     */ 
/*     */   protected synchronized String getFontPath(boolean paramBoolean)
/*     */   {
/* 767 */     isHeadless();
/* 768 */     return getFontPathNative(paramBoolean);
/*     */   }
/*     */ 
/*     */   public String[] getDefaultPlatformFont() {
/* 772 */     if (defaultPlatformFont != null) {
/* 773 */       return defaultPlatformFont;
/*     */     }
/* 775 */     String[] arrayOfString = new String[2];
/* 776 */     getFontConfigManager().initFontConfigFonts(false);
/* 777 */     FontConfigManager.FcCompFont[] arrayOfFcCompFont = getFontConfigManager().getFontConfigFonts();
/*     */ 
/* 779 */     for (int i = 0; i < arrayOfFcCompFont.length; i++) {
/* 780 */       if (("sans".equals(arrayOfFcCompFont[i].fcFamily)) && (0 == arrayOfFcCompFont[i].style))
/*     */       {
/* 782 */         arrayOfString[0] = arrayOfFcCompFont[i].firstFont.familyName;
/* 783 */         arrayOfString[1] = arrayOfFcCompFont[i].firstFont.fontFile;
/* 784 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 791 */     if (arrayOfString[0] == null) {
/* 792 */       if ((arrayOfFcCompFont.length > 0) && (arrayOfFcCompFont[0].firstFont.fontFile != null))
/*     */       {
/* 794 */         arrayOfString[0] = arrayOfFcCompFont[0].firstFont.familyName;
/* 795 */         arrayOfString[1] = arrayOfFcCompFont[0].firstFont.fontFile;
/*     */       } else {
/* 797 */         arrayOfString[0] = "Dialog";
/* 798 */         arrayOfString[1] = "/dialog.ttf";
/*     */       }
/*     */     }
/* 801 */     defaultPlatformFont = arrayOfString;
/* 802 */     return defaultPlatformFont;
/*     */   }
/*     */ 
/*     */   public synchronized FontConfigManager getFontConfigManager()
/*     */   {
/* 807 */     if (this.fcManager == null) {
/* 808 */       this.fcManager = new FontConfigManager();
/*     */     }
/*     */ 
/* 811 */     return this.fcManager;
/*     */   }
/*     */ 
/*     */   protected FontUIResource getFontConfigFUIR(String paramString, int paramInt1, int paramInt2)
/*     */   {
/* 817 */     CompositeFont localCompositeFont = getFontConfigManager().getFontConfigFont(paramString, paramInt1);
/*     */ 
/* 819 */     if (localCompositeFont == null) {
/* 820 */       return new FontUIResource(paramString, paramInt1, paramInt2);
/*     */     }
/*     */ 
/* 830 */     FontUIResource localFontUIResource = new FontUIResource(localCompositeFont.getFamilyName(null), paramInt1, paramInt2);
/*     */ 
/* 832 */     FontAccess.getFontAccess().setFont2D(localFontUIResource, localCompositeFont.handle);
/* 833 */     FontAccess.getFontAccess().setCreatedFont(localFontUIResource);
/* 834 */     return localFontUIResource;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11FontManager
 * JD-Core Version:    0.6.2
 */