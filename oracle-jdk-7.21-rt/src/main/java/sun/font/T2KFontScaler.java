/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ class T2KFontScaler extends FontScaler
/*     */ {
/*     */   private int[] bwGlyphs;
/*     */   private static final int TRUETYPE_FONT = 1;
/*     */   private static final int TYPE1_FONT = 2;
/* 169 */   private long layoutTablePtr = 0L;
/*     */ 
/*     */   private void initBWGlyphs()
/*     */   {
/*  28 */     if ((this.font.get() != null) && ("Courier New".equals(((Font2D)this.font.get()).getFontName(null)))) {
/*  29 */       this.bwGlyphs = new int[2];
/*  30 */       CharToGlyphMapper localCharToGlyphMapper = ((Font2D)this.font.get()).getMapper();
/*  31 */       this.bwGlyphs[0] = localCharToGlyphMapper.charToGlyph('W');
/*  32 */       this.bwGlyphs[1] = localCharToGlyphMapper.charToGlyph('w');
/*     */     }
/*     */   }
/*     */ 
/*     */   private static native void initIDs(Class paramClass);
/*     */ 
/*     */   private void invalidateScaler()
/*     */     throws FontScalerException
/*     */   {
/*  60 */     this.nativeScaler = 0L;
/*  61 */     this.font = null;
/*  62 */     throw new FontScalerException();
/*     */   }
/*     */ 
/*     */   public T2KFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2)
/*     */   {
/*  67 */     int i = 1;
/*  68 */     if ((paramFont2D instanceof Type1Font)) {
/*  69 */       i = 2;
/*     */     }
/*  71 */     this.font = new WeakReference(paramFont2D);
/*  72 */     initBWGlyphs();
/*  73 */     this.nativeScaler = initNativeScaler(paramFont2D, i, paramInt1, paramBoolean, paramInt2, this.bwGlyphs);
/*     */   }
/*     */ 
/*     */   synchronized StrikeMetrics getFontMetrics(long paramLong)
/*     */     throws FontScalerException
/*     */   {
/*  84 */     if (this.nativeScaler != 0L) {
/*  85 */       return getFontMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler);
/*     */     }
/*     */ 
/*  88 */     return getNullScaler().getFontMetrics(0L);
/*     */   }
/*     */ 
/*     */   synchronized float getGlyphAdvance(long paramLong, int paramInt) throws FontScalerException
/*     */   {
/*  93 */     if (this.nativeScaler != 0L) {
/*  94 */       return getGlyphAdvanceNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt);
/*     */     }
/*     */ 
/*  97 */     return getNullScaler().getGlyphAdvance(0L, paramInt);
/*     */   }
/*     */ 
/*     */   synchronized void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) throws FontScalerException
/*     */   {
/* 102 */     if (this.nativeScaler != 0L) {
/* 103 */       getGlyphMetricsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat);
/*     */     }
/*     */     else
/* 106 */       getNullScaler().getGlyphMetrics(0L, paramInt, paramFloat);
/*     */   }
/*     */ 
/*     */   synchronized long getGlyphImage(long paramLong, int paramInt)
/*     */     throws FontScalerException
/*     */   {
/* 112 */     if (this.nativeScaler != 0L) {
/* 113 */       return getGlyphImageNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt);
/*     */     }
/*     */ 
/* 116 */     return getNullScaler().getGlyphImage(0L, paramInt);
/*     */   }
/*     */ 
/*     */   synchronized Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) throws FontScalerException
/*     */   {
/* 121 */     if (this.nativeScaler != 0L) {
/* 122 */       return getGlyphOutlineBoundsNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt);
/*     */     }
/*     */ 
/* 125 */     return getNullScaler().getGlyphOutlineBounds(0L, paramInt);
/*     */   }
/*     */ 
/*     */   synchronized GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException
/*     */   {
/* 130 */     if (this.nativeScaler != 0L) {
/* 131 */       return getGlyphOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat1, paramFloat2);
/*     */     }
/*     */ 
/* 134 */     return getNullScaler().getGlyphOutline(0L, paramInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   synchronized GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
/*     */     throws FontScalerException
/*     */   {
/* 140 */     if (this.nativeScaler != 0L) {
/* 141 */       return getGlyphVectorOutlineNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
/*     */     }
/*     */ 
/* 144 */     return getNullScaler().getGlyphVectorOutline(0L, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   synchronized int getNumGlyphs() throws FontScalerException
/*     */   {
/* 149 */     if (this.nativeScaler != 0L) {
/* 150 */       return getNumGlyphsNative(this.nativeScaler);
/*     */     }
/* 152 */     return getNullScaler().getNumGlyphs();
/*     */   }
/*     */ 
/*     */   synchronized int getMissingGlyphCode() throws FontScalerException {
/* 156 */     if (this.nativeScaler != 0L) {
/* 157 */       return getMissingGlyphCodeNative(this.nativeScaler);
/*     */     }
/* 159 */     return getNullScaler().getMissingGlyphCode();
/*     */   }
/*     */ 
/*     */   synchronized int getGlyphCode(char paramChar) throws FontScalerException {
/* 163 */     if (this.nativeScaler != 0L) {
/* 164 */       return getGlyphCodeNative(this.nativeScaler, paramChar);
/*     */     }
/* 166 */     return getNullScaler().getGlyphCode(paramChar);
/*     */   }
/*     */ 
/*     */   synchronized long getLayoutTableCache()
/*     */     throws FontScalerException
/*     */   {
/* 172 */     if (this.nativeScaler == 0L) {
/* 173 */       return 0L;
/*     */     }
/*     */ 
/* 176 */     if (this.layoutTablePtr == 0L) {
/* 177 */       this.layoutTablePtr = getLayoutTableCacheNative(this.nativeScaler);
/*     */     }
/*     */ 
/* 180 */     return this.layoutTablePtr;
/*     */   }
/*     */ 
/*     */   public synchronized void dispose() {
/* 184 */     if ((this.nativeScaler != 0L) || (this.layoutTablePtr != 0L)) {
/* 185 */       disposeNativeScaler(this.nativeScaler, this.layoutTablePtr);
/* 186 */       this.nativeScaler = 0L;
/* 187 */       this.layoutTablePtr = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   synchronized Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
/*     */     throws FontScalerException
/*     */   {
/* 194 */     if (this.nativeScaler != 0L) {
/* 195 */       return getGlyphPointNative((Font2D)this.font.get(), paramLong, this.nativeScaler, paramInt1, paramInt2);
/*     */     }
/*     */ 
/* 198 */     return getNullScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   synchronized long getUnitsPerEm()
/*     */   {
/* 203 */     return getUnitsPerEMNative(this.nativeScaler);
/*     */   }
/*     */ 
/*     */   long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean)
/*     */   {
/* 209 */     if (this.nativeScaler != 0L) {
/* 210 */       return createScalerContextNative(this.nativeScaler, paramArrayOfDouble, paramInt1, paramInt2, paramFloat1, paramFloat2, paramBoolean);
/*     */     }
/*     */ 
/* 213 */     return NullFontScaler.getNullScalerContext();
/*     */   }
/*     */ 
/*     */   private native long initNativeScaler(Font2D paramFont2D, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int[] paramArrayOfInt);
/*     */ 
/*     */   private native StrikeMetrics getFontMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2);
/*     */ 
/*     */   private native float getGlyphAdvanceNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
/*     */ 
/*     */   private native void getGlyphMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, Point2D.Float paramFloat);
/*     */ 
/*     */   private native long getGlyphImageNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
/*     */ 
/*     */   private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
/*     */ 
/*     */   private native GeneralPath getGlyphOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, float paramFloat1, float paramFloat2);
/*     */ 
/*     */   private native GeneralPath getGlyphVectorOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2);
/*     */ 
/*     */   private native int getGlyphCodeNative(long paramLong, char paramChar);
/*     */ 
/*     */   private native long getLayoutTableCacheNative(long paramLong);
/*     */ 
/*     */   private native void disposeNativeScaler(long paramLong1, long paramLong2);
/*     */ 
/*     */   private native int getNumGlyphsNative(long paramLong);
/*     */ 
/*     */   private native int getMissingGlyphCodeNative(long paramLong);
/*     */ 
/*     */   private native long getUnitsPerEMNative(long paramLong);
/*     */ 
/*     */   native long createScalerContextNative(long paramLong, double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean);
/*     */ 
/*     */   native Point2D.Float getGlyphPointNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2);
/*     */ 
/*     */   void invalidateScalerContext(long paramLong)
/*     */   {
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  49 */         FontManagerNativeLibrary.load();
/*  50 */         System.loadLibrary("t2k");
/*  51 */         return null;
/*     */       }
/*     */     });
/*  54 */     initIDs(T2KFontScaler.class);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.T2KFontScaler
 * JD-Core Version:    0.6.2
 */