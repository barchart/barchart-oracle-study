/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ 
/*     */ public class X11Dingbats extends Charset
/*     */ {
/*     */   public X11Dingbats()
/*     */   {
/*  34 */     super("X11Dingbats", null);
/*     */   }
/*     */ 
/*     */   public CharsetEncoder newEncoder() {
/*  38 */     return new Encoder(this);
/*     */   }
/*     */ 
/*     */   public CharsetDecoder newDecoder()
/*     */   {
/*  45 */     throw new Error("Decoder is not supported by X11Dingbats Charset");
/*     */   }
/*     */ 
/*     */   public boolean contains(Charset paramCharset) {
/*  49 */     return paramCharset instanceof X11Dingbats;
/*     */   }
/*     */ 
/*     */   private static class Encoder extends CharsetEncoder
/*     */   {
/* 101 */     private static byte[] table = { -95, -94, -93, -92, -91, -90, -89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, 0, 0, 0, -40, -39, -38, -37, -36, -35, -34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */     public Encoder(Charset paramCharset)
/*     */     {
/*  54 */       super(1.0F, 1.0F);
/*     */     }
/*     */ 
/*     */     public boolean canEncode(char paramChar) {
/*  58 */       if ((paramChar >= '✁') && (paramChar <= '❞')) {
/*  59 */         return true;
/*     */       }
/*  61 */       if ((paramChar >= '❡') && (paramChar <= '➾')) {
/*  62 */         return table[(paramChar - '❡')] != 0;
/*     */       }
/*  64 */       return false;
/*     */     }
/*     */ 
/*     */     protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer) {
/*  68 */       char[] arrayOfChar = paramCharBuffer.array();
/*  69 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/*  70 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*  71 */       assert (i <= j);
/*  72 */       i = i <= j ? i : j;
/*  73 */       byte[] arrayOfByte = paramByteBuffer.array();
/*  74 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  75 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*  76 */       assert (k <= m);
/*  77 */       k = k <= m ? k : m;
/*     */       try
/*     */       {
/*  80 */         while (i < j) {
/*  81 */           int n = arrayOfChar[i];
/*     */           CoderResult localCoderResult2;
/*  82 */           if (m - k < 1) {
/*  83 */             return CoderResult.OVERFLOW;
/*     */           }
/*  85 */           if (!canEncode(n))
/*  86 */             return CoderResult.unmappableForLength(1);
/*  87 */           i++;
/*  88 */           if (n >= 10081)
/*  89 */             arrayOfByte[(k++)] = table[(n - 10081)];
/*     */           else {
/*  91 */             arrayOfByte[(k++)] = ((byte)(n + 32 - 9984));
/*     */           }
/*     */         }
/*  94 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/*  96 */         paramCharBuffer.position(i - paramCharBuffer.arrayOffset());
/*  97 */         paramByteBuffer.position(k - paramByteBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte)
/*     */     {
/* 129 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11Dingbats
 * JD-Core Version:    0.6.2
 */