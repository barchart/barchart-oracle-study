/*     */ package com.sun.media.sound;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ import java.io.SequenceInputStream;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.sound.midi.SysexMessage;
/*     */ import javax.sound.midi.Track;
/*     */ import javax.sound.midi.spi.MidiFileWriter;
/*     */ 
/*     */ public class StandardMidiFileWriter extends MidiFileWriter
/*     */ {
/*     */   private static final int MThd_MAGIC = 1297377380;
/*     */   private static final int MTrk_MAGIC = 1297379947;
/*     */   private static final int ONE_BYTE = 1;
/*     */   private static final int TWO_BYTE = 2;
/*     */   private static final int SYSEX = 3;
/*     */   private static final int META = 4;
/*     */   private static final int ERROR = 5;
/*     */   private static final int IGNORE = 6;
/*     */   private static final int MIDI_TYPE_0 = 0;
/*     */   private static final int MIDI_TYPE_1 = 1;
/*     */   private static final int bufferSize = 16384;
/*     */   private DataOutputStream tddos;
/*  85 */   private static final int[] types = { 0, 1 };
/*     */   private static final long mask = 127L;
/*     */ 
/*     */   public int[] getMidiFileTypes()
/*     */   {
/*  95 */     int[] arrayOfInt = new int[types.length];
/*  96 */     System.arraycopy(types, 0, arrayOfInt, 0, types.length);
/*  97 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public int[] getMidiFileTypes(Sequence paramSequence)
/*     */   {
/* 110 */     Track[] arrayOfTrack = paramSequence.getTracks();
/*     */     int[] arrayOfInt;
/* 112 */     if (arrayOfTrack.length == 1) {
/* 113 */       arrayOfInt = new int[2];
/* 114 */       arrayOfInt[0] = 0;
/* 115 */       arrayOfInt[1] = 1;
/*     */     } else {
/* 117 */       arrayOfInt = new int[1];
/* 118 */       arrayOfInt[0] = 1;
/*     */     }
/*     */ 
/* 121 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public boolean isFileTypeSupported(int paramInt) {
/* 125 */     for (int i = 0; i < types.length; i++) {
/* 126 */       if (paramInt == types[i]) {
/* 127 */         return true;
/*     */       }
/*     */     }
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   public int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream) throws IOException {
/* 134 */     byte[] arrayOfByte = null;
/*     */ 
/* 136 */     int i = 0;
/* 137 */     long l = 0L;
/*     */ 
/* 139 */     if (!isFileTypeSupported(paramInt, paramSequence)) {
/* 140 */       throw new IllegalArgumentException("Could not write MIDI file");
/*     */     }
/*     */ 
/* 143 */     InputStream localInputStream = getFileStream(paramInt, paramSequence);
/* 144 */     if (localInputStream == null) {
/* 145 */       throw new IllegalArgumentException("Could not write MIDI file");
/*     */     }
/* 147 */     arrayOfByte = new byte[16384];
/*     */ 
/* 149 */     while ((i = localInputStream.read(arrayOfByte)) >= 0) {
/* 150 */       paramOutputStream.write(arrayOfByte, 0, i);
/* 151 */       l += i;
/*     */     }
/*     */ 
/* 154 */     return (int)l;
/*     */   }
/*     */ 
/*     */   public int write(Sequence paramSequence, int paramInt, File paramFile) throws IOException {
/* 158 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/* 159 */     int i = write(paramSequence, paramInt, localFileOutputStream);
/* 160 */     localFileOutputStream.close();
/* 161 */     return i;
/*     */   }
/*     */ 
/*     */   private InputStream getFileStream(int paramInt, Sequence paramSequence)
/*     */     throws IOException
/*     */   {
/* 168 */     Track[] arrayOfTrack = paramSequence.getTracks();
/* 169 */     int i = 0;
/* 170 */     int j = 14;
/* 171 */     int k = 0;
/*     */ 
/* 175 */     PipedOutputStream localPipedOutputStream = null;
/* 176 */     DataOutputStream localDataOutputStream = null;
/* 177 */     PipedInputStream localPipedInputStream = null;
/*     */ 
/* 179 */     InputStream[] arrayOfInputStream = null;
/* 180 */     Object localObject = null;
/* 181 */     SequenceInputStream localSequenceInputStream = null;
/*     */ 
/* 184 */     if (paramInt == 0) {
/* 185 */       if (arrayOfTrack.length != 1)
/* 186 */         return null;
/*     */     }
/* 188 */     else if (paramInt == 1) {
/* 189 */       if (arrayOfTrack.length < 1) {
/* 190 */         return null;
/*     */       }
/*     */     }
/* 193 */     else if (arrayOfTrack.length == 1)
/* 194 */       paramInt = 0;
/* 195 */     else if (arrayOfTrack.length > 1)
/* 196 */       paramInt = 1;
/*     */     else {
/* 198 */       return null;
/*     */     }
/*     */ 
/* 206 */     arrayOfInputStream = new InputStream[arrayOfTrack.length];
/* 207 */     int n = 0;
/* 208 */     for (int i1 = 0; i1 < arrayOfTrack.length; i1++) {
/*     */       try {
/* 210 */         arrayOfInputStream[n] = writeTrack(arrayOfTrack[i1], paramInt);
/* 211 */         n++;
/*     */       }
/*     */       catch (InvalidMidiDataException localInvalidMidiDataException)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 219 */     if (n == 1) {
/* 220 */       localObject = arrayOfInputStream[0];
/* 221 */     } else if (n > 1) {
/* 222 */       localObject = arrayOfInputStream[0];
/* 223 */       for (i1 = 1; i1 < arrayOfTrack.length; i1++)
/*     */       {
/* 226 */         if (arrayOfInputStream[i1] != null)
/* 227 */           localObject = new SequenceInputStream((InputStream)localObject, arrayOfInputStream[i1]);
/*     */       }
/*     */     }
/*     */     else {
/* 231 */       throw new IllegalArgumentException("invalid MIDI data in sequence");
/*     */     }
/*     */ 
/* 235 */     localPipedOutputStream = new PipedOutputStream();
/* 236 */     localDataOutputStream = new DataOutputStream(localPipedOutputStream);
/* 237 */     localPipedInputStream = new PipedInputStream(localPipedOutputStream);
/*     */ 
/* 240 */     localDataOutputStream.writeInt(1297377380);
/*     */ 
/* 243 */     localDataOutputStream.writeInt(j - 8);
/*     */ 
/* 246 */     if (paramInt == 0) {
/* 247 */       localDataOutputStream.writeShort(0);
/*     */     }
/*     */     else {
/* 250 */       localDataOutputStream.writeShort(1);
/*     */     }
/*     */ 
/* 254 */     localDataOutputStream.writeShort((short)n);
/*     */ 
/* 257 */     float f = paramSequence.getDivisionType();
/*     */     int m;
/* 258 */     if (f == 0.0F) {
/* 259 */       m = paramSequence.getResolution();
/* 260 */     } else if (f == 24.0F) {
/* 261 */       m = -6144;
/* 262 */       m += (paramSequence.getResolution() & 0xFF);
/* 263 */     } else if (f == 25.0F) {
/* 264 */       m = -6400;
/* 265 */       m += (paramSequence.getResolution() & 0xFF);
/* 266 */     } else if (f == 29.969999F) {
/* 267 */       m = -7424;
/* 268 */       m += (paramSequence.getResolution() & 0xFF);
/* 269 */     } else if (f == 30.0F) {
/* 270 */       m = -7680;
/* 271 */       m += (paramSequence.getResolution() & 0xFF);
/*     */     }
/*     */     else {
/* 274 */       return null;
/*     */     }
/* 276 */     localDataOutputStream.writeShort(m);
/*     */ 
/* 279 */     localSequenceInputStream = new SequenceInputStream(localPipedInputStream, (InputStream)localObject);
/* 280 */     localDataOutputStream.close();
/*     */ 
/* 282 */     k = i + j;
/* 283 */     return localSequenceInputStream;
/*     */   }
/*     */ 
/*     */   private int getType(int paramInt)
/*     */   {
/* 291 */     if ((paramInt & 0xF0) == 240) {
/* 292 */       switch (paramInt) {
/*     */       case 240:
/*     */       case 247:
/* 295 */         return 3;
/*     */       case 255:
/* 297 */         return 4;
/*     */       }
/* 299 */       return 6;
/*     */     }
/*     */ 
/* 302 */     switch (paramInt & 0xF0) {
/*     */     case 128:
/*     */     case 144:
/*     */     case 160:
/*     */     case 176:
/*     */     case 224:
/* 308 */       return 2;
/*     */     case 192:
/*     */     case 208:
/* 311 */       return 1;
/*     */     }
/* 313 */     return 5;
/*     */   }
/*     */ 
/*     */   private int writeVarInt(long paramLong)
/*     */     throws IOException
/*     */   {
/* 319 */     int i = 1;
/* 320 */     int j = 63;
/*     */ 
/* 322 */     while ((j > 0) && ((paramLong & 127L << j) == 0L)) j -= 7;
/*     */ 
/* 324 */     while (j > 0) {
/* 325 */       this.tddos.writeByte((int)((paramLong & 127L << j) >> j | 0x80));
/* 326 */       j -= 7;
/* 327 */       i++;
/*     */     }
/* 329 */     this.tddos.writeByte((int)(paramLong & 0x7F));
/* 330 */     return i;
/*     */   }
/*     */ 
/*     */   private InputStream writeTrack(Track paramTrack, int paramInt) throws IOException, InvalidMidiDataException {
/* 334 */     int i = 0;
/* 335 */     int j = 0;
/* 336 */     int k = paramTrack.size();
/* 337 */     PipedOutputStream localPipedOutputStream = new PipedOutputStream();
/* 338 */     DataOutputStream localDataOutputStream = new DataOutputStream(localPipedOutputStream);
/* 339 */     PipedInputStream localPipedInputStream = new PipedInputStream(localPipedOutputStream);
/*     */ 
/* 341 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 342 */     this.tddos = new DataOutputStream(localByteArrayOutputStream);
/* 343 */     ByteArrayInputStream localByteArrayInputStream = null;
/*     */ 
/* 345 */     SequenceInputStream localSequenceInputStream = null;
/*     */ 
/* 347 */     long l1 = 0L;
/* 348 */     long l2 = 0L;
/* 349 */     long l3 = 0L;
/* 350 */     int m = -1;
/*     */ 
/* 355 */     for (int n = 0; n < k; n++) {
/* 356 */       MidiEvent localMidiEvent = paramTrack.get(n);
/*     */ 
/* 363 */       byte[] arrayOfByte = null;
/* 364 */       ShortMessage localShortMessage = null;
/* 365 */       MetaMessage localMetaMessage = null;
/* 366 */       SysexMessage localSysexMessage = null;
/*     */ 
/* 370 */       l3 = localMidiEvent.getTick();
/* 371 */       l2 = localMidiEvent.getTick() - l1;
/* 372 */       l1 = localMidiEvent.getTick();
/*     */ 
/* 375 */       int i1 = localMidiEvent.getMessage().getStatus();
/* 376 */       int i2 = getType(i1);
/*     */       int i3;
/*     */       int i5;
/* 378 */       switch (i2) {
/*     */       case 1:
/* 380 */         localShortMessage = (ShortMessage)localMidiEvent.getMessage();
/* 381 */         i3 = localShortMessage.getData1();
/* 382 */         i += writeVarInt(l2);
/*     */ 
/* 384 */         if (i1 != m) {
/* 385 */           m = i1;
/* 386 */           this.tddos.writeByte(i1); i++;
/*     */         }
/* 388 */         this.tddos.writeByte(i3); i++;
/* 389 */         break;
/*     */       case 2:
/* 392 */         localShortMessage = (ShortMessage)localMidiEvent.getMessage();
/* 393 */         i3 = localShortMessage.getData1();
/* 394 */         int i4 = localShortMessage.getData2();
/*     */ 
/* 396 */         i += writeVarInt(l2);
/* 397 */         if (i1 != m) {
/* 398 */           m = i1;
/* 399 */           this.tddos.writeByte(i1); i++;
/*     */         }
/* 401 */         this.tddos.writeByte(i3); i++;
/* 402 */         this.tddos.writeByte(i4); i++;
/* 403 */         break;
/*     */       case 3:
/* 406 */         localSysexMessage = (SysexMessage)localMidiEvent.getMessage();
/* 407 */         i5 = localSysexMessage.getLength();
/* 408 */         arrayOfByte = localSysexMessage.getMessage();
/* 409 */         i += writeVarInt(l2);
/*     */ 
/* 412 */         m = i1;
/* 413 */         this.tddos.writeByte(arrayOfByte[0]); i++;
/*     */ 
/* 419 */         i += writeVarInt(arrayOfByte.length - 1);
/*     */ 
/* 423 */         this.tddos.write(arrayOfByte, 1, arrayOfByte.length - 1);
/* 424 */         i += arrayOfByte.length - 1;
/* 425 */         break;
/*     */       case 4:
/* 428 */         localMetaMessage = (MetaMessage)localMidiEvent.getMessage();
/* 429 */         i5 = localMetaMessage.getLength();
/* 430 */         arrayOfByte = localMetaMessage.getMessage();
/* 431 */         i += writeVarInt(l2);
/*     */ 
/* 439 */         m = i1;
/* 440 */         this.tddos.write(arrayOfByte, 0, arrayOfByte.length);
/* 441 */         i += arrayOfByte.length;
/* 442 */         break;
/*     */       case 6:
/* 446 */         break;
/*     */       case 5:
/* 450 */         break;
/*     */       default:
/* 453 */         throw new InvalidMidiDataException("internal file writer error");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 461 */     localDataOutputStream.writeInt(1297379947);
/* 462 */     localDataOutputStream.writeInt(i);
/* 463 */     i += 8;
/*     */ 
/* 466 */     localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
/* 467 */     localSequenceInputStream = new SequenceInputStream(localPipedInputStream, localByteArrayInputStream);
/* 468 */     localDataOutputStream.close();
/* 469 */     this.tddos.close();
/*     */ 
/* 471 */     return localSequenceInputStream;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.media.sound.StandardMidiFileWriter
 * JD-Core Version:    0.6.2
 */