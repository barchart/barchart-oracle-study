/*    */ package javax.imageio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class IIOException extends IOException
/*    */ {
/*    */   public IIOException(String paramString)
/*    */   {
/* 54 */     super(paramString);
/*    */   }
/*    */ 
/*    */   public IIOException(String paramString, Throwable paramThrowable)
/*    */   {
/* 70 */     super(paramString);
/* 71 */     initCause(paramThrowable);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.imageio.IIOException
 * JD-Core Version:    0.6.2
 */