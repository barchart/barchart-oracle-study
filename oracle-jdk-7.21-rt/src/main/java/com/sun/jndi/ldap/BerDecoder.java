/*     */ package com.sun.jndi.ldap;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public final class BerDecoder extends Ber
/*     */ {
/*     */   private int origOffset;
/*     */ 
/*     */   public BerDecoder(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  45 */     this.buf = paramArrayOfByte;
/*  46 */     this.bufsize = paramInt2;
/*  47 */     this.origOffset = paramInt1;
/*     */ 
/*  49 */     reset();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  57 */     this.offset = this.origOffset;
/*     */   }
/*     */ 
/*     */   public int getParsePosition()
/*     */   {
/*  66 */     return this.offset;
/*     */   }
/*     */ 
/*     */   public int parseLength()
/*     */     throws Ber.DecodeException
/*     */   {
/*  74 */     int i = parseByte();
/*     */ 
/*  76 */     if ((i & 0x80) == 128)
/*     */     {
/*  78 */       i &= 127;
/*     */ 
/*  80 */       if (i == 0) {
/*  81 */         throw new Ber.DecodeException("Indefinite length not supported");
/*     */       }
/*     */ 
/*  85 */       if (i > 4) {
/*  86 */         throw new Ber.DecodeException("encoding too long");
/*     */       }
/*     */ 
/*  89 */       if (this.bufsize - this.offset < i) {
/*  90 */         throw new Ber.DecodeException("Insufficient data");
/*     */       }
/*     */ 
/*  93 */       int j = 0;
/*     */ 
/*  95 */       for (int k = 0; k < i; k++) {
/*  96 */         j = (j << 8) + (this.buf[(this.offset++)] & 0xFF);
/*     */       }
/*  98 */       return j;
/*     */     }
/* 100 */     return i;
/*     */   }
/*     */ 
/*     */   public int parseSeq(int[] paramArrayOfInt)
/*     */     throws Ber.DecodeException
/*     */   {
/* 112 */     int i = parseByte();
/* 113 */     int j = parseLength();
/* 114 */     if (paramArrayOfInt != null) {
/* 115 */       paramArrayOfInt[0] = j;
/*     */     }
/* 117 */     return i;
/*     */   }
/*     */ 
/*     */   void seek(int paramInt)
/*     */     throws Ber.DecodeException
/*     */   {
/* 126 */     if ((this.offset + paramInt > this.bufsize) || (this.offset + paramInt < 0)) {
/* 127 */       throw new Ber.DecodeException("array index out of bounds");
/*     */     }
/* 129 */     this.offset += paramInt;
/*     */   }
/*     */ 
/*     */   public int parseByte()
/*     */     throws Ber.DecodeException
/*     */   {
/* 137 */     if (this.bufsize - this.offset < 1) {
/* 138 */       throw new Ber.DecodeException("Insufficient data");
/*     */     }
/* 140 */     return this.buf[(this.offset++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public int peekByte()
/*     */     throws Ber.DecodeException
/*     */   {
/* 149 */     if (this.bufsize - this.offset < 1) {
/* 150 */       throw new Ber.DecodeException("Insufficient data");
/*     */     }
/* 152 */     return this.buf[this.offset] & 0xFF;
/*     */   }
/*     */ 
/*     */   public boolean parseBoolean()
/*     */     throws Ber.DecodeException
/*     */   {
/* 160 */     return parseIntWithTag(1) != 0;
/*     */   }
/*     */ 
/*     */   public int parseEnumeration()
/*     */     throws Ber.DecodeException
/*     */   {
/* 168 */     return parseIntWithTag(10);
/*     */   }
/*     */ 
/*     */   public int parseInt()
/*     */     throws Ber.DecodeException
/*     */   {
/* 176 */     return parseIntWithTag(2);
/*     */   }
/*     */ 
/*     */   private int parseIntWithTag(int paramInt)
/*     */     throws Ber.DecodeException
/*     */   {
/* 188 */     if (parseByte() != paramInt) {
/* 189 */       throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(this.buf[(this.offset - 1)] & 0xFF) + " (expected tag " + Integer.toString(paramInt) + ")");
/*     */     }
/*     */ 
/* 194 */     int i = parseLength();
/*     */ 
/* 196 */     if (i > 4)
/* 197 */       throw new Ber.DecodeException("INTEGER too long");
/* 198 */     if (i > this.bufsize - this.offset) {
/* 199 */       throw new Ber.DecodeException("Insufficient data");
/*     */     }
/*     */ 
/* 202 */     int j = this.buf[(this.offset++)];
/* 203 */     int k = 0;
/*     */ 
/* 205 */     k = j & 0x7F;
/* 206 */     for (int m = 1; m < i; m++) {
/* 207 */       k <<= 8;
/* 208 */       k |= this.buf[(this.offset++)] & 0xFF;
/*     */     }
/*     */ 
/* 211 */     if ((j & 0x80) == 128) {
/* 212 */       k = -k;
/*     */     }
/*     */ 
/* 215 */     return k;
/*     */   }
/*     */ 
/*     */   public String parseString(boolean paramBoolean)
/*     */     throws Ber.DecodeException
/*     */   {
/* 222 */     return parseStringWithTag(4, paramBoolean, null);
/*     */   }
/*     */ 
/*     */   public String parseStringWithTag(int paramInt, boolean paramBoolean, int[] paramArrayOfInt)
/*     */     throws Ber.DecodeException
/*     */   {
/* 241 */     int j = this.offset;
/*     */     int i;
/* 243 */     if ((i = parseByte()) != paramInt) {
/* 244 */       throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString((byte)i) + " (expected tag " + paramInt + ")");
/*     */     }
/*     */ 
/* 248 */     int k = parseLength();
/*     */ 
/* 250 */     if (k > this.bufsize - this.offset)
/* 251 */       throw new Ber.DecodeException("Insufficient data");
/*     */     String str;
/* 255 */     if (k == 0) {
/* 256 */       str = "";
/*     */     } else {
/* 258 */       byte[] arrayOfByte = new byte[k];
/*     */ 
/* 260 */       System.arraycopy(this.buf, this.offset, arrayOfByte, 0, k);
/* 261 */       if (paramBoolean)
/*     */         try {
/* 263 */           str = new String(arrayOfByte, "UTF8");
/*     */         } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/* 265 */           throw new Ber.DecodeException("UTF8 not available on platform");
/*     */         }
/*     */       else {
/*     */         try {
/* 269 */           str = new String(arrayOfByte, "8859_1");
/*     */         } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/* 271 */           throw new Ber.DecodeException("8859_1 not available on platform");
/*     */         }
/*     */       }
/* 274 */       this.offset += k;
/*     */     }
/*     */ 
/* 277 */     if (paramArrayOfInt != null) {
/* 278 */       paramArrayOfInt[0] = (this.offset - j);
/*     */     }
/*     */ 
/* 281 */     return str;
/*     */   }
/*     */ 
/*     */   public byte[] parseOctetString(int paramInt, int[] paramArrayOfInt)
/*     */     throws Ber.DecodeException
/*     */   {
/* 300 */     int i = this.offset;
/*     */     int j;
/* 302 */     if ((j = parseByte()) != paramInt)
/*     */     {
/* 304 */       throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(j) + " (expected tag " + Integer.toString(paramInt) + ")");
/*     */     }
/*     */ 
/* 309 */     int k = parseLength();
/*     */ 
/* 311 */     if (k > this.bufsize - this.offset) {
/* 312 */       throw new Ber.DecodeException("Insufficient data");
/*     */     }
/*     */ 
/* 315 */     byte[] arrayOfByte = new byte[k];
/* 316 */     if (k > 0) {
/* 317 */       System.arraycopy(this.buf, this.offset, arrayOfByte, 0, k);
/* 318 */       this.offset += k;
/*     */     }
/*     */ 
/* 321 */     if (paramArrayOfInt != null) {
/* 322 */       paramArrayOfInt[0] = (this.offset - i);
/*     */     }
/*     */ 
/* 325 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */   public int bytesLeft()
/*     */   {
/* 332 */     return this.bufsize - this.offset;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.BerDecoder
 * JD-Core Version:    0.6.2
 */