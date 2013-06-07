/*    */ package sun.nio.ch;
/*    */ 
/*    */ class NativeThread
/*    */ {
/*    */   static native long current();
/*    */ 
/*    */   static native void signal(long paramLong);
/*    */ 
/*    */   static native void init();
/*    */ 
/*    */   static
/*    */   {
/* 57 */     Util.load();
/* 58 */     init();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.NativeThread
 * JD-Core Version:    0.6.2
 */