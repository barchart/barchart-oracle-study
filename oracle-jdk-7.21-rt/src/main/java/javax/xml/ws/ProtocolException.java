/*    */ package javax.xml.ws;
/*    */ 
/*    */ public class ProtocolException extends WebServiceException
/*    */ {
/*    */   public ProtocolException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProtocolException(String message)
/*    */   {
/* 54 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProtocolException(String message, Throwable cause)
/*    */   {
/* 71 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public ProtocolException(Throwable cause)
/*    */   {
/* 86 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.ProtocolException
 * JD-Core Version:    0.6.2
 */