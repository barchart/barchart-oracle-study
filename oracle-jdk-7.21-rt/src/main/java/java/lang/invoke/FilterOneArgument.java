/*    */ package java.lang.invoke;
/*    */ 
/*    */ class FilterOneArgument extends BoundMethodHandle
/*    */ {
/*    */   protected final MethodHandle filter;
/*    */   protected final MethodHandle target;
/*    */   private static final MethodHandle INVOKE;
/*    */ 
/*    */   String debugString()
/*    */   {
/* 45 */     return this.target.toString();
/*    */   }
/*    */ 
/*    */   protected Object invoke(Object paramObject) throws Throwable {
/* 49 */     Object localObject = this.filter.invokeExact(paramObject);
/* 50 */     return this.target.invokeExact(localObject);
/*    */   }
/*    */ 
/*    */   protected FilterOneArgument(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*    */   {
/* 65 */     super(INVOKE);
/* 66 */     this.filter = paramMethodHandle1;
/* 67 */     this.target = paramMethodHandle2;
/*    */   }
/*    */ 
/*    */   public static MethodHandle make(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
/*    */   {
/* 75 */     if (paramMethodHandle1 == null) return paramMethodHandle2;
/* 76 */     if (paramMethodHandle2 == null) return paramMethodHandle1;
/* 77 */     return new FilterOneArgument(paramMethodHandle1, paramMethodHandle2);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 56 */       INVOKE = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(FilterOneArgument.class, "invoke", MethodType.genericMethodType(1));
/*    */     }
/*    */     catch (ReflectiveOperationException localReflectiveOperationException)
/*    */     {
/* 60 */       throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
/*    */     }
/*    */ 
/* 71 */     assert (MethodHandleNatives.workaroundWithoutRicochetFrames());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.FilterOneArgument
 * JD-Core Version:    0.6.2
 */