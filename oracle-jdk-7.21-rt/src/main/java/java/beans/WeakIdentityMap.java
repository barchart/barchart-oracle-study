/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ 
/*     */ final class WeakIdentityMap<T>
/*     */ {
/*     */   private static final int MAXIMUM_CAPACITY = 1073741824;
/*  43 */   private static final Object NULL = new Object();
/*     */   private final ReferenceQueue<Object> queue;
/*     */   private Entry<T>[] table;
/*     */   private int threshold;
/*     */   private int size;
/*     */ 
/*     */   WeakIdentityMap()
/*     */   {
/*  45 */     this.queue = new ReferenceQueue();
/*     */ 
/*  47 */     this.table = newTable(8);
/*  48 */     this.threshold = 6;
/*  49 */     this.size = 0;
/*     */   }
/*     */   public T get(Object paramObject) {
/*  52 */     removeStaleEntries();
/*  53 */     if (paramObject == null) {
/*  54 */       paramObject = NULL;
/*     */     }
/*  56 */     int i = paramObject.hashCode();
/*  57 */     int j = getIndex(this.table, i);
/*  58 */     for (Entry localEntry = this.table[j]; localEntry != null; localEntry = localEntry.next) {
/*  59 */       if (localEntry.isMatched(paramObject, i)) {
/*  60 */         return localEntry.value;
/*     */       }
/*     */     }
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */   public T put(Object paramObject, T paramT) {
/*  67 */     removeStaleEntries();
/*  68 */     if (paramObject == null) {
/*  69 */       paramObject = NULL;
/*     */     }
/*  71 */     int i = paramObject.hashCode();
/*  72 */     int j = getIndex(this.table, i);
/*  73 */     for (Object localObject1 = this.table[j]; localObject1 != null; localObject1 = ((Entry)localObject1).next) {
/*  74 */       if (((Entry)localObject1).isMatched(paramObject, i)) {
/*  75 */         Object localObject2 = ((Entry)localObject1).value;
/*  76 */         ((Entry)localObject1).value = paramT;
/*  77 */         return localObject2;
/*     */       }
/*     */     }
/*  80 */     this.table[j] = new Entry(paramObject, i, paramT, this.queue, this.table[j]);
/*  81 */     if (++this.size >= this.threshold) {
/*  82 */       if (this.table.length == 1073741824) {
/*  83 */         this.threshold = 2147483647;
/*     */       }
/*     */       else {
/*  86 */         removeStaleEntries();
/*  87 */         localObject1 = newTable(this.table.length * 2);
/*  88 */         transfer(this.table, (Entry[])localObject1);
/*     */ 
/*  93 */         if (this.size >= this.threshold / 2) {
/*  94 */           this.table = ((Entry[])localObject1);
/*  95 */           this.threshold *= 2;
/*     */         }
/*     */         else {
/*  98 */           transfer((Entry[])localObject1, this.table);
/*     */         }
/*     */       }
/*     */     }
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   private void removeStaleEntries() {
/* 106 */     for (Reference localReference = this.queue.poll(); localReference != null; localReference = this.queue.poll())
/*     */     {
/* 108 */       Entry localEntry1 = (Entry)localReference;
/* 109 */       int i = getIndex(this.table, localEntry1.hash);
/*     */ 
/* 111 */       Object localObject1 = this.table[i];
/* 112 */       Object localObject2 = localObject1;
/* 113 */       while (localObject2 != null) {
/* 114 */         Entry localEntry2 = localObject2.next;
/* 115 */         if (localObject2 == localEntry1) {
/* 116 */           if (localObject1 == localEntry1) {
/* 117 */             this.table[i] = localEntry2;
/*     */           }
/*     */           else {
/* 120 */             ((Entry)localObject1).next = localEntry2;
/*     */           }
/* 122 */           localEntry1.value = null;
/* 123 */           localEntry1.next = null;
/* 124 */           this.size -= 1;
/* 125 */           break;
/*     */         }
/* 127 */         localObject1 = localObject2;
/* 128 */         localObject2 = localEntry2;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void transfer(Entry<T>[] paramArrayOfEntry1, Entry<T>[] paramArrayOfEntry2) {
/* 134 */     for (int i = 0; i < paramArrayOfEntry1.length; i++) {
/* 135 */       Object localObject1 = paramArrayOfEntry1[i];
/* 136 */       paramArrayOfEntry1[i] = null;
/* 137 */       while (localObject1 != null) {
/* 138 */         Entry localEntry = ((Entry)localObject1).next;
/* 139 */         Object localObject2 = ((Entry)localObject1).get();
/* 140 */         if (localObject2 == null) {
/* 141 */           ((Entry)localObject1).value = null;
/* 142 */           ((Entry)localObject1).next = null;
/* 143 */           this.size -= 1;
/*     */         }
/*     */         else {
/* 146 */           int j = getIndex(paramArrayOfEntry2, ((Entry)localObject1).hash);
/* 147 */           ((Entry)localObject1).next = paramArrayOfEntry2[j];
/* 148 */           paramArrayOfEntry2[j] = localObject1;
/*     */         }
/* 150 */         localObject1 = localEntry;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Entry<T>[] newTable(int paramInt)
/*     */   {
/* 158 */     return (Entry[])new Entry[paramInt];
/*     */   }
/*     */ 
/*     */   private static int getIndex(Entry<?>[] paramArrayOfEntry, int paramInt) {
/* 162 */     return paramInt & paramArrayOfEntry.length - 1;
/*     */   }
/*     */   private static class Entry<T> extends WeakReference<Object> {
/*     */     private final int hash;
/*     */     private T value;
/*     */     private Entry<T> next;
/*     */ 
/* 171 */     Entry(Object paramObject, int paramInt, T paramT, ReferenceQueue<Object> paramReferenceQueue, Entry<T> paramEntry) { super(paramReferenceQueue);
/* 172 */       this.hash = paramInt;
/* 173 */       this.value = paramT;
/* 174 */       this.next = paramEntry; }
/*     */ 
/*     */     boolean isMatched(Object paramObject, int paramInt)
/*     */     {
/* 178 */       return (this.hash == paramInt) && (paramObject == get());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.WeakIdentityMap
 * JD-Core Version:    0.6.2
 */