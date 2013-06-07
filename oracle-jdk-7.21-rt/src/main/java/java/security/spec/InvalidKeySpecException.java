/*    */ package java.security.spec;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ 
/*    */ public class InvalidKeySpecException extends GeneralSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 3546139293998810778L;
/*    */ 
/*    */   public InvalidKeySpecException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public InvalidKeySpecException(String paramString)
/*    */   {
/* 62 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public InvalidKeySpecException(String paramString, Throwable paramThrowable)
/*    */   {
/* 77 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public InvalidKeySpecException(Throwable paramThrowable)
/*    */   {
/* 92 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.spec.InvalidKeySpecException
 * JD-Core Version:    0.6.2
 */