/*    */ package sun.awt.motif;
/*    */ 
/*    */ import sun.awt.SunToolkit;
/*    */ import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
/*    */ 
/*    */ final class MToolkitThreadBlockedHandler
/*    */   implements ToolkitThreadBlockedHandler
/*    */ {
/* 35 */   private static ToolkitThreadBlockedHandler priveleged_lock = new MToolkitThreadBlockedHandler();
/*    */ 
/*    */   static ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler()
/*    */   {
/* 39 */     return priveleged_lock;
/*    */   }
/*    */   public void lock() {
/* 42 */     SunToolkit.awtLock();
/*    */   }
/*    */   public void unlock() {
/* 45 */     SunToolkit.awtUnlock();
/*    */   }
/*    */ 
/*    */   public native void enter();
/*    */ 
/*    */   public native void exit();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.MToolkitThreadBlockedHandler
 * JD-Core Version:    0.6.2
 */