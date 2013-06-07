/*      */ package sun.invoke.util;
/*      */ 
/*      */ import java.lang.invoke.MethodHandle;
/*      */ import java.lang.invoke.MethodHandles;
/*      */ import java.lang.invoke.MethodHandles.Lookup;
/*      */ import java.lang.invoke.MethodType;
/*      */ import java.lang.reflect.Array;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumMap;
/*      */ import java.util.List;
/*      */ 
/*      */ public class ValueConversions
/*      */ {
/*      */   private static final Class<?> THIS_CLASS;
/*      */   private static final int MAX_ARITY;
/*      */   private static final MethodHandles.Lookup IMPL_LOOKUP;
/*      */   private static final EnumMap<Wrapper, MethodHandle>[] UNBOX_CONVERSIONS;
/*      */   private static final Integer ZERO_INT;
/*      */   private static final Integer ONE_INT;
/*      */   private static final EnumMap<Wrapper, MethodHandle>[] BOX_CONVERSIONS;
/*      */   private static final EnumMap<Wrapper, MethodHandle>[] REBOX_CONVERSIONS;
/*      */   private static final EnumMap<Wrapper, MethodHandle>[] CONSTANT_FUNCTIONS;
/*      */   private static final MethodHandle IDENTITY;
/*      */   private static final MethodHandle IDENTITY_I;
/*      */   private static final MethodHandle IDENTITY_J;
/*      */   private static final MethodHandle CAST_REFERENCE;
/*      */   private static final MethodHandle ALWAYS_NULL;
/*      */   private static final MethodHandle ALWAYS_ZERO;
/*      */   private static final MethodHandle ZERO_OBJECT;
/*      */   private static final MethodHandle IGNORE;
/*      */   private static final MethodHandle EMPTY;
/*      */   private static final MethodHandle NEW_ARRAY;
/*  703 */   private static final EnumMap<Wrapper, MethodHandle>[] WRAPPER_CASTS = newWrapperCaches(2);
/*      */ 
/*  875 */   private static final EnumMap<Wrapper, MethodHandle>[] CONVERT_FLOAT_FUNCTIONS = newWrapperCaches(4);
/*      */ 
/*  980 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*      */ 
/* 1021 */   private static final MethodHandle[] ARRAYS = makeArrays();
/*      */ 
/* 1068 */   private static final MethodHandle[] FILL_ARRAYS = makeFillArrays();
/*      */ 
/* 1107 */   private static final MethodHandle[] FILLERS = new MethodHandle[MAX_ARITY + 1];
/*      */ 
/* 1143 */   private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS = new ClassValue()
/*      */   {
/*      */     protected MethodHandle[] computeValue(Class<?> paramAnonymousClass) {
/* 1146 */       return new MethodHandle[256];
/*      */     }
/* 1143 */   };
/*      */ 
/* 1181 */   private static final List<Object> NO_ARGS_LIST = Arrays.asList(NO_ARGS_ARRAY);
/*      */ 
/* 1222 */   private static final MethodHandle[] LISTS = makeLists();
/*      */ 
/*      */   private static EnumMap<Wrapper, MethodHandle>[] newWrapperCaches(int paramInt)
/*      */   {
/*   59 */     EnumMap[] arrayOfEnumMap = (EnumMap[])new EnumMap[paramInt];
/*      */ 
/*   61 */     for (int i = 0; i < paramInt; i++)
/*   62 */       arrayOfEnumMap[i] = new EnumMap(Wrapper.class);
/*   63 */     return arrayOfEnumMap;
/*      */   }
/*      */ 
/*      */   static int unboxInteger(Object paramObject, boolean paramBoolean)
/*      */   {
/*   74 */     if ((paramObject instanceof Integer))
/*   75 */       return ((Integer)paramObject).intValue();
/*   76 */     return primitiveConversion(Wrapper.INT, paramObject, paramBoolean).intValue();
/*      */   }
/*      */ 
/*      */   static byte unboxByte(Object paramObject, boolean paramBoolean) {
/*   80 */     if ((paramObject instanceof Byte))
/*   81 */       return ((Byte)paramObject).byteValue();
/*   82 */     return primitiveConversion(Wrapper.BYTE, paramObject, paramBoolean).byteValue();
/*      */   }
/*      */ 
/*      */   static short unboxShort(Object paramObject, boolean paramBoolean) {
/*   86 */     if ((paramObject instanceof Short))
/*   87 */       return ((Short)paramObject).shortValue();
/*   88 */     return primitiveConversion(Wrapper.SHORT, paramObject, paramBoolean).shortValue();
/*      */   }
/*      */ 
/*      */   static boolean unboxBoolean(Object paramObject, boolean paramBoolean) {
/*   92 */     if ((paramObject instanceof Boolean))
/*   93 */       return ((Boolean)paramObject).booleanValue();
/*   94 */     return (primitiveConversion(Wrapper.BOOLEAN, paramObject, paramBoolean).intValue() & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   static char unboxCharacter(Object paramObject, boolean paramBoolean) {
/*   98 */     if ((paramObject instanceof Character))
/*   99 */       return ((Character)paramObject).charValue();
/*  100 */     return (char)primitiveConversion(Wrapper.CHAR, paramObject, paramBoolean).intValue();
/*      */   }
/*      */ 
/*      */   static long unboxLong(Object paramObject, boolean paramBoolean) {
/*  104 */     if ((paramObject instanceof Long))
/*  105 */       return ((Long)paramObject).longValue();
/*  106 */     return primitiveConversion(Wrapper.LONG, paramObject, paramBoolean).longValue();
/*      */   }
/*      */ 
/*      */   static float unboxFloat(Object paramObject, boolean paramBoolean) {
/*  110 */     if ((paramObject instanceof Float))
/*  111 */       return ((Float)paramObject).floatValue();
/*  112 */     return primitiveConversion(Wrapper.FLOAT, paramObject, paramBoolean).floatValue();
/*      */   }
/*      */ 
/*      */   static double unboxDouble(Object paramObject, boolean paramBoolean) {
/*  116 */     if ((paramObject instanceof Double))
/*  117 */       return ((Double)paramObject).doubleValue();
/*  118 */     return primitiveConversion(Wrapper.DOUBLE, paramObject, paramBoolean).doubleValue();
/*      */   }
/*      */ 
/*      */   static int unboxByteRaw(Object paramObject, boolean paramBoolean)
/*      */   {
/*  125 */     return unboxByte(paramObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   static int unboxShortRaw(Object paramObject, boolean paramBoolean) {
/*  129 */     return unboxShort(paramObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   static int unboxBooleanRaw(Object paramObject, boolean paramBoolean) {
/*  133 */     return unboxBoolean(paramObject, paramBoolean) ? 1 : 0;
/*      */   }
/*      */ 
/*      */   static int unboxCharacterRaw(Object paramObject, boolean paramBoolean) {
/*  137 */     return unboxCharacter(paramObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   static int unboxFloatRaw(Object paramObject, boolean paramBoolean) {
/*  141 */     return Float.floatToIntBits(unboxFloat(paramObject, paramBoolean));
/*      */   }
/*      */ 
/*      */   static long unboxDoubleRaw(Object paramObject, boolean paramBoolean) {
/*  145 */     return Double.doubleToRawLongBits(unboxDouble(paramObject, paramBoolean));
/*      */   }
/*      */ 
/*      */   private static MethodType unboxType(Wrapper paramWrapper, boolean paramBoolean) {
/*  149 */     return MethodType.methodType(rawWrapper(paramWrapper, paramBoolean).primitiveType(), Object.class, new Class[] { Boolean.TYPE });
/*      */   }
/*      */ 
/*      */   private static MethodHandle unbox(Wrapper paramWrapper, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  156 */     EnumMap localEnumMap = UNBOX_CONVERSIONS[(0 + 0)];
/*  157 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  158 */     if (localMethodHandle != null) {
/*  159 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  162 */     switch (3.$SwitchMap$sun$invoke$util$Wrapper[paramWrapper.ordinal()]) {
/*      */     case 1:
/*  164 */       localMethodHandle = IDENTITY; break;
/*      */     case 2:
/*  166 */       localMethodHandle = paramBoolean1 ? ALWAYS_ZERO : IGNORE; break;
/*      */     case 3:
/*      */     case 4:
/*  169 */       if (paramBoolean1) localMethodHandle = unbox(paramWrapper, false, paramBoolean2);
/*      */       break;
/*      */     }
/*  172 */     if (localMethodHandle != null) {
/*  173 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  174 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  177 */     String str = "unbox" + paramWrapper.simpleName() + (paramBoolean1 ? "Raw" : "");
/*  178 */     MethodType localMethodType = unboxType(paramWrapper, paramBoolean1);
/*      */     try {
/*  180 */       localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
/*      */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  182 */       localMethodHandle = null;
/*      */     }
/*  184 */     if (localMethodHandle != null) {
/*  185 */       localMethodHandle = MethodHandles.insertArguments(localMethodHandle, 1, new Object[] { Boolean.valueOf(paramBoolean2) });
/*  186 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  187 */       return localMethodHandle;
/*      */     }
/*  189 */     throw new IllegalArgumentException("cannot find unbox adapter for " + paramWrapper + (paramBoolean2 ? " (cast)" : "") + (paramBoolean1 ? " (raw)" : ""));
/*      */   }
/*      */ 
/*      */   public static MethodHandle unboxCast(Wrapper paramWrapper)
/*      */   {
/*  194 */     return unbox(paramWrapper, false, true);
/*      */   }
/*      */ 
/*      */   public static MethodHandle unboxRaw(Wrapper paramWrapper) {
/*  198 */     return unbox(paramWrapper, true, false);
/*      */   }
/*      */ 
/*      */   public static MethodHandle unbox(Class<?> paramClass) {
/*  202 */     return unbox(Wrapper.forPrimitiveType(paramClass), false, false);
/*      */   }
/*      */ 
/*      */   public static MethodHandle unboxCast(Class<?> paramClass) {
/*  206 */     return unbox(Wrapper.forPrimitiveType(paramClass), false, true);
/*      */   }
/*      */ 
/*      */   public static MethodHandle unboxRaw(Class<?> paramClass) {
/*  210 */     return unbox(Wrapper.forPrimitiveType(paramClass), true, false);
/*      */   }
/*      */ 
/*      */   public static Number primitiveConversion(Wrapper paramWrapper, Object paramObject, boolean paramBoolean)
/*      */   {
/*  218 */     Object localObject = null;
/*  219 */     if (paramObject == null) {
/*  220 */       if (!paramBoolean) return null;
/*  221 */       return ZERO_INT;
/*      */     }
/*  223 */     if ((paramObject instanceof Number))
/*  224 */       localObject = (Number)paramObject;
/*  225 */     else if ((paramObject instanceof Boolean))
/*  226 */       localObject = ((Boolean)paramObject).booleanValue() ? ONE_INT : ZERO_INT;
/*  227 */     else if ((paramObject instanceof Character)) {
/*  228 */       localObject = Integer.valueOf(((Character)paramObject).charValue());
/*      */     }
/*      */     else {
/*  231 */       localObject = (Number)paramObject;
/*      */     }
/*  233 */     Wrapper localWrapper = Wrapper.findWrapperType(paramObject.getClass());
/*  234 */     if ((localWrapper == null) || ((!paramBoolean) && (!paramWrapper.isConvertibleFrom(localWrapper))))
/*      */     {
/*  236 */       return (Number)paramWrapper.wrapperType().cast(paramObject);
/*  237 */     }return localObject;
/*      */   }
/*      */ 
/*      */   static Integer boxInteger(int paramInt)
/*      */   {
/*  243 */     return Integer.valueOf(paramInt);
/*      */   }
/*      */ 
/*      */   static Byte boxByte(byte paramByte) {
/*  247 */     return Byte.valueOf(paramByte);
/*      */   }
/*      */ 
/*      */   static Short boxShort(short paramShort) {
/*  251 */     return Short.valueOf(paramShort);
/*      */   }
/*      */ 
/*      */   static Boolean boxBoolean(boolean paramBoolean) {
/*  255 */     return Boolean.valueOf(paramBoolean);
/*      */   }
/*      */ 
/*      */   static Character boxCharacter(char paramChar) {
/*  259 */     return Character.valueOf(paramChar);
/*      */   }
/*      */ 
/*      */   static Long boxLong(long paramLong) {
/*  263 */     return Long.valueOf(paramLong);
/*      */   }
/*      */ 
/*      */   static Float boxFloat(float paramFloat) {
/*  267 */     return Float.valueOf(paramFloat);
/*      */   }
/*      */ 
/*      */   static Double boxDouble(double paramDouble) {
/*  271 */     return Double.valueOf(paramDouble);
/*      */   }
/*      */ 
/*      */   static Byte boxByteRaw(int paramInt)
/*      */   {
/*  277 */     return boxByte((byte)paramInt);
/*      */   }
/*      */ 
/*      */   static Short boxShortRaw(int paramInt) {
/*  281 */     return boxShort((short)paramInt);
/*      */   }
/*      */ 
/*      */   static Boolean boxBooleanRaw(int paramInt) {
/*  285 */     return boxBoolean(paramInt != 0);
/*      */   }
/*      */ 
/*      */   static Character boxCharacterRaw(int paramInt) {
/*  289 */     return boxCharacter((char)paramInt);
/*      */   }
/*      */ 
/*      */   static Float boxFloatRaw(int paramInt) {
/*  293 */     return boxFloat(Float.intBitsToFloat(paramInt));
/*      */   }
/*      */ 
/*      */   static Double boxDoubleRaw(long paramLong) {
/*  297 */     return boxDouble(Double.longBitsToDouble(paramLong));
/*      */   }
/*      */ 
/*      */   static Void boxVoidRaw(int paramInt)
/*      */   {
/*  302 */     return null;
/*      */   }
/*      */ 
/*      */   private static MethodType boxType(Wrapper paramWrapper, boolean paramBoolean)
/*      */   {
/*  307 */     Class localClass = paramWrapper.wrapperType();
/*  308 */     return MethodType.methodType(localClass, rawWrapper(paramWrapper, paramBoolean).primitiveType());
/*      */   }
/*      */ 
/*      */   private static Wrapper rawWrapper(Wrapper paramWrapper, boolean paramBoolean) {
/*  312 */     if (paramBoolean) return paramWrapper.isDoubleWord() ? Wrapper.LONG : Wrapper.INT;
/*  313 */     return paramWrapper;
/*      */   }
/*      */ 
/*      */   private static MethodHandle box(Wrapper paramWrapper, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  320 */     EnumMap localEnumMap = BOX_CONVERSIONS[(0 + 0)];
/*  321 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  322 */     if (localMethodHandle != null) {
/*  323 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  326 */     switch (3.$SwitchMap$sun$invoke$util$Wrapper[paramWrapper.ordinal()]) {
/*      */     case 1:
/*  328 */       localMethodHandle = IDENTITY; break;
/*      */     case 2:
/*  330 */       if (!paramBoolean2) localMethodHandle = ZERO_OBJECT; break;
/*      */     case 3:
/*      */     case 4:
/*  334 */       if (paramBoolean2) localMethodHandle = box(paramWrapper, paramBoolean1, false);
/*      */       break;
/*      */     }
/*  337 */     if (localMethodHandle != null) {
/*  338 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  339 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  342 */     String str = "box" + paramWrapper.simpleName() + (paramBoolean2 ? "Raw" : "");
/*  343 */     MethodType localMethodType = boxType(paramWrapper, paramBoolean2);
/*  344 */     if (paramBoolean1)
/*      */       try {
/*  346 */         localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  348 */         localMethodHandle = null;
/*      */       }
/*      */     else {
/*  351 */       localMethodHandle = box(paramWrapper, !paramBoolean1, paramBoolean2).asType(localMethodType.erase());
/*      */     }
/*  353 */     if (localMethodHandle != null) {
/*  354 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  355 */       return localMethodHandle;
/*      */     }
/*  357 */     throw new IllegalArgumentException("cannot find box adapter for " + paramWrapper + (paramBoolean1 ? " (exact)" : "") + (paramBoolean2 ? " (raw)" : ""));
/*      */   }
/*      */ 
/*      */   public static MethodHandle box(Class<?> paramClass)
/*      */   {
/*  362 */     boolean bool = false;
/*      */ 
/*  365 */     return box(Wrapper.forPrimitiveType(paramClass), bool, false);
/*      */   }
/*      */ 
/*      */   public static MethodHandle boxRaw(Class<?> paramClass) {
/*  369 */     boolean bool = false;
/*      */ 
/*  372 */     return box(Wrapper.forPrimitiveType(paramClass), bool, true);
/*      */   }
/*      */ 
/*      */   public static MethodHandle box(Wrapper paramWrapper) {
/*  376 */     boolean bool = false;
/*  377 */     return box(paramWrapper, bool, false);
/*      */   }
/*      */ 
/*      */   public static MethodHandle boxRaw(Wrapper paramWrapper) {
/*  381 */     boolean bool = false;
/*  382 */     return box(paramWrapper, bool, true);
/*      */   }
/*      */ 
/*      */   static int unboxRawInteger(Object paramObject)
/*      */   {
/*  388 */     if ((paramObject instanceof Integer)) {
/*  389 */       return ((Integer)paramObject).intValue();
/*      */     }
/*  391 */     return (int)unboxLong(paramObject, false);
/*      */   }
/*      */ 
/*      */   static Integer reboxRawInteger(Object paramObject) {
/*  395 */     if ((paramObject instanceof Integer)) {
/*  396 */       return (Integer)paramObject;
/*      */     }
/*  398 */     return Integer.valueOf((int)unboxLong(paramObject, false));
/*      */   }
/*      */ 
/*      */   static Byte reboxRawByte(Object paramObject) {
/*  402 */     if ((paramObject instanceof Byte)) return (Byte)paramObject;
/*  403 */     return boxByteRaw(unboxRawInteger(paramObject));
/*      */   }
/*      */ 
/*      */   static Short reboxRawShort(Object paramObject) {
/*  407 */     if ((paramObject instanceof Short)) return (Short)paramObject;
/*  408 */     return boxShortRaw(unboxRawInteger(paramObject));
/*      */   }
/*      */ 
/*      */   static Boolean reboxRawBoolean(Object paramObject) {
/*  412 */     if ((paramObject instanceof Boolean)) return (Boolean)paramObject;
/*  413 */     return boxBooleanRaw(unboxRawInteger(paramObject));
/*      */   }
/*      */ 
/*      */   static Character reboxRawCharacter(Object paramObject) {
/*  417 */     if ((paramObject instanceof Character)) return (Character)paramObject;
/*  418 */     return boxCharacterRaw(unboxRawInteger(paramObject));
/*      */   }
/*      */ 
/*      */   static Float reboxRawFloat(Object paramObject) {
/*  422 */     if ((paramObject instanceof Float)) return (Float)paramObject;
/*  423 */     return boxFloatRaw(unboxRawInteger(paramObject));
/*      */   }
/*      */ 
/*      */   static Long reboxRawLong(Object paramObject) {
/*  427 */     return (Long)paramObject;
/*      */   }
/*      */ 
/*      */   static Double reboxRawDouble(Object paramObject) {
/*  431 */     if ((paramObject instanceof Double)) return (Double)paramObject;
/*  432 */     return boxDoubleRaw(unboxLong(paramObject, true));
/*      */   }
/*      */ 
/*      */   private static MethodType reboxType(Wrapper paramWrapper) {
/*  436 */     Class localClass = paramWrapper.wrapperType();
/*  437 */     return MethodType.methodType(localClass, Object.class);
/*      */   }
/*      */ 
/*      */   public static MethodHandle rebox(Wrapper paramWrapper)
/*      */   {
/*  453 */     EnumMap localEnumMap = REBOX_CONVERSIONS[0];
/*  454 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  455 */     if (localMethodHandle != null) {
/*  456 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  459 */     switch (3.$SwitchMap$sun$invoke$util$Wrapper[paramWrapper.ordinal()]) {
/*      */     case 1:
/*  461 */       localMethodHandle = IDENTITY; break;
/*      */     case 2:
/*  463 */       throw new IllegalArgumentException("cannot rebox a void");
/*      */     }
/*  465 */     if (localMethodHandle != null) {
/*  466 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  467 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  470 */     String str = "reboxRaw" + paramWrapper.simpleName();
/*  471 */     MethodType localMethodType = reboxType(paramWrapper);
/*      */     try {
/*  473 */       localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, str, localMethodType);
/*  474 */       localMethodHandle = localMethodHandle.asType(IDENTITY.type());
/*      */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  476 */       localMethodHandle = null;
/*      */     }
/*  478 */     if (localMethodHandle != null) {
/*  479 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  480 */       return localMethodHandle;
/*      */     }
/*  482 */     throw new IllegalArgumentException("cannot find rebox adapter for " + paramWrapper);
/*      */   }
/*      */ 
/*      */   public static MethodHandle rebox(Class<?> paramClass) {
/*  486 */     return rebox(Wrapper.forPrimitiveType(paramClass));
/*      */   }
/*      */ 
/*      */   static long widenInt(int paramInt)
/*      */   {
/*  492 */     return paramInt;
/*      */   }
/*      */ 
/*      */   static Long widenBoxedInt(Integer paramInteger) {
/*  496 */     return Long.valueOf(paramInteger.intValue());
/*      */   }
/*      */ 
/*      */   static int narrowLong(long paramLong) {
/*  500 */     return (int)paramLong;
/*      */   }
/*      */ 
/*      */   static Integer narrowBoxedLong(Long paramLong) {
/*  504 */     return Integer.valueOf((int)paramLong.longValue());
/*      */   }
/*      */ 
/*      */   static void ignore(Object paramObject)
/*      */   {
/*      */   }
/*      */ 
/*      */   static void empty()
/*      */   {
/*      */   }
/*      */ 
/*      */   static Object zeroObject()
/*      */   {
/*  519 */     return null;
/*      */   }
/*      */ 
/*      */   static int zeroInteger() {
/*  523 */     return 0;
/*      */   }
/*      */ 
/*      */   static long zeroLong() {
/*  527 */     return 0L;
/*      */   }
/*      */ 
/*      */   static float zeroFloat() {
/*  531 */     return 0.0F;
/*      */   }
/*      */ 
/*      */   static double zeroDouble() {
/*  535 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public static MethodHandle zeroConstantFunction(Wrapper paramWrapper)
/*      */   {
/*  542 */     EnumMap localEnumMap = CONSTANT_FUNCTIONS[0];
/*  543 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  544 */     if (localMethodHandle != null) {
/*  545 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  548 */     MethodType localMethodType = MethodType.methodType(paramWrapper.primitiveType());
/*  549 */     switch (3.$SwitchMap$sun$invoke$util$Wrapper[paramWrapper.ordinal()]) {
/*      */     case 2:
/*  551 */       localMethodHandle = EMPTY;
/*  552 */       break;
/*      */     case 1:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */       try { localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, "zero" + paramWrapper.simpleName(), localMethodType);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  558 */         localMethodHandle = null;
/*      */       }
/*      */     }
/*      */ 
/*  562 */     if (localMethodHandle != null) {
/*  563 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  564 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  568 */     Wrapper localWrapper = paramWrapper.rawPrimitive();
/*  569 */     if ((localMethodHandle == null) && (localWrapper != paramWrapper)) {
/*  570 */       localMethodHandle = MethodHandles.explicitCastArguments(zeroConstantFunction(localWrapper), localMethodType);
/*      */     }
/*  572 */     if (localMethodHandle != null) {
/*  573 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  574 */       return localMethodHandle;
/*      */     }
/*  576 */     throw new IllegalArgumentException("cannot find zero constant for " + paramWrapper);
/*      */   }
/*      */ 
/*      */   static Object alwaysNull(Object paramObject)
/*      */   {
/*  587 */     return null;
/*      */   }
/*      */ 
/*      */   static int alwaysZero(Object paramObject)
/*      */   {
/*  596 */     return 0;
/*      */   }
/*      */ 
/*      */   static <T> T identity(T paramT)
/*      */   {
/*  605 */     return paramT;
/*      */   }
/*      */ 
/*      */   static int identity(int paramInt)
/*      */   {
/*  614 */     return paramInt;
/*      */   }
/*      */ 
/*      */   static byte identity(byte paramByte) {
/*  618 */     return paramByte;
/*      */   }
/*      */ 
/*      */   static short identity(short paramShort) {
/*  622 */     return paramShort;
/*      */   }
/*      */ 
/*      */   static boolean identity(boolean paramBoolean) {
/*  626 */     return paramBoolean;
/*      */   }
/*      */ 
/*      */   static char identity(char paramChar) {
/*  630 */     return paramChar;
/*      */   }
/*      */ 
/*      */   static long identity(long paramLong)
/*      */   {
/*  639 */     return paramLong;
/*      */   }
/*      */ 
/*      */   static float identity(float paramFloat) {
/*  643 */     return paramFloat;
/*      */   }
/*      */ 
/*      */   static double identity(double paramDouble) {
/*  647 */     return paramDouble;
/*      */   }
/*      */ 
/*      */   static <T, U> T castReference(Class<? extends T> paramClass, U paramU)
/*      */   {
/*  657 */     return paramClass.cast(paramU);
/*      */   }
/*      */ 
/*      */   public static MethodHandle cast(Class<?> paramClass)
/*      */   {
/*  710 */     int i = 0;
/*  711 */     if (paramClass.isPrimitive()) throw new IllegalArgumentException("cannot cast primitive type " + paramClass);
/*  712 */     MethodHandle localMethodHandle = null;
/*  713 */     Wrapper localWrapper = null;
/*  714 */     EnumMap localEnumMap = null;
/*  715 */     if (Wrapper.isWrapperType(paramClass)) {
/*  716 */       localWrapper = Wrapper.forWrapperType(paramClass);
/*  717 */       localEnumMap = WRAPPER_CASTS[0];
/*  718 */       localMethodHandle = (MethodHandle)localEnumMap.get(localWrapper);
/*  719 */       if (localMethodHandle != null) return localMethodHandle;
/*      */     }
/*  721 */     if (VerifyType.isNullReferenceConversion(Object.class, paramClass))
/*  722 */       localMethodHandle = IDENTITY;
/*  723 */     else if (VerifyType.isNullType(paramClass))
/*  724 */       localMethodHandle = ALWAYS_NULL;
/*      */     else
/*  726 */       localMethodHandle = MethodHandles.insertArguments(CAST_REFERENCE, 0, new Object[] { paramClass });
/*  727 */     if (i != 0) {
/*  728 */       MethodType localMethodType = MethodType.methodType(paramClass, Object.class);
/*  729 */       localMethodHandle = MethodHandles.explicitCastArguments(localMethodHandle, localMethodType);
/*      */     }
/*      */ 
/*  732 */     if (localEnumMap != null)
/*  733 */       localEnumMap.put(localWrapper, localMethodHandle);
/*  734 */     return localMethodHandle;
/*      */   }
/*      */ 
/*      */   public static MethodHandle identity() {
/*  738 */     return IDENTITY;
/*      */   }
/*      */ 
/*      */   public static MethodHandle identity(Class<?> paramClass)
/*      */   {
/*  743 */     return MethodHandles.identity(paramClass);
/*      */   }
/*      */ 
/*      */   public static MethodHandle identity(Wrapper paramWrapper) {
/*  747 */     EnumMap localEnumMap = CONSTANT_FUNCTIONS[1];
/*  748 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  749 */     if (localMethodHandle != null) {
/*  750 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  753 */     MethodType localMethodType = MethodType.methodType(paramWrapper.primitiveType());
/*  754 */     if (paramWrapper != Wrapper.VOID)
/*  755 */       localMethodType = localMethodType.appendParameterTypes(new Class[] { paramWrapper.primitiveType() });
/*      */     try {
/*  757 */       localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, "identity", localMethodType);
/*      */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  759 */       localMethodHandle = null;
/*      */     }
/*  761 */     if ((localMethodHandle == null) && (paramWrapper == Wrapper.VOID)) {
/*  762 */       localMethodHandle = EMPTY;
/*      */     }
/*  764 */     if (localMethodHandle != null) {
/*  765 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  766 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  769 */     if (localMethodHandle != null) {
/*  770 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  771 */       return localMethodHandle;
/*      */     }
/*  773 */     throw new IllegalArgumentException("cannot find identity for " + paramWrapper);
/*      */   }
/*      */ 
/*      */   static float doubleToFloat(double paramDouble)
/*      */   {
/*  779 */     return (float)paramDouble;
/*      */   }
/*      */   static double floatToDouble(float paramFloat) {
/*  782 */     return paramFloat;
/*      */   }
/*      */ 
/*      */   static long doubleToLong(double paramDouble)
/*      */   {
/*  787 */     return ()paramDouble;
/*      */   }
/*      */   static int doubleToInt(double paramDouble) {
/*  790 */     return (int)paramDouble;
/*      */   }
/*      */   static short doubleToShort(double paramDouble) {
/*  793 */     return (short)(int)paramDouble;
/*      */   }
/*      */   static char doubleToChar(double paramDouble) {
/*  796 */     return (char)(int)paramDouble;
/*      */   }
/*      */   static byte doubleToByte(double paramDouble) {
/*  799 */     return (byte)(int)paramDouble;
/*      */   }
/*      */   static boolean doubleToBoolean(double paramDouble) {
/*  802 */     return toBoolean((byte)(int)paramDouble);
/*      */   }
/*      */ 
/*      */   static long floatToLong(float paramFloat)
/*      */   {
/*  807 */     return ()paramFloat;
/*      */   }
/*      */   static int floatToInt(float paramFloat) {
/*  810 */     return (int)paramFloat;
/*      */   }
/*      */   static short floatToShort(float paramFloat) {
/*  813 */     return (short)(int)paramFloat;
/*      */   }
/*      */   static char floatToChar(float paramFloat) {
/*  816 */     return (char)(int)paramFloat;
/*      */   }
/*      */   static byte floatToByte(float paramFloat) {
/*  819 */     return (byte)(int)paramFloat;
/*      */   }
/*      */   static boolean floatToBoolean(float paramFloat) {
/*  822 */     return toBoolean((byte)(int)paramFloat);
/*      */   }
/*      */ 
/*      */   static double longToDouble(long paramLong)
/*      */   {
/*  827 */     return paramLong;
/*      */   }
/*      */   static double intToDouble(int paramInt) {
/*  830 */     return paramInt;
/*      */   }
/*      */   static double shortToDouble(short paramShort) {
/*  833 */     return paramShort;
/*      */   }
/*      */   static double charToDouble(char paramChar) {
/*  836 */     return paramChar;
/*      */   }
/*      */   static double byteToDouble(byte paramByte) {
/*  839 */     return paramByte;
/*      */   }
/*      */   static double booleanToDouble(boolean paramBoolean) {
/*  842 */     return fromBoolean(paramBoolean);
/*      */   }
/*      */ 
/*      */   static float longToFloat(long paramLong)
/*      */   {
/*  847 */     return (float)paramLong;
/*      */   }
/*      */   static float intToFloat(int paramInt) {
/*  850 */     return paramInt;
/*      */   }
/*      */   static float shortToFloat(short paramShort) {
/*  853 */     return paramShort;
/*      */   }
/*      */   static float charToFloat(char paramChar) {
/*  856 */     return paramChar;
/*      */   }
/*      */   static float byteToFloat(byte paramByte) {
/*  859 */     return paramByte;
/*      */   }
/*      */   static float booleanToFloat(boolean paramBoolean) {
/*  862 */     return fromBoolean(paramBoolean);
/*      */   }
/*      */ 
/*      */   static boolean toBoolean(byte paramByte)
/*      */   {
/*  867 */     return (paramByte & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   static byte fromBoolean(boolean paramBoolean) {
/*  871 */     return paramBoolean ? 1 : 0;
/*      */   }
/*      */ 
/*      */   static MethodHandle convertFloatFunction(Wrapper paramWrapper, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  878 */     EnumMap localEnumMap = CONVERT_FLOAT_FUNCTIONS[(0 + 0)];
/*  879 */     MethodHandle localMethodHandle = (MethodHandle)localEnumMap.get(paramWrapper);
/*  880 */     if (localMethodHandle != null) {
/*  881 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  884 */     Wrapper localWrapper = paramBoolean2 ? Wrapper.DOUBLE : Wrapper.FLOAT;
/*  885 */     Class localClass1 = paramWrapper.primitiveType();
/*  886 */     Class localClass2 = paramBoolean2 ? Double.TYPE : Float.TYPE;
/*  887 */     Class localClass3 = paramBoolean1 ? localClass1 : localClass2;
/*  888 */     Class localClass4 = paramBoolean1 ? localClass2 : localClass1;
/*  889 */     if (localClass3 == localClass4) return identity(paramWrapper);
/*  890 */     MethodType localMethodType = MethodType.methodType(localClass4, localClass3);
/*  891 */     switch (3.$SwitchMap$sun$invoke$util$Wrapper[paramWrapper.ordinal()]) {
/*      */     case 2:
/*  893 */       localMethodHandle = paramBoolean1 ? zeroConstantFunction(localWrapper) : MethodHandles.dropArguments(EMPTY, 0, new Class[] { localClass2 });
/*  894 */       break;
/*      */     case 1:
/*  896 */       localMethodHandle = paramBoolean1 ? unbox(localClass2) : box(localClass2);
/*  897 */       break;
/*      */     default:
/*      */       try {
/*  900 */         localMethodHandle = IMPL_LOOKUP.findStatic(THIS_CLASS, localClass3.getSimpleName() + "To" + capitalize(localClass4.getSimpleName()), localMethodType);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  902 */         localMethodHandle = null;
/*      */       }
/*      */     }
/*      */ 
/*  906 */     if (localMethodHandle != null) {
/*  907 */       assert (localMethodHandle.type() == localMethodType) : localMethodHandle;
/*  908 */       localEnumMap.put(paramWrapper, localMethodHandle);
/*  909 */       return localMethodHandle;
/*      */     }
/*      */ 
/*  912 */     throw new IllegalArgumentException("cannot find float conversion constant for " + localClass3.getSimpleName() + " -> " + localClass4.getSimpleName());
/*      */   }
/*      */ 
/*      */   public static MethodHandle convertFromFloat(Class<?> paramClass)
/*      */   {
/*  917 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
/*  918 */     return convertFloatFunction(localWrapper, false, false);
/*      */   }
/*      */   public static MethodHandle convertFromDouble(Class<?> paramClass) {
/*  921 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
/*  922 */     return convertFloatFunction(localWrapper, false, true);
/*      */   }
/*      */   public static MethodHandle convertToFloat(Class<?> paramClass) {
/*  925 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
/*  926 */     return convertFloatFunction(localWrapper, true, false);
/*      */   }
/*      */   public static MethodHandle convertToDouble(Class<?> paramClass) {
/*  929 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
/*  930 */     return convertFloatFunction(localWrapper, true, true);
/*      */   }
/*      */ 
/*      */   private static String capitalize(String paramString) {
/*  934 */     return Character.toUpperCase(paramString.charAt(0)) + paramString.substring(1);
/*      */   }
/*      */ 
/*      */   public static Object convertArrayElements(Class<?> paramClass, Object paramObject)
/*      */   {
/*  940 */     Class localClass1 = paramObject.getClass().getComponentType();
/*  941 */     Class localClass2 = paramClass.getComponentType();
/*  942 */     if ((localClass1 == null) || (localClass2 == null)) throw new IllegalArgumentException("not array type");
/*  943 */     Object localObject1 = localClass1.isPrimitive() ? Wrapper.forPrimitiveType(localClass1) : null;
/*  944 */     Object localObject2 = localClass2.isPrimitive() ? Wrapper.forPrimitiveType(localClass2) : null;
/*      */     Object[] arrayOfObject;
/*  946 */     if (localObject1 == null) {
/*  947 */       arrayOfObject = (Object[])paramObject;
/*  948 */       i = arrayOfObject.length;
/*  949 */       if (localObject2 == null)
/*  950 */         return Arrays.copyOf(arrayOfObject, i, paramClass.asSubclass([Ljava.lang.Object.class));
/*  951 */       localObject3 = localObject2.makeArray(i);
/*  952 */       localObject2.copyArrayUnboxing(arrayOfObject, 0, localObject3, 0, i);
/*  953 */       return localObject3;
/*      */     }
/*  955 */     int i = Array.getLength(paramObject);
/*      */ 
/*  957 */     if (localObject2 == null)
/*  958 */       arrayOfObject = Arrays.copyOf(NO_ARGS_ARRAY, i, paramClass.asSubclass([Ljava.lang.Object.class));
/*      */     else {
/*  960 */       arrayOfObject = new Object[i];
/*      */     }
/*  962 */     localObject1.copyArrayBoxing(paramObject, 0, arrayOfObject, 0, i);
/*  963 */     if (localObject2 == null) return arrayOfObject;
/*  964 */     Object localObject3 = localObject2.makeArray(i);
/*  965 */     localObject2.copyArrayUnboxing(arrayOfObject, 0, localObject3, 0, i);
/*  966 */     return localObject3;
/*      */   }
/*      */ 
/*      */   private static MethodHandle findCollector(String paramString, int paramInt, Class<?> paramClass, Class<?>[] paramArrayOfClass) {
/*  970 */     MethodType localMethodType = MethodType.genericMethodType(paramInt).changeReturnType(paramClass).insertParameterTypes(0, paramArrayOfClass);
/*      */     try
/*      */     {
/*  974 */       return IMPL_LOOKUP.findStatic(THIS_CLASS, paramString, localMethodType); } catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */     }
/*  976 */     return null;
/*      */   }
/*      */ 
/*      */   private static Object[] makeArray(Object[] paramArrayOfObject)
/*      */   {
/*  981 */     return paramArrayOfObject; } 
/*  982 */   private static Object[] array() { return NO_ARGS_ARRAY; } 
/*      */   private static Object[] array(Object paramObject) {
/*  984 */     return makeArray(new Object[] { paramObject });
/*      */   }
/*  986 */   private static Object[] array(Object paramObject1, Object paramObject2) { return makeArray(new Object[] { paramObject1, paramObject2 }); } 
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3) {
/*  988 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3 });
/*      */   }
/*  990 */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 }); }
/*      */ 
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
/*  993 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
/*      */   }
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
/*  996 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
/*      */   }
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
/*  999 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
/*      */   }
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
/* 1002 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
/*      */   }
/*      */ 
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
/* 1006 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
/*      */   }
/*      */ 
/*      */   private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
/* 1010 */     return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
/*      */   }
/* 1012 */   private static MethodHandle[] makeArrays() { ArrayList localArrayList = new ArrayList();
/*      */     while (true) {
/* 1014 */       MethodHandle localMethodHandle = findCollector("array", localArrayList.size(), [Ljava.lang.Object.class, new Class[0]);
/* 1015 */       if (localMethodHandle == null) break;
/* 1016 */       localArrayList.add(localMethodHandle);
/*      */     }
/* 1018 */     assert (localArrayList.size() == 11);
/* 1019 */     return (MethodHandle[])localArrayList.toArray(new MethodHandle[MAX_ARITY + 1]);
/*      */   }
/*      */ 
/*      */   private static Object[] newArray(int paramInt)
/*      */   {
/* 1024 */     return new Object[paramInt];
/*      */   }
/* 1026 */   private static void fillWithArguments(Object[] paramArrayOfObject1, int paramInt, Object[] paramArrayOfObject2) { System.arraycopy(paramArrayOfObject2, 0, paramArrayOfObject1, paramInt, paramArrayOfObject2.length); }
/*      */ 
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject)
/*      */   {
/* 1030 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject }); return paramArrayOfObject;
/*      */   }
/* 1032 */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2) { fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2 }); return paramArrayOfObject; } 
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3) {
/* 1034 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3 }); return paramArrayOfObject;
/*      */   }
/* 1036 */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 }); return paramArrayOfObject; }
/*      */ 
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
/* 1039 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 }); return paramArrayOfObject;
/*      */   }
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
/* 1042 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 }); return paramArrayOfObject;
/*      */   }
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
/* 1045 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 }); return paramArrayOfObject;
/*      */   }
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
/* 1048 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 }); return paramArrayOfObject;
/*      */   }
/*      */ 
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
/* 1052 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 }); return paramArrayOfObject;
/*      */   }
/*      */ 
/*      */   private static Object[] fillArray(Object[] paramArrayOfObject, Integer paramInteger, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
/* 1056 */     fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 }); return paramArrayOfObject;
/*      */   }
/* 1058 */   private static MethodHandle[] makeFillArrays() { ArrayList localArrayList = new ArrayList();
/* 1059 */     localArrayList.add(null);
/*      */     while (true) {
/* 1061 */       MethodHandle localMethodHandle = findCollector("fillArray", localArrayList.size(), [Ljava.lang.Object.class, new Class[] { [Ljava.lang.Object.class, Integer.class });
/* 1062 */       if (localMethodHandle == null) break;
/* 1063 */       localArrayList.add(localMethodHandle);
/*      */     }
/* 1065 */     assert (localArrayList.size() == 11);
/* 1066 */     return (MethodHandle[])localArrayList.toArray(new MethodHandle[0]);
/*      */   }
/*      */ 
/*      */   private static Object[] copyAsReferenceArray(Class<? extends Object[]> paramClass, Object[] paramArrayOfObject)
/*      */   {
/* 1071 */     return Arrays.copyOf(paramArrayOfObject, paramArrayOfObject.length, paramClass);
/*      */   }
/*      */   private static Object copyAsPrimitiveArray(Wrapper paramWrapper, Object[] paramArrayOfObject) {
/* 1074 */     Object localObject = paramWrapper.makeArray(paramArrayOfObject.length);
/* 1075 */     paramWrapper.copyArrayUnboxing(paramArrayOfObject, 0, localObject, 0, paramArrayOfObject.length);
/* 1076 */     return localObject;
/*      */   }
/*      */ 
/*      */   public static MethodHandle varargsArray(int paramInt)
/*      */   {
/* 1083 */     MethodHandle localMethodHandle1 = ARRAYS[paramInt];
/* 1084 */     if (localMethodHandle1 != null) return localMethodHandle1;
/* 1085 */     localMethodHandle1 = findCollector("array", paramInt, [Ljava.lang.Object.class, new Class[0]);
/* 1086 */     if (localMethodHandle1 != null) return ARRAYS[paramInt] =  = localMethodHandle1;
/* 1087 */     MethodHandle localMethodHandle2 = filler(0);
/* 1088 */     return ARRAYS[paramInt] =  = buildVarargsArray(localMethodHandle2, paramInt);
/*      */   }
/*      */ 
/*      */   private static MethodHandle buildVarargsArray(MethodHandle paramMethodHandle, int paramInt)
/*      */   {
/* 1095 */     MethodHandle localMethodHandle1 = filler(paramInt);
/* 1096 */     MethodHandle localMethodHandle2 = paramMethodHandle;
/* 1097 */     localMethodHandle2 = MethodHandles.dropArguments(localMethodHandle2, 1, localMethodHandle1.type().parameterList());
/* 1098 */     localMethodHandle2 = MethodHandles.foldArguments(localMethodHandle2, localMethodHandle1);
/* 1099 */     localMethodHandle2 = MethodHandles.foldArguments(localMethodHandle2, buildNewArray(paramInt));
/* 1100 */     return localMethodHandle2;
/*      */   }
/*      */ 
/*      */   private static MethodHandle buildNewArray(int paramInt) {
/* 1104 */     return MethodHandles.insertArguments(NEW_ARRAY, 0, new Object[] { Integer.valueOf(paramInt) });
/*      */   }
/*      */ 
/*      */   private static MethodHandle filler(int paramInt)
/*      */   {
/* 1110 */     MethodHandle localMethodHandle = FILLERS[paramInt];
/* 1111 */     if (localMethodHandle != null) return localMethodHandle;
/* 1112 */     return FILLERS[paramInt] =  = buildFiller(paramInt);
/*      */   }
/*      */   private static MethodHandle buildFiller(int paramInt) {
/* 1115 */     if (paramInt == 0)
/* 1116 */       return MethodHandles.identity([Ljava.lang.Object.class);
/* 1117 */     int i = FILL_ARRAYS.length - 1;
/* 1118 */     int j = paramInt % i;
/* 1119 */     int k = paramInt - j;
/* 1120 */     if (j == 0) {
/* 1121 */       k = paramInt - (j = i);
/* 1122 */       if (FILLERS[k] == null)
/*      */       {
/* 1124 */         for (int m = 0; m < k; m += i) filler(m);
/*      */       }
/*      */     }
/* 1127 */     MethodHandle localMethodHandle1 = filler(k);
/* 1128 */     MethodHandle localMethodHandle2 = FILL_ARRAYS[j];
/* 1129 */     localMethodHandle2 = MethodHandles.insertArguments(localMethodHandle2, 1, new Object[] { Integer.valueOf(k) });
/*      */ 
/* 1132 */     MethodHandle localMethodHandle3 = filler(0);
/* 1133 */     localMethodHandle3 = MethodHandles.dropArguments(localMethodHandle3, 1, localMethodHandle2.type().parameterList());
/* 1134 */     localMethodHandle3 = MethodHandles.foldArguments(localMethodHandle3, localMethodHandle2);
/* 1135 */     if (k > 0) {
/* 1136 */       localMethodHandle3 = MethodHandles.dropArguments(localMethodHandle3, 1, localMethodHandle1.type().parameterList());
/* 1137 */       localMethodHandle3 = MethodHandles.foldArguments(localMethodHandle3, localMethodHandle1);
/*      */     }
/* 1139 */     return localMethodHandle3;
/*      */   }
/*      */ 
/*      */   public static MethodHandle varargsArray(Class<?> paramClass, int paramInt)
/*      */   {
/* 1155 */     Class localClass = paramClass.getComponentType();
/* 1156 */     if (localClass == null) throw new IllegalArgumentException("not an array: " + paramClass);
/*      */ 
/* 1158 */     if (localClass == Object.class) {
/* 1159 */       return varargsArray(paramInt);
/*      */     }
/* 1161 */     MethodHandle[] arrayOfMethodHandle = (MethodHandle[])TYPED_COLLECTORS.get(localClass);
/* 1162 */     MethodHandle localMethodHandle1 = paramInt < arrayOfMethodHandle.length ? arrayOfMethodHandle[paramInt] : null;
/* 1163 */     if (localMethodHandle1 != null) return localMethodHandle1;
/* 1164 */     MethodHandle localMethodHandle2 = buildArrayProducer(paramClass);
/* 1165 */     localMethodHandle1 = buildVarargsArray(localMethodHandle2, paramInt);
/* 1166 */     localMethodHandle1 = localMethodHandle1.asType(MethodType.methodType(paramClass, Collections.nCopies(paramInt, localClass)));
/* 1167 */     arrayOfMethodHandle[paramInt] = localMethodHandle1;
/* 1168 */     return localMethodHandle1;
/*      */   }
/*      */ 
/*      */   private static MethodHandle buildArrayProducer(Class<?> paramClass) {
/* 1172 */     Class localClass = paramClass.getComponentType();
/* 1173 */     if (localClass.isPrimitive()) {
/* 1174 */       return LazyStatics.COPY_AS_PRIMITIVE_ARRAY.bindTo(Wrapper.forPrimitiveType(localClass));
/*      */     }
/* 1176 */     return LazyStatics.COPY_AS_REFERENCE_ARRAY.bindTo(paramClass);
/*      */   }
/*      */ 
/*      */   private static List<Object> makeList(Object[] paramArrayOfObject)
/*      */   {
/* 1182 */     return Arrays.asList(paramArrayOfObject); } 
/* 1183 */   private static List<Object> list() { return NO_ARGS_LIST; } 
/*      */   private static List<Object> list(Object paramObject) {
/* 1185 */     return makeList(new Object[] { paramObject });
/*      */   }
/* 1187 */   private static List<Object> list(Object paramObject1, Object paramObject2) { return makeList(new Object[] { paramObject1, paramObject2 }); } 
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3) {
/* 1189 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3 });
/*      */   }
/* 1191 */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) { return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 }); }
/*      */ 
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) {
/* 1194 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
/*      */   }
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) {
/* 1197 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
/*      */   }
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) {
/* 1200 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
/*      */   }
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) {
/* 1203 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
/*      */   }
/*      */ 
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9) {
/* 1207 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
/*      */   }
/*      */ 
/*      */   private static List<Object> list(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10) {
/* 1211 */     return makeList(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
/*      */   }
/* 1213 */   private static MethodHandle[] makeLists() { ArrayList localArrayList = new ArrayList();
/*      */     while (true) {
/* 1215 */       MethodHandle localMethodHandle = findCollector("list", localArrayList.size(), List.class, new Class[0]);
/* 1216 */       if (localMethodHandle == null) break;
/* 1217 */       localArrayList.add(localMethodHandle);
/*      */     }
/* 1219 */     assert (localArrayList.size() == 11);
/* 1220 */     return (MethodHandle[])localArrayList.toArray(new MethodHandle[MAX_ARITY + 1]);
/*      */   }
/*      */ 
/*      */   public static MethodHandle varargsList(int paramInt)
/*      */   {
/* 1228 */     MethodHandle localMethodHandle = LISTS[paramInt];
/* 1229 */     if (localMethodHandle != null) return localMethodHandle;
/* 1230 */     localMethodHandle = findCollector("list", paramInt, List.class, new Class[0]);
/* 1231 */     if (localMethodHandle != null) return LISTS[paramInt] =  = localMethodHandle;
/* 1232 */     return LISTS[paramInt] =  = buildVarargsList(paramInt);
/*      */   }
/*      */   private static MethodHandle buildVarargsList(int paramInt) {
/* 1235 */     return MethodHandles.filterReturnValue(varargsArray(paramInt), LazyStatics.MAKE_LIST);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   41 */     THIS_CLASS = ValueConversions.class;
/*      */ 
/*   45 */     Object localObject1 = { Integer.valueOf(255) };
/*   46 */     AccessController.doPrivileged(new PrivilegedAction() {
/*      */       public Void run() {
/*   48 */         this.val$values[0] = Integer.getInteger(ValueConversions.THIS_CLASS.getName() + ".MAX_ARITY", 255);
/*   49 */         return null;
/*      */       }
/*      */     });
/*   52 */     MAX_ARITY = ((Integer)localObject1[0]).intValue();
/*      */ 
/*   55 */     IMPL_LOOKUP = MethodHandles.lookup();
/*      */ 
/*  153 */     UNBOX_CONVERSIONS = newWrapperCaches(4);
/*      */ 
/*  213 */     ZERO_INT = Integer.valueOf(0); ONE_INT = Integer.valueOf(1);
/*      */ 
/*  317 */     BOX_CONVERSIONS = newWrapperCaches(4);
/*      */ 
/*  441 */     REBOX_CONVERSIONS = newWrapperCaches(1);
/*      */ 
/*  539 */     CONSTANT_FUNCTIONS = newWrapperCaches(2);
/*      */     try
/*      */     {
/*  663 */       localObject1 = MethodType.genericMethodType(1);
/*  664 */       localObject2 = ((MethodType)localObject1).insertParameterTypes(0, new Class[] { Class.class });
/*  665 */       MethodType localMethodType1 = ((MethodType)localObject1).changeReturnType(Integer.TYPE);
/*  666 */       MethodType localMethodType2 = ((MethodType)localObject1).changeReturnType(Void.TYPE);
/*  667 */       MethodType localMethodType3 = MethodType.genericMethodType(0);
/*  668 */       IDENTITY = IMPL_LOOKUP.findStatic(THIS_CLASS, "identity", (MethodType)localObject1);
/*  669 */       IDENTITY_I = IMPL_LOOKUP.findStatic(THIS_CLASS, "identity", MethodType.methodType(Integer.TYPE, Integer.TYPE));
/*  670 */       IDENTITY_J = IMPL_LOOKUP.findStatic(THIS_CLASS, "identity", MethodType.methodType(Long.TYPE, Long.TYPE));
/*      */ 
/*  672 */       CAST_REFERENCE = IMPL_LOOKUP.findStatic(THIS_CLASS, "castReference", (MethodType)localObject2);
/*  673 */       ALWAYS_NULL = IMPL_LOOKUP.findStatic(THIS_CLASS, "alwaysNull", (MethodType)localObject1);
/*  674 */       ALWAYS_ZERO = IMPL_LOOKUP.findStatic(THIS_CLASS, "alwaysZero", localMethodType1);
/*  675 */       ZERO_OBJECT = IMPL_LOOKUP.findStatic(THIS_CLASS, "zeroObject", localMethodType3);
/*  676 */       IGNORE = IMPL_LOOKUP.findStatic(THIS_CLASS, "ignore", localMethodType2);
/*  677 */       EMPTY = IMPL_LOOKUP.findStatic(THIS_CLASS, "empty", localMethodType2.dropParameterTypes(0, 1));
/*  678 */       NEW_ARRAY = IMPL_LOOKUP.findStatic(THIS_CLASS, "newArray", MethodType.methodType([Ljava.lang.Object.class, Integer.TYPE));
/*      */     } catch (NoSuchMethodException|IllegalAccessException localNoSuchMethodException) {
/*  680 */       Object localObject2 = new InternalError("uncaught exception");
/*  681 */       ((Error)localObject2).initCause(localNoSuchMethodException);
/*  682 */       throw ((Throwable)localObject2);
/*      */     }
/*      */   }
/*      */   static class LazyStatics {
/*      */     private static final MethodHandle COPY_AS_REFERENCE_ARRAY;
/*      */     private static final MethodHandle COPY_AS_PRIMITIVE_ARRAY;
/*      */     private static final MethodHandle MAKE_LIST;
/*      */ 
/*      */     static { try { COPY_AS_REFERENCE_ARRAY = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "copyAsReferenceArray", MethodType.methodType([Ljava.lang.Object.class, Class.class, new Class[] { [Ljava.lang.Object.class }));
/*  693 */         COPY_AS_PRIMITIVE_ARRAY = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "copyAsPrimitiveArray", MethodType.methodType(Object.class, Wrapper.class, new Class[] { [Ljava.lang.Object.class }));
/*  694 */         MAKE_LIST = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "makeList", MethodType.methodType(List.class, [Ljava.lang.Object.class));
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  696 */         InternalError localInternalError = new InternalError("uncaught exception");
/*  697 */         localInternalError.initCause(localReflectiveOperationException);
/*  698 */         throw localInternalError;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.util.ValueConversions
 * JD-Core Version:    0.6.2
 */