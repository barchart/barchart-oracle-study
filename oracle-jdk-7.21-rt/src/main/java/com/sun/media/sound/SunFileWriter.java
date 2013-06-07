/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import javax.sound.sampled.AudioFileFormat.Type;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.spi.AudioFileWriter;
/*     */ 
/*     */ abstract class SunFileWriter extends AudioFileWriter
/*     */ {
/*     */   protected static final int bufferSize = 16384;
/*     */   protected static final int bisBufferSize = 4096;
/*     */   final AudioFileFormat.Type[] types;
/*     */ 
/*     */   SunFileWriter(AudioFileFormat.Type[] paramArrayOfType)
/*     */   {
/*  63 */     this.types = paramArrayOfType;
/*     */   }
/*     */ 
/*     */   public AudioFileFormat.Type[] getAudioFileTypes()
/*     */   {
/*  74 */     AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
/*  75 */     System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
/*  76 */     return arrayOfType;
/*     */   }
/*     */ 
/*     */   public abstract AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream);
/*     */ 
/*     */   public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
/*     */     throws IOException;
/*     */ 
/*     */   protected int rllong(DataInputStream paramDataInputStream)
/*     */     throws IOException
/*     */   {
/* 101 */     int n = 0;
/*     */ 
/* 103 */     n = paramDataInputStream.readInt();
/*     */ 
/* 105 */     int i = (n & 0xFF) << 24;
/* 106 */     int j = (n & 0xFF00) << 8;
/* 107 */     int k = (n & 0xFF0000) >> 8;
/* 108 */     int m = (n & 0xFF000000) >>> 24;
/*     */ 
/* 110 */     n = i | j | k | m;
/*     */ 
/* 112 */     return n;
/*     */   }
/*     */ 
/*     */   protected int big2little(int paramInt)
/*     */   {
/* 125 */     int i = (paramInt & 0xFF) << 24;
/* 126 */     int j = (paramInt & 0xFF00) << 8;
/* 127 */     int k = (paramInt & 0xFF0000) >> 8;
/* 128 */     int m = (paramInt & 0xFF000000) >>> 24;
/*     */ 
/* 130 */     paramInt = i | j | k | m;
/*     */ 
/* 132 */     return paramInt;
/*     */   }
/*     */ 
/*     */   protected short rlshort(DataInputStream paramDataInputStream)
/*     */     throws IOException
/*     */   {
/* 144 */     int i = 0;
/*     */ 
/* 147 */     i = paramDataInputStream.readShort();
/*     */ 
/* 149 */     int j = (short)((i & 0xFF) << 8);
/* 150 */     int k = (short)((i & 0xFF00) >>> 8);
/*     */ 
/* 152 */     i = (short)(j | k);
/*     */ 
/* 154 */     return i;
/*     */   }
/*     */ 
/*     */   protected short big2littleShort(short paramShort)
/*     */   {
/* 167 */     int i = (short)((paramShort & 0xFF) << 8);
/* 168 */     int j = (short)((paramShort & 0xFF00) >>> 8);
/*     */ 
/* 170 */     paramShort = (short)(i | j);
/*     */ 
/* 172 */     return paramShort;
/*     */   }
/*     */ 
/*     */   protected class NoCloseInputStream extends InputStream
/*     */   {
/*     */     private final InputStream in;
/*     */ 
/*     */     public NoCloseInputStream(InputStream arg2)
/*     */     {
/*     */       Object localObject;
/* 184 */       this.in = localObject;
/*     */     }
/*     */ 
/*     */     public int read() throws IOException
/*     */     {
/* 189 */       return this.in.read();
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException
/*     */     {
/* 194 */       return this.in.read(paramArrayOfByte);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/* 199 */       return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */ 
/*     */     public long skip(long paramLong) throws IOException
/*     */     {
/* 204 */       return this.in.skip(paramLong);
/*     */     }
/*     */ 
/*     */     public int available() throws IOException
/*     */     {
/* 209 */       return this.in.available();
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mark(int paramInt)
/*     */     {
/* 219 */       this.in.mark(paramInt);
/*     */     }
/*     */ 
/*     */     public void reset() throws IOException
/*     */     {
/* 224 */       this.in.reset();
/*     */     }
/*     */ 
/*     */     public boolean markSupported()
/*     */     {
/* 229 */       return this.in.markSupported();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SunFileWriter
 * JD-Core Version:    0.6.2
 */