/*    */ package javax.activity;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class ActivityRequiredException extends RemoteException
/*    */ {
/*    */   public ActivityRequiredException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ActivityRequiredException(String paramString)
/*    */   {
/* 48 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ActivityRequiredException(Throwable paramThrowable)
/*    */   {
/* 58 */     this("", paramThrowable);
/*    */   }
/*    */ 
/*    */   public ActivityRequiredException(String paramString, Throwable paramThrowable)
/*    */   {
/* 70 */     super(paramString, paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.activity.ActivityRequiredException
 * JD-Core Version:    0.6.2
 */