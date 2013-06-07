/*     */ package java.util;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ 
/*     */ public abstract class AbstractCollection<E>
/*     */   implements Collection<E>
/*     */ {
/*     */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*     */ 
/*     */   public abstract Iterator<E> iterator();
/*     */ 
/*     */   public abstract int size();
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  86 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object paramObject)
/*     */   {
/*  99 */     Iterator localIterator = iterator();
/* 100 */     if (paramObject == null) {
/*     */       do if (!localIterator.hasNext())
/*     */           break; while (localIterator.next() != null);
/* 103 */       return true;
/*     */     }
/* 105 */     while (localIterator.hasNext()) {
/* 106 */       if (paramObject.equals(localIterator.next()))
/* 107 */         return true;
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 136 */     Object[] arrayOfObject = new Object[size()];
/* 137 */     Iterator localIterator = iterator();
/* 138 */     for (int i = 0; i < arrayOfObject.length; i++) {
/* 139 */       if (!localIterator.hasNext())
/* 140 */         return Arrays.copyOf(arrayOfObject, i);
/* 141 */       arrayOfObject[i] = localIterator.next();
/*     */     }
/* 143 */     return localIterator.hasNext() ? finishToArray(arrayOfObject, localIterator) : arrayOfObject;
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] paramArrayOfT)
/*     */   {
/* 175 */     int i = size();
/* 176 */     Object[] arrayOfObject = paramArrayOfT.length >= i ? paramArrayOfT : (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
/*     */ 
/* 179 */     Iterator localIterator = iterator();
/*     */ 
/* 181 */     for (int j = 0; j < arrayOfObject.length; j++) {
/* 182 */       if (!localIterator.hasNext()) {
/* 183 */         if (paramArrayOfT != arrayOfObject)
/* 184 */           return Arrays.copyOf(arrayOfObject, j);
/* 185 */         arrayOfObject[j] = null;
/* 186 */         return arrayOfObject;
/*     */       }
/* 188 */       arrayOfObject[j] = localIterator.next();
/*     */     }
/* 190 */     return localIterator.hasNext() ? finishToArray(arrayOfObject, localIterator) : arrayOfObject;
/*     */   }
/*     */ 
/*     */   private static <T> T[] finishToArray(T[] paramArrayOfT, Iterator<?> paramIterator)
/*     */   {
/* 212 */     int i = paramArrayOfT.length;
/* 213 */     while (paramIterator.hasNext()) {
/* 214 */       int j = paramArrayOfT.length;
/* 215 */       if (i == j) {
/* 216 */         int k = j + (j >> 1) + 1;
/*     */ 
/* 218 */         if (k - 2147483639 > 0)
/* 219 */           k = hugeCapacity(j + 1);
/* 220 */         paramArrayOfT = Arrays.copyOf(paramArrayOfT, k);
/*     */       }
/* 222 */       paramArrayOfT[(i++)] = paramIterator.next();
/*     */     }
/*     */ 
/* 225 */     return i == paramArrayOfT.length ? paramArrayOfT : Arrays.copyOf(paramArrayOfT, i);
/*     */   }
/*     */ 
/*     */   private static int hugeCapacity(int paramInt) {
/* 229 */     if (paramInt < 0) {
/* 230 */       throw new OutOfMemoryError("Required array size too large");
/*     */     }
/* 232 */     return paramInt > 2147483639 ? 2147483647 : 2147483639;
/*     */   }
/*     */ 
/*     */   public boolean add(E paramE)
/*     */   {
/* 252 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean remove(Object paramObject)
/*     */   {
/* 272 */     Iterator localIterator = iterator();
/* 273 */     if (paramObject == null) {
/*     */       do if (!localIterator.hasNext())
/*     */           break; while (localIterator.next() != null);
/* 276 */       localIterator.remove();
/* 277 */       return true;
/*     */     }
/*     */ 
/* 281 */     while (localIterator.hasNext()) {
/* 282 */       if (paramObject.equals(localIterator.next())) {
/* 283 */         localIterator.remove();
/* 284 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 288 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> paramCollection)
/*     */   {
/* 307 */     for (Iterator localIterator = paramCollection.iterator(); localIterator.hasNext(); ) { Object localObject = localIterator.next();
/* 308 */       if (!contains(localObject))
/* 309 */         return false; }
/* 310 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends E> paramCollection)
/*     */   {
/* 332 */     boolean bool = false;
/* 333 */     for (Iterator localIterator = paramCollection.iterator(); localIterator.hasNext(); ) { Object localObject = localIterator.next();
/* 334 */       if (add(localObject))
/* 335 */         bool = true; }
/* 336 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> paramCollection)
/*     */   {
/* 361 */     boolean bool = false;
/* 362 */     Iterator localIterator = iterator();
/* 363 */     while (localIterator.hasNext()) {
/* 364 */       if (paramCollection.contains(localIterator.next())) {
/* 365 */         localIterator.remove();
/* 366 */         bool = true;
/*     */       }
/*     */     }
/* 369 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> paramCollection)
/*     */   {
/* 394 */     boolean bool = false;
/* 395 */     Iterator localIterator = iterator();
/* 396 */     while (localIterator.hasNext()) {
/* 397 */       if (!paramCollection.contains(localIterator.next())) {
/* 398 */         localIterator.remove();
/* 399 */         bool = true;
/*     */       }
/*     */     }
/* 402 */     return bool;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 421 */     Iterator localIterator = iterator();
/* 422 */     while (localIterator.hasNext()) {
/* 423 */       localIterator.next();
/* 424 */       localIterator.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 442 */     Iterator localIterator = iterator();
/* 443 */     if (!localIterator.hasNext()) {
/* 444 */       return "[]";
/*     */     }
/* 446 */     StringBuilder localStringBuilder = new StringBuilder();
/* 447 */     localStringBuilder.append('[');
/*     */     while (true) {
/* 449 */       Object localObject = localIterator.next();
/* 450 */       localStringBuilder.append(localObject == this ? "(this Collection)" : localObject);
/* 451 */       if (!localIterator.hasNext())
/* 452 */         return ']';
/* 453 */       localStringBuilder.append(',').append(' ');
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.AbstractCollection
 * JD-Core Version:    0.6.2
 */