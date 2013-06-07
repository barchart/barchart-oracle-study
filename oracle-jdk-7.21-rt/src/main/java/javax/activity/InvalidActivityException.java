/*    */ package javax.activity;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class InvalidActivityException extends RemoteException
/*    */ {
/*    */   public InvalidActivityException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public InvalidActivityException(String paramString)
/*    */   {
/* 52 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public InvalidActivityException(Throwable paramThrowable)
/*    */   {
/* 62 */     this("", paramThrowable);
/*    */   }
/*    */ 
/*    */   public InvalidActivityException(String paramString, Throwable paramThrowable)
/*    */   {
/* 74 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activity.InvalidActivityException
 * JD-Core Version:    0.6.2
 */