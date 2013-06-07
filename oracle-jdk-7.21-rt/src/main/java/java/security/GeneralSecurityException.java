/*    */ package java.security;
/*    */ 
/*    */ public class GeneralSecurityException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 894798122053539237L;
/*    */ 
/*    */   public GeneralSecurityException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public GeneralSecurityException(String paramString)
/*    */   {
/* 56 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public GeneralSecurityException(String paramString, Throwable paramThrowable)
/*    */   {
/* 71 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public GeneralSecurityException(Throwable paramThrowable)
/*    */   {
/* 86 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.GeneralSecurityException
 * JD-Core Version:    0.6.2
 */