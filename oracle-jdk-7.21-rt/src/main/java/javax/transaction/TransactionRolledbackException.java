/*    */ package javax.transaction;
/*    */ 
/*    */ import java.rmi.RemoteException;
/*    */ 
/*    */ public class TransactionRolledbackException extends RemoteException
/*    */ {
/*    */   public TransactionRolledbackException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TransactionRolledbackException(String paramString)
/*    */   {
/* 50 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.transaction.TransactionRolledbackException
 * JD-Core Version:    0.6.2
 */