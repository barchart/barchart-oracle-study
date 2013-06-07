/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.LinearGradientPaint;
/*     */ import java.awt.MultipleGradientPaint.ColorSpaceType;
/*     */ import java.awt.Paint;
/*     */ import java.awt.RadialGradientPaint;
/*     */ import java.awt.TexturePaint;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.NoninvertibleTransformException;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import sun.java2d.SunGraphics2D;
/*     */ import sun.java2d.SurfaceData;
/*     */ import sun.java2d.loops.CompositeType;
/*     */ import sun.java2d.pipe.BufferedPaints;
/*     */ 
/*     */ abstract class XRPaints
/*     */ {
/*     */   static XRCompositeManager xrCompMan;
/*  40 */   static final XRGradient xrGradient = new XRGradient(null);
/*  41 */   static final XRLinearGradient xrLinearGradient = new XRLinearGradient(null);
/*  42 */   static final XRRadialGradient xrRadialGradient = new XRRadialGradient(null);
/*  43 */   static final XRTexture xrTexture = new XRTexture(null);
/*     */ 
/*     */   public static void register(XRCompositeManager paramXRCompositeManager) {
/*  46 */     xrCompMan = paramXRCompositeManager;
/*     */   }
/*     */ 
/*     */   private static XRPaints getXRPaint(SunGraphics2D paramSunGraphics2D) {
/*  50 */     switch (paramSunGraphics2D.paintState) {
/*     */     case 2:
/*  52 */       return xrGradient;
/*     */     case 3:
/*  55 */       return xrLinearGradient;
/*     */     case 4:
/*  58 */       return xrRadialGradient;
/*     */     case 5:
/*  61 */       return xrTexture;
/*     */     }
/*     */ 
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   static boolean isValid(SunGraphics2D paramSunGraphics2D)
/*     */   {
/*  75 */     XRPaints localXRPaints = getXRPaint(paramSunGraphics2D);
/*  76 */     return (localXRPaints != null) && (localXRPaints.isPaintValid(paramSunGraphics2D));
/*     */   }
/*     */ 
/*     */   static void setPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint) {
/*  80 */     XRPaints localXRPaints = getXRPaint(paramSunGraphics2D);
/*  81 */     if (localXRPaints != null)
/*  82 */       localXRPaints.setXRPaint(paramSunGraphics2D, paramPaint);
/*     */   }
/*     */ 
/*     */   abstract boolean isPaintValid(SunGraphics2D paramSunGraphics2D);
/*     */ 
/*     */   abstract void setXRPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint);
/*     */ 
/*     */   public int getGradientLength(Point2D paramPoint2D1, Point2D paramPoint2D2)
/*     */   {
/* 136 */     double d1 = Math.max(paramPoint2D1.getX(), paramPoint2D2.getX()) - Math.min(paramPoint2D1.getX(), paramPoint2D2.getX());
/* 137 */     double d2 = Math.max(paramPoint2D1.getY(), paramPoint2D2.getY()) - Math.min(paramPoint2D1.getY(), paramPoint2D2.getY());
/* 138 */     return (int)Math.ceil(Math.sqrt(d1 * d1 + d2 * d2));
/*     */   }
/*     */ 
/*     */   public int[] convertToIntArgbPixels(Color[] paramArrayOfColor, boolean paramBoolean)
/*     */   {
/* 290 */     int[] arrayOfInt = new int[paramArrayOfColor.length];
/* 291 */     for (int i = 0; i < paramArrayOfColor.length; i++) {
/* 292 */       arrayOfInt[i] = colorToIntArgbPixel(paramArrayOfColor[i], paramBoolean);
/*     */     }
/* 294 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public int colorToIntArgbPixel(Color paramColor, boolean paramBoolean) {
/* 298 */     int i = paramColor.getRGB();
/*     */ 
/* 300 */     int j = i >>> 24;
/* 301 */     int k = i >> 16 & 0xFF;
/* 302 */     int m = i >> 8 & 0xFF;
/* 303 */     int n = i & 0xFF;
/* 304 */     if (paramBoolean) {
/* 305 */       k = BufferedPaints.convertSRGBtoLinearRGB(k);
/* 306 */       m = BufferedPaints.convertSRGBtoLinearRGB(m);
/* 307 */       n = BufferedPaints.convertSRGBtoLinearRGB(n);
/*     */     }
/*     */ 
/* 310 */     j = (int)(j * xrCompMan.getExtraAlpha());
/*     */ 
/* 312 */     return j << 24 | k << 16 | m << 8 | n;
/*     */   }
/*     */ 
/*     */   private static class XRGradient extends XRPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 105 */       return true;
/*     */     }
/*     */ 
/*     */     void setXRPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint) {
/* 109 */       GradientPaint localGradientPaint = (GradientPaint)paramPaint;
/*     */ 
/* 111 */       int[] arrayOfInt = convertToIntArgbPixels(new Color[] { localGradientPaint.getColor1(), localGradientPaint.getColor2() }, false);
/*     */ 
/* 113 */       float[] arrayOfFloat = new float[2];
/* 114 */       arrayOfFloat[0] = 0.0F;
/* 115 */       arrayOfFloat[1] = 1.0F;
/*     */ 
/* 117 */       Point2D localPoint2D1 = localGradientPaint.getPoint1();
/* 118 */       Point2D localPoint2D2 = localGradientPaint.getPoint2();
/*     */ 
/* 120 */       AffineTransform localAffineTransform = (AffineTransform)paramSunGraphics2D.transform.clone();
/*     */       try {
/* 122 */         localAffineTransform.invert();
/*     */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 124 */         localAffineTransform.setToIdentity();
/*     */       }
/*     */ 
/* 127 */       int i = localGradientPaint.isCyclic() ? 3 : 2;
/*     */ 
/* 129 */       XRBackend localXRBackend = xrCompMan.getBackend();
/* 130 */       int j = localXRBackend.createLinearGradient(localPoint2D1, localPoint2D2, arrayOfFloat, arrayOfInt, i, localAffineTransform);
/* 131 */       xrCompMan.setGradientPaint(new XRSurfaceData.XRInternalSurfaceData(localXRBackend, j, localAffineTransform));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class XRLinearGradient extends XRPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 145 */       return true;
/*     */     }
/*     */ 
/*     */     void setXRPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint)
/*     */     {
/* 150 */       LinearGradientPaint localLinearGradientPaint = (LinearGradientPaint)paramPaint;
/* 151 */       boolean bool = localLinearGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
/*     */ 
/* 153 */       Color[] arrayOfColor = localLinearGradientPaint.getColors();
/* 154 */       Point2D localPoint2D1 = localLinearGradientPaint.getStartPoint();
/* 155 */       Point2D localPoint2D2 = localLinearGradientPaint.getEndPoint();
/*     */ 
/* 158 */       AffineTransform localAffineTransform = localLinearGradientPaint.getTransform();
/* 159 */       localAffineTransform.preConcatenate(paramSunGraphics2D.transform);
/*     */ 
/* 161 */       int i = XRUtils.getRepeatForCycleMethod(localLinearGradientPaint.getCycleMethod());
/* 162 */       float[] arrayOfFloat = localLinearGradientPaint.getFractions();
/* 163 */       int[] arrayOfInt = convertToIntArgbPixels(arrayOfColor, bool);
/*     */       try
/*     */       {
/* 166 */         localAffineTransform.invert();
/*     */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 168 */         localNoninvertibleTransformException.printStackTrace();
/*     */       }
/*     */ 
/* 171 */       XRBackend localXRBackend = xrCompMan.getBackend();
/* 172 */       int j = localXRBackend.createLinearGradient(localPoint2D1, localPoint2D2, arrayOfFloat, arrayOfInt, i, localAffineTransform);
/* 173 */       xrCompMan.setGradientPaint(new XRSurfaceData.XRInternalSurfaceData(localXRBackend, j, localAffineTransform));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class XRRadialGradient extends XRPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 181 */       RadialGradientPaint localRadialGradientPaint = (RadialGradientPaint)paramSunGraphics2D.paint;
/* 182 */       return localRadialGradientPaint.getFocusPoint().equals(localRadialGradientPaint.getCenterPoint());
/*     */     }
/*     */ 
/*     */     void setXRPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint)
/*     */     {
/* 187 */       RadialGradientPaint localRadialGradientPaint = (RadialGradientPaint)paramPaint;
/* 188 */       boolean bool = localRadialGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
/* 189 */       Color[] arrayOfColor = localRadialGradientPaint.getColors();
/* 190 */       Point2D localPoint2D1 = localRadialGradientPaint.getCenterPoint();
/* 191 */       Point2D localPoint2D2 = localRadialGradientPaint.getFocusPoint();
/*     */ 
/* 193 */       int i = XRUtils.getRepeatForCycleMethod(localRadialGradientPaint.getCycleMethod());
/* 194 */       float[] arrayOfFloat = localRadialGradientPaint.getFractions();
/* 195 */       int[] arrayOfInt = convertToIntArgbPixels(arrayOfColor, bool);
/* 196 */       float f = localRadialGradientPaint.getRadius();
/*     */ 
/* 199 */       double d1 = localPoint2D1.getX();
/* 200 */       double d2 = localPoint2D1.getY();
/* 201 */       double d3 = localPoint2D2.getX();
/* 202 */       double d4 = localPoint2D2.getY();
/*     */ 
/* 204 */       AffineTransform localAffineTransform = localRadialGradientPaint.getTransform();
/* 205 */       localAffineTransform.preConcatenate(paramSunGraphics2D.transform);
/* 206 */       localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
/*     */ 
/* 211 */       localAffineTransform.translate(d1, d2);
/* 212 */       localAffineTransform.rotate(d3 - d1, d4 - d2);
/*     */       try
/*     */       {
/* 217 */         localAffineTransform.invert();
/*     */       } catch (Exception localException) {
/* 219 */         localAffineTransform.setToScale(0.0D, 0.0D);
/*     */       }
/* 221 */       localPoint2D2 = localAffineTransform.transform(localPoint2D2, localPoint2D2);
/*     */ 
/* 225 */       d3 = Math.min(localPoint2D2.getX(), 0.99D);
/*     */ 
/* 227 */       XRBackend localXRBackend = xrCompMan.getBackend();
/* 228 */       int j = localXRBackend.createRadialGradient(new Point2D.Float(0.0F, 0.0F), new Point2D.Float(0.0F, 0.0F), 0.0F, f, arrayOfFloat, arrayOfInt, i, localAffineTransform);
/* 229 */       xrCompMan.setGradientPaint(new XRSurfaceData.XRInternalSurfaceData(localXRBackend, j, localAffineTransform));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class XRTexture extends XRPaints
/*     */   {
/*     */     boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
/*     */     {
/* 237 */       TexturePaint localTexturePaint = (TexturePaint)paramSunGraphics2D.paint;
/* 238 */       BufferedImage localBufferedImage = localTexturePaint.getImage();
/* 239 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)paramSunGraphics2D.getDestSurface();
/*     */ 
/* 241 */       SurfaceData localSurfaceData = localXRSurfaceData.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/* 242 */       if (!(localSurfaceData instanceof XRSurfaceData))
/*     */       {
/* 246 */         localSurfaceData = localXRSurfaceData.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/* 247 */         if (!(localSurfaceData instanceof XRSurfaceData)) {
/* 248 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 252 */       return true;
/*     */     }
/*     */ 
/*     */     void setXRPaint(SunGraphics2D paramSunGraphics2D, Paint paramPaint)
/*     */     {
/* 257 */       TexturePaint localTexturePaint = (TexturePaint)paramPaint;
/*     */ 
/* 259 */       BufferedImage localBufferedImage = localTexturePaint.getImage();
/* 260 */       SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/* 261 */       SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
/*     */ 
/* 264 */       if (!(localSurfaceData2 instanceof XRSurfaceData)) {
/* 265 */         localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(localTexturePaint.getImage(), 0, CompositeType.SrcOver, null);
/* 266 */         if (!(localSurfaceData2 instanceof XRSurfaceData)) {
/* 267 */           throw new InternalError("Surface not cachable");
/*     */         }
/*     */       }
/*     */ 
/* 271 */       XRSurfaceData localXRSurfaceData = (XRSurfaceData)localSurfaceData2;
/*     */ 
/* 273 */       AffineTransform localAffineTransform = (AffineTransform)paramSunGraphics2D.transform.clone();
/* 274 */       Rectangle2D localRectangle2D = localTexturePaint.getAnchorRect();
/* 275 */       localAffineTransform.translate(localRectangle2D.getX(), localRectangle2D.getY());
/* 276 */       localAffineTransform.scale(localRectangle2D.getWidth() / localBufferedImage.getWidth(), localRectangle2D.getHeight() / localBufferedImage.getHeight());
/*     */       try
/*     */       {
/* 279 */         localAffineTransform.invert();
/*     */       } catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/* 281 */         localAffineTransform.setToIdentity();
/*     */       }
/*     */ 
/* 284 */       localXRSurfaceData.validateAsSource(localAffineTransform, 1, XRUtils.ATransOpToXRQuality(paramSunGraphics2D.interpolationType));
/* 285 */       xrCompMan.setTexturePaint((XRSurfaceData)localSurfaceData2);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRPaints
 * JD-Core Version:    0.6.2
 */