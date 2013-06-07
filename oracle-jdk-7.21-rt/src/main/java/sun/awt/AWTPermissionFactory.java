/*    */ package sun.awt;
/*    */ 
/*    */ import java.awt.AWTPermission;
/*    */ import sun.security.util.PermissionFactory;
/*    */ 
/*    */ public class AWTPermissionFactory
/*    */   implements PermissionFactory<AWTPermission>
/*    */ {
/*    */   public AWTPermission newPermission(String paramString)
/*    */   {
/* 40 */     return new AWTPermission(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.AWTPermissionFactory
 * JD-Core Version:    0.6.2
 */