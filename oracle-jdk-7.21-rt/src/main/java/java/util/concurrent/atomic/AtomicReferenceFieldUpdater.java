/*     */ package java.util.concurrent.atomic;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public abstract class AtomicReferenceFieldUpdater<T, V>
/*     */ {
/*     */   public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> paramClass, Class<W> paramClass1, String paramString)
/*     */   {
/*  92 */     return new AtomicReferenceFieldUpdaterImpl(paramClass, paramClass1, paramString);
/*     */   }
/*     */ 
/*     */   public abstract boolean compareAndSet(T paramT, V paramV1, V paramV2);
/*     */ 
/*     */   public abstract boolean weakCompareAndSet(T paramT, V paramV1, V paramV2);
/*     */ 
/*     */   public abstract void set(T paramT, V paramV);
/*     */ 
/*     */   public abstract void lazySet(T paramT, V paramV);
/*     */ 
/*     */   public abstract V get(T paramT);
/*     */ 
/*     */   public V getAndSet(T paramT, V paramV)
/*     */   {
/*     */     while (true)
/*     */     {
/* 174 */       Object localObject = get(paramT);
/* 175 */       if (compareAndSet(paramT, localObject, paramV))
/* 176 */         return localObject;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class AtomicReferenceFieldUpdaterImpl<T, V> extends AtomicReferenceFieldUpdater<T, V>
/*     */   {
/* 182 */     private static final Unsafe unsafe = Unsafe.getUnsafe();
/*     */     private final long offset;
/*     */     private final Class<T> tclass;
/*     */     private final Class<V> vclass;
/*     */     private final Class cclass;
/*     */ 
/*     */     AtomicReferenceFieldUpdaterImpl(Class<T> paramClass, Class<V> paramClass1, String paramString)
/*     */     {
/* 203 */       Field localField = null;
/* 204 */       Class localClass1 = null;
/* 205 */       Class localClass2 = null;
/* 206 */       int i = 0;
/*     */       try {
/* 208 */         localField = paramClass.getDeclaredField(paramString);
/* 209 */         localClass2 = Reflection.getCallerClass(3);
/* 210 */         i = localField.getModifiers();
/* 211 */         ReflectUtil.ensureMemberAccess(localClass2, paramClass, null, i);
/*     */ 
/* 213 */         ReflectUtil.checkPackageAccess(paramClass);
/* 214 */         localClass1 = localField.getType();
/*     */       } catch (Exception localException) {
/* 216 */         throw new RuntimeException(localException);
/*     */       }
/*     */ 
/* 219 */       if (paramClass1 != localClass1) {
/* 220 */         throw new ClassCastException();
/*     */       }
/* 222 */       if (!Modifier.isVolatile(i)) {
/* 223 */         throw new IllegalArgumentException("Must be volatile type");
/*     */       }
/* 225 */       this.cclass = ((Modifier.isProtected(i)) && (localClass2 != paramClass) ? localClass2 : null);
/*     */ 
/* 227 */       this.tclass = paramClass;
/* 228 */       if (paramClass1 == Object.class)
/* 229 */         this.vclass = null;
/*     */       else
/* 231 */         this.vclass = paramClass1;
/* 232 */       this.offset = unsafe.objectFieldOffset(localField);
/*     */     }
/*     */ 
/*     */     void targetCheck(T paramT) {
/* 236 */       if (!this.tclass.isInstance(paramT))
/* 237 */         throw new ClassCastException();
/* 238 */       if (this.cclass != null)
/* 239 */         ensureProtectedAccess(paramT);
/*     */     }
/*     */ 
/*     */     void updateCheck(T paramT, V paramV) {
/* 243 */       if ((!this.tclass.isInstance(paramT)) || ((paramV != null) && (this.vclass != null) && (!this.vclass.isInstance(paramV))))
/*     */       {
/* 245 */         throw new ClassCastException();
/* 246 */       }if (this.cclass != null)
/* 247 */         ensureProtectedAccess(paramT);
/*     */     }
/*     */ 
/*     */     public boolean compareAndSet(T paramT, V paramV1, V paramV2) {
/* 251 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null) || ((paramV2 != null) && (this.vclass != null) && (this.vclass != paramV2.getClass())))
/*     */       {
/* 254 */         updateCheck(paramT, paramV2);
/* 255 */       }return unsafe.compareAndSwapObject(paramT, this.offset, paramV1, paramV2);
/*     */     }
/*     */ 
/*     */     public boolean weakCompareAndSet(T paramT, V paramV1, V paramV2)
/*     */     {
/* 260 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null) || ((paramV2 != null) && (this.vclass != null) && (this.vclass != paramV2.getClass())))
/*     */       {
/* 263 */         updateCheck(paramT, paramV2);
/* 264 */       }return unsafe.compareAndSwapObject(paramT, this.offset, paramV1, paramV2);
/*     */     }
/*     */ 
/*     */     public void set(T paramT, V paramV) {
/* 268 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null) || ((paramV != null) && (this.vclass != null) && (this.vclass != paramV.getClass())))
/*     */       {
/* 271 */         updateCheck(paramT, paramV);
/* 272 */       }unsafe.putObjectVolatile(paramT, this.offset, paramV);
/*     */     }
/*     */ 
/*     */     public void lazySet(T paramT, V paramV) {
/* 276 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null) || ((paramV != null) && (this.vclass != null) && (this.vclass != paramV.getClass())))
/*     */       {
/* 279 */         updateCheck(paramT, paramV);
/* 280 */       }unsafe.putOrderedObject(paramT, this.offset, paramV);
/*     */     }
/*     */ 
/*     */     public V get(T paramT) {
/* 284 */       if ((paramT == null) || (paramT.getClass() != this.tclass) || (this.cclass != null))
/* 285 */         targetCheck(paramT);
/* 286 */       return unsafe.getObjectVolatile(paramT, this.offset);
/*     */     }
/*     */ 
/*     */     private void ensureProtectedAccess(T paramT) {
/* 290 */       if (this.cclass.isInstance(paramT)) {
/* 291 */         return;
/*     */       }
/* 293 */       throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + paramT.getClass().getName()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.atomic.AtomicReferenceFieldUpdater
 * JD-Core Version:    0.6.2
 */