/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiFileFormat;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.spi.MidiFileReader;
/*     */ 
/*     */ public class StandardMidiFileReader extends MidiFileReader
/*     */ {
/*     */   private static final int MThd_MAGIC = 1297377380;
/*     */   private static final int MIDI_TYPE_0 = 0;
/*     */   private static final int MIDI_TYPE_1 = 1;
/*     */   private static final int bisBufferSize = 1024;
/*  79 */   private static final int[] types = { 0, 1 };
/*     */ 
/*     */   public MidiFileFormat getMidiFileFormat(InputStream paramInputStream)
/*     */     throws InvalidMidiDataException, IOException
/*     */   {
/*  85 */     return getMidiFileFormatFromStream(paramInputStream, -1, null);
/*     */   }
/*     */ 
/*     */   private MidiFileFormat getMidiFileFormatFromStream(InputStream paramInputStream, int paramInt, SMFParser paramSMFParser) throws InvalidMidiDataException, IOException
/*     */   {
/*  90 */     int i = 16;
/*  91 */     int j = -1;
/*     */     DataInputStream localDataInputStream;
/*  94 */     if ((paramInputStream instanceof DataInputStream))
/*  95 */       localDataInputStream = (DataInputStream)paramInputStream;
/*     */     else {
/*  97 */       localDataInputStream = new DataInputStream(paramInputStream);
/*     */     }
/*  99 */     if (paramSMFParser == null)
/* 100 */       localDataInputStream.mark(i);
/*     */     else {
/* 102 */       paramSMFParser.stream = localDataInputStream;
/*     */     }
/*     */     int k;
/*     */     float f;
/*     */     int n;
/*     */     try
/*     */     {
/* 111 */       int i1 = localDataInputStream.readInt();
/* 112 */       if (i1 != 1297377380)
/*     */       {
/* 114 */         throw new InvalidMidiDataException("not a valid MIDI file");
/*     */       }
/*     */ 
/* 118 */       int i2 = localDataInputStream.readInt() - 6;
/* 119 */       k = localDataInputStream.readShort();
/* 120 */       int m = localDataInputStream.readShort();
/* 121 */       int i3 = localDataInputStream.readShort();
/*     */ 
/* 124 */       if (i3 > 0)
/*     */       {
/* 126 */         f = 0.0F;
/* 127 */         n = i3;
/*     */       }
/*     */       else {
/* 130 */         int i4 = -1 * (i3 >> 8);
/* 131 */         switch (i4) {
/*     */         case 24:
/* 133 */           f = 24.0F;
/* 134 */           break;
/*     */         case 25:
/* 136 */           f = 25.0F;
/* 137 */           break;
/*     */         case 29:
/* 139 */           f = 29.969999F;
/* 140 */           break;
/*     */         case 30:
/* 142 */           f = 30.0F;
/* 143 */           break;
/*     */         case 26:
/*     */         case 27:
/*     */         case 28:
/*     */         default:
/* 145 */           throw new InvalidMidiDataException("Unknown frame code: " + i4);
/*     */         }
/*     */ 
/* 148 */         n = i3 & 0xFF;
/*     */       }
/* 150 */       if (paramSMFParser != null)
/*     */       {
/* 152 */         localDataInputStream.skip(i2);
/* 153 */         paramSMFParser.tracks = m;
/*     */       }
/*     */     }
/*     */     finally {
/* 157 */       if (paramSMFParser == null) {
/* 158 */         localDataInputStream.reset();
/*     */       }
/*     */     }
/* 161 */     MidiFileFormat localMidiFileFormat = new MidiFileFormat(k, f, n, paramInt, j);
/* 162 */     return localMidiFileFormat;
/*     */   }
/*     */ 
/*     */   public MidiFileFormat getMidiFileFormat(URL paramURL) throws InvalidMidiDataException, IOException
/*     */   {
/* 167 */     InputStream localInputStream = paramURL.openStream();
/* 168 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream, 1024);
/* 169 */     MidiFileFormat localMidiFileFormat = null;
/*     */     try {
/* 171 */       localMidiFileFormat = getMidiFileFormat(localBufferedInputStream);
/*     */     } finally {
/* 173 */       localBufferedInputStream.close();
/*     */     }
/* 175 */     return localMidiFileFormat;
/*     */   }
/*     */ 
/*     */   public MidiFileFormat getMidiFileFormat(File paramFile) throws InvalidMidiDataException, IOException
/*     */   {
/* 180 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/* 181 */     BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream, 1024);
/*     */ 
/* 184 */     long l = paramFile.length();
/* 185 */     if (l > 2147483647L) {
/* 186 */       l = -1L;
/*     */     }
/* 188 */     MidiFileFormat localMidiFileFormat = null;
/*     */     try {
/* 190 */       localMidiFileFormat = getMidiFileFormatFromStream(localBufferedInputStream, (int)l, null);
/*     */     } finally {
/* 192 */       localBufferedInputStream.close();
/*     */     }
/* 194 */     return localMidiFileFormat;
/*     */   }
/*     */ 
/*     */   public Sequence getSequence(InputStream paramInputStream) throws InvalidMidiDataException, IOException
/*     */   {
/* 199 */     SMFParser localSMFParser = new SMFParser();
/* 200 */     MidiFileFormat localMidiFileFormat = getMidiFileFormatFromStream(paramInputStream, -1, localSMFParser);
/*     */ 
/* 205 */     if ((localMidiFileFormat.getType() != 0) && (localMidiFileFormat.getType() != 1)) {
/* 206 */       throw new InvalidMidiDataException("Invalid or unsupported file type: " + localMidiFileFormat.getType());
/*     */     }
/*     */ 
/* 210 */     Sequence localSequence = new Sequence(localMidiFileFormat.getDivisionType(), localMidiFileFormat.getResolution());
/*     */ 
/* 213 */     for (int i = 0; (i < localSMFParser.tracks) && 
/* 214 */       (localSMFParser.nextTrack()); i++)
/*     */     {
/* 215 */       localSMFParser.readTrack(localSequence.createTrack());
/*     */     }
/*     */ 
/* 220 */     return localSequence;
/*     */   }
/*     */ 
/*     */   public Sequence getSequence(URL paramURL)
/*     */     throws InvalidMidiDataException, IOException
/*     */   {
/* 226 */     Object localObject1 = paramURL.openStream();
/* 227 */     localObject1 = new BufferedInputStream((InputStream)localObject1, 1024);
/* 228 */     Sequence localSequence = null;
/*     */     try {
/* 230 */       localSequence = getSequence((InputStream)localObject1);
/*     */     } finally {
/* 232 */       ((InputStream)localObject1).close();
/*     */     }
/* 234 */     return localSequence;
/*     */   }
/*     */ 
/*     */   public Sequence getSequence(File paramFile) throws InvalidMidiDataException, IOException
/*     */   {
/* 239 */     Object localObject1 = new FileInputStream(paramFile);
/* 240 */     localObject1 = new BufferedInputStream((InputStream)localObject1, 1024);
/* 241 */     Sequence localSequence = null;
/*     */     try {
/* 243 */       localSequence = getSequence((InputStream)localObject1);
/*     */     } finally {
/* 245 */       ((InputStream)localObject1).close();
/*     */     }
/* 247 */     return localSequence;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.StandardMidiFileReader
 * JD-Core Version:    0.6.2
 */