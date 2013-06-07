/*    */ package javax.security.auth.login;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ 
/*    */ public class LoginException extends GeneralSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = -4679091624035232488L;
/*    */ 
/*    */   public LoginException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LoginException(String paramString)
/*    */   {
/* 56 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.security.auth.login.LoginException
 * JD-Core Version:    0.6.2
 */