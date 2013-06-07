/*    */ package sun.security.acl;
/*    */ 
/*    */ import java.security.Principal;
/*    */ 
/*    */ public class WorldGroupImpl extends GroupImpl
/*    */ {
/*    */   public WorldGroupImpl(String paramString)
/*    */   {
/* 37 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public boolean isMember(Principal paramPrincipal)
/*    */   {
/* 46 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.acl.WorldGroupImpl
 * JD-Core Version:    0.6.2
 */