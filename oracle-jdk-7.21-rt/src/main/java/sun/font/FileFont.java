/*     */ package sun.font;
/*     */ 
/*     */ import java.awt.FontFormatException;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import sun.java2d.Disposer;
/*     */ import sun.java2d.DisposerRecord;
/*     */ 
/*     */ public abstract class FileFont extends PhysicalFont
/*     */ {
/*  45 */   protected boolean useJavaRasterizer = true;
/*     */   protected int fileSize;
/*     */   protected FontScaler scaler;
/*     */   protected boolean checkedNatives;
/*     */   protected boolean useNatives;
/*     */   protected NativeFont[] nativeFonts;
/*     */   protected char[] glyphToCharMap;
/*     */ 
/*     */   FileFont(String paramString, Object paramObject)
/*     */     throws FontFormatException
/*     */   {
/*  88 */     super(paramString, paramObject);
/*     */   }
/*     */ 
/*     */   FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc) {
/*  92 */     if (!this.checkedNatives) {
/*  93 */       checkUseNatives();
/*     */     }
/*  95 */     return new FileFontStrike(this, paramFontStrikeDesc);
/*     */   }
/*     */ 
/*     */   protected boolean checkUseNatives() {
/*  99 */     this.checkedNatives = true;
/* 100 */     return this.useNatives;
/*     */   }
/*     */ 
/*     */   protected abstract void close();
/*     */ 
/*     */   abstract ByteBuffer readBlock(int paramInt1, int paramInt2);
/*     */ 
/*     */   public boolean canDoStyle(int paramInt)
/*     */   {
/* 116 */     return true;
/*     */   }
/*     */ 
/*     */   void setFileToRemove(File paramFile, CreatedFontTracker paramCreatedFontTracker) {
/* 120 */     Disposer.addObjectRecord(this, new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker, null));
/*     */   }
/*     */ 
/*     */   static void setFileToRemove(Object paramObject, File paramFile, CreatedFontTracker paramCreatedFontTracker)
/*     */   {
/* 126 */     Disposer.addObjectRecord(paramObject, new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker, null));
/*     */   }
/*     */ 
/*     */   synchronized void deregisterFontAndClearStrikeCache()
/*     */   {
/* 159 */     SunFontManager localSunFontManager = SunFontManager.getInstance();
/* 160 */     localSunFontManager.deRegisterBadFont(this);
/*     */ 
/* 162 */     for (Reference localReference : this.strikeCache.values()) {
/* 163 */       if (localReference != null)
/*     */       {
/* 167 */         FileFontStrike localFileFontStrike = (FileFontStrike)localReference.get();
/* 168 */         if ((localFileFontStrike != null) && (localFileFontStrike.pScalerContext != 0L)) {
/* 169 */           this.scaler.invalidateScalerContext(localFileFontStrike.pScalerContext);
/*     */         }
/*     */       }
/*     */     }
/* 173 */     this.scaler.dispose();
/* 174 */     this.scaler = FontScaler.getNullScaler();
/*     */   }
/*     */ 
/*     */   StrikeMetrics getFontMetrics(long paramLong) {
/*     */     try {
/* 179 */       return getScaler().getFontMetrics(paramLong);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 181 */       this.scaler = FontScaler.getNullScaler();
/* 182 */     }return getFontMetrics(paramLong);
/*     */   }
/*     */ 
/*     */   float getGlyphAdvance(long paramLong, int paramInt)
/*     */   {
/*     */     try {
/* 188 */       return getScaler().getGlyphAdvance(paramLong, paramInt);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 190 */       this.scaler = FontScaler.getNullScaler();
/* 191 */     }return getGlyphAdvance(paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat)
/*     */   {
/*     */     try {
/* 197 */       getScaler().getGlyphMetrics(paramLong, paramInt, paramFloat);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 199 */       this.scaler = FontScaler.getNullScaler();
/* 200 */       getGlyphMetrics(paramLong, paramInt, paramFloat);
/*     */     }
/*     */   }
/*     */ 
/*     */   long getGlyphImage(long paramLong, int paramInt) {
/*     */     try {
/* 206 */       return getScaler().getGlyphImage(paramLong, paramInt);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 208 */       this.scaler = FontScaler.getNullScaler();
/* 209 */     }return getGlyphImage(paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt)
/*     */   {
/*     */     try {
/* 215 */       return getScaler().getGlyphOutlineBounds(paramLong, paramInt);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 217 */       this.scaler = FontScaler.getNullScaler();
/* 218 */     }return getGlyphOutlineBounds(paramLong, paramInt);
/*     */   }
/*     */ 
/*     */   GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/*     */     try {
/* 224 */       return getScaler().getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 226 */       this.scaler = FontScaler.getNullScaler();
/* 227 */     }return getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
/*     */   {
/*     */     try {
/* 233 */       return getScaler().getGlyphVectorOutline(paramLong, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
/*     */     } catch (FontScalerException localFontScalerException) {
/* 235 */       this.scaler = FontScaler.getNullScaler();
/* 236 */     }return getGlyphVectorOutline(paramLong, paramArrayOfInt, paramInt, paramFloat1, paramFloat2);
/*     */   }
/*     */ 
/*     */   protected abstract FontScaler getScaler();
/*     */ 
/*     */   protected long getUnitsPerEm()
/*     */   {
/* 245 */     return getScaler().getUnitsPerEm();
/*     */   }
/*     */ 
/*     */   protected String getPublicFileName()
/*     */   {
/* 289 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 290 */     if (localSecurityManager == null) {
/* 291 */       return this.platName;
/*     */     }
/* 293 */     int i = 1;
/*     */     try
/*     */     {
/* 296 */       localSecurityManager.checkPropertyAccess("java.io.tmpdir");
/*     */     } catch (SecurityException localSecurityException) {
/* 298 */       i = 0;
/*     */     }
/*     */ 
/* 301 */     if (i != 0) {
/* 302 */       return this.platName;
/*     */     }
/*     */ 
/* 305 */     final File localFile = new File(this.platName);
/*     */ 
/* 307 */     Boolean localBoolean = Boolean.FALSE;
/*     */     try {
/* 309 */       localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Boolean run() {
/* 312 */           File localFile = new File(System.getProperty("java.io.tmpdir"));
/*     */           try {
/* 314 */             String str1 = localFile.getCanonicalPath();
/* 315 */             String str2 = localFile.getCanonicalPath();
/*     */ 
/* 317 */             return Boolean.valueOf((str2 == null) || (str2.startsWith(str1))); } catch (IOException localIOException) {
/*     */           }
/* 319 */           return Boolean.TRUE;
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/* 327 */       localBoolean = Boolean.TRUE;
/*     */     }
/*     */ 
/* 330 */     return localBoolean.booleanValue() ? "temp file" : this.platName;
/*     */   }
/*     */ 
/*     */   private static class CreatedFontFileDisposerRecord
/*     */     implements DisposerRecord
/*     */   {
/* 251 */     File fontFile = null;
/*     */     CreatedFontTracker tracker;
/*     */ 
/*     */     private CreatedFontFileDisposerRecord(File paramFile, CreatedFontTracker paramCreatedFontTracker)
/*     */     {
/* 256 */       this.fontFile = paramFile;
/* 257 */       this.tracker = paramCreatedFontTracker;
/*     */     }
/*     */ 
/*     */     public void dispose() {
/* 261 */       AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Object run() {
/* 264 */           if (FileFont.CreatedFontFileDisposerRecord.this.fontFile != null)
/*     */             try {
/* 266 */               if (FileFont.CreatedFontFileDisposerRecord.this.tracker != null) {
/* 267 */                 FileFont.CreatedFontFileDisposerRecord.this.tracker.subBytes((int)FileFont.CreatedFontFileDisposerRecord.this.fontFile.length());
/*     */               }
/*     */ 
/* 275 */               FileFont.CreatedFontFileDisposerRecord.this.fontFile.delete();
/*     */ 
/* 278 */               SunFontManager.getInstance().tmpFontFiles.remove(FileFont.CreatedFontFileDisposerRecord.this.fontFile);
/*     */             }
/*     */             catch (Exception localException) {
/*     */             }
/* 282 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.FileFont
 * JD-Core Version:    0.6.2
 */