/*      */ package com.sun.media.sound;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import javax.sound.midi.ControllerEventListener;
/*      */ import javax.sound.midi.InvalidMidiDataException;
/*      */ import javax.sound.midi.MetaEventListener;
/*      */ import javax.sound.midi.MetaMessage;
/*      */ import javax.sound.midi.MidiDevice.Info;
/*      */ import javax.sound.midi.MidiEvent;
/*      */ import javax.sound.midi.MidiMessage;
/*      */ import javax.sound.midi.MidiSystem;
/*      */ import javax.sound.midi.MidiUnavailableException;
/*      */ import javax.sound.midi.Receiver;
/*      */ import javax.sound.midi.Sequence;
/*      */ import javax.sound.midi.Sequencer;
/*      */ import javax.sound.midi.Sequencer.SyncMode;
/*      */ import javax.sound.midi.ShortMessage;
/*      */ import javax.sound.midi.Synthesizer;
/*      */ import javax.sound.midi.Track;
/*      */ import javax.sound.midi.Transmitter;
/*      */ 
/*      */ class RealTimeSequencer extends AbstractMidiDevice
/*      */   implements Sequencer, AutoConnectSequencer
/*      */ {
/*      */   private static final boolean DEBUG_PUMP = false;
/*      */   private static final boolean DEBUG_PUMP_ALL = false;
/*      */   private static final EventDispatcher eventDispatcher;
/*   66 */   static final RealTimeSequencerInfo info = new RealTimeSequencerInfo(null);
/*      */ 
/*   69 */   private static Sequencer.SyncMode[] masterSyncModes = { Sequencer.SyncMode.INTERNAL_CLOCK };
/*   70 */   private static Sequencer.SyncMode[] slaveSyncModes = { Sequencer.SyncMode.NO_SYNC };
/*      */ 
/*   72 */   private static Sequencer.SyncMode masterSyncMode = Sequencer.SyncMode.INTERNAL_CLOCK;
/*   73 */   private static Sequencer.SyncMode slaveSyncMode = Sequencer.SyncMode.NO_SYNC;
/*      */ 
/*   79 */   private Sequence sequence = null;
/*      */ 
/*   87 */   private double cacheTempoMPQ = -1.0D;
/*      */ 
/*   94 */   private float cacheTempoFactor = -1.0F;
/*      */ 
/*   98 */   private boolean[] trackMuted = null;
/*      */ 
/*  100 */   private boolean[] trackSolo = null;
/*      */ 
/*  103 */   private MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
/*      */ 
/*  108 */   private boolean running = false;
/*      */   private PlayThread playThread;
/*  118 */   private boolean recording = false;
/*      */ 
/*  124 */   private List recordingTracks = new ArrayList();
/*      */ 
/*  127 */   private long loopStart = 0L;
/*  128 */   private long loopEnd = -1L;
/*  129 */   private int loopCount = 0;
/*      */ 
/*  135 */   private ArrayList metaEventListeners = new ArrayList();
/*      */ 
/*  141 */   private ArrayList controllerEventListeners = new ArrayList();
/*      */ 
/*  145 */   private boolean autoConnect = false;
/*      */ 
/*  148 */   private boolean doAutoConnectAtNextOpen = false;
/*      */ 
/*  151 */   Receiver autoConnectedReceiver = null;
/*      */ 
/*      */   protected RealTimeSequencer()
/*      */     throws MidiUnavailableException
/*      */   {
/*  164 */     super(info);
/*      */   }
/*      */ 
/*      */   public synchronized void setSequence(Sequence paramSequence)
/*      */     throws InvalidMidiDataException
/*      */   {
/*  178 */     if (paramSequence != this.sequence) {
/*  179 */       if ((this.sequence != null) && (paramSequence == null)) {
/*  180 */         setCaches();
/*  181 */         stop();
/*      */ 
/*  183 */         this.trackMuted = null;
/*  184 */         this.trackSolo = null;
/*  185 */         this.loopStart = 0L;
/*  186 */         this.loopEnd = -1L;
/*  187 */         this.loopCount = 0;
/*  188 */         if (getDataPump() != null) {
/*  189 */           getDataPump().setTickPos(0L);
/*  190 */           getDataPump().resetLoopCount();
/*      */         }
/*      */       }
/*      */ 
/*  194 */       if (this.playThread != null) {
/*  195 */         this.playThread.setSequence(paramSequence);
/*      */       }
/*      */ 
/*  200 */       this.sequence = paramSequence;
/*      */ 
/*  202 */       if (paramSequence != null) {
/*  203 */         this.tempoCache.refresh(paramSequence);
/*      */ 
/*  205 */         setTickPosition(0L);
/*      */ 
/*  207 */         propagateCaches();
/*      */       }
/*      */     }
/*  210 */     else if (paramSequence != null) {
/*  211 */       this.tempoCache.refresh(paramSequence);
/*  212 */       if (this.playThread != null)
/*  213 */         this.playThread.setSequence(paramSequence);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setSequence(InputStream paramInputStream)
/*      */     throws IOException, InvalidMidiDataException
/*      */   {
/*  225 */     if (paramInputStream == null) {
/*  226 */       setSequence((Sequence)null);
/*  227 */       return;
/*      */     }
/*      */ 
/*  230 */     Sequence localSequence = MidiSystem.getSequence(paramInputStream);
/*      */ 
/*  232 */     setSequence(localSequence);
/*      */   }
/*      */ 
/*      */   public Sequence getSequence()
/*      */   {
/*  240 */     return this.sequence;
/*      */   }
/*      */ 
/*      */   public synchronized void start()
/*      */   {
/*  248 */     if (!isOpen()) {
/*  249 */       throw new IllegalStateException("sequencer not open");
/*      */     }
/*      */ 
/*  253 */     if (this.sequence == null) {
/*  254 */       throw new IllegalStateException("sequence not set");
/*      */     }
/*      */ 
/*  258 */     if (this.running == true) {
/*  259 */       return;
/*      */     }
/*      */ 
/*  263 */     implStart();
/*      */   }
/*      */ 
/*      */   public synchronized void stop()
/*      */   {
/*  272 */     if (!isOpen()) {
/*  273 */       throw new IllegalStateException("sequencer not open");
/*      */     }
/*  275 */     stopRecording();
/*      */ 
/*  278 */     if (!this.running)
/*      */     {
/*  280 */       return;
/*      */     }
/*      */ 
/*  284 */     implStop();
/*      */   }
/*      */ 
/*      */   public boolean isRunning()
/*      */   {
/*  291 */     return this.running;
/*      */   }
/*      */ 
/*      */   public void startRecording()
/*      */   {
/*  296 */     if (!isOpen()) {
/*  297 */       throw new IllegalStateException("Sequencer not open");
/*      */     }
/*      */ 
/*  300 */     start();
/*  301 */     this.recording = true;
/*      */   }
/*      */ 
/*      */   public void stopRecording()
/*      */   {
/*  306 */     if (!isOpen()) {
/*  307 */       throw new IllegalStateException("Sequencer not open");
/*      */     }
/*  309 */     this.recording = false;
/*      */   }
/*      */ 
/*      */   public boolean isRecording()
/*      */   {
/*  314 */     return this.recording;
/*      */   }
/*      */ 
/*      */   public void recordEnable(Track paramTrack, int paramInt)
/*      */   {
/*  319 */     if (!findTrack(paramTrack)) {
/*  320 */       throw new IllegalArgumentException("Track does not exist in the current sequence");
/*      */     }
/*      */ 
/*  323 */     synchronized (this.recordingTracks) {
/*  324 */       RecordingTrack localRecordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
/*  325 */       if (localRecordingTrack != null)
/*  326 */         localRecordingTrack.channel = paramInt;
/*      */       else
/*  328 */         this.recordingTracks.add(new RecordingTrack(paramTrack, paramInt));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void recordDisable(Track paramTrack)
/*      */   {
/*  336 */     synchronized (this.recordingTracks) {
/*  337 */       RecordingTrack localRecordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
/*  338 */       if (localRecordingTrack != null)
/*  339 */         this.recordingTracks.remove(localRecordingTrack);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean findTrack(Track paramTrack)
/*      */   {
/*  347 */     boolean bool = false;
/*  348 */     if (this.sequence != null) {
/*  349 */       Track[] arrayOfTrack = this.sequence.getTracks();
/*  350 */       for (int i = 0; i < arrayOfTrack.length; i++) {
/*  351 */         if (paramTrack == arrayOfTrack[i]) {
/*  352 */           bool = true;
/*  353 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  357 */     return bool;
/*      */   }
/*      */ 
/*      */   public float getTempoInBPM()
/*      */   {
/*  364 */     return (float)MidiUtils.convertTempo(getTempoInMPQ());
/*      */   }
/*      */ 
/*      */   public void setTempoInBPM(float paramFloat)
/*      */   {
/*  370 */     if (paramFloat <= 0.0F)
/*      */     {
/*  372 */       paramFloat = 1.0F;
/*      */     }
/*      */ 
/*  375 */     setTempoInMPQ((float)MidiUtils.convertTempo(paramFloat));
/*      */   }
/*      */ 
/*      */   public float getTempoInMPQ()
/*      */   {
/*  382 */     if (needCaching())
/*      */     {
/*  384 */       if (this.cacheTempoMPQ != -1.0D) {
/*  385 */         return (float)this.cacheTempoMPQ;
/*      */       }
/*      */ 
/*  388 */       if (this.sequence != null) {
/*  389 */         return this.tempoCache.getTempoMPQAt(getTickPosition());
/*      */       }
/*      */ 
/*  393 */       return 500000.0F;
/*      */     }
/*  395 */     return getDataPump().getTempoMPQ();
/*      */   }
/*      */ 
/*      */   public void setTempoInMPQ(float paramFloat)
/*      */   {
/*  400 */     if (paramFloat <= 0.0F)
/*      */     {
/*  402 */       paramFloat = 1.0F;
/*      */     }
/*      */ 
/*  407 */     if (needCaching())
/*      */     {
/*  409 */       this.cacheTempoMPQ = paramFloat;
/*      */     }
/*      */     else {
/*  412 */       getDataPump().setTempoMPQ(paramFloat);
/*      */ 
/*  415 */       this.cacheTempoMPQ = -1.0D;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTempoFactor(float paramFloat)
/*      */   {
/*  421 */     if (paramFloat <= 0.0F)
/*      */     {
/*  423 */       return;
/*      */     }
/*      */ 
/*  428 */     if (needCaching()) {
/*  429 */       this.cacheTempoFactor = paramFloat;
/*      */     } else {
/*  431 */       getDataPump().setTempoFactor(paramFloat);
/*      */ 
/*  433 */       this.cacheTempoFactor = -1.0F;
/*      */     }
/*      */   }
/*      */ 
/*      */   public float getTempoFactor()
/*      */   {
/*  441 */     if (needCaching()) {
/*  442 */       if (this.cacheTempoFactor != -1.0F) {
/*  443 */         return this.cacheTempoFactor;
/*      */       }
/*  445 */       return 1.0F;
/*      */     }
/*  447 */     return getDataPump().getTempoFactor();
/*      */   }
/*      */ 
/*      */   public long getTickLength()
/*      */   {
/*  454 */     if (this.sequence == null) {
/*  455 */       return 0L;
/*      */     }
/*      */ 
/*  458 */     return this.sequence.getTickLength();
/*      */   }
/*      */ 
/*      */   public synchronized long getTickPosition()
/*      */   {
/*  465 */     if ((getDataPump() == null) || (this.sequence == null)) {
/*  466 */       return 0L;
/*      */     }
/*      */ 
/*  469 */     return getDataPump().getTickPos();
/*      */   }
/*      */ 
/*      */   public synchronized void setTickPosition(long paramLong)
/*      */   {
/*  474 */     if (paramLong < 0L)
/*      */     {
/*  476 */       return;
/*      */     }
/*      */ 
/*  481 */     if (getDataPump() == null)
/*      */     {
/*  482 */       if (paramLong == 0L);
/*      */     }
/*  486 */     else if (this.sequence == null)
/*      */     {
/*  487 */       if (paramLong == 0L);
/*      */     }
/*      */     else
/*  491 */       getDataPump().setTickPos(paramLong);
/*      */   }
/*      */ 
/*      */   public long getMicrosecondLength()
/*      */   {
/*  499 */     if (this.sequence == null) {
/*  500 */       return 0L;
/*      */     }
/*      */ 
/*  503 */     return this.sequence.getMicrosecondLength();
/*      */   }
/*      */ 
/*      */   public long getMicrosecondPosition()
/*      */   {
/*  510 */     if ((getDataPump() == null) || (this.sequence == null)) {
/*  511 */       return 0L;
/*      */     }
/*  513 */     synchronized (this.tempoCache) {
/*  514 */       return MidiUtils.tick2microsecond(this.sequence, getDataPump().getTickPos(), this.tempoCache);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMicrosecondPosition(long paramLong)
/*      */   {
/*  520 */     if (paramLong < 0L)
/*      */     {
/*  522 */       return;
/*      */     }
/*      */ 
/*  527 */     if (getDataPump() == null)
/*      */     {
/*  528 */       if (paramLong == 0L);
/*      */     }
/*  532 */     else if (this.sequence == null)
/*      */     {
/*  533 */       if (paramLong == 0L);
/*      */     }
/*      */     else
/*  537 */       synchronized (this.tempoCache) {
/*  538 */         setTickPosition(MidiUtils.microsecond2tick(this.sequence, paramLong, this.tempoCache));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void setMasterSyncMode(Sequencer.SyncMode paramSyncMode)
/*      */   {
/*      */   }
/*      */ 
/*      */   public Sequencer.SyncMode getMasterSyncMode()
/*      */   {
/*  550 */     return masterSyncMode;
/*      */   }
/*      */ 
/*      */   public Sequencer.SyncMode[] getMasterSyncModes()
/*      */   {
/*  555 */     Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[masterSyncModes.length];
/*  556 */     System.arraycopy(masterSyncModes, 0, arrayOfSyncMode, 0, masterSyncModes.length);
/*  557 */     return arrayOfSyncMode;
/*      */   }
/*      */ 
/*      */   public void setSlaveSyncMode(Sequencer.SyncMode paramSyncMode)
/*      */   {
/*      */   }
/*      */ 
/*      */   public Sequencer.SyncMode getSlaveSyncMode()
/*      */   {
/*  567 */     return slaveSyncMode;
/*      */   }
/*      */ 
/*      */   public Sequencer.SyncMode[] getSlaveSyncModes()
/*      */   {
/*  572 */     Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[slaveSyncModes.length];
/*  573 */     System.arraycopy(slaveSyncModes, 0, arrayOfSyncMode, 0, slaveSyncModes.length);
/*  574 */     return arrayOfSyncMode;
/*      */   }
/*      */ 
/*      */   protected int getTrackCount() {
/*  578 */     Sequence localSequence = getSequence();
/*  579 */     if (localSequence != null)
/*      */     {
/*  581 */       return this.sequence.getTracks().length;
/*      */     }
/*  583 */     return 0;
/*      */   }
/*      */ 
/*      */   public synchronized void setTrackMute(int paramInt, boolean paramBoolean)
/*      */   {
/*  589 */     int i = getTrackCount();
/*  590 */     if ((paramInt < 0) || (paramInt >= getTrackCount())) return;
/*  591 */     this.trackMuted = ensureBoolArraySize(this.trackMuted, i);
/*  592 */     this.trackMuted[paramInt] = paramBoolean;
/*  593 */     if (getDataPump() != null)
/*  594 */       getDataPump().muteSoloChanged();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTrackMute(int paramInt)
/*      */   {
/*  600 */     if ((paramInt < 0) || (paramInt >= getTrackCount())) return false;
/*  601 */     if ((this.trackMuted == null) || (this.trackMuted.length <= paramInt)) return false;
/*  602 */     return this.trackMuted[paramInt];
/*      */   }
/*      */ 
/*      */   public synchronized void setTrackSolo(int paramInt, boolean paramBoolean)
/*      */   {
/*  607 */     int i = getTrackCount();
/*  608 */     if ((paramInt < 0) || (paramInt >= getTrackCount())) return;
/*  609 */     this.trackSolo = ensureBoolArraySize(this.trackSolo, i);
/*  610 */     this.trackSolo[paramInt] = paramBoolean;
/*  611 */     if (getDataPump() != null)
/*  612 */       getDataPump().muteSoloChanged();
/*      */   }
/*      */ 
/*      */   public synchronized boolean getTrackSolo(int paramInt)
/*      */   {
/*  618 */     if ((paramInt < 0) || (paramInt >= getTrackCount())) return false;
/*  619 */     if ((this.trackSolo == null) || (this.trackSolo.length <= paramInt)) return false;
/*  620 */     return this.trackSolo[paramInt];
/*      */   }
/*      */ 
/*      */   public boolean addMetaEventListener(MetaEventListener paramMetaEventListener)
/*      */   {
/*  625 */     synchronized (this.metaEventListeners) {
/*  626 */       if (!this.metaEventListeners.contains(paramMetaEventListener))
/*      */       {
/*  628 */         this.metaEventListeners.add(paramMetaEventListener);
/*      */       }
/*  630 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeMetaEventListener(MetaEventListener paramMetaEventListener)
/*      */   {
/*  636 */     synchronized (this.metaEventListeners) {
/*  637 */       int i = this.metaEventListeners.indexOf(paramMetaEventListener);
/*  638 */       if (i >= 0)
/*  639 */         this.metaEventListeners.remove(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
/*      */   {
/*  646 */     synchronized (this.controllerEventListeners)
/*      */     {
/*  650 */       ControllerListElement localControllerListElement = null;
/*  651 */       int i = 0;
/*  652 */       for (int j = 0; j < this.controllerEventListeners.size(); j++)
/*      */       {
/*  654 */         localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(j);
/*      */ 
/*  656 */         if (localControllerListElement.listener.equals(paramControllerEventListener)) {
/*  657 */           localControllerListElement.addControllers(paramArrayOfInt);
/*  658 */           i = 1;
/*  659 */           break;
/*      */         }
/*      */       }
/*  662 */       if (i == 0) {
/*  663 */         localControllerListElement = new ControllerListElement(paramControllerEventListener, paramArrayOfInt, null);
/*  664 */         this.controllerEventListeners.add(localControllerListElement);
/*      */       }
/*      */ 
/*  668 */       return localControllerListElement.getControllers();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
/*      */   {
/*  674 */     synchronized (this.controllerEventListeners) {
/*  675 */       ControllerListElement localControllerListElement = null;
/*  676 */       int i = 0;
/*  677 */       for (int j = 0; j < this.controllerEventListeners.size(); j++) {
/*  678 */         localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(j);
/*  679 */         if (localControllerListElement.listener.equals(paramControllerEventListener)) {
/*  680 */           localControllerListElement.removeControllers(paramArrayOfInt);
/*  681 */           i = 1;
/*  682 */           break;
/*      */         }
/*      */       }
/*  685 */       if (i == 0) {
/*  686 */         return new int[0];
/*      */       }
/*  688 */       if (paramArrayOfInt == null) {
/*  689 */         j = this.controllerEventListeners.indexOf(localControllerListElement);
/*  690 */         if (j >= 0) {
/*  691 */           this.controllerEventListeners.remove(j);
/*      */         }
/*  693 */         return new int[0];
/*      */       }
/*  695 */       return localControllerListElement.getControllers();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLoopStartPoint(long paramLong)
/*      */   {
/*  703 */     if ((paramLong > getTickLength()) || ((this.loopEnd != -1L) && (paramLong > this.loopEnd)) || (paramLong < 0L))
/*      */     {
/*  706 */       throw new IllegalArgumentException("invalid loop start point: " + paramLong);
/*      */     }
/*  708 */     this.loopStart = paramLong;
/*      */   }
/*      */ 
/*      */   public long getLoopStartPoint() {
/*  712 */     return this.loopStart;
/*      */   }
/*      */ 
/*      */   public void setLoopEndPoint(long paramLong) {
/*  716 */     if ((paramLong > getTickLength()) || ((this.loopStart > paramLong) && (paramLong != -1L)) || (paramLong < -1L))
/*      */     {
/*  719 */       throw new IllegalArgumentException("invalid loop end point: " + paramLong);
/*      */     }
/*  721 */     this.loopEnd = paramLong;
/*      */   }
/*      */ 
/*      */   public long getLoopEndPoint() {
/*  725 */     return this.loopEnd;
/*      */   }
/*      */ 
/*      */   public void setLoopCount(int paramInt) {
/*  729 */     if ((paramInt != -1) && (paramInt < 0))
/*      */     {
/*  731 */       throw new IllegalArgumentException("illegal value for loop count: " + paramInt);
/*      */     }
/*  733 */     this.loopCount = paramInt;
/*  734 */     if (getDataPump() != null)
/*  735 */       getDataPump().resetLoopCount();
/*      */   }
/*      */ 
/*      */   public int getLoopCount()
/*      */   {
/*  740 */     return this.loopCount;
/*      */   }
/*      */ 
/*      */   protected void implOpen()
/*      */     throws MidiUnavailableException
/*      */   {
/*  754 */     this.playThread = new PlayThread();
/*      */ 
/*  760 */     if (this.sequence != null) {
/*  761 */       this.playThread.setSequence(this.sequence);
/*      */     }
/*      */ 
/*  765 */     propagateCaches();
/*      */ 
/*  767 */     if (this.doAutoConnectAtNextOpen)
/*  768 */       doAutoConnect();
/*      */   }
/*      */ 
/*      */   private void doAutoConnect()
/*      */   {
/*  775 */     Receiver localReceiver = null;
/*      */     try
/*      */     {
/*  781 */       Synthesizer localSynthesizer = MidiSystem.getSynthesizer();
/*  782 */       if ((localSynthesizer instanceof ReferenceCountingDevice)) {
/*  783 */         localReceiver = ((ReferenceCountingDevice)localSynthesizer).getReceiverReferenceCounting();
/*      */       } else {
/*  785 */         localSynthesizer.open();
/*      */         try {
/*  787 */           localReceiver = localSynthesizer.getReceiver();
/*      */         }
/*      */         finally {
/*  790 */           if (localReceiver == null)
/*  791 */             localSynthesizer.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*      */     }
/*  798 */     if (localReceiver == null)
/*      */       try
/*      */       {
/*  801 */         localReceiver = MidiSystem.getReceiver();
/*      */       }
/*      */       catch (Exception localException2)
/*      */       {
/*      */       }
/*  806 */     if (localReceiver != null) {
/*  807 */       this.autoConnectedReceiver = localReceiver;
/*      */       try {
/*  809 */         getTransmitter().setReceiver(localReceiver);
/*      */       }
/*      */       catch (Exception localException3) {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void propagateCaches() {
/*  817 */     if ((this.sequence != null) && (isOpen())) {
/*  818 */       if (this.cacheTempoFactor != -1.0F) {
/*  819 */         setTempoFactor(this.cacheTempoFactor);
/*      */       }
/*  821 */       if (this.cacheTempoMPQ == -1.0D)
/*  822 */         setTempoInMPQ(new MidiUtils.TempoCache(this.sequence).getTempoMPQAt(getTickPosition()));
/*      */       else
/*  824 */         setTempoInMPQ((float)this.cacheTempoMPQ);
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void setCaches()
/*      */   {
/*  831 */     this.cacheTempoFactor = getTempoFactor();
/*  832 */     this.cacheTempoMPQ = getTempoInMPQ();
/*      */   }
/*      */ 
/*      */   protected synchronized void implClose()
/*      */   {
/*  840 */     if (this.playThread != null)
/*      */     {
/*  844 */       this.playThread.close();
/*  845 */       this.playThread = null;
/*      */     }
/*      */ 
/*  848 */     super.implClose();
/*      */ 
/*  850 */     this.sequence = null;
/*  851 */     this.running = false;
/*  852 */     this.cacheTempoMPQ = -1.0D;
/*  853 */     this.cacheTempoFactor = -1.0F;
/*  854 */     this.trackMuted = null;
/*  855 */     this.trackSolo = null;
/*  856 */     this.loopStart = 0L;
/*  857 */     this.loopEnd = -1L;
/*  858 */     this.loopCount = 0;
/*      */ 
/*  863 */     this.doAutoConnectAtNextOpen = this.autoConnect;
/*      */ 
/*  865 */     if (this.autoConnectedReceiver != null) {
/*      */       try {
/*  867 */         this.autoConnectedReceiver.close(); } catch (Exception localException) {
/*      */       }
/*  869 */       this.autoConnectedReceiver = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void implStart()
/*      */   {
/*  878 */     if (this.playThread == null)
/*      */     {
/*  880 */       return;
/*      */     }
/*      */ 
/*  883 */     this.tempoCache.refresh(this.sequence);
/*  884 */     if (!this.running) {
/*  885 */       this.running = true;
/*  886 */       this.playThread.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void implStop()
/*      */   {
/*  895 */     if (this.playThread == null)
/*      */     {
/*  897 */       return;
/*      */     }
/*      */ 
/*  900 */     this.recording = false;
/*  901 */     if (this.running) {
/*  902 */       this.running = false;
/*  903 */       this.playThread.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void sendMetaEvents(MidiMessage paramMidiMessage)
/*      */   {
/*  914 */     if (this.metaEventListeners.size() == 0) return;
/*      */ 
/*  917 */     eventDispatcher.sendAudioEvents(paramMidiMessage, this.metaEventListeners);
/*      */   }
/*      */ 
/*      */   protected void sendControllerEvents(MidiMessage paramMidiMessage)
/*      */   {
/*  924 */     int i = this.controllerEventListeners.size();
/*  925 */     if (i == 0) return;
/*      */ 
/*  929 */     if (!(paramMidiMessage instanceof ShortMessage))
/*      */     {
/*  931 */       return;
/*      */     }
/*  933 */     ShortMessage localShortMessage = (ShortMessage)paramMidiMessage;
/*  934 */     int j = localShortMessage.getData1();
/*  935 */     ArrayList localArrayList = new ArrayList();
/*  936 */     for (int k = 0; k < i; k++) {
/*  937 */       ControllerListElement localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(k);
/*  938 */       for (int m = 0; m < localControllerListElement.controllers.length; m++) {
/*  939 */         if (localControllerListElement.controllers[m] == j) {
/*  940 */           localArrayList.add(localControllerListElement.listener);
/*  941 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  945 */     eventDispatcher.sendAudioEvents(paramMidiMessage, localArrayList);
/*      */   }
/*      */ 
/*      */   private boolean needCaching()
/*      */   {
/*  951 */     return (!isOpen()) || (this.sequence == null) || (this.playThread == null);
/*      */   }
/*      */ 
/*      */   private DataPump getDataPump()
/*      */   {
/*  961 */     if (this.playThread != null) {
/*  962 */       return this.playThread.getDataPump();
/*      */     }
/*  964 */     return null;
/*      */   }
/*      */ 
/*      */   private MidiUtils.TempoCache getTempoCache() {
/*  968 */     return this.tempoCache;
/*      */   }
/*      */ 
/*      */   private static boolean[] ensureBoolArraySize(boolean[] paramArrayOfBoolean, int paramInt) {
/*  972 */     if (paramArrayOfBoolean == null) {
/*  973 */       return new boolean[paramInt];
/*      */     }
/*  975 */     if (paramArrayOfBoolean.length < paramInt) {
/*  976 */       boolean[] arrayOfBoolean = new boolean[paramInt];
/*  977 */       System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramArrayOfBoolean.length);
/*  978 */       return arrayOfBoolean;
/*      */     }
/*  980 */     return paramArrayOfBoolean;
/*      */   }
/*      */ 
/*      */   protected boolean hasReceivers()
/*      */   {
/*  987 */     return true;
/*      */   }
/*      */ 
/*      */   protected Receiver createReceiver() throws MidiUnavailableException
/*      */   {
/*  992 */     return new SequencerReceiver();
/*      */   }
/*      */ 
/*      */   protected boolean hasTransmitters()
/*      */   {
/*  997 */     return true;
/*      */   }
/*      */ 
/*      */   protected Transmitter createTransmitter() throws MidiUnavailableException
/*      */   {
/* 1002 */     return new SequencerTransmitter(null);
/*      */   }
/*      */ 
/*      */   public void setAutoConnect(Receiver paramReceiver)
/*      */   {
/* 1008 */     this.autoConnect = (paramReceiver != null);
/* 1009 */     this.autoConnectedReceiver = paramReceiver;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  156 */     eventDispatcher = new EventDispatcher();
/*  157 */     eventDispatcher.start();
/*      */   }
/*      */ 
/*      */   private class ControllerListElement
/*      */   {
/*      */     int[] controllers;
/*      */     ControllerEventListener listener;
/*      */ 
/*      */     private ControllerListElement(ControllerEventListener paramArrayOfInt, int[] arg3)
/*      */     {
/* 1099 */       this.listener = paramArrayOfInt;
/*      */       int[] arrayOfInt;
/* 1100 */       if (arrayOfInt == null) {
/* 1101 */         arrayOfInt = new int[''];
/* 1102 */         for (int i = 0; i < 128; i++) {
/* 1103 */           arrayOfInt[i] = i;
/*      */         }
/*      */       }
/* 1106 */       this.controllers = arrayOfInt;
/*      */     }
/*      */ 
/*      */     private void addControllers(int[] paramArrayOfInt)
/*      */     {
/* 1111 */       if (paramArrayOfInt == null) {
/* 1112 */         this.controllers = new int[''];
/* 1113 */         for (int i = 0; i < 128; i++) {
/* 1114 */           this.controllers[i] = i;
/*      */         }
/* 1116 */         return;
/*      */       }
/* 1118 */       int[] arrayOfInt1 = new int[this.controllers.length + paramArrayOfInt.length];
/*      */ 
/* 1122 */       for (int k = 0; k < this.controllers.length; k++) {
/* 1123 */         arrayOfInt1[k] = this.controllers[k];
/*      */       }
/* 1125 */       int j = this.controllers.length;
/*      */ 
/* 1127 */       for (k = 0; k < paramArrayOfInt.length; k++) {
/* 1128 */         m = 0;
/*      */ 
/* 1130 */         for (int n = 0; n < this.controllers.length; n++) {
/* 1131 */           if (paramArrayOfInt[k] == this.controllers[n]) {
/* 1132 */             m = 1;
/* 1133 */             break;
/*      */           }
/*      */         }
/* 1136 */         if (m == 0) {
/* 1137 */           arrayOfInt1[(j++)] = paramArrayOfInt[k];
/*      */         }
/*      */       }
/*      */ 
/* 1141 */       int[] arrayOfInt2 = new int[j];
/* 1142 */       for (int m = 0; m < j; m++) {
/* 1143 */         arrayOfInt2[m] = arrayOfInt1[m];
/*      */       }
/* 1145 */       this.controllers = arrayOfInt2;
/*      */     }
/*      */ 
/*      */     private void removeControllers(int[] paramArrayOfInt)
/*      */     {
/* 1150 */       if (paramArrayOfInt == null) {
/* 1151 */         this.controllers = new int[0];
/*      */       } else {
/* 1153 */         int[] arrayOfInt1 = new int[this.controllers.length];
/* 1154 */         int i = 0;
/*      */ 
/* 1157 */         for (int j = 0; j < this.controllers.length; j++) {
/* 1158 */           k = 0;
/* 1159 */           for (int m = 0; m < paramArrayOfInt.length; m++) {
/* 1160 */             if (this.controllers[j] == paramArrayOfInt[m]) {
/* 1161 */               k = 1;
/* 1162 */               break;
/*      */             }
/*      */           }
/* 1165 */           if (k == 0) {
/* 1166 */             arrayOfInt1[(i++)] = this.controllers[j];
/*      */           }
/*      */         }
/*      */ 
/* 1170 */         int[] arrayOfInt2 = new int[i];
/* 1171 */         for (int k = 0; k < i; k++) {
/* 1172 */           arrayOfInt2[k] = arrayOfInt1[k];
/*      */         }
/* 1174 */         this.controllers = arrayOfInt2;
/*      */       }
/*      */     }
/*      */ 
/*      */     private int[] getControllers()
/*      */     {
/* 1183 */       if (this.controllers == null) {
/* 1184 */         return null;
/*      */       }
/*      */ 
/* 1187 */       int[] arrayOfInt = new int[this.controllers.length];
/*      */ 
/* 1189 */       for (int i = 0; i < this.controllers.length; i++) {
/* 1190 */         arrayOfInt[i] = this.controllers[i];
/*      */       }
/* 1192 */       return arrayOfInt;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DataPump
/*      */   {
/*      */     private float currTempo;
/*      */     private float tempoFactor;
/*      */     private float inverseTempoFactor;
/*      */     private long ignoreTempoEventAt;
/*      */     private int resolution;
/*      */     private float divisionType;
/*      */     private long checkPointMillis;
/*      */     private long checkPointTick;
/*      */     private int[] noteOnCache;
/*      */     private Track[] tracks;
/*      */     private boolean[] trackDisabled;
/*      */     private int[] trackReadPos;
/*      */     private long lastTick;
/* 1423 */     private boolean needReindex = false;
/* 1424 */     private int currLoopCounter = 0;
/*      */ 
/*      */     DataPump()
/*      */     {
/* 1431 */       init();
/*      */     }
/*      */ 
/*      */     synchronized void init() {
/* 1435 */       this.ignoreTempoEventAt = -1L;
/* 1436 */       this.tempoFactor = 1.0F;
/* 1437 */       this.inverseTempoFactor = 1.0F;
/* 1438 */       this.noteOnCache = new int[''];
/* 1439 */       this.tracks = null;
/* 1440 */       this.trackDisabled = null;
/*      */     }
/*      */ 
/*      */     synchronized void setTickPos(long paramLong) {
/* 1444 */       long l = paramLong;
/* 1445 */       this.lastTick = paramLong;
/* 1446 */       if (RealTimeSequencer.this.running) {
/* 1447 */         notesOff(false);
/*      */       }
/* 1449 */       if ((RealTimeSequencer.this.running) || (paramLong > 0L))
/*      */       {
/* 1451 */         chaseEvents(l, paramLong);
/*      */       }
/* 1453 */       else this.needReindex = true;
/*      */ 
/* 1455 */       if (!hasCachedTempo()) {
/* 1456 */         setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
/*      */ 
/* 1458 */         this.ignoreTempoEventAt = -1L;
/*      */       }
/*      */ 
/* 1461 */       this.checkPointMillis = 0L;
/*      */     }
/*      */ 
/*      */     long getTickPos() {
/* 1465 */       return this.lastTick;
/*      */     }
/*      */ 
/*      */     boolean hasCachedTempo()
/*      */     {
/* 1470 */       if (this.ignoreTempoEventAt != this.lastTick) {
/* 1471 */         this.ignoreTempoEventAt = -1L;
/*      */       }
/* 1473 */       return this.ignoreTempoEventAt >= 0L;
/*      */     }
/*      */ 
/*      */     synchronized void setTempoMPQ(float paramFloat)
/*      */     {
/* 1478 */       if ((paramFloat > 0.0F) && (paramFloat != this.currTempo)) {
/* 1479 */         this.ignoreTempoEventAt = this.lastTick;
/* 1480 */         this.currTempo = paramFloat;
/*      */ 
/* 1482 */         this.checkPointMillis = 0L;
/*      */       }
/*      */     }
/*      */ 
/*      */     float getTempoMPQ() {
/* 1487 */       return this.currTempo;
/*      */     }
/*      */ 
/*      */     synchronized void setTempoFactor(float paramFloat) {
/* 1491 */       if ((paramFloat > 0.0F) && (paramFloat != this.tempoFactor)) {
/* 1492 */         this.tempoFactor = paramFloat;
/* 1493 */         this.inverseTempoFactor = (1.0F / paramFloat);
/*      */ 
/* 1495 */         this.checkPointMillis = 0L;
/*      */       }
/*      */     }
/*      */ 
/*      */     float getTempoFactor() {
/* 1500 */       return this.tempoFactor;
/*      */     }
/*      */ 
/*      */     synchronized void muteSoloChanged() {
/* 1504 */       boolean[] arrayOfBoolean = makeDisabledArray();
/* 1505 */       if (RealTimeSequencer.this.running) {
/* 1506 */         applyDisabledTracks(this.trackDisabled, arrayOfBoolean);
/*      */       }
/* 1508 */       this.trackDisabled = arrayOfBoolean;
/*      */     }
/*      */ 
/*      */     synchronized void setSequence(Sequence paramSequence)
/*      */     {
/* 1514 */       if (paramSequence == null) {
/* 1515 */         init();
/* 1516 */         return;
/*      */       }
/* 1518 */       this.tracks = paramSequence.getTracks();
/* 1519 */       muteSoloChanged();
/* 1520 */       this.resolution = paramSequence.getResolution();
/* 1521 */       this.divisionType = paramSequence.getDivisionType();
/* 1522 */       this.trackReadPos = new int[this.tracks.length];
/*      */ 
/* 1524 */       this.checkPointMillis = 0L;
/* 1525 */       this.needReindex = true;
/*      */     }
/*      */ 
/*      */     synchronized void resetLoopCount() {
/* 1529 */       this.currLoopCounter = RealTimeSequencer.this.loopCount;
/*      */     }
/*      */ 
/*      */     void clearNoteOnCache() {
/* 1533 */       for (int i = 0; i < 128; i++)
/* 1534 */         this.noteOnCache[i] = 0;
/*      */     }
/*      */ 
/*      */     void notesOff(boolean paramBoolean)
/*      */     {
/* 1539 */       int i = 0;
/* 1540 */       for (int j = 0; j < 16; j++) {
/* 1541 */         int k = 1 << j;
/* 1542 */         for (int m = 0; m < 128; m++) {
/* 1543 */           if ((this.noteOnCache[m] & k) != 0) {
/* 1544 */             this.noteOnCache[m] ^= k;
/*      */ 
/* 1546 */             RealTimeSequencer.this.getTransmitterList().sendMessage(0x90 | j | m << 8, -1L);
/* 1547 */             i++;
/*      */           }
/*      */         }
/*      */ 
/* 1551 */         RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x7B00, -1L);
/*      */ 
/* 1553 */         RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x4000, -1L);
/* 1554 */         if (paramBoolean)
/*      */         {
/* 1556 */           RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x7900, -1L);
/* 1557 */           i++;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private boolean[] makeDisabledArray()
/*      */     {
/* 1565 */       if (this.tracks == null) {
/* 1566 */         return null;
/*      */       }
/* 1568 */       boolean[] arrayOfBoolean1 = new boolean[this.tracks.length];
/*      */       boolean[] arrayOfBoolean3;
/*      */       boolean[] arrayOfBoolean2;
/* 1571 */       synchronized (RealTimeSequencer.this) {
/* 1572 */         arrayOfBoolean3 = RealTimeSequencer.this.trackMuted;
/* 1573 */         arrayOfBoolean2 = RealTimeSequencer.this.trackSolo;
/*      */       }
/*      */ 
/* 1576 */       int i = 0;
/*      */       int j;
/* 1577 */       if (arrayOfBoolean2 != null) {
/* 1578 */         for (j = 0; j < arrayOfBoolean2.length; j++) {
/* 1579 */           if (arrayOfBoolean2[j] != 0) {
/* 1580 */             i = 1;
/* 1581 */             break;
/*      */           }
/*      */         }
/*      */       }
/* 1585 */       if (i != 0)
/*      */       {
/* 1587 */         for (j = 0; j < arrayOfBoolean1.length; j++) {
/* 1588 */           arrayOfBoolean1[j] = ((j >= arrayOfBoolean2.length) || (arrayOfBoolean2[j] == 0) ? 1 : false);
/*      */         }
/*      */       }
/*      */       else {
/* 1592 */         for (j = 0; j < arrayOfBoolean1.length; j++) {
/* 1593 */           arrayOfBoolean1[j] = ((arrayOfBoolean3 != null) && (j < arrayOfBoolean3.length) && (arrayOfBoolean3[j] != 0) ? 1 : false);
/*      */         }
/*      */       }
/* 1596 */       return arrayOfBoolean1;
/*      */     }
/*      */ 
/*      */     private void sendNoteOffIfOn(Track paramTrack, long paramLong)
/*      */     {
/* 1608 */       int i = paramTrack.size();
/* 1609 */       int j = 0;
/*      */       try {
/* 1611 */         for (int k = 0; k < i; k++) {
/* 1612 */           MidiEvent localMidiEvent = paramTrack.get(k);
/* 1613 */           if (localMidiEvent.getTick() > paramLong) break;
/* 1614 */           MidiMessage localMidiMessage = localMidiEvent.getMessage();
/* 1615 */           int m = localMidiMessage.getStatus();
/* 1616 */           int n = localMidiMessage.getLength();
/* 1617 */           if ((n == 3) && ((m & 0xF0) == 144)) {
/* 1618 */             int i1 = -1;
/*      */             Object localObject;
/* 1619 */             if ((localMidiMessage instanceof ShortMessage)) {
/* 1620 */               localObject = (ShortMessage)localMidiMessage;
/* 1621 */               if (((ShortMessage)localObject).getData2() > 0)
/*      */               {
/* 1623 */                 i1 = ((ShortMessage)localObject).getData1();
/*      */               }
/*      */             } else {
/* 1626 */               localObject = localMidiMessage.getMessage();
/* 1627 */               if ((localObject[2] & 0x7F) > 0)
/*      */               {
/* 1629 */                 i1 = localObject[1] & 0x7F;
/*      */               }
/*      */             }
/* 1632 */             if (i1 >= 0) {
/* 1633 */               int i2 = 1 << (m & 0xF);
/* 1634 */               if ((this.noteOnCache[i1] & i2) != 0)
/*      */               {
/* 1636 */                 RealTimeSequencer.this.getTransmitterList().sendMessage(m | i1 << 8, -1L);
/*      */ 
/* 1638 */                 this.noteOnCache[i1] &= (0xFFFF ^ i2);
/* 1639 */                 j++;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*      */     private void applyDisabledTracks(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
/*      */     {
/* 1658 */       byte[][] arrayOfByte = (byte[][])null;
/* 1659 */       synchronized (RealTimeSequencer.this) {
/* 1660 */         for (int i = 0; i < paramArrayOfBoolean2.length; i++)
/* 1661 */           if (((paramArrayOfBoolean1 == null) || (i >= paramArrayOfBoolean1.length) || (paramArrayOfBoolean1[i] == 0)) && (paramArrayOfBoolean2[i] != 0))
/*      */           {
/* 1669 */             if (this.tracks.length > i) {
/* 1670 */               sendNoteOffIfOn(this.tracks[i], this.lastTick);
/*      */             }
/*      */           }
/* 1673 */           else if ((paramArrayOfBoolean1 != null) && (i < paramArrayOfBoolean1.length) && (paramArrayOfBoolean1[i] != 0) && (paramArrayOfBoolean2[i] == 0))
/*      */           {
/* 1679 */             if (arrayOfByte == null) {
/* 1680 */               arrayOfByte = new byte[''][16];
/*      */             }
/* 1682 */             chaseTrackEvents(i, 0L, this.lastTick, true, arrayOfByte);
/*      */           }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void chaseTrackEvents(int paramInt, long paramLong1, long paramLong2, boolean paramBoolean, byte[][] paramArrayOfByte)
/*      */     {
/* 1700 */       if (paramLong1 > paramLong2)
/*      */       {
/* 1702 */         paramLong1 = 0L;
/*      */       }
/* 1704 */       byte[] arrayOfByte = new byte[16];
/*      */ 
/* 1706 */       for (int i = 0; i < 16; i++) {
/* 1707 */         arrayOfByte[i] = -1;
/* 1708 */         for (j = 0; j < 128; j++) {
/* 1709 */           paramArrayOfByte[j][i] = -1; }  } Track localTrack = this.tracks[paramInt];
/* 1713 */       int j = localTrack.size();
/*      */       int i2;
/*      */       int i3;
/*      */       try { for (int k = 0; k < j; k++) {
/* 1716 */           MidiEvent localMidiEvent = localTrack.get(k);
/* 1717 */           if (localMidiEvent.getTick() >= paramLong2) {
/* 1718 */             if ((!paramBoolean) || (paramInt >= this.trackReadPos.length)) break;
/* 1719 */             this.trackReadPos[paramInt] = (k > 0 ? k - 1 : 0); break;
/*      */           }
/*      */ 
/* 1724 */           MidiMessage localMidiMessage = localMidiEvent.getMessage();
/* 1725 */           i2 = localMidiMessage.getStatus();
/* 1726 */           i3 = localMidiMessage.getLength();
/*      */           Object localObject;
/* 1727 */           if ((i3 == 3) && ((i2 & 0xF0) == 176)) {
/* 1728 */             if ((localMidiMessage instanceof ShortMessage)) {
/* 1729 */               localObject = (ShortMessage)localMidiMessage;
/* 1730 */               paramArrayOfByte[(localObject.getData1() & 0x7F)][(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData2());
/*      */             } else {
/* 1732 */               localObject = localMidiMessage.getMessage();
/* 1733 */               paramArrayOfByte[(localObject[1] & 0x7F)][(i2 & 0xF)] = localObject[2];
/*      */             }
/*      */           }
/* 1736 */           if ((i3 == 2) && ((i2 & 0xF0) == 192)) {
/* 1737 */             if ((localMidiMessage instanceof ShortMessage)) {
/* 1738 */               localObject = (ShortMessage)localMidiMessage;
/* 1739 */               arrayOfByte[(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData1());
/*      */             } else {
/* 1741 */               localObject = localMidiMessage.getMessage();
/* 1742 */               arrayOfByte[(i2 & 0xF)] = localObject[1];
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
/*      */       {
/*      */       }
/* 1750 */       int m = 0;
/*      */ 
/* 1752 */       for (int n = 0; n < 16; n++) {
/* 1753 */         for (int i1 = 0; i1 < 128; i1++) {
/* 1754 */           i2 = paramArrayOfByte[i1][n];
/* 1755 */           if (i2 >= 0) {
/* 1756 */             i3 = 0xB0 | n | i1 << 8 | i2 << 16;
/* 1757 */             RealTimeSequencer.this.getTransmitterList().sendMessage(i3, -1L);
/* 1758 */             m++;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1763 */         if (arrayOfByte[n] >= 0) {
/* 1764 */           RealTimeSequencer.this.getTransmitterList().sendMessage(0xC0 | n | arrayOfByte[n] << 8, -1L);
/*      */         }
/* 1766 */         if ((arrayOfByte[n] >= 0) || (paramLong1 == 0L) || (paramLong2 == 0L))
/*      */         {
/* 1768 */           RealTimeSequencer.this.getTransmitterList().sendMessage(0xE0 | n | 0x400000, -1L);
/*      */ 
/* 1770 */           RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | n | 0x4000, -1L);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     synchronized void chaseEvents(long paramLong1, long paramLong2)
/*      */     {
/* 1780 */       byte[][] arrayOfByte = new byte[''][16];
/* 1781 */       for (int i = 0; i < this.tracks.length; i++)
/* 1782 */         if ((this.trackDisabled == null) || (this.trackDisabled.length <= i) || (this.trackDisabled[i] == 0))
/*      */         {
/* 1786 */           chaseTrackEvents(i, paramLong1, paramLong2, true, arrayOfByte);
/*      */         }
/*      */     }
/*      */ 
/*      */     private long getCurrentTimeMillis()
/*      */     {
/* 1796 */       return System.nanoTime() / 1000000L;
/*      */     }
/*      */ 
/*      */     private long millis2tick(long paramLong)
/*      */     {
/* 1801 */       if (this.divisionType != 0.0F) {
/* 1802 */         double d = paramLong * this.tempoFactor * this.divisionType * this.resolution / 1000.0D;
/*      */ 
/* 1806 */         return ()d;
/*      */       }
/* 1808 */       return MidiUtils.microsec2ticks(paramLong * 1000L, this.currTempo * this.inverseTempoFactor, this.resolution);
/*      */     }
/*      */ 
/*      */     private long tick2millis(long paramLong)
/*      */     {
/* 1814 */       if (this.divisionType != 0.0F) {
/* 1815 */         double d = paramLong * 1000.0D / (this.tempoFactor * this.divisionType * this.resolution);
/*      */ 
/* 1817 */         return ()d;
/*      */       }
/* 1819 */       return MidiUtils.ticks2microsec(paramLong, this.currTempo * this.inverseTempoFactor, this.resolution) / 1000L;
/*      */     }
/*      */ 
/*      */     private void ReindexTrack(int paramInt, long paramLong)
/*      */     {
/* 1825 */       if ((paramInt < this.trackReadPos.length) && (paramInt < this.tracks.length))
/* 1826 */         this.trackReadPos[paramInt] = MidiUtils.tick2index(this.tracks[paramInt], paramLong);
/*      */     }
/*      */ 
/*      */     private boolean dispatchMessage(int paramInt, MidiEvent paramMidiEvent)
/*      */     {
/* 1833 */       boolean bool = false;
/* 1834 */       MidiMessage localMidiMessage = paramMidiEvent.getMessage();
/* 1835 */       int i = localMidiMessage.getStatus();
/* 1836 */       int j = localMidiMessage.getLength();
/*      */       int k;
/* 1837 */       if ((i == 255) && (j >= 2))
/*      */       {
/* 1844 */         if (paramInt == 0) {
/* 1845 */           k = MidiUtils.getTempoMPQ(localMidiMessage);
/* 1846 */           if (k > 0) {
/* 1847 */             if (paramMidiEvent.getTick() != this.ignoreTempoEventAt) {
/* 1848 */               setTempoMPQ(k);
/* 1849 */               bool = true;
/*      */             }
/*      */ 
/* 1852 */             this.ignoreTempoEventAt = -1L;
/*      */           }
/*      */         }
/*      */ 
/* 1856 */         RealTimeSequencer.this.sendMetaEvents(localMidiMessage);
/*      */       }
/*      */       else
/*      */       {
/* 1860 */         RealTimeSequencer.this.getTransmitterList().sendMessage(localMidiMessage, -1L);
/*      */ 
/* 1862 */         switch (i & 0xF0)
/*      */         {
/*      */         case 128:
/* 1865 */           k = ((ShortMessage)localMidiMessage).getData1() & 0x7F;
/* 1866 */           this.noteOnCache[k] &= (0xFFFF ^ 1 << (i & 0xF));
/* 1867 */           break;
/*      */         case 144:
/* 1872 */           ShortMessage localShortMessage = (ShortMessage)localMidiMessage;
/* 1873 */           int m = localShortMessage.getData1() & 0x7F;
/* 1874 */           int n = localShortMessage.getData2() & 0x7F;
/* 1875 */           if (n > 0)
/*      */           {
/* 1877 */             this.noteOnCache[m] |= 1 << (i & 0xF);
/*      */           }
/*      */           else {
/* 1880 */             this.noteOnCache[m] &= (0xFFFF ^ 1 << (i & 0xF));
/*      */           }
/* 1882 */           break;
/*      */         case 176:
/* 1887 */           RealTimeSequencer.this.sendControllerEvents(localMidiMessage);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1892 */       return bool;
/*      */     }
/*      */ 
/*      */     synchronized boolean pump()
/*      */     {
/* 1901 */       long l2 = this.lastTick;
/*      */ 
/* 1903 */       boolean bool1 = false;
/* 1904 */       int i = 0;
/* 1905 */       boolean bool2 = false;
/*      */ 
/* 1907 */       long l1 = getCurrentTimeMillis();
/* 1908 */       int j = 0;
/*      */       do {
/* 1910 */         bool1 = false;
/*      */ 
/* 1913 */         if (this.needReindex)
/*      */         {
/* 1915 */           if (this.trackReadPos.length < this.tracks.length) {
/* 1916 */             this.trackReadPos = new int[this.tracks.length];
/*      */           }
/* 1918 */           for (k = 0; k < this.tracks.length; k++) {
/* 1919 */             ReindexTrack(k, l2);
/*      */           }
/*      */ 
/* 1922 */           this.needReindex = false;
/* 1923 */           this.checkPointMillis = 0L;
/*      */         }
/*      */ 
/* 1927 */         if (this.checkPointMillis == 0L)
/*      */         {
/* 1929 */           l1 = getCurrentTimeMillis();
/* 1930 */           this.checkPointMillis = l1;
/* 1931 */           l2 = this.lastTick;
/* 1932 */           this.checkPointTick = l2;
/*      */         }
/*      */         else
/*      */         {
/* 1938 */           l2 = this.checkPointTick + millis2tick(l1 - this.checkPointMillis);
/*      */ 
/* 1940 */           if ((RealTimeSequencer.this.loopEnd != -1L) && (((RealTimeSequencer.this.loopCount > 0) && (this.currLoopCounter > 0)) || (RealTimeSequencer.this.loopCount == -1)))
/*      */           {
/* 1943 */             if ((this.lastTick <= RealTimeSequencer.this.loopEnd) && (l2 >= RealTimeSequencer.this.loopEnd))
/*      */             {
/* 1946 */               l2 = RealTimeSequencer.this.loopEnd - 1L;
/* 1947 */               i = 1;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1959 */           this.lastTick = l2;
/*      */         }
/*      */ 
/* 1962 */         j = 0;
/*      */ 
/* 1964 */         for (int k = 0; k < this.tracks.length; k++) {
/*      */           try {
/* 1966 */             int m = this.trackDisabled[k];
/* 1967 */             Track localTrack = this.tracks[k];
/* 1968 */             int n = this.trackReadPos[k];
/* 1969 */             int i1 = localTrack.size();
/*      */             MidiEvent localMidiEvent;
/* 1972 */             while ((!bool1) && (n < i1) && ((localMidiEvent = localTrack.get(n)).getTick() <= l2))
/*      */             {
/* 1974 */               if ((n == i1 - 1) && (MidiUtils.isMetaEndOfTrack(localMidiEvent.getMessage())))
/*      */               {
/* 1976 */                 n = i1;
/* 1977 */                 break;
/*      */               }
/*      */ 
/* 1982 */               n++;
/*      */ 
/* 1987 */               if ((m == 0) || ((k == 0) && (MidiUtils.isMetaTempo(localMidiEvent.getMessage()))))
/*      */               {
/* 1989 */                 bool1 = dispatchMessage(k, localMidiEvent);
/*      */               }
/*      */             }
/* 1992 */             if (n >= i1) {
/* 1993 */               j++;
/*      */             }
/*      */ 
/* 2013 */             this.trackReadPos[k] = n;
/*      */           }
/*      */           catch (Exception localException)
/*      */           {
/* 2017 */             if ((localException instanceof ArrayIndexOutOfBoundsException)) {
/* 2018 */               this.needReindex = true;
/* 2019 */               bool1 = true;
/*      */             }
/*      */           }
/* 2022 */           if (bool1) {
/*      */             break;
/*      */           }
/*      */         }
/* 2026 */         bool2 = j == this.tracks.length;
/* 2027 */         if ((i != 0) || (((RealTimeSequencer.this.loopCount > 0) && (this.currLoopCounter > 0)) || ((RealTimeSequencer.this.loopCount == -1) && (!bool1) && (RealTimeSequencer.this.loopEnd == -1L) && (bool2))))
/*      */         {
/* 2034 */           long l3 = this.checkPointMillis;
/* 2035 */           long l4 = RealTimeSequencer.this.loopEnd;
/* 2036 */           if (l4 == -1L) {
/* 2037 */             l4 = this.lastTick;
/*      */           }
/*      */ 
/* 2041 */           if (RealTimeSequencer.this.loopCount != -1) {
/* 2042 */             this.currLoopCounter -= 1;
/*      */           }
/*      */ 
/* 2048 */           setTickPos(RealTimeSequencer.this.loopStart);
/*      */ 
/* 2057 */           this.checkPointMillis = (l3 + tick2millis(l4 - this.checkPointTick));
/* 2058 */           this.checkPointTick = RealTimeSequencer.this.loopStart;
/*      */ 
/* 2063 */           this.needReindex = false;
/* 2064 */           bool1 = false;
/*      */ 
/* 2066 */           i = 0;
/* 2067 */           bool2 = false;
/*      */         }
/*      */       }
/* 2069 */       while (bool1);
/*      */ 
/* 2071 */       return bool2;
/*      */     }
/*      */   }
/*      */ 
/*      */   class PlayThread
/*      */     implements Runnable
/*      */   {
/*      */     private Thread thread;
/* 1242 */     private Object lock = new Object();
/*      */ 
/* 1245 */     boolean interrupted = false;
/* 1246 */     boolean isPumping = false;
/*      */ 
/* 1248 */     private RealTimeSequencer.DataPump dataPump = new RealTimeSequencer.DataPump(RealTimeSequencer.this);
/*      */ 
/*      */     PlayThread()
/*      */     {
/* 1253 */       int i = 8;
/*      */ 
/* 1255 */       this.thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, i, true);
/*      */     }
/*      */ 
/*      */     RealTimeSequencer.DataPump getDataPump()
/*      */     {
/* 1263 */       return this.dataPump;
/*      */     }
/*      */ 
/*      */     synchronized void setSequence(Sequence paramSequence) {
/* 1267 */       this.dataPump.setSequence(paramSequence);
/*      */     }
/*      */ 
/*      */     synchronized void start()
/*      */     {
/* 1274 */       RealTimeSequencer.this.running = true;
/*      */ 
/* 1276 */       if (!this.dataPump.hasCachedTempo()) {
/* 1277 */         long l = RealTimeSequencer.this.getTickPosition();
/* 1278 */         this.dataPump.setTempoMPQ(RealTimeSequencer.this.tempoCache.getTempoMPQAt(l));
/*      */       }
/* 1280 */       this.dataPump.checkPointMillis = 0L;
/* 1281 */       this.dataPump.clearNoteOnCache();
/* 1282 */       this.dataPump.needReindex = true;
/*      */ 
/* 1284 */       this.dataPump.resetLoopCount();
/*      */ 
/* 1287 */       synchronized (this.lock) {
/* 1288 */         this.lock.notifyAll();
/*      */       }
/*      */     }
/*      */ 
/*      */     synchronized void stop()
/*      */     {
/* 1297 */       playThreadImplStop();
/* 1298 */       long l = System.nanoTime() / 1000000L;
/* 1299 */       while (this.isPumping) {
/* 1300 */         synchronized (this.lock) {
/*      */           try {
/* 1302 */             this.lock.wait(2000L);
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */         }
/* 1308 */         if (System.nanoTime() / 1000000L - l <= 1900L);
/*      */       }
/*      */     }
/*      */ 
/*      */     void playThreadImplStop()
/*      */     {
/* 1317 */       RealTimeSequencer.this.running = false;
/* 1318 */       synchronized (this.lock) {
/* 1319 */         this.lock.notifyAll();
/*      */       }
/*      */     }
/*      */ 
/*      */     void close() {
/* 1324 */       Thread localThread = null;
/* 1325 */       synchronized (this)
/*      */       {
/* 1327 */         this.interrupted = true;
/* 1328 */         localThread = this.thread;
/* 1329 */         this.thread = null;
/*      */       }
/* 1331 */       if (localThread != null)
/*      */       {
/* 1333 */         synchronized (this.lock) {
/* 1334 */           this.lock.notifyAll();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1339 */       if (localThread != null)
/*      */         try {
/* 1341 */           localThread.join(2000L);
/*      */         }
/*      */         catch (InterruptedException localInterruptedException)
/*      */         {
/*      */         }
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1355 */       while (!this.interrupted) {
/* 1356 */         boolean bool1 = false;
/* 1357 */         boolean bool2 = RealTimeSequencer.this.running;
/* 1358 */         this.isPumping = ((!this.interrupted) && (RealTimeSequencer.this.running));
/* 1359 */         while ((!bool1) && (!this.interrupted) && (RealTimeSequencer.this.running)) {
/* 1360 */           bool1 = this.dataPump.pump();
/*      */           try
/*      */           {
/* 1363 */             Thread.sleep(1L);
/*      */           }
/*      */           catch (InterruptedException localInterruptedException)
/*      */           {
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1375 */         playThreadImplStop();
/* 1376 */         if (bool2) {
/* 1377 */           this.dataPump.notesOff(true);
/*      */         }
/* 1379 */         if (bool1) {
/* 1380 */           this.dataPump.setTickPos(RealTimeSequencer.this.sequence.getTickLength());
/*      */ 
/* 1383 */           MetaMessage localMetaMessage = new MetaMessage();
/*      */           try {
/* 1385 */             localMetaMessage.setMessage(47, new byte[0], 0); } catch (InvalidMidiDataException localInvalidMidiDataException) {
/*      */           }
/* 1387 */           RealTimeSequencer.this.sendMetaEvents(localMetaMessage);
/*      */         }
/* 1389 */         synchronized (this.lock) {
/* 1390 */           this.isPumping = false;
/*      */ 
/* 1392 */           this.lock.notifyAll();
/* 1393 */           while ((!RealTimeSequencer.this.running) && (!this.interrupted))
/*      */             try {
/* 1395 */               this.lock.wait();
/*      */             }
/*      */             catch (Exception localException)
/*      */             {
/*      */             }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RealTimeSequencerInfo extends MidiDevice.Info
/*      */   {
/*      */     private static final String name = "Real Time Sequencer";
/*      */     private static final String vendor = "Oracle Corporation";
/*      */     private static final String description = "Software sequencer";
/*      */     private static final String version = "Version 1.0";
/*      */ 
/*      */     private RealTimeSequencerInfo()
/*      */     {
/* 1084 */       super("Oracle Corporation", "Software sequencer", "Version 1.0");
/*      */     }
/*      */   }
/*      */ 
/*      */   static class RecordingTrack
/*      */   {
/*      */     private Track track;
/*      */     private int channel;
/*      */ 
/*      */     RecordingTrack(Track paramTrack, int paramInt)
/*      */     {
/* 1204 */       this.track = paramTrack;
/* 1205 */       this.channel = paramInt;
/*      */     }
/*      */ 
/*      */     static RecordingTrack get(List paramList, Track paramTrack)
/*      */     {
/* 1210 */       synchronized (paramList) {
/* 1211 */         int i = paramList.size();
/*      */ 
/* 1213 */         for (int j = 0; j < i; j++) {
/* 1214 */           RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
/* 1215 */           if (localRecordingTrack.track == paramTrack) {
/* 1216 */             return localRecordingTrack;
/*      */           }
/*      */         }
/*      */       }
/* 1220 */       return null;
/*      */     }
/*      */ 
/*      */     static Track get(List paramList, int paramInt)
/*      */     {
/* 1225 */       synchronized (paramList) {
/* 1226 */         int i = paramList.size();
/* 1227 */         for (int j = 0; j < i; j++) {
/* 1228 */           RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
/* 1229 */           if ((localRecordingTrack.channel == paramInt) || (localRecordingTrack.channel == -1)) {
/* 1230 */             return localRecordingTrack.track;
/*      */           }
/*      */         }
/*      */       }
/* 1234 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   class SequencerReceiver extends AbstractMidiDevice.AbstractReceiver
/*      */   {
/*      */     SequencerReceiver()
/*      */     {
/* 1027 */       super();
/*      */     }
/*      */     void implSend(MidiMessage paramMidiMessage, long paramLong) {
/* 1030 */       if (RealTimeSequencer.this.recording) {
/* 1031 */         long l = 0L;
/*      */ 
/* 1034 */         if (paramLong < 0L)
/* 1035 */           l = RealTimeSequencer.this.getTickPosition();
/*      */         else {
/* 1037 */           synchronized (RealTimeSequencer.this.tempoCache) {
/* 1038 */             l = MidiUtils.microsecond2tick(RealTimeSequencer.this.sequence, paramLong, RealTimeSequencer.this.tempoCache);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1043 */         ??? = null;
/*      */ 
/* 1046 */         if (paramMidiMessage.getLength() > 1)
/*      */         {
/*      */           Object localObject2;
/* 1047 */           if ((paramMidiMessage instanceof ShortMessage)) {
/* 1048 */             localObject2 = (ShortMessage)paramMidiMessage;
/*      */ 
/* 1050 */             if ((((ShortMessage)localObject2).getStatus() & 0xF0) != 240) {
/* 1051 */               ??? = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, ((ShortMessage)localObject2).getChannel());
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1056 */             ??? = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, -1);
/*      */           }
/* 1058 */           if (??? != null)
/*      */           {
/* 1060 */             if ((paramMidiMessage instanceof ShortMessage))
/* 1061 */               paramMidiMessage = new FastShortMessage((ShortMessage)paramMidiMessage);
/*      */             else {
/* 1063 */               paramMidiMessage = (MidiMessage)paramMidiMessage.clone();
/*      */             }
/*      */ 
/* 1067 */             localObject2 = new MidiEvent(paramMidiMessage, l);
/* 1068 */             ((Track)???).add((MidiEvent)localObject2);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SequencerTransmitter extends AbstractMidiDevice.BasicTransmitter
/*      */   {
/*      */     private SequencerTransmitter()
/*      */     {
/* 1022 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.RealTimeSequencer
 * JD-Core Version:    0.6.2
 */