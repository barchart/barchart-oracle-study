/*    */ package java.rmi;
/*    */ 
/*    */ public class AccessException extends RemoteException
/*    */ {
/*    */   private static final long serialVersionUID = 6314925228044966088L;
/*    */ 
/*    */   public AccessException(String paramString)
/*    */   {
/* 56 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public AccessException(String paramString, Exception paramException)
/*    */   {
/* 68 */     super(paramString, paramException);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.rmi.AccessException
 * JD-Core Version:    0.6.2
 */