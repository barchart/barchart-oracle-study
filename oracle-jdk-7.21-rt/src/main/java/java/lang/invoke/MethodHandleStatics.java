/*     */ package java.lang.invoke;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ class MethodHandleStatics
/*     */ {
/*  50 */   static final boolean DEBUG_METHOD_HANDLE_NAMES = ((Boolean)arrayOfObject[0]).booleanValue();
/*     */ 
/*     */   static String getNameString(MethodHandle paramMethodHandle, MethodType paramMethodType)
/*     */   {
/*  54 */     if (paramMethodType == null)
/*  55 */       paramMethodType = paramMethodHandle.type();
/*  56 */     MemberName localMemberName = null;
/*  57 */     if (paramMethodHandle != null)
/*  58 */       localMemberName = MethodHandleNatives.getMethodName(paramMethodHandle);
/*  59 */     if (localMemberName == null)
/*  60 */       return "invoke" + paramMethodType;
/*  61 */     return localMemberName.getName() + paramMethodType;
/*     */   }
/*     */ 
/*     */   static String getNameString(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) {
/*  65 */     return getNameString(paramMethodHandle1, paramMethodHandle2 == null ? (MethodType)null : paramMethodHandle2.type());
/*     */   }
/*     */ 
/*     */   static String getNameString(MethodHandle paramMethodHandle) {
/*  69 */     return getNameString(paramMethodHandle, (MethodType)null);
/*     */   }
/*     */ 
/*     */   static String addTypeString(Object paramObject, MethodHandle paramMethodHandle) {
/*  73 */     String str = String.valueOf(paramObject);
/*  74 */     if (paramMethodHandle == null) return str;
/*  75 */     int i = str.indexOf('(');
/*  76 */     if (i >= 0) str = str.substring(0, i);
/*  77 */     return str + paramMethodHandle.type();
/*     */   }
/*     */ 
/*     */   static void checkSpreadArgument(Object paramObject, int paramInt) {
/*  81 */     if (paramObject == null) {
/*  82 */       if (paramInt != 0);
/*     */     }
/*     */     else
/*     */     {
/*     */       int i;
/*  83 */       if ((paramObject instanceof Object[])) {
/*  84 */         i = ((Object[])paramObject).length;
/*  85 */         if (i == paramInt) return; 
/*     */       }
/*  87 */       else { i = Array.getLength(paramObject);
/*  88 */         if (i == paramInt) return;
/*     */       }
/*     */     }
/*  91 */     throw newIllegalArgumentException("Array is not of length " + paramInt);
/*     */   }
/*     */ 
/*     */   static RuntimeException newIllegalStateException(String paramString)
/*     */   {
/*  96 */     return new IllegalStateException(paramString);
/*     */   }
/*     */   static RuntimeException newIllegalStateException(String paramString, Object paramObject) {
/*  99 */     return new IllegalStateException(message(paramString, paramObject));
/*     */   }
/*     */   static RuntimeException newIllegalArgumentException(String paramString) {
/* 102 */     return new IllegalArgumentException(paramString);
/*     */   }
/*     */   static RuntimeException newIllegalArgumentException(String paramString, Object paramObject) {
/* 105 */     return new IllegalArgumentException(message(paramString, paramObject));
/*     */   }
/*     */   static RuntimeException newIllegalArgumentException(String paramString, Object paramObject1, Object paramObject2) {
/* 108 */     return new IllegalArgumentException(message(paramString, paramObject1, paramObject2));
/*     */   }
/*     */   static Error uncaughtException(Throwable paramThrowable) {
/* 111 */     InternalError localInternalError = new InternalError("uncaught exception");
/* 112 */     localInternalError.initCause(paramThrowable);
/* 113 */     return localInternalError;
/*     */   }
/*     */   private static String message(String paramString, Object paramObject) {
/* 116 */     if (paramObject != null) paramString = paramString + ": " + paramObject;
/* 117 */     return paramString;
/*     */   }
/*     */   private static String message(String paramString, Object paramObject1, Object paramObject2) {
/* 120 */     if ((paramObject1 != null) || (paramObject2 != null)) paramString = paramString + ": " + paramObject1 + ", " + paramObject2;
/* 121 */     return paramString;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     Object[] arrayOfObject = { Boolean.valueOf(false) };
/*  44 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Void run() {
/*  46 */         this.val$values[0] = Boolean.valueOf(Boolean.getBoolean("java.lang.invoke.MethodHandle.DEBUG_NAMES"));
/*  47 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandleStatics
 * JD-Core Version:    0.6.2
 */