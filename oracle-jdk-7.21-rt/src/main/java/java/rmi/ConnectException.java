/*    */ package java.rmi;
/*    */ 
/*    */ public class ConnectException extends RemoteException
/*    */ {
/*    */   private static final long serialVersionUID = 4863550261346652506L;
/*    */ 
/*    */   public ConnectException(String paramString)
/*    */   {
/* 48 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public ConnectException(String paramString, Exception paramException)
/*    */   {
/* 60 */     super(paramString, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.ConnectException
 * JD-Core Version:    0.6.2
 */