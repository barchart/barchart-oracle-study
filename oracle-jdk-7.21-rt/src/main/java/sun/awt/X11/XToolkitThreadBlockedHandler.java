/*    */ package sun.awt.X11;
/*    */ 
/*    */ import java.awt.Toolkit;
/*    */ import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
/*    */ 
/*    */ final class XToolkitThreadBlockedHandler
/*    */   implements ToolkitThreadBlockedHandler
/*    */ {
/* 34 */   private static final ToolkitThreadBlockedHandler priveleged_lock = new XToolkitThreadBlockedHandler();
/*    */ 
/* 36 */   private static final XToolkit tk = (XToolkit)Toolkit.getDefaultToolkit();
/*    */ 
/*    */   static ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler()
/*    */   {
/* 40 */     return priveleged_lock;
/*    */   }
/*    */   public void lock() {
/* 43 */     XToolkit.awtLock();
/*    */   }
/*    */   public void unlock() {
/* 46 */     XToolkit.awtUnlock();
/*    */   }
/*    */   public void enter() {
/* 49 */     tk.run(true);
/*    */   }
/*    */   public void exit() {
/* 52 */     XlibWrapper.ExitSecondaryLoop();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XToolkitThreadBlockedHandler
 * JD-Core Version:    0.6.2
 */