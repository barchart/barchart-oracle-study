/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ 
/*     */ public class UlawCodec extends SunCodec
/*     */ {
/*  47 */   static final byte[] ULAW_TABH = new byte[256];
/*  48 */   static final byte[] ULAW_TABL = new byte[256];
/*     */ 
/*  50 */   private static final AudioFormat.Encoding[] ulawEncodings = { AudioFormat.Encoding.ULAW, AudioFormat.Encoding.PCM_SIGNED };
/*     */ 
/*  53 */   private static final short[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, 32767 };
/*     */ 
/*     */   public UlawCodec()
/*     */   {
/*  79 */     super(ulawEncodings, ulawEncodings);
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
/*     */   {
/*     */     AudioFormat.Encoding[] arrayOfEncoding;
/*  85 */     if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) {
/*  86 */       if (paramAudioFormat.getSampleSizeInBits() == 16) {
/*  87 */         arrayOfEncoding = new AudioFormat.Encoding[1];
/*  88 */         arrayOfEncoding[0] = AudioFormat.Encoding.ULAW;
/*  89 */         return arrayOfEncoding;
/*     */       }
/*  91 */       return new AudioFormat.Encoding[0];
/*     */     }
/*  93 */     if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding())) {
/*  94 */       if (paramAudioFormat.getSampleSizeInBits() == 8) {
/*  95 */         arrayOfEncoding = new AudioFormat.Encoding[1];
/*  96 */         arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
/*  97 */         return arrayOfEncoding;
/*     */       }
/*  99 */       return new AudioFormat.Encoding[0];
/*     */     }
/*     */ 
/* 102 */     return new AudioFormat.Encoding[0];
/*     */   }
/*     */ 
/*     */   public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
/*     */   {
/* 110 */     if (((AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding)) && (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding()))) || ((AudioFormat.Encoding.ULAW.equals(paramEncoding)) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))))
/*     */     {
/* 115 */       return getOutputFormats(paramAudioFormat);
/*     */     }
/* 117 */     return new AudioFormat[0];
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
/*     */   {
/* 124 */     AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
/* 125 */     AudioFormat.Encoding localEncoding = localAudioFormat1.getEncoding();
/*     */ 
/* 127 */     if (localEncoding.equals(paramEncoding)) {
/* 128 */       return paramAudioInputStream;
/*     */     }
/* 130 */     AudioFormat localAudioFormat2 = null;
/* 131 */     if (!isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
/* 132 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
/*     */     }
/* 134 */     if ((AudioFormat.Encoding.ULAW.equals(localEncoding)) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding)))
/*     */     {
/* 136 */       localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), 2 * localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), localAudioFormat1.isBigEndian());
/*     */     }
/* 143 */     else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (AudioFormat.Encoding.ULAW.equals(paramEncoding)))
/*     */     {
/* 145 */       localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 8, localAudioFormat1.getChannels(), localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), false);
/*     */     }
/*     */     else
/*     */     {
/* 153 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
/*     */     }
/*     */ 
/* 156 */     return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 164 */     return getConvertedStream(paramAudioFormat, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 180 */     Object localObject = null;
/*     */ 
/* 182 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/*     */ 
/* 184 */     if (localAudioFormat.matches(paramAudioFormat))
/* 185 */       localObject = paramAudioInputStream;
/*     */     else {
/* 187 */       localObject = new UlawCodecStream(paramAudioInputStream, paramAudioFormat);
/*     */     }
/* 189 */     return localObject;
/*     */   }
/*     */ 
/*     */   private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
/*     */   {
/* 202 */     Vector localVector = new Vector();
/*     */     AudioFormat localAudioFormat;
/* 205 */     if ((paramAudioFormat.getSampleSizeInBits() == 16) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())))
/*     */     {
/* 207 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, paramAudioFormat.getSampleRate(), 8, paramAudioFormat.getChannels(), paramAudioFormat.getChannels(), paramAudioFormat.getSampleRate(), false);
/*     */ 
/* 214 */       localVector.addElement(localAudioFormat);
/*     */     }
/*     */ 
/* 217 */     if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding())) {
/* 218 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), false);
/*     */ 
/* 225 */       localVector.addElement(localAudioFormat);
/*     */ 
/* 227 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), true);
/*     */ 
/* 234 */       localVector.addElement(localAudioFormat);
/*     */     }
/*     */ 
/* 237 */     AudioFormat[] arrayOfAudioFormat = new AudioFormat[localVector.size()];
/* 238 */     for (int i = 0; i < arrayOfAudioFormat.length; i++) {
/* 239 */       arrayOfAudioFormat[i] = ((AudioFormat)(AudioFormat)localVector.elementAt(i));
/*     */     }
/* 241 */     return arrayOfAudioFormat;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  60 */     for (int i = 0; i < 256; i++) {
/*  61 */       int j = i ^ 0xFFFFFFFF;
/*     */ 
/*  64 */       j &= 255;
/*  65 */       int k = ((j & 0xF) << 3) + 132;
/*  66 */       k <<= (j & 0x70) >> 4;
/*  67 */       k = (j & 0x80) != 0 ? 132 - k : k - 132;
/*     */ 
/*  69 */       ULAW_TABL[i] = ((byte)(k & 0xFF));
/*  70 */       ULAW_TABH[i] = ((byte)(k >> 8 & 0xFF));
/*     */     }
/*     */   }
/*     */ 
/*     */   class UlawCodecStream extends AudioInputStream
/*     */   {
/*     */     private static final int tempBufferSize = 64;
/* 248 */     private byte[] tempBuffer = null;
/*     */ 
/* 253 */     boolean encode = false;
/*     */     AudioFormat encodeFormat;
/*     */     AudioFormat decodeFormat;
/* 258 */     byte[] tabByte1 = null;
/* 259 */     byte[] tabByte2 = null;
/* 260 */     int highByte = 0;
/* 261 */     int lowByte = 1;
/*     */ 
/*     */     UlawCodecStream(AudioInputStream paramAudioFormat, AudioFormat arg3) {
/* 264 */       super(localAudioFormat1, -1L);
/*     */ 
/* 266 */       AudioFormat localAudioFormat2 = paramAudioFormat.getFormat();
/*     */ 
/* 269 */       if (!UlawCodec.this.isConversionSupported(localAudioFormat1, localAudioFormat2))
/* 270 */         throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat2.toString() + " to " + localAudioFormat1.toString());
/*     */       boolean bool;
/* 277 */       if (AudioFormat.Encoding.ULAW.equals(localAudioFormat2.getEncoding())) {
/* 278 */         this.encode = false;
/* 279 */         this.encodeFormat = localAudioFormat2;
/* 280 */         this.decodeFormat = localAudioFormat1;
/* 281 */         bool = localAudioFormat1.isBigEndian();
/*     */       } else {
/* 283 */         this.encode = true;
/* 284 */         this.encodeFormat = localAudioFormat1;
/* 285 */         this.decodeFormat = localAudioFormat2;
/* 286 */         bool = localAudioFormat2.isBigEndian();
/* 287 */         this.tempBuffer = new byte[64];
/*     */       }
/*     */ 
/* 291 */       if (bool) {
/* 292 */         this.tabByte1 = UlawCodec.ULAW_TABH;
/* 293 */         this.tabByte2 = UlawCodec.ULAW_TABL;
/* 294 */         this.highByte = 0;
/* 295 */         this.lowByte = 1;
/*     */       } else {
/* 297 */         this.tabByte1 = UlawCodec.ULAW_TABL;
/* 298 */         this.tabByte2 = UlawCodec.ULAW_TABH;
/* 299 */         this.highByte = 1;
/* 300 */         this.lowByte = 0;
/*     */       }
/*     */ 
/* 304 */       if ((paramAudioFormat instanceof AudioInputStream)) {
/* 305 */         this.frameLength = paramAudioFormat.getFrameLength();
/*     */       }
/*     */ 
/* 308 */       this.framePos = 0L;
/* 309 */       this.frameSize = localAudioFormat2.getFrameSize();
/* 310 */       if (this.frameSize == -1)
/* 311 */         this.frameSize = 1;
/*     */     }
/*     */ 
/*     */     private short search(short paramShort1, short[] paramArrayOfShort, short paramShort2)
/*     */     {
/* 321 */       for (short s = 0; s < paramShort2; s = (short)(s + 1)) {
/* 322 */         if (paramShort1 <= paramArrayOfShort[s]) return s;
/*     */       }
/* 324 */       return paramShort2;
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 332 */       byte[] arrayOfByte = new byte[1];
/* 333 */       if (read(arrayOfByte, 0, arrayOfByte.length) == 1) {
/* 334 */         return arrayOfByte[1] & 0xFF;
/*     */       }
/* 336 */       return -1;
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 340 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 345 */       if (paramInt2 % this.frameSize != 0) {
/* 346 */         paramInt2 -= paramInt2 % this.frameSize;
/*     */       }
/* 348 */       if (this.encode) {
/* 349 */         i = 132;
/*     */ 
/* 357 */         int i2 = 0;
/* 358 */         int i3 = paramInt1;
/* 359 */         int i4 = paramInt2 * 2;
/* 360 */         int i5 = i4 > 64 ? 64 : i4;
/*     */ 
/* 362 */         while ((i2 = super.read(this.tempBuffer, 0, i5)) > 0) {
/* 363 */           for (m = 0; m < i2; m += 2)
/*     */           {
/* 365 */             int n = (short)(this.tempBuffer[(m + this.highByte)] << 8 & 0xFF00);
/* 366 */             n = (short)(n | (short)((short)this.tempBuffer[(m + this.lowByte)] & 0xFF));
/*     */ 
/* 369 */             if (n < 0) {
/* 370 */               n = (short)(i - n);
/* 371 */               j = 127;
/*     */             } else {
/* 373 */               n = (short)(n + i);
/* 374 */               j = 255;
/*     */             }
/*     */ 
/* 377 */             k = search(n, UlawCodec.seg_end, (short)8);
/*     */             int i1;
/* 382 */             if (k >= 8) {
/* 383 */               i1 = (byte)(0x7F ^ j);
/*     */             } else {
/* 385 */               i1 = (byte)(k << 4 | n >> k + 3 & 0xF);
/* 386 */               i1 = (byte)(i1 ^ j);
/*     */             }
/*     */ 
/* 389 */             paramArrayOfByte[i3] = i1;
/* 390 */             i3++;
/*     */           }
/*     */ 
/* 393 */           i4 -= i2;
/* 394 */           i5 = i4 > 64 ? 64 : i4;
/*     */         }
/* 396 */         if ((i3 == paramInt1) && (i2 < 0)) {
/* 397 */           return i2;
/*     */         }
/* 399 */         return i3 - paramInt1;
/*     */       }
/*     */ 
/* 402 */       int j = paramInt2 / 2;
/* 403 */       int k = paramInt1 + paramInt2 / 2;
/* 404 */       int m = super.read(paramArrayOfByte, k, j);
/*     */ 
/* 406 */       if (m < 0) {
/* 407 */         return m;
/*     */       }
/* 409 */       for (int i = paramInt1; i < paramInt1 + m * 2; i += 2) {
/* 410 */         paramArrayOfByte[i] = this.tabByte1[(paramArrayOfByte[k] & 0xFF)];
/* 411 */         paramArrayOfByte[(i + 1)] = this.tabByte2[(paramArrayOfByte[k] & 0xFF)];
/* 412 */         k++;
/*     */       }
/* 414 */       return i - paramInt1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.UlawCodec
 * JD-Core Version:    0.6.2
 */