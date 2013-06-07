/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ 
/*     */ public class AlawCodec extends SunCodec
/*     */ {
/*  47 */   static final byte[] ALAW_TABH = new byte[256];
/*  48 */   static final byte[] ALAW_TABL = new byte[256];
/*     */ 
/*  50 */   private static final AudioFormat.Encoding[] alawEncodings = { AudioFormat.Encoding.ALAW, AudioFormat.Encoding.PCM_SIGNED };
/*     */ 
/*  52 */   private static final short[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, 32767 };
/*     */ 
/*     */   public AlawCodec()
/*     */   {
/*  84 */     super(alawEncodings, alawEncodings);
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
/*     */   {
/*     */     AudioFormat.Encoding[] arrayOfEncoding;
/*  93 */     if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
/*     */     {
/*  95 */       if (paramAudioFormat.getSampleSizeInBits() == 16)
/*     */       {
/*  97 */         arrayOfEncoding = new AudioFormat.Encoding[1];
/*  98 */         arrayOfEncoding[0] = AudioFormat.Encoding.ALAW;
/*  99 */         return arrayOfEncoding;
/*     */       }
/*     */ 
/* 102 */       return new AudioFormat.Encoding[0];
/*     */     }
/* 104 */     if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))
/*     */     {
/* 106 */       if (paramAudioFormat.getSampleSizeInBits() == 8)
/*     */       {
/* 108 */         arrayOfEncoding = new AudioFormat.Encoding[1];
/* 109 */         arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
/* 110 */         return arrayOfEncoding;
/*     */       }
/*     */ 
/* 113 */       return new AudioFormat.Encoding[0];
/*     */     }
/*     */ 
/* 117 */     return new AudioFormat.Encoding[0];
/*     */   }
/*     */ 
/*     */   public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
/*     */   {
/* 124 */     if (((paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) && (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))) || ((paramEncoding.equals(AudioFormat.Encoding.ALAW)) && (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))))
/*     */     {
/* 126 */       return getOutputFormats(paramAudioFormat);
/*     */     }
/* 128 */     return new AudioFormat[0];
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
/*     */   {
/* 135 */     AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
/* 136 */     AudioFormat.Encoding localEncoding = localAudioFormat1.getEncoding();
/*     */ 
/* 138 */     if (localEncoding.equals(paramEncoding)) {
/* 139 */       return paramAudioInputStream;
/*     */     }
/* 141 */     AudioFormat localAudioFormat2 = null;
/* 142 */     if (!isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
/* 143 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
/*     */     }
/* 145 */     if ((localEncoding.equals(AudioFormat.Encoding.ALAW)) && (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)))
/*     */     {
/* 148 */       localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), 2 * localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), localAudioFormat1.isBigEndian());
/*     */     }
/* 156 */     else if ((localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) && (paramEncoding.equals(AudioFormat.Encoding.ALAW)))
/*     */     {
/* 159 */       localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 8, localAudioFormat1.getChannels(), localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), false);
/*     */     }
/*     */     else
/*     */     {
/* 167 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
/*     */     }
/* 169 */     return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 177 */     return getConvertedStream(paramAudioFormat, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 195 */     Object localObject = null;
/* 196 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/*     */ 
/* 198 */     if (localAudioFormat.matches(paramAudioFormat))
/* 199 */       localObject = paramAudioInputStream;
/*     */     else {
/* 201 */       localObject = new AlawCodecStream(paramAudioInputStream, paramAudioFormat);
/*     */     }
/*     */ 
/* 204 */     return localObject;
/*     */   }
/*     */ 
/*     */   private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
/*     */   {
/* 218 */     Vector localVector = new Vector();
/*     */     AudioFormat localAudioFormat;
/* 221 */     if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) {
/* 222 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.ALAW, paramAudioFormat.getSampleRate(), 8, paramAudioFormat.getChannels(), paramAudioFormat.getChannels(), paramAudioFormat.getSampleRate(), false);
/*     */ 
/* 229 */       localVector.addElement(localAudioFormat);
/*     */     }
/*     */ 
/* 232 */     if (AudioFormat.Encoding.ALAW.equals(paramAudioFormat.getEncoding())) {
/* 233 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), false);
/*     */ 
/* 240 */       localVector.addElement(localAudioFormat);
/* 241 */       localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), true);
/*     */ 
/* 248 */       localVector.addElement(localAudioFormat);
/*     */     }
/*     */ 
/* 251 */     AudioFormat[] arrayOfAudioFormat = new AudioFormat[localVector.size()];
/* 252 */     for (int i = 0; i < arrayOfAudioFormat.length; i++) {
/* 253 */       arrayOfAudioFormat[i] = ((AudioFormat)(AudioFormat)localVector.elementAt(i));
/*     */     }
/* 255 */     return arrayOfAudioFormat;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  59 */     for (int i = 0; i < 256; i++) {
/*  60 */       int j = i ^ 0x55;
/*  61 */       int k = (j & 0xF) << 4;
/*  62 */       int m = (j & 0x70) >> 4;
/*  63 */       int n = k + 8;
/*     */ 
/*  65 */       if (m >= 1)
/*  66 */         n += 256;
/*  67 */       if (m > 1) {
/*  68 */         n <<= m - 1;
/*     */       }
/*  70 */       if ((j & 0x80) == 0) {
/*  71 */         n = -n;
/*     */       }
/*  73 */       ALAW_TABL[i] = ((byte)n);
/*  74 */       ALAW_TABH[i] = ((byte)(n >> 8));
/*     */     }
/*     */   }
/*     */ 
/*     */   class AlawCodecStream extends AudioInputStream
/*     */   {
/*     */     private static final int tempBufferSize = 64;
/* 263 */     private byte[] tempBuffer = null;
/*     */ 
/* 268 */     boolean encode = false;
/*     */     AudioFormat encodeFormat;
/*     */     AudioFormat decodeFormat;
/* 273 */     byte[] tabByte1 = null;
/* 274 */     byte[] tabByte2 = null;
/* 275 */     int highByte = 0;
/* 276 */     int lowByte = 1;
/*     */ 
/*     */     AlawCodecStream(AudioInputStream paramAudioFormat, AudioFormat arg3)
/*     */     {
/* 280 */       super(localAudioFormat1, -1L);
/*     */ 
/* 282 */       AudioFormat localAudioFormat2 = paramAudioFormat.getFormat();
/*     */ 
/* 285 */       if (!AlawCodec.this.isConversionSupported(localAudioFormat1, localAudioFormat2))
/*     */       {
/* 287 */         throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat2.toString() + " to " + localAudioFormat1.toString());
/*     */       }
/*     */       boolean bool;
/* 294 */       if (AudioFormat.Encoding.ALAW.equals(localAudioFormat2.getEncoding())) {
/* 295 */         this.encode = false;
/* 296 */         this.encodeFormat = localAudioFormat2;
/* 297 */         this.decodeFormat = localAudioFormat1;
/* 298 */         bool = localAudioFormat1.isBigEndian();
/*     */       } else {
/* 300 */         this.encode = true;
/* 301 */         this.encodeFormat = localAudioFormat1;
/* 302 */         this.decodeFormat = localAudioFormat2;
/* 303 */         bool = localAudioFormat2.isBigEndian();
/* 304 */         this.tempBuffer = new byte[64];
/*     */       }
/*     */ 
/* 307 */       if (bool) {
/* 308 */         this.tabByte1 = AlawCodec.ALAW_TABH;
/* 309 */         this.tabByte2 = AlawCodec.ALAW_TABL;
/* 310 */         this.highByte = 0;
/* 311 */         this.lowByte = 1;
/*     */       } else {
/* 313 */         this.tabByte1 = AlawCodec.ALAW_TABL;
/* 314 */         this.tabByte2 = AlawCodec.ALAW_TABH;
/* 315 */         this.highByte = 1;
/* 316 */         this.lowByte = 0;
/*     */       }
/*     */ 
/* 320 */       if ((paramAudioFormat instanceof AudioInputStream)) {
/* 321 */         this.frameLength = paramAudioFormat.getFrameLength();
/*     */       }
/*     */ 
/* 325 */       this.framePos = 0L;
/* 326 */       this.frameSize = localAudioFormat2.getFrameSize();
/* 327 */       if (this.frameSize == -1)
/* 328 */         this.frameSize = 1;
/*     */     }
/*     */ 
/*     */     private short search(short paramShort1, short[] paramArrayOfShort, short paramShort2)
/*     */     {
/* 338 */       for (short s = 0; s < paramShort2; s = (short)(s + 1)) {
/* 339 */         if (paramShort1 <= paramArrayOfShort[s]) return s;
/*     */       }
/* 341 */       return paramShort2;
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 350 */       byte[] arrayOfByte = new byte[1];
/* 351 */       return read(arrayOfByte, 0, arrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte)
/*     */       throws IOException
/*     */     {
/* 357 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */       throws IOException
/*     */     {
/* 363 */       if (paramInt2 % this.frameSize != 0) {
/* 364 */         paramInt2 -= paramInt2 % this.frameSize;
/*     */       }
/*     */ 
/* 367 */       if (this.encode)
/*     */       {
/* 369 */         i = 15;
/* 370 */         j = 4;
/*     */ 
/* 379 */         int i3 = 0;
/* 380 */         int i4 = paramInt1;
/* 381 */         int i5 = paramInt2 * 2;
/* 382 */         int i6 = i5 > 64 ? 64 : i5;
/*     */ 
/* 384 */         while ((i3 = super.read(this.tempBuffer, 0, i6)) > 0)
/*     */         {
/* 386 */           for (int n = 0; n < i3; n += 2)
/*     */           {
/* 389 */             int i1 = (short)(this.tempBuffer[(n + this.highByte)] << 8 & 0xFF00);
/* 390 */             i1 = (short)(i1 | (short)(this.tempBuffer[(n + this.lowByte)] & 0xFF));
/*     */ 
/* 392 */             if (i1 >= 0) {
/* 393 */               k = 213;
/*     */             } else {
/* 395 */               k = 85;
/* 396 */               i1 = (short)(-i1 - 8);
/*     */             }
/*     */ 
/* 399 */             m = search(i1, AlawCodec.seg_end, (short)8);
/*     */             int i2;
/* 403 */             if (m >= 8) {
/* 404 */               i2 = (byte)(0x7F ^ k);
/*     */             } else {
/* 406 */               i2 = (byte)(m << j);
/* 407 */               if (m < 2)
/* 408 */                 i2 = (byte)(i2 | (byte)(i1 >> 4 & i));
/*     */               else {
/* 410 */                 i2 = (byte)(i2 | (byte)(i1 >> m + 3 & i));
/*     */               }
/* 412 */               i2 = (byte)(i2 ^ k);
/*     */             }
/*     */ 
/* 415 */             paramArrayOfByte[i4] = i2;
/* 416 */             i4++;
/*     */           }
/*     */ 
/* 419 */           i5 -= i3;
/* 420 */           i6 = i5 > 64 ? 64 : i5;
/*     */         }
/*     */ 
/* 423 */         if ((i4 == paramInt1) && (i3 < 0)) {
/* 424 */           return i3;
/*     */         }
/*     */ 
/* 427 */         return i4 - paramInt1;
/*     */       }
/*     */ 
/* 432 */       int j = paramInt2 / 2;
/* 433 */       int k = paramInt1 + paramInt2 / 2;
/* 434 */       int m = super.read(paramArrayOfByte, k, j);
/*     */ 
/* 436 */       for (int i = paramInt1; i < paramInt1 + m * 2; i += 2) {
/* 437 */         paramArrayOfByte[i] = this.tabByte1[(paramArrayOfByte[k] & 0xFF)];
/* 438 */         paramArrayOfByte[(i + 1)] = this.tabByte2[(paramArrayOfByte[k] & 0xFF)];
/* 439 */         k++;
/*     */       }
/*     */ 
/* 442 */       if (m < 0) {
/* 443 */         return m;
/*     */       }
/*     */ 
/* 446 */       return i - paramInt1;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AlawCodec
 * JD-Core Version:    0.6.2
 */