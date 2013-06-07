/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.sound.midi.MidiDevice.Info;
/*     */ import javax.sound.midi.MidiDeviceReceiver;
/*     */ import javax.sound.midi.MidiDeviceTransmitter;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Transmitter;
/*     */ 
/*     */ abstract class AbstractMidiDevice
/*     */   implements MidiDevice, ReferenceCountingDevice
/*     */ {
/*     */   private static final boolean TRACE_TRANSMITTER = false;
/*     */   private ArrayList<Receiver> receiverList;
/*     */   private TransmitterList transmitterList;
/*  59 */   private final Object traRecLock = new Object();
/*     */   private MidiDevice.Info info;
/*  68 */   protected boolean open = false;
/*     */   private int openRefCount;
/*     */   private List openKeepingObjects;
/*  78 */   protected long id = 0L;
/*     */ 
/*     */   protected AbstractMidiDevice(MidiDevice.Info paramInfo)
/*     */   {
/*  96 */     this.info = paramInfo;
/*  97 */     this.openRefCount = 0;
/*     */   }
/*     */ 
/*     */   public MidiDevice.Info getDeviceInfo()
/*     */   {
/* 106 */     return this.info;
/*     */   }
/*     */ 
/*     */   public void open()
/*     */     throws MidiUnavailableException
/*     */   {
/* 116 */     synchronized (this) {
/* 117 */       this.openRefCount = -1;
/* 118 */       doOpen();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openInternal(Object paramObject)
/*     */     throws MidiUnavailableException
/*     */   {
/* 138 */     synchronized (this) {
/* 139 */       if (this.openRefCount != -1) {
/* 140 */         this.openRefCount += 1;
/* 141 */         getOpenKeepingObjects().add(paramObject);
/*     */       }
/*     */ 
/* 144 */       doOpen();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doOpen()
/*     */     throws MidiUnavailableException
/*     */   {
/* 152 */     synchronized (this) {
/* 153 */       if (!isOpen()) {
/* 154 */         implOpen();
/* 155 */         this.open = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 164 */     synchronized (this) {
/* 165 */       doClose();
/* 166 */       this.openRefCount = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void closeInternal(Object paramObject)
/*     */   {
/* 186 */     synchronized (this) {
/* 187 */       if ((getOpenKeepingObjects().remove(paramObject)) && 
/* 188 */         (this.openRefCount > 0)) {
/* 189 */         this.openRefCount -= 1;
/* 190 */         if (this.openRefCount == 0)
/* 191 */           doClose();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doClose()
/*     */   {
/* 202 */     synchronized (this) {
/* 203 */       if (isOpen()) {
/* 204 */         implClose();
/* 205 */         this.open = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 213 */     return this.open;
/*     */   }
/*     */ 
/*     */   protected void implClose()
/*     */   {
/* 218 */     synchronized (this.traRecLock) {
/* 219 */       if (this.receiverList != null)
/*     */       {
/* 221 */         for (int i = 0; i < this.receiverList.size(); i++) {
/* 222 */           ((Receiver)this.receiverList.get(i)).close();
/*     */         }
/* 224 */         this.receiverList.clear();
/*     */       }
/* 226 */       if (this.transmitterList != null)
/*     */       {
/* 228 */         this.transmitterList.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getMicrosecondPosition()
/*     */   {
/* 240 */     return -1L;
/*     */   }
/*     */ 
/*     */   public final int getMaxReceivers()
/*     */   {
/* 249 */     if (hasReceivers()) {
/* 250 */       return -1;
/*     */     }
/* 252 */     return 0;
/*     */   }
/*     */ 
/*     */   public final int getMaxTransmitters()
/*     */   {
/* 262 */     if (hasTransmitters()) {
/* 263 */       return -1;
/*     */     }
/* 265 */     return 0;
/*     */   }
/*     */ 
/*     */   public final Receiver getReceiver()
/*     */     throws MidiUnavailableException
/*     */   {
/*     */     Receiver localReceiver;
/* 279 */     synchronized (this.traRecLock) {
/* 280 */       localReceiver = createReceiver();
/* 281 */       getReceiverList().add(localReceiver);
/*     */     }
/* 283 */     return localReceiver;
/*     */   }
/*     */ 
/*     */   public final List<Receiver> getReceivers()
/*     */   {
/*     */     List localList;
/* 289 */     synchronized (this.traRecLock) {
/* 290 */       if (this.receiverList == null)
/* 291 */         localList = Collections.unmodifiableList(new ArrayList(0));
/*     */       else {
/* 293 */         localList = Collections.unmodifiableList((List)this.receiverList.clone());
/*     */       }
/*     */     }
/*     */ 
/* 297 */     return localList;
/*     */   }
/*     */ 
/*     */   public final Transmitter getTransmitter()
/*     */     throws MidiUnavailableException
/*     */   {
/*     */     Transmitter localTransmitter;
/* 308 */     synchronized (this.traRecLock) {
/* 309 */       localTransmitter = createTransmitter();
/* 310 */       getTransmitterList().add(localTransmitter);
/*     */     }
/* 312 */     return localTransmitter;
/*     */   }
/*     */ 
/*     */   public final List<Transmitter> getTransmitters()
/*     */   {
/*     */     List localList;
/* 318 */     synchronized (this.traRecLock) {
/* 319 */       if ((this.transmitterList == null) || (this.transmitterList.transmitters.size() == 0))
/*     */       {
/* 321 */         localList = Collections.unmodifiableList(new ArrayList(0));
/*     */       }
/* 323 */       else localList = Collections.unmodifiableList((List)this.transmitterList.transmitters.clone());
/*     */     }
/*     */ 
/* 326 */     return localList;
/*     */   }
/*     */ 
/*     */   long getId()
/*     */   {
/* 333 */     return this.id;
/*     */   }
/*     */ 
/*     */   public Receiver getReceiverReferenceCounting()
/*     */     throws MidiUnavailableException
/*     */   {
/*     */     Receiver localReceiver;
/* 347 */     synchronized (this.traRecLock) {
/* 348 */       localReceiver = getReceiver();
/* 349 */       openInternal(localReceiver);
/*     */     }
/* 351 */     return localReceiver;
/*     */   }
/*     */ 
/*     */   public Transmitter getTransmitterReferenceCounting()
/*     */     throws MidiUnavailableException
/*     */   {
/*     */     Transmitter localTransmitter;
/* 363 */     synchronized (this.traRecLock) {
/* 364 */       localTransmitter = getTransmitter();
/* 365 */       openInternal(localTransmitter);
/*     */     }
/* 367 */     return localTransmitter;
/*     */   }
/*     */ 
/*     */   private synchronized List getOpenKeepingObjects()
/*     */   {
/* 374 */     if (this.openKeepingObjects == null) {
/* 375 */       this.openKeepingObjects = new ArrayList();
/*     */     }
/* 377 */     return this.openKeepingObjects;
/*     */   }
/*     */ 
/*     */   private List<Receiver> getReceiverList()
/*     */   {
/* 388 */     synchronized (this.traRecLock) {
/* 389 */       if (this.receiverList == null) {
/* 390 */         this.receiverList = new ArrayList();
/*     */       }
/*     */     }
/* 393 */     return this.receiverList;
/*     */   }
/*     */ 
/*     */   protected boolean hasReceivers()
/*     */   {
/* 404 */     return false;
/*     */   }
/*     */ 
/*     */   protected Receiver createReceiver()
/*     */     throws MidiUnavailableException
/*     */   {
/* 416 */     throw new MidiUnavailableException("MIDI IN receiver not available");
/*     */   }
/*     */ 
/*     */   protected TransmitterList getTransmitterList()
/*     */   {
/* 426 */     synchronized (this.traRecLock) {
/* 427 */       if (this.transmitterList == null) {
/* 428 */         this.transmitterList = new TransmitterList();
/*     */       }
/*     */     }
/* 431 */     return this.transmitterList;
/*     */   }
/*     */ 
/*     */   protected boolean hasTransmitters()
/*     */   {
/* 442 */     return false;
/*     */   }
/*     */ 
/*     */   protected Transmitter createTransmitter()
/*     */     throws MidiUnavailableException
/*     */   {
/* 454 */     throw new MidiUnavailableException("MIDI OUT transmitter not available");
/*     */   }
/*     */ 
/*     */   protected abstract void implOpen()
/*     */     throws MidiUnavailableException;
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 466 */     close();
/*     */   }
/*     */ 
/*     */   abstract class AbstractReceiver
/*     */     implements MidiDeviceReceiver
/*     */   {
/* 478 */     private boolean open = true;
/*     */ 
/*     */     AbstractReceiver()
/*     */     {
/*     */     }
/*     */ 
/*     */     public final synchronized void send(MidiMessage paramMidiMessage, long paramLong)
/*     */     {
/* 489 */       if (!this.open) {
/* 490 */         throw new IllegalStateException("Receiver is not open");
/*     */       }
/* 492 */       implSend(paramMidiMessage, paramLong);
/*     */     }
/*     */ 
/*     */     abstract void implSend(MidiMessage paramMidiMessage, long paramLong);
/*     */ 
/*     */     public final void close()
/*     */     {
/* 504 */       this.open = false;
/* 505 */       synchronized (AbstractMidiDevice.this.traRecLock) {
/* 506 */         AbstractMidiDevice.this.getReceiverList().remove(this);
/*     */       }
/* 508 */       AbstractMidiDevice.this.closeInternal(this);
/*     */     }
/*     */ 
/*     */     public final MidiDevice getMidiDevice()
/*     */     {
/* 513 */       return AbstractMidiDevice.this;
/*     */     }
/*     */ 
/*     */     final boolean isOpen() {
/* 517 */       return this.open;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class BasicTransmitter
/*     */     implements MidiDeviceTransmitter
/*     */   {
/* 539 */     private Receiver receiver = null;
/* 540 */     AbstractMidiDevice.TransmitterList tlist = null;
/*     */ 
/*     */     protected BasicTransmitter() {
/*     */     }
/*     */ 
/*     */     private void setTransmitterList(AbstractMidiDevice.TransmitterList paramTransmitterList) {
/* 546 */       this.tlist = paramTransmitterList;
/*     */     }
/*     */ 
/*     */     public void setReceiver(Receiver paramReceiver) {
/* 550 */       if ((this.tlist != null) && (this.receiver != paramReceiver))
/*     */       {
/* 552 */         AbstractMidiDevice.TransmitterList.access$400(this.tlist, this, this.receiver, paramReceiver);
/* 553 */         this.receiver = paramReceiver;
/*     */       }
/*     */     }
/*     */ 
/*     */     public Receiver getReceiver() {
/* 558 */       return this.receiver;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */     {
/* 568 */       AbstractMidiDevice.this.closeInternal(this);
/* 569 */       if (this.tlist != null) {
/* 570 */         AbstractMidiDevice.TransmitterList.access$400(this.tlist, this, this.receiver, null);
/* 571 */         AbstractMidiDevice.TransmitterList.access$500(this.tlist, this);
/* 572 */         this.tlist = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public MidiDevice getMidiDevice() {
/* 577 */       return AbstractMidiDevice.this;
/*     */     }
/*     */   }
/*     */ 
/*     */   class TransmitterList
/*     */   {
/* 588 */     private ArrayList<Transmitter> transmitters = new ArrayList();
/*     */     private MidiOutDevice.MidiOutReceiver midiOutReceiver;
/* 593 */     private int optimizedReceiverCount = 0;
/*     */ 
/*     */     TransmitterList() {
/*     */     }
/* 597 */     private void add(Transmitter paramTransmitter) { synchronized (this.transmitters) {
/* 598 */         this.transmitters.add(paramTransmitter);
/*     */       }
/* 600 */       if ((paramTransmitter instanceof AbstractMidiDevice.BasicTransmitter))
/* 601 */         ((AbstractMidiDevice.BasicTransmitter)paramTransmitter).setTransmitterList(this);
/*     */     }
/*     */ 
/*     */     private void remove(Transmitter paramTransmitter)
/*     */     {
/* 607 */       synchronized (this.transmitters) {
/* 608 */         int i = this.transmitters.indexOf(paramTransmitter);
/* 609 */         if (i >= 0)
/* 610 */           this.transmitters.remove(i);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void receiverChanged(AbstractMidiDevice.BasicTransmitter paramBasicTransmitter, Receiver paramReceiver1, Receiver paramReceiver2)
/*     */     {
/* 619 */       synchronized (this.transmitters)
/*     */       {
/* 621 */         if (this.midiOutReceiver == paramReceiver1) {
/* 622 */           this.midiOutReceiver = null;
/*     */         }
/* 624 */         if ((paramReceiver2 != null) && 
/* 625 */           ((paramReceiver2 instanceof MidiOutDevice.MidiOutReceiver)) && (this.midiOutReceiver == null))
/*     */         {
/* 627 */           this.midiOutReceiver = ((MidiOutDevice.MidiOutReceiver)paramReceiver2);
/*     */         }
/*     */ 
/* 630 */         this.optimizedReceiverCount = (this.midiOutReceiver != null ? 1 : 0);
/*     */       }
/*     */     }
/*     */ 
/*     */     void close()
/*     */     {
/* 639 */       synchronized (this.transmitters) {
/* 640 */         for (int i = 0; i < this.transmitters.size(); i++) {
/* 641 */           ((Transmitter)this.transmitters.get(i)).close();
/*     */         }
/* 643 */         this.transmitters.clear();
/*     */       }
/*     */     }
/*     */ 
/*     */     void sendMessage(int paramInt, long paramLong)
/*     */     {
/*     */       try
/*     */       {
/* 658 */         synchronized (this.transmitters) {
/* 659 */           int i = this.transmitters.size();
/* 660 */           if (this.optimizedReceiverCount == i) {
/* 661 */             if (this.midiOutReceiver != null)
/*     */             {
/* 663 */               this.midiOutReceiver.sendPackedMidiMessage(paramInt, paramLong);
/*     */             }
/*     */           }
/*     */           else
/* 667 */             for (int j = 0; j < i; j++) {
/* 668 */               Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
/* 669 */               if (localReceiver != null)
/* 670 */                 if (this.optimizedReceiverCount > 0) {
/* 671 */                   if ((localReceiver instanceof MidiOutDevice.MidiOutReceiver))
/* 672 */                     ((MidiOutDevice.MidiOutReceiver)localReceiver).sendPackedMidiMessage(paramInt, paramLong);
/*     */                   else
/* 674 */                     localReceiver.send(new FastShortMessage(paramInt), paramLong);
/*     */                 }
/*     */                 else
/* 677 */                   localReceiver.send(new FastShortMessage(paramInt), paramLong);
/*     */             }
/*     */         }
/*     */       }
/*     */       catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     void sendMessage(byte[] paramArrayOfByte, long paramLong)
/*     */     {
/*     */       try
/*     */       {
/* 690 */         synchronized (this.transmitters) {
/* 691 */           int i = this.transmitters.size();
/*     */ 
/* 693 */           for (int j = 0; j < i; j++) {
/* 694 */             Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
/* 695 */             if (localReceiver != null)
/*     */             {
/* 701 */               localReceiver.send(new FastSysexMessage(paramArrayOfByte), paramLong);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     void sendMessage(MidiMessage paramMidiMessage, long paramLong)
/*     */     {
/* 716 */       if ((paramMidiMessage instanceof FastShortMessage)) {
/* 717 */         sendMessage(((FastShortMessage)paramMidiMessage).getPackedMsg(), paramLong);
/* 718 */         return;
/*     */       }
/* 720 */       synchronized (this.transmitters) {
/* 721 */         int i = this.transmitters.size();
/* 722 */         if (this.optimizedReceiverCount == i) {
/* 723 */           if (this.midiOutReceiver != null)
/*     */           {
/* 725 */             this.midiOutReceiver.send(paramMidiMessage, paramLong);
/*     */           }
/*     */         }
/*     */         else
/* 729 */           for (int j = 0; j < i; j++) {
/* 730 */             Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
/* 731 */             if (localReceiver != null)
/*     */             {
/* 739 */               localReceiver.send(paramMidiMessage, paramLong);
/*     */             }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.AbstractMidiDevice
 * JD-Core Version:    0.6.2
 */