/*    */ package java.security;
/*    */ 
/*    */ public class SignatureException extends GeneralSecurityException
/*    */ {
/*    */   private static final long serialVersionUID = 7509989324975124438L;
/*    */ 
/*    */   public SignatureException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SignatureException(String paramString)
/*    */   {
/* 55 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public SignatureException(String paramString, Throwable paramThrowable)
/*    */   {
/* 70 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public SignatureException(Throwable paramThrowable)
/*    */   {
/* 85 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.SignatureException
 * JD-Core Version:    0.6.2
 */