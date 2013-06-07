/*    */ package javax.transaction;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class InvalidTransactionException extends RemoteException
/*    */ {
/*    */   public InvalidTransactionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public InvalidTransactionException(String paramString)
/*    */   {
/* 48 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.transaction.InvalidTransactionException
 * JD-Core Version:    0.6.2
 */