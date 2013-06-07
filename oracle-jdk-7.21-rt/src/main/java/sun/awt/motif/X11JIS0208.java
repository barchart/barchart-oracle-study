/*    */ package sun.awt.motif;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CharsetEncoder;
/*    */ import sun.nio.cs.ext.JIS_X_0208_Decoder;
/*    */ import sun.nio.cs.ext.JIS_X_0208_Encoder;
/*    */ 
/*    */ public class X11JIS0208 extends Charset
/*    */ {
/*    */   public X11JIS0208()
/*    */   {
/* 36 */     super("X11JIS0208", null);
/*    */   }
/*    */ 
/*    */   public CharsetEncoder newEncoder() {
/* 40 */     return new JIS_X_0208_Encoder(this);
/*    */   }
/*    */ 
/*    */   public CharsetDecoder newDecoder() {
/* 44 */     return new JIS_X_0208_Decoder(this);
/*    */   }
/*    */ 
/*    */   public boolean contains(Charset paramCharset) {
/* 48 */     return paramCharset instanceof X11JIS0208;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11JIS0208
 * JD-Core Version:    0.6.2
 */