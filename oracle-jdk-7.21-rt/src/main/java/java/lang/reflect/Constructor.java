/*     */ package java.lang.reflect;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.util.Map;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.reflect.ConstructorAccessor;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.ReflectionFactory;
/*     */ import sun.reflect.annotation.AnnotationParser;
/*     */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*     */ import sun.reflect.generics.factory.GenericsFactory;
/*     */ import sun.reflect.generics.repository.ConstructorRepository;
/*     */ import sun.reflect.generics.scope.ConstructorScope;
/*     */ 
/*     */ public final class Constructor<T> extends AccessibleObject
/*     */   implements GenericDeclaration, Member
/*     */ {
/*     */   private Class<T> clazz;
/*     */   private int slot;
/*     */   private Class<?>[] parameterTypes;
/*     */   private Class<?>[] exceptionTypes;
/*     */   private int modifiers;
/*     */   private transient String signature;
/*     */   private transient ConstructorRepository genericInfo;
/*     */   private byte[] annotations;
/*     */   private byte[] parameterAnnotations;
/*     */   private volatile ConstructorAccessor constructorAccessor;
/*     */   private Constructor<T> root;
/*     */   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
/*     */ 
/*     */   private GenericsFactory getFactory()
/*     */   {
/*  81 */     return CoreReflectionFactory.make(this, ConstructorScope.make(this));
/*     */   }
/*     */ 
/*     */   private ConstructorRepository getGenericInfo()
/*     */   {
/*  87 */     if (this.genericInfo == null)
/*     */     {
/*  89 */       this.genericInfo = ConstructorRepository.make(getSignature(), getFactory());
/*     */     }
/*     */ 
/*  93 */     return this.genericInfo;
/*     */   }
/*     */ 
/*     */   Constructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*     */   {
/* 116 */     this.clazz = paramClass;
/* 117 */     this.parameterTypes = paramArrayOfClass1;
/* 118 */     this.exceptionTypes = paramArrayOfClass2;
/* 119 */     this.modifiers = paramInt1;
/* 120 */     this.slot = paramInt2;
/* 121 */     this.signature = paramString;
/* 122 */     this.annotations = paramArrayOfByte1;
/* 123 */     this.parameterAnnotations = paramArrayOfByte2;
/*     */   }
/*     */ 
/*     */   Constructor<T> copy()
/*     */   {
/* 139 */     Constructor localConstructor = new Constructor(this.clazz, this.parameterTypes, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations);
/*     */ 
/* 145 */     localConstructor.root = this;
/*     */ 
/* 147 */     localConstructor.constructorAccessor = this.constructorAccessor;
/* 148 */     return localConstructor;
/*     */   }
/*     */ 
/*     */   public Class<T> getDeclaringClass()
/*     */   {
/* 156 */     return this.clazz;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 164 */     return getDeclaringClass().getName();
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/* 175 */     return this.modifiers;
/*     */   }
/*     */ 
/*     */   public TypeVariable<Constructor<T>>[] getTypeParameters()
/*     */   {
/* 194 */     if (getSignature() != null) {
/* 195 */       return (TypeVariable[])getGenericInfo().getTypeParameters();
/*     */     }
/* 197 */     return (TypeVariable[])new TypeVariable[0];
/*     */   }
/*     */ 
/*     */   public Class<?>[] getParameterTypes()
/*     */   {
/* 211 */     return (Class[])this.parameterTypes.clone();
/*     */   }
/*     */ 
/*     */   public Type[] getGenericParameterTypes()
/*     */   {
/* 243 */     if (getSignature() != null) {
/* 244 */       return getGenericInfo().getParameterTypes();
/*     */     }
/* 246 */     return getParameterTypes();
/*     */   }
/*     */ 
/*     */   public Class<?>[] getExceptionTypes()
/*     */   {
/* 260 */     return (Class[])this.exceptionTypes.clone();
/*     */   }
/*     */ 
/*     */   public Type[] getGenericExceptionTypes()
/*     */   {
/*     */     Type[] arrayOfType;
/* 288 */     if ((getSignature() != null) && ((arrayOfType = getGenericInfo().getExceptionTypes()).length > 0))
/*     */     {
/* 290 */       return arrayOfType;
/*     */     }
/* 292 */     return getExceptionTypes();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 302 */     if ((paramObject != null) && ((paramObject instanceof Constructor))) {
/* 303 */       Constructor localConstructor = (Constructor)paramObject;
/* 304 */       if (getDeclaringClass() == localConstructor.getDeclaringClass())
/*     */       {
/* 306 */         Class[] arrayOfClass1 = this.parameterTypes;
/* 307 */         Class[] arrayOfClass2 = localConstructor.parameterTypes;
/* 308 */         if (arrayOfClass1.length == arrayOfClass2.length) {
/* 309 */           for (int i = 0; i < arrayOfClass1.length; i++) {
/* 310 */             if (arrayOfClass1[i] != arrayOfClass2[i])
/* 311 */               return false;
/*     */           }
/* 313 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 317 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 326 */     return getDeclaringClass().getName().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 346 */       StringBuffer localStringBuffer = new StringBuffer();
/* 347 */       int i = getModifiers() & Modifier.constructorModifiers();
/* 348 */       if (i != 0) {
/* 349 */         localStringBuffer.append(Modifier.toString(i) + " ");
/*     */       }
/* 351 */       localStringBuffer.append(Field.getTypeName(getDeclaringClass()));
/* 352 */       localStringBuffer.append("(");
/* 353 */       Class[] arrayOfClass1 = this.parameterTypes;
/* 354 */       for (int j = 0; j < arrayOfClass1.length; j++) {
/* 355 */         localStringBuffer.append(Field.getTypeName(arrayOfClass1[j]));
/* 356 */         if (j < arrayOfClass1.length - 1)
/* 357 */           localStringBuffer.append(",");
/*     */       }
/* 359 */       localStringBuffer.append(")");
/* 360 */       Class[] arrayOfClass2 = this.exceptionTypes;
/* 361 */       if (arrayOfClass2.length > 0) {
/* 362 */         localStringBuffer.append(" throws ");
/* 363 */         for (int k = 0; k < arrayOfClass2.length; k++) {
/* 364 */           localStringBuffer.append(arrayOfClass2[k].getName());
/* 365 */           if (k < arrayOfClass2.length - 1)
/* 366 */             localStringBuffer.append(",");
/*     */         }
/*     */       }
/* 369 */       return localStringBuffer.toString();
/*     */     } catch (Exception localException) {
/* 371 */       return "<" + localException + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toGenericString()
/*     */   {
/*     */     try
/*     */     {
/* 410 */       StringBuilder localStringBuilder = new StringBuilder();
/* 411 */       int i = getModifiers() & Modifier.constructorModifiers();
/* 412 */       if (i != 0) {
/* 413 */         localStringBuilder.append(Modifier.toString(i) + " ");
/*     */       }
/* 415 */       TypeVariable[] arrayOfTypeVariable1 = getTypeParameters();
/* 416 */       if (arrayOfTypeVariable1.length > 0) {
/* 417 */         int j = 1;
/* 418 */         localStringBuilder.append("<");
/* 419 */         for (TypeVariable localTypeVariable : arrayOfTypeVariable1) {
/* 420 */           if (j == 0) {
/* 421 */             localStringBuilder.append(",");
/*     */           }
/*     */ 
/* 424 */           localStringBuilder.append(localTypeVariable.toString());
/* 425 */           j = 0;
/*     */         }
/* 427 */         localStringBuilder.append("> ");
/*     */       }
/* 429 */       localStringBuilder.append(Field.getTypeName(getDeclaringClass()));
/* 430 */       localStringBuilder.append("(");
/* 431 */       Type[] arrayOfType1 = getGenericParameterTypes();
/* 432 */       for (int k = 0; k < arrayOfType1.length; k++) {
/* 433 */         String str = (arrayOfType1[k] instanceof Class) ? Field.getTypeName((Class)arrayOfType1[k]) : arrayOfType1[k].toString();
/*     */ 
/* 436 */         if ((isVarArgs()) && (k == arrayOfType1.length - 1))
/* 437 */           str = str.replaceFirst("\\[\\]$", "...");
/* 438 */         localStringBuilder.append(str);
/* 439 */         if (k < arrayOfType1.length - 1)
/* 440 */           localStringBuilder.append(",");
/*     */       }
/* 442 */       localStringBuilder.append(")");
/* 443 */       Type[] arrayOfType2 = getGenericExceptionTypes();
/* 444 */       if (arrayOfType2.length > 0) {
/* 445 */         localStringBuilder.append(" throws ");
/* 446 */         for (int n = 0; n < arrayOfType2.length; n++) {
/* 447 */           localStringBuilder.append((arrayOfType2[n] instanceof Class) ? ((Class)arrayOfType2[n]).getName() : arrayOfType2[n].toString());
/*     */ 
/* 450 */           if (n < arrayOfType2.length - 1)
/* 451 */             localStringBuilder.append(",");
/*     */         }
/*     */       }
/* 454 */       return localStringBuilder.toString();
/*     */     } catch (Exception localException) {
/* 456 */       return "<" + localException + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   public T newInstance(Object[] paramArrayOfObject)
/*     */     throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
/*     */   {
/* 512 */     if ((!this.override) && 
/* 513 */       (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers))) {
/* 514 */       localObject = Reflection.getCallerClass(2);
/*     */ 
/* 516 */       checkAccess((Class)localObject, this.clazz, null, this.modifiers);
/*     */     }
/*     */ 
/* 519 */     if ((this.clazz.getModifiers() & 0x4000) != 0)
/* 520 */       throw new IllegalArgumentException("Cannot reflectively create enum objects");
/* 521 */     Object localObject = this.constructorAccessor;
/* 522 */     if (localObject == null) {
/* 523 */       localObject = acquireConstructorAccessor();
/*     */     }
/* 525 */     return ((ConstructorAccessor)localObject).newInstance(paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public boolean isVarArgs()
/*     */   {
/* 538 */     return (getModifiers() & 0x80) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isSynthetic()
/*     */   {
/* 551 */     return Modifier.isSynthetic(getModifiers());
/*     */   }
/*     */ 
/*     */   private ConstructorAccessor acquireConstructorAccessor()
/*     */   {
/* 562 */     ConstructorAccessor localConstructorAccessor = null;
/* 563 */     if (this.root != null) localConstructorAccessor = this.root.getConstructorAccessor();
/* 564 */     if (localConstructorAccessor != null) {
/* 565 */       this.constructorAccessor = localConstructorAccessor;
/*     */     }
/*     */     else {
/* 568 */       localConstructorAccessor = reflectionFactory.newConstructorAccessor(this);
/* 569 */       setConstructorAccessor(localConstructorAccessor);
/*     */     }
/*     */ 
/* 572 */     return localConstructorAccessor;
/*     */   }
/*     */ 
/*     */   ConstructorAccessor getConstructorAccessor()
/*     */   {
/* 578 */     return this.constructorAccessor;
/*     */   }
/*     */ 
/*     */   void setConstructorAccessor(ConstructorAccessor paramConstructorAccessor)
/*     */   {
/* 584 */     this.constructorAccessor = paramConstructorAccessor;
/*     */ 
/* 586 */     if (this.root != null)
/* 587 */       this.root.setConstructorAccessor(paramConstructorAccessor);
/*     */   }
/*     */ 
/*     */   int getSlot()
/*     */   {
/* 592 */     return this.slot;
/*     */   }
/*     */ 
/*     */   String getSignature() {
/* 596 */     return this.signature;
/*     */   }
/*     */ 
/*     */   byte[] getRawAnnotations() {
/* 600 */     return this.annotations;
/*     */   }
/*     */ 
/*     */   byte[] getRawParameterAnnotations() {
/* 604 */     return this.parameterAnnotations;
/*     */   }
/*     */ 
/*     */   public <T extends Annotation> T getAnnotation(Class<T> paramClass)
/*     */   {
/* 612 */     if (paramClass == null) {
/* 613 */       throw new NullPointerException();
/*     */     }
/* 615 */     return (Annotation)declaredAnnotations().get(paramClass);
/*     */   }
/*     */ 
/*     */   public Annotation[] getDeclaredAnnotations()
/*     */   {
/* 622 */     return AnnotationParser.toArray(declaredAnnotations());
/*     */   }
/*     */ 
/*     */   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
/*     */   {
/* 628 */     if (this.declaredAnnotations == null) {
/* 629 */       this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*     */     }
/*     */ 
/* 634 */     return this.declaredAnnotations;
/*     */   }
/*     */ 
/*     */   public Annotation[][] getParameterAnnotations()
/*     */   {
/* 654 */     int i = this.parameterTypes.length;
/* 655 */     if (this.parameterAnnotations == null) {
/* 656 */       return new Annotation[i][0];
/*     */     }
/* 658 */     Annotation[][] arrayOfAnnotation = AnnotationParser.parseParameterAnnotations(this.parameterAnnotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*     */ 
/* 663 */     if (arrayOfAnnotation.length != i) {
/* 664 */       Class localClass = getDeclaringClass();
/* 665 */       if ((!localClass.isEnum()) && (!localClass.isAnonymousClass()) && (!localClass.isLocalClass()))
/*     */       {
/* 670 */         if ((!localClass.isMemberClass()) || ((localClass.isMemberClass()) && ((localClass.getModifiers() & 0x8) == 0) && (arrayOfAnnotation.length + 1 != i)))
/*     */         {
/* 676 */           throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 681 */     return arrayOfAnnotation;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.Constructor
 * JD-Core Version:    0.6.2
 */