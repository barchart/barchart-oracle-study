/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.ObjectStreamField;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import sun.invoke.util.BytecodeDescriptor;
/*     */ import sun.invoke.util.Wrapper;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public final class MethodType
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 292L;
/*     */   private final Class<?> rtype;
/*     */   private final Class<?>[] ptypes;
/*     */   private MethodTypeForm form;
/*     */   private MethodType wrapAlt;
/*     */   private Invokers invokers;
/*     */   static final HashMap<MethodType, MethodType> internTable;
/*     */   static final Class<?>[] NO_PTYPES;
/*     */   private static final MethodType[] objectOnlyTypes;
/*     */   private static final ObjectStreamField[] serialPersistentFields;
/*     */   private static final Unsafe unsafe;
/*     */   private static final long rtypeOffset;
/*     */   private static final long ptypesOffset;
/*     */ 
/*     */   private MethodType(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */   {
/* 100 */     checkRtype(paramClass);
/* 101 */     checkPtypes(paramArrayOfClass);
/* 102 */     this.rtype = paramClass;
/* 103 */     this.ptypes = paramArrayOfClass;
/*     */   }
/*     */   MethodTypeForm form() {
/* 106 */     return this.form; } 
/* 107 */   Class<?> rtype() { return this.rtype; } 
/* 108 */   Class<?>[] ptypes() { return this.ptypes; }
/*     */ 
/*     */   private static void checkRtype(Class<?> paramClass) {
/* 111 */     paramClass.equals(paramClass);
/*     */   }
/*     */   private static int checkPtype(Class<?> paramClass) {
/* 114 */     paramClass.getClass();
/* 115 */     if (paramClass == Void.TYPE)
/* 116 */       throw MethodHandleStatics.newIllegalArgumentException("parameter type cannot be void");
/* 117 */     if ((paramClass == Double.TYPE) || (paramClass == Long.TYPE)) return 1;
/* 118 */     return 0;
/*     */   }
/*     */ 
/*     */   private static int checkPtypes(Class<?>[] paramArrayOfClass) {
/* 122 */     int i = 0;
/* 123 */     for (Class<?> localClass : paramArrayOfClass) {
/* 124 */       i += checkPtype(localClass);
/*     */     }
/* 126 */     checkSlotCount(paramArrayOfClass.length + i);
/* 127 */     return i;
/*     */   }
/*     */   private static void checkSlotCount(int paramInt) {
/* 130 */     if ((paramInt & 0xFF) != paramInt)
/* 131 */       throw MethodHandleStatics.newIllegalArgumentException("bad parameter count " + paramInt); 
/*     */   }
/*     */ 
/* 134 */   private static IndexOutOfBoundsException newIndexOutOfBoundsException(Object paramObject) { if ((paramObject instanceof Integer)) paramObject = "bad index: " + paramObject;
/* 135 */     return new IndexOutOfBoundsException(paramObject.toString());
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */   {
/* 153 */     return makeImpl(paramClass, paramArrayOfClass, false);
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass, List<Class<?>> paramList)
/*     */   {
/* 165 */     boolean bool = false;
/* 166 */     return makeImpl(paramClass, listToArray(paramList), bool);
/*     */   }
/*     */ 
/*     */   private static Class<?>[] listToArray(List<Class<?>> paramList)
/*     */   {
/* 171 */     checkSlotCount(paramList.size());
/* 172 */     return (Class[])paramList.toArray(NO_PTYPES);
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass)
/*     */   {
/* 185 */     Class[] arrayOfClass = new Class[1 + paramArrayOfClass.length];
/* 186 */     arrayOfClass[0] = paramClass2;
/* 187 */     System.arraycopy(paramArrayOfClass, 0, arrayOfClass, 1, paramArrayOfClass.length);
/* 188 */     return makeImpl(paramClass1, arrayOfClass, true);
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass)
/*     */   {
/* 200 */     return makeImpl(paramClass, NO_PTYPES, true);
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 213 */     return makeImpl(paramClass1, new Class[] { paramClass2 }, true);
/*     */   }
/*     */ 
/*     */   public static MethodType methodType(Class<?> paramClass, MethodType paramMethodType)
/*     */   {
/* 225 */     return makeImpl(paramClass, paramMethodType.ptypes, true);
/*     */   }
/*     */ 
/*     */   static MethodType makeImpl(Class<?> paramClass, Class<?>[] paramArrayOfClass, boolean paramBoolean)
/*     */   {
/* 237 */     if (paramArrayOfClass.length == 0) {
/* 238 */       paramArrayOfClass = NO_PTYPES; paramBoolean = true;
/*     */     }
/* 240 */     MethodType localMethodType1 = new MethodType(paramClass, paramArrayOfClass);
/*     */     MethodType localMethodType2;
/* 242 */     synchronized (internTable) {
/* 243 */       localMethodType2 = (MethodType)internTable.get(localMethodType1);
/* 244 */       if (localMethodType2 != null)
/* 245 */         return localMethodType2;
/*     */     }
/* 247 */     if (!paramBoolean)
/*     */     {
/* 249 */       localMethodType1 = new MethodType(paramClass, (Class[])paramArrayOfClass.clone());
/*     */     }
/* 251 */     ??? = MethodTypeForm.findForm(localMethodType1);
/* 252 */     localMethodType1.form = ((MethodTypeForm)???);
/* 253 */     if (((MethodTypeForm)???).erasedType == localMethodType1)
/*     */     {
/* 255 */       MethodHandleNatives.init(localMethodType1);
/*     */     }
/* 257 */     synchronized (internTable) {
/* 258 */       localMethodType2 = (MethodType)internTable.get(localMethodType1);
/* 259 */       if (localMethodType2 != null)
/* 260 */         return localMethodType2;
/* 261 */       internTable.put(localMethodType1, localMethodType1);
/*     */     }
/* 263 */     return localMethodType1;
/*     */   }
/*     */ 
/*     */   public static MethodType genericMethodType(int paramInt, boolean paramBoolean)
/*     */   {
/* 282 */     checkSlotCount(paramInt);
/* 283 */     int i = !paramBoolean ? 0 : 1;
/* 284 */     int j = paramInt * 2 + i;
/* 285 */     if (j < objectOnlyTypes.length) {
/* 286 */       localMethodType = objectOnlyTypes[j];
/* 287 */       if (localMethodType != null) return localMethodType;
/*     */     }
/* 289 */     Class[] arrayOfClass = new Class[paramInt + i];
/* 290 */     Arrays.fill(arrayOfClass, Object.class);
/* 291 */     if (i != 0) arrayOfClass[paramInt] = [Ljava.lang.Object.class;
/* 292 */     MethodType localMethodType = makeImpl(Object.class, arrayOfClass, true);
/* 293 */     if (j < objectOnlyTypes.length) {
/* 294 */       objectOnlyTypes[j] = localMethodType;
/*     */     }
/* 296 */     return localMethodType;
/*     */   }
/*     */ 
/*     */   public static MethodType genericMethodType(int paramInt)
/*     */   {
/* 310 */     return genericMethodType(paramInt, false);
/*     */   }
/*     */ 
/*     */   public MethodType changeParameterType(int paramInt, Class<?> paramClass)
/*     */   {
/* 324 */     if (parameterType(paramInt) == paramClass) return this;
/* 325 */     checkPtype(paramClass);
/* 326 */     Class[] arrayOfClass = (Class[])this.ptypes.clone();
/* 327 */     arrayOfClass[paramInt] = paramClass;
/* 328 */     return makeImpl(this.rtype, arrayOfClass, true);
/*     */   }
/*     */ 
/*     */   public MethodType insertParameterTypes(int paramInt, Class<?>[] paramArrayOfClass)
/*     */   {
/* 343 */     int i = this.ptypes.length;
/* 344 */     if ((paramInt < 0) || (paramInt > i))
/* 345 */       throw newIndexOutOfBoundsException(Integer.valueOf(paramInt));
/* 346 */     int j = checkPtypes(paramArrayOfClass);
/* 347 */     checkSlotCount(parameterSlotCount() + paramArrayOfClass.length + j);
/* 348 */     int k = paramArrayOfClass.length;
/* 349 */     if (k == 0) return this;
/* 350 */     Class[] arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, i + k);
/* 351 */     System.arraycopy(arrayOfClass, paramInt, arrayOfClass, paramInt + k, i - paramInt);
/* 352 */     System.arraycopy(paramArrayOfClass, 0, arrayOfClass, paramInt, k);
/* 353 */     return makeImpl(this.rtype, arrayOfClass, true);
/*     */   }
/*     */ 
/*     */   public MethodType appendParameterTypes(Class<?>[] paramArrayOfClass)
/*     */   {
/* 366 */     return insertParameterTypes(parameterCount(), paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   public MethodType insertParameterTypes(int paramInt, List<Class<?>> paramList)
/*     */   {
/* 381 */     return insertParameterTypes(paramInt, listToArray(paramList));
/*     */   }
/*     */ 
/*     */   public MethodType appendParameterTypes(List<Class<?>> paramList)
/*     */   {
/* 394 */     return insertParameterTypes(parameterCount(), paramList);
/*     */   }
/*     */ 
/*     */   public MethodType dropParameterTypes(int paramInt1, int paramInt2)
/*     */   {
/* 408 */     int i = this.ptypes.length;
/* 409 */     if ((0 > paramInt1) || (paramInt1 > paramInt2) || (paramInt2 > i))
/* 410 */       throw newIndexOutOfBoundsException("start=" + paramInt1 + " end=" + paramInt2);
/* 411 */     if (paramInt1 == paramInt2) return this;
/*     */     Class[] arrayOfClass;
/* 413 */     if (paramInt1 == 0) {
/* 414 */       if (paramInt2 == i)
/*     */       {
/* 416 */         arrayOfClass = NO_PTYPES;
/*     */       }
/*     */       else {
/* 419 */         arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, paramInt2, i);
/*     */       }
/*     */     }
/* 422 */     else if (paramInt2 == i)
/*     */     {
/* 424 */       arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, paramInt1);
/*     */     } else {
/* 426 */       int j = i - paramInt2;
/* 427 */       arrayOfClass = (Class[])Arrays.copyOfRange(this.ptypes, 0, paramInt1 + j);
/* 428 */       System.arraycopy(this.ptypes, paramInt2, arrayOfClass, paramInt1, j);
/*     */     }
/*     */ 
/* 431 */     return makeImpl(this.rtype, arrayOfClass, true);
/*     */   }
/*     */ 
/*     */   public MethodType changeReturnType(Class<?> paramClass)
/*     */   {
/* 442 */     if (returnType() == paramClass) return this;
/* 443 */     return makeImpl(paramClass, this.ptypes, true);
/*     */   }
/*     */ 
/*     */   public boolean hasPrimitives()
/*     */   {
/* 452 */     return this.form.hasPrimitives();
/*     */   }
/*     */ 
/*     */   public boolean hasWrappers()
/*     */   {
/* 463 */     return unwrap() != this;
/*     */   }
/*     */ 
/*     */   public MethodType erase()
/*     */   {
/* 473 */     return this.form.erasedType();
/*     */   }
/*     */ 
/*     */   public MethodType generic()
/*     */   {
/* 484 */     return genericMethodType(parameterCount());
/*     */   }
/*     */ 
/*     */   public MethodType wrap()
/*     */   {
/* 497 */     return hasPrimitives() ? wrapWithPrims(this) : this;
/*     */   }
/*     */ 
/*     */   public MethodType unwrap()
/*     */   {
/* 508 */     MethodType localMethodType = !hasPrimitives() ? this : wrapWithPrims(this);
/* 509 */     return unwrapWithNoPrims(localMethodType);
/*     */   }
/*     */ 
/*     */   private static MethodType wrapWithPrims(MethodType paramMethodType) {
/* 513 */     assert (paramMethodType.hasPrimitives());
/* 514 */     MethodType localMethodType = paramMethodType.wrapAlt;
/* 515 */     if (localMethodType == null)
/*     */     {
/* 517 */       localMethodType = MethodTypeForm.canonicalize(paramMethodType, 2, 2);
/* 518 */       assert (localMethodType != null);
/* 519 */       paramMethodType.wrapAlt = localMethodType;
/*     */     }
/* 521 */     return localMethodType;
/*     */   }
/*     */ 
/*     */   private static MethodType unwrapWithNoPrims(MethodType paramMethodType) {
/* 525 */     assert (!paramMethodType.hasPrimitives());
/* 526 */     MethodType localMethodType = paramMethodType.wrapAlt;
/* 527 */     if (localMethodType == null)
/*     */     {
/* 529 */       localMethodType = MethodTypeForm.canonicalize(paramMethodType, 3, 3);
/* 530 */       if (localMethodType == null)
/* 531 */         localMethodType = paramMethodType;
/* 532 */       paramMethodType.wrapAlt = localMethodType;
/*     */     }
/* 534 */     return localMethodType;
/*     */   }
/*     */ 
/*     */   public Class<?> parameterType(int paramInt)
/*     */   {
/* 544 */     return this.ptypes[paramInt];
/*     */   }
/*     */ 
/*     */   public int parameterCount()
/*     */   {
/* 551 */     return this.ptypes.length;
/*     */   }
/*     */ 
/*     */   public Class<?> returnType()
/*     */   {
/* 558 */     return this.rtype;
/*     */   }
/*     */ 
/*     */   public List<Class<?>> parameterList()
/*     */   {
/* 567 */     return Collections.unmodifiableList(Arrays.asList(this.ptypes));
/*     */   }
/*     */ 
/*     */   public Class<?>[] parameterArray()
/*     */   {
/* 576 */     return (Class[])this.ptypes.clone();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 588 */     return (this == paramObject) || (((paramObject instanceof MethodType)) && (equals((MethodType)paramObject)));
/*     */   }
/*     */ 
/*     */   private boolean equals(MethodType paramMethodType) {
/* 592 */     return (this.rtype == paramMethodType.rtype) && (Arrays.equals(this.ptypes, paramMethodType.ptypes));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 608 */     int i = 31 + this.rtype.hashCode();
/* 609 */     for (Class localClass : this.ptypes)
/* 610 */       i = 31 * i + localClass.hashCode();
/* 611 */     return i;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 626 */     StringBuilder localStringBuilder = new StringBuilder();
/* 627 */     localStringBuilder.append("(");
/* 628 */     for (int i = 0; i < this.ptypes.length; i++) {
/* 629 */       if (i > 0) localStringBuilder.append(",");
/* 630 */       localStringBuilder.append(this.ptypes[i].getSimpleName());
/*     */     }
/* 632 */     localStringBuilder.append(")");
/* 633 */     localStringBuilder.append(this.rtype.getSimpleName());
/* 634 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   boolean isConvertibleTo(MethodType paramMethodType)
/*     */   {
/* 640 */     if (!canConvert(returnType(), paramMethodType.returnType()))
/* 641 */       return false;
/* 642 */     int i = parameterCount();
/* 643 */     if (i != paramMethodType.parameterCount())
/* 644 */       return false;
/* 645 */     for (int j = 0; j < i; j++) {
/* 646 */       if (!canConvert(paramMethodType.parameterType(j), parameterType(j)))
/* 647 */         return false;
/*     */     }
/* 649 */     return true;
/*     */   }
/*     */ 
/*     */   static boolean canConvert(Class<?> paramClass1, Class<?> paramClass2)
/*     */   {
/* 654 */     if ((paramClass1 == paramClass2) || (paramClass2 == Object.class)) return true;
/*     */     Wrapper localWrapper;
/* 656 */     if (paramClass1.isPrimitive())
/*     */     {
/* 659 */       if (paramClass1 == Void.TYPE) return true;
/* 660 */       localWrapper = Wrapper.forPrimitiveType(paramClass1);
/* 661 */       if (paramClass2.isPrimitive())
/*     */       {
/* 663 */         return Wrapper.forPrimitiveType(paramClass2).isConvertibleFrom(localWrapper);
/*     */       }
/*     */ 
/* 666 */       return paramClass2.isAssignableFrom(localWrapper.wrapperType());
/*     */     }
/* 668 */     if (paramClass2.isPrimitive())
/*     */     {
/* 670 */       if (paramClass2 == Void.TYPE) return true;
/* 671 */       localWrapper = Wrapper.forPrimitiveType(paramClass2);
/*     */ 
/* 678 */       if (paramClass1.isAssignableFrom(localWrapper.wrapperType())) {
/* 679 */         return true;
/*     */       }
/*     */ 
/* 685 */       if ((Wrapper.isWrapperType(paramClass1)) && (localWrapper.isConvertibleFrom(Wrapper.forWrapperType(paramClass1))))
/*     */       {
/* 688 */         return true;
/*     */       }
/*     */ 
/* 697 */       return false;
/*     */     }
/*     */ 
/* 700 */     return true;
/*     */   }
/*     */ 
/*     */   int parameterSlotCount()
/*     */   {
/* 717 */     return this.form.parameterSlotCount();
/*     */   }
/*     */ 
/*     */   Invokers invokers() {
/* 721 */     Invokers localInvokers = this.invokers;
/* 722 */     if (localInvokers != null) return localInvokers;
/* 723 */     this.invokers = (localInvokers = new Invokers(this));
/* 724 */     return localInvokers;
/*     */   }
/*     */ 
/*     */   int parameterSlotDepth(int paramInt)
/*     */   {
/* 751 */     if ((paramInt < 0) || (paramInt > this.ptypes.length))
/* 752 */       parameterType(paramInt);
/* 753 */     return this.form.parameterToArgSlot(paramInt - 1);
/*     */   }
/*     */ 
/*     */   int returnSlotCount()
/*     */   {
/* 767 */     return this.form.returnSlotCount();
/*     */   }
/*     */ 
/*     */   public static MethodType fromMethodDescriptorString(String paramString, ClassLoader paramClassLoader)
/*     */     throws IllegalArgumentException, TypeNotPresentException
/*     */   {
/* 793 */     if ((!paramString.startsWith("(")) || (paramString.indexOf(')') < 0) || (paramString.indexOf('.') >= 0))
/*     */     {
/* 796 */       throw new IllegalArgumentException("not a method descriptor: " + paramString);
/* 797 */     }List localList = BytecodeDescriptor.parseMethod(paramString, paramClassLoader);
/* 798 */     Class localClass = (Class)localList.remove(localList.size() - 1);
/* 799 */     checkSlotCount(localList.size());
/* 800 */     Class[] arrayOfClass = listToArray(localList);
/* 801 */     return makeImpl(localClass, arrayOfClass, true);
/*     */   }
/*     */ 
/*     */   public String toMethodDescriptorString()
/*     */   {
/* 818 */     return BytecodeDescriptor.unparse(this);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 848 */     paramObjectOutputStream.defaultWriteObject();
/* 849 */     paramObjectOutputStream.writeObject(returnType());
/* 850 */     paramObjectOutputStream.writeObject(parameterArray());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 866 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 868 */     Class localClass = (Class)paramObjectInputStream.readObject();
/* 869 */     Class[] arrayOfClass = (Class[])paramObjectInputStream.readObject();
/*     */ 
/* 873 */     checkRtype(localClass);
/* 874 */     checkPtypes(arrayOfClass);
/*     */ 
/* 876 */     arrayOfClass = (Class[])arrayOfClass.clone();
/* 877 */     MethodType_init(localClass, arrayOfClass);
/*     */   }
/*     */ 
/*     */   private MethodType()
/*     */   {
/* 885 */     this.rtype = null;
/* 886 */     this.ptypes = null;
/*     */   }
/*     */ 
/*     */   private void MethodType_init(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */   {
/* 891 */     checkRtype(paramClass);
/* 892 */     checkPtypes(paramArrayOfClass);
/* 893 */     unsafe.putObject(this, rtypeOffset, paramClass);
/* 894 */     unsafe.putObject(this, ptypesOffset, paramArrayOfClass);
/*     */   }
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 920 */     return methodType(this.rtype, this.ptypes);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 138 */     internTable = new HashMap();
/*     */ 
/* 141 */     NO_PTYPES = new Class[0];
/*     */ 
/* 266 */     objectOnlyTypes = new MethodType[20];
/*     */ 
/* 826 */     serialPersistentFields = new ObjectStreamField[0];
/*     */ 
/* 898 */     unsafe = Unsafe.getUnsafe();
/*     */     try
/*     */     {
/* 902 */       rtypeOffset = unsafe.objectFieldOffset(MethodType.class.getDeclaredField("rtype"));
/*     */ 
/* 904 */       ptypesOffset = unsafe.objectFieldOffset(MethodType.class.getDeclaredField("ptypes"));
/*     */     }
/*     */     catch (Exception localException) {
/* 907 */       throw new Error(localException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodType
 * JD-Core Version:    0.6.2
 */