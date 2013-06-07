/*     */ package sun.font;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public class XRGlyphCacheEntry
/*     */ {
/*     */   long glyphInfoPtr;
/*     */   int lastUsed;
/*     */   boolean pinned;
/*     */   int xOff;
/*     */   int yOff;
/*     */   int glyphSet;
/*     */ 
/*     */   public XRGlyphCacheEntry(long paramLong, GlyphList paramGlyphList)
/*     */   {
/*  48 */     this.glyphInfoPtr = paramLong;
/*     */ 
/*  51 */     this.xOff = Math.round(getXAdvance());
/*  52 */     this.yOff = Math.round(getYAdvance());
/*     */   }
/*     */ 
/*     */   public int getXOff() {
/*  56 */     return this.xOff;
/*     */   }
/*     */ 
/*     */   public int getYOff() {
/*  60 */     return this.yOff;
/*     */   }
/*     */ 
/*     */   public void setGlyphSet(int paramInt) {
/*  64 */     this.glyphSet = paramInt;
/*     */   }
/*     */ 
/*     */   public int getGlyphSet() {
/*  68 */     return this.glyphSet;
/*     */   }
/*     */ 
/*     */   public static int getGlyphID(long paramLong) {
/*  72 */     return StrikeCache.unsafe.getInt(paramLong + StrikeCache.cacheCellOffset);
/*     */   }
/*     */ 
/*     */   public static void setGlyphID(long paramLong, int paramInt) {
/*  76 */     StrikeCache.unsafe.putInt(paramLong + StrikeCache.cacheCellOffset, paramInt);
/*     */   }
/*     */ 
/*     */   public int getGlyphID() {
/*  80 */     return getGlyphID(this.glyphInfoPtr);
/*     */   }
/*     */ 
/*     */   public void setGlyphID(int paramInt) {
/*  84 */     setGlyphID(this.glyphInfoPtr, paramInt);
/*     */   }
/*     */ 
/*     */   public float getXAdvance() {
/*  88 */     return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + StrikeCache.xAdvanceOffset);
/*     */   }
/*     */ 
/*     */   public float getYAdvance() {
/*  92 */     return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + StrikeCache.yAdvanceOffset);
/*     */   }
/*     */ 
/*     */   public int getSourceRowBytes() {
/*  96 */     return StrikeCache.unsafe.getShort(this.glyphInfoPtr + StrikeCache.rowBytesOffset);
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 100 */     return StrikeCache.unsafe.getShort(this.glyphInfoPtr + StrikeCache.widthOffset);
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 104 */     return StrikeCache.unsafe.getShort(this.glyphInfoPtr + StrikeCache.heightOffset);
/*     */   }
/*     */ 
/*     */   public void writePixelData(ByteArrayOutputStream paramByteArrayOutputStream, boolean paramBoolean)
/*     */   {
/*     */     long l;
/* 109 */     if (StrikeCache.nativeAddressSize == 4)
/* 110 */       l = 0xFFFFFFFF & StrikeCache.unsafe.getInt(this.glyphInfoPtr + StrikeCache.pixelDataOffset);
/*     */     else {
/* 112 */       l = StrikeCache.unsafe.getLong(this.glyphInfoPtr + StrikeCache.pixelDataOffset);
/*     */     }
/* 114 */     if (l == 0L) {
/* 115 */       return;
/*     */     }
/*     */ 
/* 118 */     int i = getWidth();
/* 119 */     int j = getHeight();
/* 120 */     int k = getSourceRowBytes();
/* 121 */     int m = getPaddedWidth(paramBoolean);
/*     */     int n;
/*     */     int i1;
/* 123 */     if (!paramBoolean) {
/* 124 */       for (n = 0; n < j; n++) {
/* 125 */         for (i1 = 0; i1 < m; i1++) {
/* 126 */           if (i1 < i) {
/* 127 */             paramByteArrayOutputStream.write(StrikeCache.unsafe.getByte(l + (n * k + i1)));
/*     */           }
/*     */           else
/* 130 */             paramByteArrayOutputStream.write(0);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/* 135 */       for (n = 0; n < j; n++) {
/* 136 */         i1 = n * k;
/* 137 */         int i2 = i * 3;
/* 138 */         int i3 = 0;
/* 139 */         while (i3 < i2) {
/* 140 */           paramByteArrayOutputStream.write(StrikeCache.unsafe.getByte(l + (i1 + i3 + 2)));
/*     */ 
/* 142 */           paramByteArrayOutputStream.write(StrikeCache.unsafe.getByte(l + (i1 + i3 + 1)));
/*     */ 
/* 144 */           paramByteArrayOutputStream.write(StrikeCache.unsafe.getByte(l + (i1 + i3 + 0)));
/*     */ 
/* 146 */           paramByteArrayOutputStream.write(255);
/* 147 */           i3 += 3;
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public float getTopLeftXOffset()
/*     */   {
/* 154 */     return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + StrikeCache.topLeftXOffset);
/*     */   }
/*     */ 
/*     */   public float getTopLeftYOffset() {
/* 158 */     return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + StrikeCache.topLeftYOffset);
/*     */   }
/*     */ 
/*     */   public long getGlyphInfoPtr() {
/* 162 */     return this.glyphInfoPtr;
/*     */   }
/*     */ 
/*     */   public boolean isGrayscale(boolean paramBoolean) {
/* 166 */     return (getSourceRowBytes() == getWidth()) && ((getWidth() != 0) || (getHeight() != 0) || (!paramBoolean));
/*     */   }
/*     */ 
/*     */   public int getPaddedWidth(boolean paramBoolean) {
/* 170 */     int i = getWidth();
/* 171 */     return isGrayscale(paramBoolean) ? (int)Math.ceil(i / 4.0D) * 4 : i;
/*     */   }
/*     */ 
/*     */   public int getDestinationRowBytes(boolean paramBoolean) {
/* 175 */     boolean bool = isGrayscale(paramBoolean);
/* 176 */     return bool ? getPaddedWidth(bool) : getWidth() * 4;
/*     */   }
/*     */ 
/*     */   public int getGlyphDataLenth(boolean paramBoolean) {
/* 180 */     return getDestinationRowBytes(paramBoolean) * getHeight();
/*     */   }
/*     */ 
/*     */   public void setPinned() {
/* 184 */     this.pinned = true;
/*     */   }
/*     */ 
/*     */   public void setUnpinned() {
/* 188 */     this.pinned = false;
/*     */   }
/*     */ 
/*     */   public int getLastUsed() {
/* 192 */     return this.lastUsed;
/*     */   }
/*     */ 
/*     */   public void setLastUsed(int paramInt) {
/* 196 */     this.lastUsed = paramInt;
/*     */   }
/*     */ 
/*     */   public int getPixelCnt() {
/* 200 */     return getWidth() * getHeight();
/*     */   }
/*     */ 
/*     */   public boolean isPinned() {
/* 204 */     return this.pinned;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.XRGlyphCacheEntry
 * JD-Core Version:    0.6.2
 */