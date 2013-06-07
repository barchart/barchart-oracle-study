/*    */ package java.rmi;
/*    */ 
/*    */ public class UnknownHostException extends RemoteException
/*    */ {
/*    */   private static final long serialVersionUID = -8152710247442114228L;
/*    */ 
/*    */   public UnknownHostException(String paramString)
/*    */   {
/* 48 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public UnknownHostException(String paramString, Exception paramException)
/*    */   {
/* 60 */     super(paramString, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.UnknownHostException
 * JD-Core Version:    0.6.2
 */