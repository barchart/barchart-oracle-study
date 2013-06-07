/*    */ package javax.activity;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class ActivityCompletedException extends RemoteException
/*    */ {
/*    */   public ActivityCompletedException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ActivityCompletedException(String paramString)
/*    */   {
/* 50 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ActivityCompletedException(Throwable paramThrowable)
/*    */   {
/* 60 */     this("", paramThrowable);
/*    */   }
/*    */ 
/*    */   public ActivityCompletedException(String paramString, Throwable paramThrowable)
/*    */   {
/* 72 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activity.ActivityCompletedException
 * JD-Core Version:    0.6.2
 */