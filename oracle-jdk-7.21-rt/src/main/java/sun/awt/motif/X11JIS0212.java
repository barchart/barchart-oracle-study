/*    */ package sun.awt.motif;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CharsetEncoder;
/*    */ import sun.nio.cs.ext.JIS_X_0212_Decoder;
/*    */ import sun.nio.cs.ext.JIS_X_0212_Encoder;
/*    */ 
/*    */ public class X11JIS0212 extends Charset
/*    */ {
/*    */   public X11JIS0212()
/*    */   {
/* 36 */     super("X11JIS0212", null);
/*    */   }
/*    */   public CharsetEncoder newEncoder() {
/* 39 */     return new JIS_X_0212_Encoder(this);
/*    */   }
/*    */   public CharsetDecoder newDecoder() {
/* 42 */     return new JIS_X_0212_Decoder(this);
/*    */   }
/*    */ 
/*    */   public boolean contains(Charset paramCharset) {
/* 46 */     return paramCharset instanceof X11JIS0212;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11JIS0212
 * JD-Core Version:    0.6.2
 */