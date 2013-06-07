/*     */ package sun.awt.motif;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import sun.nio.cs.ext.EUC_TW.Decoder;
/*     */ import sun.nio.cs.ext.EUC_TW.Encoder;
/*     */ 
/*     */ public abstract class X11CNS11643 extends Charset
/*     */ {
/*     */   private final int plane;
/*     */ 
/*     */   public X11CNS11643(int paramInt, String paramString)
/*     */   {
/*  36 */     super(paramString, null);
/*  37 */     switch (paramInt) {
/*     */     case 1:
/*  39 */       this.plane = 0;
/*  40 */       break;
/*     */     case 2:
/*     */     case 3:
/*  43 */       this.plane = paramInt;
/*  44 */       break;
/*     */     default:
/*  46 */       throw new IllegalArgumentException("Only planes 1, 2, and 3 supported");
/*     */     }
/*     */   }
/*     */ 
/*     */   public CharsetEncoder newEncoder()
/*     */   {
/*  52 */     return new Encoder(this, this.plane);
/*     */   }
/*     */ 
/*     */   public CharsetDecoder newDecoder() {
/*  56 */     return new Decoder(this, this.plane);
/*     */   }
/*     */ 
/*     */   public boolean contains(Charset paramCharset) {
/*  60 */     return paramCharset instanceof X11CNS11643;
/*     */   }
/*     */ 
/*     */   private class Decoder extends EUC_TW.Decoder
/*     */   {
/*     */     int plane;
/*     */     private String table;
/*     */ 
/*     */     protected Decoder(Charset paramInt, int arg3)
/*     */     {
/* 134 */       super();
/*     */       int i;
/* 135 */       if (i == 0)
/* 136 */         this.plane = i;
/* 137 */       else if ((i == 2) || (i == 3))
/* 138 */         this.plane = (i - 1);
/*     */       else
/* 140 */         throw new IllegalArgumentException("Only planes 1, 2, and 3 supported");
/*     */     }
/*     */ 
/*     */     protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer)
/*     */     {
/* 146 */       byte[] arrayOfByte = paramByteBuffer.array();
/* 147 */       int i = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/* 148 */       int j = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*     */ 
/* 150 */       char[] arrayOfChar1 = paramCharBuffer.array();
/* 151 */       int k = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/* 152 */       int m = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*     */       try
/*     */       {
/* 155 */         while (i < j) {
/* 156 */           if (j - i < 2) {
/* 157 */             return CoderResult.UNDERFLOW;
/*     */           }
/* 159 */           int n = arrayOfByte[i] & 0xFF | 0x80;
/* 160 */           int i1 = arrayOfByte[(i + 1)] & 0xFF | 0x80;
/* 161 */           char[] arrayOfChar2 = toUnicode(n, i1, this.plane);
/*     */           CoderResult localCoderResult3;
/* 164 */           if ((arrayOfChar2 == null) || (arrayOfChar2.length == 2))
/* 165 */             return CoderResult.unmappableForLength(2);
/* 166 */           if (m - k < 1)
/* 167 */             return CoderResult.OVERFLOW;
/* 168 */           arrayOfChar1[(k++)] = arrayOfChar2[0];
/* 169 */           i += 2;
/*     */         }
/* 171 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/* 173 */         paramByteBuffer.position(i - paramByteBuffer.arrayOffset());
/* 174 */         paramCharBuffer.position(k - paramCharBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Encoder extends EUC_TW.Encoder
/*     */   {
/*     */     private int plane;
/*  70 */     private byte[] bb = new byte[4];
/*     */ 
/*     */     public Encoder(Charset paramInt, int arg3)
/*     */     {
/*  66 */       super();
/*     */       int i;
/*  67 */       this.plane = i;
/*     */     }
/*     */ 
/*     */     public boolean canEncode(char paramChar)
/*     */     {
/*  72 */       if (paramChar <= '') {
/*  73 */         return false;
/*     */       }
/*  75 */       int i = toEUC(paramChar, this.bb);
/*  76 */       if (i == -1)
/*  77 */         return false;
/*  78 */       int j = 0;
/*  79 */       if (i == 4)
/*  80 */         j = (this.bb[1] & 0xFF) - 160;
/*  81 */       return j == this.plane;
/*     */     }
/*     */ 
/*     */     public boolean isLegalReplacement(byte[] paramArrayOfByte) {
/*  85 */       return true;
/*     */     }
/*     */ 
/*     */     protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer) {
/*  89 */       char[] arrayOfChar = paramCharBuffer.array();
/*  90 */       int i = paramCharBuffer.arrayOffset() + paramCharBuffer.position();
/*  91 */       int j = paramCharBuffer.arrayOffset() + paramCharBuffer.limit();
/*  92 */       byte[] arrayOfByte = paramByteBuffer.array();
/*  93 */       int k = paramByteBuffer.arrayOffset() + paramByteBuffer.position();
/*  94 */       int m = paramByteBuffer.arrayOffset() + paramByteBuffer.limit();
/*     */       try
/*     */       {
/*  97 */         while (i < j) {
/*  98 */           int n = arrayOfChar[i];
/*  99 */           if ((n > 127) && (n < 65534)) {
/* 100 */             int i1 = toEUC(n, this.bb);
/* 101 */             if (i1 != -1) {
/* 102 */               int i2 = 0;
/* 103 */               if (i1 == 4)
/* 104 */                 i2 = (this.bb[1] & 0xFF) - 160;
/* 105 */               if (i2 == this.plane) {
/* 106 */                 if (m - k < 2)
/* 107 */                   return CoderResult.OVERFLOW;
/* 108 */                 if (i1 == 2) {
/* 109 */                   arrayOfByte[(k++)] = ((byte)(this.bb[0] & 0x7F));
/* 110 */                   arrayOfByte[(k++)] = ((byte)(this.bb[1] & 0x7F));
/*     */                 } else {
/* 112 */                   arrayOfByte[(k++)] = ((byte)(this.bb[2] & 0x7F));
/* 113 */                   arrayOfByte[(k++)] = ((byte)(this.bb[3] & 0x7F));
/*     */                 }
/* 115 */                 i++;
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 120 */             return CoderResult.unmappableForLength(1);
/*     */           }
/*     */         }
/* 122 */         return CoderResult.UNDERFLOW;
/*     */       } finally {
/* 124 */         paramCharBuffer.position(i - paramCharBuffer.arrayOffset());
/* 125 */         paramByteBuffer.position(k - paramByteBuffer.arrayOffset());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.motif.X11CNS11643
 * JD-Core Version:    0.6.2
 */