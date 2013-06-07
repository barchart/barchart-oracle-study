/*    */ package sun.io;
/*    */ 
/*    */ import sun.nio.cs.ISO_8859_4;
/*    */ 
/*    */ public class ByteToCharISO8859_4 extends ByteToCharSingleByte
/*    */ {
/* 38 */   private static final ISO_8859_4 nioCoder = new ISO_8859_4();
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 41 */     return "ISO8859_4";
/*    */   }
/*    */ 
/*    */   public ByteToCharISO8859_4() {
/* 45 */     this.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ByteToCharISO8859_4
 * JD-Core Version:    0.6.2
 */