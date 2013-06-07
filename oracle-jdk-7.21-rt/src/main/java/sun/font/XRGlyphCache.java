/*     */ package sun.font;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.awt.SunToolkit;
/*     */ import sun.java2d.xr.GrowableIntArray;
/*     */ import sun.java2d.xr.MutableInteger;
/*     */ import sun.java2d.xr.XRBackend;
/*     */ import sun.java2d.xr.XRCompositeManager;
/*     */ 
/*     */ public class XRGlyphCache
/*     */   implements GlyphDisposedListener
/*     */ {
/*     */   XRBackend con;
/*     */   XRCompositeManager maskBuffer;
/*  43 */   HashMap<MutableInteger, XRGlyphCacheEntry> cacheMap = new HashMap(256);
/*     */ 
/*  45 */   int nextID = 1;
/*  46 */   MutableInteger tmp = new MutableInteger(0);
/*     */   int grayGlyphSet;
/*     */   int lcdGlyphSet;
/*  51 */   int time = 0;
/*  52 */   int cachedPixels = 0;
/*     */   static final int MAX_CACHED_PIXELS = 100000;
/*  55 */   ArrayList<Integer> freeGlyphIDs = new ArrayList(255);
/*     */   static final boolean batchGlyphUpload = true;
/*     */ 
/*     */   public XRGlyphCache(XRCompositeManager paramXRCompositeManager)
/*     */   {
/*  60 */     this.con = paramXRCompositeManager.getBackend();
/*  61 */     this.maskBuffer = paramXRCompositeManager;
/*     */ 
/*  63 */     this.grayGlyphSet = this.con.XRenderCreateGlyphSet(2);
/*  64 */     this.lcdGlyphSet = this.con.XRenderCreateGlyphSet(0);
/*     */ 
/*  66 */     StrikeCache.addGlyphDisposedListener(this);
/*     */   }
/*     */ 
/*     */   public void glyphDisposed(ArrayList<Long> paramArrayList) {
/*     */     try {
/*  71 */       SunToolkit.awtLock();
/*     */ 
/*  73 */       GrowableIntArray localGrowableIntArray = new GrowableIntArray(1, paramArrayList.size());
/*  74 */       for (Iterator localIterator = paramArrayList.iterator(); localIterator.hasNext(); ) { long l = ((Long)localIterator.next()).longValue();
/*  75 */         int i = XRGlyphCacheEntry.getGlyphID(l);
/*     */ 
/*  78 */         if (i != 0) {
/*  79 */           localGrowableIntArray.addInt(i);
/*     */         }
/*     */       }
/*  82 */       freeGlyphs(localGrowableIntArray);
/*     */     } finally {
/*  84 */       SunToolkit.awtUnlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int getFreeGlyphID() {
/*  89 */     if (this.freeGlyphIDs.size() > 0) {
/*  90 */       int i = ((Integer)this.freeGlyphIDs.remove(this.freeGlyphIDs.size() - 1)).intValue();
/*  91 */       return i;
/*     */     }
/*  93 */     return this.nextID++;
/*     */   }
/*     */ 
/*     */   protected XRGlyphCacheEntry getEntryForPointer(long paramLong) {
/*  97 */     int i = XRGlyphCacheEntry.getGlyphID(paramLong);
/*     */ 
/*  99 */     if (i == 0) {
/* 100 */       return null;
/*     */     }
/*     */ 
/* 103 */     this.tmp.setValue(i);
/* 104 */     return (XRGlyphCacheEntry)this.cacheMap.get(this.tmp);
/*     */   }
/*     */ 
/*     */   public XRGlyphCacheEntry[] cacheGlyphs(GlyphList paramGlyphList) {
/* 108 */     this.time += 1;
/*     */ 
/* 110 */     XRGlyphCacheEntry[] arrayOfXRGlyphCacheEntry = new XRGlyphCacheEntry[paramGlyphList.getNumGlyphs()];
/* 111 */     long[] arrayOfLong = paramGlyphList.getImages();
/* 112 */     ArrayList localArrayList = null;
/*     */ 
/* 114 */     for (int i = 0; i < paramGlyphList.getNumGlyphs(); i++)
/*     */     {
/*     */       XRGlyphCacheEntry localXRGlyphCacheEntry;
/* 118 */       if ((localXRGlyphCacheEntry = getEntryForPointer(arrayOfLong[i])) == null) {
/* 119 */         localXRGlyphCacheEntry = new XRGlyphCacheEntry(arrayOfLong[i], paramGlyphList);
/* 120 */         localXRGlyphCacheEntry.setGlyphID(getFreeGlyphID());
/* 121 */         this.cacheMap.put(new MutableInteger(localXRGlyphCacheEntry.getGlyphID()), localXRGlyphCacheEntry);
/*     */ 
/* 123 */         if (localArrayList == null) {
/* 124 */           localArrayList = new ArrayList();
/*     */         }
/* 126 */         localArrayList.add(localXRGlyphCacheEntry);
/*     */       }
/* 128 */       localXRGlyphCacheEntry.setLastUsed(this.time);
/* 129 */       arrayOfXRGlyphCacheEntry[i] = localXRGlyphCacheEntry;
/*     */     }
/*     */ 
/* 133 */     if (localArrayList != null) {
/* 134 */       uploadGlyphs(arrayOfXRGlyphCacheEntry, localArrayList, paramGlyphList, null);
/*     */     }
/*     */ 
/* 137 */     return arrayOfXRGlyphCacheEntry;
/*     */   }
/*     */ 
/*     */   protected void uploadGlyphs(XRGlyphCacheEntry[] paramArrayOfXRGlyphCacheEntry, ArrayList<XRGlyphCacheEntry> paramArrayList, GlyphList paramGlyphList, int[] paramArrayOfInt) {
/* 141 */     for (Iterator localIterator = paramArrayList.iterator(); localIterator.hasNext(); ) { localObject = (XRGlyphCacheEntry)localIterator.next();
/* 142 */       this.cachedPixels += ((XRGlyphCacheEntry)localObject).getPixelCnt();
/*     */     }
/*     */ 
/* 145 */     if (this.cachedPixels > 100000) {
/* 146 */       clearCache(paramArrayOfXRGlyphCacheEntry);
/*     */     }
/*     */ 
/* 149 */     boolean bool = containsLCDGlyphs(paramArrayList);
/* 150 */     Object localObject = seperateGlyphTypes(paramArrayList, bool);
/* 151 */     List localList1 = localObject[0];
/* 152 */     List localList2 = localObject[1];
/*     */ 
/* 160 */     if ((localList1 != null) && (localList1.size() > 0)) {
/* 161 */       this.con.XRenderAddGlyphs(this.grayGlyphSet, paramGlyphList, localList1, generateGlyphImageStream(localList1));
/*     */     }
/* 163 */     if ((localList2 != null) && (localList2.size() > 0))
/* 164 */       this.con.XRenderAddGlyphs(this.lcdGlyphSet, paramGlyphList, localList2, generateGlyphImageStream(localList2));
/*     */   }
/*     */ 
/*     */   protected List<XRGlyphCacheEntry>[] seperateGlyphTypes(List<XRGlyphCacheEntry> paramList, boolean paramBoolean)
/*     */   {
/* 187 */     ArrayList localArrayList1 = null;
/* 188 */     ArrayList localArrayList2 = null;
/*     */ 
/* 190 */     for (XRGlyphCacheEntry localXRGlyphCacheEntry : paramList) {
/* 191 */       if (localXRGlyphCacheEntry.isGrayscale(paramBoolean)) {
/* 192 */         if (localArrayList2 == null) {
/* 193 */           localArrayList2 = new ArrayList(paramList.size());
/*     */         }
/* 195 */         localXRGlyphCacheEntry.setGlyphSet(this.grayGlyphSet);
/* 196 */         localArrayList2.add(localXRGlyphCacheEntry);
/*     */       } else {
/* 198 */         if (localArrayList1 == null) {
/* 199 */           localArrayList1 = new ArrayList(paramList.size());
/*     */         }
/* 201 */         localXRGlyphCacheEntry.setGlyphSet(this.lcdGlyphSet);
/* 202 */         localArrayList1.add(localXRGlyphCacheEntry);
/*     */       }
/*     */     }
/*     */ 
/* 206 */     return new List[] { localArrayList2, localArrayList1 };
/*     */   }
/*     */ 
/*     */   protected byte[] generateGlyphImageStream(List<XRGlyphCacheEntry> paramList)
/*     */   {
/* 213 */     boolean bool = ((XRGlyphCacheEntry)paramList.get(0)).getGlyphSet() == this.lcdGlyphSet;
/*     */ 
/* 215 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream((bool ? 4 : 1) * 48 * paramList.size());
/* 216 */     for (XRGlyphCacheEntry localXRGlyphCacheEntry : paramList) {
/* 217 */       localXRGlyphCacheEntry.writePixelData(localByteArrayOutputStream, bool);
/*     */     }
/*     */ 
/* 220 */     return localByteArrayOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   protected boolean containsLCDGlyphs(List<XRGlyphCacheEntry> paramList) {
/* 224 */     int i = 0;
/*     */ 
/* 226 */     for (XRGlyphCacheEntry localXRGlyphCacheEntry : paramList) {
/* 227 */       i = localXRGlyphCacheEntry.getSourceRowBytes() != localXRGlyphCacheEntry.getWidth() ? 1 : 0;
/*     */ 
/* 229 */       if (i != 0) {
/* 230 */         return true;
/*     */       }
/*     */     }
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   protected void clearCache(XRGlyphCacheEntry[] paramArrayOfXRGlyphCacheEntry)
/*     */   {
/* 242 */     ArrayList localArrayList = new ArrayList(this.cacheMap.values());
/* 243 */     Collections.sort(localArrayList, new Comparator() {
/*     */       public int compare(XRGlyphCacheEntry paramAnonymousXRGlyphCacheEntry1, XRGlyphCacheEntry paramAnonymousXRGlyphCacheEntry2) {
/* 245 */         return paramAnonymousXRGlyphCacheEntry2.getLastUsed() - paramAnonymousXRGlyphCacheEntry1.getLastUsed();
/*     */       }
/*     */     });
/*     */     XRGlyphCacheEntry localXRGlyphCacheEntry1;
/* 249 */     for (localXRGlyphCacheEntry1 : paramArrayOfXRGlyphCacheEntry) {
/* 250 */       localXRGlyphCacheEntry1.setPinned();
/*     */     }
/*     */ 
/* 253 */     ??? = new GrowableIntArray(1, 10);
/* 254 */     ??? = this.cachedPixels - 100000;
/*     */ 
/* 256 */     for (??? = localArrayList.size() - 1; (??? >= 0) && (??? > 0); ???--) {
/* 257 */       localXRGlyphCacheEntry1 = (XRGlyphCacheEntry)localArrayList.get(???);
/*     */ 
/* 259 */       if (!localXRGlyphCacheEntry1.isPinned()) {
/* 260 */         ??? -= localXRGlyphCacheEntry1.getPixelCnt();
/* 261 */         ((GrowableIntArray)???).addInt(localXRGlyphCacheEntry1.getGlyphID());
/*     */       }
/*     */     }
/*     */ 
/* 265 */     for (XRGlyphCacheEntry localXRGlyphCacheEntry2 : paramArrayOfXRGlyphCacheEntry) {
/* 266 */       localXRGlyphCacheEntry2.setUnpinned();
/*     */     }
/*     */ 
/* 269 */     freeGlyphs((GrowableIntArray)???);
/*     */   }
/*     */ 
/*     */   private void freeGlyphs(GrowableIntArray paramGrowableIntArray) {
/* 273 */     GrowableIntArray localGrowableIntArray1 = new GrowableIntArray(1, 10);
/* 274 */     GrowableIntArray localGrowableIntArray2 = new GrowableIntArray(1, 10);
/*     */ 
/* 276 */     for (int i = 0; i < paramGrowableIntArray.getSize(); i++) {
/* 277 */       int j = paramGrowableIntArray.getInt(i);
/* 278 */       this.freeGlyphIDs.add(Integer.valueOf(j));
/*     */ 
/* 280 */       this.tmp.setValue(j);
/* 281 */       XRGlyphCacheEntry localXRGlyphCacheEntry = (XRGlyphCacheEntry)this.cacheMap.get(this.tmp);
/* 282 */       this.cachedPixels -= localXRGlyphCacheEntry.getPixelCnt();
/* 283 */       this.cacheMap.remove(this.tmp);
/*     */ 
/* 285 */       if (localXRGlyphCacheEntry.getGlyphSet() == this.grayGlyphSet)
/* 286 */         localGrowableIntArray2.addInt(j);
/*     */       else {
/* 288 */         localGrowableIntArray1.addInt(j);
/*     */       }
/*     */ 
/* 291 */       localXRGlyphCacheEntry.setGlyphID(0);
/*     */     }
/*     */ 
/* 294 */     if (localGrowableIntArray2.getSize() > 0) {
/* 295 */       this.con.XRenderFreeGlyphs(this.grayGlyphSet, localGrowableIntArray2.getSizedArray());
/*     */     }
/*     */ 
/* 298 */     if (localGrowableIntArray1.getSize() > 0)
/* 299 */       this.con.XRenderFreeGlyphs(this.lcdGlyphSet, localGrowableIntArray1.getSizedArray());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.font.XRGlyphCache
 * JD-Core Version:    0.6.2
 */