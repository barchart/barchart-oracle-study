/*    */ package sun.io;
/*    */ 
/*    */ public class CharToByteUTF16 extends CharToByteUnicode
/*    */ {
/*    */   public CharToByteUTF16()
/*    */   {
/* 37 */     super(1, true);
/*    */   }
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 41 */     return "UTF-16";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.CharToByteUTF16
 * JD-Core Version:    0.6.2
 */