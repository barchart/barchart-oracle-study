/*    */ package sun.security.action;
/*    */ 
/*    */ import java.security.PrivilegedAction;
/*    */ 
/*    */ public class LoadLibraryAction
/*    */   implements PrivilegedAction<Void>
/*    */ {
/*    */   private String theLib;
/*    */ 
/*    */   public LoadLibraryAction(String paramString)
/*    */   {
/* 60 */     this.theLib = paramString;
/*    */   }
/*    */ 
/*    */   public Void run()
/*    */   {
/* 67 */     System.loadLibrary(this.theLib);
/* 68 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.action.LoadLibraryAction
 * JD-Core Version:    0.6.2
 */