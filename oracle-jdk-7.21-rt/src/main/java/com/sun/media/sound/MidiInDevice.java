/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Transmitter;
/*     */ 
/*     */ class MidiInDevice extends AbstractMidiDevice
/*     */   implements Runnable
/*     */ {
/*  44 */   private Thread midiInThread = null;
/*     */ 
/*     */   MidiInDevice(AbstractMidiDeviceProvider.Info paramInfo)
/*     */   {
/*  49 */     super(paramInfo);
/*     */   }
/*     */ 
/*     */   protected synchronized void implOpen()
/*     */     throws MidiUnavailableException
/*     */   {
/*  61 */     int i = ((MidiInDeviceProvider.MidiInDeviceInfo)getDeviceInfo()).getIndex();
/*  62 */     this.id = nOpen(i);
/*     */ 
/*  64 */     if (this.id == 0L) {
/*  65 */       throw new MidiUnavailableException("Unable to open native device");
/*     */     }
/*     */ 
/*  69 */     if (this.midiInThread == null) {
/*  70 */       this.midiInThread = JSSecurityManager.createThread(this, "Java Sound MidiInDevice Thread", false, -1, true);
/*     */     }
/*     */ 
/*  77 */     nStart(this.id);
/*     */   }
/*     */ 
/*     */   protected synchronized void implClose()
/*     */   {
/*  86 */     long l = this.id;
/*  87 */     this.id = 0L;
/*     */ 
/*  89 */     super.implClose();
/*     */ 
/*  92 */     nStop(l);
/*  93 */     if (this.midiInThread != null)
/*     */       try {
/*  95 */         this.midiInThread.join(1000L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException)
/*     */       {
/*     */       }
/* 100 */     nClose(l);
/*     */   }
/*     */ 
/*     */   public long getMicrosecondPosition()
/*     */   {
/* 106 */     long l = -1L;
/* 107 */     if (isOpen()) {
/* 108 */       l = nGetTimeStamp(this.id);
/*     */     }
/* 110 */     return l;
/*     */   }
/*     */ 
/*     */   protected boolean hasTransmitters()
/*     */   {
/* 118 */     return true;
/*     */   }
/*     */ 
/*     */   protected Transmitter createTransmitter()
/*     */   {
/* 123 */     return new MidiInTransmitter(null);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 141 */     while (this.id != 0L)
/*     */     {
/* 143 */       nGetMessages(this.id);
/* 144 */       if (this.id != 0L)
/*     */         try {
/* 146 */           Thread.sleep(1L);
/*     */         }
/*     */         catch (InterruptedException localInterruptedException)
/*     */         {
/*     */         }
/*     */     }
/* 152 */     this.midiInThread = null;
/*     */   }
/*     */ 
/*     */   void callbackShortMessage(int paramInt, long paramLong)
/*     */   {
/* 164 */     if ((paramInt == 0) || (this.id == 0L)) {
/* 165 */       return;
/*     */     }
/*     */ 
/* 175 */     getTransmitterList().sendMessage(paramInt, paramLong);
/*     */   }
/*     */ 
/*     */   void callbackLongMessage(byte[] paramArrayOfByte, long paramLong) {
/* 179 */     if ((this.id == 0L) || (paramArrayOfByte == null)) {
/* 180 */       return;
/*     */     }
/* 182 */     getTransmitterList().sendMessage(paramArrayOfByte, paramLong);
/*     */   }
/*     */ 
/*     */   private native long nOpen(int paramInt)
/*     */     throws MidiUnavailableException;
/*     */ 
/*     */   private native void nClose(long paramLong);
/*     */ 
/*     */   private native void nStart(long paramLong)
/*     */     throws MidiUnavailableException;
/*     */ 
/*     */   private native void nStop(long paramLong);
/*     */ 
/*     */   private native long nGetTimeStamp(long paramLong);
/*     */ 
/*     */   private native void nGetMessages(long paramLong);
/*     */ 
/*     */   private class MidiInTransmitter extends AbstractMidiDevice.BasicTransmitter
/*     */   {
/*     */     private MidiInTransmitter()
/*     */     {
/* 132 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.MidiInDevice
 * JD-Core Version:    0.6.2
 */