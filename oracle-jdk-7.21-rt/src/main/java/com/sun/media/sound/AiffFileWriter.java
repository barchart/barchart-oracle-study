/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.io.SequenceInputStream;
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFileFormat.Type;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ 
/*     */ public class AiffFileWriter extends SunFileWriter
/*     */ {
/*  58 */   private static final AudioFileFormat.Type[] aiffTypes = { AudioFileFormat.Type.AIFF };
/*     */   private static final int DOUBLE_MANTISSA_LENGTH = 52;
/*     */   private static final int DOUBLE_EXPONENT_LENGTH = 11;
/*     */   private static final long DOUBLE_SIGN_MASK = -9223372036854775808L;
/*     */   private static final long DOUBLE_EXPONENT_MASK = 9218868437227405312L;
/*     */   private static final long DOUBLE_MANTISSA_MASK = 4503599627370495L;
/*     */   private static final int DOUBLE_EXPONENT_OFFSET = 1023;
/*     */   private static final int EXTENDED_EXPONENT_OFFSET = 16383;
/*     */   private static final int EXTENDED_MANTISSA_LENGTH = 63;
/*     */   private static final int EXTENDED_EXPONENT_LENGTH = 15;
/*     */   private static final long EXTENDED_INTEGER_MASK = -9223372036854775808L;
/*     */ 
/*     */   public AiffFileWriter()
/*     */   {
/*  67 */     super(aiffTypes);
/*     */   }
/*     */ 
/*     */   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
/*     */   {
/*  75 */     AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
/*  76 */     System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
/*     */ 
/*  79 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/*  80 */     AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
/*     */ 
/*  82 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)))
/*     */     {
/*  87 */       return arrayOfType;
/*     */     }
/*     */ 
/*  90 */     return new AudioFileFormat.Type[0];
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 101 */     AiffFileFormat localAiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 104 */     if (paramAudioInputStream.getFrameLength() == -1L) {
/* 105 */       throw new IOException("stream length not specified");
/*     */     }
/*     */ 
/* 108 */     int i = writeAiffFile(paramAudioInputStream, localAiffFileFormat, paramOutputStream);
/* 109 */     return i;
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
/*     */     throws IOException
/*     */   {
/* 116 */     AiffFileFormat localAiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 119 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/* 120 */     BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
/* 121 */     int i = writeAiffFile(paramAudioInputStream, localAiffFileFormat, localBufferedOutputStream);
/* 122 */     localBufferedOutputStream.close();
/*     */ 
/* 127 */     if (localAiffFileFormat.getByteLength() == -1)
/*     */     {
/* 131 */       int j = localAiffFileFormat.getFormat().getChannels() * localAiffFileFormat.getFormat().getSampleSizeInBits();
/*     */ 
/* 133 */       int k = i;
/* 134 */       int m = k - localAiffFileFormat.getHeaderSize() + 16;
/* 135 */       long l = m - 16;
/* 136 */       int n = (int)(l * 8L / j);
/*     */ 
/* 138 */       RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
/*     */ 
/* 140 */       localRandomAccessFile.skipBytes(4);
/* 141 */       localRandomAccessFile.writeInt(k - 8);
/*     */ 
/* 143 */       localRandomAccessFile.skipBytes(4 + localAiffFileFormat.getFverChunkSize() + 4 + 4 + 2);
/*     */ 
/* 145 */       localRandomAccessFile.writeInt(n);
/*     */ 
/* 147 */       localRandomAccessFile.skipBytes(16);
/* 148 */       localRandomAccessFile.writeInt(m - 8);
/*     */ 
/* 150 */       localRandomAccessFile.close();
/*     */     }
/*     */ 
/* 153 */     return i;
/*     */   }
/*     */ 
/*     */   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
/*     */   {
/* 165 */     AudioFormat localAudioFormat1 = null;
/* 166 */     AiffFileFormat localAiffFileFormat = null;
/* 167 */     AudioFormat.Encoding localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 169 */     AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
/* 170 */     AudioFormat.Encoding localEncoding2 = localAudioFormat2.getEncoding();
/*     */ 
/* 179 */     int k = 0;
/*     */ 
/* 181 */     if (!this.types[0].equals(paramType))
/* 182 */       throw new IllegalArgumentException("File type " + paramType + " not supported.");
/*     */     int i;
/* 185 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding2)) || (AudioFormat.Encoding.ULAW.equals(localEncoding2)))
/*     */     {
/* 188 */       if (localAudioFormat2.getSampleSizeInBits() == 8)
/*     */       {
/* 190 */         localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
/* 191 */         i = 16;
/* 192 */         k = 1;
/*     */       }
/*     */       else
/*     */       {
/* 197 */         throw new IllegalArgumentException("Encoding " + localEncoding2 + " supported only for 8-bit data.");
/*     */       }
/* 199 */     } else if (localAudioFormat2.getSampleSizeInBits() == 8)
/*     */     {
/* 201 */       localEncoding1 = AudioFormat.Encoding.PCM_UNSIGNED;
/* 202 */       i = 8;
/*     */     }
/*     */     else
/*     */     {
/* 206 */       localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
/* 207 */       i = localAudioFormat2.getSampleSizeInBits();
/*     */     }
/*     */ 
/* 211 */     localAudioFormat1 = new AudioFormat(localEncoding1, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true);
/*     */     int j;
/* 220 */     if (paramAudioInputStream.getFrameLength() != -1L) {
/* 221 */       if (k != 0)
/* 222 */         j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() * 2 + 54;
/*     */       else
/* 224 */         j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + 54;
/*     */     }
/*     */     else {
/* 227 */       j = -1;
/*     */     }
/*     */ 
/* 230 */     localAiffFileFormat = new AiffFileFormat(AudioFileFormat.Type.AIFF, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
/*     */ 
/* 235 */     return localAiffFileFormat;
/*     */   }
/*     */ 
/*     */   private int writeAiffFile(InputStream paramInputStream, AiffFileFormat paramAiffFileFormat, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 241 */     int i = 0;
/* 242 */     int j = 0;
/* 243 */     InputStream localInputStream = getFileStream(paramAiffFileFormat, paramInputStream);
/* 244 */     byte[] arrayOfByte = new byte[4096];
/* 245 */     int k = paramAiffFileFormat.getByteLength();
/*     */ 
/* 247 */     while ((i = localInputStream.read(arrayOfByte)) >= 0) {
/* 248 */       if (k > 0) {
/* 249 */         if (i < k) {
/* 250 */           paramOutputStream.write(arrayOfByte, 0, i);
/* 251 */           j += i;
/* 252 */           k -= i;
/*     */         } else {
/* 254 */           paramOutputStream.write(arrayOfByte, 0, k);
/* 255 */           j += k;
/* 256 */           k = 0;
/* 257 */           break;
/*     */         }
/*     */       }
/*     */       else {
/* 261 */         paramOutputStream.write(arrayOfByte, 0, i);
/* 262 */         j += i;
/*     */       }
/*     */     }
/*     */ 
/* 266 */     return j;
/*     */   }
/*     */ 
/*     */   private InputStream getFileStream(AiffFileFormat paramAiffFileFormat, InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 273 */     AudioFormat localAudioFormat1 = paramAiffFileFormat.getFormat();
/* 274 */     AudioFormat localAudioFormat2 = null;
/* 275 */     AudioFormat.Encoding localEncoding = null;
/*     */ 
/* 280 */     int i = paramAiffFileFormat.getHeaderSize();
/*     */ 
/* 283 */     int j = paramAiffFileFormat.getFverChunkSize();
/*     */ 
/* 285 */     int k = paramAiffFileFormat.getCommChunkSize();
/* 286 */     int m = -1;
/* 287 */     int n = -1;
/*     */ 
/* 289 */     int i1 = paramAiffFileFormat.getSsndChunkOffset();
/* 290 */     int i2 = (short)localAudioFormat1.getChannels();
/* 291 */     int i3 = (short)localAudioFormat1.getSampleSizeInBits();
/* 292 */     int i4 = i2 * i3;
/* 293 */     int i5 = paramAiffFileFormat.getFrameLength();
/* 294 */     long l = -1L;
/* 295 */     if (i5 != -1) {
/* 296 */       l = i5 * i4 / 8L;
/* 297 */       n = (int)l + 16;
/* 298 */       m = (int)l + i;
/*     */     }
/* 300 */     float f = localAudioFormat1.getSampleRate();
/* 301 */     int i6 = 1313820229;
/*     */ 
/* 303 */     byte[] arrayOfByte = null;
/* 304 */     ByteArrayInputStream localByteArrayInputStream = null;
/* 305 */     ByteArrayOutputStream localByteArrayOutputStream = null;
/* 306 */     DataOutputStream localDataOutputStream = null;
/* 307 */     SequenceInputStream localSequenceInputStream = null;
/* 308 */     Object localObject = paramInputStream;
/*     */ 
/* 312 */     if ((paramInputStream instanceof AudioInputStream))
/*     */     {
/* 314 */       localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
/* 315 */       localEncoding = localAudioFormat2.getEncoding();
/*     */ 
/* 319 */       if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) || ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (!localAudioFormat2.isBigEndian())))
/*     */       {
/* 323 */         localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
/*     */       }
/* 333 */       else if ((AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.ALAW.equals(localEncoding)))
/*     */       {
/* 336 */         if (localAudioFormat2.getSampleSizeInBits() != 8) {
/* 337 */           throw new IllegalArgumentException("unsupported encoding");
/*     */         }
/*     */ 
/* 344 */         localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits() * 2, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize() * 2, localAudioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 358 */     localByteArrayOutputStream = new ByteArrayOutputStream();
/* 359 */     localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
/*     */ 
/* 362 */     localDataOutputStream.writeInt(1179603533);
/* 363 */     localDataOutputStream.writeInt(m - 8);
/* 364 */     localDataOutputStream.writeInt(1095321158);
/*     */ 
/* 372 */     localDataOutputStream.writeInt(1129270605);
/* 373 */     localDataOutputStream.writeInt(k - 8);
/* 374 */     localDataOutputStream.writeShort(i2);
/* 375 */     localDataOutputStream.writeInt(i5);
/* 376 */     localDataOutputStream.writeShort(i3);
/* 377 */     write_ieee_extended(localDataOutputStream, f);
/*     */ 
/* 385 */     localDataOutputStream.writeInt(1397968452);
/* 386 */     localDataOutputStream.writeInt(n - 8);
/*     */ 
/* 390 */     localDataOutputStream.writeInt(0);
/* 391 */     localDataOutputStream.writeInt(0);
/*     */ 
/* 395 */     localDataOutputStream.close();
/* 396 */     arrayOfByte = localByteArrayOutputStream.toByteArray();
/* 397 */     localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/*     */ 
/* 399 */     localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
/*     */ 
/* 402 */     return localSequenceInputStream;
/*     */   }
/*     */ 
/*     */   private void write_ieee_extended(DataOutputStream paramDataOutputStream, float paramFloat)
/*     */     throws IOException
/*     */   {
/* 436 */     long l1 = Double.doubleToLongBits(paramFloat);
/*     */ 
/* 438 */     long l2 = (l1 & 0x0) >> 63;
/*     */ 
/* 440 */     long l3 = (l1 & 0x0) >> 52;
/*     */ 
/* 442 */     long l4 = l1 & 0xFFFFFFFF;
/*     */ 
/* 444 */     long l5 = l3 - 1023L + 16383L;
/*     */ 
/* 446 */     long l6 = l4 << 11;
/*     */ 
/* 448 */     long l7 = l2 << 15;
/* 449 */     int i = (short)(int)(l7 | l5);
/* 450 */     long l8 = 0x0 | l6;
/*     */ 
/* 452 */     paramDataOutputStream.writeShort(i);
/* 453 */     paramDataOutputStream.writeLong(l8);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AiffFileWriter
 * JD-Core Version:    0.6.2
 */