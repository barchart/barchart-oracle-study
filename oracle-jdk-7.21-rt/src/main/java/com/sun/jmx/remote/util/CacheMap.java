/*     */ package com.sun.jmx.remote.util;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Util;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public class CacheMap<K, V> extends WeakHashMap<K, V>
/*     */ {
/* 118 */   private final LinkedList<SoftReference<K>> cache = new LinkedList();
/*     */   private final int nSoftReferences;
/*     */ 
/*     */   public CacheMap(int paramInt)
/*     */   {
/*  60 */     if (paramInt < 0) {
/*  61 */       throw new IllegalArgumentException("nSoftReferences = " + paramInt);
/*     */     }
/*     */ 
/*  64 */     this.nSoftReferences = paramInt;
/*     */   }
/*     */ 
/*     */   public V put(K paramK, V paramV) {
/*  68 */     cache(paramK);
/*  69 */     return super.put(paramK, paramV);
/*     */   }
/*     */ 
/*     */   public V get(Object paramObject) {
/*  73 */     cache(Util.cast(paramObject));
/*  74 */     return super.get(paramObject);
/*     */   }
/*     */ 
/*     */   private void cache(K paramK)
/*     */   {
/*  86 */     Iterator localIterator = this.cache.iterator();
/*  87 */     while (localIterator.hasNext()) {
/*  88 */       SoftReference localSoftReference = (SoftReference)localIterator.next();
/*  89 */       Object localObject = localSoftReference.get();
/*  90 */       if (localObject == null) {
/*  91 */         localIterator.remove();
/*  92 */       } else if (paramK.equals(localObject))
/*     */       {
/*  94 */         localIterator.remove();
/*  95 */         this.cache.add(0, localSoftReference);
/*  96 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 100 */     int i = this.cache.size();
/* 101 */     if (i == this.nSoftReferences) {
/* 102 */       if (i == 0)
/* 103 */         return;
/* 104 */       localIterator.remove();
/*     */     }
/*     */ 
/* 107 */     this.cache.add(0, new SoftReference(paramK));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jmx.remote.util.CacheMap
 * JD-Core Version:    0.6.2
 */