/*     */ package java.lang.invoke;
/*     */ 
/*     */ import sun.invoke.empty.Empty;
/*     */ 
/*     */ class Invokers
/*     */ {
/*     */   private final MethodType targetType;
/*     */   private MethodHandle exactInvoker;
/*     */   private MethodHandle erasedInvoker;
/*     */   MethodHandle erasedInvokerWithDrops;
/*     */   private MethodHandle generalInvoker;
/*     */   private MethodHandle varargsInvoker;
/*     */   private final MethodHandle[] spreadInvokers;
/*     */   private MethodHandle uninitializedCallSite;
/* 126 */   private static MethodHandle THROW_UCS = null;
/*     */ 
/*     */   Invokers(MethodType paramMethodType)
/*     */   {
/*  62 */     this.targetType = paramMethodType;
/*  63 */     this.spreadInvokers = new MethodHandle[paramMethodType.parameterCount() + 1];
/*     */   }
/*     */ 
/*     */   static MethodType invokerType(MethodType paramMethodType) {
/*  67 */     return paramMethodType.insertParameterTypes(0, new Class[] { MethodHandle.class });
/*     */   }
/*     */ 
/*     */   MethodHandle exactInvoker() {
/*  71 */     MethodHandle localMethodHandle = this.exactInvoker;
/*  72 */     if (localMethodHandle != null) return localMethodHandle;
/*  73 */     localMethodHandle = lookupInvoker("invokeExact");
/*  74 */     this.exactInvoker = localMethodHandle;
/*  75 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   MethodHandle generalInvoker() {
/*  79 */     MethodHandle localMethodHandle = this.generalInvoker;
/*  80 */     if (localMethodHandle != null) return localMethodHandle;
/*  81 */     localMethodHandle = lookupInvoker("invoke");
/*  82 */     this.generalInvoker = localMethodHandle;
/*  83 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   private MethodHandle lookupInvoker(String paramString) {
/*     */     MethodHandle localMethodHandle;
/*     */     try {
/*  89 */       localMethodHandle = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(MethodHandle.class, paramString, this.targetType);
/*     */     } catch (ReflectiveOperationException localReflectiveOperationException) {
/*  91 */       throw new InternalError("JVM cannot find invoker for " + this.targetType);
/*     */     }
/*  93 */     assert (invokerType(this.targetType) == localMethodHandle.type());
/*  94 */     assert (!localMethodHandle.isVarargsCollector());
/*  95 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   MethodHandle erasedInvoker() {
/*  99 */     MethodHandle localMethodHandle1 = exactInvoker();
/* 100 */     MethodHandle localMethodHandle2 = this.erasedInvoker;
/* 101 */     if (localMethodHandle2 != null) return localMethodHandle2;
/* 102 */     MethodType localMethodType = this.targetType.erase();
/* 103 */     localMethodHandle2 = localMethodHandle1.asType(invokerType(localMethodType));
/* 104 */     this.erasedInvoker = localMethodHandle2;
/* 105 */     return localMethodHandle2;
/*     */   }
/*     */ 
/*     */   MethodHandle spreadInvoker(int paramInt) {
/* 109 */     MethodHandle localMethodHandle1 = this.spreadInvokers[paramInt];
/* 110 */     if (localMethodHandle1 != null) return localMethodHandle1;
/* 111 */     MethodHandle localMethodHandle2 = generalInvoker();
/* 112 */     int i = this.targetType.parameterCount() - paramInt;
/* 113 */     localMethodHandle1 = localMethodHandle2.asSpreader([Ljava.lang.Object.class, i);
/* 114 */     this.spreadInvokers[paramInt] = localMethodHandle1;
/* 115 */     return localMethodHandle1;
/*     */   }
/*     */ 
/*     */   MethodHandle varargsInvoker() {
/* 119 */     MethodHandle localMethodHandle = this.varargsInvoker;
/* 120 */     if (localMethodHandle != null) return localMethodHandle;
/* 121 */     localMethodHandle = spreadInvoker(0).asType(invokerType(MethodType.genericMethodType(0, true)));
/* 122 */     this.varargsInvoker = localMethodHandle;
/* 123 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   MethodHandle uninitializedCallSite()
/*     */   {
/* 129 */     MethodHandle localMethodHandle = this.uninitializedCallSite;
/* 130 */     if (localMethodHandle != null) return localMethodHandle;
/* 131 */     if (this.targetType.parameterCount() > 0) {
/* 132 */       MethodType localMethodType = this.targetType.dropParameterTypes(0, this.targetType.parameterCount());
/* 133 */       Invokers localInvokers = localMethodType.invokers();
/* 134 */       localMethodHandle = MethodHandles.dropArguments(localInvokers.uninitializedCallSite(), 0, this.targetType.parameterList());
/*     */ 
/* 136 */       assert (localMethodHandle.type().equals(this.targetType));
/* 137 */       this.uninitializedCallSite = localMethodHandle;
/* 138 */       return localMethodHandle;
/*     */     }
/* 140 */     if (THROW_UCS == null) {
/*     */       try {
/* 142 */         THROW_UCS = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(CallSite.class, "uninitializedCallSite", MethodType.methodType(Empty.class));
/*     */       }
/*     */       catch (ReflectiveOperationException localReflectiveOperationException)
/*     */       {
/* 146 */         throw new RuntimeException(localReflectiveOperationException);
/*     */       }
/*     */     }
/* 149 */     localMethodHandle = AdapterMethodHandle.makeRetypeRaw(this.targetType, THROW_UCS);
/* 150 */     assert (localMethodHandle.type().equals(this.targetType));
/* 151 */     this.uninitializedCallSite = localMethodHandle;
/* 152 */     return localMethodHandle;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 156 */     return "Invokers" + this.targetType;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.Invokers
 * JD-Core Version:    0.6.2
 */