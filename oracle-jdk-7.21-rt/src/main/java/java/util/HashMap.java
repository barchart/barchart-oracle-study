/*      */ package java.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InvalidObjectException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.security.AccessController;
/*      */ import sun.misc.Hashing;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.misc.VM;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class HashMap<K, V> extends AbstractMap<K, V>
/*      */   implements Map<K, V>, Cloneable, Serializable
/*      */ {
/*      */   static final int DEFAULT_INITIAL_CAPACITY = 16;
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*      */   transient Entry<K, V>[] table;
/*      */   transient int size;
/*      */   int threshold;
/*      */   final float loadFactor;
/*      */   transient int modCount;
/*      */   static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = 2147483647;
/*      */   transient boolean useAltHashing;
/*  255 */   final transient int hashSeed = Hashing.randomHashSeed(this);
/*      */ 
/*  952 */   private transient Set<Map.Entry<K, V>> entrySet = null;
/*      */   private static final long serialVersionUID = 362498820763181265L;
/*      */ 
/*      */   public HashMap(int paramInt, float paramFloat)
/*      */   {
/*  267 */     if (paramInt < 0) {
/*  268 */       throw new IllegalArgumentException("Illegal initial capacity: " + paramInt);
/*      */     }
/*  270 */     if (paramInt > 1073741824)
/*  271 */       paramInt = 1073741824;
/*  272 */     if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
/*  273 */       throw new IllegalArgumentException("Illegal load factor: " + paramFloat);
/*      */     }
/*      */ 
/*  277 */     int i = 1;
/*  278 */     while (i < paramInt) {
/*  279 */       i <<= 1;
/*      */     }
/*  281 */     this.loadFactor = paramFloat;
/*  282 */     this.threshold = ((int)Math.min(i * paramFloat, 1.073742E+09F));
/*  283 */     this.table = new Entry[i];
/*  284 */     this.useAltHashing = ((VM.isBooted()) && (i >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/*  286 */     init();
/*      */   }
/*      */ 
/*      */   public HashMap(int paramInt)
/*      */   {
/*  297 */     this(paramInt, 0.75F);
/*      */   }
/*      */ 
/*      */   public HashMap()
/*      */   {
/*  305 */     this(16, 0.75F);
/*      */   }
/*      */ 
/*      */   public HashMap(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  318 */     this(Math.max((int)(paramMap.size() / 0.75F) + 1, 16), 0.75F);
/*      */ 
/*  320 */     putAllForCreate(paramMap);
/*      */   }
/*      */ 
/*      */   void init()
/*      */   {
/*      */   }
/*      */ 
/*      */   final int hash(Object paramObject)
/*      */   {
/*  343 */     int i = 0;
/*  344 */     if (this.useAltHashing) {
/*  345 */       if ((paramObject instanceof String)) {
/*  346 */         return Hashing.stringHash32((String)paramObject);
/*      */       }
/*  348 */       i = this.hashSeed;
/*      */     }
/*      */ 
/*  351 */     i ^= paramObject.hashCode();
/*      */ 
/*  356 */     i ^= i >>> 20 ^ i >>> 12;
/*  357 */     return i ^ i >>> 7 ^ i >>> 4;
/*      */   }
/*      */ 
/*      */   static int indexFor(int paramInt1, int paramInt2)
/*      */   {
/*  364 */     return paramInt1 & paramInt2 - 1;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  373 */     return this.size;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  382 */     return this.size == 0;
/*      */   }
/*      */ 
/*      */   public V get(Object paramObject)
/*      */   {
/*  403 */     if (paramObject == null)
/*  404 */       return getForNullKey();
/*  405 */     Entry localEntry = getEntry(paramObject);
/*      */ 
/*  407 */     return null == localEntry ? null : localEntry.getValue();
/*      */   }
/*      */ 
/*      */   private V getForNullKey()
/*      */   {
/*  418 */     for (Entry localEntry = this.table[0]; localEntry != null; localEntry = localEntry.next) {
/*  419 */       if (localEntry.key == null)
/*  420 */         return localEntry.value;
/*      */     }
/*  422 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(Object paramObject)
/*      */   {
/*  434 */     return getEntry(paramObject) != null;
/*      */   }
/*      */ 
/*      */   final Entry<K, V> getEntry(Object paramObject)
/*      */   {
/*  443 */     int i = paramObject == null ? 0 : hash(paramObject);
/*  444 */     for (Entry localEntry = this.table[indexFor(i, this.table.length)]; 
/*  445 */       localEntry != null; 
/*  446 */       localEntry = localEntry.next)
/*      */     {
/*      */       Object localObject;
/*  448 */       if ((localEntry.hash == i) && (((localObject = localEntry.key) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject)))))
/*      */       {
/*  450 */         return localEntry;
/*      */       }
/*      */     }
/*  452 */     return null;
/*      */   }
/*      */ 
/*      */   public V put(K paramK, V paramV)
/*      */   {
/*  469 */     if (paramK == null)
/*  470 */       return putForNullKey(paramV);
/*  471 */     int i = hash(paramK);
/*  472 */     int j = indexFor(i, this.table.length);
/*  473 */     for (Entry localEntry = this.table[j]; localEntry != null; localEntry = localEntry.next)
/*      */     {
/*      */       Object localObject1;
/*  475 */       if ((localEntry.hash == i) && (((localObject1 = localEntry.key) == paramK) || (paramK.equals(localObject1)))) {
/*  476 */         Object localObject2 = localEntry.value;
/*  477 */         localEntry.value = paramV;
/*  478 */         localEntry.recordAccess(this);
/*  479 */         return localObject2;
/*      */       }
/*      */     }
/*      */ 
/*  483 */     this.modCount += 1;
/*  484 */     addEntry(i, paramK, paramV, j);
/*  485 */     return null;
/*      */   }
/*      */ 
/*      */   private V putForNullKey(V paramV)
/*      */   {
/*  492 */     for (Entry localEntry = this.table[0]; localEntry != null; localEntry = localEntry.next) {
/*  493 */       if (localEntry.key == null) {
/*  494 */         Object localObject = localEntry.value;
/*  495 */         localEntry.value = paramV;
/*  496 */         localEntry.recordAccess(this);
/*  497 */         return localObject;
/*      */       }
/*      */     }
/*  500 */     this.modCount += 1;
/*  501 */     addEntry(0, null, paramV, 0);
/*  502 */     return null;
/*      */   }
/*      */ 
/*      */   private void putForCreate(K paramK, V paramV)
/*      */   {
/*  512 */     int i = null == paramK ? 0 : hash(paramK);
/*  513 */     int j = indexFor(i, this.table.length);
/*      */ 
/*  520 */     for (Entry localEntry = this.table[j]; localEntry != null; localEntry = localEntry.next)
/*      */     {
/*      */       Object localObject;
/*  522 */       if ((localEntry.hash == i) && (((localObject = localEntry.key) == paramK) || ((paramK != null) && (paramK.equals(localObject)))))
/*      */       {
/*  524 */         localEntry.value = paramV;
/*  525 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  529 */     createEntry(i, paramK, paramV, j);
/*      */   }
/*      */ 
/*      */   private void putAllForCreate(Map<? extends K, ? extends V> paramMap) {
/*  533 */     for (Map.Entry localEntry : paramMap.entrySet())
/*  534 */       putForCreate(localEntry.getKey(), localEntry.getValue());
/*      */   }
/*      */ 
/*      */   void resize(int paramInt)
/*      */   {
/*  552 */     Entry[] arrayOfEntry1 = this.table;
/*  553 */     int i = arrayOfEntry1.length;
/*  554 */     if (i == 1073741824) {
/*  555 */       this.threshold = 2147483647;
/*  556 */       return;
/*      */     }
/*      */ 
/*  559 */     Entry[] arrayOfEntry2 = new Entry[paramInt];
/*  560 */     boolean bool1 = this.useAltHashing;
/*  561 */     this.useAltHashing |= ((VM.isBooted()) && (paramInt >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/*  563 */     boolean bool2 = bool1 ^ this.useAltHashing;
/*  564 */     transfer(arrayOfEntry2, bool2);
/*  565 */     this.table = arrayOfEntry2;
/*  566 */     this.threshold = ((int)Math.min(paramInt * this.loadFactor, 1.073742E+09F));
/*      */   }
/*      */ 
/*      */   void transfer(Entry[] paramArrayOfEntry, boolean paramBoolean)
/*      */   {
/*  573 */     int i = paramArrayOfEntry.length;
/*  574 */     for (Object localObject : this.table)
/*  575 */       while (null != localObject) {
/*  576 */         Entry localEntry = ((Entry)localObject).next;
/*  577 */         if (paramBoolean) {
/*  578 */           ((Entry)localObject).hash = (null == ((Entry)localObject).key ? 0 : hash(((Entry)localObject).key));
/*      */         }
/*  580 */         int m = indexFor(((Entry)localObject).hash, i);
/*  581 */         ((Entry)localObject).next = paramArrayOfEntry[m];
/*  582 */         paramArrayOfEntry[m] = localObject;
/*  583 */         localObject = localEntry;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void putAll(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  597 */     int i = paramMap.size();
/*  598 */     if (i == 0) {
/*  599 */       return;
/*      */     }
/*      */ 
/*  610 */     if (i > this.threshold) {
/*  611 */       int j = (int)(i / this.loadFactor + 1.0F);
/*  612 */       if (j > 1073741824)
/*  613 */         j = 1073741824;
/*  614 */       int k = this.table.length;
/*  615 */       while (k < j)
/*  616 */         k <<= 1;
/*  617 */       if (k > this.table.length) {
/*  618 */         resize(k);
/*      */       }
/*      */     }
/*  621 */     for (Map.Entry localEntry : paramMap.entrySet())
/*  622 */       put(localEntry.getKey(), localEntry.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(Object paramObject)
/*      */   {
/*  635 */     Entry localEntry = removeEntryForKey(paramObject);
/*  636 */     return localEntry == null ? null : localEntry.value;
/*      */   }
/*      */ 
/*      */   final Entry<K, V> removeEntryForKey(Object paramObject)
/*      */   {
/*  645 */     int i = paramObject == null ? 0 : hash(paramObject);
/*  646 */     int j = indexFor(i, this.table.length);
/*  647 */     Object localObject1 = this.table[j];
/*  648 */     Object localObject2 = localObject1;
/*      */ 
/*  650 */     while (localObject2 != null) {
/*  651 */       Entry localEntry = localObject2.next;
/*      */       Object localObject3;
/*  653 */       if ((localObject2.hash == i) && (((localObject3 = localObject2.key) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject3)))))
/*      */       {
/*  655 */         this.modCount += 1;
/*  656 */         this.size -= 1;
/*  657 */         if (localObject1 == localObject2)
/*  658 */           this.table[j] = localEntry;
/*      */         else
/*  660 */           ((Entry)localObject1).next = localEntry;
/*  661 */         localObject2.recordRemoval(this);
/*  662 */         return localObject2;
/*      */       }
/*  664 */       localObject1 = localObject2;
/*  665 */       localObject2 = localEntry;
/*      */     }
/*      */ 
/*  668 */     return localObject2;
/*      */   }
/*      */ 
/*      */   final Entry<K, V> removeMapping(Object paramObject)
/*      */   {
/*  676 */     if (!(paramObject instanceof Map.Entry)) {
/*  677 */       return null;
/*      */     }
/*  679 */     Map.Entry localEntry = (Map.Entry)paramObject;
/*  680 */     Object localObject1 = localEntry.getKey();
/*  681 */     int i = localObject1 == null ? 0 : hash(localObject1);
/*  682 */     int j = indexFor(i, this.table.length);
/*  683 */     Object localObject2 = this.table[j];
/*  684 */     Object localObject3 = localObject2;
/*      */ 
/*  686 */     while (localObject3 != null) {
/*  687 */       Entry localEntry1 = localObject3.next;
/*  688 */       if ((localObject3.hash == i) && (localObject3.equals(localEntry))) {
/*  689 */         this.modCount += 1;
/*  690 */         this.size -= 1;
/*  691 */         if (localObject2 == localObject3)
/*  692 */           this.table[j] = localEntry1;
/*      */         else
/*  694 */           ((Entry)localObject2).next = localEntry1;
/*  695 */         localObject3.recordRemoval(this);
/*  696 */         return localObject3;
/*      */       }
/*  698 */       localObject2 = localObject3;
/*  699 */       localObject3 = localEntry1;
/*      */     }
/*      */ 
/*  702 */     return localObject3;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  710 */     this.modCount += 1;
/*  711 */     Entry[] arrayOfEntry = this.table;
/*  712 */     for (int i = 0; i < arrayOfEntry.length; i++)
/*  713 */       arrayOfEntry[i] = null;
/*  714 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public boolean containsValue(Object paramObject)
/*      */   {
/*  726 */     if (paramObject == null) {
/*  727 */       return containsNullValue();
/*      */     }
/*  729 */     Entry[] arrayOfEntry = this.table;
/*  730 */     for (int i = 0; i < arrayOfEntry.length; i++)
/*  731 */       for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next)
/*  732 */         if (paramObject.equals(localEntry.value))
/*  733 */           return true;
/*  734 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean containsNullValue()
/*      */   {
/*  741 */     Entry[] arrayOfEntry = this.table;
/*  742 */     for (int i = 0; i < arrayOfEntry.length; i++)
/*  743 */       for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next)
/*  744 */         if (localEntry.value == null)
/*  745 */           return true;
/*  746 */     return false;
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*  756 */     HashMap localHashMap = null;
/*      */     try {
/*  758 */       localHashMap = (HashMap)super.clone();
/*      */     }
/*      */     catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */     }
/*  762 */     localHashMap.table = new Entry[this.table.length];
/*  763 */     localHashMap.entrySet = null;
/*  764 */     localHashMap.modCount = 0;
/*  765 */     localHashMap.size = 0;
/*  766 */     localHashMap.init();
/*  767 */     localHashMap.putAllForCreate(this);
/*      */ 
/*  769 */     return localHashMap;
/*      */   }
/*      */ 
/*      */   void addEntry(int paramInt1, K paramK, V paramV, int paramInt2)
/*      */   {
/*  850 */     if ((this.size >= this.threshold) && (null != this.table[paramInt2])) {
/*  851 */       resize(2 * this.table.length);
/*  852 */       paramInt1 = null != paramK ? hash(paramK) : 0;
/*  853 */       paramInt2 = indexFor(paramInt1, this.table.length);
/*      */     }
/*      */ 
/*  856 */     createEntry(paramInt1, paramK, paramV, paramInt2);
/*      */   }
/*      */ 
/*      */   void createEntry(int paramInt1, K paramK, V paramV, int paramInt2)
/*      */   {
/*  868 */     Entry localEntry = this.table[paramInt2];
/*  869 */     this.table[paramInt2] = new Entry(paramInt1, paramK, paramV, localEntry);
/*  870 */     this.size += 1;
/*      */   }
/*      */ 
/*      */   Iterator<K> newKeyIterator()
/*      */   {
/*  940 */     return new KeyIterator(null);
/*      */   }
/*      */   Iterator<V> newValueIterator() {
/*  943 */     return new ValueIterator(null);
/*      */   }
/*      */   Iterator<Map.Entry<K, V>> newEntryIterator() {
/*  946 */     return new EntryIterator(null);
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/*  968 */     Set localSet = this.keySet;
/*  969 */     return this.keySet = new KeySet(null);
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 1004 */     Collection localCollection = this.values;
/* 1005 */     return this.values = new Values(null);
/*      */   }
/*      */ 
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/* 1040 */     return entrySet0();
/*      */   }
/*      */ 
/*      */   private Set<Map.Entry<K, V>> entrySet0() {
/* 1044 */     Set localSet = this.entrySet;
/* 1045 */     return this.entrySet = new EntrySet(null);
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 1084 */     Object localObject = this.size > 0 ? entrySet0().iterator() : null;
/*      */ 
/* 1088 */     paramObjectOutputStream.defaultWriteObject();
/*      */ 
/* 1091 */     paramObjectOutputStream.writeInt(this.table.length);
/*      */ 
/* 1094 */     paramObjectOutputStream.writeInt(this.size);
/*      */ 
/* 1097 */     if (this.size > 0)
/* 1098 */       for (Map.Entry localEntry : entrySet0()) {
/* 1099 */         paramObjectOutputStream.writeObject(localEntry.getKey());
/* 1100 */         paramObjectOutputStream.writeObject(localEntry.getValue());
/*      */       }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1115 */     paramObjectInputStream.defaultReadObject();
/* 1116 */     if ((this.loadFactor <= 0.0F) || (Float.isNaN(this.loadFactor))) {
/* 1117 */       throw new InvalidObjectException("Illegal load factor: " + this.loadFactor);
/*      */     }
/*      */ 
/* 1121 */     Holder.UNSAFE.putIntVolatile(this, Holder.HASHSEED_OFFSET, Hashing.randomHashSeed(this));
/*      */ 
/* 1125 */     paramObjectInputStream.readInt();
/*      */ 
/* 1128 */     int i = paramObjectInputStream.readInt();
/* 1129 */     if (i < 0) {
/* 1130 */       throw new InvalidObjectException("Illegal mappings count: " + i);
/*      */     }
/*      */ 
/* 1133 */     int j = (int)Math.min(i * Math.min(1.0F / this.loadFactor, 4.0F), 1.073742E+09F);
/*      */ 
/* 1139 */     int k = 1;
/*      */ 
/* 1141 */     while (k < j) {
/* 1142 */       k <<= 1;
/*      */     }
/*      */ 
/* 1145 */     this.table = new Entry[k];
/* 1146 */     this.threshold = ((int)Math.min(k * this.loadFactor, 1.073742E+09F));
/* 1147 */     this.useAltHashing = ((VM.isBooted()) && (k >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/* 1150 */     init();
/*      */ 
/* 1153 */     for (int m = 0; m < i; m++) {
/* 1154 */       Object localObject1 = paramObjectInputStream.readObject();
/* 1155 */       Object localObject2 = paramObjectInputStream.readObject();
/* 1156 */       putForCreate(localObject1, localObject2);
/*      */     }
/*      */   }
/*      */ 
/*      */   int capacity() {
/* 1161 */     return this.table.length; } 
/* 1162 */   float loadFactor() { return this.loadFactor; }
/*      */ 
/*      */ 
/*      */   static class Entry<K, V>
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     final K key;
/*      */     V value;
/*      */     Entry<K, V> next;
/*      */     int hash;
/*      */ 
/*      */     Entry(int paramInt, K paramK, V paramV, Entry<K, V> paramEntry)
/*      */     {
/*  782 */       this.value = paramV;
/*  783 */       this.next = paramEntry;
/*  784 */       this.key = paramK;
/*  785 */       this.hash = paramInt;
/*      */     }
/*      */ 
/*      */     public final K getKey() {
/*  789 */       return this.key;
/*      */     }
/*      */ 
/*      */     public final V getValue() {
/*  793 */       return this.value;
/*      */     }
/*      */ 
/*      */     public final V setValue(V paramV) {
/*  797 */       Object localObject = this.value;
/*  798 */       this.value = paramV;
/*  799 */       return localObject;
/*      */     }
/*      */ 
/*      */     public final boolean equals(Object paramObject) {
/*  803 */       if (!(paramObject instanceof Map.Entry))
/*  804 */         return false;
/*  805 */       Map.Entry localEntry = (Map.Entry)paramObject;
/*  806 */       Object localObject1 = getKey();
/*  807 */       Object localObject2 = localEntry.getKey();
/*  808 */       if ((localObject1 == localObject2) || ((localObject1 != null) && (localObject1.equals(localObject2)))) {
/*  809 */         Object localObject3 = getValue();
/*  810 */         Object localObject4 = localEntry.getValue();
/*  811 */         if ((localObject3 == localObject4) || ((localObject3 != null) && (localObject3.equals(localObject4))))
/*  812 */           return true;
/*      */       }
/*  814 */       return false;
/*      */     }
/*      */ 
/*      */     public final int hashCode() {
/*  818 */       return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
/*      */     }
/*      */ 
/*      */     public final String toString()
/*      */     {
/*  823 */       return getKey() + "=" + getValue();
/*      */     }
/*      */ 
/*      */     void recordAccess(HashMap<K, V> paramHashMap)
/*      */     {
/*      */     }
/*      */ 
/*      */     void recordRemoval(HashMap<K, V> paramHashMap)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class EntryIterator extends HashMap<K, V>.HashIterator<Map.Entry<K, V>>
/*      */   {
/*      */     private EntryIterator()
/*      */     {
/*  932 */       super();
/*      */     }
/*  934 */     public Map.Entry<K, V> next() { return nextEntry(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class EntrySet extends AbstractSet<Map.Entry<K, V>>
/*      */   {
/*      */     private EntrySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, V>> iterator()
/*      */     {
/* 1050 */       return HashMap.this.newEntryIterator();
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/* 1053 */       if (!(paramObject instanceof Map.Entry))
/* 1054 */         return false;
/* 1055 */       Map.Entry localEntry = (Map.Entry)paramObject;
/* 1056 */       HashMap.Entry localEntry1 = HashMap.this.getEntry(localEntry.getKey());
/* 1057 */       return (localEntry1 != null) && (localEntry1.equals(localEntry));
/*      */     }
/*      */     public boolean remove(Object paramObject) {
/* 1060 */       return HashMap.this.removeMapping(paramObject) != null;
/*      */     }
/*      */     public int size() {
/* 1063 */       return HashMap.this.size;
/*      */     }
/*      */     public void clear() {
/* 1066 */       HashMap.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class HashIterator<E>
/*      */     implements Iterator<E>
/*      */   {
/*      */     HashMap.Entry<K, V> next;
/*      */     int expectedModCount;
/*      */     int index;
/*      */     HashMap.Entry<K, V> current;
/*      */ 
/*      */     HashIterator()
/*      */     {
/*  880 */       this.expectedModCount = HashMap.this.modCount;
/*  881 */       if (HashMap.this.size > 0) {
/*  882 */         HashMap.Entry[] arrayOfEntry = HashMap.this.table;
/*  883 */         while ((this.index < arrayOfEntry.length) && ((this.next = arrayOfEntry[(this.index++)]) == null));
/*      */       }
/*      */     }
/*      */ 
/*      */     public final boolean hasNext() {
/*  889 */       return this.next != null;
/*      */     }
/*      */ 
/*      */     final HashMap.Entry<K, V> nextEntry() {
/*  893 */       if (HashMap.this.modCount != this.expectedModCount)
/*  894 */         throw new ConcurrentModificationException();
/*  895 */       HashMap.Entry localEntry = this.next;
/*  896 */       if (localEntry == null) {
/*  897 */         throw new NoSuchElementException();
/*      */       }
/*  899 */       if ((this.next = localEntry.next) == null) {
/*  900 */         HashMap.Entry[] arrayOfEntry = HashMap.this.table;
/*  901 */         while ((this.index < arrayOfEntry.length) && ((this.next = arrayOfEntry[(this.index++)]) == null));
/*      */       }
/*  904 */       this.current = localEntry;
/*  905 */       return localEntry;
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  909 */       if (this.current == null)
/*  910 */         throw new IllegalStateException();
/*  911 */       if (HashMap.this.modCount != this.expectedModCount)
/*  912 */         throw new ConcurrentModificationException();
/*  913 */       Object localObject = this.current.key;
/*  914 */       this.current = null;
/*  915 */       HashMap.this.removeEntryForKey(localObject);
/*  916 */       this.expectedModCount = HashMap.this.modCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Holder
/*      */   {
/*      */     static final Unsafe UNSAFE;
/*      */     static final long HASHSEED_OFFSET;
/*      */     static final int ALTERNATIVE_HASHING_THRESHOLD;
/*      */ 
/*      */     static
/*      */     {
/*  212 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.map.althashing.threshold"));
/*      */       int i;
/*      */       try
/*      */       {
/*  218 */         i = null != str ? Integer.parseInt(str) : 2147483647;
/*      */ 
/*  223 */         if (i == -1) {
/*  224 */           i = 2147483647;
/*      */         }
/*      */ 
/*  227 */         if (i < 0)
/*  228 */           throw new IllegalArgumentException("value must be positive integer.");
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException) {
/*  231 */         throw new Error("Illegal value for 'jdk.map.althashing.threshold'", localIllegalArgumentException);
/*      */       }
/*  233 */       ALTERNATIVE_HASHING_THRESHOLD = i;
/*      */       try
/*      */       {
/*  236 */         UNSAFE = Unsafe.getUnsafe();
/*  237 */         HASHSEED_OFFSET = UNSAFE.objectFieldOffset(HashMap.class.getDeclaredField("hashSeed"));
/*      */       }
/*      */       catch (NoSuchFieldException|SecurityException localNoSuchFieldException) {
/*  240 */         throw new Error("Failed to record hashSeed offset", localNoSuchFieldException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class KeyIterator extends HashMap<K, V>.HashIterator<K>
/*      */   {
/*      */     private KeyIterator()
/*      */     {
/*  926 */       super();
/*      */     }
/*  928 */     public K next() { return nextEntry().getKey(); }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class KeySet extends AbstractSet<K>
/*      */   {
/*      */     private KeySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator()
/*      */     {
/*  974 */       return HashMap.this.newKeyIterator();
/*      */     }
/*      */     public int size() {
/*  977 */       return HashMap.this.size;
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/*  980 */       return HashMap.this.containsKey(paramObject);
/*      */     }
/*      */     public boolean remove(Object paramObject) {
/*  983 */       return HashMap.this.removeEntryForKey(paramObject) != null;
/*      */     }
/*      */     public void clear() {
/*  986 */       HashMap.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private final class ValueIterator extends HashMap<K, V>.HashIterator<V>
/*      */   {
/*      */     private ValueIterator()
/*      */     {
/*  920 */       super();
/*      */     }
/*  922 */     public V next() { return nextEntry().value; }
/*      */ 
/*      */   }
/*      */ 
/*      */   private final class Values extends AbstractCollection<V>
/*      */   {
/*      */     private Values()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/* 1010 */       return HashMap.this.newValueIterator();
/*      */     }
/*      */     public int size() {
/* 1013 */       return HashMap.this.size;
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/* 1016 */       return HashMap.this.containsValue(paramObject);
/*      */     }
/*      */     public void clear() {
/* 1019 */       HashMap.this.clear();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.HashMap
 * JD-Core Version:    0.6.2
 */