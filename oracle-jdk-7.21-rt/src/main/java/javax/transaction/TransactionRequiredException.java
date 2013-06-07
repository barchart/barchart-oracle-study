/*    */ package javax.transaction;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class TransactionRequiredException extends RemoteException
/*    */ {
/*    */   public TransactionRequiredException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TransactionRequiredException(String paramString)
/*    */   {
/* 47 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.transaction.TransactionRequiredException
 * JD-Core Version:    0.6.2
 */