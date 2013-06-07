/*    */ package sun.awt.motif;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.CharsetDecoder;
/*    */ import java.nio.charset.CharsetEncoder;
/*    */ import sun.nio.cs.ext.JIS_X_0201.Decoder;
/*    */ import sun.nio.cs.ext.JIS_X_0201.Encoder;
/*    */ 
/*    */ public class X11JIS0201 extends Charset
/*    */ {
/*    */   public X11JIS0201()
/*    */   {
/* 35 */     super("X11JIS0201", null);
/*    */   }
/*    */ 
/*    */   public CharsetEncoder newEncoder() {
/* 39 */     return new Encoder(this);
/*    */   }
/*    */ 
/*    */   public CharsetDecoder newDecoder() {
/* 43 */     return new JIS_X_0201.Decoder(this);
/*    */   }
/*    */ 
/*    */   public boolean contains(Charset paramCharset) {
/* 47 */     return paramCharset instanceof X11JIS0201;
/*    */   }
/*    */ 
/*    */   private class Encoder extends JIS_X_0201.Encoder {
/*    */     public Encoder(Charset arg2) {
/* 52 */       super();
/*    */     }
/*    */ 
/*    */     public boolean canEncode(char paramChar) {
/* 56 */       if (((paramChar >= 65377) && (paramChar <= 65439)) || (paramChar == '‾') || (paramChar == '¥'))
/*    */       {
/* 59 */         return true;
/*    */       }
/* 61 */       return false;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11JIS0201
 * JD-Core Version:    0.6.2
 */