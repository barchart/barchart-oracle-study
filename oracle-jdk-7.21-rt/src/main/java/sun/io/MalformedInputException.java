/*    */ package sun.io;
/*    */ 
/*    */ import java.io.CharConversionException;
/*    */ 
/*    */ @Deprecated
/*    */ public class MalformedInputException extends CharConversionException
/*    */ {
/*    */   private static final long serialVersionUID = 2585413228493157652L;
/*    */ 
/*    */   public MalformedInputException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MalformedInputException(String paramString)
/*    */   {
/* 58 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.MalformedInputException
 * JD-Core Version:    0.6.2
 */