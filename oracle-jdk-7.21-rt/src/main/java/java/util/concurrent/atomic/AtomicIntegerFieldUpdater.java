/*     */ package java.util.concurrent.atomic;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public abstract class AtomicIntegerFieldUpdater<T>
/*     */ {
/*     */   public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString)
/*     */   {
/*  73 */     return new AtomicIntegerFieldUpdaterImpl(paramClass, paramString);
/*     */   }
/*     */ 
/*     */   public abstract boolean compareAndSet(T paramT, int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract void set(T paramT, int paramInt);
/*     */ 
/*     */   public abstract void lazySet(T paramT, int paramInt);
/*     */ 
/*     */   public abstract int get(T paramT);
/*     */ 
/*     */   public int getAndSet(T paramT, int paramInt)
/*     */   {
/*     */     while (true)
/*     */     {
/* 158 */       int i = get(paramT);
/* 159 */       if (compareAndSet(paramT, i, paramInt))
/* 160 */         return i;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getAndIncrement(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 173 */       int i = get(paramT);
/* 174 */       int j = i + 1;
/* 175 */       if (compareAndSet(paramT, i, j))
/* 176 */         return i;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getAndDecrement(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 189 */       int i = get(paramT);
/* 190 */       int j = i - 1;
/* 191 */       if (compareAndSet(paramT, i, j))
/* 192 */         return i;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getAndAdd(T paramT, int paramInt)
/*     */   {
/*     */     while (true)
/*     */     {
/* 206 */       int i = get(paramT);
/* 207 */       int j = i + paramInt;
/* 208 */       if (compareAndSet(paramT, i, j))
/* 209 */         return i;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int incrementAndGet(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 222 */       int i = get(paramT);
/* 223 */       int j = i + 1;
/* 224 */       if (compareAndSet(paramT, i, j))
/* 225 */         return j;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int decrementAndGet(T paramT)
/*     */   {
/*     */     while (true)
/*     */     {
/* 238 */       int i = get(paramT);
/* 239 */       int j = i - 1;
/* 240 */       if (compareAndSet(paramT, i, j))
/* 241 */         return j;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int addAndGet(T paramT, int paramInt)
/*     */   {
/*     */     while (true)
/*     */     {
/* 255 */       int i = get(paramT);
/* 256 */       int j = i + paramInt;
/* 257 */       if (compareAndSet(paramT, i, j))
/* 258 */         return j;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AtomicIntegerFieldUpdaterImpl<T> extends AtomicIntegerFieldUpdater<T> {
/* 266 */     private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */     private final long offset;
/*     */     private final Class<T> tclass;
/*     */     private final Class cclass;
/*     */ 
/*     */     AtomicIntegerFieldUpdaterImpl(Class<T> paramClass, String paramString) {
/* 272 */       Field localField = null;
/* 273 */       Class localClass1 = null;
/* 274 */       int i = 0;
/*     */       try {
/* 276 */         localField = paramClass.getDeclaredField(paramString);
/* 277 */         localClass1 = Reflection.getCallerClass(3);
/* 278 */         i = localField.getModifiers();
/* 279 */         ReflectUtil.ensureMemberAccess(localClass1, paramClass, null, i);
/*     */ 
/* 281 */         ReflectUtil.checkPackageAccess(paramClass);
/*     */       } catch (Exception localException) {
/* 283 */         throw new RuntimeException(localException);
/*     */       }
/*     */ 
/* 286 */       Class localClass2 = localField.getType();
/* 287 */       if (localClass2 != Integer.TYPE) {
/* 288 */         throw new IllegalArgumentException("Must be integer type");
/*     */       }
/* 290 */       if (!Modifier.isVolatile(i)) {
/* 291 */         throw new IllegalArgumentException("Must be volatile type");
/*     */       }
/* 293 */       this.cclass = ((Modifier.isProtected(i)) && (localClass1 != paramClass) ? localClass1 : null);
/*     */ 
/* 295 */       this.tclass = paramClass;
/* 296 */       this.offset = unsafe.objectFieldOffset(localField);
/*     */     }
/*     */ 
/*     */     private void fullCheck(T paramT) {
/* 300 */       if (!this.tclass.isInstance(paramT))
/* 301 */         throw new ClassCastException();
/* 302 */       if (this.cclass != null)
/* 303 */         ensureProtectedAccess(paramT);
/*     */     }
/*     */ 
/*     */     public boolean compareAndSet(T paramT, int paramInt1, int paramInt2) {
/* 307 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 308 */       return unsafe.compareAndSwapInt(paramT, this.offset, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2) {
/* 312 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 313 */       return unsafe.compareAndSwapInt(paramT, this.offset, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public void set(T paramT, int paramInt) {
/* 317 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 318 */       unsafe.putIntVolatile(paramT, this.offset, paramInt);
/*     */     }
/*     */ 
/*     */     public void lazySet(T paramT, int paramInt) {
/* 322 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 323 */       unsafe.putOrderedInt(paramT, this.offset, paramInt);
/*     */     }
/*     */ 
/*     */     public final int get(T paramT) {
/* 327 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null)) fullCheck(paramT);
/* 328 */       return unsafe.getIntVolatile(paramT, this.offset);
/*     */     }
/*     */ 
/*     */     private void ensureProtectedAccess(T paramT) {
/* 332 */       if (this.cclass.isInstance(paramT)) {
/* 333 */         return;
/*     */       }
/* 335 */       throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + paramT.getClass().getName()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.atomic.AtomicIntegerFieldUpdater
 * JD-Core Version:    0.6.2
 */