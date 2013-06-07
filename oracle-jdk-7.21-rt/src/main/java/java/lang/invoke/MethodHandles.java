/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import sun.invoke.util.ValueConversions;
/*      */ import sun.invoke.util.VerifyAccess;
/*      */ import sun.invoke.util.Wrapper;
/*      */ import sun.reflect.Reflection;
/*      */ 
/*      */ public class MethodHandles
/*      */ {
/*      */   private static final MemberName.Factory IMPL_NAMES;
/*      */ 
/*      */   public static Lookup lookup()
/*      */   {
/*   70 */     return new Lookup();
/*      */   }
/*      */ 
/*      */   public static Lookup publicLookup()
/*      */   {
/*   87 */     return Lookup.PUBLIC_LOOKUP;
/*      */   }
/*      */ 
/*      */   public static MethodHandle arrayElementGetter(Class<?> paramClass)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1360 */     return MethodHandleImpl.accessArrayElement(paramClass, false);
/*      */   }
/*      */ 
/*      */   public static MethodHandle arrayElementSetter(Class<?> paramClass)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1374 */     return MethodHandleImpl.accessArrayElement(paramClass, true);
/*      */   }
/*      */ 
/*      */   public static MethodHandle spreadInvoker(MethodType paramMethodType, int paramInt)
/*      */   {
/* 1423 */     if ((paramInt < 0) || (paramInt > paramMethodType.parameterCount()))
/* 1424 */       throw new IllegalArgumentException("bad argument count " + paramInt);
/* 1425 */     return paramMethodType.invokers().spreadInvoker(paramInt);
/*      */   }
/*      */ 
/*      */   public static MethodHandle exactInvoker(MethodType paramMethodType)
/*      */   {
/* 1465 */     return paramMethodType.invokers().exactInvoker();
/*      */   }
/*      */ 
/*      */   public static MethodHandle invoker(MethodType paramMethodType)
/*      */   {
/* 1498 */     return paramMethodType.invokers().generalInvoker();
/*      */   }
/*      */ 
/*      */   static <T0, T1> T1 checkValue(Class<T0> paramClass, Class<T1> paramClass1, Object paramObject)
/*      */     throws ClassCastException
/*      */   {
/* 1534 */     if (paramClass == paramClass1)
/*      */     {
/* 1536 */       if (paramClass.isPrimitive()) {
/* 1537 */         return Wrapper.asPrimitiveType(paramClass1).cast(paramObject);
/*      */       }
/* 1539 */       return Wrapper.OBJECT.convert(paramObject, paramClass1);
/*      */     }
/* 1541 */     boolean bool1 = paramClass.isPrimitive(); boolean bool2 = paramClass1.isPrimitive();
/* 1542 */     if (!bool1)
/*      */     {
/* 1544 */       Wrapper.OBJECT.convert(paramObject, paramClass);
/* 1545 */       if (!bool2) {
/* 1546 */         return Wrapper.OBJECT.convert(paramObject, paramClass1);
/*      */       }
/*      */ 
/* 1549 */       localWrapper = Wrapper.forPrimitiveType(paramClass1);
/* 1550 */       return localWrapper.convert(paramObject, paramClass1);
/*      */     }
/*      */ 
/* 1553 */     Wrapper.asWrapperType(paramClass).cast(paramObject);
/* 1554 */     Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass1);
/* 1555 */     return localWrapper.convert(paramObject, paramClass1);
/*      */   }
/*      */ 
/*      */   static Object checkValue(Class<?> paramClass, Object paramObject)
/*      */     throws ClassCastException
/*      */   {
/*      */     Object localObject;
/* 1565 */     if (paramObject == null)
/* 1566 */       localObject = Object.class;
/*      */     else
/* 1568 */       localObject = paramObject.getClass();
/* 1569 */     return checkValue((Class)localObject, paramClass, paramObject);
/*      */   }
/*      */ 
/*      */   public static MethodHandle explicitCastArguments(MethodHandle paramMethodHandle, MethodType paramMethodType)
/*      */   {
/* 1620 */     return MethodHandleImpl.convertArguments(paramMethodHandle, paramMethodType, 2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle permuteArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int[] paramArrayOfInt)
/*      */   {
/* 1684 */     MethodType localMethodType = paramMethodHandle.type();
/* 1685 */     checkReorder(paramArrayOfInt, paramMethodType, localMethodType);
/* 1686 */     return MethodHandleImpl.permuteArguments(paramMethodHandle, paramMethodType, localMethodType, paramArrayOfInt);
/*      */   }
/*      */ 
/*      */   private static void checkReorder(int[] paramArrayOfInt, MethodType paramMethodType1, MethodType paramMethodType2)
/*      */   {
/* 1692 */     if (paramMethodType1.returnType() != paramMethodType2.returnType()) {
/* 1693 */       throw MethodHandleStatics.newIllegalArgumentException("return types do not match", paramMethodType2, paramMethodType1);
/*      */     }
/* 1695 */     if (paramArrayOfInt.length == paramMethodType2.parameterCount()) {
/* 1696 */       int i = paramMethodType1.parameterCount();
/* 1697 */       int j = 0;
/* 1698 */       for (int k = 0; k < paramArrayOfInt.length; k++) {
/* 1699 */         int m = paramArrayOfInt[k];
/* 1700 */         if ((m < 0) || (m >= i)) {
/* 1701 */           j = 1; break;
/*      */         }
/* 1703 */         Class localClass1 = paramMethodType1.parameterType(m);
/* 1704 */         Class localClass2 = paramMethodType2.parameterType(k);
/* 1705 */         if (localClass1 != localClass2) {
/* 1706 */           throw MethodHandleStatics.newIllegalArgumentException("parameter types do not match after reorder", paramMethodType2, paramMethodType1);
/*      */         }
/*      */       }
/* 1709 */       if (j == 0) return;
/*      */     }
/* 1711 */     throw MethodHandleStatics.newIllegalArgumentException("bad reorder array: " + Arrays.toString(paramArrayOfInt));
/*      */   }
/*      */ 
/*      */   public static MethodHandle constant(Class<?> paramClass, Object paramObject)
/*      */   {
/* 1731 */     if (paramClass.isPrimitive()) {
/* 1732 */       if (paramClass == Void.TYPE)
/* 1733 */         throw MethodHandleStatics.newIllegalArgumentException("void type");
/* 1734 */       Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
/* 1735 */       return insertArguments(identity(paramClass), 0, new Object[] { localWrapper.convert(paramObject, paramClass) });
/*      */     }
/* 1737 */     return identity(paramClass).bindTo(paramClass.cast(paramObject));
/*      */   }
/*      */ 
/*      */   public static MethodHandle identity(Class<?> paramClass)
/*      */   {
/* 1750 */     if (paramClass == Void.TYPE)
/* 1751 */       throw MethodHandleStatics.newIllegalArgumentException("void type");
/* 1752 */     if (paramClass == Object.class)
/* 1753 */       return ValueConversions.identity();
/* 1754 */     if (paramClass.isPrimitive()) {
/* 1755 */       return ValueConversions.identity(Wrapper.forPrimitiveType(paramClass));
/*      */     }
/* 1757 */     return AdapterMethodHandle.makeRetypeRaw(MethodType.methodType(paramClass, paramClass), ValueConversions.identity());
/*      */   }
/*      */ 
/*      */   public static MethodHandle insertArguments(MethodHandle paramMethodHandle, int paramInt, Object[] paramArrayOfObject)
/*      */   {
/* 1793 */     int i = paramArrayOfObject.length;
/* 1794 */     MethodType localMethodType = paramMethodHandle.type();
/* 1795 */     int j = localMethodType.parameterCount();
/* 1796 */     int k = j - i;
/* 1797 */     if (k < 0)
/* 1798 */       throw MethodHandleStatics.newIllegalArgumentException("too many values to insert");
/* 1799 */     if ((paramInt < 0) || (paramInt > k))
/* 1800 */       throw MethodHandleStatics.newIllegalArgumentException("no argument type to append");
/* 1801 */     Object localObject1 = paramMethodHandle;
/* 1802 */     for (int m = 0; m < i; m++) {
/* 1803 */       Object localObject2 = paramArrayOfObject[m];
/* 1804 */       Class localClass = localMethodType.parameterType(paramInt + m);
/* 1805 */       localObject2 = checkValue(localClass, localObject2);
/* 1806 */       if ((paramInt == 0) && (!localClass.isPrimitive()))
/*      */       {
/* 1808 */         MethodHandle localMethodHandle = MethodHandleImpl.bindReceiver((MethodHandle)localObject1, localObject2);
/* 1809 */         if (localMethodHandle != null) {
/* 1810 */           localObject1 = localMethodHandle;
/* 1811 */           continue;
/*      */         }
/*      */       }
/*      */ 
/* 1815 */       localObject1 = MethodHandleImpl.bindArgument((MethodHandle)localObject1, paramInt, localObject2);
/*      */     }
/* 1817 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, List<Class<?>> paramList)
/*      */   {
/* 1864 */     MethodType localMethodType1 = paramMethodHandle.type();
/* 1865 */     if (paramList.size() == 0) return paramMethodHandle;
/* 1866 */     int i = localMethodType1.parameterCount();
/* 1867 */     int j = i + paramList.size();
/* 1868 */     if ((paramInt < 0) || (paramInt >= j))
/* 1869 */       throw MethodHandleStatics.newIllegalArgumentException("no argument type to remove");
/* 1870 */     ArrayList localArrayList = new ArrayList(localMethodType1.parameterList());
/*      */ 
/* 1872 */     localArrayList.addAll(paramInt, paramList);
/* 1873 */     MethodType localMethodType2 = MethodType.methodType(localMethodType1.returnType(), localArrayList);
/* 1874 */     return MethodHandleImpl.dropArguments(paramMethodHandle, localMethodType2, paramInt);
/*      */   }
/*      */ 
/*      */   public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, Class<?>[] paramArrayOfClass)
/*      */   {
/* 1925 */     return dropArguments(paramMethodHandle, paramInt, Arrays.asList(paramArrayOfClass));
/*      */   }
/*      */ 
/*      */   public static MethodHandle filterArguments(MethodHandle paramMethodHandle, int paramInt, MethodHandle[] paramArrayOfMethodHandle)
/*      */   {
/* 1993 */     MethodType localMethodType1 = paramMethodHandle.type();
/* 1994 */     MethodHandle localMethodHandle1 = paramMethodHandle;
/* 1995 */     MethodType localMethodType2 = null;
/* 1996 */     assert ((localMethodType2 = localMethodType1) != null);
/* 1997 */     int i = localMethodType1.parameterCount();
/* 1998 */     if (paramInt + paramArrayOfMethodHandle.length > i)
/* 1999 */       throw MethodHandleStatics.newIllegalArgumentException("too many filters");
/* 2000 */     int j = paramInt - 1;
/* 2001 */     for (MethodHandle localMethodHandle2 : paramArrayOfMethodHandle) {
/* 2002 */       j++;
/* 2003 */       if (localMethodHandle2 != null) {
/* 2004 */         localMethodHandle1 = filterArgument(localMethodHandle1, j, localMethodHandle2);
/* 2005 */         assert ((localMethodType2 = localMethodType2.changeParameterType(j, localMethodHandle2.type().parameterType(0))) != null);
/*      */       }
/*      */     }
/* 2007 */     assert (localMethodType2.equals(localMethodHandle1.type()));
/* 2008 */     return localMethodHandle1;
/*      */   }
/*      */ 
/*      */   static MethodHandle filterArgument(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
/*      */   {
/* 2013 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 2014 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 2015 */     if ((localMethodType2.parameterCount() != 1) || (localMethodType2.returnType() != localMethodType1.parameterType(paramInt)))
/*      */     {
/* 2017 */       throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", localMethodType1, localMethodType2);
/* 2018 */     }return MethodHandleImpl.filterArgument(paramMethodHandle1, paramInt, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle filterReturnValue(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/* 2080 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 2081 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 2082 */     Class localClass = localMethodType1.returnType();
/* 2083 */     int i = localMethodType2.parameterCount();
/* 2084 */     if (i == 0 ? localClass != Void.TYPE : localClass != localMethodType2.parameterType(0))
/*      */     {
/* 2087 */       throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", paramMethodHandle1, paramMethodHandle2);
/*      */     }
/*      */ 
/* 2090 */     MethodType localMethodType3 = localMethodType1.changeReturnType(localMethodType2.returnType());
/* 2091 */     MethodHandle localMethodHandle1 = null;
/* 2092 */     if (AdapterMethodHandle.canCollectArguments(localMethodType2, localMethodType1, 0, false)) {
/* 2093 */       localMethodHandle1 = AdapterMethodHandle.makeCollectArguments(paramMethodHandle2, paramMethodHandle1, 0, false);
/* 2094 */       if (localMethodHandle1 != null) return localMethodHandle1;
/*      */     }
/*      */ 
/* 2097 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/* 2098 */     MethodHandle localMethodHandle2 = dropArguments(paramMethodHandle2, i, localMethodType1.parameterList());
/* 2099 */     localMethodHandle1 = foldArguments(localMethodHandle2, paramMethodHandle1);
/* 2100 */     assert (localMethodHandle1.type().equals(localMethodType3));
/* 2101 */     return localMethodHandle1;
/*      */   }
/*      */ 
/*      */   public static MethodHandle foldArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */   {
/* 2182 */     int i = 0;
/* 2183 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 2184 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 2185 */     int j = i;
/* 2186 */     int k = localMethodType2.parameterCount();
/* 2187 */     int m = localMethodType2.returnType() == Void.TYPE ? 0 : 1;
/* 2188 */     int n = j + m;
/* 2189 */     int i1 = localMethodType1.parameterCount() >= n + k ? 1 : 0;
/* 2190 */     if ((i1 != 0) && (!localMethodType2.parameterList().equals(localMethodType1.parameterList().subList(n, n + k))))
/*      */     {
/* 2193 */       i1 = 0;
/* 2194 */     }if ((i1 != 0) && (m != 0) && (!localMethodType2.returnType().equals(localMethodType1.parameterType(0))))
/* 2195 */       i1 = 0;
/* 2196 */     if (i1 == 0)
/* 2197 */       throw misMatchedTypes("target and combiner types", localMethodType1, localMethodType2);
/* 2198 */     MethodType localMethodType3 = localMethodType1.dropParameterTypes(j, n);
/* 2199 */     MethodHandle localMethodHandle = MethodHandleImpl.foldArguments(paramMethodHandle1, localMethodType3, j, paramMethodHandle2);
/* 2200 */     if (localMethodHandle == null) throw MethodHandleStatics.newIllegalArgumentException("cannot fold from " + localMethodType3 + " to " + localMethodType1);
/* 2201 */     return localMethodHandle;
/*      */   }
/*      */ 
/*      */   public static MethodHandle guardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3)
/*      */   {
/* 2240 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 2241 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 2242 */     MethodType localMethodType3 = paramMethodHandle3.type();
/* 2243 */     if (!localMethodType2.equals(localMethodType3))
/* 2244 */       throw misMatchedTypes("target and fallback types", localMethodType2, localMethodType3);
/* 2245 */     if (localMethodType1.returnType() != Boolean.TYPE)
/* 2246 */       throw MethodHandleStatics.newIllegalArgumentException("guard type is not a predicate " + localMethodType1);
/* 2247 */     List localList1 = localMethodType2.parameterList();
/* 2248 */     List localList2 = localMethodType1.parameterList();
/* 2249 */     if (!localList1.equals(localList2)) {
/* 2250 */       int i = localList2.size(); int j = localList1.size();
/* 2251 */       if ((i >= j) || (!localList1.subList(0, i).equals(localList2)))
/* 2252 */         throw misMatchedTypes("target and test types", localMethodType2, localMethodType1);
/* 2253 */       paramMethodHandle1 = dropArguments(paramMethodHandle1, i, localList1.subList(i, j));
/* 2254 */       localMethodType1 = paramMethodHandle1.type();
/*      */     }
/* 2256 */     return MethodHandleImpl.makeGuardWithTest(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
/*      */   }
/*      */ 
/*      */   static RuntimeException misMatchedTypes(String paramString, MethodType paramMethodType1, MethodType paramMethodType2) {
/* 2260 */     return MethodHandleStatics.newIllegalArgumentException(paramString + " must match: " + paramMethodType1 + " != " + paramMethodType2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle catchException(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2)
/*      */   {
/* 2310 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 2311 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 2312 */     if ((localMethodType2.parameterCount() < 1) || (!localMethodType2.parameterType(0).isAssignableFrom(paramClass)))
/*      */     {
/* 2314 */       throw MethodHandleStatics.newIllegalArgumentException("handler does not accept exception type " + paramClass);
/* 2315 */     }if (localMethodType2.returnType() != localMethodType1.returnType())
/* 2316 */       throw misMatchedTypes("target and handler return types", localMethodType1, localMethodType2);
/* 2317 */     List localList1 = localMethodType1.parameterList();
/* 2318 */     List localList2 = localMethodType2.parameterList();
/* 2319 */     localList2 = localList2.subList(1, localList2.size());
/* 2320 */     if (!localList1.equals(localList2)) {
/* 2321 */       int i = localList2.size(); int j = localList1.size();
/* 2322 */       if ((i >= j) || (!localList1.subList(0, i).equals(localList2)))
/* 2323 */         throw misMatchedTypes("target and handler types", localMethodType1, localMethodType2);
/* 2324 */       paramMethodHandle2 = dropArguments(paramMethodHandle2, 1 + i, localList1.subList(i, j));
/* 2325 */       localMethodType2 = paramMethodHandle2.type();
/*      */     }
/* 2327 */     return MethodHandleImpl.makeGuardWithCatch(paramMethodHandle1, paramClass, paramMethodHandle2);
/*      */   }
/*      */ 
/*      */   public static MethodHandle throwException(Class<?> paramClass, Class<? extends Throwable> paramClass1)
/*      */   {
/* 2342 */     return MethodHandleImpl.throwException(MethodType.methodType(paramClass, paramClass1));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   56 */     IMPL_NAMES = MemberName.getFactory();
/*   57 */     MethodHandleImpl.initStatics();
/*      */   }
/*      */ 
/*      */   public static final class Lookup
/*      */   {
/*      */     private final Class<?> lookupClass;
/*      */     private final int allowedModes;
/*      */     public static final int PUBLIC = 1;
/*      */     public static final int PRIVATE = 2;
/*      */     public static final int PROTECTED = 4;
/*      */     public static final int PACKAGE = 8;
/*      */     private static final int ALL_MODES = 15;
/*      */     private static final int TRUSTED = -1;
/*  481 */     static final Lookup PUBLIC_LOOKUP = new Lookup(Object.class, 1);
/*      */ 
/*  484 */     static final Lookup IMPL_LOOKUP = new Lookup(Object.class, -1);
/*      */     private static final boolean ALLOW_NESTMATE_ACCESS = false;
/*      */ 
/*      */     private static int fixmods(int paramInt)
/*      */     {
/*  359 */       paramInt &= 7;
/*  360 */       return paramInt != 0 ? paramInt : 8;
/*      */     }
/*      */ 
/*      */     public Class<?> lookupClass()
/*      */     {
/*  372 */       return this.lookupClass;
/*      */     }
/*      */ 
/*      */     private Class<?> lookupClassOrNull()
/*      */     {
/*  377 */       return this.allowedModes == -1 ? null : this.lookupClass;
/*      */     }
/*      */ 
/*      */     public int lookupModes()
/*      */     {
/*  398 */       return this.allowedModes & 0xF;
/*      */     }
/*      */ 
/*      */     Lookup()
/*      */     {
/*  410 */       this(getCallerClassAtEntryPoint(false), 15);
/*      */ 
/*  412 */       checkUnprivilegedlookupClass(this.lookupClass);
/*      */     }
/*      */ 
/*      */     Lookup(Class<?> paramClass) {
/*  416 */       this(paramClass, 15);
/*      */     }
/*      */ 
/*      */     private Lookup(Class<?> paramClass, int paramInt) {
/*  420 */       this.lookupClass = paramClass;
/*  421 */       this.allowedModes = paramInt;
/*      */     }
/*      */ 
/*      */     public Lookup in(Class<?> paramClass)
/*      */     {
/*  449 */       paramClass.getClass();
/*  450 */       if (this.allowedModes == -1)
/*  451 */         return new Lookup(paramClass, 15);
/*  452 */       if (paramClass == this.lookupClass)
/*  453 */         return this;
/*  454 */       int i = this.allowedModes & 0xB;
/*  455 */       if (((i & 0x8) != 0) && (!VerifyAccess.isSamePackage(this.lookupClass, paramClass)))
/*      */       {
/*  457 */         i &= -11;
/*      */       }
/*      */ 
/*  460 */       if (((i & 0x2) != 0) && (!VerifyAccess.isSamePackageMember(this.lookupClass, paramClass)))
/*      */       {
/*  462 */         i &= -3;
/*      */       }
/*  464 */       if (((i & 0x1) != 0) && (!VerifyAccess.isClassAccessible(paramClass, this.lookupClass, this.allowedModes)))
/*      */       {
/*  468 */         i = 0;
/*      */       }
/*  470 */       checkUnprivilegedlookupClass(paramClass);
/*  471 */       return new Lookup(paramClass, i);
/*      */     }
/*      */ 
/*      */     private static void checkUnprivilegedlookupClass(Class<?> paramClass)
/*      */     {
/*  487 */       String str = paramClass.getName();
/*  488 */       if (str.startsWith("java.lang.invoke."))
/*  489 */         throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + paramClass);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  522 */       String str = this.lookupClass.getName();
/*  523 */       switch (this.allowedModes) {
/*      */       case 0:
/*  525 */         return str + "/noaccess";
/*      */       case 1:
/*  527 */         return str + "/public";
/*      */       case 9:
/*  529 */         return str + "/package";
/*      */       case 11:
/*  531 */         return str + "/private";
/*      */       case 15:
/*  533 */         return str;
/*      */       case -1:
/*  535 */         return "/trusted";
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 10:
/*      */       case 12:
/*      */       case 13:
/*  537 */       case 14: } str = str + "/" + Integer.toHexString(this.allowedModes);
/*  538 */       if (!$assertionsDisabled) throw new AssertionError(str);
/*  539 */       return str;
/*      */     }
/*      */ 
/*      */     private static Class<?> getCallerClassAtEntryPoint(boolean paramBoolean)
/*      */     {
/*  552 */       assert (Reflection.getCallerClass(2) == Lookup.class);
/*  553 */       if (!$assertionsDisabled) if (Reflection.getCallerClass(3) != (paramBoolean ? Lookup.class : MethodHandles.class)) throw new AssertionError();
/*  554 */       return Reflection.getCallerClass(4);
/*      */     }
/*      */ 
/*      */     public MethodHandle findStatic(Class<?> paramClass, String paramString, MethodType paramMethodType)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  585 */       MemberName localMemberName = resolveOrFail(paramClass, paramString, paramMethodType, true);
/*  586 */       checkSecurityManager(paramClass, localMemberName);
/*  587 */       Class localClass = findBoundCallerClass(localMemberName);
/*  588 */       return accessStatic(paramClass, localMemberName, localClass);
/*      */     }
/*      */ 
/*      */     private MethodHandle accessStatic(Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2) throws IllegalAccessException {
/*  592 */       checkMethod(paramClass1, paramMemberName, true);
/*  593 */       MethodHandle localMethodHandle = MethodHandleImpl.findMethod(paramMemberName, false, lookupClassOrNull());
/*  594 */       localMethodHandle = maybeBindCaller(paramMemberName, localMethodHandle, paramClass2);
/*  595 */       return localMethodHandle;
/*      */     }
/*      */ 
/*      */     private MethodHandle resolveStatic(Class<?> paramClass, String paramString, MethodType paramMethodType) throws NoSuchMethodException, IllegalAccessException {
/*  599 */       MemberName localMemberName = resolveOrFail(paramClass, paramString, paramMethodType, true);
/*  600 */       return accessStatic(paramClass, localMemberName, this.lookupClass);
/*      */     }
/*      */ 
/*      */     public MethodHandle findVirtual(Class<?> paramClass, String paramString, MethodType paramMethodType)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  642 */       MemberName localMemberName = resolveOrFail(paramClass, paramString, paramMethodType, false);
/*  643 */       checkSecurityManager(paramClass, localMemberName);
/*  644 */       Class localClass = findBoundCallerClass(localMemberName);
/*  645 */       return accessVirtual(paramClass, localMemberName, localClass);
/*      */     }
/*      */     private MethodHandle resolveVirtual(Class<?> paramClass, String paramString, MethodType paramMethodType) throws NoSuchMethodException, IllegalAccessException {
/*  648 */       MemberName localMemberName = resolveOrFail(paramClass, paramString, paramMethodType, false);
/*  649 */       return accessVirtual(paramClass, localMemberName, this.lookupClass);
/*      */     }
/*      */     private MethodHandle accessVirtual(Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2) throws IllegalAccessException {
/*  652 */       checkMethod(paramClass1, paramMemberName, false);
/*  653 */       MethodHandle localMethodHandle = MethodHandleImpl.findMethod(paramMemberName, true, lookupClassOrNull());
/*  654 */       localMethodHandle = maybeBindCaller(paramMemberName, localMethodHandle, paramClass2);
/*  655 */       return restrictProtectedReceiver(paramMemberName, localMethodHandle);
/*      */     }
/*      */ 
/*      */     public MethodHandle findConstructor(Class<?> paramClass, MethodType paramMethodType)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  685 */       String str = "<init>";
/*  686 */       MemberName localMemberName = resolveOrFail(paramClass, str, paramMethodType, false, false, lookupClassOrNull());
/*  687 */       checkSecurityManager(paramClass, localMemberName);
/*  688 */       return accessConstructor(paramClass, localMemberName);
/*      */     }
/*      */     private MethodHandle accessConstructor(Class<?> paramClass, MemberName paramMemberName) throws IllegalAccessException {
/*  691 */       assert (paramMemberName.isConstructor());
/*  692 */       checkAccess(paramClass, paramMemberName, false);
/*  693 */       MethodHandle localMethodHandle1 = MethodHandleImpl.findMethod(paramMemberName, false, lookupClassOrNull());
/*  694 */       MethodHandle localMethodHandle2 = MethodHandleImpl.makeAllocator(localMethodHandle1);
/*  695 */       assert (!MethodHandleNatives.isCallerSensitive(paramMemberName));
/*  696 */       return fixVarargs(localMethodHandle2, localMethodHandle1);
/*      */     }
/*      */     private MethodHandle resolveConstructor(Class<?> paramClass, MethodType paramMethodType) throws NoSuchMethodException, IllegalAccessException {
/*  699 */       String str = "<init>";
/*  700 */       MemberName localMemberName = resolveOrFail(paramClass, str, paramMethodType, false, false, lookupClassOrNull());
/*  701 */       return accessConstructor(paramClass, localMemberName);
/*      */     }
/*      */ 
/*      */     private static MethodHandle fixVarargs(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*      */     {
/*  706 */       boolean bool1 = paramMethodHandle1.isVarargsCollector();
/*  707 */       boolean bool2 = paramMethodHandle2.isVarargsCollector();
/*  708 */       if (bool1 == bool2)
/*  709 */         return paramMethodHandle1;
/*  710 */       if (bool2) {
/*  711 */         MethodType localMethodType = paramMethodHandle1.type();
/*  712 */         int i = localMethodType.parameterCount();
/*  713 */         return paramMethodHandle1.asVarargsCollector(localMethodType.parameterType(i - 1));
/*      */       }
/*  715 */       return paramMethodHandle1.asFixedArity();
/*      */     }
/*      */ 
/*      */     public MethodHandle findSpecial(Class<?> paramClass1, String paramString, MethodType paramMethodType, Class<?> paramClass2)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  755 */       checkSpecialCaller(paramClass2);
/*  756 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramMethodType, false, false, paramClass2);
/*  757 */       checkSecurityManager(paramClass1, localMemberName);
/*  758 */       Class localClass = findBoundCallerClass(localMemberName);
/*  759 */       return accessSpecial(paramClass1, localMemberName, localClass, paramClass2);
/*      */     }
/*      */ 
/*      */     private MethodHandle accessSpecial(Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2, Class<?> paramClass3) throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  764 */       checkMethod(paramClass1, paramMemberName, false);
/*      */       Class localClass;
/*  767 */       if ((paramClass1 != lookupClass()) && (paramClass1 != (localClass = lookupClass().getSuperclass())) && (paramClass1.isAssignableFrom(lookupClass())))
/*      */       {
/*  770 */         assert (!paramMemberName.getName().equals("<init>"));
/*      */ 
/*  776 */         localObject = new MemberName(localClass, paramMemberName.getName(), paramMemberName.getMethodType(), 7);
/*      */ 
/*  780 */         localObject = MethodHandles.IMPL_NAMES.resolveOrNull((MemberName)localObject, true, lookupClassOrNull());
/*  781 */         if (localObject == null) throw new InternalError(paramMemberName.toString());
/*  782 */         paramMemberName = (MemberName)localObject;
/*  783 */         paramClass1 = localClass;
/*      */ 
/*  785 */         checkMethod(paramClass1, paramMemberName, false);
/*      */       }
/*      */ 
/*  788 */       Object localObject = MethodHandleImpl.findMethod(paramMemberName, false, paramClass3);
/*  789 */       localObject = maybeBindCaller(paramMemberName, (MethodHandle)localObject, paramClass2);
/*  790 */       return restrictReceiver(paramMemberName, (MethodHandle)localObject, paramClass3);
/*      */     }
/*      */     private MethodHandle resolveSpecial(Class<?> paramClass, String paramString, MethodType paramMethodType) throws NoSuchMethodException, IllegalAccessException {
/*  793 */       Class localClass = lookupClass();
/*  794 */       checkSpecialCaller(localClass);
/*  795 */       MemberName localMemberName = resolveOrFail(paramClass, paramString, paramMethodType, false, false, localClass);
/*  796 */       return accessSpecial(paramClass, localMemberName, this.lookupClass, localClass);
/*      */     }
/*      */ 
/*      */     public MethodHandle findGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
/*      */       throws NoSuchFieldException, IllegalAccessException
/*      */     {
/*  817 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, false);
/*  818 */       checkSecurityManager(paramClass1, localMemberName);
/*  819 */       return makeAccessor(paramClass1, localMemberName, false, false, 0);
/*      */     }
/*      */     private MethodHandle resolveGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2) throws NoSuchFieldException, IllegalAccessException {
/*  822 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, false);
/*  823 */       return makeAccessor(paramClass1, localMemberName, false, false, 0);
/*      */     }
/*      */ 
/*      */     public MethodHandle findSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
/*      */       throws NoSuchFieldException, IllegalAccessException
/*      */     {
/*  844 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, false);
/*  845 */       checkSecurityManager(paramClass1, localMemberName);
/*  846 */       return makeAccessor(paramClass1, localMemberName, false, true, 0);
/*      */     }
/*      */     private MethodHandle resolveSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2) throws NoSuchFieldException, IllegalAccessException {
/*  849 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, false);
/*  850 */       return makeAccessor(paramClass1, localMemberName, false, true, 0);
/*      */     }
/*      */ 
/*      */     public MethodHandle findStaticGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
/*      */       throws NoSuchFieldException, IllegalAccessException
/*      */     {
/*  870 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, true);
/*  871 */       checkSecurityManager(paramClass1, localMemberName);
/*  872 */       return makeAccessor(paramClass1, localMemberName, false, false, 1);
/*      */     }
/*      */     private MethodHandle resolveStaticGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2) throws NoSuchFieldException, IllegalAccessException {
/*  875 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, true);
/*  876 */       return makeAccessor(paramClass1, localMemberName, false, false, 1);
/*      */     }
/*      */ 
/*      */     public MethodHandle findStaticSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
/*      */       throws NoSuchFieldException, IllegalAccessException
/*      */     {
/*  896 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, true);
/*  897 */       checkSecurityManager(paramClass1, localMemberName);
/*  898 */       return makeAccessor(paramClass1, localMemberName, false, true, 1);
/*      */     }
/*      */     private MethodHandle resolveStaticSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2) throws NoSuchFieldException, IllegalAccessException {
/*  901 */       MemberName localMemberName = resolveOrFail(paramClass1, paramString, paramClass2, true);
/*  902 */       return makeAccessor(paramClass1, localMemberName, false, true, 1);
/*      */     }
/*      */ 
/*      */     public MethodHandle bind(Object paramObject, String paramString, MethodType paramMethodType)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/*  952 */       Class localClass1 = paramObject.getClass();
/*  953 */       MemberName localMemberName = resolveOrFail(localClass1, paramString, paramMethodType, false);
/*  954 */       checkSecurityManager(localClass1, localMemberName);
/*  955 */       checkMethod(localClass1, localMemberName, false);
/*  956 */       MethodHandle localMethodHandle1 = MethodHandleImpl.findMethod(localMemberName, true, lookupClassOrNull());
/*  957 */       Class localClass2 = findBoundCallerClass(localMemberName);
/*  958 */       MethodHandle localMethodHandle2 = maybeBindCaller(localMemberName, localMethodHandle1, localClass2);
/*  959 */       if (localMethodHandle2 != localMethodHandle1) return fixVarargs(localMethodHandle2.bindTo(paramObject), localMethodHandle1);
/*  960 */       MethodHandle localMethodHandle3 = MethodHandleImpl.bindReceiver(localMethodHandle1, paramObject);
/*  961 */       if (localMethodHandle3 == null)
/*  962 */         throw localMemberName.makeAccessException("no access", this);
/*  963 */       return fixVarargs(localMethodHandle3, localMethodHandle1);
/*      */     }
/*      */ 
/*      */     public MethodHandle unreflect(Method paramMethod)
/*      */       throws IllegalAccessException
/*      */     {
/*  988 */       MemberName localMemberName = new MemberName(paramMethod);
/*  989 */       assert (localMemberName.isMethod());
/*  990 */       if (paramMethod.isAccessible())
/*  991 */         return MethodHandleImpl.findMethod(localMemberName, true, null);
/*  992 */       checkMethod(localMemberName.getDeclaringClass(), localMemberName, localMemberName.isStatic());
/*  993 */       MethodHandle localMethodHandle = MethodHandleImpl.findMethod(localMemberName, true, lookupClassOrNull());
/*  994 */       Class localClass = findBoundCallerClass(localMemberName);
/*  995 */       localMethodHandle = maybeBindCaller(localMemberName, localMethodHandle, localClass);
/*  996 */       return restrictProtectedReceiver(localMemberName, localMethodHandle);
/*      */     }
/*      */ 
/*      */     public MethodHandle unreflectSpecial(Method paramMethod, Class<?> paramClass)
/*      */       throws IllegalAccessException
/*      */     {
/* 1021 */       checkSpecialCaller(paramClass);
/* 1022 */       MemberName localMemberName = new MemberName(paramMethod);
/* 1023 */       assert (localMemberName.isMethod());
/*      */ 
/* 1025 */       checkMethod(paramMethod.getDeclaringClass(), localMemberName, false);
/* 1026 */       MethodHandle localMethodHandle = MethodHandleImpl.findMethod(localMemberName, false, lookupClassOrNull());
/* 1027 */       Class localClass = findBoundCallerClass(localMemberName);
/* 1028 */       localMethodHandle = maybeBindCaller(localMemberName, localMethodHandle, localClass);
/* 1029 */       return restrictReceiver(localMemberName, localMethodHandle, paramClass);
/*      */     }
/*      */ 
/*      */     public MethodHandle unreflectConstructor(Constructor paramConstructor)
/*      */       throws IllegalAccessException
/*      */     {
/* 1054 */       MemberName localMemberName = new MemberName(paramConstructor);
/* 1055 */       assert (localMemberName.isConstructor());
/*      */       MethodHandle localMethodHandle1;
/* 1057 */       if (paramConstructor.isAccessible()) {
/* 1058 */         localMethodHandle1 = MethodHandleImpl.findMethod(localMemberName, false, null);
/*      */       } else {
/* 1060 */         checkAccess(paramConstructor.getDeclaringClass(), localMemberName, false);
/* 1061 */         localMethodHandle1 = MethodHandleImpl.findMethod(localMemberName, false, lookupClassOrNull());
/*      */       }
/* 1063 */       assert (!MethodHandleNatives.isCallerSensitive(localMemberName));
/* 1064 */       MethodHandle localMethodHandle2 = MethodHandleImpl.makeAllocator(localMethodHandle1);
/* 1065 */       return fixVarargs(localMethodHandle2, localMethodHandle1);
/*      */     }
/*      */ 
/*      */     public MethodHandle unreflectGetter(Field paramField)
/*      */       throws IllegalAccessException
/*      */     {
/* 1083 */       return makeAccessor(paramField.getDeclaringClass(), new MemberName(paramField), paramField.isAccessible(), false, -1);
/*      */     }
/*      */ 
/*      */     public MethodHandle unreflectSetter(Field paramField)
/*      */       throws IllegalAccessException
/*      */     {
/* 1101 */       return makeAccessor(paramField.getDeclaringClass(), new MemberName(paramField), paramField.isAccessible(), true, -1);
/*      */     }
/*      */ 
/*      */     MemberName resolveOrFail(Class<?> paramClass1, String paramString, Class<?> paramClass2, boolean paramBoolean)
/*      */       throws NoSuchFieldException, IllegalAccessException
/*      */     {
/* 1107 */       checkSymbolicClass(paramClass1);
/* 1108 */       paramString.getClass(); paramClass2.getClass();
/* 1109 */       int i = paramBoolean ? 8 : 0;
/* 1110 */       return MethodHandles.IMPL_NAMES.resolveOrFail(new MemberName(paramClass1, paramString, paramClass2, i), true, lookupClassOrNull(), NoSuchFieldException.class);
/*      */     }
/*      */ 
/*      */     MemberName resolveOrFail(Class<?> paramClass, String paramString, MethodType paramMethodType, boolean paramBoolean) throws NoSuchMethodException, IllegalAccessException
/*      */     {
/* 1115 */       checkSymbolicClass(paramClass);
/* 1116 */       paramString.getClass(); paramMethodType.getClass();
/* 1117 */       int i = paramBoolean ? 8 : 0;
/* 1118 */       return MethodHandles.IMPL_NAMES.resolveOrFail(new MemberName(paramClass, paramString, paramMethodType, i), true, lookupClassOrNull(), NoSuchMethodException.class);
/*      */     }
/*      */ 
/*      */     MemberName resolveOrFail(Class<?> paramClass1, String paramString, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2, Class<?> paramClass2)
/*      */       throws NoSuchMethodException, IllegalAccessException
/*      */     {
/* 1124 */       checkSymbolicClass(paramClass1);
/* 1125 */       paramString.getClass(); paramMethodType.getClass();
/* 1126 */       int i = paramBoolean1 ? 8 : 0;
/* 1127 */       return MethodHandles.IMPL_NAMES.resolveOrFail(new MemberName(paramClass1, paramString, paramMethodType, i), paramBoolean2, paramClass2, NoSuchMethodException.class);
/*      */     }
/*      */ 
/*      */     void checkSymbolicClass(Class<?> paramClass) throws IllegalAccessException
/*      */     {
/* 1132 */       Class localClass = lookupClassOrNull();
/* 1133 */       if ((localClass != null) && (!VerifyAccess.isClassAccessible(paramClass, localClass, this.allowedModes)))
/* 1134 */         throw new MemberName(paramClass).makeAccessException("symbolic reference class is not public", this);
/*      */     }
/*      */ 
/*      */     Class<?> findBoundCallerClass(MemberName paramMemberName)
/*      */     {
/* 1145 */       Class localClass = null;
/* 1146 */       if (MethodHandleNatives.isCallerSensitive(paramMemberName))
/*      */       {
/* 1149 */         localClass = (this.allowedModes & 0x2) != 0 ? this.lookupClass : getCallerClassAtEntryPoint(true);
/*      */       }
/*      */ 
/* 1154 */       return localClass;
/*      */     }
/*      */ 
/*      */     void checkSecurityManager(Class<?> paramClass, MemberName paramMemberName)
/*      */     {
/* 1164 */       SecurityManager localSecurityManager = System.getSecurityManager();
/* 1165 */       if (localSecurityManager == null) return;
/* 1166 */       if (this.allowedModes == -1) return;
/*      */ 
/* 1168 */       localSecurityManager.checkMemberAccess(paramClass, 0);
/*      */ 
/* 1170 */       Class localClass1 = (this.allowedModes & 0x2) != 0 ? this.lookupClass : getCallerClassAtEntryPoint(true);
/*      */ 
/* 1174 */       if ((!VerifyAccess.classLoaderIsAncestor(this.lookupClass, paramClass)) || ((localClass1 != this.lookupClass) && (!VerifyAccess.classLoaderIsAncestor(localClass1, paramClass))))
/*      */       {
/* 1177 */         localSecurityManager.checkPackageAccess(VerifyAccess.getPackageName(paramClass));
/*      */       }
/* 1179 */       if (paramMemberName.isPublic()) return;
/* 1180 */       Class localClass2 = paramMemberName.getDeclaringClass();
/* 1181 */       localSecurityManager.checkMemberAccess(localClass2, 1);
/*      */ 
/* 1183 */       if (localClass2 != paramClass)
/* 1184 */         localSecurityManager.checkPackageAccess(VerifyAccess.getPackageName(localClass2));
/*      */     }
/*      */ 
/*      */     void checkMethod(Class<?> paramClass, MemberName paramMemberName, boolean paramBoolean)
/*      */       throws IllegalAccessException
/*      */     {
/*      */       String str;
/* 1207 */       if (paramMemberName.isConstructor()) {
/* 1208 */         str = "expected a method, not a constructor";
/* 1209 */       } else if (!paramMemberName.isMethod()) {
/* 1210 */         str = "expected a method";
/* 1211 */       } else if (paramBoolean != paramMemberName.isStatic()) {
/* 1212 */         str = paramBoolean ? "expected a static method" : "expected a non-static method";
/*      */       } else {
/* 1214 */         checkAccess(paramClass, paramMemberName, false); return;
/* 1215 */       }throw paramMemberName.makeAccessException(str, this);
/*      */     }
/*      */ 
/*      */     void checkAccess(Class<?> paramClass, MemberName paramMemberName, boolean paramBoolean) throws IllegalAccessException {
/* 1219 */       int i = this.allowedModes;
/* 1220 */       if (i == -1) return;
/* 1221 */       int j = paramMemberName.getModifiers();
/* 1222 */       if ((paramMemberName.isField()) && (Modifier.isFinal(j)) && (paramBoolean)) {
/* 1223 */         throw paramMemberName.makeAccessException("unexpected set of a final field", this);
/*      */       }
/* 1225 */       if ((Modifier.isPublic(j)) && (Modifier.isPublic(paramClass.getModifiers())) && (i != 0))
/* 1226 */         return;
/* 1227 */       int k = fixmods(j);
/* 1228 */       if (((k & i) != 0) && (VerifyAccess.isMemberAccessible(paramClass, paramMemberName.getDeclaringClass(), j, lookupClass(), i)))
/*      */       {
/* 1231 */         return;
/* 1232 */       }if (((k & (i ^ 0xFFFFFFFF) & 0x4) != 0) && ((i & 0x8) != 0) && (VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), lookupClass())))
/*      */       {
/* 1236 */         return;
/* 1237 */       }throw paramMemberName.makeAccessException(accessFailedMessage(paramClass, paramMemberName), this);
/*      */     }
/*      */ 
/*      */     String accessFailedMessage(Class<?> paramClass, MemberName paramMemberName) {
/* 1241 */       Class localClass = paramMemberName.getDeclaringClass();
/* 1242 */       int i = paramMemberName.getModifiers();
/*      */ 
/* 1244 */       int j = (Modifier.isPublic(localClass.getModifiers())) && ((localClass == paramClass) || (Modifier.isPublic(paramClass.getModifiers()))) ? 1 : 0;
/*      */ 
/* 1247 */       if ((j == 0) && ((this.allowedModes & 0x8) != 0)) {
/* 1248 */         j = (VerifyAccess.isClassAccessible(localClass, lookupClass(), 15)) && ((localClass == paramClass) || (VerifyAccess.isClassAccessible(paramClass, lookupClass(), 15))) ? 1 : 0;
/*      */       }
/*      */ 
/* 1252 */       if (j == 0)
/* 1253 */         return "class is not public";
/* 1254 */       if (Modifier.isPublic(i))
/* 1255 */         return "access to public member failed";
/* 1256 */       if (Modifier.isPrivate(i))
/* 1257 */         return "member is private";
/* 1258 */       if (Modifier.isProtected(i))
/* 1259 */         return "member is protected";
/* 1260 */       return "member is private to package";
/*      */     }
/*      */ 
/*      */     void checkSpecialCaller(Class<?> paramClass)
/*      */       throws IllegalAccessException
/*      */     {
/* 1266 */       if (this.allowedModes == -1) return;
/* 1267 */       if (((this.allowedModes & 0x2) == 0) || (paramClass != lookupClass()))
/*      */       {
/* 1271 */         throw new MemberName(paramClass).makeAccessException("no private access for invokespecial", this);
/*      */       }
/*      */     }
/*      */ 
/*      */     MethodHandle restrictProtectedReceiver(MemberName paramMemberName, MethodHandle paramMethodHandle)
/*      */       throws IllegalAccessException
/*      */     {
/* 1278 */       if ((!paramMemberName.isProtected()) || (paramMemberName.isStatic()) || (this.allowedModes == -1) || (paramMemberName.getDeclaringClass() == lookupClass()) || (VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), lookupClass())))
/*      */       {
/* 1284 */         return paramMethodHandle;
/*      */       }
/* 1286 */       return restrictReceiver(paramMemberName, paramMethodHandle, lookupClass());
/*      */     }
/*      */     MethodHandle restrictReceiver(MemberName paramMemberName, MethodHandle paramMethodHandle, Class<?> paramClass) throws IllegalAccessException {
/* 1289 */       assert (!paramMemberName.isStatic());
/* 1290 */       Class localClass = paramMemberName.getDeclaringClass();
/* 1291 */       if ((localClass.isInterface()) || (!localClass.isAssignableFrom(paramClass))) {
/* 1292 */         throw paramMemberName.makeAccessException("caller class must be a subclass below the method", paramClass);
/*      */       }
/* 1294 */       MethodType localMethodType1 = paramMethodHandle.type();
/* 1295 */       if (localMethodType1.parameterType(0) == paramClass) return paramMethodHandle;
/* 1296 */       MethodType localMethodType2 = localMethodType1.changeParameterType(0, paramClass);
/* 1297 */       MethodHandle localMethodHandle = MethodHandleImpl.convertArguments(paramMethodHandle, localMethodType2, localMethodType1, 0);
/* 1298 */       return fixVarargs(localMethodHandle, paramMethodHandle);
/*      */     }
/*      */ 
/*      */     private MethodHandle maybeBindCaller(MemberName paramMemberName, MethodHandle paramMethodHandle, Class<?> paramClass) throws IllegalAccessException
/*      */     {
/* 1303 */       if ((this.allowedModes == -1) || (!MethodHandleNatives.isCallerSensitive(paramMemberName)))
/* 1304 */         return paramMethodHandle;
/* 1305 */       Object localObject = this.lookupClass;
/* 1306 */       if ((this.allowedModes & 0x2) == 0)
/* 1307 */         localObject = paramClass;
/* 1308 */       MethodHandle localMethodHandle = MethodHandleImpl.bindCaller(paramMethodHandle, (Class)localObject);
/* 1309 */       localMethodHandle = fixVarargs(localMethodHandle, paramMethodHandle);
/* 1310 */       return localMethodHandle;
/*      */     }
/*      */ 
/*      */     MethodHandle makeAccessor(Class<?> paramClass, MemberName paramMemberName, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
/*      */       throws IllegalAccessException
/*      */     {
/* 1316 */       assert (paramMemberName.isField());
/* 1317 */       if (paramInt >= 0) if ((paramInt != 0) != paramMemberName.isStatic()) {
/* 1318 */           throw paramMemberName.makeAccessException(paramInt != 0 ? "expected a static field" : "expected a non-static field", this);
/*      */         }
/*      */ 
/* 1321 */       if (paramBoolean1)
/* 1322 */         return MethodHandleImpl.accessField(paramMemberName, paramBoolean2, null);
/* 1323 */       checkAccess(paramClass, paramMemberName, paramBoolean2);
/* 1324 */       MethodHandle localMethodHandle = MethodHandleImpl.accessField(paramMemberName, paramBoolean2, lookupClassOrNull());
/* 1325 */       return restrictProtectedReceiver(paramMemberName, localMethodHandle);
/*      */     }
/*      */ 
/*      */     MethodHandle linkMethodHandleConstant(int paramInt, Class<?> paramClass, String paramString, Object paramObject)
/*      */       throws ReflectiveOperationException
/*      */     {
/* 1332 */       switch (paramInt) { case 1:
/* 1333 */         return resolveGetter(paramClass, paramString, (Class)paramObject);
/*      */       case 2:
/* 1334 */         return resolveStaticGetter(paramClass, paramString, (Class)paramObject);
/*      */       case 3:
/* 1335 */         return resolveSetter(paramClass, paramString, (Class)paramObject);
/*      */       case 4:
/* 1336 */         return resolveStaticSetter(paramClass, paramString, (Class)paramObject);
/*      */       case 5:
/* 1337 */         return resolveVirtual(paramClass, paramString, (MethodType)paramObject);
/*      */       case 6:
/* 1338 */         return resolveStatic(paramClass, paramString, (MethodType)paramObject);
/*      */       case 7:
/* 1339 */         return resolveSpecial(paramClass, paramString, (MethodType)paramObject);
/*      */       case 8:
/* 1340 */         return resolveConstructor(paramClass, (MethodType)paramObject);
/*      */       case 9:
/* 1341 */         return resolveVirtual(paramClass, paramString, (MethodType)paramObject);
/*      */       }
/*      */ 
/* 1344 */       throw new ReflectiveOperationException("bad MethodHandle constant #" + paramInt + " " + paramString + " : " + paramObject);
/*      */     }
/*      */ 
/*      */     static
/*      */     {
/*  475 */       MethodHandles.IMPL_NAMES.getClass();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandles
 * JD-Core Version:    0.6.2
 */