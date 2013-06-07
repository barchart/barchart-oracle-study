/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.security.AccessController;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ class MotifColorUtilities
/*     */ {
/*     */   static final float XmRED_LUMINOSITY = 0.3F;
/*     */   static final float XmGREEN_LUMINOSITY = 0.59F;
/*     */   static final float XmBLUE_LUMINOSITY = 0.11F;
/*     */   static final int XmINTENSITY_FACTOR = 75;
/*     */   static final int XmLIGHT_FACTOR = 0;
/*     */   static final int XmLUMINOSITY_FACTOR = 25;
/*     */   static final int XmMAX_SHORT = 65535;
/*     */   static final int XmCOLOR_PERCENTILE = 655;
/*     */   static final int XmDEFAULT_DARK_THRESHOLD = 20;
/*     */   static final int XmDEFAULT_LIGHT_THRESHOLD = 93;
/*     */   static final int XmDEFAULT_FOREGROUND_THRESHOLD = 70;
/*     */   static final int BLACK = -16777216;
/*     */   static final int WHITE = -1;
/*     */   static final int MOTIF_WINDOW_COLOR = -2105377;
/*     */   static final int DEFAULT_COLOR = -3881788;
/*     */   static final int XmCOLOR_LITE_THRESHOLD = 60915;
/*     */   static final int XmCOLOR_DARK_THRESHOLD = 13100;
/*     */   static final int XmFOREGROUND_THRESHOLD = 45850;
/*     */   static final int XmCOLOR_LITE_SEL_FACTOR = 15;
/*     */   static final int XmCOLOR_LITE_BS_FACTOR = 40;
/*     */   static final int XmCOLOR_LITE_TS_FACTOR = 20;
/*     */   static final int XmCOLOR_DARK_SEL_FACTOR = 15;
/*     */   static final int XmCOLOR_DARK_BS_FACTOR = 30;
/*     */   static final int XmCOLOR_DARK_TS_FACTOR = 50;
/*     */   static final int XmCOLOR_HI_SEL_FACTOR = 15;
/*     */   static final int XmCOLOR_HI_BS_FACTOR = 40;
/*     */   static final int XmCOLOR_HI_TS_FACTOR = 60;
/*     */   static final int XmCOLOR_LO_SEL_FACTOR = 15;
/*     */   static final int XmCOLOR_LO_BS_FACTOR = 60;
/*     */   static final int XmCOLOR_LO_TS_FACTOR = 50;
/*     */ 
/*     */   static int brightness(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 112 */     paramInt1 <<= 8;
/* 113 */     paramInt2 <<= 8;
/* 114 */     paramInt3 <<= 8;
/*     */ 
/* 117 */     float f2 = (paramInt1 + paramInt2 + paramInt3) / 3;
/*     */ 
/* 125 */     float f4 = (int)(0.3F * paramInt1 + 0.59F * paramInt2 + 0.11F * paramInt3);
/*     */ 
/* 129 */     float f5 = paramInt2 > paramInt3 ? paramInt2 : paramInt1 > paramInt2 ? paramInt3 : paramInt1 > paramInt3 ? paramInt1 : paramInt3;
/*     */ 
/* 133 */     float f6 = paramInt2 < paramInt3 ? paramInt2 : paramInt1 < paramInt2 ? paramInt3 : paramInt1 < paramInt3 ? paramInt1 : paramInt3;
/*     */ 
/* 137 */     float f3 = (f6 + f5) / 2.0F;
/*     */ 
/* 139 */     float f1 = (f2 * 75.0F + f3 * 0.0F + f4 * 25.0F) / 100.0F;
/*     */ 
/* 142 */     return Math.round(f1);
/*     */   }
/*     */ 
/*     */   static int calculateForegroundFromBackground(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 147 */     int i = -1;
/* 148 */     int j = brightness(paramInt1, paramInt2, paramInt3);
/*     */ 
/* 150 */     if (j > 45850)
/* 151 */       i = -16777216;
/*     */     else {
/* 153 */       i = -1;
/*     */     }
/* 155 */     return i;
/*     */   }
/*     */ 
/*     */   static int calculateTopShadowFromBackground(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 162 */     int i = paramInt1 << 8;
/* 163 */     int j = paramInt2 << 8;
/* 164 */     int k = paramInt3 << 8;
/*     */ 
/* 166 */     int m = brightness(paramInt1, paramInt2, paramInt3);
/*     */     float f1;
/*     */     float f3;
/*     */     float f4;
/*     */     float f5;
/* 172 */     if (m < 13100)
/*     */     {
/* 175 */       f1 = i;
/* 176 */       f1 += 50.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 178 */       f3 = f1;
/*     */ 
/* 180 */       f1 = j;
/* 181 */       f1 += 50.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 183 */       f4 = f1;
/*     */ 
/* 185 */       f1 = k;
/* 186 */       f1 += 50.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 188 */       f5 = f1;
/*     */     }
/* 190 */     else if (m > 60915)
/*     */     {
/* 193 */       f1 = i;
/* 194 */       f1 -= f1 * 20.0F / 100.0F;
/* 195 */       f3 = f1;
/*     */ 
/* 197 */       f1 = j;
/* 198 */       f1 -= f1 * 20.0F / 100.0F;
/* 199 */       f4 = f1;
/*     */ 
/* 201 */       f1 = k;
/* 202 */       f1 -= f1 * 20.0F / 100.0F;
/* 203 */       f5 = f1;
/*     */     }
/*     */     else
/*     */     {
/* 208 */       float f2 = 50 + m * 10 / 65535;
/*     */ 
/* 212 */       f1 = i;
/* 213 */       f1 += f2 * (65535.0F - f1) / 100.0F;
/* 214 */       f3 = f1;
/*     */ 
/* 216 */       f1 = j;
/* 217 */       f1 += f2 * (65535.0F - f1) / 100.0F;
/* 218 */       f4 = f1;
/*     */ 
/* 220 */       f1 = k;
/* 221 */       f1 += f2 * (65535.0F - f1) / 100.0F;
/* 222 */       f5 = f1;
/*     */     }
/*     */ 
/* 228 */     int n = (int)f3 >> 8;
/* 229 */     int i1 = (int)f4 >> 8;
/* 230 */     int i2 = (int)f5 >> 8;
/*     */ 
/* 232 */     int i3 = 0xFF000000 | n << 16 | i1 << 8 | i2;
/*     */ 
/* 234 */     return i3;
/*     */   }
/*     */ 
/*     */   static int calculateBottomShadowFromBackground(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 242 */     int i = paramInt1 << 8;
/* 243 */     int j = paramInt2 << 8;
/* 244 */     int k = paramInt3 << 8;
/*     */ 
/* 246 */     int m = brightness(paramInt1, paramInt2, paramInt3);
/*     */     float f1;
/*     */     float f3;
/*     */     float f4;
/*     */     float f5;
/* 252 */     if (m < 13100)
/*     */     {
/* 254 */       f1 = i;
/* 255 */       f1 += 30.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 257 */       f3 = f1;
/*     */ 
/* 259 */       f1 = j;
/* 260 */       f1 += 30.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 262 */       f4 = f1;
/*     */ 
/* 264 */       f1 = k;
/* 265 */       f1 += 30.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 267 */       f5 = f1;
/*     */     }
/* 270 */     else if (m > 60915)
/*     */     {
/* 272 */       f1 = i;
/* 273 */       f1 -= f1 * 40.0F / 100.0F;
/* 274 */       f3 = f1;
/*     */ 
/* 276 */       f1 = j;
/* 277 */       f1 -= f1 * 40.0F / 100.0F;
/* 278 */       f4 = f1;
/*     */ 
/* 280 */       f1 = k;
/* 281 */       f1 -= f1 * 40.0F / 100.0F;
/* 282 */       f5 = f1;
/*     */     }
/*     */     else
/*     */     {
/* 287 */       float f2 = 60 + m * -20 / 65535;
/*     */ 
/* 291 */       f1 = i;
/* 292 */       f1 -= f1 * f2 / 100.0F;
/* 293 */       f3 = f1;
/*     */ 
/* 295 */       f1 = j;
/* 296 */       f1 -= f1 * f2 / 100.0F;
/* 297 */       f4 = f1;
/*     */ 
/* 299 */       f1 = k;
/* 300 */       f1 -= f1 * f2 / 100.0F;
/* 301 */       f5 = f1;
/*     */     }
/*     */ 
/* 305 */     int n = (int)f3 >> 8;
/* 306 */     int i1 = (int)f4 >> 8;
/* 307 */     int i2 = (int)f5 >> 8;
/*     */ 
/* 309 */     int i3 = 0xFF000000 | n << 16 | i1 << 8 | i2;
/*     */ 
/* 311 */     return i3;
/*     */   }
/*     */ 
/*     */   static int calculateSelectFromBackground(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 318 */     int i = paramInt1 << 8;
/* 319 */     int j = paramInt2 << 8;
/* 320 */     int k = paramInt3 << 8;
/*     */ 
/* 322 */     int m = brightness(paramInt1, paramInt2, paramInt3);
/*     */     float f1;
/*     */     float f3;
/*     */     float f4;
/*     */     float f5;
/* 328 */     if (m < 13100)
/*     */     {
/* 330 */       f1 = i;
/* 331 */       f1 += 15.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 333 */       f3 = f1;
/*     */ 
/* 335 */       f1 = j;
/* 336 */       f1 += 15.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 338 */       f4 = f1;
/*     */ 
/* 340 */       f1 = k;
/* 341 */       f1 += 15.0F * (65535.0F - f1) / 100.0F;
/*     */ 
/* 343 */       f5 = f1;
/*     */     }
/* 346 */     else if (m > 60915)
/*     */     {
/* 348 */       f1 = i;
/* 349 */       f1 -= f1 * 15.0F / 100.0F;
/* 350 */       f3 = f1;
/*     */ 
/* 352 */       f1 = j;
/* 353 */       f1 -= f1 * 15.0F / 100.0F;
/* 354 */       f4 = f1;
/*     */ 
/* 356 */       f1 = k;
/* 357 */       f1 -= f1 * 15.0F / 100.0F;
/* 358 */       f5 = f1;
/*     */     }
/*     */     else
/*     */     {
/* 363 */       float f2 = 15 + m * 0 / 65535;
/*     */ 
/* 367 */       f1 = i;
/* 368 */       f1 -= f1 * f2 / 100.0F;
/* 369 */       f3 = f1;
/*     */ 
/* 371 */       f1 = j;
/* 372 */       f1 -= f1 * f2 / 100.0F;
/* 373 */       f4 = f1;
/*     */ 
/* 375 */       f1 = k;
/* 376 */       f1 -= f1 * f2 / 100.0F;
/* 377 */       f5 = f1;
/*     */     }
/*     */ 
/* 381 */     int n = (int)f3 >> 8;
/* 382 */     int i1 = (int)f4 >> 8;
/* 383 */     int i2 = (int)f5 >> 8;
/*     */ 
/* 385 */     int i3 = 0xFF000000 | n << 16 | i1 << 8 | i2;
/*     */ 
/* 387 */     return i3;
/*     */   }
/*     */ 
/*     */   static void loadSystemColorsForCDE(int[] paramArrayOfInt) throws Exception
/*     */   {
/* 392 */     XAtom localXAtom = XAtom.get("RESOURCE_MANAGER");
/*     */ 
/* 394 */     String str1 = localXAtom.getProperty(XToolkit.getDefaultRootWindow());
/*     */ 
/* 396 */     int i = str1.indexOf("ColorPalette:");
/* 397 */     int j = str1.length();
/* 398 */     while ((i < j) && (str1.charAt(i) != ':')) i++;
/* 399 */     i++;
/* 400 */     if (str1.charAt(i) == '\t') i++;
/*     */ 
/* 402 */     String str2 = str1.substring(i, str1.indexOf("\n", i));
/*     */ 
/* 408 */     String str3 = System.getProperty("user.home") + "/.dt/palettes/" + str2;
/*     */ 
/* 410 */     File localFile = new File(str3);
/* 411 */     if (!localFile.exists())
/*     */     {
/* 414 */       str3 = "/usr/dt/palettes/" + str2;
/* 415 */       localFile = new File(str3);
/* 416 */       if (!localFile.exists())
/*     */       {
/* 418 */         throw new FileNotFoundException("Could not open : " + str3);
/*     */       }
/*     */     }
/* 421 */     BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
/*     */ 
/* 423 */     int[] arrayOfInt = new int[8];
/*     */ 
/* 427 */     for (int i1 = 0; i1 < 8; i1++) {
/* 428 */       String str4 = localBufferedReader.readLine();
/* 429 */       String str5 = str4.substring(1, str4.length());
/* 430 */       k = Integer.valueOf(str5.substring(0, 4), 16).intValue() >> 8;
/* 431 */       m = Integer.valueOf(str5.substring(4, 8), 16).intValue() >> 8;
/* 432 */       n = Integer.valueOf(str5.substring(8, 12), 16).intValue() >> 8;
/* 433 */       arrayOfInt[i1] = (0xFF000000 | k << 16 | m << 8 | n);
/*     */     }
/*     */ 
/* 437 */     paramArrayOfInt[1] = arrayOfInt[0];
/* 438 */     paramArrayOfInt[3] = arrayOfInt[0];
/*     */ 
/* 440 */     paramArrayOfInt[4] = arrayOfInt[1];
/* 441 */     paramArrayOfInt[6] = arrayOfInt[1];
/*     */ 
/* 443 */     paramArrayOfInt[7] = arrayOfInt[1];
/*     */ 
/* 445 */     paramArrayOfInt[8] = arrayOfInt[1];
/* 446 */     paramArrayOfInt[10] = arrayOfInt[1];
/*     */ 
/* 448 */     paramArrayOfInt[12] = arrayOfInt[3];
/*     */ 
/* 450 */     paramArrayOfInt[23] = arrayOfInt[1];
/* 451 */     paramArrayOfInt[17] = arrayOfInt[1];
/*     */ 
/* 458 */     int k = (arrayOfInt[0] & 0xFF0000) >> 16;
/* 459 */     int m = (arrayOfInt[0] & 0xFF00) >> 8;
/* 460 */     int n = arrayOfInt[0] & 0xFF;
/*     */ 
/* 462 */     i1 = calculateForegroundFromBackground(k, m, n);
/*     */ 
/* 464 */     k = (arrayOfInt[1] & 0xFF0000) >> 16;
/* 465 */     m = (arrayOfInt[1] & 0xFF00) >> 8;
/* 466 */     n = arrayOfInt[1] & 0xFF;
/*     */ 
/* 468 */     int i2 = calculateForegroundFromBackground(k, m, n);
/*     */ 
/* 470 */     int i4 = calculateTopShadowFromBackground(k, m, n);
/* 471 */     int i5 = calculateBottomShadowFromBackground(k, m, n);
/*     */ 
/* 474 */     k = (arrayOfInt[3] & 0xFF0000) >> 16;
/* 475 */     m = (arrayOfInt[3] & 0xFF00) >> 8;
/* 476 */     n = arrayOfInt[3] & 0xFF;
/*     */ 
/* 478 */     int i3 = calculateForegroundFromBackground(k, m, n);
/*     */ 
/* 481 */     paramArrayOfInt[2] = i1;
/* 482 */     paramArrayOfInt[5] = i2;
/* 483 */     paramArrayOfInt[9] = i2;
/* 484 */     paramArrayOfInt[11] = i2;
/* 485 */     paramArrayOfInt[13] = i3;
/* 486 */     paramArrayOfInt[14] = -16777216;
/* 487 */     paramArrayOfInt[15] = -3881788;
/* 488 */     paramArrayOfInt[18] = i2;
/* 489 */     Color localColor = new Color(i4);
/* 490 */     paramArrayOfInt[19] = i4;
/* 491 */     paramArrayOfInt[20] = localColor.brighter().getRGB();
/*     */ 
/* 493 */     localColor = new Color(i5);
/* 494 */     paramArrayOfInt[21] = i5;
/* 495 */     paramArrayOfInt[22] = localColor.darker().getRGB();
/*     */   }
/*     */ 
/*     */   static void loadMotifDefaultColors(int[] paramArrayOfInt)
/*     */   {
/* 501 */     paramArrayOfInt[7] = -2105377;
/* 502 */     paramArrayOfInt[12] = -1;
/* 503 */     paramArrayOfInt[9] = -16777216;
/* 504 */     paramArrayOfInt[11] = -16777216;
/* 505 */     paramArrayOfInt[2] = -16777216;
/* 506 */     paramArrayOfInt[5] = -16777216;
/* 507 */     paramArrayOfInt[13] = -16777216;
/* 508 */     paramArrayOfInt[14] = -16777216;
/* 509 */     paramArrayOfInt[15] = -3881788;
/* 510 */     paramArrayOfInt[18] = -16777216;
/* 511 */     paramArrayOfInt[8] = -3881788;
/* 512 */     paramArrayOfInt[10] = -3881788;
/* 513 */     paramArrayOfInt[23] = -3881788;
/* 514 */     paramArrayOfInt[17] = -2105377;
/*     */ 
/* 516 */     int i = 196;
/* 517 */     int j = 196;
/* 518 */     int k = 196;
/*     */ 
/* 521 */     int m = calculateTopShadowFromBackground(i, j, k);
/* 522 */     int n = calculateBottomShadowFromBackground(i, j, k);
/*     */ 
/* 524 */     Color localColor = new Color(m);
/* 525 */     paramArrayOfInt[19] = m;
/* 526 */     paramArrayOfInt[20] = localColor.brighter().getRGB();
/*     */ 
/* 528 */     localColor = new Color(n);
/* 529 */     paramArrayOfInt[21] = n;
/* 530 */     paramArrayOfInt[22] = localColor.darker().getRGB();
/*     */   }
/*     */ 
/*     */   static void loadSystemColors(int[] paramArrayOfInt)
/*     */   {
/* 536 */     if ("Linux".equals(AccessController.doPrivileged(new GetPropertyAction("os.name")))) {
/* 537 */       loadMotifDefaultColors(paramArrayOfInt);
/*     */     }
/*     */     else
/*     */       try
/*     */       {
/* 542 */         loadSystemColorsForCDE(paramArrayOfInt);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 546 */         loadMotifDefaultColors(paramArrayOfInt);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.MotifColorUtilities
 * JD-Core Version:    0.6.2
 */