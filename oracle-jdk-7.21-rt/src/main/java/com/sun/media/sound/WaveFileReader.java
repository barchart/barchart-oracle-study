/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFileFormat.Type;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class WaveFileReader extends SunFileReader
/*     */ {
/*     */   private static final int MAX_READ_LENGTH = 12;
/*  70 */   public static final AudioFileFormat.Type[] types = { AudioFileFormat.Type.WAVE };
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 102 */     AudioFileFormat localAudioFileFormat = getFMT(paramInputStream, true);
/*     */ 
/* 105 */     paramInputStream.reset();
/* 106 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 121 */     InputStream localInputStream = paramURL.openStream();
/* 122 */     AudioFileFormat localAudioFileFormat = null;
/*     */     try {
/* 124 */       localAudioFileFormat = getFMT(localInputStream, false);
/*     */     } finally {
/* 126 */       localInputStream.close();
/*     */     }
/* 128 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 143 */     AudioFileFormat localAudioFileFormat = null;
/* 144 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/*     */     try
/*     */     {
/* 147 */       localAudioFileFormat = getFMT(localFileInputStream, false);
/*     */     } finally {
/* 149 */       localFileInputStream.close();
/*     */     }
/*     */ 
/* 152 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 177 */     AudioFileFormat localAudioFileFormat = getFMT(paramInputStream, true);
/*     */ 
/* 181 */     return new AudioInputStream(paramInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 197 */     InputStream localInputStream = paramURL.openStream();
/* 198 */     AudioFileFormat localAudioFileFormat = null;
/*     */     try {
/* 200 */       localAudioFileFormat = getFMT(localInputStream, false);
/*     */     } finally {
/* 202 */       if (localAudioFileFormat == null) {
/* 203 */         localInputStream.close();
/*     */       }
/*     */     }
/* 206 */     return new AudioInputStream(localInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 222 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/* 223 */     AudioFileFormat localAudioFileFormat = null;
/*     */     try
/*     */     {
/* 226 */       localAudioFileFormat = getFMT(localFileInputStream, false);
/*     */     } finally {
/* 228 */       if (localAudioFileFormat == null) {
/* 229 */         localFileInputStream.close();
/*     */       }
/*     */     }
/* 232 */     return new AudioInputStream(localFileInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   private AudioFileFormat getFMT(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 244 */     int i = 0;
/*     */ 
/* 246 */     int k = 0;
/* 247 */     int m = 0;
/*     */ 
/* 253 */     AudioFormat.Encoding localEncoding = null;
/*     */ 
/* 255 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*     */ 
/* 257 */     if (paramBoolean) {
/* 258 */       localDataInputStream.mark(12);
/*     */     }
/*     */ 
/* 261 */     int i3 = localDataInputStream.readInt();
/* 262 */     int i4 = rllong(localDataInputStream);
/* 263 */     int i5 = localDataInputStream.readInt();
/*     */     int i6;
/* 265 */     if (i4 <= 0) {
/* 266 */       i4 = -1;
/* 267 */       i6 = -1;
/*     */     } else {
/* 269 */       i6 = i4 + 8;
/*     */     }
/*     */ 
/* 272 */     if ((i3 != 1380533830) || (i5 != 1463899717))
/*     */     {
/* 274 */       if (paramBoolean) {
/* 275 */         localDataInputStream.reset();
/*     */       }
/* 277 */       throw new UnsupportedAudioFileException("not a WAVE file");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/* 285 */         int j = localDataInputStream.readInt();
/* 286 */         i += 4;
/* 287 */         if (j == 1718449184)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 292 */         k = rllong(localDataInputStream);
/* 293 */         i += 4;
/* 294 */         if (k % 2 > 0) k++;
/* 295 */         i += localDataInputStream.skipBytes(k);
/*     */       }
/*     */     }
/*     */     catch (EOFException localEOFException1) {
/* 299 */       throw new UnsupportedAudioFileException("Not a valid WAV file");
/*     */     }
/*     */ 
/* 304 */     k = rllong(localDataInputStream);
/* 305 */     i += 4;
/*     */ 
/* 308 */     int i7 = i + k;
/*     */ 
/* 313 */     m = rlshort(localDataInputStream); i += 2;
/*     */ 
/* 315 */     if (m == 1)
/* 316 */       localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/* 317 */     else if (m == 6)
/* 318 */       localEncoding = AudioFormat.Encoding.ALAW;
/* 319 */     else if (m == 7) {
/* 320 */       localEncoding = AudioFormat.Encoding.ULAW;
/*     */     }
/*     */     else {
/* 323 */       throw new UnsupportedAudioFileException("Not a supported WAV file");
/*     */     }
/*     */ 
/* 326 */     int n = rlshort(localDataInputStream); i += 2;
/*     */ 
/* 329 */     long l1 = rllong(localDataInputStream); i += 4;
/*     */ 
/* 332 */     long l2 = rllong(localDataInputStream); i += 4;
/*     */ 
/* 335 */     int i1 = rlshort(localDataInputStream); i += 2;
/*     */ 
/* 338 */     int i2 = rlshort(localDataInputStream); i += 2;
/*     */ 
/* 341 */     if ((i2 == 8) && (localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))) {
/* 342 */       localEncoding = AudioFormat.Encoding.PCM_UNSIGNED;
/*     */     }
/*     */ 
/* 351 */     if (k % 2 != 0) k++;
/*     */ 
/* 355 */     if (i7 > i) {
/* 356 */       i += localDataInputStream.skipBytes(i7 - i);
/*     */     }
/*     */ 
/* 361 */     i = 0;
/*     */     try {
/*     */       while (true) {
/* 364 */         int i8 = localDataInputStream.readInt();
/* 365 */         i += 4;
/* 366 */         if (i8 == 1684108385)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 371 */         int i10 = rllong(localDataInputStream); i += 4;
/* 372 */         if (i10 % 2 > 0) i10++;
/* 373 */         i += localDataInputStream.skipBytes(i10);
/*     */       }
/*     */     }
/*     */     catch (EOFException localEOFException2) {
/* 377 */       throw new UnsupportedAudioFileException("Not a valid WAV file");
/*     */     }
/*     */ 
/* 381 */     int i9 = rllong(localDataInputStream); i += 4;
/*     */ 
/* 385 */     AudioFormat localAudioFormat = new AudioFormat(localEncoding, (float)l1, i2, n, calculatePCMFrameSize(i2, n), (float)l1, false);
/*     */ 
/* 391 */     return new WaveFileFormat(AudioFileFormat.Type.WAVE, i6, localAudioFormat, i9 / localAudioFormat.getFrameSize());
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.WaveFileReader
 * JD-Core Version:    0.6.2
 */