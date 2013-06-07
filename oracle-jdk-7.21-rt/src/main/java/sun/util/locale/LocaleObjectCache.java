/*     */ package sun.util.locale;
/*     */ 
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public abstract class LocaleObjectCache<K, V>
/*     */ {
/*     */   private ConcurrentMap<K, CacheEntry<K, V>> map;
/*  41 */   private ReferenceQueue<V> queue = new ReferenceQueue();
/*     */ 
/*     */   public LocaleObjectCache() {
/*  44 */     this(16, 0.75F, 16);
/*     */   }
/*     */ 
/*     */   public LocaleObjectCache(int paramInt1, float paramFloat, int paramInt2) {
/*  48 */     this.map = new ConcurrentHashMap(paramInt1, paramFloat, paramInt2);
/*     */   }
/*     */ 
/*     */   public V get(K paramK) {
/*  52 */     Object localObject1 = null;
/*     */ 
/*  54 */     cleanStaleEntries();
/*  55 */     CacheEntry localCacheEntry1 = (CacheEntry)this.map.get(paramK);
/*  56 */     if (localCacheEntry1 != null) {
/*  57 */       localObject1 = localCacheEntry1.get();
/*     */     }
/*  59 */     if (localObject1 == null) {
/*  60 */       paramK = normalizeKey(paramK);
/*  61 */       Object localObject2 = createObject(paramK);
/*  62 */       if ((paramK == null) || (localObject2 == null))
/*     */       {
/*  64 */         return null;
/*     */       }
/*     */ 
/*  67 */       CacheEntry localCacheEntry2 = new CacheEntry(paramK, localObject2, this.queue);
/*     */ 
/*  69 */       localCacheEntry1 = (CacheEntry)this.map.putIfAbsent(paramK, localCacheEntry2);
/*  70 */       if (localCacheEntry1 == null) {
/*  71 */         localObject1 = localObject2;
/*     */       } else {
/*  73 */         localObject1 = localCacheEntry1.get();
/*  74 */         if (localObject1 == null) {
/*  75 */           this.map.put(paramK, localCacheEntry2);
/*  76 */           localObject1 = localObject2;
/*     */         }
/*     */       }
/*     */     }
/*  80 */     return localObject1;
/*     */   }
/*     */ 
/*     */   protected V put(K paramK, V paramV) {
/*  84 */     CacheEntry localCacheEntry1 = new CacheEntry(paramK, paramV, this.queue);
/*  85 */     CacheEntry localCacheEntry2 = (CacheEntry)this.map.put(paramK, localCacheEntry1);
/*  86 */     return localCacheEntry2 == null ? null : localCacheEntry2.get();
/*     */   }
/*     */ 
/*     */   private void cleanStaleEntries()
/*     */   {
/*     */     CacheEntry localCacheEntry;
/*  92 */     while ((localCacheEntry = (CacheEntry)this.queue.poll()) != null)
/*  93 */       this.map.remove(localCacheEntry.getKey());
/*     */   }
/*     */ 
/*     */   protected abstract V createObject(K paramK);
/*     */ 
/*     */   protected K normalizeKey(K paramK)
/*     */   {
/* 100 */     return paramK;
/*     */   }
/*     */ 
/*     */   private static class CacheEntry<K, V> extends SoftReference<V> {
/*     */     private K key;
/*     */ 
/*     */     CacheEntry(K paramK, V paramV, ReferenceQueue<V> paramReferenceQueue) {
/* 107 */       super(paramReferenceQueue);
/* 108 */       this.key = paramK;
/*     */     }
/*     */ 
/*     */     K getKey() {
/* 112 */       return this.key;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.util.locale.LocaleObjectCache
 * JD-Core Version:    0.6.2
 */