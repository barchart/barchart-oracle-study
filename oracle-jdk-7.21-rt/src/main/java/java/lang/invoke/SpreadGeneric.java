/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import sun.invoke.util.ValueConversions;
/*     */ 
/*     */ class SpreadGeneric
/*     */ {
/*     */   private final MethodType targetType;
/*     */   private final int spreadCount;
/*     */   private final Adapter adapter;
/*     */   private final MethodHandle entryPoint;
/*     */ 
/*     */   private SpreadGeneric(MethodType paramMethodType, int paramInt)
/*     */   {
/*  54 */     assert (paramMethodType == paramMethodType.generic());
/*  55 */     this.targetType = paramMethodType;
/*  56 */     this.spreadCount = paramInt;
/*     */ 
/*  58 */     MethodHandle[] arrayOfMethodHandle = { null };
/*  59 */     Adapter localAdapter = findAdapter(this, arrayOfMethodHandle);
/*  60 */     if (localAdapter != null) {
/*  61 */       this.adapter = localAdapter;
/*  62 */       this.entryPoint = arrayOfMethodHandle[0];
/*  63 */       return;
/*     */     }
/*  65 */     this.adapter = buildAdapterFromBytecodes(paramMethodType, paramInt, arrayOfMethodHandle);
/*  66 */     this.entryPoint = arrayOfMethodHandle[0];
/*     */   }
/*     */ 
/*     */   static MethodType preSpreadType(MethodType paramMethodType, int paramInt)
/*     */   {
/*  78 */     ArrayList localArrayList = new ArrayList(paramMethodType.parameterList());
/*  79 */     int i = localArrayList.size();
/*  80 */     localArrayList.subList(i - paramInt, i).clear();
/*  81 */     localArrayList.add(Object.class);
/*  82 */     return MethodType.methodType(paramMethodType.returnType(), localArrayList);
/*     */   }
/*     */ 
/*     */   MethodHandle makeInstance(MethodHandle paramMethodHandle) {
/*  86 */     MethodType localMethodType = paramMethodHandle.type();
/*  87 */     if (localMethodType != this.targetType) {
/*  88 */       throw new UnsupportedOperationException("NYI type=" + localMethodType);
/*     */     }
/*  90 */     return this.adapter.makeInstance(this, paramMethodHandle);
/*     */   }
/*     */ 
/*     */   public static MethodHandle make(MethodHandle paramMethodHandle, int paramInt)
/*     */   {
/* 101 */     MethodType localMethodType1 = paramMethodHandle.type();
/* 102 */     MethodType localMethodType2 = localMethodType1.generic();
/* 103 */     if (localMethodType1 == localMethodType2) {
/* 104 */       return of(localMethodType1, paramInt).makeInstance(paramMethodHandle);
/*     */     }
/* 106 */     MethodHandle localMethodHandle1 = FromGeneric.make(paramMethodHandle);
/* 107 */     assert (localMethodHandle1.type() == localMethodType2);
/* 108 */     MethodHandle localMethodHandle2 = of(localMethodType2, paramInt).makeInstance(localMethodHandle1);
/* 109 */     return ToGeneric.make(preSpreadType(localMethodType1, paramInt), localMethodHandle2);
/*     */   }
/*     */ 
/*     */   static SpreadGeneric of(MethodType paramMethodType, int paramInt)
/*     */   {
/* 115 */     if (paramMethodType != paramMethodType.generic())
/* 116 */       throw new UnsupportedOperationException("NYI type=" + paramMethodType);
/* 117 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/* 118 */     int i = localMethodTypeForm.parameterCount();
/* 119 */     assert (paramInt <= i);
/* 120 */     SpreadGeneric[] arrayOfSpreadGeneric = localMethodTypeForm.spreadGeneric;
/* 121 */     if (arrayOfSpreadGeneric == null)
/* 122 */       localMethodTypeForm.spreadGeneric = (arrayOfSpreadGeneric = new SpreadGeneric[i + 1]);
/* 123 */     SpreadGeneric localSpreadGeneric = arrayOfSpreadGeneric[paramInt];
/* 124 */     if (localSpreadGeneric == null)
/*     */     {
/*     */       void tmp114_111 = new SpreadGeneric(localMethodTypeForm.erasedType(), paramInt); localSpreadGeneric = tmp114_111; arrayOfSpreadGeneric[paramInt] = tmp114_111;
/* 126 */     }return localSpreadGeneric;
/*     */   }
/*     */ 
/*     */   String debugString() {
/* 130 */     return getClass().getSimpleName() + this.targetType + "[" + this.spreadCount + "]";
/*     */   }
/*     */ 
/*     */   protected Object check(Object paramObject, int paramInt)
/*     */   {
/* 136 */     MethodHandleStatics.checkSpreadArgument(paramObject, paramInt);
/* 137 */     return paramObject;
/*     */   }
/*     */ 
/*     */   protected Object select(Object paramObject, int paramInt)
/*     */   {
/* 142 */     return ((Object[])(Object[])paramObject)[paramInt];
/*     */   }
/*     */ 
/*     */   static Adapter findAdapter(SpreadGeneric paramSpreadGeneric, MethodHandle[] paramArrayOfMethodHandle)
/*     */   {
/* 157 */     MethodType localMethodType1 = paramSpreadGeneric.targetType;
/* 158 */     int i = paramSpreadGeneric.spreadCount;
/* 159 */     int j = localMethodType1.parameterCount();
/* 160 */     int k = j - i;
/* 161 */     if (k < 0) return null;
/* 162 */     MethodType localMethodType2 = MethodType.genericMethodType(k + 1);
/* 163 */     String str1 = "S" + j;
/* 164 */     String[] arrayOfString1 = { str1 };
/* 165 */     String str2 = "invoke_S" + i;
/*     */ 
/* 167 */     for (String str3 : arrayOfString1) {
/* 168 */       Class localClass = Adapter.findSubClass(str3);
/* 169 */       if (localClass != null)
/*     */       {
/* 171 */         MethodHandle localMethodHandle = null;
/*     */         try {
/* 173 */           localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findSpecial(localClass, str2, localMethodType2, localClass);
/*     */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*     */         }
/* 176 */         if (localMethodHandle != null) {
/* 177 */           Constructor localConstructor = null;
/*     */           try {
/* 179 */             localConstructor = localClass.getDeclaredConstructor(new Class[] { SpreadGeneric.class });
/*     */           } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */           } catch (SecurityException localSecurityException) {
/*     */           }
/* 183 */           if (localConstructor != null)
/*     */             try
/*     */             {
/* 186 */               Adapter localAdapter = (Adapter)localConstructor.newInstance(new Object[] { paramSpreadGeneric });
/* 187 */               paramArrayOfMethodHandle[0] = localMethodHandle;
/* 188 */               return localAdapter;
/*     */             } catch (IllegalArgumentException localIllegalArgumentException) {
/*     */             } catch (InvocationTargetException localInvocationTargetException) {
/* 191 */               Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 192 */               if ((localThrowable instanceof Error)) throw ((Error)localThrowable);
/* 193 */               if ((localThrowable instanceof RuntimeException)) throw ((RuntimeException)localThrowable);  } catch (InstantiationException localInstantiationException) {
/*     */             } catch (IllegalAccessException localIllegalAccessException) {  }
/*     */ 
/*     */         }
/*     */       }
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   static Adapter buildAdapterFromBytecodes(MethodType paramMethodType, int paramInt, MethodHandle[] paramArrayOfMethodHandle)
/*     */   {
/* 203 */     throw new UnsupportedOperationException("NYI");
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  70 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*     */   }
/*     */ 
/*     */   static abstract class Adapter extends BoundMethodHandle
/*     */   {
/*     */     protected final SpreadGeneric outer;
/*     */     protected final MethodHandle target;
/*     */     static final MethodHandle NO_ENTRY;
/* 263 */     private static final String CLASS_PREFIX = str1.substring(0, str1.length() - str2.length());
/*     */ 
/*     */     String debugString()
/*     */     {
/* 228 */       return MethodHandleStatics.addTypeString(this.target, this);
/*     */     }
/*     */ 
/*     */     protected boolean isPrototype()
/*     */     {
/* 233 */       return this.target == null;
/*     */     }
/* 235 */     protected Adapter(SpreadGeneric paramSpreadGeneric) { super();
/* 236 */       this.outer = paramSpreadGeneric;
/* 237 */       this.target = null;
/* 238 */       assert (isPrototype()); }
/*     */ 
/*     */     protected Adapter(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle)
/*     */     {
/* 242 */       super();
/* 243 */       this.outer = paramSpreadGeneric;
/* 244 */       this.target = paramMethodHandle;
/*     */     }
/*     */ 
/*     */     protected abstract Adapter makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle);
/*     */ 
/*     */     protected Object check(Object paramObject, int paramInt)
/*     */     {
/* 252 */       return this.outer.check(paramObject, paramInt);
/*     */     }
/*     */     protected Object select(Object paramObject, int paramInt) {
/* 255 */       return this.outer.select(paramObject, paramInt);
/*     */     }
/*     */ 
/*     */     static Class<? extends Adapter> findSubClass(String paramString)
/*     */     {
/* 267 */       String str = CLASS_PREFIX + paramString;
/*     */       try {
/* 269 */         return Class.forName(str).asSubclass(Adapter.class);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 271 */         return null; } catch (ClassCastException localClassCastException) {
/*     */       }
/* 273 */       return null;
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/* 231 */       NO_ENTRY = ValueConversions.identity();
/*     */ 
/* 260 */       String str1 = Adapter.class.getName();
/* 261 */       String str2 = Adapter.class.getSimpleName();
/* 262 */       if (!str1.endsWith(str2)) throw new InternalError();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S0 extends SpreadGeneric.Adapter
/*     */   {
/*     */     protected S0(SpreadGeneric paramSpreadGeneric)
/*     */     {
/* 417 */       super(); } 
/* 418 */     protected S0(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 419 */     protected S0 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S0(paramSpreadGeneric, paramMethodHandle); } 
/* 420 */     protected Object invoke_S0(Object paramObject) throws Throwable { paramObject = super.check(paramObject, 0);
/* 421 */       return this.target.invokeExact(); } 
/*     */   }
/*     */   static class S1 extends SpreadGeneric.Adapter {
/* 424 */     protected S1(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 425 */     protected S1(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 426 */     protected S1 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S1(paramSpreadGeneric, paramMethodHandle); } 
/* 427 */     protected Object invoke_S0(Object paramObject1, Object paramObject2) throws Throwable { paramObject2 = super.check(paramObject2, 0);
/* 428 */       return this.target.invokeExact(paramObject1); } 
/* 429 */     protected Object invoke_S1(Object paramObject) throws Throwable { paramObject = super.check(paramObject, 1);
/* 430 */       return this.target.invokeExact(super.select(paramObject, 0));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S10 extends SpreadGeneric.Adapter
/*     */   {
/*     */     protected S10(SpreadGeneric paramSpreadGeneric)
/*     */     {
/* 638 */       super(); } 
/* 639 */     protected S10(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 640 */     protected S10 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S10(paramSpreadGeneric, paramMethodHandle); } 
/* 641 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable { paramObject11 = super.check(paramObject11, 0);
/* 642 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10); } 
/* 643 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { paramObject10 = super.check(paramObject10, 1);
/* 644 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, super.select(paramObject10, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 646 */       paramObject9 = super.check(paramObject9, 2);
/* 647 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, super.select(paramObject9, 0), super.select(paramObject9, 1));
/*     */     }
/* 649 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { paramObject8 = super.check(paramObject8, 3);
/* 650 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, super.select(paramObject8, 0), super.select(paramObject8, 1), super.select(paramObject8, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/* 652 */       paramObject7 = super.check(paramObject7, 4);
/* 653 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, super.select(paramObject7, 0), super.select(paramObject7, 1), super.select(paramObject7, 2), super.select(paramObject7, 3));
/*     */     }
/* 655 */     protected Object invoke_S5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { paramObject6 = super.check(paramObject6, 5);
/* 656 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, super.select(paramObject6, 0), super.select(paramObject6, 1), super.select(paramObject6, 2), super.select(paramObject6, 3), super.select(paramObject6, 4)); }
/*     */ 
/*     */     protected Object invoke_S6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/* 659 */       paramObject5 = super.check(paramObject5, 6);
/* 660 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0), super.select(paramObject5, 1), super.select(paramObject5, 2), super.select(paramObject5, 3), super.select(paramObject5, 4), super.select(paramObject5, 5));
/*     */     }
/*     */     protected Object invoke_S7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/* 663 */       paramObject4 = super.check(paramObject4, 7);
/* 664 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1), super.select(paramObject4, 2), super.select(paramObject4, 3), super.select(paramObject4, 4), super.select(paramObject4, 5), super.select(paramObject4, 6));
/*     */     }
/*     */     protected Object invoke_S8(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/* 667 */       paramObject3 = super.check(paramObject3, 8);
/* 668 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2), super.select(paramObject3, 3), super.select(paramObject3, 4), super.select(paramObject3, 5), super.select(paramObject3, 6), super.select(paramObject3, 7));
/*     */     }
/*     */     protected Object invoke_S9(Object paramObject1, Object paramObject2) throws Throwable {
/* 671 */       paramObject2 = super.check(paramObject2, 9);
/* 672 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3), super.select(paramObject2, 4), super.select(paramObject2, 5), super.select(paramObject2, 6), super.select(paramObject2, 7), super.select(paramObject2, 8));
/*     */     }
/*     */ 
/*     */     protected Object invoke_S10(Object paramObject) throws Throwable {
/* 676 */       paramObject = super.check(paramObject, 10);
/* 677 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4), super.select(paramObject, 5), super.select(paramObject, 6), super.select(paramObject, 7), super.select(paramObject, 8), super.select(paramObject, 9));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S2 extends SpreadGeneric.Adapter
/*     */   {
/*     */     protected S2(SpreadGeneric paramSpreadGeneric)
/*     */     {
/* 434 */       super(); } 
/* 435 */     protected S2(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 436 */     protected S2 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S2(paramSpreadGeneric, paramMethodHandle); } 
/* 437 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { paramObject3 = super.check(paramObject3, 0);
/* 438 */       return this.target.invokeExact(paramObject1, paramObject2); } 
/* 439 */     protected Object invoke_S1(Object paramObject1, Object paramObject2) throws Throwable { paramObject2 = super.check(paramObject2, 1);
/* 440 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject) throws Throwable {
/* 442 */       paramObject = super.check(paramObject, 2);
/* 443 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1));
/*     */     }
/*     */   }
/*     */   static class S3 extends SpreadGeneric.Adapter {
/* 447 */     protected S3(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 448 */     protected S3(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 449 */     protected S3 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S3(paramSpreadGeneric, paramMethodHandle); } 
/* 450 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { paramObject4 = super.check(paramObject4, 0);
/* 451 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3); } 
/* 452 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { paramObject3 = super.check(paramObject3, 1);
/* 453 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2) throws Throwable {
/* 455 */       paramObject2 = super.check(paramObject2, 2);
/* 456 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1));
/*     */     }
/* 458 */     protected Object invoke_S3(Object paramObject) throws Throwable { paramObject = super.check(paramObject, 3);
/* 459 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2)); } 
/*     */   }
/*     */ 
/*     */   static class S4 extends SpreadGeneric.Adapter {
/* 463 */     protected S4(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 464 */     protected S4(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 465 */     protected S4 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S4(paramSpreadGeneric, paramMethodHandle); } 
/* 466 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { paramObject5 = super.check(paramObject5, 0);
/* 467 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4); } 
/* 468 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { paramObject4 = super.check(paramObject4, 1);
/* 469 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/* 471 */       paramObject3 = super.check(paramObject3, 2);
/* 472 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1));
/*     */     }
/* 474 */     protected Object invoke_S3(Object paramObject1, Object paramObject2) throws Throwable { paramObject2 = super.check(paramObject2, 3);
/* 475 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject) throws Throwable {
/* 477 */       paramObject = super.check(paramObject, 4);
/* 478 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3));
/*     */     }
/*     */   }
/*     */   static class S5 extends SpreadGeneric.Adapter {
/* 482 */     protected S5(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 483 */     protected S5(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 484 */     protected S5 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S5(paramSpreadGeneric, paramMethodHandle); } 
/* 485 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { paramObject6 = super.check(paramObject6, 0);
/* 486 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); } 
/* 487 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { paramObject5 = super.check(paramObject5, 1);
/* 488 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/* 490 */       paramObject4 = super.check(paramObject4, 2);
/* 491 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1));
/*     */     }
/* 493 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { paramObject3 = super.check(paramObject3, 3);
/* 494 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2) throws Throwable {
/* 496 */       paramObject2 = super.check(paramObject2, 4);
/* 497 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3));
/*     */     }
/* 499 */     protected Object invoke_S5(Object paramObject) throws Throwable { paramObject = super.check(paramObject, 5);
/* 500 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4)); }
/*     */   }
/*     */ 
/*     */   static class S6 extends SpreadGeneric.Adapter {
/*     */     protected S6(SpreadGeneric paramSpreadGeneric) {
/* 505 */       super(); } 
/* 506 */     protected S6(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 507 */     protected S6 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S6(paramSpreadGeneric, paramMethodHandle); } 
/* 508 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { paramObject7 = super.check(paramObject7, 0);
/* 509 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6); } 
/* 510 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { paramObject6 = super.check(paramObject6, 1);
/* 511 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, super.select(paramObject6, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/* 513 */       paramObject5 = super.check(paramObject5, 2);
/* 514 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0), super.select(paramObject5, 1));
/*     */     }
/* 516 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { paramObject4 = super.check(paramObject4, 3);
/* 517 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1), super.select(paramObject4, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/* 519 */       paramObject3 = super.check(paramObject3, 4);
/* 520 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2), super.select(paramObject3, 3));
/*     */     }
/* 522 */     protected Object invoke_S5(Object paramObject1, Object paramObject2) throws Throwable { paramObject2 = super.check(paramObject2, 5);
/* 523 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3), super.select(paramObject2, 4)); }
/*     */ 
/*     */     protected Object invoke_S6(Object paramObject) throws Throwable {
/* 526 */       paramObject = super.check(paramObject, 6);
/* 527 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4), super.select(paramObject, 5));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S7 extends SpreadGeneric.Adapter {
/* 532 */     protected S7(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 533 */     protected S7(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 534 */     protected S7 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S7(paramSpreadGeneric, paramMethodHandle); } 
/* 535 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { paramObject8 = super.check(paramObject8, 0);
/* 536 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7); } 
/* 537 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { paramObject7 = super.check(paramObject7, 1);
/* 538 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, super.select(paramObject7, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/* 540 */       paramObject6 = super.check(paramObject6, 2);
/* 541 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, super.select(paramObject6, 0), super.select(paramObject6, 1));
/*     */     }
/* 543 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { paramObject5 = super.check(paramObject5, 3);
/* 544 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0), super.select(paramObject5, 1), super.select(paramObject5, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/* 546 */       paramObject4 = super.check(paramObject4, 4);
/* 547 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1), super.select(paramObject4, 2), super.select(paramObject4, 3));
/*     */     }
/* 549 */     protected Object invoke_S5(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { paramObject3 = super.check(paramObject3, 5);
/* 550 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2), super.select(paramObject3, 3), super.select(paramObject3, 4)); }
/*     */ 
/*     */     protected Object invoke_S6(Object paramObject1, Object paramObject2) throws Throwable {
/* 553 */       paramObject2 = super.check(paramObject2, 6);
/* 554 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3), super.select(paramObject2, 4), super.select(paramObject2, 5));
/*     */     }
/*     */     protected Object invoke_S7(Object paramObject) throws Throwable {
/* 557 */       paramObject = super.check(paramObject, 7);
/* 558 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4), super.select(paramObject, 5), super.select(paramObject, 6));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S8 extends SpreadGeneric.Adapter {
/* 563 */     protected S8(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 564 */     protected S8(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 565 */     protected S8 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S8(paramSpreadGeneric, paramMethodHandle); } 
/* 566 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { paramObject9 = super.check(paramObject9, 0);
/* 567 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8); } 
/* 568 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { paramObject8 = super.check(paramObject8, 1);
/* 569 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, super.select(paramObject8, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/* 571 */       paramObject7 = super.check(paramObject7, 2);
/* 572 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, super.select(paramObject7, 0), super.select(paramObject7, 1));
/*     */     }
/* 574 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { paramObject6 = super.check(paramObject6, 3);
/* 575 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, super.select(paramObject6, 0), super.select(paramObject6, 1), super.select(paramObject6, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/* 577 */       paramObject5 = super.check(paramObject5, 4);
/* 578 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0), super.select(paramObject5, 1), super.select(paramObject5, 2), super.select(paramObject5, 3));
/*     */     }
/* 580 */     protected Object invoke_S5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { paramObject4 = super.check(paramObject4, 5);
/* 581 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1), super.select(paramObject4, 2), super.select(paramObject4, 3), super.select(paramObject4, 4)); }
/*     */ 
/*     */     protected Object invoke_S6(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/* 584 */       paramObject3 = super.check(paramObject3, 6);
/* 585 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2), super.select(paramObject3, 3), super.select(paramObject3, 4), super.select(paramObject3, 5));
/*     */     }
/*     */     protected Object invoke_S7(Object paramObject1, Object paramObject2) throws Throwable {
/* 588 */       paramObject2 = super.check(paramObject2, 7);
/* 589 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3), super.select(paramObject2, 4), super.select(paramObject2, 5), super.select(paramObject2, 6));
/*     */     }
/*     */     protected Object invoke_S8(Object paramObject) throws Throwable {
/* 592 */       paramObject = super.check(paramObject, 8);
/* 593 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4), super.select(paramObject, 5), super.select(paramObject, 6), super.select(paramObject, 7));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class S9 extends SpreadGeneric.Adapter {
/* 598 */     protected S9(SpreadGeneric paramSpreadGeneric) { super(); } 
/* 599 */     protected S9(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { super(paramMethodHandle); } 
/* 600 */     protected S9 makeInstance(SpreadGeneric paramSpreadGeneric, MethodHandle paramMethodHandle) { return new S9(paramSpreadGeneric, paramMethodHandle); } 
/* 601 */     protected Object invoke_S0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { paramObject10 = super.check(paramObject10, 0);
/* 602 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9); } 
/* 603 */     protected Object invoke_S1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { paramObject9 = super.check(paramObject9, 1);
/* 604 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, super.select(paramObject9, 0)); } 
/*     */     protected Object invoke_S2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/* 606 */       paramObject8 = super.check(paramObject8, 2);
/* 607 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, super.select(paramObject8, 0), super.select(paramObject8, 1));
/*     */     }
/* 609 */     protected Object invoke_S3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { paramObject7 = super.check(paramObject7, 3);
/* 610 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, super.select(paramObject7, 0), super.select(paramObject7, 1), super.select(paramObject7, 2)); } 
/*     */     protected Object invoke_S4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/* 612 */       paramObject6 = super.check(paramObject6, 4);
/* 613 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, super.select(paramObject6, 0), super.select(paramObject6, 1), super.select(paramObject6, 2), super.select(paramObject6, 3));
/*     */     }
/* 615 */     protected Object invoke_S5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { paramObject5 = super.check(paramObject5, 5);
/* 616 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, super.select(paramObject5, 0), super.select(paramObject5, 1), super.select(paramObject5, 2), super.select(paramObject5, 3), super.select(paramObject5, 4)); }
/*     */ 
/*     */     protected Object invoke_S6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/* 619 */       paramObject4 = super.check(paramObject4, 6);
/* 620 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, super.select(paramObject4, 0), super.select(paramObject4, 1), super.select(paramObject4, 2), super.select(paramObject4, 3), super.select(paramObject4, 4), super.select(paramObject4, 5));
/*     */     }
/*     */     protected Object invoke_S7(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/* 623 */       paramObject3 = super.check(paramObject3, 7);
/* 624 */       return this.target.invokeExact(paramObject1, paramObject2, super.select(paramObject3, 0), super.select(paramObject3, 1), super.select(paramObject3, 2), super.select(paramObject3, 3), super.select(paramObject3, 4), super.select(paramObject3, 5), super.select(paramObject3, 6));
/*     */     }
/*     */     protected Object invoke_S8(Object paramObject1, Object paramObject2) throws Throwable {
/* 627 */       paramObject2 = super.check(paramObject2, 8);
/* 628 */       return this.target.invokeExact(paramObject1, super.select(paramObject2, 0), super.select(paramObject2, 1), super.select(paramObject2, 2), super.select(paramObject2, 3), super.select(paramObject2, 4), super.select(paramObject2, 5), super.select(paramObject2, 6), super.select(paramObject2, 7));
/*     */     }
/*     */     protected Object invoke_S9(Object paramObject) throws Throwable {
/* 631 */       paramObject = super.check(paramObject, 9);
/* 632 */       return this.target.invokeExact(super.select(paramObject, 0), super.select(paramObject, 1), super.select(paramObject, 2), super.select(paramObject, 3), super.select(paramObject, 4), super.select(paramObject, 5), super.select(paramObject, 6), super.select(paramObject, 7), super.select(paramObject, 8));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.SpreadGeneric
 * JD-Core Version:    0.6.2
 */