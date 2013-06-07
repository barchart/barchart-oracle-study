/*    */ package sun.io;
/*    */ 
/*    */ import sun.nio.cs.ISO_8859_7;
/*    */ 
/*    */ public class ByteToCharISO8859_7 extends ByteToCharSingleByte
/*    */ {
/* 39 */   private static final ISO_8859_7 nioCoder = new ISO_8859_7();
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 42 */     return "ISO8859_7";
/*    */   }
/*    */ 
/*    */   public ByteToCharISO8859_7() {
/* 46 */     this.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ByteToCharISO8859_7
 * JD-Core Version:    0.6.2
 */