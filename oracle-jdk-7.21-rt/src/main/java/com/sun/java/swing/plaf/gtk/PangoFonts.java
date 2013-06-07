/*     */ package com.sun.java.swing.plaf.gtk;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.swing.plaf.FontUIResource;
/*     */ import sun.font.FontUtilities;
/*     */ 
/*     */ class PangoFonts
/*     */ {
/*     */   public static final String CHARS_DIGITS = "0123456789";
/*  60 */   private static double fontScale = 1.0D;
/*     */ 
/*     */   static Font lookupFont(String paramString)
/*     */   {
/*  83 */     String str1 = "";
/*  84 */     int i = 0;
/*  85 */     int j = 10;
/*     */ 
/*  87 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/*     */ 
/*  89 */     while (localStringTokenizer.hasMoreTokens()) {
/*  90 */       String str2 = localStringTokenizer.nextToken();
/*     */ 
/*  92 */       if (str2.equalsIgnoreCase("italic")) {
/*  93 */         i |= 2;
/*  94 */       } else if (str2.equalsIgnoreCase("bold")) {
/*  95 */         i |= 1;
/*  96 */       } else if ("0123456789".indexOf(str2.charAt(0)) != -1) {
/*     */         try {
/*  98 */           j = Integer.parseInt(str2);
/*     */         } catch (NumberFormatException localNumberFormatException) {
/*     */         }
/*     */       } else {
/* 102 */         if (str1.length() > 0) {
/* 103 */           str1 = str1 + " ";
/*     */         }
/*     */ 
/* 106 */         str1 = str1 + str2;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 162 */     double d = j;
/* 163 */     int k = 96;
/* 164 */     Object localObject1 = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Xft/DPI");
/*     */ 
/* 166 */     if ((localObject1 instanceof Integer)) {
/* 167 */       k = ((Integer)localObject1).intValue() / 1024;
/* 168 */       if (k == -1) {
/* 169 */         k = 96;
/*     */       }
/* 171 */       if (k < 50) {
/* 172 */         k = 50;
/*     */       }
/*     */ 
/* 177 */       d = k * j / 72.0D;
/*     */     }
/*     */     else
/*     */     {
/* 183 */       d = j * fontScale;
/*     */     }
/*     */ 
/* 187 */     j = (int)(d + 0.5D);
/* 188 */     if (j < 1) {
/* 189 */       j = 1;
/*     */     }
/*     */ 
/* 192 */     String str3 = str1.toLowerCase();
/* 193 */     if (FontUtilities.mapFcName(str3) != null)
/*     */     {
/* 195 */       localObject2 = FontUtilities.getFontConfigFUIR(str3, i, j);
/* 196 */       localObject2 = ((Font)localObject2).deriveFont(i, (float)d);
/* 197 */       return new FontUIResource((Font)localObject2);
/*     */     }
/*     */ 
/* 200 */     Object localObject2 = new Font(str1, i, j);
/*     */ 
/* 202 */     localObject2 = ((Font)localObject2).deriveFont(i, (float)d);
/* 203 */     FontUIResource localFontUIResource = new FontUIResource((Font)localObject2);
/* 204 */     return FontUtilities.getCompositeFontUIResource(localFontUIResource);
/*     */   }
/*     */ 
/*     */   static int getFontSize(String paramString)
/*     */   {
/* 217 */     int i = 10;
/*     */ 
/* 219 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
/* 220 */     while (localStringTokenizer.hasMoreTokens()) {
/* 221 */       String str = localStringTokenizer.nextToken();
/*     */ 
/* 223 */       if ("0123456789".indexOf(str.charAt(0)) != -1)
/*     */         try {
/* 225 */           i = Integer.parseInt(str);
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException)
/*     */         {
/*     */         }
/*     */     }
/* 231 */     return i;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  61 */     GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */ 
/*  64 */     if (!GraphicsEnvironment.isHeadless()) {
/*  65 */       GraphicsConfiguration localGraphicsConfiguration = localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
/*     */ 
/*  67 */       AffineTransform localAffineTransform = localGraphicsConfiguration.getNormalizingTransform();
/*  68 */       fontScale = localAffineTransform.getScaleY();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.java.swing.plaf.gtk.PangoFonts
 * JD-Core Version:    0.6.2
 */