/*     */ package java.lang.invoke;
/*     */ 
/*     */ import sun.invoke.empty.Empty;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public abstract class CallSite
/*     */ {
/*     */   private MemberName vmmethod;
/*     */   private int vmindex;
/*     */   MethodHandle target;
/*     */   private static final MethodHandle GET_TARGET;
/*     */   private static final Unsafe unsafe;
/*     */   private static final long TARGET_OFFSET;
/*     */ 
/*     */   CallSite(MethodType paramMethodType)
/*     */   {
/* 110 */     this.target = paramMethodType.invokers().uninitializedCallSite();
/*     */   }
/*     */ 
/*     */   CallSite(MethodHandle paramMethodHandle)
/*     */   {
/* 120 */     paramMethodHandle.type();
/* 121 */     this.target = paramMethodHandle;
/*     */   }
/*     */ 
/*     */   CallSite(MethodType paramMethodType, MethodHandle paramMethodHandle)
/*     */     throws Throwable
/*     */   {
/* 136 */     this(paramMethodType);
/* 137 */     ConstantCallSite localConstantCallSite = (ConstantCallSite)this;
/* 138 */     MethodHandle localMethodHandle = (MethodHandle)paramMethodHandle.invokeWithArguments(new Object[] { localConstantCallSite });
/* 139 */     checkTargetChange(this.target, localMethodHandle);
/* 140 */     this.target = localMethodHandle;
/*     */   }
/*     */ 
/*     */   public MethodType type()
/*     */   {
/* 152 */     return this.target.type();
/*     */   }
/*     */ 
/*     */   void initializeFromJVM(String paramString, MethodType paramMethodType, MemberName paramMemberName, int paramInt)
/*     */   {
/* 162 */     if (this.vmmethod != null)
/*     */     {
/* 164 */       throw new BootstrapMethodError("call site has already been linked to an invokedynamic instruction");
/*     */     }
/* 166 */     if (!type().equals(paramMethodType)) {
/* 167 */       throw wrongTargetType(this.target, paramMethodType);
/*     */     }
/* 169 */     this.vmindex = paramInt;
/* 170 */     this.vmmethod = paramMemberName;
/*     */   }
/*     */ 
/*     */   public abstract MethodHandle getTarget();
/*     */ 
/*     */   public abstract void setTarget(MethodHandle paramMethodHandle);
/*     */ 
/*     */   void checkTargetChange(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*     */   {
/* 210 */     MethodType localMethodType1 = paramMethodHandle1.type();
/* 211 */     MethodType localMethodType2 = paramMethodHandle2.type();
/* 212 */     if (!localMethodType2.equals(localMethodType1))
/* 213 */       throw wrongTargetType(paramMethodHandle2, localMethodType1);
/*     */   }
/*     */ 
/*     */   private static WrongMethodTypeException wrongTargetType(MethodHandle paramMethodHandle, MethodType paramMethodType) {
/* 217 */     return new WrongMethodTypeException(String.valueOf(paramMethodHandle) + " should be of type " + paramMethodType);
/*     */   }
/*     */ 
/*     */   public abstract MethodHandle dynamicInvoker();
/*     */ 
/*     */   MethodHandle makeDynamicInvoker()
/*     */   {
/* 237 */     MethodHandle localMethodHandle1 = MethodHandleImpl.bindReceiver(GET_TARGET, this);
/* 238 */     MethodHandle localMethodHandle2 = MethodHandles.exactInvoker(type());
/* 239 */     return MethodHandles.foldArguments(localMethodHandle2, localMethodHandle1);
/*     */   }
/*     */ 
/*     */   static Empty uninitializedCallSite()
/*     */   {
/* 255 */     throw new IllegalStateException("uninitialized call site");
/*     */   }
/*     */ 
/*     */   void setTargetNormal(MethodHandle paramMethodHandle)
/*     */   {
/* 270 */     MethodHandleNatives.setCallSiteTargetNormal(this, paramMethodHandle);
/*     */   }
/*     */ 
/*     */   MethodHandle getTargetVolatile() {
/* 274 */     return (MethodHandle)unsafe.getObjectVolatile(this, TARGET_OFFSET);
/*     */   }
/*     */ 
/*     */   void setTargetVolatile(MethodHandle paramMethodHandle) {
/* 278 */     MethodHandleNatives.setCallSiteTargetVolatile(this, paramMethodHandle);
/*     */   }
/*     */ 
/*     */   static CallSite makeSite(MethodHandle paramMethodHandle, String paramString, MethodType paramMethodType, Object paramObject, MemberName paramMemberName, int paramInt)
/*     */   {
/* 289 */     Class localClass = paramMemberName.getDeclaringClass();
/* 290 */     MethodHandles.Lookup localLookup = MethodHandles.Lookup.IMPL_LOOKUP.in(localClass);
/*     */     CallSite localCallSite;
/*     */     try
/*     */     {
/* 294 */       paramObject = maybeReBox(paramObject);
/*     */       Object localObject1;
/* 295 */       if (paramObject == null) {
/* 296 */         localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType);
/* 297 */       } else if (!paramObject.getClass().isArray()) {
/* 298 */         localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, paramObject);
/*     */       } else {
/* 300 */         localObject2 = (Object[])paramObject;
/* 301 */         maybeReBoxElements((Object[])localObject2);
/* 302 */         if (3 + localObject2.length > 255)
/* 303 */           throw new BootstrapMethodError("too many bootstrap method arguments");
/* 304 */         MethodType localMethodType = paramMethodHandle.type();
/* 305 */         if ((localMethodType.parameterCount() == 4) && (localMethodType.parameterType(3) == [Ljava.lang.Object.class))
/* 306 */           localObject1 = paramMethodHandle.invoke(localLookup, paramString, paramMethodType, (Object[])localObject2);
/*     */         else {
/* 308 */           localObject1 = MethodHandles.spreadInvoker(localMethodType, 3).invoke(paramMethodHandle, localLookup, paramString, paramMethodType, (Object[])localObject2);
/*     */         }
/*     */       }
/*     */ 
/* 312 */       if ((localObject1 instanceof CallSite))
/* 313 */         localCallSite = (CallSite)localObject1;
/*     */       else {
/* 315 */         throw new ClassCastException("bootstrap method failed to produce a CallSite");
/*     */       }
/* 317 */       if (!localCallSite.getTarget().type().equals(paramMethodType))
/* 318 */         throw new WrongMethodTypeException("wrong type: " + localCallSite.getTarget());
/*     */     }
/*     */     catch (Throwable localThrowable)
/*     */     {
/*     */       Object localObject2;
/* 321 */       if ((localThrowable instanceof BootstrapMethodError))
/* 322 */         localObject2 = (BootstrapMethodError)localThrowable;
/*     */       else
/* 324 */         localObject2 = new BootstrapMethodError("call site initialization exception", localThrowable);
/* 325 */       throw ((Throwable)localObject2);
/*     */     }
/* 327 */     return localCallSite;
/*     */   }
/*     */ 
/*     */   private static Object maybeReBox(Object paramObject) {
/* 331 */     if ((paramObject instanceof Integer)) {
/* 332 */       int i = ((Integer)paramObject).intValue();
/* 333 */       if (i == (byte)i)
/* 334 */         paramObject = Integer.valueOf(i);
/*     */     }
/* 336 */     return paramObject;
/*     */   }
/*     */   private static void maybeReBoxElements(Object[] paramArrayOfObject) {
/* 339 */     for (int i = 0; i < paramArrayOfObject.length; i++)
/* 340 */       paramArrayOfObject[i] = maybeReBox(paramArrayOfObject[i]);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  88 */     MethodHandleImpl.initStatics();
/*     */     try
/*     */     {
/* 245 */       GET_TARGET = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(CallSite.class, "getTarget", MethodType.methodType(MethodHandle.class));
/*     */     }
/*     */     catch (ReflectiveOperationException localReflectiveOperationException) {
/* 248 */       throw new InternalError();
/*     */     }
/*     */ 
/* 259 */     unsafe = Unsafe.getUnsafe();
/*     */     try
/*     */     {
/* 264 */       TARGET_OFFSET = unsafe.objectFieldOffset(CallSite.class.getDeclaredField("target")); } catch (Exception localException) {
/* 265 */       throw new Error(localException);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.CallSite
 * JD-Core Version:    0.6.2
 */