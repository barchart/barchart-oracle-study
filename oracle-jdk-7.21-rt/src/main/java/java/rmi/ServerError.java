/*    */ package java.rmi;
/*    */ 
/*    */ public class ServerError extends RemoteException
/*    */ {
/*    */   private static final long serialVersionUID = 8455284893909696482L;
/*    */ 
/*    */   public ServerError(String paramString, Error paramError)
/*    */   {
/* 54 */     super(paramString, paramError);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.ServerError
 * JD-Core Version:    0.6.2
 */