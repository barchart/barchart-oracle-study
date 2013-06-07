/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
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
/*     */ public class AuFileReader extends SunFileReader
/*     */ {
/*  67 */   public static final AudioFileFormat.Type[] types = { AudioFileFormat.Type.AU };
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 101 */     AudioFormat localAudioFormat = null;
/* 102 */     AuFileFormat localAuFileFormat = null;
/* 103 */     int i = 28;
/* 104 */     boolean bool = false;
/* 105 */     int j = -1;
/* 106 */     int k = -1;
/* 107 */     int m = -1;
/* 108 */     int n = -1;
/* 109 */     int i1 = -1;
/* 110 */     int i2 = -1;
/* 111 */     int i3 = -1;
/* 112 */     int i4 = -1;
/* 113 */     int i5 = 0;
/* 114 */     int i6 = 0;
/* 115 */     int i7 = 0;
/* 116 */     AudioFormat.Encoding localEncoding = null;
/*     */ 
/* 118 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*     */ 
/* 120 */     localDataInputStream.mark(i);
/*     */ 
/* 122 */     j = localDataInputStream.readInt();
/*     */ 
/* 124 */     if ((j != 779316836) || (j == 779314176) || (j == 1684960046) || (j == 6583086))
/*     */     {
/* 128 */       localDataInputStream.reset();
/* 129 */       throw new UnsupportedAudioFileException("not an AU file");
/*     */     }
/*     */ 
/* 132 */     if ((j == 779316836) || (j == 779314176)) {
/* 133 */       bool = true;
/*     */     }
/*     */ 
/* 136 */     k = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream); i7 += 4;
/* 137 */     m = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream); i7 += 4;
/* 138 */     n = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream); i7 += 4;
/* 139 */     i1 = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream); i7 += 4;
/* 140 */     i4 = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream); i7 += 4;
/*     */ 
/* 142 */     i2 = i1;
/*     */ 
/* 144 */     switch (n) {
/*     */     case 1:
/* 146 */       localEncoding = AudioFormat.Encoding.ULAW;
/* 147 */       i5 = 8;
/* 148 */       break;
/*     */     case 27:
/* 150 */       localEncoding = AudioFormat.Encoding.ALAW;
/* 151 */       i5 = 8;
/* 152 */       break;
/*     */     case 2:
/* 155 */       localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/* 156 */       i5 = 8;
/* 157 */       break;
/*     */     case 3:
/* 159 */       localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/* 160 */       i5 = 16;
/* 161 */       break;
/*     */     case 4:
/* 163 */       localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 165 */       i5 = 24;
/* 166 */       break;
/*     */     case 5:
/* 168 */       localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 170 */       i5 = 32;
/* 171 */       break;
/*     */     default:
/* 198 */       localDataInputStream.reset();
/* 199 */       throw new UnsupportedAudioFileException("not a valid AU file");
/*     */     }
/*     */ 
/* 202 */     i3 = calculatePCMFrameSize(i5, i4);
/*     */ 
/* 204 */     if (m < 0) {
/* 205 */       i6 = -1;
/*     */     }
/*     */     else {
/* 208 */       i6 = m / i3;
/*     */     }
/*     */ 
/* 211 */     localAudioFormat = new AudioFormat(localEncoding, i1, i5, i4, i3, i2, bool);
/*     */ 
/* 214 */     localAuFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, m + k, localAudioFormat, i6);
/*     */ 
/* 217 */     localDataInputStream.reset();
/* 218 */     return localAuFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 235 */     InputStream localInputStream = null;
/* 236 */     BufferedInputStream localBufferedInputStream = null;
/* 237 */     AudioFileFormat localAudioFileFormat = null;
/* 238 */     Object localObject1 = null;
/*     */ 
/* 240 */     localInputStream = paramURL.openStream();
/*     */     try
/*     */     {
/* 243 */       localBufferedInputStream = new BufferedInputStream(localInputStream, 4096);
/*     */ 
/* 245 */       localAudioFileFormat = getAudioFileFormat(localBufferedInputStream);
/*     */     } finally {
/* 247 */       localInputStream.close();
/*     */     }
/*     */ 
/* 250 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 266 */     FileInputStream localFileInputStream = null;
/* 267 */     BufferedInputStream localBufferedInputStream = null;
/* 268 */     AudioFileFormat localAudioFileFormat = null;
/* 269 */     Object localObject1 = null;
/*     */ 
/* 271 */     localFileInputStream = new FileInputStream(paramFile);
/*     */     try
/*     */     {
/* 274 */       localBufferedInputStream = new BufferedInputStream(localFileInputStream, 4096);
/* 275 */       localAudioFileFormat = getAudioFileFormat(localBufferedInputStream);
/*     */     } finally {
/* 277 */       localFileInputStream.close();
/*     */     }
/*     */ 
/* 280 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 305 */     DataInputStream localDataInputStream = null;
/*     */ 
/* 307 */     AudioFileFormat localAudioFileFormat = null;
/* 308 */     AudioFormat localAudioFormat = null;
/*     */ 
/* 311 */     localAudioFileFormat = getAudioFileFormat(paramInputStream);
/*     */ 
/* 315 */     localAudioFormat = localAudioFileFormat.getFormat();
/*     */ 
/* 317 */     localDataInputStream = new DataInputStream(paramInputStream);
/*     */ 
/* 321 */     localDataInputStream.readInt();
/* 322 */     int i = localAudioFormat.isBigEndian() == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
/* 323 */     localDataInputStream.skipBytes(i - 8);
/*     */ 
/* 329 */     return new AudioInputStream(localDataInputStream, localAudioFormat, localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 346 */     InputStream localInputStream = null;
/* 347 */     BufferedInputStream localBufferedInputStream = null;
/* 348 */     Object localObject1 = null;
/*     */ 
/* 350 */     localInputStream = paramURL.openStream();
/* 351 */     AudioInputStream localAudioInputStream = null;
/*     */     try {
/* 353 */       localBufferedInputStream = new BufferedInputStream(localInputStream, 4096);
/* 354 */       localAudioInputStream = getAudioInputStream(localBufferedInputStream);
/*     */     } finally {
/* 356 */       if (localAudioInputStream == null) {
/* 357 */         localInputStream.close();
/*     */       }
/*     */     }
/* 360 */     return localAudioInputStream;
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 377 */     FileInputStream localFileInputStream = null;
/* 378 */     BufferedInputStream localBufferedInputStream = null;
/* 379 */     Object localObject1 = null;
/*     */ 
/* 381 */     localFileInputStream = new FileInputStream(paramFile);
/* 382 */     AudioInputStream localAudioInputStream = null;
/*     */     try
/*     */     {
/* 385 */       localBufferedInputStream = new BufferedInputStream(localFileInputStream, 4096);
/* 386 */       localAudioInputStream = getAudioInputStream(localBufferedInputStream);
/*     */     } finally {
/* 388 */       if (localAudioInputStream == null) {
/* 389 */         localFileInputStream.close();
/*     */       }
/*     */     }
/*     */ 
/* 393 */     return localAudioInputStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AuFileReader
 * JD-Core Version:    0.6.2
 */