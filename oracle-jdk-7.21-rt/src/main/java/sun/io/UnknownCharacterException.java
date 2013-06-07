/*    */ package sun.io;
/*    */ 
/*    */ import java.io.CharConversionException;
/*    */ 
/*    */ @Deprecated
/*    */ public class UnknownCharacterException extends CharConversionException
/*    */ {
/*    */   private static final long serialVersionUID = -8563196502398436986L;
/*    */ 
/*    */   public UnknownCharacterException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnknownCharacterException(String paramString)
/*    */   {
/* 58 */     super(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.UnknownCharacterException
 * JD-Core Version:    0.6.2
 */