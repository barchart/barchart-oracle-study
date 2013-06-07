/*    */ package sun.io;
/*    */ 
/*    */ import sun.nio.cs.MS1254;
/*    */ 
/*    */ public class ByteToCharCp1254 extends ByteToCharSingleByte
/*    */ {
/* 39 */   private static final MS1254 nioCoder = new MS1254();
/*    */ 
/*    */   public String getCharacterEncoding() {
/* 42 */     return "Cp1254";
/*    */   }
/*    */ 
/*    */   public ByteToCharCp1254() {
/* 46 */     this.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.io.ByteToCharCp1254
 * JD-Core Version:    0.6.2
 */