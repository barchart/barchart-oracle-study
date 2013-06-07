/*      */ package java.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.io.StreamCorruptedException;
/*      */ import java.security.AccessController;
/*      */ import sun.misc.Hashing;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.misc.VM;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ 
/*      */ public class Hashtable<K, V> extends Dictionary<K, V>
/*      */   implements Map<K, V>, Cloneable, Serializable
/*      */ {
/*      */   private transient Entry<K, V>[] table;
/*      */   private transient int count;
/*      */   private int threshold;
/*      */   private float loadFactor;
/*  161 */   private transient int modCount = 0;
/*      */   private static final long serialVersionUID = 1421746759512286392L;
/*      */   static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = 2147483647;
/*      */   transient boolean useAltHashing;
/*      */   private static final Unsafe UNSAFE;
/*      */   private static final long HASHSEED_OFFSET;
/*  246 */   final transient int hashSeed = Hashing.randomHashSeed(this);
/*      */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*  712 */   private volatile transient Set<K> keySet = null;
/*  713 */   private volatile transient Set<Map.Entry<K, V>> entrySet = null;
/*  714 */   private volatile transient Collection<V> values = null;
/*      */   private static final int KEYS = 0;
/*      */   private static final int VALUES = 1;
/*      */   private static final int ENTRIES = 2;
/*      */ 
/*      */   private int hash(Object paramObject)
/*      */   {
/*  249 */     if (this.useAltHashing) {
/*  250 */       if (paramObject.getClass() == String.class) {
/*  251 */         return Hashing.stringHash32((String)paramObject);
/*      */       }
/*  253 */       int i = this.hashSeed ^ paramObject.hashCode();
/*      */ 
/*  258 */       i ^= i >>> 20 ^ i >>> 12;
/*  259 */       return i ^ i >>> 7 ^ i >>> 4;
/*      */     }
/*      */ 
/*  262 */     return paramObject.hashCode();
/*      */   }
/*      */ 
/*      */   public Hashtable(int paramInt, float paramFloat)
/*      */   {
/*  276 */     if (paramInt < 0) {
/*  277 */       throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
/*      */     }
/*  279 */     if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
/*  280 */       throw new IllegalArgumentException("Illegal Load: " + paramFloat);
/*      */     }
/*  282 */     if (paramInt == 0)
/*  283 */       paramInt = 1;
/*  284 */     this.loadFactor = paramFloat;
/*  285 */     this.table = new Entry[paramInt];
/*  286 */     this.threshold = ((int)Math.min(paramInt * paramFloat, 2.147484E+09F));
/*  287 */     this.useAltHashing = ((VM.isBooted()) && (paramInt >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */   }
/*      */ 
/*      */   public Hashtable(int paramInt)
/*      */   {
/*  300 */     this(paramInt, 0.75F);
/*      */   }
/*      */ 
/*      */   public Hashtable()
/*      */   {
/*  308 */     this(11, 0.75F);
/*      */   }
/*      */ 
/*      */   public Hashtable(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  321 */     this(Math.max(2 * paramMap.size(), 11), 0.75F);
/*  322 */     putAll(paramMap);
/*      */   }
/*      */ 
/*      */   public synchronized int size()
/*      */   {
/*  331 */     return this.count;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isEmpty()
/*      */   {
/*  341 */     return this.count == 0;
/*      */   }
/*      */ 
/*      */   public synchronized Enumeration<K> keys()
/*      */   {
/*  354 */     return getEnumeration(0);
/*      */   }
/*      */ 
/*      */   public synchronized Enumeration<V> elements()
/*      */   {
/*  369 */     return getEnumeration(1);
/*      */   }
/*      */ 
/*      */   public synchronized boolean contains(Object paramObject)
/*      */   {
/*  389 */     if (paramObject == null) {
/*  390 */       throw new NullPointerException();
/*      */     }
/*      */ 
/*  393 */     Entry[] arrayOfEntry = this.table;
/*  394 */     for (int i = arrayOfEntry.length; i-- > 0; ) {
/*  395 */       for (Entry localEntry = arrayOfEntry[i]; localEntry != null; localEntry = localEntry.next) {
/*  396 */         if (localEntry.value.equals(paramObject)) {
/*  397 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  401 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean containsValue(Object paramObject)
/*      */   {
/*  417 */     return contains(paramObject);
/*      */   }
/*      */ 
/*      */   public synchronized boolean containsKey(Object paramObject)
/*      */   {
/*  431 */     Entry[] arrayOfEntry = this.table;
/*  432 */     int i = hash(paramObject);
/*  433 */     int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*  434 */     for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
/*  435 */       if ((localEntry.hash == i) && (localEntry.key.equals(paramObject))) {
/*  436 */         return true;
/*      */       }
/*      */     }
/*  439 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized V get(Object paramObject)
/*      */   {
/*  458 */     Entry[] arrayOfEntry = this.table;
/*  459 */     int i = hash(paramObject);
/*  460 */     int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*  461 */     for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
/*  462 */       if ((localEntry.hash == i) && (localEntry.key.equals(paramObject))) {
/*  463 */         return localEntry.value;
/*      */       }
/*      */     }
/*  466 */     return null;
/*      */   }
/*      */ 
/*      */   protected void rehash()
/*      */   {
/*  485 */     int i = this.table.length;
/*  486 */     Entry[] arrayOfEntry1 = this.table;
/*      */ 
/*  489 */     int j = (i << 1) + 1;
/*  490 */     if (j - 2147483639 > 0) {
/*  491 */       if (i == 2147483639)
/*      */       {
/*  493 */         return;
/*  494 */       }j = 2147483639;
/*      */     }
/*  496 */     Entry[] arrayOfEntry2 = new Entry[j];
/*      */ 
/*  498 */     this.modCount += 1;
/*  499 */     this.threshold = ((int)Math.min(j * this.loadFactor, 2.147484E+09F));
/*  500 */     boolean bool1 = this.useAltHashing;
/*  501 */     this.useAltHashing = ((VM.isBooted()) && (j >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/*  503 */     boolean bool2 = bool1 ^ this.useAltHashing;
/*      */ 
/*  505 */     this.table = arrayOfEntry2;
/*      */ 
/*  507 */     for (int k = i; k-- > 0; )
/*  508 */       for (localEntry1 = arrayOfEntry1[k]; localEntry1 != null; ) {
/*  509 */         Entry localEntry2 = localEntry1;
/*  510 */         localEntry1 = localEntry1.next;
/*      */ 
/*  512 */         if (bool2) {
/*  513 */           localEntry2.hash = hash(localEntry2.key);
/*      */         }
/*  515 */         int m = (localEntry2.hash & 0x7FFFFFFF) % j;
/*  516 */         localEntry2.next = arrayOfEntry2[m];
/*  517 */         arrayOfEntry2[m] = localEntry2;
/*      */       }
/*      */     Entry localEntry1;
/*      */   }
/*      */ 
/*      */   public synchronized V put(K paramK, V paramV)
/*      */   {
/*  541 */     if (paramV == null) {
/*  542 */       throw new NullPointerException();
/*      */     }
/*      */ 
/*  546 */     Entry[] arrayOfEntry = this.table;
/*  547 */     int i = hash(paramK);
/*  548 */     int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*  549 */     for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = localEntry.next) {
/*  550 */       if ((localEntry.hash == i) && (localEntry.key.equals(paramK))) {
/*  551 */         Object localObject = localEntry.value;
/*  552 */         localEntry.value = paramV;
/*  553 */         return localObject;
/*      */       }
/*      */     }
/*      */ 
/*  557 */     this.modCount += 1;
/*  558 */     if (this.count >= this.threshold)
/*      */     {
/*  560 */       rehash();
/*      */ 
/*  562 */       arrayOfEntry = this.table;
/*  563 */       i = hash(paramK);
/*  564 */       j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*      */     }
/*      */ 
/*  568 */     localEntry = arrayOfEntry[j];
/*  569 */     arrayOfEntry[j] = new Entry(i, paramK, paramV, localEntry);
/*  570 */     this.count += 1;
/*  571 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized V remove(Object paramObject)
/*      */   {
/*  584 */     Entry[] arrayOfEntry = this.table;
/*  585 */     int i = hash(paramObject);
/*  586 */     int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*  587 */     Entry localEntry1 = arrayOfEntry[j]; for (Entry localEntry2 = null; localEntry1 != null; localEntry1 = localEntry1.next) {
/*  588 */       if ((localEntry1.hash == i) && (localEntry1.key.equals(paramObject))) {
/*  589 */         this.modCount += 1;
/*  590 */         if (localEntry2 != null)
/*  591 */           localEntry2.next = localEntry1.next;
/*      */         else {
/*  593 */           arrayOfEntry[j] = localEntry1.next;
/*      */         }
/*  595 */         this.count -= 1;
/*  596 */         Object localObject = localEntry1.value;
/*  597 */         localEntry1.value = null;
/*  598 */         return localObject;
/*      */       }
/*  587 */       localEntry2 = localEntry1;
/*      */     }
/*      */ 
/*  601 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized void putAll(Map<? extends K, ? extends V> paramMap)
/*      */   {
/*  614 */     for (Map.Entry localEntry : paramMap.entrySet())
/*  615 */       put(localEntry.getKey(), localEntry.getValue());
/*      */   }
/*      */ 
/*      */   public synchronized void clear()
/*      */   {
/*  622 */     Entry[] arrayOfEntry = this.table;
/*  623 */     this.modCount += 1;
/*  624 */     int i = arrayOfEntry.length;
/*      */     while (true) { i--; if (i < 0) break;
/*  625 */       arrayOfEntry[i] = null; }
/*  626 */     this.count = 0;
/*      */   }
/*      */ 
/*      */   public synchronized Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  638 */       Hashtable localHashtable = (Hashtable)super.clone();
/*  639 */       localHashtable.table = new Entry[this.table.length];
/*  640 */       for (int i = this.table.length; i-- > 0; ) {
/*  641 */         localHashtable.table[i] = (this.table[i] != null ? (Entry)this.table[i].clone() : null);
/*      */       }
/*      */ 
/*  644 */       localHashtable.keySet = null;
/*  645 */       localHashtable.entrySet = null;
/*  646 */       localHashtable.values = null;
/*  647 */       localHashtable.modCount = 0;
/*  648 */       return localHashtable;
/*      */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */     }
/*  651 */     throw new InternalError();
/*      */   }
/*      */ 
/*      */   public synchronized String toString()
/*      */   {
/*  666 */     int i = size() - 1;
/*  667 */     if (i == -1) {
/*  668 */       return "{}";
/*      */     }
/*  670 */     StringBuilder localStringBuilder = new StringBuilder();
/*  671 */     Iterator localIterator = entrySet().iterator();
/*      */ 
/*  673 */     localStringBuilder.append('{');
/*  674 */     for (int j = 0; ; j++) {
/*  675 */       Map.Entry localEntry = (Map.Entry)localIterator.next();
/*  676 */       Object localObject1 = localEntry.getKey();
/*  677 */       Object localObject2 = localEntry.getValue();
/*  678 */       localStringBuilder.append(localObject1 == this ? "(this Map)" : localObject1.toString());
/*  679 */       localStringBuilder.append('=');
/*  680 */       localStringBuilder.append(localObject2 == this ? "(this Map)" : localObject2.toString());
/*      */ 
/*  682 */       if (j == i)
/*  683 */         return '}';
/*  684 */       localStringBuilder.append(", ");
/*      */     }
/*      */   }
/*      */ 
/*      */   private <T> Enumeration<T> getEnumeration(int paramInt)
/*      */   {
/*  690 */     if (this.count == 0) {
/*  691 */       return Collections.emptyEnumeration();
/*      */     }
/*  693 */     return new Enumerator(paramInt, false);
/*      */   }
/*      */ 
/*      */   private <T> Iterator<T> getIterator(int paramInt)
/*      */   {
/*  698 */     if (this.count == 0) {
/*  699 */       return Collections.emptyIterator();
/*      */     }
/*  701 */     return new Enumerator(paramInt, true);
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/*  732 */     if (this.keySet == null)
/*  733 */       this.keySet = Collections.synchronizedSet(new KeySet(null), this);
/*  734 */     return this.keySet;
/*      */   }
/*      */ 
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/*  772 */     if (this.entrySet == null)
/*  773 */       this.entrySet = Collections.synchronizedSet(new EntrySet(null), this);
/*  774 */     return this.entrySet;
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/*  852 */     if (this.values == null) {
/*  853 */       this.values = Collections.synchronizedCollection(new ValueCollection(null), this);
/*      */     }
/*  855 */     return this.values;
/*      */   }
/*      */ 
/*      */   public synchronized boolean equals(Object paramObject)
/*      */   {
/*  885 */     if (paramObject == this) {
/*  886 */       return true;
/*      */     }
/*  888 */     if (!(paramObject instanceof Map))
/*  889 */       return false;
/*  890 */     Map localMap = (Map)paramObject;
/*  891 */     if (localMap.size() != size())
/*  892 */       return false;
/*      */     try
/*      */     {
/*  895 */       Iterator localIterator = entrySet().iterator();
/*  896 */       while (localIterator.hasNext()) {
/*  897 */         Map.Entry localEntry = (Map.Entry)localIterator.next();
/*  898 */         Object localObject1 = localEntry.getKey();
/*  899 */         Object localObject2 = localEntry.getValue();
/*  900 */         if (localObject2 == null) {
/*  901 */           if ((localMap.get(localObject1) != null) || (!localMap.containsKey(localObject1)))
/*  902 */             return false;
/*      */         }
/*  904 */         else if (!localObject2.equals(localMap.get(localObject1)))
/*  905 */           return false;
/*      */       }
/*      */     }
/*      */     catch (ClassCastException localClassCastException) {
/*  909 */       return false;
/*      */     } catch (NullPointerException localNullPointerException) {
/*  911 */       return false;
/*      */     }
/*      */ 
/*  914 */     return true;
/*      */   }
/*      */ 
/*      */   public synchronized int hashCode()
/*      */   {
/*  935 */     int i = 0;
/*  936 */     if ((this.count == 0) || (this.loadFactor < 0.0F)) {
/*  937 */       return i;
/*      */     }
/*  939 */     this.loadFactor = (-this.loadFactor);
/*  940 */     Entry[] arrayOfEntry1 = this.table;
/*  941 */     for (Entry localEntry : arrayOfEntry1)
/*  942 */       while (localEntry != null) {
/*  943 */         i += localEntry.hashCode();
/*  944 */         localEntry = localEntry.next;
/*      */       }
/*  946 */     this.loadFactor = (-this.loadFactor);
/*      */ 
/*  948 */     return i;
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  963 */     Entry localEntry1 = null;
/*      */ 
/*  965 */     synchronized (this)
/*      */     {
/*  967 */       paramObjectOutputStream.defaultWriteObject();
/*      */ 
/*  970 */       paramObjectOutputStream.writeInt(this.table.length);
/*  971 */       paramObjectOutputStream.writeInt(this.count);
/*      */ 
/*  974 */       for (int i = 0; i < this.table.length; i++) {
/*  975 */         Entry localEntry2 = this.table[i];
/*      */ 
/*  977 */         while (localEntry2 != null) {
/*  978 */           localEntry1 = new Entry(0, localEntry2.key, localEntry2.value, localEntry1);
/*      */ 
/*  980 */           localEntry2 = localEntry2.next;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  986 */     while (localEntry1 != null) {
/*  987 */       paramObjectOutputStream.writeObject(localEntry1.key);
/*  988 */       paramObjectOutputStream.writeObject(localEntry1.value);
/*  989 */       localEntry1 = localEntry1.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1000 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/* 1003 */     UNSAFE.putIntVolatile(this, HASHSEED_OFFSET, Hashing.randomHashSeed(this));
/*      */ 
/* 1007 */     int i = paramObjectInputStream.readInt();
/* 1008 */     int j = paramObjectInputStream.readInt();
/*      */ 
/* 1014 */     int k = (int)(j * this.loadFactor) + j / 20 + 3;
/* 1015 */     if ((k > j) && ((k & 0x1) == 0))
/* 1016 */       k--;
/* 1017 */     if ((i > 0) && (k > i)) {
/* 1018 */       k = i;
/*      */     }
/* 1020 */     Entry[] arrayOfEntry = new Entry[k];
/* 1021 */     this.threshold = ((int)Math.min(k * this.loadFactor, 2.147484E+09F));
/* 1022 */     this.count = 0;
/* 1023 */     this.useAltHashing = ((VM.isBooted()) && (k >= Holder.ALTERNATIVE_HASHING_THRESHOLD));
/*      */ 
/* 1027 */     for (; j > 0; j--) {
/* 1028 */       Object localObject1 = paramObjectInputStream.readObject();
/* 1029 */       Object localObject2 = paramObjectInputStream.readObject();
/*      */ 
/* 1031 */       reconstitutionPut(arrayOfEntry, localObject1, localObject2);
/*      */     }
/* 1033 */     this.table = arrayOfEntry;
/*      */   }
/*      */ 
/*      */   private void reconstitutionPut(Entry<K, V>[] paramArrayOfEntry, K paramK, V paramV)
/*      */     throws StreamCorruptedException
/*      */   {
/* 1050 */     if (paramV == null) {
/* 1051 */       throw new StreamCorruptedException();
/*      */     }
/*      */ 
/* 1055 */     int i = hash(paramK);
/* 1056 */     int j = (i & 0x7FFFFFFF) % paramArrayOfEntry.length;
/* 1057 */     for (Object localObject = paramArrayOfEntry[j]; localObject != null; localObject = ((Entry)localObject).next) {
/* 1058 */       if ((((Entry)localObject).hash == i) && (((Entry)localObject).key.equals(paramK))) {
/* 1059 */         throw new StreamCorruptedException();
/*      */       }
/*      */     }
/*      */ 
/* 1063 */     localObject = paramArrayOfEntry[j];
/* 1064 */     paramArrayOfEntry[j] = new Entry(i, paramK, paramV, (Entry)localObject);
/* 1065 */     this.count += 1;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  234 */       UNSAFE = Unsafe.getUnsafe();
/*  235 */       HASHSEED_OFFSET = UNSAFE.objectFieldOffset(Hashtable.class.getDeclaredField("hashSeed"));
/*      */     }
/*      */     catch (NoSuchFieldException|SecurityException localNoSuchFieldException) {
/*  238 */       throw new Error("Failed to record hashSeed offset", localNoSuchFieldException);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Entry<K, V>
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     int hash;
/*      */     final K key;
/*      */     V value;
/*      */     Entry<K, V> next;
/*      */ 
/*      */     protected Entry(int paramInt, K paramK, V paramV, Entry<K, V> paramEntry)
/*      */     {
/* 1078 */       this.hash = paramInt;
/* 1079 */       this.key = paramK;
/* 1080 */       this.value = paramV;
/* 1081 */       this.next = paramEntry;
/*      */     }
/*      */ 
/*      */     protected Object clone() {
/* 1085 */       return new Entry(this.hash, this.key, this.value, this.next == null ? null : (Entry)this.next.clone());
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1092 */       return this.key;
/*      */     }
/*      */ 
/*      */     public V getValue() {
/* 1096 */       return this.value;
/*      */     }
/*      */ 
/*      */     public V setValue(V paramV) {
/* 1100 */       if (paramV == null) {
/* 1101 */         throw new NullPointerException();
/*      */       }
/* 1103 */       Object localObject = this.value;
/* 1104 */       this.value = paramV;
/* 1105 */       return localObject;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object paramObject) {
/* 1109 */       if (!(paramObject instanceof Map.Entry))
/* 1110 */         return false;
/* 1111 */       Map.Entry localEntry = (Map.Entry)paramObject;
/*      */ 
/* 1113 */       return (this.key.equals(localEntry.getKey())) && (this.value.equals(localEntry.getValue()));
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 1117 */       return this.hash ^ this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1121 */       return this.key.toString() + "=" + this.value.toString();
/*      */     }
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
/*  779 */       return Hashtable.this.getIterator(2);
/*      */     }
/*      */ 
/*      */     public boolean add(Map.Entry<K, V> paramEntry) {
/*  783 */       return super.add(paramEntry);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object paramObject) {
/*  787 */       if (!(paramObject instanceof Map.Entry))
/*  788 */         return false;
/*  789 */       Map.Entry localEntry = (Map.Entry)paramObject;
/*  790 */       Object localObject = localEntry.getKey();
/*  791 */       Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
/*  792 */       int i = Hashtable.this.hash(localObject);
/*  793 */       int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*      */ 
/*  795 */       for (Hashtable.Entry localEntry1 = arrayOfEntry[j]; localEntry1 != null; localEntry1 = localEntry1.next)
/*  796 */         if ((localEntry1.hash == i) && (localEntry1.equals(localEntry)))
/*  797 */           return true;
/*  798 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object paramObject) {
/*  802 */       if (!(paramObject instanceof Map.Entry))
/*  803 */         return false;
/*  804 */       Map.Entry localEntry = (Map.Entry)paramObject;
/*  805 */       Object localObject = localEntry.getKey();
/*  806 */       Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
/*  807 */       int i = Hashtable.this.hash(localObject);
/*  808 */       int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
/*      */ 
/*  810 */       Hashtable.Entry localEntry1 = arrayOfEntry[j]; for (Hashtable.Entry localEntry2 = null; localEntry1 != null; 
/*  811 */         localEntry1 = localEntry1.next) {
/*  812 */         if ((localEntry1.hash == i) && (localEntry1.equals(localEntry))) {
/*  813 */           Hashtable.access$608(Hashtable.this);
/*  814 */           if (localEntry2 != null)
/*  815 */             localEntry2.next = localEntry1.next;
/*      */           else {
/*  817 */             arrayOfEntry[j] = localEntry1.next;
/*      */           }
/*  819 */           Hashtable.access$210(Hashtable.this);
/*  820 */           localEntry1.value = null;
/*  821 */           return true;
/*      */         }
/*  811 */         localEntry2 = localEntry1;
/*      */       }
/*      */ 
/*  824 */       return false;
/*      */     }
/*      */ 
/*      */     public int size() {
/*  828 */       return Hashtable.this.count;
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  832 */       Hashtable.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Enumerator<T>
/*      */     implements Enumeration<T>, Iterator<T>
/*      */   {
/* 1138 */     Hashtable.Entry[] table = Hashtable.this.table;
/* 1139 */     int index = this.table.length;
/* 1140 */     Hashtable.Entry<K, V> entry = null;
/* 1141 */     Hashtable.Entry<K, V> lastReturned = null;
/*      */     int type;
/*      */     boolean iterator;
/* 1155 */     protected int expectedModCount = Hashtable.this.modCount;
/*      */ 
/*      */     Enumerator(int paramBoolean, boolean arg3) {
/* 1158 */       this.type = paramBoolean;
/*      */       boolean bool;
/* 1159 */       this.iterator = bool;
/*      */     }
/*      */ 
/*      */     public boolean hasMoreElements() {
/* 1163 */       Hashtable.Entry localEntry = this.entry;
/* 1164 */       int i = this.index;
/* 1165 */       Hashtable.Entry[] arrayOfEntry = this.table;
/*      */ 
/* 1167 */       while ((localEntry == null) && (i > 0)) {
/* 1168 */         localEntry = arrayOfEntry[(--i)];
/*      */       }
/* 1170 */       this.entry = localEntry;
/* 1171 */       this.index = i;
/* 1172 */       return localEntry != null;
/*      */     }
/*      */ 
/*      */     public T nextElement() {
/* 1176 */       Hashtable.Entry localEntry1 = this.entry;
/* 1177 */       int i = this.index;
/* 1178 */       Hashtable.Entry[] arrayOfEntry = this.table;
/*      */ 
/* 1180 */       while ((localEntry1 == null) && (i > 0)) {
/* 1181 */         localEntry1 = arrayOfEntry[(--i)];
/*      */       }
/* 1183 */       this.entry = localEntry1;
/* 1184 */       this.index = i;
/* 1185 */       if (localEntry1 != null) {
/* 1186 */         Hashtable.Entry localEntry2 = this.lastReturned = this.entry;
/* 1187 */         this.entry = localEntry2.next;
/* 1188 */         return this.type == 1 ? localEntry2.value : this.type == 0 ? localEntry2.key : localEntry2;
/*      */       }
/* 1190 */       throw new NoSuchElementException("Hashtable Enumerator");
/*      */     }
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 1195 */       return hasMoreElements();
/*      */     }
/*      */ 
/*      */     public T next() {
/* 1199 */       if (Hashtable.this.modCount != this.expectedModCount)
/* 1200 */         throw new ConcurrentModificationException();
/* 1201 */       return nextElement();
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1205 */       if (!this.iterator)
/* 1206 */         throw new UnsupportedOperationException();
/* 1207 */       if (this.lastReturned == null)
/* 1208 */         throw new IllegalStateException("Hashtable Enumerator");
/* 1209 */       if (Hashtable.this.modCount != this.expectedModCount) {
/* 1210 */         throw new ConcurrentModificationException();
/*      */       }
/* 1212 */       synchronized (Hashtable.this) {
/* 1213 */         Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
/* 1214 */         int i = (this.lastReturned.hash & 0x7FFFFFFF) % arrayOfEntry.length;
/*      */ 
/* 1216 */         Hashtable.Entry localEntry1 = arrayOfEntry[i]; for (Hashtable.Entry localEntry2 = null; localEntry1 != null; 
/* 1217 */           localEntry1 = localEntry1.next) {
/* 1218 */           if (localEntry1 == this.lastReturned) {
/* 1219 */             Hashtable.access$608(Hashtable.this);
/* 1220 */             this.expectedModCount += 1;
/* 1221 */             if (localEntry2 == null)
/* 1222 */               arrayOfEntry[i] = localEntry1.next;
/*      */             else
/* 1224 */               localEntry2.next = localEntry1.next;
/* 1225 */             Hashtable.access$210(Hashtable.this);
/* 1226 */             this.lastReturned = null;
/*      */             return;
/*      */           }
/* 1217 */           localEntry2 = localEntry1;
/*      */         }
/*      */ 
/* 1230 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Holder
/*      */   {
/*  211 */     static final int ALTERNATIVE_HASHING_THRESHOLD = i;
/*      */ 
/*      */     static
/*      */     {
/*  189 */       String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.map.althashing.threshold"));
/*      */       int i;
/*      */       try
/*      */       {
/*  195 */         i = null != str ? Integer.parseInt(str) : 2147483647;
/*      */ 
/*  200 */         if (i == -1) {
/*  201 */           i = 2147483647;
/*      */         }
/*      */ 
/*  204 */         if (i < 0)
/*  205 */           throw new IllegalArgumentException("value must be positive integer.");
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException) {
/*  208 */         throw new Error("Illegal value for 'jdk.map.althashing.threshold'", localIllegalArgumentException);
/*      */       }
/*      */     }
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
/*  739 */       return Hashtable.this.getIterator(0);
/*      */     }
/*      */     public int size() {
/*  742 */       return Hashtable.this.count;
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/*  745 */       return Hashtable.this.containsKey(paramObject);
/*      */     }
/*      */     public boolean remove(Object paramObject) {
/*  748 */       return Hashtable.this.remove(paramObject) != null;
/*      */     }
/*      */     public void clear() {
/*  751 */       Hashtable.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ValueCollection extends AbstractCollection<V>
/*      */   {
/*      */     private ValueCollection()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/*  860 */       return Hashtable.this.getIterator(1);
/*      */     }
/*      */     public int size() {
/*  863 */       return Hashtable.this.count;
/*      */     }
/*      */     public boolean contains(Object paramObject) {
/*  866 */       return Hashtable.this.containsValue(paramObject);
/*      */     }
/*      */     public void clear() {
/*  869 */       Hashtable.this.clear();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.Hashtable
 * JD-Core Version:    0.6.2
 */