/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.MultipleGradientPaint.CycleMethod;
/*     */ import sun.java2d.loops.SurfaceType;
/*     */ 
/*     */ public class XRUtils
/*     */ {
/*     */   public static final int None = 0;
/*     */   public static final byte PictOpClear = 0;
/*     */   public static final byte PictOpSrc = 1;
/*     */   public static final byte PictOpDst = 2;
/*     */   public static final byte PictOpOver = 3;
/*     */   public static final byte PictOpOverReverse = 4;
/*     */   public static final byte PictOpIn = 5;
/*     */   public static final byte PictOpInReverse = 6;
/*     */   public static final byte PictOpOut = 7;
/*     */   public static final byte PictOpOutReverse = 8;
/*     */   public static final byte PictOpAtop = 9;
/*     */   public static final byte PictOpAtopReverse = 10;
/*     */   public static final byte PictOpXor = 11;
/*     */   public static final byte PictOpAdd = 12;
/*     */   public static final byte PictOpSaturate = 13;
/*     */   public static final int RepeatNone = 0;
/*     */   public static final int RepeatNormal = 1;
/*     */   public static final int RepeatPad = 2;
/*     */   public static final int RepeatReflect = 3;
/*     */   public static final int FAST = 0;
/*     */   public static final int GOOD = 1;
/*     */   public static final int BEST = 2;
/*  69 */   public static final byte[] FAST_NAME = "fast".getBytes();
/*  70 */   public static final byte[] GOOD_NAME = "good".getBytes();
/*  71 */   public static final byte[] BEST_NAME = "best".getBytes();
/*     */   public static final int PictStandardARGB32 = 0;
/*     */   public static final int PictStandardRGB24 = 1;
/*     */   public static final int PictStandardA8 = 2;
/*     */   public static final int PictStandardA4 = 3;
/*     */   public static final int PictStandardA1 = 4;
/*     */ 
/*     */   public static int ATransOpToXRQuality(int paramInt)
/*     */   {
/*  86 */     switch (paramInt) {
/*     */     case 1:
/*  88 */       return 0;
/*     */     case 2:
/*  91 */       return 1;
/*     */     case 3:
/*  94 */       return 2;
/*     */     }
/*     */ 
/*  97 */     return -1;
/*     */   }
/*     */ 
/*     */   public static byte[] ATransOpToXRQualityName(int paramInt)
/*     */   {
/* 106 */     switch (paramInt) {
/*     */     case 1:
/* 108 */       return FAST_NAME;
/*     */     case 2:
/* 111 */       return GOOD_NAME;
/*     */     case 3:
/* 114 */       return BEST_NAME;
/*     */     }
/*     */ 
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] getFilterName(int paramInt)
/*     */   {
/* 122 */     switch (paramInt) {
/*     */     case 0:
/* 124 */       return FAST_NAME;
/*     */     case 1:
/* 126 */       return GOOD_NAME;
/*     */     case 2:
/* 128 */       return BEST_NAME;
/*     */     }
/*     */ 
/* 131 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getPictureFormatForTransparency(int paramInt)
/*     */   {
/* 140 */     switch (paramInt) {
/*     */     case 1:
/* 142 */       return 1;
/*     */     case 2:
/*     */     case 3:
/* 146 */       return 0;
/*     */     }
/*     */ 
/* 149 */     return -1;
/*     */   }
/*     */ 
/*     */   public static SurfaceType getXRSurfaceTypeForTransparency(int paramInt)
/*     */   {
/* 154 */     if (paramInt == 1) {
/* 155 */       return SurfaceType.IntRgb;
/*     */     }
/* 157 */     return SurfaceType.IntArgbPre;
/*     */   }
/*     */ 
/*     */   public static int getRepeatForCycleMethod(MultipleGradientPaint.CycleMethod paramCycleMethod)
/*     */   {
/* 165 */     if (paramCycleMethod.equals(MultipleGradientPaint.CycleMethod.NO_CYCLE))
/* 166 */       return 2;
/* 167 */     if (paramCycleMethod.equals(MultipleGradientPaint.CycleMethod.REFLECT))
/* 168 */       return 3;
/* 169 */     if (paramCycleMethod.equals(MultipleGradientPaint.CycleMethod.REPEAT)) {
/* 170 */       return 1;
/*     */     }
/*     */ 
/* 173 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int XDoubleToFixed(double paramDouble)
/*     */   {
/* 180 */     return (int)(paramDouble * 65536.0D);
/*     */   }
/*     */ 
/*     */   public static double XFixedToDouble(int paramInt) {
/* 184 */     return paramInt / 65536.0D;
/*     */   }
/*     */ 
/*     */   public static int[] convertFloatsToFixed(float[] paramArrayOfFloat) {
/* 188 */     int[] arrayOfInt = new int[paramArrayOfFloat.length];
/*     */ 
/* 190 */     for (int i = 0; i < paramArrayOfFloat.length; i++) {
/* 191 */       arrayOfInt[i] = XDoubleToFixed(paramArrayOfFloat[i]);
/*     */     }
/*     */ 
/* 194 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public static long intToULong(int paramInt) {
/* 198 */     if (paramInt < 0) {
/* 199 */       return paramInt + 4294967296L;
/*     */     }
/*     */ 
/* 203 */     return paramInt;
/*     */   }
/*     */ 
/*     */   public static byte j2dAlphaCompToXR(int paramInt)
/*     */   {
/* 211 */     switch (paramInt) {
/*     */     case 1:
/* 213 */       return 0;
/*     */     case 2:
/* 216 */       return 1;
/*     */     case 9:
/* 219 */       return 2;
/*     */     case 3:
/* 222 */       return 3;
/*     */     case 4:
/* 225 */       return 4;
/*     */     case 5:
/* 228 */       return 5;
/*     */     case 6:
/* 231 */       return 6;
/*     */     case 7:
/* 234 */       return 7;
/*     */     case 8:
/* 237 */       return 8;
/*     */     case 10:
/* 240 */       return 9;
/*     */     case 11:
/* 243 */       return 10;
/*     */     case 12:
/* 246 */       return 11;
/*     */     }
/*     */ 
/* 249 */     throw new InternalError("No XRender equivalent available for requested java2d composition rule: " + paramInt);
/*     */   }
/*     */ 
/*     */   public static short clampToShort(int paramInt) {
/* 253 */     return (short)(paramInt < -32768 ? -32768 : paramInt > 32767 ? 32767 : paramInt);
/*     */   }
/*     */ 
/*     */   public static short clampToUShort(int paramInt)
/*     */   {
/* 259 */     return (short)(paramInt < 0 ? 0 : paramInt > 65535 ? 65535 : paramInt);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRUtils
 * JD-Core Version:    0.6.2
 */