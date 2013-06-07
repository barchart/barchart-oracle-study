/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.applet.AudioClip;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaEventListener;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiFileFormat;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.Clip;
/*     */ import javax.sound.sampled.DataLine.Info;
/*     */ import javax.sound.sampled.Line;
/*     */ import javax.sound.sampled.LineEvent;
/*     */ import javax.sound.sampled.LineListener;
/*     */ import javax.sound.sampled.SourceDataLine;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class JavaSoundAudioClip
/*     */   implements AudioClip, MetaEventListener, LineListener
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final int BUFFER_SIZE = 16384;
/*  71 */   private long lastPlayCall = 0L;
/*     */   private static final int MINIMUM_PLAY_DELAY = 30;
/*  74 */   private byte[] loadedAudio = null;
/*  75 */   private int loadedAudioByteLength = 0;
/*  76 */   private AudioFormat loadedAudioFormat = null;
/*     */ 
/*  78 */   private AutoClosingClip clip = null;
/*  79 */   private boolean clipLooping = false;
/*     */ 
/*  81 */   private DataPusher datapusher = null;
/*     */ 
/*  83 */   private Sequencer sequencer = null;
/*  84 */   private Sequence sequence = null;
/*  85 */   private boolean sequencerloop = false;
/*     */   private static final long CLIP_THRESHOLD = 1048576L;
/*     */   private static final int STREAM_BUFFER_SIZE = 1024;
/*     */ 
/*     */   public JavaSoundAudioClip(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 104 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream, 1024);
/* 105 */     localBufferedInputStream.mark(1024);
/* 106 */     boolean bool = false;
/*     */     try {
/* 108 */       AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(localBufferedInputStream);
/*     */ 
/* 110 */       bool = loadAudioData(localAudioInputStream);
/*     */ 
/* 112 */       if (bool) {
/* 113 */         bool = false;
/* 114 */         if (this.loadedAudioByteLength < 1048576L) {
/* 115 */           bool = createClip();
/*     */         }
/* 117 */         if (!bool)
/* 118 */           bool = createSourceDataLine();
/*     */       }
/*     */     }
/*     */     catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
/*     */     {
/*     */       try {
/* 124 */         MidiFileFormat localMidiFileFormat = MidiSystem.getMidiFileFormat(localBufferedInputStream);
/* 125 */         bool = createSequencer(localBufferedInputStream);
/*     */       } catch (InvalidMidiDataException localInvalidMidiDataException) {
/* 127 */         bool = false;
/*     */       }
/*     */     }
/* 130 */     if (!bool)
/* 131 */       throw new IOException("Unable to create AudioClip from input stream");
/*     */   }
/*     */ 
/*     */   public synchronized void play()
/*     */   {
/* 137 */     startImpl(false);
/*     */   }
/*     */ 
/*     */   public synchronized void loop()
/*     */   {
/* 142 */     startImpl(true);
/*     */   }
/*     */ 
/*     */   private synchronized void startImpl(boolean paramBoolean)
/*     */   {
/* 147 */     long l1 = System.currentTimeMillis();
/* 148 */     long l2 = l1 - this.lastPlayCall;
/* 149 */     if (l2 < 30L)
/*     */     {
/* 151 */       return;
/*     */     }
/* 153 */     this.lastPlayCall = l1;
/*     */     try
/*     */     {
/* 157 */       if (this.clip != null) {
/* 158 */         if (!this.clip.isOpen())
/*     */         {
/* 160 */           this.clip.open(this.loadedAudioFormat, this.loadedAudio, 0, this.loadedAudioByteLength);
/*     */         }
/*     */         else {
/* 163 */           this.clip.flush();
/* 164 */           if (paramBoolean != this.clipLooping)
/*     */           {
/* 167 */             this.clip.stop();
/*     */           }
/*     */         }
/* 170 */         this.clip.setFramePosition(0);
/* 171 */         if (paramBoolean)
/*     */         {
/* 173 */           this.clip.loop(-1);
/*     */         }
/*     */         else {
/* 176 */           this.clip.start();
/*     */         }
/* 178 */         this.clipLooping = paramBoolean;
/*     */       }
/* 181 */       else if (this.datapusher != null) {
/* 182 */         this.datapusher.start(paramBoolean);
/*     */       }
/* 185 */       else if (this.sequencer != null) {
/* 186 */         this.sequencerloop = paramBoolean;
/* 187 */         if (this.sequencer.isRunning()) {
/* 188 */           this.sequencer.setMicrosecondPosition(0L);
/*     */         }
/* 190 */         if (!this.sequencer.isOpen())
/*     */           try {
/* 192 */             this.sequencer.open();
/* 193 */             this.sequencer.setSequence(this.sequence);
/*     */           }
/*     */           catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */           {
/*     */           }
/*     */           catch (MidiUnavailableException localMidiUnavailableException)
/*     */           {
/*     */           }
/* 201 */         this.sequencer.addMetaEventListener(this);
/*     */         try {
/* 203 */           this.sequencer.start();
/*     */         }
/*     */         catch (Exception localException1)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localException2)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void stop()
/*     */   {
/* 217 */     this.lastPlayCall = 0L;
/*     */ 
/* 219 */     if (this.clip != null)
/*     */     {
/*     */       try {
/* 222 */         this.clip.flush();
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/*     */       }
/*     */       try {
/* 228 */         this.clip.stop();
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/*     */       }
/*     */     }
/* 234 */     else if (this.datapusher != null) {
/* 235 */       this.datapusher.stop();
/*     */     }
/* 238 */     else if (this.sequencer != null) {
/*     */       try {
/* 240 */         this.sequencerloop = false;
/* 241 */         this.sequencer.addMetaEventListener(this);
/* 242 */         this.sequencer.stop();
/*     */       }
/*     */       catch (Exception localException3) {
/*     */       }
/*     */       try {
/* 247 */         this.sequencer.close();
/*     */       }
/*     */       catch (Exception localException4)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void update(LineEvent paramLineEvent)
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void meta(MetaMessage paramMetaMessage)
/*     */   {
/* 267 */     if (paramMetaMessage.getType() == 47)
/* 268 */       if (this.sequencerloop)
/*     */       {
/* 270 */         this.sequencer.setMicrosecondPosition(0L);
/* 271 */         loop();
/*     */       } else {
/* 273 */         stop();
/*     */       }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 280 */     return getClass().toString();
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 286 */     if (this.clip != null)
/*     */     {
/* 288 */       this.clip.close();
/*     */     }
/*     */ 
/* 292 */     if (this.datapusher != null) {
/* 293 */       this.datapusher.close();
/*     */     }
/*     */ 
/* 296 */     if (this.sequencer != null)
/* 297 */       this.sequencer.close();
/*     */   }
/*     */ 
/*     */   private boolean loadAudioData(AudioInputStream paramAudioInputStream)
/*     */     throws IOException, UnsupportedAudioFileException
/*     */   {
/* 307 */     paramAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(paramAudioInputStream);
/* 308 */     if (paramAudioInputStream == null) {
/* 309 */       return false;
/*     */     }
/*     */ 
/* 312 */     this.loadedAudioFormat = paramAudioInputStream.getFormat();
/* 313 */     long l1 = paramAudioInputStream.getFrameLength();
/* 314 */     int i = this.loadedAudioFormat.getFrameSize();
/* 315 */     long l2 = -1L;
/* 316 */     if ((l1 != -1L) && (l1 > 0L) && (i != -1) && (i > 0))
/*     */     {
/* 320 */       l2 = l1 * i;
/*     */     }
/* 322 */     if (l2 != -1L)
/*     */     {
/* 324 */       readStream(paramAudioInputStream, l2);
/*     */     }
/*     */     else {
/* 327 */       readStream(paramAudioInputStream);
/*     */     }
/*     */ 
/* 332 */     return true;
/*     */   }
/*     */ 
/*     */   private void readStream(AudioInputStream paramAudioInputStream, long paramLong)
/*     */     throws IOException
/*     */   {
/*     */     int i;
/* 340 */     if (paramLong > 2147483647L)
/* 341 */       i = 2147483647;
/*     */     else {
/* 343 */       i = (int)paramLong;
/*     */     }
/* 345 */     this.loadedAudio = new byte[i];
/* 346 */     this.loadedAudioByteLength = 0;
/*     */     while (true)
/*     */     {
/* 350 */       int j = paramAudioInputStream.read(this.loadedAudio, this.loadedAudioByteLength, i - this.loadedAudioByteLength);
/* 351 */       if (j <= 0) {
/* 352 */         paramAudioInputStream.close();
/* 353 */         break;
/*     */       }
/* 355 */       this.loadedAudioByteLength += j;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readStream(AudioInputStream paramAudioInputStream) throws IOException
/*     */   {
/* 361 */     DirectBAOS localDirectBAOS = new DirectBAOS();
/* 362 */     byte[] arrayOfByte = new byte[16384];
/* 363 */     int i = 0;
/* 364 */     int j = 0;
/*     */     while (true)
/*     */     {
/* 368 */       i = paramAudioInputStream.read(arrayOfByte, 0, arrayOfByte.length);
/* 369 */       if (i <= 0) {
/* 370 */         paramAudioInputStream.close();
/* 371 */         break;
/*     */       }
/* 373 */       j += i;
/* 374 */       localDirectBAOS.write(arrayOfByte, 0, i);
/*     */     }
/* 376 */     this.loadedAudio = localDirectBAOS.getInternalBuffer();
/* 377 */     this.loadedAudioByteLength = j;
/*     */   }
/*     */ 
/*     */   private boolean createClip()
/*     */   {
/*     */     try
/*     */     {
/* 388 */       DataLine.Info localInfo = new DataLine.Info(Clip.class, this.loadedAudioFormat);
/* 389 */       if (!AudioSystem.isLineSupported(localInfo))
/*     */       {
/* 392 */         return false;
/*     */       }
/* 394 */       Line localLine = AudioSystem.getLine(localInfo);
/* 395 */       if (!(localLine instanceof AutoClosingClip))
/*     */       {
/* 398 */         return false;
/*     */       }
/* 400 */       this.clip = ((AutoClosingClip)localLine);
/* 401 */       this.clip.setAutoClosing(true);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 406 */       return false;
/*     */     }
/*     */ 
/* 409 */     if (this.clip == null)
/*     */     {
/* 411 */       return false;
/*     */     }
/*     */ 
/* 415 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean createSourceDataLine()
/*     */   {
/*     */     try {
/* 421 */       DataLine.Info localInfo = new DataLine.Info(SourceDataLine.class, this.loadedAudioFormat);
/* 422 */       if (!AudioSystem.isLineSupported(localInfo))
/*     */       {
/* 425 */         return false;
/*     */       }
/* 427 */       SourceDataLine localSourceDataLine = (SourceDataLine)AudioSystem.getLine(localInfo);
/* 428 */       this.datapusher = new DataPusher(localSourceDataLine, this.loadedAudioFormat, this.loadedAudio, this.loadedAudioByteLength);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 432 */       return false;
/*     */     }
/*     */ 
/* 435 */     if (this.datapusher == null)
/*     */     {
/* 437 */       return false;
/*     */     }
/*     */ 
/* 441 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean createSequencer(BufferedInputStream paramBufferedInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 450 */       this.sequencer = MidiSystem.getSequencer();
/*     */     }
/*     */     catch (MidiUnavailableException localMidiUnavailableException) {
/* 453 */       return false;
/*     */     }
/* 455 */     if (this.sequencer == null) {
/* 456 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 460 */       this.sequence = MidiSystem.getSequence(paramBufferedInputStream);
/* 461 */       if (this.sequence == null)
/* 462 */         return false;
/*     */     }
/*     */     catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */     {
/* 466 */       return false;
/*     */     }
/*     */ 
/* 470 */     return true;
/*     */   }
/*     */ 
/*     */   private static class DirectBAOS extends ByteArrayOutputStream
/*     */   {
/*     */     public byte[] getInternalBuffer()
/*     */     {
/* 484 */       return this.buf;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.JavaSoundAudioClip
 * JD-Core Version:    0.6.2
 */