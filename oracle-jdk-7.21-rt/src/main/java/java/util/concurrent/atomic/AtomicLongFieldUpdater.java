/*     */ package java.util.concurrent.atomic;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public abstract class AtomicLongFieldUpdater<T>
/*     */ {
/*     */   public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString)
/*     */   {
/*  73 */     if (AtomicLong.VM_SUPPORTS_LONG_CAS) {
/*  74 */       return new CASUpdater(paramClass, paramString);
/*     */     }
/*  76 */     return new LockedUpdater(paramClass, paramString);
/*     */   }
/*     */ 
/*     */   public abstract boolean compareAndSet(T paramT, long paramLong1, long paramLong2);
/*     */ 
/*     */   public abstract boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2);
/*     */ 
/*     */   public abstract void set(T paramT, long paramLong);
/*     */ 
/*     */   public abstract void lazySet(T paramT, long paramLong);
/*     */ 
/*     */   public abstract long get(T paramT);
/*     */ 
/*     */   public long getAndSet(T paramT, long paramLong)
/*     */   {
/*     */     while (true)
/*     */     {
/* 160 */       long l = get(paramT);
/* 161 */       if (compareAndSet(paramT, l, paramLong))
/* 162 */         return l;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getAndIncrement(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 175 */       long l1 = get(paramT);
/* 176 */       long l2 = l1 + 1L;
/* 177 */       if (compareAndSet(paramT, l1, l2))
/* 178 */         return l1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getAndDecrement(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 191 */       long l1 = get(paramT);
/* 192 */       long l2 = l1 - 1L;
/* 193 */       if (compareAndSet(paramT, l1, l2))
/* 194 */         return l1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getAndAdd(T paramT, long paramLong)
/*     */   {
/*     */     while (true)
/*     */     {
/* 208 */       long l1 = get(paramT);
/* 209 */       long l2 = l1 + paramLong;
/* 210 */       if (compareAndSet(paramT, l1, l2))
/* 211 */         return l1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long incrementAndGet(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 224 */       long l1 = get(paramT);
/* 225 */       long l2 = l1 + 1L;
/* 226 */       if (compareAndSet(paramT, l1, l2))
/* 227 */         return l2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long decrementAndGet(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 240 */       long l1 = get(paramT);
/* 241 */       long l2 = l1 - 1L;
/* 242 */       if (compareAndSet(paramT, l1, l2))
/* 243 */         return l2;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long addAndGet(T paramT, long paramLong)
/*     */   {
/*     */     while (true)
/*     */     {
/* 257 */       long l1 = get(paramT);
/* 258 */       long l2 = l1 + paramLong;
/* 259 */       if (compareAndSet(paramT, l1, l2))
/* 260 */         return l2;  } 
/*     */   }
/*     */ 
/* 265 */   private static class CASUpdater<T> extends AtomicLongFieldUpdater<T> { private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */     private final long offset;
/*     */     private final Class<T> tclass;
/*     */     private final Class cclass;
/*     */ 
/* 271 */     CASUpdater(Class<T> paramClass, String paramString) { Field localField = null;
/* 272 */       Class localClass1 = null;
/* 273 */       int i = 0;
/*     */       try {
/* 275 */         localField = paramClass.getDeclaredField(paramString);
/* 276 */         localClass1 = Reflection.getCallerClass(3);
/* 277 */         i = localField.getModifiers();
/* 278 */         ReflectUtil.ensureMemberAccess(localClass1, paramClass, null, i);
/*     */ 
/* 280 */         ReflectUtil.checkPackageAccess(paramClass);
/*     */       } catch (Exception localException) {
/* 282 */         throw new RuntimeException(localException);
/*     */       }
/*     */ 
/* 285 */       Class localClass2 = localField.getType();
/* 286 */       if (localClass2 != Long.TYPE) {
/* 287 */         throw new IllegalArgumentException("Must be long type");
/*     */       }
/* 289 */       if (!Modifier.isVolatile(i)) {
/* 290 */         throw new IllegalArgumentException("Must be volatile type");
/*     */       }
/* 292 */       this.cclass = ((Modifier.isProtected(i)) && (localClass1 != paramClass) ? localClass1 : null);
/*     */ 
/* 294 */       this.tclass = paramClass;
/* 295 */       this.offset = unsafe.objectFieldOffset(localField); }
/*     */ 
/*     */     private void fullCheck(T paramT)
/*     */     {
/* 299 */       if (!this.tclass.isInstance(paramT))
/* 300 */         throw new ClassCastException();
/* 301 */       if (this.cclass != null)
/* 302 */         ensureProtectedAccess(paramT);
/*     */     }
/*     */ 
/*     */     public boolean compareAndSet(T paramT, long paramLong1, long paramLong2) {
/* 306 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 307 */       return unsafe.compareAndSwapLong(paramT, this.offset, paramLong1, paramLong2);
/*     */     }
/*     */ 
/*     */     public boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2) {
/* 311 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 312 */       return unsafe.compareAndSwapLong(paramT, this.offset, paramLong1, paramLong2);
/*     */     }
/*     */ 
/*     */     public void set(T paramT, long paramLong) {
/* 316 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 317 */       unsafe.putLongVolatile(paramT, this.offset, paramLong);
/*     */     }
/*     */ 
/*     */     public void lazySet(T paramT, long paramLong) {
/* 321 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 322 */       unsafe.putOrderedLong(paramT, this.offset, paramLong);
/*     */     }
/*     */ 
/*     */     public long get(T paramT) {
/* 326 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 327 */       return unsafe.getLongVolatile(paramT, this.offset);
/*     */     }
/*     */ 
/*     */     private void ensureProtectedAccess(T paramT) {
/* 331 */       if (this.cclass.isInstance(paramT)) {
/* 332 */         return;
/*     */       }
/* 334 */       throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + paramT.getClass().getName()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LockedUpdater<T> extends AtomicLongFieldUpdater<T>
/*     */   {
/* 348 */     private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */     private final long offset;
/*     */     private final Class<T> tclass;
/*     */     private final Class cclass;
/*     */ 
/*     */     LockedUpdater(Class<T> paramClass, String paramString)
/*     */     {
/* 354 */       Field localField = null;
/* 355 */       Class localClass1 = null;
/* 356 */       int i = 0;
/*     */       try {
/* 358 */         localField = paramClass.getDeclaredField(paramString);
/* 359 */         localClass1 = Reflection.getCallerClass(3);
/* 360 */         i = localField.getModifiers();
/* 361 */         ReflectUtil.ensureMemberAccess(localClass1, paramClass, null, i);
/*     */ 
/* 363 */         ReflectUtil.checkPackageAccess(paramClass);
/*     */       } catch (Exception localException) {
/* 365 */         throw new RuntimeException(localException);
/*     */       }
/*     */ 
/* 368 */       Class localClass2 = localField.getType();
/* 369 */       if (localClass2 != Long.TYPE) {
/* 370 */         throw new IllegalArgumentException("Must be long type");
/*     */       }
/* 372 */       if (!Modifier.isVolatile(i)) {
/* 373 */         throw new IllegalArgumentException("Must be volatile type");
/*     */       }
/* 375 */       this.cclass = ((Modifier.isProtected(i)) && (localClass1 != paramClass) ? localClass1 : null);
/*     */ 
/* 377 */       this.tclass = paramClass;
/* 378 */       this.offset = unsafe.objectFieldOffset(localField);
/*     */     }
/*     */ 
/*     */     private void fullCheck(T paramT) {
/* 382 */       if (!this.tclass.isInstance(paramT))
/* 383 */         throw new ClassCastException();
/* 384 */       if (this.cclass != null)
/* 385 */         ensureProtectedAccess(paramT);
/*     */     }
/*     */ 
/*     */     public boolean compareAndSet(T paramT, long paramLong1, long paramLong2) {
/* 389 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 390 */       synchronized (this) {
/* 391 */         long l = unsafe.getLong(paramT, this.offset);
/* 392 */         if (l != paramLong1)
/* 393 */           return false;
/* 394 */         unsafe.putLong(paramT, this.offset, paramLong2);
/* 395 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2) {
/* 400 */       return compareAndSet(paramT, paramLong1, paramLong2);
/*     */     }
/*     */ 
/*     */     public void set(T paramT, long paramLong) {
/* 404 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 405 */       synchronized (this) {
/* 406 */         unsafe.putLong(paramT, this.offset, paramLong);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void lazySet(T paramT, long paramLong) {
/* 411 */       set(paramT, paramLong);
/*     */     }
/*     */ 
/*     */     public long get(T paramT) {
/* 415 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 416 */       synchronized (this) {
/* 417 */         return unsafe.getLong(paramT, this.offset);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void ensureProtectedAccess(T paramT) {
/* 422 */       if (this.cclass.isInstance(paramT)) {
/* 423 */         return;
/*     */       }
/* 425 */       throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + paramT.getClass().getName()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.atomic.AtomicLongFieldUpdater
 * JD-Core Version:    0.6.2
 */