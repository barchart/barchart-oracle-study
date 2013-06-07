/*    */ package java.lang.invoke;
/*    */ 
/*    */ class CountingMethodHandle extends AdapterMethodHandle
/*    */ {
/*    */   private int vmcount;
/*    */ 
/*    */   private CountingMethodHandle(MethodHandle paramMethodHandle)
/*    */   {
/* 40 */     super(paramMethodHandle, paramMethodHandle.type(), AdapterMethodHandle.makeConv(0));
/*    */   }
/*    */ 
/*    */   static MethodHandle wrap(MethodHandle paramMethodHandle)
/*    */   {
/* 45 */     if (MethodHandleNatives.COUNT_GWT) {
/* 46 */       return new CountingMethodHandle(paramMethodHandle);
/*    */     }
/* 48 */     return paramMethodHandle;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.CountingMethodHandle
 * JD-Core Version:    0.6.2
 */