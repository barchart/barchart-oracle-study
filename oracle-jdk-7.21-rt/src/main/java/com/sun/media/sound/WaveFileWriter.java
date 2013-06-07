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
/*     */ public class WaveFileWriter extends SunFileWriter
/*     */ {
/*     */   static final int RIFF_MAGIC = 1380533830;
/*     */   static final int WAVE_MAGIC = 1463899717;
/*     */   static final int FMT_MAGIC = 1718449184;
/*     */   static final int DATA_MAGIC = 1684108385;
/*     */   static final int WAVE_FORMAT_UNKNOWN = 0;
/*     */   static final int WAVE_FORMAT_PCM = 1;
/*     */   static final int WAVE_FORMAT_ADPCM = 2;
/*     */   static final int WAVE_FORMAT_ALAW = 6;
/*     */   static final int WAVE_FORMAT_MULAW = 7;
/*     */   static final int WAVE_FORMAT_OKI_ADPCM = 16;
/*     */   static final int WAVE_FORMAT_DIGISTD = 21;
/*     */   static final int WAVE_FORMAT_DIGIFIX = 22;
/*     */   static final int WAVE_IBM_FORMAT_MULAW = 257;
/*     */   static final int WAVE_IBM_FORMAT_ALAW = 258;
/*     */   static final int WAVE_IBM_FORMAT_ADPCM = 259;
/*     */   static final int WAVE_FORMAT_DVI_ADPCM = 17;
/*     */   static final int WAVE_FORMAT_SX7383 = 7175;
/*  79 */   private static final AudioFileFormat.Type[] waveTypes = { AudioFileFormat.Type.WAVE };
/*     */ 
/*     */   public WaveFileWriter()
/*     */   {
/*  88 */     super(waveTypes);
/*     */   }
/*     */ 
/*     */   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
/*     */   {
/*  97 */     AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
/*  98 */     System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
/*     */ 
/* 101 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/* 102 */     AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
/*     */ 
/* 104 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)))
/*     */     {
/* 109 */       return arrayOfType;
/*     */     }
/*     */ 
/* 112 */     return new AudioFileFormat.Type[0];
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 123 */     WaveFileFormat localWaveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 128 */     if (paramAudioInputStream.getFrameLength() == -1L) {
/* 129 */       throw new IOException("stream length not specified");
/*     */     }
/*     */ 
/* 132 */     int i = writeWaveFile(paramAudioInputStream, localWaveFileFormat, paramOutputStream);
/* 133 */     return i;
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
/*     */     throws IOException
/*     */   {
/* 140 */     WaveFileFormat localWaveFileFormat = (WaveFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 143 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/* 144 */     BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
/* 145 */     int i = writeWaveFile(paramAudioInputStream, localWaveFileFormat, localBufferedOutputStream);
/* 146 */     localBufferedOutputStream.close();
/*     */ 
/* 151 */     if (localWaveFileFormat.getByteLength() == -1)
/*     */     {
/* 153 */       int j = i - localWaveFileFormat.getHeaderSize();
/* 154 */       int k = j + localWaveFileFormat.getHeaderSize() - 8;
/*     */ 
/* 156 */       RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
/*     */ 
/* 158 */       localRandomAccessFile.skipBytes(4);
/* 159 */       localRandomAccessFile.writeInt(big2little(k));
/*     */ 
/* 161 */       localRandomAccessFile.skipBytes(12 + WaveFileFormat.getFmtChunkSize(localWaveFileFormat.getWaveType()) + 4);
/* 162 */       localRandomAccessFile.writeInt(big2little(j));
/*     */ 
/* 164 */       localRandomAccessFile.close();
/*     */     }
/*     */ 
/* 167 */     return i;
/*     */   }
/*     */ 
/*     */   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
/*     */   {
/* 177 */     AudioFormat localAudioFormat1 = null;
/* 178 */     WaveFileFormat localWaveFileFormat = null;
/* 179 */     Object localObject = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 181 */     AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
/* 182 */     AudioFormat.Encoding localEncoding = localAudioFormat2.getEncoding();
/*     */ 
/* 191 */     if (!this.types[0].equals(paramType)) {
/* 192 */       throw new IllegalArgumentException("File type " + paramType + " not supported.");
/*     */     }
/* 194 */     int k = 1;
/*     */     int i;
/* 196 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)))
/*     */     {
/* 199 */       localObject = localEncoding;
/* 200 */       i = localAudioFormat2.getSampleSizeInBits();
/* 201 */       if (localEncoding.equals(AudioFormat.Encoding.ALAW))
/* 202 */         k = 6;
/*     */       else
/* 204 */         k = 7;
/*     */     }
/* 206 */     else if (localAudioFormat2.getSampleSizeInBits() == 8) {
/* 207 */       localObject = AudioFormat.Encoding.PCM_UNSIGNED;
/* 208 */       i = 8;
/*     */     } else {
/* 210 */       localObject = AudioFormat.Encoding.PCM_SIGNED;
/* 211 */       i = localAudioFormat2.getSampleSizeInBits();
/*     */     }
/*     */ 
/* 215 */     localAudioFormat1 = new AudioFormat((AudioFormat.Encoding)localObject, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false);
/*     */     int j;
/* 223 */     if (paramAudioInputStream.getFrameLength() != -1L) {
/* 224 */       j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + WaveFileFormat.getHeaderSize(k);
/*     */     }
/*     */     else {
/* 227 */       j = -1;
/*     */     }
/*     */ 
/* 230 */     localWaveFileFormat = new WaveFileFormat(AudioFileFormat.Type.WAVE, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
/*     */ 
/* 235 */     return localWaveFileFormat;
/*     */   }
/*     */ 
/*     */   private int writeWaveFile(InputStream paramInputStream, WaveFileFormat paramWaveFileFormat, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 241 */     int i = 0;
/* 242 */     int j = 0;
/* 243 */     InputStream localInputStream = getFileStream(paramWaveFileFormat, paramInputStream);
/* 244 */     byte[] arrayOfByte = new byte[4096];
/* 245 */     int k = paramWaveFileFormat.getByteLength();
/*     */ 
/* 247 */     while ((i = localInputStream.read(arrayOfByte)) >= 0)
/*     */     {
/* 249 */       if (k > 0) {
/* 250 */         if (i < k) {
/* 251 */           paramOutputStream.write(arrayOfByte, 0, i);
/* 252 */           j += i;
/* 253 */           k -= i;
/*     */         } else {
/* 255 */           paramOutputStream.write(arrayOfByte, 0, k);
/* 256 */           j += k;
/* 257 */           k = 0;
/* 258 */           break;
/*     */         }
/*     */       } else {
/* 261 */         paramOutputStream.write(arrayOfByte, 0, i);
/* 262 */         j += i;
/*     */       }
/*     */     }
/*     */ 
/* 266 */     return j;
/*     */   }
/*     */ 
/*     */   private InputStream getFileStream(WaveFileFormat paramWaveFileFormat, InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 273 */     AudioFormat localAudioFormat1 = paramWaveFileFormat.getFormat();
/* 274 */     int i = paramWaveFileFormat.getHeaderSize();
/* 275 */     int j = 1380533830;
/* 276 */     int k = 1463899717;
/* 277 */     int m = 1718449184;
/* 278 */     int n = WaveFileFormat.getFmtChunkSize(paramWaveFileFormat.getWaveType());
/* 279 */     short s1 = (short)paramWaveFileFormat.getWaveType();
/* 280 */     int i1 = (short)localAudioFormat1.getChannels();
/* 281 */     int i2 = (short)localAudioFormat1.getSampleSizeInBits();
/* 282 */     int i3 = (int)localAudioFormat1.getSampleRate();
/* 283 */     int i4 = localAudioFormat1.getFrameSize();
/* 284 */     int i5 = (int)localAudioFormat1.getFrameRate();
/* 285 */     int i6 = i1 * i2 * i3 / 8;
/* 286 */     short s2 = (short)(i2 / 8 * i1);
/* 287 */     int i7 = 1684108385;
/* 288 */     int i8 = paramWaveFileFormat.getFrameLength() * i4;
/* 289 */     int i9 = paramWaveFileFormat.getByteLength();
/* 290 */     int i10 = i8 + i - 8;
/*     */ 
/* 292 */     byte[] arrayOfByte = null;
/* 293 */     ByteArrayInputStream localByteArrayInputStream = null;
/* 294 */     ByteArrayOutputStream localByteArrayOutputStream = null;
/* 295 */     DataOutputStream localDataOutputStream = null;
/* 296 */     SequenceInputStream localSequenceInputStream = null;
/*     */ 
/* 298 */     AudioFormat localAudioFormat2 = null;
/* 299 */     AudioFormat.Encoding localEncoding = null;
/* 300 */     Object localObject = paramInputStream;
/*     */ 
/* 303 */     if ((paramInputStream instanceof AudioInputStream)) {
/* 304 */       localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
/*     */ 
/* 306 */       localEncoding = localAudioFormat2.getEncoding();
/*     */ 
/* 308 */       if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && 
/* 309 */         (i2 == 8)) {
/* 310 */         s1 = 1;
/*     */ 
/* 312 */         localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
/*     */       }
/*     */ 
/* 323 */       if (((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (localAudioFormat2.isBigEndian())) || ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) && (!localAudioFormat2.isBigEndian())) || ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) && (localAudioFormat2.isBigEndian())))
/*     */       {
/* 326 */         if (i2 != 8) {
/* 327 */           s1 = 1;
/*     */ 
/* 329 */           localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), false), (AudioInputStream)paramInputStream);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 345 */     localByteArrayOutputStream = new ByteArrayOutputStream();
/* 346 */     localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
/*     */ 
/* 349 */     localDataOutputStream.writeInt(j);
/* 350 */     localDataOutputStream.writeInt(big2little(i10));
/* 351 */     localDataOutputStream.writeInt(k);
/* 352 */     localDataOutputStream.writeInt(m);
/* 353 */     localDataOutputStream.writeInt(big2little(n));
/* 354 */     localDataOutputStream.writeShort(big2littleShort(s1));
/* 355 */     localDataOutputStream.writeShort(big2littleShort(i1));
/* 356 */     localDataOutputStream.writeInt(big2little(i3));
/* 357 */     localDataOutputStream.writeInt(big2little(i6));
/* 358 */     localDataOutputStream.writeShort(big2littleShort(s2));
/* 359 */     localDataOutputStream.writeShort(big2littleShort(i2));
/*     */ 
/* 361 */     if (s1 != 1)
/*     */     {
/* 363 */       localDataOutputStream.writeShort(0);
/*     */     }
/*     */ 
/* 366 */     localDataOutputStream.writeInt(i7);
/* 367 */     localDataOutputStream.writeInt(big2little(i8));
/*     */ 
/* 369 */     localDataOutputStream.close();
/* 370 */     arrayOfByte = localByteArrayOutputStream.toByteArray();
/* 371 */     localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 372 */     localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
/*     */ 
/* 375 */     return localSequenceInputStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.WaveFileWriter
 * JD-Core Version:    0.6.2
 */