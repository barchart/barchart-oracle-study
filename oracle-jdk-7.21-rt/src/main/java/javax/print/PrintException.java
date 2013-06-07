/*    */ package javax.print;
/*    */ 
/*    */ public class PrintException extends Exception
/*    */ {
/*    */   public PrintException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PrintException(String paramString)
/*    */   {
/* 51 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public PrintException(Exception paramException)
/*    */   {
/* 60 */     super(paramException);
/*    */   }
/*    */ 
/*    */   public PrintException(String paramString, Exception paramException)
/*    */   {
/* 70 */     super(paramString, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.PrintException
 * JD-Core Version:    0.6.2
 */