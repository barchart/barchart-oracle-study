/*    */ package java.awt.event;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import sun.security.action.LoadLibraryAction;
/*    */ 
/*    */ class NativeLibLoader
/*    */ {
/*    */   static void loadLibraries()
/*    */   {
/* 56 */     AccessController.doPrivileged(new LoadLibraryAction("awt"));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.event.NativeLibLoader
 * JD-Core Version:    0.6.2
 */