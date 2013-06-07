/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import sun.invoke.util.ValueConversions;
/*      */ import sun.invoke.util.Wrapper;
/*      */ 
/*      */ class ToGeneric
/*      */ {
/*      */   private final MethodType entryType;
/*      */   private final MethodType rawEntryType;
/*      */   private final Adapter adapter;
/*      */   private final MethodHandle entryPoint;
/*      */   private final int[] primsAtEndOrder;
/*      */   private final MethodHandle invoker;
/*      */   private final MethodHandle returnConversion;
/*      */ 
/*      */   private ToGeneric(MethodType paramMethodType)
/*      */   {
/*   72 */     assert (paramMethodType.erase() == paramMethodType);
/*      */ 
/*   74 */     this.entryType = paramMethodType;
/*   75 */     MethodHandle localMethodHandle1 = paramMethodType.generic().invokers().exactInvoker();
/*      */     Object localObject1;
/*   77 */     Adapter localAdapter = findAdapter(localObject1 = paramMethodType);
/*   78 */     if (localAdapter != null)
/*      */     {
/*   81 */       this.returnConversion = computeReturnConversion(paramMethodType, (MethodType)localObject1, false);
/*   82 */       this.rawEntryType = ((MethodType)localObject1);
/*   83 */       this.adapter = localAdapter;
/*   84 */       this.entryPoint = localAdapter.prototypeEntryPoint();
/*   85 */       this.primsAtEndOrder = null;
/*   86 */       this.invoker = localMethodHandle1;
/*   87 */       return;
/*      */     }
/*      */ 
/*   91 */     MethodType localMethodType1 = paramMethodType.form().primsAtEnd();
/*      */ 
/*   93 */     this.primsAtEndOrder = MethodTypeForm.primsAtEndOrder(paramMethodType);
/*   94 */     if (this.primsAtEndOrder != null)
/*      */     {
/*   96 */       localObject2 = of(localMethodType1);
/*   97 */       this.adapter = ((ToGeneric)localObject2).adapter;
/*   98 */       throw new UnsupportedOperationException("NYI: primitive parameters must follow references; entryType = " + paramMethodType);
/*      */     }
/*      */ 
/*  109 */     Object localObject2 = localMethodType1.form().primsAsInts();
/*  110 */     localAdapter = findAdapter(localObject1 = localObject2);
/*      */     MethodHandle localMethodHandle2;
/*  112 */     if (localAdapter != null) {
/*  113 */       localMethodHandle2 = localAdapter.prototypeEntryPoint();
/*      */     }
/*      */     else
/*      */     {
/*  118 */       localMethodType2 = localMethodType1.form().primsAsLongs();
/*  119 */       localAdapter = findAdapter(localObject1 = localMethodType2);
/*  120 */       if (localAdapter != null) {
/*  121 */         MethodType localMethodType3 = localMethodType2.insertParameterTypes(0, new Class[] { localAdapter.getClass() });
/*  122 */         MethodType localMethodType4 = ((MethodType)localObject2).insertParameterTypes(0, new Class[] { localAdapter.getClass() });
/*  123 */         localMethodHandle2 = localAdapter.prototypeEntryPoint();
/*  124 */         Object localObject3 = localMethodType3;
/*  125 */         int i = 0; for (int j = ((MethodType)localObject3).parameterCount(); i < j; i++) {
/*  126 */           if (((MethodType)localObject3).parameterType(i) != localMethodType4.parameterType(i)) {
/*  127 */             assert (((MethodType)localObject3).parameterType(i) == Long.TYPE);
/*  128 */             assert (localMethodType4.parameterType(i) == Integer.TYPE);
/*  129 */             MethodType localMethodType5 = ((MethodType)localObject3).changeParameterType(i, Integer.TYPE);
/*  130 */             localMethodHandle2 = MethodHandleImpl.convertArguments(localMethodHandle2, localMethodType5, (MethodType)localObject3, 0);
/*      */ 
/*  132 */             localObject3 = localMethodType5;
/*      */           }
/*      */         }
/*  135 */         assert (localObject3 == localMethodType4);
/*      */       }
/*      */       else
/*      */       {
/*  139 */         localAdapter = buildAdapterFromBytecodes(localObject1 = localObject2);
/*  140 */         localMethodHandle2 = localAdapter.prototypeEntryPoint();
/*      */       }
/*      */     }
/*  143 */     MethodType localMethodType2 = paramMethodType.insertParameterTypes(0, new Class[] { localAdapter.getClass() });
/*  144 */     this.entryPoint = AdapterMethodHandle.makeRetypeRaw(localMethodType2, localMethodHandle2);
/*      */ 
/*  146 */     if (this.entryPoint == null) {
/*  147 */       throw new UnsupportedOperationException("cannot retype to " + paramMethodType + " from " + localMethodHandle2.type().dropParameterTypes(0, 1));
/*      */     }
/*  149 */     this.returnConversion = computeReturnConversion(paramMethodType, (MethodType)localObject1, false);
/*  150 */     this.rawEntryType = ((MethodType)localObject1);
/*  151 */     this.adapter = localAdapter;
/*  152 */     this.invoker = makeRawArgumentFilter(localMethodHandle1, (MethodType)localObject1, paramMethodType);
/*      */   }
/*      */ 
/*      */   static MethodHandle makeRawArgumentFilter(MethodHandle paramMethodHandle, MethodType paramMethodType1, MethodType paramMethodType2)
/*      */   {
/*  166 */     MethodHandle localMethodHandle1 = null;
/*  167 */     int i = 0; for (int j = paramMethodType1.parameterCount(); i < j; i++) {
/*  168 */       Class localClass1 = paramMethodType1.parameterType(i);
/*  169 */       Class localClass2 = paramMethodType2.parameterType(i);
/*  170 */       if (localClass1 != localClass2) {
/*  171 */         assert ((localClass1.isPrimitive()) && (localClass2.isPrimitive()));
/*  172 */         if (localMethodHandle1 == null) {
/*  173 */           localMethodHandle1 = AdapterMethodHandle.makeCheckCast(paramMethodHandle.type().generic(), paramMethodHandle, 0, MethodHandle.class);
/*      */ 
/*  176 */           if (localMethodHandle1 == null) throw new UnsupportedOperationException("NYI");
/*      */         }
/*  178 */         MethodHandle localMethodHandle2 = ValueConversions.rebox(localClass2);
/*  179 */         localMethodHandle1 = FilterGeneric.makeArgumentFilter(1 + i, localMethodHandle2, localMethodHandle1);
/*  180 */         if (localMethodHandle1 == null) throw new InternalError(); 
/*      */       }
/*      */     }
/*  182 */     if (localMethodHandle1 == null) return paramMethodHandle;
/*  183 */     return AdapterMethodHandle.makeRetypeOnly(paramMethodHandle.type(), localMethodHandle1);
/*      */   }
/*      */ 
/*      */   private static MethodHandle computeReturnConversion(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean)
/*      */   {
/*  199 */     Class localClass1 = paramMethodType1.returnType();
/*  200 */     Class localClass2 = paramMethodType2.returnType();
/*  201 */     if ((paramBoolean) || (!localClass1.isPrimitive())) {
/*  202 */       assert (!localClass1.isPrimitive());
/*  203 */       assert (!localClass2.isPrimitive());
/*  204 */       if ((localClass2 == Object.class) && (!paramBoolean))
/*  205 */         return null;
/*  206 */       return ValueConversions.cast(localClass1);
/*  207 */     }if (localClass1 == localClass2) {
/*  208 */       return ValueConversions.unbox(localClass1);
/*      */     }
/*  210 */     assert (localClass2.isPrimitive());
/*  211 */     assert (localClass1 == Double.TYPE ? localClass2 == Long.TYPE : localClass2 == Integer.TYPE);
/*  212 */     return ValueConversions.unboxRaw(localClass1);
/*      */   }
/*      */ 
/*      */   Adapter makeInstance(MethodType paramMethodType, MethodHandle paramMethodHandle)
/*      */   {
/*  217 */     paramMethodHandle.getClass();
/*  218 */     MethodHandle localMethodHandle1 = this.returnConversion;
/*  219 */     if (this.primsAtEndOrder != null)
/*      */     {
/*  221 */       throw new UnsupportedOperationException("NYI");
/*  222 */     }if (paramMethodType == this.entryType) {
/*  223 */       if (localMethodHandle1 == null) localMethodHandle1 = ValueConversions.identity();
/*  224 */       return this.adapter.makeInstance(this.entryPoint, this.invoker, localMethodHandle1, paramMethodHandle);
/*      */     }
/*      */ 
/*  227 */     assert (paramMethodType.erase() == this.entryType);
/*  228 */     if (localMethodHandle1 == null) {
/*  229 */       localMethodHandle1 = computeReturnConversion(paramMethodType, this.rawEntryType, true);
/*      */     }
/*  231 */     MethodType localMethodType = paramMethodType.insertParameterTypes(0, new Class[] { this.adapter.getClass() });
/*  232 */     MethodHandle localMethodHandle2 = AdapterMethodHandle.makeRetypeRaw(localMethodType, this.entryPoint);
/*      */ 
/*  234 */     return this.adapter.makeInstance(localMethodHandle2, this.invoker, localMethodHandle1, paramMethodHandle);
/*      */   }
/*      */ 
/*      */   public static MethodHandle make(MethodType paramMethodType, MethodHandle paramMethodHandle)
/*      */   {
/*  245 */     MethodType localMethodType = paramMethodHandle.type();
/*  246 */     if (paramMethodType.generic() != localMethodType)
/*  247 */       throw MethodHandleStatics.newIllegalArgumentException("type must be generic");
/*  248 */     if (paramMethodType == localMethodType) return paramMethodHandle;
/*  249 */     return of(paramMethodType).makeInstance(paramMethodType, paramMethodHandle);
/*      */   }
/*      */ 
/*      */   static ToGeneric of(MethodType paramMethodType)
/*      */   {
/*  254 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/*  255 */     ToGeneric localToGeneric = localMethodTypeForm.toGeneric;
/*  256 */     if (localToGeneric == null)
/*  257 */       localMethodTypeForm.toGeneric = (localToGeneric = new ToGeneric(localMethodTypeForm.erasedType()));
/*  258 */     return localToGeneric;
/*      */   }
/*      */ 
/*      */   String debugString() {
/*  262 */     return "ToGeneric" + this.entryType + (this.primsAtEndOrder != null ? "[reorder]" : "");
/*      */   }
/*      */ 
/*      */   static Adapter findAdapter(MethodType paramMethodType)
/*      */   {
/*  268 */     MethodTypeForm localMethodTypeForm = paramMethodType.form();
/*  269 */     Class localClass1 = paramMethodType.returnType();
/*  270 */     int i = localMethodTypeForm.parameterCount();
/*  271 */     int j = localMethodTypeForm.longPrimitiveParameterCount();
/*  272 */     int k = localMethodTypeForm.primitiveParameterCount() - j;
/*  273 */     String str1 = (k > 0 ? "I" + k : "") + (j > 0 ? "J" + j : "");
/*  274 */     String str2 = String.valueOf(Wrapper.forPrimitiveType(localClass1).basicTypeChar());
/*  275 */     String str3 = "invoke_" + str2;
/*  276 */     String str4 = "invoke";
/*  277 */     String[] arrayOfString1 = { str3, str4 };
/*  278 */     String str5 = str2 + i;
/*  279 */     String str6 = "A" + i;
/*  280 */     String[] arrayOfString2 = { str6, str6 + str1, str5, str5 + str1 };
/*      */ 
/*  282 */     for (String str7 : arrayOfString2) {
/*  283 */       Class localClass2 = Adapter.findSubClass(str7);
/*  284 */       if (localClass2 != null)
/*      */       {
/*  286 */         for (String str8 : arrayOfString1) {
/*  287 */           MethodHandle localMethodHandle = null;
/*      */           try {
/*  289 */             localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findSpecial(localClass2, str8, paramMethodType, localClass2);
/*      */           }
/*      */           catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */           }
/*  293 */           if (localMethodHandle != null) {
/*  294 */             Constructor localConstructor = null;
/*      */             try
/*      */             {
/*  297 */               localConstructor = localClass2.getDeclaredConstructor(new Class[] { MethodHandle.class });
/*      */             } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */             } catch (SecurityException localSecurityException) {
/*      */             }
/*  301 */             if (localConstructor != null)
/*      */               try {
/*  303 */                 return (Adapter)localConstructor.newInstance(new Object[] { localMethodHandle });
/*      */               } catch (IllegalArgumentException localIllegalArgumentException) {
/*      */               } catch (InvocationTargetException localInvocationTargetException) {
/*  306 */                 Throwable localThrowable = localInvocationTargetException.getTargetException();
/*  307 */                 if ((localThrowable instanceof Error)) throw ((Error)localThrowable);
/*  308 */                 if ((localThrowable instanceof RuntimeException)) throw ((RuntimeException)localThrowable);  } catch (InstantiationException localInstantiationException) {
/*      */               } catch (IllegalAccessException localIllegalAccessException) {  }
/*      */ 
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  314 */     return null;
/*      */   }
/*      */ 
/*      */   static Adapter buildAdapterFromBytecodes(MethodType paramMethodType) {
/*  318 */     throw new UnsupportedOperationException("NYI: " + paramMethodType);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  156 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*      */   }
/*      */ 
/*      */   static class A0 extends ToGeneric.Adapter
/*      */   {
/*      */     protected A0(MethodHandle paramMethodHandle)
/*      */     {
/*  624 */       super(); } 
/*  625 */     protected A0(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  626 */     protected A0 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A0(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  627 */     protected Object target() throws Throwable { return this.invoker.invokeExact(this.target); } 
/*  628 */     protected Object targetA0() throws Throwable { return target(); } 
/*  629 */     protected Object invoke_L() throws Throwable { return return_L(targetA0()); } 
/*  630 */     protected int invoke_I() throws Throwable { return return_I(targetA0()); } 
/*  631 */     protected long invoke_J() throws Throwable { return return_J(targetA0()); } 
/*  632 */     protected float invoke_F() throws Throwable { return return_F(targetA0()); } 
/*  633 */     protected double invoke_D() throws Throwable { return return_D(targetA0()); } 
/*      */   }
/*      */   static class A1 extends ToGeneric.Adapter {
/*  636 */     protected A1(MethodHandle paramMethodHandle) { super(); } 
/*  637 */     protected A1(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  638 */     protected A1 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A1(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  639 */     protected Object target(Object paramObject) throws Throwable { return this.invoker.invokeExact(this.target, paramObject); } 
/*  640 */     protected Object targetA1(Object paramObject) throws Throwable { return target(paramObject); } 
/*  641 */     protected Object targetA1(int paramInt) throws Throwable { return target(Integer.valueOf(paramInt)); } 
/*  642 */     protected Object targetA1(long paramLong) throws Throwable { return target(Long.valueOf(paramLong)); } 
/*  643 */     protected Object invoke_L(Object paramObject) throws Throwable { return return_L(targetA1(paramObject)); } 
/*  644 */     protected int invoke_I(Object paramObject) throws Throwable { return return_I(targetA1(paramObject)); } 
/*  645 */     protected long invoke_J(Object paramObject) throws Throwable { return return_J(targetA1(paramObject)); } 
/*  646 */     protected float invoke_F(Object paramObject) throws Throwable { return return_F(targetA1(paramObject)); } 
/*  647 */     protected double invoke_D(Object paramObject) throws Throwable { return return_D(targetA1(paramObject)); } 
/*  648 */     protected Object invoke_L(int paramInt) throws Throwable { return return_L(targetA1(paramInt)); } 
/*  649 */     protected int invoke_I(int paramInt) throws Throwable { return return_I(targetA1(paramInt)); } 
/*  650 */     protected long invoke_J(int paramInt) throws Throwable { return return_J(targetA1(paramInt)); } 
/*  651 */     protected float invoke_F(int paramInt) throws Throwable { return return_F(targetA1(paramInt)); } 
/*  652 */     protected double invoke_D(int paramInt) throws Throwable { return return_D(targetA1(paramInt)); } 
/*  653 */     protected Object invoke_L(long paramLong) throws Throwable { return return_L(targetA1(paramLong)); } 
/*  654 */     protected int invoke_I(long paramLong) throws Throwable { return return_I(targetA1(paramLong)); } 
/*  655 */     protected long invoke_J(long paramLong) throws Throwable { return return_J(targetA1(paramLong)); } 
/*  656 */     protected float invoke_F(long paramLong) throws Throwable { return return_F(targetA1(paramLong)); } 
/*  657 */     protected double invoke_D(long paramLong) throws Throwable { return return_D(targetA1(paramLong)); }
/*      */ 
/*      */   }
/*      */ 
/*      */   static class A10 extends ToGeneric.Adapter
/*      */   {
/*      */     protected A10(MethodHandle paramMethodHandle)
/*      */     {
/* 1017 */       super(); } 
/* 1018 */     protected A10(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 1019 */     protected A10 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A10(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/* 1020 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10); } 
/* 1021 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10); } 
/* 1022 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, Long.valueOf(paramLong)); } 
/* 1023 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/* 1024 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/* 1025 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/* 1026 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/* 1027 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6)); } 
/* 1028 */     protected Object targetA10(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7)); } 
/* 1029 */     protected Object targetA10(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8)); } 
/* 1030 */     protected Object targetA10(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8), Long.valueOf(paramLong9)); } 
/* 1031 */     protected Object targetA10(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8), Long.valueOf(paramLong9), Long.valueOf(paramLong10)); } 
/* 1032 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 1033 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 1034 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10)); } 
/* 1035 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, long paramLong) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramLong)); } 
/* 1036 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, long paramLong) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramLong)); } 
/* 1037 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, long paramLong) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramLong)); } 
/* 1038 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong1, paramLong2)); } 
/* 1039 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong1, paramLong2)); } 
/* 1040 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong1, paramLong2)); } 
/* 1041 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2, paramLong3)); } 
/* 1042 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2, paramLong3)); } 
/* 1043 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2, paramLong3)); } 
/* 1044 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/* 1045 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/* 1046 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/* 1047 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1048 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1049 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1050 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1051 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1052 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1053 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1054 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1055 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1056 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_L(targetA10(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1057 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_I(targetA10(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1058 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_J(targetA10(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1059 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_L(targetA10(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); } 
/* 1060 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_I(targetA10(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); } 
/* 1061 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_J(targetA10(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); } 
/* 1062 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10) throws Throwable { return return_L(targetA10(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9, paramLong10)); } 
/* 1063 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10) throws Throwable { return return_I(targetA10(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9, paramLong10)); } 
/* 1064 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9, long paramLong10) throws Throwable { return return_J(targetA10(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9, paramLong10)); }
/*      */ 
/*      */   }
/*      */ 
/*      */   static class A2 extends ToGeneric.Adapter
/*      */   {
/*      */     protected A2(MethodHandle paramMethodHandle)
/*      */     {
/*  660 */       super(); } 
/*  661 */     protected A2(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  662 */     protected A2 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A2(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  663 */     protected Object target(Object paramObject1, Object paramObject2) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2); } 
/*  664 */     protected Object targetA2(Object paramObject1, Object paramObject2) throws Throwable { return target(paramObject1, paramObject2); } 
/*  665 */     protected Object targetA2(Object paramObject, int paramInt) throws Throwable { return target(paramObject, Integer.valueOf(paramInt)); } 
/*  666 */     protected Object targetA2(int paramInt1, int paramInt2) throws Throwable { return target(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); } 
/*  667 */     protected Object targetA2(Object paramObject, long paramLong) throws Throwable { return target(paramObject, Long.valueOf(paramLong)); } 
/*  668 */     protected Object targetA2(long paramLong1, long paramLong2) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  669 */     protected Object invoke_L(Object paramObject1, Object paramObject2) throws Throwable { return return_L(targetA2(paramObject1, paramObject2)); } 
/*  670 */     protected int invoke_I(Object paramObject1, Object paramObject2) throws Throwable { return return_I(targetA2(paramObject1, paramObject2)); } 
/*  671 */     protected long invoke_J(Object paramObject1, Object paramObject2) throws Throwable { return return_J(targetA2(paramObject1, paramObject2)); } 
/*  672 */     protected float invoke_F(Object paramObject1, Object paramObject2) throws Throwable { return return_F(targetA2(paramObject1, paramObject2)); } 
/*  673 */     protected double invoke_D(Object paramObject1, Object paramObject2) throws Throwable { return return_D(targetA2(paramObject1, paramObject2)); } 
/*  674 */     protected Object invoke_L(Object paramObject, int paramInt) throws Throwable { return return_L(targetA2(paramObject, paramInt)); } 
/*  675 */     protected int invoke_I(Object paramObject, int paramInt) throws Throwable { return return_I(targetA2(paramObject, paramInt)); } 
/*  676 */     protected long invoke_J(Object paramObject, int paramInt) throws Throwable { return return_J(targetA2(paramObject, paramInt)); } 
/*  677 */     protected float invoke_F(Object paramObject, int paramInt) throws Throwable { return return_F(targetA2(paramObject, paramInt)); } 
/*  678 */     protected double invoke_D(Object paramObject, int paramInt) throws Throwable { return return_D(targetA2(paramObject, paramInt)); } 
/*  679 */     protected Object invoke_L(int paramInt1, int paramInt2) throws Throwable { return return_L(targetA2(paramInt1, paramInt2)); } 
/*  680 */     protected int invoke_I(int paramInt1, int paramInt2) throws Throwable { return return_I(targetA2(paramInt1, paramInt2)); } 
/*  681 */     protected long invoke_J(int paramInt1, int paramInt2) throws Throwable { return return_J(targetA2(paramInt1, paramInt2)); } 
/*  682 */     protected float invoke_F(int paramInt1, int paramInt2) throws Throwable { return return_F(targetA2(paramInt1, paramInt2)); } 
/*  683 */     protected double invoke_D(int paramInt1, int paramInt2) throws Throwable { return return_D(targetA2(paramInt1, paramInt2)); } 
/*  684 */     protected Object invoke_L(Object paramObject, long paramLong) throws Throwable { return return_L(targetA2(paramObject, paramLong)); } 
/*  685 */     protected int invoke_I(Object paramObject, long paramLong) throws Throwable { return return_I(targetA2(paramObject, paramLong)); } 
/*  686 */     protected long invoke_J(Object paramObject, long paramLong) throws Throwable { return return_J(targetA2(paramObject, paramLong)); } 
/*  687 */     protected float invoke_F(Object paramObject, long paramLong) throws Throwable { return return_F(targetA2(paramObject, paramLong)); } 
/*  688 */     protected double invoke_D(Object paramObject, long paramLong) throws Throwable { return return_D(targetA2(paramObject, paramLong)); } 
/*  689 */     protected Object invoke_L(long paramLong1, long paramLong2) throws Throwable { return return_L(targetA2(paramLong1, paramLong2)); } 
/*  690 */     protected int invoke_I(long paramLong1, long paramLong2) throws Throwable { return return_I(targetA2(paramLong1, paramLong2)); } 
/*  691 */     protected long invoke_J(long paramLong1, long paramLong2) throws Throwable { return return_J(targetA2(paramLong1, paramLong2)); } 
/*  692 */     protected float invoke_F(long paramLong1, long paramLong2) throws Throwable { return return_F(targetA2(paramLong1, paramLong2)); } 
/*  693 */     protected double invoke_D(long paramLong1, long paramLong2) throws Throwable { return return_D(targetA2(paramLong1, paramLong2)); } 
/*      */   }
/*      */   static class A3 extends ToGeneric.Adapter {
/*  696 */     protected A3(MethodHandle paramMethodHandle) { super(); } 
/*  697 */     protected A3(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  698 */     protected A3 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A3(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  699 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3); } 
/*  700 */     protected Object targetA3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return target(paramObject1, paramObject2, paramObject3); } 
/*  701 */     protected Object targetA3(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return target(paramObject1, paramObject2, Integer.valueOf(paramInt)); } 
/*  702 */     protected Object targetA3(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return target(paramObject, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); } 
/*  703 */     protected Object targetA3(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return target(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3)); } 
/*  704 */     protected Object targetA3(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong)); } 
/*  705 */     protected Object targetA3(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  706 */     protected Object targetA3(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  707 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return return_L(targetA3(paramObject1, paramObject2, paramObject3)); } 
/*  708 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return return_I(targetA3(paramObject1, paramObject2, paramObject3)); } 
/*  709 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return return_J(targetA3(paramObject1, paramObject2, paramObject3)); } 
/*  710 */     protected float invoke_F(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return return_F(targetA3(paramObject1, paramObject2, paramObject3)); } 
/*  711 */     protected double invoke_D(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return return_D(targetA3(paramObject1, paramObject2, paramObject3)); } 
/*  712 */     protected Object invoke_L(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return return_L(targetA3(paramObject1, paramObject2, paramInt)); } 
/*  713 */     protected int invoke_I(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return return_I(targetA3(paramObject1, paramObject2, paramInt)); } 
/*  714 */     protected long invoke_J(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return return_J(targetA3(paramObject1, paramObject2, paramInt)); } 
/*  715 */     protected float invoke_F(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return return_F(targetA3(paramObject1, paramObject2, paramInt)); } 
/*  716 */     protected double invoke_D(Object paramObject1, Object paramObject2, int paramInt) throws Throwable { return return_D(targetA3(paramObject1, paramObject2, paramInt)); } 
/*  717 */     protected Object invoke_L(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return return_L(targetA3(paramObject, paramInt1, paramInt2)); } 
/*  718 */     protected int invoke_I(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return return_I(targetA3(paramObject, paramInt1, paramInt2)); } 
/*  719 */     protected long invoke_J(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return return_J(targetA3(paramObject, paramInt1, paramInt2)); } 
/*  720 */     protected float invoke_F(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return return_F(targetA3(paramObject, paramInt1, paramInt2)); } 
/*  721 */     protected double invoke_D(Object paramObject, int paramInt1, int paramInt2) throws Throwable { return return_D(targetA3(paramObject, paramInt1, paramInt2)); } 
/*  722 */     protected Object invoke_L(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_L(targetA3(paramInt1, paramInt2, paramInt3)); } 
/*  723 */     protected int invoke_I(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_I(targetA3(paramInt1, paramInt2, paramInt3)); } 
/*  724 */     protected long invoke_J(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_J(targetA3(paramInt1, paramInt2, paramInt3)); } 
/*  725 */     protected float invoke_F(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_F(targetA3(paramInt1, paramInt2, paramInt3)); } 
/*  726 */     protected double invoke_D(int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_D(targetA3(paramInt1, paramInt2, paramInt3)); } 
/*  727 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return return_L(targetA3(paramObject1, paramObject2, paramLong)); } 
/*  728 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return return_I(targetA3(paramObject1, paramObject2, paramLong)); } 
/*  729 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return return_J(targetA3(paramObject1, paramObject2, paramLong)); } 
/*  730 */     protected float invoke_F(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return return_F(targetA3(paramObject1, paramObject2, paramLong)); } 
/*  731 */     protected double invoke_D(Object paramObject1, Object paramObject2, long paramLong) throws Throwable { return return_D(targetA3(paramObject1, paramObject2, paramLong)); } 
/*  732 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA3(paramObject, paramLong1, paramLong2)); } 
/*  733 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA3(paramObject, paramLong1, paramLong2)); } 
/*  734 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA3(paramObject, paramLong1, paramLong2)); } 
/*  735 */     protected float invoke_F(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return return_F(targetA3(paramObject, paramLong1, paramLong2)); } 
/*  736 */     protected double invoke_D(Object paramObject, long paramLong1, long paramLong2) throws Throwable { return return_D(targetA3(paramObject, paramLong1, paramLong2)); } 
/*  737 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA3(paramLong1, paramLong2, paramLong3)); } 
/*  738 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA3(paramLong1, paramLong2, paramLong3)); } 
/*  739 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA3(paramLong1, paramLong2, paramLong3)); } 
/*  740 */     protected float invoke_F(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_F(targetA3(paramLong1, paramLong2, paramLong3)); } 
/*  741 */     protected double invoke_D(long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_D(targetA3(paramLong1, paramLong2, paramLong3)); } 
/*      */   }
/*      */ 
/*      */   static class A4 extends ToGeneric.Adapter {
/*  745 */     protected A4(MethodHandle paramMethodHandle) { super(); } 
/*  746 */     protected A4(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  747 */     protected A4 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A4(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  748 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4); } 
/*  749 */     protected Object targetA4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4); } 
/*  750 */     protected Object targetA4(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Integer.valueOf(paramInt)); } 
/*  751 */     protected Object targetA4(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return target(paramObject1, paramObject2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); } 
/*  752 */     protected Object targetA4(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return target(paramObject, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3)); } 
/*  753 */     protected Object targetA4(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return target(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4)); } 
/*  754 */     protected Object targetA4(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong)); } 
/*  755 */     protected Object targetA4(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  756 */     protected Object targetA4(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  757 */     protected Object targetA4(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  758 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return return_L(targetA4(paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*  759 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return return_I(targetA4(paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*  760 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return return_J(targetA4(paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*  761 */     protected float invoke_F(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return return_F(targetA4(paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*  762 */     protected double invoke_D(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return return_D(targetA4(paramObject1, paramObject2, paramObject3, paramObject4)); } 
/*  763 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return return_L(targetA4(paramObject1, paramObject2, paramObject3, paramInt)); } 
/*  764 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return return_I(targetA4(paramObject1, paramObject2, paramObject3, paramInt)); } 
/*  765 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return return_J(targetA4(paramObject1, paramObject2, paramObject3, paramInt)); } 
/*  766 */     protected float invoke_F(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return return_F(targetA4(paramObject1, paramObject2, paramObject3, paramInt)); } 
/*  767 */     protected double invoke_D(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt) throws Throwable { return return_D(targetA4(paramObject1, paramObject2, paramObject3, paramInt)); } 
/*  768 */     protected Object invoke_L(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return return_L(targetA4(paramObject1, paramObject2, paramInt1, paramInt2)); } 
/*  769 */     protected int invoke_I(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return return_I(targetA4(paramObject1, paramObject2, paramInt1, paramInt2)); } 
/*  770 */     protected long invoke_J(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return return_J(targetA4(paramObject1, paramObject2, paramInt1, paramInt2)); } 
/*  771 */     protected float invoke_F(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return return_F(targetA4(paramObject1, paramObject2, paramInt1, paramInt2)); } 
/*  772 */     protected double invoke_D(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) throws Throwable { return return_D(targetA4(paramObject1, paramObject2, paramInt1, paramInt2)); } 
/*  773 */     protected Object invoke_L(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_L(targetA4(paramObject, paramInt1, paramInt2, paramInt3)); } 
/*  774 */     protected int invoke_I(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_I(targetA4(paramObject, paramInt1, paramInt2, paramInt3)); } 
/*  775 */     protected long invoke_J(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_J(targetA4(paramObject, paramInt1, paramInt2, paramInt3)); } 
/*  776 */     protected float invoke_F(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_F(targetA4(paramObject, paramInt1, paramInt2, paramInt3)); } 
/*  777 */     protected double invoke_D(Object paramObject, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_D(targetA4(paramObject, paramInt1, paramInt2, paramInt3)); } 
/*  778 */     protected Object invoke_L(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_L(targetA4(paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  779 */     protected int invoke_I(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_I(targetA4(paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  780 */     protected long invoke_J(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_J(targetA4(paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  781 */     protected float invoke_F(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_F(targetA4(paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  782 */     protected double invoke_D(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_D(targetA4(paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  783 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return return_L(targetA4(paramObject1, paramObject2, paramObject3, paramLong)); } 
/*  784 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return return_I(targetA4(paramObject1, paramObject2, paramObject3, paramLong)); } 
/*  785 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return return_J(targetA4(paramObject1, paramObject2, paramObject3, paramLong)); } 
/*  786 */     protected float invoke_F(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return return_F(targetA4(paramObject1, paramObject2, paramObject3, paramLong)); } 
/*  787 */     protected double invoke_D(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong) throws Throwable { return return_D(targetA4(paramObject1, paramObject2, paramObject3, paramLong)); } 
/*  788 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA4(paramObject1, paramObject2, paramLong1, paramLong2)); } 
/*  789 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA4(paramObject1, paramObject2, paramLong1, paramLong2)); } 
/*  790 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA4(paramObject1, paramObject2, paramLong1, paramLong2)); } 
/*  791 */     protected float invoke_F(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return return_F(targetA4(paramObject1, paramObject2, paramLong1, paramLong2)); } 
/*  792 */     protected double invoke_D(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2) throws Throwable { return return_D(targetA4(paramObject1, paramObject2, paramLong1, paramLong2)); } 
/*  793 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA4(paramObject, paramLong1, paramLong2, paramLong3)); } 
/*  794 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA4(paramObject, paramLong1, paramLong2, paramLong3)); } 
/*  795 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA4(paramObject, paramLong1, paramLong2, paramLong3)); } 
/*  796 */     protected float invoke_F(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_F(targetA4(paramObject, paramLong1, paramLong2, paramLong3)); } 
/*  797 */     protected double invoke_D(Object paramObject, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_D(targetA4(paramObject, paramLong1, paramLong2, paramLong3)); } 
/*  798 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA4(paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  799 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA4(paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  800 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA4(paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  801 */     protected float invoke_F(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_F(targetA4(paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  802 */     protected double invoke_D(long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_D(targetA4(paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*      */   }
/*      */ 
/*      */   static class A5 extends ToGeneric.Adapter {
/*  806 */     protected A5(MethodHandle paramMethodHandle) { super(); } 
/*  807 */     protected A5(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  808 */     protected A5 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A5(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  809 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); } 
/*  810 */     protected Object targetA5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5); } 
/*  811 */     protected Object targetA5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, int paramInt) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Integer.valueOf(paramInt)); } 
/*  812 */     protected Object targetA5(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt1, int paramInt2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); } 
/*  813 */     protected Object targetA5(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return target(paramObject1, paramObject2, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3)); } 
/*  814 */     protected Object targetA5(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return target(paramObject, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4)); } 
/*  815 */     protected Object targetA5(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws Throwable { return target(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5)); } 
/*  816 */     protected Object targetA5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong)); } 
/*  817 */     protected Object targetA5(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  818 */     protected Object targetA5(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  819 */     protected Object targetA5(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  820 */     protected Object targetA5(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/*  821 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/*  822 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/*  823 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5)); } 
/*  824 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, int paramInt) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramInt)); } 
/*  825 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, int paramInt) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramInt)); } 
/*  826 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, int paramInt) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramInt)); } 
/*  827 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt1, int paramInt2) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramObject3, paramInt1, paramInt2)); } 
/*  828 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt1, int paramInt2) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramObject3, paramInt1, paramInt2)); } 
/*  829 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, int paramInt1, int paramInt2) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramObject3, paramInt1, paramInt2)); } 
/*  830 */     protected Object invoke_L(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramInt1, paramInt2, paramInt3)); } 
/*  831 */     protected int invoke_I(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramInt1, paramInt2, paramInt3)); } 
/*  832 */     protected long invoke_J(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2, int paramInt3) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramInt1, paramInt2, paramInt3)); } 
/*  833 */     protected Object invoke_L(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_L(targetA5(paramObject, paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  834 */     protected int invoke_I(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_I(targetA5(paramObject, paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  835 */     protected long invoke_J(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws Throwable { return return_J(targetA5(paramObject, paramInt1, paramInt2, paramInt3, paramInt4)); } 
/*  836 */     protected Object invoke_L(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws Throwable { return return_L(targetA5(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5)); } 
/*  837 */     protected int invoke_I(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws Throwable { return return_I(targetA5(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5)); } 
/*  838 */     protected long invoke_J(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) throws Throwable { return return_J(targetA5(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5)); } 
/*  839 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramLong)); } 
/*  840 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramLong)); } 
/*  841 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramObject3, paramObject4, paramLong)); } 
/*  842 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2)); } 
/*  843 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2)); } 
/*  844 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2)); } 
/*  845 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA5(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3)); } 
/*  846 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA5(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3)); } 
/*  847 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA5(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3)); } 
/*  848 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA5(paramObject, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  849 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA5(paramObject, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  850 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA5(paramObject, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  851 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA5(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  852 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA5(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  853 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA5(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*      */   }
/*      */ 
/*      */   static class A6 extends ToGeneric.Adapter {
/*  857 */     protected A6(MethodHandle paramMethodHandle) { super(); } 
/*  858 */     protected A6(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  859 */     protected A6 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A6(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  860 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6); } 
/*  861 */     protected Object targetA6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6); } 
/*  862 */     protected Object targetA6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, Long.valueOf(paramLong)); } 
/*  863 */     protected Object targetA6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  864 */     protected Object targetA6(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  865 */     protected Object targetA6(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  866 */     protected Object targetA6(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/*  867 */     protected Object targetA6(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6)); } 
/*  868 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return return_L(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/*  869 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return return_I(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/*  870 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable { return return_J(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6)); } 
/*  871 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong) throws Throwable { return return_L(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong)); } 
/*  872 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong) throws Throwable { return return_I(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong)); } 
/*  873 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong) throws Throwable { return return_J(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong)); } 
/*  874 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2)); } 
/*  875 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2)); } 
/*  876 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA6(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2)); } 
/*  877 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA6(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3)); } 
/*  878 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA6(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3)); } 
/*  879 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA6(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3)); } 
/*  880 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA6(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  881 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA6(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  882 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA6(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  883 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA6(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  884 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA6(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  885 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA6(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  886 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_L(targetA6(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  887 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_I(targetA6(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  888 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_J(targetA6(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*      */   }
/*      */   static class A7 extends ToGeneric.Adapter {
/*  891 */     protected A7(MethodHandle paramMethodHandle) { super(); } 
/*  892 */     protected A7(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  893 */     protected A7 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A7(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  894 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7); } 
/*  895 */     protected Object targetA7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7); } 
/*  896 */     protected Object targetA7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, Long.valueOf(paramLong)); } 
/*  897 */     protected Object targetA7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  898 */     protected Object targetA7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  899 */     protected Object targetA7(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  900 */     protected Object targetA7(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/*  901 */     protected Object targetA7(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6)); } 
/*  902 */     protected Object targetA7(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7)); } 
/*  903 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/*  904 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/*  905 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7)); } 
/*  906 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong)); } 
/*  907 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong)); } 
/*  908 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong)); } 
/*  909 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2)); } 
/*  910 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2)); } 
/*  911 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2)); } 
/*  912 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3)); } 
/*  913 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3)); } 
/*  914 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3)); } 
/*  915 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  916 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  917 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  918 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA7(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  919 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA7(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  920 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA7(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  921 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_L(targetA7(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  922 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_I(targetA7(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  923 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_J(targetA7(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  924 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_L(targetA7(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*  925 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_I(targetA7(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*  926 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_J(targetA7(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*      */   }
/*      */   static class A8 extends ToGeneric.Adapter {
/*  929 */     protected A8(MethodHandle paramMethodHandle) { super(); } 
/*  930 */     protected A8(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  931 */     protected A8 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A8(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  932 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8); } 
/*  933 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8); } 
/*  934 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, Long.valueOf(paramLong)); } 
/*  935 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  936 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  937 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  938 */     protected Object targetA8(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/*  939 */     protected Object targetA8(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6)); } 
/*  940 */     protected Object targetA8(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7)); } 
/*  941 */     protected Object targetA8(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8)); } 
/*  942 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/*  943 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/*  944 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8)); } 
/*  945 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong)); } 
/*  946 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong)); } 
/*  947 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong)); } 
/*  948 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2)); } 
/*  949 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2)); } 
/*  950 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2)); } 
/*  951 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3)); } 
/*  952 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3)); } 
/*  953 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3)); } 
/*  954 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  955 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  956 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  957 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  958 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  959 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/*  960 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_L(targetA8(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  961 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_I(targetA8(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  962 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_J(targetA8(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/*  963 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_L(targetA8(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*  964 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_I(targetA8(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*  965 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_J(targetA8(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/*  966 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_L(targetA8(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/*  967 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_I(targetA8(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/*  968 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_J(targetA8(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/*      */   }
/*      */   static class A9 extends ToGeneric.Adapter {
/*  971 */     protected A9(MethodHandle paramMethodHandle) { super(); } 
/*  972 */     protected A9(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { super(paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  973 */     protected A9 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) { return new A9(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3, paramMethodHandle4); } 
/*  974 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9); } 
/*  975 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9); } 
/*  976 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, Long.valueOf(paramLong)); } 
/*  977 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, Long.valueOf(paramLong1), Long.valueOf(paramLong2)); } 
/*  978 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3)); } 
/*  979 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4)); } 
/*  980 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return target(paramObject1, paramObject2, paramObject3, paramObject4, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5)); } 
/*  981 */     protected Object targetA9(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return target(paramObject1, paramObject2, paramObject3, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6)); } 
/*  982 */     protected Object targetA9(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return target(paramObject1, paramObject2, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7)); } 
/*  983 */     protected Object targetA9(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return target(paramObject, Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8)); } 
/*  984 */     protected Object targetA9(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return target(Long.valueOf(paramLong1), Long.valueOf(paramLong2), Long.valueOf(paramLong3), Long.valueOf(paramLong4), Long.valueOf(paramLong5), Long.valueOf(paramLong6), Long.valueOf(paramLong7), Long.valueOf(paramLong8), Long.valueOf(paramLong9)); } 
/*  985 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/*  986 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/*  987 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9)); } 
/*  988 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong)); } 
/*  989 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong)); } 
/*  990 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, long paramLong) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramLong)); } 
/*  991 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2)); } 
/*  992 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2)); } 
/*  993 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, long paramLong1, long paramLong2) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramLong1, paramLong2)); } 
/*  994 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3)); } 
/*  995 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3)); } 
/*  996 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, long paramLong1, long paramLong2, long paramLong3) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramLong1, paramLong2, paramLong3)); } 
/*  997 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  998 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/*  999 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, long paramLong1, long paramLong2, long paramLong3, long paramLong4) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramLong1, paramLong2, paramLong3, paramLong4)); } 
/* 1000 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1001 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1002 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramObject4, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)); } 
/* 1003 */     protected Object invoke_L(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1004 */     protected int invoke_I(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1005 */     protected long invoke_J(Object paramObject1, Object paramObject2, Object paramObject3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramObject3, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6)); } 
/* 1006 */     protected Object invoke_L(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_L(targetA9(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1007 */     protected int invoke_I(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_I(targetA9(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1008 */     protected long invoke_J(Object paramObject1, Object paramObject2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7) throws Throwable { return return_J(targetA9(paramObject1, paramObject2, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7)); } 
/* 1009 */     protected Object invoke_L(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_L(targetA9(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1010 */     protected int invoke_I(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_I(targetA9(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1011 */     protected long invoke_J(Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8) throws Throwable { return return_J(targetA9(paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8)); } 
/* 1012 */     protected Object invoke_L(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_L(targetA9(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); } 
/* 1013 */     protected int invoke_I(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_I(targetA9(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); } 
/* 1014 */     protected long invoke_J(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, long paramLong8, long paramLong9) throws Throwable { return return_J(targetA9(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramLong7, paramLong8, paramLong9)); }
/*      */ 
/*      */   }
/*      */ 
/*      */   static abstract class Adapter extends BoundMethodHandle
/*      */   {
/*      */     protected final MethodHandle invoker;
/*      */     protected final MethodHandle target;
/*      */     protected final MethodHandle convert;
/*  407 */     private static final String CLASS_PREFIX = str1.substring(0, str1.length() - str2.length());
/*      */ 
/*      */     String debugString()
/*      */     {
/*  344 */       return this.target == null ? "prototype:" + this.convert : MethodHandleStatics.addTypeString(this.target, this);
/*      */     }
/*      */     protected boolean isPrototype() {
/*  347 */       return this.target == null;
/*      */     }
/*      */     protected Adapter(MethodHandle paramMethodHandle) {
/*  350 */       super();
/*  351 */       this.invoker = null;
/*  352 */       this.convert = paramMethodHandle;
/*  353 */       this.target = null;
/*  354 */       assert (isPrototype());
/*      */     }
/*      */     protected MethodHandle prototypeEntryPoint() {
/*  357 */       if (!isPrototype()) throw new InternalError();
/*  358 */       return this.convert;
/*      */     }
/*      */ 
/*      */     protected Adapter(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4) {
/*  362 */       super();
/*  363 */       this.invoker = paramMethodHandle2;
/*  364 */       this.convert = paramMethodHandle3;
/*  365 */       this.target = paramMethodHandle4;
/*      */     }
/*      */ 
/*      */     protected abstract Adapter makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4);
/*      */ 
/*      */     protected Object target()
/*      */       throws Throwable
/*      */     {
/*  374 */       return this.invoker.invokeExact(this.target); } 
/*  375 */     protected Object target(Object paramObject) throws Throwable { return this.invoker.invokeExact(this.target, paramObject); } 
/*      */     protected Object target(Object paramObject1, Object paramObject2) throws Throwable {
/*  377 */       return this.invoker.invokeExact(this.target, paramObject1, paramObject2);
/*      */     }
/*  379 */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3); } 
/*      */     protected Object target(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  381 */       return this.invoker.invokeExact(this.target, paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */ 
/*      */     protected Object return_L(Object paramObject)
/*      */       throws Throwable
/*      */     {
/*  396 */       return this.convert.invokeExact(paramObject); } 
/*  397 */     protected int return_I(Object paramObject) throws Throwable { return this.convert.invokeExact(paramObject); } 
/*  398 */     protected long return_J(Object paramObject) throws Throwable { return this.convert.invokeExact(paramObject); } 
/*  399 */     protected float return_F(Object paramObject) throws Throwable { return this.convert.invokeExact(paramObject); } 
/*  400 */     protected double return_D(Object paramObject) throws Throwable { return this.convert.invokeExact(paramObject); }
/*      */ 
/*      */ 
/*      */     static Class<? extends Adapter> findSubClass(String paramString)
/*      */     {
/*  411 */       String str = CLASS_PREFIX + paramString;
/*      */       try {
/*  413 */         return Class.forName(str).asSubclass(Adapter.class);
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/*  415 */         return null; } catch (ClassCastException localClassCastException) {
/*      */       }
/*  417 */       return null;
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/*  404 */       String str1 = Adapter.class.getName();
/*  405 */       String str2 = Adapter.class.getSimpleName();
/*  406 */       if (!str1.endsWith(str2)) throw new InternalError();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.ToGeneric
 * JD-Core Version:    0.6.2
 */