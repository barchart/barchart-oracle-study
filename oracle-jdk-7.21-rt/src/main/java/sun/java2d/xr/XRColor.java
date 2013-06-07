/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Color;
/*     */ 
/*     */ public class XRColor
/*     */ {
/*  37 */   public static final XRColor FULL_ALPHA = new XRColor(65535, 0, 0, 0);
/*  38 */   public static final XRColor NO_ALPHA = new XRColor(0, 0, 0, 0);
/*     */   int red;
/*     */   int green;
/*     */   int blue;
/*     */   int alpha;
/*     */ 
/*     */   public XRColor()
/*     */   {
/*  43 */     this.red = 0;
/*  44 */     this.green = 0;
/*  45 */     this.blue = 0;
/*  46 */     this.alpha = 0;
/*     */   }
/*     */ 
/*     */   public XRColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  50 */     this.alpha = paramInt1;
/*  51 */     this.red = paramInt2;
/*  52 */     this.green = paramInt3;
/*  53 */     this.blue = paramInt4;
/*     */   }
/*     */ 
/*     */   public XRColor(Color paramColor) {
/*     */   }
/*     */ 
/*     */   public void setColorValues(Color paramColor) {
/*  60 */     this.alpha = byteToXRColorValue(paramColor.getAlpha());
/*     */ 
/*  62 */     this.red = byteToXRColorValue((int)(paramColor.getRed() * paramColor.getAlpha() / 255.0D));
/*     */ 
/*  64 */     this.green = byteToXRColorValue((int)(paramColor.getGreen() * paramColor.getAlpha() / 255.0D));
/*     */ 
/*  66 */     this.blue = byteToXRColorValue((int)(paramColor.getBlue() * paramColor.getAlpha() / 255.0D));
/*     */   }
/*     */ 
/*     */   public static int[] ARGBPrePixelToXRColors(int[] paramArrayOfInt)
/*     */   {
/*  71 */     int[] arrayOfInt = new int[paramArrayOfInt.length * 4];
/*  72 */     XRColor localXRColor = new XRColor();
/*     */ 
/*  74 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/*  75 */       localXRColor.setColorValues(paramArrayOfInt[i], true);
/*  76 */       arrayOfInt[(i * 4 + 0)] = localXRColor.alpha;
/*  77 */       arrayOfInt[(i * 4 + 1)] = localXRColor.red;
/*  78 */       arrayOfInt[(i * 4 + 2)] = localXRColor.green;
/*  79 */       arrayOfInt[(i * 4 + 3)] = localXRColor.blue;
/*     */     }
/*     */ 
/*  82 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public void setColorValues(int paramInt, boolean paramBoolean) {
/*  86 */     long l = XRUtils.intToULong(paramInt);
/*  87 */     this.alpha = ((int)(((l & 0xFF000000) >> 16) + 255L));
/*  88 */     this.red = ((int)(((l & 0xFF0000) >> 8) + 255L));
/*  89 */     this.green = ((int)(((l & 0xFF00) >> 0) + 255L));
/*  90 */     this.blue = ((int)(((l & 0xFF) << 8) + 255L));
/*     */ 
/*  92 */     if (this.alpha == 255) {
/*  93 */       this.alpha = 0;
/*     */     }
/*     */ 
/*  96 */     if (!paramBoolean) {
/*  97 */       double d = XRUtils.XFixedToDouble(this.alpha);
/*  98 */       this.red = ((int)(this.red * d));
/*  99 */       this.green = ((int)(this.green * d));
/* 100 */       this.blue = ((int)(this.blue * d));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int byteToXRColorValue(int paramInt) {
/* 105 */     int i = 0;
/*     */ 
/* 107 */     if (paramInt != 0) {
/* 108 */       if (paramInt == 255)
/* 109 */         i = 65535;
/*     */       else {
/* 111 */         i = (paramInt << 8) + 255;
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return i;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 119 */     return "A:" + this.alpha + "  R:" + this.red + "  G:" + this.green + " B:" + this.blue;
/*     */   }
/*     */ 
/*     */   public void setAlpha(int paramInt) {
/* 123 */     this.alpha = paramInt;
/*     */   }
/*     */ 
/*     */   public int getAlpha() {
/* 127 */     return this.alpha;
/*     */   }
/*     */ 
/*     */   public int getRed() {
/* 131 */     return this.red;
/*     */   }
/*     */ 
/*     */   public int getGreen() {
/* 135 */     return this.green;
/*     */   }
/*     */ 
/*     */   public int getBlue() {
/* 139 */     return this.blue;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRColor
 * JD-Core Version:    0.6.2
 */