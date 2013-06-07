/*    */ package java.security;
/*    */ 
/*    */ public class ProviderException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 5256023526693665674L;
/*    */ 
/*    */   public ProviderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProviderException(String paramString)
/*    */   {
/* 57 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ProviderException(String paramString, Throwable paramThrowable)
/*    */   {
/* 72 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public ProviderException(Throwable paramThrowable)
/*    */   {
/* 87 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.ProviderException
 * JD-Core Version:    0.6.2
 */