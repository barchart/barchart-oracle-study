/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ 
/*      */ class FilterGeneric
/*      */ {
/*      */   private final MethodType entryType;
/*      */   private final Adapter[] adapters;
/*      */ 
/*      */   FilterGeneric(MethodType paramMethodType)
/*      */   {
/*   48 */     this.entryType = paramMethodType;
/*   49 */     int i = Kind.LIMIT.invokerIndex(1 + paramMethodType.parameterCount());
/*   50 */     this.adapters = new Adapter[i];
/*      */   }
/*      */ 
/*      */   Adapter getAdapter(Kind paramKind, int paramInt) {
/*   54 */     int i = paramKind.invokerIndex(paramInt);
/*   55 */     Adapter localAdapter = this.adapters[i];
/*   56 */     if (localAdapter != null) return localAdapter;
/*   57 */     localAdapter = findAdapter(this.entryType, paramKind, paramInt);
/*   58 */     if (localAdapter == null)
/*   59 */       localAdapter = buildAdapterFromBytecodes(this.entryType, paramKind, paramInt);
/*   60 */     this.adapters[i] = localAdapter;
/*   61 */     return localAdapter;
/*      */   }
/*      */ 
/*      */   Adapter makeInstance(Kind paramKind, int paramInt, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/*   69 */     Adapter localAdapter = getAdapter(paramKind, paramInt);
/*   70 */     return localAdapter.makeInstance(localAdapter.prototypeEntryPoint(), paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle makeArgumentFilter(int paramInt, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/*   81 */     return make(Kind.value, paramInt, paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle makeArgumentFolder(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/*   92 */     int i = paramMethodHandle1.type().parameterCount();
/*   93 */     return make(Kind.fold, i, paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle makeFlyby(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/*  105 */     return make(Kind.flyby, 0, paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle makeArgumentCollector(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/*  116 */     int i = paramMethodHandle2.type().parameterCount() - 1;
/*  117 */     return make(Kind.collect, i, paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   static MethodHandle make(Kind paramKind, int paramInt, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
/*  121 */     FilterGeneric localFilterGeneric = of(paramKind, paramInt, paramMethodHandle1.type(), paramMethodHandle2.type());
/*  122 */     return localFilterGeneric.makeInstance(paramKind, paramInt, paramMethodHandle1, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   static FilterGeneric of(Kind paramKind, int paramInt, MethodType paramMethodType1, MethodType paramMethodType2)
/*      */   {
/*  127 */     MethodType localMethodType = entryType(paramKind, paramInt, paramMethodType1, paramMethodType2);
/*  128 */     if (localMethodType.generic() != localMethodType)
/*  129 */       throw MethodHandleStatics.newIllegalArgumentException("must be generic: " + localMethodType);
/*  130 */     MethodTypeForm localMethodTypeForm = localMethodType.form();
/*  131 */     FilterGeneric localFilterGeneric = localMethodTypeForm.filterGeneric;
/*  132 */     if (localFilterGeneric == null)
/*  133 */       localMethodTypeForm.filterGeneric = (localFilterGeneric = new FilterGeneric(localMethodType));
/*  134 */     return localFilterGeneric;
/*      */   }
/*      */ 
/*      */   public String toString() {
/*  138 */     return "FilterGeneric/" + this.entryType;
/*      */   }
/*      */ 
/*      */   static MethodType targetType(MethodType paramMethodType1, Kind paramKind, int paramInt, MethodType paramMethodType2) {
/*  142 */     MethodType localMethodType = paramMethodType1;
/*  143 */     switch (1.$SwitchMap$java$lang$invoke$FilterGeneric$Kind[paramKind.ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/*  146 */       break;
/*      */     case 3:
/*  148 */       localMethodType = localMethodType.insertParameterTypes(0, new Class[] { paramMethodType2.returnType() });
/*  149 */       break;
/*      */     case 4:
/*  151 */       localMethodType = localMethodType.dropParameterTypes(paramInt, localMethodType.parameterCount());
/*  152 */       localMethodType = localMethodType.insertParameterTypes(paramInt, new Class[] { paramMethodType2.returnType() });
/*  153 */       break;
/*      */     default:
/*  155 */       throw new InternalError();
/*      */     }
/*  157 */     return localMethodType;
/*      */   }
/*      */ 
/*      */   static MethodType entryType(Kind paramKind, int paramInt, MethodType paramMethodType1, MethodType paramMethodType2) {
/*  161 */     MethodType localMethodType = paramMethodType2;
/*  162 */     switch (1.$SwitchMap$java$lang$invoke$FilterGeneric$Kind[paramKind.ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/*  165 */       break;
/*      */     case 3:
/*  167 */       localMethodType = localMethodType.dropParameterTypes(0, 1);
/*  168 */       break;
/*      */     case 4:
/*  170 */       localMethodType = localMethodType.dropParameterTypes(paramInt, paramInt + 1);
/*  171 */       localMethodType = localMethodType.insertParameterTypes(paramInt, paramMethodType1.parameterList());
/*  172 */       break;
/*      */     default:
/*  174 */       throw new InternalError();
/*      */     }
/*  176 */     return localMethodType;
/*      */   }
/*      */ 
/*      */   static Adapter findAdapter(MethodType paramMethodType, Kind paramKind, int paramInt)
/*      */   {
/*  181 */     int i = paramMethodType.parameterCount();
/*  182 */     String str1 = "F" + i;
/*  183 */     String str2 = "F" + i + paramKind.key;
/*  184 */     String[] arrayOfString1 = { str1, str2 };
/*  185 */     String str3 = paramKind.invokerName(paramInt);
/*      */ 
/*  187 */     for (String str4 : arrayOfString1) {
/*  188 */       Class localClass = Adapter.findSubClass(str4);
/*  189 */       if (localClass != null)
/*      */       {
/*  191 */         MethodHandle localMethodHandle = null;
/*      */         try {
/*  193 */           localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findSpecial(localClass, str3, paramMethodType, localClass);
/*      */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */         }
/*  196 */         if (localMethodHandle != null) {
/*  197 */           Constructor localConstructor = null;
/*      */           try {
/*  199 */             localConstructor = localClass.getDeclaredConstructor(new Class[] { MethodHandle.class });
/*      */           } catch (NoSuchMethodException localNoSuchMethodException) {
/*      */           } catch (SecurityException localSecurityException) {
/*      */           }
/*  203 */           if (localConstructor != null)
/*      */             try
/*      */             {
/*  206 */               return (Adapter)localConstructor.newInstance(new Object[] { localMethodHandle });
/*      */             } catch (IllegalArgumentException localIllegalArgumentException) {
/*      */             } catch (InvocationTargetException localInvocationTargetException) {
/*  209 */               Throwable localThrowable = localInvocationTargetException.getTargetException();
/*  210 */               if ((localThrowable instanceof Error)) throw ((Error)localThrowable);
/*  211 */               if ((localThrowable instanceof RuntimeException)) throw ((RuntimeException)localThrowable);  } catch (InstantiationException localInstantiationException) {
/*      */             } catch (IllegalAccessException localIllegalAccessException) {  }
/*      */ 
/*      */         }
/*      */       }
/*      */     }
/*  216 */     return null;
/*      */   }
/*      */ 
/*      */   static Adapter buildAdapterFromBytecodes(MethodType paramMethodType, Kind paramKind, int paramInt) {
/*  220 */     throw new UnsupportedOperationException("NYI");
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   65 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*      */   }
/*      */ 
/*      */   static abstract class Adapter extends BoundMethodHandle
/*      */   {
/*      */     protected final MethodHandle filter;
/*      */     protected final MethodHandle target;
/*  268 */     private static final String CLASS_PREFIX = str1.substring(0, str1.length() - str2.length());
/*      */ 
/*      */     String debugString()
/*      */     {
/*  238 */       return MethodHandleStatics.addTypeString(this.target, this);
/*      */     }
/*      */     protected boolean isPrototype() {
/*  241 */       return this.target == null;
/*      */     }
/*  243 */     protected Adapter(MethodHandle paramMethodHandle) { this(paramMethodHandle, paramMethodHandle, null);
/*  244 */       assert (isPrototype()); }
/*      */ 
/*      */     protected MethodHandle prototypeEntryPoint() {
/*  247 */       if (!isPrototype()) throw new InternalError();
/*  248 */       return this.filter;
/*      */     }
/*      */ 
/*      */     protected Adapter(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3)
/*      */     {
/*  253 */       super();
/*  254 */       this.filter = paramMethodHandle2;
/*  255 */       this.target = paramMethodHandle3;
/*      */     }
/*      */ 
/*      */     protected abstract Adapter makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3);
/*      */ 
/*      */     static Class<? extends Adapter> findSubClass(String paramString)
/*      */     {
/*  272 */       String str = CLASS_PREFIX + paramString;
/*      */       try {
/*  274 */         return Class.forName(str).asSubclass(Adapter.class);
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {
/*  276 */         return null; } catch (ClassCastException localClassCastException) {
/*      */       }
/*  278 */       return null;
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/*  265 */       String str1 = Adapter.class.getName();
/*  266 */       String str2 = Adapter.class.getSimpleName();
/*  267 */       if (!str1.endsWith(str2)) throw new InternalError();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F0 extends FilterGeneric.Adapter
/*      */   {
/*  342 */     static final Object[] NO_ARGS = new Object[0];
/*      */ 
/*      */     protected F0(MethodHandle paramMethodHandle)
/*      */     {
/*  333 */       super();
/*      */     }
/*  335 */     protected F0(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F0 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  337 */       return new F0(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  339 */     protected Object invoke_F0() throws Throwable { return this.target.invokeExact(this.filter.invokeExact()); } 
/*      */     protected Object invoke_C0() throws Throwable {
/*  341 */       return this.target.invokeExact(this.filter.invokeExact());
/*      */     }
/*      */     protected Object invoke_Y0() throws Throwable {
/*  344 */       this.filter.invokeExact(NO_ARGS);
/*  345 */       return this.target.invokeExact();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F1 extends FilterGeneric.Adapter
/*      */   {
/*      */     protected F1(MethodHandle paramMethodHandle)
/*      */     {
/*  506 */       super();
/*      */     }
/*  508 */     protected F1(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F1 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  510 */       return new F1(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  512 */     protected Object invoke_V0(Object paramObject) throws Throwable { return this.target.invokeExact(this.filter.invokeExact(paramObject)); } 
/*      */     protected Object invoke_F0(Object paramObject) throws Throwable {
/*  514 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject);
/*      */     }
/*      */     protected Object invoke_F1(Object paramObject) throws Throwable {
/*  517 */       return this.target.invokeExact(this.filter.invokeExact(paramObject), paramObject);
/*      */     }
/*      */     protected Object invoke_C0(Object paramObject) throws Throwable {
/*  520 */       return this.target.invokeExact(this.filter.invokeExact(paramObject));
/*      */     }
/*  522 */     protected Object invoke_C1(Object paramObject) throws Throwable { return this.target.invokeExact(paramObject, this.filter.invokeExact()); } 
/*      */     protected Object invoke_Y0(Object paramObject) throws Throwable {
/*  524 */       Object[] arrayOfObject = { paramObject };
/*  525 */       this.filter.invokeExact(arrayOfObject);
/*  526 */       return this.target.invokeExact(arrayOfObject[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F10 extends FilterGeneric.Adapter
/*      */   {
/*      */     protected F10(MethodHandle paramMethodHandle)
/*      */     {
/* 1115 */       super();
/*      */     }
/* 1117 */     protected F10(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F10 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 1119 */       return new F10(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1123 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1127 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1131 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1135 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1139 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1143 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1147 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1151 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1155 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1159 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1163 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1168 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1173 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1178 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1183 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1188 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1193 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1198 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1203 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1208 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1213 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable
/*      */     {
/* 1218 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1222 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1226 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1230 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1234 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1238 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1242 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1246 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1250 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1254 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1258 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) throws Throwable {
/* 1262 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 };
/* 1263 */       this.filter.invokeExact(arrayOfObject);
/* 1264 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9]);
/*      */     }
/*      */   }
/* 1267 */   static class F11 extends FilterGeneric.Adapter { protected F11(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F11(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 1269 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 1271 */     protected F11 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F11(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1275 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1280 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1285 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1290 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1295 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1300 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1305 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1310 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1315 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1320 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1325 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1330 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1335 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1340 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1345 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1350 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1355 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1360 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1365 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1370 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1375 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1380 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1385 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1390 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1395 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1400 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1405 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1410 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1415 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1420 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1425 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1430 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1435 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable
/*      */     {
/* 1440 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable {
/* 1444 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11) throws Throwable {
/* 1448 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11 };
/* 1449 */       this.filter.invokeExact(arrayOfObject);
/* 1450 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10]);
/*      */     } } 
/*      */   static class F12 extends FilterGeneric.Adapter {
/* 1453 */     protected F12(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F12(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 1455 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 1457 */     protected F12 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F12(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1461 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1466 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1471 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1476 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1481 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1486 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1491 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1496 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1501 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1506 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1511 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1516 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1521 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1526 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1531 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1536 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1541 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1546 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1551 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1556 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1561 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1566 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1571 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1576 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12)
/*      */       throws Throwable
/*      */     {
/* 1582 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1587 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1592 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1597 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1602 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1607 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1612 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1617 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1622 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1627 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1632 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1637 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable {
/* 1641 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable
/*      */     {
/* 1646 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12) throws Throwable {
/* 1650 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12 };
/* 1651 */       this.filter.invokeExact(arrayOfObject);
/* 1652 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11]);
/*      */     }
/*      */   }
/* 1655 */   static class F13 extends FilterGeneric.Adapter { protected F13(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F13(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 1657 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 1659 */     protected F13 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F13(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1664 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1670 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1676 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1682 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1688 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1694 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1700 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1706 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1712 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1718 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1724 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1730 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1736 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1742 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1748 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1754 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1760 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1766 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1772 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1778 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1784 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1790 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1796 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1802 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1808 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1815 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1822 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1828 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1834 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1840 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1846 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1852 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1858 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1864 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1870 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1876 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1882 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1888 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13) throws Throwable
/*      */     {
/* 1893 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1899 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13)
/*      */       throws Throwable
/*      */     {
/* 1905 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13) throws Throwable
/*      */     {
/* 1910 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13 };
/* 1911 */       this.filter.invokeExact(arrayOfObject);
/* 1912 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12]);
/*      */     } } 
/*      */   static class F14 extends FilterGeneric.Adapter {
/* 1915 */     protected F14(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F14(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 1917 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 1919 */     protected F14 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F14(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1924 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1930 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1936 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1942 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1948 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1954 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1960 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1966 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1972 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1978 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1984 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1990 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 1996 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2002 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2008 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2014 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2020 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2026 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2032 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2038 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2044 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2050 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2056 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2062 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2068 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2074 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2081 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2088 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2095 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2101 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2107 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2113 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2119 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2125 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2131 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2137 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2143 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2149 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2155 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2161 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14) throws Throwable
/*      */     {
/* 2166 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2172 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2178 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
/*      */       throws Throwable
/*      */     {
/* 2184 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14) throws Throwable
/*      */     {
/* 2189 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14 };
/* 2190 */       this.filter.invokeExact(arrayOfObject);
/* 2191 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13]);
/*      */     }
/*      */   }
/* 2194 */   static class F15 extends FilterGeneric.Adapter { protected F15(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F15(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 2196 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 2198 */     protected F15 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F15(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2203 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2209 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2215 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2221 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2227 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2233 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2239 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2245 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2251 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2257 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2263 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2269 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2275 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2281 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2287 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2293 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2299 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2305 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2311 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2317 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2323 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2329 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2335 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2341 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2347 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2353 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2359 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2366 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2373 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2380 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2387 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2393 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2399 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2405 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2411 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2417 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2423 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2429 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2435 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2441 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2447 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2453 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15) throws Throwable
/*      */     {
/* 2458 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2464 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2470 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2476 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15)
/*      */       throws Throwable
/*      */     {
/* 2482 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15) throws Throwable
/*      */     {
/* 2487 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15 };
/* 2488 */       this.filter.invokeExact(arrayOfObject);
/* 2489 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14]);
/*      */     } } 
/*      */   static class F16 extends FilterGeneric.Adapter {
/* 2492 */     protected F16(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F16(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 2494 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 2496 */     protected F16 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F16(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2501 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2507 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2513 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2519 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2525 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2531 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2537 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2543 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2549 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2555 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2561 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2567 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2573 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2579 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2585 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15), paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2591 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2597 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2603 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2609 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2615 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2621 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2627 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2633 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2639 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2645 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2651 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2657 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2663 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2670 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2677 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2684 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2691 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2698 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2704 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2710 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2716 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2722 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2728 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2734 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2740 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2746 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2752 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2758 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2764 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16) throws Throwable
/*      */     {
/* 2769 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2775 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2781 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2787 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15, paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2793 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16)
/*      */       throws Throwable
/*      */     {
/* 2799 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16) throws Throwable
/*      */     {
/* 2804 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16 };
/* 2805 */       this.filter.invokeExact(arrayOfObject);
/* 2806 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14], arrayOfObject[15]);
/*      */     }
/*      */   }
/* 2809 */   static class F17 extends FilterGeneric.Adapter { protected F17(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F17(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 2811 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 2813 */     protected F17 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F17(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2819 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2826 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2833 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2840 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2847 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2854 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2861 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2868 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2875 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2882 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2889 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2896 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2903 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2910 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2917 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15), paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2924 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16), paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2931 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2938 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2945 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2952 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2959 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2966 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2973 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2980 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2987 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 2994 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3001 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3008 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3015 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3023 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3031 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3039 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3047 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3055 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3063 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3070 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3077 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3084 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3091 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3098 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3105 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3112 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3119 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3126 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3133 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3140 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3146 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3153 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3160 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3167 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15, paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3174 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16, paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3181 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3188 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17)
/*      */       throws Throwable
/*      */     {
/* 3194 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17 };
/* 3195 */       this.filter.invokeExact(arrayOfObject);
/* 3196 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14], arrayOfObject[15], arrayOfObject[16]);
/*      */     } } 
/*      */   static class F18 extends FilterGeneric.Adapter {
/* 3199 */     protected F18(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F18(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 3201 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 3203 */     protected F18 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F18(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3209 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3216 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3223 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3230 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3237 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3244 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3251 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3258 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3265 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3272 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3279 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3286 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3293 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3300 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3307 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15), paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3314 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16), paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3321 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17), paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3328 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3335 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3342 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3349 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3356 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3363 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3370 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3377 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3384 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3391 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3398 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3405 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3412 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3420 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3428 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3436 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3444 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3452 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3460 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3468 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3475 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3482 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3489 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3496 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3503 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3510 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3517 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3524 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3531 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3538 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3545 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3551 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3558 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3565 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3572 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15, paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3579 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16, paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3586 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17, paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3593 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3600 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18)
/*      */       throws Throwable
/*      */     {
/* 3606 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18 };
/* 3607 */       this.filter.invokeExact(arrayOfObject);
/* 3608 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14], arrayOfObject[15], arrayOfObject[16], arrayOfObject[17]);
/*      */     }
/*      */   }
/* 3611 */   static class F19 extends FilterGeneric.Adapter { protected F19(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F19(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 3613 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/* 3615 */     protected F19 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F19(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3621 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3628 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3635 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3642 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3649 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3656 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3663 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3670 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3677 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3684 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3691 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3698 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3705 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3712 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3719 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15), paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3726 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16), paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3733 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17), paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3740 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18), paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3747 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, this.filter.invokeExact(paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3754 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3761 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3768 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3775 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3782 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3789 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3796 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3803 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3810 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3817 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3824 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3831 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3839 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3847 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3855 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3863 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3871 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3879 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3887 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F19(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3895 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3902 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3909 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3916 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3923 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3930 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3937 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3944 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3951 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3958 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3965 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3972 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3978 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3985 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3992 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 3999 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15, paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4006 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16, paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4013 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17, paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4020 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18, paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4027 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, this.filter.invokeExact(paramObject19));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C19(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4034 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19)
/*      */       throws Throwable
/*      */     {
/* 4040 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19 };
/* 4041 */       this.filter.invokeExact(arrayOfObject);
/* 4042 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14], arrayOfObject[15], arrayOfObject[16], arrayOfObject[17], arrayOfObject[18]);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F2 extends FilterGeneric.Adapter
/*      */   {
/*      */     protected F2(MethodHandle paramMethodHandle)
/*      */     {
/*  529 */       super();
/*      */     }
/*  531 */     protected F2(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F2 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  533 */       return new F2(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  535 */     protected Object invoke_V0(Object paramObject1, Object paramObject2) throws Throwable { return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2); } 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2) throws Throwable {
/*  537 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2));
/*      */     }
/*  539 */     protected Object invoke_F0(Object paramObject1, Object paramObject2) throws Throwable { return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2); }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2) throws Throwable {
/*  542 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2);
/*      */     }
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2) throws Throwable {
/*  545 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2);
/*      */     }
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2) throws Throwable {
/*  548 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2));
/*      */     }
/*  550 */     protected Object invoke_C1(Object paramObject1, Object paramObject2) throws Throwable { return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2)); } 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2) throws Throwable {
/*  552 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact());
/*      */     }
/*  554 */     protected Object invoke_Y0(Object paramObject1, Object paramObject2) throws Throwable { Object[] arrayOfObject = { paramObject1, paramObject2 };
/*  555 */       this.filter.invokeExact(arrayOfObject);
/*  556 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1]);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F20 extends FilterGeneric.Adapter
/*      */   {
/*      */     protected F20(MethodHandle paramMethodHandle)
/*      */     {
/* 4045 */       super();
/*      */     }
/* 4047 */     protected F20(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F20 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/* 4049 */       return new F20(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4055 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4062 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4069 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4076 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4083 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4090 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4097 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4104 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4111 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9), paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4118 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10), paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4125 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11), paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4132 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12), paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4139 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13), paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4146 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14), paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4153 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15), paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4160 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16), paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4167 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17), paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4174 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18), paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4181 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, this.filter.invokeExact(paramObject19), paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V19(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4188 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, this.filter.invokeExact(paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4195 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4202 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4209 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4216 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4223 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4230 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4237 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4244 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4251 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4258 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4265 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4272 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4280 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4288 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4296 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4304 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4312 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4320 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4328 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F19(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4336 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F20(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4344 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4351 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4358 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4365 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4372 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4379 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4386 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4393 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4400 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4407 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4414 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact(paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C10(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4421 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, this.filter.invokeExact(paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C11(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4427 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, this.filter.invokeExact(paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C12(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4434 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, this.filter.invokeExact(paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C13(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4441 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, this.filter.invokeExact(paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C14(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4448 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, this.filter.invokeExact(paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C15(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4455 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, this.filter.invokeExact(paramObject16, paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C16(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4462 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, this.filter.invokeExact(paramObject17, paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C17(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4469 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, this.filter.invokeExact(paramObject18, paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C18(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4476 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, this.filter.invokeExact(paramObject19, paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C19(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4483 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, this.filter.invokeExact(paramObject20));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C20(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4490 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14, Object paramObject15, Object paramObject16, Object paramObject17, Object paramObject18, Object paramObject19, Object paramObject20)
/*      */       throws Throwable
/*      */     {
/* 4496 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10, paramObject11, paramObject12, paramObject13, paramObject14, paramObject15, paramObject16, paramObject17, paramObject18, paramObject19, paramObject20 };
/* 4497 */       this.filter.invokeExact(arrayOfObject);
/* 4498 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8], arrayOfObject[9], arrayOfObject[10], arrayOfObject[11], arrayOfObject[12], arrayOfObject[13], arrayOfObject[14], arrayOfObject[15], arrayOfObject[16], arrayOfObject[17], arrayOfObject[18], arrayOfObject[19]);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class F3 extends FilterGeneric.Adapter
/*      */   {
/*      */     protected F3(MethodHandle paramMethodHandle)
/*      */     {
/*  559 */       super();
/*      */     }
/*  561 */     protected F3(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F3 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  563 */       return new F3(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  565 */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3); } 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  567 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3);
/*      */     }
/*  569 */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3)); } 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  571 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3);
/*      */     }
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  574 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3);
/*      */     }
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  577 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3);
/*      */     }
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  580 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3);
/*      */     }
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  583 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3));
/*      */     }
/*  585 */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3)); } 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  587 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3));
/*      */     }
/*  589 */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact()); } 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  591 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3 };
/*  592 */       this.filter.invokeExact(arrayOfObject);
/*  593 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2]);
/*      */     }
/*      */   }
/*  596 */   static class F4 extends FilterGeneric.Adapter { protected F4(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F4(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  598 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  600 */     protected F4 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F4(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  602 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4);
/*      */     }
/*  604 */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4); } 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  606 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4);
/*      */     }
/*  608 */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4)); } 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  610 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  613 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  616 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  619 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  622 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  625 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4));
/*      */     }
/*  627 */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4)); } 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  629 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4));
/*      */     }
/*  631 */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4)); } 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  633 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact());
/*      */     }
/*  635 */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable { Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4 };
/*  636 */       this.filter.invokeExact(arrayOfObject);
/*  637 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3]); }  } 
/*      */   static class F5 extends FilterGeneric.Adapter {
/*      */     protected F5(MethodHandle paramMethodHandle) {
/*  640 */       super();
/*      */     }
/*  642 */     protected F5(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { super(paramMethodHandle2, paramMethodHandle3); } 
/*      */     protected F5 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  644 */       return new F5(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  647 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  650 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  653 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5);
/*      */     }
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  656 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5);
/*      */     }
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  659 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5));
/*      */     }
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  662 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  666 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  670 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  674 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  678 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  682 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  686 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5));
/*      */     }
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  689 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5));
/*      */     }
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  692 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5));
/*      */     }
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  695 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5));
/*      */     }
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  698 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5));
/*      */     }
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  701 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact());
/*      */     }
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  704 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 };
/*  705 */       this.filter.invokeExact(arrayOfObject);
/*  706 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4]);
/*      */     }
/*      */   }
/*  709 */   static class F6 extends FilterGeneric.Adapter { protected F6(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F6(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  711 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  713 */     protected F6 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F6(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  716 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  719 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  722 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  725 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6);
/*      */     }
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  728 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6);
/*      */     }
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  731 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6));
/*      */     }
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  734 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  738 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  742 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  746 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  750 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  754 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  758 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  762 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6));
/*      */     }
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  765 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6));
/*      */     }
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  768 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6));
/*      */     }
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  771 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6));
/*      */     }
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  774 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6));
/*      */     }
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  777 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6));
/*      */     }
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  780 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact());
/*      */     }
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  783 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 };
/*  784 */       this.filter.invokeExact(arrayOfObject);
/*  785 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5]);
/*      */     } } 
/*      */   static class F7 extends FilterGeneric.Adapter {
/*  788 */     protected F7(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F7(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  790 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  792 */     protected F7 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F7(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  795 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  798 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  801 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  804 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  807 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7);
/*      */     }
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  810 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7);
/*      */     }
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  813 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7));
/*      */     }
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  816 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  820 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  824 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  828 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  832 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  836 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  840 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  844 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  848 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  851 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  854 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  857 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  860 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  863 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7));
/*      */     }
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  866 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7));
/*      */     }
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  869 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact());
/*      */     }
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  872 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 };
/*  873 */       this.filter.invokeExact(arrayOfObject);
/*  874 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6]);
/*      */     }
/*      */   }
/*  877 */   static class F8 extends FilterGeneric.Adapter { protected F8(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F8(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  879 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  881 */     protected F8 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F8(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  884 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  887 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  890 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  893 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  896 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  899 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8);
/*      */     }
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  902 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8);
/*      */     }
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  905 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8));
/*      */     }
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  908 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  912 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  916 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  920 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  924 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  928 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  932 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  936 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  940 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  944 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  947 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  950 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  953 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  956 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  959 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  962 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8));
/*      */     }
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  965 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8));
/*      */     }
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  968 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact());
/*      */     }
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  971 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 };
/*  972 */       this.filter.invokeExact(arrayOfObject);
/*  973 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7]);
/*      */     } } 
/*      */   static class F9 extends FilterGeneric.Adapter {
/*  976 */     protected F9(MethodHandle paramMethodHandle) { super(); } 
/*      */     protected F9(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  978 */       super(paramMethodHandle2, paramMethodHandle3);
/*      */     }
/*  980 */     protected F9 makeInstance(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) { return new F9(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3); }
/*      */ 
/*      */     protected Object invoke_V0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/*  984 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/*  988 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2), paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/*  992 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3), paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/*  996 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4), paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1000 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5), paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1004 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6), paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1008 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7), paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1012 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8), paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_V8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1016 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_F0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1020 */       return this.target.invokeExact(this.filter.invokeExact(), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1025 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1030 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1035 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1040 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1045 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1050 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1055 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1060 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_F9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1065 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9), paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9);
/*      */     }
/*      */ 
/*      */     protected Object invoke_C0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable
/*      */     {
/* 1070 */       return this.target.invokeExact(this.filter.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C1(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1074 */       return this.target.invokeExact(paramObject1, this.filter.invokeExact(paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C2(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1078 */       return this.target.invokeExact(paramObject1, paramObject2, this.filter.invokeExact(paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C3(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1082 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, this.filter.invokeExact(paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1086 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, this.filter.invokeExact(paramObject5, paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1090 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, this.filter.invokeExact(paramObject6, paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1094 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, this.filter.invokeExact(paramObject7, paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1098 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, this.filter.invokeExact(paramObject8, paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1102 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, this.filter.invokeExact(paramObject9));
/*      */     }
/*      */ 
/*      */     protected Object invoke_C9(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1106 */       return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, this.filter.invokeExact());
/*      */     }
/*      */ 
/*      */     protected Object invoke_Y0(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) throws Throwable {
/* 1110 */       Object[] arrayOfObject = { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 };
/* 1111 */       this.filter.invokeExact(arrayOfObject);
/* 1112 */       return this.target.invokeExact(arrayOfObject[0], arrayOfObject[1], arrayOfObject[2], arrayOfObject[3], arrayOfObject[4], arrayOfObject[5], arrayOfObject[6], arrayOfObject[7], arrayOfObject[8]);
/*      */     }
/*      */   }
/*      */ 
/*      */   static enum Kind
/*      */   {
/*  284 */     value('V'), 
/*  285 */     fold('F'), 
/*  286 */     collect('C'), 
/*  287 */     flyby('Y'), 
/*  288 */     LIMIT('?');
/*      */ 
/*  289 */     static final int COUNT = LIMIT.ordinal();
/*      */     final char key;
/*      */ 
/*  292 */     private Kind(char paramChar) { this.key = paramChar; } 
/*  293 */     String invokerName(int paramInt) { return "invoke_" + this.key + "" + paramInt; } 
/*  294 */     int invokerIndex(int paramInt) { return paramInt * COUNT + ordinal(); }
/*      */ 
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.FilterGeneric
 * JD-Core Version:    0.6.2
 */