/*    */ package java.rmi;
/*    */ 
/*    */ public class ServerException extends RemoteException
/*    */ {
/*    */   private static final long serialVersionUID = -4775845313121906682L;
/*    */ 
/*    */   public ServerException(String paramString)
/*    */   {
/* 53 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ServerException(String paramString, Exception paramException)
/*    */   {
/* 65 */     super(paramString, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.ServerException
 * JD-Core Version:    0.6.2
 */