/*    */ package java.util.concurrent;
/*    */ 
/*    */ public class ExecutionException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 7830266012832686185L;
/*    */ 
/*    */   protected ExecutionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected ExecutionException(String paramString)
/*    */   {
/* 65 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ExecutionException(String paramString, Throwable paramThrowable)
/*    */   {
/* 77 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public ExecutionException(Throwable paramThrowable)
/*    */   {
/* 92 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.util.concurrent.ExecutionException
 * JD-Core Version:    0.6.2
 */