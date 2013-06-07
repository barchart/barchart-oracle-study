/*      */ package java.util;
/*      */ 
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.security.AccessController;
/*      */ import sun.misc.Hashing;
/*      */ import sun.misc.VM;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class WeakHashMap<K, V> extends AbstractMap<K, V>
/*      */   implements Map<K, V>
/*      */ {
/*      */   private static final int DEFAULT_INITIAL_CAPACITY = 16;
/*      */   private static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*      */   Entry<K, V>[] table;
/*      */   private int size;
/*      */   private int threshold;
/*      */   private final float loadFactor;
/*  174 */   private final ReferenceQueue<Object> queue = new ReferenceQueue();
/*      */   int modCount;
/*      */   static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = 2147483647;
/*      */   transient boolean useAltHashing;
/*  245 */   final transient int hashSeed = Hashing.randomHashSeed(this);
/*      */ 
/*  322 */   private static final Object NULL_KEY = new Object();
/*      */ 
/*  926 */   private transient Set<Map.Entry<K, V>> entrySet = null;
/*      */ 
/*      */   private Entry<K, V>[] newTable(int paramInt)
/*      */   {
/*  249 */     return (Entry[])new Entry[paramInt];
/*      */   }
/*      */ 
/*      */   public WeakHashMap(int paramInt, float paramFloat)
/*      */   {
/*  262 */     if (paramInt < 0) {
/*  263 */       throw new IllegalArgumentException("Illegal Initial Capacity: " + paramInt);
/*      */     }
/*  265 */     if (paramInt > 1073741824) {
/*  266 */       paramInt = 1073741824;
/*      */     }
/*  268 */     if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
/*  269 */       throw new IllegalArgumentException("Illegal Load factor: " + paramFloat);
/*      */     }
/*  271 */     int i = 1;
/*  272 */     while (i < paramInt)
/*  273 */       i <<= 1;
/*  274 */     this.table = newTable(i);
/*  275 */     this.loadFactor = paramFloat;
/*  276 */     this.threshold = ((int)(i * paramFloat));
/*  277 */     this.useAltHashing = ((VM.isBooted()) && (i >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */   }
/*      */ 
/*      */   public WeakHashMap(int paramInt)
/*      */   {
/*  289 */     this(paramInt, 0.75F);
/*      */   }
/*      */ 
/*      */   public WeakHashMap()
/*      */   {
/*  297 */     this(16, 0.75F);
/*      */   }
/*      */ 
/*      */   public WeakHashMap(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  311 */     this(Math.max((int)(paramMap.size() / 0.75F) + 1, 16), 0.75F);
/*      */ 
/*  314 */     putAll(paramMap);
/*      */   }
/*      */ 
/*      */   private static Object maskNull(Object paramObject)
/*      */   {
/*  328 */     return paramObject == null ? NULL_KEY : paramObject;
/*      */   }
/*      */ 
/*      */   static Object unmaskNull(Object paramObject)
/*      */   {
/*  335 */     return paramObject == NULL_KEY ? null : paramObject;
/*      */   }
/*      */ 
/*      */   private static boolean eq(Object paramObject1, Object paramObject2)
/*      */   {
/*  343 */     return (paramObject1 == paramObject2) || (paramObject1.equals(paramObject2));
/*      */   }
/*      */ 
/*      */   int hash(Object paramObject)
/*      */   {
/*      */     int i;
/*  356 */     if (this.useAltHashing) {
/*  357 */       i = this.hashSeed;
/*  358 */       if ((paramObject instanceof String)) {
/*  359 */         return Hashing.stringHash32((String)paramObject);
/*      */       }
/*  361 */       i ^= paramObject.hashCode();
/*      */     }
/*      */     else {
/*  364 */       i = paramObject.hashCode();
/*      */     }
/*      */ 
/*  370 */     i ^= i >>> 20 ^ i >>> 12;
/*  371 */     return i ^ i >>> 7 ^ i >>> 4;
/*      */   }
/*      */ 
/*      */   private static int indexFor(int paramInt1, int paramInt2)
/*      */   {
/*  378 */     return paramInt1 & paramInt2 - 1;
/*      */   }
/*      */ 
/*      */   private void expungeStaleEntries()
/*      */   {
/*      */     Reference localReference;
/*  385 */     while ((localReference = this.queue.poll()) != null)
/*  386 */       synchronized (this.queue)
/*      */       {
/*  388 */         Entry localEntry1 = (Entry)localReference;
/*  389 */         int i = indexFor(localEntry1.hash, this.table.length);
/*      */ 
/*  391 */         Object localObject1 = this.table[i];
/*  392 */         Object localObject2 = localObject1;
/*  393 */         while (localObject2 != null) {
/*  394 */           Entry localEntry2 = localObject2.next;
/*  395 */           if (localObject2 == localEntry1) {
/*  396 */             if (localObject1 == localEntry1)
/*  397 */               this.table[i] = localEntry2;
/*      */             else {
/*  399 */               ((Entry)localObject1).next = localEntry2;
/*      */             }
/*      */ 
/*  402 */             localEntry1.value = null;
/*  403 */             this.size -= 1;
/*  404 */             break;
/*      */           }
/*  406 */           localObject1 = localObject2;
/*  407 */           localObject2 = localEntry2;
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   private Entry<K, V>[] getTable()
/*      */   {
/*  417 */     expungeStaleEntries();
/*  418 */     return this.table;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  428 */     if (this.size == 0)
/*  429 */       return 0;
/*  430 */     expungeStaleEntries();
/*  431 */     return this.size;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  441 */     return size() == 0;
/*      */   }
/*      */ 
/*      */   public V get(Object paramObject)
/*      */   {
/*  462 */     Object localObject = maskNull(paramObject);
/*  463 */     int i = hash(localObject);
/*  464 */     Entry[] arrayOfEntry = getTable();
/*  465 */     int j = indexFor(i, arrayOfEntry.length);
/*  466 */     Entry localEntry = arrayOfEntry[j];
/*  467 */     while (localEntry != null) {
/*  468 */       if ((localEntry.hash == i) && (eq(localObject, localEntry.get())))
/*  469 */         return localEntry.value;
/*  470 */       localEntry = localEntry.next;
/*      */     }
/*  472 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(Object paramObject)
/*      */   {
/*  484 */     return getEntry(paramObject) != null;
/*      */   }
/*      */ 
/*      */   Entry<K, V> getEntry(Object paramObject)
/*      */   {
/*  492 */     Object localObject = maskNull(paramObject);
/*  493 */     int i = hash(localObject);
/*  494 */     Entry[] arrayOfEntry = getTable();
/*  495 */     int j = indexFor(i, arrayOfEntry.length);
/*  496 */     Entry localEntry = arrayOfEntry[j];
/*  497 */     while ((localEntry != null) && ((localEntry.hash != i) || (!eq(localObject, localEntry.get()))))
/*  498 */       localEntry = localEntry.next;
/*  499 */     return localEntry;
/*      */   }
/*      */ 
/*      */   public V put(K paramK, V paramV)
/*      */   {
/*  515 */     Object localObject1 = maskNull(paramK);
/*  516 */     int i = hash(localObject1);
/*  517 */     Entry[] arrayOfEntry = getTable();
/*  518 */     int j = indexFor(i, arrayOfEntry.length);
/*      */ 
/*  520 */     for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
/*  521 */       if ((i == localEntry.hash) && (eq(localObject1, localEntry.get()))) {
/*  522 */         Object localObject2 = localEntry.value;
/*  523 */         if (paramV != localObject2)
/*  524 */           localEntry.value = paramV;
/*  525 */         return localObject2;
/*      */       }
/*      */     }
/*      */ 
/*  529 */     this.modCount += 1;
/*  530 */     localEntry = arrayOfEntry[j];
/*  531 */     arrayOfEntry[j] = new Entry(localObject1, paramV, this.queue, i, localEntry);
/*  532 */     if (++this.size >= this.threshold)
/*  533 */       resize(arrayOfEntry.length * 2);
/*  534 */     return null;
/*      */   }
/*      */ 
/*      */   void resize(int paramInt)
/*      */   {
/*  552 */     Entry[] arrayOfEntry1 = getTable();
/*  553 */     int i = arrayOfEntry1.length;
/*  554 */     if (i == 1073741824) {
/*  555 */       this.threshold = 2147483647;
/*  556 */       return;
/*      */     }
/*      */ 
/*  559 */     Entry[] arrayOfEntry2 = newTable(paramInt);
/*  560 */     boolean bool1 = this.useAltHashing;
/*  561 */     this.useAltHashing |= ((VM.isBooted()) && (paramInt >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/*  563 */     boolean bool2 = bool1 ^ this.useAltHashing;
/*  564 */     transfer(arrayOfEntry1, arrayOfEntry2, bool2);
/*  565 */     this.table = arrayOfEntry2;
/*      */ 
/*  572 */     if (this.size >= this.threshold / 2) {
/*  573 */       this.threshold = ((int)(paramInt * this.loadFactor));
/*      */     } else {
/*  575 */       expungeStaleEntries();
/*  576 */       transfer(arrayOfEntry2, arrayOfEntry1, false);
/*  577 */       this.table = arrayOfEntry1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void transfer(Entry<K, V>[] paramArrayOfEntry1, Entry<K, V>[] paramArrayOfEntry2, boolean paramBoolean)
/*      */   {
/*  583 */     for (int i = 0; i < paramArrayOfEntry1.length; i++) {
/*  584 */       Object localObject1 = paramArrayOfEntry1[i];
/*  585 */       paramArrayOfEntry1[i] = null;
/*  586 */       while (localObject1 != null) {
/*  587 */         Entry localEntry = ((Entry)localObject1).next;
/*  588 */         Object localObject2 = ((Entry)localObject1).get();
/*  589 */         if (localObject2 == null) {
/*  590 */           ((Entry)localObject1).next = null;
/*  591 */           ((Entry)localObject1).value = null;
/*  592 */           this.size -= 1;
/*      */         } else {
/*  594 */           if (paramBoolean) {
/*  595 */             ((Entry)localObject1).hash = hash(localObject2);
/*      */           }
/*  597 */           int j = indexFor(((Entry)localObject1).hash, paramArrayOfEntry2.length);
/*  598 */           ((Entry)localObject1).next = paramArrayOfEntry2[j];
/*  599 */           paramArrayOfEntry2[j] = localObject1;
/*      */         }
/*  601 */         localObject1 = localEntry;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void putAll(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  615 */     int i = paramMap.size();
/*  616 */     if (i == 0) {
/*  617 */       return;
/*      */     }
/*      */ 
/*  628 */     if (i > this.threshold) {
/*  629 */       int j = (int)(i / this.loadFactor + 1.0F);
/*  630 */       if (j > 1073741824)
/*  631 */         j = 1073741824;
/*  632 */       int k = this.table.length;
/*  633 */       while (k < j)
/*  634 */         k <<= 1;
/*  635 */       if (k > this.table.length) {
/*  636 */         resize(k);
/*      */       }
/*      */     }
/*  639 */     for (Map.Entry localEntry : paramMap.entrySet())
/*  640 */       put(localEntry.getKey(), localEntry.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(Object paramObject)
/*      */   {
/*  664 */     Object localObject1 = maskNull(paramObject);
/*  665 */     int i = hash(localObject1);
/*  666 */     Entry[] arrayOfEntry = getTable();
/*  667 */     int j = indexFor(i, arrayOfEntry.length);
/*  668 */     Object localObject2 = arrayOfEntry[j];
/*  669 */     Object localObject3 = localObject2;
/*      */ 
/*  671 */     while (localObject3 != null) {
/*  672 */       Entry localEntry = localObject3.next;
/*  673 */       if ((i == localObject3.hash) && (eq(localObject1, localObject3.get()))) {
/*  674 */         this.modCount += 1;
/*  675 */         this.size -= 1;
/*  676 */         if (localObject2 == localObject3)
/*  677 */           arrayOfEntry[j] = localEntry;
/*      */         else
/*  679 */           ((Entry)localObject2).next = localEntry;
/*  680 */         return localObject3.value;
/*      */       }
/*  682 */       localObject2 = localObject3;
/*  683 */       localObject3 = localEntry;
/*      */     }
/*      */ 
/*  686 */     return null;
/*      */   }
/*      */ 
/*      */   boolean removeMapping(Object paramObject)
/*      */   {
/*  691 */     if (!(paramObject instanceof Map.Entry))
/*  692 */       return false;
/*  693 */     Entry[] arrayOfEntry = getTable();
/*  694 */     Map.Entry localEntry = (Map.Entry)paramObject;
/*  695 */     Object localObject1 = maskNull(localEntry.getKey());
/*  696 */     int i = hash(localObject1);
/*  697 */     int j = indexFor(i, arrayOfEntry.length);
/*  698 */     Object localObject2 = arrayOfEntry[j];
/*  699 */     Object localObject3 = localObject2;
/*      */ 
/*  701 */     while (localObject3 != null) {
/*  702 */       Entry localEntry1 = localObject3.next;
/*  703 */       if ((i == localObject3.hash) && (localObject3.equals(localEntry))) {
/*  704 */         this.modCount += 1;
/*  705 */         this.size -= 1;
/*  706 */         if (localObject2 == localObject3)
/*  707 */           arrayOfEntry[j] = localEntry1;
/*      */         else
/*  709 */           ((Entry)localObject2).next = localEntry1;
/*  710 */         return true;
/*      */       }
/*  712 */       localObject2 = localObject3;
/*  713 */       localObject3 = localEntry1;
/*      */     }
/*      */ 
/*  716 */     return false;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  726 */     while (this.queue.poll() != null);
/*  729 */     this.modCount += 1;
/*  730 */     Arrays.fill(this.table, null);
/*  731 */     this.size = 0;
/*      */ 
/*  736 */     while (this.queue.poll() != null);
/*      */   }
/*      */ 
/*      */   public boolean containsValue(Object paramObject)
/*      */   {
/*  749 */     if (paramObject == null) {
/*  750 */       return containsNullValue();
/*      */     }
/*  752 */     Entry[] arrayOfEntry = getTable();
/*  753 */     for (int i = arrayOfEntry.length; i-- > 0; )
/*  754 */       for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next)
/*  755 */         if (paramObject.equals(localEntry.value))
/*  756 */           return true;
/*  757 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean containsNullValue()
/*      */   {
/*  764 */     Entry[] arrayOfEntry = getTable();
/*  765 */     for (int i = arrayOfEntry.length; i-- > 0; )
/*  766 */       for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next)
/*  767 */         if (localEntry.value == null)
/*  768 */           return true;
/*  769 */     return false;
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/*  942 */     Set localSet = this.keySet;
/*  943 */     return this.keySet = new KeySet(null);
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/*  987 */     Collection localCollection = this.values;
/*  988 */     return this.values = new Values(null);
/*      */   }
/*      */ 
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/* 1024 */     Set localSet = this.entrySet;
/* 1025 */     return this.entrySet = new EntrySet(null);
/*      */   }
/*      */ 
/*      */   private static class Entry<K, V> extends WeakReference<Object>
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     V value;
/*      */     int hash;
/*      */     Entry<K, V> next;
/*      */ 
/*      */     Entry(Object paramObject, V paramV, ReferenceQueue<Object> paramReferenceQueue, int paramInt, Entry<K, V> paramEntry)
/*      */     {
/*  787 */       super(paramReferenceQueue);
/*  788 */       this.value = paramV;
/*  789 */       this.hash = paramInt;
/*  790 */       this.next = paramEntry;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/*  795 */       return WeakHashMap.unmaskNull(get());
/*      */     }
/*      */ 
/*      */     public V getValue() {
/*  799 */       return this.value;
/*      */     }
/*      */ 
/*      */     public V setValue(V paramV) {
/*  803 */       Object localObject = this.value;
/*  804 */       this.value = paramV;
/*  805 */       return localObject;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/*  809 */       if (!(paramObject instanceof Map.Entry))
/*  810 */         return false;
/*  811 */       Map.Entry localEntry = (Map.Entry)paramObject;
/*  812 */       Object localObject1 = getKey();
/*  813 */       Object localObject2 = localEntry.getKey();
/*  814 */       if ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2)))) {
/*  815 */         Object localObject3 = getValue();
/*  816 */         Object localObject4 = localEntry.getValue();
/*  817 */         if ((localObject3 == localObject4) || ((localObject3 != null) && (localObject3.equals(localObject4))))
/*  818 */           return true;
/*      */       }
/*  820 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  824 */       Object localObject1 = getKey();
/*  825 */       Object localObject2 = getValue();
/*  826 */       return (localObject1 == null ? 0 : localObject1.hashCode()) ^ (localObject2 == null ? 0 : localObject2.hashCode());
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  831 */       return getKey() + "=" + getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class EntryIterator extends WeakHashMap<K, V>.HashIterator<Map.Entry<K, V>>
/*      */   {
/*      */     private EntryIterator()
/*      */     {
/*  918 */       super();
/*      */     }
/*  920 */     public Map.Entry<K, V> next() { return nextEntry(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class EntrySet extends AbstractSet<Map.Entry<K, V>>
/*      */   {
/*      */     private EntrySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, V>> iterator()
/*      */     {
/* 1030 */       return new WeakHashMap.EntryIterator(WeakHashMap.this, null);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/* 1034 */       if (!(paramObject instanceof Map.Entry))
/* 1035 */         return false;
/* 1036 */       Map.Entry localEntry = (Map.Entry)paramObject;
/* 1037 */       WeakHashMap.Entry localEntry1 = WeakHashMap.this.getEntry(localEntry.getKey());
/* 1038 */       return (localEntry1 != null) && (localEntry1.equals(localEntry));
/*      */     }
/*      */ 
/*      */     public boolean remove(Object paramObject) {
/* 1042 */       return WeakHashMap.this.removeMapping(paramObject);
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1046 */       return WeakHashMap.this.size();
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1050 */       WeakHashMap.this.clear();
/*      */     }
/*      */ 
/*      */     private List<Map.Entry<K, V>> deepCopy() {
/* 1054 */       ArrayList localArrayList = new ArrayList(size());
/* 1055 */       for (Map.Entry localEntry : this)
/* 1056 */         localArrayList.add(new AbstractMap.SimpleEntry(localEntry));
/* 1057 */       return localArrayList;
/*      */     }
/*      */ 
/*      */     public Object[] toArray() {
/* 1061 */       return deepCopy().toArray();
/*      */     }
/*      */ 
/*      */     public <T> T[] toArray(T[] paramArrayOfT) {
/* 1065 */       return deepCopy().toArray(paramArrayOfT);
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class HashIterator<T>
/*      */     implements Iterator<T>
/*      */   {
/*      */     private int index;
/*  837 */     private WeakHashMap.Entry<K, V> entry = null;
/*  838 */     private WeakHashMap.Entry<K, V> lastReturned = null;
/*  839 */     private int expectedModCount = WeakHashMap.this.modCount;
/*      */ 
/*  845 */     private Object nextKey = null;
/*      */ 
/*  851 */     private Object currentKey = null;
/*      */ 
/*      */     HashIterator() {
/*  854 */       this.index = (WeakHashMap.this.isEmpty() ? 0 : WeakHashMap.this.table.length);
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/*  858 */       WeakHashMap.Entry[] arrayOfEntry = WeakHashMap.this.table;
/*      */ 
/*  860 */       while (this.nextKey == null) {
/*  861 */         WeakHashMap.Entry localEntry = this.entry;
/*  862 */         int i = this.index;
/*  863 */         while ((localEntry == null) && (i > 0))
/*  864 */           localEntry = arrayOfEntry[(--i)];
/*  865 */         this.entry = localEntry;
/*  866 */         this.index = i;
/*  867 */         if (localEntry == null) {
/*  868 */           this.currentKey = null;
/*  869 */           return false;
/*      */         }
/*  871 */         this.nextKey = localEntry.get();
/*  872 */         if (this.nextKey == null)
/*  873 */           this.entry = this.entry.next;
/*      */       }
/*  875 */       return true;
/*      */     }
/*      */ 
/*      */     protected WeakHashMap.Entry<K, V> nextEntry()
/*      */     {
/*  880 */       if (WeakHashMap.this.modCount != this.expectedModCount)
/*  881 */         throw new ConcurrentModificationException();
/*  882 */       if ((this.nextKey == null) && (!hasNext())) {
/*  883 */         throw new NoSuchElementException();
/*      */       }
/*  885 */       this.lastReturned = this.entry;
/*  886 */       this.entry = this.entry.next;
/*  887 */       this.currentKey = this.nextKey;
/*  888 */       this.nextKey = null;
/*  889 */       return this.lastReturned;
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  893 */       if (this.lastReturned == null)
/*  894 */         throw new IllegalStateException();
/*  895 */       if (WeakHashMap.this.modCount != this.expectedModCount) {
/*  896 */         throw new ConcurrentModificationException();
/*      */       }
/*  898 */       WeakHashMap.this.remove(this.currentKey);
/*  899 */       this.expectedModCount = WeakHashMap.this.modCount;
/*  900 */       this.lastReturned = null;
/*  901 */       this.currentKey = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Holder
/*      */   {
/*  231 */     static final int ALTERNATIVE_HASHING_THRESHOLD = i;
/*      */ 
/*      */     static
/*      */     {
/*  210 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.map.althashing.threshold"));
/*      */       int i;
/*      */       try
/*      */       {
/*  216 */         i = null != str ? Integer.parseInt(str) : 2147483647;
/*      */ 
/*  221 */         if (i == -1) {
/*  222 */           i = 2147483647;
/*      */         }
/*      */ 
/*  225 */         if (i < 0)
/*  226 */           throw new IllegalArgumentException("value must be positive integer.");
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException) {
/*  229 */         throw new Error("Illegal value for 'jdk.map.althashing.threshold'", localIllegalArgumentException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class KeyIterator extends WeakHashMap<K, V>.HashIterator<K>
/*      */   {
/*      */     private KeyIterator()
/*      */     {
/*  912 */       super();
/*      */     }
/*  914 */     public K next() { return nextEntry().getKey(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class KeySet extends AbstractSet<K>
/*      */   {
/*      */     private KeySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator()
/*      */     {
/*  948 */       return new WeakHashMap.KeyIterator(WeakHashMap.this, null);
/*      */     }
/*      */ 
/*      */     public int size() {
/*  952 */       return WeakHashMap.this.size();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/*  956 */       return WeakHashMap.this.containsKey(paramObject);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object paramObject) {
/*  960 */       if (WeakHashMap.this.containsKey(paramObject)) {
/*  961 */         WeakHashMap.this.remove(paramObject);
/*  962 */         return true;
/*      */       }
/*      */ 
/*  965 */       return false;
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  969 */       WeakHashMap.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ValueIterator extends WeakHashMap<K, V>.HashIterator<V>
/*      */   {
/*      */     private ValueIterator()
/*      */     {
/*  906 */       super();
/*      */     }
/*  908 */     public V next() { return nextEntry().value; }
/*      */ 
/*      */   }
/*      */ 
/*      */   private class Values extends AbstractCollection<V>
/*      */   {
/*      */     private Values()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/*  993 */       return new WeakHashMap.ValueIterator(WeakHashMap.this, null);
/*      */     }
/*      */ 
/*      */     public int size() {
/*  997 */       return WeakHashMap.this.size();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/* 1001 */       return WeakHashMap.this.containsValue(paramObject);
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1005 */       WeakHashMap.this.clear();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.WeakHashMap
 * JD-Core Version:    0.6.2
 */