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
/*     */ public class AuFileWriter extends SunFileWriter
/*     */ {
/*     */   public static final int UNKNOWN_SIZE = -1;
/*  60 */   private static final AudioFileFormat.Type[] auTypes = { AudioFileFormat.Type.AU };
/*     */ 
/*     */   public AuFileWriter()
/*     */   {
/*  69 */     super(auTypes);
/*     */   }
/*     */ 
/*     */   public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
/*     */   {
/*  76 */     AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
/*  77 */     System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
/*     */ 
/*  80 */     AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
/*  81 */     AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
/*     */ 
/*  83 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)))
/*     */     {
/*  88 */       return arrayOfType;
/*     */     }
/*     */ 
/*  91 */     return new AudioFileFormat.Type[0];
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 104 */     AuFileFormat localAuFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 106 */     int i = writeAuFile(paramAudioInputStream, localAuFileFormat, paramOutputStream);
/* 107 */     return i;
/*     */   }
/*     */ 
/*     */   public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
/*     */     throws IOException
/*     */   {
/* 115 */     AuFileFormat localAuFileFormat = (AuFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
/*     */ 
/* 118 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/* 119 */     BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
/* 120 */     int i = writeAuFile(paramAudioInputStream, localAuFileFormat, localBufferedOutputStream);
/* 121 */     localBufferedOutputStream.close();
/*     */ 
/* 126 */     if (localAuFileFormat.getByteLength() == -1)
/*     */     {
/* 130 */       RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
/* 131 */       if (localRandomAccessFile.length() <= 2147483647L)
/*     */       {
/* 133 */         localRandomAccessFile.skipBytes(8);
/* 134 */         localRandomAccessFile.writeInt(i - 24);
/*     */       }
/*     */ 
/* 137 */       localRandomAccessFile.close();
/*     */     }
/*     */ 
/* 140 */     return i;
/*     */   }
/*     */ 
/*     */   private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
/*     */   {
/* 152 */     AudioFormat localAudioFormat1 = null;
/* 153 */     AuFileFormat localAuFileFormat = null;
/* 154 */     Object localObject = AudioFormat.Encoding.PCM_SIGNED;
/*     */ 
/* 156 */     AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
/* 157 */     AudioFormat.Encoding localEncoding = localAudioFormat2.getEncoding();
/*     */ 
/* 167 */     if (!this.types[0].equals(paramType))
/* 168 */       throw new IllegalArgumentException("File type " + paramType + " not supported.");
/*     */     int i;
/* 171 */     if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)))
/*     */     {
/* 174 */       localObject = localEncoding;
/* 175 */       i = localAudioFormat2.getSampleSizeInBits();
/*     */     }
/* 177 */     else if (localAudioFormat2.getSampleSizeInBits() == 8)
/*     */     {
/* 179 */       localObject = AudioFormat.Encoding.PCM_SIGNED;
/* 180 */       i = 8;
/*     */     }
/*     */     else
/*     */     {
/* 184 */       localObject = AudioFormat.Encoding.PCM_SIGNED;
/* 185 */       i = localAudioFormat2.getSampleSizeInBits();
/*     */     }
/*     */ 
/* 189 */     localAudioFormat1 = new AudioFormat((AudioFormat.Encoding)localObject, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true);
/*     */     int j;
/* 198 */     if (paramAudioInputStream.getFrameLength() != -1L)
/* 199 */       j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + 24;
/*     */     else {
/* 201 */       j = -1;
/*     */     }
/*     */ 
/* 204 */     localAuFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
/*     */ 
/* 209 */     return localAuFileFormat;
/*     */   }
/*     */ 
/*     */   private InputStream getFileStream(AuFileFormat paramAuFileFormat, InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 217 */     AudioFormat localAudioFormat1 = paramAuFileFormat.getFormat();
/*     */ 
/* 219 */     int i = 779316836;
/* 220 */     int j = 24;
/* 221 */     long l1 = paramAuFileFormat.getFrameLength();
/*     */ 
/* 224 */     long l2 = l1 == -1L ? -1L : l1 * localAudioFormat1.getFrameSize();
/* 225 */     if (l2 > 2147483647L) {
/* 226 */       l2 = -1L;
/*     */     }
/* 228 */     int k = paramAuFileFormat.getAuType();
/* 229 */     int m = (int)localAudioFormat1.getSampleRate();
/* 230 */     int n = localAudioFormat1.getChannels();
/*     */ 
/* 233 */     boolean bool = true;
/*     */ 
/* 235 */     byte[] arrayOfByte = null;
/* 236 */     ByteArrayInputStream localByteArrayInputStream = null;
/* 237 */     ByteArrayOutputStream localByteArrayOutputStream = null;
/* 238 */     DataOutputStream localDataOutputStream = null;
/* 239 */     SequenceInputStream localSequenceInputStream = null;
/*     */ 
/* 241 */     AudioFormat localAudioFormat2 = null;
/* 242 */     AudioFormat.Encoding localEncoding = null;
/* 243 */     Object localObject = paramInputStream;
/*     */ 
/* 247 */     localObject = paramInputStream;
/*     */ 
/* 249 */     if ((paramInputStream instanceof AudioInputStream))
/*     */     {
/* 252 */       localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
/* 253 */       localEncoding = localAudioFormat2.getEncoding();
/*     */ 
/* 256 */       if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) || ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (bool != localAudioFormat2.isBigEndian())))
/*     */       {
/* 264 */         localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), bool), (AudioInputStream)paramInputStream);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 278 */     localByteArrayOutputStream = new ByteArrayOutputStream();
/* 279 */     localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
/*     */ 
/* 282 */     if (bool) {
/* 283 */       localDataOutputStream.writeInt(779316836);
/* 284 */       localDataOutputStream.writeInt(j);
/* 285 */       localDataOutputStream.writeInt((int)l2);
/* 286 */       localDataOutputStream.writeInt(k);
/* 287 */       localDataOutputStream.writeInt(m);
/* 288 */       localDataOutputStream.writeInt(n);
/*     */     } else {
/* 290 */       localDataOutputStream.writeInt(1684960046);
/* 291 */       localDataOutputStream.writeInt(big2little(j));
/* 292 */       localDataOutputStream.writeInt(big2little((int)l2));
/* 293 */       localDataOutputStream.writeInt(big2little(k));
/* 294 */       localDataOutputStream.writeInt(big2little(m));
/* 295 */       localDataOutputStream.writeInt(big2little(n));
/*     */     }
/*     */ 
/* 301 */     localDataOutputStream.close();
/* 302 */     arrayOfByte = localByteArrayOutputStream.toByteArray();
/* 303 */     localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
/* 304 */     localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
/*     */ 
/* 307 */     return localSequenceInputStream;
/*     */   }
/*     */ 
/*     */   private int writeAuFile(InputStream paramInputStream, AuFileFormat paramAuFileFormat, OutputStream paramOutputStream) throws IOException
/*     */   {
/* 312 */     int i = 0;
/* 313 */     int j = 0;
/* 314 */     InputStream localInputStream = getFileStream(paramAuFileFormat, paramInputStream);
/* 315 */     byte[] arrayOfByte = new byte[4096];
/* 316 */     int k = paramAuFileFormat.getByteLength();
/*     */ 
/* 318 */     while ((i = localInputStream.read(arrayOfByte)) >= 0) {
/* 319 */       if (k > 0) {
/* 320 */         if (i < k) {
/* 321 */           paramOutputStream.write(arrayOfByte, 0, i);
/* 322 */           j += i;
/* 323 */           k -= i;
/*     */         } else {
/* 325 */           paramOutputStream.write(arrayOfByte, 0, k);
/* 326 */           j += k;
/* 327 */           k = 0;
/* 328 */           break;
/*     */         }
/*     */       } else {
/* 331 */         paramOutputStream.write(arrayOfByte, 0, i);
/* 332 */         j += i;
/*     */       }
/*     */     }
/*     */ 
/* 336 */     return j;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AuFileWriter
 * JD-Core Version:    0.6.2
 */