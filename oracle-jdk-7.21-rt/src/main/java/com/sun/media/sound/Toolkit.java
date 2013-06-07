/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ 
/*     */ public class Toolkit
/*     */ {
/*     */   static void getUnsigned8(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  45 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
/*     */     {
/*     */       int tmp11_10 = i; paramArrayOfByte[tmp11_10] = ((byte)(paramArrayOfByte[tmp11_10] + 128));
/*     */     }
/*     */   }
/*     */ 
/*     */   static void getByteSwapped(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/*  58 */     for (int j = paramInt1; j < paramInt1 + paramInt2; j += 2)
/*     */     {
/*  60 */       int i = paramArrayOfByte[j];
/*  61 */       paramArrayOfByte[j] = paramArrayOfByte[(j + 1)];
/*  62 */       paramArrayOfByte[(j + 1)] = i;
/*     */     }
/*     */   }
/*     */ 
/*     */   static float linearToDB(float paramFloat)
/*     */   {
/*  72 */     float f = (float)(Math.log(paramFloat == 0.0D ? 0.0001D : paramFloat) / Math.log(10.0D) * 20.0D);
/*  73 */     return f;
/*     */   }
/*     */ 
/*     */   static float dBToLinear(float paramFloat)
/*     */   {
/*  82 */     float f = (float)Math.pow(10.0D, paramFloat / 20.0D);
/*  83 */     return f;
/*     */   }
/*     */ 
/*     */   static long align(long paramLong, int paramInt)
/*     */   {
/*  92 */     if (paramInt <= 1) {
/*  93 */       return paramLong;
/*     */     }
/*  95 */     return paramLong - paramLong % paramInt;
/*     */   }
/*     */ 
/*     */   static int align(int paramInt1, int paramInt2)
/*     */   {
/* 100 */     if (paramInt2 <= 1) {
/* 101 */       return paramInt1;
/*     */     }
/* 103 */     return paramInt1 - paramInt1 % paramInt2;
/*     */   }
/*     */ 
/*     */   static long millis2bytes(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 111 */     long l = ()((float)paramLong * paramAudioFormat.getFrameRate() / 1000.0F * paramAudioFormat.getFrameSize());
/* 112 */     return align(l, paramAudioFormat.getFrameSize());
/*     */   }
/*     */ 
/*     */   static long bytes2millis(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 119 */     return ()((float)paramLong / paramAudioFormat.getFrameRate() * 1000.0F / paramAudioFormat.getFrameSize());
/*     */   }
/*     */ 
/*     */   static long micros2bytes(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 126 */     long l = ()((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F * paramAudioFormat.getFrameSize());
/* 127 */     return align(l, paramAudioFormat.getFrameSize());
/*     */   }
/*     */ 
/*     */   static long bytes2micros(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 134 */     return ()((float)paramLong / paramAudioFormat.getFrameRate() * 1000000.0F / paramAudioFormat.getFrameSize());
/*     */   }
/*     */ 
/*     */   static long micros2frames(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 141 */     return ()((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F);
/*     */   }
/*     */ 
/*     */   static long frames2micros(AudioFormat paramAudioFormat, long paramLong)
/*     */   {
/* 148 */     return ()(paramLong / paramAudioFormat.getFrameRate() * 1000000.0D);
/*     */   }
/*     */ 
/*     */   static void isFullySpecifiedAudioFormat(AudioFormat paramAudioFormat) {
/* 152 */     if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)))
/*     */     {
/* 157 */       return;
/*     */     }
/* 159 */     if (paramAudioFormat.getFrameRate() <= 0.0F) {
/* 160 */       throw new IllegalArgumentException("invalid frame rate: " + (paramAudioFormat.getFrameRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameRate())));
/*     */     }
/*     */ 
/* 164 */     if (paramAudioFormat.getSampleRate() <= 0.0F) {
/* 165 */       throw new IllegalArgumentException("invalid sample rate: " + (paramAudioFormat.getSampleRate() == -1.0F ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleRate())));
/*     */     }
/*     */ 
/* 169 */     if (paramAudioFormat.getSampleSizeInBits() <= 0) {
/* 170 */       throw new IllegalArgumentException("invalid sample size in bits: " + (paramAudioFormat.getSampleSizeInBits() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleSizeInBits())));
/*     */     }
/*     */ 
/* 174 */     if (paramAudioFormat.getFrameSize() <= 0) {
/* 175 */       throw new IllegalArgumentException("invalid frame size: " + (paramAudioFormat.getFrameSize() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameSize())));
/*     */     }
/*     */ 
/* 179 */     if (paramAudioFormat.getChannels() <= 0)
/* 180 */       throw new IllegalArgumentException("invalid number of channels: " + (paramAudioFormat.getChannels() == -1 ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getChannels())));
/*     */   }
/*     */ 
/*     */   static boolean isFullySpecifiedPCMFormat(AudioFormat paramAudioFormat)
/*     */   {
/* 188 */     if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
/*     */     {
/* 190 */       return false;
/*     */     }
/* 192 */     if ((paramAudioFormat.getFrameRate() <= 0.0F) || (paramAudioFormat.getSampleRate() <= 0.0F) || (paramAudioFormat.getSampleSizeInBits() <= 0) || (paramAudioFormat.getFrameSize() <= 0) || (paramAudioFormat.getChannels() <= 0))
/*     */     {
/* 197 */       return false;
/*     */     }
/* 199 */     return true;
/*     */   }
/*     */ 
/*     */   public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream paramAudioInputStream)
/*     */   {
/* 206 */     AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
/*     */ 
/* 208 */     if ((!localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
/*     */     {
/*     */       try
/*     */       {
/* 212 */         AudioFormat localAudioFormat2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), localAudioFormat1.getChannels() * 2, localAudioFormat1.getSampleRate(), Platform.isBigEndian());
/*     */ 
/* 220 */         paramAudioInputStream = AudioSystem.getAudioInputStream(localAudioFormat2, paramAudioInputStream);
/*     */       }
/*     */       catch (Exception localException) {
/* 223 */         paramAudioInputStream = null;
/*     */       }
/*     */     }
/*     */ 
/* 227 */     return paramAudioInputStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.Toolkit
 * JD-Core Version:    0.6.2
 */