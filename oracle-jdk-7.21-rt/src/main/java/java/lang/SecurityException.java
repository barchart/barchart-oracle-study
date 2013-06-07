/*    */ package java.lang;
/*    */ 
/*    */ public class SecurityException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 6878364983674394167L;
/*    */ 
/*    */   public SecurityException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SecurityException(String paramString)
/*    */   {
/* 52 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public SecurityException(String paramString, Throwable paramThrowable)
/*    */   {
/* 67 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public SecurityException(Throwable paramThrowable)
/*    */   {
/* 82 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.SecurityException
 * JD-Core Version:    0.6.2
 */