/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class IndexedPropertyDescriptor extends PropertyDescriptor
/*     */ {
/*     */   private Reference<Class> indexedPropertyTypeRef;
/*     */   private Reference<Method> indexedReadMethodRef;
/*     */   private Reference<Method> indexedWriteMethodRef;
/*     */   private String indexedReadMethodName;
/*     */   private String indexedWriteMethodName;
/*     */ 
/*     */   public IndexedPropertyDescriptor(String paramString, Class<?> paramClass)
/*     */     throws IntrospectionException
/*     */   {
/*  67 */     this(paramString, paramClass, "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString), "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString));
/*     */   }
/*     */ 
/*     */   public IndexedPropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3, String paramString4, String paramString5)
/*     */     throws IntrospectionException
/*     */   {
/* 100 */     super(paramString1, paramClass, paramString2, paramString3);
/*     */ 
/* 102 */     this.indexedReadMethodName = paramString4;
/* 103 */     if ((paramString4 != null) && (getIndexedReadMethod() == null)) {
/* 104 */       throw new IntrospectionException("Method not found: " + paramString4);
/*     */     }
/*     */ 
/* 107 */     this.indexedWriteMethodName = paramString5;
/* 108 */     if ((paramString5 != null) && (getIndexedWriteMethod() == null)) {
/* 109 */       throw new IntrospectionException("Method not found: " + paramString5);
/*     */     }
/*     */ 
/* 112 */     findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
/*     */   }
/*     */ 
/*     */   public IndexedPropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4)
/*     */     throws IntrospectionException
/*     */   {
/* 134 */     super(paramString, paramMethod1, paramMethod2);
/*     */ 
/* 136 */     setIndexedReadMethod0(paramMethod3);
/* 137 */     setIndexedWriteMethod0(paramMethod4);
/*     */ 
/* 140 */     setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
/*     */   }
/*     */ 
/*     */   IndexedPropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4)
/*     */     throws IntrospectionException
/*     */   {
/* 158 */     super(paramClass, paramString, paramMethod1, paramMethod2);
/*     */ 
/* 160 */     setIndexedReadMethod0(paramMethod3);
/* 161 */     setIndexedWriteMethod0(paramMethod4);
/*     */ 
/* 164 */     setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
/*     */   }
/*     */ 
/*     */   public synchronized Method getIndexedReadMethod()
/*     */   {
/* 176 */     Method localMethod = getIndexedReadMethod0();
/* 177 */     if (localMethod == null) {
/* 178 */       Class localClass = getClass0();
/* 179 */       if ((localClass == null) || ((this.indexedReadMethodName == null) && (this.indexedReadMethodRef == null)))
/*     */       {
/* 182 */         return null;
/*     */       }
/* 184 */       if (this.indexedReadMethodName == null) {
/* 185 */         localObject = getIndexedPropertyType0();
/* 186 */         if ((localObject == Boolean.TYPE) || (localObject == null))
/* 187 */           this.indexedReadMethodName = ("is" + getBaseName());
/*     */         else {
/* 189 */           this.indexedReadMethodName = ("get" + getBaseName());
/*     */         }
/*     */       }
/*     */ 
/* 193 */       Object localObject = { Integer.TYPE };
/* 194 */       localMethod = Introspector.findMethod(localClass, this.indexedReadMethodName, 1, (Class[])localObject);
/* 195 */       if (localMethod == null)
/*     */       {
/* 197 */         this.indexedReadMethodName = ("get" + getBaseName());
/* 198 */         localMethod = Introspector.findMethod(localClass, this.indexedReadMethodName, 1, (Class[])localObject);
/*     */       }
/* 200 */       setIndexedReadMethod0(localMethod);
/*     */     }
/* 202 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public synchronized void setIndexedReadMethod(Method paramMethod)
/*     */     throws IntrospectionException
/*     */   {
/* 214 */     setIndexedPropertyType(findIndexedPropertyType(paramMethod, getIndexedWriteMethod0()));
/*     */ 
/* 216 */     setIndexedReadMethod0(paramMethod);
/*     */   }
/*     */ 
/*     */   private void setIndexedReadMethod0(Method paramMethod) {
/* 220 */     if (paramMethod == null) {
/* 221 */       this.indexedReadMethodName = null;
/* 222 */       this.indexedReadMethodRef = null;
/* 223 */       return;
/*     */     }
/* 225 */     setClass0(paramMethod.getDeclaringClass());
/*     */ 
/* 227 */     this.indexedReadMethodName = paramMethod.getName();
/* 228 */     this.indexedReadMethodRef = getSoftReference(paramMethod);
/* 229 */     setTransient((Transient)paramMethod.getAnnotation(Transient.class));
/*     */   }
/*     */ 
/*     */   public synchronized Method getIndexedWriteMethod()
/*     */   {
/* 241 */     Method localMethod = getIndexedWriteMethod0();
/* 242 */     if (localMethod == null) {
/* 243 */       Class localClass1 = getClass0();
/* 244 */       if ((localClass1 == null) || ((this.indexedWriteMethodName == null) && (this.indexedWriteMethodRef == null)))
/*     */       {
/* 247 */         return null;
/*     */       }
/*     */ 
/* 253 */       Class localClass2 = getIndexedPropertyType0();
/* 254 */       if (localClass2 == null) {
/*     */         try {
/* 256 */           localClass2 = findIndexedPropertyType(getIndexedReadMethod(), null);
/* 257 */           setIndexedPropertyType(localClass2);
/*     */         }
/*     */         catch (IntrospectionException localIntrospectionException) {
/* 260 */           Class localClass3 = getPropertyType();
/* 261 */           if (localClass3.isArray()) {
/* 262 */             localClass2 = localClass3.getComponentType();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 267 */       if (this.indexedWriteMethodName == null) {
/* 268 */         this.indexedWriteMethodName = ("set" + getBaseName());
/*     */       }
/*     */ 
/* 271 */       Class[] arrayOfClass = { Integer.TYPE, localClass2 == null ? null : localClass2 };
/* 272 */       localMethod = Introspector.findMethod(localClass1, this.indexedWriteMethodName, 2, arrayOfClass);
/* 273 */       if ((localMethod != null) && 
/* 274 */         (!localMethod.getReturnType().equals(Void.TYPE))) {
/* 275 */         localMethod = null;
/*     */       }
/*     */ 
/* 278 */       setIndexedWriteMethod0(localMethod);
/*     */     }
/* 280 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public synchronized void setIndexedWriteMethod(Method paramMethod)
/*     */     throws IntrospectionException
/*     */   {
/* 292 */     Class localClass = findIndexedPropertyType(getIndexedReadMethod(), paramMethod);
/*     */ 
/* 294 */     setIndexedPropertyType(localClass);
/* 295 */     setIndexedWriteMethod0(paramMethod);
/*     */   }
/*     */ 
/*     */   private void setIndexedWriteMethod0(Method paramMethod) {
/* 299 */     if (paramMethod == null) {
/* 300 */       this.indexedWriteMethodName = null;
/* 301 */       this.indexedWriteMethodRef = null;
/* 302 */       return;
/*     */     }
/* 304 */     setClass0(paramMethod.getDeclaringClass());
/*     */ 
/* 306 */     this.indexedWriteMethodName = paramMethod.getName();
/* 307 */     this.indexedWriteMethodRef = getSoftReference(paramMethod);
/* 308 */     setTransient((Transient)paramMethod.getAnnotation(Transient.class));
/*     */   }
/*     */ 
/*     */   public synchronized Class<?> getIndexedPropertyType()
/*     */   {
/* 322 */     Class localClass = getIndexedPropertyType0();
/* 323 */     if (localClass == null)
/*     */       try {
/* 325 */         localClass = findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
/*     */ 
/* 327 */         setIndexedPropertyType(localClass);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException)
/*     */       {
/*     */       }
/* 332 */     return localClass;
/*     */   }
/*     */ 
/*     */   private void setIndexedPropertyType(Class paramClass)
/*     */   {
/* 338 */     this.indexedPropertyTypeRef = getWeakReference(paramClass);
/*     */   }
/*     */ 
/*     */   private Class getIndexedPropertyType0() {
/* 342 */     return this.indexedPropertyTypeRef != null ? (Class)this.indexedPropertyTypeRef.get() : null;
/*     */   }
/*     */ 
/*     */   private Method getIndexedReadMethod0()
/*     */   {
/* 348 */     return this.indexedReadMethodRef != null ? (Method)this.indexedReadMethodRef.get() : null;
/*     */   }
/*     */ 
/*     */   private Method getIndexedWriteMethod0()
/*     */   {
/* 354 */     return this.indexedWriteMethodRef != null ? (Method)this.indexedWriteMethodRef.get() : null;
/*     */   }
/*     */ 
/*     */   private Class findIndexedPropertyType(Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 362 */     Class localClass = null;
/*     */ 
/* 364 */     if (paramMethod1 != null) {
/* 365 */       localObject = getParameterTypes(getClass0(), paramMethod1);
/* 366 */       if (localObject.length != 1) {
/* 367 */         throw new IntrospectionException("bad indexed read method arg count");
/*     */       }
/* 369 */       if (localObject[0] != Integer.TYPE) {
/* 370 */         throw new IntrospectionException("non int index to indexed read method");
/*     */       }
/* 372 */       localClass = getReturnType(getClass0(), paramMethod1);
/* 373 */       if (localClass == Void.TYPE) {
/* 374 */         throw new IntrospectionException("indexed read method returns void");
/*     */       }
/*     */     }
/* 377 */     if (paramMethod2 != null) {
/* 378 */       localObject = getParameterTypes(getClass0(), paramMethod2);
/* 379 */       if (localObject.length != 2) {
/* 380 */         throw new IntrospectionException("bad indexed write method arg count");
/*     */       }
/* 382 */       if (localObject[0] != Integer.TYPE) {
/* 383 */         throw new IntrospectionException("non int index to indexed write method");
/*     */       }
/* 385 */       if ((localClass != null) && (localClass != localObject[1])) {
/* 386 */         throw new IntrospectionException("type mismatch between indexed read and indexed write methods: " + getName());
/*     */       }
/*     */ 
/* 390 */       localClass = localObject[1];
/*     */     }
/* 392 */     Object localObject = getPropertyType();
/* 393 */     if ((localObject != null) && ((!((Class)localObject).isArray()) || (((Class)localObject).getComponentType() != localClass)))
/*     */     {
/* 395 */       throw new IntrospectionException("type mismatch between indexed and non-indexed methods: " + getName());
/*     */     }
/*     */ 
/* 398 */     return localClass;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 412 */     if (this == paramObject) {
/* 413 */       return true;
/*     */     }
/*     */ 
/* 416 */     if ((paramObject != null) && ((paramObject instanceof IndexedPropertyDescriptor))) {
/* 417 */       IndexedPropertyDescriptor localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramObject;
/* 418 */       Method localMethod1 = localIndexedPropertyDescriptor.getIndexedReadMethod();
/* 419 */       Method localMethod2 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
/*     */ 
/* 421 */       if (!compareMethods(getIndexedReadMethod(), localMethod1)) {
/* 422 */         return false;
/*     */       }
/*     */ 
/* 425 */       if (!compareMethods(getIndexedWriteMethod(), localMethod2)) {
/* 426 */         return false;
/*     */       }
/*     */ 
/* 429 */       if (getIndexedPropertyType() != localIndexedPropertyDescriptor.getIndexedPropertyType()) {
/* 430 */         return false;
/*     */       }
/* 432 */       return super.equals(paramObject);
/*     */     }
/* 434 */     return false;
/*     */   }
/*     */ 
/*     */   IndexedPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
/*     */   {
/* 447 */     super(paramPropertyDescriptor1, paramPropertyDescriptor2);
/*     */     IndexedPropertyDescriptor localIndexedPropertyDescriptor;
/*     */     Method localMethod3;
/* 448 */     if ((paramPropertyDescriptor1 instanceof IndexedPropertyDescriptor)) {
/* 449 */       localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor1;
/*     */       try {
/* 451 */         Method localMethod1 = localIndexedPropertyDescriptor.getIndexedReadMethod();
/* 452 */         if (localMethod1 != null) {
/* 453 */           setIndexedReadMethod(localMethod1);
/*     */         }
/*     */ 
/* 456 */         localMethod3 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
/* 457 */         if (localMethod3 != null)
/* 458 */           setIndexedWriteMethod(localMethod3);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException1)
/*     */       {
/* 462 */         throw new AssertionError(localIntrospectionException1);
/*     */       }
/*     */     }
/* 465 */     if ((paramPropertyDescriptor2 instanceof IndexedPropertyDescriptor)) {
/* 466 */       localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor2;
/*     */       try {
/* 468 */         Method localMethod2 = localIndexedPropertyDescriptor.getIndexedReadMethod();
/* 469 */         if ((localMethod2 != null) && (localMethod2.getDeclaringClass() == getClass0())) {
/* 470 */           setIndexedReadMethod(localMethod2);
/*     */         }
/*     */ 
/* 473 */         localMethod3 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
/* 474 */         if ((localMethod3 != null) && (localMethod3.getDeclaringClass() == getClass0()))
/* 475 */           setIndexedWriteMethod(localMethod3);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException2)
/*     */       {
/* 479 */         throw new AssertionError(localIntrospectionException2);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   IndexedPropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor)
/*     */   {
/* 489 */     super(paramIndexedPropertyDescriptor);
/* 490 */     this.indexedReadMethodRef = paramIndexedPropertyDescriptor.indexedReadMethodRef;
/* 491 */     this.indexedWriteMethodRef = paramIndexedPropertyDescriptor.indexedWriteMethodRef;
/* 492 */     this.indexedPropertyTypeRef = paramIndexedPropertyDescriptor.indexedPropertyTypeRef;
/* 493 */     this.indexedWriteMethodName = paramIndexedPropertyDescriptor.indexedWriteMethodName;
/* 494 */     this.indexedReadMethodName = paramIndexedPropertyDescriptor.indexedReadMethodName;
/*     */   }
/*     */ 
/*     */   void updateGenericsFor(Class<?> paramClass) {
/* 498 */     super.updateGenericsFor(paramClass);
/*     */     try {
/* 500 */       setIndexedPropertyType(findIndexedPropertyType(getIndexedReadMethod0(), getIndexedWriteMethod0()));
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException) {
/* 503 */       setIndexedPropertyType(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 515 */     int i = super.hashCode();
/*     */ 
/* 517 */     i = 37 * i + (this.indexedWriteMethodName == null ? 0 : this.indexedWriteMethodName.hashCode());
/*     */ 
/* 519 */     i = 37 * i + (this.indexedReadMethodName == null ? 0 : this.indexedReadMethodName.hashCode());
/*     */ 
/* 521 */     i = 37 * i + (getIndexedPropertyType() == null ? 0 : getIndexedPropertyType().hashCode());
/*     */ 
/* 524 */     return i;
/*     */   }
/*     */ 
/*     */   void appendTo(StringBuilder paramStringBuilder) {
/* 528 */     super.appendTo(paramStringBuilder);
/* 529 */     appendTo(paramStringBuilder, "indexedPropertyType", this.indexedPropertyTypeRef);
/* 530 */     appendTo(paramStringBuilder, "indexedReadMethod", this.indexedReadMethodRef);
/* 531 */     appendTo(paramStringBuilder, "indexedWriteMethod", this.indexedWriteMethodRef);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.IndexedPropertyDescriptor
 * JD-Core Version:    0.6.2
 */