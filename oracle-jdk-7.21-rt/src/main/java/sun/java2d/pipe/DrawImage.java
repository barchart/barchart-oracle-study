/*      */ package sun.java2d.pipe;
/*      */ 
/*      */ import java.awt.AlphaComposite;
/*      */ import java.awt.Color;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.Image;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.image.AffineTransformOp;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.BufferedImageOp;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.VolatileImage;
/*      */ import sun.awt.image.BytePackedRaster;
/*      */ import sun.awt.image.ImageRepresentation;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.java2d.InvalidPipeException;
/*      */ import sun.java2d.SunGraphics2D;
/*      */ import sun.java2d.SurfaceData;
/*      */ import sun.java2d.loops.Blit;
/*      */ import sun.java2d.loops.BlitBg;
/*      */ import sun.java2d.loops.CompositeType;
/*      */ import sun.java2d.loops.MaskBlit;
/*      */ import sun.java2d.loops.ScaledBlit;
/*      */ import sun.java2d.loops.SurfaceType;
/*      */ import sun.java2d.loops.TransformHelper;
/*      */ 
/*      */ public class DrawImage
/*      */   implements DrawImagePipe
/*      */ {
/*      */   private static final double MAX_TX_ERROR = 0.0001D;
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor)
/*      */   {
/*   68 */     int i = paramImage.getWidth(null);
/*   69 */     int j = paramImage.getHeight(null);
/*   70 */     if (isSimpleTranslate(paramSunGraphics2D)) {
/*   71 */       return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, 0, 0, i, j);
/*      */     }
/*      */ 
/*   75 */     AffineTransform localAffineTransform = paramSunGraphics2D.transform;
/*   76 */     if ((paramInt1 | paramInt2) != 0) {
/*   77 */       localAffineTransform = new AffineTransform(localAffineTransform);
/*   78 */       localAffineTransform.translate(paramInt1, paramInt2);
/*      */     }
/*   80 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
/*      */ 
/*   82 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
/*      */   {
/*   89 */     if (isSimpleTranslate(paramSunGraphics2D)) {
/*   90 */       return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */ 
/*   94 */     scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor);
/*      */ 
/*   96 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
/*      */   {
/*  103 */     int i = paramImage.getWidth(null);
/*  104 */     int j = paramImage.getHeight(null);
/*      */ 
/*  109 */     if ((paramInt3 > 0) && (paramInt4 > 0) && (isSimpleTranslate(paramSunGraphics2D))) {
/*  110 */       double d1 = paramInt1 + paramSunGraphics2D.transX;
/*  111 */       double d2 = paramInt2 + paramSunGraphics2D.transY;
/*  112 */       double d3 = d1 + paramInt3;
/*  113 */       double d4 = d2 + paramInt4;
/*  114 */       if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, 0, 0, i, j, d1, d2, d3, d4))
/*      */       {
/*  118 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  122 */     AffineTransform localAffineTransform = paramSunGraphics2D.transform;
/*  123 */     if (((paramInt1 | paramInt2) != 0) || (paramInt3 != i) || (paramInt4 != j)) {
/*  124 */       localAffineTransform = new AffineTransform(localAffineTransform);
/*  125 */       localAffineTransform.translate(paramInt1, paramInt2);
/*  126 */       localAffineTransform.scale(paramInt3 / i, paramInt4 / j);
/*      */     }
/*  128 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
/*      */ 
/*  130 */     return true;
/*      */   }
/*      */ 
/*      */   protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, AffineTransform paramAffineTransform, int paramInt3)
/*      */   {
/*  144 */     int i = paramAffineTransform.getType();
/*  145 */     int j = paramImage.getWidth(null);
/*  146 */     int k = paramImage.getHeight(null);
/*      */     int m;
/*  149 */     if ((paramSunGraphics2D.transformState <= 2) && ((i == 0) || (i == 1)))
/*      */     {
/*  156 */       double d1 = paramAffineTransform.getTranslateX();
/*  157 */       double d2 = paramAffineTransform.getTranslateY();
/*  158 */       d1 += paramSunGraphics2D.transform.getTranslateX();
/*  159 */       d2 += paramSunGraphics2D.transform.getTranslateY();
/*  160 */       int n = (int)Math.floor(d1 + 0.5D);
/*  161 */       int i1 = (int)Math.floor(d2 + 0.5D);
/*  162 */       if ((paramInt3 == 1) || ((closeToInteger(n, d1)) && (closeToInteger(i1, d2))))
/*      */       {
/*  165 */         renderImageCopy(paramSunGraphics2D, paramImage, null, paramInt1 + n, paramInt2 + i1, 0, 0, j, k);
/*  166 */         return;
/*      */       }
/*  168 */       m = 0;
/*  169 */     } else if ((paramSunGraphics2D.transformState <= 3) && ((i & 0x78) == 0))
/*      */     {
/*  181 */       localObject = new double[] { 0.0D, 0.0D, j, k };
/*      */ 
/*  184 */       paramAffineTransform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
/*  185 */       localObject[0] += paramInt1;
/*  186 */       localObject[1] += paramInt2;
/*  187 */       localObject[2] += paramInt1;
/*  188 */       localObject[3] += paramInt2;
/*  189 */       paramSunGraphics2D.transform.transform((double[])localObject, 0, (double[])localObject, 0, 2);
/*      */ 
/*  191 */       if (tryCopyOrScale(paramSunGraphics2D, paramImage, 0, 0, j, k, null, paramInt3, (double[])localObject))
/*      */       {
/*  194 */         return;
/*      */       }
/*  196 */       m = 0;
/*      */     } else {
/*  198 */       m = 1;
/*      */     }
/*      */ 
/*  202 */     Object localObject = new AffineTransform(paramSunGraphics2D.transform);
/*  203 */     ((AffineTransform)localObject).translate(paramInt1, paramInt2);
/*  204 */     ((AffineTransform)localObject).concatenate(paramAffineTransform);
/*      */ 
/*  209 */     if (m != 0)
/*      */     {
/*  214 */       transformImage(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
/*      */     }
/*  216 */     else renderImageXform(paramSunGraphics2D, paramImage, (AffineTransform)localObject, paramInt3, 0, 0, j, k, null);
/*      */   }
/*      */ 
/*      */   protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
/*      */   {
/*  248 */     double[] arrayOfDouble = new double[6];
/*      */ 
/*  251 */     arrayOfDouble[2] = (paramInt4 - paramInt2);
/*      */     double tmp28_27 = (paramInt5 - paramInt3); arrayOfDouble[5] = tmp28_27; arrayOfDouble[3] = tmp28_27;
/*  253 */     paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 3);
/*      */ 
/*  260 */     if ((Math.abs(arrayOfDouble[0] - arrayOfDouble[4]) < 0.0001D) && (Math.abs(arrayOfDouble[3] - arrayOfDouble[5]) < 0.0001D) && (tryCopyOrScale(paramSunGraphics2D, paramImage, paramInt2, paramInt3, paramInt4, paramInt5, paramColor, paramInt1, arrayOfDouble)))
/*      */     {
/*  265 */       return;
/*      */     }
/*      */ 
/*  268 */     renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
/*      */   }
/*      */ 
/*      */   protected boolean tryCopyOrScale(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, int paramInt5, double[] paramArrayOfDouble)
/*      */   {
/*  285 */     double d1 = paramArrayOfDouble[0];
/*  286 */     double d2 = paramArrayOfDouble[1];
/*  287 */     double d3 = paramArrayOfDouble[2] - d1;
/*  288 */     double d4 = paramArrayOfDouble[3] - d2;
/*      */ 
/*  290 */     if ((closeToInteger(paramInt3 - paramInt1, d3)) && (closeToInteger(paramInt4 - paramInt2, d4)))
/*      */     {
/*  293 */       int i = (int)Math.floor(d1 + 0.5D);
/*  294 */       int j = (int)Math.floor(d2 + 0.5D);
/*  295 */       if ((paramInt5 == 1) || ((closeToInteger(i, d1)) && (closeToInteger(j, d2))))
/*      */       {
/*  298 */         renderImageCopy(paramSunGraphics2D, paramImage, paramColor, i, j, paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
/*      */ 
/*  301 */         return true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  306 */     if ((d3 > 0.0D) && (d4 > 0.0D) && 
/*  307 */       (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramInt5, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfDouble[0], paramArrayOfDouble[1], paramArrayOfDouble[2], paramArrayOfDouble[3])))
/*      */     {
/*  311 */       return true;
/*      */     }
/*      */ 
/*  314 */     return false;
/*      */   }
/*      */ 
/*      */   BufferedImage makeBufferedImage(Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  326 */     BufferedImage localBufferedImage = new BufferedImage(paramInt4 - paramInt2, paramInt5 - paramInt3, paramInt1);
/*  327 */     Graphics2D localGraphics2D = localBufferedImage.createGraphics();
/*  328 */     localGraphics2D.setComposite(AlphaComposite.Src);
/*  329 */     if (paramColor != null) {
/*  330 */       localGraphics2D.setColor(paramColor);
/*  331 */       localGraphics2D.fillRect(0, 0, paramInt4 - paramInt2, paramInt5 - paramInt3);
/*  332 */       localGraphics2D.setComposite(AlphaComposite.SrcOver);
/*      */     }
/*  334 */     localGraphics2D.drawImage(paramImage, -paramInt2, -paramInt3, null);
/*  335 */     localGraphics2D.dispose();
/*  336 */     return localBufferedImage;
/*      */   }
/*      */ 
/*      */   protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
/*      */   {
/*  344 */     Region localRegion = paramSunGraphics2D.getCompClip();
/*  345 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  346 */     SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */ 
/*  351 */     if (localSurfaceData2 == null) {
/*  352 */       paramImage = getBufferedImage(paramImage);
/*  353 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */ 
/*  357 */       if (localSurfaceData2 == null)
/*      */       {
/*  359 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  363 */     if (isBgOperation(localSurfaceData2, paramColor))
/*      */     {
/*  367 */       paramImage = makeBufferedImage(paramImage, paramColor, 1, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */ 
/*  370 */       paramInt4 -= paramInt2;
/*  371 */       paramInt5 -= paramInt3;
/*  372 */       paramInt2 = paramInt3 = 0;
/*      */ 
/*  374 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
/*      */     }
/*      */ 
/*  380 */     SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  381 */     TransformHelper localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
/*      */ 
/*  383 */     if (localTransformHelper == null)
/*      */     {
/*  391 */       int i = localSurfaceData2.getTransparency() == 1 ? 1 : 2;
/*      */ 
/*  394 */       paramImage = makeBufferedImage(paramImage, null, i, paramInt2, paramInt3, paramInt4, paramInt5);
/*      */ 
/*  396 */       paramInt4 -= paramInt2;
/*  397 */       paramInt5 -= paramInt3;
/*  398 */       paramInt2 = paramInt3 = 0;
/*      */ 
/*  400 */       localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, null);
/*      */ 
/*  404 */       localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  405 */       localTransformHelper = TransformHelper.getFromCache(localSurfaceType1);
/*      */     }
/*      */ 
/*      */     AffineTransform localAffineTransform;
/*      */     try
/*      */     {
/*  411 */       localAffineTransform = paramAffineTransform.createInverse();
/*      */     }
/*      */     catch (NoninvertibleTransformException localNoninvertibleTransformException) {
/*  414 */       return;
/*      */     }
/*      */ 
/*  426 */     double[] arrayOfDouble = new double[8];
/*      */     double tmp260_259 = (paramInt4 - paramInt2); arrayOfDouble[6] = tmp260_259; arrayOfDouble[2] = tmp260_259;
/*      */     double tmp276_275 = (paramInt5 - paramInt3); arrayOfDouble[7] = tmp276_275; arrayOfDouble[5] = tmp276_275;
/*  432 */     paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
/*      */     double d3;
/*  434 */     double d1 = d3 = arrayOfDouble[0];
/*      */     double d4;
/*  435 */     double d2 = d4 = arrayOfDouble[1];
/*  436 */     for (int j = 2; j < arrayOfDouble.length; j += 2) {
/*  437 */       double d5 = arrayOfDouble[j];
/*  438 */       if (d1 > d5) d1 = d5;
/*  439 */       else if (d3 < d5) d3 = d5;
/*  440 */       d5 = arrayOfDouble[(j + 1)];
/*  441 */       if (d2 > d5) d2 = d5;
/*  442 */       else if (d4 < d5) d4 = d5;
/*      */     }
/*  444 */     j = (int)Math.floor(d1);
/*  445 */     int k = (int)Math.floor(d2);
/*  446 */     int m = (int)Math.ceil(d3);
/*  447 */     int n = (int)Math.ceil(d4);
/*      */ 
/*  449 */     SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*      */     MaskBlit localMaskBlit1;
/*      */     Blit localBlit;
/*  452 */     if (paramSunGraphics2D.compositeState <= 1)
/*      */     {
/*  456 */       localMaskBlit1 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, paramSunGraphics2D.imageComp, localSurfaceType2);
/*      */ 
/*  466 */       if (localMaskBlit1.getNativePrim() != 0L)
/*      */       {
/*  468 */         localTransformHelper.Transform(localMaskBlit1, localSurfaceData2, localSurfaceData1, paramSunGraphics2D.composite, localRegion, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, j, k, m, n, null, 0, 0);
/*      */ 
/*  474 */         return;
/*      */       }
/*  476 */       localBlit = null;
/*      */     }
/*      */     else
/*      */     {
/*  481 */       localMaskBlit1 = null;
/*  482 */       localBlit = Blit.getFromCache(SurfaceType.IntArgbPre, paramSunGraphics2D.imageComp, localSurfaceType2);
/*      */     }
/*      */ 
/*  489 */     BufferedImage localBufferedImage = new BufferedImage(m - j, n - k, 2);
/*      */ 
/*  491 */     SurfaceData localSurfaceData3 = SurfaceData.getPrimarySurfaceData(localBufferedImage);
/*  492 */     SurfaceType localSurfaceType3 = localSurfaceData3.getSurfaceType();
/*  493 */     MaskBlit localMaskBlit2 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, localSurfaceType3);
/*      */ 
/*  511 */     int[] arrayOfInt = new int[(n - k) * 2 + 2];
/*      */ 
/*  515 */     localTransformHelper.Transform(localMaskBlit2, localSurfaceData2, localSurfaceData3, AlphaComposite.Src, null, localAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, m - j, n - k, arrayOfInt, j, k);
/*      */ 
/*  526 */     int i1 = 2;
/*  527 */     for (int i2 = arrayOfInt[0]; i2 < arrayOfInt[1]; i2++) {
/*  528 */       int i3 = arrayOfInt[(i1++)];
/*  529 */       int i4 = arrayOfInt[(i1++)];
/*  530 */       if (i3 < i4)
/*      */       {
/*  533 */         if (localMaskBlit1 != null) {
/*  534 */           localMaskBlit1.MaskBlit(localSurfaceData3, localSurfaceData1, paramSunGraphics2D.composite, localRegion, i3, i2, j + i3, k + i2, i4 - i3, 1, null, 0, 0);
/*      */         }
/*      */         else
/*      */         {
/*  541 */           localBlit.Blit(localSurfaceData3, localSurfaceData1, paramSunGraphics2D.composite, localRegion, i3, i2, j + i3, k + i2, i4 - i3, 1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean renderImageCopy(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*  558 */     Region localRegion = paramSunGraphics2D.getCompClip();
/*  559 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*      */ 
/*  561 */     int i = 0;
/*      */     while (true)
/*      */     {
/*  566 */       SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 0, paramSunGraphics2D.imageComp, paramColor);
/*      */ 
/*  571 */       if (localSurfaceData2 == null) {
/*  572 */         return false;
/*      */       }
/*      */       try
/*      */       {
/*  576 */         SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  577 */         SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*  578 */         blitSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt5, paramInt6, paramColor);
/*      */ 
/*  581 */         return true;
/*      */       } catch (NullPointerException localNullPointerException) {
/*  583 */         if ((!SurfaceData.isNull(localSurfaceData1)) && (!SurfaceData.isNull(localSurfaceData2)))
/*      */         {
/*  587 */           throw localNullPointerException;
/*      */         }
/*  589 */         return false;
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException)
/*      */       {
/*  595 */         i++;
/*  596 */         localRegion = paramSunGraphics2D.getCompClip();
/*  597 */         localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  598 */         if ((SurfaceData.isNull(localSurfaceData1)) || (SurfaceData.isNull(localSurfaceData2)) || (i > 1))
/*      */         {
/*  601 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean renderImageScale(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*      */   {
/*  617 */     if (paramInt1 != 1) {
/*  618 */       return false;
/*      */     }
/*      */ 
/*  621 */     Region localRegion = paramSunGraphics2D.getCompClip();
/*  622 */     SurfaceData localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*      */ 
/*  624 */     int i = 0;
/*      */     while (true)
/*      */     {
/*  629 */       SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 3, paramSunGraphics2D.imageComp, paramColor);
/*      */ 
/*  635 */       if ((localSurfaceData2 == null) || (isBgOperation(localSurfaceData2, paramColor))) {
/*  636 */         return false;
/*      */       }
/*      */       try
/*      */       {
/*  640 */         SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
/*  641 */         SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
/*  642 */         return scaleSurfaceData(paramSunGraphics2D, localRegion, localSurfaceData2, localSurfaceData1, localSurfaceType1, localSurfaceType2, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*      */       }
/*      */       catch (NullPointerException localNullPointerException)
/*      */       {
/*  647 */         if (!SurfaceData.isNull(localSurfaceData1))
/*      */         {
/*  649 */           throw localNullPointerException;
/*      */         }
/*  651 */         return false;
/*      */       }
/*      */       catch (InvalidPipeException localInvalidPipeException)
/*      */       {
/*  657 */         i++;
/*  658 */         localRegion = paramSunGraphics2D.getCompClip();
/*  659 */         localSurfaceData1 = paramSunGraphics2D.surfaceData;
/*  660 */         if ((SurfaceData.isNull(localSurfaceData1)) || (SurfaceData.isNull(localSurfaceData2)) || (i > 1))
/*      */         {
/*  663 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor)
/*      */   {
/*  676 */     int i4 = 0;
/*  677 */     int i5 = 0;
/*  678 */     int i6 = 0;
/*  679 */     int i7 = 0;
/*      */     int i;
/*      */     int n;
/*  681 */     if (paramInt7 > paramInt5) {
/*  682 */       i = paramInt7 - paramInt5;
/*  683 */       n = paramInt5;
/*      */     } else {
/*  685 */       i4 = 1;
/*  686 */       i = paramInt5 - paramInt7;
/*  687 */       n = paramInt7;
/*      */     }
/*      */     int j;
/*      */     int i1;
/*  689 */     if (paramInt8 > paramInt6) {
/*  690 */       j = paramInt8 - paramInt6;
/*  691 */       i1 = paramInt6;
/*      */     } else {
/*  693 */       i5 = 1;
/*  694 */       j = paramInt6 - paramInt8;
/*  695 */       i1 = paramInt8;
/*      */     }
/*      */     int k;
/*      */     int i2;
/*  697 */     if (paramInt3 > paramInt1) {
/*  698 */       k = paramInt3 - paramInt1;
/*  699 */       i2 = paramInt1;
/*      */     } else {
/*  701 */       k = paramInt1 - paramInt3;
/*  702 */       i6 = 1;
/*  703 */       i2 = paramInt3;
/*      */     }
/*      */     int m;
/*      */     int i3;
/*  705 */     if (paramInt4 > paramInt2) {
/*  706 */       m = paramInt4 - paramInt2;
/*  707 */       i3 = paramInt2;
/*      */     } else {
/*  709 */       m = paramInt2 - paramInt4;
/*  710 */       i7 = 1;
/*  711 */       i3 = paramInt4;
/*      */     }
/*  713 */     if ((i <= 0) || (j <= 0)) {
/*  714 */       return true;
/*      */     }
/*      */ 
/*  717 */     if ((i4 == i6) && (i5 == i7) && (isSimpleTranslate(paramSunGraphics2D)))
/*      */     {
/*  721 */       double d1 = i2 + paramSunGraphics2D.transX;
/*  722 */       double d3 = i3 + paramSunGraphics2D.transY;
/*  723 */       double d5 = d1 + k;
/*  724 */       double d6 = d3 + m;
/*  725 */       if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, n, i1, n + i, i1 + j, d1, d3, d5, d6))
/*      */       {
/*  729 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  733 */     AffineTransform localAffineTransform = new AffineTransform(paramSunGraphics2D.transform);
/*  734 */     localAffineTransform.translate(paramInt1, paramInt2);
/*  735 */     double d2 = (paramInt3 - paramInt1) / (paramInt7 - paramInt5);
/*  736 */     double d4 = (paramInt4 - paramInt2) / (paramInt8 - paramInt6);
/*  737 */     localAffineTransform.scale(d2, d4);
/*  738 */     localAffineTransform.translate(n - paramInt5, i1 - paramInt6);
/*      */ 
/*  740 */     int i8 = paramImage.getWidth(null);
/*  741 */     int i9 = paramImage.getHeight(null);
/*  742 */     i += n;
/*  743 */     j += i1;
/*      */ 
/*  745 */     if (i > i8) {
/*  746 */       i = i8;
/*      */     }
/*  748 */     if (j > i9) {
/*  749 */       j = i9;
/*      */     }
/*  751 */     if (n < 0) {
/*  752 */       localAffineTransform.translate(-n, 0.0D);
/*  753 */       n = 0;
/*      */     }
/*  755 */     if (i1 < 0) {
/*  756 */       localAffineTransform.translate(0.0D, -i1);
/*  757 */       i1 = 0;
/*      */     }
/*  759 */     if ((n >= i) || (i1 >= j)) {
/*  760 */       return true;
/*      */     }
/*      */ 
/*  770 */     transformImage(paramSunGraphics2D, paramImage, localAffineTransform, paramSunGraphics2D.interpolationType, n, i1, i, j, paramColor);
/*      */ 
/*  772 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean closeToInteger(int paramInt, double paramDouble)
/*      */   {
/*  798 */     return Math.abs(paramDouble - paramInt) < 0.0001D;
/*      */   }
/*      */ 
/*      */   public static boolean isSimpleTranslate(SunGraphics2D paramSunGraphics2D) {
/*  802 */     int i = paramSunGraphics2D.transformState;
/*  803 */     if (i <= 1)
/*      */     {
/*  805 */       return true;
/*      */     }
/*  807 */     if (i >= 3)
/*      */     {
/*  809 */       return false;
/*      */     }
/*      */ 
/*  812 */     if (paramSunGraphics2D.interpolationType == 1) {
/*  813 */       return true;
/*      */     }
/*  815 */     return false;
/*      */   }
/*      */ 
/*      */   protected static boolean isBgOperation(SurfaceData paramSurfaceData, Color paramColor)
/*      */   {
/*  821 */     return (paramSurfaceData == null) || ((paramColor != null) && (paramSurfaceData.getTransparency() != 1));
/*      */   }
/*      */ 
/*      */   protected BufferedImage getBufferedImage(Image paramImage)
/*      */   {
/*  827 */     if ((paramImage instanceof BufferedImage)) {
/*  828 */       return (BufferedImage)paramImage;
/*      */     }
/*      */ 
/*  831 */     return ((VolatileImage)paramImage).getSnapshot();
/*      */   }
/*      */ 
/*      */   private ColorModel getTransformColorModel(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, AffineTransform paramAffineTransform)
/*      */   {
/*  841 */     ColorModel localColorModel = paramBufferedImage.getColorModel();
/*  842 */     Object localObject1 = localColorModel;
/*      */ 
/*  844 */     if (paramAffineTransform.isIdentity()) {
/*  845 */       return localObject1;
/*      */     }
/*  847 */     int i = paramAffineTransform.getType();
/*  848 */     int j = (i & (0x18 | 0x20)) != 0 ? 1 : 0;
/*      */     Object localObject2;
/*  850 */     if ((j == 0) && (i != 1) && (i != 0))
/*      */     {
/*  852 */       localObject2 = new double[4];
/*  853 */       paramAffineTransform.getMatrix((double[])localObject2);
/*      */ 
/*  856 */       j = (localObject2[0] != (int)localObject2[0]) || (localObject2[3] != (int)localObject2[3]) ? 1 : 0;
/*      */     }
/*      */ 
/*  859 */     if (paramSunGraphics2D.renderHint != 2) {
/*  860 */       if ((localColorModel instanceof IndexColorModel)) {
/*  861 */         localObject2 = paramBufferedImage.getRaster();
/*  862 */         IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
/*      */ 
/*  864 */         if ((j != 0) && (localColorModel.getTransparency() == 1))
/*      */         {
/*  866 */           if ((localObject2 instanceof BytePackedRaster)) {
/*  867 */             localObject1 = ColorModel.getRGBdefault();
/*      */           }
/*      */           else {
/*  870 */             double[] arrayOfDouble = new double[6];
/*  871 */             paramAffineTransform.getMatrix(arrayOfDouble);
/*  872 */             if ((arrayOfDouble[1] != 0.0D) || (arrayOfDouble[2] != 0.0D) || (arrayOfDouble[4] != 0.0D) || (arrayOfDouble[5] != 0.0D))
/*      */             {
/*  877 */               int k = localIndexColorModel.getMapSize();
/*  878 */               if (k < 256) {
/*  879 */                 int[] arrayOfInt = new int[k + 1];
/*  880 */                 localIndexColorModel.getRGBs(arrayOfInt);
/*  881 */                 arrayOfInt[k] = 0;
/*  882 */                 localObject1 = new IndexColorModel(localIndexColorModel.getPixelSize(), k + 1, arrayOfInt, 0, true, k, 0);
/*      */               }
/*      */               else
/*      */               {
/*  889 */                 localObject1 = ColorModel.getRGBdefault();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  895 */       else if ((j != 0) && (localColorModel.getTransparency() == 1))
/*      */       {
/*  899 */         localObject1 = ColorModel.getRGBdefault();
/*      */       }
/*      */ 
/*      */     }
/*  904 */     else if (((localColorModel instanceof IndexColorModel)) || ((j != 0) && (localColorModel.getTransparency() == 1)))
/*      */     {
/*  910 */       localObject1 = ColorModel.getRGBdefault();
/*      */     }
/*      */ 
/*  914 */     return localObject1;
/*      */   }
/*      */ 
/*      */   protected void blitSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor)
/*      */   {
/*  927 */     if ((paramInt5 <= 0) || (paramInt6 <= 0))
/*      */     {
/*  944 */       return;
/*      */     }
/*  946 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/*  947 */     if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && ((paramSurfaceData1.getTransparency() == 1) || ((paramColor != null) && (paramColor.getTransparency() == 1))))
/*      */     {
/*  952 */       localCompositeType = CompositeType.SrcNoEa;
/*      */     }
/*      */     Object localObject;
/*  954 */     if (!isBgOperation(paramSurfaceData1, paramColor)) {
/*  955 */       localObject = Blit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  956 */       ((Blit)localObject).Blit(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */     else {
/*  959 */       localObject = BlitBg.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  960 */       ((BlitBg)localObject).BlitBg(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramColor.getRGB(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean scaleSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
/*      */   {
/*  976 */     CompositeType localCompositeType = paramSunGraphics2D.imageComp;
/*  977 */     if ((CompositeType.SrcOverNoEa.equals(localCompositeType)) && (paramSurfaceData1.getTransparency() == 1))
/*      */     {
/*  980 */       localCompositeType = CompositeType.SrcNoEa;
/*      */     }
/*      */ 
/*  983 */     ScaledBlit localScaledBlit = ScaledBlit.getFromCache(paramSurfaceType1, localCompositeType, paramSurfaceType2);
/*  984 */     if (localScaledBlit != null) {
/*  985 */       localScaledBlit.Scale(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
/*      */ 
/*  987 */       return true;
/*      */     }
/*  989 */     return false;
/*      */   }
/*      */ 
/*      */   protected static boolean imageReady(ToolkitImage paramToolkitImage, ImageObserver paramImageObserver)
/*      */   {
/*  995 */     if (paramToolkitImage.hasError()) {
/*  996 */       if (paramImageObserver != null) {
/*  997 */         paramImageObserver.imageUpdate(paramToolkitImage, 192, -1, -1, -1, -1);
/*      */       }
/*      */ 
/* 1001 */       return false;
/*      */     }
/* 1003 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1010 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1011 */       return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramColor);
/*      */     }
/* 1013 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1014 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1015 */       return false;
/*      */     }
/* 1017 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1018 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramColor, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1026 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1027 */       return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor);
/*      */     }
/* 1029 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1030 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1031 */       return false;
/*      */     }
/* 1033 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1034 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1046 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1047 */       return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor);
/*      */     }
/* 1049 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1050 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1051 */       return false;
/*      */     }
/* 1053 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1054 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
/*      */   {
/* 1064 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1065 */       return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor);
/*      */     }
/*      */ 
/* 1068 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1069 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1070 */       return false;
/*      */     }
/* 1072 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1073 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
/*      */   {
/* 1081 */     if (!(paramImage instanceof ToolkitImage)) {
/* 1082 */       transformImage(paramSunGraphics2D, paramImage, 0, 0, paramAffineTransform, paramSunGraphics2D.interpolationType);
/* 1083 */       return true;
/*      */     }
/* 1085 */     ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
/* 1086 */     if (!imageReady(localToolkitImage, paramImageObserver)) {
/* 1087 */       return false;
/*      */     }
/* 1089 */     ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
/* 1090 */     return localImageRepresentation.drawToBufImage(paramSunGraphics2D, localToolkitImage, paramAffineTransform, paramImageObserver);
/*      */   }
/*      */ 
/*      */   public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
/*      */   {
/* 1097 */     if (paramBufferedImageOp != null) {
/* 1098 */       if ((paramBufferedImageOp instanceof AffineTransformOp)) {
/* 1099 */         AffineTransformOp localAffineTransformOp = (AffineTransformOp)paramBufferedImageOp;
/* 1100 */         transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, localAffineTransformOp.getTransform(), localAffineTransformOp.getInterpolationType());
/*      */ 
/* 1103 */         return;
/*      */       }
/* 1105 */       paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
/*      */     }
/*      */ 
/* 1108 */     copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.pipe.DrawImage
 * JD-Core Version:    0.6.2
 */