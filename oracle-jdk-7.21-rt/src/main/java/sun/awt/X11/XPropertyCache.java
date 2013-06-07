/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public class XPropertyCache
/*     */ {
/*  78 */   private static Map<Long, Map<XAtom, PropertyCacheEntry>> windowToMap = new HashMap();
/*     */ 
/*     */   public static boolean isCached(long paramLong, XAtom paramXAtom) {
/*  81 */     Map localMap = (Map)windowToMap.get(Long.valueOf(paramLong));
/*  82 */     if (localMap != null) {
/*  83 */       return localMap.containsKey(paramXAtom);
/*     */     }
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   public static PropertyCacheEntry getCacheEntry(long paramLong, XAtom paramXAtom)
/*     */   {
/*  90 */     Map localMap = (Map)windowToMap.get(Long.valueOf(paramLong));
/*  91 */     if (localMap != null) {
/*  92 */       return (PropertyCacheEntry)localMap.get(paramXAtom);
/*     */     }
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   public static void storeCache(PropertyCacheEntry paramPropertyCacheEntry, long paramLong, XAtom paramXAtom)
/*     */   {
/*  99 */     Object localObject = (Map)windowToMap.get(Long.valueOf(paramLong));
/* 100 */     if (localObject == null) {
/* 101 */       localObject = new HashMap();
/* 102 */       windowToMap.put(Long.valueOf(paramLong), localObject);
/*     */     }
/* 104 */     ((Map)localObject).put(paramXAtom, paramPropertyCacheEntry);
/*     */   }
/*     */ 
/*     */   public static void clearCache(long paramLong) {
/* 108 */     windowToMap.remove(Long.valueOf(paramLong));
/*     */   }
/*     */ 
/*     */   public static void clearCache(long paramLong, XAtom paramXAtom) {
/* 112 */     Map localMap = (Map)windowToMap.get(Long.valueOf(paramLong));
/* 113 */     if (localMap != null)
/* 114 */       localMap.remove(paramXAtom);
/*     */   }
/*     */ 
/*     */   public static boolean isCachingSupported()
/*     */   {
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */   static class PropertyCacheEntry
/*     */   {
/*     */     private final int format;
/*     */     private final int numberOfItems;
/*     */     private final long bytesAfter;
/*     */     private final long data;
/*     */     private final int dataLength;
/*     */ 
/*     */     public PropertyCacheEntry(int paramInt1, int paramInt2, long paramLong1, long paramLong2, int paramInt3)
/*     */     {
/*  49 */       this.format = paramInt1;
/*  50 */       this.numberOfItems = paramInt2;
/*  51 */       this.bytesAfter = paramLong1;
/*  52 */       this.data = XlibWrapper.unsafe.allocateMemory(paramInt3);
/*  53 */       this.dataLength = paramInt3;
/*  54 */       XlibWrapper.memcpy(this.data, paramLong2, paramInt3);
/*     */     }
/*     */ 
/*     */     public int getFormat() {
/*  58 */       return this.format;
/*     */     }
/*     */ 
/*     */     public int getNumberOfItems() {
/*  62 */       return this.numberOfItems;
/*     */     }
/*     */ 
/*     */     public long getBytesAfter() {
/*  66 */       return this.bytesAfter;
/*     */     }
/*     */ 
/*     */     public long getData() {
/*  70 */       return this.data;
/*     */     }
/*     */ 
/*     */     public int getDataLength() {
/*  74 */       return this.dataLength;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XPropertyCache
 * JD-Core Version:    0.6.2
 */