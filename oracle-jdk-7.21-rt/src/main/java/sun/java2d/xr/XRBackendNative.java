/*     */ package sun.java2d.xr;
/*     */ 
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.util.List;
/*     */ import sun.font.GlyphList;
/*     */ import sun.font.XRGlyphCacheEntry;
/*     */ import sun.java2d.jules.TrapezoidList;
/*     */ import sun.java2d.pipe.Region;
/*     */ 
/*     */ public class XRBackendNative
/*     */   implements XRBackend
/*     */ {
/*     */   private static long FMTPTR_A8;
/*     */   private static long FMTPTR_ARGB32;
/*     */   private static long MASK_XIMG;
/*     */ 
/*     */   private static native void initIDs();
/*     */ 
/*     */   public native long createGC(int paramInt);
/*     */ 
/*     */   public native void freeGC(long paramLong);
/*     */ 
/*     */   public native int createPixmap(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*     */ 
/*     */   private native int createPictureNative(int paramInt, long paramLong);
/*     */ 
/*     */   public native void freePicture(int paramInt);
/*     */ 
/*     */   public native void freePixmap(int paramInt);
/*     */ 
/*     */   public native void setGCExposures(long paramLong, boolean paramBoolean);
/*     */ 
/*     */   public native void setGCForeground(long paramLong, int paramInt);
/*     */ 
/*     */   public native void setPictureRepeat(int paramInt1, int paramInt2);
/*     */ 
/*     */   public native void copyArea(int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
/*     */ 
/*     */   public native void setGCMode(long paramLong, boolean paramBoolean);
/*     */ 
/*     */   private static native void GCRectanglesNative(int paramInt1, long paramLong, int[] paramArrayOfInt, int paramInt2);
/*     */ 
/*     */   public native void renderComposite(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11);
/*     */ 
/*     */   private native void renderRectangle(int paramInt1, byte paramByte, short paramShort1, short paramShort2, short paramShort3, short paramShort4, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*     */ 
/*     */   private static native void XRenderRectanglesNative(int paramInt1, byte paramByte, short paramShort1, short paramShort2, short paramShort3, short paramShort4, int[] paramArrayOfInt, int paramInt2);
/*     */ 
/*     */   private native void XRSetTransformNative(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
/*     */ 
/*     */   private static native int XRCreateLinearGradientPaintNative(float[] paramArrayOfFloat, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12);
/*     */ 
/*     */   private static native int XRCreateRadialGradientPaintNative(float[] paramArrayOfFloat, short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10);
/*     */ 
/*     */   public native void setFilter(int paramInt1, int paramInt2);
/*     */ 
/*     */   private static native void XRSetClipNative(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion, boolean paramBoolean);
/*     */ 
/*     */   public void GCRectangles(int paramInt, long paramLong, GrowableRectArray paramGrowableRectArray)
/*     */   {
/* 127 */     GCRectanglesNative(paramInt, paramLong, paramGrowableRectArray.getArray(), paramGrowableRectArray.getSize());
/*     */   }
/*     */ 
/*     */   public int createPicture(int paramInt1, int paramInt2) {
/* 131 */     return createPictureNative(paramInt1, getFormatPtr(paramInt2));
/*     */   }
/*     */ 
/*     */   public void setPictureTransform(int paramInt, AffineTransform paramAffineTransform) {
/* 135 */     XRSetTransformNative(paramInt, XRUtils.XDoubleToFixed(paramAffineTransform.getScaleX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearX()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearY()), XRUtils.XDoubleToFixed(paramAffineTransform.getScaleY()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateY()));
/*     */   }
/*     */ 
/*     */   public void renderRectangle(int paramInt1, byte paramByte, XRColor paramXRColor, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*     */   {
/* 146 */     renderRectangle(paramInt1, paramByte, (short)paramXRColor.red, (short)paramXRColor.green, (short)paramXRColor.blue, (short)paramXRColor.alpha, paramInt2, paramInt3, paramInt4, paramInt5);
/*     */   }
/*     */ 
/*     */   private short[] getRenderColors(int[] paramArrayOfInt)
/*     */   {
/* 152 */     short[] arrayOfShort = new short[paramArrayOfInt.length * 4];
/*     */ 
/* 154 */     XRColor localXRColor = new XRColor();
/* 155 */     for (int i = 0; i < paramArrayOfInt.length; i++) {
/* 156 */       localXRColor.setColorValues(paramArrayOfInt[i], true);
/* 157 */       arrayOfShort[(i * 4 + 0)] = ((short)localXRColor.alpha);
/* 158 */       arrayOfShort[(i * 4 + 1)] = ((short)localXRColor.red);
/* 159 */       arrayOfShort[(i * 4 + 2)] = ((short)localXRColor.green);
/* 160 */       arrayOfShort[(i * 4 + 3)] = ((short)localXRColor.blue);
/*     */     }
/*     */ 
/* 163 */     return arrayOfShort;
/*     */   }
/*     */ 
/*     */   private static long getFormatPtr(int paramInt) {
/* 167 */     switch (paramInt) {
/*     */     case 2:
/* 169 */       return FMTPTR_A8;
/*     */     case 0:
/* 171 */       return FMTPTR_ARGB32;
/*     */     }
/*     */ 
/* 174 */     return 0L;
/*     */   }
/*     */ 
/*     */   public int createLinearGradient(Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, int[] paramArrayOfInt, int paramInt, AffineTransform paramAffineTransform)
/*     */   {
/* 180 */     short[] arrayOfShort = getRenderColors(paramArrayOfInt);
/* 181 */     int i = XRCreateLinearGradientPaintNative(paramArrayOfFloat, arrayOfShort, XRUtils.XDoubleToFixed(paramPoint2D1.getX()), XRUtils.XDoubleToFixed(paramPoint2D1.getY()), XRUtils.XDoubleToFixed(paramPoint2D2.getX()), XRUtils.XDoubleToFixed(paramPoint2D2.getY()), paramArrayOfFloat.length, paramInt, XRUtils.XDoubleToFixed(paramAffineTransform.getScaleX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearX()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearY()), XRUtils.XDoubleToFixed(paramAffineTransform.getScaleY()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateY()));
/*     */ 
/* 192 */     return i;
/*     */   }
/*     */ 
/*     */   public int createRadialGradient(Point2D paramPoint2D1, Point2D paramPoint2D2, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int[] paramArrayOfInt, int paramInt, AffineTransform paramAffineTransform)
/*     */   {
/* 200 */     short[] arrayOfShort = getRenderColors(paramArrayOfInt);
/* 201 */     return XRCreateRadialGradientPaintNative(paramArrayOfFloat, arrayOfShort, paramArrayOfFloat.length, XRUtils.XDoubleToFixed(paramFloat1), XRUtils.XDoubleToFixed(paramFloat2), paramInt, XRUtils.XDoubleToFixed(paramAffineTransform.getScaleX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearX()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearY()), XRUtils.XDoubleToFixed(paramAffineTransform.getScaleY()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateY()));
/*     */   }
/*     */ 
/*     */   public void setGCClipRectangles(long paramLong, Region paramRegion)
/*     */   {
/* 215 */     XRSetClipNative(paramLong, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion.isRectangular() ? null : paramRegion, true);
/*     */   }
/*     */ 
/*     */   public void setClipRectangles(int paramInt, Region paramRegion)
/*     */   {
/* 221 */     if (paramRegion != null) {
/* 222 */       XRSetClipNative(paramInt, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion.isRectangular() ? null : paramRegion, false);
/*     */     }
/*     */     else
/*     */     {
/* 226 */       XRSetClipNative(paramInt, 0, 0, 32767, 32767, null, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void renderRectangles(int paramInt, byte paramByte, XRColor paramXRColor, GrowableRectArray paramGrowableRectArray)
/*     */   {
/* 232 */     XRenderRectanglesNative(paramInt, paramByte, (short)paramXRColor.red, (short)paramXRColor.green, (short)paramXRColor.blue, (short)paramXRColor.alpha, paramGrowableRectArray.getArray(), paramGrowableRectArray.getSize());
/*     */   }
/*     */ 
/*     */   private static long[] getGlyphInfoPtrs(List<XRGlyphCacheEntry> paramList)
/*     */   {
/* 240 */     long[] arrayOfLong = new long[paramList.size()];
/* 241 */     for (int i = 0; i < paramList.size(); i++) {
/* 242 */       arrayOfLong[i] = ((XRGlyphCacheEntry)paramList.get(i)).getGlyphInfoPtr();
/*     */     }
/* 244 */     return arrayOfLong;
/*     */   }
/*     */ 
/*     */   public void XRenderAddGlyphs(int paramInt, GlyphList paramGlyphList, List<XRGlyphCacheEntry> paramList, byte[] paramArrayOfByte)
/*     */   {
/* 250 */     long[] arrayOfLong = getGlyphInfoPtrs(paramList);
/* 251 */     XRAddGlyphsNative(paramInt, arrayOfLong, arrayOfLong.length, paramArrayOfByte, paramArrayOfByte.length);
/*     */   }
/*     */ 
/*     */   public void XRenderFreeGlyphs(int paramInt, int[] paramArrayOfInt)
/*     */   {
/* 256 */     XRFreeGlyphsNative(paramInt, paramArrayOfInt, paramArrayOfInt.length);
/*     */   }
/*     */ 
/*     */   private static native void XRAddGlyphsNative(int paramInt1, long[] paramArrayOfLong, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
/*     */ 
/*     */   private static native void XRFreeGlyphsNative(int paramInt1, int[] paramArrayOfInt, int paramInt2);
/*     */ 
/*     */   private static native void XRenderCompositeTextNative(int paramInt1, int paramInt2, int paramInt3, long paramLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4, int paramInt5);
/*     */ 
/*     */   public int XRenderCreateGlyphSet(int paramInt)
/*     */   {
/* 274 */     return XRenderCreateGlyphSetNative(getFormatPtr(paramInt));
/*     */   }
/*     */ 
/*     */   private static native int XRenderCreateGlyphSetNative(long paramLong);
/*     */ 
/*     */   public void XRenderCompositeText(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, GrowableEltArray paramGrowableEltArray)
/*     */   {
/* 284 */     GrowableIntArray localGrowableIntArray = paramGrowableEltArray.getGlyphs();
/* 285 */     XRenderCompositeTextNative(paramByte, paramInt1, paramInt2, 0L, paramGrowableEltArray.getArray(), localGrowableIntArray.getArray(), paramGrowableEltArray.getSize(), localGrowableIntArray.getSize());
/*     */   }
/*     */ 
/*     */   public void putMaskImage(int paramInt1, long paramLong, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, float paramFloat)
/*     */   {
/* 294 */     putMaskNative(paramInt1, paramLong, paramArrayOfByte, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9, paramFloat, MASK_XIMG);
/*     */   }
/*     */ 
/*     */   private static native void putMaskNative(int paramInt1, long paramLong1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, float paramFloat, long paramLong2);
/*     */ 
/*     */   public void padBlit(byte paramByte, int paramInt1, int paramInt2, int paramInt3, AffineTransform paramAffineTransform, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13)
/*     */   {
/* 310 */     padBlitNative(paramByte, paramInt1, paramInt2, paramInt3, XRUtils.XDoubleToFixed(paramAffineTransform.getScaleX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearX()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateX()), XRUtils.XDoubleToFixed(paramAffineTransform.getShearY()), XRUtils.XDoubleToFixed(paramAffineTransform.getScaleY()), XRUtils.XDoubleToFixed(paramAffineTransform.getTranslateY()), paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9, paramInt10, paramInt11, paramInt12, paramInt13);
/*     */   }
/*     */ 
/*     */   private static native void padBlitNative(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16, int paramInt17, int paramInt18, int paramInt19);
/*     */ 
/*     */   public void renderCompositeTrapezoids(byte paramByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, TrapezoidList paramTrapezoidList)
/*     */   {
/* 334 */     renderCompositeTrapezoidsNative(paramByte, paramInt1, getFormatPtr(paramInt2), paramInt3, paramInt4, paramInt5, paramTrapezoidList.getTrapArray());
/*     */   }
/*     */ 
/*     */   private static native void renderCompositeTrapezoidsNative(byte paramByte, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);
/*     */ 
/*     */   static
/*     */   {
/*  47 */     initIDs();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.XRBackendNative
 * JD-Core Version:    0.6.2
 */