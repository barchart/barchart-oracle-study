/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ import javax.sound.sampled.spi.AudioFileReader;
/*     */ 
/*     */ abstract class SunFileReader extends AudioFileReader
/*     */ {
/*     */   protected static final int bisBufferSize = 4096;
/*     */ 
/*     */   public abstract AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   public abstract AudioFileFormat getAudioFileFormat(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   public abstract AudioFileFormat getAudioFileFormat(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   public abstract AudioInputStream getAudioInputStream(InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   public abstract AudioInputStream getAudioInputStream(URL paramURL)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   public abstract AudioInputStream getAudioInputStream(File paramFile)
/*     */     throws UnsupportedAudioFileException, IOException;
/*     */ 
/*     */   protected int rllong(DataInputStream paramDataInputStream)
/*     */     throws IOException
/*     */   {
/* 173 */     int n = 0;
/*     */ 
/* 175 */     n = paramDataInputStream.readInt();
/*     */ 
/* 177 */     int i = (n & 0xFF) << 24;
/* 178 */     int j = (n & 0xFF00) << 8;
/* 179 */     int k = (n & 0xFF0000) >> 8;
/* 180 */     int m = (n & 0xFF000000) >>> 24;
/*     */ 
/* 182 */     n = i | j | k | m;
/*     */ 
/* 184 */     return n;
/*     */   }
/*     */ 
/*     */   protected int big2little(int paramInt)
/*     */   {
/* 197 */     int i = (paramInt & 0xFF) << 24;
/* 198 */     int j = (paramInt & 0xFF00) << 8;
/* 199 */     int k = (paramInt & 0xFF0000) >> 8;
/* 200 */     int m = (paramInt & 0xFF000000) >>> 24;
/*     */ 
/* 202 */     paramInt = i | j | k | m;
/*     */ 
/* 204 */     return paramInt;
/*     */   }
/*     */ 
/*     */   protected short rlshort(DataInputStream paramDataInputStream)
/*     */     throws IOException
/*     */   {
/* 216 */     int i = 0;
/*     */ 
/* 219 */     i = paramDataInputStream.readShort();
/*     */ 
/* 221 */     int j = (short)((i & 0xFF) << 8);
/* 222 */     int k = (short)((i & 0xFF00) >>> 8);
/*     */ 
/* 224 */     i = (short)(j | k);
/*     */ 
/* 226 */     return i;
/*     */   }
/*     */ 
/*     */   protected short big2littleShort(short paramShort)
/*     */   {
/* 239 */     int i = (short)((paramShort & 0xFF) << 8);
/* 240 */     int j = (short)((paramShort & 0xFF00) >>> 8);
/*     */ 
/* 242 */     paramShort = (short)(i | j);
/*     */ 
/* 244 */     return paramShort;
/*     */   }
/*     */ 
/*     */   protected static int calculatePCMFrameSize(int paramInt1, int paramInt2)
/*     */   {
/* 257 */     return (paramInt1 + 7) / 8 * paramInt2;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.SunFileReader
 * JD-Core Version:    0.6.2
 */