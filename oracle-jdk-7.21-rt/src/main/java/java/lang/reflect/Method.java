/*     */ package java.lang.reflect;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.AnnotationFormatError;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Map;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ import sun.reflect.MethodAccessor;
/*     */ import sun.reflect.Reflection;
/*     */ import sun.reflect.ReflectionFactory;
/*     */ import sun.reflect.annotation.AnnotationParser;
/*     */ import sun.reflect.annotation.AnnotationType;
/*     */ import sun.reflect.annotation.ExceptionProxy;
/*     */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*     */ import sun.reflect.generics.factory.GenericsFactory;
/*     */ import sun.reflect.generics.repository.MethodRepository;
/*     */ import sun.reflect.generics.scope.MethodScope;
/*     */ 
/*     */ public final class Method extends AccessibleObject
/*     */   implements GenericDeclaration, Member
/*     */ {
/*     */   private Class<?> clazz;
/*     */   private int slot;
/*     */   private String name;
/*     */   private Class<?> returnType;
/*     */   private Class<?>[] parameterTypes;
/*     */   private Class<?>[] exceptionTypes;
/*     */   private int modifiers;
/*     */   private transient String signature;
/*     */   private transient MethodRepository genericInfo;
/*     */   private byte[] annotations;
/*     */   private byte[] parameterAnnotations;
/*     */   private byte[] annotationDefault;
/*     */   private volatile MethodAccessor methodAccessor;
/*     */   private Method root;
/*     */   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
/*     */ 
/*     */   private String getGenericSignature()
/*     */   {
/*  88 */     return this.signature;
/*     */   }
/*     */ 
/*     */   private GenericsFactory getFactory()
/*     */   {
/*  93 */     return CoreReflectionFactory.make(this, MethodScope.make(this));
/*     */   }
/*     */ 
/*     */   private MethodRepository getGenericInfo()
/*     */   {
/*  99 */     if (this.genericInfo == null)
/*     */     {
/* 101 */       this.genericInfo = MethodRepository.make(getGenericSignature(), getFactory());
/*     */     }
/*     */ 
/* 104 */     return this.genericInfo;
/*     */   }
/*     */ 
/*     */   Method(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
/*     */   {
/* 124 */     this.clazz = paramClass1;
/* 125 */     this.name = paramString1;
/* 126 */     this.parameterTypes = paramArrayOfClass1;
/* 127 */     this.returnType = paramClass2;
/* 128 */     this.exceptionTypes = paramArrayOfClass2;
/* 129 */     this.modifiers = paramInt1;
/* 130 */     this.slot = paramInt2;
/* 131 */     this.signature = paramString2;
/* 132 */     this.annotations = paramArrayOfByte1;
/* 133 */     this.parameterAnnotations = paramArrayOfByte2;
/* 134 */     this.annotationDefault = paramArrayOfByte3;
/*     */   }
/*     */ 
/*     */   Method copy()
/*     */   {
/* 150 */     Method localMethod = new Method(this.clazz, this.name, this.parameterTypes, this.returnType, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations, this.annotationDefault);
/*     */ 
/* 153 */     localMethod.root = this;
/*     */ 
/* 155 */     localMethod.methodAccessor = this.methodAccessor;
/* 156 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public Class<?> getDeclaringClass()
/*     */   {
/* 164 */     return this.clazz;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 172 */     return this.name;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/* 183 */     return this.modifiers;
/*     */   }
/*     */ 
/*     */   public TypeVariable<Method>[] getTypeParameters()
/*     */   {
/* 202 */     if (getGenericSignature() != null) {
/* 203 */       return (TypeVariable[])getGenericInfo().getTypeParameters();
/*     */     }
/* 205 */     return (TypeVariable[])new TypeVariable[0];
/*     */   }
/*     */ 
/*     */   public Class<?> getReturnType()
/*     */   {
/* 215 */     return this.returnType;
/*     */   }
/*     */ 
/*     */   public Type getGenericReturnType()
/*     */   {
/* 243 */     if (getGenericSignature() != null)
/* 244 */       return getGenericInfo().getReturnType();
/* 245 */     return getReturnType();
/*     */   }
/*     */ 
/*     */   public Class<?>[] getParameterTypes()
/*     */   {
/* 259 */     return (Class[])this.parameterTypes.clone();
/*     */   }
/*     */ 
/*     */   public Type[] getGenericParameterTypes()
/*     */   {
/* 290 */     if (getGenericSignature() != null) {
/* 291 */       return getGenericInfo().getParameterTypes();
/*     */     }
/* 293 */     return getParameterTypes();
/*     */   }
/*     */ 
/*     */   public Class<?>[] getExceptionTypes()
/*     */   {
/* 308 */     return (Class[])this.exceptionTypes.clone();
/*     */   }
/*     */ 
/*     */   public Type[] getGenericExceptionTypes()
/*     */   {
/*     */     Type[] arrayOfType;
/* 335 */     if ((getGenericSignature() != null) && ((arrayOfType = getGenericInfo().getExceptionTypes()).length > 0))
/*     */     {
/* 337 */       return arrayOfType;
/*     */     }
/* 339 */     return getExceptionTypes();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 349 */     if ((paramObject != null) && ((paramObject instanceof Method))) {
/* 350 */       Method localMethod = (Method)paramObject;
/* 351 */       if ((getDeclaringClass() == localMethod.getDeclaringClass()) && (getName() == localMethod.getName()))
/*     */       {
/* 353 */         if (!this.returnType.equals(localMethod.getReturnType())) {
/* 354 */           return false;
/*     */         }
/* 356 */         Class[] arrayOfClass1 = this.parameterTypes;
/* 357 */         Class[] arrayOfClass2 = localMethod.parameterTypes;
/* 358 */         if (arrayOfClass1.length == arrayOfClass2.length) {
/* 359 */           for (int i = 0; i < arrayOfClass1.length; i++) {
/* 360 */             if (arrayOfClass1[i] != arrayOfClass2[i])
/* 361 */               return false;
/*     */           }
/* 363 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 367 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 376 */     return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 403 */       StringBuilder localStringBuilder = new StringBuilder();
/* 404 */       int i = getModifiers() & Modifier.methodModifiers();
/* 405 */       if (i != 0) {
/* 406 */         localStringBuilder.append(Modifier.toString(i)).append(' ');
/*     */       }
/* 408 */       localStringBuilder.append(Field.getTypeName(getReturnType())).append(' ');
/* 409 */       localStringBuilder.append(Field.getTypeName(getDeclaringClass())).append('.');
/* 410 */       localStringBuilder.append(getName()).append('(');
/* 411 */       Class[] arrayOfClass1 = this.parameterTypes;
/* 412 */       for (int j = 0; j < arrayOfClass1.length; j++) {
/* 413 */         localStringBuilder.append(Field.getTypeName(arrayOfClass1[j]));
/* 414 */         if (j < arrayOfClass1.length - 1)
/* 415 */           localStringBuilder.append(',');
/*     */       }
/* 417 */       localStringBuilder.append(')');
/* 418 */       Class[] arrayOfClass2 = this.exceptionTypes;
/* 419 */       if (arrayOfClass2.length > 0) {
/* 420 */         localStringBuilder.append(" throws ");
/* 421 */         for (int k = 0; k < arrayOfClass2.length; k++) {
/* 422 */           localStringBuilder.append(arrayOfClass2[k].getName());
/* 423 */           if (k < arrayOfClass2.length - 1)
/* 424 */             localStringBuilder.append(',');
/*     */         }
/*     */       }
/* 427 */       return localStringBuilder.toString();
/*     */     } catch (Exception localException) {
/* 429 */       return "<" + localException + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toGenericString()
/*     */   {
/*     */     try
/*     */     {
/* 473 */       StringBuilder localStringBuilder = new StringBuilder();
/* 474 */       int i = getModifiers() & Modifier.methodModifiers();
/* 475 */       if (i != 0) {
/* 476 */         localStringBuilder.append(Modifier.toString(i)).append(' ');
/*     */       }
/* 478 */       TypeVariable[] arrayOfTypeVariable = getTypeParameters();
/* 479 */       if (arrayOfTypeVariable.length > 0) {
/* 480 */         int j = 1;
/* 481 */         localStringBuilder.append('<');
/* 482 */         for (Object localObject2 : arrayOfTypeVariable) {
/* 483 */           if (j == 0) {
/* 484 */             localStringBuilder.append(',');
/*     */           }
/*     */ 
/* 487 */           localStringBuilder.append(localObject2.toString());
/* 488 */           j = 0;
/*     */         }
/* 490 */         localStringBuilder.append("> ");
/*     */       }
/*     */ 
/* 493 */       Type localType = getGenericReturnType();
/* 494 */       localStringBuilder.append((localType instanceof Class) ? Field.getTypeName((Class)localType) : localType.toString()).append(' ');
/*     */ 
/* 498 */       localStringBuilder.append(Field.getTypeName(getDeclaringClass())).append('.');
/* 499 */       localStringBuilder.append(getName()).append('(');
/* 500 */       ??? = getGenericParameterTypes();
/* 501 */       for (??? = 0; ??? < ???.length; ???++) {
/* 502 */         String str = (???[???] instanceof Class) ? Field.getTypeName((Class)???[???]) : ???[???].toString();
/*     */ 
/* 505 */         if ((isVarArgs()) && (??? == ???.length - 1))
/* 506 */           str = str.replaceFirst("\\[\\]$", "...");
/* 507 */         localStringBuilder.append(str);
/* 508 */         if (??? < ???.length - 1)
/* 509 */           localStringBuilder.append(',');
/*     */       }
/* 511 */       localStringBuilder.append(')');
/* 512 */       Type[] arrayOfType = getGenericExceptionTypes();
/* 513 */       if (arrayOfType.length > 0) {
/* 514 */         localStringBuilder.append(" throws ");
/* 515 */         for (int n = 0; n < arrayOfType.length; n++) {
/* 516 */           localStringBuilder.append((arrayOfType[n] instanceof Class) ? ((Class)arrayOfType[n]).getName() : arrayOfType[n].toString());
/*     */ 
/* 519 */           if (n < arrayOfType.length - 1)
/* 520 */             localStringBuilder.append(',');
/*     */         }
/*     */       }
/* 523 */       return localStringBuilder.toString();
/*     */     } catch (Exception localException) {
/* 525 */       return "<" + localException + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object invoke(Object paramObject, Object[] paramArrayOfObject)
/*     */     throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
/*     */   {
/* 590 */     if ((!this.override) && 
/* 591 */       (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers))) {
/* 592 */       localObject = Reflection.getCallerClass(1);
/*     */ 
/* 594 */       checkAccess((Class)localObject, this.clazz, paramObject, this.modifiers);
/*     */     }
/*     */ 
/* 597 */     Object localObject = this.methodAccessor;
/* 598 */     if (localObject == null) {
/* 599 */       localObject = acquireMethodAccessor();
/*     */     }
/* 601 */     return ((MethodAccessor)localObject).invoke(paramObject, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public boolean isBridge()
/*     */   {
/* 613 */     return (getModifiers() & 0x40) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isVarArgs()
/*     */   {
/* 626 */     return (getModifiers() & 0x80) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isSynthetic()
/*     */   {
/* 638 */     return Modifier.isSynthetic(getModifiers());
/*     */   }
/*     */ 
/*     */   private MethodAccessor acquireMethodAccessor()
/*     */   {
/* 648 */     MethodAccessor localMethodAccessor = null;
/* 649 */     if (this.root != null) localMethodAccessor = this.root.getMethodAccessor();
/* 650 */     if (localMethodAccessor != null) {
/* 651 */       this.methodAccessor = localMethodAccessor;
/*     */     }
/*     */     else {
/* 654 */       localMethodAccessor = reflectionFactory.newMethodAccessor(this);
/* 655 */       setMethodAccessor(localMethodAccessor);
/*     */     }
/*     */ 
/* 658 */     return localMethodAccessor;
/*     */   }
/*     */ 
/*     */   MethodAccessor getMethodAccessor()
/*     */   {
/* 664 */     return this.methodAccessor;
/*     */   }
/*     */ 
/*     */   void setMethodAccessor(MethodAccessor paramMethodAccessor)
/*     */   {
/* 670 */     this.methodAccessor = paramMethodAccessor;
/*     */ 
/* 672 */     if (this.root != null)
/* 673 */       this.root.setMethodAccessor(paramMethodAccessor);
/*     */   }
/*     */ 
/*     */   public <T extends Annotation> T getAnnotation(Class<T> paramClass)
/*     */   {
/* 682 */     if (paramClass == null) {
/* 683 */       throw new NullPointerException();
/*     */     }
/* 685 */     return (Annotation)declaredAnnotations().get(paramClass);
/*     */   }
/*     */ 
/*     */   public Annotation[] getDeclaredAnnotations()
/*     */   {
/* 692 */     return AnnotationParser.toArray(declaredAnnotations());
/*     */   }
/*     */ 
/*     */   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
/*     */   {
/* 698 */     if (this.declaredAnnotations == null) {
/* 699 */       this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*     */     }
/*     */ 
/* 704 */     return this.declaredAnnotations;
/*     */   }
/*     */ 
/*     */   public Object getDefaultValue()
/*     */   {
/* 722 */     if (this.annotationDefault == null)
/* 723 */       return null;
/* 724 */     Class localClass = AnnotationType.invocationHandlerReturnType(getReturnType());
/*     */ 
/* 726 */     Object localObject = AnnotationParser.parseMemberValue(localClass, ByteBuffer.wrap(this.annotationDefault), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*     */ 
/* 731 */     if ((localObject instanceof ExceptionProxy))
/* 732 */       throw new AnnotationFormatError("Invalid default: " + this);
/* 733 */     return localObject;
/*     */   }
/*     */ 
/*     */   public Annotation[][] getParameterAnnotations()
/*     */   {
/* 753 */     int i = this.parameterTypes.length;
/* 754 */     if (this.parameterAnnotations == null) {
/* 755 */       return new Annotation[i][0];
/*     */     }
/* 757 */     Annotation[][] arrayOfAnnotation = AnnotationParser.parseParameterAnnotations(this.parameterAnnotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*     */ 
/* 762 */     if (arrayOfAnnotation.length != i) {
/* 763 */       throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
/*     */     }
/* 765 */     return arrayOfAnnotation;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.Method
 * JD-Core Version:    0.6.2
 */