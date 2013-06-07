/*     */ package sun.audio;
/*     */ 
/*     */ import com.sun.media.sound.DataPusher;
/*     */ import com.sun.media.sound.Toolkit;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaEventListener;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiFileFormat;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioFormat.Encoding;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.DataLine.Info;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.Mixer;
/*     */ import javax.sound.sampled.SourceDataLine;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class AudioDevice
/*     */ {
/*  63 */   private boolean DEBUG = false;
/*     */   private Hashtable clipStreams;
/*     */   private Vector infos;
/*  71 */   private boolean playing = false;
/*     */ 
/*  74 */   private Mixer mixer = null;
/*     */ 
/*  82 */   public static final AudioDevice device = new AudioDevice();
/*     */ 
/*     */   private AudioDevice()
/*     */   {
/*  89 */     this.clipStreams = new Hashtable();
/*  90 */     this.infos = new Vector();
/*     */   }
/*     */ 
/*     */   private synchronized void startSampled(AudioInputStream paramAudioInputStream, InputStream paramInputStream)
/*     */     throws UnsupportedAudioFileException, LineUnavailableException
/*     */   {
/*  98 */     Info localInfo = null;
/*  99 */     DataPusher localDataPusher = null;
/* 100 */     DataLine.Info localInfo1 = null;
/* 101 */     SourceDataLine localSourceDataLine = null;
/*     */ 
/* 104 */     paramAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(paramAudioInputStream);
/*     */ 
/* 106 */     if (paramAudioInputStream == null)
/*     */     {
/* 108 */       return;
/*     */     }
/*     */ 
/* 111 */     localInfo1 = new DataLine.Info(SourceDataLine.class, paramAudioInputStream.getFormat());
/*     */ 
/* 113 */     if (!AudioSystem.isLineSupported(localInfo1)) {
/* 114 */       return;
/*     */     }
/* 116 */     localSourceDataLine = (SourceDataLine)AudioSystem.getLine(localInfo1);
/* 117 */     localDataPusher = new DataPusher(localSourceDataLine, paramAudioInputStream);
/*     */ 
/* 119 */     localInfo = new Info(null, paramInputStream, localDataPusher);
/* 120 */     this.infos.addElement(localInfo);
/*     */ 
/* 122 */     localDataPusher.start();
/*     */   }
/*     */ 
/*     */   private synchronized void startMidi(InputStream paramInputStream1, InputStream paramInputStream2)
/*     */     throws InvalidMidiDataException, MidiUnavailableException
/*     */   {
/* 129 */     Sequencer localSequencer = null;
/* 130 */     Info localInfo = null;
/*     */ 
/* 132 */     localSequencer = MidiSystem.getSequencer();
/* 133 */     localSequencer.open();
/*     */     try {
/* 135 */       localSequencer.setSequence(paramInputStream1);
/*     */     } catch (IOException localIOException) {
/* 137 */       throw new InvalidMidiDataException(localIOException.getMessage());
/*     */     }
/*     */ 
/* 140 */     localInfo = new Info(localSequencer, paramInputStream2, null);
/*     */ 
/* 142 */     this.infos.addElement(localInfo);
/*     */ 
/* 145 */     localSequencer.addMetaEventListener(localInfo);
/*     */ 
/* 147 */     localSequencer.start();
/*     */   }
/*     */ 
/*     */   public synchronized void openChannel(InputStream paramInputStream)
/*     */   {
/* 159 */     if (this.DEBUG) {
/* 160 */       System.out.println("AudioDevice: openChannel");
/* 161 */       System.out.println("input stream =" + paramInputStream);
/*     */     }
/*     */ 
/* 164 */     Info localInfo = null;
/*     */ 
/* 167 */     for (int i = 0; i < this.infos.size(); i++) {
/* 168 */       localInfo = (Info)this.infos.elementAt(i);
/* 169 */       if (localInfo.in == paramInputStream)
/*     */       {
/* 171 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     AudioInputStream localAudioInputStream1 = null;
/*     */ 
/* 178 */     if ((paramInputStream instanceof AudioStream))
/*     */     {
/* 180 */       if (((AudioStream)paramInputStream).midiformat != null)
/*     */       {
/*     */         try
/*     */         {
/* 184 */           startMidi(((AudioStream)paramInputStream).stream, paramInputStream);
/*     */         } catch (Exception localException1) {
/* 186 */           return;
/*     */         }
/*     */ 
/*     */       }
/* 190 */       else if (((AudioStream)paramInputStream).ais != null)
/*     */       {
/*     */         try
/*     */         {
/* 194 */           startSampled(((AudioStream)paramInputStream).ais, paramInputStream);
/*     */         } catch (Exception localException2) {
/* 196 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 200 */     else if ((paramInputStream instanceof AudioDataStream)) {
/* 201 */       if ((paramInputStream instanceof ContinuousAudioDataStream))
/*     */         try {
/* 203 */           AudioInputStream localAudioInputStream2 = new AudioInputStream(paramInputStream, ((AudioDataStream)paramInputStream).getAudioData().format, -1L);
/*     */ 
/* 206 */           startSampled(localAudioInputStream2, paramInputStream);
/*     */         } catch (Exception localException3) {
/* 208 */           return;
/*     */         }
/*     */       else
/*     */         try
/*     */         {
/* 213 */           AudioInputStream localAudioInputStream3 = new AudioInputStream(paramInputStream, ((AudioDataStream)paramInputStream).getAudioData().format, ((AudioDataStream)paramInputStream).getAudioData().buffer.length);
/*     */ 
/* 216 */           startSampled(localAudioInputStream3, paramInputStream);
/*     */         } catch (Exception localException4) {
/* 218 */           return;
/*     */         }
/*     */     }
/*     */     else {
/* 222 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream, 1024);
/*     */       try
/*     */       {
/*     */         try
/*     */         {
/* 227 */           localAudioInputStream1 = AudioSystem.getAudioInputStream(localBufferedInputStream);
/*     */         } catch (IOException localIOException1) {
/* 229 */           return;
/*     */         }
/*     */ 
/* 232 */         startSampled(localAudioInputStream1, paramInputStream);
/*     */       }
/*     */       catch (UnsupportedAudioFileException localUnsupportedAudioFileException1)
/*     */       {
/*     */         try {
/*     */           try {
/* 238 */             MidiFileFormat localMidiFileFormat = MidiSystem.getMidiFileFormat(localBufferedInputStream);
/*     */           }
/*     */           catch (IOException localIOException2) {
/* 241 */             return;
/*     */           }
/*     */ 
/* 244 */           startMidi(localBufferedInputStream, paramInputStream);
/*     */         }
/*     */         catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */         {
/* 253 */           AudioFormat localAudioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
/*     */           try
/*     */           {
/* 256 */             AudioInputStream localAudioInputStream4 = new AudioInputStream(localBufferedInputStream, localAudioFormat, -1L);
/*     */ 
/* 258 */             startSampled(localAudioInputStream4, paramInputStream);
/*     */           } catch (UnsupportedAudioFileException localUnsupportedAudioFileException2) {
/* 260 */             return;
/*     */           } catch (LineUnavailableException localLineUnavailableException2) {
/* 262 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (MidiUnavailableException localMidiUnavailableException)
/*     */         {
/* 268 */           return;
/*     */         }
/*     */       }
/*     */       catch (LineUnavailableException localLineUnavailableException1)
/*     */       {
/* 273 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 278 */     notify();
/*     */   }
/*     */ 
/*     */   public synchronized void closeChannel(InputStream paramInputStream)
/*     */   {
/* 287 */     if (this.DEBUG) {
/* 288 */       System.out.println("AudioDevice.closeChannel");
/*     */     }
/*     */ 
/* 291 */     if (paramInputStream == null) return;
/*     */ 
/* 295 */     for (int i = 0; i < this.infos.size(); i++)
/*     */     {
/* 297 */       Info localInfo = (Info)this.infos.elementAt(i);
/*     */ 
/* 299 */       if (localInfo.in == paramInputStream)
/*     */       {
/* 301 */         if (localInfo.sequencer != null)
/*     */         {
/* 303 */           localInfo.sequencer.stop();
/*     */ 
/* 305 */           this.infos.removeElement(localInfo);
/*     */         }
/* 307 */         else if (localInfo.datapusher != null)
/*     */         {
/* 309 */           localInfo.datapusher.stop();
/* 310 */           this.infos.removeElement(localInfo);
/*     */         }
/*     */       }
/*     */     }
/* 314 */     notify();
/*     */   }
/*     */ 
/*     */   public synchronized void open()
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void play()
/*     */   {
/* 348 */     if (this.DEBUG)
/* 349 */       System.out.println("exiting play()");
/*     */   }
/*     */ 
/*     */   public synchronized void closeStreams()
/*     */   {
/* 360 */     for (int i = 0; i < this.infos.size(); i++)
/*     */     {
/* 362 */       Info localInfo = (Info)this.infos.elementAt(i);
/*     */ 
/* 364 */       if (localInfo.sequencer != null)
/*     */       {
/* 366 */         localInfo.sequencer.stop();
/* 367 */         localInfo.sequencer.close();
/* 368 */         this.infos.removeElement(localInfo);
/*     */       }
/* 370 */       else if (localInfo.datapusher != null)
/*     */       {
/* 372 */         localInfo.datapusher.stop();
/* 373 */         this.infos.removeElement(localInfo);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 378 */     if (this.DEBUG) {
/* 379 */       System.err.println("Audio Device: Streams all closed.");
/*     */     }
/*     */ 
/* 382 */     this.clipStreams = new Hashtable();
/* 383 */     this.infos = new Vector();
/*     */   }
/*     */ 
/*     */   public int openChannels()
/*     */   {
/* 390 */     return this.infos.size();
/*     */   }
/*     */ 
/*     */   void setVerbose(boolean paramBoolean)
/*     */   {
/* 397 */     this.DEBUG = paramBoolean;
/*     */   }
/*     */ 
/*     */   class Info
/*     */     implements MetaEventListener
/*     */   {
/*     */     Sequencer sequencer;
/*     */     InputStream in;
/*     */     DataPusher datapusher;
/*     */ 
/*     */     Info(Sequencer paramInputStream, InputStream paramDataPusher, DataPusher arg4)
/*     */     {
/* 415 */       this.sequencer = paramInputStream;
/* 416 */       this.in = paramDataPusher;
/*     */       Object localObject;
/* 417 */       this.datapusher = localObject;
/*     */     }
/*     */ 
/*     */     public void meta(MetaMessage paramMetaMessage) {
/* 421 */       if ((paramMetaMessage.getType() == 47) && (this.sequencer != null))
/* 422 */         this.sequencer.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioDevice
 * JD-Core Version:    0.6.2
 */