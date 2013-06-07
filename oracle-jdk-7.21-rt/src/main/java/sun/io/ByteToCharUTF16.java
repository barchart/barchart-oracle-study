/*    */ package sun.io;
/*    */ 
/*    */ public class ByteToCharUTF16 extends ByteToCharUnicode
/*    */ {
/*    */   public ByteToCharUTF16()
/*    */   {
/* 37 */     super(0, true);
/*    */   }
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 41 */     return "UTF-16";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ByteToCharUTF16
 * JD-Core Version:    0.6.2
 */