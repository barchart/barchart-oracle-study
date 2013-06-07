/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import sun.nio.cs.ext.DoubleByteEncoder;
/*     */ 
/*     */ public class X11SunUnicode_0 extends Charset
/*     */ {
/*     */   public X11SunUnicode_0()
/*     */   {
/*  36 */     super("X11SunUnicode_0", null);
/*     */   }
/*     */ 
/*     */   public CharsetEncoder newEncoder() {
/*  40 */     return new Encoder(this);
/*     */   }
/*     */ 
/*     */   public CharsetDecoder newDecoder()
/*     */   {
/*  47 */     throw new Error("Decoder is not implemented for X11SunUnicode_0 Charset");
/*     */   }
/*     */ 
/*     */   public boolean contains(Charset paramCharset) {
/*  51 */     return paramCharset instanceof X11SunUnicode_0;
/*     */   }
/*     */ 
/*     */   private static class Encoder extends DoubleByteEncoder
/*     */   {
/*     */     private static final String innerIndex0 = "";
/* 125 */     private static final short[] index1 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/* 144 */     private static final String[] index2 = { "" };
/*     */ 
/*     */     public Encoder(Charset paramCharset)
/*     */     {
/*  56 */       super(index1, index2);
/*     */     }
/*     */ 
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte)
/*     */     {
/* 150 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11SunUnicode_0
 * JD-Core Version:    0.6.2
 */