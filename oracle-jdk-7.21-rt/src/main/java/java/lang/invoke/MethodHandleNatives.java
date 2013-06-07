/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.security.AccessController;
/*     */ import java.sql.DriverManager;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import sun.misc.Unsafe;
/*     */ import sun.reflect.Reflection;
/*     */ 
/*     */ class MethodHandleNatives
/*     */ {
/*     */   static final int JVM_PUSH_LIMIT;
/*     */   static final int JVM_STACK_MOVE_UNIT;
/*     */   static final int CONV_OP_IMPLEMENTED_MASK;
/*     */   static final boolean HAVE_RICOCHET_FRAMES;
/*     */   static final int OP_ROT_ARGS_DOWN_LIMIT_BIAS;
/*     */   static final boolean COUNT_GWT;
/*     */ 
/*     */   static native void init(MemberName paramMemberName, Object paramObject);
/*     */ 
/*     */   static native void expand(MemberName paramMemberName);
/*     */ 
/*     */   static native void resolve(MemberName paramMemberName, Class<?> paramClass);
/*     */ 
/*     */   static native int getMembers(Class<?> paramClass1, String paramString1, String paramString2, int paramInt1, Class<?> paramClass2, int paramInt2, MemberName[] paramArrayOfMemberName);
/*     */ 
/*     */   static native void init(AdapterMethodHandle paramAdapterMethodHandle, MethodHandle paramMethodHandle, int paramInt);
/*     */ 
/*     */   static native void init(BoundMethodHandle paramBoundMethodHandle, Object paramObject, int paramInt);
/*     */ 
/*     */   static native void init(DirectMethodHandle paramDirectMethodHandle, Object paramObject, boolean paramBoolean, Class<?> paramClass);
/*     */ 
/*     */   static native void init(MethodType paramMethodType);
/*     */ 
/*     */   static native Object getTarget(MethodHandle paramMethodHandle, int paramInt);
/*     */ 
/*     */   static MemberName getMethodName(MethodHandle paramMethodHandle)
/*     */   {
/*  74 */     return (MemberName)getTarget(paramMethodHandle, 2);
/*     */   }
/*     */ 
/*     */   static AccessibleObject getTargetMethod(MethodHandle paramMethodHandle)
/*     */   {
/*  80 */     return (AccessibleObject)getTarget(paramMethodHandle, 3);
/*     */   }
/*     */ 
/*     */   static Object getTargetInfo(MethodHandle paramMethodHandle)
/*     */   {
/*  88 */     return getTarget(paramMethodHandle, 0);
/*     */   }
/*     */ 
/*     */   static Object[] makeTarget(Class<?> paramClass1, String paramString1, String paramString2, int paramInt, Class<?> paramClass2) {
/*  92 */     return new Object[] { paramClass1, paramString1, paramString2, Integer.valueOf(paramInt), paramClass2 };
/*     */   }
/*     */ 
/*     */   static native int getConstant(int paramInt);
/*     */ 
/*     */   static native void setCallSiteTargetNormal(CallSite paramCallSite, MethodHandle paramMethodHandle);
/*     */ 
/*     */   static native void setCallSiteTargetVolatile(CallSite paramCallSite, MethodHandle paramMethodHandle);
/*     */ 
/*     */   private static native void registerNatives();
/*     */ 
/*     */   private static native int getNamedCon(int paramInt, Object[] paramArrayOfObject);
/*     */ 
/*     */   static boolean verifyConstants()
/*     */   {
/* 262 */     Object[] arrayOfObject = { null };
/* 263 */     for (int i = 0; ; i++) {
/* 264 */       arrayOfObject[0] = null;
/* 265 */       int j = getNamedCon(i, arrayOfObject);
/* 266 */       if (arrayOfObject[0] == null) break;
/* 267 */       String str1 = (String)arrayOfObject[0];
/*     */       try {
/* 269 */         Field localField = Constants.class.getDeclaredField(str1);
/* 270 */         int k = localField.getInt(null);
/* 271 */         if (k != j) {
/* 272 */           String str3 = str1 + ": JVM has " + j + " while Java has " + k;
/* 273 */           if (str1.equals("CONV_OP_LIMIT")) {
/* 274 */             System.err.println("warning: " + str3);
/*     */           }
/*     */           else
/* 277 */             throw new InternalError(str3); 
/*     */         }
/*     */       } catch (Exception localException) { if ((localException instanceof NoSuchFieldException)) {
/* 280 */           String str2 = str1 + ": JVM has " + j + " which Java does not define";
/*     */ 
/* 282 */           if ((str1.startsWith("OP_")) || (str1.startsWith("GC_"))) {
/* 283 */             System.err.println("warning: " + str2);
/* 284 */             continue;
/*     */           }
/*     */         }
/* 287 */         throw new InternalError(str1 + ": access failed, got " + localException);
/*     */       }
/*     */     }
/* 290 */     return true;
/*     */   }
/*     */ 
/*     */   static CallSite makeDynamicCallSite(MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, MemberName paramMemberName, int paramInt)
/*     */   {
/* 306 */     return CallSite.makeSite(paramMethodHandle, paramString, paramMethodType, paramObject, paramMemberName, paramInt);
/*     */   }
/*     */ 
/*     */   static void checkSpreadArgument(Object paramObject, int paramInt)
/*     */   {
/* 313 */     MethodHandleStatics.checkSpreadArgument(paramObject, paramInt);
/*     */   }
/*     */ 
/*     */   static MethodType findMethodHandleType(Class<?> paramClass, Class<?>[] paramArrayOfClass)
/*     */   {
/* 320 */     return MethodType.makeImpl(paramClass, paramArrayOfClass, true);
/*     */   }
/*     */ 
/*     */   static void notifyGenericMethodType(MethodType paramMethodType)
/*     */   {
/* 327 */     paramMethodType.form().notifyGenericMethodType();
/*     */   }
/*     */ 
/*     */   static void raiseException(int paramInt, Object paramObject1, Object paramObject2)
/*     */   {
/* 334 */     String str1 = null;
/* 335 */     switch (paramInt) {
/*     */     case 190:
/*     */       try {
/* 338 */         String str2 = "";
/* 339 */         if ((paramObject2 instanceof AdapterMethodHandle)) {
/* 340 */           i = ((AdapterMethodHandle)paramObject2).getConversion();
/* 341 */           int j = AdapterMethodHandle.extractStackMove(i);
/* 342 */           str2 = " of length " + (j + 1);
/*     */         }
/* 344 */         int i = paramObject1 == null ? 0 : Array.getLength(paramObject1);
/* 345 */         str1 = "required array" + str2 + ", but encountered wrong length " + i;
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException)
/*     */       {
/* 349 */         paramObject2 = [Ljava.lang.Object.class;
/* 350 */         paramInt = 192;
/*     */       }
/*     */ 
/*     */     case 191:
/* 354 */       if (paramObject2 == BootstrapMethodError.class) {
/* 355 */         throw new BootstrapMethodError((Throwable)paramObject1);
/*     */       }
/*     */       break;
/*     */     }
/*     */ 
/* 360 */     if (str1 == null) {
/* 361 */       if ((!(paramObject1 instanceof Class)) && (!(paramObject1 instanceof MethodType)))
/* 362 */         paramObject1 = paramObject1.getClass();
/* 363 */       if (paramObject1 != null)
/* 364 */         str1 = "required " + paramObject2 + " but encountered " + paramObject1;
/*     */       else
/* 366 */         str1 = "required " + paramObject2;
/*     */     }
/* 368 */     switch (paramInt) {
/*     */     case 190:
/* 370 */       throw new ArrayIndexOutOfBoundsException(str1);
/*     */     case 50:
/* 372 */       throw new ClassCastException(str1);
/*     */     case 192:
/* 374 */       throw new ClassCastException(str1);
/*     */     }
/* 376 */     throw new InternalError("unexpected code " + paramInt + ": " + str1);
/*     */   }
/*     */ 
/*     */   static MethodHandle linkMethodHandleConstant(Class<?> paramClass1, int paramInt, Class<?> paramClass2, String paramString, Object paramObject)
/*     */   {
/*     */     try
/*     */     {
/* 387 */       MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP.in(paramClass1);
/* 388 */       return localLookup.linkMethodHandleConstant(paramInt, paramClass2, paramString, paramObject);
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 390 */       IncompatibleClassChangeError localIncompatibleClassChangeError = new IncompatibleClassChangeError();
/* 391 */       localIncompatibleClassChangeError.initCause(localReflectiveOperationException);
/* 392 */       throw localIncompatibleClassChangeError;
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean workaroundWithoutRicochetFrames()
/*     */   {
/* 401 */     assert (!HAVE_RICOCHET_FRAMES) : "this code should not be executed if `-XX:+UseRicochetFrames is enabled";
/* 402 */     return true;
/*     */   }
/*     */ 
/*     */   static boolean isCallerSensitive(MemberName paramMemberName)
/*     */   {
/* 412 */     if (!paramMemberName.isInvocable()) return false;
/* 413 */     Class localClass = paramMemberName.getDeclaringClass();
/* 414 */     switch (paramMemberName.getName()) {
/*     */     case "doPrivileged":
/*     */     case "doPrivilegedWithCombiner":
/* 417 */       return localClass == AccessController.class;
/*     */     case "checkMemberAccess":
/* 419 */       return canBeCalledVirtual(paramMemberName, SecurityManager.class);
/*     */     case "getUnsafe":
/* 421 */       return localClass == Unsafe.class;
/*     */     case "lookup":
/* 423 */       return localClass == MethodHandles.class;
/*     */     case "bind":
/*     */     case "findConstructor":
/*     */     case "findGetter":
/*     */     case "findSetter":
/*     */     case "findSpecial":
/*     */     case "findStatic":
/*     */     case "findStaticGetter":
/*     */     case "findStaticSetter":
/*     */     case "findVirtual":
/*     */     case "unreflect":
/*     */     case "unreflectConstructor":
/*     */     case "unreflectGetter":
/*     */     case "unreflectSetter":
/*     */     case "unreflectSpecial":
/* 438 */       return localClass == MethodHandles.Lookup.class;
/*     */     case "invoke":
/* 440 */       return localClass == Method.class;
/*     */     case "get":
/*     */     case "getBoolean":
/*     */     case "getByte":
/*     */     case "getChar":
/*     */     case "getDouble":
/*     */     case "getFloat":
/*     */     case "getInt":
/*     */     case "getLong":
/*     */     case "getShort":
/*     */     case "set":
/*     */     case "setBoolean":
/*     */     case "setByte":
/*     */     case "setChar":
/*     */     case "setDouble":
/*     */     case "setFloat":
/*     */     case "setInt":
/*     */     case "setLong":
/*     */     case "setShort":
/* 459 */       return localClass == Field.class;
/*     */     case "newInstance":
/* 461 */       if (localClass == Constructor.class) return true;
/* 462 */       if (localClass == Class.class) return true;
/*     */       break;
/*     */     case "forName":
/*     */     case "getClassLoader":
/*     */     case "getClasses":
/*     */     case "getConstructor":
/*     */     case "getConstructors":
/*     */     case "getDeclaredClasses":
/*     */     case "getDeclaredConstructor":
/*     */     case "getDeclaredConstructors":
/*     */     case "getDeclaredField":
/*     */     case "getDeclaredFields":
/*     */     case "getDeclaredMethod":
/*     */     case "getDeclaredMethods":
/*     */     case "getField":
/*     */     case "getFields":
/*     */     case "getMethod":
/*     */     case "getMethods":
/* 480 */       return localClass == Class.class;
/*     */     case "deregisterDriver":
/*     */     case "getConnection":
/*     */     case "getDriver":
/*     */     case "getDrivers":
/* 485 */       return localClass == DriverManager.class;
/*     */     case "newUpdater":
/* 487 */       if (localClass == AtomicIntegerFieldUpdater.class) return true;
/* 488 */       if (localClass == AtomicLongFieldUpdater.class) return true;
/* 489 */       if (localClass == AtomicReferenceFieldUpdater.class) return true;
/*     */       break;
/*     */     case "getContextClassLoader":
/* 492 */       return canBeCalledVirtual(paramMemberName, Thread.class);
/*     */     case "getPackage":
/*     */     case "getPackages":
/* 495 */       return localClass == Package.class;
/*     */     case "getParent":
/*     */     case "getSystemClassLoader":
/* 498 */       return localClass == ClassLoader.class;
/*     */     case "load":
/*     */     case "loadLibrary":
/* 501 */       if (localClass == Runtime.class) return true;
/* 502 */       if (localClass == System.class) return true;
/*     */       break;
/*     */     case "getCallerClass":
/* 505 */       if (localClass == Reflection.class) return true;
/* 506 */       if (localClass == System.class) return true;
/*     */       break;
/*     */     case "getCallerClassLoader":
/* 509 */       return localClass == ClassLoader.class;
/*     */     case "registerAsParallelCapable":
/* 511 */       return canBeCalledVirtual(paramMemberName, ClassLoader.class);
/*     */     case "getProxyClass":
/*     */     case "newProxyInstance":
/* 514 */       return localClass == Proxy.class;
/*     */     case "asInterfaceInstance":
/* 516 */       return localClass == MethodHandleProxies.class;
/*     */     case "clearCache":
/*     */     case "getBundle":
/* 519 */       return localClass == ResourceBundle.class;
/*     */     }
/* 521 */     return false;
/*     */   }
/*     */   static boolean canBeCalledVirtual(MemberName paramMemberName, Class<?> paramClass) {
/* 524 */     Class localClass = paramMemberName.getDeclaringClass();
/* 525 */     if (localClass == paramClass) return true;
/* 526 */     if ((paramMemberName.isStatic()) || (paramMemberName.isPrivate())) return false;
/* 527 */     return (paramClass.isAssignableFrom(localClass)) || (localClass.isInterface());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 124 */     registerNatives();
/*     */ 
/* 126 */     JVM_PUSH_LIMIT = getConstant(0);
/* 127 */     JVM_STACK_MOVE_UNIT = getConstant(1);
/* 128 */     int i = getConstant(2);
/* 129 */     CONV_OP_IMPLEMENTED_MASK = i != 0 ? i : 991;
/* 130 */     i = getConstant(3);
/* 131 */     OP_ROT_ARGS_DOWN_LIMIT_BIAS = i != 0 ? (byte)i : -1;
/* 132 */     HAVE_RICOCHET_FRAMES = (CONV_OP_IMPLEMENTED_MASK & 0x400) != 0;
/* 133 */     COUNT_GWT = getConstant(4) != 0;
/*     */ 
/* 293 */     assert (verifyConstants());
/*     */   }
/*     */ 
/*     */   static class Constants
/*     */   {
/*     */     static final int GC_JVM_PUSH_LIMIT = 0;
/*     */     static final int GC_JVM_STACK_MOVE_UNIT = 1;
/*     */     static final int GC_CONV_OP_IMPLEMENTED_MASK = 2;
/*     */     static final int GC_OP_ROT_ARGS_DOWN_LIMIT_BIAS = 3;
/*     */     static final int GC_COUNT_GWT = 4;
/*     */     static final int ETF_HANDLE_OR_METHOD_NAME = 0;
/*     */     static final int ETF_DIRECT_HANDLE = 1;
/*     */     static final int ETF_METHOD_NAME = 2;
/*     */     static final int ETF_REFLECT_METHOD = 3;
/*     */     static final int MN_IS_METHOD = 65536;
/*     */     static final int MN_IS_CONSTRUCTOR = 131072;
/*     */     static final int MN_IS_FIELD = 262144;
/*     */     static final int MN_IS_TYPE = 524288;
/*     */     static final int MN_SEARCH_SUPERCLASSES = 1048576;
/*     */     static final int MN_SEARCH_INTERFACES = 2097152;
/*     */     static final int VM_INDEX_UNINITIALIZED = -99;
/*     */     static final int ARG_SLOT_PUSH_SHIFT = 16;
/*     */     static final int ARG_SLOT_MASK = 65535;
/*     */     static final int OP_RETYPE_ONLY = 0;
/*     */     static final int OP_RETYPE_RAW = 1;
/*     */     static final int OP_CHECK_CAST = 2;
/*     */     static final int OP_PRIM_TO_PRIM = 3;
/*     */     static final int OP_REF_TO_PRIM = 4;
/*     */     static final int OP_PRIM_TO_REF = 5;
/*     */     static final int OP_SWAP_ARGS = 6;
/*     */     static final int OP_ROT_ARGS = 7;
/*     */     static final int OP_DUP_ARGS = 8;
/*     */     static final int OP_DROP_ARGS = 9;
/*     */     static final int OP_COLLECT_ARGS = 10;
/*     */     static final int OP_SPREAD_ARGS = 11;
/*     */     static final int OP_FOLD_ARGS = 12;
/*     */     static final int CONV_OP_LIMIT = 14;
/*     */     static final int CONV_OP_MASK = 3840;
/*     */     static final int CONV_TYPE_MASK = 15;
/*     */     static final int CONV_VMINFO_MASK = 255;
/*     */     static final int CONV_VMINFO_SHIFT = 0;
/*     */     static final int CONV_OP_SHIFT = 8;
/*     */     static final int CONV_DEST_TYPE_SHIFT = 12;
/*     */     static final int CONV_SRC_TYPE_SHIFT = 16;
/*     */     static final int CONV_STACK_MOVE_SHIFT = 20;
/*     */     static final int CONV_STACK_MOVE_MASK = 4095;
/*     */     static final int DEFAULT_CONV_OP_IMPLEMENTED_MASK = 991;
/*     */     static final int T_BOOLEAN = 4;
/*     */     static final int T_CHAR = 5;
/*     */     static final int T_FLOAT = 6;
/*     */     static final int T_DOUBLE = 7;
/*     */     static final int T_BYTE = 8;
/*     */     static final int T_SHORT = 9;
/*     */     static final int T_INT = 10;
/*     */     static final int T_LONG = 11;
/*     */     static final int T_OBJECT = 12;
/*     */     static final int T_VOID = 14;
/*     */     static final int T_ILLEGAL = 99;
/*     */     static final int REF_getField = 1;
/*     */     static final int REF_getStatic = 2;
/*     */     static final int REF_putField = 3;
/*     */     static final int REF_putStatic = 4;
/*     */     static final int REF_invokeVirtual = 5;
/*     */     static final int REF_invokeStatic = 6;
/*     */     static final int REF_invokeSpecial = 7;
/*     */     static final int REF_newInvokeSpecial = 8;
/*     */     static final int REF_invokeInterface = 9;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandleNatives
 * JD-Core Version:    0.6.2
 */