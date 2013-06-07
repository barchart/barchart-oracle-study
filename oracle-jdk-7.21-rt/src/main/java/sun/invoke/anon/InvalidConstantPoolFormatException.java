/*    */ package sun.invoke.anon;
/*    */ 
/*    */ public class InvalidConstantPoolFormatException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -6103888330523770949L;
/*    */ 
/*    */   public InvalidConstantPoolFormatException(String paramString, Throwable paramThrowable)
/*    */   {
/* 35 */     super(paramString, paramThrowable);
/*    */   }
/*    */ 
/*    */   public InvalidConstantPoolFormatException(String paramString) {
/* 39 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public InvalidConstantPoolFormatException(Throwable paramThrowable) {
/* 43 */     super(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.invoke.anon.InvalidConstantPoolFormatException
 * JD-Core Version:    0.6.2
 */