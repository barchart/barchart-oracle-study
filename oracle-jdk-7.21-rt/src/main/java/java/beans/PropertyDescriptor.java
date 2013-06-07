/*     */ package java.beans;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class PropertyDescriptor extends FeatureDescriptor
/*     */ {
/*     */   private Reference<Class> propertyTypeRef;
/*     */   private Reference<Method> readMethodRef;
/*     */   private Reference<Method> writeMethodRef;
/*     */   private Reference<Class> propertyEditorClassRef;
/*     */   private boolean bound;
/*     */   private boolean constrained;
/*     */   private String baseName;
/*     */   private String writeMethodName;
/*     */   private String readMethodName;
/*     */ 
/*     */   public PropertyDescriptor(String paramString, Class<?> paramClass)
/*     */     throws IntrospectionException
/*     */   {
/*  70 */     this(paramString, paramClass, "is" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString));
/*     */   }
/*     */ 
/*     */   public PropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3)
/*     */     throws IntrospectionException
/*     */   {
/*  92 */     if (paramClass == null) {
/*  93 */       throw new IntrospectionException("Target Bean class is null");
/*     */     }
/*  95 */     if ((paramString1 == null) || (paramString1.length() == 0)) {
/*  96 */       throw new IntrospectionException("bad property name");
/*     */     }
/*  98 */     if (("".equals(paramString2)) || ("".equals(paramString3))) {
/*  99 */       throw new IntrospectionException("read or write method name should not be the empty string");
/*     */     }
/* 101 */     setName(paramString1);
/* 102 */     setClass0(paramClass);
/*     */ 
/* 104 */     this.readMethodName = paramString2;
/* 105 */     if ((paramString2 != null) && (getReadMethod() == null)) {
/* 106 */       throw new IntrospectionException("Method not found: " + paramString2);
/*     */     }
/* 108 */     this.writeMethodName = paramString3;
/* 109 */     if ((paramString3 != null) && (getWriteMethod() == null)) {
/* 110 */       throw new IntrospectionException("Method not found: " + paramString3);
/*     */     }
/*     */ 
/* 115 */     Class[] arrayOfClass = { PropertyChangeListener.class };
/* 116 */     this.bound = (null != Introspector.findMethod(paramClass, "addPropertyChangeListener", arrayOfClass.length, arrayOfClass));
/*     */   }
/*     */ 
/*     */   public PropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 133 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 134 */       throw new IntrospectionException("bad property name");
/*     */     }
/* 136 */     setName(paramString);
/* 137 */     setReadMethod(paramMethod1);
/* 138 */     setWriteMethod(paramMethod2);
/*     */   }
/*     */ 
/*     */   PropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 154 */     if (paramClass == null) {
/* 155 */       throw new IntrospectionException("Target Bean class is null");
/*     */     }
/* 157 */     setClass0(paramClass);
/* 158 */     setName(Introspector.decapitalize(paramString));
/* 159 */     setReadMethod(paramMethod1);
/* 160 */     setWriteMethod(paramMethod2);
/* 161 */     this.baseName = paramString;
/*     */   }
/*     */ 
/*     */   public synchronized Class<?> getPropertyType()
/*     */   {
/* 177 */     Class localClass = getPropertyType0();
/* 178 */     if (localClass == null)
/*     */       try {
/* 180 */         localClass = findPropertyType(getReadMethod(), getWriteMethod());
/* 181 */         setPropertyType(localClass);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException)
/*     */       {
/*     */       }
/* 186 */     return localClass;
/*     */   }
/*     */ 
/*     */   private void setPropertyType(Class paramClass) {
/* 190 */     this.propertyTypeRef = getWeakReference(paramClass);
/*     */   }
/*     */ 
/*     */   private Class getPropertyType0() {
/* 194 */     return this.propertyTypeRef != null ? (Class)this.propertyTypeRef.get() : null;
/*     */   }
/*     */ 
/*     */   public synchronized Method getReadMethod()
/*     */   {
/* 206 */     Method localMethod = getReadMethod0();
/* 207 */     if (localMethod == null) {
/* 208 */       Class localClass1 = getClass0();
/* 209 */       if ((localClass1 == null) || ((this.readMethodName == null) && (this.readMethodRef == null)))
/*     */       {
/* 211 */         return null;
/*     */       }
/* 213 */       if (this.readMethodName == null) {
/* 214 */         Class localClass2 = getPropertyType0();
/* 215 */         if ((localClass2 == Boolean.TYPE) || (localClass2 == null))
/* 216 */           this.readMethodName = ("is" + getBaseName());
/*     */         else {
/* 218 */           this.readMethodName = ("get" + getBaseName());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 227 */       localMethod = Introspector.findMethod(localClass1, this.readMethodName, 0);
/* 228 */       if (localMethod == null) {
/* 229 */         this.readMethodName = ("get" + getBaseName());
/* 230 */         localMethod = Introspector.findMethod(localClass1, this.readMethodName, 0);
/*     */       }
/*     */       try {
/* 233 */         setReadMethod(localMethod);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException) {
/*     */       }
/*     */     }
/* 238 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public synchronized void setReadMethod(Method paramMethod)
/*     */     throws IntrospectionException
/*     */   {
/* 248 */     if (paramMethod == null) {
/* 249 */       this.readMethodName = null;
/* 250 */       this.readMethodRef = null;
/* 251 */       return;
/*     */     }
/*     */ 
/* 254 */     setPropertyType(findPropertyType(paramMethod, getWriteMethod0()));
/* 255 */     setClass0(paramMethod.getDeclaringClass());
/*     */ 
/* 257 */     this.readMethodName = paramMethod.getName();
/* 258 */     this.readMethodRef = getSoftReference(paramMethod);
/* 259 */     setTransient((Transient)paramMethod.getAnnotation(Transient.class));
/*     */   }
/*     */ 
/*     */   public synchronized Method getWriteMethod()
/*     */   {
/* 269 */     Method localMethod = getWriteMethod0();
/* 270 */     if (localMethod == null) {
/* 271 */       Class localClass1 = getClass0();
/* 272 */       if ((localClass1 == null) || ((this.writeMethodName == null) && (this.writeMethodRef == null)))
/*     */       {
/* 274 */         return null;
/*     */       }
/*     */ 
/* 278 */       Class localClass2 = getPropertyType0();
/* 279 */       if (localClass2 == null) {
/*     */         try
/*     */         {
/* 282 */           localClass2 = findPropertyType(getReadMethod(), null);
/* 283 */           setPropertyType(localClass2);
/*     */         }
/*     */         catch (IntrospectionException localIntrospectionException1)
/*     */         {
/* 287 */           return null;
/*     */         }
/*     */       }
/*     */ 
/* 291 */       if (this.writeMethodName == null) {
/* 292 */         this.writeMethodName = ("set" + getBaseName());
/*     */       }
/*     */ 
/* 295 */       Class[] arrayOfClass = { localClass2 == null ? null : localClass2 };
/* 296 */       localMethod = Introspector.findMethod(localClass1, this.writeMethodName, 1, arrayOfClass);
/* 297 */       if ((localMethod != null) && 
/* 298 */         (!localMethod.getReturnType().equals(Void.TYPE))) {
/* 299 */         localMethod = null;
/*     */       }
/*     */       try
/*     */       {
/* 303 */         setWriteMethod(localMethod);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException2) {
/*     */       }
/*     */     }
/* 308 */     return localMethod;
/*     */   }
/*     */ 
/*     */   public synchronized void setWriteMethod(Method paramMethod)
/*     */     throws IntrospectionException
/*     */   {
/* 318 */     if (paramMethod == null) {
/* 319 */       this.writeMethodName = null;
/* 320 */       this.writeMethodRef = null;
/* 321 */       return;
/*     */     }
/*     */ 
/* 324 */     setPropertyType(findPropertyType(getReadMethod(), paramMethod));
/* 325 */     setClass0(paramMethod.getDeclaringClass());
/*     */ 
/* 327 */     this.writeMethodName = paramMethod.getName();
/* 328 */     this.writeMethodRef = getSoftReference(paramMethod);
/* 329 */     setTransient((Transient)paramMethod.getAnnotation(Transient.class));
/*     */   }
/*     */ 
/*     */   private Method getReadMethod0() {
/* 333 */     return this.readMethodRef != null ? (Method)this.readMethodRef.get() : null;
/*     */   }
/*     */ 
/*     */   private Method getWriteMethod0()
/*     */   {
/* 339 */     return this.writeMethodRef != null ? (Method)this.writeMethodRef.get() : null;
/*     */   }
/*     */ 
/*     */   void setClass0(Class paramClass)
/*     */   {
/* 348 */     if ((getClass0() != null) && (paramClass.isAssignableFrom(getClass0())))
/*     */     {
/* 350 */       return;
/*     */     }
/* 352 */     super.setClass0(paramClass);
/*     */   }
/*     */ 
/*     */   public boolean isBound()
/*     */   {
/* 362 */     return this.bound;
/*     */   }
/*     */ 
/*     */   public void setBound(boolean paramBoolean)
/*     */   {
/* 372 */     this.bound = paramBoolean;
/*     */   }
/*     */ 
/*     */   public boolean isConstrained()
/*     */   {
/* 382 */     return this.constrained;
/*     */   }
/*     */ 
/*     */   public void setConstrained(boolean paramBoolean)
/*     */   {
/* 392 */     this.constrained = paramBoolean;
/*     */   }
/*     */ 
/*     */   public void setPropertyEditorClass(Class<?> paramClass)
/*     */   {
/* 405 */     this.propertyEditorClassRef = getWeakReference(paramClass);
/*     */   }
/*     */ 
/*     */   public Class<?> getPropertyEditorClass()
/*     */   {
/* 419 */     return this.propertyEditorClassRef != null ? (Class)this.propertyEditorClassRef.get() : null;
/*     */   }
/*     */ 
/*     */   public PropertyEditor createPropertyEditor(Object paramObject)
/*     */   {
/* 438 */     Object localObject = null;
/*     */ 
/* 440 */     Class localClass = getPropertyEditorClass();
/* 441 */     if (localClass != null) {
/* 442 */       Constructor localConstructor = null;
/* 443 */       if (paramObject != null)
/*     */         try {
/* 445 */           localConstructor = localClass.getConstructor(new Class[] { Object.class });
/*     */         }
/*     */         catch (Exception localException1)
/*     */         {
/*     */         }
/*     */       try {
/* 451 */         if (localConstructor == null)
/* 452 */           localObject = localClass.newInstance();
/*     */         else {
/* 454 */           localObject = localConstructor.newInstance(new Object[] { paramObject });
/*     */         }
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/* 459 */         throw new RuntimeException("PropertyEditor not instantiated", localException2);
/*     */       }
/*     */     }
/*     */ 
/* 463 */     return (PropertyEditor)localObject;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 476 */     if (this == paramObject) {
/* 477 */       return true;
/*     */     }
/* 479 */     if ((paramObject != null) && ((paramObject instanceof PropertyDescriptor))) {
/* 480 */       PropertyDescriptor localPropertyDescriptor = (PropertyDescriptor)paramObject;
/* 481 */       Method localMethod1 = localPropertyDescriptor.getReadMethod();
/* 482 */       Method localMethod2 = localPropertyDescriptor.getWriteMethod();
/*     */ 
/* 484 */       if (!compareMethods(getReadMethod(), localMethod1)) {
/* 485 */         return false;
/*     */       }
/*     */ 
/* 488 */       if (!compareMethods(getWriteMethod(), localMethod2)) {
/* 489 */         return false;
/*     */       }
/*     */ 
/* 492 */       if ((getPropertyType() == localPropertyDescriptor.getPropertyType()) && (getPropertyEditorClass() == localPropertyDescriptor.getPropertyEditorClass()) && (this.bound == localPropertyDescriptor.isBound()) && (this.constrained == localPropertyDescriptor.isConstrained()) && (this.writeMethodName == localPropertyDescriptor.writeMethodName) && (this.readMethodName == localPropertyDescriptor.readMethodName))
/*     */       {
/* 497 */         return true;
/*     */       }
/*     */     }
/* 500 */     return false;
/*     */   }
/*     */ 
/*     */   boolean compareMethods(Method paramMethod1, Method paramMethod2)
/*     */   {
/* 512 */     if ((paramMethod1 == null ? 1 : 0) != (paramMethod2 == null ? 1 : 0)) {
/* 513 */       return false;
/*     */     }
/*     */ 
/* 516 */     if ((paramMethod1 != null) && (paramMethod2 != null) && 
/* 517 */       (!paramMethod1.equals(paramMethod2))) {
/* 518 */       return false;
/*     */     }
/*     */ 
/* 521 */     return true;
/*     */   }
/*     */ 
/*     */   PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
/*     */   {
/* 533 */     super(paramPropertyDescriptor1, paramPropertyDescriptor2);
/*     */ 
/* 535 */     if (paramPropertyDescriptor2.baseName != null)
/* 536 */       this.baseName = paramPropertyDescriptor2.baseName;
/*     */     else {
/* 538 */       this.baseName = paramPropertyDescriptor1.baseName;
/*     */     }
/*     */ 
/* 541 */     if (paramPropertyDescriptor2.readMethodName != null)
/* 542 */       this.readMethodName = paramPropertyDescriptor2.readMethodName;
/*     */     else {
/* 544 */       this.readMethodName = paramPropertyDescriptor1.readMethodName;
/*     */     }
/*     */ 
/* 547 */     if (paramPropertyDescriptor2.writeMethodName != null)
/* 548 */       this.writeMethodName = paramPropertyDescriptor2.writeMethodName;
/*     */     else {
/* 550 */       this.writeMethodName = paramPropertyDescriptor1.writeMethodName;
/*     */     }
/*     */ 
/* 553 */     if (paramPropertyDescriptor2.propertyTypeRef != null)
/* 554 */       this.propertyTypeRef = paramPropertyDescriptor2.propertyTypeRef;
/*     */     else {
/* 556 */       this.propertyTypeRef = paramPropertyDescriptor1.propertyTypeRef;
/*     */     }
/*     */ 
/* 560 */     Method localMethod1 = paramPropertyDescriptor1.getReadMethod();
/* 561 */     Method localMethod2 = paramPropertyDescriptor2.getReadMethod();
/*     */     try
/*     */     {
/* 565 */       if (isAssignable(localMethod1, localMethod2))
/* 566 */         setReadMethod(localMethod2);
/*     */       else {
/* 568 */         setReadMethod(localMethod1);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException1)
/*     */     {
/*     */     }
/*     */ 
/* 576 */     if ((localMethod1 != null) && (localMethod2 != null) && (localMethod1.getDeclaringClass() == localMethod2.getDeclaringClass()) && (getReturnType(getClass0(), localMethod1) == Boolean.TYPE) && (getReturnType(getClass0(), localMethod2) == Boolean.TYPE) && (localMethod1.getName().indexOf("is") == 0) && (localMethod2.getName().indexOf("get") == 0))
/*     */     {
/*     */       try
/*     */       {
/* 583 */         setReadMethod(localMethod1);
/*     */       }
/*     */       catch (IntrospectionException localIntrospectionException2)
/*     */       {
/*     */       }
/*     */     }
/* 589 */     Method localMethod3 = paramPropertyDescriptor1.getWriteMethod();
/* 590 */     Method localMethod4 = paramPropertyDescriptor2.getWriteMethod();
/*     */     try
/*     */     {
/* 593 */       if ((localMethod4 != null) && (localMethod4.getDeclaringClass() == getClass0()))
/* 594 */         setWriteMethod(localMethod4);
/*     */       else {
/* 596 */         setWriteMethod(localMethod3);
/*     */       }
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException3)
/*     */     {
/*     */     }
/* 602 */     if (paramPropertyDescriptor2.getPropertyEditorClass() != null)
/* 603 */       setPropertyEditorClass(paramPropertyDescriptor2.getPropertyEditorClass());
/*     */     else {
/* 605 */       setPropertyEditorClass(paramPropertyDescriptor1.getPropertyEditorClass());
/*     */     }
/*     */ 
/* 609 */     paramPropertyDescriptor1.bound |= paramPropertyDescriptor2.bound;
/* 610 */     paramPropertyDescriptor1.constrained |= paramPropertyDescriptor2.constrained;
/*     */   }
/*     */ 
/*     */   PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor)
/*     */   {
/* 618 */     super(paramPropertyDescriptor);
/* 619 */     this.propertyTypeRef = paramPropertyDescriptor.propertyTypeRef;
/* 620 */     this.readMethodRef = paramPropertyDescriptor.readMethodRef;
/* 621 */     this.writeMethodRef = paramPropertyDescriptor.writeMethodRef;
/* 622 */     this.propertyEditorClassRef = paramPropertyDescriptor.propertyEditorClassRef;
/*     */ 
/* 624 */     this.writeMethodName = paramPropertyDescriptor.writeMethodName;
/* 625 */     this.readMethodName = paramPropertyDescriptor.readMethodName;
/* 626 */     this.baseName = paramPropertyDescriptor.baseName;
/*     */ 
/* 628 */     this.bound = paramPropertyDescriptor.bound;
/* 629 */     this.constrained = paramPropertyDescriptor.constrained;
/*     */   }
/*     */ 
/*     */   void updateGenericsFor(Class<?> paramClass) {
/* 633 */     setClass0(paramClass);
/*     */     try {
/* 635 */       setPropertyType(findPropertyType(getReadMethod0(), getWriteMethod0()));
/*     */     }
/*     */     catch (IntrospectionException localIntrospectionException) {
/* 638 */       setPropertyType(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Class findPropertyType(Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 652 */     Class localClass = null;
/*     */     try
/*     */     {
/*     */       Class[] arrayOfClass;
/* 654 */       if (paramMethod1 != null) {
/* 655 */         arrayOfClass = getParameterTypes(getClass0(), paramMethod1);
/* 656 */         if (arrayOfClass.length != 0) {
/* 657 */           throw new IntrospectionException("bad read method arg count: " + paramMethod1);
/*     */         }
/*     */ 
/* 660 */         localClass = getReturnType(getClass0(), paramMethod1);
/* 661 */         if (localClass == Void.TYPE) {
/* 662 */           throw new IntrospectionException("read method " + paramMethod1.getName() + " returns void");
/*     */         }
/*     */       }
/*     */ 
/* 666 */       if (paramMethod2 != null) {
/* 667 */         arrayOfClass = getParameterTypes(getClass0(), paramMethod2);
/* 668 */         if (arrayOfClass.length != 1) {
/* 669 */           throw new IntrospectionException("bad write method arg count: " + paramMethod2);
/*     */         }
/*     */ 
/* 672 */         if ((localClass != null) && (localClass != arrayOfClass[0])) {
/* 673 */           throw new IntrospectionException("type mismatch between read and write methods");
/*     */         }
/* 675 */         localClass = arrayOfClass[0];
/*     */       }
/*     */     } catch (IntrospectionException localIntrospectionException) {
/* 678 */       throw localIntrospectionException;
/*     */     }
/* 680 */     return localClass;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 692 */     int i = 7;
/*     */ 
/* 694 */     i = 37 * i + (getPropertyType() == null ? 0 : getPropertyType().hashCode());
/*     */ 
/* 696 */     i = 37 * i + (getReadMethod() == null ? 0 : getReadMethod().hashCode());
/*     */ 
/* 698 */     i = 37 * i + (getWriteMethod() == null ? 0 : getWriteMethod().hashCode());
/*     */ 
/* 700 */     i = 37 * i + (getPropertyEditorClass() == null ? 0 : getPropertyEditorClass().hashCode());
/*     */ 
/* 702 */     i = 37 * i + (this.writeMethodName == null ? 0 : this.writeMethodName.hashCode());
/*     */ 
/* 704 */     i = 37 * i + (this.readMethodName == null ? 0 : this.readMethodName.hashCode());
/*     */ 
/* 706 */     i = 37 * i + getName().hashCode();
/* 707 */     i = 37 * i + (!this.bound ? 0 : 1);
/* 708 */     i = 37 * i + (!this.constrained ? 0 : 1);
/*     */ 
/* 710 */     return i;
/*     */   }
/*     */ 
/*     */   String getBaseName()
/*     */   {
/* 715 */     if (this.baseName == null) {
/* 716 */       this.baseName = NameGenerator.capitalize(getName());
/*     */     }
/* 718 */     return this.baseName;
/*     */   }
/*     */ 
/*     */   void appendTo(StringBuilder paramStringBuilder) {
/* 722 */     appendTo(paramStringBuilder, "bound", this.bound);
/* 723 */     appendTo(paramStringBuilder, "constrained", this.constrained);
/* 724 */     appendTo(paramStringBuilder, "propertyEditorClass", this.propertyEditorClassRef);
/* 725 */     appendTo(paramStringBuilder, "propertyType", this.propertyTypeRef);
/* 726 */     appendTo(paramStringBuilder, "readMethod", this.readMethodRef);
/* 727 */     appendTo(paramStringBuilder, "writeMethod", this.writeMethodRef);
/*     */   }
/*     */ 
/*     */   private boolean isAssignable(Method paramMethod1, Method paramMethod2) {
/* 731 */     if (paramMethod1 == null) {
/* 732 */       return true;
/*     */     }
/* 734 */     if (paramMethod2 == null) {
/* 735 */       return false;
/*     */     }
/* 737 */     if (!paramMethod1.getName().equals(paramMethod2.getName())) {
/* 738 */       return true;
/*     */     }
/* 740 */     Class localClass1 = paramMethod1.getDeclaringClass();
/* 741 */     Class localClass2 = paramMethod2.getDeclaringClass();
/* 742 */     if (!localClass1.isAssignableFrom(localClass2)) {
/* 743 */       return false;
/*     */     }
/* 745 */     localClass1 = getReturnType(getClass0(), paramMethod1);
/* 746 */     localClass2 = getReturnType(getClass0(), paramMethod2);
/* 747 */     if (!localClass1.isAssignableFrom(localClass2)) {
/* 748 */       return false;
/*     */     }
/* 750 */     Class[] arrayOfClass1 = getParameterTypes(getClass0(), paramMethod1);
/* 751 */     Class[] arrayOfClass2 = getParameterTypes(getClass0(), paramMethod2);
/* 752 */     if (arrayOfClass1.length != arrayOfClass2.length) {
/* 753 */       return true;
/*     */     }
/* 755 */     for (int i = 0; i < arrayOfClass1.length; i++) {
/* 756 */       if (!arrayOfClass1[i].isAssignableFrom(arrayOfClass2[i])) {
/* 757 */         return false;
/*     */       }
/*     */     }
/* 760 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.PropertyDescriptor
 * JD-Core Version:    0.6.2
 */