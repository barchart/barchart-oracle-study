/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
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
/*     */ public class AiffFileReader extends SunFileReader
/*     */ {
/*     */   private static final int MAX_READ_LENGTH = 8;
/*  69 */   public static final AudioFileFormat.Type[] types = { AudioFileFormat.Type.AIFF };
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 105 */     AudioFileFormat localAudioFileFormat = getCOMM(paramInputStream, true);
/*     */ 
/* 108 */     paramInputStream.reset();
/* 109 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 124 */     AudioFileFormat localAudioFileFormat = null;
/* 125 */     InputStream localInputStream = paramURL.openStream();
/*     */     try {
/* 127 */       localAudioFileFormat = getCOMM(localInputStream, false);
/*     */     } finally {
/* 129 */       localInputStream.close();
/*     */     }
/* 131 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat getAudioFileFormat(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 146 */     AudioFileFormat localAudioFileFormat = null;
/* 147 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/*     */     try
/*     */     {
/* 150 */       localAudioFileFormat = getCOMM(localFileInputStream, false);
/*     */     } finally {
/* 152 */       localFileInputStream.close();
/*     */     }
/*     */ 
/* 155 */     return localAudioFileFormat;
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 182 */     AudioFileFormat localAudioFileFormat = getCOMM(paramInputStream, true);
/*     */ 
/* 186 */     return new AudioInputStream(paramInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 202 */     InputStream localInputStream = paramURL.openStream();
/* 203 */     AudioFileFormat localAudioFileFormat = null;
/*     */     try {
/* 205 */       localAudioFileFormat = getCOMM(localInputStream, false);
/*     */     } finally {
/* 207 */       if (localAudioFileFormat == null) {
/* 208 */         localInputStream.close();
/*     */       }
/*     */     }
/* 211 */     return new AudioInputStream(localInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 229 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/* 230 */     AudioFileFormat localAudioFileFormat = null;
/*     */     try
/*     */     {
/* 233 */       localAudioFileFormat = getCOMM(localFileInputStream, false);
/*     */     } finally {
/* 235 */       if (localAudioFileFormat == null) {
/* 236 */         localFileInputStream.close();
/*     */       }
/*     */     }
/* 239 */     return new AudioInputStream(localFileInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   private AudioFileFormat getCOMM(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws UnsupportedAudioFileException, IOException
/*     */   {
/* 247 */     DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
/*     */ 
/* 249 */     if (paramBoolean) {
/* 250 */       localDataInputStream.mark(8);
/*     */     }
/*     */ 
/* 256 */     int i = 0;
/* 257 */     int j = 0;
/* 258 */     AudioFormat localAudioFormat = null;
/*     */ 
/* 261 */     int k = localDataInputStream.readInt();
/*     */ 
/* 264 */     if (k != 1179603533)
/*     */     {
/* 266 */       if (paramBoolean) {
/* 267 */         localDataInputStream.reset();
/*     */       }
/* 269 */       throw new UnsupportedAudioFileException("not an AIFF file");
/*     */     }
/*     */ 
/* 272 */     int m = localDataInputStream.readInt();
/* 273 */     int n = localDataInputStream.readInt();
/* 274 */     i += 12;
/*     */     int i1;
/* 277 */     if (m <= 0) {
/* 278 */       m = -1;
/* 279 */       i1 = -1;
/*     */     } else {
/* 281 */       i1 = m + 8;
/*     */     }
/*     */ 
/* 285 */     int i2 = 0;
/*     */ 
/* 287 */     if (n == 1095321155) {
/* 288 */       i2 = 1;
/*     */     }
/*     */ 
/* 293 */     int i3 = 0;
/* 294 */     while (i3 == 0)
/*     */     {
/* 296 */       int i4 = localDataInputStream.readInt();
/* 297 */       int i5 = localDataInputStream.readInt();
/* 298 */       i += 8;
/*     */ 
/* 300 */       int i6 = 0;
/*     */       int i7;
/* 303 */       switch (i4)
/*     */       {
/*     */       case 1180058962:
/* 306 */         break;
/*     */       case 1129270605:
/* 311 */         if (((i2 == 0) && (i5 < 18)) || ((i2 != 0) && (i5 < 22))) {
/* 312 */           throw new UnsupportedAudioFileException("Invalid AIFF/COMM chunksize");
/*     */         }
/*     */ 
/* 315 */         i7 = localDataInputStream.readShort();
/* 316 */         localDataInputStream.readInt();
/* 317 */         int i8 = localDataInputStream.readShort();
/* 318 */         float f = (float)read_ieee_extended(localDataInputStream);
/* 319 */         i6 += 18;
/*     */ 
/* 323 */         AudioFormat.Encoding localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 325 */         if (i2 != 0) {
/* 326 */           i9 = localDataInputStream.readInt(); i6 += 4;
/* 327 */           switch (i9) {
/*     */           case 1313820229:
/* 329 */             localEncoding = AudioFormat.Encoding.PCM_SIGNED;
/* 330 */             break;
/*     */           case 1970037111:
/* 332 */             localEncoding = AudioFormat.Encoding.ULAW;
/* 333 */             i8 = 8;
/* 334 */             break;
/*     */           default:
/* 336 */             throw new UnsupportedAudioFileException("Invalid AIFF encoding");
/*     */           }
/*     */         }
/* 339 */         int i9 = calculatePCMFrameSize(i8, i7);
/*     */ 
/* 344 */         localAudioFormat = new AudioFormat(localEncoding, f, i8, i7, i9, f, true);
/*     */ 
/* 347 */         break;
/*     */       case 1397968452:
/* 352 */         int i10 = localDataInputStream.readInt();
/* 353 */         int i11 = localDataInputStream.readInt();
/* 354 */         i6 += 8;
/*     */ 
/* 367 */         if (i5 < m) {
/* 368 */           j = i5 - i6;
/*     */         }
/*     */         else {
/* 371 */           j = m - (i + i6);
/*     */         }
/* 373 */         i3 = 1;
/*     */       }
/*     */ 
/* 376 */       i += i6;
/*     */ 
/* 378 */       if (i3 == 0) {
/* 379 */         i7 = i5 - i6;
/* 380 */         if (i7 > 0) {
/* 381 */           i += localDataInputStream.skipBytes(i7);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 386 */     if (localAudioFormat == null) {
/* 387 */       throw new UnsupportedAudioFileException("missing COMM chunk");
/*     */     }
/* 389 */     AudioFileFormat.Type localType = i2 != 0 ? AudioFileFormat.Type.AIFC : AudioFileFormat.Type.AIFF;
/*     */ 
/* 391 */     return new AiffFileFormat(localType, i1, localAudioFormat, j / localAudioFormat.getFrameSize());
/*     */   }
/*     */ 
/*     */   private void write_ieee_extended(DataOutputStream paramDataOutputStream, double paramDouble)
/*     */     throws IOException
/*     */   {
/* 404 */     int i = 16398;
/* 405 */     double d = paramDouble;
/*     */ 
/* 409 */     while (d < 44000.0D) {
/* 410 */       d *= 2.0D;
/* 411 */       i--;
/*     */     }
/* 413 */     paramDataOutputStream.writeShort(i);
/* 414 */     paramDataOutputStream.writeInt((int)d << 16);
/* 415 */     paramDataOutputStream.writeInt(0);
/*     */   }
/*     */ 
/*     */   private double read_ieee_extended(DataInputStream paramDataInputStream)
/*     */     throws IOException
/*     */   {
/* 428 */     double d1 = 0.0D;
/* 429 */     int i = 0;
/* 430 */     long l1 = 0L; long l2 = 0L;
/*     */ 
/* 432 */     double d2 = 3.402823466385289E+38D;
/*     */ 
/* 435 */     i = paramDataInputStream.readUnsignedShort();
/*     */ 
/* 437 */     long l3 = paramDataInputStream.readUnsignedShort();
/* 438 */     long l4 = paramDataInputStream.readUnsignedShort();
/* 439 */     l1 = l3 << 16 | l4;
/*     */ 
/* 441 */     l3 = paramDataInputStream.readUnsignedShort();
/* 442 */     l4 = paramDataInputStream.readUnsignedShort();
/* 443 */     l2 = l3 << 16 | l4;
/*     */ 
/* 445 */     if ((i == 0) && (l1 == 0L) && (l2 == 0L)) {
/* 446 */       d1 = 0.0D;
/*     */     }
/* 448 */     else if (i == 32767) {
/* 449 */       d1 = d2;
/*     */     } else {
/* 451 */       i -= 16383;
/* 452 */       i -= 31;
/* 453 */       d1 = l1 * Math.pow(2.0D, i);
/* 454 */       i -= 32;
/* 455 */       d1 += l2 * Math.pow(2.0D, i);
/*     */     }
/*     */ 
/* 459 */     return d1;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AiffFileReader
 * JD-Core Version:    0.6.2
 */