/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import sun.invoke.util.ValueConversions;
/*      */ import sun.invoke.util.VerifyType;
/*      */ import sun.invoke.util.Wrapper;
/*      */ 
/*      */ class AdapterMethodHandle extends BoundMethodHandle
/*      */ {
/*      */   private final int conversion;
/*      */   static final int MAX_ARG_ROTATION = 1;
/*      */ 
/*      */   private AdapterMethodHandle(MethodHandle paramMethodHandle, MethodType paramMethodType, long paramLong, Object paramObject)
/*      */   {
/*   51 */     super(paramMethodType, paramObject, paramMethodType.parameterSlotDepth(1 + convArgPos(paramLong)));
/*   52 */     this.conversion = convCode(paramLong);
/*      */ 
/*   54 */     MethodHandleNatives.init(this, paramMethodHandle, convArgPos(paramLong));
/*      */   }
/*      */ 
/*      */   AdapterMethodHandle(MethodHandle paramMethodHandle, MethodType paramMethodType, long paramLong) {
/*   58 */     this(paramMethodHandle, paramMethodType, paramLong, null);
/*      */   }
/*      */   int getConversion() {
/*   61 */     return this.conversion;
/*      */   }
/*      */ 
/*      */   static boolean canPairwiseConvert(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt)
/*      */   {
/*   71 */     int i = paramMethodType1.parameterCount();
/*   72 */     if (i != paramMethodType2.parameterCount()) {
/*   73 */       return false;
/*      */     }
/*      */ 
/*   76 */     Class localClass1 = paramMethodType1.returnType();
/*   77 */     Class localClass2 = paramMethodType2.returnType();
/*   78 */     if (!VerifyType.isNullConversion(localClass2, localClass1)) {
/*   79 */       if (!convOpSupported(10))
/*   80 */         return false;
/*   81 */       if (!canConvertArgument(localClass2, localClass1, paramInt)) {
/*   82 */         return false;
/*      */       }
/*      */     }
/*      */ 
/*   86 */     for (int j = 0; j < i; j++) {
/*   87 */       Class localClass3 = paramMethodType1.parameterType(j);
/*   88 */       Class localClass4 = paramMethodType2.parameterType(j);
/*   89 */       if (!canConvertArgument(localClass3, localClass4, paramInt)) {
/*   90 */         return false;
/*      */       }
/*      */     }
/*   93 */     return true;
/*      */   }
/*      */ 
/*      */   static boolean canConvertArgument(Class<?> paramClass1, Class<?> paramClass2, int paramInt)
/*      */   {
/*  102 */     if (VerifyType.isNullConversion(paramClass1, paramClass2))
/*  103 */       return true;
/*  104 */     if (convOpSupported(10))
/*      */     {
/*  106 */       return true;
/*  107 */     }if (paramClass1.isPrimitive()) {
/*  108 */       if (paramClass2.isPrimitive()) {
/*  109 */         return canPrimCast(paramClass1, paramClass2);
/*      */       }
/*  111 */       return canBoxArgument(paramClass1, paramClass2);
/*      */     }
/*  113 */     if (paramClass2.isPrimitive()) {
/*  114 */       return canUnboxArgument(paramClass1, paramClass2, paramInt);
/*      */     }
/*  116 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makePairwiseConvert(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt)
/*      */   {
/*  134 */     MethodType localMethodType1 = paramMethodHandle.type();
/*  135 */     if (paramMethodType == localMethodType1) return paramMethodHandle;
/*      */ 
/*  137 */     if (!canPairwiseConvert(paramMethodType, localMethodType1, paramInt)) {
/*  138 */       return null;
/*      */     }
/*      */ 
/*  142 */     int i = paramMethodType.parameterCount() - 1;
/*  143 */     while (i >= 0) {
/*  144 */       localClass1 = paramMethodType.parameterType(i);
/*  145 */       localClass2 = localMethodType1.parameterType(i);
/*  146 */       if (!isTrivialConversion(localClass1, localClass2, paramInt)) break;
/*  147 */       i--;
/*      */     }
/*      */ 
/*  153 */     Class localClass1 = paramMethodType.returnType();
/*  154 */     Class localClass2 = localMethodType1.returnType();
/*  155 */     int j = !isTrivialConversion(localClass2, localClass1, paramInt) ? 1 : 0;
/*      */ 
/*  158 */     Object localObject1 = paramMethodHandle;
/*  159 */     Object localObject2 = localMethodType1;
/*      */     MethodHandle localMethodHandle;
/*  160 */     for (int k = 0; k <= i; k++) {
/*  161 */       Class localClass3 = paramMethodType.parameterType(k);
/*  162 */       Class localClass4 = ((MethodType)localObject2).parameterType(k);
/*  163 */       if (!isTrivialConversion(localClass3, localClass4, paramInt))
/*      */       {
/*  168 */         localObject2 = ((MethodType)localObject2).changeParameterType(k, localClass3);
/*  169 */         if (k == i)
/*      */         {
/*  172 */           MethodType localMethodType2 = paramMethodType;
/*  173 */           if (j != 0) localMethodType2 = localMethodType2.changeReturnType(localClass2);
/*  174 */           assert (VerifyType.isNullConversion(localMethodType2, (MethodType)localObject2));
/*  175 */           localObject2 = localMethodType2;
/*      */         }
/*      */ 
/*  180 */         if (localClass3.isPrimitive()) {
/*  181 */           if (localClass4.isPrimitive())
/*  182 */             localMethodHandle = makePrimCast((MethodType)localObject2, (MethodHandle)localObject1, k, localClass4);
/*      */           else {
/*  184 */             localMethodHandle = makeBoxArgument((MethodType)localObject2, (MethodHandle)localObject1, k, localClass3);
/*      */           }
/*      */         }
/*  187 */         else if (localClass4.isPrimitive())
/*      */         {
/*  194 */           localMethodHandle = makeUnboxArgument((MethodType)localObject2, (MethodHandle)localObject1, k, localClass4, paramInt);
/*      */         }
/*      */         else
/*      */         {
/*  200 */           localMethodHandle = makeCheckCast((MethodType)localObject2, (MethodHandle)localObject1, k, localClass4);
/*      */         }
/*      */ 
/*  203 */         if ((!$assertionsDisabled) && (localMethodHandle == null)) throw new AssertionError(Arrays.asList(new Object[] { localClass3, localClass4, localObject2, localObject1, Integer.valueOf(k), paramMethodHandle, paramMethodType }));
/*  204 */         assert (localMethodHandle.type() == localObject2);
/*  205 */         localObject1 = localMethodHandle;
/*      */       }
/*      */     }
/*  207 */     if (j != 0) {
/*  208 */       localMethodHandle = makeReturnConversion((MethodHandle)localObject1, localClass2, localClass1);
/*  209 */       assert (localMethodHandle != null);
/*  210 */       localObject1 = localMethodHandle;
/*      */     }
/*  212 */     if (((MethodHandle)localObject1).type() != paramMethodType)
/*      */     {
/*  214 */       localMethodHandle = makeRetypeOnly(paramMethodType, (MethodHandle)localObject1);
/*  215 */       assert (localMethodHandle != null);
/*  216 */       localObject1 = localMethodHandle;
/*      */ 
/*  218 */       assert ((i == -1) || (j != 0));
/*      */     }
/*  220 */     assert (((MethodHandle)localObject1).type() == paramMethodType);
/*  221 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private static boolean isTrivialConversion(Class<?> paramClass1, Class<?> paramClass2, int paramInt) {
/*  225 */     if ((paramClass1 == paramClass2) || (paramClass2 == Void.TYPE)) return true;
/*  226 */     if (!VerifyType.isNullConversion(paramClass1, paramClass2)) return false;
/*  227 */     if (paramInt > 1) return true;
/*  228 */     boolean bool1 = paramClass1.isPrimitive();
/*  229 */     boolean bool2 = paramClass2.isPrimitive();
/*  230 */     if (bool1 != bool2) return false;
/*  231 */     if (bool1)
/*      */     {
/*  233 */       return Wrapper.forPrimitiveType(paramClass2).isConvertibleFrom(Wrapper.forPrimitiveType(paramClass1));
/*      */     }
/*      */ 
/*  236 */     return paramClass2.isAssignableFrom(paramClass1);
/*      */   }
/*      */ 
/*      */   private static MethodHandle makeReturnConversion(MethodHandle paramMethodHandle, Class<?> paramClass1, Class<?> paramClass2)
/*      */   {
/*      */     Object localObject;
/*      */     MethodHandle localMethodHandle;
/*  242 */     if (paramClass1 == Void.TYPE)
/*      */     {
/*  244 */       localObject = Wrapper.forBasicType(paramClass2).zero();
/*  245 */       localMethodHandle = MethodHandles.constant(paramClass2, localObject);
/*      */     } else {
/*  247 */       localObject = MethodType.methodType(paramClass2, paramClass1);
/*  248 */       localMethodHandle = MethodHandles.identity(paramClass2).asType((MethodType)localObject);
/*      */     }
/*  250 */     if (!canCollectArguments(localMethodHandle.type(), paramMethodHandle.type(), 0, false)) {
/*  251 */       assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  252 */       throw new InternalError("NYI");
/*      */     }
/*  254 */     return makeCollectArguments(localMethodHandle, paramMethodHandle, 0, false);
/*      */   }
/*      */ 
/*      */   static MethodHandle makePermutation(MethodType paramMethodType, MethodHandle paramMethodHandle, int[] paramArrayOfInt)
/*      */   {
/*  271 */     MethodType localMethodType = paramMethodHandle.type();
/*  272 */     int i = 1;
/*  273 */     for (int j = 0; j < paramArrayOfInt.length; j++) {
/*  274 */       int k = paramArrayOfInt[j];
/*  275 */       if (k != j)
/*  276 */         i = 0;
/*  277 */       if ((k < 0) || (k >= paramMethodType.parameterCount())) {
/*  278 */         paramArrayOfInt = new int[0]; break;
/*      */       }
/*      */     }
/*  281 */     if (paramArrayOfInt.length != localMethodType.parameterCount())
/*  282 */       throw MethodHandleStatics.newIllegalArgumentException("bad permutation: " + Arrays.toString(paramArrayOfInt));
/*  283 */     if (i != 0) {
/*  284 */       localObject = makePairwiseConvert(paramMethodType, paramMethodHandle, 0);
/*      */ 
/*  286 */       if (localObject == null)
/*  287 */         throw MethodHandleStatics.newIllegalArgumentException("cannot convert pairwise: " + paramMethodType);
/*  288 */       return localObject;
/*      */     }
/*      */ 
/*  292 */     Object localObject = paramMethodType.returnType();
/*  293 */     Class localClass1 = localMethodType.returnType();
/*  294 */     if (!VerifyType.isNullConversion(localClass1, (Class)localObject)) {
/*  295 */       throw MethodHandleStatics.newIllegalArgumentException("bad return conversion for " + paramMethodType);
/*      */     }
/*      */ 
/*  298 */     for (int m = 0; m < paramArrayOfInt.length; m++) {
/*  299 */       int n = paramArrayOfInt[m];
/*  300 */       Class localClass2 = paramMethodType.parameterType(n);
/*  301 */       Class localClass3 = localMethodType.parameterType(m);
/*  302 */       if (!VerifyType.isNullConversion(localClass2, localClass3)) {
/*  303 */         throw MethodHandleStatics.newIllegalArgumentException("bad argument #" + n + " conversion for " + paramMethodType);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  327 */     throw new UnsupportedOperationException("NYI");
/*      */   }
/*      */ 
/*      */   private static byte basicType(Class<?> paramClass) {
/*  331 */     if (paramClass == null) return 14;
/*  332 */     switch (1.$SwitchMap$sun$invoke$util$Wrapper[Wrapper.forBasicType(paramClass).ordinal()]) { case 1:
/*  333 */       return 4;
/*      */     case 2:
/*  334 */       return 5;
/*      */     case 3:
/*  335 */       return 6;
/*      */     case 4:
/*  336 */       return 7;
/*      */     case 5:
/*  337 */       return 8;
/*      */     case 6:
/*  338 */       return 9;
/*      */     case 7:
/*  339 */       return 10;
/*      */     case 8:
/*  340 */       return 11;
/*      */     case 9:
/*  341 */       return 12;
/*      */     case 10:
/*  342 */       return 14;
/*      */     }
/*  344 */     return 99;
/*      */   }
/*      */ 
/*      */   private static int type2size(int paramInt)
/*      */   {
/*  351 */     assert ((paramInt >= 4) && (paramInt <= 12));
/*  352 */     return (paramInt == 11) || (paramInt == 7) ? 2 : 1;
/*      */   }
/*      */   private static int type2size(Class<?> paramClass) {
/*  355 */     return type2size(basicType(paramClass));
/*      */   }
/*      */ 
/*      */   private static long insertStackMove(int paramInt)
/*      */   {
/*  366 */     long l = paramInt * MethodHandleNatives.JVM_STACK_MOVE_UNIT;
/*  367 */     return (l & 0xFFF) << 20;
/*      */   }
/*      */ 
/*      */   static int extractStackMove(int paramInt) {
/*  371 */     int i = paramInt >> 20;
/*  372 */     return i / MethodHandleNatives.JVM_STACK_MOVE_UNIT;
/*      */   }
/*      */ 
/*      */   static int extractStackMove(MethodHandle paramMethodHandle) {
/*  376 */     if ((paramMethodHandle instanceof AdapterMethodHandle)) {
/*  377 */       AdapterMethodHandle localAdapterMethodHandle = (AdapterMethodHandle)paramMethodHandle;
/*  378 */       return extractStackMove(localAdapterMethodHandle.getConversion());
/*      */     }
/*  380 */     return 0;
/*      */   }
/*      */ 
/*      */   private static long makeConv(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  386 */     assert (paramInt3 == (paramInt3 & 0xF));
/*  387 */     assert (paramInt4 == (paramInt4 & 0xF));
/*  388 */     assert (((paramInt1 >= 2) && (paramInt1 <= 5)) || (paramInt1 == 10));
/*  389 */     int i = type2size(paramInt4) - type2size(paramInt3);
/*  390 */     return paramInt2 << 32 | paramInt1 << 8 | paramInt3 << 16 | paramInt4 << 12 | insertStackMove(i);
/*      */   }
/*      */ 
/*      */   private static long makeDupConv(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  399 */     assert ((paramInt1 == 8) || (paramInt1 == 9));
/*  400 */     int i = 0; int j = 0;
/*  401 */     return paramInt2 << 32 | paramInt1 << 8 | i << 16 | j << 12 | insertStackMove(paramInt3);
/*      */   }
/*      */ 
/*      */   private static long makeSwapConv(int paramInt1, int paramInt2, byte paramByte1, int paramInt3, byte paramByte2)
/*      */   {
/*  410 */     assert ((paramInt1 == 6) || (paramInt1 == 7));
/*  411 */     return paramInt2 << 32 | paramInt1 << 8 | paramByte1 << 16 | paramByte2 << 12 | paramInt3 << 0;
/*      */   }
/*      */ 
/*      */   private static long makeSpreadConv(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  420 */     assert ((paramInt1 == 11) || (paramInt1 == 10) || (paramInt1 == 12));
/*      */ 
/*  423 */     return paramInt2 << 32 | paramInt1 << 8 | paramInt3 << 16 | paramInt4 << 12 | insertStackMove(paramInt5);
/*      */   }
/*      */ 
/*      */   static long makeConv(int paramInt)
/*      */   {
/*  431 */     assert ((paramInt == 0) || (paramInt == 1));
/*  432 */     return 0x0 | paramInt << 8;
/*      */   }
/*      */   private static int convCode(long paramLong) {
/*  435 */     return (int)paramLong;
/*      */   }
/*      */   private static int convArgPos(long paramLong) {
/*  438 */     return (int)(paramLong >>> 32);
/*      */   }
/*      */   private static boolean convOpSupported(int paramInt) {
/*  441 */     assert ((paramInt >= 0) && (paramInt <= 14));
/*  442 */     return (1 << paramInt & MethodHandleNatives.CONV_OP_IMPLEMENTED_MASK) != 0;
/*      */   }
/*      */ 
/*      */   int conversionOp() {
/*  446 */     return (this.conversion & 0xF00) >> 8;
/*      */   }
/*      */ 
/*      */   private static int diffTypes(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean)
/*      */   {
/*  464 */     int i = diffReturnTypes(paramMethodType1, paramMethodType2, paramBoolean);
/*  465 */     if (i != 0) return i;
/*  466 */     int j = paramMethodType1.parameterCount();
/*  467 */     if (j != paramMethodType2.parameterCount())
/*  468 */       return -1;
/*  469 */     i = diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, j, paramBoolean);
/*      */ 
/*  472 */     return i;
/*      */   }
/*      */ 
/*      */   private static int diffReturnTypes(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean)
/*      */   {
/*  477 */     Class localClass1 = paramMethodType2.returnType();
/*  478 */     Class localClass2 = paramMethodType1.returnType();
/*  479 */     if ((!paramBoolean ? VerifyType.canPassUnchecked(localClass1, localClass2) : VerifyType.canPassRaw(localClass1, localClass2)) > 0)
/*      */     {
/*  483 */       return 0;
/*  484 */     }if ((paramBoolean) && (!localClass1.isPrimitive()) && (!localClass2.isPrimitive())) {
/*  485 */       return 0;
/*      */     }
/*  487 */     return -1;
/*      */   }
/*      */ 
/*      */   private static int diffParamTypes(MethodType paramMethodType1, int paramInt1, MethodType paramMethodType2, int paramInt2, int paramInt3, boolean paramBoolean)
/*      */   {
/*  492 */     assert (paramInt3 >= 0);
/*  493 */     int i = 0;
/*  494 */     for (int j = 0; j < paramInt3; j++) {
/*  495 */       Class localClass1 = paramMethodType1.parameterType(paramInt1 + j);
/*  496 */       Class localClass2 = paramMethodType2.parameterType(paramInt2 + j);
/*  497 */       if ((!paramBoolean ? VerifyType.canPassUnchecked(localClass1, localClass2) : VerifyType.canPassRaw(localClass1, localClass2)) <= 0)
/*      */       {
/*  502 */         if (i != 0)
/*  503 */           return -1 - i;
/*  504 */         i = 1 + j;
/*      */       }
/*      */     }
/*  507 */     return i;
/*      */   }
/*      */ 
/*      */   static boolean canRetypeOnly(MethodType paramMethodType1, MethodType paramMethodType2)
/*      */   {
/*  512 */     return canRetype(paramMethodType1, paramMethodType2, false);
/*      */   }
/*      */ 
/*      */   static boolean canRetypeRaw(MethodType paramMethodType1, MethodType paramMethodType2)
/*      */   {
/*  521 */     return canRetype(paramMethodType1, paramMethodType2, true);
/*      */   }
/*      */   static boolean canRetype(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean) {
/*  524 */     if (!convOpSupported(paramBoolean ? 1 : 0)) return false;
/*  525 */     int i = diffTypes(paramMethodType1, paramMethodType2, paramBoolean);
/*      */ 
/*  527 */     if ((!$assertionsDisabled) && (!paramBoolean)) if ((i == 0) != VerifyType.isNullConversion(paramMethodType1, paramMethodType2)) throw new AssertionError();
/*  528 */     return i == 0;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeRetypeOnly(MethodType paramMethodType, MethodHandle paramMethodHandle)
/*      */   {
/*  536 */     return makeRetype(paramMethodType, paramMethodHandle, false);
/*      */   }
/*      */   static MethodHandle makeRetypeRaw(MethodType paramMethodType, MethodHandle paramMethodHandle) {
/*  539 */     return makeRetype(paramMethodType, paramMethodHandle, true);
/*      */   }
/*      */   static MethodHandle makeRetype(MethodType paramMethodType, MethodHandle paramMethodHandle, boolean paramBoolean) {
/*  542 */     MethodType localMethodType = paramMethodHandle.type();
/*  543 */     if (localMethodType == paramMethodType) return paramMethodHandle;
/*  544 */     if (!canRetype(paramMethodType, localMethodType, paramBoolean)) {
/*  545 */       return null;
/*      */     }
/*  547 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, makeConv(paramBoolean ? 1 : 0));
/*      */   }
/*      */ 
/*      */   static MethodHandle makeVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass) {
/*  551 */     MethodType localMethodType = paramMethodHandle.type();
/*  552 */     int i = localMethodType.parameterCount() - 1;
/*  553 */     if (localMethodType.parameterType(i) != paramClass)
/*  554 */       paramMethodHandle = paramMethodHandle.asType(localMethodType.changeParameterType(i, paramClass));
/*  555 */     paramMethodHandle = paramMethodHandle.asFixedArity();
/*  556 */     return new AsVarargsCollector(paramMethodHandle, paramClass);
/*      */   }
/*      */ 
/*      */   static boolean canCheckCast(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt, Class<?> paramClass)
/*      */   {
/*  612 */     if (!convOpSupported(2)) return false;
/*  613 */     Class localClass1 = paramMethodType1.parameterType(paramInt);
/*  614 */     Class localClass2 = paramMethodType2.parameterType(paramInt);
/*  615 */     if ((!canCheckCast(localClass1, paramClass)) || (!VerifyType.isNullConversion(paramClass, localClass2)))
/*      */     {
/*  617 */       return false;
/*  618 */     }int i = diffTypes(paramMethodType1, paramMethodType2, false);
/*  619 */     return (i == paramInt + 1) || (i == 0);
/*      */   }
/*      */ 
/*      */   static boolean canCheckCast(Class<?> paramClass1, Class<?> paramClass2) {
/*  623 */     return (!paramClass1.isPrimitive()) && (!paramClass2.isPrimitive());
/*      */   }
/*      */ 
/*      */   static MethodHandle makeCheckCast(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt, Class<?> paramClass)
/*      */   {
/*  633 */     if (!canCheckCast(paramMethodType, paramMethodHandle.type(), paramInt, paramClass))
/*  634 */       return null;
/*  635 */     long l = makeConv(2, paramInt, 12, 12);
/*  636 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l, paramClass);
/*      */   }
/*      */ 
/*      */   static boolean canPrimCast(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt, Class<?> paramClass)
/*      */   {
/*  645 */     if (!convOpSupported(3)) return false;
/*  646 */     Class localClass1 = paramMethodType1.parameterType(paramInt);
/*  647 */     Class localClass2 = paramMethodType2.parameterType(paramInt);
/*  648 */     if ((!canPrimCast(localClass1, paramClass)) || (!VerifyType.isNullConversion(paramClass, localClass2)))
/*      */     {
/*  650 */       return false;
/*  651 */     }int i = diffTypes(paramMethodType1, paramMethodType2, false);
/*  652 */     return i == paramInt + 1;
/*      */   }
/*      */ 
/*      */   static boolean canPrimCast(Class<?> paramClass1, Class<?> paramClass2) {
/*  656 */     if ((paramClass1 == paramClass2) || (!paramClass1.isPrimitive()) || (!paramClass2.isPrimitive())) {
/*  657 */       return false;
/*      */     }
/*  659 */     boolean bool1 = Wrapper.forPrimitiveType(paramClass1).isFloating();
/*  660 */     boolean bool2 = Wrapper.forPrimitiveType(paramClass2).isFloating();
/*  661 */     return !(bool1 | bool2);
/*      */   }
/*      */ 
/*      */   static MethodHandle makePrimCast(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt, Class<?> paramClass)
/*      */   {
/*  673 */     Class localClass = paramMethodType.parameterType(paramInt);
/*  674 */     if (canPrimCast(localClass, paramClass))
/*  675 */       return makePrimCastOnly(paramMethodType, paramMethodHandle, paramInt, paramClass);
/*  676 */     Class<?> localClass1 = paramClass;
/*  677 */     boolean bool1 = Wrapper.forPrimitiveType(localClass).isFloating();
/*  678 */     boolean bool2 = Wrapper.forPrimitiveType(localClass1).isFloating();
/*  679 */     if ((bool1 | bool2))
/*      */     {
/*      */       MethodHandle localMethodHandle;
/*  681 */       if (bool1) {
/*  682 */         localMethodHandle = localClass == Double.TYPE ? ValueConversions.convertFromDouble(localClass1) : ValueConversions.convertFromFloat(localClass1);
/*      */       }
/*      */       else
/*      */       {
/*  686 */         localMethodHandle = localClass1 == Double.TYPE ? ValueConversions.convertToDouble(localClass) : ValueConversions.convertToFloat(localClass);
/*      */       }
/*      */ 
/*  689 */       long l = makeConv(10, paramInt, basicType(localClass), basicType(localClass1));
/*  690 */       return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l, localMethodHandle);
/*      */     }
/*  692 */     throw new InternalError("makePrimCast");
/*      */   }
/*      */ 
/*      */   static MethodHandle makePrimCastOnly(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt, Class<?> paramClass) {
/*  696 */     MethodType localMethodType = paramMethodHandle.type();
/*  697 */     if (!canPrimCast(paramMethodType, localMethodType, paramInt, paramClass))
/*  698 */       return null;
/*  699 */     Class localClass = paramMethodType.parameterType(paramInt);
/*  700 */     long l = makeConv(3, paramInt, basicType(localClass), basicType(paramClass));
/*  701 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l);
/*      */   }
/*      */ 
/*      */   static boolean canUnboxArgument(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt1, Class<?> paramClass, int paramInt2)
/*      */   {
/*  710 */     if (!convOpSupported(4)) return false;
/*  711 */     Class localClass1 = paramMethodType1.parameterType(paramInt1);
/*  712 */     Class localClass2 = paramMethodType2.parameterType(paramInt1);
/*  713 */     Class localClass3 = Wrapper.asWrapperType(paramClass);
/*  714 */     paramClass = Wrapper.asPrimitiveType(paramClass);
/*  715 */     if ((!canCheckCast(localClass1, localClass3)) || (localClass3 == paramClass) || (!VerifyType.isNullConversion(paramClass, localClass2)))
/*      */     {
/*  718 */       return false;
/*  719 */     }int i = diffTypes(paramMethodType1, paramMethodType2, false);
/*  720 */     return i == paramInt1 + 1;
/*      */   }
/*      */ 
/*      */   static boolean canUnboxArgument(Class<?> paramClass1, Class<?> paramClass2, int paramInt) {
/*  724 */     assert (paramClass2.isPrimitive());
/*      */ 
/*  726 */     if (convOpSupported(5)) return true;
/*  727 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass2);
/*      */ 
/*  729 */     if (paramInt == 0) return !paramClass1.isPrimitive();
/*  730 */     assert ((paramInt >= 0) && (paramInt <= 2));
/*      */ 
/*  734 */     return localWrapper.wrapperType() == paramClass1;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeUnboxArgument(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt1, Class<?> paramClass, int paramInt2)
/*      */   {
/*  742 */     MethodType localMethodType1 = paramMethodHandle.type();
/*  743 */     Class localClass1 = paramMethodType.parameterType(paramInt1);
/*  744 */     Class localClass2 = localMethodType1.parameterType(paramInt1);
/*  745 */     Class localClass3 = Wrapper.asWrapperType(paramClass);
/*  746 */     Class localClass4 = Wrapper.asPrimitiveType(paramClass);
/*  747 */     if (!canUnboxArgument(paramMethodType, localMethodType1, paramInt1, paramClass, paramInt2))
/*  748 */       return null;
/*  749 */     MethodType localMethodType2 = paramMethodType;
/*  750 */     if (!VerifyType.isNullConversion(localClass1, localClass3))
/*      */     {
/*  752 */       if (paramInt2 != 0)
/*      */       {
/*  754 */         if ((localClass1 == Object.class) || (!Wrapper.isWrapperType(localClass1)))
/*      */         {
/*  756 */           localObject1 = paramInt2 == 1 ? ValueConversions.unbox(localClass2) : ValueConversions.unboxCast(localClass2);
/*      */ 
/*  759 */           long l2 = makeConv(10, paramInt1, basicType(localClass1), basicType(localClass2));
/*  760 */           return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l2, localObject1);
/*      */         }
/*      */ 
/*  764 */         Object localObject1 = Wrapper.forWrapperType(localClass1).primitiveType();
/*  765 */         MethodType localMethodType3 = paramMethodType.changeParameterType(paramInt1, (Class)localObject1);
/*      */ 
/*  767 */         if (canPrimCast(localMethodType3, localMethodType1, paramInt1, localClass2))
/*  768 */           localObject2 = makePrimCast(localMethodType3, paramMethodHandle, paramInt1, localClass2);
/*      */         else
/*  770 */           localObject2 = paramMethodHandle;
/*  771 */         return makeUnboxArgument(paramMethodType, (MethodHandle)localObject2, paramInt1, (Class)localObject1, 0);
/*      */       }
/*  773 */       localMethodType2 = paramMethodType.changeParameterType(paramInt1, localClass3);
/*      */     }
/*  775 */     long l1 = makeConv(4, paramInt1, 12, basicType(localClass4));
/*  776 */     Object localObject2 = new AdapterMethodHandle(paramMethodHandle, localMethodType2, l1, localClass3);
/*  777 */     if (localMethodType2 == paramMethodType)
/*  778 */       return localObject2;
/*  779 */     return makeCheckCast(paramMethodType, (MethodHandle)localObject2, paramInt1, localClass3);
/*      */   }
/*      */ 
/*      */   static boolean canBoxArgument(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt, Class<?> paramClass)
/*      */   {
/*  785 */     if (!convOpSupported(5)) return false;
/*  786 */     Class localClass1 = paramMethodType1.parameterType(paramInt);
/*  787 */     Class localClass2 = paramMethodType2.parameterType(paramInt);
/*  788 */     Class localClass3 = Wrapper.asWrapperType(paramClass);
/*  789 */     paramClass = Wrapper.asPrimitiveType(paramClass);
/*  790 */     if ((!canCheckCast(localClass3, localClass2)) || (localClass3 == paramClass) || (!VerifyType.isNullConversion(localClass1, paramClass)))
/*      */     {
/*  793 */       return false;
/*  794 */     }int i = diffTypes(paramMethodType1, paramMethodType2, false);
/*  795 */     return i == paramInt + 1;
/*      */   }
/*      */ 
/*      */   static boolean canBoxArgument(Class<?> paramClass1, Class<?> paramClass2)
/*      */   {
/*  800 */     if (!convOpSupported(5)) return false;
/*  801 */     return (paramClass1.isPrimitive()) && (!paramClass2.isPrimitive());
/*      */   }
/*      */ 
/*      */   static MethodHandle makeBoxArgument(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt, Class<?> paramClass)
/*      */   {
/*  809 */     MethodType localMethodType = paramMethodHandle.type();
/*  810 */     Class localClass1 = paramMethodType.parameterType(paramInt);
/*  811 */     Class localClass2 = localMethodType.parameterType(paramInt);
/*  812 */     Class localClass3 = Wrapper.asWrapperType(paramClass);
/*  813 */     Class localClass4 = Wrapper.asPrimitiveType(paramClass);
/*  814 */     if (!canBoxArgument(paramMethodType, localMethodType, paramInt, paramClass)) {
/*  815 */       return null;
/*      */     }
/*  817 */     if (!VerifyType.isNullConversion(localClass3, localClass2))
/*  818 */       paramMethodHandle = makeCheckCast(localMethodType.changeParameterType(paramInt, localClass3), paramMethodHandle, paramInt, localClass2);
/*  819 */     MethodHandle localMethodHandle = ValueConversions.box(Wrapper.forPrimitiveType(localClass4));
/*  820 */     long l = makeConv(5, paramInt, basicType(localClass4), 12);
/*  821 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l, localMethodHandle);
/*      */   }
/*      */ 
/*      */   static boolean canDropArguments(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt1, int paramInt2)
/*      */   {
/*  827 */     if (paramInt2 == 0)
/*  828 */       return canRetypeOnly(paramMethodType1, paramMethodType2);
/*  829 */     if (!convOpSupported(9)) return false;
/*  830 */     if (diffReturnTypes(paramMethodType1, paramMethodType2, false) != 0)
/*  831 */       return false;
/*  832 */     int i = paramMethodType1.parameterCount();
/*      */ 
/*  834 */     if ((paramInt1 != 0) && (diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, paramInt1, false) != 0))
/*  835 */       return false;
/*  836 */     int j = paramInt1 + paramInt2;
/*  837 */     int k = i - j;
/*  838 */     if ((paramInt1 < 0) || (paramInt1 >= i) || (paramInt2 < 1) || (j > i) || (paramMethodType2.parameterCount() != i - paramInt2))
/*      */     {
/*  841 */       return false;
/*      */     }
/*  843 */     if ((k != 0) && (diffParamTypes(paramMethodType1, j, paramMethodType2, paramInt1, k, false) != 0))
/*  844 */       return false;
/*  845 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeDropArguments(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt1, int paramInt2)
/*      */   {
/*  854 */     if (paramInt2 == 0)
/*  855 */       return makeRetypeOnly(paramMethodType, paramMethodHandle);
/*  856 */     if (!canDropArguments(paramMethodType, paramMethodHandle.type(), paramInt1, paramInt2)) {
/*  857 */       return null;
/*      */     }
/*      */ 
/*  860 */     int i = paramInt1 + paramInt2;
/*  861 */     int j = paramMethodType.parameterSlotDepth(i);
/*  862 */     int k = paramMethodType.parameterSlotDepth(paramInt1);
/*  863 */     int m = k - j;
/*  864 */     assert (m >= paramInt2);
/*  865 */     assert (paramMethodHandle.type().parameterSlotCount() + m == paramMethodType.parameterSlotCount());
/*  866 */     long l = makeDupConv(9, paramInt1 + paramInt2 - 1, -m);
/*  867 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l);
/*      */   }
/*      */ 
/*      */   static boolean canDupArguments(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt1, int paramInt2)
/*      */   {
/*  873 */     if (!convOpSupported(8)) return false;
/*  874 */     if (diffReturnTypes(paramMethodType1, paramMethodType2, false) != 0)
/*  875 */       return false;
/*  876 */     int i = paramMethodType1.parameterCount();
/*  877 */     if ((paramInt2 < 0) || (paramInt1 + paramInt2 > i))
/*  878 */       return false;
/*  879 */     if (paramMethodType2.parameterCount() != i + paramInt2) {
/*  880 */       return false;
/*      */     }
/*  882 */     if (diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, i, false) != 0) {
/*  883 */       return false;
/*      */     }
/*  885 */     if (diffParamTypes(paramMethodType1, paramInt1, paramMethodType2, i, paramInt2, false) != 0)
/*  886 */       return false;
/*  887 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeDupArguments(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt1, int paramInt2)
/*      */   {
/*  895 */     if (!canDupArguments(paramMethodType, paramMethodHandle.type(), paramInt1, paramInt2))
/*  896 */       return null;
/*  897 */     if (paramInt2 == 0) {
/*  898 */       return paramMethodHandle;
/*      */     }
/*      */ 
/*  901 */     int i = paramInt1 + paramInt2;
/*  902 */     int j = paramMethodType.parameterSlotDepth(i);
/*  903 */     int k = paramMethodType.parameterSlotDepth(paramInt1);
/*  904 */     int m = k - j;
/*  905 */     assert (paramMethodHandle.type().parameterSlotCount() - m == paramMethodType.parameterSlotCount());
/*  906 */     long l = makeDupConv(8, paramInt1 + paramInt2 - 1, m);
/*  907 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l);
/*      */   }
/*      */ 
/*      */   static boolean canSwapArguments(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt1, int paramInt2)
/*      */   {
/*  913 */     if (!convOpSupported(6)) return false;
/*  914 */     if (diffReturnTypes(paramMethodType1, paramMethodType2, false) != 0)
/*  915 */       return false;
/*  916 */     if (paramInt1 >= paramInt2) return false;
/*  917 */     int i = paramMethodType1.parameterCount();
/*  918 */     if (paramMethodType2.parameterCount() != i)
/*  919 */       return false;
/*  920 */     if ((paramInt1 < 0) || (paramInt2 >= i))
/*  921 */       return false;
/*  922 */     if (diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, paramInt1, false) != 0)
/*  923 */       return false;
/*  924 */     if (diffParamTypes(paramMethodType1, paramInt1, paramMethodType2, paramInt2, 1, false) != 0)
/*  925 */       return false;
/*  926 */     if (diffParamTypes(paramMethodType1, paramInt1 + 1, paramMethodType2, paramInt1 + 1, paramInt2 - paramInt1 - 1, false) != 0)
/*  927 */       return false;
/*  928 */     if (diffParamTypes(paramMethodType1, paramInt2, paramMethodType2, paramInt1, 1, false) != 0)
/*  929 */       return false;
/*  930 */     if (diffParamTypes(paramMethodType1, paramInt2 + 1, paramMethodType2, paramInt2 + 1, i - paramInt2 - 1, false) != 0)
/*  931 */       return false;
/*  932 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeSwapArguments(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt1, int paramInt2)
/*      */   {
/*  940 */     if (paramInt1 == paramInt2)
/*  941 */       return paramMethodHandle;
/*  942 */     int i;
/*  942 */     if (paramInt1 > paramInt2) { i = paramInt1; paramInt1 = paramInt2; paramInt2 = i; }
/*  943 */     if (type2size(paramMethodType.parameterType(paramInt1)) != type2size(paramMethodType.parameterType(paramInt2)))
/*      */     {
/*  947 */       i = paramInt2 - paramInt1 + 1;
/*      */ 
/*  949 */       ArrayList localArrayList = new ArrayList(paramMethodHandle.type().parameterList());
/*  950 */       Collections.rotate(localArrayList.subList(paramInt1, paramInt1 + i), -1);
/*  951 */       MethodType localMethodType = MethodType.methodType(paramMethodHandle.type().returnType(), localArrayList);
/*  952 */       MethodHandle localMethodHandle1 = makeRotateArguments(localMethodType, paramMethodHandle, paramInt1, i, 1);
/*  953 */       assert (localMethodHandle1 != null);
/*  954 */       if (i == 2) return localMethodHandle1;
/*  955 */       MethodHandle localMethodHandle2 = makeRotateArguments(paramMethodType, localMethodHandle1, paramInt1, i - 1, -1);
/*  956 */       assert (localMethodHandle2 != null);
/*  957 */       return localMethodHandle2;
/*      */     }
/*  959 */     if (!canSwapArguments(paramMethodType, paramMethodHandle.type(), paramInt1, paramInt2))
/*  960 */       return null;
/*  961 */     Class localClass1 = paramMethodType.parameterType(paramInt1);
/*  962 */     Class localClass2 = paramMethodType.parameterType(paramInt2);
/*      */ 
/*  965 */     int j = paramMethodType.parameterSlotDepth(paramInt2 + 1);
/*  966 */     long l = makeSwapConv(6, paramInt1, basicType(localClass1), j, basicType(localClass2));
/*  967 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l);
/*      */   }
/*      */ 
/*      */   static int positiveRotation(int paramInt1, int paramInt2) {
/*  971 */     assert (paramInt1 > 0);
/*  972 */     if (paramInt2 >= 0) {
/*  973 */       if (paramInt2 < paramInt1)
/*  974 */         return paramInt2;
/*  975 */       return paramInt2 % paramInt1;
/*  976 */     }if (paramInt2 >= -paramInt1) {
/*  977 */       return paramInt2 + paramInt1;
/*      */     }
/*  979 */     return -1 - (-1 - paramInt2) % paramInt1 + paramInt1;
/*      */   }
/*      */ 
/*      */   static boolean canRotateArguments(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  988 */     if (!convOpSupported(7)) return false;
/*  989 */     paramInt3 = positiveRotation(paramInt2, paramInt3);
/*  990 */     if (paramInt3 == 0) return false;
/*  991 */     if ((paramInt3 > 1) && (paramInt3 < paramInt2 - 1)) {
/*  992 */       return false;
/*      */     }
/*  994 */     if (diffReturnTypes(paramMethodType1, paramMethodType2, false) != 0)
/*  995 */       return false;
/*  996 */     int i = paramMethodType1.parameterCount();
/*  997 */     if (paramMethodType2.parameterCount() != i)
/*  998 */       return false;
/*  999 */     if ((paramInt1 < 0) || (paramInt1 >= i)) return false;
/* 1000 */     int j = paramInt1 + paramInt2;
/* 1001 */     if (j > i) return false;
/* 1002 */     if (diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, paramInt1, false) != 0)
/* 1003 */       return false;
/* 1004 */     int k = paramInt2 - paramInt3; int m = paramInt3;
/*      */ 
/* 1006 */     if (diffParamTypes(paramMethodType1, paramInt1, paramMethodType2, j - k, k, false) != 0) {
/* 1007 */       return false;
/*      */     }
/* 1009 */     if (diffParamTypes(paramMethodType1, paramInt1 + k, paramMethodType2, paramInt1, m, false) != 0)
/* 1010 */       return false;
/* 1011 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeRotateArguments(MethodType paramMethodType, MethodHandle paramMethodHandle, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/* 1019 */     paramInt3 = positiveRotation(paramInt2, paramInt3);
/* 1020 */     if (!canRotateArguments(paramMethodType, paramMethodHandle.type(), paramInt1, paramInt2, paramInt3)) {
/* 1021 */       return null;
/*      */     }
/*      */ 
/* 1025 */     int i = paramInt1 + paramInt2;
/* 1026 */     int j = paramMethodType.parameterSlotDepth(paramInt1);
/* 1027 */     int k = paramMethodType.parameterSlotDepth(i - paramInt3);
/* 1028 */     int m = paramMethodType.parameterSlotDepth(i);
/* 1029 */     int n = j - k; assert (n > 0);
/* 1030 */     int i1 = k - m; assert (i1 > 0);
/*      */     int i2;
/*      */     int i3;
/*      */     int i4;
/*      */     int i5;
/* 1036 */     if (paramInt3 == 1)
/*      */     {
/* 1040 */       i2 = i - 1;
/* 1041 */       i3 = paramInt1;
/*      */ 
/* 1043 */       i4 = j + MethodHandleNatives.OP_ROT_ARGS_DOWN_LIMIT_BIAS;
/* 1044 */       i5 = i1;
/*      */     }
/*      */     else
/*      */     {
/* 1049 */       i2 = paramInt1;
/* 1050 */       i3 = i - 1;
/* 1051 */       i4 = m;
/* 1052 */       i5 = n;
/*      */     }
/* 1054 */     byte b1 = basicType(paramMethodType.parameterType(i2));
/* 1055 */     byte b2 = basicType(paramMethodType.parameterType(i3));
/* 1056 */     assert (i5 == type2size(b1));
/* 1057 */     long l = makeSwapConv(7, i2, b1, i4, b2);
/* 1058 */     return new AdapterMethodHandle(paramMethodHandle, paramMethodType, l);
/*      */   }
/*      */ 
/*      */   static boolean canSpreadArguments(MethodType paramMethodType1, MethodType paramMethodType2, Class<?> paramClass, int paramInt1, int paramInt2)
/*      */   {
/* 1064 */     if (!convOpSupported(11)) return false;
/* 1065 */     if (diffReturnTypes(paramMethodType1, paramMethodType2, false) != 0)
/* 1066 */       return false;
/* 1067 */     int i = paramMethodType1.parameterCount();
/*      */ 
/* 1069 */     if ((paramInt1 != 0) && (diffParamTypes(paramMethodType1, 0, paramMethodType2, 0, paramInt1, false) != 0))
/* 1070 */       return false;
/* 1071 */     int j = paramInt1 + paramInt2;
/* 1072 */     int k = i - (paramInt1 + 1);
/* 1073 */     if ((paramInt1 < 0) || (paramInt1 >= i) || (paramInt2 < 0) || (paramMethodType2.parameterCount() != j + k))
/*      */     {
/* 1076 */       return false;
/*      */     }
/* 1078 */     if ((k != 0) && (diffParamTypes(paramMethodType1, paramInt1 + 1, paramMethodType2, j, k, false) != 0)) {
/* 1079 */       return false;
/*      */     }
/* 1081 */     Class localClass1 = paramMethodType1.parameterType(paramInt1);
/* 1082 */     if ((localClass1 != paramClass) && (!canCheckCast(localClass1, paramClass)))
/* 1083 */       return false;
/* 1084 */     for (int m = 0; m < paramInt2; m++) {
/* 1085 */       Class localClass2 = VerifyType.spreadArgElementType(paramClass, m);
/* 1086 */       Class localClass3 = paramMethodType2.parameterType(paramInt1 + m);
/* 1087 */       if ((localClass2 == null) || (!canConvertArgument(localClass2, localClass3, 1)))
/* 1088 */         return false;
/*      */     }
/* 1090 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeSpreadArguments(MethodType paramMethodType, MethodHandle paramMethodHandle, Class<?> paramClass, int paramInt1, int paramInt2)
/*      */   {
/* 1098 */     MethodType localMethodType = paramMethodHandle.type();
/*      */ 
/* 1100 */     if ((!$assertionsDisabled) && (!canSpreadArguments(paramMethodType, localMethodType, paramClass, paramInt1, paramInt2))) throw new AssertionError("[newType, targetType, spreadArgType, spreadArgPos, spreadArgCount] = " + Arrays.asList(new Serializable[] { paramMethodType, localMethodType, paramClass, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
/*      */ 
/* 1103 */     int i = 14;
/* 1104 */     for (int j = 0; j < paramInt2; j++) {
/* 1105 */       Object localObject = VerifyType.spreadArgElementType(paramClass, j);
/* 1106 */       if (localObject == null) localObject = Object.class;
/* 1107 */       m = basicType((Class)localObject);
/* 1108 */       if (i == 14) i = m;
/* 1109 */       else if (i != m) i = 14;
/* 1110 */       if (i == 14) break;
/* 1111 */       localMethodType = localMethodType.changeParameterType(paramInt1 + j, (Class)localObject);
/*      */     }
/* 1113 */     paramMethodHandle = paramMethodHandle.asType(localMethodType);
/* 1114 */     j = 1;
/*      */ 
/* 1117 */     int k = paramInt1 + paramInt2;
/* 1118 */     int m = localMethodType.parameterSlotDepth(paramInt1);
/* 1119 */     int n = localMethodType.parameterSlotDepth(k);
/* 1120 */     assert (n == paramMethodType.parameterSlotDepth(paramInt1 + j));
/* 1121 */     int i1 = m - n;
/* 1122 */     assert (i1 >= paramInt2);
/* 1123 */     int i2 = -j + i1;
/* 1124 */     long l = makeSpreadConv(11, paramInt1, 12, i, i2);
/* 1125 */     AdapterMethodHandle localAdapterMethodHandle = new AdapterMethodHandle(paramMethodHandle, paramMethodType, l, paramClass);
/* 1126 */     assert (localAdapterMethodHandle.type().parameterType(paramInt1) == paramClass);
/* 1127 */     return localAdapterMethodHandle;
/*      */   }
/*      */ 
/*      */   static boolean canCollectArguments(MethodType paramMethodType1, MethodType paramMethodType2, int paramInt, boolean paramBoolean)
/*      */   {
/* 1133 */     if (!convOpSupported(paramBoolean ? 12 : 10)) return false;
/* 1134 */     int i = paramMethodType2.parameterCount();
/* 1135 */     Class localClass = paramMethodType2.returnType();
/*      */ 
/* 1138 */     if ((!$assertionsDisabled) && (localClass != Void.TYPE) && (paramMethodType1.parameterType(paramInt) != localClass)) throw new AssertionError(Arrays.asList(new Serializable[] { paramMethodType1, paramMethodType2, Integer.valueOf(paramInt), Integer.valueOf(i) }));
/*      */ 
/* 1140 */     return true;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeCollectArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt, boolean paramBoolean)
/*      */   {
/* 1146 */     assert (canCollectArguments(paramMethodHandle1.type(), paramMethodHandle2.type(), paramInt, paramBoolean));
/* 1147 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 1148 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 1149 */     int i = localMethodType2.parameterCount();
/* 1150 */     Class localClass = localMethodType2.returnType();
/* 1151 */     int j = localClass == Void.TYPE ? 0 : 1;
/* 1152 */     int k = localMethodType2.returnSlotCount();
/* 1153 */     MethodType localMethodType3 = localMethodType1.dropParameterTypes(paramInt, paramInt + j);
/*      */ 
/* 1155 */     if (!paramBoolean) {
/* 1156 */       localMethodType3 = localMethodType3.insertParameterTypes(paramInt, localMethodType2.parameterList());
/*      */     }
/* 1161 */     else if ((!$assertionsDisabled) && (diffParamTypes(localMethodType3, paramInt, localMethodType1, j, i, false) != 0)) throw new AssertionError(Arrays.asList(new Object[] { paramMethodHandle1, paramMethodHandle2, Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) }));
/*      */ 
/* 1166 */     int m = paramInt + i;
/* 1167 */     int n = localMethodType3.parameterSlotDepth(paramInt);
/* 1168 */     int i1 = localMethodType3.parameterSlotDepth(m);
/* 1169 */     int i2 = n - i1;
/* 1170 */     assert (i2 >= i);
/* 1171 */     if (!$assertionsDisabled) if (i1 != localMethodType1.parameterSlotDepth(paramInt + j + (paramBoolean ? i : 0))) throw new AssertionError();
/*      */ 
/* 1173 */     int i3 = basicType(localClass);
/* 1174 */     int i4 = 14;
/*      */ 
/* 1176 */     for (int i5 = 0; i5 < i; i5++) {
/* 1177 */       i6 = basicType(localMethodType2.parameterType(i5));
/* 1178 */       if (i4 == 14) i4 = i6;
/* 1179 */       else if (i4 != i6) i4 = 14;
/* 1180 */       if (i4 == 14) break;
/*      */     }
/* 1182 */     i5 = k;
/* 1183 */     if (!paramBoolean) i5 -= i2;
/* 1184 */     int i6 = m - 1;
/* 1185 */     long l = makeSpreadConv(paramBoolean ? 12 : 10, i6, i4, i3, i5);
/*      */ 
/* 1187 */     AdapterMethodHandle localAdapterMethodHandle = new AdapterMethodHandle(paramMethodHandle1, localMethodType3, l, paramMethodHandle2);
/* 1188 */     assert (localAdapterMethodHandle.type().parameterList().subList(paramInt, paramInt + i).equals(paramMethodHandle2.type().parameterList()));
/*      */ 
/* 1190 */     return localAdapterMethodHandle;
/*      */   }
/*      */ 
/*      */   String debugString()
/*      */   {
/* 1195 */     return MethodHandleStatics.getNameString(nonAdapter((MethodHandle)this.vmtarget), this);
/*      */   }
/*      */ 
/*      */   private static MethodHandle nonAdapter(MethodHandle paramMethodHandle) {
/* 1199 */     while ((paramMethodHandle instanceof AdapterMethodHandle)) {
/* 1200 */       paramMethodHandle = (MethodHandle)paramMethodHandle.vmtarget;
/*      */     }
/* 1202 */     return paramMethodHandle;
/*      */   }
/*      */ 
/*      */   static class AsVarargsCollector extends AdapterMethodHandle
/*      */   {
/*      */     final MethodHandle target;
/*      */     final Class<?> arrayType;
/*      */     MethodHandle cache;
/*      */ 
/*      */     AsVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass)
/*      */     {
/*  565 */       super(paramMethodHandle.type(), makeConv(0));
/*  566 */       this.target = paramMethodHandle;
/*  567 */       this.arrayType = paramClass;
/*  568 */       this.cache = paramMethodHandle.asCollector(paramClass, 0);
/*      */     }
/*      */ 
/*      */     public boolean isVarargsCollector()
/*      */     {
/*  573 */       return true;
/*      */     }
/*      */ 
/*      */     public MethodHandle asFixedArity()
/*      */     {
/*  578 */       return this.target;
/*      */     }
/*      */ 
/*      */     public MethodHandle asType(MethodType paramMethodType)
/*      */     {
/*  583 */       MethodType localMethodType = type();
/*  584 */       int i = localMethodType.parameterCount() - 1;
/*  585 */       int j = paramMethodType.parameterCount();
/*  586 */       if ((j == i + 1) && (localMethodType.parameterType(i).isAssignableFrom(paramMethodType.parameterType(i))))
/*      */       {
/*  589 */         return super.asType(paramMethodType);
/*      */       }
/*      */ 
/*  592 */       if (this.cache.type().parameterCount() == j) {
/*  593 */         return this.cache.asType(paramMethodType);
/*  595 */       }int k = j - i;
/*      */       MethodHandle localMethodHandle;
/*      */       try {
/*  598 */         localMethodHandle = this.target.asCollector(this.arrayType, k);
/*      */       } catch (IllegalArgumentException localIllegalArgumentException) {
/*  600 */         throw new WrongMethodTypeException("cannot build collector");
/*      */       }
/*  602 */       this.cache = localMethodHandle;
/*  603 */       return localMethodHandle.asType(paramMethodType);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.AdapterMethodHandle
 * JD-Core Version:    0.6.2
 */