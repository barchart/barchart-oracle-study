/*      */ package java.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ 
/*      */ public class ArrayList<E> extends AbstractList<E>
/*      */   implements List<E>, RandomAccess, Cloneable, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 8683452581122892189L;
/*      */   private transient Object[] elementData;
/*      */   private int size;
/*      */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*      */ 
/*      */   public ArrayList(int paramInt)
/*      */   {
/*  129 */     if (paramInt < 0) {
/*  130 */       throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
/*      */     }
/*  132 */     this.elementData = new Object[paramInt];
/*      */   }
/*      */ 
/*      */   public ArrayList()
/*      */   {
/*  139 */     this(10);
/*      */   }
/*      */ 
/*      */   public ArrayList(Collection<? extends E> paramCollection)
/*      */   {
/*  151 */     this.elementData = paramCollection.toArray();
/*  152 */     this.size = this.elementData.length;
/*      */ 
/*  154 */     if (this.elementData.getClass() != [Ljava.lang.Object.class)
/*  155 */       this.elementData = Arrays.copyOf(this.elementData, this.size, [Ljava.lang.Object.class);
/*      */   }
/*      */ 
/*      */   public void trimToSize()
/*      */   {
/*  164 */     this.modCount += 1;
/*  165 */     int i = this.elementData.length;
/*  166 */     if (this.size < i)
/*  167 */       this.elementData = Arrays.copyOf(this.elementData, this.size);
/*      */   }
/*      */ 
/*      */   public void ensureCapacity(int paramInt)
/*      */   {
/*  179 */     if (paramInt > 0)
/*  180 */       ensureCapacityInternal(paramInt);
/*      */   }
/*      */ 
/*      */   private void ensureCapacityInternal(int paramInt) {
/*  184 */     this.modCount += 1;
/*      */ 
/*  186 */     if (paramInt - this.elementData.length > 0)
/*  187 */       grow(paramInt);
/*      */   }
/*      */ 
/*      */   private void grow(int paramInt)
/*      */   {
/*  206 */     int i = this.elementData.length;
/*  207 */     int j = i + (i >> 1);
/*  208 */     if (j - paramInt < 0)
/*  209 */       j = paramInt;
/*  210 */     if (j - 2147483639 > 0) {
/*  211 */       j = hugeCapacity(paramInt);
/*      */     }
/*  213 */     this.elementData = Arrays.copyOf(this.elementData, j);
/*      */   }
/*      */ 
/*      */   private static int hugeCapacity(int paramInt) {
/*  217 */     if (paramInt < 0)
/*  218 */       throw new OutOfMemoryError();
/*  219 */     return paramInt > 2147483639 ? 2147483647 : 2147483639;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  230 */     return this.size;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  239 */     return this.size == 0;
/*      */   }
/*      */ 
/*      */   public boolean contains(Object paramObject)
/*      */   {
/*  252 */     return indexOf(paramObject) >= 0;
/*      */   }
/*      */ 
/*      */   public int indexOf(Object paramObject)
/*      */   {
/*      */     int i;
/*  263 */     if (paramObject == null)
/*  264 */       for (i = 0; i < this.size; i++)
/*  265 */         if (this.elementData[i] == null)
/*  266 */           return i;
/*      */     else {
/*  268 */       for (i = 0; i < this.size; i++)
/*  269 */         if (paramObject.equals(this.elementData[i]))
/*  270 */           return i;
/*      */     }
/*  272 */     return -1;
/*      */   }
/*      */ 
/*      */   public int lastIndexOf(Object paramObject)
/*      */   {
/*      */     int i;
/*  283 */     if (paramObject == null)
/*  284 */       for (i = this.size - 1; i >= 0; i--)
/*  285 */         if (this.elementData[i] == null)
/*  286 */           return i;
/*      */     else {
/*  288 */       for (i = this.size - 1; i >= 0; i--)
/*  289 */         if (paramObject.equals(this.elementData[i]))
/*  290 */           return i;
/*      */     }
/*  292 */     return -1;
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  304 */       ArrayList localArrayList = (ArrayList)super.clone();
/*  305 */       localArrayList.elementData = Arrays.copyOf(this.elementData, this.size);
/*  306 */       localArrayList.modCount = 0;
/*  307 */       return localArrayList;
/*      */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*      */     }
/*  310 */     throw new InternalError();
/*      */   }
/*      */ 
/*      */   public Object[] toArray()
/*      */   {
/*  329 */     return Arrays.copyOf(this.elementData, this.size);
/*      */   }
/*      */ 
/*      */   public <T> T[] toArray(T[] paramArrayOfT)
/*      */   {
/*  358 */     if (paramArrayOfT.length < this.size)
/*      */     {
/*  360 */       return (Object[])Arrays.copyOf(this.elementData, this.size, paramArrayOfT.getClass());
/*  361 */     }System.arraycopy(this.elementData, 0, paramArrayOfT, 0, this.size);
/*  362 */     if (paramArrayOfT.length > this.size)
/*  363 */       paramArrayOfT[this.size] = null;
/*  364 */     return paramArrayOfT;
/*      */   }
/*      */ 
/*      */   E elementData(int paramInt)
/*      */   {
/*  371 */     return this.elementData[paramInt];
/*      */   }
/*      */ 
/*      */   public E get(int paramInt)
/*      */   {
/*  382 */     rangeCheck(paramInt);
/*      */ 
/*  384 */     return elementData(paramInt);
/*      */   }
/*      */ 
/*      */   public E set(int paramInt, E paramE)
/*      */   {
/*  397 */     rangeCheck(paramInt);
/*      */ 
/*  399 */     Object localObject = elementData(paramInt);
/*  400 */     this.elementData[paramInt] = paramE;
/*  401 */     return localObject;
/*      */   }
/*      */ 
/*      */   public boolean add(E paramE)
/*      */   {
/*  411 */     ensureCapacityInternal(this.size + 1);
/*  412 */     this.elementData[(this.size++)] = paramE;
/*  413 */     return true;
/*      */   }
/*      */ 
/*      */   public void add(int paramInt, E paramE)
/*      */   {
/*  426 */     rangeCheckForAdd(paramInt);
/*      */ 
/*  428 */     ensureCapacityInternal(this.size + 1);
/*  429 */     System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + 1, this.size - paramInt);
/*      */ 
/*  431 */     this.elementData[paramInt] = paramE;
/*  432 */     this.size += 1;
/*      */   }
/*      */ 
/*      */   public E remove(int paramInt)
/*      */   {
/*  445 */     rangeCheck(paramInt);
/*      */ 
/*  447 */     this.modCount += 1;
/*  448 */     Object localObject = elementData(paramInt);
/*      */ 
/*  450 */     int i = this.size - paramInt - 1;
/*  451 */     if (i > 0) {
/*  452 */       System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i);
/*      */     }
/*  454 */     this.elementData[(--this.size)] = null;
/*      */ 
/*  456 */     return localObject;
/*      */   }
/*      */ 
/*      */   public boolean remove(Object paramObject)
/*      */   {
/*      */     int i;
/*  473 */     if (paramObject == null)
/*  474 */       for (i = 0; i < this.size; i++)
/*  475 */         if (this.elementData[i] == null) {
/*  476 */           fastRemove(i);
/*  477 */           return true;
/*      */         }
/*      */     else {
/*  480 */       for (i = 0; i < this.size; i++)
/*  481 */         if (paramObject.equals(this.elementData[i])) {
/*  482 */           fastRemove(i);
/*  483 */           return true;
/*      */         }
/*      */     }
/*  486 */     return false;
/*      */   }
/*      */ 
/*      */   private void fastRemove(int paramInt)
/*      */   {
/*  494 */     this.modCount += 1;
/*  495 */     int i = this.size - paramInt - 1;
/*  496 */     if (i > 0) {
/*  497 */       System.arraycopy(this.elementData, paramInt + 1, this.elementData, paramInt, i);
/*      */     }
/*  499 */     this.elementData[(--this.size)] = null;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  507 */     this.modCount += 1;
/*      */ 
/*  510 */     for (int i = 0; i < this.size; i++) {
/*  511 */       this.elementData[i] = null;
/*      */     }
/*  513 */     this.size = 0;
/*      */   }
/*      */ 
/*      */   public boolean addAll(Collection<? extends E> paramCollection)
/*      */   {
/*  530 */     Object[] arrayOfObject = paramCollection.toArray();
/*  531 */     int i = arrayOfObject.length;
/*  532 */     ensureCapacityInternal(this.size + i);
/*  533 */     System.arraycopy(arrayOfObject, 0, this.elementData, this.size, i);
/*  534 */     this.size += i;
/*  535 */     return i != 0;
/*      */   }
/*      */ 
/*      */   public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
/*      */   {
/*  554 */     rangeCheckForAdd(paramInt);
/*      */ 
/*  556 */     Object[] arrayOfObject = paramCollection.toArray();
/*  557 */     int i = arrayOfObject.length;
/*  558 */     ensureCapacityInternal(this.size + i);
/*      */ 
/*  560 */     int j = this.size - paramInt;
/*  561 */     if (j > 0) {
/*  562 */       System.arraycopy(this.elementData, paramInt, this.elementData, paramInt + i, j);
/*      */     }
/*      */ 
/*  565 */     System.arraycopy(arrayOfObject, 0, this.elementData, paramInt, i);
/*  566 */     this.size += i;
/*  567 */     return i != 0;
/*      */   }
/*      */ 
/*      */   protected void removeRange(int paramInt1, int paramInt2)
/*      */   {
/*  585 */     this.modCount += 1;
/*  586 */     int i = this.size - paramInt2;
/*  587 */     System.arraycopy(this.elementData, paramInt2, this.elementData, paramInt1, i);
/*      */ 
/*  591 */     int j = this.size - (paramInt2 - paramInt1);
/*  592 */     while (this.size != j)
/*  593 */       this.elementData[(--this.size)] = null;
/*      */   }
/*      */ 
/*      */   private void rangeCheck(int paramInt)
/*      */   {
/*  603 */     if (paramInt >= this.size)
/*  604 */       throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
/*      */   }
/*      */ 
/*      */   private void rangeCheckForAdd(int paramInt)
/*      */   {
/*  611 */     if ((paramInt > this.size) || (paramInt < 0))
/*  612 */       throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
/*      */   }
/*      */ 
/*      */   private String outOfBoundsMsg(int paramInt)
/*      */   {
/*  621 */     return "Index: " + paramInt + ", Size: " + this.size;
/*      */   }
/*      */ 
/*      */   public boolean removeAll(Collection<?> paramCollection)
/*      */   {
/*  640 */     return batchRemove(paramCollection, false);
/*      */   }
/*      */ 
/*      */   public boolean retainAll(Collection<?> paramCollection)
/*      */   {
/*  660 */     return batchRemove(paramCollection, true);
/*      */   }
/*      */ 
/*      */   private boolean batchRemove(Collection<?> paramCollection, boolean paramBoolean) {
/*  664 */     Object[] arrayOfObject = this.elementData;
/*  665 */     int i = 0; int j = 0;
/*  666 */     boolean bool = false;
/*      */     try {
/*  668 */       for (; i < this.size; i++) {
/*  669 */         if (paramCollection.contains(arrayOfObject[i]) == paramBoolean) {
/*  670 */           arrayOfObject[(j++)] = arrayOfObject[i];
/*      */         }
/*      */       }
/*      */ 
/*  674 */       if (i != this.size) {
/*  675 */         System.arraycopy(arrayOfObject, i, arrayOfObject, j, this.size - i);
/*      */ 
/*  678 */         j += this.size - i;
/*      */       }
/*  680 */       if (j != this.size) {
/*  681 */         for (int k = j; k < this.size; k++)
/*  682 */           arrayOfObject[k] = null;
/*  683 */         this.modCount += this.size - j;
/*  684 */         this.size = j;
/*  685 */         bool = true;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  674 */       if (i != this.size) {
/*  675 */         System.arraycopy(arrayOfObject, i, arrayOfObject, j, this.size - i);
/*      */ 
/*  678 */         j += this.size - i;
/*      */       }
/*  680 */       if (j != this.size) {
/*  681 */         for (int m = j; m < this.size; m++)
/*  682 */           arrayOfObject[m] = null;
/*  683 */         this.modCount += this.size - j;
/*  684 */         this.size = j;
/*  685 */         bool = true;
/*      */       }
/*      */     }
/*  688 */     return bool;
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/*  702 */     int i = this.modCount;
/*  703 */     paramObjectOutputStream.defaultWriteObject();
/*      */ 
/*  706 */     paramObjectOutputStream.writeInt(this.elementData.length);
/*      */ 
/*  709 */     for (int j = 0; j < this.size; j++) {
/*  710 */       paramObjectOutputStream.writeObject(this.elementData[j]);
/*      */     }
/*  712 */     if (this.modCount != i)
/*  713 */       throw new ConcurrentModificationException();
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/*  725 */     paramObjectInputStream.defaultReadObject();
/*      */ 
/*  728 */     int i = paramObjectInputStream.readInt();
/*  729 */     Object[] arrayOfObject = this.elementData = new Object[i];
/*      */ 
/*  732 */     for (int j = 0; j < this.size; j++)
/*  733 */       arrayOfObject[j] = paramObjectInputStream.readObject();
/*      */   }
/*      */ 
/*      */   public ListIterator<E> listIterator(int paramInt)
/*      */   {
/*  749 */     if ((paramInt < 0) || (paramInt > this.size))
/*  750 */       throw new IndexOutOfBoundsException("Index: " + paramInt);
/*  751 */     return new ListItr(paramInt);
/*      */   }
/*      */ 
/*      */   public ListIterator<E> listIterator()
/*      */   {
/*  763 */     return new ListItr(0);
/*      */   }
/*      */ 
/*      */   public Iterator<E> iterator()
/*      */   {
/*  774 */     return new Itr(null);
/*      */   }
/*      */ 
/*      */   public List<E> subList(int paramInt1, int paramInt2)
/*      */   {
/*  914 */     subListRangeCheck(paramInt1, paramInt2, this.size);
/*  915 */     return new SubList(this, 0, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   static void subListRangeCheck(int paramInt1, int paramInt2, int paramInt3) {
/*  919 */     if (paramInt1 < 0)
/*  920 */       throw new IndexOutOfBoundsException("fromIndex = " + paramInt1);
/*  921 */     if (paramInt2 > paramInt3)
/*  922 */       throw new IndexOutOfBoundsException("toIndex = " + paramInt2);
/*  923 */     if (paramInt1 > paramInt2)
/*  924 */       throw new IllegalArgumentException("fromIndex(" + paramInt1 + ") > toIndex(" + paramInt2 + ")");
/*      */   }
/*      */ 
/*      */   private class Itr
/*      */     implements Iterator<E>
/*      */   {
/*      */     int cursor;
/*  782 */     int lastRet = -1;
/*  783 */     int expectedModCount = ArrayList.this.modCount;
/*      */ 
/*      */     private Itr() {  } 
/*  786 */     public boolean hasNext() { return this.cursor != ArrayList.this.size; }
/*      */ 
/*      */ 
/*      */     public E next()
/*      */     {
/*  791 */       checkForComodification();
/*  792 */       int i = this.cursor;
/*  793 */       if (i >= ArrayList.this.size)
/*  794 */         throw new NoSuchElementException();
/*  795 */       Object[] arrayOfObject = ArrayList.this.elementData;
/*  796 */       if (i >= arrayOfObject.length)
/*  797 */         throw new ConcurrentModificationException();
/*  798 */       this.cursor = (i + 1);
/*  799 */       return arrayOfObject[(this.lastRet = i)];
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  803 */       if (this.lastRet < 0)
/*  804 */         throw new IllegalStateException();
/*  805 */       checkForComodification();
/*      */       try
/*      */       {
/*  808 */         ArrayList.this.remove(this.lastRet);
/*  809 */         this.cursor = this.lastRet;
/*  810 */         this.lastRet = -1;
/*  811 */         this.expectedModCount = ArrayList.this.modCount;
/*      */       } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  813 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     }
/*      */ 
/*      */     final void checkForComodification() {
/*  818 */       if (ArrayList.this.modCount != this.expectedModCount)
/*  819 */         throw new ConcurrentModificationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ListItr extends ArrayList<E>.Itr
/*      */     implements ListIterator<E>
/*      */   {
/*      */     ListItr(int arg2)
/*      */     {
/*  828 */       super(null);
/*      */       int i;
/*  829 */       this.cursor = i;
/*      */     }
/*      */ 
/*      */     public boolean hasPrevious() {
/*  833 */       return this.cursor != 0;
/*      */     }
/*      */ 
/*      */     public int nextIndex() {
/*  837 */       return this.cursor;
/*      */     }
/*      */ 
/*      */     public int previousIndex() {
/*  841 */       return this.cursor - 1;
/*      */     }
/*      */ 
/*      */     public E previous()
/*      */     {
/*  846 */       checkForComodification();
/*  847 */       int i = this.cursor - 1;
/*  848 */       if (i < 0)
/*  849 */         throw new NoSuchElementException();
/*  850 */       Object[] arrayOfObject = ArrayList.this.elementData;
/*  851 */       if (i >= arrayOfObject.length)
/*  852 */         throw new ConcurrentModificationException();
/*  853 */       this.cursor = i;
/*  854 */       return arrayOfObject[(this.lastRet = i)];
/*      */     }
/*      */ 
/*      */     public void set(E paramE) {
/*  858 */       if (this.lastRet < 0)
/*  859 */         throw new IllegalStateException();
/*  860 */       checkForComodification();
/*      */       try
/*      */       {
/*  863 */         ArrayList.this.set(this.lastRet, paramE);
/*      */       } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  865 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void add(E paramE) {
/*  870 */       checkForComodification();
/*      */       try
/*      */       {
/*  873 */         int i = this.cursor;
/*  874 */         ArrayList.this.add(i, paramE);
/*  875 */         this.cursor = (i + 1);
/*  876 */         this.lastRet = -1;
/*  877 */         this.expectedModCount = ArrayList.this.modCount;
/*      */       } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  879 */         throw new ConcurrentModificationException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SubList extends AbstractList<E>
/*      */     implements RandomAccess
/*      */   {
/*      */     private final AbstractList<E> parent;
/*      */     private final int parentOffset;
/*      */     private final int offset;
/*      */     int size;
/*      */ 
/*      */     SubList(int paramInt1, int paramInt2, int arg4)
/*      */     {
/*  936 */       this.parent = paramInt1;
/*      */       int i;
/*  937 */       this.parentOffset = i;
/*  938 */       this.offset = (paramInt2 + i);
/*      */       int j;
/*  939 */       this.size = (j - i);
/*  940 */       this.modCount = ArrayList.this.modCount;
/*      */     }
/*      */ 
/*      */     public E set(int paramInt, E paramE) {
/*  944 */       rangeCheck(paramInt);
/*  945 */       checkForComodification();
/*  946 */       Object localObject = ArrayList.this.elementData(this.offset + paramInt);
/*  947 */       ArrayList.this.elementData[(this.offset + paramInt)] = paramE;
/*  948 */       return localObject;
/*      */     }
/*      */ 
/*      */     public E get(int paramInt) {
/*  952 */       rangeCheck(paramInt);
/*  953 */       checkForComodification();
/*  954 */       return ArrayList.this.elementData(this.offset + paramInt);
/*      */     }
/*      */ 
/*      */     public int size() {
/*  958 */       checkForComodification();
/*  959 */       return this.size;
/*      */     }
/*      */ 
/*      */     public void add(int paramInt, E paramE) {
/*  963 */       rangeCheckForAdd(paramInt);
/*  964 */       checkForComodification();
/*  965 */       this.parent.add(this.parentOffset + paramInt, paramE);
/*  966 */       this.modCount = this.parent.modCount;
/*  967 */       this.size += 1;
/*      */     }
/*      */ 
/*      */     public E remove(int paramInt) {
/*  971 */       rangeCheck(paramInt);
/*  972 */       checkForComodification();
/*  973 */       Object localObject = this.parent.remove(this.parentOffset + paramInt);
/*  974 */       this.modCount = this.parent.modCount;
/*  975 */       this.size -= 1;
/*  976 */       return localObject;
/*      */     }
/*      */ 
/*      */     protected void removeRange(int paramInt1, int paramInt2) {
/*  980 */       checkForComodification();
/*  981 */       this.parent.removeRange(this.parentOffset + paramInt1, this.parentOffset + paramInt2);
/*      */ 
/*  983 */       this.modCount = this.parent.modCount;
/*  984 */       this.size -= paramInt2 - paramInt1;
/*      */     }
/*      */ 
/*      */     public boolean addAll(Collection<? extends E> paramCollection) {
/*  988 */       return addAll(this.size, paramCollection);
/*      */     }
/*      */ 
/*      */     public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
/*  992 */       rangeCheckForAdd(paramInt);
/*  993 */       int i = paramCollection.size();
/*  994 */       if (i == 0) {
/*  995 */         return false;
/*      */       }
/*  997 */       checkForComodification();
/*  998 */       this.parent.addAll(this.parentOffset + paramInt, paramCollection);
/*  999 */       this.modCount = this.parent.modCount;
/* 1000 */       this.size += i;
/* 1001 */       return true;
/*      */     }
/*      */ 
/*      */     public Iterator<E> iterator() {
/* 1005 */       return listIterator();
/*      */     }
/*      */ 
/*      */     public ListIterator<E> listIterator(final int paramInt) {
/* 1009 */       checkForComodification();
/* 1010 */       rangeCheckForAdd(paramInt);
/* 1011 */       final int i = this.offset;
/*      */ 
/* 1013 */       return new ListIterator() {
/* 1014 */         int cursor = paramInt;
/* 1015 */         int lastRet = -1;
/* 1016 */         int expectedModCount = ArrayList.this.modCount;
/*      */ 
/*      */         public boolean hasNext() {
/* 1019 */           return this.cursor != ArrayList.SubList.this.size;
/*      */         }
/*      */ 
/*      */         public E next()
/*      */         {
/* 1024 */           checkForComodification();
/* 1025 */           int i = this.cursor;
/* 1026 */           if (i >= ArrayList.SubList.this.size)
/* 1027 */             throw new NoSuchElementException();
/* 1028 */           Object[] arrayOfObject = ArrayList.this.elementData;
/* 1029 */           if (i + i >= arrayOfObject.length)
/* 1030 */             throw new ConcurrentModificationException();
/* 1031 */           this.cursor = (i + 1);
/* 1032 */           return arrayOfObject[(i + (this.lastRet = i))];
/*      */         }
/*      */ 
/*      */         public boolean hasPrevious() {
/* 1036 */           return this.cursor != 0;
/*      */         }
/*      */ 
/*      */         public E previous()
/*      */         {
/* 1041 */           checkForComodification();
/* 1042 */           int i = this.cursor - 1;
/* 1043 */           if (i < 0)
/* 1044 */             throw new NoSuchElementException();
/* 1045 */           Object[] arrayOfObject = ArrayList.this.elementData;
/* 1046 */           if (i + i >= arrayOfObject.length)
/* 1047 */             throw new ConcurrentModificationException();
/* 1048 */           this.cursor = i;
/* 1049 */           return arrayOfObject[(i + (this.lastRet = i))];
/*      */         }
/*      */ 
/*      */         public int nextIndex() {
/* 1053 */           return this.cursor;
/*      */         }
/*      */ 
/*      */         public int previousIndex() {
/* 1057 */           return this.cursor - 1;
/*      */         }
/*      */ 
/*      */         public void remove() {
/* 1061 */           if (this.lastRet < 0)
/* 1062 */             throw new IllegalStateException();
/* 1063 */           checkForComodification();
/*      */           try
/*      */           {
/* 1066 */             ArrayList.SubList.this.remove(this.lastRet);
/* 1067 */             this.cursor = this.lastRet;
/* 1068 */             this.lastRet = -1;
/* 1069 */             this.expectedModCount = ArrayList.this.modCount;
/*      */           } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/* 1071 */             throw new ConcurrentModificationException();
/*      */           }
/*      */         }
/*      */ 
/*      */         public void set(E paramAnonymousE) {
/* 1076 */           if (this.lastRet < 0)
/* 1077 */             throw new IllegalStateException();
/* 1078 */           checkForComodification();
/*      */           try
/*      */           {
/* 1081 */             ArrayList.this.set(i + this.lastRet, paramAnonymousE);
/*      */           } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/* 1083 */             throw new ConcurrentModificationException();
/*      */           }
/*      */         }
/*      */ 
/*      */         public void add(E paramAnonymousE) {
/* 1088 */           checkForComodification();
/*      */           try
/*      */           {
/* 1091 */             int i = this.cursor;
/* 1092 */             ArrayList.SubList.this.add(i, paramAnonymousE);
/* 1093 */             this.cursor = (i + 1);
/* 1094 */             this.lastRet = -1;
/* 1095 */             this.expectedModCount = ArrayList.this.modCount;
/*      */           } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/* 1097 */             throw new ConcurrentModificationException();
/*      */           }
/*      */         }
/*      */ 
/*      */         final void checkForComodification() {
/* 1102 */           if (this.expectedModCount != ArrayList.this.modCount)
/* 1103 */             throw new ConcurrentModificationException();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public List<E> subList(int paramInt1, int paramInt2) {
/* 1109 */       ArrayList.subListRangeCheck(paramInt1, paramInt2, this.size);
/* 1110 */       return new SubList(ArrayList.this, this, this.offset, paramInt1, paramInt2);
/*      */     }
/*      */ 
/*      */     private void rangeCheck(int paramInt) {
/* 1114 */       if ((paramInt < 0) || (paramInt >= this.size))
/* 1115 */         throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
/*      */     }
/*      */ 
/*      */     private void rangeCheckForAdd(int paramInt) {
/* 1119 */       if ((paramInt < 0) || (paramInt > this.size))
/* 1120 */         throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
/*      */     }
/*      */ 
/*      */     private String outOfBoundsMsg(int paramInt) {
/* 1124 */       return "Index: " + paramInt + ", Size: " + this.size;
/*      */     }
/*      */ 
/*      */     private void checkForComodification() {
/* 1128 */       if (ArrayList.this.modCount != this.modCount)
/* 1129 */         throw new ConcurrentModificationException();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.ArrayList
 * JD-Core Version:    0.6.2
 */