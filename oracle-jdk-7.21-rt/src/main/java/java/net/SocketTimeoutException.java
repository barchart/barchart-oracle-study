/*    */ package java.net;
/*    */ 
/*    */ import java.io.InterruptedIOException;
/*    */ 
/*    */ public class SocketTimeoutException extends InterruptedIOException
/*    */ {
/*    */   private static final long serialVersionUID = -8846654841826352300L;
/*    */ 
/*    */   public SocketTimeoutException(String paramString)
/*    */   {
/* 43 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public SocketTimeoutException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocketTimeoutException
 * JD-Core Version:    0.6.2
 */