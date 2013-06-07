/*    */ package javax.xml.ws;
/*    */ 
/*    */ import java.security.BasicPermission;
/*    */ 
/*    */ public final class WebServicePermission extends BasicPermission
/*    */ {
/*    */   private static final long serialVersionUID = -146474640053770988L;
/*    */ 
/*    */   public WebServicePermission(String name)
/*    */   {
/* 69 */     super(name);
/*    */   }
/*    */ 
/*    */   public WebServicePermission(String name, String actions)
/*    */   {
/* 82 */     super(name, actions);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.WebServicePermission
 * JD-Core Version:    0.6.2
 */