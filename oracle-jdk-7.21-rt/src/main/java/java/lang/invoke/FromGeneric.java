/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import sun.invoke.util.ValueConversions;
/*     */ import sun.invoke.util.Wrapper;
/*     */ 
/*     */ class FromGeneric
/*     */ {
/*     */   private final MethodType targetType;
/*     */   private final MethodType internalType;
/*     */   private final Adapter adapter;
/*     */   private final MethodHandle entryPoint;
/*     */   private final MethodHandle unboxingInvoker;
/*     */   private final MethodHandle returnConversion;
/*     */ 
/*     */   private FromGeneric(MethodType paramMethodType)
/*     */   {
/*  70 */     this.targetType = paramMethodType;
/*     */     Object localObject;
/*  73 */     Adapter localAdapter = findAdapter(localObject = paramMethodType.erase());
/*  74 */     if (localAdapter != null)
/*     */     {
/*  77 */       this.internalType = ((MethodType)localObject);
/*  78 */       this.adapter = localAdapter;
/*  79 */       this.entryPoint = localAdapter.prototypeEntryPoint();
/*  80 */       this.returnConversion = computeReturnConversion(paramMethodType, (MethodType)localObject);
/*  81 */       this.unboxingInvoker = computeUnboxingInvoker(paramMethodType, (MethodType)localObject);
/*  82 */       return;
/*     */     }
/*     */ 
/*  86 */     MethodType localMethodType1 = paramMethodType.form().primArgsAsBoxes();
/*  87 */     MethodType localMethodType2 = localMethodType1.form().primsAsInts();
/*  88 */     if (localMethodType2 != paramMethodType)
/*  89 */       localAdapter = findAdapter(localObject = localMethodType2);
/*  90 */     if (localAdapter == null) {
/*  91 */       localAdapter = buildAdapterFromBytecodes(localObject = paramMethodType);
/*     */     }
/*  93 */     this.internalType = ((MethodType)localObject);
/*  94 */     this.adapter = localAdapter;
/*  95 */     MethodType localMethodType3 = paramMethodType.insertParameterTypes(0, new Class[] { this.adapter.getClass() });
/*  96 */     this.entryPoint = localAdapter.prototypeEntryPoint();
/*  97 */     this.returnConversion = computeReturnConversion(paramMethodType, (MethodType)localObject);
/*  98 */     this.unboxingInvoker = computeUnboxingInvoker(paramMethodType, (MethodType)localObject);
/*     */   }
/*     */ 
/*     */   private static MethodHandle computeReturnConversion(MethodType paramMethodType1, MethodType paramMethodType2)
/*     */   {
/* 112 */     Class localClass1 = paramMethodType1.returnType();
/* 113 */     Class localClass2 = paramMethodType2.returnType();
/* 114 */     Wrapper localWrapper = Wrapper.forBasicType(localClass1);
/* 115 */     if (!localClass2.isPrimitive()) {
/* 116 */       assert (localClass2 == Object.class);
/* 117 */       return ValueConversions.identity();
/* 118 */     }if (localWrapper.primitiveType() == localClass2) {
/* 119 */       return ValueConversions.box(localWrapper);
/*     */     }
/* 121 */     assert (localClass1 == Double.TYPE ? localClass2 == Long.TYPE : localClass2 == Integer.TYPE);
/* 122 */     return ValueConversions.boxRaw(localWrapper);
/*     */   }
/*     */ 
/*     */   private static MethodHandle computeUnboxingInvoker(MethodType paramMethodType1, MethodType paramMethodType2)
/*     */   {
/* 136 */     assert (paramMethodType2 == paramMethodType2.erase());
/* 137 */     MethodHandle localMethodHandle1 = paramMethodType1.invokers().exactInvoker();
/*     */ 
/* 139 */     MethodType localMethodType = paramMethodType2.changeReturnType(paramMethodType1.returnType());
/* 140 */     MethodHandle localMethodHandle2 = MethodHandleImpl.convertArguments(localMethodHandle1, Invokers.invokerType(localMethodType), localMethodHandle1.type(), 0);
/*     */ 
/* 143 */     if (localMethodHandle2 == null) {
/* 144 */       throw new InternalError("bad fixArgs");
/*     */     }
/* 146 */     MethodHandle localMethodHandle3 = AdapterMethodHandle.makeRetypeRaw(Invokers.invokerType(paramMethodType2), localMethodHandle2);
/*     */ 
/* 148 */     if (localMethodHandle3 == null)
/* 149 */       throw new InternalError("bad retyper");
/* 150 */     return localMethodHandle3;
/*     */   }
/*     */ 
/*     */   Adapter makeInstance(MethodHandle paramMethodHandle) {
/* 154 */     MethodType localMethodType = paramMethodHandle.type();
/* 155 */     if (localMethodType == this.targetType) {
/* 156 */       return this.adapter.makeInstance(this.entryPoint, this.unboxingInvoker, this.returnConversion, paramMethodHandle);
/*     */     }
/*     */ 
/* 159 */     assert (localMethodType.erase() == this.targetType);
/* 160 */     MethodHandle localMethodHandle = computeUnboxingInvoker(localMethodType, this.internalType);
/* 161 */     return this.adapter.makeInstance(this.entryPoint, localMethodHandle, this.returnConversion, paramMethodHandle);
/*     */   }
/*     */ 
/*     */   public static MethodHandle make(MethodHandle paramMethodHandle)
/*     */   {
/* 171 */     MethodType localMethodType = paramMethodHandle.type();
/* 172 */     if (localMethodType == localMethodType.generic()) return paramMethodHandle;
/* 173 */     return of(localMethodType).makeInstance(paramMethodHandle);
/*     */   }
/*     */ 
/*     */   static FromGeneric of(MethodType paramMethodType)
/*     */   {
/* 178 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/* 179 */     FromGeneric localFromGeneric = localMethodTypeForm.fromGeneric;
/* 180 */     if (localFromGeneric == null)
/* 181 */       localMethodTypeForm.fromGeneric = (localFromGeneric = new FromGeneric(localMethodTypeForm.erasedType()));
/* 182 */     return localFromGeneric;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 186 */     return "FromGeneric" + this.targetType;
/*     */   }
/*     */ 
/*     */   static Adapter findAdapter(MethodType paramMethodType)
/*     */   {
/* 191 */     MethodType localMethodType = paramMethodType.generic();
/* 192 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/* 193 */     Class localClass1 = paramMethodType.returnType();
/* 194 */     int i = localMethodTypeForm.parameterCount();
/* 195 */     int j = localMethodTypeForm.longPrimitiveParameterCount();
/* 196 */     int k = localMethodTypeForm.primitiveParameterCount() - j;
/* 197 */     String str1 = (k > 0 ? "I" + k : "") + (j > 0 ? "J" + j : "");
/* 198 */     String str2 = String.valueOf(Wrapper.forPrimitiveType(localClass1).basicTypeChar());
/* 199 */     String str3 = str2 + i;
/* 200 */     String str4 = "A" + i;
/* 201 */     String[] arrayOfString1 = { str3 + str1, str3, str4 + str1, str4 };
/* 202 */     String str5 = "invoke_" + str3 + str1;
/*     */ 
/* 204 */     for (String str6 : arrayOfString1) {
/* 205 */       Class localClass2 = Adapter.findSubClass(str6);
/* 206 */       if (localClass2 != null)
/*     */       {
/* 208 */         MethodHandle localMethodHandle = null;
/*     */         try {
/* 210 */           localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findSpecial(localClass2, str5, localMethodType, localClass2);
/*     */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*     */         }
/* 213 */         if (localMethodHandle != null) {
/* 214 */           Constructor localConstructor = null;
/*     */           try {
/* 216 */             localConstructor = localClass2.getDeclaredConstructor(new Class[] { MethodHandle.class });
/*     */           } catch (NoSuchMethodException localNoSuchMethodException) {
/*     */           } catch (SecurityException localSecurityException) {
/*     */           }
/* 220 */           if (localConstructor != null)
/*     */             try
/*     */             {
/* 223 */               return (Adapter)localConstructor.newInstance(new Object[] { localMethodHandle });
/*     */             } catch (IllegalArgumentException localIllegalArgumentException) {
/*     */             } catch (InvocationTargetException localInvocationTargetException) {
/* 226 */               Throwable localThrowable = localInvocationTargetException.getTargetException();
/* 227 */               if ((localThrowable instanceof Error)) throw ((Error)localThrowable);
/* 228 */               if ((localThrowable instanceof RuntimeException)) throw ((RuntimeException)localThrowable);  } catch (InstantiationException localInstantiationException) {
/*     */             } catch (IllegalAccessException localIllegalAccessException) {  }
/*     */ 
/*     */         }
/*     */       }
/*     */     }
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   static Adapter buildAdapterFromBytecodes(MethodType paramMethodType) {
/* 237 */     throw new UnsupportedOperationException("NYI " + paramMethodType);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 102 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*     */   }
/*     */ 
/*     */   static class A0 extends FromGeneric.Adapter
/*     */   {
/*     */     protected A0(MethodHandle paramMethodHandle)
/*     */     {
/* 502 */       super();
/*     */     }
/* 504 */     protected A0(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*     */     protected A0 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 506 */       return new A0(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 507 */     protected Object invoke_L0() throws Throwable { return convert_L(this.invoker.invokeExact(this.target)); } 
/* 508 */     protected Object invoke_I0() throws Throwable { return convert_I(this.invoker.invokeExact(this.target)); } 
/* 509 */     protected Object invoke_J0() throws Throwable { return convert_J(this.invoker.invokeExact(this.target)); } 
/* 510 */     protected Object invoke_F0() throws Throwable { return convert_F(this.invoker.invokeExact(this.target)); } 
/* 511 */     protected Object invoke_D0() throws Throwable { return convert_D(this.invoker.invokeExact(this.target)); } 
/*     */   }
/*     */   static class A1 extends FromGeneric.Adapter {
/* 514 */     protected A1(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A1(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 516 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 518 */     protected A1 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A1(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 519 */     protected Object invoke_L1(Object paramObject) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject)); } 
/* 520 */     protected Object invoke_I1(Object paramObject) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject)); } 
/* 521 */     protected Object invoke_J1(Object paramObject) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject)); } 
/* 522 */     protected Object invoke_F1(Object paramObject) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject)); } 
/* 523 */     protected Object invoke_D1(Object paramObject) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject)); }
/*     */ 
/*     */   }
/*     */ 
/*     */   static class A10 extends FromGeneric.Adapter
/*     */   {
/*     */     protected A10(MethodHandle paramMethodHandle)
/*     */     {
/* 622 */       super();
/*     */     }
/* 624 */     protected A10(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*     */     protected A10 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 626 */       return new A10(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 627 */     protected Object invoke_L10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 628 */     protected Object invoke_I10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 629 */     protected Object invoke_J10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 630 */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 631 */     protected Object invoke_D10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); }
/*     */ 
/*     */   }
/*     */ 
/*     */   static class A2 extends FromGeneric.Adapter
/*     */   {
/*     */     protected A2(MethodHandle paramMethodHandle)
/*     */     {
/* 526 */       super();
/*     */     }
/* 528 */     protected A2(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*     */     protected A2 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 530 */       return new A2(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 531 */     protected Object invoke_L2(Object paramObject1, Object paramObject2) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2)); } 
/* 532 */     protected Object invoke_I2(Object paramObject1, Object paramObject2) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2)); } 
/* 533 */     protected Object invoke_J2(Object paramObject1, Object paramObject2) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2)); } 
/* 534 */     protected Object invoke_F2(Object paramObject1, Object paramObject2) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2)); } 
/* 535 */     protected Object invoke_D2(Object paramObject1, Object paramObject2) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2)); } 
/*     */   }
/*     */   static class A3 extends FromGeneric.Adapter {
/* 538 */     protected A3(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A3(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 540 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 542 */     protected A3 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A3(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 543 */     protected Object invoke_L3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3)); } 
/* 544 */     protected Object invoke_I3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3)); } 
/* 545 */     protected Object invoke_J3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3)); } 
/* 546 */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3)); } 
/* 547 */     protected Object invoke_D3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3)); } 
/*     */   }
/*     */   static class A4 extends FromGeneric.Adapter {
/* 550 */     protected A4(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A4(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 552 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 554 */     protected A4 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A4(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 555 */     protected Object invoke_L4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4)); } 
/* 556 */     protected Object invoke_I4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4)); } 
/* 557 */     protected Object invoke_J4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4)); } 
/* 558 */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4)); } 
/* 559 */     protected Object invoke_D4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*     */   }
/*     */   static class A5 extends FromGeneric.Adapter {
/* 562 */     protected A5(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A5(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 564 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 566 */     protected A5 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A5(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 567 */     protected Object invoke_L5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/* 568 */     protected Object invoke_I5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/* 569 */     protected Object invoke_J5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/* 570 */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/* 571 */     protected Object invoke_D5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/*     */   }
/*     */   static class A6 extends FromGeneric.Adapter {
/* 574 */     protected A6(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A6(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 576 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 578 */     protected A6 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A6(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 579 */     protected Object invoke_L6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/* 580 */     protected Object invoke_I6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/* 581 */     protected Object invoke_J6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/* 582 */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/* 583 */     protected Object invoke_D6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/*     */   }
/*     */   static class A7 extends FromGeneric.Adapter {
/* 586 */     protected A7(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A7(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 588 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 590 */     protected A7 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A7(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 591 */     protected Object invoke_L7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/* 592 */     protected Object invoke_I7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/* 593 */     protected Object invoke_J7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/* 594 */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/* 595 */     protected Object invoke_D7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/*     */   }
/*     */   static class A8 extends FromGeneric.Adapter {
/* 598 */     protected A8(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A8(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 600 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 602 */     protected A8 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A8(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 603 */     protected Object invoke_L8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/* 604 */     protected Object invoke_I8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/* 605 */     protected Object invoke_J8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/* 606 */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/* 607 */     protected Object invoke_D8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/*     */   }
/*     */   static class A9 extends FromGeneric.Adapter {
/* 610 */     protected A9(MethodHandle paramMethodHandle) { super(); } 
/*     */     protected A9(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/* 612 */       super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4);
/*     */     }
/* 614 */     protected A9 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A9(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 615 */     protected Object invoke_L9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return convert_L(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/* 616 */     protected Object invoke_I9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return convert_I(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/* 617 */     protected Object invoke_J9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return convert_J(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/* 618 */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return convert_F(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/* 619 */     protected Object invoke_D9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return convert_D(this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); }
/*     */ 
/*     */   }
/*     */ 
/*     */   static abstract class Adapter extends BoundMethodHandle
/*     */   {
/*     */     protected final MethodHandle invoker;
/*     */     protected final MethodHandle convert;
/*     */     protected final MethodHandle target;
/* 302 */     private static final String CLASS_PREFIX = str1.substring(0, str1.length() - str2.length());
/*     */ 
/*     */     String debugString()
/*     */     {
/* 264 */       return MethodHandleStatics.addTypeString(this.target, this);
/*     */     }
/*     */     protected boolean isPrototype() {
/* 267 */       return this.target == null;
/*     */     }
/* 269 */     protected Adapter(MethodHandle paramMethodHandle) { this(paramMethodHandle, null, paramMethodHandle, null);
/* 270 */       assert (isPrototype()); }
/*     */ 
/*     */     protected MethodHandle prototypeEntryPoint() {
/* 273 */       if (!isPrototype()) throw new InternalError();
/* 274 */       return this.convert;
/*     */     }
/*     */ 
/*     */     protected Adapter(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4)
/*     */     {
/* 279 */       super();
/* 280 */       this.invoker = paramMethodHandle2;
/* 281 */       this.convert = paramMethodHandle3;
/* 282 */       this.target = paramMethodHandle4;
/*     */     }
/*     */ 
/*     */     protected abstract Adapter makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4);
/*     */ 
/*     */     protected Object convert_L(Object paramObject)
/*     */       throws Throwable
/*     */     {
/* 291 */       return this.convert.invokeExact(paramObject); } 
/* 292 */     protected Object convert_I(int paramInt) throws Throwable { return this.convert.invokeExact(paramInt); } 
/* 293 */     protected Object convert_J(long paramLong) throws Throwable { return this.convert.invokeExact(paramLong); } 
/* 294 */     protected Object convert_F(float paramFloat) throws Throwable { return this.convert.invokeExact(paramFloat); } 
/* 295 */     protected Object convert_D(double paramDouble) throws Throwable { return this.convert.invokeExact(paramDouble); }
/*     */ 
/*     */ 
/*     */     static Class<? extends Adapter> findSubClass(String paramString)
/*     */     {
/* 306 */       String str = CLASS_PREFIX + paramString;
/*     */       try {
/* 308 */         return Class.forName(str).asSubclass(Adapter.class);
/*     */       } catch (ClassNotFoundException localClassNotFoundException) {
/* 310 */         return null; } catch (ClassCastException localClassCastException) {
/*     */       }
/* 312 */       return null;
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/* 299 */       String str1 = Adapter.class.getName();
/* 300 */       String str2 = Adapter.class.getSimpleName();
/* 301 */       if (!str1.endsWith(str2)) throw new InternalError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.FromGeneric
 * JD-Core Version:    0.6.2
 */