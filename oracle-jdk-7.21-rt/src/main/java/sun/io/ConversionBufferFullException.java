/*    */ package sun.io;
/*    */ 
/*    */ import java.io.CharConversionException;
/*    */ 
/*    */ @Deprecated
/*    */ public class ConversionBufferFullException extends CharConversionException
/*    */ {
/*    */   private static final long serialVersionUID = -6537318994265003622L;
/*    */ 
/*    */   public ConversionBufferFullException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ConversionBufferFullException(String paramString)
/*    */   {
/* 58 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ConversionBufferFullException
 * JD-Core Version:    0.6.2
 */