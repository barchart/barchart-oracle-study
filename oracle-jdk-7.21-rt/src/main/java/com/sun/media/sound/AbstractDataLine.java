/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.Control;
/*     */ import javax.sound.sampled.DataLine;
/*     */ import javax.sound.sampled.DataLine.Info;
/*     */ import javax.sound.sampled.LineEvent;
/*     */ import javax.sound.sampled.LineEvent.Type;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ 
/*     */ abstract class AbstractDataLine extends AbstractLine
/*     */   implements DataLine
/*     */ {
/*     */   protected AudioFormat defaultFormat;
/*     */   protected int defaultBufferSize;
/*  55 */   protected Object lock = new Object();
/*     */   protected AudioFormat format;
/*     */   protected int bufferSize;
/*  65 */   protected boolean running = false;
/*  66 */   private boolean started = false;
/*  67 */   private boolean active = false;
/*     */ 
/*     */   protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl)
/*     */   {
/*  74 */     this(paramInfo, paramAbstractMixer, paramArrayOfControl, null, -1);
/*     */   }
/*     */ 
/*     */   protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl, AudioFormat paramAudioFormat, int paramInt)
/*     */   {
/*  82 */     super(paramInfo, paramAbstractMixer, paramArrayOfControl);
/*     */ 
/*  85 */     if (paramAudioFormat != null) {
/*  86 */       this.defaultFormat = paramAudioFormat;
/*     */     }
/*     */     else {
/*  89 */       this.defaultFormat = new AudioFormat(44100.0F, 16, 2, true, Platform.isBigEndian());
/*     */     }
/*  91 */     if (paramInt > 0) {
/*  92 */       this.defaultBufferSize = paramInt;
/*     */     }
/*     */     else {
/*  95 */       this.defaultBufferSize = ((int)(this.defaultFormat.getFrameRate() / 2.0F) * this.defaultFormat.getFrameSize());
/*     */     }
/*     */ 
/*  99 */     this.format = this.defaultFormat;
/* 100 */     this.bufferSize = this.defaultBufferSize;
/*     */   }
/*     */ 
/*     */   public void open(AudioFormat paramAudioFormat, int paramInt)
/*     */     throws LineUnavailableException
/*     */   {
/* 108 */     synchronized (this.mixer)
/*     */     {
/* 112 */       if (!isOpen())
/*     */       {
/* 115 */         Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
/*     */ 
/* 120 */         this.mixer.open(this);
/*     */         try
/*     */         {
/* 124 */           implOpen(paramAudioFormat, paramInt);
/*     */ 
/* 127 */           setOpen(true);
/*     */         }
/*     */         catch (LineUnavailableException localLineUnavailableException)
/*     */         {
/* 131 */           this.mixer.close(this);
/* 132 */           throw localLineUnavailableException;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 140 */         if (!paramAudioFormat.matches(getFormat())) {
/* 141 */           throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize());
/*     */         }
/*     */ 
/* 145 */         if (paramInt > 0)
/* 146 */           setBufferSize(paramInt);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void open(AudioFormat paramAudioFormat)
/*     */     throws LineUnavailableException
/*     */   {
/* 156 */     open(paramAudioFormat, -1);
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/* 164 */     return 0;
/*     */   }
/*     */ 
/*     */   public void drain()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 186 */     synchronized (this.mixer)
/*     */     {
/* 190 */       if (isOpen())
/*     */       {
/* 192 */         if (!isStartedRunning()) {
/* 193 */           this.mixer.start(this);
/* 194 */           implStart();
/* 195 */           this.running = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 200 */     synchronized (this.lock) {
/* 201 */       this.lock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 211 */     synchronized (this.mixer)
/*     */     {
/* 215 */       if (isOpen())
/*     */       {
/* 217 */         if (isStartedRunning())
/*     */         {
/* 219 */           implStop();
/* 220 */           this.mixer.stop(this);
/*     */ 
/* 222 */           this.running = false;
/*     */ 
/* 225 */           if ((this.started) && (!isActive())) {
/* 226 */             setStarted(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 232 */     synchronized (this.lock) {
/* 233 */       this.lock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 253 */     return this.started;
/*     */   }
/*     */ 
/*     */   public boolean isActive() {
/* 257 */     return this.active;
/*     */   }
/*     */ 
/*     */   public long getMicrosecondPosition()
/*     */   {
/* 263 */     long l = getLongFramePosition();
/* 264 */     if (l != -1L) {
/* 265 */       l = Toolkit.frames2micros(getFormat(), l);
/*     */     }
/* 267 */     return l;
/*     */   }
/*     */ 
/*     */   public AudioFormat getFormat()
/*     */   {
/* 272 */     return this.format;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 277 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public int setBufferSize(int paramInt)
/*     */   {
/* 284 */     return getBufferSize();
/*     */   }
/*     */ 
/*     */   public float getLevel()
/*     */   {
/* 291 */     return -1.0F;
/*     */   }
/*     */ 
/*     */   protected boolean isStartedRunning()
/*     */   {
/* 308 */     return this.running;
/*     */   }
/*     */ 
/*     */   protected void setActive(boolean paramBoolean)
/*     */   {
/* 322 */     synchronized (this)
/*     */     {
/* 327 */       if (this.active != paramBoolean)
/* 328 */         this.active = paramBoolean;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setStarted(boolean paramBoolean)
/*     */   {
/* 358 */     int i = 0;
/* 359 */     long l = getLongFramePosition();
/*     */ 
/* 361 */     synchronized (this)
/*     */     {
/* 366 */       if (this.started != paramBoolean) {
/* 367 */         this.started = paramBoolean;
/* 368 */         i = 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 375 */     if (i != 0)
/*     */     {
/* 377 */       if (paramBoolean)
/* 378 */         sendEvents(new LineEvent(this, LineEvent.Type.START, l));
/*     */       else
/* 380 */         sendEvents(new LineEvent(this, LineEvent.Type.STOP, l));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setEOM()
/*     */   {
/* 396 */     setStarted(false);
/*     */   }
/*     */ 
/*     */   public void open()
/*     */     throws LineUnavailableException
/*     */   {
/* 416 */     open(this.format, this.bufferSize);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 427 */     synchronized (this.mixer)
/*     */     {
/* 430 */       if (isOpen())
/*     */       {
/* 433 */         stop();
/*     */ 
/* 436 */         setOpen(false);
/*     */ 
/* 439 */         implClose();
/*     */ 
/* 442 */         this.mixer.close(this);
/*     */ 
/* 445 */         this.format = this.defaultFormat;
/* 446 */         this.bufferSize = this.defaultBufferSize;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract void implOpen(AudioFormat paramAudioFormat, int paramInt)
/*     */     throws LineUnavailableException;
/*     */ 
/*     */   abstract void implClose();
/*     */ 
/*     */   abstract void implStart();
/*     */ 
/*     */   abstract void implStop();
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AbstractDataLine
 * JD-Core Version:    0.6.2
 */