/*    */ package sun.io;
/*    */ 
/*    */ import sun.nio.cs.MS1257;
/*    */ 
/*    */ public class ByteToCharCp1257 extends ByteToCharSingleByte
/*    */ {
/* 39 */   private static final MS1257 nioCoder = new MS1257();
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 42 */     return "Cp1257";
/*    */   }
/*    */ 
/*    */   public ByteToCharCp1257() {
/* 46 */     this.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ByteToCharCp1257
 * JD-Core Version:    0.6.2
 */