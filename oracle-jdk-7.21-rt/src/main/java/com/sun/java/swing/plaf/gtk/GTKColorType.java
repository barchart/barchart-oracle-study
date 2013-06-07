/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import javax.swing.plaf.ColorUIResource;
/*     */ import javax.swing.plaf.synth.ColorType;
/*     */ 
/*     */ public class GTKColorType extends ColorType
/*     */ {
/*  38 */   public static final ColorType LIGHT = new GTKColorType("Light");
/*  39 */   public static final ColorType DARK = new GTKColorType("Dark");
/*  40 */   public static final ColorType MID = new GTKColorType("Mid");
/*  41 */   public static final ColorType BLACK = new GTKColorType("Black");
/*  42 */   public static final ColorType WHITE = new GTKColorType("White");
/*     */ 
/*  50 */   public static final int MAX_COUNT = WHITE.getID() + 1;
/*     */ 
/*  46 */   private static final float[] HLS_COLORS = new float[3];
/*  47 */   private static final Object HLS_COLOR_LOCK = new Object();
/*     */ 
/*     */   private static int hlsToRGB(float paramFloat1, float paramFloat2, float paramFloat3)
/*     */   {
/*  54 */     float f1 = paramFloat2 <= 0.5F ? paramFloat2 * (1.0F + paramFloat3) : paramFloat2 + paramFloat3 - paramFloat2 * paramFloat3;
/*  55 */     float f2 = 2.0F * paramFloat2 - f1;
/*     */     float f5;
/*     */     float f4;
/*     */     float f3;
/*  58 */     if (paramFloat3 == 0.0D) {
/*  59 */       if (paramFloat1 == 0.0D) {
/*  60 */         f3 = f4 = f5 = paramFloat2;
/*     */       }
/*     */       else
/*  63 */         f3 = f4 = f5 = 0.0F;
/*     */     }
/*     */     else
/*     */     {
/*  67 */       f3 = hlsValue(f2, f1, paramFloat1 + 120.0F);
/*  68 */       f4 = hlsValue(f2, f1, paramFloat1);
/*  69 */       f5 = hlsValue(f2, f1, paramFloat1 - 120.0F);
/*     */     }
/*  71 */     return (int)(f3 * 255.0F) << 16 | (int)(f4 * 255.0D) << 8 | (int)(f5 * 255.0F);
/*     */   }
/*     */ 
/*     */   private static float hlsValue(float paramFloat1, float paramFloat2, float paramFloat3)
/*     */   {
/*  76 */     if (paramFloat3 > 360.0F) {
/*  77 */       paramFloat3 -= 360.0F;
/*     */     }
/*  79 */     else if (paramFloat3 < 0.0F) {
/*  80 */       paramFloat3 += 360.0F;
/*     */     }
/*  82 */     if (paramFloat3 < 60.0F) {
/*  83 */       return paramFloat1 + (paramFloat2 - paramFloat1) * paramFloat3 / 60.0F;
/*     */     }
/*  85 */     if (paramFloat3 < 180.0F) {
/*  86 */       return paramFloat2;
/*     */     }
/*  88 */     if (paramFloat3 < 240.0F) {
/*  89 */       return paramFloat1 + (paramFloat2 - paramFloat1) * (240.0F - paramFloat3) / 60.0F;
/*     */     }
/*  91 */     return paramFloat1;
/*     */   }
/*     */ 
/*     */   private static float[] rgbToHLS(int paramInt, float[] paramArrayOfFloat)
/*     */   {
/*  98 */     float f1 = ((paramInt & 0xFF0000) >> 16) / 255.0F;
/*  99 */     float f2 = ((paramInt & 0xFF00) >> 8) / 255.0F;
/* 100 */     float f3 = (paramInt & 0xFF) / 255.0F;
/*     */ 
/* 103 */     float f4 = Math.max(Math.max(f1, f2), f3);
/* 104 */     float f5 = Math.min(Math.min(f1, f2), f3);
/* 105 */     float f6 = (f4 + f5) / 2.0F;
/* 106 */     float f7 = 0.0F;
/* 107 */     float f8 = 0.0F;
/*     */ 
/* 109 */     if (f4 != f5) {
/* 110 */       float f9 = f4 - f5;
/* 111 */       f7 = f6 <= 0.5F ? f9 / (f4 + f5) : f9 / (2.0F - f4 - f5);
/* 112 */       if (f1 == f4) {
/* 113 */         f8 = (f2 - f3) / f9;
/*     */       }
/* 115 */       else if (f2 == f4) {
/* 116 */         f8 = 2.0F + (f3 - f1) / f9;
/*     */       }
/*     */       else {
/* 119 */         f8 = 4.0F + (f1 - f2) / f9;
/*     */       }
/* 121 */       f8 *= 60.0F;
/* 122 */       if (f8 < 0.0F) {
/* 123 */         f8 += 360.0F;
/*     */       }
/*     */     }
/* 126 */     if (paramArrayOfFloat == null) {
/* 127 */       paramArrayOfFloat = new float[3];
/*     */     }
/* 129 */     paramArrayOfFloat[0] = f8;
/* 130 */     paramArrayOfFloat[1] = f6;
/* 131 */     paramArrayOfFloat[2] = f7;
/* 132 */     return paramArrayOfFloat;
/*     */   }
/*     */ 
/*     */   static Color adjustColor(Color paramColor, float paramFloat1, float paramFloat2, float paramFloat3)
/*     */   {
/* 152 */     synchronized (HLS_COLOR_LOCK) {
/* 153 */       float[] arrayOfFloat = rgbToHLS(paramColor.getRGB(), HLS_COLORS);
/* 154 */       f1 = arrayOfFloat[0];
/* 155 */       f2 = arrayOfFloat[1];
/* 156 */       f3 = arrayOfFloat[2];
/*     */     }
/* 158 */     float f1 = Math.min(360.0F, paramFloat1 * f1);
/* 159 */     float f2 = Math.min(1.0F, paramFloat2 * f2);
/* 160 */     float f3 = Math.min(1.0F, paramFloat3 * f3);
/* 161 */     return new ColorUIResource(hlsToRGB(f1, f2, f3));
/*     */   }
/*     */ 
/*     */   protected GTKColorType(String paramString) {
/* 165 */     super(paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.GTKColorType
 * JD-Core Version:    0.6.2
 */