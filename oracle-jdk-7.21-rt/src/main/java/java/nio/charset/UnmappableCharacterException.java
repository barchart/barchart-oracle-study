/*    */ package java.nio.charset;
/*    */ 
/*    */ public class UnmappableCharacterException extends CharacterCodingException
/*    */ {
/*    */   private static final long serialVersionUID = -7026962371537706123L;
/*    */   private int inputLength;
/*    */ 
/*    */   public UnmappableCharacterException(int paramInt)
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
 * Qualified Name:     java.nio.charset.UnmappableCharacterException
 * JD-Core Version:    0.6.2
 */