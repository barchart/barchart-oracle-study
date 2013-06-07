/*     */ package com.sun.beans.finder;
/*     */ 
/*     */ import com.sun.beans.TypeResolver;
/*     */ import com.sun.beans.WeakCache;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Arrays;
/*     */ import sun.reflect.misc.ReflectUtil;
/*     */ 
/*     */ public final class MethodFinder extends AbstractFinder<Method>
/*     */ {
/*  48 */   private static final WeakCache<Signature, Method> CACHE = new WeakCache();
/*     */   private final String name;
/*     */ 
/*     */   public static Method findMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     throws NoSuchMethodException
/*     */   {
/*  62 */     if (paramString == null) {
/*  63 */       throw new IllegalArgumentException("Method name is not set");
/*     */     }
/*  65 */     PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramArrayOfClass);
/*  66 */     Signature localSignature = new Signature(paramClass, paramString, paramArrayOfClass);
/*     */ 
/*  68 */     Method localMethod = (Method)CACHE.get(localSignature);
/*  69 */     int i = localMethod != null ? 1 : 0;
/*  70 */     if ((i != 0) && (ReflectUtil.isPackageAccessible(localMethod.getDeclaringClass()))) {
/*  71 */       return localMethod;
/*     */     }
/*  73 */     localMethod = findAccessibleMethod((Method)new MethodFinder(paramString, paramArrayOfClass).find(paramClass.getMethods()));
/*  74 */     if (i == 0) {
/*  75 */       CACHE.put(localSignature, localMethod);
/*     */     }
/*  77 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public static Method findInstanceMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     throws NoSuchMethodException
/*     */   {
/*  92 */     Method localMethod = findMethod(paramClass, paramString, paramArrayOfClass);
/*  93 */     if (Modifier.isStatic(localMethod.getModifiers())) {
/*  94 */       throw new NoSuchMethodException("Method '" + paramString + "' is static");
/*     */     }
/*  96 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public static Method findStaticMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
/*     */     throws NoSuchMethodException
/*     */   {
/* 111 */     Method localMethod = findMethod(paramClass, paramString, paramArrayOfClass);
/* 112 */     if (!Modifier.isStatic(localMethod.getModifiers())) {
/* 113 */       throw new NoSuchMethodException("Method '" + paramString + "' is not static");
/*     */     }
/* 115 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public static Method findAccessibleMethod(Method paramMethod)
/*     */     throws NoSuchMethodException
/*     */   {
/* 127 */     Class localClass = paramMethod.getDeclaringClass();
/* 128 */     if ((Modifier.isPublic(localClass.getModifiers())) && (ReflectUtil.isPackageAccessible(localClass))) {
/* 129 */       return paramMethod;
/*     */     }
/* 131 */     if (Modifier.isStatic(paramMethod.getModifiers())) {
/* 132 */       throw new NoSuchMethodException("Method '" + paramMethod.getName() + "' is not accessible");
/*     */     }
/* 134 */     for (Type localType : localClass.getGenericInterfaces()) {
/*     */       try {
/* 136 */         return findAccessibleMethod(paramMethod, localType);
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException)
/*     */       {
/*     */       }
/*     */     }
/* 142 */     return findAccessibleMethod(paramMethod, localClass.getGenericSuperclass());
/*     */   }
/*     */ 
/*     */   private static Method findAccessibleMethod(Method paramMethod, Type paramType)
/*     */     throws NoSuchMethodException
/*     */   {
/* 155 */     String str = paramMethod.getName();
/* 156 */     Class[] arrayOfClass1 = paramMethod.getParameterTypes();
/*     */     Object localObject;
/* 157 */     if ((paramType instanceof Class)) {
/* 158 */       localObject = (Class)paramType;
/* 159 */       return findAccessibleMethod(((Class)localObject).getMethod(str, arrayOfClass1));
/*     */     }
/* 161 */     if ((paramType instanceof ParameterizedType)) {
/* 162 */       localObject = (ParameterizedType)paramType;
/* 163 */       Class localClass = (Class)((ParameterizedType)localObject).getRawType();
/* 164 */       for (Method localMethod : localClass.getMethods()) {
/* 165 */         if (localMethod.getName().equals(str)) {
/* 166 */           Class[] arrayOfClass2 = localMethod.getParameterTypes();
/* 167 */           if (arrayOfClass2.length == arrayOfClass1.length) {
/* 168 */             if (Arrays.equals(arrayOfClass1, arrayOfClass2)) {
/* 169 */               return findAccessibleMethod(localMethod);
/*     */             }
/* 171 */             Type[] arrayOfType = localMethod.getGenericParameterTypes();
/* 172 */             if (Arrays.equals(arrayOfClass1, TypeResolver.erase(TypeResolver.resolve((Type)localObject, arrayOfType)))) {
/* 173 */               return findAccessibleMethod(localMethod);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 179 */     throw new NoSuchMethodException("Method '" + str + "' is not accessible");
/*     */   }
/*     */ 
/*     */   private MethodFinder(String paramString, Class<?>[] paramArrayOfClass)
/*     */   {
/* 192 */     super(paramArrayOfClass);
/* 193 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */   protected Class<?>[] getParameters(Method paramMethod)
/*     */   {
/* 206 */     return paramMethod.getParameterTypes();
/*     */   }
/*     */ 
/*     */   protected boolean isVarArgs(Method paramMethod)
/*     */   {
/* 220 */     return paramMethod.isVarArgs();
/*     */   }
/*     */ 
/*     */   protected boolean isValid(Method paramMethod)
/*     */   {
/* 234 */     return (!paramMethod.isBridge()) && (Modifier.isPublic(paramMethod.getModifiers())) && (paramMethod.getName().equals(this.name));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.finder.MethodFinder
 * JD-Core Version:    0.6.2
 */