/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ 
/*     */ public class PCMtoPCMCodec extends SunCodec
/*     */ {
/*  46 */   private static final AudioFormat.Encoding[] inputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
/*     */ 
/*  51 */   private static final AudioFormat.Encoding[] outputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
/*     */   private static final int tempBufferSize = 64;
/*  59 */   private byte[] tempBuffer = null;
/*     */ 
/*     */   public PCMtoPCMCodec()
/*     */   {
/*  66 */     super(inputEncodings, outputEncodings);
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
/*     */   {
/*  76 */     if ((paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
/*     */     {
/*  79 */       AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[2];
/*  80 */       arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
/*  81 */       arrayOfEncoding[1] = AudioFormat.Encoding.PCM_UNSIGNED;
/*  82 */       return arrayOfEncoding;
/*     */     }
/*  84 */     return new AudioFormat.Encoding[0];
/*     */   }
/*     */ 
/*     */   public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
/*     */   {
/*  95 */     AudioFormat[] arrayOfAudioFormat1 = getOutputFormats(paramAudioFormat);
/*  96 */     Vector localVector = new Vector();
/*  97 */     for (int i = 0; i < arrayOfAudioFormat1.length; i++) {
/*  98 */       if (arrayOfAudioFormat1[i].getEncoding().equals(paramEncoding)) {
/*  99 */         localVector.addElement(arrayOfAudioFormat1[i]);
/*     */       }
/*     */     }
/*     */ 
/* 103 */     AudioFormat[] arrayOfAudioFormat2 = new AudioFormat[localVector.size()];
/*     */ 
/* 105 */     for (int j = 0; j < arrayOfAudioFormat2.length; j++) {
/* 106 */       arrayOfAudioFormat2[j] = ((AudioFormat)(AudioFormat)localVector.elementAt(j));
/*     */     }
/*     */ 
/* 109 */     return arrayOfAudioFormat2;
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
/*     */   {
/* 117 */     if (isConversionSupported(paramEncoding, paramAudioInputStream.getFormat()))
/*     */     {
/* 119 */       AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
/* 120 */       AudioFormat localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), localAudioFormat1.getSampleSizeInBits(), localAudioFormat1.getChannels(), localAudioFormat1.getFrameSize(), localAudioFormat1.getFrameRate(), localAudioFormat1.isBigEndian());
/*     */ 
/* 128 */       return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
/*     */     }
/*     */ 
/* 131 */     throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 140 */     return getConvertedStream(paramAudioFormat, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 159 */     Object localObject = null;
/*     */ 
/* 161 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/*     */ 
/* 163 */     if (localAudioFormat.matches(paramAudioFormat))
/*     */     {
/* 165 */       localObject = paramAudioInputStream;
/*     */     }
/*     */     else {
/* 168 */       localObject = new PCMtoPCMCodecStream(paramAudioInputStream, paramAudioFormat);
/* 169 */       this.tempBuffer = new byte[64];
/*     */     }
/* 171 */     return localObject;
/*     */   }
/*     */ 
/*     */   private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
/*     */   {
/* 186 */     Vector localVector = new Vector();
/*     */ 
/* 189 */     int i = paramAudioFormat.getSampleSizeInBits();
/* 190 */     boolean bool = paramAudioFormat.isBigEndian();
/*     */     AudioFormat localAudioFormat;
/* 193 */     if (i == 8) {
/* 194 */       if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))
/*     */       {
/* 196 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 203 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */ 
/* 206 */       if (AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding()))
/*     */       {
/* 208 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 215 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */     }
/* 218 */     else if (i == 16)
/*     */     {
/* 220 */       if ((AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) && (bool))
/*     */       {
/* 222 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 229 */         localVector.addElement(localAudioFormat);
/* 230 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 237 */         localVector.addElement(localAudioFormat);
/* 238 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 245 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */ 
/* 248 */       if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding())) && (bool))
/*     */       {
/* 250 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 257 */         localVector.addElement(localAudioFormat);
/* 258 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 265 */         localVector.addElement(localAudioFormat);
/* 266 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 273 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */ 
/* 276 */       if ((AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) && (!bool))
/*     */       {
/* 278 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 285 */         localVector.addElement(localAudioFormat);
/* 286 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 293 */         localVector.addElement(localAudioFormat);
/* 294 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 301 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */ 
/* 304 */       if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding())) && (!bool))
/*     */       {
/* 306 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
/*     */ 
/* 313 */         localVector.addElement(localAudioFormat);
/* 314 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 321 */         localVector.addElement(localAudioFormat);
/* 322 */         localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
/*     */ 
/* 329 */         localVector.addElement(localAudioFormat);
/*     */       }
/*     */     }
/*     */     AudioFormat[] arrayOfAudioFormat;
/* 334 */     synchronized (localVector)
/*     */     {
/* 336 */       arrayOfAudioFormat = new AudioFormat[localVector.size()];
/*     */ 
/* 338 */       for (int j = 0; j < arrayOfAudioFormat.length; j++)
/*     */       {
/* 340 */         arrayOfAudioFormat[j] = ((AudioFormat)(AudioFormat)localVector.elementAt(j));
/*     */       }
/*     */     }
/*     */ 
/* 344 */     return arrayOfAudioFormat;
/*     */   }
/*     */ 
/*     */   class PCMtoPCMCodecStream extends AudioInputStream
/*     */   {
/* 350 */     private final int PCM_SWITCH_SIGNED_8BIT = 1;
/* 351 */     private final int PCM_SWITCH_ENDIAN = 2;
/* 352 */     private final int PCM_SWITCH_SIGNED_LE = 3;
/* 353 */     private final int PCM_SWITCH_SIGNED_BE = 4;
/* 354 */     private final int PCM_UNSIGNED_LE2SIGNED_BE = 5;
/* 355 */     private final int PCM_SIGNED_LE2UNSIGNED_BE = 6;
/* 356 */     private final int PCM_UNSIGNED_BE2SIGNED_LE = 7;
/* 357 */     private final int PCM_SIGNED_BE2UNSIGNED_LE = 8;
/*     */ 
/* 359 */     private int sampleSizeInBytes = 0;
/* 360 */     private int conversionType = 0;
/*     */ 
/*     */     PCMtoPCMCodecStream(AudioInputStream paramAudioFormat, AudioFormat arg3)
/*     */     {
/* 365 */       super(localAudioFormat1, -1L);
/*     */ 
/* 367 */       int i = 0;
/* 368 */       AudioFormat.Encoding localEncoding1 = null;
/* 369 */       AudioFormat.Encoding localEncoding2 = null;
/*     */ 
/* 373 */       AudioFormat localAudioFormat2 = paramAudioFormat.getFormat();
/*     */ 
/* 376 */       if (!PCMtoPCMCodec.this.isConversionSupported(localAudioFormat2, localAudioFormat1))
/*     */       {
/* 378 */         throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat2.toString() + " to " + localAudioFormat1.toString());
/*     */       }
/*     */ 
/* 381 */       localEncoding1 = localAudioFormat2.getEncoding();
/* 382 */       localEncoding2 = localAudioFormat1.getEncoding();
/* 383 */       boolean bool1 = localAudioFormat2.isBigEndian();
/* 384 */       boolean bool2 = localAudioFormat1.isBigEndian();
/* 385 */       i = localAudioFormat2.getSampleSizeInBits();
/* 386 */       this.sampleSizeInBytes = (i / 8);
/*     */ 
/* 390 */       if (i == 8) {
/* 391 */         if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2)))
/*     */         {
/* 393 */           this.conversionType = 1;
/*     */         }
/* 396 */         else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2)))
/*     */         {
/* 398 */           this.conversionType = 1;
/*     */         }
/*     */ 
/*     */       }
/* 403 */       else if ((localEncoding1.equals(localEncoding2)) && (bool1 != bool2))
/*     */       {
/* 405 */         this.conversionType = 2;
/*     */       }
/* 409 */       else if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (!bool1) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2)) && (bool2))
/*     */       {
/* 412 */         this.conversionType = 5;
/*     */       }
/* 415 */       else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (!bool1) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2)) && (bool2))
/*     */       {
/* 418 */         this.conversionType = 6;
/*     */       }
/* 421 */       else if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding1)) && (bool1) && (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding2)) && (!bool2))
/*     */       {
/* 424 */         this.conversionType = 7;
/*     */       }
/* 427 */       else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding1)) && (bool1) && (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding2)) && (!bool2))
/*     */       {
/* 430 */         this.conversionType = 8;
/*     */       }
/*     */ 
/* 438 */       this.frameSize = localAudioFormat2.getFrameSize();
/* 439 */       if (this.frameSize == -1) {
/* 440 */         this.frameSize = 1;
/*     */       }
/* 442 */       if ((paramAudioFormat instanceof AudioInputStream))
/* 443 */         this.frameLength = paramAudioFormat.getFrameLength();
/*     */       else {
/* 445 */         this.frameLength = -1L;
/*     */       }
/*     */ 
/* 449 */       this.framePos = 0L;
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 465 */       if (this.frameSize == 1) {
/* 466 */         if (this.conversionType == 1) {
/* 467 */           int i = super.read();
/*     */ 
/* 469 */           if (i < 0) return i;
/*     */ 
/* 471 */           int j = (byte)(i & 0xF);
/* 472 */           j = j >= 0 ? (byte)(0x80 | j) : (byte)(0x7F & j);
/* 473 */           i = j & 0xF;
/*     */ 
/* 475 */           return i;
/*     */         }
/*     */ 
/* 479 */         throw new IOException("cannot read a single byte if frame size > 1");
/*     */       }
/*     */ 
/* 482 */       throw new IOException("cannot read a single byte if frame size > 1");
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte)
/*     */       throws IOException
/*     */     {
/* 489 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */       throws IOException
/*     */     {
/* 498 */       if (paramInt2 % this.frameSize != 0) {
/* 499 */         paramInt2 -= paramInt2 % this.frameSize;
/*     */       }
/*     */ 
/* 502 */       if ((this.frameLength != -1L) && (paramInt2 / this.frameSize > this.frameLength - this.framePos)) {
/* 503 */         paramInt2 = (int)(this.frameLength - this.framePos) * this.frameSize;
/*     */       }
/*     */ 
/* 506 */       int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
/*     */ 
/* 509 */       if (i < 0) {
/* 510 */         return i;
/*     */       }
/*     */ 
/* 515 */       switch (this.conversionType)
/*     */       {
/*     */       case 1:
/* 518 */         switchSigned8bit(paramArrayOfByte, paramInt1, paramInt2, i);
/* 519 */         break;
/*     */       case 2:
/* 522 */         switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
/* 523 */         break;
/*     */       case 3:
/* 526 */         switchSignedLE(paramArrayOfByte, paramInt1, paramInt2, i);
/* 527 */         break;
/*     */       case 4:
/* 530 */         switchSignedBE(paramArrayOfByte, paramInt1, paramInt2, i);
/* 531 */         break;
/*     */       case 5:
/*     */       case 6:
/* 535 */         switchSignedLE(paramArrayOfByte, paramInt1, paramInt2, i);
/* 536 */         switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
/* 537 */         break;
/*     */       case 7:
/*     */       case 8:
/* 541 */         switchSignedBE(paramArrayOfByte, paramInt1, paramInt2, i);
/* 542 */         switchEndian(paramArrayOfByte, paramInt1, paramInt2, i);
/* 543 */         break;
/*     */       }
/*     */ 
/* 550 */       return i;
/*     */     }
/*     */ 
/*     */     private void switchSigned8bit(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 556 */       for (int i = paramInt1; i < paramInt1 + paramInt3; i++)
/* 557 */         paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
/*     */     }
/*     */ 
/*     */     private void switchSignedBE(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 563 */       for (int i = paramInt1; i < paramInt1 + paramInt3; i += this.sampleSizeInBytes)
/* 564 */         paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
/*     */     }
/*     */ 
/*     */     private void switchSignedLE(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 570 */       for (int i = paramInt1 + this.sampleSizeInBytes - 1; i < paramInt1 + paramInt3; i += this.sampleSizeInBytes)
/* 571 */         paramArrayOfByte[i] = (paramArrayOfByte[i] >= 0 ? (byte)(0x80 | paramArrayOfByte[i]) : (byte)(0x7F & paramArrayOfByte[i]));
/*     */     }
/*     */ 
/*     */     private void switchEndian(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 577 */       if (this.sampleSizeInBytes == 2)
/* 578 */         for (int i = paramInt1; i < paramInt1 + paramInt3; i += this.sampleSizeInBytes)
/*     */         {
/* 580 */           int j = paramArrayOfByte[i];
/* 581 */           paramArrayOfByte[i] = paramArrayOfByte[(i + 1)];
/* 582 */           paramArrayOfByte[(i + 1)] = j;
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.PCMtoPCMCodec
 * JD-Core Version:    0.6.2
 */