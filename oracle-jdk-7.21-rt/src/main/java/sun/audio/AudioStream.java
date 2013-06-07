/*     */ package sun.audio;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiFileFormat;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class AudioStream extends FilterInputStream
/*     */ {
/*  47 */   protected AudioInputStream ais = null;
/*  48 */   protected AudioFormat format = null;
/*  49 */   protected MidiFileFormat midiformat = null;
/*  50 */   protected InputStream stream = null;
/*     */ 
/*     */   public AudioStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  61 */     super(paramInputStream);
/*     */ 
/*  63 */     this.stream = paramInputStream;
/*     */ 
/*  65 */     if (!paramInputStream.markSupported())
/*     */     {
/*  67 */       this.stream = new BufferedInputStream(paramInputStream, 1024);
/*     */     }
/*     */     try
/*     */     {
/*  71 */       this.ais = AudioSystem.getAudioInputStream(this.stream);
/*  72 */       this.format = this.ais.getFormat();
/*  73 */       this.in = this.ais;
/*     */     }
/*     */     catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
/*     */     {
/*     */       try
/*     */       {
/*  79 */         this.midiformat = MidiSystem.getMidiFileFormat(this.stream);
/*     */       }
/*     */       catch (InvalidMidiDataException localInvalidMidiDataException) {
/*  82 */         throw new IOException("could not create audio stream from input stream");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public AudioData getData()
/*     */     throws IOException
/*     */   {
/* 103 */     int i = getLength();
/*     */ 
/* 106 */     if (i < 1048576) {
/* 107 */       byte[] arrayOfByte = new byte[i];
/*     */       try {
/* 109 */         this.ais.read(arrayOfByte, 0, i);
/*     */       } catch (IOException localIOException) {
/* 111 */         throw new IOException("Could not create AudioData Object");
/*     */       }
/* 113 */       return new AudioData(this.format, arrayOfByte);
/*     */     }
/*     */ 
/* 127 */     throw new IOException("could not create AudioData object");
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 133 */     if ((this.ais != null) && (this.format != null)) {
/* 134 */       return (int)(this.ais.getFrameLength() * this.ais.getFormat().getFrameSize());
/*     */     }
/*     */ 
/* 137 */     if (this.midiformat != null) {
/* 138 */       return this.midiformat.getByteLength();
/*     */     }
/*     */ 
/* 141 */     return -1;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.audio.AudioStream
 * JD-Core Version:    0.6.2
 */