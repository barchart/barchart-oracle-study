/*     */ package java.util.concurrent;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractQueue;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.SortedSet;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public class PriorityBlockingQueue<E> extends AbstractQueue<E>
/*     */   implements BlockingQueue<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 5595510919245408276L;
/*     */   private static final int DEFAULT_INITIAL_CAPACITY = 11;
/*     */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*     */   private transient Object[] queue;
/*     */   private transient int size;
/*     */   private transient Comparator<? super E> comparator;
/*     */   private final ReentrantLock lock;
/*     */   private final Condition notEmpty;
/*     */   private volatile transient int allocationSpinLock;
/*     */   private PriorityQueue q;
/*     */   private static final Unsafe UNSAFE;
/*     */   private static final long allocationSpinLockOffset;
/*     */ 
/*     */   public PriorityBlockingQueue()
/*     */   {
/* 179 */     this(11, null);
/*     */   }
/*     */ 
/*     */   public PriorityBlockingQueue(int paramInt)
/*     */   {
/* 192 */     this(paramInt, null);
/*     */   }
/*     */ 
/*     */   public PriorityBlockingQueue(int paramInt, Comparator<? super E> paramComparator)
/*     */   {
/* 209 */     if (paramInt < 1)
/* 210 */       throw new IllegalArgumentException();
/* 211 */     this.lock = new ReentrantLock();
/* 212 */     this.notEmpty = this.lock.newCondition();
/* 213 */     this.comparator = paramComparator;
/* 214 */     this.queue = new Object[paramInt];
/*     */   }
/*     */ 
/*     */   public PriorityBlockingQueue(Collection<? extends E> paramCollection)
/*     */   {
/* 234 */     this.lock = new ReentrantLock();
/* 235 */     this.notEmpty = this.lock.newCondition();
/* 236 */     int i = 1;
/* 237 */     int j = 1;
/* 238 */     if ((paramCollection instanceof SortedSet)) {
/* 239 */       localObject = (SortedSet)paramCollection;
/* 240 */       this.comparator = ((SortedSet)localObject).comparator();
/* 241 */       i = 0;
/*     */     }
/* 243 */     else if ((paramCollection instanceof PriorityBlockingQueue)) {
/* 244 */       localObject = (PriorityBlockingQueue)paramCollection;
/*     */ 
/* 246 */       this.comparator = ((PriorityBlockingQueue)localObject).comparator();
/* 247 */       j = 0;
/* 248 */       if (localObject.getClass() == PriorityBlockingQueue.class)
/* 249 */         i = 0;
/*     */     }
/* 251 */     Object localObject = paramCollection.toArray();
/* 252 */     int k = localObject.length;
/*     */ 
/* 254 */     if (localObject.getClass() != [Ljava.lang.Object.class)
/* 255 */       localObject = Arrays.copyOf((Object[])localObject, k, [Ljava.lang.Object.class);
/* 256 */     if ((j != 0) && ((k == 1) || (this.comparator != null))) {
/* 257 */       for (int m = 0; m < k; m++)
/* 258 */         if (localObject[m] == null)
/* 259 */           throw new NullPointerException();
/*     */     }
/* 261 */     this.queue = ((Object[])localObject);
/* 262 */     this.size = k;
/* 263 */     if (i != 0)
/* 264 */       heapify();
/*     */   }
/*     */ 
/*     */   private void tryGrow(Object[] paramArrayOfObject, int paramInt)
/*     */   {
/* 277 */     this.lock.unlock();
/* 278 */     Object[] arrayOfObject = null;
/* 279 */     if ((this.allocationSpinLock == 0) && (UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1)))
/*     */     {
/*     */       try
/*     */       {
/* 283 */         int i = paramInt + (paramInt < 64 ? paramInt + 2 : paramInt >> 1);
/*     */ 
/* 286 */         if (i - 2147483639 > 0) {
/* 287 */           int j = paramInt + 1;
/* 288 */           if ((j < 0) || (j > 2147483639))
/* 289 */             throw new OutOfMemoryError();
/* 290 */           i = 2147483639;
/*     */         }
/* 292 */         if ((i > paramInt) && (this.queue == paramArrayOfObject))
/* 293 */           arrayOfObject = new Object[i];
/*     */       } finally {
/* 295 */         this.allocationSpinLock = 0;
/*     */       }
/*     */     }
/* 298 */     if (arrayOfObject == null)
/* 299 */       Thread.yield();
/* 300 */     this.lock.lock();
/* 301 */     if ((arrayOfObject != null) && (this.queue == paramArrayOfObject)) {
/* 302 */       this.queue = arrayOfObject;
/* 303 */       System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   private E extract()
/*     */   {
/* 312 */     int i = this.size - 1;
/*     */     E ?;
/* 313 */     if (i < 0) {
/* 314 */       ? = null;
/*     */     } else {
/* 316 */       Object[] arrayOfObject = this.queue;
/* 317 */       ? = arrayOfObject[0];
/* 318 */       Object localObject = arrayOfObject[i];
/* 319 */       arrayOfObject[i] = null;
/* 320 */       Comparator localComparator = this.comparator;
/* 321 */       if (localComparator == null)
/* 322 */         siftDownComparable(0, localObject, arrayOfObject, i);
/*     */       else
/* 324 */         siftDownUsingComparator(0, localObject, arrayOfObject, i, localComparator);
/* 325 */       this.size = i;
/*     */     }
/* 327 */     return ?;
/*     */   }
/*     */ 
/*     */   private static <T> void siftUpComparable(int paramInt, T paramT, Object[] paramArrayOfObject)
/*     */   {
/* 347 */     Comparable localComparable = (Comparable)paramT;
/* 348 */     while (paramInt > 0) {
/* 349 */       int i = paramInt - 1 >>> 1;
/* 350 */       Object localObject = paramArrayOfObject[i];
/* 351 */       if (localComparable.compareTo(localObject) >= 0)
/*     */         break;
/* 353 */       paramArrayOfObject[paramInt] = localObject;
/* 354 */       paramInt = i;
/*     */     }
/* 356 */     paramArrayOfObject[paramInt] = localComparable;
/*     */   }
/*     */ 
/*     */   private static <T> void siftUpUsingComparator(int paramInt, T paramT, Object[] paramArrayOfObject, Comparator<? super T> paramComparator)
/*     */   {
/* 361 */     while (paramInt > 0) {
/* 362 */       int i = paramInt - 1 >>> 1;
/* 363 */       Object localObject = paramArrayOfObject[i];
/* 364 */       if (paramComparator.compare(paramT, localObject) >= 0)
/*     */         break;
/* 366 */       paramArrayOfObject[paramInt] = localObject;
/* 367 */       paramInt = i;
/*     */     }
/* 369 */     paramArrayOfObject[paramInt] = paramT;
/*     */   }
/*     */ 
/*     */   private static <T> void siftDownComparable(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2)
/*     */   {
/* 384 */     Comparable localComparable = (Comparable)paramT;
/* 385 */     int i = paramInt2 >>> 1;
/* 386 */     while (paramInt1 < i) {
/* 387 */       int j = (paramInt1 << 1) + 1;
/* 388 */       Object localObject = paramArrayOfObject[j];
/* 389 */       int k = j + 1;
/* 390 */       if ((k < paramInt2) && (((Comparable)localObject).compareTo(paramArrayOfObject[k]) > 0))
/*     */       {
/* 392 */         localObject = paramArrayOfObject[(j = k)];
/* 393 */       }if (localComparable.compareTo(localObject) <= 0)
/*     */         break;
/* 395 */       paramArrayOfObject[paramInt1] = localObject;
/* 396 */       paramInt1 = j;
/*     */     }
/* 398 */     paramArrayOfObject[paramInt1] = localComparable;
/*     */   }
/*     */ 
/*     */   private static <T> void siftDownUsingComparator(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2, Comparator<? super T> paramComparator)
/*     */   {
/* 404 */     int i = paramInt2 >>> 1;
/* 405 */     while (paramInt1 < i) {
/* 406 */       int j = (paramInt1 << 1) + 1;
/* 407 */       Object localObject = paramArrayOfObject[j];
/* 408 */       int k = j + 1;
/* 409 */       if ((k < paramInt2) && (paramComparator.compare(localObject, paramArrayOfObject[k]) > 0))
/* 410 */         localObject = paramArrayOfObject[(j = k)];
/* 411 */       if (paramComparator.compare(paramT, localObject) <= 0)
/*     */         break;
/* 413 */       paramArrayOfObject[paramInt1] = localObject;
/* 414 */       paramInt1 = j;
/*     */     }
/* 416 */     paramArrayOfObject[paramInt1] = paramT;
/*     */   }
/*     */ 
/*     */   private void heapify()
/*     */   {
/* 424 */     Object[] arrayOfObject = this.queue;
/* 425 */     int i = this.size;
/* 426 */     int j = (i >>> 1) - 1;
/* 427 */     Comparator localComparator = this.comparator;
/*     */     int k;
/* 428 */     if (localComparator == null) {
/* 429 */       for (k = j; k >= 0; k--)
/* 430 */         siftDownComparable(k, arrayOfObject[k], arrayOfObject, i);
/*     */     }
/*     */     else
/* 433 */       for (k = j; k >= 0; k--)
/* 434 */         siftDownUsingComparator(k, arrayOfObject[k], arrayOfObject, i, localComparator);
/*     */   }
/*     */ 
/*     */   public boolean add(E paramE)
/*     */   {
/* 449 */     return offer(paramE);
/*     */   }
/*     */ 
/*     */   public boolean offer(E paramE)
/*     */   {
/* 464 */     if (paramE == null)
/* 465 */       throw new NullPointerException();
/* 466 */     ReentrantLock localReentrantLock = this.lock;
/* 467 */     localReentrantLock.lock();
/*     */     int i;
/*     */     Object[] arrayOfObject;
/*     */     int j;
/* 470 */     while ((i = this.size) >= (j = (arrayOfObject = this.queue).length))
/* 471 */       tryGrow(arrayOfObject, j);
/*     */     try {
/* 473 */       Comparator localComparator = this.comparator;
/* 474 */       if (localComparator == null)
/* 475 */         siftUpComparable(i, paramE, arrayOfObject);
/*     */       else
/* 477 */         siftUpUsingComparator(i, paramE, arrayOfObject, localComparator);
/* 478 */       this.size = (i + 1);
/* 479 */       this.notEmpty.signal();
/*     */     } finally {
/* 481 */       localReentrantLock.unlock();
/*     */     }
/* 483 */     return true;
/*     */   }
/*     */ 
/*     */   public void put(E paramE)
/*     */   {
/* 497 */     offer(paramE);
/*     */   }
/*     */ 
/*     */   public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
/*     */   {
/* 516 */     return offer(paramE);
/*     */   }
/* 520 */   public E poll() { ReentrantLock localReentrantLock = this.lock;
/* 521 */     localReentrantLock.lock();
/*     */     Object localObject1;
/*     */     try {
/* 524 */       localObject1 = extract();
/*     */     } finally {
/* 526 */       localReentrantLock.unlock();
/*     */     }
/* 528 */     return localObject1; } 
/*     */   public E take() throws InterruptedException {
/* 532 */     ReentrantLock localReentrantLock = this.lock;
/* 533 */     localReentrantLock.lockInterruptibly();
/*     */     Object localObject1;
/*     */     try {
/* 536 */       while ((localObject1 = extract()) == null)
/* 537 */         this.notEmpty.await();
/*     */     } finally {
/* 539 */       localReentrantLock.unlock();
/*     */     }
/* 541 */     return localObject1;
/*     */   }
/* 545 */   public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { long l = paramTimeUnit.toNanos(paramLong);
/* 546 */     ReentrantLock localReentrantLock = this.lock;
/* 547 */     localReentrantLock.lockInterruptibly();
/*     */     Object localObject1;
/*     */     try { while (((localObject1 = extract()) == null) && (l > 0L))
/* 551 */         l = this.notEmpty.awaitNanos(l);
/*     */     } finally {
/* 553 */       localReentrantLock.unlock();
/*     */     }
/* 555 */     return localObject1; } 
/*     */   public E peek() {
/* 559 */     ReentrantLock localReentrantLock = this.lock;
/* 560 */     localReentrantLock.lock();
/*     */     E ?;
/*     */     try {
/* 563 */       ? = this.size > 0 ? this.queue[0] : null;
/*     */     } finally {
/* 565 */       localReentrantLock.unlock();
/*     */     }
/* 567 */     return ?;
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/* 580 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 584 */     ReentrantLock localReentrantLock = this.lock;
/* 585 */     localReentrantLock.lock();
/*     */     try {
/* 587 */       return this.size;
/*     */     } finally {
/* 589 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int remainingCapacity()
/*     */   {
/* 599 */     return 2147483647;
/*     */   }
/*     */ 
/*     */   private int indexOf(Object paramObject) {
/* 603 */     if (paramObject != null) {
/* 604 */       Object[] arrayOfObject = this.queue;
/* 605 */       int i = this.size;
/* 606 */       for (int j = 0; j < i; j++)
/* 607 */         if (paramObject.equals(arrayOfObject[j]))
/* 608 */           return j;
/*     */     }
/* 610 */     return -1;
/*     */   }
/*     */ 
/*     */   private void removeAt(int paramInt)
/*     */   {
/* 617 */     Object[] arrayOfObject = this.queue;
/* 618 */     int i = this.size - 1;
/* 619 */     if (i == paramInt) {
/* 620 */       arrayOfObject[paramInt] = null;
/*     */     } else {
/* 622 */       Object localObject = arrayOfObject[i];
/* 623 */       arrayOfObject[i] = null;
/* 624 */       Comparator localComparator = this.comparator;
/* 625 */       if (localComparator == null)
/* 626 */         siftDownComparable(paramInt, localObject, arrayOfObject, i);
/*     */       else
/* 628 */         siftDownUsingComparator(paramInt, localObject, arrayOfObject, i, localComparator);
/* 629 */       if (arrayOfObject[paramInt] == localObject) {
/* 630 */         if (localComparator == null)
/* 631 */           siftUpComparable(paramInt, localObject, arrayOfObject);
/*     */         else
/* 633 */           siftUpUsingComparator(paramInt, localObject, arrayOfObject, localComparator);
/*     */       }
/*     */     }
/* 636 */     this.size = i;
/*     */   }
/*     */ 
/*     */   public boolean remove(Object paramObject)
/*     */   {
/* 651 */     boolean bool = false;
/* 652 */     ReentrantLock localReentrantLock = this.lock;
/* 653 */     localReentrantLock.lock();
/*     */     try {
/* 655 */       int i = indexOf(paramObject);
/* 656 */       if (i != -1) {
/* 657 */         removeAt(i);
/* 658 */         bool = true;
/*     */       }
/*     */     } finally {
/* 661 */       localReentrantLock.unlock();
/*     */     }
/* 663 */     return bool;
/*     */   }
/*     */ 
/*     */   private void removeEQ(Object paramObject)
/*     */   {
/* 671 */     ReentrantLock localReentrantLock = this.lock;
/* 672 */     localReentrantLock.lock();
/*     */     try {
/* 674 */       Object[] arrayOfObject = this.queue;
/* 675 */       int i = this.size;
/* 676 */       for (int j = 0; j < i; j++)
/* 677 */         if (paramObject == arrayOfObject[j]) {
/* 678 */           removeAt(j);
/* 679 */           break;
/*     */         }
/*     */     }
/*     */     finally {
/* 683 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean contains(Object paramObject)
/*     */   {
/* 697 */     ReentrantLock localReentrantLock = this.lock;
/* 698 */     localReentrantLock.lock();
/*     */     int i;
/*     */     try
/*     */     {
/* 700 */       i = indexOf(paramObject);
/*     */     } finally {
/* 702 */       localReentrantLock.unlock();
/*     */     }
/* 704 */     return i != -1;
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 721 */     ReentrantLock localReentrantLock = this.lock;
/* 722 */     localReentrantLock.lock();
/*     */     try {
/* 724 */       return Arrays.copyOf(this.queue, this.size);
/*     */     } finally {
/* 726 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 732 */     ReentrantLock localReentrantLock = this.lock;
/* 733 */     localReentrantLock.lock();
/*     */     try {
/* 735 */       int i = this.size;
/* 736 */       if (i == 0)
/* 737 */         return "[]";
/* 738 */       Object localObject1 = new StringBuilder();
/* 739 */       ((StringBuilder)localObject1).append('[');
/* 740 */       for (int j = 0; j < i; j++) {
/* 741 */         Object localObject2 = this.queue[j];
/* 742 */         ((StringBuilder)localObject1).append(localObject2 == this ? "(this Collection)" : localObject2);
/* 743 */         if (j != i - 1)
/* 744 */           ((StringBuilder)localObject1).append(',').append(' ');
/*     */       }
/* 746 */       return ']';
/*     */     } finally {
/* 748 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int drainTo(Collection<? super E> paramCollection)
/*     */   {
/* 759 */     if (paramCollection == null)
/* 760 */       throw new NullPointerException();
/* 761 */     if (paramCollection == this)
/* 762 */       throw new IllegalArgumentException();
/* 763 */     ReentrantLock localReentrantLock = this.lock;
/* 764 */     localReentrantLock.lock();
/*     */     try {
/* 766 */       int i = 0;
/*     */       Object localObject1;
/* 768 */       while ((localObject1 = extract()) != null) {
/* 769 */         paramCollection.add(localObject1);
/* 770 */         i++;
/*     */       }
/* 772 */       return i;
/*     */     } finally {
/* 774 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int drainTo(Collection<? super E> paramCollection, int paramInt)
/*     */   {
/* 785 */     if (paramCollection == null)
/* 786 */       throw new NullPointerException();
/* 787 */     if (paramCollection == this)
/* 788 */       throw new IllegalArgumentException();
/* 789 */     if (paramInt <= 0)
/* 790 */       return 0;
/* 791 */     ReentrantLock localReentrantLock = this.lock;
/* 792 */     localReentrantLock.lock();
/*     */     try {
/* 794 */       int i = 0;
/*     */       Object localObject1;
/* 796 */       while ((i < paramInt) && ((localObject1 = extract()) != null)) {
/* 797 */         paramCollection.add(localObject1);
/* 798 */         i++;
/*     */       }
/* 800 */       return i;
/*     */     } finally {
/* 802 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 811 */     ReentrantLock localReentrantLock = this.lock;
/* 812 */     localReentrantLock.lock();
/*     */     try {
/* 814 */       Object[] arrayOfObject = this.queue;
/* 815 */       int i = this.size;
/* 816 */       this.size = 0;
/* 817 */       for (int j = 0; j < i; j++)
/* 818 */         arrayOfObject[j] = null;
/*     */     } finally {
/* 820 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] paramArrayOfT)
/*     */   {
/* 862 */     ReentrantLock localReentrantLock = this.lock;
/* 863 */     localReentrantLock.lock();
/*     */     try {
/* 865 */       int i = this.size;
/*     */       Object localObject1;
/* 866 */       if (paramArrayOfT.length < i)
/*     */       {
/* 868 */         return (Object[])Arrays.copyOf(this.queue, this.size, paramArrayOfT.getClass());
/* 869 */       }System.arraycopy(this.queue, 0, paramArrayOfT, 0, i);
/* 870 */       if (paramArrayOfT.length > i)
/* 871 */         paramArrayOfT[i] = null;
/* 872 */       return paramArrayOfT;
/*     */     } finally {
/* 874 */       localReentrantLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator<E> iterator()
/*     */   {
/* 892 */     return new Itr(toArray());
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 935 */     this.lock.lock();
/*     */     try {
/* 937 */       int i = this.size;
/* 938 */       this.q = new PriorityQueue(i == 0 ? 1 : i, this.comparator);
/* 939 */       this.q.addAll(this);
/* 940 */       paramObjectOutputStream.defaultWriteObject();
/*     */     } finally {
/* 942 */       this.q = null;
/* 943 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 956 */       paramObjectInputStream.defaultReadObject();
/* 957 */       this.queue = new Object[this.q.size()];
/* 958 */       this.comparator = this.q.comparator();
/* 959 */       addAll(this.q);
/*     */     } finally {
/* 961 */       this.q = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 970 */       UNSAFE = Unsafe.getUnsafe();
/* 971 */       PriorityBlockingQueue localPriorityBlockingQueue = PriorityBlockingQueue.class;
/* 972 */       allocationSpinLockOffset = UNSAFE.objectFieldOffset(localPriorityBlockingQueue.getDeclaredField("allocationSpinLock"));
/*     */     }
/*     */     catch (Exception localException) {
/* 975 */       throw new Error(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   final class Itr
/*     */     implements Iterator<E>
/*     */   {
/*     */     final Object[] array;
/*     */     int cursor;
/* 904 */     int lastRet = -1;
/*     */ 
/*     */     Itr(Object[] arg2)
/*     */     {
/*     */       Object localObject;
/* 905 */       this.array = localObject;
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 909 */       return this.cursor < this.array.length;
/*     */     }
/*     */ 
/*     */     public E next() {
/* 913 */       if (this.cursor >= this.array.length)
/* 914 */         throw new NoSuchElementException();
/* 915 */       this.lastRet = this.cursor;
/* 916 */       return this.array[(this.cursor++)];
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 920 */       if (this.lastRet < 0)
/* 921 */         throw new IllegalStateException();
/* 922 */       PriorityBlockingQueue.this.removeEQ(this.array[this.lastRet]);
/* 923 */       this.lastRet = -1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.PriorityBlockingQueue
 * JD-Core Version:    0.6.2
 */