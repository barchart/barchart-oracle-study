/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import sun.invoke.empty.Empty;
/*      */ import sun.invoke.util.ValueConversions;
/*      */ import sun.invoke.util.VerifyType;
/*      */ import sun.invoke.util.Wrapper;
/*      */ import sun.misc.Unsafe;
/*      */ import sun.reflect.Reflection;
/*      */ 
/*      */ abstract class MethodHandleImpl
/*      */ {
/*   50 */   private static final MemberName.Factory LOOKUP = MemberName.Factory.INSTANCE;
/*      */   static MethodHandle SELECT_ALTERNATIVE;
/*      */   static MethodHandle THROW_EXCEPTION;
/*      */ 
/*      */   static void initStatics()
/*      */   {
/*      */   }
/*      */ 
/*      */   static MethodHandle findMethod(MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass)
/*      */     throws IllegalAccessException
/*      */   {
/*   78 */     MethodType localMethodType = paramMemberName.getMethodType();
/*   79 */     if (!paramMemberName.isStatic())
/*      */     {
/*   82 */       localObject = paramMemberName.getDeclaringClass();
/*   83 */       localMethodType = localMethodType.insertParameterTypes(0, new Class[] { localObject });
/*      */     }
/*   85 */     Object localObject = new DirectMethodHandle(localMethodType, paramMemberName, paramBoolean, paramClass);
/*   86 */     if (!((DirectMethodHandle)localObject).isValid())
/*   87 */       throw paramMemberName.makeAccessException("no direct method handle", paramClass);
/*   88 */     assert (((DirectMethodHandle)localObject).type() == localMethodType);
/*   89 */     if (!paramMemberName.isVarargs())
/*   90 */       return localObject;
/*   91 */     int i = localMethodType.parameterCount();
/*   92 */     if (i != 0) {
/*   93 */       Class localClass = localMethodType.parameterType(i - 1);
/*   94 */       if (localClass.isArray())
/*   95 */         return AdapterMethodHandle.makeVarargsCollector((MethodHandle)localObject, localClass);
/*      */     }
/*   97 */     throw paramMemberName.makeAccessException("cannot make variable arity", null);
/*      */   }
/*      */ 
/*      */   static MethodHandle makeAllocator(MethodHandle paramMethodHandle)
/*      */   {
/*  102 */     MethodType localMethodType1 = paramMethodHandle.type();
/*  103 */     Class localClass = localMethodType1.parameterType(0);
/*      */ 
/*  105 */     if (AdapterMethodHandle.canCollectArguments(localMethodType1, MethodType.methodType(localClass), 0, true))
/*      */     {
/*  109 */       localMethodHandle1 = MethodHandles.identity(localClass);
/*  110 */       MethodType localMethodType2 = localMethodType1.insertParameterTypes(0, new Class[] { localClass }).changeReturnType(localClass);
/*  111 */       MethodHandle localMethodHandle2 = AdapterMethodHandle.makeCollectArguments(localMethodHandle1, paramMethodHandle, 1, false);
/*  112 */       assert (localMethodHandle2.type().equals(localMethodType2));
/*  113 */       localMethodType2 = localMethodType2.dropParameterTypes(0, 1);
/*  114 */       localMethodHandle2 = AdapterMethodHandle.makeCollectArguments(localMethodHandle2, localMethodHandle1, 0, true);
/*  115 */       AllocateObject localAllocateObject = new AllocateObject(localClass, null);
/*      */ 
/*  117 */       assert (localAllocateObject.type().equals(MethodType.methodType(localClass)));
/*  118 */       localMethodType2 = localMethodType2.dropParameterTypes(0, 1);
/*  119 */       MethodHandle localMethodHandle3 = foldArguments(localMethodHandle2, localMethodType2, 0, localAllocateObject);
/*  120 */       return localMethodHandle3;
/*      */     }
/*  122 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  123 */     MethodHandle localMethodHandle1 = AllocateObject.make(localClass, paramMethodHandle);
/*      */ 
/*  125 */     assert (localMethodHandle1.type().equals(localMethodType1.dropParameterTypes(0, 1).changeReturnType(localMethodType1.parameterType(0))));
/*      */ 
/*  127 */     return localMethodHandle1;
/*      */   }
/*      */ 
/*      */   static MethodHandle accessField(MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass)
/*      */   {
/*  277 */     FieldAccessor localFieldAccessor = new FieldAccessor(paramMemberName, paramBoolean);
/*  278 */     return localFieldAccessor;
/*      */   }
/*      */ 
/*      */   static MethodHandle accessArrayElement(Class<?> paramClass, boolean paramBoolean)
/*      */   {
/*  283 */     if (!paramClass.isArray())
/*  284 */       throw MethodHandleStatics.newIllegalArgumentException("not an array: " + paramClass);
/*  285 */     Class localClass = paramClass.getComponentType();
/*  286 */     MethodHandle[] arrayOfMethodHandle = (MethodHandle[])FieldAccessor.ARRAY_CACHE.get(localClass);
/*  287 */     if (arrayOfMethodHandle == null) {
/*  288 */       if (!FieldAccessor.doCache(localClass))
/*  289 */         return FieldAccessor.ahandle(paramClass, paramBoolean);
/*  290 */       arrayOfMethodHandle = new MethodHandle[] { FieldAccessor.ahandle(paramClass, false), FieldAccessor.ahandle(paramClass, true) };
/*      */ 
/*  294 */       if (arrayOfMethodHandle[0].type().parameterType(0) == Class.class) {
/*  295 */         arrayOfMethodHandle[0] = arrayOfMethodHandle[0].bindTo(localClass);
/*  296 */         arrayOfMethodHandle[1] = arrayOfMethodHandle[1].bindTo(localClass);
/*      */       }
/*  298 */       synchronized (FieldAccessor.ARRAY_CACHE) {
/*  299 */       }FieldAccessor.ARRAY_CACHE.put(localClass, arrayOfMethodHandle);
/*      */     }
/*  301 */     return arrayOfMethodHandle[0];
/*      */   }
/*      */ 
/*      */   static MethodHandle bindReceiver(MethodHandle paramMethodHandle, Object paramObject)
/*      */   {
/*  497 */     if (paramObject == null) return null;
/*  498 */     if (((paramMethodHandle instanceof AdapterMethodHandle)) && (((AdapterMethodHandle)paramMethodHandle).conversionOp() == 0))
/*      */     {
/*  501 */       Object localObject = MethodHandleNatives.getTargetInfo(paramMethodHandle);
/*  502 */       if ((localObject instanceof DirectMethodHandle)) {
/*  503 */         DirectMethodHandle localDirectMethodHandle = (DirectMethodHandle)localObject;
/*  504 */         if (localDirectMethodHandle.type().parameterType(0).isAssignableFrom(paramObject.getClass())) {
/*  505 */           BoundMethodHandle localBoundMethodHandle = new BoundMethodHandle(localDirectMethodHandle, paramObject, 0);
/*  506 */           MethodType localMethodType = paramMethodHandle.type().dropParameterTypes(0, 1);
/*  507 */           return convertArguments(localBoundMethodHandle, localMethodType, localBoundMethodHandle.type(), 0);
/*      */         }
/*      */       }
/*      */     }
/*  511 */     if ((paramMethodHandle instanceof DirectMethodHandle))
/*  512 */       return new BoundMethodHandle((DirectMethodHandle)paramMethodHandle, paramObject, 0);
/*  513 */     return null;
/*      */   }
/*      */ 
/*      */   static MethodHandle bindArgument(MethodHandle paramMethodHandle, int paramInt, Object paramObject)
/*      */   {
/*  525 */     return new BoundMethodHandle(paramMethodHandle, paramObject, paramInt);
/*      */   }
/*      */ 
/*      */   static MethodHandle permuteArguments(MethodHandle paramMethodHandle, MethodType paramMethodType1, MethodType paramMethodType2, int[] paramArrayOfInt)
/*      */   {
/*  532 */     assert (paramMethodType2.parameterCount() == paramMethodHandle.type().parameterCount());
/*  533 */     int i = paramMethodType2.parameterCount(); int j = paramMethodType1.parameterCount();
/*  534 */     if (paramArrayOfInt.length != i) {
/*  535 */       throw MethodHandleStatics.newIllegalArgumentException("wrong number of arguments in permutation");
/*      */     }
/*  537 */     Class[] arrayOfClass = new Class[i];
/*  538 */     for (int k = 0; k < i; k++)
/*  539 */       arrayOfClass[k] = paramMethodType1.parameterType(paramArrayOfInt[k]);
/*  540 */     MethodType localMethodType1 = MethodType.methodType(paramMethodType2.returnType(), arrayOfClass);
/*  541 */     paramMethodHandle = convertArguments(paramMethodHandle, localMethodType1, paramMethodType2, 0);
/*  542 */     assert (paramMethodHandle != null);
/*  543 */     paramMethodType2 = paramMethodHandle.type();
/*  544 */     ArrayList localArrayList1 = new ArrayList();
/*  545 */     ArrayList localArrayList2 = new ArrayList();
/*  546 */     ArrayList localArrayList3 = new ArrayList();
/*  547 */     ArrayList localArrayList4 = new ArrayList();
/*      */ 
/*  550 */     for (int m = 0; m < i; m++) {
/*  551 */       localArrayList2.add(Integer.valueOf(paramArrayOfInt[m] * 10));
/*      */     }
/*      */ 
/*  554 */     for (m = 0; m < j; m++)
/*  555 */       if (localArrayList2.contains(Integer.valueOf(m * 10))) {
/*  556 */         localArrayList1.add(Integer.valueOf(m * 10));
/*      */       }
/*      */       else
/*  559 */         localArrayList3.add(Integer.valueOf(m));
/*      */     int n;
/*      */     int i1;
/*      */     int i2;
/*  563 */     while (localArrayList2.size() > localArrayList1.size()) {
/*  564 */       for (m = 0; m < localArrayList2.size(); m++) {
/*  565 */         n = ((Integer)localArrayList2.get(m)).intValue();
/*  566 */         i1 = localArrayList2.indexOf(Integer.valueOf(n));
/*  567 */         if (i1 != m)
/*      */         {
/*  569 */           i2 = j++ * 10;
/*  570 */           localArrayList2.set(m, Integer.valueOf(i2));
/*  571 */           localArrayList4.add(Integer.valueOf(localArrayList1.indexOf(Integer.valueOf(n))));
/*  572 */           localArrayList1.add(Integer.valueOf(i2));
/*      */         }
/*      */       }
/*      */     }
/*  576 */     assert (localArrayList2.size() == localArrayList1.size());
/*  577 */     m = localArrayList1.size();
/*      */     int i3;
/*      */     Object localObject2;
/*  578 */     while (!localArrayList2.equals(localArrayList1))
/*      */     {
/*  581 */       n = -100; i1 = 0;
/*  582 */       i2 = -100; i3 = 0;
/*      */       int i5;
/*      */       int i6;
/*  583 */       for (int i4 = 0; i4 < m; i4++) {
/*  584 */         i5 = ((Integer)localArrayList2.get(i4)).intValue();
/*      */ 
/*  586 */         if (i5 == i2 + 10) {
/*  587 */           i2 = i5;
/*  588 */           i3++;
/*  589 */           if (i1 < i3) {
/*  590 */             i1 = i3;
/*  591 */             n = i2;
/*      */           }
/*      */         }
/*      */         else {
/*  595 */           i3 = 0;
/*  596 */           i2 = -100;
/*      */ 
/*  598 */           i6 = ((Integer)localArrayList1.get(i4)).intValue();
/*      */ 
/*  600 */           if ((i5 != i6) && (i5 >= i6 - 10) && (i5 <= i6 + 10))
/*      */           {
/*  603 */             i2 = i5;
/*  604 */             i3 = 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       int i7;
/*  608 */       if (i1 >= 2)
/*      */       {
/*  612 */         i4 = localArrayList2.indexOf(Integer.valueOf(n));
/*  613 */         i5 = localArrayList1.indexOf(Integer.valueOf(n));
/*  614 */         i6 = i4 - i5;
/*  615 */         i7 = i4 - (i1 - 1);
/*  616 */         int i8 = i5 - (i1 - 1);
/*  617 */         assert ((i4 | i7 | i5 | i8) >= 0);
/*      */ 
/*  619 */         int i9 = Math.min(i7, i8);
/*  620 */         int i10 = Math.max(i4, i5);
/*  621 */         int i11 = 0;
/*  622 */         for (int i12 = i9; i12 <= i10; i12++) {
/*  623 */           if (((Integer)localArrayList2.get(i12)).intValue() != ((Integer)localArrayList1.get(i12)).intValue())
/*  624 */             i11++;
/*      */         }
/*  626 */         List localList2 = localArrayList2.subList(i9, i10 + 1);
/*  627 */         Collections.rotate(localList2, -i6);
/*  628 */         for (int i13 = i9; i13 <= i10; i13++) {
/*  629 */           if (((Integer)localArrayList2.get(i13)).intValue() != ((Integer)localArrayList1.get(i13)).intValue())
/*  630 */             i11--;
/*      */         }
/*  632 */         if (i11 >= 2)
/*      */         {
/*  634 */           List localList3 = Arrays.asList(paramMethodType2.parameterArray());
/*  635 */           Collections.rotate(localList3.subList(i9, i10 + 1), -i6);
/*  636 */           MethodType localMethodType3 = MethodType.methodType(paramMethodType2.returnType(), localList3);
/*  637 */           MethodHandle localMethodHandle = AdapterMethodHandle.makeRotateArguments(localMethodType3, paramMethodHandle, i9, localList2.size(), i6);
/*      */ 
/*  640 */           if (localMethodHandle != null)
/*      */           {
/*  642 */             paramMethodHandle = localMethodHandle;
/*  643 */             paramMethodType2 = localMethodType3;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  648 */           Collections.rotate(localList2, i6);
/*      */         }
/*      */       }
/*      */       else {
/*  652 */         localObject2 = Arrays.asList(paramMethodType2.parameterArray());
/*  653 */         for (i5 = 0; i5 < m; i5++)
/*      */         {
/*  655 */           i6 = ((Integer)localArrayList1.get(i5)).intValue();
/*  656 */           if (i6 != ((Integer)localArrayList2.get(i5)).intValue())
/*      */           {
/*  658 */             i7 = localArrayList2.indexOf(Integer.valueOf(i6));
/*  659 */             Collections.swap((List)localObject2, i5, i7);
/*  660 */             MethodType localMethodType2 = MethodType.methodType(paramMethodType2.returnType(), (List)localObject2);
/*  661 */             paramMethodHandle = AdapterMethodHandle.makeSwapArguments(localMethodType2, paramMethodHandle, i5, i7);
/*  662 */             if (paramMethodHandle == null) throw MethodHandleStatics.newIllegalArgumentException("cannot swap");
/*  663 */             assert (paramMethodHandle.type() == localMethodType2);
/*  664 */             paramMethodType2 = localMethodType2;
/*  665 */             Collections.swap(localArrayList2, i5, i7);
/*      */           }
/*      */         }
/*      */ 
/*  669 */         assert (localArrayList2.equals(localArrayList1));
/*      */       }
/*      */     }
/*      */     Object localObject1;
/*  671 */     while (!localArrayList4.isEmpty())
/*      */     {
/*  673 */       n = localArrayList4.size() - 1;
/*  674 */       i1 = ((Integer)localArrayList4.get(n)).intValue(); i2 = 1;
/*  675 */       while (n - 1 >= 0) {
/*  676 */         i3 = ((Integer)localArrayList4.get(n - 1)).intValue();
/*  677 */         if (i3 != i1 - 1) break;
/*  678 */         i1--;
/*  679 */         i2++;
/*  680 */         n--;
/*      */       }
/*      */ 
/*  683 */       localArrayList4.subList(n, localArrayList4.size()).clear();
/*      */ 
/*  685 */       localObject1 = paramMethodType2.parameterList();
/*  686 */       localObject1 = ((List)localObject1).subList(0, ((List)localObject1).size() - i2);
/*  687 */       localObject2 = MethodType.methodType(paramMethodType2.returnType(), (List)localObject1);
/*  688 */       paramMethodHandle = AdapterMethodHandle.makeDupArguments((MethodType)localObject2, paramMethodHandle, i1, i2);
/*  689 */       if (paramMethodHandle == null)
/*  690 */         throw MethodHandleStatics.newIllegalArgumentException("cannot dup");
/*  691 */       paramMethodType2 = paramMethodHandle.type();
/*      */     }
/*  693 */     while (!localArrayList3.isEmpty())
/*      */     {
/*  695 */       n = ((Integer)localArrayList3.get(0)).intValue(); i1 = 1;
/*  696 */       while (i1 < localArrayList3.size()) {
/*  697 */         i2 = ((Integer)localArrayList3.get(i1)).intValue();
/*  698 */         if (i2 != n + i1) break;
/*  699 */         i1++;
/*      */       }
/*      */ 
/*  702 */       localArrayList3.subList(0, i1).clear();
/*  703 */       List localList1 = paramMethodType1.parameterList().subList(n, n + i1);
/*      */ 
/*  705 */       localObject1 = paramMethodType2.insertParameterTypes(n, localList1);
/*  706 */       paramMethodHandle = AdapterMethodHandle.makeDropArguments((MethodType)localObject1, paramMethodHandle, n, i1);
/*  707 */       if (paramMethodHandle == null) throw MethodHandleStatics.newIllegalArgumentException("cannot drop");
/*  708 */       paramMethodType2 = paramMethodHandle.type();
/*      */     }
/*  710 */     paramMethodHandle = convertArguments(paramMethodHandle, paramMethodType1, paramMethodType2, 0);
/*  711 */     assert (paramMethodHandle != null);
/*  712 */     return paramMethodHandle;
/*      */   }
/*      */ 
/*      */   static MethodHandle convertArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int paramInt)
/*      */   {
/*  717 */     MethodType localMethodType = paramMethodHandle.type();
/*  718 */     if (localMethodType.equals(paramMethodType))
/*  719 */       return paramMethodHandle;
/*  720 */     assert ((paramInt > 1) || (localMethodType.isConvertibleTo(paramMethodType)));
/*  721 */     MethodHandle localMethodHandle = null;
/*  722 */     Class localClass1 = localMethodType.returnType();
/*  723 */     Class localClass2 = paramMethodType.returnType();
/*  724 */     if (!VerifyType.isNullConversion(localClass1, localClass2)) {
/*  725 */       if (localClass1 == Void.TYPE) {
/*  726 */         localObject1 = localClass2.isPrimitive() ? Wrapper.forPrimitiveType(localClass2) : Wrapper.OBJECT;
/*  727 */         localMethodHandle = ValueConversions.zeroConstantFunction((Wrapper)localObject1);
/*      */       } else {
/*  729 */         localMethodHandle = MethodHandles.identity(localClass2);
/*  730 */         localMethodHandle = convertArguments(localMethodHandle, localMethodHandle.type().changeParameterType(0, localClass1), paramInt);
/*      */       }
/*  732 */       paramMethodType = paramMethodType.changeReturnType(localClass1);
/*      */     }
/*  734 */     Object localObject1 = null;
/*  735 */     Object localObject2 = null;
/*      */     try {
/*  737 */       localObject1 = convertArguments(paramMethodHandle, paramMethodType, localMethodType, paramInt);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  739 */       localObject2 = localIllegalArgumentException;
/*      */     }
/*  741 */     if (localObject1 == null) {
/*  742 */       WrongMethodTypeException localWrongMethodTypeException = new WrongMethodTypeException("cannot convert to " + paramMethodType + ": " + paramMethodHandle);
/*  743 */       localWrongMethodTypeException.initCause(localObject2);
/*  744 */       throw localWrongMethodTypeException;
/*      */     }
/*  746 */     if (localMethodHandle != null)
/*  747 */       localObject1 = MethodHandles.filterReturnValue((MethodHandle)localObject1, localMethodHandle);
/*  748 */     return localObject1;
/*      */   }
/*      */ 
/*      */   static MethodHandle convertArguments(MethodHandle paramMethodHandle, MethodType paramMethodType1, MethodType paramMethodType2, int paramInt)
/*      */   {
/*  755 */     assert (paramMethodType2.parameterCount() == paramMethodHandle.type().parameterCount());
/*  756 */     if (paramMethodType1 == paramMethodType2)
/*  757 */       return paramMethodHandle;
/*  758 */     if (paramMethodType2.parameterCount() != paramMethodType1.parameterCount())
/*  759 */       throw MethodHandleStatics.newIllegalArgumentException("mismatched parameter count", paramMethodType2, paramMethodType1);
/*  760 */     MethodHandle localMethodHandle1 = AdapterMethodHandle.makePairwiseConvert(paramMethodType1, paramMethodHandle, paramInt);
/*  761 */     if (localMethodHandle1 != null) {
/*  762 */       return localMethodHandle1;
/*      */     }
/*      */ 
/*  765 */     int i = paramMethodType2.parameterCount();
/*  766 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*      */ 
/*  772 */     MethodType localMethodType = MethodType.genericMethodType(i);
/*  773 */     MethodHandle localMethodHandle2 = AdapterMethodHandle.makePairwiseConvert(localMethodType, paramMethodHandle, paramInt);
/*  774 */     if (localMethodHandle2 == null)
/*  775 */       localMethodHandle2 = FromGeneric.make(paramMethodHandle);
/*  776 */     localMethodHandle1 = AdapterMethodHandle.makePairwiseConvert(paramMethodType1, localMethodHandle2, paramInt);
/*  777 */     if (localMethodHandle1 != null)
/*  778 */       return localMethodHandle1;
/*  779 */     return ToGeneric.make(paramMethodType1, localMethodHandle2);
/*      */   }
/*      */ 
/*      */   static MethodHandle spreadArguments(MethodHandle paramMethodHandle, Class<?> paramClass, int paramInt) {
/*  783 */     MethodType localMethodType1 = paramMethodHandle.type();
/*  784 */     int i = localMethodType1.parameterCount();
/*  785 */     int j = i - paramInt;
/*  786 */     MethodType localMethodType2 = localMethodType1.dropParameterTypes(j, i).insertParameterTypes(j, new Class[] { paramClass });
/*      */ 
/*  789 */     return spreadArguments(paramMethodHandle, localMethodType2, j, paramClass, paramInt);
/*      */   }
/*      */ 
/*      */   static MethodHandle spreadArgumentsFromPos(MethodHandle paramMethodHandle, MethodType paramMethodType, int paramInt) {
/*  793 */     int i = paramMethodHandle.type().parameterCount() - paramInt;
/*  794 */     return spreadArguments(paramMethodHandle, paramMethodType, paramInt, [Ljava.lang.Object.class, i);
/*      */   }
/*      */ 
/*      */   static MethodHandle spreadArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int paramInt1, Class<?> paramClass, int paramInt2)
/*      */   {
/*  802 */     MethodType localMethodType = paramMethodHandle.type();
/*      */ 
/*  804 */     assert (paramInt2 == localMethodType.parameterCount() - paramInt1);
/*  805 */     assert (paramMethodType.parameterType(paramInt1) == paramClass);
/*  806 */     return AdapterMethodHandle.makeSpreadArguments(paramMethodType, paramMethodHandle, paramClass, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   static MethodHandle collectArguments(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
/*      */   {
/*  812 */     MethodType localMethodType1 = paramMethodHandle1.type();
/*  813 */     Class localClass = paramMethodHandle2.type().returnType();
/*  814 */     assert (localClass != Void.TYPE);
/*  815 */     if (localClass != localMethodType1.parameterType(paramInt))
/*  816 */       paramMethodHandle1 = paramMethodHandle1.asType(localMethodType1.changeParameterType(paramInt, localClass));
/*  817 */     MethodType localMethodType2 = localMethodType1.dropParameterTypes(paramInt, paramInt + 1).insertParameterTypes(paramInt, paramMethodHandle2.type().parameterArray());
/*      */ 
/*  820 */     return collectArguments(paramMethodHandle1, localMethodType2, paramInt, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   static MethodHandle collectArguments(MethodHandle paramMethodHandle1, MethodType paramMethodType, int paramInt, MethodHandle paramMethodHandle2)
/*      */   {
/*  826 */     MethodType localMethodType1 = paramMethodHandle1.type();
/*      */ 
/*  828 */     MethodType localMethodType2 = paramMethodHandle2.type();
/*      */ 
/*  830 */     assert (paramMethodType.parameterCount() == paramInt + localMethodType2.parameterCount());
/*  831 */     assert (localMethodType1.parameterCount() == paramInt + 1);
/*  832 */     MethodHandle localMethodHandle1 = null;
/*  833 */     if (AdapterMethodHandle.canCollectArguments(localMethodType1, localMethodType2, paramInt, false)) {
/*  834 */       localMethodHandle1 = AdapterMethodHandle.makeCollectArguments(paramMethodHandle1, paramMethodHandle2, paramInt, false);
/*      */     }
/*  836 */     if (localMethodHandle1 == null) {
/*  837 */       assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  838 */       MethodHandle localMethodHandle2 = convertArguments(paramMethodHandle1, localMethodType1.generic(), localMethodType1, 0);
/*  839 */       MethodHandle localMethodHandle3 = convertArguments(paramMethodHandle2, localMethodType2.generic(), localMethodType2, 0);
/*  840 */       if ((localMethodHandle2 == null) || (localMethodHandle3 == null)) return null;
/*  841 */       MethodHandle localMethodHandle4 = FilterGeneric.makeArgumentCollector(localMethodHandle3, localMethodHandle2);
/*  842 */       localMethodHandle1 = convertArguments(localMethodHandle4, paramMethodType, localMethodHandle4.type(), 0);
/*      */     }
/*  844 */     return localMethodHandle1;
/*      */   }
/*      */ 
/*      */   static MethodHandle filterArgument(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
/*      */   {
/*  850 */     Object localObject1 = paramMethodHandle1.type();
/*  851 */     Object localObject2 = paramMethodHandle2.type();
/*  852 */     assert (((MethodType)localObject2).parameterCount() == 1);
/*  853 */     MethodHandle localMethodHandle = null;
/*  854 */     if (AdapterMethodHandle.canCollectArguments((MethodType)localObject1, (MethodType)localObject2, paramInt, false)) {
/*  855 */       localMethodHandle = AdapterMethodHandle.makeCollectArguments(paramMethodHandle1, paramMethodHandle2, paramInt, false);
/*  856 */       if (localMethodHandle != null) return localMethodHandle;
/*      */     }
/*  858 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  859 */     MethodType localMethodType1 = ((MethodType)localObject1).changeParameterType(paramInt, ((MethodType)localObject2).parameterType(0));
/*  860 */     MethodType localMethodType2 = ((MethodType)localObject1).generic();
/*  861 */     if (localObject1 != localMethodType2) {
/*  862 */       paramMethodHandle1 = convertArguments(paramMethodHandle1, localMethodType2, (MethodType)localObject1, 0);
/*  863 */       localObject1 = localMethodType2;
/*      */     }
/*  865 */     MethodType localMethodType3 = ((MethodType)localObject2).generic();
/*  866 */     if (localObject2 != localMethodType3) {
/*  867 */       paramMethodHandle2 = convertArguments(paramMethodHandle2, localMethodType3, (MethodType)localObject2, 0);
/*  868 */       localObject2 = localMethodType3;
/*      */     }
/*  870 */     if (localObject2 == localObject1)
/*      */     {
/*  872 */       localMethodHandle = FilterOneArgument.make(paramMethodHandle2, paramMethodHandle1);
/*      */     }
/*  874 */     else localMethodHandle = FilterGeneric.makeArgumentFilter(paramInt, paramMethodHandle2, paramMethodHandle1);
/*      */ 
/*  876 */     if (localMethodHandle.type() != localMethodType1)
/*  877 */       localMethodHandle = localMethodHandle.asType(localMethodType1);
/*  878 */     return localMethodHandle;
/*      */   }
/*      */ 
/*      */   static MethodHandle foldArguments(MethodHandle paramMethodHandle1, MethodType paramMethodType, int paramInt, MethodHandle paramMethodHandle2)
/*      */   {
/*  885 */     MethodType localMethodType1 = paramMethodHandle1.type();
/*  886 */     MethodType localMethodType2 = paramMethodHandle2.type();
/*  887 */     if (AdapterMethodHandle.canCollectArguments(localMethodType1, localMethodType2, paramInt, true)) {
/*  888 */       localMethodHandle1 = AdapterMethodHandle.makeCollectArguments(paramMethodHandle1, paramMethodHandle2, paramInt, true);
/*  889 */       if (localMethodHandle1 != null) return localMethodHandle1;
/*      */     }
/*  891 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  892 */     if (paramInt != 0) return null;
/*  893 */     MethodHandle localMethodHandle1 = convertArguments(paramMethodHandle1, localMethodType1.generic(), localMethodType1, 0);
/*  894 */     MethodHandle localMethodHandle2 = convertArguments(paramMethodHandle2, localMethodType2.generic(), localMethodType2, 0);
/*  895 */     if (localMethodType2.returnType() == Void.TYPE) {
/*  896 */       localMethodHandle1 = dropArguments(localMethodHandle1, localMethodType1.generic().insertParameterTypes(paramInt, new Class[] { Object.class }), paramInt);
/*      */     }
/*  898 */     if ((localMethodHandle1 == null) || (localMethodHandle2 == null)) return null;
/*  899 */     MethodHandle localMethodHandle3 = FilterGeneric.makeArgumentFolder(localMethodHandle2, localMethodHandle1);
/*  900 */     return convertArguments(localMethodHandle3, paramMethodType, localMethodHandle3.type(), 0);
/*      */   }
/*      */ 
/*      */   static MethodHandle dropArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int paramInt)
/*      */   {
/*  906 */     int i = paramMethodType.parameterCount() - paramMethodHandle.type().parameterCount();
/*  907 */     MethodHandle localMethodHandle = AdapterMethodHandle.makeDropArguments(paramMethodType, paramMethodHandle, paramInt, i);
/*  908 */     if (localMethodHandle != null)
/*  909 */       return localMethodHandle;
/*  910 */     throw new UnsupportedOperationException("NYI");
/*      */   }
/*      */ 
/*      */   static MethodHandle selectAlternative(boolean paramBoolean, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/* 1040 */     return paramBoolean ? paramMethodHandle1 : paramMethodHandle2;
/*      */   }
/*      */ 
/*      */   static MethodHandle selectAlternative()
/*      */   {
/* 1045 */     if (SELECT_ALTERNATIVE != null) return SELECT_ALTERNATIVE; try
/*      */     {
/* 1047 */       SELECT_ALTERNATIVE = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, "selectAlternative", MethodType.methodType(MethodHandle.class, Boolean.TYPE, new Class[] { MethodHandle.class, MethodHandle.class }));
/*      */     }
/*      */     catch (ReflectiveOperationException localReflectiveOperationException)
/*      */     {
/* 1051 */       throw new RuntimeException(localReflectiveOperationException);
/*      */     }
/* 1053 */     return SELECT_ALTERNATIVE;
/*      */   }
/*      */ 
/*      */   static MethodHandle makeGuardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3)
/*      */   {
/* 1065 */     assert (paramMethodHandle1.type().returnType() == Boolean.TYPE);
/* 1066 */     MethodType localMethodType1 = paramMethodHandle2.type();
/* 1067 */     MethodType localMethodType2 = localMethodType1.insertParameterTypes(0, new Class[] { Boolean.TYPE });
/* 1068 */     if ((AdapterMethodHandle.canCollectArguments(localMethodType2, paramMethodHandle1.type(), 0, true)) && (GuardWithTest.preferRicochetFrame(localMethodType1)))
/*      */     {
/* 1071 */       assert (paramMethodHandle2.type().equals(paramMethodHandle3.type()));
/* 1072 */       MethodHandle localMethodHandle1 = MethodHandles.exactInvoker(paramMethodHandle2.type());
/* 1073 */       MethodHandle localMethodHandle2 = selectAlternative();
/* 1074 */       localMethodHandle2 = bindArgument(localMethodHandle2, 2, CountingMethodHandle.wrap(paramMethodHandle3));
/* 1075 */       localMethodHandle2 = bindArgument(localMethodHandle2, 1, CountingMethodHandle.wrap(paramMethodHandle2));
/*      */ 
/* 1077 */       MethodHandle localMethodHandle3 = filterArgument(localMethodHandle1, 0, localMethodHandle2);
/* 1078 */       assert (localMethodHandle3.type().parameterType(0) == Boolean.TYPE);
/* 1079 */       MethodHandle localMethodHandle4 = foldArguments(localMethodHandle3, localMethodHandle3.type().dropParameterTypes(0, 1), 0, paramMethodHandle1);
/* 1080 */       return localMethodHandle4;
/*      */     }
/* 1082 */     return GuardWithTest.make(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */   }
/*      */ 
/*      */   static MethodHandle makeGuardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2)
/*      */   {
/* 1219 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 1220 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 1221 */     int i = localMethodType1.parameterCount();
/* 1222 */     if (i < GuardWithCatch.INVOKES.length) {
/* 1223 */       localMethodType3 = localMethodType1.generic();
/* 1224 */       localMethodType4 = localMethodType3.insertParameterTypes(0, new Class[] { Throwable.class });
/*      */ 
/* 1226 */       localMethodHandle1 = convertArguments(paramMethodHandle1, localMethodType3, localMethodType1, 2);
/* 1227 */       localMethodHandle2 = convertArguments(paramMethodHandle2, localMethodType4, localMethodType2, 2);
/* 1228 */       localGuardWithCatch = new GuardWithCatch(localMethodHandle1, paramClass, localMethodHandle2);
/* 1229 */       if ((localMethodHandle1 == null) || (localMethodHandle2 == null) || (localGuardWithCatch == null)) return null;
/* 1230 */       return convertArguments(localGuardWithCatch, localMethodType1, localMethodType3, 2);
/*      */     }
/* 1232 */     MethodType localMethodType3 = MethodType.genericMethodType(0, true);
/* 1233 */     MethodType localMethodType4 = localMethodType3.insertParameterTypes(0, new Class[] { Throwable.class });
/* 1234 */     MethodHandle localMethodHandle1 = spreadArgumentsFromPos(paramMethodHandle1, localMethodType3, 0);
/* 1235 */     paramMethodHandle2 = paramMethodHandle2.asType(localMethodType2.changeParameterType(0, Throwable.class));
/* 1236 */     MethodHandle localMethodHandle2 = spreadArgumentsFromPos(paramMethodHandle2, localMethodType4, 1);
/* 1237 */     GuardWithCatch localGuardWithCatch = new GuardWithCatch(GuardWithCatch.VARARGS_INVOKE, localMethodHandle1, paramClass, localMethodHandle2);
/* 1238 */     if ((localMethodHandle1 == null) || (localMethodHandle2 == null) || (localGuardWithCatch == null)) return null;
/* 1239 */     return collectArguments(localGuardWithCatch, localMethodType1, 0, ValueConversions.varargsArray(i)).asType(localMethodType1);
/*      */   }
/*      */ 
/*      */   static MethodHandle throwException(MethodType paramMethodType)
/*      */   {
/* 1245 */     return AdapterMethodHandle.makeRetypeRaw(paramMethodType, throwException());
/*      */   }
/*      */ 
/*      */   static MethodHandle throwException()
/*      */   {
/* 1250 */     if (THROW_EXCEPTION != null) return THROW_EXCEPTION; try
/*      */     {
/* 1252 */       THROW_EXCEPTION = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, "throwException", MethodType.methodType(Empty.class, Throwable.class));
/*      */     }
/*      */     catch (ReflectiveOperationException localReflectiveOperationException)
/*      */     {
/* 1256 */       throw new RuntimeException(localReflectiveOperationException);
/*      */     }
/* 1258 */     return THROW_EXCEPTION;
/*      */   }
/* 1260 */   static <T extends Throwable> Empty throwException(T paramT) throws Throwable { throw paramT; }
/*      */ 
/*      */ 
/*      */   static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass)
/*      */   {
/* 1272 */     return BindCaller.bindCaller(paramMethodHandle, paramClass);
/*      */   }
/*      */ 
/*      */   static final class AllocateObject<C> extends BoundMethodHandle
/*      */   {
/*      */     private static final Unsafe unsafe;
/*      */     private final Class<C> allocateClass;
/*      */     private final MethodHandle rawConstructor;
/*      */     static final MethodHandle[] INVOKES;
/*      */     static final MethodHandle VARARGS_INVOKE;
/*      */     static final MethodHandle ALLOCATE;
/*      */     static final MethodType[] CON_TYPES;
/*  266 */     static final MethodType VARARGS_CON_TYPE = makeConType(VARARGS_INVOKE);
/*      */ 
/*      */     private AllocateObject(MethodHandle paramMethodHandle1, Class<C> paramClass, MethodHandle paramMethodHandle2)
/*      */     {
/*  138 */       super();
/*  139 */       this.allocateClass = paramClass;
/*  140 */       this.rawConstructor = paramMethodHandle2;
/*  141 */       assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*      */     }
/*      */ 
/*      */     private AllocateObject(Class<C> paramClass) {
/*  145 */       super();
/*  146 */       this.allocateClass = paramClass;
/*  147 */       this.rawConstructor = null;
/*      */     }
/*      */     static MethodHandle make(Class<?> paramClass, MethodHandle paramMethodHandle) {
/*  150 */       assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  151 */       MethodType localMethodType1 = paramMethodHandle.type();
/*  152 */       assert (localMethodType1.parameterType(0) == paramClass);
/*  153 */       MethodType localMethodType2 = localMethodType1.dropParameterTypes(0, 1).changeReturnType(paramClass);
/*  154 */       int i = localMethodType1.parameterCount() - 1;
/*  155 */       if (i < INVOKES.length) {
/*  156 */         localMethodHandle1 = INVOKES[i];
/*  157 */         localMethodType3 = CON_TYPES[i];
/*  158 */         localMethodHandle2 = MethodHandleImpl.convertArguments(paramMethodHandle, localMethodType3, localMethodType1, 0);
/*  159 */         if (localMethodHandle2 == null) return null;
/*  160 */         localAllocateObject = new AllocateObject(localMethodHandle1, paramClass, localMethodHandle2);
/*  161 */         assert (localAllocateObject.type() == localMethodType2.generic());
/*  162 */         return MethodHandleImpl.convertArguments(localAllocateObject, localMethodType2, localAllocateObject.type(), 0);
/*      */       }
/*  164 */       MethodHandle localMethodHandle1 = VARARGS_INVOKE;
/*  165 */       MethodType localMethodType3 = CON_TYPES[i];
/*  166 */       MethodHandle localMethodHandle2 = MethodHandleImpl.spreadArgumentsFromPos(paramMethodHandle, localMethodType3, 1);
/*  167 */       if (localMethodHandle2 == null) return null;
/*  168 */       AllocateObject localAllocateObject = new AllocateObject(localMethodHandle1, paramClass, localMethodHandle2);
/*  169 */       return MethodHandleImpl.collectArguments(localAllocateObject, localMethodType2, 1, null);
/*      */     }
/*      */ 
/*      */     String debugString()
/*      */     {
/*  174 */       return MethodHandleStatics.addTypeString(this.allocateClass.getSimpleName(), this);
/*      */     }
/*      */ 
/*      */     private C allocate() throws InstantiationException {
/*  178 */       return unsafe.allocateInstance(this.allocateClass);
/*      */     }
/*      */     private C invoke_V(Object[] paramArrayOfObject) throws Throwable {
/*  181 */       Object localObject = allocate();
/*  182 */       this.rawConstructor.invokeExact(localObject, paramArrayOfObject);
/*  183 */       return localObject;
/*      */     }
/*      */     private C invoke_L0() throws Throwable {
/*  186 */       Object localObject = allocate();
/*  187 */       this.rawConstructor.invokeExact(localObject);
/*  188 */       return localObject;
/*      */     }
/*      */     private C invoke_L1(Object paramObject) throws Throwable {
/*  191 */       Object localObject = allocate();
/*  192 */       this.rawConstructor.invokeExact(localObject, paramObject);
/*  193 */       return localObject;
/*      */     }
/*      */     private C invoke_L2(Object paramObject1, Object paramObject2) throws Throwable {
/*  196 */       Object localObject = allocate();
/*  197 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2);
/*  198 */       return localObject;
/*      */     }
/*      */     private C invoke_L3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  201 */       Object localObject = allocate();
/*  202 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3);
/*  203 */       return localObject;
/*      */     }
/*      */     private C invoke_L4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  206 */       Object localObject = allocate();
/*  207 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3, paramObject4);
/*  208 */       return localObject;
/*      */     }
/*      */     private C invoke_L5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  211 */       Object localObject = allocate();
/*  212 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*  213 */       return localObject;
/*      */     }
/*      */     private C invoke_L6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  216 */       Object localObject = allocate();
/*  217 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*  218 */       return localObject;
/*      */     }
/*      */     private C invoke_L7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  221 */       Object localObject = allocate();
/*  222 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*  223 */       return localObject;
/*      */     }
/*      */     private C invoke_L8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*  226 */       Object localObject = allocate();
/*  227 */       this.rawConstructor.invokeExact(localObject, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*  228 */       return localObject;
/*      */     }
/*      */     static MethodHandle[] makeInvokes() {
/*  231 */       ArrayList localArrayList = new ArrayList();
/*  232 */       MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP;
/*      */       while (true) {
/*  234 */         int i = localArrayList.size();
/*  235 */         String str = "invoke_L" + i;
/*  236 */         MethodHandle localMethodHandle = null;
/*      */         try {
/*  238 */           localMethodHandle = localLookup.findVirtual(AllocateObject.class, str, MethodType.genericMethodType(i));
/*      */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */         }
/*  241 */         if (localMethodHandle == null) break;
/*  242 */         localArrayList.add(localMethodHandle);
/*      */       }
/*  244 */       assert (localArrayList.size() == 9);
/*  245 */       return (MethodHandle[])localArrayList.toArray(new MethodHandle[0]);
/*      */     }
/*      */ 
/*      */     static MethodType makeConType(MethodHandle paramMethodHandle)
/*      */     {
/*  268 */       MethodType localMethodType = paramMethodHandle.type();
/*  269 */       return localMethodType.changeParameterType(0, Object.class).changeReturnType(Void.TYPE);
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/*  131 */       unsafe = Unsafe.getUnsafe();
/*      */ 
/*  247 */       INVOKES = makeInvokes();
/*      */       try
/*      */       {
/*  254 */         VARARGS_INVOKE = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(AllocateObject.class, "invoke_V", MethodType.genericMethodType(0, true));
/*  255 */         ALLOCATE = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(AllocateObject.class, "allocate", MethodType.genericMethodType(0));
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  257 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*      */ 
/*  261 */       CON_TYPES = new MethodType[INVOKES.length];
/*      */ 
/*  263 */       for (int i = 0; i < INVOKES.length; i++)
/*  264 */         CON_TYPES[i] = makeConType(INVOKES[i]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class BindCaller
/*      */   {
/*      */     private static final Unsafe UNSAFE;
/*      */     private static ClassValue<MethodHandle> CV_makeInjectedInvoker;
/*      */     private static final MethodHandle MH_checkCallerClass;
/* 1397 */     private static final byte[] T_BYTES = (byte[])localObject[0];
/*      */ 
/*      */     static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass)
/*      */     {
/* 1281 */       if ((paramClass == null) || (paramClass.isArray()) || (paramClass.isPrimitive()) || (paramClass.getName().startsWith("java.")) || (paramClass.getName().startsWith("sun.")))
/*      */       {
/* 1286 */         throw new InternalError();
/*      */       }
/*      */ 
/* 1289 */       MethodHandle localMethodHandle1 = prepareForInvoker(paramMethodHandle);
/*      */ 
/* 1291 */       MethodHandle localMethodHandle2 = (MethodHandle)CV_makeInjectedInvoker.get(paramClass);
/* 1292 */       return restoreToType(localMethodHandle2.bindTo(localMethodHandle1), paramMethodHandle.type());
/*      */     }
/*      */ 
/*      */     private static MethodHandle makeInjectedInvoker(Class<?> paramClass)
/*      */     {
/* 1298 */       Class localClass = UNSAFE.defineAnonymousClass(paramClass, T_BYTES, null);
/* 1299 */       if (paramClass.getClassLoader() != localClass.getClassLoader())
/* 1300 */         throw new InternalError(paramClass.getName() + " (CL)");
/*      */       try {
/* 1302 */         if (paramClass.getProtectionDomain() != localClass.getProtectionDomain())
/* 1303 */           throw new InternalError(paramClass.getName() + " (PD)");
/*      */       }
/*      */       catch (SecurityException localSecurityException)
/*      */       {
/*      */       }
/*      */       try {
/* 1309 */         MethodHandle localMethodHandle1 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(localClass, "init", MethodType.methodType(Void.TYPE));
/* 1310 */         localMethodHandle1.invokeExact();
/*      */       } catch (Throwable localThrowable1) {
/* 1312 */         throw MethodHandleStatics.uncaughtException(localThrowable1);
/*      */       }
/*      */       MethodHandle localMethodHandle2;
/*      */       try {
/* 1316 */         MethodType localMethodType = MethodType.methodType(Object.class, MethodHandle.class, new Class[] { [Ljava.lang.Object.class });
/* 1317 */         localMethodHandle2 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(localClass, "invoke_V", localMethodType);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 1319 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*      */       try
/*      */       {
/* 1323 */         MethodHandle localMethodHandle3 = prepareForInvoker(MH_checkCallerClass);
/* 1324 */         Object localObject = localMethodHandle2.invokeExact(localMethodHandle3, new Object[] { paramClass, localClass });
/*      */       } catch (Throwable localThrowable2) {
/* 1326 */         throw new InternalError(localThrowable2.toString());
/*      */       }
/* 1328 */       return localMethodHandle2;
/*      */     }
/*      */ 
/*      */     private static MethodHandle prepareForInvoker(MethodHandle paramMethodHandle)
/*      */     {
/* 1338 */       paramMethodHandle = paramMethodHandle.asFixedArity();
/* 1339 */       MethodType localMethodType = paramMethodHandle.type();
/* 1340 */       int i = localMethodType.parameterCount();
/* 1341 */       MethodHandle localMethodHandle = paramMethodHandle.asType(localMethodType.generic());
/* 1342 */       localMethodHandle = localMethodHandle.asSpreader([Ljava.lang.Object.class, i);
/* 1343 */       return localMethodHandle;
/*      */     }
/*      */ 
/*      */     private static MethodHandle restoreToType(MethodHandle paramMethodHandle, MethodType paramMethodType)
/*      */     {
/* 1348 */       return paramMethodHandle.asCollector([Ljava.lang.Object.class, paramMethodType.parameterCount()).asType(paramMethodType);
/*      */     }
/*      */ 
/*      */     private static boolean checkCallerClass(Class<?> paramClass1, Class<?> paramClass2)
/*      */     {
/* 1367 */       Class localClass = Reflection.getCallerClass(2);
/* 1368 */       if ((localClass != paramClass1) && (localClass != paramClass2)) {
/* 1369 */         throw new InternalError("found " + localClass.getName() + ", expected " + paramClass1.getName() + (paramClass1 == paramClass2 ? "" : new StringBuilder().append(", or else ").append(paramClass2.getName()).toString()));
/*      */       }
/* 1371 */       return true;
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/* 1295 */       UNSAFE = Unsafe.getUnsafe();
/*      */ 
/* 1330 */       CV_makeInjectedInvoker = new ClassValue() {
/*      */         protected MethodHandle computeValue(Class<?> paramAnonymousClass) {
/* 1332 */           return MethodHandleImpl.BindCaller.makeInjectedInvoker(paramAnonymousClass);
/*      */         }
/*      */       };
/* 1353 */       Object localObject = BindCaller.class;
/* 1354 */       assert (checkCallerClass((Class)localObject, (Class)localObject));
/*      */       try {
/* 1356 */         MH_checkCallerClass = MethodHandles.Lookup.IMPL_LOOKUP.findStatic((Class)localObject, "checkCallerClass", MethodType.methodType(Boolean.TYPE, Class.class, new Class[] { Class.class }));
/*      */ 
/* 1359 */         if ((!$assertionsDisabled) && (!MH_checkCallerClass.invokeExact((Class)localObject, (Class)localObject))) throw new AssertionError(); 
/*      */       }
/* 1361 */       catch (Throwable localThrowable) { throw new InternalError(localThrowable.toString()); }
/*      */ 
/*      */ 
/* 1376 */       localObject = new Object[] { null };
/* 1377 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Void run() {
/*      */           try {
/* 1380 */             MethodHandleImpl.BindCaller.T localT = MethodHandleImpl.BindCaller.T.class;
/* 1381 */             String str1 = localT.getName();
/* 1382 */             String str2 = str1.substring(str1.lastIndexOf('.') + 1) + ".class";
/* 1383 */             URLConnection localURLConnection = localT.getResource(str2).openConnection();
/* 1384 */             int i = localURLConnection.getContentLength();
/* 1385 */             byte[] arrayOfByte = new byte[i];
/* 1386 */             InputStream localInputStream = localURLConnection.getInputStream(); Object localObject1 = null;
/*      */             try { int j = localInputStream.read(arrayOfByte);
/* 1388 */               if (j != i) throw new IOException(str2);
/*      */             }
/*      */             catch (Throwable localThrowable2)
/*      */             {
/* 1386 */               localObject1 = localThrowable2; throw localThrowable2;
/*      */             }
/*      */             finally {
/* 1389 */               if (localInputStream != null) if (localObject1 != null) try { localInputStream.close(); } catch (Throwable localThrowable3) { localObject1.addSuppressed(localThrowable3); } else localInputStream.close(); 
/*      */             }
/* 1390 */             this.val$values[0] = arrayOfByte;
/*      */           } catch (IOException localIOException) {
/* 1392 */             throw new InternalError(localIOException.toString());
/*      */           }
/* 1394 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     private static class T {
/*      */       static void init() {
/*      */       }
/*      */ 
/*      */       static Object invoke_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject) throws Throwable {
/* 1404 */         return paramMethodHandle.invokeExact(paramArrayOfObject);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class FieldAccessor<C, V> extends BoundMethodHandle
/*      */   {
/*  305 */     private static final Unsafe unsafe = Unsafe.getUnsafe();
/*      */     final Object base;
/*      */     final long offset;
/*      */     final String name;
/*  420 */     static final HashMap<Class<?>, MethodHandle[]> ARRAY_CACHE = new HashMap();
/*      */ 
/*      */     FieldAccessor(MemberName paramMemberName, boolean paramBoolean)
/*      */     {
/*  311 */       super();
/*  312 */       this.offset = paramMemberName.getVMIndex();
/*  313 */       this.name = paramMemberName.getName();
/*  314 */       this.base = staticBase(paramMemberName);
/*      */     }
/*      */     String debugString() {
/*  317 */       return MethodHandleStatics.addTypeString(this.name, this);
/*      */     }
/*  319 */     int getFieldI(C paramC) { return unsafe.getInt(paramC, this.offset); } 
/*  320 */     void setFieldI(C paramC, int paramInt) { unsafe.putInt(paramC, this.offset, paramInt); } 
/*  321 */     long getFieldJ(C paramC) { return unsafe.getLong(paramC, this.offset); } 
/*  322 */     void setFieldJ(C paramC, long paramLong) { unsafe.putLong(paramC, this.offset, paramLong); } 
/*  323 */     float getFieldF(C paramC) { return unsafe.getFloat(paramC, this.offset); } 
/*  324 */     void setFieldF(C paramC, float paramFloat) { unsafe.putFloat(paramC, this.offset, paramFloat); } 
/*  325 */     double getFieldD(C paramC) { return unsafe.getDouble(paramC, this.offset); } 
/*  326 */     void setFieldD(C paramC, double paramDouble) { unsafe.putDouble(paramC, this.offset, paramDouble); } 
/*  327 */     boolean getFieldZ(C paramC) { return unsafe.getBoolean(paramC, this.offset); } 
/*  328 */     void setFieldZ(C paramC, boolean paramBoolean) { unsafe.putBoolean(paramC, this.offset, paramBoolean); } 
/*  329 */     byte getFieldB(C paramC) { return unsafe.getByte(paramC, this.offset); } 
/*  330 */     void setFieldB(C paramC, byte paramByte) { unsafe.putByte(paramC, this.offset, paramByte); } 
/*  331 */     short getFieldS(C paramC) { return unsafe.getShort(paramC, this.offset); } 
/*  332 */     void setFieldS(C paramC, short paramShort) { unsafe.putShort(paramC, this.offset, paramShort); } 
/*  333 */     char getFieldC(C paramC) { return unsafe.getChar(paramC, this.offset); } 
/*  334 */     void setFieldC(C paramC, char paramChar) { unsafe.putChar(paramC, this.offset, paramChar); } 
/*      */     V getFieldL(C paramC) {
/*  336 */       return unsafe.getObject(paramC, this.offset);
/*      */     }
/*  338 */     void setFieldL(C paramC, V paramV) { unsafe.putObject(paramC, this.offset, paramV); }
/*      */ 
/*      */     static Object staticBase(MemberName paramMemberName)
/*      */     {
/*  342 */       if (!paramMemberName.isStatic()) return null;
/*  343 */       return AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Object run() {
/*      */           try {
/*  346 */             Class localClass = this.val$field.getDeclaringClass();
/*      */ 
/*  348 */             Field localField = localClass.getDeclaredField(this.val$field.getName());
/*  349 */             return MethodHandleImpl.FieldAccessor.unsafe.staticFieldBase(localField);
/*      */           } catch (NoSuchFieldException localNoSuchFieldException) {
/*  351 */             throw MethodHandleStatics.uncaughtException(localNoSuchFieldException);
/*      */           }
/*      */         } } );
/*      */     }
/*      */ 
/*      */     int getStaticI() {
/*  357 */       return unsafe.getInt(this.base, this.offset); } 
/*  358 */     void setStaticI(int paramInt) { unsafe.putInt(this.base, this.offset, paramInt); } 
/*  359 */     long getStaticJ() { return unsafe.getLong(this.base, this.offset); } 
/*  360 */     void setStaticJ(long paramLong) { unsafe.putLong(this.base, this.offset, paramLong); } 
/*  361 */     float getStaticF() { return unsafe.getFloat(this.base, this.offset); } 
/*  362 */     void setStaticF(float paramFloat) { unsafe.putFloat(this.base, this.offset, paramFloat); } 
/*  363 */     double getStaticD() { return unsafe.getDouble(this.base, this.offset); } 
/*  364 */     void setStaticD(double paramDouble) { unsafe.putDouble(this.base, this.offset, paramDouble); } 
/*  365 */     boolean getStaticZ() { return unsafe.getBoolean(this.base, this.offset); } 
/*  366 */     void setStaticZ(boolean paramBoolean) { unsafe.putBoolean(this.base, this.offset, paramBoolean); } 
/*  367 */     byte getStaticB() { return unsafe.getByte(this.base, this.offset); } 
/*  368 */     void setStaticB(byte paramByte) { unsafe.putByte(this.base, this.offset, paramByte); } 
/*  369 */     short getStaticS() { return unsafe.getShort(this.base, this.offset); } 
/*  370 */     void setStaticS(short paramShort) { unsafe.putShort(this.base, this.offset, paramShort); } 
/*  371 */     char getStaticC() { return unsafe.getChar(this.base, this.offset); } 
/*  372 */     void setStaticC(char paramChar) { unsafe.putChar(this.base, this.offset, paramChar); } 
/*  373 */     V getStaticL() { return unsafe.getObject(this.base, this.offset); } 
/*  374 */     void setStaticL(V paramV) { unsafe.putObject(this.base, this.offset, paramV); }
/*      */ 
/*      */ 
/*      */     static String fname(Class<?> paramClass, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/*      */       String str;
/*  378 */       if (!paramBoolean2)
/*  379 */         str = !paramBoolean1 ? "getField" : "setField";
/*      */       else
/*  381 */         str = !paramBoolean1 ? "getStatic" : "setStatic";
/*  382 */       return str + Wrapper.basicTypeChar(paramClass);
/*      */     }
/*      */ 
/*      */     static MethodType ftype(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean1, boolean paramBoolean2) {
/*  386 */       if (!paramBoolean2) {
/*  387 */         if (!paramBoolean1) {
/*  388 */           return MethodType.methodType(paramClass2, paramClass1);
/*      */         }
/*  390 */         return MethodType.methodType(Void.TYPE, paramClass1, new Class[] { paramClass2 });
/*      */       }
/*  392 */       if (!paramBoolean1) {
/*  393 */         return MethodType.methodType(paramClass2);
/*      */       }
/*  395 */       return MethodType.methodType(Void.TYPE, paramClass2);
/*      */     }
/*      */ 
/*      */     static MethodHandle fhandle(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean1, boolean paramBoolean2) {
/*  399 */       String str = fname(paramClass2, paramBoolean1, paramBoolean2);
/*  400 */       if (paramClass1.isPrimitive()) throw MethodHandleStatics.newIllegalArgumentException("primitive " + paramClass1);
/*  401 */       Object localObject1 = Object.class;
/*  402 */       Object localObject2 = paramClass2;
/*  403 */       if (!((Class)localObject2).isPrimitive()) localObject2 = Object.class; MethodType localMethodType1 = ftype(localObject1, (Class)localObject2, paramBoolean1, paramBoolean2);
/*      */       MethodHandle localMethodHandle;
/*      */       try {
/*  407 */         localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(FieldAccessor.class, str, localMethodType1);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  409 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*  411 */       if ((localObject2 != paramClass2) || ((!paramBoolean2) && (localObject1 != paramClass1))) {
/*  412 */         MethodType localMethodType2 = ftype(paramClass1, paramClass2, paramBoolean1, paramBoolean2);
/*  413 */         localMethodType2 = localMethodType2.insertParameterTypes(0, new Class[] { FieldAccessor.class });
/*  414 */         localMethodHandle = MethodHandleImpl.convertArguments(localMethodHandle, localMethodType2, 0);
/*      */       }
/*  416 */       return localMethodHandle;
/*      */     }
/*      */ 
/*      */     static boolean doCache(Class<?> paramClass)
/*      */     {
/*  424 */       if (paramClass.isPrimitive()) return true;
/*  425 */       ClassLoader localClassLoader = paramClass.getClassLoader();
/*  426 */       return (localClassLoader == null) || (localClassLoader == ClassLoader.getSystemClassLoader());
/*      */     }
/*  428 */     static int getElementI(int[] paramArrayOfInt, int paramInt) { return paramArrayOfInt[paramInt]; } 
/*  429 */     static void setElementI(int[] paramArrayOfInt, int paramInt1, int paramInt2) { paramArrayOfInt[paramInt1] = paramInt2; } 
/*  430 */     static long getElementJ(long[] paramArrayOfLong, int paramInt) { return paramArrayOfLong[paramInt]; } 
/*  431 */     static void setElementJ(long[] paramArrayOfLong, int paramInt, long paramLong) { paramArrayOfLong[paramInt] = paramLong; } 
/*  432 */     static float getElementF(float[] paramArrayOfFloat, int paramInt) { return paramArrayOfFloat[paramInt]; } 
/*  433 */     static void setElementF(float[] paramArrayOfFloat, int paramInt, float paramFloat) { paramArrayOfFloat[paramInt] = paramFloat; } 
/*  434 */     static double getElementD(double[] paramArrayOfDouble, int paramInt) { return paramArrayOfDouble[paramInt]; } 
/*  435 */     static void setElementD(double[] paramArrayOfDouble, int paramInt, double paramDouble) { paramArrayOfDouble[paramInt] = paramDouble; } 
/*  436 */     static boolean getElementZ(boolean[] paramArrayOfBoolean, int paramInt) { return paramArrayOfBoolean[paramInt]; } 
/*  437 */     static void setElementZ(boolean[] paramArrayOfBoolean, int paramInt, boolean paramBoolean) { paramArrayOfBoolean[paramInt] = paramBoolean; } 
/*  438 */     static byte getElementB(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[paramInt]; } 
/*  439 */     static void setElementB(byte[] paramArrayOfByte, int paramInt, byte paramByte) { paramArrayOfByte[paramInt] = paramByte; } 
/*  440 */     static short getElementS(short[] paramArrayOfShort, int paramInt) { return paramArrayOfShort[paramInt]; } 
/*  441 */     static void setElementS(short[] paramArrayOfShort, int paramInt, short paramShort) { paramArrayOfShort[paramInt] = paramShort; } 
/*  442 */     static char getElementC(char[] paramArrayOfChar, int paramInt) { return paramArrayOfChar[paramInt]; } 
/*  443 */     static void setElementC(char[] paramArrayOfChar, int paramInt, char paramChar) { paramArrayOfChar[paramInt] = paramChar; } 
/*  444 */     static Object getElementL(Object[] paramArrayOfObject, int paramInt) { return paramArrayOfObject[paramInt]; } 
/*  445 */     static void setElementL(Object[] paramArrayOfObject, int paramInt, Object paramObject) { paramArrayOfObject[paramInt] = paramObject; } 
/*  446 */     static <V> V getElementL(Class<V[]> paramClass, V[] paramArrayOfV, int paramInt) { return ((Object[])paramClass.cast(paramArrayOfV))[paramInt]; } 
/*  447 */     static <V> void setElementL(Class<V[]> paramClass, V[] paramArrayOfV, int paramInt, V paramV) { ((Object[])paramClass.cast(paramArrayOfV))[paramInt] = paramV; }
/*      */ 
/*      */     static String aname(Class<?> paramClass, boolean paramBoolean) {
/*  450 */       Class localClass = paramClass.getComponentType();
/*  451 */       if (localClass == null) throw new IllegalArgumentException();
/*  452 */       return (!paramBoolean ? "getElement" : "setElement") + Wrapper.basicTypeChar(localClass);
/*      */     }
/*      */     static MethodType atype(Class<?> paramClass, boolean paramBoolean) {
/*  455 */       Class localClass = paramClass.getComponentType();
/*  456 */       if (!paramBoolean) {
/*  457 */         return MethodType.methodType(localClass, paramClass, new Class[] { Integer.TYPE });
/*      */       }
/*  459 */       return MethodType.methodType(Void.TYPE, paramClass, new Class[] { Integer.TYPE, localClass });
/*      */     }
/*      */     static MethodHandle ahandle(Class<?> paramClass, boolean paramBoolean) {
/*  462 */       Object localObject = paramClass.getComponentType();
/*  463 */       String str = aname(paramClass, paramBoolean);
/*  464 */       Class<?> localClass = null;
/*  465 */       if ((!((Class)localObject).isPrimitive()) && (localObject != Object.class)) {
/*  466 */         localClass = paramClass;
/*  467 */         paramClass = [Ljava.lang.Object.class;
/*  468 */         localObject = Object.class;
/*      */       }
/*  470 */       MethodType localMethodType1 = atype(paramClass, paramBoolean);
/*  471 */       if (localClass != null)
/*  472 */         localMethodType1 = localMethodType1.insertParameterTypes(0, new Class[] { Class.class });
/*      */       MethodHandle localMethodHandle;
/*      */       try {
/*  475 */         localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(FieldAccessor.class, str, localMethodType1);
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  477 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*  479 */       if (localClass != null) {
/*  480 */         MethodType localMethodType2 = atype(localClass, paramBoolean);
/*  481 */         localMethodHandle = localMethodHandle.bindTo(localClass);
/*  482 */         localMethodHandle = MethodHandleImpl.convertArguments(localMethodHandle, localMethodType2, 0);
/*      */       }
/*  484 */       return localMethodHandle;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class GuardWithCatch extends BoundMethodHandle
/*      */   {
/*      */     private final MethodHandle target;
/*      */     private final Class<? extends Throwable> exType;
/*      */     private final MethodHandle catcher;
/*      */     static final MethodHandle[] INVOKES;
/*      */     static final MethodHandle VARARGS_INVOKE;
/*      */ 
/*      */     GuardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2)
/*      */     {
/* 1090 */       this(INVOKES[paramMethodHandle1.type().parameterCount()], paramMethodHandle1, paramClass, paramMethodHandle2);
/*      */     }
/*      */ 
/*      */     GuardWithCatch(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle3)
/*      */     {
/* 1095 */       super();
/* 1096 */       this.target = paramMethodHandle2;
/* 1097 */       this.exType = paramClass;
/* 1098 */       this.catcher = paramMethodHandle3;
/*      */     }
/*      */ 
/*      */     String debugString() {
/* 1102 */       return MethodHandleStatics.addTypeString(this.target, this);
/*      */     }
/*      */     private Object invoke_V(Object[] paramArrayOfObject) throws Throwable {
/*      */       try {
/* 1106 */         return this.target.invokeExact(paramArrayOfObject);
/*      */       } catch (Throwable localThrowable) {
/* 1108 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1109 */         return this.catcher.invokeExact(localThrowable, paramArrayOfObject);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L0() throws Throwable {
/*      */       try { return this.target.invokeExact();
/*      */       } catch (Throwable localThrowable) {
/* 1116 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1117 */         return this.catcher.invokeExact(localThrowable);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L1(Object paramObject) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject);
/*      */       } catch (Throwable localThrowable) {
/* 1124 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1125 */         return this.catcher.invokeExact(localThrowable, paramObject);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L2(Object paramObject1, Object paramObject2) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2);
/*      */       } catch (Throwable localThrowable) {
/* 1132 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1133 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3);
/*      */       } catch (Throwable localThrowable) {
/* 1140 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1141 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4);
/*      */       } catch (Throwable localThrowable) {
/* 1148 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1149 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3, paramObject4);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */       } catch (Throwable localThrowable) {
/* 1156 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1157 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */       } catch (Throwable localThrowable) {
/* 1164 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1165 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */       } catch (Throwable localThrowable) {
/* 1172 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1173 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */       }
/*      */     }
/*      */ 
/*      */     private Object invoke_L8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/*      */       try { return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */       } catch (Throwable localThrowable) {
/* 1180 */         if (!this.exType.isInstance(localThrowable)) throw localThrowable;
/* 1181 */         return this.catcher.invokeExact(localThrowable, paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */       }
/*      */     }
/*      */ 
/* 1185 */     static MethodHandle[] makeInvokes() { ArrayList localArrayList = new ArrayList();
/* 1186 */       MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP;
/*      */       while (true) {
/* 1188 */         int i = localArrayList.size();
/* 1189 */         String str = "invoke_L" + i;
/* 1190 */         MethodHandle localMethodHandle = null;
/*      */         try {
/* 1192 */           localMethodHandle = localLookup.findVirtual(GuardWithCatch.class, str, MethodType.genericMethodType(i));
/*      */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */         }
/* 1195 */         if (localMethodHandle == null) break;
/* 1196 */         localArrayList.add(localMethodHandle);
/*      */       }
/* 1198 */       assert (localArrayList.size() == 9);
/* 1199 */       return (MethodHandle[])localArrayList.toArray(new MethodHandle[0]); } 
/*      */     static {
/* 1201 */       INVOKES = makeInvokes();
/*      */       try
/*      */       {
/* 1207 */         VARARGS_INVOKE = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(GuardWithCatch.class, "invoke_V", MethodType.genericMethodType(0, true));
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 1209 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class GuardWithTest extends BoundMethodHandle
/*      */   {
/*      */     private final MethodHandle test;
/*      */     private final MethodHandle target;
/*      */     private final MethodHandle fallback;
/*      */     static final MethodHandle[] INVOKES;
/*      */     static final MethodHandle VARARGS_INVOKE;
/*      */ 
/*      */     private GuardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3, MethodHandle paramMethodHandle4)
/*      */     {
/*  917 */       super();
/*  918 */       this.test = paramMethodHandle2;
/*  919 */       this.target = paramMethodHandle3;
/*  920 */       this.fallback = paramMethodHandle4;
/*      */     }
/*      */     static boolean preferRicochetFrame(MethodType paramMethodType) {
/*  923 */       return true;
/*      */     }
/*      */     static MethodHandle make(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3) {
/*  926 */       MethodType localMethodType1 = paramMethodHandle2.type();
/*  927 */       int i = localMethodType1.parameterCount();
/*  928 */       if (i < INVOKES.length) {
/*  929 */         if ((preferRicochetFrame(localMethodType1)) && 
/*  930 */           (!$assertionsDisabled) && (!MethodHandleNatives.workaroundWithoutRicochetFrames())) throw new AssertionError();
/*  931 */         localMethodHandle1 = INVOKES[i];
/*  932 */         localMethodType2 = localMethodType1.generic();
/*  933 */         assert (localMethodHandle1.type().dropParameterTypes(0, 1) == localMethodType2);
/*      */ 
/*  935 */         localMethodHandle2 = MethodHandleImpl.convertArguments(paramMethodHandle1, localMethodType2.changeReturnType(Boolean.TYPE), paramMethodHandle1.type(), 2);
/*  936 */         localMethodHandle3 = MethodHandleImpl.convertArguments(paramMethodHandle2, localMethodType2, localMethodType1, 2);
/*  937 */         localMethodHandle4 = MethodHandleImpl.convertArguments(paramMethodHandle3, localMethodType2, localMethodType1, 2);
/*  938 */         if ((localMethodHandle2 == null) || (localMethodHandle3 == null) || (localMethodHandle4 == null)) return null;
/*  939 */         localGuardWithTest = new GuardWithTest(localMethodHandle1, localMethodHandle2, localMethodHandle3, localMethodHandle4);
/*  940 */         return MethodHandleImpl.convertArguments(localGuardWithTest, localMethodType1, localMethodType2, 2);
/*      */       }
/*  942 */       assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*  943 */       MethodHandle localMethodHandle1 = VARARGS_INVOKE;
/*  944 */       MethodType localMethodType2 = MethodType.genericMethodType(1);
/*  945 */       assert (localMethodHandle1.type().dropParameterTypes(0, 1) == localMethodType2);
/*  946 */       MethodHandle localMethodHandle2 = MethodHandleImpl.spreadArgumentsFromPos(paramMethodHandle1, localMethodType2.changeReturnType(Boolean.TYPE), 0);
/*  947 */       MethodHandle localMethodHandle3 = MethodHandleImpl.spreadArgumentsFromPos(paramMethodHandle2, localMethodType2, 0);
/*  948 */       MethodHandle localMethodHandle4 = MethodHandleImpl.spreadArgumentsFromPos(paramMethodHandle3, localMethodType2, 0);
/*  949 */       GuardWithTest localGuardWithTest = new GuardWithTest(localMethodHandle1, localMethodHandle2, localMethodHandle3, localMethodHandle4);
/*  950 */       if ((localMethodHandle2 == null) || (localMethodHandle3 == null) || (localMethodHandle4 == null)) return null;
/*  951 */       return MethodHandleImpl.collectArguments(localGuardWithTest, localMethodType1, 0, null);
/*      */     }
/*      */ 
/*      */     String debugString()
/*      */     {
/*  956 */       return MethodHandleStatics.addTypeString(this.target, this);
/*      */     }
/*      */     private Object invoke_V(Object[] paramArrayOfObject) throws Throwable {
/*  959 */       if (this.test.invokeExact(paramArrayOfObject))
/*  960 */         return this.target.invokeExact(paramArrayOfObject);
/*  961 */       return this.fallback.invokeExact(paramArrayOfObject);
/*      */     }
/*      */     private Object invoke_L0() throws Throwable {
/*  964 */       if (this.test.invokeExact())
/*  965 */         return this.target.invokeExact();
/*  966 */       return this.fallback.invokeExact();
/*      */     }
/*      */     private Object invoke_L1(Object paramObject) throws Throwable {
/*  969 */       if (this.test.invokeExact(paramObject))
/*  970 */         return this.target.invokeExact(paramObject);
/*  971 */       return this.fallback.invokeExact(paramObject);
/*      */     }
/*      */     private Object invoke_L2(Object paramObject1, Object paramObject2) throws Throwable {
/*  974 */       if (this.test.invokeExact(paramObject1, paramObject2))
/*  975 */         return this.target.invokeExact(paramObject1, paramObject2);
/*  976 */       return this.fallback.invokeExact(paramObject1, paramObject2);
/*      */     }
/*      */     private Object invoke_L3(Object paramObject1, Object paramObject2, Object paramObject3) throws Throwable {
/*  979 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3))
/*  980 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3);
/*  981 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3);
/*      */     }
/*      */     private Object invoke_L4(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) throws Throwable {
/*  984 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4))
/*  985 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4);
/*  986 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4);
/*      */     }
/*      */     private Object invoke_L5(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5) throws Throwable {
/*  989 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5))
/*  990 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*  991 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5);
/*      */     }
/*      */     private Object invoke_L6(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6) throws Throwable {
/*  994 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6))
/*  995 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*  996 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
/*      */     }
/*      */     private Object invoke_L7(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7) throws Throwable {
/*  999 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7))
/* 1000 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/* 1001 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7);
/*      */     }
/*      */     private Object invoke_L8(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8) throws Throwable {
/* 1004 */       if (this.test.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8))
/* 1005 */         return this.target.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/* 1006 */       return this.fallback.invokeExact(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
/*      */     }
/*      */     static MethodHandle[] makeInvokes() {
/* 1009 */       ArrayList localArrayList = new ArrayList();
/* 1010 */       MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP;
/*      */       while (true) {
/* 1012 */         int i = localArrayList.size();
/* 1013 */         String str = "invoke_L" + i;
/* 1014 */         MethodHandle localMethodHandle = null;
/*      */         try {
/* 1016 */           localMethodHandle = localLookup.findVirtual(GuardWithTest.class, str, MethodType.genericMethodType(i));
/*      */         } catch (ReflectiveOperationException localReflectiveOperationException) {
/*      */         }
/* 1019 */         if (localMethodHandle == null) break;
/* 1020 */         localArrayList.add(localMethodHandle);
/*      */       }
/* 1022 */       assert (localArrayList.size() == 9);
/* 1023 */       return (MethodHandle[])localArrayList.toArray(new MethodHandle[0]);
/*      */     }
/* 1025 */     static { INVOKES = makeInvokes();
/*      */       try
/*      */       {
/* 1031 */         VARARGS_INVOKE = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(GuardWithTest.class, "invoke_V", MethodType.genericMethodType(0, true));
/*      */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 1033 */         throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandleImpl
 * JD-Core Version:    0.6.2
 */