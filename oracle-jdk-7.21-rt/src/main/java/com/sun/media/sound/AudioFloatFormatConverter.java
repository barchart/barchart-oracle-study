/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.spi.FormatConversionProvider;
/*     */ 
/*     */ public class AudioFloatFormatConverter extends FormatConversionProvider
/*     */ {
/* 471 */   private AudioFormat.Encoding[] formats = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
/*     */   {
/* 476 */     if (paramAudioInputStream.getFormat().getEncoding().equals(paramEncoding))
/* 477 */       return paramAudioInputStream;
/* 478 */     AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
/* 479 */     int i = localAudioFormat1.getChannels();
/* 480 */     AudioFormat.Encoding localEncoding = paramEncoding;
/* 481 */     float f = localAudioFormat1.getSampleRate();
/* 482 */     int j = localAudioFormat1.getSampleSizeInBits();
/* 483 */     boolean bool = localAudioFormat1.isBigEndian();
/* 484 */     if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT))
/* 485 */       j = 32;
/* 486 */     AudioFormat localAudioFormat2 = new AudioFormat(localEncoding, f, j, i, i * j / 8, f, bool);
/*     */ 
/* 488 */     return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
/*     */   {
/* 493 */     if (!isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat())) {
/* 494 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
/*     */     }
/*     */ 
/* 497 */     return getAudioInputStream(paramAudioFormat, AudioFloatInputStream.getInputStream(paramAudioInputStream));
/*     */   }
/*     */ 
/*     */   public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
/*     */   {
/* 504 */     if (!isConversionSupported(paramAudioFormat, paramAudioFloatInputStream.getFormat())) {
/* 505 */       throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFloatInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
/*     */     }
/*     */ 
/* 508 */     if (paramAudioFormat.getChannels() != paramAudioFloatInputStream.getFormat().getChannels())
/*     */     {
/* 510 */       paramAudioFloatInputStream = new AudioFloatInputStreamChannelMixer(paramAudioFloatInputStream, paramAudioFormat.getChannels());
/*     */     }
/* 512 */     if (Math.abs(paramAudioFormat.getSampleRate() - paramAudioFloatInputStream.getFormat().getSampleRate()) > 1.0E-06D)
/*     */     {
/* 514 */       paramAudioFloatInputStream = new AudioFloatInputStreamResampler(paramAudioFloatInputStream, paramAudioFormat);
/*     */     }
/* 516 */     return new AudioInputStream(new AudioFloatFormatConverterInputStream(paramAudioFormat, paramAudioFloatInputStream), paramAudioFormat, paramAudioFloatInputStream.getFrameLength());
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getSourceEncodings()
/*     */   {
/* 522 */     return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getTargetEncodings()
/*     */   {
/* 527 */     return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
/*     */   }
/*     */ 
/*     */   public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
/*     */   {
/* 532 */     if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
/* 533 */       return new AudioFormat.Encoding[0];
/* 534 */     return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
/*     */   }
/*     */ 
/*     */   public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
/*     */   {
/* 540 */     if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
/* 541 */       return new AudioFormat[0];
/* 542 */     int i = paramAudioFormat.getChannels();
/*     */ 
/* 544 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 546 */     if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
/* 547 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, i, i, -1.0F, false));
/*     */     }
/*     */ 
/* 550 */     if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
/* 551 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, i, i, -1.0F, false));
/*     */     }
/*     */ 
/* 555 */     for (int j = 16; j < 32; j += 8) {
/* 556 */       if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
/* 557 */         localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
/*     */ 
/* 560 */         localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
/*     */       }
/*     */ 
/* 564 */       if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
/* 565 */         localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
/*     */ 
/* 568 */         localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 574 */     if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
/* 575 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, false));
/*     */ 
/* 578 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, true));
/*     */ 
/* 581 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, false));
/*     */ 
/* 584 */       localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, true));
/*     */     }
/*     */ 
/* 589 */     return (AudioFormat[])localArrayList.toArray(new AudioFormat[localArrayList.size()]);
/*     */   }
/*     */ 
/*     */   public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2)
/*     */   {
/* 594 */     if (AudioFloatConverter.getConverter(paramAudioFormat2) == null)
/* 595 */       return false;
/* 596 */     if (AudioFloatConverter.getConverter(paramAudioFormat1) == null)
/* 597 */       return false;
/* 598 */     if (paramAudioFormat2.getChannels() <= 0)
/* 599 */       return false;
/* 600 */     if (paramAudioFormat1.getChannels() <= 0)
/* 601 */       return false;
/* 602 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
/*     */   {
/* 607 */     if (AudioFloatConverter.getConverter(paramAudioFormat) == null)
/* 608 */       return false;
/* 609 */     for (int i = 0; i < this.formats.length; i++) {
/* 610 */       if (paramEncoding.equals(this.formats[i]))
/* 611 */         return true;
/*     */     }
/* 613 */     return false;
/*     */   }
/*     */ 
/*     */   private static class AudioFloatFormatConverterInputStream extends InputStream
/*     */   {
/*     */     private AudioFloatConverter converter;
/*     */     private AudioFloatInputStream stream;
/*     */     private float[] readfloatbuffer;
/*  55 */     private int fsize = 0;
/*     */ 
/*     */     public AudioFloatFormatConverterInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
/*     */     {
/*  59 */       this.stream = paramAudioFloatInputStream;
/*  60 */       this.converter = AudioFloatConverter.getConverter(paramAudioFormat);
/*  61 */       this.fsize = ((paramAudioFormat.getSampleSizeInBits() + 7) / 8);
/*     */     }
/*     */ 
/*     */     public int read() throws IOException {
/*  65 */       byte[] arrayOfByte = new byte[1];
/*  66 */       int i = read(arrayOfByte);
/*  67 */       if (i < 0)
/*  68 */         return i;
/*  69 */       return arrayOfByte[0] & 0xFF;
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
/*     */     {
/*  74 */       int i = paramInt2 / this.fsize;
/*  75 */       if ((this.readfloatbuffer == null) || (this.readfloatbuffer.length < i))
/*  76 */         this.readfloatbuffer = new float[i];
/*  77 */       int j = this.stream.read(this.readfloatbuffer, 0, i);
/*  78 */       if (j < 0)
/*  79 */         return j;
/*  80 */       this.converter.toByteArray(this.readfloatbuffer, 0, j, paramArrayOfByte, paramInt1);
/*  81 */       return j * this.fsize;
/*     */     }
/*     */ 
/*     */     public int available() throws IOException {
/*  85 */       int i = this.stream.available();
/*  86 */       if (i < 0)
/*  87 */         return i;
/*  88 */       return i * this.fsize;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException {
/*  92 */       this.stream.close();
/*     */     }
/*     */ 
/*     */     public synchronized void mark(int paramInt) {
/*  96 */       this.stream.mark(paramInt * this.fsize);
/*     */     }
/*     */ 
/*     */     public boolean markSupported() {
/* 100 */       return this.stream.markSupported();
/*     */     }
/*     */ 
/*     */     public synchronized void reset() throws IOException {
/* 104 */       this.stream.reset();
/*     */     }
/*     */ 
/*     */     public long skip(long paramLong) throws IOException {
/* 108 */       long l = this.stream.skip(paramLong / this.fsize);
/* 109 */       if (l < 0L)
/* 110 */         return l;
/* 111 */       return l * this.fsize;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AudioFloatInputStreamChannelMixer extends AudioFloatInputStream
/*     */   {
/*     */     private int targetChannels;
/*     */     private int sourceChannels;
/*     */     private AudioFloatInputStream ais;
/*     */     private AudioFormat targetFormat;
/*     */     private float[] conversion_buffer;
/*     */ 
/*     */     public AudioFloatInputStreamChannelMixer(AudioFloatInputStream paramAudioFloatInputStream, int paramInt)
/*     */     {
/* 131 */       this.sourceChannels = paramAudioFloatInputStream.getFormat().getChannels();
/* 132 */       this.targetChannels = paramInt;
/* 133 */       this.ais = paramAudioFloatInputStream;
/* 134 */       AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
/* 135 */       this.targetFormat = new AudioFormat(localAudioFormat.getEncoding(), localAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), paramInt, localAudioFormat.getFrameSize() / this.sourceChannels * paramInt, localAudioFormat.getFrameRate(), localAudioFormat.isBigEndian());
/*     */     }
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 143 */       return this.ais.available() / this.sourceChannels * this.targetChannels;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException {
/* 147 */       this.ais.close();
/*     */     }
/*     */ 
/*     */     public AudioFormat getFormat() {
/* 151 */       return this.targetFormat;
/*     */     }
/*     */ 
/*     */     public long getFrameLength() {
/* 155 */       return this.ais.getFrameLength();
/*     */     }
/*     */ 
/*     */     public void mark(int paramInt) {
/* 159 */       this.ais.mark(paramInt / this.targetChannels * this.sourceChannels);
/*     */     }
/*     */ 
/*     */     public boolean markSupported() {
/* 163 */       return this.ais.markSupported();
/*     */     }
/*     */ 
/*     */     public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException {
/* 167 */       int i = paramInt2 / this.targetChannels * this.sourceChannels;
/* 168 */       if ((this.conversion_buffer == null) || (this.conversion_buffer.length < i))
/* 169 */         this.conversion_buffer = new float[i];
/* 170 */       int j = this.ais.read(this.conversion_buffer, 0, i);
/* 171 */       if (j < 0)
/* 172 */         return j;
/*     */       int k;
/*     */       int m;
/*     */       int i1;
/*     */       int i2;
/* 173 */       if (this.sourceChannels == 1) {
/* 174 */         k = this.targetChannels;
/* 175 */         for (m = 0; m < this.targetChannels; m++) {
/* 176 */           i1 = 0; for (i2 = paramInt1 + m; i1 < i; i2 += k) {
/* 177 */             paramArrayOfFloat[i2] = this.conversion_buffer[i1];
/*     */ 
/* 176 */             i1++;
/*     */           }
/*     */         }
/*     */       }
/* 180 */       else if (this.targetChannels == 1) {
/* 181 */         k = this.sourceChannels;
/* 182 */         m = 0; for (i1 = paramInt1; m < i; i1++) {
/* 183 */           paramArrayOfFloat[i1] = this.conversion_buffer[m];
/*     */ 
/* 182 */           m += k;
/*     */         }
/*     */ 
/* 185 */         for (m = 1; m < this.sourceChannels; m++) {
/* 186 */           i1 = m; for (i2 = paramInt1; i1 < i; i2++) {
/* 187 */             paramArrayOfFloat[i2] += this.conversion_buffer[i1];
/*     */ 
/* 186 */             i1 += k;
/*     */           }
/*     */         }
/*     */ 
/* 190 */         float f = 1.0F / this.sourceChannels;
/* 191 */         i1 = 0; for (i2 = paramInt1; i1 < i; i2++) {
/* 192 */           paramArrayOfFloat[i2] *= f;
/*     */ 
/* 191 */           i1 += k;
/*     */         }
/*     */       }
/*     */       else {
/* 195 */         k = Math.min(this.sourceChannels, this.targetChannels);
/* 196 */         int n = paramInt1 + paramInt2;
/* 197 */         i1 = this.targetChannels;
/* 198 */         i2 = this.sourceChannels;
/*     */         int i4;
/* 199 */         for (int i3 = 0; i3 < k; i3++) {
/* 200 */           i4 = paramInt1 + i3; for (int i5 = i3; i4 < n; i5 += i2) {
/* 201 */             paramArrayOfFloat[i4] = this.conversion_buffer[i5];
/*     */ 
/* 200 */             i4 += i1;
/*     */           }
/*     */         }
/*     */ 
/* 204 */         for (i3 = k; i3 < this.targetChannels; i3++) {
/* 205 */           for (i4 = paramInt1 + i3; i4 < n; i4 += i1) {
/* 206 */             paramArrayOfFloat[i4] = 0.0F;
/*     */           }
/*     */         }
/*     */       }
/* 210 */       return j / this.sourceChannels * this.targetChannels;
/*     */     }
/*     */ 
/*     */     public void reset() throws IOException {
/* 214 */       this.ais.reset();
/*     */     }
/*     */ 
/*     */     public long skip(long paramLong) throws IOException {
/* 218 */       long l = this.ais.skip(paramLong / this.targetChannels * this.sourceChannels);
/* 219 */       if (l < 0L)
/* 220 */         return l;
/* 221 */       return l / this.sourceChannels * this.targetChannels;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AudioFloatInputStreamResampler extends AudioFloatInputStream
/*     */   {
/*     */     private AudioFloatInputStream ais;
/*     */     private AudioFormat targetFormat;
/*     */     private float[] skipbuffer;
/*     */     private SoftAbstractResampler resampler;
/* 237 */     private float[] pitch = new float[1];
/*     */     private float[] ibuffer2;
/*     */     private float[][] ibuffer;
/* 243 */     private float ibuffer_index = 0.0F;
/*     */ 
/* 245 */     private int ibuffer_len = 0;
/*     */ 
/* 247 */     private int nrofchannels = 0;
/*     */     private float[][] cbuffer;
/* 251 */     private int buffer_len = 512;
/*     */     private int pad;
/*     */     private int pad2;
/* 257 */     private float[] ix = new float[1];
/*     */ 
/* 259 */     private int[] ox = new int[1];
/*     */ 
/* 261 */     private float[][] mark_ibuffer = (float[][])null;
/*     */ 
/* 263 */     private float mark_ibuffer_index = 0.0F;
/*     */ 
/* 265 */     private int mark_ibuffer_len = 0;
/*     */ 
/*     */     public AudioFloatInputStreamResampler(AudioFloatInputStream paramAudioFloatInputStream, AudioFormat paramAudioFormat)
/*     */     {
/* 269 */       this.ais = paramAudioFloatInputStream;
/* 270 */       AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
/* 271 */       this.targetFormat = new AudioFormat(localAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), localAudioFormat.getChannels(), localAudioFormat.getFrameSize(), paramAudioFormat.getSampleRate(), localAudioFormat.isBigEndian());
/*     */ 
/* 275 */       this.nrofchannels = this.targetFormat.getChannels();
/* 276 */       Object localObject = paramAudioFormat.getProperty("interpolation");
/* 277 */       if ((localObject != null) && ((localObject instanceof String))) {
/* 278 */         String str = (String)localObject;
/* 279 */         if (str.equalsIgnoreCase("point"))
/* 280 */           this.resampler = new SoftPointResampler();
/* 281 */         if (str.equalsIgnoreCase("linear"))
/* 282 */           this.resampler = new SoftLinearResampler2();
/* 283 */         if (str.equalsIgnoreCase("linear1"))
/* 284 */           this.resampler = new SoftLinearResampler();
/* 285 */         if (str.equalsIgnoreCase("linear2"))
/* 286 */           this.resampler = new SoftLinearResampler2();
/* 287 */         if (str.equalsIgnoreCase("cubic"))
/* 288 */           this.resampler = new SoftCubicResampler();
/* 289 */         if (str.equalsIgnoreCase("lanczos"))
/* 290 */           this.resampler = new SoftLanczosResampler();
/* 291 */         if (str.equalsIgnoreCase("sinc"))
/* 292 */           this.resampler = new SoftSincResampler();
/*     */       }
/* 294 */       if (this.resampler == null) {
/* 295 */         this.resampler = new SoftLinearResampler2();
/*     */       }
/* 297 */       this.pitch[0] = (localAudioFormat.getSampleRate() / paramAudioFormat.getSampleRate());
/* 298 */       this.pad = this.resampler.getPadding();
/* 299 */       this.pad2 = (this.pad * 2);
/* 300 */       this.ibuffer = new float[this.nrofchannels][this.buffer_len + this.pad2];
/* 301 */       this.ibuffer2 = new float[this.nrofchannels * this.buffer_len];
/* 302 */       this.ibuffer_index = (this.buffer_len + this.pad);
/* 303 */       this.ibuffer_len = this.buffer_len;
/*     */     }
/*     */ 
/*     */     public int available() throws IOException {
/* 307 */       return 0;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException {
/* 311 */       this.ais.close();
/*     */     }
/*     */ 
/*     */     public AudioFormat getFormat() {
/* 315 */       return this.targetFormat;
/*     */     }
/*     */ 
/*     */     public long getFrameLength() {
/* 319 */       return -1L;
/*     */     }
/*     */ 
/*     */     public void mark(int paramInt) {
/* 323 */       this.ais.mark((int)(paramInt * this.pitch[0]));
/* 324 */       this.mark_ibuffer_index = this.ibuffer_index;
/* 325 */       this.mark_ibuffer_len = this.ibuffer_len;
/* 326 */       if (this.mark_ibuffer == null) {
/* 327 */         this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length];
/*     */       }
/* 329 */       for (int i = 0; i < this.ibuffer.length; i++) {
/* 330 */         float[] arrayOfFloat1 = this.ibuffer[i];
/* 331 */         float[] arrayOfFloat2 = this.mark_ibuffer[i];
/* 332 */         for (int j = 0; j < arrayOfFloat2.length; j++)
/* 333 */           arrayOfFloat2[j] = arrayOfFloat1[j];
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean markSupported()
/*     */     {
/* 339 */       return this.ais.markSupported();
/*     */     }
/*     */ 
/*     */     private void readNextBuffer() throws IOException
/*     */     {
/* 344 */       if (this.ibuffer_len == -1)
/*     */         return;
/*     */       int m;
/*     */       int n;
/* 347 */       for (int i = 0; i < this.nrofchannels; i++) {
/* 348 */         float[] arrayOfFloat1 = this.ibuffer[i];
/* 349 */         int k = this.ibuffer_len + this.pad2;
/* 350 */         m = this.ibuffer_len; for (n = 0; m < k; n++) {
/* 351 */           arrayOfFloat1[n] = arrayOfFloat1[m];
/*     */ 
/* 350 */           m++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 355 */       this.ibuffer_index -= this.ibuffer_len;
/*     */ 
/* 357 */       this.ibuffer_len = this.ais.read(this.ibuffer2);
/* 358 */       if (this.ibuffer_len >= 0) {
/* 359 */         while (this.ibuffer_len < this.ibuffer2.length) {
/* 360 */           i = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
/*     */ 
/* 362 */           if (i == -1)
/*     */             break;
/* 364 */           this.ibuffer_len += i;
/*     */         }
/* 366 */         Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
/* 367 */         this.ibuffer_len /= this.nrofchannels;
/*     */       } else {
/* 369 */         Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
/*     */       }
/*     */ 
/* 372 */       i = this.ibuffer2.length;
/* 373 */       for (int j = 0; j < this.nrofchannels; j++) {
/* 374 */         float[] arrayOfFloat2 = this.ibuffer[j];
/* 375 */         m = j; for (n = this.pad2; m < i; n++) {
/* 376 */           arrayOfFloat2[n] = this.ibuffer2[m];
/*     */ 
/* 375 */           m += this.nrofchannels;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
/*     */       throws IOException
/*     */     {
/* 384 */       if ((this.cbuffer == null) || (this.cbuffer[0].length < paramInt2 / this.nrofchannels)) {
/* 385 */         this.cbuffer = new float[this.nrofchannels][paramInt2 / this.nrofchannels];
/*     */       }
/* 387 */       if (this.ibuffer_len == -1)
/* 388 */         return -1;
/* 389 */       if (paramInt2 < 0)
/* 390 */         return 0;
/* 391 */       int i = paramInt1 + paramInt2;
/* 392 */       int j = paramInt2 / this.nrofchannels;
/* 393 */       int k = 0;
/* 394 */       int m = this.ibuffer_len;
/*     */       int i1;
/*     */       float[] arrayOfFloat;
/* 395 */       while (j > 0) {
/* 396 */         if (this.ibuffer_len >= 0) {
/* 397 */           if (this.ibuffer_index >= this.ibuffer_len + this.pad)
/* 398 */             readNextBuffer();
/* 399 */           m = this.ibuffer_len + this.pad;
/*     */         }
/*     */ 
/* 402 */         if (this.ibuffer_len < 0) {
/* 403 */           m = this.pad2;
/* 404 */           if (this.ibuffer_index >= m) {
/*     */             break;
/*     */           }
/*     */         }
/* 408 */         if (this.ibuffer_index < 0.0F)
/*     */           break;
/* 410 */         n = k;
/* 411 */         for (i1 = 0; i1 < this.nrofchannels; i1++) {
/* 412 */           this.ix[0] = this.ibuffer_index;
/* 413 */           this.ox[0] = k;
/* 414 */           arrayOfFloat = this.ibuffer[i1];
/* 415 */           this.resampler.interpolate(arrayOfFloat, this.ix, m, this.pitch, 0.0F, this.cbuffer[i1], this.ox, paramInt2 / this.nrofchannels);
/*     */         }
/*     */ 
/* 418 */         this.ibuffer_index = this.ix[0];
/* 419 */         k = this.ox[0];
/* 420 */         j -= k - n;
/*     */       }
/* 422 */       for (int n = 0; n < this.nrofchannels; n++) {
/* 423 */         i1 = 0;
/* 424 */         arrayOfFloat = this.cbuffer[n];
/* 425 */         for (int i2 = n + paramInt1; i2 < i; i2 += this.nrofchannels) {
/* 426 */           paramArrayOfFloat[i2] = arrayOfFloat[(i1++)];
/*     */         }
/*     */       }
/* 429 */       return paramInt2 - j * this.nrofchannels;
/*     */     }
/*     */ 
/*     */     public void reset() throws IOException {
/* 433 */       this.ais.reset();
/* 434 */       if (this.mark_ibuffer == null)
/* 435 */         return;
/* 436 */       this.ibuffer_index = this.mark_ibuffer_index;
/* 437 */       this.ibuffer_len = this.mark_ibuffer_len;
/* 438 */       for (int i = 0; i < this.ibuffer.length; i++) {
/* 439 */         float[] arrayOfFloat1 = this.mark_ibuffer[i];
/* 440 */         float[] arrayOfFloat2 = this.ibuffer[i];
/* 441 */         for (int j = 0; j < arrayOfFloat2.length; j++)
/* 442 */           arrayOfFloat2[j] = arrayOfFloat1[j];
/*     */       }
/*     */     }
/*     */ 
/*     */     public long skip(long paramLong)
/*     */       throws IOException
/*     */     {
/* 449 */       if (paramLong < 0L)
/* 450 */         return 0L;
/* 451 */       if (this.skipbuffer == null)
/* 452 */         this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()];
/* 453 */       float[] arrayOfFloat = this.skipbuffer;
/* 454 */       long l = paramLong;
/* 455 */       while (l > 0L) {
/* 456 */         int i = read(arrayOfFloat, 0, (int)Math.min(l, this.skipbuffer.length));
/*     */ 
/* 458 */         if (i < 0) {
/* 459 */           if (l != paramLong) break;
/* 460 */           return i;
/*     */         }
/*     */ 
/* 463 */         l -= i;
/*     */       }
/* 465 */       return paramLong - l;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AudioFloatFormatConverter
 * JD-Core Version:    0.6.2
 */