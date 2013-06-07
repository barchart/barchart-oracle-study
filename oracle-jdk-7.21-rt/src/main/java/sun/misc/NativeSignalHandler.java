/*    */ package sun.misc;
/*    */ 
/*    */ final class NativeSignalHandler
/*    */   implements SignalHandler
/*    */ {
/*    */   private final long handler;
/*    */ 
/*    */   long getHandler()
/*    */   {
/* 35 */     return this.handler;
/*    */   }
/*    */ 
/*    */   NativeSignalHandler(long paramLong) {
/* 39 */     this.handler = paramLong;
/*    */   }
/*    */ 
/*    */   public void handle(Signal paramSignal) {
/* 43 */     handle0(paramSignal.getNumber(), this.handler);
/*    */   }
/*    */ 
/*    */   private static native void handle0(int paramInt, long paramLong);
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.NativeSignalHandler
 * JD-Core Version:    0.6.2
 */