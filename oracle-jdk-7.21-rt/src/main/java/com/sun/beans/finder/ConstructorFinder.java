/*     */ package com.sun.beans.finder;
/*     */ 
/*     */ import com.sun.beans.WeakCache;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Modifier;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public final class ConstructorFinder extends AbstractFinder<Constructor<?>>
/*     */ {
/*  44 */   private static final WeakCache<Signature, Constructor<?>> CACHE = new WeakCache();
/*     */ 
/*     */   public static Constructor<?> findConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */     throws NoSuchMethodException
/*     */   {
/*  57 */     if (paramClass.isPrimitive()) {
/*  58 */       throw new NoSuchMethodException("Primitive wrapper does not contain constructors");
/*     */     }
/*  60 */     if (paramClass.isInterface()) {
/*  61 */       throw new NoSuchMethodException("Interface does not contain constructors");
/*     */     }
/*  63 */     if (Modifier.isAbstract(paramClass.getModifiers())) {
/*  64 */       throw new NoSuchMethodException("Abstract class cannot be instantiated");
/*     */     }
/*  66 */     if ((!Modifier.isPublic(paramClass.getModifiers())) || (!ReflectUtil.isPackageAccessible(paramClass))) {
/*  67 */       throw new NoSuchMethodException("Class is not accessible");
/*     */     }
/*  69 */     PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramArrayOfClass);
/*  70 */     Signature localSignature = new Signature(paramClass, paramArrayOfClass);
/*     */ 
/*  72 */     Constructor localConstructor = (Constructor)CACHE.get(localSignature);
/*  73 */     if (localConstructor != null) {
/*  74 */       return localConstructor;
/*     */     }
/*  76 */     localConstructor = (Constructor)new ConstructorFinder(paramArrayOfClass).find(paramClass.getConstructors());
/*  77 */     CACHE.put(localSignature, localConstructor);
/*  78 */     return localConstructor;
/*     */   }
/*     */ 
/*     */   private ConstructorFinder(Class<?>[] paramArrayOfClass)
/*     */   {
/*  87 */     super(paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   protected Class<?>[] getParameters(Constructor<?> paramConstructor)
/*     */   {
/* 100 */     return paramConstructor.getParameterTypes();
/*     */   }
/*     */ 
/*     */   protected boolean isVarArgs(Constructor<?> paramConstructor)
/*     */   {
/* 114 */     return paramConstructor.isVarArgs();
/*     */   }
/*     */ 
/*     */   protected boolean isValid(Constructor<?> paramConstructor)
/*     */   {
/* 127 */     return Modifier.isPublic(paramConstructor.getModifiers());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.finder.ConstructorFinder
 * JD-Core Version:    0.6.2
 */