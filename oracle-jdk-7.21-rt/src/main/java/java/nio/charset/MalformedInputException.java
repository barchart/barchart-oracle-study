/*    */ package java.nio.charset;
/*    */ 
/*    */ public class MalformedInputException extends CharacterCodingException
/*    */ {
/*    */   private static final long serialVersionUID = -3438823399834806194L;
/*    */   private int inputLength;
/*    */ 
/*    */   public MalformedInputException(int paramInt)
/*    */   {
/* 46 */     this.inputLength = paramInt;
/*    */   }
/*    */ 
/*    */   public int getInputLength() {
/* 50 */     return this.inputLength;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 54 */     return "Input length = " + this.inputLength;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.charset.MalformedInputException
 * JD-Core Version:    0.6.2
 */