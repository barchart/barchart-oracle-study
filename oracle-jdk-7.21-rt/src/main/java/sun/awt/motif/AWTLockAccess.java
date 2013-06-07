/*    */ package sun.awt.motif;
/*    */ 
/*    */ final class AWTLockAccess
/*    */ {
/*    */   static native void awtLock();
/*    */ 
/*    */   static native void awtUnlock();
/*    */ 
/*    */   static void awtWait()
/*    */   {
/* 31 */     awtWait(0L);
/*    */   }
/*    */ 
/*    */   static native void awtWait(long paramLong);
/*    */ 
/*    */   static native void awtNotifyAll();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.AWTLockAccess
 * JD-Core Version:    0.6.2
 */