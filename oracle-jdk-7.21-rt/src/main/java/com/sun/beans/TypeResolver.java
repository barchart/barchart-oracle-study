/*     */ package com.sun.beans;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
/*     */ import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
/*     */ 
/*     */ public final class TypeResolver
/*     */ {
/* 242 */   private static final WeakCache<Type, TypeResolver> CACHE = new WeakCache();
/*     */ 
/* 244 */   private final Map<TypeVariable<?>, Type> map = new HashMap();
/*     */ 
/*     */   public static Type resolveInClass(Class<?> paramClass, Type paramType)
/*     */   {
/*  78 */     return resolve(getActualType(paramClass), paramType);
/*     */   }
/*     */ 
/*     */   public static Type[] resolveInClass(Class<?> paramClass, Type[] paramArrayOfType)
/*     */   {
/*  93 */     return resolve(getActualType(paramClass), paramArrayOfType);
/*     */   }
/*     */ 
/*     */   public static Type resolve(Type paramType1, Type paramType2)
/*     */   {
/* 157 */     return getTypeResolver(paramType1).resolve(paramType2);
/*     */   }
/*     */ 
/*     */   public static Type[] resolve(Type paramType, Type[] paramArrayOfType)
/*     */   {
/* 172 */     return getTypeResolver(paramType).resolve(paramArrayOfType);
/*     */   }
/*     */ 
/*     */   public static Class<?> erase(Type paramType)
/*     */   {
/* 185 */     if ((paramType instanceof Class))
/* 186 */       return (Class)paramType;
/*     */     Object localObject;
/* 188 */     if ((paramType instanceof ParameterizedType)) {
/* 189 */       localObject = (ParameterizedType)paramType;
/* 190 */       return (Class)((ParameterizedType)localObject).getRawType();
/*     */     }
/*     */     Type[] arrayOfType;
/* 192 */     if ((paramType instanceof TypeVariable)) {
/* 193 */       localObject = (TypeVariable)paramType;
/* 194 */       arrayOfType = ((TypeVariable)localObject).getBounds();
/* 195 */       return 0 < arrayOfType.length ? erase(arrayOfType[0]) : Object.class;
/*     */     }
/*     */ 
/* 199 */     if ((paramType instanceof WildcardType)) {
/* 200 */       localObject = (WildcardType)paramType;
/* 201 */       arrayOfType = ((WildcardType)localObject).getUpperBounds();
/* 202 */       return 0 < arrayOfType.length ? erase(arrayOfType[0]) : Object.class;
/*     */     }
/*     */ 
/* 206 */     if ((paramType instanceof GenericArrayType)) {
/* 207 */       localObject = (GenericArrayType)paramType;
/* 208 */       return Array.newInstance(erase(((GenericArrayType)localObject).getGenericComponentType()), 0).getClass();
/*     */     }
/* 210 */     throw new IllegalArgumentException("Unknown Type kind: " + paramType.getClass());
/*     */   }
/*     */ 
/*     */   public static Class[] erase(Type[] paramArrayOfType)
/*     */   {
/* 223 */     int i = paramArrayOfType.length;
/* 224 */     Class[] arrayOfClass = new Class[i];
/* 225 */     for (int j = 0; j < i; j++) {
/* 226 */       arrayOfClass[j] = erase(paramArrayOfType[j]);
/*     */     }
/* 228 */     return arrayOfClass;
/*     */   }
/*     */ 
/*     */   public static TypeResolver getTypeResolver(Type paramType) {
/* 232 */     synchronized (CACHE) {
/* 233 */       TypeResolver localTypeResolver = (TypeResolver)CACHE.get(paramType);
/* 234 */       if (localTypeResolver == null) {
/* 235 */         localTypeResolver = new TypeResolver(paramType);
/* 236 */         CACHE.put(paramType, localTypeResolver);
/*     */       }
/* 238 */       return localTypeResolver;
/*     */     }
/*     */   }
/*     */ 
/*     */   private TypeResolver(Type paramType)
/*     */   {
/* 254 */     prepare(paramType);
/*     */   }
/*     */ 
/*     */   private void prepare(Type paramType)
/*     */   {
/* 271 */     Class localClass = (Class)((paramType instanceof Class) ? paramType : ((ParameterizedType)paramType).getRawType());
/*     */ 
/* 275 */     TypeVariable[] arrayOfTypeVariable = localClass.getTypeParameters();
/*     */ 
/* 277 */     Type[] arrayOfType = (paramType instanceof Class) ? arrayOfTypeVariable : ((ParameterizedType)paramType).getActualTypeArguments();
/*     */ 
/* 281 */     assert (arrayOfTypeVariable.length == arrayOfType.length);
/* 282 */     for (int i = 0; i < arrayOfTypeVariable.length; i++) {
/* 283 */       this.map.put(arrayOfTypeVariable[i], arrayOfType[i]);
/*     */     }
/* 285 */     Type localType1 = localClass.getGenericSuperclass();
/* 286 */     if (localType1 != null) {
/* 287 */       prepare(localType1);
/*     */     }
/* 289 */     for (Type localType2 : localClass.getGenericInterfaces()) {
/* 290 */       prepare(localType2);
/*     */     }
/*     */ 
/* 294 */     if (((paramType instanceof Class)) && (arrayOfTypeVariable.length > 0))
/* 295 */       for (??? = this.map.entrySet().iterator(); ((Iterator)???).hasNext(); ) { Map.Entry localEntry = (Map.Entry)((Iterator)???).next();
/* 296 */         localEntry.setValue(erase((Type)localEntry.getValue()));
/*     */       }
/*     */   }
/*     */ 
/*     */   private Type resolve(Type paramType)
/*     */   {
/* 309 */     if ((paramType instanceof Class)) {
/* 310 */       return paramType;
/*     */     }
/* 312 */     if ((paramType instanceof GenericArrayType)) {
/* 313 */       localObject = ((GenericArrayType)paramType).getGenericComponentType();
/* 314 */       localObject = resolve((Type)localObject);
/* 315 */       return (localObject instanceof Class) ? Array.newInstance((Class)localObject, 0).getClass() : GenericArrayTypeImpl.make((Type)localObject);
/*     */     }
/*     */     Type[] arrayOfType1;
/* 319 */     if ((paramType instanceof ParameterizedType)) {
/* 320 */       localObject = (ParameterizedType)paramType;
/* 321 */       arrayOfType1 = resolve(((ParameterizedType)localObject).getActualTypeArguments());
/* 322 */       return ParameterizedTypeImpl.make((Class)((ParameterizedType)localObject).getRawType(), arrayOfType1, ((ParameterizedType)localObject).getOwnerType());
/*     */     }
/*     */ 
/* 325 */     if ((paramType instanceof WildcardType)) {
/* 326 */       localObject = (WildcardType)paramType;
/* 327 */       arrayOfType1 = resolve(((WildcardType)localObject).getUpperBounds());
/* 328 */       Type[] arrayOfType2 = resolve(((WildcardType)localObject).getLowerBounds());
/* 329 */       return new WildcardTypeImpl(arrayOfType1, arrayOfType2);
/*     */     }
/* 331 */     if (!(paramType instanceof TypeVariable)) {
/* 332 */       throw new IllegalArgumentException("Bad Type kind: " + paramType.getClass());
/*     */     }
/* 334 */     Object localObject = (Type)this.map.get((TypeVariable)paramType);
/* 335 */     if ((localObject == null) || (localObject.equals(paramType))) {
/* 336 */       return paramType;
/*     */     }
/* 338 */     localObject = fixGenericArray((Type)localObject);
/* 339 */     return resolve((Type)localObject);
/*     */   }
/*     */ 
/*     */   private Type[] resolve(Type[] paramArrayOfType)
/*     */   {
/* 359 */     int i = paramArrayOfType.length;
/* 360 */     Type[] arrayOfType = new Type[i];
/* 361 */     for (int j = 0; j < i; j++) {
/* 362 */       arrayOfType[j] = resolve(paramArrayOfType[j]);
/*     */     }
/* 364 */     return arrayOfType;
/*     */   }
/*     */ 
/*     */   private static Type fixGenericArray(Type paramType)
/*     */   {
/* 386 */     if ((paramType instanceof GenericArrayType)) {
/* 387 */       Type localType = ((GenericArrayType)paramType).getGenericComponentType();
/* 388 */       localType = fixGenericArray(localType);
/* 389 */       if ((localType instanceof Class)) {
/* 390 */         return Array.newInstance((Class)localType, 0).getClass();
/*     */       }
/*     */     }
/* 393 */     return paramType;
/*     */   }
/*     */ 
/*     */   private static Type getActualType(Class<?> paramClass)
/*     */   {
/* 415 */     TypeVariable[] arrayOfTypeVariable = paramClass.getTypeParameters();
/* 416 */     return arrayOfTypeVariable.length == 0 ? paramClass : ParameterizedTypeImpl.make(paramClass, arrayOfTypeVariable, paramClass.getEnclosingClass());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.TypeResolver
 * JD-Core Version:    0.6.2
 */