/*    */ package javax.xml.ws.http;
/*    */ 
/*    */ import javax.xml.ws.ProtocolException;
/*    */ 
/*    */ public class HTTPException extends ProtocolException
/*    */ {
/*    */   private int statusCode;
/*    */ 
/*    */   public HTTPException(int statusCode)
/*    */   {
/* 46 */     this.statusCode = statusCode;
/*    */   }
/*    */ 
/*    */   public int getStatusCode()
/*    */   {
/* 54 */     return this.statusCode;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.ws.http.HTTPException
 * JD-Core Version:    0.6.2
 */