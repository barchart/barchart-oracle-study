/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFileFormat.Type;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ 
/*     */ class WaveFileFormat extends AudioFileFormat
/*     */ {
/*     */   private int waveType;
/*     */   private static final int STANDARD_HEADER_SIZE = 28;
/*     */   private static final int STANDARD_FMT_CHUNK_SIZE = 16;
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
/*     */ 
/*     */   WaveFileFormat(AudioFileFormat paramAudioFileFormat)
/*     */   {
/*  95 */     this(paramAudioFileFormat.getType(), paramAudioFileFormat.getByteLength(), paramAudioFileFormat.getFormat(), paramAudioFileFormat.getFrameLength());
/*     */   }
/*     */ 
/*     */   WaveFileFormat(AudioFileFormat.Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2)
/*     */   {
/* 100 */     super(paramType, paramInt1, paramAudioFormat, paramInt2);
/*     */ 
/* 102 */     AudioFormat.Encoding localEncoding = paramAudioFormat.getEncoding();
/*     */ 
/* 104 */     if (localEncoding.equals(AudioFormat.Encoding.ALAW))
/* 105 */       this.waveType = 6;
/* 106 */     else if (localEncoding.equals(AudioFormat.Encoding.ULAW))
/* 107 */       this.waveType = 7;
/* 108 */     else if ((localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) || (localEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)))
/*     */     {
/* 110 */       this.waveType = 1;
/*     */     }
/* 112 */     else this.waveType = 0;
/*     */   }
/*     */ 
/*     */   int getWaveType()
/*     */   {
/* 118 */     return this.waveType;
/*     */   }
/*     */ 
/*     */   int getHeaderSize() {
/* 122 */     return getHeaderSize(getWaveType());
/*     */   }
/*     */ 
/*     */   static int getHeaderSize(int paramInt)
/*     */   {
/* 128 */     return 28 + getFmtChunkSize(paramInt);
/*     */   }
/*     */ 
/*     */   static int getFmtChunkSize(int paramInt)
/*     */   {
/* 134 */     int i = 16;
/* 135 */     if (paramInt != 1) {
/* 136 */       i += 2;
/*     */     }
/* 138 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.WaveFileFormat
 * JD-Core Version:    0.6.2
 */