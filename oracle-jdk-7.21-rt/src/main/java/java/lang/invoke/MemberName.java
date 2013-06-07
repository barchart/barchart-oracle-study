/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import sun.invoke.util.BytecodeDescriptor;
/*     */ 
/*     */ final class MemberName
/*     */   implements Member, Cloneable
/*     */ {
/*     */   private Class<?> clazz;
/*     */   private String name;
/*     */   private Object type;
/*     */   private int flags;
/*     */   private Object vmtarget;
/*  78 */   private int vmindex = -99;
/*     */   static final int BRIDGE = 64;
/*     */   static final int VARARGS = 128;
/*     */   static final int SYNTHETIC = 4096;
/*     */   static final int ANNOTATION = 8192;
/*     */   static final int ENUM = 16384;
/*     */   static final String CONSTRUCTOR_NAME = "<init>";
/*     */   static final int RECOGNIZED_MODIFIERS = 65535;
/*     */   static final int IS_METHOD = 65536;
/*     */   static final int IS_CONSTRUCTOR = 131072;
/*     */   static final int IS_FIELD = 262144;
/*     */   static final int IS_TYPE = 524288;
/*     */   static final int SEARCH_SUPERCLASSES = 1048576;
/*     */   static final int SEARCH_INTERFACES = 2097152;
/*     */   static final int ALL_ACCESS = 7;
/*     */   static final int ALL_KINDS = 983040;
/*     */   static final int IS_INVOCABLE = 196608;
/*     */   static final int IS_FIELD_OR_METHOD = 327680;
/*     */   static final int SEARCH_ALL_SUPERS = 3145728;
/*     */ 
/*     */   public Class<?> getDeclaringClass()
/*     */   {
/*  84 */     if ((this.clazz == null) && (isResolved())) {
/*  85 */       expandFromVM();
/*     */     }
/*  87 */     return this.clazz;
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader()
/*     */   {
/*  92 */     return this.clazz.getClassLoader();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 101 */     if (this.name == null) {
/* 102 */       expandFromVM();
/* 103 */       if (this.name == null) return null;
/*     */     }
/* 105 */     return this.name;
/*     */   }
/*     */ 
/*     */   public MethodType getMethodType()
/*     */   {
/* 112 */     if (this.type == null) {
/* 113 */       expandFromVM();
/* 114 */       if (this.type == null) return null;
/*     */     }
/* 116 */     if (!isInvocable())
/* 117 */       throw MethodHandleStatics.newIllegalArgumentException("not invocable, no method type");
/* 118 */     if ((this.type instanceof MethodType))
/* 119 */       return (MethodType)this.type;
/*     */     Object localObject1;
/*     */     Object localObject2;
/* 121 */     if ((this.type instanceof String)) {
/* 122 */       localObject1 = (String)this.type;
/* 123 */       localObject2 = MethodType.fromMethodDescriptorString((String)localObject1, getClassLoader());
/* 124 */       this.type = localObject2;
/* 125 */       return localObject2;
/*     */     }
/* 127 */     if ((this.type instanceof Object[])) {
/* 128 */       localObject1 = (Object[])this.type;
/* 129 */       localObject2 = (Class[])localObject1[1];
/* 130 */       Class localClass = (Class)localObject1[0];
/* 131 */       MethodType localMethodType = MethodType.methodType(localClass, (Class[])localObject2);
/* 132 */       this.type = localMethodType;
/* 133 */       return localMethodType;
/*     */     }
/* 135 */     throw new InternalError("bad method type " + this.type);
/*     */   }
/*     */ 
/*     */   public MethodType getInvocationType()
/*     */   {
/* 143 */     MethodType localMethodType = getMethodType();
/* 144 */     if (!isStatic())
/* 145 */       localMethodType = localMethodType.insertParameterTypes(0, new Class[] { this.clazz });
/* 146 */     return localMethodType;
/*     */   }
/*     */ 
/*     */   public Class<?>[] getParameterTypes()
/*     */   {
/* 151 */     return getMethodType().parameterArray();
/*     */   }
/*     */ 
/*     */   public Class<?> getReturnType()
/*     */   {
/* 156 */     return getMethodType().returnType();
/*     */   }
/*     */ 
/*     */   public Class<?> getFieldType()
/*     */   {
/* 164 */     if (this.type == null) {
/* 165 */       expandFromVM();
/* 166 */       if (this.type == null) return null;
/*     */     }
/* 168 */     if (isInvocable())
/* 169 */       throw MethodHandleStatics.newIllegalArgumentException("not a field or nested class, no simple type");
/* 170 */     if ((this.type instanceof Class)) {
/* 171 */       return (Class)this.type;
/*     */     }
/* 173 */     if ((this.type instanceof String)) {
/* 174 */       String str = (String)this.type;
/* 175 */       MethodType localMethodType = MethodType.fromMethodDescriptorString("()" + str, getClassLoader());
/* 176 */       Class localClass = localMethodType.returnType();
/* 177 */       this.type = localClass;
/* 178 */       return localClass;
/*     */     }
/* 180 */     throw new InternalError("bad field type " + this.type);
/*     */   }
/*     */ 
/*     */   public Object getType()
/*     */   {
/* 185 */     return isInvocable() ? getMethodType() : getFieldType();
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/* 192 */     if (this.type == null) {
/* 193 */       expandFromVM();
/* 194 */       if (this.type == null) return null;
/*     */     }
/* 196 */     if ((this.type instanceof String))
/* 197 */       return (String)this.type;
/* 198 */     if (isInvocable()) {
/* 199 */       return BytecodeDescriptor.unparse(getMethodType());
/*     */     }
/* 201 */     return BytecodeDescriptor.unparse(getFieldType());
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/* 208 */     return this.flags & 0xFFFF;
/*     */   }
/*     */ 
/*     */   private void setFlags(int paramInt) {
/* 212 */     this.flags = paramInt;
/* 213 */     assert (testAnyFlags(983040));
/*     */   }
/*     */ 
/*     */   private boolean testFlags(int paramInt1, int paramInt2) {
/* 217 */     return (this.flags & paramInt1) == paramInt2;
/*     */   }
/*     */   private boolean testAllFlags(int paramInt) {
/* 220 */     return testFlags(paramInt, paramInt);
/*     */   }
/*     */   private boolean testAnyFlags(int paramInt) {
/* 223 */     return !testFlags(paramInt, 0);
/*     */   }
/*     */ 
/*     */   public boolean isStatic()
/*     */   {
/* 228 */     return Modifier.isStatic(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isPublic() {
/* 232 */     return Modifier.isPublic(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isPrivate() {
/* 236 */     return Modifier.isPrivate(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isProtected() {
/* 240 */     return Modifier.isProtected(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isFinal() {
/* 244 */     return Modifier.isFinal(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isAbstract() {
/* 248 */     return Modifier.isAbstract(this.flags);
/*     */   }
/*     */ 
/*     */   public boolean isBridge()
/*     */   {
/* 260 */     return testAllFlags(65600);
/*     */   }
/*     */ 
/*     */   public boolean isVarargs() {
/* 264 */     return (testAllFlags(128)) && (isInvocable());
/*     */   }
/*     */ 
/*     */   public boolean isSynthetic() {
/* 268 */     return testAllFlags(4096);
/*     */   }
/*     */ 
/*     */   public boolean isInvocable()
/*     */   {
/* 294 */     return testAnyFlags(196608);
/*     */   }
/*     */ 
/*     */   public boolean isFieldOrMethod() {
/* 298 */     return testAnyFlags(327680);
/*     */   }
/*     */ 
/*     */   public boolean isMethod() {
/* 302 */     return testAllFlags(65536);
/*     */   }
/*     */ 
/*     */   public boolean isConstructor() {
/* 306 */     return testAllFlags(131072);
/*     */   }
/*     */ 
/*     */   public boolean isField() {
/* 310 */     return testAllFlags(262144);
/*     */   }
/*     */ 
/*     */   public boolean isType() {
/* 314 */     return testAllFlags(524288);
/*     */   }
/*     */ 
/*     */   public boolean isPackage() {
/* 318 */     return !testAnyFlags(7);
/*     */   }
/*     */ 
/*     */   private void init(Class<?> paramClass, String paramString, Object paramObject, int paramInt)
/*     */   {
/* 327 */     this.clazz = paramClass;
/* 328 */     this.name = paramString;
/* 329 */     this.type = paramObject;
/* 330 */     setFlags(paramInt);
/* 331 */     assert (!isResolved());
/*     */   }
/*     */ 
/*     */   private void expandFromVM() {
/* 335 */     if (!isResolved()) return;
/* 336 */     if ((this.type instanceof Object[]))
/* 337 */       this.type = null;
/* 338 */     MethodHandleNatives.expand(this);
/*     */   }
/*     */ 
/*     */   private static int flagsMods(int paramInt1, int paramInt2)
/*     */   {
/* 343 */     assert ((paramInt1 & 0xFFFF) == 0);
/* 344 */     assert ((paramInt2 & 0xFFFF0000) == 0);
/* 345 */     return paramInt1 | paramInt2;
/*     */   }
/*     */ 
/*     */   public MemberName(Method paramMethod) {
/* 349 */     Object[] arrayOfObject = { paramMethod.getReturnType(), paramMethod.getParameterTypes() };
/* 350 */     init(paramMethod.getDeclaringClass(), paramMethod.getName(), arrayOfObject, flagsMods(65536, paramMethod.getModifiers()));
/*     */ 
/* 352 */     MethodHandleNatives.init(this, paramMethod);
/* 353 */     assert (isResolved());
/*     */   }
/*     */ 
/*     */   public MemberName(Constructor paramConstructor) {
/* 357 */     Object[] arrayOfObject = { Void.TYPE, paramConstructor.getParameterTypes() };
/* 358 */     init(paramConstructor.getDeclaringClass(), "<init>", arrayOfObject, flagsMods(131072, paramConstructor.getModifiers()));
/*     */ 
/* 360 */     MethodHandleNatives.init(this, paramConstructor);
/* 361 */     assert (isResolved());
/*     */   }
/*     */ 
/*     */   public MemberName(Field paramField) {
/* 365 */     init(paramField.getDeclaringClass(), paramField.getName(), paramField.getType(), flagsMods(262144, paramField.getModifiers()));
/*     */ 
/* 367 */     MethodHandleNatives.init(this, paramField);
/* 368 */     assert (isResolved());
/*     */   }
/*     */ 
/*     */   public MemberName(Class<?> paramClass) {
/* 372 */     init(paramClass.getDeclaringClass(), paramClass.getSimpleName(), paramClass, flagsMods(524288, paramClass.getModifiers()));
/* 373 */     this.vmindex = 0;
/* 374 */     assert (isResolved());
/*     */   }
/*     */ 
/*     */   MemberName()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected MemberName clone() {
/*     */     try {
/* 383 */       return (MemberName)super.clone(); } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 385 */     throw new InternalError();
/*     */   }
/*     */ 
/*     */   public MemberName(Class<?> paramClass1, String paramString, Class<?> paramClass2, int paramInt)
/*     */   {
/* 397 */     init(paramClass1, paramString, paramClass2, 0x40000 | paramInt & 0xFFFF);
/*     */   }
/*     */ 
/*     */   public MemberName(Class<?> paramClass1, String paramString, Class<?> paramClass2)
/*     */   {
/* 405 */     this(paramClass1, paramString, paramClass2, 0);
/*     */   }
/*     */ 
/*     */   public MemberName(Class<?> paramClass, String paramString, MethodType paramMethodType, int paramInt)
/*     */   {
/* 413 */     int i = paramString.equals("<init>") ? 131072 : 65536;
/* 414 */     init(paramClass, paramString, paramMethodType, i | paramInt & 0xFFFF);
/*     */   }
/*     */ 
/*     */   public MemberName(Class<?> paramClass, String paramString, MethodType paramMethodType)
/*     */   {
/* 423 */     this(paramClass, paramString, paramMethodType, 0);
/*     */   }
/*     */ 
/*     */   public boolean isResolved()
/*     */   {
/* 432 */     return this.vmindex != -99;
/*     */   }
/*     */ 
/*     */   public boolean hasReceiverTypeDispatch()
/*     */   {
/* 438 */     return (isMethod()) && (getVMIndex() >= 0);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 450 */     if (isType()) {
/* 451 */       return this.type.toString();
/*     */     }
/* 453 */     StringBuilder localStringBuilder = new StringBuilder();
/* 454 */     if (getDeclaringClass() != null) {
/* 455 */       localStringBuilder.append(getName(this.clazz));
/* 456 */       localStringBuilder.append('.');
/*     */     }
/* 458 */     String str = getName();
/* 459 */     localStringBuilder.append(str == null ? "*" : str);
/* 460 */     Object localObject = getType();
/* 461 */     if (!isInvocable()) {
/* 462 */       localStringBuilder.append('/');
/* 463 */       localStringBuilder.append(localObject == null ? "*" : getName(localObject));
/*     */     } else {
/* 465 */       localStringBuilder.append(localObject == null ? "(*)*" : getName(localObject));
/*     */     }
/*     */ 
/* 483 */     return localStringBuilder.toString();
/*     */   }
/*     */   private static String getName(Object paramObject) {
/* 486 */     if ((paramObject instanceof Class))
/* 487 */       return ((Class)paramObject).getName();
/* 488 */     return String.valueOf(paramObject);
/*     */   }
/*     */ 
/*     */   int getVMIndex()
/*     */   {
/* 494 */     if (!isResolved())
/* 495 */       throw MethodHandleStatics.newIllegalStateException("not resolved", this);
/* 496 */     return this.vmindex;
/*     */   }
/*     */ 
/*     */   public IllegalAccessException makeAccessException(String paramString, Object paramObject)
/*     */   {
/* 505 */     paramString = paramString + ": " + toString();
/* 506 */     if (paramObject != null) paramString = paramString + ", from " + paramObject;
/* 507 */     return new IllegalAccessException(paramString);
/*     */   }
/*     */   private String message() {
/* 510 */     if (isResolved())
/* 511 */       return "no access";
/* 512 */     if (isConstructor())
/* 513 */       return "no such constructor";
/* 514 */     if (isMethod()) {
/* 515 */       return "no such method";
/*     */     }
/* 517 */     return "no such field";
/*     */   }
/*     */   public ReflectiveOperationException makeAccessException() {
/* 520 */     String str = message() + ": " + toString();
/* 521 */     if (isResolved())
/* 522 */       return new IllegalAccessException(str);
/* 523 */     if (isConstructor())
/* 524 */       return new NoSuchMethodException(str);
/* 525 */     if (isMethod()) {
/* 526 */       return new NoSuchMethodException(str);
/*     */     }
/* 528 */     return new NoSuchFieldException(str);
/*     */   }
/*     */ 
/*     */   static Factory getFactory()
/*     */   {
/* 533 */     return Factory.INSTANCE;
/*     */   }
/*     */ 
/*     */   static class Factory
/*     */   {
/* 540 */     static Factory INSTANCE = new Factory();
/*     */ 
/* 542 */     private static int ALLOWED_FLAGS = 4128768;
/*     */ 
/*     */     List<MemberName> getMembers(Class<?> paramClass1, String paramString, Object paramObject, int paramInt, Class<?> paramClass2)
/*     */     {
/* 548 */       paramInt &= ALLOWED_FLAGS;
/* 549 */       String str = null;
/* 550 */       if (paramObject != null) {
/* 551 */         str = BytecodeDescriptor.unparse(paramObject);
/* 552 */         if (str.startsWith("("))
/* 553 */           paramInt &= -786433;
/*     */         else {
/* 555 */           paramInt &= -720897;
/*     */         }
/*     */       }
/* 558 */       int i = paramObject == null ? 4 : paramString == null ? 10 : 1;
/* 559 */       MemberName[] arrayOfMemberName = newMemberBuffer(i);
/* 560 */       int j = 0;
/* 561 */       ArrayList localArrayList1 = null;
/* 562 */       int k = 0;
/*     */       while (true) {
/* 564 */         k = MethodHandleNatives.getMembers(paramClass1, paramString, str, paramInt, paramClass2, j, arrayOfMemberName);
/*     */ 
/* 568 */         if (k <= arrayOfMemberName.length) {
/* 569 */           if (k < 0) k = 0;
/* 570 */           j += k;
/* 571 */           break;
/*     */         }
/*     */ 
/* 574 */         j += arrayOfMemberName.length;
/* 575 */         int m = k - arrayOfMemberName.length;
/* 576 */         if (localArrayList1 == null) localArrayList1 = new ArrayList(1);
/* 577 */         localArrayList1.add(arrayOfMemberName);
/* 578 */         int n = arrayOfMemberName.length;
/* 579 */         n = Math.max(n, m);
/* 580 */         n = Math.max(n, j / 4);
/* 581 */         arrayOfMemberName = newMemberBuffer(Math.min(8192, n));
/*     */       }
/* 583 */       ArrayList localArrayList2 = new ArrayList(j);
/*     */       Iterator localIterator;
/* 584 */       if (localArrayList1 != null)
/* 585 */         for (localIterator = localArrayList1.iterator(); localIterator.hasNext(); ) { localObject = (MemberName[])localIterator.next();
/* 586 */           Collections.addAll(localArrayList2, (Object[])localObject);
/*     */         }
/*     */       Object localObject;
/* 589 */       localArrayList2.addAll(Arrays.asList(arrayOfMemberName).subList(0, k));
/*     */ 
/* 593 */       if ((paramObject != null) && (paramObject != str)) {
/* 594 */         for (localIterator = localArrayList2.iterator(); localIterator.hasNext(); ) {
/* 595 */           localObject = (MemberName)localIterator.next();
/* 596 */           if (!paramObject.equals(((MemberName)localObject).getType()))
/* 597 */             localIterator.remove();
/*     */         }
/*     */       }
/* 600 */       return localArrayList2;
/*     */     }
/*     */     boolean resolveInPlace(MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass) {
/* 603 */       if ((paramMemberName.name == null) || (paramMemberName.type == null)) {
/* 604 */         Class localClass = paramMemberName.getDeclaringClass();
/* 605 */         localObject = null;
/* 606 */         if (paramMemberName.isMethod())
/* 607 */           localObject = getMethods(localClass, paramBoolean, paramMemberName.name, (MethodType)paramMemberName.type, paramClass);
/* 608 */         else if (paramMemberName.isConstructor())
/* 609 */           localObject = getConstructors(localClass, paramClass);
/* 610 */         else if (paramMemberName.isField()) {
/* 611 */           localObject = getFields(localClass, paramBoolean, paramMemberName.name, (Class)paramMemberName.type, paramClass);
/*     */         }
/* 613 */         if ((localObject == null) || (((List)localObject).size() != 1))
/* 614 */           return false;
/* 615 */         if (paramMemberName.name == null) paramMemberName.name = ((MemberName)((List)localObject).get(0)).name;
/* 616 */         if (paramMemberName.type == null) paramMemberName.type = ((MemberName)((List)localObject).get(0)).type;
/*     */       }
/* 618 */       MethodHandleNatives.resolve(paramMemberName, paramClass);
/* 619 */       if (paramMemberName.isResolved()) return true;
/* 620 */       int i = paramMemberName.flags | (paramBoolean ? 3145728 : 0);
/* 621 */       Object localObject = paramMemberName.getSignature();
/* 622 */       MemberName[] arrayOfMemberName = { paramMemberName };
/* 623 */       int j = MethodHandleNatives.getMembers(paramMemberName.getDeclaringClass(), paramMemberName.getName(), (String)localObject, i, paramClass, 0, arrayOfMemberName);
/*     */ 
/* 625 */       if (j != 1) return false;
/* 626 */       return paramMemberName.isResolved();
/*     */     }
/*     */ 
/*     */     public MemberName resolveOrNull(MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass)
/*     */     {
/* 635 */       MemberName localMemberName = paramMemberName.clone();
/* 636 */       if (resolveInPlace(localMemberName, paramBoolean, paramClass))
/* 637 */         return localMemberName;
/* 638 */       return null;
/*     */     }
/*     */ 
/*     */     public <NoSuchMemberException extends ReflectiveOperationException> MemberName resolveOrFail(MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass, Class<NoSuchMemberException> paramClass1)
/*     */       throws IllegalAccessException, ReflectiveOperationException
/*     */     {
/* 651 */       MemberName localMemberName = resolveOrNull(paramMemberName, paramBoolean, paramClass);
/* 652 */       if (localMemberName != null)
/* 653 */         return localMemberName;
/* 654 */       ReflectiveOperationException localReflectiveOperationException = paramMemberName.makeAccessException();
/* 655 */       if ((localReflectiveOperationException instanceof IllegalAccessException)) throw ((IllegalAccessException)localReflectiveOperationException);
/* 656 */       throw ((ReflectiveOperationException)paramClass1.cast(localReflectiveOperationException));
/*     */     }
/*     */ 
/*     */     public List<MemberName> getMethods(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
/*     */     {
/* 665 */       return getMethods(paramClass1, paramBoolean, null, null, paramClass2);
/*     */     }
/*     */ 
/*     */     public List<MemberName> getMethods(Class<?> paramClass1, boolean paramBoolean, String paramString, MethodType paramMethodType, Class<?> paramClass2)
/*     */     {
/* 675 */       int i = 0x10000 | (paramBoolean ? 3145728 : 0);
/* 676 */       return getMembers(paramClass1, paramString, paramMethodType, i, paramClass2);
/*     */     }
/*     */ 
/*     */     public List<MemberName> getConstructors(Class<?> paramClass1, Class<?> paramClass2)
/*     */     {
/* 683 */       return getMembers(paramClass1, null, null, 131072, paramClass2);
/*     */     }
/*     */ 
/*     */     public List<MemberName> getFields(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
/*     */     {
/* 692 */       return getFields(paramClass1, paramBoolean, null, null, paramClass2);
/*     */     }
/*     */ 
/*     */     public List<MemberName> getFields(Class<?> paramClass1, boolean paramBoolean, String paramString, Class<?> paramClass2, Class<?> paramClass3)
/*     */     {
/* 702 */       int i = 0x40000 | (paramBoolean ? 3145728 : 0);
/* 703 */       return getMembers(paramClass1, paramString, paramClass2, i, paramClass3);
/*     */     }
/*     */ 
/*     */     public List<MemberName> getNestedTypes(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
/*     */     {
/* 712 */       int i = 0x80000 | (paramBoolean ? 3145728 : 0);
/* 713 */       return getMembers(paramClass1, null, null, i, paramClass2);
/*     */     }
/*     */     private static MemberName[] newMemberBuffer(int paramInt) {
/* 716 */       MemberName[] arrayOfMemberName = new MemberName[paramInt];
/*     */ 
/* 718 */       for (int i = 0; i < paramInt; i++)
/* 719 */         arrayOfMemberName[i] = new MemberName();
/* 720 */       return arrayOfMemberName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MemberName
 * JD-Core Version:    0.6.2
 */